/**
 * Copyright (C) 2014 cherimojava (http://github.com/cherimojava/orchidae)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cherimojava.orchidae.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mongodb.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.entity.Picture;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.FindOptions;

/**
 * Does the handling of uploading and serving pictures
 * 
 * @author philnate
 */
@RestController
@RequestMapping(value = "/picture")
public class PictureController {

	@Value("${limit.latestPictures:30}")
	int latestPictureLimit;

	@Value("${picture.path:./pictures}")
	String storagePath;

	@Autowired
	EntityFactory factory;

	private Random rand = new Random();

	private Logger LOG = LogManager.getLogger();

	/**
	 * Returns a list (json) with the {number} most recent photos of the given {user}.
	 * 
	 * @param user
	 *            to retrieve pictures from
	 * @param number
	 *            number of pictures to ask for. Number is constrained by {@link #latestPictureLimit}
	 * @return picture json list with the latest pictures
	 * @since 1.0.0
	 */
	@RequestMapping(value = "/{user}/latest/{number}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<Picture> pictures(String user, @PathVariable("number") Integer number) {
		if (number > latestPictureLimit) {
			LOG.info("latest picture request was ({}) greater than max allowed {}. Only returning max", number,
					latestPictureLimit);
			number = latestPictureLimit;
		}

		// TODO only return pictures visible to the current user
		MongoIterable<Picture> it = (MongoIterable<Picture>) factory.getCollection(Picture.class).find(
				new FindOptions().limit(number).sort(new Document("uploaded", 1)));
		return Lists.newArrayList(it);
	}

	// TODO check that only pictures visible to user are handed out

	/**
	 * serves the requested picture {id} for the given {user}
	 * 
	 * @param user
	 *            user to lookup picture
	 * @param id
	 *            id of the picture to load
	 * @return the picture which belongs to the given id, or {@link org.springframework.http.HttpStatus#NOT_FOUND} if no
	 *         such picture exists
	 * @throws IOException
	 * @since 1.0.0
	 */
	@ResponseBody
	@RequestMapping(value = "/{user}/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<Resource> getPicture(@PathVariable("user") String user, @PathVariable("id") String id)
			throws IOException {
		File picture = new File(storagePath, id);
		if (picture.exists()) {
			return new ResponseEntity<Resource>(new InputStreamResource(FileUtils.openInputStream(picture)),
					HttpStatus.OK);
		} else {
			LOG.debug("Could not find picture with id {}", id);
			// picture doesn't exist so return 404
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * uploads multiple files into the system for the current user
	 * 
	 * @param request
	 *            request with pictures to store
	 * @return {@link org.springframework.http.HttpStatus#CREATED} if the upload was successful or
	 *         {@link org.springframework.http.HttpStatus#OK} if some of the pictures couldn't be uploaded together with
	 *         information which pictures couldn't be uploaded
	 * @since 1.0.0
	 */
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> handleFileUpload(MultipartHttpServletRequest request) {
		List<String> badFiles = Lists.newArrayList();
		for (Iterator<String> it = request.getFileNames(); it.hasNext();) {
			MultipartFile file = request.getFile(it.next());
			// Create uuid and Picture entity
			Picture picture = factory.create(Picture.class);
			// With Java 8 provide default method which allows to generate an id
			picture.setTitle(StringUtils.split(file.getOriginalFilename(), ".")[0]);
			picture.setId(Long.toHexString(rand.nextLong()));
			picture.setOriginalName(file.getOriginalFilename());
			picture.setUploaded(DateTime.now());
			try {
				file.transferTo(new File(storagePath + "/" + picture.getId()));
				LOG.info("Uploaded {} and assigned id {}", file.getOriginalFilename(), picture.getId());
				// TODO can't put everything into one folder
				// TODO need to create thumbnails
				picture.save();
			} catch (Exception e) {
				LOG.warn("failed to store picture", e);
				badFiles.add(file.getOriginalFilename());
			}
		}
		if (badFiles.isEmpty()) {
			return new ResponseEntity<>("You successfully uploaded!", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Could not upload all files. Failed to upload: "
					+ Joiner.on(",").join(badFiles), HttpStatus.OK);
		}
	}
}

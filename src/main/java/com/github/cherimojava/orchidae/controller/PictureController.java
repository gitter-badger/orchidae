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

import static com.github.cherimojava.orchidae.util.FileUtil.generateId;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.imgscalr.Scalr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.entity.Access;
import com.github.cherimojava.orchidae.entity.Picture;
import com.github.cherimojava.orchidae.entity.User;
import com.github.cherimojava.orchidae.util.FileUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.QueryBuilder;

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

	@Value("${picture.thumbnail.maxHeight:150}")
	int maxHeight;

	@Autowired
	EntityFactory factory;

	@Autowired
	FileUtil fileUtil;

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
	 * @see #_getPicturesMeta(String, int)
	 */
	@RequestMapping(value = "/{user}/latest/{number:[0-9]+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Picture> latestPicturesByUserLimit(@PathVariable("user") String user,
			@PathVariable("number") Integer number) {
		return _getPicturesMeta(user, number);
	}

	/**
	 * Returns a list (json) with the {@link #latestPictureLimit} most recent photos of the given {user}.
	 *
	 * @param user
	 *            to retrieve pictures from
	 * @return picture json list with the latest pictures
	 * @since 1.0.0
	 * @see #_getPicturesMeta(String, int)
	 */
	@RequestMapping(value = "/{user}/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Picture> latestPicturesByUserDefLimit(@PathVariable("user") String user) {
		return _getPicturesMeta(user, latestPictureLimit);
	}

	/**
	 * actual method handling the providing of meta data. Returned data is constrained in such a way that only pictures
	 * visible to current user are returned (no private pictures of a different user e.g.)
	 * 
	 * @param user
	 *            user to get pictures from
	 * @param amount
	 *            number of pictures to read, limited by {@link #latestPictureLimit}
	 * @return list of pictures visible to current user
	 */
	protected List<Picture> _getPicturesMeta(String user, int amount) {
		if (amount > latestPictureLimit) {
			LOG.info("latest picture request was ({}) greater than max allowed {}. Only returning max", amount,
					latestPictureLimit);
			amount = latestPictureLimit;
		}

		// TODO only return pictures visible to the current user
		MongoIterable<Picture> it = factory.getCollection(Picture.class).find(
				QueryBuilder.query("user.$id").is(user).toDocument(), Picture.class).limit(amount).sort(
				new Document("uploaded", 1));
		return Lists.newArrayList(it);
	}

	/**
	 * serves the requested picture thumbnail {id} for the given {user}
	 * 
	 * @param user
	 *            user to lookup picture
	 * @param id
	 *            id of the picture to load
	 * @return the picture which belongs to the given id, or {@link org.springframework.http.HttpStatus#NOT_FOUND} if no
	 *         such picture exists
	 * @throws IOException
	 * @since 1.0.0
	 * @see {@link #_getPicture(String, String, String)}
	 */
	@ResponseBody
	@RequestMapping(value = "/{user}/{id:[a-f0-9]+}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE, params = { "f=t" })
	public ResponseEntity<Resource> getPictureThumbnail(@PathVariable("user") String user, @PathVariable("id") String id)
			throws IOException {
		return _getPicture(user, id, "_t");
	}

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
	 * @see {@link #_getPicture(String, String, String)}
	 */
	@ResponseBody
	@RequestMapping(value = "/{user}/{id:[a-f0-9]+}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<Resource> getPictureOriginal(@PathVariable("user") String user, @PathVariable("id") String id)
			throws IOException {
		return _getPicture(user, id, "");
	}

	/**
	 * actual method retrieving the picture from disk. Requested picture is only returned if the current user is allowed
	 * to view it
	 * 
	 * @param user
	 * @param id
	 * @param type
	 * @return
	 * @throws IOException
	 */
	protected ResponseEntity<Resource> _getPicture(String user, String id, String type) throws IOException {
		File picture = fileUtil.getFileHandle(id + type);
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
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> handleFileUpload(MultipartHttpServletRequest request) {
		List<String> badFiles = Lists.newArrayList();
		for (Iterator<String> it = request.getFileNames(); it.hasNext();) {
			MultipartFile file = request.getFile(it.next());
			// Create uuid and Picture entity
			Picture picture = factory.create(Picture.class);
			User user = factory.load(User.class, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			picture.setUser(user);
			picture.setTitle(StringUtils.split(file.getOriginalFilename(), ".")[0]);
			picture.setId(generateId());
			picture.setOriginalName(file.getOriginalFilename());
			picture.setUploaded(DateTime.now());

			String type = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

			try {
				File storedPicture = fileUtil.getFileHandle(picture.getId());
				// save picture
				file.transferTo(storedPicture);

				// read some some properties from it
				BufferedImage image = ImageIO.read(storedPicture);
				picture.setHeight(image.getHeight());
				picture.setWidth(image.getWidth());
				picture.setAccess(Access.PRIVATE);// TODO for now only private access
				createThumbnail(picture.getId(), image, type);
				LOG.info("Uploaded {} and assigned id {}", file.getOriginalFilename(), picture.getId());
				// TODO can't put everything into one folder
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

	/**
	 * creates the Thumbnail for the given picture and stores it on the disk
	 * 
	 * @param id
	 * @param image
	 * @param type
	 */
	private void createThumbnail(String id, BufferedImage image, String type) {
		int height = image.getHeight();
		int width = image.getWidth();
		double scale = maxHeight / (double) height;
		BufferedImage thumbnail = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY,
				((Double) (width * scale)).intValue(), ((Double) (height * scale)).intValue(), Scalr.OP_ANTIALIAS);
		try {
			ImageIO.write(thumbnail, type, fileUtil.getFileHandle(id + "_t"));
		} catch (IOException e) {
			LOG.error("failed to create thumbnail for picture", e);
		}
	}
}

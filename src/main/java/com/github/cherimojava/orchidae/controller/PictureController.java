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
import static java.lang.String.format;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import com.mongodb.client.FindIterable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonString;
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
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.controller.api.UploadResponse;
import com.github.cherimojava.orchidae.entity.Access;
import com.github.cherimojava.orchidae.entity.BatchUpload;
import com.github.cherimojava.orchidae.entity.Picture;
import com.github.cherimojava.orchidae.entity.User;
import com.github.cherimojava.orchidae.util.FileUtil;
import com.github.cherimojava.orchidae.util.UserUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoIterable;

/**
 * Does the handling of uploading and serving pictures
 * 
 * @author philnate
 */
@RestController
@RequestMapping(value = "/picture")
public class PictureController {

	/**
	 * URI pattern for pictures
	 */
	public static final String PICTURE_URI = "/{user}/{id:[a-f0-9]+}";

	@Value("${limit.latestPictures:30}")
	int latestPictureLimit;

	@Value("${picture.small.maxHeight:300}")
	int maxHeight;

	@Autowired
	EntityFactory factory;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserUtil userUtil;

	/**
	 * identifier on clientside for batch
	 */
	protected static final String BATCH_IDENTIFIER = "batch";

	private static final Logger LOG = LogManager.getLogger();

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
	@RequestMapping(value = "/{user}/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PostFilter("@paa.hasAccess(filterObject.id)")
	public List<Picture> latestPicturesMetaByUserLimit(@PathVariable("user") String user,
			@RequestParam(value = "n", required = false) Integer number) {
		if (number == null || number > latestPictureLimit) {
			LOG.info("latest picture request was ({}) greater than max allowed {}. Only returning max", number,
					latestPictureLimit);
			number = latestPictureLimit;
		}

		MongoIterable<Picture> it = factory.getCollection(Picture.class).find(
				new BsonDocument("user", new BsonString(user)), Picture.class).limit(number).sort(
				new Document("order", -1));
		return Lists.newArrayList(it);
	}

	/**
	 * returns the total number of pictures for this user according to the permissions of the requester. E.g. the owner
	 * will get private pictures included, while everyone else doesn't
	 * 
	 * @param user
	 *            to retrieve the count of pictures from
	 * @return
	 * @since 1.0.0
	 */
	@RequestMapping(value = "/{user}/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> latestPicturesMetaByUserLimit(@PathVariable("user") String user) {
		BsonDocument query = new BsonDocument("user", new BsonString(user));
				String curUser = UserUtil.getLoggedInUser();
		if (!StringUtils.equals(curUser,user)) {
			query.append("access",new BsonString(Access.PUBLIC.toString()));
		}
		return new ResponseEntity<>(format("{'count':%d}", factory.getCollection(Picture.class).count(query)),HttpStatus.OK);
	}

	/**
	 * serves the requested picture {id} and size {f} for the given {user}
	 * 
	 * @param user
	 *            user to lookup picture
	 * @param id
	 *            id of the picture to load
	 * @param format
	 *            the format/Size of the picture to return
	 * @return the picture which belongs to the given id, or {@link org.springframework.http.HttpStatus#NOT_FOUND} if no
	 *         such picture exists
	 * @throws IOException
	 * @since 1.0.0
	 * @see {@link #_getPicture(String, String)}
	 */
	@ResponseBody
	@RequestMapping(value = PICTURE_URI, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE, params = "f")
	@PreAuthorize("@paa.hasAccess(#id)")
	public ResponseEntity<Resource> getPicture(@PathVariable("user") String user, @PathVariable("id") String id,
			@RequestParam(value = "f") String format) throws IOException {
		ResponseEntity resp;
		switch (format) {
		case "s":// small image
			return _getPicture(id, "_s");
		case "o":// Original
			return _getPicture(id, "");
		default:// All unknown garbage
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * serves the requested picture {id} metadata for the given {user}
	 * 
	 * @param user
	 *            user to lookup picture
	 * @param id
	 *            picture id to lookup
	 * @return picture metadata
	 */
	@ResponseBody
	@RequestMapping(value = PICTURE_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = "!f")
	@PreAuthorize("@paa.hasAccess(#id)")
	public ResponseEntity<Picture> getPictureMeta(@PathVariable("user") String user, @PathVariable("id") String id) {
		Picture pic = factory.load(Picture.class, id);
		return (pic != null) ? new ResponseEntity<>(pic, HttpStatus.OK) : new ResponseEntity<Picture>(
				HttpStatus.NOT_FOUND);
	}

	/**
	 * deletes the given picture {id} and all related data for the given {user}
	 * 
	 * @param user
	 *            user to lookup picture
	 * @param id
	 *            picture to delete
	 * @return appropriate HTTP code
	 */
	@ResponseBody
	@RequestMapping(value = PICTURE_URI, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("@paa.canDelete(#id)")
	public ResponseEntity<String> deletePicture(@PathVariable("user") String user, @PathVariable("id") String id) {
		Picture pic = factory.load(Picture.class, id);
		if (pic != null) {
			File f = fileUtil.getFileHandle(pic.getId());
			f.delete();
			new File(f.getAbsolutePath() + "_s").delete();
			pic.drop();
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * actual method retrieving the picture from disk. Requested picture is only returned if the current user is allowed
	 * to view it
	 * 
	 * @param id
	 *            identification of picture
	 * @param type
	 *            type of the picture eg _t for thumbnail etc.
	 * @return ResponseEntity containing the resource to the picture or NOT_FOUND
	 * @throws IOException
	 */
	private ResponseEntity<Resource> _getPicture(String id, String type) throws IOException {
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
	public ResponseEntity<UploadResponse> handleFileUpload(MultipartHttpServletRequest request,
			@RequestParam(value = "batch", required = false) String batchId) {
		List<String> badFiles = Lists.newArrayList();
		UploadResponse response = factory.create(UploadResponse.class);
		User user = userUtil.getUser((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		for (Iterator<String> it = request.getFileNames(); it.hasNext();) {
			MultipartFile file = request.getFile(it.next());
			// Create uuid and Picture entity
			Picture picture = factory.create(Picture.class);
			picture.setUser(user);
			picture.setTitle(StringUtils.split(file.getOriginalFilename(), ".")[0]);
			picture.setId(generateId());
			picture.setOriginalName(file.getOriginalFilename());
			picture.setUploadDate(DateTime.now());
			picture.setOrder(user.getPictureCount().incrementAndGet());

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
				createSmall(picture.getId(), image, type);
				LOG.info("Uploaded {} and assigned id {}", file.getOriginalFilename(), picture.getId());
				checkBatch(picture, batchId);
				picture.save();
				// after the picture is saved we can add it to the response
				response.addIds(picture.getId());
			} catch (Exception e) {
				LOG.warn("failed to store picture", e);
				badFiles.add(file.getOriginalFilename());
			}
		}
		user.save();// We should persist this information? Or should we rely on the persistence magic?
		if (badFiles.isEmpty()) {
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			response.setIds(Lists.<String> newArrayList());
			return new ResponseEntity<>(response.setMsg("Could not upload all files. Failed to upload: "
					+ Joiner.on(",").join(badFiles)), HttpStatus.OK);
		}
	}

	/**
	 * check if batching should applied to the current picture upload
	 * 
	 * @param pic
	 * @param batchId
	 */
	private void checkBatch(Picture pic, String batchId) {
		if (StringUtils.isNotEmpty(batchId)) {
			if (!FileUtil.validateId(batchId)) {
				// ignore the batching if the id isn't valid
				return;
			}
			BatchUpload batch = factory.load(BatchUpload.class, batchId);
			// if the batch doesn't exist, create it
			if (batch == null) {
				batch = factory.create(BatchUpload.class);
				batch.setUploadDate(DateTime.now()).setId(batchId);
			}
			batch.addPictures(pic);
			pic.setBatchUpload(batch);
			batch.save();
		}
	}

	/**
	 * creates the Thumbnail for the given picture and stores it on the disk
	 * 
	 * @param id
	 * @param image
	 * @param type
	 */
	private void createSmall(String id, BufferedImage image, String type) {
		int height = image.getHeight();
		int width = image.getWidth();
		double scale = maxHeight / (double) height;
		BufferedImage thumbnail = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY,
				((Double) (width * scale)).intValue(), ((Double) (height * scale)).intValue(), Scalr.OP_ANTIALIAS);
		try {
			ImageIO.write(thumbnail, type, fileUtil.getFileHandle(id + "_s"));
		} catch (IOException e) {
			LOG.error("failed to create thumbnail for picture", e);
		}
	}
}

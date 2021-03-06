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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.github.cherimojava.orchidae.hook.HookHandler;
import org.bson.Document;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.mongo.entity.EntityUtils;
import com.github.cherimojava.data.spring.EntityConverter;
import com.github.cherimojava.orchidae.controller.api.UploadResponse;
import com.github.cherimojava.orchidae.api.entities.BatchUpload;
import com.github.cherimojava.orchidae.api.entities.Picture;
import com.github.cherimojava.orchidae.api.entities.User;
import com.github.cherimojava.orchidae.util.FileUtil;
import com.github.cherimojava.orchidae.util.UserUtil;
import com.mongodb.client.MongoDatabase;

//TODO add test for permissions
public class _PictureController extends ControllerTestBase {
	private MockMvc mvc;

	@Autowired
	private EntityFactory factory;

	private String user = ownr;

	@Autowired
	private MongoDatabase db;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UserUtil userUtil;

	@Autowired
	HookHandler hookHandler;

	private MockHttpSession session;

	PictureController controller;

	@Before
	public void setup() {
		controller = new PictureController();
		controller.userUtil = userUtil;
		controller.factory = factory;
		controller.fileUtil = fileUtil;
		controller.latestPictureLimit = 10;
		controller.hookHandler = hookHandler;

		mvc = MockMvcBuilders.standaloneSetup(controller).setMessageConverters(new EntityConverter(factory),
				new StringHttpMessageConverter(), new ResourceHttpMessageConverter()).build();

		setAuthentication(owner);

		// TODO this information should be pulled out from somewhere else
		factory.create(User.class).setUsername(ownr).setPassword("1").setMemberSince(DateTime.now()).setPictureCount(
				new AtomicInteger(0)).save();

		session = new MockHttpSession();
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext());
	}

	@After
	public void cleanUp() {
		userUtil._clear();
		db.getCollection(EntityUtils.getCollectionName(Picture.class)).deleteMany(new Document());
		db.getCollection(EntityUtils.getCollectionName(BatchUpload.class)).deleteMany(new Document());
		db.getCollection(EntityUtils.getCollectionName(User.class)).deleteMany(new Document());
	}

	@Test
	public void pictureUploadCheck() throws Exception {
		// check that there's no picture yet
		getLatest(10).andExpect(content().string("[]"));

		// upload one picture
		UploadResponse response = factory.readEntity(UploadResponse.class,
				createPicture("test", "jpg").andReturn().getResponse().getContentAsString());

		assertEquals(1, response.getIds().size());
		assertTrue(FileUtil.validateId(response.getIds().get(0)));
		// make sure the picture is there
		getLatest(10).andExpect(jsonPath("$[0].title", is("test"))).andExpect(
				jsonPath("$[0].originalName", is("test.jpg"))).andExpect(jsonPath("$[0].order", is(1)));

		userUtil._clear();
		// Upload another picture
		response = factory.readEntity(UploadResponse.class,
				createPicture("b", "png").andReturn().getResponse().getContentAsString());
		assertEquals(1, response.getIds().size());
		assertTrue(FileUtil.validateId(response.getIds().get(0)));

		// verify we have two pictures now
		getLatest(10).andExpect(jsonPath("$[1].title", is("test"))).andExpect(
				jsonPath("$[1].originalName", is("test.jpg"))).andExpect(jsonPath("$[1].order", is(1))).andExpect(
				jsonPath("$[0].title", is("b"))).andExpect(jsonPath("$[0].originalName", is("b.png"))).andExpect(
				jsonPath("$[0].order", is(2)));

		// check that if some IOException happens we get it returned appropriate
		MockMultipartFile file = new MockMultipartFile("b", "b.png", "image/png", "nonsensecontent".getBytes());
		mvc.perform(fileUpload("/picture").file(file).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(
				content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().string(containsString("b.png")));
	}

	@Test
	public void getPicture() throws Exception {
		// verify no picture there
		mvc.perform(get(url("acd3131"))).andExpect(status().isNotFound());
		// upload one picture
		createPicture("test", "jpeg");
		// retrieve its id
		MvcResult result = getLatest(10).andReturn();
		List<Picture> pictures = factory.readList(Picture.class, result.getResponse().getContentAsString());
		assertEquals(pictures.size(), 1);
		ByteArrayInputStream bais = new ByteArrayInputStream(
				mvc.perform(get(url(pictures.get(0).getId())).param("f", "o")).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
		BufferedImage image = ImageIO.read(bais);
		assertEquals(42, image.getWidth());
		assertEquals(42, image.getHeight());
	}

	@Test
	public void getSmall() throws Exception {
		controller.maxHeight = 10;
		// verify no picture there
		mvc.perform(get(url("acd3131"))).andExpect(status().isNotFound());
		// upload one picture
		createPicture("test", "jpeg");
		// retrieve its id
		MvcResult result = getLatest(10).andReturn();
		List<Picture> pictures = factory.readList(Picture.class, result.getResponse().getContentAsString());
		assertEquals(pictures.size(), 1);
		ByteArrayInputStream bais = new ByteArrayInputStream(
				mvc.perform(get(url(pictures.get(0).getId())).param("f", "s")).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
		BufferedImage image = ImageIO.read(bais);
		assertEquals(10, image.getWidth());
		assertEquals(10, image.getHeight());
	}

	@Test
	public void pictureLimit() throws Exception {
		// upload two files
		createPicture("a", "jpg");
		createPicture("b", "png");

		// check both are returned
		getLatest(10).andExpect(jsonPath("$", hasSize(2)));

		// lower limit and check that only one is returned
		controller.latestPictureLimit = 1;
		getLatest(10).andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void pictureOrdering() throws Exception {
		createPicture("one", "png");
		createPicture("two", "jpg");
		getLatest(10).andExpect(jsonPath("$[1].originalName", is("one.png"))).andExpect(
				jsonPath("$[0].originalName", is("two.jpg")));
	}

	@Test
	public void pictureSkipping() throws Exception {
		createPicture("one", "png");
		Thread.sleep(10);
		createPicture("two", "jpg");
		getLatest(1).andExpect(jsonPath("$[0].originalName", is("two.jpg")));
		getLatest(1,1).andExpect(jsonPath("$[0].originalName", is("one.png")));
		getLatest(10,-2).andExpect(jsonPath("$[1].originalName", is("one.png"))).andExpect(
				jsonPath("$[0].originalName", is("two.jpg")));;//shouldn't throw an exception, simply fallback to default
	}

	@Test
	public void onlyAlphanum() throws Exception {
		createPicture("one", "png");
		String resp = getLatest(1).andReturn().getResponse().getContentAsString();
		JsonNode json = new ObjectMapper().reader().readTree(resp);
		String id = json.get(0).get("_id").asText();
		mvc.perform(get(url(id))).andExpect(status().isOk());
		mvc.perform(get(url("../" + id))).andExpect(status().isNotFound());
		mvc.perform(get(url(id + "g"))).andExpect(status().isNotFound());
	}

	@Test
	public void pictureOnlyOfCurrentUser() throws Exception {
		createPicture("one", "png");
		getLatest(10).andExpect(jsonPath("$[0].originalName", is("one.png")));
		user = "nopictures";
		assertThat(getLatest(10).andReturn().getResponse().getContentAsString(), sameJSONAs("[]"));
	}

	@Test
	public void pictureCount() throws Exception {
		createPicture("one", "png");
		mvc.perform(get(url("count"))).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(
				content().string(sameJSONAs("{'count':1}"))).andExpect(status().isOk());
		user = "nopictures";
		mvc.perform(get(url("count"))).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(
				content().string(sameJSONAs("{'count':0}"))).andExpect(status().isOk());
	}

	@Test
	public void testAuthorizationDefined() throws NoSuchMethodException {
		assertTrue(PictureController.class.getMethod("getPicture", String.class, String.class, String.class).isAnnotationPresent(
				PreAuthorize.class));
		assertTrue(PictureController.class.getMethod("latestPicturesMetaByUserLimit", String.class, Integer.class, Integer.class).isAnnotationPresent(
				PostFilter.class));
		assertTrue(PictureController.class.getMethod("getPictureMeta", String.class, String.class).isAnnotationPresent(
				PreAuthorize.class));
		assertTrue(PictureController.class.getMethod("deletePicture", String.class, String.class).isAnnotationPresent(
				PreAuthorize.class));
	}

	@Test
	public void batchingWorking() throws Exception {
		String batchId = "0123456789abcdef";
		try (InputStream s = new ClassPathResource("gradient.png").getInputStream();) {

			byte[] bytes = StreamUtils.copyToByteArray(s);
			MockMultipartFile file = new MockMultipartFile("b", "b.png", "image/png", bytes);
			mvc.perform(
					fileUpload("/picture").file(file).accept(MediaType.APPLICATION_JSON).session(session).param(
							PictureController.BATCH_IDENTIFIER, batchId)).andExpect(status().isCreated()).andExpect(
					content().contentType(MediaType.APPLICATION_JSON));

			file = new MockMultipartFile("c", "c.png", "image/png", bytes);
			mvc.perform(
					fileUpload("/picture").file(file).accept(MediaType.APPLICATION_JSON).session(session).param(
							PictureController.BATCH_IDENTIFIER, batchId)).andExpect(status().isCreated()).andExpect(
					content().contentType(MediaType.APPLICATION_JSON));
		}
		getLatest(10).andExpect(jsonPath("$[1].title", is("b"))).andExpect(jsonPath("$[1].batchUpload", is(batchId))).andExpect(
				jsonPath("$[1].originalName", is("b.png"))).andExpect(jsonPath("$[0].title", is("c"))).andExpect(
				jsonPath("$[0].batchUpload", is(batchId))).andExpect(jsonPath("$[0].originalName", is("c.png")));
		assertEquals(2, factory.load(BatchUpload.class, batchId).getPictures().size());
	}

	@Test
	public void deletePicture() throws Exception {
		assertEquals(0, db.getCollection(EntityUtils.getCollectionName(Picture.class)).count());
		UploadResponse response = factory.readEntity(UploadResponse.class,
				createPicture("one", "png").andReturn().getResponse().getContentAsString());
		assertEquals(1, db.getCollection(EntityUtils.getCollectionName(Picture.class)).count());
		mvc.perform(delete(url(response.getIds().get(0)))).andExpect(status().isOk());
		assertEquals(0, db.getCollection(EntityUtils.getCollectionName(Picture.class)).count());
	}

	@Test
	public void deletePictureNotFound() throws Exception {
		mvc.perform(delete(url("doesntExist"))).andExpect(status().isNotFound());
	}

	private ResultActions createPicture(String name, String type) throws Exception {
		try (InputStream s = new ClassPathResource("gradient.png").getInputStream();) {

			byte[] bytes = StreamUtils.copyToByteArray(s);
			MockMultipartFile file = new MockMultipartFile(name, name + "." + type, "image/" + type, bytes);
			return mvc.perform(fileUpload("/picture").file(file).accept(MediaType.APPLICATION_JSON).session(session)).andExpect(
					status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(
					content().string(containsString("ids")));
		}
	}

	private ResultActions getLatest(int count) throws Exception {
		return getLatest(count,-1);
	}

	private ResultActions getLatest(int count,int skip) throws Exception {
		String query = "latest?n=" + count;
		if (skip != -1) {
			query = query + "&s="+skip;
		}
		return mvc.perform(get(url(query)).contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	public String url(String... parts) {
		return "/picture/" + user + "/" + super.url(parts);
	}
}

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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.spring.EntityConverter;
import com.github.cherimojava.orchidae.entity.Picture;
import com.mongodb.client.MongoDatabase;

public class _PictureController extends ControllerTestBase {
	private MockMvc mvc;

	@Autowired
	private EntityFactory factory;

	private String user = "test";

	@Autowired
	private MongoDatabase db;

	PictureController repo;

	@Before
	public void setup() {
		repo = new PictureController();
		repo.factory = factory;
		repo.storagePath = "./target";

		mvc = MockMvcBuilders.standaloneSetup(repo).setMessageConverters(new EntityConverter(factory),
				new StringHttpMessageConverter(), new ResourceHttpMessageConverter()).build();
	}

	@After
	public void cleanUp() {
		db.getCollection("pictures").deleteMany(new Document());
	}

	@Test
	public void pictureUploadCheck() throws Exception {
		// check that there's no picture yet
		getLatest(10).andExpect(content().string("[]"));

		// upload one picture
		createPicture("test", "jpg");

		// make sure the picture is there
		getLatest(10).andExpect(jsonPath("$[0].title", is("test"))).andExpect(
				jsonPath("$[0].originalName", is("test.jpg")));

		// Upload another picture
		createPicture("b", "png");

		// verify we have two pictures now
		getLatest(10).andExpect(jsonPath("$[0].title", is("test"))).andExpect(
				jsonPath("$[0].originalName", is("test.jpg"))).andExpect(jsonPath("$[1].title", is("b"))).andExpect(
				jsonPath("$[1].originalName", is("b.png")));

		// check that if some IOException happens we get it returned appropriate
		repo.storagePath = null;
		MockMultipartFile file = new MockMultipartFile("b", "b.png", "image/png", "nonsensecontent".getBytes());
		mvc.perform(fileUpload("/picture").file(file).accept(MediaType.TEXT_HTML)).andExpect(status().isOk()).andExpect(
				content().contentType(MediaType.TEXT_HTML)).andExpect(content().string(containsString("b.png")));
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
		mvc.perform(get(url(pictures.get(0).getId()))).andExpect(status().isOk());
	}

	@Test
	public void pictureLimit() throws Exception {
		// upload two files
		createPicture("a", "jpg");
		createPicture("b", "png");

		// check both are returned
		getLatest(10).andExpect(jsonPath("$", hasSize(2)));

		// lower limit and check that only one is returned
		repo.latestPictureLimit = 1;
		getLatest(10).andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void pictureOrdering() throws Exception {
		createPicture("one", "png");
		Thread.sleep(10);
		createPicture("two", "jpg");
        getLatest(10).andExpect(jsonPath("$[0].originalName",is("one.png"))).andExpect(jsonPath("$[1].originalName",is("two.jpg")));
	}

	private ResultActions createPicture(String name, String type) throws Exception {
		MockMultipartFile file = new MockMultipartFile(name, name + "." + type, "image/" + type,
				"nonsensecontent".getBytes());
		return mvc.perform(fileUpload("/picture").file(file).accept(MediaType.TEXT_HTML)).andExpect(
				status().isCreated()).andExpect(content().contentType(MediaType.TEXT_HTML));
	}
    
    private ResultActions getLatest(int count) throws Exception {
       return mvc.perform(get(url("latest", "10")).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(
                content().contentType(MediaType.APPLICATION_JSON));
    }

	public String url(String... parts) {
		return "/picture/" + user + "/" + super.url(parts);
	}
}

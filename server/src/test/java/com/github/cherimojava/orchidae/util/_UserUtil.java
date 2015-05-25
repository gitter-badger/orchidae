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
package com.github.cherimojava.orchidae.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.mongo.entity.EntityUtils;
import com.github.cherimojava.orchidae.SpringTestBase;
import com.github.cherimojava.orchidae.api.entities.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class _UserUtil extends SpringTestBase {

	@Autowired
	UserUtil userUtil;

	@Autowired
	EntityFactory factory;

	@Autowired
	MongoDatabase db;

	@Test
	public void removalListening() {
		String firstName = "firstName";
		for (int i = 0; i < userUtil.cacheSize + 2; i++) {
			// just create those users in time
			factory.create(User.class).setUsername("" + i).setPassword("123456").setMemberSince(DateTime.now()).setPictureCount(new AtomicInteger(10)).save();
			User u = userUtil.getUser("" + i);
			u.getPictureCount().incrementAndGet();
			u.setFirstName("" + i);
		}
		MongoCollection coll = db.getCollection(EntityUtils.getCollectionName(User.class));
		assertEquals(22, coll.count());

		MongoCursor<Document> cursor = coll.find().sort(new BsonDocument(Entity.ID, new BsonInt32(1))).iterator();
		Document doc = cursor.next();
		assertEquals("0", doc.get(Entity.ID));
		assertEquals("0", doc.get(firstName));
		doc = cursor.next();
		assertEquals("1", doc.get(Entity.ID));
		assertEquals("1", doc.get(firstName));
		doc = cursor.next();
		assertFalse(doc.containsKey(firstName));
	}

	@After
	public void cleanup() {
		db.getCollection(EntityUtils.getCollectionName(User.class)).drop();
	}
}

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
package com.github.cherimojava.orchidae.entity;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.annotation.Final;
import com.github.cherimojava.data.mongo.entity.annotation.Id;
import com.github.cherimojava.data.mongo.entity.annotation.Reference;

/**
 * Picture object contains everything an Object needs to have
 */
public interface Picture extends Entity<Picture> {

	/**
	 * globally unique id for the picture
	 */
	@Id
	public String getId();

	public Picture setId(String id);

	// TODO final should work here too?
	// @Final
	@NotNull(groups= Entity.Special.class)
	@Reference
	public User getUser();

	public Picture setUser(User user);

	/**
	 * title of the picture
	 * 
	 * @return
	 */
	public String getTitle();

	public Picture setTitle(String title);

	/**
	 * original upload filename of the picture
	 * 
	 * @return
	 */
	@Final
	public String getOriginalName();

	public Picture setOriginalName(String name);

	/**
	 * date the picture was uploaded
	 * 
	 * @return
	 */
	public DateTime getUploadDate();

	public Picture setUploadDate(DateTime uploadTime);

	/**
	 * original width of the picture
	 * 
	 * @return
	 */
	@Final
	public int getWidth();

	public Picture setWidth(int width);

	/**
	 * original height of the picture
	 * 
	 * @return
	 */
	@Final
	public int getHeight();

	public Picture setHeight(int height);

	/**
	 * access permissions of the picture (private, public etc.)
	 * 
	 * @return
	 */
	public Access getAccess();

	public Picture setAccess(Access access);

	/**
	 * the batch this picture was uploaded within
	 * 
	 * @return
	 */
	@Reference
	public BatchUpload getBatchUpload();

	public Picture setBatchUpload(BatchUpload batch);

	/**
	 * order number providing information about the ordering of the picture within the users timeline
	 * 
	 * @return
	 */
	public int getOrder();

	public Picture setOrder(int order);
}

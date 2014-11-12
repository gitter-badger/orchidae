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

import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.github.cherimojava.data.mongo.entity.annotation.Final;
import org.joda.time.DateTime;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.annotation.Id;

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

	@Final
	@NotNull
	public String getUser();

	public Picture setUser(String user);

	public String getTitle();

	public Picture setTitle(String title);

	@Final
	public String getOriginalName();

	public Picture setOriginalName(String name);

	public DateTime getUploaded();

	public Picture setUploaded(DateTime uploadTime);
}

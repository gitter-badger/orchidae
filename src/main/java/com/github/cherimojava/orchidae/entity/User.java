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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Entity for users on the system
 */
public interface User extends Entity<User> {

	/**
	 * username
	 * 
	 * @return
	 */
	@Id
	@NotNull
	public String getUsername();

	public User setUsername(String username);

	/**
	 * first name of the user
	 * 
	 * @return
	 */
	public String getFirstName();

	public User setFirstName(String firstname);

	/**
	 * last name of the user
	 * 
	 * @return
	 */
	public String getLastName();

	public User setLastName(String lastName);

	/**
	 * users encrypted password
	 * 
	 * @return
	 */
	@NotNull
	public String getPassword();

	public User setPassword(String password);

	/**
	 * date the user registered
	 * 
	 * @return
	 */
	@Final
	@NotNull
	public DateTime getMemberSince();

	public User setMemberSince(DateTime registered);

	public AtomicInteger getPictureCount();

	/**
	 * don't call this directly except for setup.
	 * @param i
	 * @return
	 */
	public User setPictureCount(AtomicInteger i);
}

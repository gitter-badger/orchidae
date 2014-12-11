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
package com.github.cherimojava.orchidae.security.authenticator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.entity.Access;
import com.github.cherimojava.orchidae.entity.Picture;

/**
 * Validator which checks if the given user is allowed to view the requested picture or not.
 *
 * @author philnate
 * @since 1.0.0
 */
public class PictureAccessAuthenticator {

	@Autowired
	EntityFactory factory;

	/**
	 * performs the access validation. Returns true if the picture is owned by the current user or is public
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasAccess(String id) {
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Picture pic = factory.load(Picture.class, id);
		return pic != null && (pic.getUser().getUsername().equals(user) || Access.PUBLIC.equals(pic.getAccess()));
	}
}

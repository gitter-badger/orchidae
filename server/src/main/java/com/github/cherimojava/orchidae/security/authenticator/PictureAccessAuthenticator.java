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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.api.entities.Access;
import com.github.cherimojava.orchidae.api.entities.Picture;

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
		PicInfo picInfo = getPictureInfo(id);
		return picInfo.picture != null
				&& (picInfo.picture.getUser().getUsername().equals(picInfo.user) || Access.PUBLIC.equals(picInfo.picture.getAccess()));
	}

	/**
	 * performs delete permission validation. Returns true only if the uploader is the equal to the current user,
	 * otherwise false
	 * 
	 * @param id
	 * @return
	 */
	public boolean canDelete(String id) {
		PicInfo picInfo = getPictureInfo(id);
		return picInfo.picture != null && StringUtils.equals(picInfo.picture.getUser().getUsername(), picInfo.user);
	}

	private PicInfo getPictureInfo(String id) {
		PicInfo picInfo = new PicInfo();
		picInfo.user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		picInfo.picture = factory.load(Picture.class, id);
		return picInfo;
	}

	/**
	 * some small helper class to pull out data
	 */
	private static class PicInfo {
		Picture picture;
		String user;
	}
}

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
package com.github.cherimojava.orchidae.hook;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import com.github.cherimojava.orchidae.api.entities.Access;
import com.github.cherimojava.orchidae.api.entities.Picture;
import com.github.cherimojava.orchidae.api.entities.User;
import com.github.cherimojava.orchidae.api.hook.Order;
import com.github.cherimojava.orchidae.api.hook.UploadHook;

/**
 * UploadHook providing the basic information of a picture, including the association with the user. This hook should be
 * ran as the first one during upload.
 * 
 * @author philnate
 * @since 1.0.0.
 */
@Order(category = Order.Category.SYSTEM, order = Integer.MAX_VALUE)
public class BasePictureInformationUploadHook implements UploadHook {

	@Override
	public void upload(Picture newPicture, User user, MultipartFile file, BufferedImage image) {
		newPicture.setUser(user);
		newPicture.setTitle(StringUtils.split(file.getOriginalFilename(), ".")[0]);
		newPicture.setOriginalName(file.getOriginalFilename());
		newPicture.setUploadDate(DateTime.now());
		newPicture.setOrder(user.getPictureCount().incrementAndGet());
		newPicture.setAccess(Access.PRIVATE);// TODO for now only private access

		// read some some properties from picture
		newPicture.setHeight(image.getHeight());
		newPicture.setWidth(image.getWidth());
	}
}

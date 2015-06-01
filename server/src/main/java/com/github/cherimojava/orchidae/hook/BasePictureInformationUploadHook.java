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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.github.cherimojava.orchidae.api.entities.Access;
import com.github.cherimojava.orchidae.api.entities.Picture;
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
	public void upload(UploadInfo ui) {
		Picture picture = ui.pictureUploaded;
		picture.setUser(ui.uploadingUser);
		picture.setTitle(StringUtils.split(ui.uploadedFile.getOriginalFilename(), ".")[0]);
		picture.setOriginalName(ui.uploadedFile.getOriginalFilename());
		picture.setUploadDate(DateTime.now());
		picture.setOrder(ui.uploadingUser.getPictureCount().incrementAndGet());
		picture.setAccess(Access.PRIVATE);// TODO for now only private access
		// read some some properties from picture
		picture.setHeight(ui.storedImage.getHeight());
		picture.setWidth(ui.storedImage.getWidth());
	}
}

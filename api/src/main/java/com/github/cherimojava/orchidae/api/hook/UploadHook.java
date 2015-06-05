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
package com.github.cherimojava.orchidae.api.hook;

import java.awt.image.BufferedImage;

import org.springframework.web.multipart.MultipartFile;

import com.github.cherimojava.orchidae.api.entities.Picture;
import com.github.cherimojava.orchidae.api.entities.User;

/**
 * hook being called during upload of a picture
 * 
 * @author philnate
 * @since 1.0.0
 */
@Hook
public interface UploadHook {

	/**
	 * this hook is called after the picture object has been created, but not yet saved. File being uploaded is provided
	 * as MultipartFile for further usage
	 * 
	 * @param ui
	 *            information container of picture Upload
	 */
	public void upload(UploadInfo ui);

	public static class UploadInfo {
		/**
		 * picture uploaded
		 */
		public Picture pictureUploaded;
		/**
		 * user uploading the picture
		 */
		public User uploadingUser;
		/**
		 * file uploaded
		 */
		public MultipartFile uploadedFile;
		/**
		 * actually stored picture
		 */
		public BufferedImage storedImage;
	}
}

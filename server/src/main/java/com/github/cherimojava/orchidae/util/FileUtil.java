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

import java.io.File;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class FileUtil {

	@Value("${storage.folderdepth:2}")
	int subfolders;

	private final int foldernameSize = 2;

	// TODO consolidate the storage of those properties
	@Value("${picture.path:./pictures}")
	String storagePath;

	private static char[] hex = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '0' };

	private LoadingCache<String, File> files = CacheBuilder.newBuilder().maximumSize(2000).build(
			new CacheLoader<String, File>() {
				@Override
				public File load(String key) throws Exception {
					File filePath = new File(storagePath);

					for (int i = 0; i < subfolders; i++) {
						filePath = new File(filePath, StringUtils.substring(key, i * foldernameSize, i * foldernameSize
								+ foldernameSize));
					}
					filePath.mkdirs();
					return filePath;
				}
			});

	public File getFileHandle(String id) {
		String substring = StringUtils.substring(id, 0, subfolders * foldernameSize);
		return new File(files.getUnchecked(substring), StringUtils.substring(id, subfolders * foldernameSize));
	}

	/**
	 * generates a hex id of the length 16
	 * 
	 * @return
	 */
	public static String generateId() {
		return RandomStringUtils.random(16, hex);
	}

	/**
	 * checks if the given id is 16 chars long and is based upon hex chars
	 * 
	 * @param id
	 * @return
	 */
	public static boolean validateId(String id) {
		return id != null && id.length() == 16 && StringUtils.containsOnly(id, hex);
	}
}

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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.github.cherimojava.orchidae.TestBase;

public class _FileUtil extends TestBase {

	private FileUtil sepator;

	@Before
	public void setup() {
		sepator = new FileUtil();
		sepator.subfolders = 2;
		sepator.storagePath = "target";
	}

	@Test
	public void fileSeparator() throws IOException {
		File f = sepator.getFileHandle("abcdefgh");
		assertEquals(new File("target", "ab/cd/efgh").getCanonicalPath(), f.getCanonicalPath());
		assertTrue(f.getParentFile().exists());
		assertFalse(f.exists());
	}

	@Test
	public void checkValidIdValidation() {
		assertTrue(FileUtil.validateId("0123456789abcdef"));
		assertFalse(FileUtil.validateId("-z"));
		assertFalse(FileUtil.validateId("x"));
		assertFalse(FileUtil.validateId("0123456"));
		assertFalse(FileUtil.validateId("0000000000000000000"));
	}
}

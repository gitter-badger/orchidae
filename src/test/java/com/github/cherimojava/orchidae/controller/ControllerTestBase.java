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
package com.github.cherimojava.orchidae.controller;

import static org.junit.Assert.assertThat;

import com.github.cherimojava.orchidae.TestBase;
import org.hamcrest.Matcher;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.cherimojava.orchidae.config.RootConfig;
import com.github.cherimojava.orchidae.config.WebMvcConfig;
import com.github.cherimojava.orchidae.config.cfgMongo;
import com.google.common.base.Joiner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { cfgMongo.class, WebMvcConfig.class })
@ActiveProfiles(RootConfig.PROFILE_TESTING)
@WebAppConfiguration
public class ControllerTestBase extends TestBase {

	public void assertJson(Matcher<? super String> expected, String actual) {
		assertThat(actual, expected);
	}

	/**
	 * join multiple parts to a string
	 * 
	 * @param parts
	 * @return
	 */
	public String url(String... parts) {
		return Joiner.on("/").join(parts);
	}
}

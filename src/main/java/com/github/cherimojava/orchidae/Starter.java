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
package com.github.cherimojava.orchidae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.github.cherimojava.orchidae.config.Markers;
import com.github.cherimojava.orchidae.config.RootConfig;

/**
 * Starts orchidae, pretty simple not much to see
 * 
 * @author philnate
 */
@EnableAutoConfiguration
public class Starter {

	public static Markers markers;

	/**
	 * application entry point
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.setProperty("spring.profiles.default", RootConfig.PROFILE_PRODUCTION);
		new SpringApplication(Starter.class, RootConfig.class).run(args);
	}
}

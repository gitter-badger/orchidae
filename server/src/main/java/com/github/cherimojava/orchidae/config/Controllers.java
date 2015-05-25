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
package com.github.cherimojava.orchidae.config;

import com.github.cherimojava.orchidae.controller.PictureController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.cherimojava.orchidae.controller.LayoutController;

/**
 * Activate explicitly controllers, all controller available are configured here
 *
 * @author philnate
 */
@Configuration
public class Controllers {

	@Bean
	public PictureController pictureRepository() {
		return new PictureController();
	}

	@Bean
	public LayoutController layoutController() {
		return new LayoutController();
	}
}

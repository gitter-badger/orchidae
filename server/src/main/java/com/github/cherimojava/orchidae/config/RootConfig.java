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

import com.github.cherimojava.orchidae.hook.HookHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main configuration only referring to other config files
 * 
 * @author philnate
 */
@Configuration
@Import({ Controllers.class, WebMvcConfig.class, cfgMongo.class, cfgSecurity.class, cfgHooks.class })
public class RootConfig {
	@Autowired
	private HookHandler handler;
	// config needed for production
	public static final String PROFILE_PRODUCTION = "production";
	// config needed for testing
	public static final String PROFILE_TESTING = "testing";
}

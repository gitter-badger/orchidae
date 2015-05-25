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

import static com.github.cherimojava.orchidae.config.RootConfig.PROFILE_PRODUCTION;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(PROFILE_PRODUCTION)
public class cfgProduction {

	@Value("${mongo.path:./mongo}")
	private String dbpath;

	private String mongoDBName = "orchidae";

	@Bean(name = "dbPath")
	public String dbpath() {
		return dbpath;
	}

	@Bean(name = "dbName")
	public String mongoDBName() {
		return mongoDBName;
	}
}

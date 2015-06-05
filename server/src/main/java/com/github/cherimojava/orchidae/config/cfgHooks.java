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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.cherimojava.orchidae.api.hook.Hook;
import com.github.cherimojava.orchidae.hook.HookHandler;

/**
 * Hook related configurations
 * 
 * @author philnate
 */
@Configuration
public class cfgHooks {

	@Bean
	public HookHandler hookHandler(org.reflections.Configuration config, URL pluginURL) {
		return new HookHandler(config, pluginURL);
	}

	@Bean
	public org.reflections.Configuration config() {
		return new ConfigurationBuilder().setScanners(new TypeAnnotationsScanner()).setUrls(
				ClasspathHelper.forPackage(Hook.class.getPackage().getName()));
	}

	@Bean
	public URL pluginURL() throws MalformedURLException {
		return new File("./plugins").toURI().toURL();
	}
}

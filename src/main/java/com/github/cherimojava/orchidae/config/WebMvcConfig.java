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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.spring.EntityConverter;

/**
 * MvcConfiguration
 * 
 * @author philnate
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	private static final File webapp = new File(SystemUtils.getUserDir(), "webapp");

	@Autowired
	EntityFactory factory;

	/**
	 * velocityconfiguration setting the path for the resources to ./webapp folder instead of convention value
	 * 
	 * @return
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resource/**").addResourceLocations("file:" + webapp.toString() + "/resources/");
		registry.addResourceHandler("/favicon.ico").addResourceLocations(
				"file:" + webapp.toString() + "/resources/img/favicon.ico");
	}

	@Bean
	public VelocityConfig velocityConfig() {
		Properties p = new Properties();
		p.put("resource.loader", "webapp");
		p.put("webapp.resource.loader.path", new File(webapp, "html").toString());
		p.put("webapp.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		VelocityConfigurer vc = new VelocityConfigurer();
		VelocityEngine engine = new VelocityEngine(p);
		engine.init();
		vc.setVelocityEngine(engine);
		return vc;
	}

	@Bean
	public CommonsMultipartResolver filterMultipartResolver() {
		return new CommonsMultipartResolver();
	}

	/**
	 * actual resolver for the html files
	 * 
	 * @return
	 */
	@Bean
	public VelocityViewResolver velocityViewResolver() {
		VelocityViewResolver vvw = new VelocityViewResolver();
		vvw.setPrefix("");
		vvw.setSuffix(".html");
		// TODO in production we might actuall enable caching
		vvw.setCache(false);
		vvw.setExposeSpringMacroHelpers(true);
		return vvw;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// addDefaultHttpMessageConverters(converters);
		converters.add(entityConverter(factory));
		converters.add(new ByteArrayHttpMessageConverter());
		super.configureMessageConverters(converters);
	}

	public EntityConverter entityConverter(EntityFactory factory) {
		return new EntityConverter(factory);
	}

	@Bean
	public MessageDigest messageDigest() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA-256");
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setLocation(new FileSystemResource(new File("./application.properties")));
		return configurer;
	}
}

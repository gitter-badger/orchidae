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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.multipart.support.MultipartFilter;

/**
 * Modifies the filter chain to support csrf protected multipart uploads
 * 
 * @author philnate
 */
public class ServletListener extends AbstractSecurityWebApplicationInitializer implements ServletContextListener {
	AnnotationConfigWebApplicationContext ctx;
	private static Logger LOG = LogManager.getLogger();

	public ServletListener(AnnotationConfigWebApplicationContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			onStartup(sce.getServletContext());
		} catch (ServletException e) {
			LOG.fatal("Could not setup context, bailing out", e);
			System.exit(1);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// noop
	}

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		super.beforeSpringSecurityFilterChain(servletContext);
		insertFilters(servletContext, new MultipartFilter());
	}
}

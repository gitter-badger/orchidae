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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.servlet.MultipartConfigElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.github.cherimojava.orchidae.config.Markers;
import com.github.cherimojava.orchidae.config.RootConfig;
import com.github.cherimojava.orchidae.config.ServletListener;
import com.github.cherimojava.orchidae.config.WebMvcConfig;

/**
 * Starts orchidae, pretty simple not much to see
 * 
 * @author philnate
 */
public class Starter {

	public static Markers markers;

	private static final int DEFAULT_PORT = 8080;

	private static Logger LOG = LogManager.getLogger();

	public static void main(String[] args) throws Exception {
		new Starter(getPort(args));
	}

	private AnnotationConfigWebApplicationContext getContext() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		// set the Production profile to be active
		context.getEnvironment().setDefaultProfiles(RootConfig.PROFILE_PRODUCTION);
		context.register(RootConfig.class, WebMvcConfig.class);
		return context;
	}

	private ServletContextHandler getServletContextHandler(AnnotationConfigWebApplicationContext context)
			throws IOException {
		ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.addEventListener(new ContextLoaderListener(context));
		ServletHolder holder = new ServletHolder("default", new DispatcherServlet(context));
		holder.getRegistration().setMultipartConfig(new MultipartConfigElement("./tmp",20000000,20000000,200000));
		servletContext.addServlet(holder, "/*");
		servletContext.addEventListener(new ServletListener(context));
		servletContext.setResourceBase(new FileSystemResource(new File("./")).toString());
		servletContext.setSessionHandler(new SessionHandler());
		return servletContext;
	}

	private Starter(int port) throws Exception {
		Server server = new Server(8082);
		server.setHandler(getServletContextHandler(getContext()));
		server.start();
		server.join();
	}

	private static int getPort(String[] args) {
		if (args.length > 0) {
			try {
				return Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				LOG.info("Could not parse port falling back to default", e);
			}
		}
		return DEFAULT_PORT;
	}
}

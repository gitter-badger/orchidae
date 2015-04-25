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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.cherimojava.orchidae.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.entity.User;
import com.google.common.collect.ImmutableList;

/**
 * Controller simply returning the page layouts
 */
@Controller
public class LayoutController {

	private static final Logger LOG = LogManager.getLogger();
	@Autowired
	PasswordEncoder pwEncoder;

	@Autowired
	private EntityFactory factory;

	protected static final String UPLOAD_PAGE = "upload";

	private static final List<String> formPages = ImmutableList.of("register", "login", UPLOAD_PAGE);
	protected static final String BATCH = "batch";

	/**
	 * Serves page layouts. Anything which ends with html is supposed to be a layout and will be handled through this
	 * method
	 */
	@RequestMapping(value = "/**/{page}.html", method = GET)
	public String layout(@PathVariable("page") String page, HttpSession session, HttpServletRequest request,
			ModelMap map) {
		if (formPages.contains(page)) {
			// For form pages we have to add the csrf token
			map.addAttribute("_csrf", request.getAttribute(CsrfToken.class.getName()));
		}
		if (UPLOAD_PAGE.equals(page)) {
			// provide some batch uuid, which will later be used to group all those pictures uploaded in a batch
			map.addAttribute(BATCH, FileUtil.generateId());
		}
		return "layout/" + page;
	}

	@RequestMapping(value = "/**/{form}.form", method = POST)
	public Object processForm(HttpServletRequest request, @PathVariable("form") String form) {
		switch (form.toLowerCase()) {
		case "register":
			return registerUser(request);
		default:
			return new ResponseEntity<>("Not Found", HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping("/")
	String home() {
		return "index";
	}

	private ResponseEntity registerUser(HttpServletRequest request) {
		// TODO send messages out if something isn't right
		if (StringUtils.isEmpty(request.getParameter("username"))) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		if (factory.load(User.class, request.getParameter("username")) != null) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		String pwd = request.getParameter("password");
		if (StringUtils.isNotEmpty(pwd) && pwd.equals(request.getParameter("password2"))) {
			User newUser = factory.create(User.class);
			newUser.setMemberSince(DateTime.now());
			newUser.setUsername(request.getParameter("username"));
			newUser.setPassword(pwEncoder.encode(pwd));
			newUser.setPictureCount(new AtomicInteger(0));
			newUser.save();
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}

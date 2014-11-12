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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

	private static Logger LOG = LogManager.getLogger();

	@Autowired
	MessageDigest md;

	@Autowired
	String salt;

	@Autowired
	PasswordEncoder pwEncoder;

	@Autowired
	private EntityFactory factory;
	// switch to ajax requests http://blog.trifork.com/2014/03/20/web-forms-with-java-angularjs-and-other-approaches/
	private static final List<String> formPages = ImmutableList.of("register", "login");

	@RequestMapping(value = "/layout/{page}", method = GET)
	public String layout(@PathVariable("page") String page, HttpServletRequest request, ModelMap map) {
		if (formPages.contains(page)) {
			// For form pages we have to add the csrf token
			map.addAttribute("_csrf", request.getAttribute(CsrfToken.class.getName()));
		}
		return "layout/" + page;
	}

	@RequestMapping(value = "/layout/{page}", method = POST)
	public ResponseEntity<String> processForm(HttpServletRequest request, @PathVariable("page") String page) {
		if (!formPages.contains(page)) {
			return new ResponseEntity<>("Not Found", HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	@RequestMapping("/")
	String home() {
		return "index";
	}

	@RequestMapping(value = "/register", method = POST)
	public String registerUser(HttpServletRequest request) {
		// this needs a hell of improvement
		if (StringUtils.isEmpty(request.getParameter("username"))) {
			return "redirect:/";
		}
		if (factory.load(User.class, request.getParameter("username")) != null) {
			return "redirect:/";
		}
		String pwd = request.getParameter("password");
		if (StringUtils.isNotEmpty(pwd) && pwd.equals(request.getParameter("password2"))) {
			User newUser = factory.create(User.class);
			newUser.setMemberSince(DateTime.now());
			newUser.setUsername(request.getParameter("username"));
			newUser.setPassword(pwEncoder.encode(pwd));
			newUser.save();
		}
		return "redirect:/";
	}

	private String toHex(byte[] bytes) {
		return String.format("%x", new BigInteger(bytes));
	}
}

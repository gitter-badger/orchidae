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

import com.google.common.collect.ImmutableList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller simply returning the page layouts
 */
@Controller
public class LayoutController {

    private static final List<String> formPages = ImmutableList.of("register", "login");

    @RequestMapping(value = "/layout/{page}", method = GET)
    public String layout(@PathVariable("page") String page, HttpServletRequest request, ModelMap map) {
        if (formPages.contains(page)) {
            //For form pages we have to add the csrf token
            map.addAttribute("_csrf", request.getAttribute(CsrfToken.class.getName()));
        }
        return "layout/" + page;
    }

    @RequestMapping(value = "/layout/{page}", method = POST)
    public ResponseEntity<String> processForm(HttpServletRequest request, @PathVariable("page") String page) {
        if (!formPages.contains(page)) {
            return new ResponseEntity<String>("Not Found", HttpStatus.BAD_REQUEST);
        }
        return null;
    }


    @RequestMapping("/")
    String home() {
        return "index";
    }

    @RequestMapping(value = "/register", method = POST)
    public void registerUser(HttpServletRequest request) {
        System.out.println("registration");
    }
}

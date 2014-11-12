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
package com.github.cherimojava.orchidae.security;

import static com.github.cherimojava.orchidae.config.Markers.AUTH;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authenticates users against MongoDB data store
 */
public class MongoAuthenticationProvider implements AuthenticationProvider {

	private static final String ERROR_MSG = "Incorrect password and/or username";

	private static Logger LOG = LogManager.getLogger();

	@Autowired
	private PasswordEncoder pwEncoder;

	@Autowired
	UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		LOG.info(AUTH, "login attempt for user {}", authentication.getName());
		UserDetails details = userDetailsService.loadUserByUsername((String) authentication.getPrincipal());

		if (details == null || !pwEncoder.matches((String) authentication.getCredentials(), details.getPassword())) {
			LOG.info(AUTH, "failed to authenticate user {}", authentication.getName());
			throw new BadCredentialsException(ERROR_MSG);
		}

		LOG.info(AUTH, "login attempt for user {}", authentication.getName());

		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
				details.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}
}

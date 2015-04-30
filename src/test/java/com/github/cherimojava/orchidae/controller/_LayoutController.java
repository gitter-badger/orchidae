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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockSettings;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.ui.ModelMap;

import com.github.cherimojava.orchidae.util.FileUtil;

public class _LayoutController extends ControllerTestBase {

	LayoutController controller;

	@Before
	public void setup() {
		controller = new LayoutController();
		controller.tokenRepository = mock(CsrfTokenRepository.class, RETURNS_DEEP_STUBS);
	}

	@Test
	public void verifyValidBatchIdProvided() {
		ModelMap modelMap = new ModelMap();
		controller.layout(LayoutController.UPLOAD_PAGE, mock(HttpSession.class), mock(HttpServletRequest.class),
				modelMap);
		assertTrue(FileUtil.validateId((String) modelMap.get(LayoutController.BATCH)));
	}
}

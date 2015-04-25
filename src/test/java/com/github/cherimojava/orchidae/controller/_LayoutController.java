package com.github.cherimojava.orchidae.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.github.cherimojava.orchidae.util.FileUtil;

public class _LayoutController extends ControllerTestBase {

	LayoutController controller;

	@Before
	public void setup() {
		controller = new LayoutController();
	}

	@Test
	public void verifyValidBatchIdProvided() {
		ModelMap modelMap = new ModelMap();
		controller.layout(LayoutController.UPLOAD_PAGE, mock(HttpSession.class), mock(HttpServletRequest.class),
				modelMap);
		assertTrue(FileUtil.validateId((String) modelMap.get(LayoutController.BATCH)));
	}
}

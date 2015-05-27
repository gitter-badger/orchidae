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
package com.github.cherimojava.orchidae.hook;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.cherimojava.orchidae.TestBase;
import com.github.cherimojava.orchidae.api.hook.Hook;
import com.github.cherimojava.orchidae.api.hook.Order;
import com.github.cherimojava.orchidae.api.hook.UploadHook;
import com.github.cherimojava.orchidae.config.cfgHooks;

public class _HookHandler extends TestBase {

	@Test
	public void hookDetection() throws MalformedURLException {
		Set set = HookHandler.getHookOrdering(TestHook.class, new File("./target").toURI().toURL());
		assertEquals(3, set.size());
		Iterator it = set.iterator();
		assertEquals(Systm.class, it.next().getClass());
		assertEquals(SystemLow.class, it.next().getClass());
		assertEquals(Custom.class, it.next().getClass());
	}

	@Test
	public void hookCollection() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(cfgHooks.class);
		HookHandler handler = ctx.getBean(HookHandler.class);
		assertEquals(0, handler.getHook(Hook.class).size());
		assertEquals(0, handler.getHook(UploadHook.class).size());
	}

	private static interface TestHook extends Hook {
	}

	@Order(order = 1, category = Order.Category.SYSTEM)
	public static class Systm implements TestHook {
	}

	@Order(order = 0, category = Order.Category.SYSTEM)
	public static class SystemLow implements TestHook {
	}

	@Order(order = 1, category = Order.Category.CUSTOM)
	public static class Custom implements TestHook {
	}
}

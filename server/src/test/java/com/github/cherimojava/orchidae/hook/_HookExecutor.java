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

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.cherimojava.orchidae.TestBase;
import com.github.cherimojava.orchidae.api.hook.Hook;
import com.github.cherimojava.orchidae.api.hook.Order;
import com.github.cherimojava.orchidae.config.cfgHooks;

public class _HookExecutor extends TestBase {

	@Test
	public void callAllHooks() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(cfgHooks.class);
		HookHandler handler = ctx.getBean(HookHandler.class);
		for (TestHook i : handler.getHook(TestHook.class)) {
			assertEquals(0, i.getCount());
		}
		handler.callHook(TestHook.class).callAll().callMe();
		for (TestHook i : handler.getHook(TestHook.class)) {
			assertEquals(1, i.getCount());
		}
	}

	@Hook
	private static interface TestHook {
		void callMe();

		int getCount();
	}

	@Order(order = 0, category = Order.Category.SYSTEM)
	public static class Hook1 implements TestHook {
		private int i = 0;

		@Override
		public void callMe() {
			i++;
		}

		@Override
		public int getCount() {
			return i;
		}
	}

	@Order(order = 1, category = Order.Category.CUSTOM)
	public static class Hook2 implements TestHook {
		private int i = 0;

		@Override
		public void callMe() {
			i++;
		}

		@Override
		public int getCount() {
			return i;
		}
	}
}

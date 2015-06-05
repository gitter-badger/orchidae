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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.SortedSet;

/**
 * HookExecutor allows to influence hook execution. Simplifying working with hooks
 * 
 * @author philnate
 * @param <H>
 *            Hook class
 * @since 1.0.0
 */
public class HookExecutor<H> {

	private final Class<H> hook;
	private final SortedSet<H> hooks;

	/**
	 * Constructs a new instance for the given hook and it's implementation
	 * 
	 * @param hook
	 *            hook being proxied
	 * @param hooks
	 *            hooks belonging to this proxy, not null
	 * @throws NullPointerException
	 *             if hooks is null
	 */
	protected HookExecutor(Class<H> hook, SortedSet<H> hooks) {
		this.hook = hook;
		this.hooks = checkNotNull(hooks);
	}

	/**
	 * Call all hook implementations
	 * 
	 * @return hook to be called
	 */
	public H callAll() {
		return createHandler();
	}

	/**
	 * creates a new Handler for the given hook and hook implementations
	 * 
	 * @return
	 */
	private H createHandler() {
		return (H) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { hook },
				new HookInvocationHandler<>(hook, hooks));
	}

	/**
	 * actual hook doing all of the functionality
	 * 
	 * @param <H>
	 *            Hook class
	 */
	private static class HookInvocationHandler<H> implements InvocationHandler {
		private final Class<H> hook;
		private final SortedSet<H> hooks;

		HookInvocationHandler(Class<H> hook, SortedSet<H> hooks) {
			this.hook = hook;
			this.hooks = checkNotNull(hooks);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			for (H hook : hooks) {
				method.invoke(hook, args);
			}
			return null;
		}
	}
}

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

/**
 * interface for Hook call handling, dynamic proxy
 * 
 * @author philnate
 * @param <H> Hook being stubbed here
 * @since 1.0.0
 */
public interface HookCaller<H> {
	/**
	 * Call all hook implementations associated to the given Hook
	 * @param arg
	 *            hook parameter handed to each hook called
	 * @return number of hooks called
	 */
	public H callAll();
}

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

import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.github.cherimojava.orchidae.api.hook.Hook;
import com.github.cherimojava.orchidae.api.hook.Order;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Performs all the Hook handling
 * 
 * @author philnate
 * @since 1.0.0
 */
public class HookHandler {

	private static Logger LOG = LogManager.getLogger();
	private static final HookComparator comparator = new HookComparator();
	private Map<Class<? extends Hook>, SortedSet<Hook>> hooks;
	private static final SortedSet<Hook> EMPTY_SET = ImmutableSortedSet.of();

	/**
	 * creates a new HookHandler containing all Hooks found
	 * 
	 * @param config
	 * @param pluginURL
	 */
	public HookHandler(Configuration config, URL pluginURL) {
		hooks = Maps.newHashMap();
		Reflections r = new Reflections(config);
		Set<Class<? extends Hook>> hookTypes = r.getSubTypesOf(Hook.class);
		for (Class<? extends Hook> c : hookTypes) {
			hooks.put(c, ImmutableSortedSet.copyOf(HookHandler.getHookOrdering(c, pluginURL)));
		}
	}

	/**
	 * Returns all hooks for the given hook Interface
	 * 
	 * @param hook
	 * @return
	 */
	public <T extends Hook> SortedSet<T> getHook(Class<T> hook) {
		return (SortedSet<T>) (hooks.containsKey(hook) ? hooks.get(hook) : EMPTY_SET);
	}

	public static <H extends Hook, C extends Class<H>> SortedSet<Hook> getHookOrdering(C hook, URL url) {
		Configuration config = new ConfigurationBuilder().addUrls(url).addUrls(ClasspathHelper.forPackage(HookHandler.class.getPackage().getName())).setScanners(new SubTypesScanner());
		Reflections reflect = new Reflections(config);
		SortedSet<Hook> hooks = Sets.newTreeSet(new ReverseComparator(comparator));
		LOG.info("Searching for hooks of {}",hook);
		for (Class<? extends H> c : reflect.getSubTypesOf(hook)) {
			try {
				LOG.info("Found hook {}", c);
				hooks.add(c.newInstance());
			} catch (IllegalAccessException | InstantiationException e) {
				LOG.error("Failed to instantiate {} please make sure it has a no param Constructor. {}", c, e);
			}
		}
		return hooks;
	}

	/**
	 * Hook ordering is following these principles:
	 * <ul>
	 * <li>Hooks with Order definition have higher precedence</li>
	 * <li>{@link com.github.cherimojava.orchidae.api.hook.Order.Category#SYSTEM} hooks before
	 * {@link com.github.cherimojava.orchidae.api.hook.Order.Category#CUSTOM}</li>
	 * <li>Higher order int, before lower ones</li>
	 * <li>Alphabetical ordering based on name for same ordering</li>
	 * </ul>
	 * 
	 * @author philnate
	 * @since 1.0.0
	 */
	protected static class HookComparator implements Comparator<Hook> {
		@Override
		public int compare(Hook o1, Hook o2) {
			boolean b1 = o1.getClass().isAnnotationPresent(Order.class);
			boolean b2 = o2.getClass().isAnnotationPresent(Order.class);
			if (!b1 && !b2) {
				return 0;
			} else if (b1 && !b2) {
				return 1;
			} else if (!b1 && b2) {
				return -1;
			} else {
				Order ord1 = o1.getClass().getAnnotation(Order.class);
				Order ord2 = o2.getClass().getAnnotation(Order.class);
				if (ord1.category() == ord2.category()) {
					int order = Integer.compare(ord1.order(), ord2.order());
					switch (order) {
					case -1:/* fallthrough */
					case 1:
						break;
					case 0:
						// need to flip as the lexicographic ordering is invers to the compare methodology
						return o2.getClass().getName().compareTo(o1.getClass().getName());
					}
					return order;
				} else if (ord1.category() == Order.Category.SYSTEM) {
					return 1;
				} else {
					return -1;
				}
			}
		}
	}
}

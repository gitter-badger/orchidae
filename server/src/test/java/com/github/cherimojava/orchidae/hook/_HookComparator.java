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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.cherimojava.orchidae.TestBase;
import com.github.cherimojava.orchidae.api.hook.Hook;
import com.github.cherimojava.orchidae.api.hook.Order;

public class _HookComparator extends TestBase {
	private static HookHandler.HookComparator comparator;

	@BeforeClass
	public static void setup() {
		comparator = new HookHandler.HookComparator();
	}

	@AfterClass
	public static void tearDown() {
		comparator = null;
	}

	@Test
	public void equality() {
		assertEquals(0, comparator.compare(new DefaultOrder(), new DefaultOrder()));
		assertEquals(0, comparator.compare(new NoOrder(), new NoOrder()));
	}

	@Test
	public void annotatedNotAnnotated() {
		assertEquals(1, comparator.compare(new DefaultOrder(), new NoOrder()));
		assertEquals(-1, comparator.compare(new NoOrder(), new DefaultOrder()));
	}

	@Test
	public void systemCustomOrdering() {
		assertEquals(1, comparator.compare(new System(), new Custom()));
		assertEquals(-1, comparator.compare(new Custom(), new System()));
		assertEquals(0, comparator.compare(new System(), new System()));
		assertEquals(0, comparator.compare(new Custom(), new Custom()));
	}

	@Test
	public void orderCounts() {
		assertEquals(1, comparator.compare(new One(), new Two()));
		assertEquals(-1, comparator.compare(new Two(), new One()));
		assertEquals(0, comparator.compare(new One(), new One()));
		assertEquals(0, comparator.compare(new Two(), new Two()));
	}

	@Test
	public void alphabeticalOrdering() {
		assertEquals(1, comparator.compare(new A(), new B()));
		assertEquals(-1, comparator.compare(new B(), new A()));
		assertEquals(0, comparator.compare(new A(), new A()));
		assertEquals(0, comparator.compare(new B(), new B()));
	}

	@Order()
	@Hook
	private static class DefaultOrder {
		// just some empty order should give it higher priority
	}

	@Hook
	private static class NoOrder {

	}

	@Order(category = Order.Category.SYSTEM)
	@Hook
	private static class System {

	}

	@Order(category = Order.Category.CUSTOM)
	@Hook
	private static class Custom {
	}

	@Order(category = Order.Category.SYSTEM, order = 1)
	@Hook
	private static class One {

	}

	@Order(category = Order.Category.SYSTEM, order = 0)
	@Hook
	private static class Two {

	}

	@Order(category = Order.Category.SYSTEM, order = 1)
	@Hook
	private static class A {

	}

	@Order(category = Order.Category.SYSTEM, order = 1)
	@Hook
	private static class B {

	}
}

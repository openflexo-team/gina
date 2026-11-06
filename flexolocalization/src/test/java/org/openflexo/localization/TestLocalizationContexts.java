/**
 *
 * Copyright (c) 2013-2014, Openflexo
 *
 * This file is part of Flexolocalization, a component of the software infrastructure
 * developed at Openflexo.
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or
 *          combining it with software containing parts covered by the terms
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. *
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.localization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openflexo.rm.ResourceLocator;

/**
 * Tests of the resolution of keys among several parents, of the creation of entries in a given context, and of the policy of a localizer
 * which may not create entries.
 */
public class TestLocalizationContexts {

	private LocalizedDelegateImpl platform;
	private LocalizedDelegateImpl adapter;
	private LocalizedDelegateImpl module;

	@Before
	public void setup() {
		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
		// Localizers held in memory: they create entries and write nothing
		platform = new LocalizedDelegateImpl(null) {
		};
		platform.setContext("platform");
		adapter = new LocalizedDelegateImpl(platform) {
		};
		adapter.setContext("ta:xlsx");
		module = new LocalizedDelegateImpl(platform) {
		};
		module.setContext("module:formod");
		module.addParent(adapter);
	}

	@Test
	public void testParentsOrder() {
		assertEquals(2, module.getParents().size());
		assertSame(platform, module.getParents().get(0));
		assertSame(adapter, module.getParents().get(1));
		assertEquals("module:formod", module.getContext());
	}

	@Test
	public void testKeyFoundInSecondParent() {
		adapter.registerNewEntry("cell_value", Language.ENGLISH, "Cell value");
		assertTrue(module.hasKey("cell_value", Language.ENGLISH, true));
		assertFalse(module.hasKey("cell_value", Language.ENGLISH, false));
		assertEquals("Cell value", module.localizedForKey("cell_value"));
	}

	@Test
	public void testFirstParentWins() {
		platform.registerNewEntry("shared", Language.ENGLISH, "From platform");
		adapter.registerNewEntry("shared", Language.ENGLISH, "From adapter");
		assertEquals("From platform", module.localizedForKey("shared"));
	}

	@Test
	public void testOwnEntryWinsOverParents() {
		platform.registerNewEntry("shared", Language.ENGLISH, "From platform");
		module.registerNewEntry("shared", Language.ENGLISH, "From module");
		assertEquals("From module", module.localizedForKey("shared"));
	}

	@Test
	public void testNewEntryCreatedInAskedContext() {
		module.localizedForKeyInContext("ta:xlsx", "new_cell_key");
		assertTrue(adapter.hasKey("new_cell_key", Language.ENGLISH, false));
		assertFalse(module.hasKey("new_cell_key", Language.ENGLISH, false));
		assertFalse(platform.hasKey("new_cell_key", Language.ENGLISH, false));
		assertEquals("New cell key", module.localizedForKey("new_cell_key"));
	}

	@Test
	public void testNewEntryCreatedInAncestorContext() {
		module.localizedForKeyInContext("platform", "very_common_key");
		assertTrue(platform.hasKey("very_common_key", Language.ENGLISH, false));
		assertFalse(module.hasKey("very_common_key", Language.ENGLISH, false));
	}

	@Test
	public void testUnknownContextFallsBackToAskedLocalizer() {
		module.localizedForKeyInContext("ta:nowhere", "orphan_key");
		assertTrue(module.hasKey("orphan_key", Language.ENGLISH, false));
	}

	@Test
	public void testNoContextCreatesInAskedLocalizer() {
		module.localizedForKey("plain_key");
		assertTrue(module.hasKey("plain_key", Language.ENGLISH, false));
		assertFalse(adapter.hasKey("plain_key", Language.ENGLISH, false));
	}

	@Test
	public void testNoCreationWhenNotAllowed() {
		// Directory-based, no automatic saving: this is the situation of an installation, where dictionaries are read-only
		LocalizedDelegateImpl readOnly = new LocalizedDelegateImpl(ResourceLocator.locateResource("TestLocales"), null, false, false);
		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		assertEquals("Some unknown key", readOnly.localizedForKey("some_unknown_key"));
		assertFalse(readOnly.hasKey("some_unknown_key", Language.FRENCH, false));
		assertEquals("Not a key", readOnly.localizedForKey("Not a key"));
		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
	}
}

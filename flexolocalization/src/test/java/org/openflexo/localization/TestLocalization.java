/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.localization;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * This unit test is intended to test localization features
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestLocalization {

	private static LocalizedDelegate localizer;

	@BeforeClass
	public static void setupClass() {
		localizer = new LocalizedDelegateImpl(ResourceLocator.locateResource("TestLocales"), null, true, false);
	}

	@Test
	@TestOrder(1)
	public void testTranslateToFrench() {
		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		assertEquals("Mot anglais", localizer.localizedForKey("english_word"));
		assertEquals("no_translation", localizer.localizedForKey("no_translation"));
		assertEquals("Bonjour World !", localizer.localizedForKeyWithParams("hello_($firstParameter)", this));
		assertEquals("Bonjour Pierre, Paul et Jacques !",
				localizer.localizedForKeyWithParams("hello_($0)_($1)_and_($2)", "Pierre", "Paul", "Jacques"));
	}

	@Test
	@TestOrder(2)
	public void testTranslateToEnglish() {
		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
		assertEquals("English word", localizer.localizedForKey("english_word"));
		assertEquals("No translation", localizer.localizedForKey("no_translation")); // Automatic english translation !!!
		assertEquals("Hello World !", localizer.localizedForKeyWithParams("hello_($firstParameter)", this));
		assertEquals("Hello Pierre, Paul and Jacques !",
				localizer.localizedForKeyWithParams("hello_($0)_($1)_and_($2)", "Pierre", "Paul", "Jacques"));
	}

	public String getFirstParameter() {
		return "World";
	}
}

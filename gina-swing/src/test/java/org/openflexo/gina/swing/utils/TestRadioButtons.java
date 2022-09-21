/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Fml-technologyadapter-ui, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.test.GenericFIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test FlexoConceptPanel fib
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestRadioButtons extends GenericFIBTestCase {

	public <T> FIBJPanel<T> instanciateFIB(Resource fibResource, T context, final Class<T> contextType) {
		return new FIBJPanel<T>(fibResource, context, ApplicationFIBLibraryImpl.instance(), FlexoLocalization.getMainLocalizer()) {

			@Override
			public Class<T> getRepresentedType() {
				return contextType;
			}

			@Override
			public void delete() {
			}

		};

	}

	private static SwingGraphicalContextDelegate gcDelegate;

	private static Resource fibResource;

	private static Family family;

	@BeforeClass
	public static void setupClass() {
		initGUI();
	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testLoadWidget() {

		fibResource = ResourceLocator.locateResource("TestRadioButtons.fib");
		assertTrue(fibResource != null);
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testValidateWidget() throws InterruptedException {

		validateFIB(fibResource);
	}

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void testInstanciateWidget() {

		family = new Family();

		for (Person person : family.getChildren()) {
			System.out.println(" > " + person);
		}

		FIBJPanel<Family> widget = instanciateFIB(fibResource, family, Family.class);

		gcDelegate.addTab("Test", widget.getController());
	}

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestRadioButtons.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}

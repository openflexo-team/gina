/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.fib.swing.utils.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.swing.utils.SwingGraphicalContextDelegate;
import org.openflexo.gina.FIBTestCase;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test some data manipulation
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class DataManagingTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel firstNameLabel;
	private static FIBTextField firstNameTF;
	private static FIBLabel lastNameLabel;
	private static FIBTextField lastNameTF;
	private static FIBLabel fullNameLabel;
	private static FIBTextField fullNameTF;

	private static FIBController controller;
	private static Family family1;
	private static Family family2;
	private static Family family3;

	/**
	 * Create an initial component
	 */
	@Test
	@TestOrder(1)
	public void test1CreateComponent() {

		component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);

		firstNameLabel = newFIBLabel("first_name");
		component.addToSubComponents(firstNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF = newFIBTextField();
		firstNameTF.setData(new DataBinding<String>("data.father.firstName", firstNameTF, String.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(firstNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(firstNameTF.getData().isValid());

		lastNameLabel = newFIBLabel("last_name");
		component.addToSubComponents(lastNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF = newFIBTextField();
		lastNameTF.setData(new DataBinding<String>("data.father.lastName", lastNameTF, String.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(lastNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(lastNameTF.getData().isValid());

		fullNameLabel = newFIBLabel("full_name");
		component.addToSubComponents(fullNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF = newFIBTextField();
		fullNameTF.setData(new DataBinding<String>("data.father.firstName + ' ' + data.father.lastName", fullNameTF, String.class,
				BindingDefinitionType.GET));
		component.addToSubComponents(fullNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(fullNameTF.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	@TestOrder(2)
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family1 = new Family();
		controller.setDataObject(family1);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(firstNameLabel));
		assertNotNull(controller.viewForComponent(firstNameTF));
		assertNotNull(controller.viewForComponent(lastNameLabel));
		assertNotNull(controller.viewForComponent(lastNameTF));
		assertNotNull(controller.viewForComponent(fullNameLabel));
		assertNotNull(controller.viewForComponent(fullNameTF));

		// controller.viewForComponent(firstNameTF).update();

		assertEquals("Robert", controller.viewForWidget(firstNameTF).getRepresentedValue());
		assertEquals("Smith", controller.viewForWidget(lastNameTF).getRepresentedValue());
		assertEquals("Robert Smith", controller.viewForWidget(fullNameTF).getRepresentedValue());
	}

	/**
	 * Update data object, sets new data and then update controller <br>
	 * Check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	public void test3ModifyValueInModel() {

		family2 = new Family();
		family2.getFather().setFirstName("Roger");
		family2.getFather().setLastName("Rabbit");
		controller.setDataObject(family2);
		assertEquals("Roger", controller.viewForWidget(firstNameTF).getRepresentedValue());
		assertEquals("Rabbit", controller.viewForWidget(lastNameTF).getRepresentedValue());
		assertEquals("Roger Rabbit", controller.viewForWidget(fullNameTF).getRepresentedValue());
	}

	/**
	 * Update data object, update controller with new data and then modify new data <br>
	 * Check that widgets have well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4ModifyValueInModel() {

		family3 = new Family();

		controller.setDataObject(family3);

		family3.getFather().setFirstName("Jeannot");
		family3.getFather().setLastName("Lapin");
		assertEquals("Jeannot", controller.viewForWidget(firstNameTF).getRepresentedValue());
		assertEquals("Lapin", controller.viewForWidget(lastNameTF).getRepresentedValue());
		assertEquals("Jeannot Lapin", controller.viewForWidget(fullNameTF).getRepresentedValue());
	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(DataManagingTest.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}

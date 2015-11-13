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

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.FIBTestCase;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.swing.utils.SwingGraphicalContextDelegate;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBDropDownWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBDropDown widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBDropDownWidgetTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel dropDownLabel1;
	private static FIBDropDown dropDown1;
	private static FIBLabel dropDownLabel2;
	private static FIBDropDown dropDown2;
	private static FIBLabel dropDownLabel3;
	private static FIBDropDown dropDown3;
	private static FIBLabel dropDownLabel4;
	private static FIBDropDown dropDown4;
	private static FIBLabel dropDownLabel5;
	private static FIBDropDown dropDown5;
	private static FIBLabel dropDownLabel6;
	private static FIBDropDown dropDown6;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	@TestOrder(1)
	public void test1CreateComponent() {

		component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);

		dropDownLabel1 = newFIBLabel("static_list_auto_select");
		component.addToSubComponents(dropDownLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false,
				false));
		dropDown1 = newFIBDropDown();
		dropDown1.setStaticList("value1,value2,value3");
		dropDown1.setAutoSelectFirstRow(true);
		component
				.addToSubComponents(dropDown1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		dropDownLabel2 = newFIBLabel("static_list_no_auto_select");
		component.addToSubComponents(dropDownLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false,
				false));
		dropDown2 = newFIBDropDown();
		dropDown2.setStaticList("value1,value2,value3");
		dropDown2.setAutoSelectFirstRow(false);
		component
				.addToSubComponents(dropDown2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		dropDownLabel3 = newFIBLabel("dynamic_list");
		component.addToSubComponents(dropDownLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false,
				false));
		dropDown3 = newFIBDropDown();
		dropDown3.setList(new DataBinding<List<?>>("data.children", dropDown3, List.class, BindingDefinitionType.GET));
		dropDown3.setAutoSelectFirstRow(true);
		component
				.addToSubComponents(dropDown3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown3.getList().isValid());

		dropDownLabel4 = newFIBLabel("dynamic_array");
		component.addToSubComponents(dropDownLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false,
				false));
		dropDown4 = newFIBDropDown();
		dropDown4.setArray(new DataBinding<Object[]>("data.parents", dropDown4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		dropDown4.setAutoSelectFirstRow(true);
		component
				.addToSubComponents(dropDown4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown4.getArray().isValid());

		dropDownLabel5 = newFIBLabel("dynamic_list_bound_to_data");
		component.addToSubComponents(dropDownLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false,
				false));
		dropDown5 = newFIBDropDown();
		dropDown5.setData(new DataBinding<Person>("data.biggestChild", dropDown5, Person.class,
				BindingDefinitionType.GET_SET));
		dropDown5.setList(new DataBinding<List<?>>("data.children", dropDown5, List.class, BindingDefinitionType.GET));
		dropDown5.setAutoSelectFirstRow(true);
		component
				.addToSubComponents(dropDown5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown5.getData().isValid());
		assertTrue(dropDown5.getList().isValid());

		dropDownLabel6 = newFIBLabel("enum");
		component.addToSubComponents(dropDownLabel6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false,
				false));
		dropDown6 = newFIBDropDown();
		dropDown6.setData(new DataBinding<Gender>("data.father.gender", dropDown6, Gender.class,
				BindingDefinitionType.GET_SET));
		dropDown6.setAutoSelectFirstRow(true);
		component
				.addToSubComponents(dropDown6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(dropDown6.getData().isValid());

		// dropDown1.setData(new DataBinding<String>("data.father.firstName",
		// firstNameTF, String.class, BindingDefinitionType.GET_SET));
		// component.addToSubComponents(dropDown1, new
		// TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		// assertTrue(firstNameTF.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	@TestOrder(2)
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE,
				FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(dropDownLabel1));
		assertNotNull(controller.viewForComponent(dropDown1));
		assertNotNull(controller.viewForComponent(dropDownLabel2));
		assertNotNull(controller.viewForComponent(dropDown2));
		assertNotNull(controller.viewForComponent(dropDownLabel3));
		assertNotNull(controller.viewForComponent(dropDown3));
		assertNotNull(controller.viewForComponent(dropDownLabel4));
		assertNotNull(controller.viewForComponent(dropDown4));

		assertEquals("value1", controller.viewForWidget(dropDown1).getData());
		assertEquals(null, controller.viewForWidget(dropDown2).getData());
		assertEquals(family.getChildren().get(0), controller.viewForWidget(dropDown3).getData());
		assertEquals(family.getParents()[0], controller.viewForWidget(dropDown4).getData());
		assertEquals(family.getJackies().get(2), controller.viewForWidget(dropDown5).getData());
		assertEquals(Gender.Male, controller.viewForWidget(dropDown6).getData());

		// assertEquals("Robert",
		// controller.viewForComponent(firstNameTF).getData());
		// assertEquals("Smith",
		// controller.viewForComponent(lastNameTF).getData());
		// assertEquals("Robert Smith",
		// controller.viewForComponent(fullNameTF).getData());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	public void test3ModifyValueInModel() {

		family.setBiggestChild(family.getChildren().get(0));
		family.getFather().setGender(Gender.Female);
		assertEquals(family.getChildren().get(0), controller.viewForWidget(dropDown5).getData());
		assertEquals(Gender.Female, controller.viewForWidget(dropDown6).getData());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4ModifyValueInWidget() {
		JFIBDropDownWidget<?> w5 = (JFIBDropDownWidget<?>) controller.viewForWidget(dropDown5);
		JFIBDropDownWidget<?> w6 = (JFIBDropDownWidget<?>) controller.viewForWidget(dropDown6);

		w5.getTechnologyComponent().getJComboBox().setSelectedItem(family.getChildren().get(1));
		w6.getTechnologyComponent().getJComboBox().setSelectedItem(Gender.Male);

		assertEquals(family.getChildren().get(1), family.getBiggestChild());
		assertEquals(Gender.Male, family.getFather().getGender());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(5)
	public void test5ModifyListValueInModel() {

		JFIBDropDownWidget<?> w5 = (JFIBDropDownWidget<?>) controller.viewForComponent(dropDown5);

		assertEquals(5, w5.getTechnologyComponent().getJComboBox().getModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getTechnologyComponent().getJComboBox().getModel().getSize());

		// System.out.println("new children=" + family.getChildren());
		// System.out.println("List model = " +
		// w5.getDynamicJComponent().getModel());

		family.setBiggestChild(junior);
		assertEquals(junior, controller.viewForWidget(dropDown5).getData());
		assertEquals(junior, w5.getTechnologyComponent().getJComboBox().getSelectedItem());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBDropDownWidgetTest.class.getSimpleName());
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

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
import org.openflexo.fib.swing.utils.SwingGraphicalContextDelegate;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Family.Gender;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBRadioButtonListWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBRadioButtonList widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBRadioButtonListWidgetTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel radioButtonListLabel1;
	private static FIBRadioButtonList radioButtonList1;
	private static FIBLabel radioButtonListLabel2;
	private static FIBRadioButtonList radioButtonList2;
	private static FIBLabel radioButtonListLabel3;
	private static FIBRadioButtonList radioButtonList3;
	private static FIBLabel radioButtonListLabel4;
	private static FIBRadioButtonList radioButtonList4;
	private static FIBLabel radioButtonListLabel5;
	private static FIBRadioButtonList radioButtonList5;
	private static FIBLabel radioButtonListLabel6;
	private static FIBRadioButtonList radioButtonList6;
	private static FIBLabel radioButtonListLabel7;
	private static FIBRadioButtonList radioButtonList7;

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

		radioButtonListLabel1 = newFIBLabel("static_radioButtonList_auto_select");
		component.addToSubComponents(radioButtonListLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList1 = newFIBRadioButtonList();
		radioButtonList1.setStaticList("value1,value2,value3");
		radioButtonList1.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		radioButtonListLabel2 = newFIBLabel("static_radioButtonList_no_auto_select");
		component.addToSubComponents(radioButtonListLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList2 = newFIBRadioButtonList();
		radioButtonList2.setStaticList("value1,value2,value3");
		radioButtonList2.setAutoSelectFirstRow(false);
		component.addToSubComponents(radioButtonList2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		radioButtonListLabel3 = newFIBLabel("dynamic_radioButtonList");
		component.addToSubComponents(radioButtonListLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList3 = newFIBRadioButtonList();
		radioButtonList3.setList(new DataBinding<List<?>>("data.children", radioButtonList3, List.class, BindingDefinitionType.GET));
		radioButtonList3.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList3.getList().isValid());

		radioButtonListLabel4 = newFIBLabel("dynamic_array");
		component.addToSubComponents(radioButtonListLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList4 = newFIBRadioButtonList();
		radioButtonList4.setArray(new DataBinding<Object[]>("data.parents", radioButtonList4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		radioButtonList4.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList4.getArray().isValid());

		radioButtonListLabel5 = newFIBLabel("dynamic_radioButtonList_bound_to_data");
		component.addToSubComponents(radioButtonListLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList5 = newFIBRadioButtonList();
		radioButtonList5
				.setData(new DataBinding<Person>("data.biggestChild", radioButtonList5, Person.class, BindingDefinitionType.GET_SET));
		radioButtonList5.setList(new DataBinding<List<?>>("data.children", radioButtonList5, List.class, BindingDefinitionType.GET));
		radioButtonList5.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList5.getData().isValid());
		assertTrue(radioButtonList5.getList().isValid());

		radioButtonListLabel6 = newFIBLabel("enum");
		component.addToSubComponents(radioButtonListLabel6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList6 = newFIBRadioButtonList();
		radioButtonList6
				.setData(new DataBinding<Gender>("data.father.gender", radioButtonList6, Gender.class, BindingDefinitionType.GET_SET));
		radioButtonList6.setAutoSelectFirstRow(true);
		component.addToSubComponents(radioButtonList6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList6.getData().isValid());

		radioButtonListLabel7 = newFIBLabel("dynamic_radioButtonList_bound_to_selection");
		component.addToSubComponents(radioButtonListLabel7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		radioButtonList7 = newFIBRadioButtonList();
		radioButtonList7.setList(new DataBinding<List<?>>("data.children", radioButtonList7, List.class, BindingDefinitionType.GET));
		radioButtonList7.setAutoSelectFirstRow(true);
		// radioButtonList7.setBoundToSelectionManager(true);
		component.addToSubComponents(radioButtonList7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(radioButtonList7.getList().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	@TestOrder(2)
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(radioButtonListLabel1));
		assertNotNull(controller.viewForComponent(radioButtonList1));
		assertNotNull(controller.viewForComponent(radioButtonListLabel2));
		assertNotNull(controller.viewForComponent(radioButtonList2));
		assertNotNull(controller.viewForComponent(radioButtonListLabel3));
		assertNotNull(controller.viewForComponent(radioButtonList3));
		assertNotNull(controller.viewForComponent(radioButtonListLabel4));
		assertNotNull(controller.viewForComponent(radioButtonList4));

		assertEquals("value1", controller.viewForWidget(radioButtonList1).getRepresentedValue());
		assertEquals(null, controller.viewForWidget(radioButtonList2).getRepresentedValue());
		assertEquals(family.getChildren().get(0), controller.viewForWidget(radioButtonList3).getRepresentedValue());
		assertEquals(family.getParents()[0], controller.viewForWidget(radioButtonList4).getRepresentedValue());
		assertEquals(family.getJackies().get(2), controller.viewForWidget(radioButtonList5).getRepresentedValue());
		assertEquals(Gender.Male, controller.viewForWidget(radioButtonList6).getRepresentedValue());

		// assertEquals("Robert", controller.viewForComponent(firstNameTF).getData());
		// assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		// assertEquals("Robert Smith", controller.viewForComponent(fullNameTF).getData());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	public void test3ModifyValueInModel() {

		family.setBiggestChild(family.getChildren().get(0));
		family.getFather().setGender(Gender.Female);
		assertEquals(family.getChildren().get(0), controller.viewForWidget(radioButtonList5).getRepresentedValue());
		assertEquals(Gender.Female, controller.viewForWidget(radioButtonList6).getRepresentedValue());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4ModifyValueInWidget() {
		JFIBRadioButtonListWidget<Person> w5 = (JFIBRadioButtonListWidget<Person>) controller.viewForWidget(radioButtonList5);
		JFIBRadioButtonListWidget<Gender> w6 = (JFIBRadioButtonListWidget<Gender>) controller.viewForWidget(radioButtonList6);

		w5.setSelectedValue(family.getChildren().get(1));
		w6.setSelectedValue(Gender.Male);

		assertEquals(family.getChildren().get(1), family.getBiggestChild());
		assertEquals(Gender.Male, family.getFather().getGender());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(5)
	public void test5ModifyListValueInModel() {

		JFIBRadioButtonListWidget<?> w5 = (JFIBRadioButtonListWidget<?>) controller.viewForComponent(radioButtonList5);

		assertEquals(5, w5.getMultipleValueModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getMultipleValueModel().getSize());

		// System.out.println("new children=" + family.getChildren());
		// System.out.println("List model = " + w5.getDynamicJComponent().getModel());

		family.setBiggestChild(junior);
		assertEquals(junior, controller.viewForWidget(radioButtonList5).getRepresentedValue());
		assertEquals(junior, w5.getSelectedValue());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBRadioButtonListWidgetTest.class.getSimpleName());
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

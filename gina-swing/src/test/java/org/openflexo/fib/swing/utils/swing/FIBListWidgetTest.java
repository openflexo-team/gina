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

import java.util.ArrayList;
import java.util.Collections;
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
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.sampleData.Family.Gender;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBListWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBList widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBListWidgetTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel listLabel1;
	private static FIBList list1;
	private static FIBLabel listLabel2;
	private static FIBList list2;
	private static FIBLabel listLabel3;
	private static FIBList list3;
	private static FIBLabel listLabel4;
	private static FIBList list4;
	private static FIBLabel listLabel5;
	private static FIBList list5;
	private static FIBLabel listLabel6;
	private static FIBList list6;
	private static FIBLabel listLabel7;
	private static FIBList list7;

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

		listLabel1 = newFIBLabel("static_list_auto_select");
		component.addToSubComponents(listLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list1 = newFIBList();
		list1.setStaticList("value1,value2,value3");
		list1.setAutoSelectFirstRow(true);
		component.addToSubComponents(list1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		listLabel2 = newFIBLabel("static_list_no_auto_select");
		component.addToSubComponents(listLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list2 = newFIBList();
		list2.setStaticList("value1,value2,value3");
		list2.setAutoSelectFirstRow(false);
		component.addToSubComponents(list2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		listLabel3 = newFIBLabel("dynamic_list");
		component.addToSubComponents(listLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list3 = newFIBList();
		list3.setList(new DataBinding<List<?>>("data.children", list3, List.class, BindingDefinitionType.GET));
		list3.setAutoSelectFirstRow(true);
		component.addToSubComponents(list3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list3.getList().isValid());

		listLabel4 = newFIBLabel("dynamic_array");
		component.addToSubComponents(listLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list4 = newFIBList();
		list4.setArray(new DataBinding<Object[]>("data.parents", list4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		list4.setAutoSelectFirstRow(true);
		component.addToSubComponents(list4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list4.getArray().isValid());

		listLabel5 = newFIBLabel("dynamic_list_bound_to_data");
		component.addToSubComponents(listLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list5 = newFIBList();
		list5.setData(new DataBinding<Person>("data.biggestChild", list5, Person.class, BindingDefinitionType.GET_SET));
		list5.setList(new DataBinding<List<?>>("data.children", list5, List.class, BindingDefinitionType.GET));
		list5.setAutoSelectFirstRow(true);
		component.addToSubComponents(list5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list5.getData().isValid());
		assertTrue(list5.getList().isValid());

		listLabel6 = newFIBLabel("enum");
		component.addToSubComponents(listLabel6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list6 = newFIBList();
		list6.setData(new DataBinding<Gender>("data.father.gender", list6, Gender.class, BindingDefinitionType.GET_SET));
		list6.setAutoSelectFirstRow(true);
		component.addToSubComponents(list6, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list6.getData().isValid());

		listLabel7 = newFIBLabel("dynamic_list_bound_to_selection");
		component.addToSubComponents(listLabel7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list7 = newFIBList();
		list7.setList(new DataBinding<List<?>>("data.children", list7, List.class, BindingDefinitionType.GET));
		list7.setAutoSelectFirstRow(true);
		list7.setBoundToSelectionManager(true);
		component.addToSubComponents(list7, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(list7.getList().isValid());

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
		assertNotNull(controller.viewForComponent(listLabel1));
		assertNotNull(controller.viewForComponent(list1));
		assertNotNull(controller.viewForComponent(listLabel2));
		assertNotNull(controller.viewForComponent(list2));
		assertNotNull(controller.viewForComponent(listLabel3));
		assertNotNull(controller.viewForComponent(list3));
		assertNotNull(controller.viewForComponent(listLabel4));
		assertNotNull(controller.viewForComponent(list4));

		assertEquals("value1", controller.viewForWidget(list1).getRepresentedValue());
		assertEquals(null, controller.viewForWidget(list2).getRepresentedValue());
		assertEquals(family.getChildren().get(0), controller.viewForWidget(list3).getRepresentedValue());
		assertEquals(family.getParents()[0], controller.viewForWidget(list4).getRepresentedValue());
		assertEquals(family.getJackies().get(2), controller.viewForWidget(list5).getRepresentedValue());
		assertEquals(Gender.Male, controller.viewForWidget(list6).getRepresentedValue());

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
		assertEquals(family.getChildren().get(0), controller.viewForWidget(list5).getRepresentedValue());
		assertEquals(Gender.Female, controller.viewForWidget(list6).getRepresentedValue());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4ModifyValueInWidget() {
		JFIBListWidget<?> w5 = (JFIBListWidget<?>) controller.viewForWidget(list5);
		JFIBListWidget<?> w6 = (JFIBListWidget<?>) controller.viewForWidget(list6);

		w5.getTechnologyComponent().setSelectedValue(family.getChildren().get(1), true);
		w6.getTechnologyComponent().setSelectedValue(Gender.Male, true);

		assertEquals(family.getChildren().get(1), family.getBiggestChild());
		assertEquals(Gender.Male, family.getFather().getGender());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(5)
	public void test5ModifyListValueInModel() {

		JFIBListWidget<?> w5 = (JFIBListWidget<?>) controller.viewForComponent(list5);

		assertEquals(5, w5.getTechnologyComponent().getModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getTechnologyComponent().getModel().getSize());

		// System.out.println("new children=" + family.getChildren());
		// System.out.println("List model = " + w5.getDynamicJComponent().getModel());

		family.setBiggestChild(junior);
		assertEquals(junior, controller.viewForWidget(list5).getRepresentedValue());
		assertEquals(junior, w5.getTechnologyComponent().getSelectedValue());

	}

	/**
	 * Try to select some objects, check that selection is in sync with it
	 */
	@Test
	@TestOrder(6)
	public void test6PerfomSomeTestsWithSelection() {

		JFIBListWidget<?> w7 = (JFIBListWidget<?>) controller.viewForComponent(list7);
		assertEquals(6, w7.getTechnologyComponent().getModel().getSize());

		System.out.println("w7.getSelection()=" + w7.getSelection());

		assertEquals(Collections.singletonList(family.getChildren().get(0)), w7.getSelection());

		// int[] indices = new int[3];
		// indices[0] = 1;
		// indices[1] = 2;
		// indices[2] = 4;
		// w7.getDynamicJComponent().setSelectedIndices(indices);
		w7.getTechnologyComponent().getSelectionModel().clearSelection();
		w7.getTechnologyComponent().getSelectionModel().addSelectionInterval(1, 2);
		w7.getTechnologyComponent().getSelectionModel().addSelectionInterval(4, 4);

		List<Person> expectedSelection = new ArrayList<Person>();
		expectedSelection.add(family.getChildren().get(1));
		expectedSelection.add(family.getChildren().get(2));
		expectedSelection.add(family.getChildren().get(4));

		assertEquals(expectedSelection, w7.getSelection());

		controller.setFocusedWidget(w7);
		assertEquals(expectedSelection, controller.getSelectionLeader().getSelection());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBListWidgetTest.class.getSimpleName());
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

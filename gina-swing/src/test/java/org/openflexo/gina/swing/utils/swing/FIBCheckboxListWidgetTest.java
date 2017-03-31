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

package org.openflexo.gina.swing.utils.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBCheckboxListWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import com.google.common.reflect.TypeToken;

/**
 * Test the structural and behavioural features of FIBCheckboxList widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBCheckboxListWidgetTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBLabel checkboxListLabel1;
	private static FIBCheckboxList checkboxList1;
	private static FIBLabel checkboxListLabel2;
	private static FIBCheckboxList checkboxList2;
	private static FIBLabel checkboxListLabel3;
	private static FIBCheckboxList checkboxList3;
	private static FIBLabel checkboxListLabel4;
	private static FIBCheckboxList checkboxList4;
	private static FIBLabel checkboxListLabel5;
	private static FIBCheckboxList checkboxList5;

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

		checkboxListLabel1 = newFIBLabel("static_checkboxList_auto_select");
		component.addToSubComponents(checkboxListLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList1 = newFIBCheckboxList();
		checkboxList1.setStaticList("value1,value2,value3");
		checkboxList1.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		checkboxListLabel2 = newFIBLabel("static_checkboxList_no_auto_select");
		component.addToSubComponents(checkboxListLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList2 = newFIBCheckboxList();
		checkboxList2.setStaticList("value1,value2,value3");
		checkboxList2.setAutoSelectFirstRow(false);
		component.addToSubComponents(checkboxList2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		checkboxListLabel3 = newFIBLabel("dynamic_checkboxList");
		component.addToSubComponents(checkboxListLabel3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList3 = newFIBCheckboxList();
		checkboxList3.setList(new DataBinding<List<?>>("data.children", checkboxList3, List.class, BindingDefinitionType.GET));
		checkboxList3.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(checkboxList3.getList().isValid());

		checkboxListLabel4 = newFIBLabel("dynamic_array");
		component.addToSubComponents(checkboxListLabel4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList4 = newFIBCheckboxList();
		checkboxList4.setArray(new DataBinding<Object[]>("data.parents", checkboxList4, new TypeToken<Object[]>() {
		}.getType(), BindingDefinitionType.GET));
		checkboxList4.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList4, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(checkboxList4.getArray().isValid());

		checkboxListLabel5 = newFIBLabel("dynamic_checkboxList_bound_to_data");
		component.addToSubComponents(checkboxListLabel5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkboxList5 = newFIBCheckboxList();
		checkboxList5.setData(new DataBinding<List<Person>>("data.jackies", checkboxList5, List.class, BindingDefinitionType.GET_SET));
		checkboxList5.setList(new DataBinding<List<?>>("data.children", checkboxList5, List.class, BindingDefinitionType.GET));
		// checkboxList5.setAutoSelectFirstRow(true);
		component.addToSubComponents(checkboxList5, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
		assertTrue(checkboxList5.getData().isValid());
		assertTrue(checkboxList5.getList().isValid());

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
		assertNotNull(controller.viewForComponent(checkboxListLabel1));
		assertNotNull(controller.viewForComponent(checkboxList1));
		assertNotNull(controller.viewForComponent(checkboxListLabel2));
		assertNotNull(controller.viewForComponent(checkboxList2));
		assertNotNull(controller.viewForComponent(checkboxListLabel3));
		assertNotNull(controller.viewForComponent(checkboxList3));
		assertNotNull(controller.viewForComponent(checkboxListLabel4));
		assertNotNull(controller.viewForComponent(checkboxList4));

		assertEquals(Collections.singletonList("value1"), controller.viewForWidget(checkboxList1).getRepresentedValue());
		assertEquals(null, controller.viewForWidget(checkboxList2).getRepresentedValue());
		assertEquals(Collections.singletonList(family.getChildren().get(0)), controller.viewForWidget(checkboxList3).getRepresentedValue());
		assertEquals(Collections.singletonList(family.getParents()[0]), controller.viewForWidget(checkboxList4).getRepresentedValue());
		assertEquals(family.getJackies(), controller.viewForWidget(checkboxList5).getRepresentedValue());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	public void test3ModifyValueInModel() {

		JFIBCheckboxListWidget<?> w5 = (JFIBCheckboxListWidget<?>) controller.viewForComponent(checkboxList5);

		assertEquals(false, w5.getCheckboxAtIndex(0).isSelected());
		assertEquals(false, w5.getCheckboxAtIndex(1).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(2).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(3).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(4).isSelected());

		System.out.println("Add to jackies: " + family.getChildren().firstElement());

		family.addToJackies(family.getChildren().firstElement());

		System.out.println("Jackies: " + family.getJackies());
		System.out.println("data: " + checkboxList5.getData());
		System.out.println("valid: " + checkboxList5.getData().isValid());

		assertEquals(true, w5.getCheckboxAtIndex(0).isSelected());
		assertEquals(false, w5.getCheckboxAtIndex(1).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(2).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(3).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(4).isSelected());

		assertEquals(family.getJackies(), controller.viewForWidget(checkboxList5).getRepresentedValue());
	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4ModifyValueInWidget() {
		JFIBCheckboxListWidget<?> w5 = (JFIBCheckboxListWidget<?>) controller.viewForComponent(checkboxList5);

		w5.getCheckboxAtIndex(1).doClick();

		// w5.getDynamicJComponent().setSelectedValue(family.getChildren().get(1), true);
		// w6.getDynamicJComponent().setSelectedValue(Gender.Male, true);

		// Assert jackies and children are same list (order is different)
		assertTrue(family.getChildren().containsAll(family.getJackies()));
		assertTrue(family.getJackies().containsAll(family.getChildren()));

		w5.getCheckboxAtIndex(1).doClick();
		assertEquals(4, family.getJackies().size());

		w5.getCheckboxAtIndex(0).doClick();
		assertEquals(3, family.getJackies().size());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(5)
	public void test5ModifyListValueInModel() {

		JFIBCheckboxListWidget<?> w5 = (JFIBCheckboxListWidget<?>) controller.viewForComponent(checkboxList5);

		assertEquals(5, w5.getMultipleValueModel().getSize());

		Person junior = family.createChild();

		assertEquals(6, w5.getMultipleValueModel().getSize());

		family.addToJackies(junior);

		assertEquals(false, w5.getCheckboxAtIndex(0).isSelected());
		assertEquals(false, w5.getCheckboxAtIndex(1).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(2).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(3).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(4).isSelected());
		assertEquals(true, w5.getCheckboxAtIndex(5).isSelected());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBCheckboxListWidgetTest.class.getSimpleName());
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

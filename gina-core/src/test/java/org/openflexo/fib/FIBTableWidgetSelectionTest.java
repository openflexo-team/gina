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

package org.openflexo.fib;

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
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.FIBDropDownColumn;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabelColumn;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBNumberColumn;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextFieldColumn;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a table widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBTableWidgetSelectionTest extends FIBTestCase {

	private static GraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsPanel;
	private static FIBTable table;

	private static FIBLabel firstNameLabel;
	private static FIBTextField firstNameTF;
	private static FIBLabel lastNameLabel;
	private static FIBTextField lastNameTF;
	private static FIBLabel fullNameLabel;
	private static FIBTextField fullNameTF;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	@TestOrder(1)
	public void test1CreateComponent() {

		component = newFIBPanel();
		component.setLayout(Layout.border);
		component.setDataClass(Family.class);

		table = newFIBTable();
		table.setName("table");
		table.setData(new DataBinding<List<?>>("data.children", table, List.class, BindingDefinitionType.GET));
		table.setAutoSelectFirstRow(true);
		table.setIteratorClass(Person.class);
		table.setBoundToSelectionManager(true);
		table.setManageDynamicModel(true);

		FIBTextFieldColumn c1 = newFIBTextFieldColumn();
		c1.setData(new DataBinding<String>("iterator.firstName", c1, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c1);
		FIBTextFieldColumn c2 = newFIBTextFieldColumn();
		c2.setData(new DataBinding<String>("iterator.lastName", c2, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c2);
		FIBNumberColumn c3 = newFIBNumberColumn();
		c3.setNumberType(NumberType.IntegerType);
		c3.setData(new DataBinding<Integer>("iterator.age", c3, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c3);
		FIBDropDownColumn c4 = newFIBDropDownColumn();
		c4.setData(new DataBinding<Gender>("iterator.gender", c4, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c4);
		FIBLabelColumn c5 = newFIBLabelColumn();
		c5.setData(new DataBinding<String>("iterator.toString", c5, String.class, BindingDefinitionType.GET));
		table.addToColumns(c5);

		detailsPanel = newFIBPanel();
		detailsPanel.setLayout(Layout.twocols);

		firstNameLabel = newFIBLabel("first_name");
		detailsPanel.addToSubComponents(firstNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF = newFIBTextField();
		firstNameTF.setData(new DataBinding<String>("table.selected.firstName", firstNameTF, String.class, BindingDefinitionType.GET_SET));
		detailsPanel.addToSubComponents(firstNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel = newFIBLabel("last_name");
		detailsPanel.addToSubComponents(lastNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF = newFIBTextField();
		lastNameTF.setData(new DataBinding<String>("table.selected.lastName", lastNameTF, String.class, BindingDefinitionType.GET_SET));
		detailsPanel.addToSubComponents(lastNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel = newFIBLabel("full_name");
		detailsPanel.addToSubComponents(fullNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF = newFIBTextField();
		fullNameTF.setData(new DataBinding<String>("table.selected.firstName + ' ' + table.selected.lastName", fullNameTF, String.class,
				BindingDefinitionType.GET));
		detailsPanel.addToSubComponents(fullNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		component.addToSubComponents(table, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(table.getData().isValid());
		assertTrue(firstNameTF.getData().isValid());
		assertTrue(lastNameTF.getData().isValid());
		assertTrue(fullNameTF.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	@TestOrder(2)
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(table));

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);
		assertEquals(family.getChildren(), w.getTableModel().getValues());
		assertEquals(5, w.getTechnologyComponent().getModel().getRowCount());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	public void test3SelectEditAndCheckValues() {

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);

		w.getTechnologyComponent().getSelectionModel().clearSelection();
		w.getTechnologyComponent().getSelectionModel().addSelectionInterval(4, 4);

		assertEquals("Jacky3", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Jacky3 Smith", controller.viewForComponent(fullNameTF).getData());

		family.getBiggestChild().setFirstName("Roger");
		family.getBiggestChild().setLastName("Rabbit");
		family.getBiggestChild().setAge(12);
		family.getBiggestChild().setGender(Gender.Female);

		assertEquals("Roger", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Rabbit", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Roger Rabbit", controller.viewForComponent(fullNameTF).getData());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4SelectMultipleValues() {

		FIBTableWidget<?> w = (FIBTableWidget<?>) controller.viewForComponent(table);

		w.getTechnologyComponent().getSelectionModel().clearSelection();
		w.getTechnologyComponent().getSelectionModel().addSelectionInterval(1, 3);

		assertEquals("Jacky2", controller.viewForComponent(firstNameTF).getData());
		assertEquals("Smith", controller.viewForComponent(lastNameTF).getData());
		assertEquals("Jacky2 Smith", controller.viewForComponent(fullNameTF).getData());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(FIBTableWidgetSelectionTest.class.getSimpleName());
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

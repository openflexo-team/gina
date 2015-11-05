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
import static org.junit.Assert.assertFalse;
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
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.container.BorderLayoutConstraints;
import org.openflexo.fib.model.container.FIBPanel;
import org.openflexo.fib.model.container.GridBagLayoutConstraints;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints;
import org.openflexo.fib.model.container.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.container.FIBPanel.Layout;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.model.widget.FIBDropDownColumn;
import org.openflexo.fib.model.widget.FIBLabel;
import org.openflexo.fib.model.widget.FIBLabelColumn;
import org.openflexo.fib.model.widget.FIBNumberColumn;
import org.openflexo.fib.model.widget.FIBTable;
import org.openflexo.fib.model.widget.FIBTextField;
import org.openflexo.fib.model.widget.FIBTextFieldColumn;
import org.openflexo.fib.model.widget.FIBNumber.NumberType;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.swing.utils.SwingGraphicalContextDelegate;
import org.openflexo.fib.swing.view.SwingViewFactory;
import org.openflexo.fib.swing.view.widget.JFIBTableWidget;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a table widget, and where details panel are
 * multiple
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBTableWidgetSelectionTest2 extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsContainerPanel;
	private static FIBTable table;

	private static FIBPanel detailsPanel1;
	private static FIBLabel label1;
	private static FIBLabel firstNameLabel1;
	private static FIBTextField firstNameTF1;
	private static FIBLabel lastNameLabel1;
	private static FIBTextField lastNameTF1;
	private static FIBLabel fullNameLabel1;
	private static FIBTextField fullNameTF1;

	private static FIBPanel detailsPanel2;
	private static FIBLabel label2;
	private static FIBLabel firstNameLabel2;
	private static FIBTextField firstNameTF2;
	private static FIBLabel lastNameLabel2;
	private static FIBTextField lastNameTF2;
	private static FIBLabel fullNameLabel2;
	private static FIBTextField fullNameTF2;

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

		detailsPanel1 = newFIBPanel();
		detailsPanel1.setLayout(Layout.twocols);

		label1 = newFIBLabel("This detail panel represents a Jacky");
		detailsPanel1.addToSubComponents(label1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel1 = newFIBLabel("first_name");
		detailsPanel1.addToSubComponents(firstNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF1 = newFIBTextField();
		firstNameTF1
				.setData(new DataBinding<String>("table.selected.firstName", firstNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(firstNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel1 = newFIBLabel("last_name");
		detailsPanel1.addToSubComponents(lastNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF1 = newFIBTextField();
		lastNameTF1.setData(new DataBinding<String>("table.selected.lastName", lastNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(lastNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel1 = newFIBLabel("full_name");
		detailsPanel1.addToSubComponents(fullNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF1 = newFIBTextField();
		fullNameTF1.setData(new DataBinding<String>("table.selected.firstName + ' ' + table.selected.lastName", fullNameTF1, String.class,
				BindingDefinitionType.GET));
		detailsPanel1.addToSubComponents(fullNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel2 = newFIBPanel();
		detailsPanel2.setLayout(Layout.twocols);

		label2 = newFIBLabel("This detail panel represents a normal child");
		detailsPanel2.addToSubComponents(label2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel2 = newFIBLabel("first_name");
		detailsPanel2.addToSubComponents(firstNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF2 = newFIBTextField();
		firstNameTF2
				.setData(new DataBinding<String>("table.selected.firstName", firstNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(firstNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel2 = newFIBLabel("last_name");
		detailsPanel2.addToSubComponents(lastNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF2 = newFIBTextField();
		lastNameTF2.setData(new DataBinding<String>("table.selected.lastName", lastNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(lastNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel2 = newFIBLabel("full_name");
		detailsPanel2.addToSubComponents(fullNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF2 = newFIBTextField();
		fullNameTF2.setData(new DataBinding<String>("table.selected.firstName + ' ' + table.selected.lastName", fullNameTF2, String.class,
				BindingDefinitionType.GET));
		detailsPanel2.addToSubComponents(fullNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel1.setVisible(
				new DataBinding<Boolean>("data.jackies.contains(table.selected)", detailsPanel1, Boolean.class, BindingDefinitionType.GET));
		detailsPanel2.setVisible(new DataBinding<Boolean>("!data.jackies.contains(table.selected)", detailsPanel2, Boolean.class,
				BindingDefinitionType.GET));

		detailsContainerPanel = newFIBPanel();
		detailsContainerPanel.setLayout(Layout.gridbag);
		detailsContainerPanel.addToSubComponents(detailsPanel1, new GridBagLayoutConstraints());
		detailsContainerPanel.addToSubComponents(detailsPanel2, new GridBagLayoutConstraints());

		component.addToSubComponents(table, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsContainerPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(table.getData().isValid());
		assertTrue(firstNameTF1.getData().isValid());
		assertTrue(lastNameTF1.getData().isValid());
		assertTrue(fullNameTF1.getData().isValid());
		assertTrue(detailsPanel1.getVisible().isValid());
		assertTrue(firstNameTF2.getData().isValid());
		assertTrue(lastNameTF2.getData().isValid());
		assertTrue(fullNameTF2.getData().isValid());
		assertTrue(detailsPanel2.getVisible().isValid());

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
		assertNotNull(controller.viewForComponent(table));

		JFIBTableWidget<?> w = (JFIBTableWidget<?>) controller.viewForComponent(table);
		assertEquals(family.getChildren(), w.getTableModel().getValues());
		assertEquals(5, w.getTechnologyComponent().getJTable().getModel().getRowCount());
	}

	/**
	 * Select some values, check that master/details scheme works
	 */
	@Test
	@TestOrder(3)
	public void test3SelectSomeValues() {

		JFIBTableWidget<?> w = (JFIBTableWidget<?>) controller.viewForComponent(table);
		FIBPanelView<?, ?> details1 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel1);
		FIBPanelView<?, ?> details2 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel2);

		w.getTechnologyComponent().getJTable().getSelectionModel().clearSelection();
		w.getTechnologyComponent().getJTable().getSelectionModel().addSelectionInterval(1, 1);

		assertFalse(details1.isViewVisible());
		assertTrue(details2.isViewVisible());
		assertEquals("Suzy", controller.viewForWidget(firstNameTF1).getData());
		assertEquals("Smith", controller.viewForWidget(lastNameTF1).getData());
		assertEquals("Suzy Smith", controller.viewForWidget(fullNameTF1).getData());

		w.getTechnologyComponent().getJTable().getSelectionModel().clearSelection();
		w.getTechnologyComponent().getJTable().getSelectionModel().addSelectionInterval(2, 2);

		assertTrue(details1.isViewVisible());
		assertFalse(details2.isViewVisible());
		assertEquals("Jacky1", controller.viewForWidget(firstNameTF2).getData());
		assertEquals("Smith", controller.viewForWidget(lastNameTF2).getData());
		assertEquals("Jacky1 Smith", controller.viewForWidget(fullNameTF2).getData());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBTableWidgetSelectionTest2.class.getSimpleName());
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

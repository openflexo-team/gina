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
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.swing.utils.SwingGraphicalContextDelegate;
import org.openflexo.gina.FIBTestCase;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.gina.model.widget.FIBDropDownColumn;
import org.openflexo.gina.model.widget.FIBLabelColumn;
import org.openflexo.gina.model.widget.FIBNumber.NumberType;
import org.openflexo.gina.model.widget.FIBNumberColumn;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableAction.FIBAddAction;
import org.openflexo.gina.model.widget.FIBTextFieldColumn;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Family.Gender;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test {@link BindingModel} management on some widgets (focus on derived {@link BindingModel})
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestWidgetsBindingModel extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBBrowser browser;
	private static FIBTable table;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	@TestOrder(1)
	public void test1CreateBrowser() {

		log("test1CreateBrowser()");

		component = newFIBPanel();
		component.setLayout(Layout.border);
		component.setDataClass(Family.class);

		browser = newFIBBrowser();
		browser.setName("browser");
		browser.setRoot(new DataBinding<Object>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setIteratorClass(Person.class);
		browser.setBoundToSelectionManager(true);
		browser.setManageDynamicModel(true);

	}

	@Test
	@TestOrder(2)
	public void test2TestFormat() {

		log("test2TestFormat()");

		browser.setFormat(new DataBinding<String>("object.toString"));
		assertTrue(browser.getFormat().isValid());

		System.out.println("Browser BM = " + browser.getBindingModel());

		assertEquals(4, browser.getBindingModel().getBindingVariablesCount());
		assertNotNull(browser.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(browser.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(browser.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(browser.getBindingModel().bindingVariableNamed("controller"));

		System.out.println("Browser formatter BM = " + browser.getFormat().getOwner().getBindingModel());

		assertEquals(5, browser.getFormat().getOwner().getBindingModel().getBindingVariablesCount());
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("value"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("object"));

		component.addToSubComponents(browser, new BorderLayoutConstraints(BorderLayoutLocation.west));

		System.out.println("Browser formatter BM = " + browser.getFormat().getOwner().getBindingModel());

		assertEquals(7, browser.getFormat().getOwner().getBindingModel().getBindingVariablesCount());
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("value"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("object"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("data"));
		assertNotNull(browser.getFormat().getOwner().getBindingModel().bindingVariableNamed("browser"));

	}

	@Test
	@TestOrder(3)
	public void test3TestValueBindable() {

		log("test3TestValueBindable()");

		System.out.println("Value Bindable BM = " + browser.getValueBindable().getBindingModel());

		assertEquals(7, browser.getValueBindable().getBindingModel().getBindingVariablesCount());
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("value"));
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("data"));
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("browser"));
		assertNotNull(browser.getValueBindable().getBindingModel().bindingVariableNamed("valueToTransform"));

	}

	@Test
	@TestOrder(4)
	public void test4TestEventListener() {

		log("test4TestEventListener()");

		assertEquals(7, browser.getEventListener().getBindingModel().getBindingVariablesCount());
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("value"));
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("data"));
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("browser"));
		assertNotNull(browser.getEventListener().getBindingModel().bindingVariableNamed("event"));

	}

	@Test
	@TestOrder(5)
	public void test5TestFIBBrowser() {

		FIBBrowserElement rootElement = newFIBBrowserElement();
		rootElement.setName("family");
		rootElement.setDataClass(Family.class);
		rootElement.setLabel(new DataBinding<String>("\"My Family\"", browser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren parents = newFIBBrowserElementChildren();
		parents.setData(new DataBinding<Object>("family.parents", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(parents);
		FIBBrowserElementChildren children = newFIBBrowserElementChildren();
		parents.setData(new DataBinding<Object>("family.children", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(children);

		browser.addToElements(rootElement);

		FIBBrowserElement personElement = newFIBBrowserElement();
		personElement.setName("person");
		personElement.setDataClass(Person.class);
		personElement
				.setLabel(new DataBinding<String>("\"My relative: \"+person.toString", browser, String.class, BindingDefinitionType.GET));

		org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction addAction = factory
				.newInstance(org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction.class);
		personElement.addToActions(addAction);

		assertEquals(2, addAction.getBindingModel().getBindingVariablesCount());

		browser.addToElements(personElement);
		assertTrue(browser.getRoot().isValid());

		assertEquals(8, addAction.getBindingModel().getBindingVariablesCount());

		org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction addAction2 = factory
				.newInstance(org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction.class);
		personElement.addToActions(addAction2);

		System.out.println("AddAction2 BM=" + addAction2.getBindingModel());
		assertEquals(8, addAction2.getBindingModel().getBindingVariablesCount());

		System.out.println("Person Element BM = " + personElement.getBindingModel());

		assertEquals(6, personElement.getBindingModel().getBindingVariablesCount());
		assertNotNull(personElement.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(personElement.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(personElement.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(personElement.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(personElement.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(personElement.getBindingModel().bindingVariableNamed("browser"));

		System.out.println("Person Element Iterator BM = " + personElement.getIterator().getBindingModel());

		assertEquals(7, personElement.getIterator().getBindingModel().getBindingVariablesCount());
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("value"));
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("data"));
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("browser"));
		assertNotNull(personElement.getIterator().getBindingModel().bindingVariableNamed("person"));

		System.out.println("Parents child bindable BM = " + parents.getChildBindable().getBindingModel());

		assertEquals(7, parents.getChildBindable().getBindingModel().getBindingVariablesCount());
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("value"));
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("data"));
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("browser"));
		assertNotNull(parents.getChildBindable().getBindingModel().bindingVariableNamed("child"));

		org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction addAction3 = factory
				.newInstance(org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction.class);
		personElement.addToActions(addAction3);

		System.out.println("AddAction3 BM=" + addAction3.getBindingModel());

	}

	@Test
	@TestOrder(6)
	public void test6TestFIBTable() {

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

		FIBAddAction action = factory.newInstance(FIBAddAction.class);
		action.setName("action");
		action.setMethod(new DataBinding<Object>("selected.toString"));
		table.addToActions(action);

		System.out.println("Table BM = " + table.getBindingModel());

		assertEquals(5, table.getBindingModel().getBindingVariablesCount());
		assertNotNull(table.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(table.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(table.getBindingModel().bindingVariableNamed("selectedIndex"));
		assertNotNull(table.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(table.getBindingModel().bindingVariableNamed("controller"));

		assertEquals(6, c1.getBindingModel().getBindingVariablesCount());
		assertNotNull(c1.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("selectedIndex"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("iterator"));

		assertEquals(6, action.getBindingModel().getBindingVariablesCount());
		assertNotNull(action.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selectedIndex"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selected"));

		component.addToSubComponents(table, new BorderLayoutConstraints(BorderLayoutLocation.center));

		System.out.println("C1 BM = " + c1.getBindingModel());

		assertEquals(9, c1.getBindingModel().getBindingVariablesCount());
		assertNotNull(c1.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("selectedIndex"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("iterator"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("table"));
		assertNotNull(c1.getBindingModel().bindingVariableNamed("browser"));

		System.out.println("action BM = " + action.getBindingModel());

		assertEquals(9, action.getBindingModel().getBindingVariablesCount());
		assertNotNull(action.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selected"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selectedIndex"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("selection"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("table"));
		assertNotNull(action.getBindingModel().bindingVariableNamed("browser"));
	}

	/**
	 * Instanciate component
	 */
	@Test
	@TestOrder(10)
	public void test10InstanciateComponent() {

		log("test2InstanciateComponent()");

		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(browser));

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestWidgetsBindingModel.class.getSimpleName());
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

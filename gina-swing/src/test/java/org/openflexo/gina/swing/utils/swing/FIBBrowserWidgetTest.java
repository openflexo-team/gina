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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBBrowserWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test the structural and behavioural features of {@link FIBBrowserWidget} widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBBrowserWidgetTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBBrowser browser;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create an initial component
	 */
	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void test1CreateComponent() {

		// Default behaviour is to update browser cells asynchronously in event-dispatch-thread
		// But in this test environment, we need to "force" the update to be done synchrounously
		FIBBrowserModel.UPDATE_BROWSER_SYNCHRONOUSLY = true;

		component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);

		browser = newFIBBrowser();
		browser.setRoot(new DataBinding<>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setBoundToSelectionManager(true);
		browser.setIteratorType(Person.class);

		FIBBrowserElement rootElement = newFIBBrowserElement();
		rootElement.setName("family");
		rootElement.setDataType(Family.class);
		rootElement.setLabel(new DataBinding<>("\"My Family\"", browser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren parents = newFIBBrowserElementChildren();
		parents.setData(new DataBinding<>("family.parents", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(parents);
		FIBBrowserElementChildren children = newFIBBrowserElementChildren();
		parents.setData(new DataBinding<>("family.children", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(children);

		browser.addToElements(rootElement);

		FIBBrowserElement personElement = newFIBBrowserElement();
		personElement.setName("person");
		personElement.setDataType(Person.class);
		personElement.setLabel(new DataBinding<>("\"My relative: \"+person.toString", browser, String.class, BindingDefinitionType.GET));

		browser.addToElements(personElement);
		component.addToSubComponents(browser, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));

		assertTrue(browser.getRoot().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void test2InstanciateComponent() {
		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());
		assertNotNull(controller.viewForComponent(browser));

		// FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);
		// assertEquals(family.getChildren(), w.getTableModel().getValues());
		// assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void test3ModifyValueInModel() {

		JFIBBrowserWidget<Person> w = (JFIBBrowserWidget<Person>) controller.viewForComponent(browser);

		BrowserCell root = (BrowserCell) w.getBrowserModel().getRoot();

		assertEquals(family, root.getUserObject());
		assertEquals(5, root.getChildCount());
		assertEquals(family.getChildren().get(0), ((BrowserCell) root.getChildAt(0)).getUserObject());
		assertEquals(family.getChildren().get(1), ((BrowserCell) root.getChildAt(1)).getUserObject());
		assertEquals(family.getChildren().get(2), ((BrowserCell) root.getChildAt(2)).getUserObject());
		assertEquals(family.getChildren().get(3), ((BrowserCell) root.getChildAt(3)).getUserObject());
		assertEquals(family.getChildren().get(4), ((BrowserCell) root.getChildAt(4)).getUserObject());

		Person junior = family.createChild();

		assertEquals(6, root.getChildCount());
		assertEquals(junior, ((BrowserCell) root.getChildAt(5)).getUserObject());
	}

	/**
	 * Try to select some objects, check that selection is in sync with it
	 */
	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void test4PerfomSomeTestsWithSelection() {

		FIBBrowserWidget<?, Object> w = (FIBBrowserWidget<?, Object>) controller.viewForComponent(browser);

		w.resetSelection();
		w.addToSelection(family);

		// The selection is here empty because iterator class has been declared as Person, Family is not a Person, therefore the selection
		// is null
		assertEquals(Collections.emptyList()/*Collections.singletonList(family)*/, w.getSelection());

		w.resetSelection();
		w.addToSelection(family.getChildren().get(0));

		assertEquals(Collections.singletonList(family.getChildren().get(0)), w.getSelection());

		// int[] indices = new int[3];
		// indices[0] = 1;
		// indices[1] = 2;
		// indices[2] = 4;
		// w7.getDynamicJComponent().setSelectedIndices(indices);

		Person child1 = family.getChildren().get(1);
		Person child2 = family.getChildren().get(2);
		Person child4 = family.getChildren().get(4);

		w.resetSelection();
		w.addToSelection(child1);
		w.addToSelection(child2);
		w.addToSelection(child4);

		List<Person> expectedSelection = new ArrayList<>();
		expectedSelection.add(child1);
		expectedSelection.add(child2);
		expectedSelection.add(child4);

		assertEquals(expectedSelection, w.getSelection());

		controller.setFocusedWidget(w);
		assertEquals(expectedSelection, controller.getSelectionLeader().getSelection());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBBrowserWidgetTest.class.getSimpleName());

		// Default behaviour is to update browser cells asynchronously in event-dispatch-thread
		// But in this test environment, we need to "force" the update to be done synchronously
		FIBBrowserModel.UPDATE_BROWSER_SYNCHRONOUSLY = true;

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

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
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Family.Gender;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a browser widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBBrowserWidgetSelectionTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsPanel;
	private static FIBBrowser browser;

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

		log("test1CreateComponent()");

		component = newFIBPanel();
		component.setLayout(Layout.border);
		component.setDataClass(Family.class);

		browser = newFIBBrowser();
		browser.setName("browser");
		browser.setRoot(new DataBinding<Object>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setIteratorClass(Person.class);
		browser.setBoundToSelectionManager(true);
		browser.setManageDynamicModel(true);

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

		browser.addToElements(personElement);

		detailsPanel = newFIBPanel();
		detailsPanel.setLayout(Layout.twocols);

		firstNameLabel = newFIBLabel("first_name");
		detailsPanel.addToSubComponents(firstNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF = newFIBTextField();
		firstNameTF
				.setData(new DataBinding<String>("browser.selected.firstName", firstNameTF, String.class, BindingDefinitionType.GET_SET));
		detailsPanel.addToSubComponents(firstNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel = newFIBLabel("last_name");
		detailsPanel.addToSubComponents(lastNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF = newFIBTextField();
		lastNameTF.setData(new DataBinding<String>("browser.selected.lastName", lastNameTF, String.class, BindingDefinitionType.GET_SET));
		detailsPanel.addToSubComponents(lastNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel = newFIBLabel("full_name");
		detailsPanel.addToSubComponents(fullNameLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF = newFIBTextField();
		fullNameTF.setData(new DataBinding<String>("browser.selected.firstName + ' ' + browser.selected.lastName", fullNameTF, String.class,
				BindingDefinitionType.GET));
		detailsPanel.addToSubComponents(fullNameTF, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		component.addToSubComponents(browser, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(browser.getRoot().isValid());
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

		log("test2InstanciateComponent()");

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
	public void test3SelectEditAndCheckValues() {

		log("test3SelectEditAndCheckValues()");

		FIBBrowserWidget w = (FIBBrowserWidget) controller.viewForComponent(browser);

		w.resetSelection();
		w.addToSelection(family.getBiggestChild());

		assertEquals("Jacky3", controller.viewForWidget(firstNameTF).getRepresentedValue());
		assertEquals("Smith", controller.viewForWidget(lastNameTF).getRepresentedValue());
		assertEquals("Jacky3 Smith", controller.viewForWidget(fullNameTF).getRepresentedValue());

		family.getBiggestChild().setFirstName("Roger");
		family.getBiggestChild().setLastName("Rabbit");
		family.getBiggestChild().setAge(12);
		family.getBiggestChild().setGender(Gender.Female);

		assertEquals("Roger", controller.viewForWidget(firstNameTF).getRepresentedValue());
		assertEquals("Rabbit", controller.viewForWidget(lastNameTF).getRepresentedValue());
		assertEquals("Roger Rabbit", controller.viewForWidget(fullNameTF).getRepresentedValue());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4SelectMultipleValues() {

		log("test4SelectMultipleValues()");

		FIBBrowserWidget w = (FIBBrowserWidget) controller.viewForComponent(browser);

		w.resetSelection();
		w.addToSelection(family.getChildren().get(1));
		w.addToSelection(family.getChildren().get(2));
		w.addToSelection(family.getChildren().get(3));

		assertEquals("Jacky2", controller.viewForWidget(firstNameTF).getRepresentedValue());
		assertEquals("Smith", controller.viewForWidget(lastNameTF).getRepresentedValue());
		assertEquals("Jacky2 Smith", controller.viewForWidget(fullNameTF).getRepresentedValue());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBBrowserWidgetSelectionTest.class.getSimpleName());

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

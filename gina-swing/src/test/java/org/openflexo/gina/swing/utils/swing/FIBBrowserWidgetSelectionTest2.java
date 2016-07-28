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
import static org.junit.Assert.assertFalse;
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
import org.openflexo.gina.model.container.FIBPanel.Border;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.gina.model.container.layout.GridBagLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.container.FIBPanelView;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the structural and behavioural features of a simple master-detail pattern driven by a browser widget, and where details panel are
 * multiple
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBBrowserWidgetSelectionTest2 extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBPanel detailsContainerPanel;
	private static FIBBrowser browser;

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
		component.setName("Main");
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
		personElement.setEnabled(
				new DataBinding<Boolean>("!data.jackies.contains(person)", personElement, Boolean.class, BindingDefinitionType.GET));

		browser.addToElements(personElement);

		detailsPanel1 = newFIBPanel();
		detailsPanel1.setName("DetailsPanel1");
		detailsPanel1.setLayout(Layout.twocols);
		detailsPanel1.setBorder(Border.titled);
		detailsPanel1.setBorderTitle("Detail Panel 1");

		label1 = newFIBLabel("This detail panel represents a Jacky");
		detailsPanel1.addToSubComponents(label1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel1 = newFIBLabel("first_name");
		detailsPanel1.addToSubComponents(firstNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF1 = newFIBTextField();
		firstNameTF1
				.setData(new DataBinding<String>("browser.selected.firstName", firstNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(firstNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel1 = newFIBLabel("last_name");
		detailsPanel1.addToSubComponents(lastNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF1 = newFIBTextField();
		lastNameTF1.setData(new DataBinding<String>("browser.selected.lastName", lastNameTF1, String.class, BindingDefinitionType.GET_SET));
		detailsPanel1.addToSubComponents(lastNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel1 = newFIBLabel("full_name");
		detailsPanel1.addToSubComponents(fullNameLabel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF1 = newFIBTextField();
		fullNameTF1.setData(new DataBinding<String>("browser.selected.firstName + ' ' + browser.selected.lastName", fullNameTF1,
				String.class, BindingDefinitionType.GET));
		fullNameTF1.setReadOnly(true);
		detailsPanel1.addToSubComponents(fullNameTF1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel2 = newFIBPanel();
		detailsPanel2.setName("DetailsPanel2");
		detailsPanel2.setLayout(Layout.twocols);
		detailsPanel2.setBorder(Border.titled);
		detailsPanel2.setBorderTitle("Detail Panel 2");

		label2 = newFIBLabel("This detail panel represents a normal child");
		detailsPanel2.addToSubComponents(label2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, false, false));
		firstNameLabel2 = newFIBLabel("first_name");
		detailsPanel2.addToSubComponents(firstNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		firstNameTF2 = newFIBTextField();
		firstNameTF2
				.setData(new DataBinding<String>("browser.selected.firstName", firstNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(firstNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		lastNameLabel2 = newFIBLabel("last_name");
		detailsPanel2.addToSubComponents(lastNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		lastNameTF2 = newFIBTextField();
		lastNameTF2.setData(new DataBinding<String>("browser.selected.lastName", lastNameTF2, String.class, BindingDefinitionType.GET_SET));
		detailsPanel2.addToSubComponents(lastNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		fullNameLabel2 = newFIBLabel("full_name");
		detailsPanel2.addToSubComponents(fullNameLabel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		fullNameTF2 = newFIBTextField();
		fullNameTF2.setData(new DataBinding<String>("browser.selected.firstName + ' ' + browser.selected.lastName", fullNameTF2,
				String.class, BindingDefinitionType.GET));
		fullNameTF2.setReadOnly(true);
		detailsPanel2.addToSubComponents(fullNameTF2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		detailsPanel1.setVisible(new DataBinding<Boolean>("data.jackies.contains(browser.selected)", detailsPanel1, Boolean.class,
				BindingDefinitionType.GET));
		detailsPanel2.setVisible(new DataBinding<Boolean>("!data.jackies.contains(browser.selected)", detailsPanel2, Boolean.class,
				BindingDefinitionType.GET));

		detailsContainerPanel = newFIBPanel();
		detailsContainerPanel.setName("DetailsContainerPanel");
		detailsContainerPanel.setLayout(Layout.gridbag);
		detailsContainerPanel.addToSubComponents(detailsPanel1, new GridBagLayoutConstraints());
		detailsContainerPanel.addToSubComponents(detailsPanel2, new GridBagLayoutConstraints());
		/*detailsContainerPanel.setLayout(Layout.twocols);
		detailsContainerPanel.addToSubComponents(detailsPanel1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));
		detailsContainerPanel.addToSubComponents(detailsPanel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));*/

		component.addToSubComponents(browser, new BorderLayoutConstraints(BorderLayoutLocation.west));
		component.addToSubComponents(detailsContainerPanel, new BorderLayoutConstraints(BorderLayoutLocation.center));

		assertTrue(browser.getRoot().isValid());
		assertTrue(firstNameTF1.getData().isValid());
		assertTrue(lastNameTF1.getData().isValid());
		assertTrue(fullNameTF1.getData().isValid());
		assertTrue(detailsPanel1.getVisible().isValid());
		assertTrue(firstNameTF2.getData().isValid());
		assertTrue(lastNameTF2.getData().isValid());
		assertTrue(fullNameTF2.getData().isValid());
		assertTrue(detailsPanel2.getVisible().isValid());

		// System.out.println("Component:" + component.getFactory().stringRepresentation(component));

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
		assertNotNull(controller.viewForComponent(browser));

		// FIBBrowserWidget<?> w = (FIBBrowserWidget<?>) controller.viewForComponent(browser);
		// assertEquals(family.getChildren(), w.getTableModel().getValues());
		// assertEquals(5, w.getDynamicJComponent().getModel().getRowCount());
	}

	/**
	 * Select some values, check that master/details scheme works
	 */
	@Test
	@TestOrder(3)
	public void test3SelectSomeValues() {

		FIBBrowserWidget w = (FIBBrowserWidget) controller.viewForComponent(browser);
		FIBPanelView<?, ?> details1 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel1);
		FIBPanelView<?, ?> details2 = (FIBPanelView<?, ?>) controller.viewForComponent(detailsPanel2);

		w.resetSelection();
		w.addToSelection(family.getChildren().get(1));

		assertFalse(details1.isViewVisible());
		assertTrue(details2.isViewVisible());
		assertEquals("Suzy", controller.viewForWidget(firstNameTF2).getRepresentedValue());
		assertEquals("Smith", controller.viewForWidget(lastNameTF2).getRepresentedValue());
		assertEquals("Suzy Smith", controller.viewForWidget(fullNameTF2).getRepresentedValue());

		w.resetSelection();
		w.addToSelection(family.getChildren().get(2));

		assertTrue(details1.isViewVisible());
		assertFalse(details2.isViewVisible());
		assertEquals("Jacky1", controller.viewForWidget(firstNameTF1).getRepresentedValue());
		assertEquals("Smith", controller.viewForWidget(lastNameTF1).getRepresentedValue());
		assertEquals("Jacky1 Smith", controller.viewForWidget(fullNameTF1).getRepresentedValue());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBBrowserWidgetSelectionTest2.class.getSimpleName());
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

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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
import java.util.concurrent.atomic.AtomicInteger;

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
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBBrowserWidget;
import org.openflexo.gina.sampleData.SampleData;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserElementType;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Cells of a browser whose children are evaluated while their objects change (GINA-D-3):
 * <ul>
 * <li>a cell found without children is not frozen as a leaf: it gets its children once they appear, at the next update of its
 * parent;</li>
 * <li>the bindings of an element are evaluated in a context of their own: concurrent evaluations for different objects do not mix
 * them;</li>
 * <li>the cast of the children is evaluated with the child and the variables of the context of the object;</li>
 * <li>a change notified while a cell is being updated is not lost: the cell is updated again (GINA-D-6).</li>
 * </ul>
 * 
 * @author sylvain
 *
 */
@RunWith(OrderedRunner.class)
public class FIBBrowserLeafCellTest extends FIBTestCase {

	/**
	 * A node of a tree, which does not notify the changes of its children
	 */
	public static class Node {
		private final String name;
		private final List<Node> children = new ArrayList<>();

		public Node(String name, Node... children) {
			this.name = name;
			Collections.addAll(this.children, children);
		}

		public String getName() {
			return name;
		}

		public List<Node> getChildren() {
			return children;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * A node notifying the changes of its children, which can add a child while its children are being read, right after having
	 * returned them (as a resource center registering a resource while a browser reads the contents of a folder)
	 */
	public static class ObservableNode extends SampleData {
		private final String name;
		private final List<ObservableNode> children = new ArrayList<>();
		private ObservableNode toAddDuringNextRead;

		public ObservableNode(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<ObservableNode> getChildren() {
			List<ObservableNode> returned = new ArrayList<>(children);
			if (toAddDuringNextRead != null) {
				ObservableNode added = toAddDuringNextRead;
				toAddDuringNextRead = null;
				children.add(added);
				getPropertyChangeSupport().firePropertyChange("children", null, added);
			}
			return returned;
		}

		public void addChild(ObservableNode child) {
			children.add(child);
			getPropertyChangeSupport().firePropertyChange("children", null, child);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBBrowser browser;
	private static FIBController controller;

	private static Node a;
	private static Node b;
	private static Node root;

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void test1CreateComponent() {
		component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Node.class);

		browser = newFIBBrowser();
		browser.setRoot(new DataBinding<>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setIteratorType(Node.class);

		FIBBrowserElement nodeElement = newFIBBrowserElement();
		nodeElement.setName("node");
		nodeElement.setDataType(Node.class);
		nodeElement.setLabel(new DataBinding<>("node.name", browser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren children = newFIBBrowserElementChildren();
		children.setData(new DataBinding<>("node.children", browser, Object.class, BindingDefinitionType.GET));
		nodeElement.addToChildren(children);
		browser.addToElements(nodeElement);

		component.addToSubComponents(browser, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));
		assertTrue(browser.getRoot().isValid());
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void test2CellFoundEmptyIsNotFrozen() {
		a = new Node("a");
		b = new Node("b");
		root = new Node("root", a, b);

		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		controller.setDataObject(root);
		controller.buildView();
		gcDelegate.addTab("Test2", controller);

		JFIBBrowserWidget<?> w = (JFIBBrowserWidget<?>) controller.viewForComponent(browser);
		BrowserCell rootCell = (BrowserCell) w.getBrowserModel().getRoot();
		assertEquals(2, rootCell.getChildCount());
		BrowserCell aCell = (BrowserCell) rootCell.getChildAt(0);
		assertEquals(a, aCell.getRepresentedObject());
		assertEquals(0, aCell.getChildCount());

		// The children of a appear without any notification (as when the cell was created while they were not computed yet)
		Node a1 = new Node("a1");
		a.getChildren().add(a1);

		// The next update of the parent looks up the children of the cell again
		rootCell.update(false);
		assertEquals(1, aCell.getChildCount());
		assertEquals(a1, ((BrowserCell) aCell.getChildAt(0)).getRepresentedObject());
		assertEquals(0, ((BrowserCell) rootCell.getChildAt(1)).getChildCount());
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void test3ConcurrentEvaluationsDoNotMixObjects() throws InterruptedException {
		JFIBBrowserWidget<?> w = (JFIBBrowserWidget<?>) controller.viewForComponent(browser);
		FIBBrowserElementType elementType = w.getBrowserModel().elementTypeForObject(a);
		assertEquals(elementType, w.getBrowserModel().elementTypeForObject(b));

		AtomicInteger errors = new AtomicInteger();
		List<Thread> threads = new ArrayList<>();
		for (int t = 0; t < 4; t++) {
			final Node node = t % 2 == 0 ? a : b;
			Thread thread = new Thread(() -> {
				for (int i = 0; i < 2000; i++) {
					List<Object> children = elementType.getChildrenFor(node);
					String label = elementType.getLabelFor(node);
					if (!children.equals(node.getChildren()) || !node.getName().equals(label)) {
						errors.incrementAndGet();
					}
				}
			});
			threads.add(thread);
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		assertEquals(0, errors.get());
	}

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void test4CastOfTheChildren() {
		FIBPanel castComponent = newFIBPanel();
		castComponent.setLayout(Layout.twocols);
		castComponent.setDataClass(Node.class);
		FIBBrowser castBrowser = newFIBBrowser();
		castBrowser.setRoot(new DataBinding<>("data", castBrowser, Object.class, BindingDefinitionType.GET));
		castBrowser.setIteratorType(Node.class);
		FIBBrowserElement nodeElement = newFIBBrowserElement();
		nodeElement.setName("node");
		nodeElement.setDataType(Node.class);
		nodeElement.setLabel(new DataBinding<>("node.name", castBrowser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren children = newFIBBrowserElementChildren();
		children.setData(new DataBinding<>("node.children", castBrowser, Object.class, BindingDefinitionType.GET));
		// The cast reads the child, and a variable of the component through the context of the object
		children.setCast(new DataBinding<>("child.name + \"<\" + data.name", children.getChildBindable(), Object.class,
				BindingDefinitionType.GET));
		nodeElement.addToChildren(children);
		castBrowser.addToElements(nodeElement);
		castComponent.addToSubComponents(castBrowser, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));
		assertTrue(String.valueOf(children.getCast().invalidBindingReason()), children.getCast().isValid());

		FIBController castController = FIBController.instanciateController(castComponent, SwingViewFactory.INSTANCE,
				FlexoLocalization.getMainLocalizer());
		castController.setDataObject(root);
		castController.buildView();
		JFIBBrowserWidget<?> w = (JFIBBrowserWidget<?>) castController.viewForComponent(castBrowser);
		assertEquals(java.util.Arrays.asList("a<root", "b<root"), w.getBrowserModel().elementTypeForObject(root).getChildrenFor(root));
	}

	@Test
	@TestOrder(5)
	@Category(UITest.class)
	public void test5ChangeNotifiedDuringAnUpdateIsNotLost() {
		FIBPanel observableComponent = newFIBPanel();
		observableComponent.setLayout(Layout.twocols);
		observableComponent.setDataClass(ObservableNode.class);
		FIBBrowser observableBrowser = newFIBBrowser();
		observableBrowser.setRoot(new DataBinding<>("data", observableBrowser, Object.class, BindingDefinitionType.GET));
		observableBrowser.setIteratorType(ObservableNode.class);
		FIBBrowserElement nodeElement = newFIBBrowserElement();
		nodeElement.setName("node");
		nodeElement.setDataType(ObservableNode.class);
		nodeElement.setLabel(new DataBinding<>("node.name", observableBrowser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren children = newFIBBrowserElementChildren();
		children.setData(new DataBinding<>("node.children", observableBrowser, Object.class, BindingDefinitionType.GET));
		nodeElement.addToChildren(children);
		observableBrowser.addToElements(nodeElement);
		observableComponent.addToSubComponents(observableBrowser, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));

		ObservableNode folder = new ObservableNode("folder");
		folder.addChild(new ObservableNode("r1"));
		folder.addChild(new ObservableNode("r2"));

		FIBController observableController = FIBController.instanciateController(observableComponent, SwingViewFactory.INSTANCE,
				FlexoLocalization.getMainLocalizer());
		observableController.setDataObject(folder);
		observableController.buildView();
		gcDelegate.addTab("Test5", observableController);
		JFIBBrowserWidget<?> w = (JFIBBrowserWidget<?>) observableController.viewForComponent(observableBrowser);
		BrowserCell folderCell = (BrowserCell) w.getBrowserModel().getRoot();
		assertEquals(2, folderCell.getChildCount());

		// A child is added and notified while the cell reads the children, after they were returned to it
		folder.toAddDuringNextRead = new ObservableNode("r3");
		folderCell.update(false);
		assertEquals(3, folderCell.getChildCount());
		assertEquals("r3", ((BrowserCell) folderCell.getChildAt(2)).getRepresentedObject().toString());
	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBBrowserLeafCellTest.class.getSimpleName());
		// Cells are updated synchronously in this test environment
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

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBListWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.widget.impl.FIBMultipleValueWidgetImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * The model of a list widget is read by the event dispatch thread while it may be updated from another thread, notifying a change
 * of its binding: the reader must never see its contents cleared (GINA-D-4)
 * 
 * @author sylvain
 *
 */
@RunWith(OrderedRunner.class)
public class FIBListModelConcurrencyTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void test1UpdateWhileReading() throws Exception {
		FIBPanel component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(Family.class);
		FIBList list = newFIBList();
		list.setList(new DataBinding<List<?>>("data.children", list, List.class, BindingDefinitionType.GET));
		component.addToSubComponents(list, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));

		FIBController controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE,
				FlexoLocalization.getMainLocalizer());
		Family family = new Family();
		controller.setDataObject(family);
		controller.buildView();
		gcDelegate.addTab("Test1", controller);

		JFIBListWidget<?> w = (JFIBListWidget<?>) controller.viewForComponent(list);
		FIBMultipleValueWidgetImpl.FIBMultipleValueModelImpl model = (FIBMultipleValueWidgetImpl.FIBMultipleValueModelImpl) w
				.getMultipleValueModel();
		assertNotNull(model);
		int size = family.getChildren().size();
		assertEquals(size, model.getSize());

		// update() is what a notification of the list binding runs, on the notifying thread
		Method update = FIBMultipleValueWidgetImpl.FIBMultipleValueModelImpl.class.getDeclaredMethod("update");
		update.setAccessible(true);

		AtomicBoolean running = new AtomicBoolean(true);
		AtomicInteger errors = new AtomicInteger();
		List<Thread> readers = new ArrayList<>();
		for (int t = 0; t < 3; t++) {
			Thread reader = new Thread(() -> {
				while (running.get()) {
					try {
						int n = model.getSize();
						for (int i = 0; i < n; i++) {
							if (model.getElementAt(i) == null) {
								errors.incrementAndGet();
							}
						}
					} catch (RuntimeException e) {
						errors.incrementAndGet();
					}
				}
			});
			readers.add(reader);
			reader.start();
		}
		for (int i = 0; i < 20000; i++) {
			update.invoke(model);
		}
		running.set(false);
		for (Thread reader : readers) {
			reader.join();
		}
		assertEquals(0, errors.get());
		assertEquals(size, model.getSize());
	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBListModelConcurrencyTest.class.getSimpleName());
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

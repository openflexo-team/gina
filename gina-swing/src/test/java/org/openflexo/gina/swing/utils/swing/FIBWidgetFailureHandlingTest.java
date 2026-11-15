/**
 * 
 * Copyright (c) 2026, Openflexo
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBButtonWidget;
import org.openflexo.gina.swing.view.widget.JFIBTextFieldWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * The failure of model code invoked by a widget (the action of a button, the setting of the data of a text field) reaches
 * {@link FIBController#handleException(Throwable)}, once per failure, and the view goes on (CORE-F-11, phase 4)
 */
@RunWith(OrderedRunner.class)
public class FIBWidgetFailureHandlingTest extends FIBTestCase {

	/** The data of the component: its action and its setter fail */
	public static class FailingModel {
		private String value = "initial";

		public void fail() {
			throw new IllegalStateException("the action failed");
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			throw new IllegalArgumentException("cannot set " + value);
		}
	}

	/** A controller that records the failures it is asked to handle */
	public static class RecordingController extends FIBController {
		final List<Throwable> handled = new ArrayList<>();

		public RecordingController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
			super(rootComponent, viewFactory);
		}

		@Override
		public boolean handleException(Throwable t) {
			handled.add(t);
			return true;
		}
	}

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;
	private static FIBButton button;
	private static FIBTextField textField;
	private static RecordingController controller;

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void test1CreateComponent() {
		component = newFIBPanel();
		component.setLayout(Layout.twocols);
		component.setDataClass(FailingModel.class);

		button = factory.newInstance(FIBButton.class);
		button.setLabel("fail");
		button.setAction(new DataBinding<>("data.fail()", button, Object.class, BindingDefinitionType.EXECUTE));
		component.addToSubComponents(button, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));
		assertTrue(button.getAction().isValid());

		textField = newFIBTextField();
		textField.setData(new DataBinding<String>("data.value", textField, String.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(textField, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));
		assertTrue(textField.getData().isValid());
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void test2FailingButtonActionIsHandledOnce() {
		controller = new RecordingController(component, SwingViewFactory.INSTANCE);
		controller.setDataObject(new FailingModel());
		controller.buildView();
		gcDelegate.addTab("Test2", controller);

		JFIBButtonWidget buttonWidget = (JFIBButtonWidget) controller.viewForComponent(button);
		buttonWidget.buttonClicked();
		assertEquals(1, controller.handled.size());
		assertTrue(rootCause(controller.handled.get(0)) instanceof IllegalStateException);
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void test3FailingDataSetIsHandledOnce() {
		controller.handled.clear();
		JFIBTextFieldWidget textFieldWidget = (JFIBTextFieldWidget) controller.viewForComponent(textField);
		textFieldWidget.getTechnologyComponent().setText("other");
		// setText removes the text, then inserts the new one: each set fails, each failure is handled
		assertFalse(controller.handled.isEmpty());
		for (Throwable t : controller.handled) {
			assertTrue(rootCause(t) instanceof IllegalArgumentException);
		}
	}

	private static Throwable rootCause(Throwable t) {
		Throwable c = t;
		while (c.getCause() != null && c.getCause() != c) {
			c = c.getCause();
		}
		return c;
	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBWidgetFailureHandlingTest.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}
}

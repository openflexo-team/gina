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
import org.openflexo.gina.model.container.layout.GridLayoutConstraints;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBNumber.NumberType;
import org.openflexo.gina.sampleData.Numbers;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBNumberWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the structural and behavioural features of FIBNumber widget
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class FIBNumberWidgetTest extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel component;

	private static FIBLabel byteLabel;
	private static FIBNumber bytePWidget;
	private static FIBNumber byteOWidget;
	private static FIBNumber byteSWidget;

	private static FIBLabel shortLabel;
	private static FIBNumber shortPWidget;
	private static FIBNumber shortOWidget;
	private static FIBNumber shortSWidget;

	private static FIBLabel integerLabel;
	private static FIBNumber integerPWidget;
	private static FIBNumber integerOWidget;
	private static FIBNumber integerSWidget;

	private static FIBLabel longLabel;
	private static FIBNumber longPWidget;
	private static FIBNumber longOWidget;
	private static FIBNumber longSWidget;

	private static FIBLabel floatLabel;
	private static FIBNumber floatPWidget;
	private static FIBNumber floatOWidget;
	private static FIBNumber floatSWidget;

	private static FIBLabel doubleLabel;
	private static FIBNumber doublePWidget;
	private static FIBNumber doubleOWidget;
	private static FIBNumber doubleSWidget;

	private static FIBController controller;
	private static Numbers numbers;

	/**
	 * Create an initial component
	 */
	@Test
	@TestOrder(1)
	public void test1CreateComponent() {

		log("FIBNumberWidgetTest test1CreateComponent() on thread " + Thread.currentThread());

		System.out.println("FIBNumberWidgetTest test1CreateComponent() on thread " + Thread.currentThread());

		component = newFIBPanel();
		component.setLayout(Layout.grid);
		component.setRows(6);
		component.setCols(4);

		component.setDataClass(Numbers.class);

		byteLabel = newFIBLabel("byte");
		component.addToSubComponents(byteLabel, new GridLayoutConstraints(0, 0));
		bytePWidget = newFIBNumber();
		bytePWidget.setNumberType(NumberType.ByteType);
		bytePWidget.setData(new DataBinding<String>("data.byteP", bytePWidget, Byte.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(bytePWidget, new GridLayoutConstraints(1, 0));
		byteOWidget = newFIBNumber();
		byteOWidget.setNumberType(NumberType.ByteType);
		byteOWidget.setData(new DataBinding<String>("data.byteO", byteOWidget, Byte.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(byteOWidget, new GridLayoutConstraints(2, 0));
		byteSWidget = newFIBNumber();
		byteSWidget.setNumberType(NumberType.ByteType);
		byteSWidget.setData(new DataBinding<String>("data.byteP+data.byteO", byteSWidget, Byte.class, BindingDefinitionType.GET));
		component.addToSubComponents(byteSWidget, new GridLayoutConstraints(3, 0));

		assertTrue(bytePWidget.getData().isValid());
		assertTrue(byteOWidget.getData().isValid());
		assertTrue(byteSWidget.getData().isValid());

		shortLabel = newFIBLabel("short");
		component.addToSubComponents(shortLabel, new GridLayoutConstraints(0, 1));
		shortPWidget = newFIBNumber();
		shortPWidget.setNumberType(NumberType.ShortType);
		shortPWidget.setData(new DataBinding<String>("data.shortP", shortPWidget, Short.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(shortPWidget, new GridLayoutConstraints(1, 1));
		shortOWidget = newFIBNumber();
		shortOWidget.setNumberType(NumberType.ShortType);
		shortOWidget.setData(new DataBinding<String>("data.shortO", shortOWidget, Short.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(shortOWidget, new GridLayoutConstraints(2, 1));
		shortSWidget = newFIBNumber();
		shortSWidget.setNumberType(NumberType.ShortType);
		shortSWidget.setData(new DataBinding<String>("data.shortP+data.shortO", shortSWidget, Short.class, BindingDefinitionType.GET));
		component.addToSubComponents(shortSWidget, new GridLayoutConstraints(3, 1));

		assertTrue(shortPWidget.getData().isValid());
		assertTrue(shortOWidget.getData().isValid());
		assertTrue(shortSWidget.getData().isValid());

		integerLabel = newFIBLabel("integer");
		component.addToSubComponents(integerLabel, new GridLayoutConstraints(0, 2));
		integerPWidget = newFIBNumber();
		integerPWidget.setNumberType(NumberType.IntegerType);
		integerPWidget.setData(new DataBinding<String>("data.intP", integerPWidget, Integer.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(integerPWidget, new GridLayoutConstraints(1, 2));
		integerOWidget = newFIBNumber();
		integerOWidget.setNumberType(NumberType.IntegerType);
		integerOWidget.setData(new DataBinding<String>("data.intO", integerOWidget, Integer.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(integerOWidget, new GridLayoutConstraints(2, 2));
		integerSWidget = newFIBNumber();
		integerSWidget.setNumberType(NumberType.IntegerType);
		integerSWidget.setData(new DataBinding<String>("data.intP+data.intO", integerSWidget, Integer.class, BindingDefinitionType.GET));
		component.addToSubComponents(integerSWidget, new GridLayoutConstraints(3, 2));

		assertTrue(integerPWidget.getData().isValid());
		assertTrue(integerOWidget.getData().isValid());
		assertTrue(integerSWidget.getData().isValid());

		longLabel = newFIBLabel("long");
		component.addToSubComponents(longLabel, new GridLayoutConstraints(0, 3));
		longPWidget = newFIBNumber();
		longPWidget.setNumberType(NumberType.LongType);
		longPWidget.setData(new DataBinding<String>("data.longP", longPWidget, Long.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(longPWidget, new GridLayoutConstraints(1, 3));
		longOWidget = newFIBNumber();
		longOWidget.setNumberType(NumberType.LongType);
		longOWidget.setData(new DataBinding<String>("data.longO", longOWidget, Long.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(longOWidget, new GridLayoutConstraints(2, 3));
		longSWidget = newFIBNumber();
		longSWidget.setNumberType(NumberType.LongType);
		longSWidget.setData(new DataBinding<String>("data.longP+data.longO", longSWidget, Long.class, BindingDefinitionType.GET));
		component.addToSubComponents(longSWidget, new GridLayoutConstraints(3, 3));

		assertTrue(longPWidget.getData().isValid());
		assertTrue(longOWidget.getData().isValid());
		assertTrue(longSWidget.getData().isValid());

		floatLabel = newFIBLabel("float");
		component.addToSubComponents(floatLabel, new GridLayoutConstraints(0, 4));
		floatPWidget = newFIBNumber();
		floatPWidget.setNumberType(NumberType.FloatType);
		floatPWidget.setData(new DataBinding<String>("data.floatP", floatPWidget, Float.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(floatPWidget, new GridLayoutConstraints(1, 4));
		floatOWidget = newFIBNumber();
		floatOWidget.setNumberType(NumberType.FloatType);
		floatOWidget.setData(new DataBinding<String>("data.floatO", floatOWidget, Float.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(floatOWidget, new GridLayoutConstraints(2, 4));
		floatSWidget = newFIBNumber();
		floatSWidget.setNumberType(NumberType.FloatType);
		floatSWidget.setData(new DataBinding<String>("data.floatP+data.floatO", floatSWidget, Float.class, BindingDefinitionType.GET));
		component.addToSubComponents(floatSWidget, new GridLayoutConstraints(3, 4));

		assertTrue(floatPWidget.getData().isValid());
		assertTrue(floatOWidget.getData().isValid());
		assertTrue(floatSWidget.getData().isValid());

		doubleLabel = newFIBLabel("double");
		component.addToSubComponents(doubleLabel, new GridLayoutConstraints(0, 5));
		doublePWidget = newFIBNumber();
		doublePWidget.setNumberType(NumberType.DoubleType);
		doublePWidget.setData(new DataBinding<String>("data.doubleP", doublePWidget, Double.TYPE, BindingDefinitionType.GET_SET));
		component.addToSubComponents(doublePWidget, new GridLayoutConstraints(1, 5));
		doubleOWidget = newFIBNumber();
		doubleOWidget.setNumberType(NumberType.DoubleType);
		doubleOWidget.setData(new DataBinding<String>("data.doubleO", doubleOWidget, Double.class, BindingDefinitionType.GET_SET));
		component.addToSubComponents(doubleOWidget, new GridLayoutConstraints(2, 5));
		doubleSWidget = newFIBNumber();
		doubleSWidget.setNumberType(NumberType.DoubleType);
		doubleSWidget.setData(new DataBinding<String>("data.doubleP+data.doubleO", doubleSWidget, Double.class, BindingDefinitionType.GET));
		component.addToSubComponents(doubleSWidget, new GridLayoutConstraints(3, 5));

		assertTrue(doublePWidget.getData().isValid());
		assertTrue(doubleOWidget.getData().isValid());
		assertTrue(doubleSWidget.getData().isValid());

	}

	/**
	 * Instanciate component, while instanciating view AFTER data has been set
	 */
	@Test
	@TestOrder(2)
	public void test2InstanciateComponent() {

		log("FIBNumberWidgetTest test2InstanciateComponent() on thread " + Thread.currentThread());

		System.out.println("FIBNumberWidgetTest test2InstanciateComponent() on thread " + Thread.currentThread());

		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		numbers = new Numbers();
		controller.setDataObject(numbers);
		controller.buildView();

		gcDelegate.addTab("Test2", controller);

		assertNotNull(controller.getRootView());

		assertNotNull(controller.viewForComponent(byteLabel));
		assertNotNull(controller.viewForComponent(bytePWidget));
		assertNotNull(controller.viewForComponent(byteOWidget));

		assertNotNull(controller.viewForComponent(shortLabel));
		assertNotNull(controller.viewForComponent(shortPWidget));
		assertNotNull(controller.viewForComponent(shortOWidget));

		assertNotNull(controller.viewForComponent(integerLabel));
		assertNotNull(controller.viewForComponent(integerPWidget));
		assertNotNull(controller.viewForComponent(integerOWidget));

		assertNotNull(controller.viewForComponent(longLabel));
		assertNotNull(controller.viewForComponent(longPWidget));
		assertNotNull(controller.viewForComponent(longOWidget));

		assertNotNull(controller.viewForComponent(floatLabel));
		assertNotNull(controller.viewForComponent(floatPWidget));
		assertNotNull(controller.viewForComponent(floatOWidget));

		assertNotNull(controller.viewForComponent(doubleLabel));
		assertNotNull(controller.viewForComponent(doublePWidget));
		assertNotNull(controller.viewForComponent(doubleOWidget));

		// controller.viewForComponent(firstNameTF).update();

		assertEquals(Byte.valueOf((byte) 1), controller.viewForWidget(bytePWidget).getRepresentedValue());
		assertEquals(Byte.valueOf((byte) 7), controller.viewForWidget(byteOWidget).getRepresentedValue());
		assertEquals(Byte.valueOf((byte) 8), controller.viewForWidget(byteSWidget).getRepresentedValue());

		assertEquals(Short.valueOf((short) 2), controller.viewForWidget(shortPWidget).getRepresentedValue());
		assertEquals(Short.valueOf((short) 8), controller.viewForWidget(shortOWidget).getRepresentedValue());
		assertEquals(Short.valueOf((short) 10), controller.viewForWidget(shortSWidget).getRepresentedValue());

		assertEquals(Integer.valueOf(3), controller.viewForWidget(integerPWidget).getRepresentedValue());
		assertEquals(Integer.valueOf(9), controller.viewForWidget(integerOWidget).getRepresentedValue());
		assertEquals(Integer.valueOf(12), controller.viewForWidget(integerSWidget).getRepresentedValue());

		assertEquals(Long.valueOf(4), controller.viewForWidget(longPWidget).getRepresentedValue());
		assertEquals(Long.valueOf(10), controller.viewForWidget(longOWidget).getRepresentedValue());
		assertEquals(Long.valueOf(14), controller.viewForWidget(longSWidget).getRepresentedValue());

		assertEquals(Float.valueOf(5), controller.viewForWidget(floatPWidget).getRepresentedValue());
		assertEquals(Float.valueOf(11), controller.viewForWidget(floatOWidget).getRepresentedValue());
		assertEquals(Float.valueOf(16), controller.viewForWidget(floatSWidget).getRepresentedValue());

		assertEquals(Double.valueOf(6), controller.viewForWidget(doublePWidget).getRepresentedValue());
		assertEquals(Double.valueOf(12), controller.viewForWidget(doubleOWidget).getRepresentedValue());
		assertEquals(Double.valueOf(18), controller.viewForWidget(doubleSWidget).getRepresentedValue());

	}

	/**
	 * Update the model, and check that widgets have well reacted
	 */
	@Test
	@TestOrder(3)
	public void test3ModifyValueInModel() {
		numbers.setByteP((byte) 101);
		numbers.setByteO(Byte.valueOf((byte) 107));
		assertEquals((byte) 101, controller.viewForWidget(bytePWidget).getRepresentedValue());
		assertEquals((byte) 107, controller.viewForWidget(byteOWidget).getRepresentedValue());
		assertEquals((byte) 208, controller.viewForWidget(byteSWidget).getRepresentedValue());

		numbers.setShortP((short) 102);
		numbers.setShortO(Short.valueOf((short) 108));
		assertEquals((short) 102, controller.viewForWidget(shortPWidget).getRepresentedValue());
		assertEquals((short) 108, controller.viewForWidget(shortOWidget).getRepresentedValue());
		assertEquals((short) 210, controller.viewForWidget(shortSWidget).getRepresentedValue());

		numbers.setIntP(103);
		numbers.setIntO(Integer.valueOf(109));
		assertEquals(103, controller.viewForWidget(integerPWidget).getRepresentedValue());
		assertEquals(109, controller.viewForWidget(integerOWidget).getRepresentedValue());
		assertEquals(212, controller.viewForWidget(integerSWidget).getRepresentedValue());

		numbers.setLongP(104);
		numbers.setLongO(Long.valueOf(110));
		assertEquals((long) 104, controller.viewForWidget(longPWidget).getRepresentedValue());
		assertEquals((long) 110, controller.viewForWidget(longOWidget).getRepresentedValue());
		assertEquals((long) 214, controller.viewForWidget(longSWidget).getRepresentedValue());

		numbers.setFloatP(105);
		numbers.setFloatO(Float.valueOf(111));
		assertEquals((float) 105, controller.viewForWidget(floatPWidget).getRepresentedValue());
		assertEquals((float) 111, controller.viewForWidget(floatOWidget).getRepresentedValue());
		assertEquals((float) 216, controller.viewForWidget(floatSWidget).getRepresentedValue());

		numbers.setDoubleP(106);
		numbers.setDoubleO(Double.valueOf(112));
		assertEquals((double) 106, controller.viewForWidget(doublePWidget).getRepresentedValue());
		assertEquals((double) 112, controller.viewForWidget(doubleOWidget).getRepresentedValue());
		assertEquals((double) 218, controller.viewForWidget(doubleSWidget).getRepresentedValue());

	}

	/**
	 * Update the widget, and check that model has well reacted
	 */
	@Test
	@TestOrder(4)
	public void test4ModifyValueInWidget() {

		JFIBNumberWidget<Byte> bytePWidgetView = (JFIBNumberWidget<Byte>) controller.viewForComponent(bytePWidget);
		bytePWidgetView.getTechnologyComponent().setEditedValue((byte) 201);
		assertEquals((byte) 201, numbers.getByteP());

		JFIBNumberWidget<Byte> byteOWidgetView = (JFIBNumberWidget<Byte>) controller.viewForComponent(byteOWidget);
		byteOWidgetView.getTechnologyComponent().setEditedValue((byte) 207);
		assertEquals(Byte.valueOf((byte) 207), numbers.getByteO());

		assertEquals((byte) 408, controller.viewForWidget(byteSWidget).getRepresentedValue());

		JFIBNumberWidget<Short> shortPWidgetView = (JFIBNumberWidget<Short>) controller.viewForComponent(shortPWidget);
		shortPWidgetView.getTechnologyComponent().setEditedValue((short) 202);
		assertEquals((short) 202, numbers.getShortP());

		JFIBNumberWidget<Short> shortOWidgetView = (JFIBNumberWidget<Short>) controller.viewForComponent(shortOWidget);
		shortOWidgetView.getTechnologyComponent().setEditedValue((short) 208);
		assertEquals(Short.valueOf((short) 208), numbers.getShortO());

		assertEquals((short) 410, controller.viewForWidget(shortSWidget).getRepresentedValue());

		JFIBNumberWidget<Integer> integerPWidgetView = (JFIBNumberWidget<Integer>) controller.viewForComponent(integerPWidget);
		integerPWidgetView.getTechnologyComponent().setEditedValue(203);
		assertEquals(203, numbers.getIntP());

		JFIBNumberWidget<Integer> integerOWidgetView = (JFIBNumberWidget<Integer>) controller.viewForComponent(integerOWidget);
		integerOWidgetView.getTechnologyComponent().setEditedValue(209);
		assertEquals(Integer.valueOf(209), numbers.getIntO());

		assertEquals(412, controller.viewForWidget(integerSWidget).getRepresentedValue());

		JFIBNumberWidget<Long> longPWidgetView = (JFIBNumberWidget<Long>) controller.viewForComponent(longPWidget);
		longPWidgetView.getTechnologyComponent().setEditedValue((long) 204);
		assertEquals(204, numbers.getLongP());

		JFIBNumberWidget<Long> longOWidgetView = (JFIBNumberWidget<Long>) controller.viewForComponent(longOWidget);
		longOWidgetView.getTechnologyComponent().setEditedValue((long) 210);
		assertEquals(Long.valueOf(210), numbers.getLongO());

		assertEquals((long) 414, controller.viewForWidget(longSWidget).getRepresentedValue());

		JFIBNumberWidget<Float> floatPWidgetView = (JFIBNumberWidget<Float>) controller.viewForComponent(floatPWidget);
		floatPWidgetView.getTechnologyComponent().setEditedValue((float) 205);
		assertEquals(205, numbers.getFloatP(), 0.000001);

		JFIBNumberWidget<Float> floatOWidgetView = (JFIBNumberWidget<Float>) controller.viewForComponent(floatOWidget);
		floatOWidgetView.getTechnologyComponent().setEditedValue((float) 211);
		assertEquals(Float.valueOf(211), numbers.getFloatO(), 0.000001);

		assertEquals((float) 416, controller.viewForWidget(floatSWidget).getRepresentedValue());

		JFIBNumberWidget<Double> doublePWidgetView = (JFIBNumberWidget<Double>) controller.viewForComponent(doublePWidget);
		doublePWidgetView.getTechnologyComponent().setEditedValue((double) 205);
		assertEquals(205, numbers.getDoubleP(), 0.000001);

		JFIBNumberWidget<Double> doubleOWidgetView = (JFIBNumberWidget<Double>) controller.viewForComponent(doubleOWidget);
		doubleOWidgetView.getTechnologyComponent().setEditedValue((double) 211);
		assertEquals(Double.valueOf(211), numbers.getDoubleO(), 0.000001);

		assertEquals((double) 416, controller.viewForWidget(doubleSWidget).getRepresentedValue());

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(FIBNumberWidgetTest.class.getSimpleName());
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

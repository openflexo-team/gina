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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.model.bindings.FIBChildBindingVariable;
import org.openflexo.gina.model.bindings.FIBVariableBindingVariable;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBNumber.NumberType;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.widget.JFIBCheckBoxWidget;
import org.openflexo.gina.swing.view.widget.JFIBNumberWidget;
import org.openflexo.gina.swing.view.widget.JFIBTextFieldWidget;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test BindingModel computation according to a dynamic component structure
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestBindingModel extends FIBTestCase {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static FIBPanel panel1;
	private static FIBPanel panel2;
	private static FIBPanel panel3;

	private static FIBLabel label;
	private static FIBTextField textfield1;
	private static FIBTextField textfield2;
	private static FIBCheckBox checkbox;
	private static FIBNumber number;

	private static FIBController controller;
	private static Family family;

	/**
	 * Create and test root component
	 */
	@Test
	@TestOrder(1)
	public void test1CreateRootPanel() {

		log("test1CreateRootPanel");

		panel1 = newFIBPanel();
		panel1.setLayout(Layout.twocols);
		panel1.setDataClass(Person.class);

		System.out.println("BindingModel for panel1 =" + panel1.getBindingModel());

		// Perform some checks on BindingModel
		assertNotNull(panel1.getBindingModel());
		assertEquals(2, panel1.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(panel1.getBindingModel().bindingVariableNamed("controller"));
		assertTrue(panel1.getBindingModel().bindingVariableNamed("data") instanceof FIBVariableBindingVariable);
		assertSame(panel1.getVariable("data"),
				((FIBVariableBindingVariable) panel1.getBindingModel().bindingVariableNamed("data")).getVariable());
		assertSame(panel1.getBindingModel().getControllerBindingVariable(), panel1.getBindingModel().bindingVariableNamed("controller"));
		assertEquals(Person.class, panel1.getBindingModel().bindingVariableNamed("data").getType());
		assertEquals(FIBController.class, panel1.getBindingModel().getControllerBindingVariable().getType());

		// Change the data class, check that 'data' BindingVariable changed its
		// type
		panel1.setDataClass(Family.class);
		assertEquals(Family.class, panel1.getBindingModel().bindingVariableNamed("data").getType());

		// Change the controller class, check that 'controller' BindingVariable
		// changed its type
		panel1.setControllerClass(TestCustomFIBController.class);
		assertEquals(TestCustomFIBController.class, panel1.getBindingModel().getControllerBindingVariable().getType());

		// Gives a name to the root panel
		panel1.setName("RootPanel");

		// BindingModel remains the same
		assertEquals(2, panel1.getBindingModel().getBindingVariablesCount());
	}

	/**
	 * First tests on component hierarchy
	 */
	@Test
	@TestOrder(2)
	public void test2TestBindingModelOnSingleWidget() {

		log("test2TestBindingModelOnSingleWidget");

		System.out.println("BindingModel for panel1 =" + panel1.getBindingModel());

		label = newFIBLabel("person");
		panel1.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));

		System.out.println("BindingModel for label =" + label.getBindingModel());
		assertEquals(panel1.getBindingModel(), label.getBindingModel().getBaseBindingModel());
		assertNotNull(label.getBindingModel().bindingVariableNamed("value"));
		assertEquals(String.class, label.getBindingModel().bindingVariableNamed("value").getType());

		textfield1 = newFIBTextField();
		textfield1.setData(new DataBinding<String>("data.father.firstName", textfield1, String.class, BindingDefinitionType.GET_SET));

		System.out.println("BindingModel for textfield1 =" + textfield1.getBindingModel());

		// Perform some checks on BindingModel on disconnected widget
		assertNotNull(textfield1.getBindingModel());

		assertEquals(2, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("value"));
		assertEquals(String.class, textfield1.getBindingModel().bindingVariableNamed("value").getType());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertEquals(FIBController.class, textfield1.getBindingModel().bindingVariableNamed("controller").getType());
		assertEquals(FIBController.class, textfield1.getBindingModel().getControllerBindingVariable().getType());
		assertSame(textfield1.getBindingModel().getControllerBindingVariable(),
				textfield1.getBindingModel().bindingVariableNamed("controller"));

		assertFalse(textfield1.getData().isValid());

		// Now connect the widget in hierarchy, check BindingModel validity
		panel1.addToSubComponents(textfield1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		System.out.println("BindingModel for textfield1 =" + textfield1.getBindingModel());

		assertNotNull(textfield1.getBindingModel());
		assertEquals(panel1.getBindingModel(), textfield1.getBindingModel().getBaseBindingModel());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("value"));
		assertEquals(String.class, textfield1.getBindingModel().bindingVariableNamed("value").getType());

		assertEquals(3, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("value"));

		// Gives a name to the widget, BindingModel should remain with 3
		// BindingVariable
		textfield1.setName("TextField");
		assertEquals(3, textfield1.getBindingModel().getBindingVariablesCount());

		// Sets widget to have dynamic access, BindingModel should be augmented
		// with textfield1 dynamic access binding variable
		textfield1.setManageDynamicModel(true);

		System.out.println("BindingModel for textfield1 =" + textfield1.getBindingModel());

		assertEquals(4, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(textfield1.getName()));

		// Changes name of the widget, BindingModel should be update with TextField1 name
		textfield1.setName("TextField1");

		System.out.println("BindingModel for textfield1 =" + textfield1.getBindingModel());

		assertEquals(4, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("value"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(textfield1.getName()));

		assertTrue(textfield1.getData().isValid());
	}

	/**
	 * Extended tests on component hierarchy
	 */
	@Test
	@TestOrder(3)
	public void test3TestBindingModelWithMultipleWidgetsInContainer() {

		log("test3TestBindingModelWithMultipleWidgetsInContainer");

		panel2 = newFIBPanel();
		panel2.setLayout(Layout.twocols);

		// Perform some checks on BindingModel
		assertNotNull(panel2.getBindingModel());
		assertEquals(1, panel2.getBindingModel().getBindingVariablesCount());
		assertNull(panel2.getBindingModel().bindingVariableNamed("data")); // data
		// was
		// not
		// set
		// !
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("controller"));
		// assertSame(panel2.getVariable("data").getBindingVariable(),
		// panel2.getBindingModel().bindingVariableNamed("data"));
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		// assertEquals(Object.class,
		// panel2.getVariable("data").getBindingVariable().getType());
		assertEquals(FIBController.class, panel2.getBindingModel().getControllerBindingVariable().getType());

		FIBLabel l1 = newFIBLabel("textfield");
		panel2.addToSubComponents(l1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		textfield2 = newFIBTextField();
		textfield2.setData(new DataBinding<String>("person.firstName", textfield2, String.class, BindingDefinitionType.GET_SET));
		panel2.addToSubComponents(textfield2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		FIBLabel l2 = newFIBLabel("checkbox");
		panel2.addToSubComponents(l2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkbox = newFIBCheckBox();
		checkbox.setData(new DataBinding<Boolean>("person.firstName = 'Robert'", checkbox, Boolean.class, BindingDefinitionType.GET));
		panel2.addToSubComponents(checkbox, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		FIBLabel l3 = newFIBLabel("number");
		panel2.addToSubComponents(l3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		number = newFIBNumber();
		number.setNumberType(NumberType.IntegerType);
		number.setData(new DataBinding<Integer>("person.age", number, Integer.class, BindingDefinitionType.GET_SET));
		panel2.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		// Bindings are not valid since panel2 is not yet known
		assertFalse(textfield2.getData().isValid());
		assertFalse(checkbox.getData().isValid());
		assertFalse(number.getData().isValid());

		// Gives a name to the root panel, dynamic access should now be
		// accessible and a third BindingVariable should be managed
		panel2.setName("Panel");

		System.out.println("BindingModel for panel2 =" + panel2.getBindingModel());

		assertEquals(1, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		// assertNotNull(panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		// assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		// assertTrue(panel2.getDynamicAccessBindingVariable().getType() instanceof FIBViewType);

		// assertEquals(Object.class, ((ParameterizedType)
		// panel2.getDynamicAccessBindingVariable().getType()).getActualTypeArguments()[1]);

		FIBVariable<Person> personVariable = (FIBVariable<Person>) panel2.getModelFactory().newFIBVariable(panel2, "aPerson", Person.class);

		// panel2.setDataClass(Person.class);
		System.out.println("BindingModel for panel2 =" + panel2.getBindingModel());

		assertEquals(2, panel2.getBindingModel().getBindingVariablesCount());

		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("aPerson"));

		assertTrue(panel2.getBindingModel().bindingVariableNamed("aPerson") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("aPerson"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("aPerson")).getVariable());

		assertEquals(Person.class, panel2.getVariable("aPerson").getType());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("aPerson").getType());

		// Bindings are still not valid because 'person' not known
		assertFalse(textfield2.getData().isValid());
		assertFalse(checkbox.getData().isValid());
		assertFalse(number.getData().isValid());

		System.out.println("Attention je mets a jour le nom de la variable");

		personVariable.setName("person");

		System.out.println("BindingModel for panel2 =" + panel2.getBindingModel());

		assertEquals(2, panel2.getBindingModel().getBindingVariablesCount());

		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("person"));

		assertTrue(panel2.getBindingModel().bindingVariableNamed("person") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("person"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("person")).getVariable());

		assertEquals(Person.class, panel2.getVariable("person").getType());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("person").getType());

		// Bindings should now be valid

		System.out.println("textfield2.getData()=" + textfield2.getData());
		System.out.println("valid=" + textfield2.getData().isValid());
		System.out.println("BM=" + textfield2.getData().getOwner().getBindingModel());

		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

		// We change name to panel2
		panel2.setName("Panel2");

		// We remove now widgets from panel2
		panel2.removeFromSubComponents(l1);
		panel2.removeFromSubComponents(textfield2);
		panel2.removeFromSubComponents(l2);
		panel2.removeFromSubComponents(checkbox);
		panel2.removeFromSubComponents(l3);
		panel2.removeFromSubComponents(number);

		// Now the bindings should not be valid anymore
		assertFalse(textfield2.getData().isValid());
		assertFalse(checkbox.getData().isValid());
		assertFalse(number.getData().isValid());

		panel2.addToSubComponents(l1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		panel2.addToSubComponents(textfield2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		panel2.addToSubComponents(l2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		panel2.addToSubComponents(checkbox, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		panel2.addToSubComponents(l3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		panel2.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		// Now the bindings should be valid again
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

		assertEquals(2, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertTrue(panel2.getBindingModel().bindingVariableNamed("person") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("person"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("person")).getVariable());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("person").getType());

		// Changing widget name does not change anything
		textfield2.setName("TextField2");
		assertEquals(2, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertTrue(panel2.getBindingModel().bindingVariableNamed("person") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("person"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("person")).getVariable());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("person").getType());

		// Setting manageDynamicModel for widget adds an entry in BindingModel
		textfield2.setManageDynamicModel(true);

		System.out.println("bm=" + panel2.getBindingModel());

		assertEquals(3, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertTrue(panel2.getBindingModel().bindingVariableNamed("person") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("person"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("person")).getVariable());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("person").getType());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()));
		assertTrue(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(textfield2.getName())).getComponent(),
				textfield2);

		textfield2.setName("TextField3");
		assertTrue(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(textfield2.getName())).getComponent(),
				textfield2);
		textfield2.setName("TextField2");

		checkbox.setName("CheckBox");
		checkbox.setManageDynamicModel(true);

		System.out.println("bm=" + panel2.getBindingModel());

		assertEquals(4, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertTrue(panel2.getBindingModel().bindingVariableNamed("person") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("person"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("person")).getVariable());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("person").getType());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()));
		assertTrue(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(textfield2.getName())).getComponent(),
				textfield2);
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(checkbox.getName()));
		assertTrue(panel2.getBindingModel().bindingVariableNamed(checkbox.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(checkbox.getName())).getComponent(), checkbox);

		number.setName("Number");
		number.setManageDynamicModel(true);

		assertEquals(5, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel().getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertTrue(panel2.getBindingModel().bindingVariableNamed("person") instanceof FIBVariableBindingVariable);
		assertSame(panel2.getVariable("person"),
				((FIBVariableBindingVariable) panel2.getBindingModel().bindingVariableNamed("person")).getVariable());
		assertEquals(Person.class, panel2.getBindingModel().bindingVariableNamed("person").getType());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()));
		assertTrue(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(textfield2.getName())).getComponent(),
				textfield2);
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(checkbox.getName()));
		assertTrue(panel2.getBindingModel().bindingVariableNamed(checkbox.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(checkbox.getName())).getComponent(), checkbox);
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(number.getName()));
		assertTrue(panel2.getBindingModel().bindingVariableNamed(number.getName()) instanceof FIBChildBindingVariable);
		assertSame(((FIBChildBindingVariable) panel2.getBindingModel().bindingVariableNamed(number.getName())).getComponent(), number);
	}

	/**
	 * Extended tests on component hierarchy
	 */
	@Test
	@TestOrder(4)
	public void test4TestComponentHierarchy() {

		log("test4TestComponentHierarchy");

		assertEquals(3, panel1.getBindingModel().getBindingVariablesCount());

		panel1.addToSubComponents(panel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));

		System.out.println("BM for panel1: " + panel1.getBindingModel());
		assertEquals(7, panel1.getBindingModel().getBindingVariablesCount());

		System.out.println("BM for panel2: " + panel2.getBindingModel());
		assertEquals(8, panel2.getBindingModel().getBindingVariablesCount());

		System.out.println("BM for textfield1: " + textfield1.getBindingModel());
		assertEquals(8, textfield1.getBindingModel().getBindingVariablesCount());

		System.out.println("BM for textfield2: " + textfield2.getBindingModel());
		assertEquals(9, textfield2.getBindingModel().getBindingVariablesCount());

		System.out.println("BM for checkbox: " + checkbox.getBindingModel());
		assertEquals(9, checkbox.getBindingModel().getBindingVariablesCount());

		System.out.println("BM for number: " + number.getBindingModel());
		assertEquals(9, number.getBindingModel().getBindingVariablesCount());

		// The bindings should remain valid
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

		assertEquals("person.firstName", textfield2.getData().toString());
		assertEquals("(person.firstName = \"Robert\")", checkbox.getData().toString());
		assertEquals("person.age", number.getData().toString());

		// We change the name of Panel2
		panel2.setName("Panel3");

		// The bindings should remain valid, but serialization has changed
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

		// We check that the serialization has changed
		assertEquals("person.firstName", textfield2.getData().toString());
		assertEquals("(person.firstName = \"Robert\")", checkbox.getData().toString());
		assertEquals("person.age", number.getData().toString());

		FIBVariable<Person> personVariable = panel2.getVariable("person");
		assertNotNull(personVariable);
		personVariable.setValue(new DataBinding<Person>("data.father"));

		assertTrue(personVariable.getValue().isValid());

		// assertTrue(panel2.getData().isValid());

		// We try here to "force" data type to be something else, but because binding is valid, analyzed type of binding is used instead
		personVariable.setType(Family.class);
		assertEquals(Person.class, personVariable.getType());

		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());
	}

	/**
	 * Extended tests on component hierarchy
	 */
	/*@Test
	@TestOrder(5)
	public void test5TestWidgetMove() {
	
		log("test5TestWidgetMove");
	
		assertEquals(8, panel1.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel(), panel1.getBindingModel());
		assertSame(textfield1.getBindingModel(), panel1.getBindingModel());
		assertSame(textfield2.getBindingModel(), panel1.getBindingModel());
		assertSame(checkbox.getBindingModel(), panel1.getBindingModel());
		assertSame(number.getBindingModel(), panel1.getBindingModel());
		assertNotNull(panel1.getBindingModel().bindingVariableNamed(textfield1.getName()));
	
		panel1.removeFromSubComponents(label);
		panel1.removeFromSubComponents(textfield1);
	
		assertEquals(7, panel1.getBindingModel().getBindingVariablesCount());
		assertNull(panel1.getBindingModel().bindingVariableNamed(textfield1.getName()));
	
		panel2.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		panel2.addToSubComponents(textfield1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
	
		assertEquals(8, panel1.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel1.getBindingModel().bindingVariableNamed(textfield1.getName()));
	
		panel2.removeFromSubComponents(label);
		panel2.removeFromSubComponents(textfield1);
	
		assertEquals(7, panel2.getBindingModel().getBindingVariablesCount());
		assertNull(panel2.getBindingModel().bindingVariableNamed(textfield1.getName()));
	
		panel1.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		panel1.addToSubComponents(textfield1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
	
		assertEquals(8, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(textfield1.getName()));
	}*/

	/**
	 * Instanciate component
	 */
	@Test
	@TestOrder(10)
	public void testInstanciateComponent() {

		log("testInstanciateComponent");

		controller = FIBController.instanciateController(panel1, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		assertTrue(controller instanceof TestCustomFIBController);
		family = new Family();
		controller.buildView();
		controller.setDataObject(family);

		gcDelegate.addTab("TestBindingModel", controller);

		assertNotNull(controller.getRootView());

		System.out.println("Panel1: ");
		System.out.println(panel1.getModelFactory().stringRepresentation(panel1));

		JFIBTextFieldWidget tf1 = (JFIBTextFieldWidget) controller.viewForWidget(textfield1);
		assertEquals("Robert", tf1.getValue());
		JFIBTextFieldWidget tf2 = (JFIBTextFieldWidget) controller.viewForWidget(textfield2);
		assertEquals("Robert", tf2.getValue());
		JFIBCheckBoxWidget cb = (JFIBCheckBoxWidget) controller.viewForWidget(checkbox);
		assertEquals(true, cb.getValue());
		JFIBNumberWidget<?> nb = (JFIBNumberWidget<?>) controller.viewForWidget(number);
		assertEquals(39, nb.getValue());

		/*System.out.println("Panel2: ");
		System.out.println(panel2.getModelFactory().stringRepresentation(panel2));*/

	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestBindingModel.class.getSimpleName());
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

	public static class TestCustomFIBController extends FIBController {

		public TestCustomFIBController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
			super(rootComponent, viewFactory);
		}

	}

}

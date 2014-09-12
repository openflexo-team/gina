package org.openflexo.fib.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.ParameterizedType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.fib.FIBTestCase;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.testutils.GraphicalContextDelegate;
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

	private static GraphicalContextDelegate gcDelegate;

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

		panel1 = newFIBPanel();
		panel1.setLayout(Layout.twocols);
		panel1.setDataClass(Person.class);

		// Perform some checks on BindingModel
		assertNotNull(panel1.getBindingModel());
		assertEquals(2, panel1.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(panel1.getBindingModel().bindingVariableNamed("controller"));
		assertSame(panel1.getDataBindingVariable(), panel1.getBindingModel().bindingVariableNamed("data"));
		assertSame(panel1.getControllerBindingVariable(), panel1.getBindingModel().bindingVariableNamed("controller"));
		assertEquals(Person.class, panel1.getDataBindingVariable().getType());
		assertEquals(FIBController.class, panel1.getControllerBindingVariable().getType());

		// Change the data class, check that 'data' BindingVariable changed its type
		panel1.setDataClass(Family.class);
		assertEquals(Family.class, panel1.getDataBindingVariable().getType());

		// Change the controller class, check that 'controller' BindingVariable changed its type
		panel1.setControllerClass(TestCustomFIBController.class);
		assertEquals(TestCustomFIBController.class, panel1.getControllerBindingVariable().getType());

		// Gives a name to the root panel, dynamic access should now be accessible and a third BindingVariable should be managed
		panel1.setName("RootPanel");
		assertEquals(3, panel1.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel1.getBindingModel().bindingVariableNamed(panel1.getName()));
		assertSame(panel1.getDynamicAccessBindingVariable(), panel1.getBindingModel().bindingVariableNamed(panel1.getName()));
	}

	/**
	 * First tests on component hierarchy
	 */
	@Test
	@TestOrder(2)
	public void test2TestBindingModelOnSingleWidget() {

		label = newFIBLabel("person");
		panel1.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));

		textfield1 = newFIBTextField();
		textfield1.setData(new DataBinding<String>("data.father.firstName", textfield1, String.class, BindingDefinitionType.GET_SET));

		// Perform some checks on BindingModel on disconnected widget
		assertNotNull(textfield1.getBindingModel());
		assertEquals(2, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertSame(textfield1.getDataBindingVariable(), textfield1.getBindingModel().bindingVariableNamed("data"));
		assertSame(textfield1.getControllerBindingVariable(), textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertEquals(String.class, textfield1.getDataBindingVariable().getType());
		assertEquals(FIBController.class, textfield1.getControllerBindingVariable().getType());

		assertFalse(textfield1.getData().isValid());

		// Now connect the widget in hierarchy, check BindingModel validity
		panel1.addToSubComponents(textfield1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		assertNotNull(textfield1.getBindingModel());
		assertSame(panel1.getBindingModel(), textfield1.getBindingModel());
		assertEquals(3, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(panel1.getName()));

		// Gives a name to the widget, BindingModel should remain with 3 BindingVariable
		textfield1.setName("TextField");
		assertEquals(3, textfield1.getBindingModel().getBindingVariablesCount());

		// Sets widget to have dynamic access, BindingModel should be augmented with textfield1 dynamic access binding variable
		textfield1.setManageDynamicModel(true);
		assertEquals(4, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(panel1.getName()));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(textfield1.getName()));

		// Changes name of the widget, BindingModel should remain with 4 BindingVariable
		textfield1.setName("TextField1");
		assertEquals(4, textfield1.getBindingModel().getBindingVariablesCount());
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(panel1.getName()));
		assertNotNull(textfield1.getBindingModel().bindingVariableNamed(textfield1.getName()));

		assertTrue(textfield1.getData().isValid());
	}

	/**
	 * Extended tests on component hierarchy
	 */
	@Test
	@TestOrder(3)
	public void test3TestBindingModelWithMultipleWidgetsInContainer() {

		panel2 = newFIBPanel();
		panel2.setLayout(Layout.twocols);

		// Perform some checks on BindingModel
		assertNotNull(panel2.getBindingModel());
		assertEquals(2, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("controller"));
		assertSame(panel2.getDataBindingVariable(), panel2.getBindingModel().bindingVariableNamed("data"));
		assertSame(panel2.getControllerBindingVariable(), panel2.getBindingModel().bindingVariableNamed("controller"));
		assertEquals(Object.class, panel2.getDataBindingVariable().getType());
		assertEquals(FIBController.class, panel2.getControllerBindingVariable().getType());

		FIBLabel l1 = newFIBLabel("textfield");
		panel2.addToSubComponents(l1, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		textfield2 = newFIBTextField();
		textfield2.setData(new DataBinding<String>("Panel2.data.firstName", textfield2, String.class, BindingDefinitionType.GET_SET));
		panel2.addToSubComponents(textfield2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		FIBLabel l2 = newFIBLabel("checkbox");
		panel2.addToSubComponents(l2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		checkbox = newFIBCheckBox();
		checkbox.setData(new DataBinding<Boolean>("Panel2.data.firstName = 'Robert'", checkbox, Boolean.class, BindingDefinitionType.GET));
		panel2.addToSubComponents(checkbox, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		FIBLabel l3 = newFIBLabel("number");
		panel2.addToSubComponents(l3, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		number = newFIBNumber();
		number.setNumberType(NumberType.IntegerType);
		number.setData(new DataBinding<Integer>("Panel2.data.age", number, Integer.class, BindingDefinitionType.GET_SET));
		panel2.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));

		// Bindings are not valid since panel2 is not yet known
		assertFalse(textfield2.getData().isValid());
		assertFalse(checkbox.getData().isValid());
		assertFalse(number.getData().isValid());

		// Gives a name to the root panel, dynamic access should now be accessible and a third BindingVariable should be managed
		panel2.setName("Panel");
		assertEquals(3, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertTrue(panel2.getDynamicAccessBindingVariable().getType() instanceof ParameterizedType);
		assertEquals(Object.class, ((ParameterizedType) panel2.getDynamicAccessBindingVariable().getType()).getActualTypeArguments()[2]);

		panel2.setDataClass(Person.class);
		assertEquals(3, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertEquals(Person.class, ((ParameterizedType) panel2.getDynamicAccessBindingVariable().getType()).getActualTypeArguments()[2]);

		// Bindings are still not valid because
		assertFalse(textfield2.getData().isValid());
		assertFalse(checkbox.getData().isValid());
		assertFalse(number.getData().isValid());

		// We change name to panel2
		panel2.setName("Panel2");

		// Now the bindings should become valid
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

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

		assertEquals(3, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));

		textfield2.setName("TextField2");
		assertEquals(3, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));

		textfield2.setManageDynamicModel(true);
		assertEquals(4, panel2.getBindingModel().getBindingVariablesCount());
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("data"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed("controller"));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(textfield2.getName()));
		assertSame(textfield2.getDynamicAccessBindingVariable(), textfield2.getBindingModel().bindingVariableNamed(textfield2.getName()));

		checkbox.setManageDynamicModel(true);
		assertEquals(4, panel2.getBindingModel().getBindingVariablesCount());

		checkbox.setName("CheckBox");
		assertEquals(5, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(checkbox.getName()));
		assertSame(checkbox.getDynamicAccessBindingVariable(), checkbox.getBindingModel().bindingVariableNamed(checkbox.getName()));

		number.setName("Number");
		number.setManageDynamicModel(true);
		assertEquals(6, panel2.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getDynamicAccessBindingVariable(), panel2.getBindingModel().bindingVariableNamed(panel2.getName()));
		assertNotNull(panel2.getBindingModel().bindingVariableNamed(number.getName()));
		assertSame(number.getDynamicAccessBindingVariable(), number.getBindingModel().bindingVariableNamed(number.getName()));

	}

	/**
	 * Extended tests on component hierarchy
	 */
	@Test
	@TestOrder(4)
	public void test4TestComponentHierarchy() {

		assertEquals(4, panel1.getBindingModel().getBindingVariablesCount());

		panel1.addToSubComponents(panel2, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));

		assertEquals(8, panel1.getBindingModel().getBindingVariablesCount());
		assertSame(panel2.getBindingModel(), panel1.getBindingModel());
		assertSame(textfield1.getBindingModel(), panel1.getBindingModel());
		assertSame(textfield2.getBindingModel(), panel1.getBindingModel());
		assertSame(checkbox.getBindingModel(), panel1.getBindingModel());
		assertSame(number.getBindingModel(), panel1.getBindingModel());

		// The bindings should remain valid
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

		assertEquals("Panel2.data.firstName", textfield2.getData().toString());
		assertEquals("(Panel2.data.firstName = \"Robert\")", checkbox.getData().toString());
		assertEquals("Panel2.data.age", number.getData().toString());

		// We change the name of Panel2
		panel2.setName("Panel3");

		// The bindings should remain valid, but serialization has changed
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());

		// We check that the serialization has changed
		assertEquals("Panel3.data.firstName", textfield2.getData().toString());
		assertEquals("(Panel3.data.firstName = \"Robert\")", checkbox.getData().toString());
		assertEquals("Panel3.data.age", number.getData().toString());

		panel2.setData(new DataBinding<Person>("data.father"));
		assertTrue(panel2.getData().isValid());

		panel2.setDataClass(Family.class);
		assertFalse(textfield2.getData().isValid());
		assertFalse(checkbox.getData().isValid());
		assertFalse(number.getData().isValid());

		panel2.setDataClass(Person.class);
		assertTrue(textfield2.getData().isValid());
		assertTrue(checkbox.getData().isValid());
		assertTrue(number.getData().isValid());
	}

	/**
	 * Extended tests on component hierarchy
	 */
	@Test
	@TestOrder(5)
	public void test5TestWidgetMove() {

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
	}

	/**
	 * Instanciate component
	 */
	@Test
	@TestOrder(10)
	public void test10InstanciateComponent() {

		controller = FIBController.instanciateController(panel1, FlexoLocalization.getMainLocalizer());
		assertNotNull(controller);
		assertTrue(controller instanceof TestCustomFIBController);
		family = new Family();
		controller.setDataObject(family);
		controller.buildView();

		gcDelegate.addTab("TestBindingModel", controller);

		assertNotNull(controller.getRootView());
	}

	@BeforeClass
	public static void initGUI() {
		gcDelegate = new GraphicalContextDelegate(TestBindingModel.class.getSimpleName());
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

		public TestCustomFIBController(FIBComponent rootComponent) {
			super(rootComponent);
		}

	}

}

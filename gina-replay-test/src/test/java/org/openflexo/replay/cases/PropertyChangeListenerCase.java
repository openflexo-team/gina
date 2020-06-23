package org.openflexo.replay.cases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.sampleData.Family.Gender;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.replay.test.Case;
import org.openflexo.replay.test.GraphicalContextDelegate;
import org.openflexo.replay.test.Window;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

@RunWith(OrderedRunner.class)
public class PropertyChangeListenerCase extends Case {

	private static Person personA;

	public static void main(String[] args) {
		// initExecutor(2);
		initCase(new PropertyChangeListenerCase());
	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testInit() {
		main(null);

		assertNotNull("the case is null", Case.getInstance());
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testModify() {
		getPersonA().setAge(20);
		getPersonA().setFirstName("Alex");
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void testWait() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		new Window(getManager(), 'A', Person.class, PropertyChangeListenerController.class, getPersonA());
	}

	@Override
	public Dimension getWindowSize() {
		return new Dimension(320, 100);
	}

	@Override
	public void initWindow(Window w) {
		FIBLabel labelFirstname = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		labelFirstname.setLabel("Firstname :");

		FIBTextField textFieldFirstname = GraphicalContextDelegate.getFactory().newInstance(FIBTextField.class);
		textFieldFirstname
				.setData(new DataBinding<String>("data.firstName", textFieldFirstname, String.class, BindingDefinitionType.GET_SET));
		textFieldFirstname.setName("firstnameTextField");

		FIBLabel labelLastname = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		labelLastname.setLabel("Lastname :");

		FIBTextField textFieldLastname = GraphicalContextDelegate.getFactory().newInstance(FIBTextField.class);
		textFieldLastname.setData(new DataBinding<String>("data.lastName", textFieldLastname, String.class, BindingDefinitionType.GET_SET));
		textFieldLastname.setName("lastnameTextField");

		w.getComponent().addToSubComponents(labelFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(textFieldFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		w.getComponent().addToSubComponents(labelLastname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(textFieldLastname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		assertTrue("component is invalid", w.getComponent().isValid());
	}

	public static Person getPersonA() {
		if (personA == null) {
			personA = new Person("Robert", "Smith", 39, Gender.Male);
			getEventManager().trackPropertyChange(personA);
		}

		return personA;
	}

	public static void setPersonA(Person person) {
		personA.setLastName(person.getLastName());
		personA.setFirstName(person.getFirstName());

	}

}

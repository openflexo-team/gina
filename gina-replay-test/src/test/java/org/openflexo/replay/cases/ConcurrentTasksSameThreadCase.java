package org.openflexo.replay.cases;

import java.awt.Dimension;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.sampleData.Family.Gender;
//import org.openflexo.replay.sampleData.Family.Gender;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.replay.test.Case;
import org.openflexo.replay.test.GraphicalContextDelegate;
import org.openflexo.replay.test.Window;

public class ConcurrentTasksSameThreadCase extends Case {

	private static Person personA;

	public static void main(String[] args) {
		initExecutor(2);
		initCase(new ConcurrentTasksSameThreadCase());
	}

	@Override
	public void start() {
		new Window(getManager(), 'A', Person.class, ConcurrentTasksSameThreadController.class, getPersonA());
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
		textFieldFirstname.setData(new DataBinding<>("data.firstName", textFieldFirstname, String.class, BindingDefinitionType.GET_SET));
		textFieldFirstname.setName("firstnameTextField");

		FIBButton buttonTask = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonTask.setLabel("Start count task");
		buttonTask.setAction(new DataBinding<>("controller.startTask()"));
		buttonTask.setName("taskButton");

		w.getComponent().addToSubComponents(labelFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(textFieldFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		w.getComponent().addToSubComponents(buttonTask, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));
	}

	public static Person getPersonA() {
		if (personA == null)
			personA = new Person("Robert", "Smith", 39, Gender.Male);

		return personA;
	}

	public static void setPersonA(Person person) {
		personA.setLastName(person.getLastName());
		personA.setFirstName(person.getFirstName());

	}

}

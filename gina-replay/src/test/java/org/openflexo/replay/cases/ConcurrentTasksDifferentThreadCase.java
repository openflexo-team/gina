package org.openflexo.replay.cases;

import java.awt.Dimension;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.replay.sampleData.Person;
import org.openflexo.replay.sampleData.Family.Gender;
import org.openflexo.replay.utils.GraphicalContextDelegate;
import org.openflexo.replay.utils.Window;

public class ConcurrentTasksDifferentThreadCase extends ConcurrentTasksSameThreadCase {

	private static Person personA;
	
	public static void main(String[] args) {
		initExecutor(1);
		initCase(new ConcurrentTasksDifferentThreadCase());
	}
	
	@Override
	public void start() {
		new Window(getManager(), 'A', Person.class, ConcurrentTasksDifferentThreadController.class, getPersonA());
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
		textFieldFirstname.setData(new DataBinding<String>("data.firstName", textFieldFirstname, String.class, BindingDefinitionType.GET_SET));
		textFieldFirstname.setName("firstnameTextField");
		
		FIBButton buttonTask = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonTask.setLabel("Start count task");
		buttonTask.setAction(new DataBinding<Object>("controller.startTask()"));
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

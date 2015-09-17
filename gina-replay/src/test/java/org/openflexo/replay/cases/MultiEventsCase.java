/**
 * HIM Recording example of a simple gina interface
 * 
 * @author Alexandre
 *
 */

package org.openflexo.replay.cases;

import java.awt.Dimension;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.replay.sampleData.Gender;
//import org.openflexo.replay.sampleData.Family.Gender;
import org.openflexo.replay.sampleData.Person;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.GraphicalContextDelegate;
import org.openflexo.replay.utils.Window;

public class MultiEventsCase extends Case {

	private static Person personA, personB;
	private static Window windowB;
	
	public static void main(String[] args) {
		initExecutor(1);
		initCase(new MultiEventsCase());
	}
	
	@Override
	public void start() {
		new Window(getManager(), 'A', Person.class, MultiEventsController.class, getPersonA());
		windowB = new Window(getManager(), 'B', Person.class, MultiEventsController.class, getPersonB());
	}
	
	@Override
	public Dimension getWindowSize() {
		return new Dimension(320, 280);
	}
	
	@Override
	public void initWindow(Window w) {
		FIBLabel labelFirstname = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		labelFirstname.setLabel("Firstname :");

		FIBLabel labelLastname = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		labelLastname.setLabel("Lastname :");
		
		FIBLabel labelOk = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		labelOk.setLabel("Prenium :");
		
		FIBTextField textFieldFirstname = GraphicalContextDelegate.getFactory().newInstance(FIBTextField.class);
		textFieldFirstname.setData(new DataBinding<String>("data.firstName", textFieldFirstname, String.class, BindingDefinitionType.GET_SET));
		textFieldFirstname.setName("firstnameTextField");
		
		FIBTextField textFieldLastname = GraphicalContextDelegate.getFactory().newInstance(FIBTextField.class);
		textFieldLastname.setData(new DataBinding<String>("data.lastName", textFieldLastname, String.class, BindingDefinitionType.GET_SET));
		textFieldLastname.setName("lastnameTextField");
		
		FIBCheckBox okCheckBox = GraphicalContextDelegate.getFactory().newInstance(FIBCheckBox.class);
		okCheckBox.setName("okCheckBox");
		
		FIBNumber numberWidget = GraphicalContextDelegate.getFactory().newInstance(FIBNumber.class);
		numberWidget.setNumberType(FIBNumber.NumberType.DoubleType);
		numberWidget.setIncrement(0.5f);
		numberWidget.setName("number");
		
		/*FIBRadioButtonList radioButtonList = GraphicalContextDelegate.getFactory().newInstance(FIBRadioButtonList.class);
		radioButtonList.setName("radio");*/
		
		FIBTextArea textArea = GraphicalContextDelegate.getFactory().newInstance(FIBTextArea.class);
		textArea.setName("textarea");
		
		FIBButton buttonOpen = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonOpen.setLabel("Open a copy of " + w.getLetter());
		buttonOpen.setAction(new DataBinding<Object>("controller.openCopy('" + w.getLetter() + "')"));
		buttonOpen.setName("openButton");
		
		FIBButton buttonCopy = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonCopy.setLabel("Copy To " + (w.getLetter() == 'A' ? 'B' : 'A'));
		buttonCopy.setEnable(new DataBinding<Boolean>("(data.firstName!='')&&(data.lastName!='')", buttonOpen, Boolean.class, BindingDefinitionType.EXECUTE));
		buttonCopy.setAction(new DataBinding<Object>("controller.copyTo('" + (w.getLetter() == 'A' ? 'B' : 'A') + "')"));
		buttonCopy.setName("copyButton");
		
		FIBButton buttonTask = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonTask.setLabel("Start count task");
		buttonTask.setAction(new DataBinding<Object>("controller.startTask()"));
		buttonTask.setName("taskButton");
		
		w.getComponent().addToSubComponents(labelFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(textFieldFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		w.getComponent().addToSubComponents(labelLastname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(textFieldLastname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		w.getComponent().addToSubComponents(labelOk, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(okCheckBox, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		w.getComponent().addToSubComponents(numberWidget, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(textArea, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		
		w.getComponent().addToSubComponents(buttonOpen, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		w.getComponent().addToSubComponents(buttonCopy, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		
		w.getComponent().addToSubComponents(buttonTask, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
	}

	public Window getWindowB() {
		return windowB;
	}

	public static Person getPersonA() {
		if (personA == null)
			personA = new Person("Robert", "Smith", 39, Gender.Male);

		return personA;
	}

	public static Person getPersonB() {
		if (personB == null)
			personB = new Person("Bob", "Martin", 14, Gender.Male);

		return personB;
	}
	
	public static void setPersonA(Person person) {
		personA.setLastName(person.getLastName());
		personA.setFirstName(person.getFirstName());
		
	}

	public static void setPersonB(Person person) {
		personB.setLastName(person.getLastName());
		personB.setFirstName(person.getFirstName());

	}

}

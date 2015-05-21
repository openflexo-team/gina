package org.openflexo.himtester.simple;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.himtester.utils.GraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;

public class SimpleWindow {
	private GraphicalContextDelegate gcDelegate;
	private FIBPanel component;
	private FIBController controller;
	private char letter;
	
	public SimpleWindow(char letter, Person person) {
		this.letter = letter;

		gcDelegate = new GraphicalContextDelegate("Window " + letter);
		gcDelegate.setUp();

		component = GraphicalContextDelegate.getFactory().newInstance(FIBPanel.class);
		component.setName("WindowFIB");
		component.setLayout(Layout.twocols);
		component.setDataClass(Person.class);
		component.setControllerClass(SimpleWindowController.class);
		
		this.createWindowPeople();
		
		// Instantiate the controller
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		controller.setDataObject(person);
		controller.buildView();

		gcDelegate.setController(controller);
	}
	
	public void createWindowPeople() {
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
		numberWidget.setName("number");
		
		/*FIBRadioButtonList radioButtonList = GraphicalContextDelegate.getFactory().newInstance(FIBRadioButtonList.class);
		radioButtonList.setName("radio");*/
		
		FIBTextArea textArea = GraphicalContextDelegate.getFactory().newInstance(FIBTextArea.class);
		textArea.setName("textarea");
		
		FIBButton buttonOpen = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonOpen.setLabel("Open a copy of " + this.letter);
		buttonOpen.setAction(new DataBinding<Object>("controller.openCopy('" + letter + "')"));
		buttonOpen.setName("openButton");
		
		FIBButton buttonCopy = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonCopy.setLabel("Copy To " + (this.letter == 'A' ? 'B' : 'A'));
		buttonCopy.setEnable(new DataBinding<Boolean>("(data.firstName!='')&&(data.lastName!='')", buttonOpen, Boolean.class, BindingDefinitionType.EXECUTE));
		buttonCopy.setAction(new DataBinding<Object>("controller.copyTo('" + (this.letter == 'A' ? 'B' : 'A') + "')"));
		buttonCopy.setName("copyButton");
		
		component.addToSubComponents(labelFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		component.addToSubComponents(textFieldFirstname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		component.addToSubComponents(labelLastname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		component.addToSubComponents(textFieldLastname, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		component.addToSubComponents(labelOk, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		component.addToSubComponents(okCheckBox, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		component.addToSubComponents(numberWidget, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		component.addToSubComponents(textArea, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		
		component.addToSubComponents(buttonOpen, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		component.addToSubComponents(buttonCopy, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
	}

}

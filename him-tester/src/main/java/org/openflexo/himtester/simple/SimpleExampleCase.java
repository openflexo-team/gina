/**
 * HIM Recording example of a simple gina interface
 * 
 * @author Alexandre
 *
 */

package org.openflexo.himtester.simple;

import java.io.File;

import org.openflexo.connie.DataBinding;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.sampleData.Person;
import org.openflexo.fib.sampleData.Family.Gender;
import org.openflexo.himtester.FIBRecorderEditor;
import org.openflexo.himtester.FIBRecorderManager;
import org.openflexo.himtester.utils.GraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;

public class SimpleExampleCase {

	private static SimpleExampleCase instance;
	private static Person personA, personB;

	private SimpleWindow windowB;
	private GraphicalContextDelegate gcDelegate;
	private FIBPanel component;
	private FIBController controller;

	public SimpleExampleCase() {
		new SimpleWindow('A', getPersonA());
		windowB = new SimpleWindow('B', getPersonB());
		
		createControllerWindow();
	}

	public void createControllerWindow() {
		gcDelegate = new GraphicalContextDelegate("FIB Recorder");
		gcDelegate.setUp();

		component = GraphicalContextDelegate.getFactory().newInstance(FIBPanel.class);
		component.setName("WindowRecorderFIB");
		component.setLayout(Layout.twocols);
		component.setDataClass(Person.class);
		component.setControllerClass(SimpleWindowController.class);
		
		FIBButton buttonPlay = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonPlay.setLabel("Save");
		buttonPlay.setAction(new DataBinding<Object>("controller.save()"));
		buttonPlay.setName("playButton");

		component.addToSubComponents(buttonPlay, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		
		// Instantiate the controller
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		//controller.setDataObject(null);
		controller.buildView();

		gcDelegate.setController(controller);
	}

	public SimpleWindow getWindowB() {
		return windowB;
	}

	public static SimpleExampleCase getInstance() {
		return instance;
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
	
	public static void main(String[] args) {
		FIBRecorderManager.getInstance().createAndSetToCurrent();
		//FIBRecorderManager.getInstance().getCurrentRecorder().load(new File("D:/test-gina-recorder"));
		//FIBRecorderManager.getInstance().getCurrentRecorder().startRecording();
		FIBRecorderEditor editor = new FIBRecorderEditor();
		
		instance = new SimpleExampleCase();
	}

}

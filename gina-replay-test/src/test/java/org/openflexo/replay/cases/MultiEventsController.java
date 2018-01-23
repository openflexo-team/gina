package org.openflexo.replay.cases;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.view.GinaViewFactory;

public class MultiEventsController extends FIBController {

	public MultiEventsController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public void openCopy(String windowLetter) {
		// System.out.println("openCopy");

		// Unused final char letter = windowLetter.charAt(0);

		/*MultiEventsCase.getTaskManager().scheduleExecution(new GinaTaskTemp("Open Window") {
		
			@Override
			public void performTask() throws InterruptedException {
				switch(letter)
				{
				case 'A':
					new Window(getRecorderManager(), letter, Person.class, MultiEventsController.class, MultiEventsCase.getPersonA());
					break;
				case 'B':
					new Window(getRecorderManager(), letter, Person.class, MultiEventsController.class, MultiEventsCase.getPersonB());
					break;
				}
			}
			
		});*/

		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch(letter)
				{
				case 'A':
					new SimpleWindow(letter, SimpleExampleCase.getPersonA());
					break;
				case 'B':
					new SimpleWindow(letter, SimpleExampleCase.getPersonB());
					break;
				}
			}
		
		});*/

		/*Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				switch(letter)
				{
				case 'A':
					new SimpleWindow(letter, SimpleExampleCase.getPersonA());
					break;
				case 'B':
					new SimpleWindow(letter, SimpleExampleCase.getPersonB());
					break;
				}
			}
		
		});
		t.start();*/
	}

	public void copyTo(String windowLetter) {
		// System.out.println("copyToB");

		char letter = windowLetter.charAt(0);

		switch (letter) {
			case 'A':
				MultiEventsCase.setPersonA(MultiEventsCase.getPersonB());
				break;
			case 'B':
				MultiEventsCase.setPersonB(MultiEventsCase.getPersonA());
				break;
		}
	}

	public void startTask() {
		/*Task task = new Task(Case.getManager(), "Count task", MultiEventsCase.getPersonA());
		MultiEventsCase.getTaskManager().scheduleExecution(task);
		Task task2 = new Task(Case.getManager(), "Count task 2", MultiEventsCase.getPersonA());
		MultiEventsCase.getTaskManager().scheduleExecution(task2);*/
	}

	public void save() {
		// GinaRecorderManager.getInstance().getCurrentRecorder().save(new File("D:/test-gina-recorder"));
	}

}

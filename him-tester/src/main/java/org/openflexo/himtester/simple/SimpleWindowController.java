package org.openflexo.himtester.simple;

import java.io.File;

import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.gina.task.GinaTask;
import org.openflexo.gina.task.ThreadPoolGinaTaskManager;
import org.openflexo.replay.GinaRecorderManager;

public class SimpleWindowController extends FIBController {

	public SimpleWindowController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public void openCopy(String windowLetter) {
		//System.out.println("openCopy");
		
		final char letter = windowLetter.charAt(0);
		
		SimpleExampleCase.getTaskManager().scheduleExecution(new GinaTask("Open Window") {

			@Override
			public void performTask() throws InterruptedException {
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
		//System.out.println("copyToB");
		
		char letter = windowLetter.charAt(0);
		
		switch(letter)
		{
		case 'A':
			SimpleExampleCase.setPersonA(SimpleExampleCase.getPersonB());
			break;
		case 'B':
			SimpleExampleCase.setPersonB(SimpleExampleCase.getPersonA());
			break;
		}
	}
	
	public void startTask() {
		SimpleTask task = new SimpleTask("Count task");
		SimpleExampleCase.getTaskManager().scheduleExecution(task);
	}
	
	public void save() {
		GinaRecorderManager.getInstance().getCurrentRecorder().save(new File("D:/test-gina-recorder"));
	}

}

package org.openflexo.replay.cases;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.Task;

public class PropertyChangeListenerController extends FIBController {

	public PropertyChangeListenerController(FIBComponent rootComponent) {
		super(rootComponent);
	}
	
	public void startTask() {
		Task task = new ChangeFirstnameTask(Case.getEventManager(), "Count task", ConcurrentTasksSameThreadCase.getPersonA());
		Case.getTaskExecutor().execute(task);
		Task task2 = new ChangeFirstnameTask(Case.getEventManager(), "Count task 2", ConcurrentTasksSameThreadCase.getPersonA());
		Case.getTaskExecutor().execute(task2);
	}

}

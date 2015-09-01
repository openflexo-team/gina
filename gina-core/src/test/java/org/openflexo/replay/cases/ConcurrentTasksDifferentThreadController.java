package org.openflexo.replay.cases;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.Task;

public class ConcurrentTasksDifferentThreadController extends FIBController {

	public ConcurrentTasksDifferentThreadController(FIBComponent rootComponent) {
		super(rootComponent);
	}
	
	public void startTask() {
		Task task = new ChangeFirstnameTask(Case.getManager(), "Count task", ConcurrentTasksDifferentThreadCase.getPersonA());
		Case.getTaskExecutor().execute(task);
		Task task2 = new ChangeFirstnameTask(Case.getManager(), "Count task 2", ConcurrentTasksDifferentThreadCase.getPersonA());
		Case.getTaskExecutor().execute(task2);
	}

}

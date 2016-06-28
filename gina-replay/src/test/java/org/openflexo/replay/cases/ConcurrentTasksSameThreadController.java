package org.openflexo.replay.cases;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.Task;

public class ConcurrentTasksSameThreadController extends FIBController {

	public ConcurrentTasksSameThreadController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public void startTask() {
		Task task = new ChangeFirstnameTask(Case.getEventManager(), "Count task", ConcurrentTasksSameThreadCase.getPersonA());
		Case.getTaskExecutor().execute(task);
		Task task2 = new ChangeFirstnameTask(Case.getEventManager(), "Count task 2", ConcurrentTasksSameThreadCase.getPersonA());
		Case.getTaskExecutor().execute(task2);
	}

}

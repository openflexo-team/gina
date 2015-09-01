package org.openflexo.replay.utils;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;

public class CaseCommandController extends FIBController {

	public CaseCommandController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public void save() {
		//GinaRecorderManager.getInstance().getCurrentRecorder().save(new File("D:/test-gina-recorder"));
	}

}

package org.openflexo.replay.utils;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;

public class CaseCommandController extends FIBController {

	public CaseCommandController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public void save() {
		/**
		 * TODO : currently the scenario is saved automatically to 'last-scenario'
		 */
		//File scenarioDir = ((FileResourceImpl) ResourceLocator.locateSourceCodeResource("scenarii")).getFile();
		//GinaRecorderManager.getInstance().getCurrentRecorder().save(scenarioDir);
	}

}

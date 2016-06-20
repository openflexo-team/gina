package org.openflexo.replay.utils;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.view.GinaViewFactory;

public class CaseCommandController extends FIBController {

	public CaseCommandController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public void save() {
		/**
		 * TODO : currently the scenario is saved automatically to 'last-scenario'
		 */
		//File scenarioDir = ((FileResourceImpl) ResourceLocator.locateSourceCodeResource("scenarii")).getFile();
		//GinaRecorderManager.getInstance().getCurrentRecorder().save(scenarioDir);
	}

}

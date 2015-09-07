package org.openflexo.replay.utils;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.replay.GinaReplayManager;

public class Window {
	protected GraphicalContextDelegate gcDelegate;
	protected GinaReplayManager manager;

	protected FIBPanel component;
	protected FIBController controller;
	
	private char letter;
	
	public Window(GinaReplayManager manager, char letter, Class<?> dataClass, Class<? extends FIBController> controllerClass, Object data) {
		this.letter = letter;
		this.manager = manager;

		gcDelegate = new GraphicalContextDelegate("Window " + this.letter, Case.getInstance().getWindowSize());
		gcDelegate.setUp();

		component = GraphicalContextDelegate.getFactory().newInstance(FIBPanel.class);
		component.setName("WindowFIB");
		component.setLayout(Layout.twocols);
		component.setDataClass(dataClass);
		component.setControllerClass(controllerClass);
		
		// Create the window
		Case.getInstance().initWindow(this);
		
		// Instantiate the controller
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer(), manager.getEventManager());
		controller.setDataObject(data);
		controller.buildView();

		gcDelegate.setController(controller);
	}

	public char getLetter() {
		return this.letter;
	}

	public FIBPanel getComponent() {
		return this.component;
	}
	
}

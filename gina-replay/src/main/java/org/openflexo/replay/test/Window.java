package org.openflexo.replay.test;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.replay.GinaReplayManager;

/**
 * Basic window for test purposes.
 * 
 * @author Alexandre
 *
 */
public class Window {
	protected GraphicalContextDelegate gcDelegate;
	protected GinaReplayManager manager;

	protected FIBPanel component;
	protected FIBController controller;

	private final char letter;

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
		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer(),
				manager.getEventManager());
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

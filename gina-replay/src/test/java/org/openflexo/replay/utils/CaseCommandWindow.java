package org.openflexo.replay.utils;

import org.openflexo.connie.DataBinding;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.container.FIBPanel;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints;
import org.openflexo.fib.model.container.FIBPanel.Layout;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.model.widget.FIBButton;
import org.openflexo.fib.model.widget.FIBLabel;
import org.openflexo.fib.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.sampleData.Person;

public class CaseCommandWindow {
	private GraphicalContextDelegate gcDelegate;
	private FIBPanel component;
	private FIBController controller;

	private GinaReplayManager manager;

	public CaseCommandWindow(GinaReplayManager manager) {
		super();

		this.manager = manager;

		createRecorderControllerWindow();
	}

	public void initCase() {
		manager = new GinaReplayManager();
		GinaReplaySession recorder = new GinaReplaySession(manager);
		manager.setCurrentSession(recorder);
		recorder.startRecording();

		createRecorderControllerWindow();
	}

	public void createRecorderControllerWindow() {
		gcDelegate = new GraphicalContextDelegate("FIB Recorder");
		gcDelegate.setUp();

		component = GraphicalContextDelegate.getFactory().newInstance(FIBPanel.class);
		component.setName("WindowRecorderFIB");
		component.setLayout(Layout.twocols);
		component.setDataClass(Person.class);
		component.setControllerClass(CaseCommandController.class);

		FIBButton buttonPlay = GraphicalContextDelegate.getFactory().newInstance(FIBButton.class);
		buttonPlay.setLabel("Save the Scenario");
		buttonPlay.setAction(new DataBinding<Object>("controller.save()"));
		buttonPlay.setName("saveButton");
		component.addToSubComponents(buttonPlay, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));

		FIBLabel listLabel = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		listLabel.setLabel("For the replay, save and launch org.openflexo.replay.test.Tester");
		component.addToSubComponents(listLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));

		// Instantiate the controller
		controller = FIBController.instanciateController(component, SwingViewFactory.INSTANCE, FlexoLocalization.getMainLocalizer());
		controller.buildView();

		gcDelegate.setController(controller);
	}

	public GinaReplayManager getManager() {
		return manager;
	}
}

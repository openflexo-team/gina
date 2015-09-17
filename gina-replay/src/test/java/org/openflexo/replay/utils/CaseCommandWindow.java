package org.openflexo.replay.utils;

import org.openflexo.connie.DataBinding;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.replay.sampleData.Person;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.replay.GinaReplay;
import org.openflexo.replay.GinaReplayManager;

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
		GinaReplay recorder = new GinaReplay(manager);
		manager.setCurrentReplayer(recorder);
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
		controller = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
		controller.buildView();

		gcDelegate.setController(controller);
	}
	
	public GinaReplayManager getManager() {
		return manager;
	}
}

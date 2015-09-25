package org.openflexo.replay.utils;

import java.awt.Dimension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openflexo.gina.event.description.FIBEventDescription;
import org.openflexo.gina.event.description.TreeNodeEventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.test.ReplayTestConfiguration;

public abstract class Case {

	static private Case instance;
	static private CaseCommandWindow commandWindow;
	static private GinaReplayManager manager;
	static private ExecutorService executor;
	
	static private ReplayTestConfiguration testConfiguration;

	static public void initCase(Case c) {
		instance = c;

		manager = new GinaReplayManager();
		manager.addEventDescriptionModels(FIBEventDescription.class, TreeNodeEventDescription.class);
		GinaReplaySession recorder = new GinaReplaySession(manager);
		manager.setCurrentReplayer(recorder);
		
		if (testConfiguration == null)
			recorder.startRecording();
		else
			testConfiguration.startup(manager);

		commandWindow = new CaseCommandWindow(manager);
		
		c.start();
	}
	
	static public void ginaReplayStartupHook(ReplayTestConfiguration config) {
		testConfiguration = config;
	}
	
	static public void initExecutor(int threadNumber) {
		executor = Executors.newFixedThreadPool(threadNumber);
	}

	static public GinaReplayManager getManager() {
		return manager;
	}
	
	static public EventManager getEventManager() {
		return manager.getEventManager();
	}

	static public ExecutorService getTaskExecutor() {
		return executor;
	}

	static public Case getInstance() {
		return instance;
	}
	
	public abstract void initWindow(Window w);

	public abstract void start();

	public abstract Dimension getWindowSize();

}

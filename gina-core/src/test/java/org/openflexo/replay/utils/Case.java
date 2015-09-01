package org.openflexo.replay.utils;

import java.awt.Dimension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openflexo.gina.event.description.FIBEventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.GinaReplayManager;
import org.openflexo.replay.GinaReplay;

public abstract class Case {

	static private Case instance;
	static private CaseCommandWindow commandWindow;
	static private GinaReplayManager manager;
	static private ExecutorService executor;

	static public void initCase(Case c) {
		instance = c;

		manager = new GinaReplayManager();
		manager.addEventDescriptionModels(FIBEventDescription.class);
		GinaReplay recorder = new GinaReplay(manager);
		manager.setCurrentReplayer(recorder);
		recorder.startRecording();

		commandWindow = new CaseCommandWindow(manager);
		
		c.start();
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

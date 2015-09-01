package org.openflexo.replay.utils;

import java.awt.Dimension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openflexo.gina.manager.GinaManager;
import org.openflexo.replay.GinaReplay;

public abstract class Case {

	static private Case instance;
	static private CaseCommandWindow commandWindow;
	static private GinaManager manager;
	static private ExecutorService executor;

	static public void initCase(Case c) {
		instance = c;

		manager = new GinaManager();
		GinaReplay recorder = new GinaReplay(manager);
		manager.setCurrentReplayer(recorder);
		recorder.startRecording();

		commandWindow = new CaseCommandWindow(manager);
		
		c.start();
	}
	
	static public void initExecutor(int threadNumber) {
		executor = Executors.newFixedThreadPool(threadNumber);
	}

	static public GinaManager getManager() {
		return manager;
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

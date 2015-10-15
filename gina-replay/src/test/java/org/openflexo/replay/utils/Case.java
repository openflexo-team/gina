package org.openflexo.replay.utils;

import java.awt.Dimension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.description.ApplicationEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBEventDescription;
import org.openflexo.gina.event.description.TreeNodeEventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.replay.Scenario;
import org.openflexo.replay.ScenarioNode;
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
		manager.setCurrentSession(recorder);
		
		assertNotNull(manager);
		assertNotNull(recorder);
		assertEquals(manager.getCurrentSession(), recorder);
		
		recorder.start(testConfiguration);
		if (testConfiguration == null) {
			assertRecordedInteraction(manager, 0, ApplicationEventDescription.class);
		}

		commandWindow = new CaseCommandWindow(manager);
		
		c.start();
	}
	
	static public void assertRecordedInteraction(GinaReplayManager manager, int index,
			Class<? extends EventDescription> cls) {
		assertRecordedItem(manager, true, index, cls);
	}
	
	static public void assertRecordedEvent(GinaReplayManager manager, int index,
			Class<? extends EventDescription> cls) {
		assertRecordedItem(manager, false, index, cls);
	}
	
	static public void assertRecordedItem(GinaReplayManager manager, boolean userInteraction,
			int index, Class<? extends EventDescription> cls) {
		Scenario s = manager.getCurrentSession().getScenario();
		assertEquals(s.size(), index + 1);
		ScenarioNode node = s.getNodes().get(index);

		assert(node instanceof InteractionCycle);
		EventDescription d;
		if (userInteraction) {
			UserInteraction ui = ((InteractionCycle) node).getUserInteraction();
			assertNotNull(ui);
	
			d = ui.getDescription();
			// todo
			assert(d.getClass().isAssignableFrom(cls));
		}
		else {
			/*UserInteraction ui = ((InteractionCycle) node).getNonUserInteractionsByKind("");
			assertNotNull(ui);
	
			d = ui.getDescription();*/
		}
		//assert(d.getClass().isAssignableFrom(cls));
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

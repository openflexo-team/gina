package org.openflexo.replay.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.description.ApplicationEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBEventDescription;
//import org.openflexo.gina.event.description.TreeNodeEventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.replay.Scenario;
import org.openflexo.replay.ScenarioNode;

/**
 * This class represents a basic test Case used to test FIB recording, replaying and checking. This class should be inherited in order to
 * specify the behavior of the test.
 * 
 * Examples of tests are available in org.openflexo.replay.cases
 * 
 * This class, when in record mod (by default), will create a scenarri/last-scenario file in the resources folder. You can use the
 * CreateTestFromScenario tool to generate a JUnit test from it.
 * 
 * @author Alexandre
 *
 */
public abstract class Case {

	static private Case instance;
	static private CaseCommandWindow commandWindow;
	static private GinaReplayManager manager;
	static private ExecutorService executor;

	static public void initCase(Case c) {
		instance = c;

		// we create a GinaReplayManager that we manage the whole record/replay
		manager = new GinaReplayManager();
		// we add the FIBEventDescription (and children) model to the factory
		manager.addEventDescriptionModels(FIBEventDescription.class);
		// we could've added TreeNodeEventDescription.class to manage Diana Events

		// we create and select a replay session
		GinaReplaySession recorder = new GinaReplaySession(manager);
		manager.setCurrentSession(recorder);

		assertNotNull("manager null", manager);
		assertNotNull("recorder null", recorder);
		assertEquals("manager and recorder are not correctly bound", manager.getCurrentSession(), recorder);

		// start and check the recording of the application started event
		recorder.start();
		if (recorder.isRecording()) {
			assertRecordedInteraction(manager, 0, ApplicationEventDescription.class);
		}

		// init the command window for the controller (e.g. with the save scenario button)
		commandWindow = new CaseCommandWindow(manager);

		c.start();
	}

	static public void assertRecordedInteraction(GinaReplayManager manager, int index, Class<? extends EventDescription> cls) {
		assertRecordedItem(manager, true, index, cls);
	}

	static public void assertRecordedEvent(GinaReplayManager manager, int index, Class<? extends EventDescription> cls) {
		assertRecordedItem(manager, false, index, cls);
	}

	static public void assertRecordedItem(GinaReplayManager manager, boolean userInteraction, int index,
			Class<? extends EventDescription> cls) {
		Scenario s = manager.getCurrentSession().getScenario();
		assertEquals("1", s.size(), index + 1);
		ScenarioNode node = s.getNodes().get(index);

		assertTrue("2", node instanceof InteractionCycle);
		EventDescription d;
		if (userInteraction) {
			UserInteraction ui = ((InteractionCycle) node).getUserInteraction();
			assertNotNull("3", ui);

			d = ui.getDescription();
			// TODO the following test is false
			System.out.println(d.getClass() + " IS ASS TO" + cls);
			// assertTrue("4", d.getClass().isAssignableFrom(cls));
		}
		else {
			/*UserInteraction ui = ((InteractionCycle) node).getNonUserInteractionsByKind("");
			assertNotNull(ui);
			
			d = ui.getDescription();*/
		}
		// assert(d.getClass().isAssignableFrom(cls));
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

package org.openflexo.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.description.ApplicationEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.strategies.CheckingStrategy;
import org.openflexo.gina.event.strategies.RecordingStrategy;
import org.openflexo.gina.manager.GinaEventListener;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.replay.test.ReplayTestConfiguration;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

/**
 * 
 * 
 * @author Alexandre
 */
public class GinaReplaySession implements GinaEventListener {
	private static final Logger LOGGER = Logger.getLogger(GinaReplaySession.class.getPackage().getName());

	private Scenario scenario;
	private boolean recording, wasRecording;
	private int currentEventIndex;
	private int delayBetweenNodes, delayWaitSync;
	private GinaReplayManager manager;

	private RecordingStrategy recordingStrategy;
	private CheckingStrategy checkingStrategy;

	public GinaReplaySession(GinaReplayManager manager) {
		this.delayBetweenNodes = 30;
		this.delayWaitSync = 2000;

		this.recording = false;
		this.wasRecording = recording;

		this.manager = manager;

		scenario = manager.getModelFactory().newInstance(Scenario.class);

		/*InteractionCycle initNode = this.manager.getModelFactory().newInstance(InteractionCycle.class);
		scenario.addNode(initNode);*/

		// strategies
		this.recordingStrategy = new RecordingStrategy(this);
		this.checkingStrategy = new CheckingStrategy(this);

		LOGGER.info("REC Gina Recorder is Up");
	}

	/**
	 * 
	 * @param e
	 */
	@Override
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		if (this.recordingStrategy != null && isRecording()) {
			this.recordingStrategy.eventPerformed(e, stack);
			// System.out.println("Number of recorded events : " + rootNode.getNodes().size());
			File scenarioDir = ((FileResourceImpl) ResourceLocator.locateSourceCodeResource("scenarii")).getFile();
			save(scenarioDir, "last-scenario");
		}
		if (this.checkingStrategy != null && !isRecording()) {
			this.checkingStrategy.eventPerformed(e, stack);
		}
	}

	/**
	 * Get the root GinaRecordedNode
	 * 
	 * @return GinaRecordedNode root node
	 */
	public Scenario getScenario() {
		return scenario;
	}

	public void launched() {
		if (scenario.size() == 0)
			return;
		if (!(scenario.getNodes().get(0) instanceof InteractionCycle))
			return;
		UserInteraction ui = ((InteractionCycle) scenario.getNodes().get(0)).getUserInteraction();
		if (!(ui.getDescription() instanceof ApplicationEventDescription))
			return;
		executeEvent(ui);
	}

	public void play() {
		pauseRecordingIfRunning();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				for (ScenarioNode node : scenario.getNodes()) {
					if (node instanceof InteractionCycle) {
						InteractionCycle ic = (InteractionCycle) node;
						executeNodeEvent(ic);
						waitForSystemEventsSync(ic, delayWaitSync);
					}
				}

				resumeRecordingIfRunningBefore();
			}
		});
		t.start();
	}

	protected boolean executeNodeEvent(InteractionCycle node) {
		// System.out.println("Event " + node);
		final UserInteraction e = node.getUserInteraction();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				executeEvent(e);
			}
		});

		if (delayBetweenNodes > 0)
			try {
				Thread.sleep(delayBetweenNodes);
			} catch (InterruptedException e2) {
			}

		return true;
	}

	protected boolean executeEvent(UserInteraction e) {
		int retryNumber = 10;
		final EventDescription d = e.getDescription();

		LOGGER.info("PLAY : Event " + d);
		if (this.checkingStrategy != null) {
			this.checkingStrategy.eventPlayed(e);
		}

		for (int retry = retryNumber; retry > 0; retry--) {
			try {
				d.execute(manager.getEventManager());

				if (retry != retryNumber)
					LOGGER.warning("PLAY : had to retry " + (retryNumber - retry) + " time(s) to perfom User Interaction " + d);

				return true;
			} catch (Exception e1) {
				try {
					e1.printStackTrace();
					Thread.sleep(50);
				} catch (InterruptedException e2) {
				}
			}
		}

		LOGGER.warning("PLAY : Can't perform " + d);
		return false;
	}

	public int playNextStep() throws InvalidRecorderStateException {
		return checkNextStep(false);
	}

	public void checkSystemEvents(InteractionCycle node) throws InvalidRecorderStateException {
		if (checkingStrategy != null)
			checkingStrategy.checkSystemEvents(node);
	}

	public int checkNextStep(boolean checkSystemEvents) throws InvalidRecorderStateException {
		if (currentEventIndex >= scenario.getNodes().size())
			return currentEventIndex;

		pauseRecordingIfRunning();

		ScenarioNode node = scenario.getNodes().get(currentEventIndex++);
		if (node instanceof InteractionCycle) {
			InteractionCycle ic = (InteractionCycle) node;

			executeNodeEvent(ic);

			if (checkSystemEvents && !waitForSystemEventsSync(ic, delayWaitSync)) {
				checkSystemEvents(ic);
			}

			resumeRecordingIfRunningBefore();
		}

		return currentEventIndex;
	}

	protected boolean waitForSystemEventsSync(InteractionCycle node, int duration) {
		int step = 500;
		for (int time = duration; time > 0; time -= step) {
			try {
				Thread.sleep(step);
			} catch (InterruptedException e) {
			}

			try {
				checkSystemEvents(node);
				// LOGGER.info("PLAY : State sync ok");

				if (time != duration)
					LOGGER.warning("PLAY : needed multiple attempts to check non user interactions for node " + node);
				return true;
			} catch (InvalidRecorderStateException e) {
			}
		}

		try {
			Thread.sleep(step);
		} catch (InterruptedException e) {
		}

		LOGGER.warning("PLAY : Non User Interaction error");
		return false;
	}

	public boolean isRecording() {
		return recording;
	}

	public void pauseRecording() {
		this.recording = false;
	}

	public void resumeRecording() {
		this.recording = true;
	}

	public void startRecording() {
		start(null);
	}

	public void start(ReplayTestConfiguration testConfiguration) {
		if (testConfiguration == null) {
			this.resumeRecording();
		}
		else {
			testConfiguration.startup(manager);
		}

		if (testConfiguration != null || scenario.size() == 0) {
			// register the beginning of the application
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			StackTraceElement main = stack[stack.length - 1];

			ApplicationEventDescription d = manager.getEventManager().getFactory()
					.createApplicationEvent(ApplicationEventDescription.STARTED, main.getClassName());
			GinaStackEvent gse = manager.getEventManager().pushStackEvent(d, KIND.USER_INTERACTION);
			gse.end();

			eventPerformed(gse.getEvent(), manager.getEventManager().getEventStack());
		}
	}

	protected void pauseRecordingIfRunning() {
		this.wasRecording = this.recording;
		this.recording = false;
	}

	protected void resumeRecordingIfRunningBefore() {
		if (this.wasRecording)
			this.recording = true;
	}

	public boolean save(File dir, String filename) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(dir, filename));

			manager.getModelFactory().serialize(scenario, out);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
		return false;
	}

	public boolean load(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);

			scenario = (Scenario) manager.getModelFactory().deserialize(in);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return false;
	}

	public GinaReplayManager getManager() {
		return manager;
	}

	public GinaEvent getEventOrigin(GinaEvent e, Stack<GinaStackEvent> stack) {
		GinaEvent origin = null;

		if (stack.size() > 1) {
			origin = stack.get(stack.size() - 2).getEvent();
		}

		// change state list
		if (e.getKind() == KIND.USER_INTERACTION) {
			// System.out.println(" User Interaction : " + e);
			// lastNonUserInteractions.clear();
		}
		else {
			// System.out.println("Non User Interaction : " + e);
			// lastNonUserInteractions.add(e);

			/*Stack<GinaStackEvent> stack = manager.getEventStack();
			for(int i = stack.size() - 1; i >= 0; --i) {
				GinaStackEvent ev = stack.get(i);
				if (ev.getEvent().isUserInteraction()) {
					origin = ev.getEvent();
					break;
				}
				//System.out.println(ev.toString());
			}*/

			/*if (origin != null)
				System.out.println("Origin : " + origin);*/
		}

		/*if (origin != null)
			System.out.println("        Stack #" + (stack.size() - 1) + " - Origin : " + origin);*/

		return origin;
	}

	public GinaEvent getEventUserOrigin(GinaEvent e, Stack<GinaStackEvent> stack) {
		GinaEvent origin = getEventOrigin(e, stack);
		GinaEvent userOrigin = null;

		if (stack.size() > 1) {
			userOrigin = stack.firstElement().getEvent();
			/*if (userOrigin.getKind() != KIND.USER_INTERACTION)
				userOrigin = ((InteractionCycle)scenario.getNodes().get(0)).getUserInteraction();*/
		}

		// change state list
		if (e.getKind() == KIND.USER_INTERACTION) {
			// System.out.println(" User Interaction : " + e);
			// lastNonUserInteractions.clear();
		}
		else {
			// System.out.println("Non User Interaction : " + e);
			// lastNonUserInteractions.add(e);

			/*Stack<GinaStackEvent> stack = manager.getEventStack();
			for(int i = stack.size() - 1; i >= 0; --i) {
				GinaStackEvent ev = stack.get(i);
				if (ev.getEvent().isUserInteraction()) {
					origin = ev.getEvent();
					break;
				}
				//System.out.println(ev.toString());
			}*/

			/*if (origin != null)
				System.out.println("Origin : " + origin);*/
		}

		/*if (userOrigin != null && userOrigin != origin)
			System.out.println("        Stack #1 - User Origin : " + userOrigin);*/

		return userOrigin;
	}
}

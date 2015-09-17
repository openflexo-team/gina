package org.openflexo.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.description.ApplicationEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.manager.GinaEventListener;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.event.strategies.RecordingStrategy;
import org.openflexo.gina.event.strategies.CheckingStrategy;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

/**
 * 
 * 
 * @author Alexandre
 */
public class GinaReplay implements GinaEventListener {
	private static final Logger LOGGER = Logger.getLogger(GinaReplay.class.getPackage().getName());

	private Scenario scenario;
	private boolean recording, wasRecording;
	private int currentEventIndex;
	private int delayBetweenNodes, delayWaitSync;
	private GinaReplayManager manager;
	
	private RecordingStrategy recordingStrategy;
	private CheckingStrategy checkingStrategy;
	
	private LinkedList<GinaEvent> lastNonUserInteractions;

	public GinaReplay(GinaReplayManager manager) {
		this.delayBetweenNodes = 500;
		this.delayWaitSync = 2000;

		this.recording = false;
		this.wasRecording = recording;
		
		this.manager = manager;
		
		this.lastNonUserInteractions = new LinkedList<GinaEvent>();
		
		scenario = manager.getModelFactory().newInstance(Scenario.class);

		/*InteractionCycle initNode = this.manager.getModelFactory().newInstance(InteractionCycle.class);
		scenario.addNode(initNode);*/
		
		// strategies
		this.recordingStrategy = new RecordingStrategy(scenario, this.manager);//null;
		this.checkingStrategy = null;
		
		LOGGER.info("REC Gina Recorder is Up");
	}

	/**
	 * 
	 * @param e
	 */
	@Override
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		if (!isRecording())
			return;

		if (this.recordingStrategy != null)
			this.recordingStrategy.eventPerformed(e, stack);

		//System.out.println("Number of recorded events : " + rootNode.getNodes().size());
		File scenarioDir = ((FileResourceImpl) ResourceLocator.locateSourceCodeResource("scenarii")).getFile();
		System.out.println(scenarioDir);
		save(scenarioDir, "last-scenario");
	}
	
	/**
	 * Get the root GinaRecordedNode
	 * @return GinaRecordedNode root node
	 */
	public Scenario getScenario() {
		return scenario;
	}

	public void play() {
		pauseRecordingIfRunning();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				for(ScenarioNode node : scenario.getNodes()) {
					if (node instanceof InteractionCycle) {
						InteractionCycle ic = (InteractionCycle) node;
						executeNodeEvents(ic);
						waitForNonUserInteractionsSync(ic, delayWaitSync);
					}
				}

				resumeRecordingIfRunningBefore();
			}
		});
		t.start();
	}
	
	protected boolean executeNodeEvents(InteractionCycle node) {
		//System.out.println("Event " + node);
		final GinaEvent e = node.getUserInteraction();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				executeEvent(e.getDescription());
			}
		});
		
		if (delayBetweenNodes > 0)
			try {
				Thread.sleep(delayBetweenNodes);
			} catch (InterruptedException e2) {}
		
		return true;
	}
	
	protected boolean executeEvent(final EventDescription e) {
		LOGGER.info("PLAY : Event " + e);
		
		int retryNumber = 10;
		
		for(int retry = retryNumber; retry > 0; retry--) {
			try{
				e.execute(manager.getEventManager());
				
				if (retry != retryNumber)
					LOGGER.warning("PLAY : had to retry " + (retryNumber - retry) + " time(s) to perfom User Interaction " + e);
				
				return true;
			} catch(Exception e1) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e2) {}
			}
		}

		LOGGER.warning("PLAY : Can't perform " + e);
		return false;
	}

	public int playNextStep() throws InvalidRecorderStateException {
		return checkNextStep(false);
	}

	public int checkNextStep(boolean checkNonUserInteractions) throws InvalidRecorderStateException {
		if (currentEventIndex >= scenario.getNodes().size())
			return currentEventIndex;

		pauseRecordingIfRunning();

		ScenarioNode node = scenario.getNodes().get(currentEventIndex++);
		if (node instanceof InteractionCycle) {
			InteractionCycle ic = (InteractionCycle) node;
	
			executeNodeEvents(ic);
	
			if (checkNonUserInteractions) {
				if (!waitForNonUserInteractionsSync(ic, delayWaitSync)) {
					checkNonUserInteractions(ic, lastNonUserInteractions);
				}
			}
			
			lastNonUserInteractions.clear();
	
			resumeRecordingIfRunningBefore();
		}

		return currentEventIndex;
	}

	protected void checkNonUserInteractions(InteractionCycle node, LinkedList<GinaEvent> nonUserInteractions) throws InvalidRecorderStateException {
		List<GinaEvent> l = new LinkedList<GinaEvent>();
		//l.addAll(node.getSystemEventTree());
		l.add(node.getUserInteraction());
		
		System.out.println("1 : " + nonUserInteractions);
		System.out.println("2 : " + l);
		
		for(GinaEvent e : l) {
			GinaEvent matching = findMatchingEvent(nonUserInteractions, e);

			if (matching == null) {
				throw new InvalidRecorderStateException("No matching state"/*, node*/, e);
			}
			
			//e.checkMatchingEvent(matching);
		}
	}
	
	protected boolean waitForNonUserInteractionsSync(InteractionCycle node, int duration) {
		int step = 500;
		for(int time = duration; time > 0; time -= step) {
			try {
				Thread.sleep(step);
			} catch (InterruptedException e) {}
			
			try {
				checkNonUserInteractions(node, lastNonUserInteractions);
				//LOGGER.info("PLAY : State sync ok");
				
				if (time != duration)
					LOGGER.warning("PLAY : needed multiple attempts to check non user interactions for node " + node);
				return true;
			} catch (InvalidRecorderStateException e) {
			}
		}
		
		try {
			Thread.sleep(step);
		} catch (InterruptedException e) {}

		LOGGER.warning("PLAY : Non User Interaction error");
		return false;
	}
	
	protected GinaEvent findMatchingEvent(LinkedList<GinaEvent> events, GinaEvent target) {
		for(GinaEvent e : events) {
			if (e.matchesIdentity(target))
				return e;
		}
		
		return null;
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
		this.resumeRecording();

		if (scenario.size() == 0) {
			// register the beginning of the application
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			StackTraceElement main = stack[stack.length - 1];

			ApplicationEventDescription d = manager.getEventManager().getFactory().createApplicationEvent(ApplicationEventDescription.STARTED, main.getClassName());
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
}

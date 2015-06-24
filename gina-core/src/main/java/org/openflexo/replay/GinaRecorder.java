package org.openflexo.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.openflexo.fib.listener.FIBActionListener;
import org.openflexo.fib.listener.FIBActionListenerManager;
import org.openflexo.fib.listener.GinaHandler;
import org.openflexo.fib.listener.GinaStackEvent;
import org.openflexo.gina.event.FIBEvent;
import org.openflexo.gina.event.GinaEvent;

public class GinaRecorder implements FIBActionListener {
	private static final Logger LOGGER = Logger.getLogger(GinaRecorder.class.getPackage().getName());

	private GinaRecordedNode rootNode;
	private boolean recording, wasRecording;
	private int currentEventIndex;
	
	private LinkedList<GinaEvent> lastStates;

	public GinaRecorder() {
		recording = false;
		wasRecording = recording;
		rootNode = GinaRecorderManager.getInstance().getFactory().newInstance(GinaRecordedNode.class);

		lastStates = new LinkedList<GinaEvent>();

		GinaRecordedNode initNode = GinaRecorderManager.getInstance().getFactory().newInstance(GinaRecordedNode.class);
		rootNode.addNode(initNode);

		FIBActionListenerManager.getInstance().addListenerAndEnable(this);
		
		LOGGER.info("REC Gina Recorder is Up");
	}
	
	@Override
	public void actionPerformed(FIBEvent e) {
		actionPerformed((GinaEvent) e);
	}

	/**
	 * 
	 * @param e
	 */
	public void actionPerformed(GinaEvent e) {
		//ignore non recorded events
		if (!isRecording())
			return;
		
		GinaEvent origin = null;
		
		// change state list
		if (e.isFromUserOrigin()) {
			lastStates.clear();
			System.out.println("Event USER : " + e);
		}
		else {
			lastStates.add(e);
			System.out.println("Event STATE : " + e);

			Stack<GinaStackEvent> stack = GinaHandler.getInstance().getEventStack();
			for(int i = stack.size() - 1; i >= 0; --i) {
				GinaStackEvent ev = stack.get(i);
				if (ev.getEvent().isFromUserOrigin()) {
					origin = ev.getEvent();
					break;
				}
				//System.out.println(ev.toString());
			}

			if (origin != null)
				System.out.println("Origin : " + origin);
		}

		//TODO
		//if (e.getWidgetID() == "playButton")
		//	return;
		
		// add as event or state depending of its user origin
		if (e.isFromUserOrigin()) {
			GinaRecordedNode node = GinaRecorderManager.getInstance().getFactory().newInstance(GinaRecordedNode.class);
			node.addEvent(e);

			//System.out.println(e);
			rootNode.addNode(node);
		}
		else {
			//System.out.println("State updated : " + e);
			rootNode.getNodes().get(rootNode.getNodes().size() - 1).addState(e);
		}
		
		//System.out.println("Number of recorded events : " + rootNode.getNodes().size());
		save(new File("D:/test-gina-recorder-temp"));
	}
	
	/**
	 * Get the root GinaRecordedNode
	 * @return GinaRecordedNode root node
	 */
	public GinaRecordedNode getRootNode() {
		return rootNode;
	}

	public void play() {
		pauseRecordingIfRunning();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				for(GinaRecordedNode node : rootNode.getNodes()) {
					executeNodeEvents(node);
					waitForStateSync(node, 10000);
				}

				resumeRecordingIfRunningBefore();
			}
		});
		t.start();
	}
	
	protected boolean waitForStateSync(GinaRecordedNode node, int duration) {
		int step = 1000;
		for(int time = duration; time > 0; time -= step) {
			try {
				Thread.sleep(step);
			} catch (InterruptedException e) {}
			
			try {
				checkStates(node, lastStates);
				//LOGGER.info("PLAY : State sync ok");
				return true;
			} catch (InvalidRecorderStateException e) {
			}
		}
		
		try {
			Thread.sleep(step);
		} catch (InterruptedException e) {}

		LOGGER.warning("PLAY : State error");
		return false;
	}

	public int playNextStep() throws InvalidRecorderStateException {
		return checkNextStep(false);
	}

	public int checkNextStep(boolean checkStates) throws InvalidRecorderStateException {
		if (currentEventIndex >= rootNode.getNodes().size())
			return currentEventIndex;

		pauseRecordingIfRunning();

		GinaRecordedNode node = rootNode.getNodes().get(currentEventIndex++);

		executeNodeEvents(node);

		/*try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/

		if (checkStates) {
			if (!waitForStateSync(node, 2000)) {
				checkStates(node, lastStates);
			}
			/*boolean retry = false;
			try {
				this.checkStates(node, lastStates);
			} catch (FIBInvalidStateException e) {
				retry = true;
			}

			if (retry) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				this.checkStates(node, lastStates);
			}*/
		}

		resumeRecordingIfRunningBefore();

		return currentEventIndex;
	}

	protected void checkStates(GinaRecordedNode node, LinkedList<GinaEvent> states) throws InvalidRecorderStateException {
		//System.out.println(states);
		//System.out.println(node.getStates());
		
		for(GinaEvent e : node.getStates()) {
			GinaEvent matching = findMatchingEvent(states, e);

			if (matching == null)
				throw new InvalidRecorderStateException("No matching state", node, e);
			
			e.matchesState(matching);
		}
	}
	
	protected GinaEvent findMatchingEvent(LinkedList<GinaEvent> events, GinaEvent target) {
		for(GinaEvent e : events) {
			if (e.isMatchingIdentity(target))
				return e;
		}
		
		return null;
	}
	
	protected boolean executeNodeEvents(GinaRecordedNode node) {
		//System.out.println("Event " + node);
		for(GinaEvent e : node.getEvents()) {
			if (!executeEvent(e))
				return false;
		}
		
		return true;
	}
	
	protected boolean executeEvent(GinaEvent e) {
		LOGGER.info("PLAY : Event " + e);
		
		for(int retry = 10; retry > 0; retry--) {
			try{
				/*FIBWidgetView<?, ?, ?> wv = FIBHandler.getInstance().findWidgetViewChildByID(e.getComponent(), e.getWidgetID());
				if (wv == null)
					throw new NullPointerException();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {}
				wv.executeEvent(e);*/
				e.execute();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {}
				return true;
			} catch(Exception e1) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e2) {}
			}
		}
		
		LOGGER.warning("PLAY : Can't perform " + e);
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
		this.resumeRecording();
	}
	
	protected void pauseRecordingIfRunning() {
		this.wasRecording = this.recording;
		this.recording = false;
	}
	
	protected void resumeRecordingIfRunningBefore() {
		if (this.wasRecording)
			this.recording = true;
	}
	
	public boolean save(File file) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);

			GinaRecorderManager.getInstance().getFactory().serialize(rootNode, out);

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

			rootNode = (GinaRecordedNode) GinaRecorderManager.getInstance().getFactory().deserialize(in);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return false;
	}
}

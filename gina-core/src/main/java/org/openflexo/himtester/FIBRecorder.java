package org.openflexo.himtester;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.widget.FIBButtonWidget;
import org.openflexo.fib.view.widget.FIBTextFieldWidget;
import org.openflexo.himtester.events.FIBActionEvent;
import org.openflexo.himtester.events.FIBTextEvent;
import org.openflexo.himtester.listener.FIBActionListener;
import org.openflexo.himtester.listener.FIBActionListenerManager;
import org.openflexo.himtester.listener.FIBHandler;

public class FIBRecorder implements FIBActionListener {
	private FIBRecordedNode rootNode;
	private boolean recording, wasRecording;
	private int currentEventIndex;
	
	private LinkedList<FIBActionEvent> lastStates;

	public FIBRecorder() {
		recording = false;
		wasRecording = recording;
		rootNode = FIBRecorderManager.getInstance().getFactory().newInstance(FIBRecordedNode.class);
		
		lastStates = new LinkedList<FIBActionEvent>();
		
		FIBRecordedNode initNode = FIBRecorderManager.getInstance().getFactory().newInstance(FIBRecordedNode.class);
		rootNode.addNode(initNode);
		
		FIBActionListenerManager.getInstance().addListenerAndEnable(this);
	}

	public void actionPerformed(FIBActionEvent e) {
		if (e.isFromUserOrigin()) {
			lastStates.clear();
			//System.out.println("!RESET " + e);
		}
		else {
			lastStates.add(e);
			//System.out.println("!> " + e);
		}
		
		if (!isRecording())
			return;

		if (e.getWidgetID() == "playButton") {
			System.out.println("ignored");
			return;
		}
		
		/*HIMRecordedEvent re = HIMRecorderManager.getInstance().getFactory().newInstance(HIMRecordedEvent.class);
		re.constructFromActionEvent(e);*/
		
		if (e.isFromUserOrigin()) {
			FIBRecordedNode node = FIBRecorderManager.getInstance().getFactory().newInstance(FIBRecordedNode.class);
			node.addEvent(e);

			//System.out.println(e);
			rootNode.addNode(node);
		}
		else {
			//System.out.println("State updated : " + e);
			rootNode.getNodes().get(rootNode.getNodes().size() - 1).addState(e);
		}
		
		System.out.println("Number of recorded events : " + rootNode.getNodes().size());
		save(new File("D:/test-gina-recorder-temp"));
	}
	
	public FIBRecordedNode getRootNode() {
		return rootNode;
	}

	public void play() {
		pauseRecordingIfRunning();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				for(FIBRecordedNode node : rootNode.getNodes()) {
					executeNodeEvents(node);
					
					try {
						Thread.sleep(150);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				resumeRecordingIfRunningBefore();
			}
		});
		t.start();
	}
	
	public int playNextStep() throws FIBInvalidStateException {
		return checkNextStep(false);
	}
	
	public int checkNextStep(boolean checkStates) throws FIBInvalidStateException {
		if (currentEventIndex >= rootNode.getNodes().size())
			return currentEventIndex;

		pauseRecordingIfRunning();
		
		FIBRecordedNode node = rootNode.getNodes().get(currentEventIndex++);
		
		executeNodeEvents(node);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (checkStates) {
			boolean retry = false;
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
			}
		}
		
		resumeRecordingIfRunningBefore();
		
		return currentEventIndex;
	}
	
	protected void checkStates(FIBRecordedNode node, LinkedList<FIBActionEvent> states) throws FIBInvalidStateException {
		//System.out.println(states);
		//System.out.println(node.getStates());
		
		for(FIBActionEvent e : node.getStates()) {
			FIBActionEvent matching = findMatchingEvent(states, e);
			FIBWidgetView<?, ?, ?> wv = FIBHandler.getInstance().findWidgetViewChildByID(e.getComponent(), e.getWidgetID());

			if (matching == null)
				throw new FIBInvalidStateException("No matching state", node, e);

			switch(e.getAction()) {
			case "text-inserted":
			case "test-removed":
				FIBTextFieldWidget wb1 = (FIBTextFieldWidget) wv;
				if (!wb1.getValue().equals(e.getAbsoluteValue()))
					throw new FIBInvalidStateException("Value is not identical : "
				+ wb1.getValue() + " != " + e.getAbsoluteValue(), node, e);
				break;
			}
		}
	}
	
	protected FIBActionEvent findMatchingEvent(LinkedList<FIBActionEvent> events, FIBActionEvent target) {
		for(FIBActionEvent e : events) {
			//System.out.println(e + " ??? " + target + "   ===== " + e.matchIdentity(target));
			if (e.matchIdentity(target))
				return e;
		}
		
		return null;
	}
	
	protected void executeNodeEvents(FIBRecordedNode node) {
		for(FIBActionEvent e : node.getEvents()) {
			executeEvent(e);
		}
	}
	
	protected void executeEvent(FIBActionEvent e) {
		System.out.println("Event " + e);
		
		FIBWidgetView<?, ?, ?> wv = FIBHandler.getInstance().findWidgetViewChildByID(e.getComponent(), e.getWidgetID());
		if (wv != null) {
			//System.out.println(e.getWidgetClass());
			
			switch(e.getWidgetClass()) {
			case "Button":
				FIBButtonWidget wb = (FIBButtonWidget) wv;

				wb.buttonClicked();

				break;
			case "TextField":
				FIBTextFieldWidget wb1 = (FIBTextFieldWidget) wv;

				wb1.execute(e);

				break;
			}
		}
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

			FIBRecorderManager.getInstance().getFactory().serialize(rootNode, out);

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

			rootNode = (FIBRecordedNode) FIBRecorderManager.getInstance().getFactory().deserialize(in);
			
			//System.out.println(rootNode.getNodes().size());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return false;
	}
}

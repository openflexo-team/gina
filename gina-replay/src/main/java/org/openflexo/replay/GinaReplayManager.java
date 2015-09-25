package org.openflexo.replay;

import java.util.Stack;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.GinaEventFactory;
import org.openflexo.gina.manager.GinaEventListener;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.model.factory.ModelFactory;

/**
 * This class manages manages :
 *  - all the registered items
 *  - the current stack event
 *  - the current replayer
 *  
 *  It should be unique for a set of registered items.
 * 
 * @author Alexandre
 *
 */
public class GinaReplayManager implements GinaEventListener {

	private GinaReplaySession replayer;
	private GinaEventFactory factory;
	private EventManager eventManager;
	
	public GinaReplayManager(EventManager eventManager) {
		if (eventManager == null)
			this.eventManager = new EventManager();
		else
			this.eventManager = eventManager;
		this.eventManager.addListener(this);
		
		factory = new GinaEventFactory();
		factory.addModel(Scenario.class);
		factory.addModel(EventDescription.class);
		factory.addModel(GinaEvent.class);
	}

	public GinaReplayManager() {
		this(null);
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Instantiate a GinaRecorder and set it as the current recorder
	 * @return the newly created recorder
	 */
	public GinaReplaySession createAndSetToCurrent() {
		GinaReplaySession r = new GinaReplaySession(this);

		setCurrentReplayer(r);

		return r;
	}

	/**
	 * Set the current recorder
	 * @param r the recorder instance
	 */
	public void setCurrentReplayer(GinaReplaySession r) {
		replayer = r;
	}

	/**
	 * Return the current recorder
	 * @return the current recorder
	 */
	public GinaReplaySession getCurrentReplayer() {
		return replayer;
	}

	/**
	 * Get the Pamela model factory for the GinaRecordedNode and the GinaEvent
	 * @return the model factory
	 */
	public GinaEventFactory getFactory() {
		return factory;
	}
	
	public ModelFactory getModelFactory() {
		return getFactory().getModelFactory();
	}

	public void addEventDescriptionModels(@SuppressWarnings("unchecked") Class<? extends EventDescription> ... clss) {
		for(Class<? extends EventDescription> cls : clss)
			factory.addModel(cls);
	}

	public void setup() {
		if (replayer == null) {
			createAndSetToCurrent();

			/*recorder.load(new File("D:/test-gina-recorder"));
			recorder.play();*/

			replayer.startRecording();

			//GinaRecorderEditor editor = new GinaRecorderEditor();
		}
	}
	
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		if (getCurrentReplayer() != null)
			getCurrentReplayer().eventPerformed(e, stack);
	}

}

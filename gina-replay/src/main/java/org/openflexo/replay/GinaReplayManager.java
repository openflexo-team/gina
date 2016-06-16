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
 *  - the current GinaReplaySession recorded / replayer
 *  
 *  It should be unique for a set of registered items. (only 1 Manager for 1 managed Widget)
 * 
 * @author Alexandre
 *
 */
public class GinaReplayManager implements GinaEventListener {

	private GinaReplaySession session;
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

		setCurrentSession(r);

		return r;
	}

	/**
	 * Set the current recorder
	 * @param r the recorder instance
	 */
	public void setCurrentSession(GinaReplaySession r) {
		session = r;
	}

	/**
	 * Return the current recorder
	 * @return the current recorder
	 */
	public GinaReplaySession getCurrentSession() {
		return session;
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
		if (session == null) {
			createAndSetToCurrent();

			/*recorder.load(new File("D:/test-gina-recorder"));
			recorder.play();*/

			session.startRecording();

			//GinaRecorderEditor editor = new GinaRecorderEditor();
		}
	}
	
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		if (getCurrentSession() != null)
			getCurrentSession().eventPerformed(e, stack);
	}

}

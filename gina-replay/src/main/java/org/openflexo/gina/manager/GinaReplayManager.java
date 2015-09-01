package org.openflexo.gina.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/*import java.util.LinkedList;
import java.util.List;*/
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.tuple.Pair;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.replay.GinaReplay;
import org.openflexo.replay.Scenario;
import org.openflexo.toolbox.HasPropertyChangeSupport;

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

	private GinaReplay replayer;
	private GinaEventFactory factory;
	private EventManager eventManager;
	
	public GinaReplayManager(EventManager eventManager) {
		/*if (eventManager == null)
			this.eventManager = new EventManager();
		else*/
			this.eventManager = eventManager;
		//this.eventManager.addListener(this);
		
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
	public GinaReplay createAndSetToCurrent() {
		GinaReplay r = new GinaReplay(this);

		setCurrentReplayer(r);

		return r;
	}

	/**
	 * Set the current recorder
	 * @param r the recorder instance
	 */
	public void setCurrentReplayer(GinaReplay r) {
		replayer = r;
	}

	/**
	 * Return the current recorder
	 * @return the current recorder
	 */
	public GinaReplay getCurrentReplayer() {
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

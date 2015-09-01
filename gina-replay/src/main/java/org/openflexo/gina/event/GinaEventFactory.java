package org.openflexo.gina.event;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.GinaNotifyMethodEventDescription;
import org.openflexo.gina.event.description.GinaTaskEventDescription;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * This factory creates NotifiyMethodEvent, TaskEvent, EventDescription and GinaEvents from an EventDescription
 * 
 * @author Alexandre
 */

public class GinaEventFactory {
	static private GinaEventFactory instance;
	private ModelFactory factory;
	private ModelContext context;
	
	static public GinaEventFactory getInstance() {
		if (instance == null)
			instance = new GinaEventFactory();
		
		return instance;
	}


	private GinaEventFactory() {
		super();
		this.factory = null;
		this.context = null;
	}

	public ModelFactory getModelFactory() {
		if (factory == null) {
			try {
				context = new ModelContext(GinaEvent.class);
				factory = new ModelFactory(context);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		return factory;
	}


	public GinaEvent createEventFromDescription(EventDescription d, GinaEvent.KIND kind) {
		return (GinaEvent) getModelFactory().newInstance(getEventClass(kind), d);
	}
	
	public EventDescription createGenericEvent() {
		return getModelFactory().newInstance(EventDescription.class);
	}
	
	public EventDescription createNotifiyMethodEvent(String action, String methodClass, String methodName, String info) {
		return getModelFactory().newInstance(GinaNotifyMethodEventDescription.class, action, methodClass, methodName, info);
	}

	public GinaTaskEventDescription createTaskEvent(String action, String identifier) {
		return getModelFactory().newInstance(GinaTaskEventDescription.class, action, identifier);
	}
	
	static public Class<?> getEventClass(KIND kind) {
		switch(kind) {
		case USER_INTERACTION:
			return UserInteraction.class;
		case SYSTEM_EVENT:
			return SystemEvent.class;
		case UNKNOWN:
			break;
		default:
			break;
		}

		return null;
	}
}

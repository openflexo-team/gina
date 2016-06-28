package org.openflexo.gina.manager;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.description.ApplicationEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.NotifyMethodEventDescription;
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
	private ModelFactory factory;
	private ModelContext context;
	private List<Class<?>> eventDescriptionModelClasses;

	public GinaEventFactory() {
		super();
		
		this.eventDescriptionModelClasses = new LinkedList<Class<?>>();
	}
	
	public void addModel(Class<?> cls) {
		if (!this.eventDescriptionModelClasses.contains(cls))
			this.eventDescriptionModelClasses.add(cls);
	}

	public ModelFactory getModelFactory() {
		if (factory == null) {
			try {
				context = new ModelContext(this.eventDescriptionModelClasses.toArray(new Class[this.eventDescriptionModelClasses.size()]));
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
		return getModelFactory().newInstance(NotifyMethodEventDescription.class, action, methodClass, methodName, info);
	}

	public GinaTaskEventDescription createTaskEvent(String action, String identifier) {
		return getModelFactory().newInstance(GinaTaskEventDescription.class, action, identifier);
	}
	
	public ApplicationEventDescription createApplicationEvent(String action,
			String className) {
		return getModelFactory().newInstance(ApplicationEventDescription.class, action, className);
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

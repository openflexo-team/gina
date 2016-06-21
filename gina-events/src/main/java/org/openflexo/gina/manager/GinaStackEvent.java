package org.openflexo.gina.manager;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.UserInteraction;

/**
 * A GinaStackEvent is used to manage the causality of different events.
 * It describes one GinaEvent and should be added to the EventManager's stack at its creation.
 * Once done with it, the end method should be called to pop this GinaStackEvent from the EventManager's stack.
 * 
 * getParentEvent and getOrigin are used to browse the stack and retrieve causality information.
 * 
 * @author Alexandre
 *
 */
public class GinaStackEvent {
	private GinaEvent event;
	private EventManager manager;
	
	public GinaStackEvent() {
		this(null, null);
	}

	public GinaStackEvent(GinaEvent event, EventManager manager) {
		super();
		this.event = event;
		this.manager = manager;
	}
	
	public void end() {
		if (this.event != null && this.manager != null)
			manager.popStackEvent(this);
	}
	
	public GinaEvent getEvent() {
		return event;
	}
	
	public GinaEvent getParentEvent() {
		Stack<GinaStackEvent> stack = manager.getEventStack();
		if (stack.size() > 1)
			return (UserInteraction) stack.get(stack.size() - 2).getEvent();
		
		return null;
	}
	
	public UserInteraction getOrigin() {
		if (manager.getEventStack().size() > 0)
			return (UserInteraction) manager.getEventStack().firstElement().getEvent();
		
		return null;
	}
	
	public String toString() {
		if (event == null)
			return "<null stack>";
		return "<stack> " + event.toString();
	}
}

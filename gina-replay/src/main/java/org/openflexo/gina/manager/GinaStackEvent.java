package org.openflexo.gina.manager;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.UserInteraction;

public class GinaStackEvent {
	private GinaEvent event;
	private GinaManager manager;
	
	public GinaStackEvent() {
		this(null, null);
	}

	public GinaStackEvent(GinaEvent event, GinaManager manager) {
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

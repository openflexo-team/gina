package org.openflexo.fib.listener;

import org.openflexo.gina.event.GinaEvent;

public class GinaStackEvent {
	private GinaEvent event;

	public GinaStackEvent(GinaEvent event) {
		super();
		this.event = event;
	}
	
	public void unstack() {
		GinaHandler.getInstance().popStackEvent(this);
	}
	
	public GinaEvent getEvent() {
		return event;
	}
	
	public String toString() {
		if (event == null)
			return "<null stack>";
		return "<stack> " + event.toString();
	}
}

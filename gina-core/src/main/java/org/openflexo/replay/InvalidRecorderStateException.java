package org.openflexo.replay;

import org.openflexo.gina.event.GinaEvent;

public class InvalidRecorderStateException extends Exception {
	private GinaRecordedNode node;
	private GinaEvent state;
	
	public InvalidRecorderStateException(String message) {
        this(message, null, null);
    }

	public InvalidRecorderStateException(String message, GinaRecordedNode node, GinaEvent e) {
        super(message);
        
        this.node = node;
        this.state = e;
    }
	
	public String getMessage() {
		return super.getMessage() + "\nState : " + this.state +
				(this.node.getEvents().size() > 0 ? "\nFrom event : " + this.node.getEvents().get(0) : "");
	}
}

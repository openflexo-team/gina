package org.openflexo.replay;

import org.openflexo.gina.event.GinaEvent;

public class InvalidRecorderStateException extends Exception {
	private InteractionCycle node;
	private GinaEvent state;
	
	public InvalidRecorderStateException(String message) {
        this(message, null, null);
    }
	
	public InvalidRecorderStateException(String value1, String value2) {
        this("Values are different :\n" + value1 + " != " + value2, null, null);
    }

	public InvalidRecorderStateException(String message, InteractionCycle node, GinaEvent e) {
        super(message);
        
        this.node = node;
        this.state = e;
    }
	
	public String getMessage() {
		return super.getMessage() + "\nNon User Interation : " + this.state +
				(this.node != null && this.node.getUserInteractions().size() > 0 ? "\nFrom User Interaction : " + this.node.getUserInteractions().get(0) : "");
	}
}

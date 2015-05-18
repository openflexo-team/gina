package org.openflexo.himtester;

import org.openflexo.himtester.events.FIBActionEvent;

public class FIBInvalidStateException extends Exception {
	private FIBRecordedNode node;
	private FIBActionEvent state;

	public FIBInvalidStateException(String message, FIBRecordedNode node, FIBActionEvent state) {
        super(message);
        
        this.node = node;
        this.state = state;
    }
	
	public String getMessage() {
		return super.getMessage() + "\nState : " + this.state +
				(this.node.getEvents().size() > 0 ? "\nFrom event : " + this.node.getEvents().get(0) : "");
	}
}

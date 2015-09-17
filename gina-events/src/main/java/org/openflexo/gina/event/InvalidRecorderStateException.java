package org.openflexo.gina.event;


public class InvalidRecorderStateException extends Exception {
	private static final long serialVersionUID = -8987939023490611302L;
	//private InteractionCycle node;
	private GinaEvent state;
	
	public InvalidRecorderStateException(String message) {
        this(message/*, null*/, (GinaEvent)null);
    }
	
	public InvalidRecorderStateException(String value1, String value2) {
        this("Values are different :\n" + value1 + " != " + value2/*, null*/, (GinaEvent)null);
    }

	public InvalidRecorderStateException(String message/*, InteractionCycle node*/, GinaEvent e) {
        super(message);
        
        //this.node = node;
        this.state = e;
    }
	
	public String getMessage() {
		return super.getMessage() + "\nNon User Interation : " + this.state /*+
				(this.node != null && this.node.getUserInteractions().size() > 0 ? "\nFrom User Interaction : " + this.node.getUserInteractions().get(0) : "")*/;
	}
}

package org.openflexo.gina.event;


public class MissingIdentityParameterException extends Exception {
	private static final long serialVersionUID = 3685124099798553449L;
	private String attribute, className;
	
	public MissingIdentityParameterException(String attribute, String className) {
		super(attribute + " is missing for the identification of " + className);
		this.attribute = attribute;
		this.className = className;
    }

	public String getAttribute() {
		return attribute;
	}

	public String getClassName() {
		return className;
	}
	
	/*public String getMessage() {
		return super.getMessage() + "\nNon User Interation : " + this.state;
	}*/
}

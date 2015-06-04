package org.openflexo.gina.event;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.replay.InvalidRecorderStateException;

@ModelEntity
@ImplementationClass(GinaEvent.GinaEventImpl.class)
@Imports({ @Import(FIBEvent.class) })
@XMLElement(xmlTag = "Event")
public abstract interface GinaEvent {
	@PropertyIdentifier(type = String.class)
	public static final String ACTION = "action";
	
	@PropertyIdentifier(type = String.class)
	public static final String VALUE = "value";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String ABSOLUTE_VALUE = "absoluteValue";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String DELAY = "delay";
	
	@PropertyIdentifier(type = boolean.class)
	public static final String FROM_USER = "fromUser";
	
	@PropertyIdentifier(type = boolean.class)
	public static final String ENABLED = "enabled";


	@Initializer
	public GinaEvent init(@Parameter(ACTION) String action);


	@Getter(value = ACTION)
	@XMLAttribute
	public String getAction();

	@Setter(ACTION)
	public void setAction(String action);

	@Getter(value = VALUE)
	@XMLAttribute
	public String getValue();

	@Setter(VALUE)
	public void setValue(String value);
	
	@Getter(value = ABSOLUTE_VALUE)
	@XMLAttribute
	public String getAbsoluteValue();

	@Setter(ABSOLUTE_VALUE)
	public void setAbsoluteValue(String absoluteValue);
	
	@Getter(value = DELAY, defaultValue = "0")
	@XMLAttribute
	public int getDelay();

	@Setter(DELAY)
	public void setDelay(int delay);
	
	@Getter(value = FROM_USER, defaultValue = "false")
	@XMLAttribute
	public boolean isFromUserOrigin();

	@Setter(FROM_USER)
	public void setFromUser(boolean isFromUserOrigin);
	
	@Getter(value = ENABLED, defaultValue = "true")
	@XMLAttribute
	public boolean isEnabled();

	@Setter(ENABLED)
	public void setEnabled(boolean enabled);
	
	public boolean isMatchingIdentity(GinaEvent e);
	//public boolean isMatchingState(TesterEvent e);
	public boolean matchesState(GinaEvent e) throws InvalidRecorderStateException;
	public boolean isGeneric();
	
	public void execute();
	
	public abstract class GinaEventImpl implements GinaEvent {
		public String toString() {
			return "Generic event " + getAction() + ", value = " + getValue();
		}
		
		public boolean isMatchingIdentity(GinaEvent e) {
			return e.getAction().equals(getAction());
		}
		
		/*public boolean isMatchingState(TesterEvent e) {
			return true;
		}*/
		
		public boolean matchesState(GinaEvent e) throws InvalidRecorderStateException {
			return true;
		}
		
		public void execute() {}
		
		public boolean isGeneric() {
			return true;
		}

	}

}

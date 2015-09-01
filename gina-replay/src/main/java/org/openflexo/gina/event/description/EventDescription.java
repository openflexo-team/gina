package org.openflexo.gina.event.description;

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
@ImplementationClass(EventDescription.EventDescriptionImpl.class)
@Imports({ @Import(GinaNotifyMethodEventDescription.class), @Import(GinaTaskEventDescription.class) })
@XMLElement(xmlTag = "EventDescription")
public abstract interface EventDescription {
	public static final String EVENT_DEFINITION_ERROR = "event-definition-error";
	
	@PropertyIdentifier(type = String.class)
	public static final String ACTION = "action";
	
	@PropertyIdentifier(type = String.class)
	public static final String PARENT_IDENTIFIER = "parentIdentifier";
	
	@PropertyIdentifier(type = String.class)
	public static final String VALUE = "value";
	
	@PropertyIdentifier(type = String.class)
	public static final String ABSOLUTE_VALUE = "absoluteValue";
	
	@PropertyIdentifier(type = boolean.class)
	public static final String ENABLED = "enabled";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String DELAY = "delay";


	@Initializer
	public EventDescription init(@Parameter(ACTION) String action);


	@Getter(value = ACTION)
	@XMLAttribute
	public String getAction();

	@Setter(ACTION)
	public void setAction(String action);
	
	@Getter(value = PARENT_IDENTIFIER, defaultValue = "")
	@XMLAttribute
	public String getParentIdentifier();

	@Setter(PARENT_IDENTIFIER)
	public void setParentIdentifier(String parentIdentifier);

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
	
	@Getter(value = ENABLED, defaultValue = "true")
	@XMLAttribute
	public boolean isEnabled();

	@Setter(ENABLED)
	public void setEnabled(boolean enabled);
	
	public boolean matchesIdentity(EventDescription e);
	
	public boolean matchesEvent(EventDescription e);

	public void checkMatchingEvent(EventDescription e) throws InvalidRecorderStateException;
	
	public void execute();
	
	public String getNamespace();
	
	public abstract class EventDescriptionImpl implements EventDescription {
		public String toString() {
			return "Generic event " + getAction() + ", value = " + getValue();
		}

		public boolean matchesIdentity(EventDescription e) {
			return e.getAction().equals(getAction());
		}

		public void checkMatchingEvent(EventDescription e) throws InvalidRecorderStateException {}

		public boolean matchesEvent(EventDescription e) {
			try {
				checkMatchingEvent(e);
				return true;
			} catch (InvalidRecorderStateException e1) {
				return false;
			}
		}

		public void execute() {}

		public abstract String getNamespace();
	}

}

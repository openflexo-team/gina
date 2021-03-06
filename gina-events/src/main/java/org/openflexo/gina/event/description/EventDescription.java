package org.openflexo.gina.event.description;

import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(EventDescription.EventDescriptionImpl.class)
@Imports({ @Import(NotifyMethodEventDescription.class), @Import(GinaTaskEventDescription.class), @Import(ApplicationEventDescription.class) })
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
	
	public void execute(EventManager manager);
	
	public String getNamespace();
	
	public abstract class EventDescriptionImpl implements EventDescription {
		public String toString() {
			return "Generic event " + getAction() + " (" + String.valueOf(getDelay()) + ") value = " + getValue();
		}

		@Override
		public boolean matchesIdentity(EventDescription e) {
			return e.getAction().equals(getAction());
		}

		@Override
		public void checkMatchingEvent(EventDescription e) throws InvalidRecorderStateException {}

		@Override
		public boolean matchesEvent(EventDescription e) {
			try {
				checkMatchingEvent(e);
				return true;
			} catch (InvalidRecorderStateException e1) {
				return false;
			}
		}

		@Override
		public void execute(EventManager manager) {}

		@Override
		public abstract String getNamespace();
	}

}

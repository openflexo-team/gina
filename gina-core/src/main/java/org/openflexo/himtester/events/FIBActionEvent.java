package org.openflexo.himtester.events;

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

@ModelEntity
@ImplementationClass(FIBActionEvent.FIBActionEventImpl.class)
@Imports({ @Import(FIBButtonEvent.class), @Import(FIBTextEvent.class), @Import(FIBFocusEvent.class) })
@XMLElement(xmlTag = "ActionEvent")
public abstract interface FIBActionEvent {
	@PropertyIdentifier(type = String.class)
	public static final String WIDGET_CLASS = "widgetClass";
	
	@PropertyIdentifier(type = String.class)
	public static final String WIDGET_ID = "widgetID";
	
	@PropertyIdentifier(type = String.class)
	public static final String ACTION = "action";
	
	@PropertyIdentifier(type = String.class)
	public static final String COMPONENT = "component";
	
	@PropertyIdentifier(type = String.class)
	public static final String VALUE = "value";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String ABSOLUTE_VALUE = "absoluteValue";
	
	/*@PropertyIdentifier(type = Integer.class)
	public static final String SIZE = "size";*/
	
	@PropertyIdentifier(type = boolean.class)
	public static final String FROM_USER = "fromUser";


	@Initializer
	public FIBActionEvent init(@Parameter(ACTION) String action);


	/*@Initializer
	public FIBActionEvent init(@Parameter(WIDGET_CLASS) String widgetClass, @Parameter(WIDGET_ID) String widgetID,
			@Parameter(COMPONENT) String component, @Parameter(ACTION) String action);

	@Initializer
	public FIBActionEvent init(@Parameter(WIDGET_CLASS) String widgetClass, @Parameter(WIDGET_ID) String widgetID,
			@Parameter(COMPONENT) String component, @Parameter(ACTION) String action,
			@Parameter(VALUE) String value, @Parameter(ABSOLUTE_VALUE) String absoluteValue);*/
	
	
	@Getter(value = WIDGET_CLASS)
	@XMLAttribute
	public String getWidgetClass();

	@Setter(WIDGET_CLASS)
	public void setWidgetClass(String widgetClass);
	
	@Getter(value = WIDGET_ID)
	@XMLAttribute
	public String getWidgetID();

	@Setter(WIDGET_ID)
	public void setWidgetID(String widgetID);
	
	@Getter(value = ACTION)
	@XMLAttribute
	public String getAction();

	@Setter(ACTION)
	public void setAction(String action);
	
	@Getter(value = COMPONENT)
	@XMLAttribute
	public String getComponent();

	@Setter(COMPONENT)
	public void setComponent(String component);
	
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
	
	@Getter(value = FROM_USER, defaultValue = "0")
	@XMLAttribute
	public boolean isFromUserOrigin();

	@Setter(FROM_USER)
	public void setFromUser(boolean isFromUserOrigin);
	
	public void setIdentity(String widgetClass, String widgetID, String component);
	
	public boolean matchIdentity(FIBActionEvent e);
	
	public abstract class FIBActionEventImpl implements FIBActionEvent {
		/*public FIBActionEventImpl() {
			super();
		}

		public FIBActionEventImpl(String widgetClass, String widgetID, String component, String action) {
			super();
			
			setWidgetClass(widgetClass);
			setWidgetID(widgetID);
			setComponent(component);
			setAction(action);
		}*/
		
		public void setIdentity(String widgetClass, String widgetID, String component) {
			setWidgetClass(widgetClass);
			setWidgetID(widgetID);
			setComponent(component);
		}

		public String toString() {
			return "Action " + getAction() + " @ " + getWidgetID() + " (" + getWidgetClass() + ") within " + getComponent() + ", value = " + getValue();
		}
		
		public boolean matchIdentity(FIBActionEvent e) {
			return e.getAction().equals(getAction()) && e.getWidgetClass().equals(getWidgetClass()) && e.getWidgetID().equals(getWidgetID())
					&& e.getComponent().equals(getComponent());
		}

	}

}

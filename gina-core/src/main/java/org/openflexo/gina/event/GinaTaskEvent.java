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
@ImplementationClass(GinaTaskEvent.GinaTaskEventImpl.class)
@Imports({ @Import(FIBMouseEvent.class), @Import(FIBTextEvent.class), @Import(FIBFocusEvent.class),
	@Import(FIBControllerEvent.class), @Import(FIBChangeValueEvent.class), @Import(FIBSelectionEvent.class) })
@XMLElement(xmlTag = "FibEvent")
public abstract interface GinaTaskEvent extends GinaEvent {
	@PropertyIdentifier(type = String.class)
	public static final String WIDGET_CLASS = "widgetClass";
	
	@PropertyIdentifier(type = String.class)
	public static final String WIDGET_ID = "widgetID";
	
	@PropertyIdentifier(type = String.class)
	public static final String COMPONENT = "component";


	@Initializer
	public GinaTaskEvent init(@Parameter(ACTION) String action);


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
	
	@Getter(value = COMPONENT)
	@XMLAttribute
	public String getComponent();

	@Setter(COMPONENT)
	public void setComponent(String component);
	
	public void setIdentity(String widgetClass, String widgetID, String component);
	
	public boolean matchIdentity(GinaTaskEvent e);
	
	public abstract class GinaTaskEventImpl extends GinaEventImpl implements GinaTaskEvent {
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
			return "Event " + getAction() + " @ (id=" + getWidgetID() + ", class=" + getWidgetClass() + ", within " + getComponent() + ") value = " + getValue();
		}
		
		public boolean isMatchingIdentity(GinaEvent e) {
			if (!(e instanceof GinaTaskEvent))
				return super.isMatchingIdentity(e);
			
			GinaTaskEvent fe = (GinaTaskEvent) e;
			return fe.getAction().equals(getAction()) && fe.getWidgetClass().equals(getWidgetClass()) && fe.getWidgetID().equals(getWidgetID())
					&& fe.getComponent().equals(getComponent());
		}
		
		public boolean matchesState(GinaEvent e) throws InvalidRecorderStateException {
			return true;
		}
		
		public void execute() {

		}
		
		public boolean isGeneric() {
			return false;
		}

	}

}

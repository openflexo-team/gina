package org.openflexo.gina.event.description;

import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.gina.event.InvalidRecorderStateException;
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
@ImplementationClass(FIBEventDescription.FIBEventDescriptionImpl.class)
@Imports({ @Import(FIBMouseEventDescription.class), @Import(FIBTextEventDescription.class), @Import(FIBFocusEventDescription.class),
	@Import(FIBControllerEventDescription.class), @Import(FIBValueEventDescription.class), @Import(FIBSelectionEventDescription.class) })
@XMLElement(xmlTag = "FibEvent")
public abstract interface FIBEventDescription extends EventDescription {
	@PropertyIdentifier(type = String.class)
	public static final String WIDGET_CLASS = "widgetClass";
	
	@PropertyIdentifier(type = String.class)
	public static final String WIDGET_ID = "widgetID";


	@Initializer
	public FIBEventDescription init(@Parameter(ACTION) String action);


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
	
	public void setIdentity(String widgetClass, String widgetID);
	
	public abstract class FIBEventDescriptionImpl extends EventDescriptionImpl implements FIBEventDescription {
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
		
		public void setIdentity(String widgetClass, String widgetID) {
			setWidgetClass(widgetClass);
			setWidgetID(widgetID);
		}

		public String toString() {
			return "Event " + getAction() + " (" + String.valueOf(getDelay()) + ") @ (id=" + getWidgetID() + ", class=" + getWidgetClass() + ", within " + getParentIdentifier() + ") value = " + getValue();
		}

		public boolean matchesIdentity(EventDescription e) {
			if (!(e instanceof FIBEventDescription))
				return super.matchesIdentity(e);

			FIBEventDescription fe = (FIBEventDescription) e;
			return fe.getAction().equals(getAction()) && fe.getWidgetClass().equals(getWidgetClass()) && fe.getWidgetID().equals(getWidgetID())
					&& fe.getParentIdentifier().equals(getParentIdentifier());
		}

		public void checkMatchingEvent(EventDescription e) throws InvalidRecorderStateException {
			if (!(e instanceof EventDescription)) {
				super.checkMatchingEvent(e);
				return;
			}
			
			if (getAbsoluteValue() != null && e.getAbsoluteValue() != null && !getAbsoluteValue().equals(e.getAbsoluteValue()))
				throw new InvalidRecorderStateException(getAbsoluteValue(), e.getAbsoluteValue());

		}

		public void execute() {
			/*FIBWidgetView<?, ?, ?> wv = GinaHandlerTemp.getInstance().findWidgetViewChildByID(getComponent(), getWidgetID());
			if (wv == null)
				throw new NullPointerException();
			wv.executeEvent(this);*/
		}

		public String getNamespace() {
			return "description.fib";
		}

	}

}

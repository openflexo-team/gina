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
@ImplementationClass(GinaTaskEventDescription.GinaTaskEventDescriptionImpl.class)
@XMLElement(xmlTag = "TaskEvent")
public abstract interface GinaTaskEventDescription extends EventDescription {
	public static final String STARTED = "task-started";
	public static final String FINISHED = "task-finished";


	@PropertyIdentifier(type = String.class)
	public static final String TASK_TITLE = "taskTitle";
	
	@PropertyIdentifier(type = String.class)
	public static final String TASK_CLASS = "taskClass";


	@Initializer
	public GinaTaskEventDescription init(@Parameter(ACTION) String action);
	
	@Initializer
	public GinaTaskEventDescription init(@Parameter(ACTION) String action, @Parameter(TASK_TITLE) String title);


	/*@Initializer
	public FIBActionEvent init(@Parameter(WIDGET_CLASS) String widgetClass, @Parameter(WIDGET_ID) String widgetID,
			@Parameter(COMPONENT) String component, @Parameter(ACTION) String action);

	@Initializer
	public FIBActionEvent init(@Parameter(WIDGET_CLASS) String widgetClass, @Parameter(WIDGET_ID) String widgetID,
			@Parameter(COMPONENT) String component, @Parameter(ACTION) String action,
			@Parameter(VALUE) String value, @Parameter(ABSOLUTE_VALUE) String absoluteValue);*/
	
	
	@Getter(value = TASK_CLASS)
	@XMLAttribute
	public String getTaskClass();

	@Setter(TASK_CLASS)
	public void setTaskClass(String taskClass);
	
	@Getter(value = TASK_TITLE)
	@XMLAttribute
	public String getTaskTitle();

	@Setter(TASK_TITLE)
	public void setTaskTitle(String taskTitle);

	
	public void setIdentity(String taskClass, String taskTitle);
	
	public boolean matchIdentity(GinaTaskEventDescription e);
	
	public abstract class GinaTaskEventDescriptionImpl extends EventDescriptionImpl implements GinaTaskEventDescription {
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

		public void setIdentity(String taskClass, String taskTitle) {
			setTaskClass(taskClass);
			setTaskTitle(taskTitle);
		}

		public String toString() {
			return "TaskEvent " + getAction() + " @ (" + getTaskTitle();// + ", class=" + getTaskClass() +")";
		}

		public boolean matchesIdentity(EventDescription e) {
			/*if (!(e instanceof GinaTaskEventDescription))
				return super.matchesIdentity(e);
			
			GinaTaskEventDescription fe = (GinaTaskEventDescription) e;
			return fe.getAction().equals(getAction()) && fe.getWidgetClass().equals(getWidgetClass()) && fe.getWidgetID().equals(getWidgetID())
					&& fe.getComponent().equals(getComponent());*/
			return false;
		}

		public void checkMatchingEvent(EventDescription e) throws InvalidRecorderStateException {}

		public void execute() {

		}

		public String getNamespace() {
			return "description.task";
		}

	}

}

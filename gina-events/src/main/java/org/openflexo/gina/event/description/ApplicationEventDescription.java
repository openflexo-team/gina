package org.openflexo.gina.event.description;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ApplicationEventDescription.ApplicationDescriptionImpl.class)
@XMLElement(xmlTag = "ApplicationEvent")
public abstract interface ApplicationEventDescription extends EventDescription {
	public static final String STARTED = "application-started";
	public static final String FINISHED = "application-finished";


	@PropertyIdentifier(type = String.class)
	public static final String MAIN_CLASS = "mainClass";


	@Initializer
	public ApplicationEventDescription init(@Parameter(ACTION) String action);
	
	@Initializer
	public ApplicationEventDescription init(@Parameter(ACTION) String action, @Parameter(MAIN_CLASS) String main);
	
	
	@Getter(value = MAIN_CLASS)
	@XMLAttribute
	public String getMainClass();

	@Setter(MAIN_CLASS)
	public void setMainClass(String mainClass);

	
	public boolean matchIdentity(ApplicationEventDescription e);
	
	public abstract class ApplicationDescriptionImpl extends EventDescriptionImpl implements ApplicationEventDescription {

		public String toString() {
			return "ApplicationEvent " + getAction() + " (" + getMainClass() + ")";
		}

		@Override
		public String getNamespace() {
			return "description.application";
		}

	}

}

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
@ImplementationClass(NotifyMethodEventDescription.GinaNotifyMethodEventDescriptionImpl.class)
@XMLElement(xmlTag = "GinaNotifyMethodEvent")
public abstract interface NotifyMethodEventDescription extends
		EventDescription {
	public static final String NOTIFIED = "method-notified";

	@PropertyIdentifier(type = String.class)
	public static final String METHOD_CLASS = "methodClass";

	@PropertyIdentifier(type = String.class)
	public static final String METHOD_NAME = "methodName";

	@PropertyIdentifier(type = String.class)
	public static final String INFO = "info";

	@Initializer
	public NotifyMethodEventDescription init(
			@Parameter(ACTION) String action,
			@Parameter(METHOD_CLASS) String methodClass,
			@Parameter(METHOD_NAME) String methodName,
			@Parameter(INFO) String info);

	@Getter(value = METHOD_CLASS)
	@XMLAttribute
	public String getMethodClass();

	@Setter(METHOD_CLASS)
	public void setMethodClass(String methodClass);

	@Getter(value = METHOD_NAME)
	@XMLAttribute
	public String getMethodName();

	@Setter(METHOD_NAME)
	public void setMethodName(String methodName);
	
	@Getter(value = INFO)
	@XMLAttribute
	public String getInfo();

	@Setter(INFO)
	public void setInfo(String info);

	public abstract class GinaNotifyMethodEventDescriptionImpl extends EventDescriptionImpl implements
			NotifyMethodEventDescription {
		public String toString() {
			return "NotifyMethod event " + getAction() + " : "
					+ getMethodName() + " in class " + getMethodClass() + "  " + getInfo();
		}
	}

}

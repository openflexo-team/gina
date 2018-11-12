package org.openflexo.gina.event.description;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

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

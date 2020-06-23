package org.openflexo.gina.event.description;

import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ValueEvent")
public abstract interface FIBValueEventDescription extends FIBEventDescription {
	public static final String CHECKED = "checked";
	public static final String UNCHECKED = "unchecked";
	public static final String CHANGED = "changed";
	
	@Initializer
	public FIBValueEventDescription init(@Parameter(ACTION) String action, @Parameter(VALUE) String value/*, @Parameter(ABSOLUTE_VALUE) String absoluteValue*/);
}

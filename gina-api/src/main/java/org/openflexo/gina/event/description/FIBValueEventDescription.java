package org.openflexo.gina.event.description;

import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ValueEvent")
public abstract interface FIBValueEventDescription extends FIBEventDescription {
	public static final String CHECKED = "checked";
	public static final String UNCHECKED = "unchecked";
	public static final String CHANGED = "changed";
	
	@Initializer
	public FIBValueEventDescription init(@Parameter(ACTION) String action, @Parameter(VALUE) String value/*, @Parameter(ABSOLUTE_VALUE) String absoluteValue*/);
}

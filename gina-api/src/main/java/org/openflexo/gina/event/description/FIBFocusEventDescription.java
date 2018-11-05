package org.openflexo.gina.event.description;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "FocusEvent")
public abstract interface FIBFocusEventDescription extends FIBEventDescription {
	public static final String FOCUS_GAINED = "focus-gained";
	public static final String FOCUS_LOST = "focus-lost";
}

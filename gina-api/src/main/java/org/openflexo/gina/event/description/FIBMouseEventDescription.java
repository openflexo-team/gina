package org.openflexo.gina.event.description;

import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "MouseEvent")
public abstract interface FIBMouseEventDescription extends FIBEventDescription {
	public static final String CLICKED = "mouse-clicked";
}

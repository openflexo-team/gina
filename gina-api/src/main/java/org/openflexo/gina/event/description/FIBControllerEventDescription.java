package org.openflexo.gina.event.description;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ControllerEvent")
public abstract interface FIBControllerEventDescription extends FIBEventDescription {
}

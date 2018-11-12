package org.openflexo.gina.event.description;

import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ControllerEvent")
public abstract interface FIBControllerEventDescription extends FIBEventDescription {
}

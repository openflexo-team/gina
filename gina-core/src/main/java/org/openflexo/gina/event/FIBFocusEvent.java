package org.openflexo.gina.event;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "FocusEvent")
public abstract interface FIBFocusEvent extends FIBEvent {
}

package org.openflexo.gina.event;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "MouseEvent")
public abstract interface FIBMouseEvent extends FIBEvent {
}

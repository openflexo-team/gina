package org.openflexo.himtester.events;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "MouseEvent")
public abstract interface FIBMouseEvent extends FIBActionEvent {
}

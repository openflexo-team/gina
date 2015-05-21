package org.openflexo.himtester.events;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ComponentEvent")
public abstract interface FIBControllerEvent extends FIBActionEvent {
}

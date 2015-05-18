package org.openflexo.himtester.events;

import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
//@ImplementationClass(FIBButtonEvent.FIBButtonEventImpl.class)
@XMLElement(xmlTag = "ButtonEvent")
public abstract interface FIBButtonEvent extends FIBActionEvent {
}

package org.openflexo.gina.event;

import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "ChangeValueEvent")
public abstract interface FIBChangeValueEvent extends FIBEvent {
	@Initializer
	public FIBChangeValueEvent init(@Parameter(ACTION) String action, @Parameter(VALUE) String value);
}

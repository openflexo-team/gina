package org.openflexo.gina.event.description.item;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "Item")
@Imports({ @Import(DescriptionIntegerItem.class) })
public abstract interface DescriptionItem {
	@PropertyIdentifier(type = String.class)
	public static final String ACTION = "action";

	@Initializer
	public DescriptionItem init(@Parameter(ACTION) String action);

	@Getter(value = ACTION)
	@XMLAttribute
	public String getAction();

	@Setter(ACTION)
	public void setAction(String action);
}

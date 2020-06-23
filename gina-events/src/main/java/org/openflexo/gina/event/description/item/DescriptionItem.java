package org.openflexo.gina.event.description.item;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

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

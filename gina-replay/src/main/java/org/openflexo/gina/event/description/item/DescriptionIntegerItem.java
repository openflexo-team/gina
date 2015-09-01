package org.openflexo.gina.event.description.item;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "EventIntegerItem")
public abstract interface DescriptionIntegerItem extends DescriptionItem {
	@PropertyIdentifier(type = Integer.class)
	public static final String VALUE = "value";


	@Initializer
	public DescriptionIntegerItem init(@Parameter(ACTION) String action, @Parameter(VALUE) Integer value);


	@Getter(value = VALUE, defaultValue = "0")
	@XMLAttribute
	public Integer getValue();

	@Setter(VALUE)
	public void setValue(Integer value);
}

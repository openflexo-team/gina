package org.openflexo.gina.event.description.item;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(DescriptionIntegerItem.DescriptionIntegerItemImpl.class)
@XMLElement(xmlTag = "IntegerItem")
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
	
	public abstract class DescriptionIntegerItemImpl implements DescriptionIntegerItem {
		public String toString() {
			return "[DI:" + getAction() + "]" + getValue() + "(Integer)";
		}
	}
}

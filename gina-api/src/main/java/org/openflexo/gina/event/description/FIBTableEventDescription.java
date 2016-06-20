package org.openflexo.gina.event.description;

import org.openflexo.gina.manager.EventManager;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTableEventDescription.FIBTableEventDescriptionImpl.class)
@XMLElement(xmlTag = "TableEvent")
public abstract interface FIBTableEventDescription extends FIBEventDescription {
	public static final String CHECKED = "checked";
	public static final String UNCHECKED = "unchecked";
	public static final String CHANGED = "changed";

	@PropertyIdentifier(type = String.class)
	public static final String CLASS_VALUE = "classValue";

	@PropertyIdentifier(type = Integer.class)
	public static final String ROW = "row";

	@PropertyIdentifier(type = Integer.class)
	public static final String COL = "col";

	@Initializer
	public FIBTableEventDescription init(@Parameter(ACTION) String action, @Parameter(VALUE) String value,
			@Parameter(CLASS_VALUE) String classValue, @Parameter(ROW) Integer row, @Parameter(COL) Integer col);

	@Getter(value = ROW, defaultValue = "0")
	@XMLAttribute
	public int getRow();

	@Setter(ROW)
	public void setRow(int row);

	@Getter(value = COL, defaultValue = "0")
	@XMLAttribute
	public int getCol();

	@Setter(COL)
	public void setCol(int col);

	@Getter(value = CLASS_VALUE, defaultValue = "")
	@XMLAttribute
	public String getClassValue();

	@Setter(CLASS_VALUE)
	public void setClassValue(String col);

	public Object getObjectValue();

	public abstract class FIBTableEventDescriptionImpl extends FIBEventDescriptionImpl implements FIBTableEventDescription {

		@Override
		public Object getObjectValue() {
			return EventManager.getObjectValue(getValue(), getClassValue());
		}

	}
}

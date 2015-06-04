package org.openflexo.gina.event;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "SelectionEvent")
public abstract interface FIBSelectionEvent extends FIBEvent {
	@PropertyIdentifier(type = Integer.class)
	public static final String FIRST_ELEMENT = "firstElement";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String LAST_ELEMENT = "lastElement";
	
	@Initializer
	public FIBSelectionEvent init(@Parameter(ACTION) String action, @Parameter(FIRST_ELEMENT) Integer firstElement, @Parameter(LAST_ELEMENT) Integer lastElement);
	
	@Getter(value = FIRST_ELEMENT, defaultValue = "0")
	@XMLAttribute
	public int getFirstElement();

	@Setter(FIRST_ELEMENT)
	public void setFirstElement(int firstElement);
	
	@Getter(value = LAST_ELEMENT, defaultValue = "0")
	@XMLAttribute
	public int getLastElement();

	@Setter(LAST_ELEMENT)
	public void setLastElement(int lastElement);
}

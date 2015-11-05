package org.openflexo.gina.event.description;

import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

@ModelEntity
@XMLElement(xmlTag = "SelectionEvent")
//@Imports({ @Import(EventItem.class) })
public abstract interface FIBSelectionEventDescription extends FIBEventDescription {
	public static final String SELECTED = "selection-selected";
	public static final String DESELECTED = "selection-deselected";
	public static final String CLEAR_SELECTION = "selection-clear";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String LEAD = "lead";
	
	/*@PropertyIdentifier(type = Integer.class)
	public static final String LAST_ELEMENT = "lastElement";*/
	
	@Initializer
	public FIBSelectionEventDescription init(@Parameter(ACTION) String action, @Parameter(LEAD) Integer lead);
	
	/*@Getter(value = FIRST_ELEMENT, defaultValue = "0")
	@XMLAttribute
	public int getFirstElement();

	@Setter(FIRST_ELEMENT)
	public void setFirstElement(int firstElement);*/
	
	@Getter(value = LEAD, defaultValue = "-1")
	@XMLAttribute
	public int getLead();

	@Setter(LEAD)
	public void setLead(int lead);
	
	// selection
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUES_KEY = "values";

	@Getter(value = VALUES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<DescriptionItem> getValues();
	
	@Adder(VALUES_KEY)
	public void addValue(DescriptionItem aColumn);
	
	@Remover(VALUES_KEY)
	public void removeValue(DescriptionItem aColumn);
}

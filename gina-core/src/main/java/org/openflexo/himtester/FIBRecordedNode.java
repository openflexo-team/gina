package org.openflexo.himtester;

import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.himtester.events.FIBActionEvent;
import org.openflexo.himtester.events.FIBMouseEvent;
import org.openflexo.himtester.events.FIBEventFactory;
import org.openflexo.himtester.events.FIBFocusEvent;
import org.openflexo.himtester.events.FIBTextEvent;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
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
@XMLElement(xmlTag = "Node")
public interface FIBRecordedNode {
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EVENTS_KEY = "events";
	
	@PropertyIdentifier(type = DataBinding.class)
	public static final String NODES_KEY = "nodes";
	
	@PropertyIdentifier(type = DataBinding.class)
	public static final String STATES_KEY = "states";
	
	@PropertyIdentifier(type = boolean.class)
	public static final String ENABLED = "enabled";

	@Getter(value = EVENTS_KEY, cardinality = Cardinality.LIST)
	@XMLElement(context = "EVENT_")
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBActionEvent> getEvents();
	
	@Adder(EVENTS_KEY)
	public void addEvent(FIBActionEvent aColumn);
	
	@Remover(EVENTS_KEY)
	public void removeEvent(FIBActionEvent aColumn);
	
	@Getter(value = NODES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBRecordedNode> getNodes();
	
	@Adder(NODES_KEY)
	public void addNode(FIBRecordedNode aColumn);
	
	@Remover(NODES_KEY)
	public void removeNode(FIBRecordedNode aColumn);
	
	@Getter(value = STATES_KEY, cardinality = Cardinality.LIST)
	@XMLElement(context = "STATE_")
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBActionEvent> getStates();
	
	@Adder(STATES_KEY)
	public void addState(FIBActionEvent aColumn);
	
	@Remover(STATES_KEY)
	public void removeState(FIBActionEvent aColumn);
	
	@Getter(value = ENABLED, defaultValue = "true")
	@XMLAttribute
	public boolean isEnabled();

	@Setter(ENABLED)
	public void setEnabled(boolean enabled);
}

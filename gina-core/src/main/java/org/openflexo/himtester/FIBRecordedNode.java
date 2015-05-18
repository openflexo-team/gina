package org.openflexo.himtester;

import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.himtester.events.FIBActionEvent;
import org.openflexo.himtester.events.FIBButtonEvent;
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
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

@ModelEntity
@XMLElement(xmlTag = "Node")
@Imports({ @Import(FIBRecordedNode.FIBRecordedNodeStates.class) })
@ImplementationClass(FIBRecordedNode.FIBRecordedNodeImpl.class)
public interface FIBRecordedNode {
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EVENTS_KEY = "events";
	
	@PropertyIdentifier(type = DataBinding.class)
	public static final String NODES_KEY = "nodes";
	
	@PropertyIdentifier(type = DataBinding.class)
	public static final String STATES_KEY = "states";

	@Getter(value = EVENTS_KEY, cardinality = Cardinality.LIST)
	@XMLElement//(context = "Events")
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
	
	@Getter(value = STATES_KEY, cardinality = Cardinality.SINGLE)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public FIBRecordedNode.FIBRecordedNodeStates getStateList();
	
	@Setter(value = STATES_KEY)
	public void setStateList(FIBRecordedNode.FIBRecordedNodeStates list);
	
	public List<FIBActionEvent> getStates();
	public void addState(FIBActionEvent aColumn);
	public void removeState(FIBActionEvent aColumn);
	
	@ModelEntity
	@XMLElement(xmlTag = "States")
	public interface FIBRecordedNodeStates {
		@PropertyIdentifier(type = DataBinding.class)
		public static final String STATES_KEY = "states";

		@Getter(value = STATES_KEY, cardinality = Cardinality.LIST)
		@XMLElement
		@CloningStrategy(StrategyType.CLONE)
		@Embedded
		public List<FIBActionEvent> getStates();
		
		@Adder(STATES_KEY)
		public void addState(FIBActionEvent aColumn);
		
		@Remover(STATES_KEY)
		public void removeState(FIBActionEvent aColumn);
	}
	
	public abstract class FIBRecordedNodeImpl implements FIBRecordedNode {
		public List<FIBActionEvent> getStates() {
			return getStatesInstance().getStates();
		}
		
		public void addState(FIBActionEvent aColumn) {
			getStatesInstance().addState(aColumn);
		}
		
		public void removeState(FIBActionEvent aColumn) {
			getStatesInstance().removeState(aColumn);
		}
		
		private FIBRecordedNodeStates getStatesInstance() {
			if (getStateList() == null)
				setStateList(FIBRecorderManager.getInstance().getFactory().newInstance(FIBRecordedNode.FIBRecordedNodeStates.class));
			
			return getStateList();
		}

	}
}

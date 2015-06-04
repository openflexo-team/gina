package org.openflexo.replay;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.event.FIBEvent;
import org.openflexo.gina.event.FIBEventFactory;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

@ModelEntity
@ImplementationClass(GinaRecordedNode.FIBRecordedNodeImpl.class)
@XMLElement(xmlTag = "Node")
public interface GinaRecordedNode {
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
	public List<GinaEvent> getEvents();
	
	@Adder(EVENTS_KEY)
	public void addEvent(GinaEvent aColumn);
	
	@Remover(EVENTS_KEY)
	public void removeEvent(GinaEvent aColumn);
	
	@Getter(value = NODES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<GinaRecordedNode> getNodes();
	
	@Adder(NODES_KEY)
	public void addNode(GinaRecordedNode aColumn);
	
	@Remover(NODES_KEY)
	public void removeNode(GinaRecordedNode aColumn);
	
	@Getter(value = STATES_KEY, cardinality = Cardinality.LIST)
	@XMLElement(context = "STATE_")
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<GinaEvent> getStates();
	
	@Adder(STATES_KEY)
	public void addState(GinaEvent aColumn);
	
	@Remover(STATES_KEY)
	public void removeState(GinaEvent aColumn);
	
	@Getter(value = ENABLED, defaultValue = "true")
	@XMLAttribute
	public boolean isEnabled();

	@Setter(ENABLED)
	public void setEnabled(boolean enabled);
	
	public void addNewState();
	public void addNewEvent();
	public void addNewNode();
	
	public List<FIBEvent> getFIBEvents();
	public List<GinaEvent> getGenericOnlyEvents();
	
	public List<FIBEvent> getFIBStates();
	public List<GinaEvent> getGenericOnlyStates();
	
	public abstract class FIBRecordedNodeImpl implements GinaRecordedNode {
		@Override
		public void addNewState() {
			addState(FIBEventFactory.getInstance().createEvent());
		}

		@Override
		public void addNewEvent() {
			addEvent(FIBEventFactory.getInstance().createEvent());
		}
		
		@Override
		public void addNewNode() {
			addNode(GinaRecorderManager.getInstance().getFactory().newInstance(GinaRecordedNode.class));
		}
		
		public List<FIBEvent> getFIBEvents() {
			List<FIBEvent> l = new LinkedList<FIBEvent>();
			for(GinaEvent e : getEvents())
				if (!e.isGeneric())
					l.add((FIBEvent) e);
			return l;
		}
		
		public List<GinaEvent> getGenericOnlyEvents() {
			List<GinaEvent> l = new LinkedList<GinaEvent>();
			for(GinaEvent e : getEvents())
				if (e.isGeneric())
					l.add((FIBEvent) e);
			return l;
		}
		
		public List<FIBEvent> getFIBStates() {
			List<FIBEvent> l = new LinkedList<FIBEvent>();
			for(GinaEvent e : getStates())
				if (!e.isGeneric())
					l.add((FIBEvent) e);
			return l;
		}
		
		public List<GinaEvent> getGenericOnlyStates() {
			List<GinaEvent> l = new LinkedList<GinaEvent>();
			for(GinaEvent e : getStates())
				if (e.isGeneric())
					l.add((FIBEvent) e);
			return l;
		}
	}
}

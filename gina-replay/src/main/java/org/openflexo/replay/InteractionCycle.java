package org.openflexo.replay;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.manager.GinaManager;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

/**
 * This class represents an cycle of interaction (user and system) :
 *  - It starts by 1 UserInteraction
 *  - It is followed by 1 or many SystemEvent, in response of the previous UserInteraction
 * 
 * @author Alexandre
 */

@ModelEntity
@ImplementationClass(InteractionCycle.FIBRecordedNodeImpl.class)
@Imports({ @Import(SystemEventTreeNode.class) })
@XMLElement(xmlTag = "InteractionCycle")
public interface InteractionCycle extends ScenarioNode {
	@PropertyIdentifier(type = DataBinding.class)
	public static final String USER_INTERACTION_KEY = "userInteraction";
	
	@PropertyIdentifier(type = SystemEventTreeNode.class)
	public static final String SYSTEM_EVENT_NODE_KEY = "systemEventNode";


	@Getter(value = USER_INTERACTION_KEY, cardinality = Cardinality.LIST)
	@XMLElement(context = "UserInteraction_")
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<UserInteraction> getUserInteractions();
	
	@Adder(USER_INTERACTION_KEY)
	public void addUserInteraction(UserInteraction aColumn);
	
	@Remover(USER_INTERACTION_KEY)
	public void removeUserInteraction(UserInteraction aColumn);
	
	
	@Getter(value = SYSTEM_EVENT_NODE_KEY)
	@XMLElement(context = "SystemEvent_")
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public SystemEventTreeNode getSystemEventTree();
	
	@Setter(SYSTEM_EVENT_NODE_KEY)
	public void setSystemEventTree(SystemEventTreeNode systemEventTree);


	public void addNewNonUserInteraction();
	public void addNewUserInteraction();
	
	public List<GinaEvent> getUserInteractionsByKind(String kind);
	public List<GinaEvent> getNonUserInteractionsByKind(String kind);
	
	public void init(GinaManager manager);
	
	public abstract class FIBRecordedNodeImpl extends ScenarioNodeImpl implements InteractionCycle {
		
		public void init(GinaManager manager) {
			if (this.getSystemEventTree() == null)
				this.setSystemEventTree(manager.getFactory().newInstance(SystemEventTreeNode.class));
		}
		
		@Override
		public void addNewNonUserInteraction() {
			//addNonUserInteraction(FIBEventFactory.getInstance().createEvent());
		}

		@Override
		public void addNewUserInteraction() {
			//addUserInteraction(FIBEventFactory.getInstance().createEvent());
		}

		
		public List<GinaEvent> getUserInteractionsByKind(String namespace) {
			List<GinaEvent> l = new LinkedList<GinaEvent>();
			
			for(UserInteraction e : getUserInteractions())
				if (e.getDescription().getNamespace() == namespace)
					l.add(e);
			
			return l;
		}
		
		public List<GinaEvent> getNonUserInteractionsByKind(String namespace) {
			List<GinaEvent> l = new LinkedList<GinaEvent>();
			
			/*for(SystemEvent e : getSystemEvents())
				if (e.getDescription().getNamespace() == namespace)
					l.add(e);*/
			
			return l;
		}
	}
}

package org.openflexo.replay;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
<<<<<<< HEAD
 * This class represents a cycle of interaction (user and system) :
 *  - It starts by 1 UserInteraction
 *  - It is followed by 1 or many SystemEvent, in response of the previous UserInteraction
=======
 * This class represents an cycle of interaction (user and system) : - It starts by 1 UserInteraction - It is followed by 1 or many
 * SystemEvent, in response of the previous UserInteraction
>>>>>>> branch '2.0' of ssh://git@github.com/openflexo-team/gina
 * 
 * @author Alexandre
 */

@ModelEntity
@ImplementationClass(InteractionCycle.FIBRecordedNodeImpl.class)
@Imports({ @Import(SystemEventTreeNode.class) })
@XMLElement(xmlTag = "InteractionCycle")
public interface InteractionCycle extends ScenarioNode {
	@PropertyIdentifier(type = UserInteraction.class)
	public static final String USER_INTERACTION_KEY = "userInteraction";

	@PropertyIdentifier(type = SystemEventTreeNode.class)
	public static final String SYSTEM_EVENT_NODE_KEY = "systemEventNode";

	@Getter(value = USER_INTERACTION_KEY)
	@XMLElement(context = "UserInteraction_")
	@Embedded
	public UserInteraction getUserInteraction();

	@Setter(USER_INTERACTION_KEY)
	public void setUserInteraction(UserInteraction userInteraction);

	@Getter(value = SYSTEM_EVENT_NODE_KEY)
	@XMLElement(context = "SystemEvent_")
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public SystemEventTreeNode getSystemEventTree();

	@Setter(SYSTEM_EVENT_NODE_KEY)
	public void setSystemEventTree(SystemEventTreeNode systemEventTree);

	public void addNewNonUserInteraction();

	public void addNewUserInteraction();

	public List<GinaEvent> getNonUserInteractionsByKind(String kind);

	public void init(GinaReplayManager manager);

	public abstract class FIBRecordedNodeImpl extends ScenarioNodeImpl implements InteractionCycle {

		@Override
		public void init(GinaReplayManager manager) {
			if (this.getSystemEventTree() == null)
				this.setSystemEventTree(manager.getModelFactory().newInstance(SystemEventTreeNode.class));
		}

		@Override
		public void addNewNonUserInteraction() {
			// addNonUserInteraction(FIBEventFactory.getInstance().createEvent());
		}

		@Override
		public void addNewUserInteraction() {
			// addUserInteraction(FIBEventFactory.getInstance().createEvent());
		}

		@Override
		public List<GinaEvent> getNonUserInteractionsByKind(String namespace) {
			List<GinaEvent> l = new LinkedList<GinaEvent>();

			/*for(SystemEvent e : getSystemEvents())
				if (e.getDescription().getNamespace() == namespace)
					l.add(e);*/

			return l;
		}

		@Override
		public String toString() {
			if (getUserInteraction() == null)
				return "Interaction without UserInteraction";
			else
				return getUserInteraction().toString();
		}
	}
}

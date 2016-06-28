package org.openflexo.replay;

import java.util.List;
import org.openflexo.connie.DataBinding;
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
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

/**
 * This represents a scenario.
 * A scenario is composed of a list of ScenarioNode, each can be:
 *   - an InteractionCycle that represents events from the user and the responses of the system
 *   - another Scenario that can be loaded and played *TODO not functional yet*
 * 
 * @author Alexandre
 */

@ModelEntity
@ImplementationClass(Scenario.ScenarioImpl.class)
@Imports({ @Import(ScenarioNode.class) })
@XMLElement(xmlTag = "Scenario")
public interface Scenario extends ScenarioNode {
	@PropertyIdentifier(type = DataBinding.class)
	public static final String NODES_KEY = "nodes";


	@Getter(value = NODES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<ScenarioNode> getNodes();
	
	@Adder(NODES_KEY)
	public void addNode(ScenarioNode aColumn);
	
	@Remover(NODES_KEY)
	public void removeNode(ScenarioNode aColumn);


	public void addNewNode();
	
	public int getCurrentIndex();

	public void setCurrentIndex(int currentEventIndex);
	
	public void resetIndex();
	
	public boolean eof();
	
	public int size();
	
	public ScenarioNode getNextNode();
	
	public ScenarioNode getNodeByUserInteraction(UserInteraction userOrigin);
	
	public abstract class ScenarioImpl extends ScenarioNodeImpl implements Scenario {
		
		private int currentIndex;
		
		public int getCurrentIndex() {
			return this.currentIndex;
		}

		public void setCurrentIndex(int currentEventIndex) {
			this.currentIndex = currentEventIndex;
		}
		
		public void resetIndex() {
			this.currentIndex = 0;
		}
		
		public boolean eof() {
			return this.currentIndex >= getNodes().size();
		}
		
		public ScenarioNode getNextNode() {
			if (eof())
				return null;

			return getNodes().get(this.currentIndex++);
		}
		
		public ScenarioNode getNodeByUserInteraction(UserInteraction userOrigin) {
			for(ScenarioNode node : getNodes()) {
				if (node instanceof InteractionCycle) {
					InteractionCycle ic = (InteractionCycle) node;
					if (ic.getUserInteraction() == userOrigin)
						return ic;
				}
			}

			return null;
		}
		
		public int size() {
			return getNodes().size();
		}

		@Override
		public void addNewNode() {
			//addNode(GinaManager.getInstance().getFactory().newInstance(Scenario.class));
		}

	}
}

package org.openflexo.replay;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * This class represents a node in a scenario. It could be :
 *   - an InteractionCycle that represents events from the user and the responses of the system
 *   - another Scenario that can be loaded and played *TODO not functional yet*
 * 
 * @author Alexandre
 */
@ModelEntity
@ImplementationClass(ScenarioNode.ScenarioNodeImpl.class)
@Imports({ @Import(InteractionCycle.class) })
@XMLElement(xmlTag = "Node")
public interface ScenarioNode {
	@PropertyIdentifier(type = boolean.class)
	public static final String ENABLED = "enabled";


	@Getter(value = ENABLED, defaultValue = "true")
	@XMLAttribute
	public boolean isEnabled();

	@Setter(ENABLED)
	public void setEnabled(boolean enabled);

	
	public abstract class ScenarioNodeImpl implements ScenarioNode {
		
	}
}

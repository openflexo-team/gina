package org.openflexo.replay;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * This interface/implementation represents a node in a scenarion : it could be an InteractionCycle or another Scenario
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

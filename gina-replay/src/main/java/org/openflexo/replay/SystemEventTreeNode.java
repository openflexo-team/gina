package org.openflexo.replay;

import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Getter.Cardinality;

/**
 * This class is used to organize SystemEvent as a tree depending of their origin.
 * This is used to keep track of the causality of the SystemEvent responses.
 * 
 * @author Alexandre
 *
 */
@ModelEntity
// @ImplementationClass(SystemEventTreeNode.SystemEventTreeNodeImpl.class)
@Imports({ @Import(InteractionCycle.class) })
@XMLElement(xmlTag = "SystemEventNode")
public interface SystemEventTreeNode {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SYSTEM_EVENT_KEY = "systemEvent";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String CHILDREN_KEY = "children";

	@Initializer
	public SystemEventTreeNode init();

	@Initializer
	public SystemEventTreeNode init(@Parameter(SYSTEM_EVENT_KEY) SystemEvent systemEvent);

	@Getter(value = SYSTEM_EVENT_KEY)
	@XMLElement
	public SystemEvent getSystemEvent();

	@Setter(SYSTEM_EVENT_KEY)
	public void setSystemEvent(SystemEvent description);

	@Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<SystemEventTreeNode> getChildren();

	@Adder(CHILDREN_KEY)
	public void addChild(SystemEventTreeNode aColumn);

	@Remover(CHILDREN_KEY)
	public void removeChild(SystemEventTreeNode aColumn);

	/*public abstract class SystemEventTreeNodeImpl implements SystemEventTreeNode {
		
	}*/
}

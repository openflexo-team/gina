package org.openflexo.replay;

import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.SystemEvent;
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
//@ImplementationClass(SystemEventTreeNode.SystemEventTreeNodeImpl.class)
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

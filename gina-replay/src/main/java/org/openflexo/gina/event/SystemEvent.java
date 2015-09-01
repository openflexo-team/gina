package org.openflexo.gina.event;

import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

@ModelEntity
@ImplementationClass(SystemEvent.SystemEventImpl.class)
@XMLElement(xmlTag = "SystemEvent")
public abstract interface SystemEvent extends GinaEvent {

	public abstract class SystemEventImpl extends GinaEventImpl implements
			SystemEvent {

		public KIND getKind() {
			return KIND.SYSTEM_EVENT;
		}

	}

}

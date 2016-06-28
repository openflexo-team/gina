package org.openflexo.gina.event;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Describes a GinaEvent performed by the system, as a response to an UserInteraction
 * 
 * @author Alexandre
 */
@ModelEntity
@ImplementationClass(SystemEvent.SystemEventImpl.class)
@XMLElement(xmlTag = "SystemEvent")
public abstract interface SystemEvent extends GinaEvent {

	public abstract class SystemEventImpl extends GinaEventImpl implements SystemEvent {

		@Override
		public KIND getKind() {
			return KIND.SYSTEM_EVENT;
		}

	}

}

package org.openflexo.gina.event;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Describes a GinaEvent performed by an user (or simulated by the system as an user input)
 * 
 * @author Alexandre
 */
@ModelEntity
@ImplementationClass(UserInteraction.UserInteractionImpl.class)
@XMLElement(xmlTag = "UserInteraction")
public abstract interface UserInteraction extends GinaEvent {
	
	public abstract class UserInteractionImpl extends GinaEventImpl implements UserInteraction {

		public KIND getKind() {
			return KIND.USER_INTERACTION;
		}

	}

}

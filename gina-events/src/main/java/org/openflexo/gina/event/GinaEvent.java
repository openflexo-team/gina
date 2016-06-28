package org.openflexo.gina.event;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * This abstract class represents an event :
 *  - performed by the system in the case of the inherited class SystemEvent
 *  - performed by the user in the case of the inherited class UserInteraction
 * All the description of the event is contained in the EventDescription attribute.
 * 
 * @author Alexandre
 */
@ModelEntity
@ImplementationClass(value = GinaEvent.GinaEventImpl.class)
@Imports({ @Import(EventDescription.class), @Import(UserInteraction.class), @Import(SystemEvent.class) })
@XMLElement(xmlTag = "Event")
public abstract interface GinaEvent {

	public static enum KIND {
		UNKNOWN, USER_INTERACTION, SYSTEM_EVENT
	};

	@PropertyIdentifier(type = DataBinding.class)
	public static final String DESCRIPTION = "description";

	@Initializer
	public GinaEvent init(@Parameter(DESCRIPTION) EventDescription description);

	@Getter(value = DESCRIPTION)
	@Embedded
	@XMLElement
	public EventDescription getDescription();

	@Setter(DESCRIPTION)
	public void setDescription(EventDescription description);

	public KIND getKind();

	public boolean matchesIdentity(GinaEvent e);
	public boolean matchesEvent(GinaEvent e);
	public void checkMatchingEvent(GinaEvent e) throws InvalidRecorderStateException;

	public abstract class GinaEventImpl implements GinaEvent {

		@Override
		public boolean matchesIdentity(GinaEvent e) {
			return this.getDescription().matchesIdentity(e.getDescription());
		}

		public boolean matchesEvent(GinaEvent e) {
			return this.getDescription().matchesEvent(e.getDescription());
		}
		
		public void checkMatchingEvent(GinaEvent e) throws InvalidRecorderStateException {
			this.getDescription().checkMatchingEvent(e.getDescription());
		}

		@Override
		public String toString() {
			EventDescription d = getDescription();

			if (d == null)
				return super.toString();
			return d.toString();
		}

	}

}

package org.openflexo.gina.event;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

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

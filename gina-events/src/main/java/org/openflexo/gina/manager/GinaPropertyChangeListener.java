package org.openflexo.gina.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.description.NotifyMethodEventDescription;

public class GinaPropertyChangeListener implements PropertyChangeListener, Registrable {

	private GinaEventNotifier<NotifyMethodEventDescription> GENotifier;

	public GinaPropertyChangeListener(EventManager manager) {
		GENotifier = new GinaEventNotifier<NotifyMethodEventDescription>(manager, this) {

			@Override
			public KIND computeClass(NotifyMethodEventDescription d) {
				return KIND.SYSTEM_EVENT;
			}

			@Override
			public void setIdentity(NotifyMethodEventDescription d, Object o) {

			}

		};
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		GENotifier.notifyMethod(evt.getPropertyName());

	}

	@Override
	public String getBaseIdentifier() {
		return null;
	}

	@Override
	public URID getURID() {
		return null;
	}

	@Override
	public void setURID(URID urid) {

	}

	@Override
	public EventManager getEventManager() {
		return null;
	}

	@Override
	public void setEventManager(EventManager manager) {

	}

}

package org.openflexo.gina.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.description.GinaNotifyMethodEventDescription;

public class GinaPropertyChangeListener implements PropertyChangeListener, Registerable {
	
	private GinaEventNotifier<GinaNotifyMethodEventDescription> GENotifier;

	public GinaPropertyChangeListener(EventManager manager) {
		GENotifier = new GinaEventNotifier<GinaNotifyMethodEventDescription>(manager, this) {

			@Override
			public KIND computeClass(GinaNotifyMethodEventDescription d) {
				return KIND.SYSTEM_EVENT;
			}

			@Override
			public void setIdentity(GinaNotifyMethodEventDescription d) {
				
			}
			
		};
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		GENotifier.notifyMethod(evt.getPropertyName());
		
	}

	@Override
	public String getBaseIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URID getURID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setURID(URID urid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EventManager getEventManager() {
		// TODO Auto-generated method stub
		return null;
	}

}

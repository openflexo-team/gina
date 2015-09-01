package org.openflexo.gina.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.description.GinaNotifyMethodEventDescription;

public class GinaPropertyChangeListener implements PropertyChangeListener {
	
	private GinaEventNotifier<GinaNotifyMethodEventDescription> GENotifier;

	public GinaPropertyChangeListener(GinaManager ginaManager) {
		GENotifier = new GinaEventNotifier<GinaNotifyMethodEventDescription>(ginaManager) {

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

}

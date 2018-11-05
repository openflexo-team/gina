package org.openflexo.gina.manager;

import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.description.EventDescription;

public interface HasEventNotifier<D extends EventDescription> {
	
	public GinaEventNotifier<D> getNotifier();

}

package org.openflexo.gina.event;

import org.openflexo.gina.event.description.EventDescription;

public interface GenericEventPerformer<T extends EventDescription> {

	public void executeEvent(EventDescription e);

}

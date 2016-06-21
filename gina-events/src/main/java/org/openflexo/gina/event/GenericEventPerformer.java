package org.openflexo.gina.event;

import org.openflexo.gina.event.description.EventDescription;

/**
 * Interface describing the capacity to execute an EventDescription
 * 
 * @author Alexandre
 */
public interface GenericEventPerformer<T extends EventDescription> {

	public void executeEvent(EventDescription e);

}

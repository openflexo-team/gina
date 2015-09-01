package org.openflexo.gina.manager;

import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.description.EventDescription;

/**
 * This interface extends a Registerable that can raise a GinaEvent.
 * 
 * @author Alexandre
 *
 * @param <D> the type of the EventDescription that will be raised
 */
public interface NotifierRegisterable<D extends EventDescription> extends Registerable {

	public GinaEventNotifier<D> getNotifier();

}

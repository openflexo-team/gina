package org.openflexo.gina.event.description;

public interface GenericEventPerformer<T extends EventDescription> {

	public void executeEvent(EventDescription e);
	
	/*public GinaStackEvent eventPerformed(T e);
	
	public boolean isMatching(GinaEvent e);*/

}

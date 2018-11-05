package org.openflexo.gina.manager;

/**
 * This interface represents a way to identify in an unique way an object using URID.
 * 
 * @author Alexandre
 *
 */
public interface Registrable extends HasBaseIdentifier {

	public URID getURID();
	
	public void setURID(URID urid);
	
	public EventManager getEventManager();
	
	public void setEventManager(EventManager manager);

}

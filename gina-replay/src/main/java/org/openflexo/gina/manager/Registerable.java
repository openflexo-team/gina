package org.openflexo.gina.manager;

/**
 * This interface represents a way to identify in an unique way an object.
 * 
 * @author Alexandre
 *
 */
public interface Registerable {

	public URID getURID();
	
	public String getBaseIdentifier();

}

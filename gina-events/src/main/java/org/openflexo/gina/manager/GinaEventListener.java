package org.openflexo.gina.manager;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;

/**
 * Listener of any GinaEvent performed.
 * Should be rattached to an EventManager.
 * 
 * @author Alexandre
 *
 */
public interface GinaEventListener {
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack);
}

package org.openflexo.gina.manager;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;

public interface GinaEventListener {
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack);
}

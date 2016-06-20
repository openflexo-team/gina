package org.openflexo.gina.event.strategies;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.replay.GinaReplaySession;

/**
 * 
 * This class manages the way the events (UserInteraction & SystemEvent) are captured
 * during a recording session.
 * 
 * This class should be extended to adapt the behavior.
 * An example of behavior could be find in StandardRecordingStrategy
 * 
 * @author Alexandre
 *
 */
public abstract class RecordingStrategy {
	protected GinaReplaySession session;

	public RecordingStrategy(GinaReplaySession session) {
		super();
		this.session = session;
	}

	public abstract void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack);

}

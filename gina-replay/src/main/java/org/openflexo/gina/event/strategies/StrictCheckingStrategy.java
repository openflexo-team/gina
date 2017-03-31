package org.openflexo.gina.event.strategies;

import java.util.LinkedList;
import java.util.List;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.replay.SystemEventTreeNode;

/**
 * 
 * This class implements the following checking strategy : - expect any recorded user interaction and system response to be present - expect
 * any of them to match exactly the recorded data - any additional system response will be ignored and will not cause any error
 * 
 * @author Alexandre
 *
 */
public class StrictCheckingStrategy extends CheckingStrategy {

	public StrictCheckingStrategy(GinaReplaySession session) {
		super(session);
	}

	@Override
	public void checkSystemEvents(InteractionCycle node) throws InvalidRecorderStateException {
		// List<GinaEvent> l = new LinkedList<GinaEvent>();
		// l.addAll(node.getSystemEventTree());
		// l.add(node.getUserInteraction());

		List<GinaEvent> listReplayed = new LinkedList<>();
		List<GinaEvent> listExcepted = new LinkedList<>();
		if (replayEvents.containsKey(node.getUserInteraction())) {
			listReplayed.addAll(replayEvents.get(node.getUserInteraction()));
		}

		SystemEventTreeNode treeNode = node.getSystemEventTree();
		if (treeNode != null) {
			for (SystemEventTreeNode n : treeNode.getChildren()) {
				listExcepted.add(n.getSystemEvent());
			}
		}

		// System.out.println("1 : " + listReplayed);
		// System.out.println("2 : " + listExcepted);

		for (GinaEvent e : listExcepted) {
			GinaEvent matching = findSystemEvent(listReplayed, e);

			// System.out.println("/// Found \\\\\\" + matching);

			if (matching == null) {
				throw new InvalidRecorderStateException("No matching state", e);
			}

			e.checkMatchingEvent(matching);
		}
	}

}

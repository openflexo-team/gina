package org.openflexo.gina.event.strategies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.description.FIBSelectionEventDescription;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.InteractionCycle;

public class CheckingStrategy {

	private UserInteraction lastEventReplayed;
	private GinaReplaySession session;
	private Map<UserInteraction, List<SystemEvent>> replayEvents;
	private Map<GinaEvent, UserInteraction> replayNodeToEvents;

	public CheckingStrategy(GinaReplaySession session) {
		super();
		this.session = session;
		this.replayEvents = new HashMap<UserInteraction, List<SystemEvent>>();
		this.replayNodeToEvents = new HashMap<GinaEvent, UserInteraction>();
	}

	public void eventPlayed(UserInteraction e) {
		if (replayEvents.containsKey(e))
			return;

		System.out.println("[Replay] : " + e);
		replayEvents.put(e, new LinkedList<SystemEvent>());
		lastEventReplayed = e;
	}

	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		GinaEvent origin = session.getEventOrigin(e, stack);
		GinaEvent userOrigin = session.getEventUserOrigin(e, stack);

		if (lastEventReplayed != null) {
			System.out.println("Replay : " + e + ", origin : " + origin + ", userOrigin : " + userOrigin);
			replayNodeToEvents.put(e, lastEventReplayed);
			lastEventReplayed = null;
			return;
		}
		System.out.println("Response : " + e + ", origin : " + origin + ", userOrigin : " + userOrigin);
		if (e.getDescription() instanceof FIBSelectionEventDescription) {
			FIBSelectionEventDescription fe = (FIBSelectionEventDescription) e.getDescription();
			System.out.println(fe.getLead());
			for (DescriptionItem di : fe.getValues())
				System.out.println(di);
		}

		// add as event or state depending of its origin
		if (e.getKind() != KIND.USER_INTERACTION && replayNodeToEvents.containsKey(userOrigin)) {
			// System.out.println("State updated : " + e);
			UserInteraction replayed = replayNodeToEvents.get(userOrigin);
			if (replayEvents.containsKey(replayed)) {
				System.out.println("/// ADDED \\\\\\");
				replayEvents.get(replayed).add((SystemEvent) e);
			}
		}
	}

	public void checkSystemEvents(InteractionCycle node) throws InvalidRecorderStateException {
		// List<GinaEvent> l = new LinkedList<GinaEvent>();
		// l.addAll(node.getSystemEventTree());
		// l.add(node.getUserInteraction());

		List<GinaEvent> l = new LinkedList<GinaEvent>();
		if (!replayEvents.containsKey(node.getUserInteraction())) {
			return;
		}
		l.addAll(replayEvents.get(node.getUserInteraction()));

		System.out.println("1 : " + l);
		// System.out.println("2 : " + l);

		for (GinaEvent e : l) {
			GinaEvent matching = findSystemEvent(l, e);

			System.out.println("/// Found \\\\\\" + matching);

			if (matching == null) {
				throw new InvalidRecorderStateException("No matching state", e);
			}

			// e.checkMatchingEvent(matching);
		}
	}

	protected GinaEvent findSystemEvent(List<GinaEvent> events, GinaEvent target) {
		for (GinaEvent e : events) {
			if (e.matchesIdentity(target))
				return e;
		}

		return null;
	}

	public boolean check() {
		return false;
	}

}

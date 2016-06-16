package org.openflexo.gina.event.strategies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.manager.GinaStackEvent;

/**
 * This class manage the way a replayed scenario should be checked.
 * The default behavior is :
 *   - expect any recorded user interaction and system response to be present
 *   - expect any of them to match exactly the recorded data
 *   - any additional system response will be ignored and will not cause any error
 *   
 * This class should be extended to adapt the behavior.
 * An example of behavior could be found in StrictCheckingStrategy.
 * 
 * Any event played or recorded will be stored here in order to find matching events between
 * recorded data and replayed ones.
 * 
 * @author Alexandre
 *
 */
public abstract class CheckingStrategy {
	
	protected UserInteraction lastEventReplayed;
	protected GinaReplaySession session;
	protected Map<UserInteraction, List<SystemEvent>> replayEvents;
	protected Map<GinaEvent, UserInteraction> replayNodeToEvents;
	
	public CheckingStrategy(GinaReplaySession session) {
		super();
		this.session = session;
		this.replayEvents = new HashMap<UserInteraction, List<SystemEvent>>();
		this.replayNodeToEvents = new HashMap<GinaEvent, UserInteraction>();
	}
	
	/**
	 * Records a played event
	 * @param e Event played
	 */
	public void eventPlayed(UserInteraction e) {
		if (replayEvents.containsKey(e))
			return;
		
		//System.out.println("[Replay] : " + e);
		replayEvents.put(e, new LinkedList<SystemEvent>());
		lastEventReplayed = e;
	}
	
	/**
	 * Records a replayed event that can be a simulated user interaction or a system response to a
	 * stimulus.
	 * 
	 * @param e
	 * @param stack
	 */
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		GinaEvent origin = session.getEventOrigin(e, stack);
		GinaEvent userOrigin = session.getEventUserOrigin(e, stack);
		
		if (lastEventReplayed != null) {
			//System.out.println("Replay : " + e + ", origin : " + origin + ", userOrigin : " + userOrigin);
			replayNodeToEvents.put(e, lastEventReplayed);
			lastEventReplayed = null;
			return;
		}
		/*System.out.println("Response : " + e + ", origin : " + origin + ", userOrigin : " + userOrigin);
		if (e.getDescription() instanceof FIBSelectionEventDescription) {
			FIBSelectionEventDescription fe = (FIBSelectionEventDescription) e.getDescription();
			System.out.println(fe.getLead());
			for(DescriptionItem di : fe.getValues())
				System.out.println(di);
		}*/
		
		// add as event or state depending of its origin
		if (e.getKind() != KIND.USER_INTERACTION && replayNodeToEvents.containsKey(userOrigin)) {
			//System.out.println("State updated : " + e);
			UserInteraction replayed = replayNodeToEvents.get(userOrigin);
			if (replayEvents.containsKey(replayed)) {
				//System.out.println("/// ADDED \\\\\\");
				replayEvents.get(replayed).add((SystemEvent) e);
			}
		}
	}
	
	protected GinaEvent findSystemEvent(List<GinaEvent> events, GinaEvent target) {
		for(GinaEvent e : events) {
			if (e.matchesIdentity(target))
				return e;
		}
		
		return null;
	}
	
	/**
	 * Checks if an InteractionCycle has a valid system response.
	 * This method should be override in order to adapt the strategy behavior.
	 * 
	 * @param node
	 * @throws InvalidRecorderStateException
	 */
	public abstract void checkSystemEvents(InteractionCycle node) throws InvalidRecorderStateException;

}

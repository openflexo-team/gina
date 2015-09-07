package org.openflexo.gina.event.strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.replay.Scenario;

public class CheckingStrategy {
	
	private Scenario scenario;
	private GinaReplayManager manager;
	private Map<InteractionCycle, InteractionCycle> replayEvents;
	
	public CheckingStrategy(Scenario scenario, GinaReplayManager manager) {
		super();
		this.scenario = scenario;
		this.manager = manager;
		this.replayEvents = new HashMap<InteractionCycle, InteractionCycle>();
	}
	
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		
	}

	public boolean check() {
		return false;
	}

}

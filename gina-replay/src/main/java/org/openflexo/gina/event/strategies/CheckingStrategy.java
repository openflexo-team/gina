package org.openflexo.gina.event.strategies;

import java.util.HashMap;
import java.util.Map;

import org.openflexo.replay.InteractionCycle;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.manager.GinaManager;
import org.openflexo.replay.Scenario;

public class CheckingStrategy {
	
	private Scenario scenario;
	private GinaManager manager;
	private Map<InteractionCycle, InteractionCycle> replayEvents;
	
	public CheckingStrategy(Scenario scenario, GinaManager manager) {
		super();
		this.scenario = scenario;
		this.manager = manager;
		this.replayEvents = new HashMap<InteractionCycle, InteractionCycle>();
	}
	
	public void eventPerformed(GinaEvent e) {
		
	}

	public boolean check() {
		return false;
	}

}

package org.openflexo.gina.event.strategies;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.SystemEventTreeNode;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.replay.Scenario;
import org.openflexo.replay.ScenarioNode;

public class RecordingStrategy {
	
	private Scenario scenario;
	private GinaReplayManager manager;

	public RecordingStrategy(Scenario scenario, GinaReplayManager manager) {
		super();
		this.scenario = scenario;
		this.manager = manager;
	}

	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		GinaEvent origin = null, userOrigin = null;

		if (stack.size() > 1) {
			origin = stack.get(stack.size() - 2).getEvent();
			userOrigin = stack.firstElement().getEvent();
		}
		
		// change state list
		if (e.getKind() == KIND.USER_INTERACTION) {
			System.out.println("    User Interaction : " + e);
			//lastNonUserInteractions.clear();
		}
		else {
			System.out.println("Non User Interaction : " + e);
			//lastNonUserInteractions.add(e);

			/*Stack<GinaStackEvent> stack = manager.getEventStack();
			for(int i = stack.size() - 1; i >= 0; --i) {
				GinaStackEvent ev = stack.get(i);
				if (ev.getEvent().isUserInteraction()) {
					origin = ev.getEvent();
					break;
				}
				//System.out.println(ev.toString());
			}*/

			/*if (origin != null)
				System.out.println("Origin : " + origin);*/
		}
		
		if (origin != null)
			System.out.println("        Stack #" + (stack.size() - 1) + " - Origin : " + origin);
		if (userOrigin != null && userOrigin != origin)
			System.out.println("        Stack #1 - User Origin : " + userOrigin);

		//TODO
		//if (e.getWidgetID() == "playButton")
		//	return;
		
		// add as event or state depending of its origin
		if (e.getKind() == KIND.USER_INTERACTION) {
			InteractionCycle node = manager.getModelFactory().newInstance(InteractionCycle.class);
			node.addUserInteraction((UserInteraction) e);

			//System.out.println(e);
			scenario.addNode(node);
		}
		else {
			//System.out.println("State updated : " + e);
			ScenarioNode node = scenario.getNodeByUserInteraction((UserInteraction) userOrigin);
			if (node instanceof InteractionCycle) {
				InteractionCycle ic = (InteractionCycle) node;
				ic.init(manager);

				SystemEventTreeNode treeNode = manager.getModelFactory().newInstance(SystemEventTreeNode.class, (SystemEvent) e);
				System.out.println(treeNode.getSystemEvent());

				//if (ic.getSystemEventTree())
				ic.getSystemEventTree().addChild(treeNode);
			}
		}
	}

}

package org.openflexo.gina.event.strategies;

import java.util.Stack;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.SystemEvent;
import org.openflexo.gina.event.UserInteraction;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.replay.ScenarioNode;
import org.openflexo.replay.SystemEventTreeNode;

public class RecordingStrategy {
	private GinaReplaySession session;

	public RecordingStrategy(GinaReplaySession session) {
		super();
		this.session = session;
	}

	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		GinaEvent origin = session.getEventOrigin(e, stack);
		GinaEvent userOrigin = session.getEventUserOrigin(e, stack);

		// TODO
		// if (e.getWidgetID() == "playButton")
		// return;

		// add as event or state depending of its origin
		if (e.getKind() == KIND.USER_INTERACTION) {
			InteractionCycle node = session.getManager().getModelFactory().newInstance(InteractionCycle.class);
			node.setUserInteraction((UserInteraction) e);

			System.out.println("    User Interaction : " + e + "\n        origin : " + origin + "\n        userOrigin : " + userOrigin);

			// System.out.println(e);
			session.getScenario().addNode(node);
		}
		else {
			System.out.println("Non User Interaction : " + e + "\n        origin : " + origin + "\n        userOrigin : " + userOrigin);
			// System.out.println("State updated : " + e);
			ScenarioNode node = session.getScenario().getNodeByUserInteraction((UserInteraction) userOrigin);
			if (node instanceof InteractionCycle) {
				InteractionCycle ic = (InteractionCycle) node;
				ic.init(session.getManager());

				SystemEventTreeNode treeNode = session.getManager().getModelFactory().newInstance(SystemEventTreeNode.class,
						(SystemEvent) e);
				System.out.println(treeNode.getSystemEvent());

				// if (ic.getSystemEventTree())
				ic.getSystemEventTree().addChild(treeNode);
			}
		}
	}

}

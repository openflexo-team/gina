package org.openflexo.gina.event;

import java.util.Stack;

import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.GinaTaskEventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.GinaEventFactory;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.manager.GinaTask;
import org.openflexo.gina.manager.Registerable;

public abstract class GinaTaskEventNotifier extends GinaEventNotifier<GinaTaskEventDescription> {
	private Stack<GinaStackEvent> stackEvents;
	private GinaStackEvent gseTask;

	public GinaTaskEventNotifier(EventManager manager, Registerable parent) {
		super(manager, parent);
	}
	
	public void createBranch() {
		if (this.manager != null)
			this.stackEvents = this.manager.createStackCopy();
		System.out.println("New branch = " + this.stackEvents);
	}
	
	public Stack<GinaStackEvent> getStackBranch() {
		return this.stackEvents;
	}
	
	public void switchToStackBranch() {
		System.out.println("STACK" + this.stackEvents);
		//this.manager.setStack(this.stackEvents);
	}
	
	public void resetStackBranch() {
		System.out.println("REVERT" + this.stackEvents);
		//this.manager.resetDefaultStack();
	}
	
	public void startThreadStack(GinaTask task) {
		if (this.manager != null) {
			this.manager.startThreadStack(this.stackEvents);
			
			EventDescription desc = manager.getFactory()
					.createTaskEvent(GinaTaskEventDescription.STARTED, task.getURID().getIdentifier());
			
			gseTask = this.raise((GinaTaskEventDescription) desc);
		}
	}
	
	public void endTask(GinaTask task) {
		
		if (gseTask == null)
			return;
		
		EventDescription desc = manager.getFactory()
				.createTaskEvent(GinaTaskEventDescription.FINISHED, task.getURID().getIdentifier());
		
		this.raise((GinaTaskEventDescription) desc).end();
		
		gseTask.end();

	}

}

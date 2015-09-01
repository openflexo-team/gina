package org.openflexo.gina.event;

import java.util.Stack;

import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.GinaTaskEventDescription;
import org.openflexo.gina.manager.GinaManager;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.manager.GinaTask;

public abstract class GinaTaskEventNotifier extends GinaEventNotifier<GinaTaskEventDescription> {
	private Stack<GinaStackEvent> stackEvents;
	private GinaStackEvent gseTask;

	public GinaTaskEventNotifier(GinaManager manager) {
		super(manager);
	}
	
	public void createBranch() {
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
		this.manager.startThreadStack(this.stackEvents);
		
		EventDescription desc = GinaEventFactory.getInstance()
				.createTaskEvent(GinaTaskEventDescription.STARTED, task.getURID().getIdentifier());
		
		gseTask = this.raise((GinaTaskEventDescription) desc);
	}
	
	public void endTask(GinaTask task) {
		
		if (gseTask == null)
			return;
		
		EventDescription desc = GinaEventFactory.getInstance()
				.createTaskEvent(GinaTaskEventDescription.FINISHED, task.getURID().getIdentifier());
		
		this.raise((GinaTaskEventDescription) desc).end();
		
		gseTask.end();

	}

}

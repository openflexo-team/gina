package org.openflexo.replay.utils;

import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.GinaTaskEventNotifier;
import org.openflexo.gina.event.description.GinaTaskEventDescription;
import org.openflexo.gina.manager.GinaManager;
import org.openflexo.gina.manager.GinaTask;
import org.openflexo.gina.manager.URID;

public abstract class Task implements GinaTask, Runnable {

	private URID urid;
	private String title;

	protected GinaTaskEventNotifier GENotifier;

	public Task(GinaManager manager, String title) {
		this.title = title;
		System.out.println("<<TASK>> created");

		this.GENotifier = new GinaTaskEventNotifier(manager) {

			@Override
			public KIND computeClass(GinaTaskEventDescription d) {
				return KIND.SYSTEM_EVENT;
			}

			@Override
			public void setIdentity(GinaTaskEventDescription d) {
				d.setIdentity(Task.class.getName(),
						Task.this.getTaskTitle());
			}

		};

		this.GENotifier.createBranch();

	}

	@Override
	public URID getURID() {
		if (urid == null)
			urid = this.GENotifier.getManager().generateURID(this);
		return urid;
	}

	@Override
	public String getBaseIdentifier() {
		return "task-" + this.getTaskTitle();
	}

	private String getTaskTitle() {
		return title;
	}

	@Override
	public GinaEventNotifier<GinaTaskEventDescription> getNotifier() {
		return this.GENotifier;
	}

}

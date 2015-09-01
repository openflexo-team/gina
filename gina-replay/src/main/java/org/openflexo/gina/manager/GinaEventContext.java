package org.openflexo.gina.manager;

import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.GinaTaskEventNotifier;

public class GinaEventContext {

	private GinaManager manager;
	private Registerable parent;
	private NotifierRegisterable<?> notifierParent;
	private GinaStackEvent eventParent;
	private GinaEventFilter filter;

	public GinaEventContext(GinaManager manager, Registerable parent, GinaEventFilter filter) {
		this.manager = manager;
		this.parent = parent;
		this.filter = filter;

		this.eventParent = null;
	}

	public GinaEventContext(GinaManager manager, Registerable parent) {
		this(manager, parent, null);
	}
	
	public GinaEventContext(GinaManager manager, NotifierRegisterable<?> parent, GinaEventFilter filter) {
		this(manager, (Registerable)parent, filter);
		
		this.notifierParent = parent;
	}
	
	public GinaEventContext(GinaManager manager, NotifierRegisterable<?> parent) {
		this(manager, parent, null);
	}

	public GinaEventFilter getFilter() {
		return filter;
	}

	public void setFilter(GinaEventFilter filter) {
		this.filter = filter;
	}

	public void open() {
		this.manager.register(this);
		if (this.filter != null)
			this.filter.lock(manager);
		else
			this.manager.getLock().lock();
		if (this.notifierParent != null) {
			GinaEventNotifier<?> gen = this.notifierParent.getNotifier();
			if (gen instanceof GinaTaskEventNotifier)
				((GinaTaskEventNotifier) gen).switchToStackBranch();
		}
	}

	public void close() {
		if (this.filter != null)
			this.filter.unlock();
		else
			this.manager.getLock().unlock();
		this.manager.unregister(this);
		this.manager.resetDefaultStack();
	}

	public GinaManager getManager() {
		return manager;
	}

	public Registerable getParent() {
		return parent;
	}

	public GinaStackEvent getEventParent() {
		return eventParent;
	}
}

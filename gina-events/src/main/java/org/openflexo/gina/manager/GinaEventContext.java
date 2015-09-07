package org.openflexo.gina.manager;

import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.GinaTaskEventNotifier;

public class GinaEventContext {

	private EventManager manager;
	private HasEventNotifier<?> notifierParent;
	private GinaStackEvent eventParent;
	private GinaEventFilter filter;

	/*public GinaEventContext(GinaManager manager, Registerable parent, GinaEventFilter filter) {
		this.manager = manager;
		this.parent = parent;
		this.filter = filter;

		this.eventParent = null;
	}

	public GinaEventContext(GinaManager manager, Registerable parent) {
		this(manager, parent, null);
	}*/
	
	public GinaEventContext(EventManager manager, HasEventNotifier<?> parent, GinaEventFilter filter) {
		this.manager = manager;
		this.eventParent = null;
		this.filter = filter;
		
		this.notifierParent = parent;
	}
	
	public GinaEventContext(EventManager manager, HasEventNotifier<?> parent) {
		this(manager, parent, null);
	}

	public GinaEventFilter getFilter() {
		return filter;
	}

	public void setFilter(GinaEventFilter filter) {
		this.filter = filter;
	}

	public void open() {
		if (this.manager != null) {
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
	}

	public void close() {
		if (this.manager != null) {
			if (this.filter != null)
				this.filter.unlock();
			else
				this.manager.getLock().unlock();
			this.manager.unregister(this);
			this.manager.resetDefaultStack();
		}
	}

	public EventManager getManager() {
		return manager;
	}
	
	public HasEventNotifier<?> getParent() {
		return notifierParent;
	}

	public GinaStackEvent getEventParent() {
		return eventParent;
	}
}

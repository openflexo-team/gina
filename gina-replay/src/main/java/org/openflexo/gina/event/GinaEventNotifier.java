package org.openflexo.gina.event;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.GinaNotifyMethodEventDescription;
import org.openflexo.gina.manager.GinaEventListener;
import org.openflexo.gina.manager.GinaManager;
import org.openflexo.gina.manager.GinaStackEvent;

public abstract class GinaEventNotifier<D extends EventDescription> {
	private List<GinaEventListener> ginaListeners = new ArrayList<GinaEventListener>();
	
	protected GinaManager manager;

	public GinaEventNotifier(GinaManager manager) {
		this.manager = manager;
		if (this.manager != null)
			this.addListener(this.manager);
	}

	public GinaManager getManager() {
		return manager;
	}

	public void addListener(GinaEventListener l) {
		ginaListeners.add(l);
	}

	public void removeListener(GinaEventListener l) {
		ginaListeners.remove(l);
	}

	public void clearListeners() {
		ginaListeners.clear();
	}

	public GinaStackEvent raise(D d) {
		if (this.manager == null)
			return new GinaStackEvent();

		this.setIdentity(d);

		//this.findBranchAncestor();

		// create the event and the stack element
		GinaStackEvent se = manager.pushStackEvent(d, this.computeClass(d));

		for (GinaEventListener fl : ginaListeners)
			fl.eventPerformed(se.getEvent());

		return se;
	}
	
	/*public void findBranchAncestor() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for(int i = 0; i < stack.length; ++i) {
			StackTraceElement trace = stack[i];
			System.out.println("> " + trace.getMethodName() + trace.getClassName());
			if (trace.getClass().isAssignableFrom(GinaTaskTemp.class))
				System.out.println("Hi !" + trace.getMethodName());
		}
	}*/
	
	public GinaStackEvent notifyMethod() {
		return notifyMethod(null);
	}

	public GinaStackEvent notifyMethod(String info) {
		if (this.manager == null)
			return new GinaStackEvent();

		StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
		// System.out.println("[Notify] " + trace.getMethodName() + " in class "
		// + trace.getClassName());

		EventDescription desc = GinaEventFactory.getInstance()
				.createNotifiyMethodEvent(GinaNotifyMethodEventDescription.NOTIFIED,
						trace.getClassName(), trace.getMethodName(), info);
		
		// create the event and the stack element
		GinaStackEvent se = manager.pushStackEvent(desc, GinaEvent.KIND.UNKNOWN);

		for (GinaEventListener fl : ginaListeners)
			fl.eventPerformed(se.getEvent());

		return se;
	}

	public abstract GinaEvent.KIND computeClass(D d);

	public abstract void setIdentity(D d);

}

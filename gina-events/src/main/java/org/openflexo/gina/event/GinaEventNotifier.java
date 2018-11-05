package org.openflexo.gina.event;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.NotifyMethodEventDescription;
import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.GinaEventListener;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.manager.Registrable;

/**
 * An GineEventNotifier is used to throw events using the method raise. It requires an EventManager to send events to and a Registerable
 * parent to identify the origin.
 * 
 * @author Alexandre
 *
 * @param <D>
 *            EventDescription root class
 */
public abstract class GinaEventNotifier<D extends EventDescription> {
	private List<GinaEventListener> ginaListeners = new ArrayList<>();

	protected EventManager manager;
	protected Registrable parent;

	public GinaEventNotifier(EventManager manager, Registrable parent) {
		this.parent = parent;
		this.manager = manager;
		if (this.manager != null)
			this.addListener(this.manager);
	}

	public void setManager(EventManager manager) {
		if (this.manager != null)
			this.removeListener(this.manager);
		this.manager = manager;
		if (this.manager != null)
			this.addListener(this.manager);
	}

	public EventManager getManager() {
		return manager;
	}

	public void addListener(GinaEventListener l) {
		if (!ginaListeners.contains(l))
			ginaListeners.add(l);
	}

	public void removeListener(GinaEventListener l) {
		ginaListeners.remove(l);
	}

	public void clearListeners() {
		ginaListeners.clear();
	}

	public GinaStackEvent raise(D d) {
		return raise(d, null);
	}

	/**
	 * This throw a GinaEvent to the EventManager and also create a stack of events to manage causality. Please remember to call
	 * ginaStackEvent.end() before unreferencing it.
	 */
	public GinaStackEvent raise(D d, Object o) {
		if (this.manager == null)
			return new GinaStackEvent();

		d.setParentIdentifier(parent.getURID().getIdentifier());
		try {
			this.setIdentity(d, o);
		} catch (MissingIdentityParameterException e) {
			System.out.println("Attention : " + e.getMessage());
		}

		// this.findBranchAncestor();

		// create the event and the stack element
		GinaStackEvent se = manager.pushStackEvent(d, this.computeClass(d));

		for (GinaEventListener fl : ginaListeners)
			fl.eventPerformed(se.getEvent(), manager.getEventStack());

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

	/**
	 * Used for debug, to notify something append in a method.
	 */
	public GinaStackEvent notifyMethod() {
		return notifyMethod(null);
	}

	public GinaStackEvent notifyMethod(String info) {
		if (this.manager == null)
			return new GinaStackEvent();

		StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
		// System.out.println("[Notify] " + trace.getMethodName() + " in class "
		// + trace.getClassName());

		EventDescription desc = manager.getFactory().createNotifiyMethodEvent(NotifyMethodEventDescription.NOTIFIED, trace.getClassName(),
				trace.getMethodName(), info);

		// create the event and the stack element
		GinaStackEvent se = manager.pushStackEvent(desc, GinaEvent.KIND.UNKNOWN);

		for (GinaEventListener fl : ginaListeners)
			fl.eventPerformed(se.getEvent(), manager.getEventStack());

		return se;
	}

	/**
	 * This method is used to determine if an action is an User Interaction or a System Event.
	 * 
	 * @param d
	 *            EventDescription
	 */
	public abstract GinaEvent.KIND computeClass(D d);

	/**
	 * This method is used to set the identity of the event emitter, using the object o
	 */
	public abstract void setIdentity(D d, Object o) throws MissingIdentityParameterException;

}

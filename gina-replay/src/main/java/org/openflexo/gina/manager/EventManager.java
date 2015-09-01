package org.openflexo.gina.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.tuple.Pair;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.replay.Scenario;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class EventManager implements GinaEventListener {
	private List<GinaEventListener> listeners = new ArrayList<GinaEventListener>();

	private Map<URID, Registerable> registry;
	private Map<String, Integer> uridSequences;
	private Map<Long, ThreadStack> threadStackEvents;
	private List<GinaEventContext> contexts;
	private Map<Object, Pair<Lock, Integer>> locks;
	private Lock globalLock, headLock;
	
	private GinaEventFactory factory;
	
	public EventManager() {
		this.registry = new HashMap<URID, Registerable>();
		this.uridSequences = new HashMap<String, Integer>();
		this.threadStackEvents = new HashMap<Long, ThreadStack>();
		
		this.contexts = new LinkedList<GinaEventContext>();

		this.globalLock = new ReentrantLock();
		this.headLock = new ReentrantLock();
		this.locks = new HashMap<Object, Pair<Lock, Integer>>();
		
		factory = new GinaEventFactory();
		factory.addModel(EventDescription.class);
		factory.addModel(GinaEvent.class);
	}
	
	public GinaEventFactory getFactory() {
		return factory;
	}
	
	public ModelFactory getModelFactory() {
		return getFactory().getModelFactory();
	}

	public Lock getLock() {
		return globalLock;
	}
	
	public void addListener(GinaEventListener l) {
		if (l != this && !listeners.contains(l))
			listeners.add(l);
	}

	public void removeListener(GinaEventListener l) {
		listeners.remove(l);
	}

	public void clearListeners() {
		listeners.clear();
	}
	
	public synchronized Lock registerLock(Object watch) {
		headLock.lock();

		Lock l;
		if (this.locks.containsKey(watch)) {
			Pair<Lock, Integer> p = this.locks.get(watch);
			l = p.getLeft();
			
			this.locks.put(watch, Pair.of(l, p.getValue() + 1));
		}
		else {
			l = new ReentrantLock();
			this.locks.put(watch, Pair.of(l, 0));
		}
		
		headLock.unlock();
		return l;
	}
	
	public synchronized void unregisterLock(Object watch) {
		headLock.lock();
		
		if (this.locks.containsKey(watch)) {
			Pair<Lock, Integer> p = this.locks.get(watch);
			int n = p.getValue() - 1;
			if (n == 0)
				this.locks.remove(watch);
			else
				this.locks.put(watch, Pair.of(p.getLeft(), n));
		}
		
		headLock.unlock();
	}
	
	public synchronized GinaEventContext findContext(EventDescription d) {
		headLock.lock();

		GinaEventContext c;
		if (this.contexts.isEmpty()) 
			c = null;
		else
			c = this.contexts.get(0);
		
		headLock.unlock();
		return c;
	}
	
	public synchronized void register(GinaEventContext context) {
		headLock.lock();

		this.contexts.add(context);

		headLock.unlock();
	}
	
	public synchronized void unregister(GinaEventContext context) {
		headLock.lock();

		this.contexts.remove(context);

		headLock.unlock();
	}
	
	public URID register(Registerable obj) {
		if (obj.getURID() != null)
			return obj.getURID();
		
		URID urid = generateURID(obj);
		
		System.out.println("Registering " + urid);
		
		this.registry.put(urid, obj);
		obj.setURID(urid);
		
		return urid;
	}

	public void unregister(Registerable obj) {
		System.out.println("Unregistering " + obj.getURID());
		obj.setURID(null);
		
		this.registry.remove(obj.getURID());
	}

	public URID generateURID(Registerable obj) {
		String base = obj.getBaseIdentifier();
		int sequence = 0;
		
		if (this.uridSequences.containsKey(base)) {
			sequence = this.uridSequences.get(base);
			this.uridSequences.put(base, sequence + 1);
		}
		else {
			this.uridSequences.put(base, 1);
		}

		URID urid = new SequentialURID(base, sequence);
		
		return urid;
	}
	
	private ThreadStack getCurrentThreadStack() {
		long threadId = Thread.currentThread().getId();
		if (!threadStackEvents.containsKey(threadId))
			threadStackEvents.put(threadId, new ThreadStack(threadId));
		return threadStackEvents.get(threadId);
	}
	
	@SuppressWarnings("unchecked")
	public Stack<GinaStackEvent> createStackCopy() {
		ThreadStack ts = getCurrentThreadStack();
		return (Stack<GinaStackEvent>) ts.getStack().clone();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Stack<GinaStackEvent> getEventStack() {
		ThreadStack ts = getCurrentThreadStack();
		return (Stack<GinaStackEvent>) ts.getStack().clone();
	}
	
	public void setStack(Stack<GinaStackEvent> stack) {
		getCurrentThreadStack().setStack(stack);
	}
	
	public void resetDefaultStack() {
		getCurrentThreadStack().resetDefaultStack();
	}
	
	public void startThreadStack(Stack<GinaStackEvent> stackEvents) {
		long threadId = Thread.currentThread().getId();
		if (threadStackEvents.containsKey(threadId))
			return;

		threadStackEvents.put(threadId, new ThreadStack(threadId, stackEvents));
	}
	
	public GinaStackEvent pushStackEvent(EventDescription d, KIND kind) {
		ThreadStack ts = getCurrentThreadStack();

		GinaEventContext context = this.findContext(d);
		
		// check if the stack is not empty
		// it means this is not a user interaction but a consequence
		if (!ts.getStack().isEmpty())
			kind = KIND.SYSTEM_EVENT;
		else if (context != null) {
			System.out.println("Context | " + context);
		}
		
		GinaEvent e = getFactory().createEventFromDescription(d, kind);
		
		System.out.println("IN 1" + ts.getStack());
		GinaStackEvent stack = new GinaStackEvent(e, this);
		ts.getStack().push(stack);
		System.out.println("IN 2" + ts.getStack());
		
		return stack;
	}
	
	public synchronized void popStackEvent(GinaStackEvent e) {
		getCurrentThreadStack().popStackEvent(e);
	}

	@Override
	public void eventPerformed(GinaEvent e, Stack<GinaStackEvent> stack) {
		for (GinaEventListener fl : listeners)
			fl.eventPerformed(e, stack);
	}
	
	public void trackPropertyChange(HasPropertyChangeSupport obj) {
		GinaPropertyChangeListener listener = new GinaPropertyChangeListener(this);
		
		obj.getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	
	/**
	 * This class represents a stack of the GinaEvents for a given thread.
	 * 
	 * @author Alexandre
	 *
	 */
	public class ThreadStack {
		private long threadId;
		private Stack<GinaStackEvent> stackEvents, previousStackEvents;
		
		public ThreadStack(long threadId) {
			super();
			this.threadId = threadId;
			this.stackEvents = new Stack<GinaStackEvent>();
			this.previousStackEvents = null;
		}

		public Stack<GinaStackEvent> getStack() {
			return this.stackEvents;
		}

		public ThreadStack(long threadId, Stack<GinaStackEvent> stackEvents) {
			super();
			this.threadId = threadId;
			this.stackEvents = stackEvents;
			this.previousStackEvents = null;
		}

		public long getThreadId() {
			return threadId;
		}

		public void setStack(Stack<GinaStackEvent> stack) {
			if (previousStackEvents == null)
				previousStackEvents = stackEvents;
			stackEvents = stack;
		}
		
		public void resetDefaultStack() {
			if (previousStackEvents != null)
				stackEvents = previousStackEvents;
			previousStackEvents = null;
		}
		
		public synchronized void popStackEvent(GinaStackEvent e) {
			System.out.println("POP" + stackEvents);
			if (!stackEvents.contains(e))
				return;
			
			while(stackEvents.pop() != e);
		}
	}
}

package org.openflexo.gina.manager;

import java.lang.reflect.Method;
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
	
	private long startTime;
	
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
		
		startTime = System.currentTimeMillis();
	}
	
	public long getSpentTime() {
		return System.currentTimeMillis() - startTime;
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
	
	public Registerable find(String identifier) {
		for(Map.Entry<URID, Registerable> r : this.registry.entrySet()) {
			if (r.getKey().getIdentifier().equals(identifier)) {
				return r.getValue();
			}
		}
		
		return null;
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
	
	public synchronized void startThreadStack(GinaTask task, Stack<GinaStackEvent> stackEvents) {
		long threadId = Thread.currentThread().getId();
		if (!threadStackEvents.containsKey(threadId))
			threadStackEvents.put(threadId, new ThreadStack(threadId, stackEvents));

		threadStackEvents.get(threadId).addLinkedTask(task);
	}
	
	public synchronized void endTask(GinaTask task) {
		long threadId = Thread.currentThread().getId();
		if (threadStackEvents.containsKey(threadId)) {
			threadStackEvents.get(threadId).removeLinkedTask(task);
			if (threadStackEvents.get(threadId).finished())
				threadStackEvents.remove(threadId);
		}
	}
	
	public GinaStackEvent pushStackEvent(EventDescription d, KIND kind) {
		d.setDelay((int) getSpentTime());

		ThreadStack ts = getCurrentThreadStack();

		GinaEventContext context = this.findContext(d);
		
		// check if the stack is not empty
		// it means this is not a user interaction but a consequence
		if (!ts.getStack().isEmpty())
			kind = KIND.SYSTEM_EVENT;
		else if (context != null) {
			System.out.println("Context | " + context);
		}
		
		if (kind == KIND.UNKNOWN && ts.getStack().isEmpty())
			kind = KIND.USER_INTERACTION;

		GinaEvent e = getFactory().createEventFromDescription(d, kind);

		//System.out.println("IN 1" + ts.getStack());
		GinaStackEvent stack = new GinaStackEvent(e, this);
		ts.getStack().push(stack);
		//System.out.println("IN 2" + ts.getStack());
		
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
		private List<GinaTask> linkedTasks;
		private Stack<GinaStackEvent> stackEvents, previousStackEvents;
		
		public ThreadStack(long threadId) {
			super();
			this.linkedTasks = new LinkedList<GinaTask>();
			this.stackEvents = new Stack<GinaStackEvent>();
			this.previousStackEvents = null;
			this.threadId = threadId;
		}
		
		public ThreadStack(long threadId, Stack<GinaStackEvent> stackEvents) {
			super();
			this.linkedTasks = new LinkedList<GinaTask>();
			this.threadId = threadId;
			this.stackEvents = stackEvents;
			this.previousStackEvents = null;
		}

		public synchronized boolean finished() {
			return this.linkedTasks.isEmpty();
		}

		public synchronized void addLinkedTask(GinaTask task) {
			this.linkedTasks.add(task);
		}
		
		public synchronized void removeLinkedTask(GinaTask task) {
			this.linkedTasks.remove(task);
		}

		public synchronized Stack<GinaStackEvent> getStack() {
			return this.stackEvents;
		}

		public long getThreadId() {
			return threadId;
		}

		public synchronized void setStack(Stack<GinaStackEvent> stack) {
			if (previousStackEvents == null)
				previousStackEvents = stackEvents;
			stackEvents = stack;
		}
		
		public synchronized void resetDefaultStack() {
			if (previousStackEvents != null)
				stackEvents = previousStackEvents;
			previousStackEvents = null;
		}
		
		public synchronized void popStackEvent(GinaStackEvent e) {
			if (stackEvents.contains(e))
				while(stackEvents.pop() != e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Object getObjectValue(String value, String classValue) {
		
		Class<?> cls;
		try {
			cls = Class.forName(classValue);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		if (String.class == cls)
			return value;
		
		if( Boolean.class == cls ) return Boolean.parseBoolean( value );
	    if( Byte.class == cls ) return Byte.parseByte( value );
	    if( Short.class == cls ) return Short.parseShort( value );
	    if( Integer.class == cls ) return Integer.parseInt( value );
	    if( Long.class == cls ) return Long.parseLong( value );
	    if( Float.class == cls ) return Float.parseFloat( value );
	    if( Double.class == cls ) return Double.parseDouble( value );
		
		if (cls.isEnum()) {
			return Enum.valueOf((Class<Enum>)cls, value);
		}
		
		return null;
	}

	public static Class<?> getClassForName(String type) throws ClassNotFoundException {
		try {
			return Class.forName(type);
		} catch (ClassNotFoundException e) {
			return Class.forName("java.lang." + type);
		}
	}

}

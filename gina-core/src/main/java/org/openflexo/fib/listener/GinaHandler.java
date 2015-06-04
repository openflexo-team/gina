package org.openflexo.fib.listener;

import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.gina.event.FIBEventFactory;
import org.openflexo.gina.event.GinaEvent;

public class GinaHandler {
	static private GinaHandler instance;

	private Map<String, Integer> indexes;
	//private Map<String, FIBComponent> components;
	private LinkedList<FIBController> controllers;
	private Map<Long, Stack<GinaStackEvent>> stackEvents;
	
	static public GinaHandler getInstance() {
		if (instance == null)
			instance = new GinaHandler();
		
		return instance;
	}

	private GinaHandler() {
		this.indexes = new HashMap<String, Integer>();
		//this.components = new HashMap<String, FIBComponent>();
		this.controllers = new LinkedList<FIBController>();
		this.stackEvents = new HashMap<Long, Stack<GinaStackEvent>>();
	}

	public String generateUniqueID(FIBComponent component) {
		int index = 0;
		
		String baseName = (component.getBaseName() == null ? "" : component.getBaseName())
				+ ":" + (component.getName() == null ? "" : component.getName())
				+ ":" + (component.getResource() == null ? "" : component.getResource().getRelativePath());
		
		if (indexes.containsKey(baseName)) {
			index = indexes.get(baseName);
		}
		
		String id = baseName + ":" + index;
		
		indexes.put(baseName, ++index);
		
		/*if (component != null)
			register(id, component);*/
		
		return id;
	}

	/*public void register(String id, FIBComponent component) {
		components.put(id, component);
	}*/
	
	public void register(FIBController controller) {
		controllers.add(controller);
		
		controller.actionPerformed(FIBEventFactory.getInstance().createControllerEvent("register"));
	}
	
	public void unregister(FIBController controller) {
		controllers.remove(controller);
		
		controller.actionPerformed(FIBEventFactory.getInstance().createControllerEvent("unregister"));
	}
	
	protected synchronized long getThreadIdAndCreateStack() {
		long threadId = Thread.currentThread().getId();
		if (stackEvents.get(threadId) == null)
			stackEvents.put(threadId, new Stack<GinaStackEvent>());
		
		return threadId;
	}
	
	public synchronized GinaStackEvent pushStackEvent(GinaEvent e) {
		long threadId = getThreadIdAndCreateStack();
		
		GinaStackEvent stack = new GinaStackEvent(e);
		stackEvents.get(threadId).push(stack);
		
		return stack;
	}
	
	public synchronized void popStackEvent(GinaStackEvent e) {
		long threadId = getThreadIdAndCreateStack();
		
		if (!stackEvents.get(threadId).contains(e))
			return;
		
		while(stackEvents.get(threadId).pop() != e);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Stack<GinaStackEvent> getEventStack() {
		long threadId = getThreadIdAndCreateStack();
		return (Stack<GinaStackEvent>) stackEvents.get(threadId).clone();
	}
	
	public synchronized void setTreadStack(long threadId, Stack<GinaStackEvent> callStackEvents) {
		stackEvents.put(threadId, callStackEvents);
	}
	
	public FIBController findControllerByRootComponentID(String componentID) {
		for(FIBController c : controllers) {
			//System.out.println(c.getRootComponent().getID() + "?=" + componentID + (c.getRootComponent().getID().equals(componentID)));
			if (c.getRootComponent().getUniqueID().equals(componentID))
				return c;
		}
		
		return null;
	}
	
	public FIBWidgetView<?, ?, ?> findWidgetViewChildByID(String componentID, String widgetID) {
		FIBController c = findControllerByRootComponentID(componentID);
		if (c == null)
			return null;

		List<FIBView<?, ?, ?>> l = c.getAllViews();
		
		for(FIBView<?, ?, ?> v : l) {
			if (v instanceof FIBWidgetView<?, ?, ?>) {
				FIBWidgetView<?, ?, ?> wv = (FIBWidgetView<?, ?, ?>) v;

				if (wv.getWidget().getName() != null && wv.getWidget().getName().equals(widgetID))
					return wv;
			}
		}
		
		return null;
	}
}

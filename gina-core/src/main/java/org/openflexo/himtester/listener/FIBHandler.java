package org.openflexo.himtester.listener;

import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;

public class FIBHandler {
	static private FIBHandler instance;

	private Map<String, Integer> indexes;
	//private Map<String, FIBComponent> components;
	private LinkedList<FIBController> controllers;
	
	static public FIBHandler getInstance() {
		if (instance == null)
			instance = new FIBHandler();
		
		return instance;
	}

	private FIBHandler() {
		this.indexes = new HashMap<String, Integer>();
		//this.components = new HashMap<String, FIBComponent>();
		this.controllers = new LinkedList<FIBController>();
	}

	public String generateUniqueID(FIBComponent component) {
		int index = 0;
		
		String baseName = (component.getBaseName() == null ? "" : component.getBaseName())
				+ "/" + (component.getName() == null ? "" : component.getName())
				+ "/" + (component.getResource() == null ? "" : component.getResource().getRelativePath());
		
		if (indexes.containsKey(baseName)) {
			index = indexes.get(baseName);
		}
		
		String id = baseName + "/" + index;
		
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
	}
	
	public void unregister(FIBController controller) {
		controllers.remove(controller);
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

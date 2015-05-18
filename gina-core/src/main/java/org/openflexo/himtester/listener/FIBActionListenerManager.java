package org.openflexo.himtester.listener;

import java.util.LinkedList;

import org.openflexo.himtester.events.FIBActionEvent;

public class FIBActionListenerManager implements FIBActionListener {
	static private FIBActionListenerManager instance;
	
	private boolean listenModEnabled;
	private LinkedList<FIBActionListener> listeners;
	//private FIBEventFactory factory;
	
	static public FIBActionListenerManager getInstance() {
		if (instance == null)
			instance = new FIBActionListenerManager();
		
		return instance;
	}
	
	/*public static FIBEventFactory getInstanceFactory() {
		return getInstance().getFactory();
	}*/


	private FIBActionListenerManager() {
		this.listenModEnabled = false;
		this.listeners = new LinkedList<FIBActionListener>();
		//this.factory = new FIBEventFactory();
	}

	public boolean isEnabled() {
		return listenModEnabled;
	}

	public void addListener(FIBActionListener l) {
		listeners.add(l);
	}
	
	public void removeListener(FIBActionListener l) {
		listeners.remove(l);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public void addListenerAndEnable(FIBActionListener l) {
		addListener(l);
		enable();
	}
	
	public void enable() {
		listenModEnabled = true;
	}
	
	public void disable() {
		listenModEnabled = false;
	}

	public LinkedList<FIBActionListener> getListeners() {
		return listeners;
	}
	
	/*public FIBEventFactory getFactory() {
		return factory;
	}*/

	@Override
	public void actionPerformed(FIBActionEvent e) {
		for (FIBActionListener l : listeners)
			l.actionPerformed(e);
	}
}

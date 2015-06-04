package org.openflexo.fib.listener;

import java.util.LinkedList;

import org.openflexo.gina.event.FIBEvent;

public class FIBActionListenerManager implements FIBActionListener {
	static private FIBActionListenerManager instance;
	
	private boolean listenModEnabled;
	private LinkedList<FIBActionListener> listeners;
	
	static public FIBActionListenerManager getInstance() {
		if (instance == null)
			instance = new FIBActionListenerManager();
		
		return instance;
	}


	private FIBActionListenerManager() {
		this.listenModEnabled = false;
		this.listeners = new LinkedList<FIBActionListener>();
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

	@Override
	public void actionPerformed(FIBEvent e) {
		for (FIBActionListener l : listeners)
			l.actionPerformed(e);
	}
}

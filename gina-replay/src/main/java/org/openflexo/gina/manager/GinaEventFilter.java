package org.openflexo.gina.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GinaEventFilter {

	private Map<Object, Lock> locks;
	private GinaManager manager;

	public GinaEventFilter(Object... watches) {
		this.locks = new HashMap<Object, Lock>();
		for(Object o : watches) {
			this.locks.put(o, null);
		}
	}
	
	protected void lock(GinaManager manager) {
		this.manager = manager;
		
		for(Entry<Object, Lock> entry : this.locks.entrySet()) {
			this.locks.put(entry.getKey(), this.manager.registerLock(entry.getKey()));
		}
		
		for(Entry<Object, Lock> entry : this.locks.entrySet()) {
			if (entry.getValue() != null)
				entry.getValue().lock();
		}
	}
	
	protected void unlock() {
		if (this.manager == null)
			return;
		
		for(Entry<Object, Lock> entry : this.locks.entrySet()) {
			if (entry.getValue() != null)
				entry.getValue().unlock();
		}
	
		for(Entry<Object, Lock> entry : this.locks.entrySet()) {
			this.manager.unregisterLock(entry.getKey());
			this.locks.put(entry.getKey(), null);
		}
	}
	
}

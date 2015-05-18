package org.openflexo.himtester;

import org.openflexo.himtester.events.FIBActionEvent;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;


public class FIBRecorderManager {
	static private FIBRecorderManager instance;

	private FIBRecorder recorder;
	private ModelFactory factory;
	private ModelContext context;
	
	static public FIBRecorderManager getInstance() {
		if (instance == null)
			instance = new FIBRecorderManager();
		
		return instance;
	}


	private FIBRecorderManager() {
		this.recorder = null;
		this.factory = null;
		this.context = null;
	}

	public FIBRecorder createAndSetToCurrent() {
		FIBRecorder r = new FIBRecorder();
		
		setCurrentRecorder(r);
		
		return r;
	}

	public void setCurrentRecorder(FIBRecorder r) {
		recorder = r;
	}

	public FIBRecorder getCurrentRecorder() {
		return recorder;
	}
	
	public ModelFactory getFactory() {
		if (factory == null) {
			try {
				context = new ModelContext(FIBActionEvent.class, FIBRecordedNode.class);
				factory = new ModelFactory(context);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		return factory;
	}
}

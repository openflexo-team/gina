package org.openflexo.himtester.events;

import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public class FIBEventFactory {
	static private FIBEventFactory instance;
	private ModelFactory factory;
	
	static public FIBEventFactory getInstance() {
		if (instance == null)
			instance = new FIBEventFactory();
		
		return instance;
	}


	private FIBEventFactory() {
		super();
		this.factory = null;
	}

	public ModelFactory getModelFactory() {
		if (factory == null) {
			try {
				factory = new ModelFactory(FIBActionEvent.class);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		return factory;
	}
	
	public FIBActionEvent createActionEvent(String widgetClass, String widgetID, String component, String action) {
		return getModelFactory().newInstance(FIBActionEvent.class, widgetClass, widgetID, component, action);
	}
	
	public FIBMouseEvent createMouseEvent(String action) {
		return getModelFactory().newInstance(FIBMouseEvent.class, action);
	}
	
	public FIBControllerEvent createControllerEvent(String action) {
		return getModelFactory().newInstance(FIBControllerEvent.class, action);
	}
	
	public FIBTextEvent createTextEvent(String action, int position, int length, String value, String absoluteValue) {
		return getModelFactory().newInstance(FIBTextEvent.class, action, position, length, value, absoluteValue);
	}
	
	public FIBFocusEvent createFocusEvent(String action) {
		return getModelFactory().newInstance(FIBFocusEvent.class, action);
	}
	
	public <T> FIBChangeValueEvent createChangeValueEvent(String action, T value) {
		return getModelFactory().newInstance(FIBChangeValueEvent.class, action, String.valueOf(value));
	}
}

package org.openflexo.gina.event.description;

import java.util.Arrays;
import javax.swing.*;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.description.item.DescriptionIntegerItem;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public class FIBEventFactory {
	static private FIBEventFactory instance;
	private ModelFactory factory;
	private ModelContext context;
	
	static public FIBEventFactory getInstance() {
		if (instance == null)
			instance = new FIBEventFactory();
		
		return instance;
	}


	private FIBEventFactory() {
		super();
		this.factory = null;
		this.context = null;
	}

	public ModelFactory getModelFactory() {
		if (factory == null) {
			try {
				context = new ModelContext(Arrays.asList(GinaEvent.class, EventDescription.class, DescriptionItem.class, FIBEventDescription.class));
				factory = new ModelFactory(context);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		return factory;
	}
	
	public FIBEventDescription createFibEvent(String widgetClass, String widgetID, String component, String action) {
		return getModelFactory().newInstance(FIBEventDescription.class, widgetClass, widgetID, component, action);
	}
	
	public FIBMouseEventDescription createMouseEvent(String action) {
		return getModelFactory().newInstance(FIBMouseEventDescription.class, action);
	}
	
	public FIBControllerEventDescription createControllerEvent(String action) {
		return getModelFactory().newInstance(FIBControllerEventDescription.class, action);
	}
	
	public FIBTextEventDescription createTextEvent(String action, int position, int length, String value, String absoluteValue) {
		return getModelFactory().newInstance(FIBTextEventDescription.class, action, position, length, value, absoluteValue);
	}
	
	public FIBFocusEventDescription createFocusEvent(String action) {
		return getModelFactory().newInstance(FIBFocusEventDescription.class, action);
	}
	
	public <T> FIBValueEventDescription createValueEvent(String action, T value) {
		return getModelFactory().newInstance(FIBValueEventDescription.class, action, String.valueOf(value));
	}
	
	public <T> FIBTableEventDescription createTableEvent(String action, T value, String classValue, int row, int col) {
		return getModelFactory().newInstance(FIBTableEventDescription.class, action, String.valueOf(value), classValue, row, col);
	}
	
	public FIBSelectionEventDescription createSelectionEvent(ListSelectionModel selection, int lead, int min, int max) {
		FIBSelectionEventDescription e = getModelFactory().newInstance(FIBSelectionEventDescription.class, FIBSelectionEventDescription.SELECTED, lead);
		
		for(int i = min; i <= max; ++i)
			e.addValue(getModelFactory().newInstance(DescriptionIntegerItem.class,
					(selection.isSelectedIndex(i) ? FIBSelectionEventDescription.SELECTED : FIBSelectionEventDescription.DESELECTED), i));
		
		return e;
	}

	public FIBEventDescription createEvent() {
		return getModelFactory().newInstance(FIBEventDescription.class);
	}

}

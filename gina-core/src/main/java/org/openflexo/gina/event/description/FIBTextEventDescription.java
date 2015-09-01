package org.openflexo.gina.event.description;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
//@ImplementationClass(FIBTextEvent.FIBTextFieldEventImpl.class)
@XMLElement(xmlTag = "TextEvent")
public abstract interface FIBTextEventDescription extends FIBEventDescription {
	public static final String INSERTED = "text-inserted";
	public static final String REMOVED = "text-removed";

	@PropertyIdentifier(type = Integer.class)
	public static final String POSITION = "position";
	
	@PropertyIdentifier(type = Integer.class)
	public static final String LENGTH = "length";


	@Initializer
	public FIBTextEventDescription init(@Parameter(ACTION) String action, @Parameter(POSITION) Integer position, @Parameter(LENGTH) Integer size,
			@Parameter(VALUE) String value, @Parameter(ABSOLUTE_VALUE) String absoluteValue);


	@Getter(value = POSITION, defaultValue = "0")
	@XMLAttribute
	public int getPosition();

	@Setter(POSITION)
	public void setLength(int length);
	
	@Getter(value = LENGTH, defaultValue = "0")
	@XMLAttribute
	public int getLength();

	@Setter(LENGTH)
	public void setPosition(int position);


	/*public abstract class FIBTextFieldEventImpl extends FIBEventImpl implements FIBTextEvent {
		public boolean matchesState(GinaEvent e) throws InvalidRecorderStateException {
			if (!(e instanceof FIBTextEvent))
				return super.matchesState(e);
			
			FIBEvent fe = (FIBEvent) e;
			FIBWidgetView<?, ?, ?> wv = GinaHandler.getInstance().findWidgetViewChildByID(fe.getComponent(), fe.getWidgetID());

			switch(fe.getAction()) {
			case "text-inserted":
			case "test-removed":
				FIBTextFieldWidget wb1 = (FIBTextFieldWidget) wv;
				if (!wb1.getValue().equals(e.getAbsoluteValue()))
					throw new InvalidRecorderStateException("Value is not identical : "
				+ wb1.getValue() + " != " + e.getAbsoluteValue());
				break;
			}
			
			return true;
		}

	}*/
}

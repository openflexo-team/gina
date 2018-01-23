/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.gina.view.widget.impl;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBNumberWidget;

/**
 * Base implementation for a widget able to edit a number
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 *
 * @author sylvain
 */
public abstract class FIBNumberWidgetImpl<C, T extends Number> extends FIBWidgetViewImpl<FIBNumber, C, T> implements FIBNumberWidget<C, T> {

	static final Logger logger = Logger.getLogger(FIBNumberWidgetImpl.class.getPackage().getName());

	boolean validateOnReturn;
	protected boolean ignoreTextfieldChanges = false;

	/**
	 * @param model
	 */
	public FIBNumberWidgetImpl(FIBNumber model, FIBController controller, NumberWidgetRenderingAdapter<C, T> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		validateOnReturn = model.getValidateOnReturn();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateCheckboxVisibility();
		updateColumns();
	}

	/*@Override
	public boolean update() {
		// System.out.println("Update in FIBNumberWidget, getValue()=" + getValue());
		// System.out.println("getData()=" + getData());
		boolean returned = super.update();
		// System.out.println("getData()=" + getData());
		updateCheckboxVisibility();
		updateColumns();
		// System.out.println("getData()=" + getData());
		return returned;
	}*/

	@Override
	public NumberWidgetRenderingAdapter<C, T> getRenderingAdapter() {
		return (NumberWidgetRenderingAdapter<C, T>) super.getRenderingAdapter();
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
			case FIBValueEventDescription.CHANGED: {
				T value = parseValue(e.getValue());
				System.out.println(value);
				getRenderingAdapter().setNumber(getTechnologyComponent(), value);
				break;
			}
		}

		widgetExecuting = false;
	}

	public T parseValue(String str) {
		if (getWidget().getNumberType() == null) {
			return null;
		}
		else {
			switch (getWidget().getNumberType()) {
				case ByteType:
					return (T) (Byte) Byte.parseByte(str);
				case ShortType:
					return (T) (Short) Short.parseShort(str);
				case IntegerType:
					return (T) (Integer) Integer.parseInt(str);
				case LongType:
					return (T) (Long) Long.parseLong(str);
				case FloatType:
					return (T) (Float) Float.parseFloat(str);
				case DoubleType:
					return (T) (Double) Double.parseDouble(str);
				default:
					return null;
			}
		}
	}

	public T getDefaultValue() {
		/*if (getWidget().getMinValue() != null) {
			return (T) getWidget().getMinValue();
		}*/
		if (getWidget().getNumberType() == null) {
			return null;
		}
		else {
			switch (getWidget().getNumberType()) {
				case ByteType:
					return (T) (Byte.valueOf((byte) 0));
				case ShortType:
					return (T) (Short.valueOf((short) 0));
				case IntegerType:
					return (T) Integer.valueOf(0);
				case LongType:
					return (T) Long.valueOf(0);
				case FloatType:
					return (T) Float.valueOf(0);
				case DoubleType:
					return (T) Double.valueOf(0);
				default:
					return null;
			}
		}
	}

	@Override
	public T updateData() {

		T newValue = super.updateData();

		// logger.info("updateWidgetFromModel() with "+getValue());
		T editedValue = null;
		if (!getRenderingAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedValue = getEditedValue();
		}
		if (notEquals(newValue, editedValue)) {

			getRenderingAdapter().setWidgetEnabled(getTechnologyComponent(),
					(newValue != null || !getWidget().getAllowsNull()) && isEnabled());
			getRenderingAdapter().setCheckboxSelected(getTechnologyComponent(), newValue == null);
			T currentValue = null;

			if (newValue == null) {
				// setValue(getDefaultValue());
				currentValue = getDefaultValue();
			}
			else {
				try {
					currentValue = newValue;
				} catch (ClassCastException e) {
					logger.warning("ClassCastException: " + e.getMessage());
					logger.warning("Data: " + getWidget().getData() + " returned " + newValue);
				}
			}

			ignoreTextfieldChanges = true;
			try {
				getRenderingAdapter().setNumber(getTechnologyComponent(), currentValue);
			} catch (IllegalArgumentException e) {
				logger.warning("IllegalArgumentException: " + e.getMessage());
			}

			ignoreTextfieldChanges = false;

			return currentValue;
		}
		return newValue;
	}

	public T getEditedValue() {
		return getRenderingAdapter().getNumber(getTechnologyComponent());
	}

	public void setEditedValue(T aValue) {
		getRenderingAdapter().setNumber(getTechnologyComponent(), aValue);
	}

	protected boolean valueChanged() {
		T editedValue = null;
		if (!getComponent().getAllowsNull() || !getRenderingAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedValue = getEditedValue();
		}
		if (notEquals(getValue(), editedValue)) {
			if (isReadOnly()) {
				return false;
			}
			// modelUpdating = true;
			setValue(editedValue);
			// modelUpdating = false;
			return true;
		}
		return false;
	}

	final public void updateCheckboxVisibility() {
		getRenderingAdapter().setCheckboxVisible(getTechnologyComponent(),
				getWidget().getAllowsNull() && !TypeUtils.isPrimitive(getComponent().getDataType()));
	}

	public void updateColumns() {
		if (getComponent().getColumns() != null) {
			getRenderingAdapter().setColumns(getTechnologyComponent(), getComponent().getColumns());
		}
		else {
			getRenderingAdapter().setColumns(getTechnologyComponent(), 0);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBNumber.ALLOWS_NULL_KEY)) {
			updateCheckboxVisibility();
		}
		else if (evt.getPropertyName().equals(FIBNumber.COLUMNS_KEY)) {
			updateColumns();
		}
		super.propertyChange(evt);
	}

}

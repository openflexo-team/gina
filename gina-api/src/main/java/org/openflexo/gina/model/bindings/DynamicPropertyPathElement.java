/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina, a component of the software infrastructure 
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

package org.openflexo.gina.model.bindings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBViewType.DynamicProperty;
import org.openflexo.gina.model.FIBWidgetType;

/**
 * Abstract BindingPathElement representing a widget dynamic property<br>
 * 
 * Such path element applies on a {@link BindingPathElement} with relevant {@link FIBWidgetType}<br>
 * Note that type of component referenced by {@link FIBWidgetType} should match W type
 * 
 * @param W
 *            type of widget on which this dynamic property applies
 * 
 * @author sylvain
 *
 */
public class DynamicPropertyPathElement<W extends FIBComponent> extends SimplePathElement<DynamicProperty>
		implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(DynamicPropertyPathElement.class.getPackage().getName());

	private Type lastKnownType = null;
	private final W widget;
	// private final DynamicProperty dynamicProperty;

	public DynamicPropertyPathElement(IBindingPathElement parent, W widget, DynamicProperty dynamicProperty) {
		super(parent, "", Object.class);
		// this.dynamicProperty = dynamicProperty;
		this.widget = widget;
		setProperty(dynamicProperty);
		/*if (dynamicProperty != null) {
			setPropertyName(dynamicProperty.getName());
			setType(dynamicProperty.getType());
			lastKnownType = getType();
		}*/
	}

	@Override
	public void setProperty(DynamicProperty property) {
		super.setProperty(property);
		lastKnownType = getType();
	}

	@Override
	public boolean isNotificationSafe() {
		return false;
	}

	@Override
	public void activate() {
		super.activate();
		if (widget != null && widget.getPropertyChangeSupport() != null) {
			widget.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void desactivate() {
		if (widget != null && widget.getPropertyChangeSupport() != null) {
			widget.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.desactivate();
	}

	public W getWidget() {
		return widget;
	}

	@Override
	public String getLabel() {
		return getProperty().getName();
	}

	@Override
	public Type getType() {
		return getProperty().getType();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return getProperty().getTooltip();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getWidget()) {
			if (lastKnownType != null && !lastKnownType.equals(getType())) {
				// We might arrive here only in the case notification was not properly triggered.
				// We warn it to 'tell' the developper that such notification should be done
				logger.warning("Detecting un-notified type changing for FIBWidget " + widget + " from " + lastKnownType + " to " + getType()
						+ ". Trying to handle case.");
				handleTypeMightHaveChanged();
			}
		}
	}

	protected void handleTypeMightHaveChanged() {
		if (lastKnownType == null || !lastKnownType.equals(getType())) {
			getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, getType());
			getWidget().getBindingModel().getPropertyChangeSupport().firePropertyChange(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED,
					lastKnownType, getType());
			lastKnownType = getType();
		}
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {
		return getProperty().getBindingValue(target, context);
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException {
		getProperty().setBindingValue(value, target, context);
	}

	@Override
	public boolean isResolved() {
		return getProperty() != null;
	}

	@Override
	public void resolve() {
	}

}

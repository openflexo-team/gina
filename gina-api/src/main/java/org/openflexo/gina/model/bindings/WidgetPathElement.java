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

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.FIBWidgetType;
import org.openflexo.localization.FlexoLocalization;

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
public abstract class WidgetPathElement<W extends FIBWidget> extends SimplePathElement implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(WidgetPathElement.class.getPackage().getName());

	private Type lastKnownType = null;
	private final W widget;

	public WidgetPathElement(BindingPathElement parent, W widget) {
		super(parent, "", Object.class);
		this.widget = widget;
		if (widget != null) {
			setPropertyName(getDynamicPropertyName(widget));
			setType(getDynamicPropertyType(widget));
			lastKnownType = getType();
		}
		if (widget != null && widget.getPropertyChangeSupport() != null) {
			widget.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	protected abstract String getDynamicPropertyName(W widget);

	protected abstract Type getDynamicPropertyType(W widget);

	protected abstract String getDynamicPropertyTooltip(W widget);

	@Override
	public void delete() {
		if (widget != null && widget.getPropertyChangeSupport() != null) {
			widget.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}

	public W getWidget() {
		return widget;
	}

	@Override
	public String getLabel() {
		return getDynamicPropertyName(widget);
	}

	@Override
	public Type getType() {
		return getDynamicPropertyType(widget);
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey(getDynamicPropertyTooltip(widget));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getWidget()) {
			if (lastKnownType != getType()) {
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

}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import org.openflexo.connie.BindingVariable;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBViewType.DynamicProperty;
import org.openflexo.gina.view.FIBView;

/**
 * Binding variable matching a {@link DynamicProperty} of a {@link FIBComponent}
 * 
 * @author sylvain
 *
 */
public class DynamicPropertyBindingVariable extends BindingVariable implements PropertyChangeListener {
	static final Logger logger = Logger.getLogger(DynamicPropertyBindingVariable.class.getPackage().getName());

	private final DynamicProperty dynamicProperty;
	private Type lastKnownType = null;

	public DynamicPropertyBindingVariable(DynamicProperty dynamicProperty) {
		super(dynamicProperty.getName(), dynamicProperty.getType(), true);
		this.dynamicProperty = dynamicProperty;
		if (dynamicProperty != null) {
			lastKnownType = dynamicProperty.getType();
		}
		if (dynamicProperty != null && dynamicProperty.getPropertyChangeSupport() != null) {
			dynamicProperty.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void delete() {
		if (dynamicProperty != null && dynamicProperty.getPropertyChangeSupport() != null) {
			dynamicProperty.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}

	@Override
	public String getVariableName() {
		return getDynamicProperty().getName();
	}

	private boolean typeIsBeingFetched = false;

	@Override
	public Type getType() {
		if (!typeIsBeingFetched) {
			typeIsBeingFetched = true;
			try {
				return getDynamicProperty().getType();
			} finally {
				typeIsBeingFetched = false;
			}
		}
		return super.getType();
	}

	public DynamicProperty getDynamicProperty() {
		return dynamicProperty;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() == getDynamicProperty()) {
			if (evt.getPropertyName().equals(DynamicProperty.NAME_KEY)) {
				// System.out.println("Notify name changing for " + getFlexoRole() + " new=" + getVariableName());
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange(VARIABLE_NAME_PROPERTY, evt.getOldValue(), getVariableName());
				}
			}
			if (evt.getPropertyName().equals(DynamicProperty.TYPE_KEY)) {
				Type newType = getDynamicProperty().getType();
				if (lastKnownType == null || !lastKnownType.equals(newType)) {
					if (getPropertyChangeSupport() != null) {
						getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, newType);
					}
					lastKnownType = newType;
				}
			}
			if (lastKnownType != getType()) {
				// We might arrive here only in the case of a FIBComponent does not correctely notify
				// its type change. We warn it to 'tell' the developper that such notification should be done
				logger.warning("Detecting un-notified type changing for DynamicProperty " + dynamicProperty + " from " + lastKnownType
						+ " to " + getType() + ". Trying to handle case.");
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, getType());
				}
				lastKnownType = getType();
			}
		}
	}

	public Object getValue(FIBView<?, ?> componentView) {

		/*if (variable instanceof FlexoRole) {
			if (variable.getCardinality().isMultipleCardinality()) {
				return flexoConceptInstance.getFlexoActorList((FlexoRole<?>) variable);
			}
			else {
				return flexoConceptInstance.getFlexoActor((FlexoRole<?>) variable);
			}
		}
		
		return flexoConceptInstance.getFlexoPropertyValue(variable);*/

		System.out.println("TO BE IMPLEMENTED HERE !!!");
		return null;

	}

}

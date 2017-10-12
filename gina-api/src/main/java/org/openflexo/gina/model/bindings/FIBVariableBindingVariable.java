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
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.view.FIBView;

/**
 * Binding variable matching a {@link FIBVariable} in a {@link FIBComponent}
 * 
 * @author sylvain
 *
 */
public class FIBVariableBindingVariable extends BindingVariable implements PropertyChangeListener {
	static final Logger logger = Logger.getLogger(FIBVariableBindingVariable.class.getPackage().getName());

	private final FIBVariable<?> variable;
	private Type lastKnownType = null;

	public FIBVariableBindingVariable(FIBVariable<?> variable) {
		super(variable.getName(), variable.getType(), true);
		this.variable = variable;
	}

	@Override
	public void activate() {
		super.activate();
		if (variable != null) {
			lastKnownType = variable.getType();
		}
		if (variable != null && variable.getPropertyChangeSupport() != null) {
			variable.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void desactivate() {
		if (variable != null && variable.getPropertyChangeSupport() != null) {
			variable.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.desactivate();
	}

	@Override
	public String getVariableName() {
		return getVariable().getName();
	}

	private boolean typeIsBeingFetched = false;

	// TODO: investigate on this
	// sgu : i commented out the fix of xtof while i do not understand what was to be fixed
	@Override
	public Type getType() {
		if (!typeIsBeingFetched) {
			typeIsBeingFetched = true;
			try {
				return type = getVariable().getType();
			} finally {
				typeIsBeingFetched = false;
			}
		}
		// return super.getType();
		return type;
	}

	/*@Override
	public Type getType() {
	
		// XtoF:: Not sure about this, but trying to fix issue with FCIType
		if (!typeIsBeingFetched) {
			typeIsBeingFetched = true;
			try {
				Type stype = super.getType();
				Type vtype = variable.getType();
				if (vtype != stype && stype != java.lang.Object.class && TypeUtils.isTypeAssignableFrom(vtype, stype, true)){
					return stype;
				}
				return vtype;
			} finally {
				typeIsBeingFetched = false;
			}
		}
		return super.getType();
	}*/

	public FIBVariable<?> getVariable() {
		return variable;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() == variable) {
			if (evt.getPropertyName().equals(FIBVariable.NAME_KEY)) {
				System.out.println("Notify name changing for " + variable + " new=" + getVariableName());
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange(VARIABLE_NAME_PROPERTY, evt.getOldValue(), getVariableName());
				}
			}
			if (evt.getPropertyName().equals(TYPE_PROPERTY) || evt.getPropertyName().equals(FIBVariable.TYPE_KEY)) {
				Type newType = variable.getType();
				if (lastKnownType == null || !lastKnownType.equals(newType)) {
					/*System.out.println("pcSupport=" + getPropertyChangeSupport());
					if (getPropertyChangeSupport() == null) {
						System.out.println("trop con lui");
						System.out.println("ca vient de " + evt.getSource());
						Thread.dumpStack();
					}*/
					if (getPropertyChangeSupport() != null) {
						getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, newType);
					}
					lastKnownType = newType;
				}
			}
			if (lastKnownType != getType()) {
				// We might arrive here only in the case of a FlexoRole does not correctely notify
				// its type change. We warn it to 'tell' the developper that such notification should be done
				// in FlexoRole (see IndividualRole for example)
				logger.warning("Detecting un-notified type changing for FIBVariable " + variable + " from " + lastKnownType + " to "
						+ getType() + ". Trying to handle case.");
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

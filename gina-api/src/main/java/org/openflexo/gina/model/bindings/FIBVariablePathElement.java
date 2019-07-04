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
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.view.FIBView;

public class FIBVariablePathElement extends SimplePathElement implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(FIBVariablePathElement.class.getPackage().getName());

	private Type lastKnownType = null;
	private final FIBVariable<?> fibVariable;

	public FIBVariablePathElement(IBindingPathElement parent, FIBVariable<?> fibVariable) {
		super(parent, fibVariable.getName(), fibVariable.getType());

		this.fibVariable = fibVariable;
		lastKnownType = fibVariable.getType();
	}

	@Override
	public void activate() {
		super.activate();
		if (fibVariable != null && fibVariable.getPropertyChangeSupport() != null) {
			fibVariable.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void desactivate() {
		if (fibVariable != null && fibVariable.getPropertyChangeSupport() != null) {
			fibVariable.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.desactivate();
	}

	public FIBVariable<?> getFIBVariable() {
		return fibVariable;
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return fibVariable.getDescription();
	}

	@Override
	public Type getType() {
		return getFIBVariable().getType();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		// System.out.println("j'evalue " + fibVariable + " pour " + target);
		// System.out.println("il s'agit de " + fibVariable.getValue());
		if (target instanceof FIBView) {
			Object returned = ((FIBView<?, ?>) target).getVariableValue(fibVariable);
			// System.out.println("returned=" + returned);
			if (returned == null || TypeUtils.isOfType(returned, getType())) {
				// System.out.println("Et je retourne");
				return returned;
			}
			else {
				// System.out.println("Ouhlala, on me demande " + getType() + " mais j'ai " + returned.getClass());
				// System.out.println("On s'arrete");
				return null;
			}

			// System.out.println("je retourne " + ((FIBView)
			// target).getVariableValue(fibVariable));
			// return ((FIBView) target).getVariableValue(fibVariable);
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException {
		if (target instanceof FIBView) {
			((FIBView) target).setVariableValue(fibVariable, value);
			return;
		}
		logger.warning("Please implement me, target=" + target + " context=" + context);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getFIBVariable()) {
			if (evt.getPropertyName().equals(FIBVariable.NAME_KEY)) {
				// System.out.println("Notify name changing for " +
				// getFlexoProperty() + " new=" + getVariableName());
				getPropertyChangeSupport().firePropertyChange(NAME_PROPERTY, evt.getOldValue(), getLabel());
				fibVariable.getOwner().getBindingModel().getPropertyChangeSupport()
						.firePropertyChange(BindingModel.BINDING_PATH_ELEMENT_NAME_CHANGED, evt.getOldValue(), getLabel());
			}
			if (evt.getPropertyName().equals(FIBVariable.TYPE_KEY)) {
				Type newType = getFIBVariable().getType();
				if (lastKnownType == null || !lastKnownType.equals(newType)) {
					getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, newType);
					fibVariable.getOwner().getBindingModel().getPropertyChangeSupport()
							.firePropertyChange(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED, lastKnownType, newType);
					lastKnownType = newType;
				}
			}
			if (lastKnownType != getType()) {
				// We might arrive here only in the case of a FIBVariable does
				// not correctely notify
				// its type change. We warn it to 'tell' the developper that
				// such notification should be done
				// in FlexoProperty (see IndividualProperty for example)
				logger.warning("Detecting un-notified type changing for FIBVariable " + fibVariable + " from " + lastKnownType + " to "
						+ getType() + ". Trying to handle case.");
				getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, getType());
				fibVariable.getOwner().getBindingModel().getPropertyChangeSupport()
						.firePropertyChange(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED, lastKnownType, getType());
				lastKnownType = getType();
			}
		}
	}

}

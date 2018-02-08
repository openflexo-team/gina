/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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
import org.openflexo.gina.model.operator.FIBIteration;

/**
 * BindingVariable associated to a {@link FIBIteration}
 * 
 * @author sylvain
 * 
 */
public class IteratorBindingVariable extends BindingVariable implements PropertyChangeListener {
	static final Logger logger = Logger.getLogger(IteratorBindingVariable.class.getPackage().getName());

	private final FIBIteration iteration;
	private Type lastKnownType = null;

	public IteratorBindingVariable(FIBIteration iteration) {
		super(iteration.getIteratorName(), iteration.getIteratorType(), true);
		this.iteration = iteration;
		if (iteration != null) {
			lastKnownType = iteration.getIteratorType();
		}
		if (iteration != null && iteration.getPropertyChangeSupport() != null) {
			iteration.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void delete() {
		if (iteration != null && iteration.getPropertyChangeSupport() != null) {
			iteration.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}

	@Override
	public String getVariableName() {
		return getIteration().getIteratorName();
	}

	@Override
	public Type getType() {
		if (getIteration() != null) {
			return getIteration().getIteratorType();
		}
		return null;
	}

	public FIBIteration getIteration() {
		return iteration;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// if (debug) {
		// System.out.println("****** propertyChange " + evt.getPropertyName() + " source=" + evt.getSource());
		// }
		if (evt.getSource() == getIteration()) {
			if (evt.getPropertyName().equals(FIBIteration.ITERATOR_NAME_KEY)) {
				// System.out.println("Notify name changing for " + getFlexoRole() + " new=" + getVariableName());
				getPropertyChangeSupport().firePropertyChange(VARIABLE_NAME_PROPERTY, evt.getOldValue(), getVariableName());
			}
			iteratorTypeMightHaveChanged();
		}
	}

	private void iteratorTypeMightHaveChanged() {
		if (lastKnownType != getType()) {
			// if (debug) {
			// System.out.println("Iterator type changed for " + getType());
			// }
			getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, getType());
			lastKnownType = getType();
		}
	}

}

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
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;

public class TableSelectedPathElement extends SimplePathElement implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(TableSelectedPathElement.class.getPackage().getName());

	private Type lastKnownType = null;
	private final FIBTable table;

	public TableSelectedPathElement(BindingPathElement parent, FIBTable table) {
		super(parent, "selected", table.getIteratorClass());
		this.table = table;
		if (table != null && table.getPropertyChangeSupport() != null) {
			table.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
		if (table != null) {
			lastKnownType = table.getIteratorClass();
		}
	}

	@Override
	public void delete() {
		if (table != null && table.getPropertyChangeSupport() != null) {
			table.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}

	public FIBTable getTable() {
		return table;
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey("table_selected_value");
	}

	@Override
	public Type getType() {
		return table.getIteratorClass();
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (target instanceof FIBTableWidget) {
			return ((FIBTableWidget<?, ?>) target).getSelected();
		}
		logger.warning("Unexpected target=" + target + " context=" + context);
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException {
		if (target instanceof FIBTableWidget) {
			((FIBTableWidget) target).performSelect(value);
			return;
		}
		logger.warning("Unexpected target=" + target + " context=" + context);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getTable()) {
			if (evt.getPropertyName().equals(FIBTable.ITERATOR_CLASS_KEY)) {
				Type newType = getTable().getIteratorClass();
				if (lastKnownType == null || !lastKnownType.equals(newType)) {
					getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, newType);
					getTable().getBindingModel().getPropertyChangeSupport()
							.firePropertyChange(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED, lastKnownType, newType);
					lastKnownType = newType;
				}
			}
			if (lastKnownType != getType()) {
				// We might arrive here only in the case notification was not properly triggered.
				// We warn it to 'tell' the developper that such notification should be done
				logger.warning("Detecting un-notified type changing for FIBTable " + table + " from " + lastKnownType + " to " + getType()
						+ ". Trying to handle case.");
				getPropertyChangeSupport().firePropertyChange(TYPE_PROPERTY, lastKnownType, getType());
				getTable().getBindingModel().getPropertyChangeSupport().firePropertyChange(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED,
						lastKnownType, getType());
				lastKnownType = getType();
			}
		}
	}

}

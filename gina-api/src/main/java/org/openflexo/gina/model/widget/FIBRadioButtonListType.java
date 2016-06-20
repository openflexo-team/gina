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

package org.openflexo.gina.model.widget;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.model.FIBWidgetType;
import org.openflexo.gina.view.widget.FIBRadioButtonListWidget;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBRadioButtonListWidget} representing a given {@link FIBRadioButtonList}<br>
 * Extends base {@link FIBWidgetType} by exposing "selected","selectedIndex" and "selection" dynamic properties
 * 
 * @author sylvain
 * 
 */
public class FIBRadioButtonListType extends FIBWidgetType<FIBRadioButtonList> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBRadioButtonListType.class.getPackage().getName());

	public final DynamicProperty SELECTED = new DynamicProperty() {
		@Override
		public String getName() {
			return "selected";
		}

		@Override
		public Type getType() {
			return getFIBComponent().getIteratorClass();
		}

		@Override
		public String getTooltip() {
			return "value_being_currently_selected_in_list";
		}

		@Override
		public Object getBindingValue(Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBRadioButtonListWidget) {
				return ((FIBRadioButtonListWidget<?, ?>) target).getSelected();
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
			return null;
		}

		@Override
		public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBRadioButtonListWidget) {
				((FIBRadioButtonListWidget) target).setSelected(value);
				return;
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
		}

	};

	public final DynamicProperty SELECTED_INDEX = new DynamicProperty() {
		@Override
		public String getName() {
			return "selectedIndex";
		}

		@Override
		public Type getType() {
			return Integer.TYPE;
		}

		@Override
		public String getTooltip() {
			return "index_of_value_being_currently_selected_in_browser";
		}

		@Override
		public Object getBindingValue(Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBRadioButtonListWidget) {
				return ((FIBRadioButtonListWidget<?, ?>) target).getSelectedIndex();
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
			return null;
		}

		@Override
		public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBRadioButtonListWidget) {
				((FIBRadioButtonListWidget) target).setSelectedIndex((Integer) value);
				return;
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
		}

	};

	public FIBRadioButtonListType(FIBRadioButtonList aWidget) {
		super(aWidget);
	}

	@Override
	public Class<FIBRadioButtonListWidget> getBaseClass() {
		return FIBRadioButtonListWidget.class;
	}

	@Override
	public List<DynamicProperty> getDynamicProperties() {

		List<DynamicProperty> returned = new ArrayList<DynamicProperty>();
		returned.addAll(super.getDynamicProperties());
		returned.add(SELECTED);
		returned.add(SELECTED_INDEX);
		return returned;
	}

}

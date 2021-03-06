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
import org.openflexo.gina.view.widget.FIBCustomWidget;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBCustomWidget} representing a given {@link FIBCustom}<br>
 * Extends base {@link FIBWidgetType} by exposing "component" dynamic property
 * 
 * @author sylvain
 * 
 */
public class FIBCustomType extends FIBWidgetType<FIBCustom> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBCustomType.class.getPackage().getName());

	public final DynamicProperty COMPONENT = new DynamicProperty() {
		@Override
		public String getName() {
			return "component";
		}

		@Override
		public Type getType() {
			return getFIBComponent().getComponentClass();
		}

		@Override
		public String getTooltip() {
			return "component_beeing_represented";
		}

		@Override
		public Object getBindingValue(Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			System.out.println("getBindingValue() for target=" + target + " with context=" + context);
			if (target instanceof FIBCustomWidget) {
				return ((FIBCustomWidget<?, ?, ?>) target).getCustomComponent();
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
			return null;
		}

		@Override
		public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBCustomWidget) {
				return;
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
		}

	};

	public FIBCustomType(FIBCustom aWidget) {
		super(aWidget);
	}

	@Override
	public Class<FIBCustomWidget> getBaseClass() {
		return FIBCustomWidget.class;
	}

	@Override
	public List<DynamicProperty> getDynamicProperties() {

		List<DynamicProperty> returned = new ArrayList<>();
		returned.addAll(super.getDynamicProperties());
		returned.add(COMPONENT);
		return returned;
	}

}

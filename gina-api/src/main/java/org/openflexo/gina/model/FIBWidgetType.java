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

package org.openflexo.gina.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.model.bindings.DynamicPropertyPathElement;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBWidgetView} representing a given {@link FIBWidget}<br>
 * Note that to be able to expose dynamic properties, this class should be extended.
 * 
 * @author sylvain
 * 
 */
public class FIBWidgetType<W extends FIBWidget> extends FIBViewType<W> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBWidgetType.class.getPackage().getName());

	protected final Map<IBindingPathElement, List<DynamicPropertyPathElement<? super W>>> pathElements;

	public final DynamicProperty VALUE = new DynamicProperty() {
		@Override
		public String getName() {
			return "value";
		}

		@Override
		public Type getType() {
			return getFIBComponent().getDataType();
		}

		@Override
		public String getTooltip() {
			return "value_being_represented_by_widget";
		}

		@Override
		public Object getBindingValue(Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBWidgetView) {
				return ((FIBWidgetView<?, ?, ?>) target).getValue();
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
			return null;
		}

		@Override
		public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException {
			if (target instanceof FIBWidgetView) {
				((FIBWidgetView<?, ?, Object>) target).setValue(value);
				return;
			}
			logger.warning("Unexpected target=" + target + " context=" + context);
		}

	};

	public FIBWidgetType(W aWidget) {
		super(aWidget);
		pathElements = new HashMap<>();
	}

	@Override
	public Class<? extends FIBWidgetView> getBaseClass() {
		return FIBWidgetView.class;
	}

	@Override
	public List<DynamicPropertyPathElement<? super W>> getAccessibleSimplePathElements(IBindingPathElement parent, Bindable bindable) {

		if (parent != null && parent.getType() instanceof FIBWidgetType) {

			List<DynamicPropertyPathElement<? super W>> returned = pathElements.get(parent);
			if (returned == null) {
				returned = new ArrayList<>();
				for (DynamicProperty p : getDynamicProperties()) {
					returned.add(new DynamicPropertyPathElement<FIBWidget>(parent, getFIBComponent(), p, bindable));
				}
				pathElements.put(parent, returned);
			}
			return returned;
		}

		return Collections.emptyList();
	}

	@Override
	public List<DynamicProperty> getDynamicProperties() {
		return Collections.singletonList(VALUE);
	}

}

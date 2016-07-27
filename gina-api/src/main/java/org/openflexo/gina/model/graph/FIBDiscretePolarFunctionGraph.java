/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina.model.graph;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a 2D-base polar graph [r=f(A)] representing functions where:
 * <ul>
 * <li>angle is iterated over discrete values, expressed as radian angles between 0 and 360 degrees</li>
 * <li>radius is based on an expression using angle (iterated value, which is here discrete)</li><br>
 * </ul>
 * 
 * Such graphs allows only one parameter which is angle of the polar graph
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FIBDiscretePolarFunctionGraph.FIBDiscretePolarFunctionGraphImpl.class)
@XMLElement
public interface FIBDiscretePolarFunctionGraph extends FIBPolarFunctionGraph {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUES_KEY = "values";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LABELS_KEY = "labels";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ANGLE_EXTENT_KEY = "angleExtent";

	@Getter(VALUES_KEY)
	@XMLAttribute
	public DataBinding<List<?>> getValues();

	@Setter(VALUES_KEY)
	public void setValues(DataBinding<List<?>> values);

	@Getter(LABELS_KEY)
	@XMLAttribute
	public DataBinding<String> getLabels();

	@Setter(LABELS_KEY)
	public void setLabels(DataBinding<String> label);

	@Getter(ANGLE_EXTENT_KEY)
	@XMLAttribute
	public DataBinding<Double> getAngleExtent();

	@Setter(ANGLE_EXTENT_KEY)
	public void setAngleExtent(DataBinding<Double> angleExtent);

	public static abstract class FIBDiscretePolarFunctionGraphImpl extends FIBPolarFunctionGraphImpl
			implements FIBDiscretePolarFunctionGraph {

		private DataBinding<List<?>> values = null;
		private DataBinding<String> labels = null;
		private DataBinding<Double> angleExtent = null;
		private ParameterExpressionDelegate parameterExpressionDelegate;

		public FIBDiscretePolarFunctionGraphImpl() {
			parameterExpressionDelegate = new ParameterExpressionDelegate();
		}

		@Override
		public void revalidateBindings() {
			if (values != null) {
				values.revalidate();
				if (parameterBindingVariable != null) {
					parameterBindingVariable.setType(getParameterType());
				}
			}
			if (labels != null) {
				labels.revalidate();
			}
			if (angleExtent != null) {
				angleExtent.revalidate();
			}
			super.revalidateBindings();
		}

		public ParameterExpressionDelegate getParameterExpressionDelegate() {
			return parameterExpressionDelegate;
		}

		@Override
		public DataBinding<List<?>> getValues() {
			if (values == null) {
				values = new DataBinding<List<?>>(this, List.class, DataBinding.BindingDefinitionType.GET);
				values.setBindingName(VALUES_KEY);
			}
			return values;
		}

		@Override
		public void setValues(DataBinding<List<?>> values) {
			if (values != null) {
				values.setOwner(this);
				values.setDeclaredType(List.class);
				values.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				values.setBindingName(VALUES_KEY);
			}
			this.values = values;
			getPropertyChangeSupport().firePropertyChange(VALUES_KEY, null, values);
		}

		@Override
		public DataBinding<Double> getAngleExtent() {
			if (angleExtent == null) {
				angleExtent = new DataBinding<Double>(parameterExpressionDelegate, Double.class, DataBinding.BindingDefinitionType.GET);
				angleExtent.setBindingName(ANGLE_EXTENT_KEY);
			}
			return angleExtent;
		}

		@Override
		public void setAngleExtent(DataBinding<Double> angleExtent) {
			if (angleExtent != null) {
				angleExtent.setOwner(parameterExpressionDelegate);
				angleExtent.setDeclaredType(Double.class);
				angleExtent.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				angleExtent.setBindingName(ANGLE_EXTENT_KEY);
			}
			this.angleExtent = angleExtent;
			getPropertyChangeSupport().firePropertyChange(ANGLE_EXTENT_KEY, null, angleExtent);
		}

		@Override
		public DataBinding<String> getLabels() {
			if (labels == null) {
				labels = new DataBinding<String>(parameterExpressionDelegate, String.class, DataBinding.BindingDefinitionType.GET);
				labels.setBindingName(LABELS_KEY);
			}
			return labels;
		}

		@Override
		public void setLabels(DataBinding<String> labels) {
			if (labels != null) {
				labels.setOwner(parameterExpressionDelegate);
				labels.setDeclaredType(String.class);
				labels.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				labels.setBindingName(LABELS_KEY);
			}
			this.labels = labels;
			getPropertyChangeSupport().firePropertyChange(LABELS_KEY, null, labels);
		}

		@Override
		public Type getParameterType() {
			if (getValues() != null && getValues().isSet() && getValues().isValid()) {
				Type accessedType = getValues().getAnalyzedType();
				if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
					return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
				}
			}
			return Object.class;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			super.notifiedBindingChanged(binding);
			if (binding == values) {
				if (parameterBindingVariable != null) {
					System.out.println("Changing type to " + getParameterType());
					parameterBindingVariable.setType(getParameterType());
				}
			}
		}

		private class ParameterExpressionDelegate extends DefaultBindable {
			@Override
			public BindingModel getBindingModel() {
				return getGraphBindingModel();
			}

			public FIBComponent getComponent() {
				return FIBDiscretePolarFunctionGraphImpl.this;
			}

			@Override
			public BindingFactory getBindingFactory() {
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			}

		}

	}
}

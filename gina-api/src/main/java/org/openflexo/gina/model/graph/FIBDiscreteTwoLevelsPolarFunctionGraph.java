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
import org.openflexo.connie.BindingVariable;
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
@ImplementationClass(FIBDiscreteTwoLevelsPolarFunctionGraph.FIBDiscreteTwoLevelsPolarFunctionGraphImpl.class)
@XMLElement
public interface FIBDiscreteTwoLevelsPolarFunctionGraph extends FIBDiscretePolarFunctionGraph {

	@PropertyIdentifier(type = String.class)
	public static final String PRIMARY_PARAMETER_NAME_KEY = "primaryParameterName";
	@PropertyIdentifier(type = String.class)
	public static final String SECONDARY_PARAMETER_NAME_KEY = "secondaryParameterName";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SECONDARY_VALUES_KEY = "secondaryValues";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SECONDARY_LABELS_KEY = "secondaryLabels";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SECONDARY_ANGLE_EXTENT_KEY = "secondaryAngleExtent";

	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_SECONDARY_LABELS_KEY = "displaySecondaryLabels";

	@Getter(SECONDARY_VALUES_KEY)
	@XMLAttribute
	public DataBinding<List<?>> getSecondaryValues();

	@Setter(SECONDARY_VALUES_KEY)
	public void setSecondaryValues(DataBinding<List<?>> secondaryValues);

	@Getter(SECONDARY_LABELS_KEY)
	@XMLAttribute
	public DataBinding<String> getSecondaryLabels();

	@Setter(SECONDARY_LABELS_KEY)
	public void setSecondaryLabels(DataBinding<String> label);

	@Getter(SECONDARY_ANGLE_EXTENT_KEY)
	@XMLAttribute
	public DataBinding<Double> getSecondaryAngleExtent();

	@Setter(SECONDARY_ANGLE_EXTENT_KEY)
	public void setSecondaryAngleExtent(DataBinding<Double> angleExtent);

	@Getter(value = PRIMARY_PARAMETER_NAME_KEY, defaultValue = "param1")
	@XMLAttribute
	public String getPrimaryParameterName();

	@Setter(PRIMARY_PARAMETER_NAME_KEY)
	public void setPrimaryParameterName(String parameterName);

	@Getter(value = SECONDARY_PARAMETER_NAME_KEY, defaultValue = "param2")
	@XMLAttribute
	public String getSecondaryParameterName();

	@Setter(SECONDARY_PARAMETER_NAME_KEY)
	public void setSecondaryParameterName(String parameterName);

	public Type getSecondaryParameterType();

	public BindingModel getSecondaryGraphBindingModel();

	@Getter(value = DISPLAY_SECONDARY_LABELS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDisplaySecondaryLabels();

	@Setter(DISPLAY_SECONDARY_LABELS_KEY)
	public void setDisplaySecondaryLabels(boolean displayLabels);

	public static abstract class FIBDiscreteTwoLevelsPolarFunctionGraphImpl extends FIBDiscretePolarFunctionGraphImpl
			implements FIBDiscreteTwoLevelsPolarFunctionGraph {

		private DataBinding<List<?>> secondaryValues = null;
		private DataBinding<String> secondaryLabels = null;
		private DataBinding<Double> secondaryAngleExtent = null;

		private BindingModel secondaryGraphBindingModel;
		protected BindingVariable secondaryParameterBindingVariable;

		private final SecondaryParameterExpressionDelegate secondaryParameterExpressionDelegate;

		public FIBDiscreteTwoLevelsPolarFunctionGraphImpl() {
			secondaryParameterExpressionDelegate = new SecondaryParameterExpressionDelegate();
		}

		@Override
		public BindingModel getSecondaryGraphBindingModel() {
			if (secondaryGraphBindingModel == null) {
				createSecondaryGraphBindingModel();
			}
			return secondaryGraphBindingModel;
		}

		protected BindingModel createSecondaryGraphBindingModel() {
			secondaryGraphBindingModel = new BindingModel(getGraphBindingModel());
			secondaryParameterBindingVariable = new BindingVariable(getSecondaryParameterName(), getSecondaryParameterType());
			secondaryGraphBindingModel.addToBindingVariables(secondaryParameterBindingVariable);
			return secondaryGraphBindingModel;
		}

		@Override
		public void setSecondaryParameterName(String parameterName) {
			performSuperSetter(SECONDARY_PARAMETER_NAME_KEY, parameterName);
			if (secondaryParameterBindingVariable != null) {
				secondaryParameterBindingVariable.setVariableName(parameterName);
			}
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (secondaryValues != null) {
				secondaryValues.revalidate();
				if (secondaryParameterBindingVariable != null) {
					secondaryParameterBindingVariable.setType(getSecondaryParameterType());
				}
			}
			if (secondaryLabels != null) {
				secondaryLabels.revalidate();
			}
			if (secondaryAngleExtent != null) {
				secondaryAngleExtent.revalidate();
			}
		}

		/*@Override
		protected BindingModel createGraphBindingModel() {
			BindingModel returned = super.createGraphBindingModel();
			secondaryParameterBindingVariable = new BindingVariable(getSecondaryParameterName(), getSecondaryParameterType());
			returned.addToBindingVariables(secondaryParameterBindingVariable);
			return returned;
		}*/

		@Override
		public DataBinding<List<?>> getSecondaryValues() {
			if (secondaryValues == null) {
				secondaryValues = new DataBinding<>(secondaryParameterExpressionDelegate, List.class,
						DataBinding.BindingDefinitionType.GET);
				secondaryValues.setBindingName(SECONDARY_VALUES_KEY);
			}
			return secondaryValues;
		}

		@Override
		public void setSecondaryValues(DataBinding<List<?>> secondaryValues) {
			if (secondaryValues != null) {
				secondaryValues.setOwner(secondaryParameterExpressionDelegate);
				secondaryValues.setDeclaredType(List.class);
				secondaryValues.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				secondaryValues.setBindingName(SECONDARY_VALUES_KEY);
			}
			this.secondaryValues = secondaryValues;
			getPropertyChangeSupport().firePropertyChange(SECONDARY_VALUES_KEY, null, secondaryValues);
			notifiedBindingChanged(this.secondaryValues);
		}

		@Override
		public DataBinding<Double> getSecondaryAngleExtent() {
			if (secondaryAngleExtent == null) {
				secondaryAngleExtent = new DataBinding<>(secondaryParameterExpressionDelegate, Double.class,
						DataBinding.BindingDefinitionType.GET);
				secondaryAngleExtent.setBindingName(SECONDARY_ANGLE_EXTENT_KEY);
			}
			return secondaryAngleExtent;
		}

		@Override
		public void setSecondaryAngleExtent(DataBinding<Double> secondaryAngleExtent) {
			if (secondaryAngleExtent != null) {
				secondaryAngleExtent.setOwner(secondaryParameterExpressionDelegate);
				secondaryAngleExtent.setDeclaredType(Double.class);
				secondaryAngleExtent.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				secondaryAngleExtent.setBindingName(SECONDARY_ANGLE_EXTENT_KEY);
			}
			this.secondaryAngleExtent = secondaryAngleExtent;
			getPropertyChangeSupport().firePropertyChange(SECONDARY_ANGLE_EXTENT_KEY, null, secondaryAngleExtent);
		}

		@Override
		public DataBinding<String> getSecondaryLabels() {
			if (secondaryLabels == null) {
				secondaryLabels = new DataBinding<>(secondaryParameterExpressionDelegate, String.class,
						DataBinding.BindingDefinitionType.GET);
				secondaryLabels.setBindingName(SECONDARY_LABELS_KEY);
			}
			return secondaryLabels;
		}

		@Override
		public void setSecondaryLabels(DataBinding<String> secondaryLabels) {
			if (secondaryLabels != null) {
				secondaryLabels.setOwner(secondaryParameterExpressionDelegate);
				secondaryLabels.setDeclaredType(String.class);
				secondaryLabels.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				secondaryLabels.setBindingName(LABELS_KEY);
			}
			this.secondaryLabels = secondaryLabels;
			getPropertyChangeSupport().firePropertyChange(SECONDARY_LABELS_KEY, null, secondaryLabels);
			notifiedBindingChanged(this.secondaryLabels);
		}

		@Override
		public Type getSecondaryParameterType() {
			if (getSecondaryValues() != null && getSecondaryValues().isSet() && getSecondaryValues().isValid()) {
				Type accessedType = getSecondaryValues().getAnalyzedType();
				if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
					return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
				}
			}
			return Object.class;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			super.notifiedBindingChanged(binding);
			if (binding == getValues()) {
				// values have been changed, this will determine the type of secondary value type
				notifiedBindingChanged(secondaryValues);
			}
			if (binding == secondaryValues) {
				if (secondaryParameterBindingVariable != null) {
					secondaryParameterBindingVariable.setType(getSecondaryParameterType());
				}
			}
		}

		private class SecondaryParameterExpressionDelegate extends DefaultBindable {
			@Override
			public BindingModel getBindingModel() {
				return getSecondaryGraphBindingModel();
			}

			public FIBComponent getComponent() {
				return FIBDiscreteTwoLevelsPolarFunctionGraphImpl.this;
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

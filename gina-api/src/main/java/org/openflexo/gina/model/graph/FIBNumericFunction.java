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

import java.lang.reflect.Type;

import org.openflexo.connie.DataBinding;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBNumericFunction.FIBNumericFunctionImpl.class)
@XMLElement
public interface FIBNumericFunction extends FIBGraphFunction {

	public static final Double DEFAULT_MIN_VALUE = 0.0;
	public static final Double DEFAULT_MAX_VALUE = 100.0;
	public static final Double DEFAULT_MINOR_TICK_SPACING = 5.0;
	public static final Double DEFAULT_MAJOR_TICK_SPACING = 20.0;
	public static final Integer DEFAULT_STEPS_NUMBER = 100;

	@PropertyIdentifier(type = DataBinding.class)
	public static final String MIN_VALUE_KEY = "minValue";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String MAX_VALUE_KEY = "maxValue";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String MINOR_TICK_SPACING_KEY = "minorTickSpacing";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String MAJOR_TICK_SPACING_KEY = "majorTickSpacing";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String STEPS_NUMBER_KEY = "stepsNumber";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_MAJOR_TICKS_KEY = "displayMajorTicks";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_MINOR_TICKS_KEY = "displayMinorTicks";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_LABELS_KEY = "displayLabels";

	@Getter(MIN_VALUE_KEY)
	@XMLAttribute
	public DataBinding<? extends Number> getMinValue();

	@Setter(MIN_VALUE_KEY)
	public void setMinValue(DataBinding<? extends Number> minValue);

	@Getter(MAX_VALUE_KEY)
	@XMLAttribute
	public DataBinding<? extends Number> getMaxValue();

	@Setter(MAX_VALUE_KEY)
	public void setMaxValue(DataBinding<? extends Number> maxValue);

	@Getter(MINOR_TICK_SPACING_KEY)
	@XMLAttribute
	public DataBinding<? extends Number> getMinorTickSpacing();

	@Setter(MINOR_TICK_SPACING_KEY)
	public void setMinorTickSpacing(DataBinding<? extends Number> tickSpacing);

	@Getter(MAJOR_TICK_SPACING_KEY)
	@XMLAttribute
	public DataBinding<? extends Number> getMajorTickSpacing();

	@Setter(MAJOR_TICK_SPACING_KEY)
	public void setMajorTickSpacing(DataBinding<? extends Number> tickSpacing);

	@Getter(STEPS_NUMBER_KEY)
	@XMLAttribute
	public DataBinding<Integer> getStepsNumber();

	@Setter(STEPS_NUMBER_KEY)
	public void setStepsNumber(DataBinding<Integer> stepsNumber);

	@Getter(value = DISPLAY_MAJOR_TICKS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDisplayMajorTicks();

	@Setter(DISPLAY_MAJOR_TICKS_KEY)
	public void setDisplayMajorTicks(boolean displayMajorTicks);

	@Getter(value = DISPLAY_MINOR_TICKS_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getDisplayMinorTicks();

	@Setter(DISPLAY_MINOR_TICKS_KEY)
	public void setDisplayMinorTicks(boolean displayMinorTicks);

	@Getter(value = DISPLAY_LABELS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDisplayLabels();

	@Setter(DISPLAY_LABELS_KEY)
	public void setDisplayLabels(boolean displayLabels);

	public static abstract class FIBNumericFunctionImpl extends FIBGraphFunctionImpl implements FIBNumericFunction {

		private DataBinding<? extends Number> minValue = null;
		private DataBinding<? extends Number> maxValue = null;
		private DataBinding<? extends Number> minorTickSpacing = null;
		private DataBinding<? extends Number> majorTickSpacing = null;
		private DataBinding<Integer> stepsNumber = null;

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (minValue != null) {
				minValue.forceRevalidate();
			}
			if (maxValue != null) {
				maxValue.forceRevalidate();
			}
			if (minorTickSpacing != null) {
				minorTickSpacing.forceRevalidate();
			}
			if (majorTickSpacing != null) {
				majorTickSpacing.forceRevalidate();
			}
			if (stepsNumber != null) {
				stepsNumber.forceRevalidate();
			}
		}

		@Override
		public Type getType() {
			if (getExpression() != null && getExpression().isSet() && getExpression().isValid()) {
				return getExpression().getAnalyzedType();
			}
			return Number.class;
		}

		@Override
		public DataBinding<? extends Number> getMinValue() {
			if (minValue == null) {
				minValue = new DataBinding<>(this, Number.class, DataBinding.BindingDefinitionType.GET);
				minValue.setBindingName(MIN_VALUE_KEY);
			}
			return minValue;
		}

		@Override
		public void setMinValue(DataBinding<? extends Number> minValue) {
			if (minValue != null) {
				minValue.setOwner(this);
				minValue.setDeclaredType(Number.class);
				minValue.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				minValue.setBindingName(MIN_VALUE_KEY);
			}
			this.minValue = minValue;
			getPropertyChangeSupport().firePropertyChange(MIN_VALUE_KEY, null, minValue);
		}

		@Override
		public DataBinding<? extends Number> getMaxValue() {
			if (maxValue == null) {
				maxValue = new DataBinding<>(this, Number.class, DataBinding.BindingDefinitionType.GET);
				maxValue.setBindingName(MAX_VALUE_KEY);
			}
			return maxValue;
		}

		@Override
		public void setMaxValue(DataBinding<? extends Number> maxValue) {
			if (maxValue != null) {
				maxValue.setOwner(this);
				maxValue.setDeclaredType(Number.class);
				maxValue.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				maxValue.setBindingName(MAX_VALUE_KEY);
			}
			this.maxValue = maxValue;
			getPropertyChangeSupport().firePropertyChange(MAX_VALUE_KEY, null, maxValue);
		}

		@Override
		public DataBinding<? extends Number> getMinorTickSpacing() {
			if (minorTickSpacing == null) {
				minorTickSpacing = new DataBinding<>(this, Number.class, DataBinding.BindingDefinitionType.GET);
				minorTickSpacing.setBindingName(MINOR_TICK_SPACING_KEY);
			}
			return minorTickSpacing;
		}

		@Override
		public void setMinorTickSpacing(DataBinding<? extends Number> minorTickSpacing) {
			if (minorTickSpacing != null) {
				minorTickSpacing.setOwner(this);
				minorTickSpacing.setDeclaredType(Number.class);
				minorTickSpacing.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				minorTickSpacing.setBindingName(MINOR_TICK_SPACING_KEY);
			}
			this.minorTickSpacing = minorTickSpacing;
			getPropertyChangeSupport().firePropertyChange(MINOR_TICK_SPACING_KEY, null, minorTickSpacing);
		}

		@Override
		public DataBinding<? extends Number> getMajorTickSpacing() {
			if (majorTickSpacing == null) {
				majorTickSpacing = new DataBinding<>(this, Number.class, DataBinding.BindingDefinitionType.GET);
				majorTickSpacing.setBindingName(MAJOR_TICK_SPACING_KEY);
			}
			return majorTickSpacing;
		}

		@Override
		public void setMajorTickSpacing(DataBinding<? extends Number> majorTickSpacing) {
			if (majorTickSpacing != null) {
				majorTickSpacing.setOwner(this);
				majorTickSpacing.setDeclaredType(Number.class);
				majorTickSpacing.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				majorTickSpacing.setBindingName(MAJOR_TICK_SPACING_KEY);
			}
			this.majorTickSpacing = majorTickSpacing;
			getPropertyChangeSupport().firePropertyChange(MAJOR_TICK_SPACING_KEY, null, majorTickSpacing);
		}

		@Override
		public DataBinding<Integer> getStepsNumber() {
			if (stepsNumber == null) {
				stepsNumber = new DataBinding<>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
				stepsNumber.setBindingName(STEPS_NUMBER_KEY);
			}
			return stepsNumber;
		}

		@Override
		public void setStepsNumber(DataBinding<Integer> stepsNumber) {
			if (stepsNumber != null) {
				stepsNumber.setOwner(this);
				stepsNumber.setDeclaredType(Integer.class);
				stepsNumber.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				stepsNumber.setBindingName(STEPS_NUMBER_KEY);
			}
			this.stepsNumber = stepsNumber;
			getPropertyChangeSupport().firePropertyChange(STEPS_NUMBER_KEY, null, stepsNumber);
		}

	}
}

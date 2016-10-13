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

/**
 * Represents a 2D-base polar graph [r=f(A)] representing functions where:
 * <ul>
 * <li>angle is iterated over continuous values, expressed as radian angles between 0 and 360 degrees</li>
 * <li>radius is based on an expression using angle (iterated value, which is here continuous)</li><br>
 * </ul>
 * 
 * Such graphs allows only one parameter which is angle of the polar graph
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FIBContinuousPolarFunctionGraph.FIBContinuousPolarFunctionGraphImpl.class)
@XMLElement
public interface FIBContinuousPolarFunctionGraph extends FIBPolarFunctionGraph {

	public static final Double DEFAULT_ANGLE_TICK_SPACING = 0.1;
	public static final Integer DEFAULT_STEPS_NUMBER = 100;

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ANGLE_TICK_SPACING_KEY = "angleTickSpacing";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String STEPS_NUMBER_KEY = "stepsNumber";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_ANGLE_TICKS_KEY = "displayAngleTicks";

	@Getter(ANGLE_TICK_SPACING_KEY)
	@XMLAttribute
	public DataBinding<Double> getAngleTickSpacing();

	@Setter(ANGLE_TICK_SPACING_KEY)
	public void setAngleTickSpacing(DataBinding<Double> tickSpacing);

	@Getter(STEPS_NUMBER_KEY)
	@XMLAttribute
	public DataBinding<Integer> getStepsNumber();

	@Setter(STEPS_NUMBER_KEY)
	public void setStepsNumber(DataBinding<Integer> stepsNumber);

	@Getter(value = DISPLAY_ANGLE_TICKS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDisplayAngleTicks();

	@Setter(DISPLAY_ANGLE_TICKS_KEY)
	public void setDisplayAngleTicks(boolean displayAngleTicks);

	public static abstract class FIBContinuousPolarFunctionGraphImpl extends FIBPolarFunctionGraphImpl
			implements FIBContinuousPolarFunctionGraph {

		private DataBinding<Double> angleTickSpacing = null;
		private DataBinding<Integer> stepsNumber = null;

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (angleTickSpacing != null) {
				angleTickSpacing.revalidate();
			}
			if (stepsNumber != null) {
				stepsNumber.revalidate();
			}
		}

		@Override
		public DataBinding<Double> getAngleTickSpacing() {
			if (angleTickSpacing == null) {
				angleTickSpacing = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
				angleTickSpacing.setBindingName(ANGLE_TICK_SPACING_KEY);
			}
			return angleTickSpacing;
		}

		@Override
		public void setAngleTickSpacing(DataBinding<Double> angleTickSpacing) {
			if (angleTickSpacing != null) {
				angleTickSpacing.setOwner(this);
				angleTickSpacing.setDeclaredType(Number.class);
				angleTickSpacing.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				angleTickSpacing.setBindingName(ANGLE_TICK_SPACING_KEY);
			}
			this.angleTickSpacing = angleTickSpacing;
			getPropertyChangeSupport().firePropertyChange(ANGLE_TICK_SPACING_KEY, null, angleTickSpacing);
		}

		@Override
		public DataBinding<Integer> getStepsNumber() {
			if (stepsNumber == null) {
				stepsNumber = new DataBinding<Integer>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
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

		@Override
		public Type getParameterType() {
			return Double.class;
		}

	}
}

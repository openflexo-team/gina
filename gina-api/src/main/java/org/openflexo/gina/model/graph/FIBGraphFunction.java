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

import java.awt.Color;
import java.lang.reflect.Type;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBGraphFunction.FIBGraphFunctionImpl.class)
@Imports({ @Import(FIBNumericFunction.class), @Import(FIBDiscreteFunction.class) })
public interface FIBGraphFunction extends FIBModelObject {

	public static enum GraphType {
		POINTS, POLYLIN, RECT_POLYLIN, CURVE, BAR_GRAPH, COLORED_STEPS
	}

	public static enum FunctionBackgroundType {
		COLORED, GRADIENT, NONE;
	}

	public static final Color DEFAULT_FOREGROUND = Color.BLUE;
	public static final FunctionBackgroundType DEFAULT_BACKGROUND_TYPE = FunctionBackgroundType.COLORED;
	public static final Color DEFAULT_BACKGROUND_1 = new Color(0, 204, 255);
	public static final Color DEFAULT_BACKGROUND_2 = Color.WHITE;

	@PropertyIdentifier(type = FIBGraph.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EXPRESSION_KEY = "expression";
	@PropertyIdentifier(type = Type.class)
	public static final String TYPE_KEY = "type";
	@PropertyIdentifier(type = GraphType.class)
	public static final String GRAPH_TYPE_KEY = "graphType";
	@PropertyIdentifier(type = Color.class)
	public static final String FOREGROUND_COLOR_KEY = "foregroundColor";
	@PropertyIdentifier(type = FunctionBackgroundType.class)
	public static final String BACKGROUND_TYPE_KEY = "backgroundType";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_1_KEY = "backgroundColor1";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_2_KEY = "backgroundColor2";

	@Getter(value = OWNER_KEY)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBGraph getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBGraph graph);

	@Getter(value = EXPRESSION_KEY)
	@XMLAttribute
	public DataBinding<?> getExpression();

	@Setter(EXPRESSION_KEY)
	public void setExpression(DataBinding<?> value);

	@Getter(value = GRAPH_TYPE_KEY)
	@XMLAttribute
	public GraphType getGraphType();

	@Setter(GRAPH_TYPE_KEY)
	public void setGraphType(GraphType graphType);

	@Getter(value = FOREGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getForegroundColor();

	@Setter(FOREGROUND_COLOR_KEY)
	public void setForegroundColor(Color foregroundColor);

	@Getter(value = BACKGROUND_TYPE_KEY)
	@XMLAttribute
	public FunctionBackgroundType getBackgroundType();

	@Setter(BACKGROUND_TYPE_KEY)
	public void setBackgroundType(FunctionBackgroundType backgroundType);

	@Getter(value = BACKGROUND_COLOR_1_KEY)
	@XMLAttribute
	public Color getBackgroundColor1();

	@Setter(BACKGROUND_COLOR_1_KEY)
	public void setBackgroundColor1(Color backgroundColor);

	@Getter(value = BACKGROUND_COLOR_2_KEY)
	@XMLAttribute
	public Color getBackgroundColor2();

	@Setter(BACKGROUND_COLOR_2_KEY)
	public void setBackgroundColor2(Color backgroundColor);

	public Type getType();

	public static abstract class FIBGraphFunctionImpl extends FIBModelObjectImpl implements FIBGraphFunction {

		private DataBinding<?> expression;

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		@Override
		public String getPresentationName() {
			if (getExpression() != null && getExpression().isSet() && getExpression().isValid()) {
				if (getOwner() instanceof FIBSimpleFunctionGraph) {
					return "y = " + getExpression().toString();
				}
				else if (getOwner() instanceof FIBPolarFunctionGraph) {
					return "r = " + getExpression().toString();
				}
				else {
					return getExpression().toString();
				}

			}
			return super.getPresentationName();
		}

		@Override
		public DataBinding<?> getExpression() {
			if (expression == null) {
				expression = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
				expression.setBindingName(getName());
				expression.setMandatory(true);
			}
			return expression;
		}

		@Override
		public void setExpression(DataBinding<?> expression) {
			if (expression != null) {
				this.expression = new DataBinding<Object>(expression.toString(), this, Object.class, DataBinding.BindingDefinitionType.GET);
				this.expression.setBindingName(getName());
				expression.setMandatory(true);
			}
			else {
				this.expression = null;
			}
			getPropertyChangeSupport().firePropertyChange(EXPRESSION_KEY, null, getExpression());
			getPropertyChangeSupport().firePropertyChange(TYPE_KEY, null, getType());
		}

		@Override
		public Type getType() {
			if (getExpression() != null && getExpression().isSet() && getExpression().isValid()) {
				return getExpression().getAnalyzedType();
			}
			return Object.class;
		}

		@Override
		public BindingModel getBindingModel() {
			if (getOwner() != null) {
				return getOwner().getGraphBindingModel();
			}
			return null;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			super.notifiedBindingChanged(binding);
			if (binding == expression) {
				getPropertyChangeSupport().firePropertyChange(EXPRESSION_KEY, null, getExpression());
				getPropertyChangeSupport().firePropertyChange(TYPE_KEY, null, getType());
				getPropertyChangeSupport().firePropertyChange("presentationName", null, getPresentationName());
			}
		}

		@Override
		public Color getForegroundColor() {
			Color returned = (Color) performSuperGetter(FOREGROUND_COLOR_KEY);
			if (returned == null) {
				return DEFAULT_FOREGROUND;
			}
			return returned;
		}

		@Override
		public Color getBackgroundColor1() {
			Color returned = (Color) performSuperGetter(BACKGROUND_COLOR_1_KEY);
			if (returned == null) {
				return DEFAULT_BACKGROUND_1;
			}
			return returned;
		}

		@Override
		public Color getBackgroundColor2() {
			Color returned = (Color) performSuperGetter(BACKGROUND_COLOR_2_KEY);
			if (returned == null) {
				return DEFAULT_BACKGROUND_2;
			}
			return returned;
		}

		@Override
		public FunctionBackgroundType getBackgroundType() {
			FunctionBackgroundType returned = (FunctionBackgroundType) performSuperGetter(BACKGROUND_TYPE_KEY);
			if (returned == null) {
				return DEFAULT_BACKGROUND_TYPE;
			}
			return returned;
		}
	}
}

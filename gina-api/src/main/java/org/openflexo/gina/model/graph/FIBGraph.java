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
import java.util.List;

import org.openflexo.connie.BindingModel;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the common super class for all graphs
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FIBGraph.FIBGraphImpl.class)
@Imports({ @Import(FIBContinuousSimpleFunctionGraph.class), @Import(FIBDiscreteSimpleFunctionGraph.class) })
public interface FIBGraph extends FIBWidget {

	public static final int DEFAULT_BORDER_TOP = 10;
	public static final int DEFAULT_BORDER_BOTTOM = 10;
	public static final int DEFAULT_BORDER_RIGHT = 10;
	public static final int DEFAULT_BORDER_LEFT = 10;

	@PropertyIdentifier(type = List.class)
	public static final String FUNCTIONS_KEY = "functions";

	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_TOP_KEY = "borderTop";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_BOTTOM_KEY = "borderBottom";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_LEFT_KEY = "borderLeft";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_RIGHT_KEY = "borderRight";

	@Getter(value = FUNCTIONS_KEY, cardinality = Cardinality.LIST, inverse = FIBGraphFunction.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBGraphFunction> getFunctions();

	@Setter(FUNCTIONS_KEY)
	public void setFunctions(List<FIBGraphFunction> functions);

	@Adder(FUNCTIONS_KEY)
	public void addToFunctions(FIBGraphFunction aFunction);

	@Remover(FUNCTIONS_KEY)
	public void removeFromFunctions(FIBGraphFunction aFunction);

	@Getter(value = BORDER_TOP_KEY, defaultValue = "" + DEFAULT_BORDER_TOP)
	@XMLAttribute
	public int getBorderTop();

	@Setter(value = BORDER_TOP_KEY)
	public void setBorderTop(int top);

	@Getter(value = BORDER_BOTTOM_KEY, defaultValue = "" + DEFAULT_BORDER_BOTTOM)
	@XMLAttribute
	public int getBorderBottom();

	@Setter(value = BORDER_BOTTOM_KEY)
	public void setBorderBottom(int bottom);

	@Getter(value = BORDER_LEFT_KEY, defaultValue = "" + DEFAULT_BORDER_LEFT)
	@XMLAttribute
	public int getBorderLeft();

	@Setter(value = BORDER_LEFT_KEY)
	public void setBorderLeft(int left);

	@Getter(value = BORDER_RIGHT_KEY, defaultValue = "" + DEFAULT_BORDER_RIGHT)
	@XMLAttribute
	public int getBorderRight();

	@Setter(value = BORDER_RIGHT_KEY)
	public void setBorderRight(int right);

	public BindingModel getGraphBindingModel();

	public static abstract class FIBGraphImpl extends FIBWidgetImpl implements FIBGraph {

		@Override
		public String getBaseName() {
			return "Graph";
		}

		@Override
		public Type getDefaultDataType() {
			return Object.class;
		}

	}
}

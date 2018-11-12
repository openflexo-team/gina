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

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;

/**
 * A graph determined by a unique parameter on which we iterate<br>
 * This parameter might be continuous or take values on a discrete set of values
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FIBSingleParameteredGraph.FIBSingleParameteredGraphImpl.class)
public interface FIBSingleParameteredGraph extends FIBGraph {

	@PropertyIdentifier(type = String.class)
	public static final String PARAMETER_NAME_KEY = "parameterName";

	@Getter(value = PARAMETER_NAME_KEY)
	@XMLAttribute
	public String getParameterName();

	@Setter(PARAMETER_NAME_KEY)
	public void setParameterName(String parameterName);

	public Type getParameterType();

	public static abstract class FIBSingleParameteredGraphImpl extends FIBGraphImpl implements FIBSingleParameteredGraph {

		private BindingModel graphBindingModel;
		protected BindingVariable parameterBindingVariable;

		@Override
		public BindingModel getGraphBindingModel() {
			if (graphBindingModel == null) {
				createGraphBindingModel();
			}
			return graphBindingModel;
		}

		private BindingModel createGraphBindingModel() {
			graphBindingModel = new BindingModel(getBindingModel());
			parameterBindingVariable = new BindingVariable(getParameterName(), getParameterType());
			parameterBindingVariable.setCacheable(false);
			graphBindingModel.addToBindingVariables(parameterBindingVariable);
			return graphBindingModel;
		}

		@Override
		public void setParameterName(String parameterName) {
			performSuperSetter(PARAMETER_NAME_KEY, parameterName);
			if (parameterBindingVariable != null) {
				parameterBindingVariable.setVariableName(parameterName);
			}
		}

	}
}

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

package org.openflexo.gina.model.operator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBindingFactory;
import org.openflexo.gina.model.FIBOperator;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.bindings.FIBIterationBindingModel;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import com.google.common.reflect.TypeToken;

/**
 * Represents an iteration over a given list of objects
 *
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(FIBIteration.FIBIterationImpl.class)
@XMLElement(xmlTag = "Iteration")
public interface FIBIteration extends FIBOperator {

	public static final String DEFAULT_ITERATOR_NAME = "iterator";

	@PropertyIdentifier(type = String.class)
	public static final String ITERATOR_NAME_KEY = "iterator";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LIST_KEY = "list";
	@PropertyIdentifier(type = Type.class)
	public static final String ITERATOR_TYPE_KEY = "iteratorType";

	@Getter(value = LIST_KEY)
	@XMLAttribute
	@CloningStrategy(value = StrategyType.REFERENCE, cloneAfterProperty = ITERATOR_TYPE_KEY)
	public DataBinding<List<?>> getList();

	@Setter(LIST_KEY)
	public void setList(DataBinding<List<?>> list);

	@Getter(value = ITERATOR_TYPE_KEY, isStringConvertable = true)
	@XMLAttribute(xmlTag = "iteratorClassName")
	public Type getIteratorType();

	@Setter(ITERATOR_TYPE_KEY)
	public void setIteratorType(Type iteratorType);

	@Getter(value = ITERATOR_NAME_KEY)
	@XMLAttribute
	public String getIteratorName();

	@Setter(ITERATOR_NAME_KEY)
	public void setIteratorName(String iteratorName);

	// public BindingModel getIterationBindingModel();

	public Type getInferedIteratorType();

	public static abstract class FIBIterationImpl extends FIBOperatorImpl implements FIBIteration {

		private static final Logger logger = Logger.getLogger(FIBIteration.class.getPackage().getName());

		private Type iteratorType;
		private DataBinding<List<?>> list;
		private FIBIterationBindingModel iterationBindingModel;

		@Override
		public String getIteratorName() {
			String returned = (String) performSuperGetter(ITERATOR_NAME_KEY);
			if (returned == null) {
				return DEFAULT_ITERATOR_NAME;
			}
			return returned;
		}

		@Override
		public DataBinding<List<?>> getList() {
			if (list == null) {
				list = DataBindingFactory.makeListBinding(this);
			}
			return list;
		}

		@Override
		public void setList(DataBinding<List<?>> list) {
			if (list != null) {
				list.setOwner(this);
				list.setDeclaredType(new TypeToken<List<?>>() {
				}.getType());
				list.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.list = list;
		}

		/*private Type LIST_BINDING_TYPE;
		
		private Type getListBindingType() {
			if (LIST_BINDING_TYPE == null) {
				LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(getIteratorType()));
			}
			return LIST_BINDING_TYPE;
		}*/

		@Override
		public Type getInferedIteratorType() {
			// Attempt to infer iterator type
			// System.out.println("getList()=" + getList());
			// System.out.println("getList().isValid()=" + getList().isValid());
			// System.out.println("getList().invalidBindingReason()=" + getList().invalidBindingReason());
			if (getList() != null && getList().isSet() && getList().isValid()) {
				Type accessedType = getList().getAnalyzedType();
				// System.out.println("accessed type=" + accessedType);
				if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
					return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
				}
			}
			return null;
		}

		@Override
		public Type getIteratorType() {

			if (iteratorType == null) {
				Type infered = getInferedIteratorType();
				if (infered != null) {
					return infered;
				}
				return Object.class;
			}
			return iteratorType;
		}

		@Override
		public void setIteratorType(Type iteratorType) {
			FIBPropertyNotification<Type> notification = requireChange(ITERATOR_TYPE_KEY, iteratorType);
			if (notification != null) {
				// LIST_BINDING_TYPE = null;
				this.iteratorType = iteratorType;
				hasChanged(notification);
			}
		}

		/*@Override
		public Type getDataType() {
			if (isStaticList()) {
				return String.class;
			}
			if (iteratorClass != null) {
				return iteratorClass;
			}
			return super.getDataType();
		}*/

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			logger.fine("notifyBindingChanged with " + binding);
			super.notifiedBindingChanged(binding);
			if (binding == getList() && getList() != null) {
				getPropertyChangeSupport().firePropertyChange(ITERATOR_TYPE_KEY, null, getIteratorType());
				if (iterationBindingModel != null) {
					iterationBindingModel.getBindingVariableAt(0).setType(getIteratorType());
				}
			}
		}

		@Override
		public FIBIterationBindingModel getInferedBindingModel() {
			if (iterationBindingModel == null) {
				createIterationBindingModel();
			}
			return iterationBindingModel;
		}

		private void createIterationBindingModel() {
			iterationBindingModel = new FIBIterationBindingModel(this);

			// BindingVariable iteratorVariable = new BindingVariable(getIteratorName(), getIteratorType());
			// iteratorVariable.setCacheable(false);

			// iterationBindingModel.addToBindingVariables(iteratorVariable);
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (getList() != null && getList().isValid()) {
				if (iterationBindingModel != null) {
					iterationBindingModel.getBindingVariableAt(0).setType(getInferedIteratorType());
				}
			}
			if (list != null) {
				list.revalidate();
			}
		}

		@Override
		protected FIBIterationType makeViewType() {
			return new FIBIterationType(this);
		}

	}
}

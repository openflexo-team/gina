/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.gina.model;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * A {@link FIBVariable} allows to define an accessible and named value in a {@link FIBComponent}<br>
 * 
 * They are mainly two cases in which variables are usefull:
 * <ul>
 * <li>to define an external API for a component</li>
 * <li>to maintain some internal value inside the scope of a {@link FIBComponent}</li>
 * </ul>
 * 
 * @param T
 *            type of value represented by this variable
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FIBVariable.FIBVariableImpl.class)
@XMLElement
public interface FIBVariable<T> extends FIBModelObject {

	@PropertyIdentifier(type = FIBComponent.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";
	@PropertyIdentifier(type = Type.class)
	public static final String TYPE_KEY = "type";
	@PropertyIdentifier(type = Boolean.class)
	public static final String MANDATORY_KEY = "mandatory";

	@Getter(value = OWNER_KEY)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBComponent getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBComponent ownerComponent);

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<T> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<T> value);

	@Getter(value = TYPE_KEY, isStringConvertable = true)
	@XMLAttribute
	public Type getType();

	@Setter(TYPE_KEY)
	public void setType(Type type);

	/*public Class<T> getTypeClass();
	
	public void setTypeClass(Class<T> typeClass);*/

	@Getter(value = MANDATORY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isMandatory();

	@Setter(MANDATORY_KEY)
	public void setMandatory(boolean mandatory);

	// public BindingVariable getBindingVariable();

	// public BindingVariable appendToBindingModel(BindingModel bindingModel);

	public void revalidateBindings();

	public static abstract class FIBVariableImpl<T> extends FIBModelObjectImpl implements FIBVariable<T> {

		private static final Logger LOGGER = Logger.getLogger(FIBVariable.class.getPackage().getName());

		private DataBinding<T> value;

		/*@Override
		public void setName(String name) {
			performSuperSetter(NAME_KEY, name);
			getBindingVariable().setVariableName(getName());
		}*/

		private Type variableType = null;

		@Override
		public Type getType() {
			// System.out.println("On me demande mon type " + getName() + " value=" + getValue());
			if (variableType == null && !isCreatedByCloning() && getOwner() != null && getOwner().getRootComponent() != null
					&& !getOwner().getRootComponent().isDeserializing() && !getOwner().getRootComponent().isCreatedByCloning()
					&& getValue() != null && getValue().isSet() && getValue().isValid()) {
				variableType = getValue().getAnalyzedType();
			}
			// Type returned = (Type) performSuperGetter(TYPE_KEY);
			if (variableType != null) {
				return variableType;
			}
			return Object.class;
		}

		@Override
		public void setType(Type type) {
			if (getValue() != null && getValue().isSet() && getValue().isValid()) {
				if (!TypeUtils.isTypeAssignableFrom(getValue().getAnalyzedType(), type, true)) {
					// supplied type is not compatible with analysed type as infered from value binding, abort
					return;
				}
			}
			// Class<T> oldTypeClass = getTypeClass();
			Type oldType = variableType;
			variableType = type;
			getPropertyChangeSupport().firePropertyChange(TYPE_KEY, oldType, variableType);
			// getPropertyChangeSupport().firePropertyChange("typeClass", oldTypeClass, getTypeClass());
		}

		@Override
		public DataBinding<T> getValue() {
			if (value == null) {
				value = new DataBinding<>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET) {
					@Override
					public void notifyBindingChanged(Expression oldValue, Expression newValue) {
						super.notifyBindingChanged(oldValue, newValue);
						/*getPropertyChangeSupport().firePropertyChange(TYPE_KEY, null, getType());
						getPropertyChangeSupport().firePropertyChange("typeClass", null, getTypeClass());*/
						if (isSet() && isValid()) {
							setType(getAnalyzedType());
						}
					}
				};
				value.setBindingName(getName());
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<T> value) {

			if (value != null) {
				getValue().setUnparsedBinding(value.getUnparsedBinding());
			}
			else {
				this.value = null;
			}
		}

		// private BindingVariable bindingVariable;

		/*@Override
		public BindingVariable getBindingVariable() {
			if (bindingVariable == null) {
				bindingVariable = new BindingVariable(getName(), getType(), true);
			}
			return bindingVariable;
		}*/

		/**
		 * Return (create when null) binding variable identified by "data"<br>
		 * Default behavior is to generate a binding variable with the java type identified by data class
		 */
		/*@Override
		public BindingVariable appendToBindingModel(BindingModel bindingModel) {
			bindingModel.addToBindingVariables(getBindingVariable());
			return getBindingVariable();
		}*/

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		@Override
		public String toString() {
			return getName() + "/" + TypeUtils.fullQualifiedRepresentation(getType());
		}

		/*@Override
		public Class<T> getTypeClass() {
			return (Class<T>) TypeUtils.getBaseClass(getType());
		}
		
		@Override
		public void setTypeClass(Class<T> typeClass) {
			setType(typeClass);
		}*/

		@Override
		public void setOwner(FIBComponent ownerComponent) {
			performSuperSetter(FIBVariable.OWNER_KEY, ownerComponent);
			if (getValue() != null) {
				getValue().setOwner(ownerComponent);
			}
		}

		@Override
		public void revalidateBindings() {
			if (value != null) {
				value.forceRevalidate();
			}
		}

	}

	@DefineValidationRule
	public static class ValueBindingMustBeValid extends BindingMustBeValid<FIBVariable> {
		public ValueBindingMustBeValid() {
			super("'value'_binding_is_not_valid", FIBVariable.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBVariable object) {
			return object.getValue();
		}

	}

}

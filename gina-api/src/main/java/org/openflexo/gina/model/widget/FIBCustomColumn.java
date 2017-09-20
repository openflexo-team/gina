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

package org.openflexo.gina.model.widget;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent.CustomComponentParameter;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomImpl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBCustomColumn.FIBCustomColumnImpl.class)
@XMLElement(xmlTag = "CustomColumn")
public interface FIBCustomColumn extends FIBTableColumn {

	@PropertyIdentifier(type = Class.class)
	public static final String COMPONENT_CLASS_KEY = "componentClass";
	@PropertyIdentifier(type = List.class)
	public static final String ASSIGNMENTS_KEY = "assignments";
	@PropertyIdentifier(type = String.class)
	public static final String CUSTOM_RENDERING_KEY = "customRendering";
	@PropertyIdentifier(type = String.class)
	public static final String DISABLE_TERMINATE_EDIT_ON_FOCUS_LOST = "disableTerminateEditOnFocusLost";

	@Getter(value = COMPONENT_CLASS_KEY)
	@XMLAttribute(xmlTag = "componentClassName")
	public Class<?> getComponentClass();

	@Setter(value = COMPONENT_CLASS_KEY)
	public void setComponentClass(Class<?> componentClass);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBCustomAssignment.OWNER_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBCustomAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<FIBCustomAssignment> parameters);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(FIBCustomAssignment aParameter);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(FIBCustomAssignment aParameter);

	@Getter(value = CUSTOM_RENDERING_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isCustomRendering();

	@Setter(CUSTOM_RENDERING_KEY)
	public void setCustomRendering(boolean customRendering);

	@Getter(value = DISABLE_TERMINATE_EDIT_ON_FOCUS_LOST, defaultValue = "false")
	@XMLAttribute
	public boolean isDisableTerminateEditOnFocusLost();

	@Setter(DISABLE_TERMINATE_EDIT_ON_FOCUS_LOST)
	public void setDisableTerminateEditOnFocusLost(boolean disableTerminateEditOnFocusLost);

	public BindingModel getCustomComponentBindingModel();

	public static abstract class FIBCustomColumnImpl extends FIBTableColumnImpl implements FIBCustomColumn {

		private static final Logger logger = Logger.getLogger(FIBCustomColumn.class.getPackage().getName());

		private Class<?> componentClass;
		private boolean customRendering = false;

		private boolean disableTerminateEditOnFocusLost = false;

		private Vector<FIBCustomAssignment> assignments;

		public FIBCustomColumnImpl() {
			assignments = new Vector<>();
		}

		@Override
		public Class<?> getComponentClass() {
			return componentClass;

		}

		@Override
		public void setComponentClass(Class componentClass) {
			FIBPropertyNotification<Class> notification = requireChange(COMPONENT_CLASS_KEY, componentClass);
			if (notification != null) {
				this.componentClass = componentClass;
				if (componentClass != null) {
					createCustomComponentBindingModel();
					for (Method m : componentClass.getMethods()) {
						CustomComponentParameter annotation = m.getAnnotation(CustomComponentParameter.class);
						if (annotation != null) {
							String variableName = FIBCustomImpl.COMPONENT_NAME + "." + annotation.name();
							if (!hasAssignment(variableName)) {
								FIBCustomAssignment newAssignment = getModelFactory().newInstance(FIBCustomAssignment.class);
								newAssignment.setOwner(this);
								newAssignment.setMandatory(annotation.type() == CustomComponentParameter.Type.MANDATORY);
								newAssignment.setVariable(new DataBinding<>(variableName));
								newAssignment.setValue(null);
								addToAssignments(newAssignment);
							}
						}

					}
				}
				hasChanged(notification);
			}
		}

		@Override
		public Type getDefaultDataClass() {
			return Object.class;
		}

		public boolean hasAssignment(String variableName) {
			return getAssignent(variableName) != null;
		}

		public FIBCustomAssignment getAssignent(String variableName) {
			for (FIBCustomAssignment a : assignments) {
				if (variableName.equals(a.getVariable().toString())) {
					return a;
				}
			}
			return null;
		}

		@Override
		public Vector<FIBCustomAssignment> getAssignments() {
			return assignments;
		}

		public void setAssignments(Vector<FIBCustomAssignment> assignments) {
			this.assignments = assignments;
		}

		@Override
		public void addToAssignments(FIBCustomAssignment p) {
			if (getAssignent(p.getVariable().toString()) != null) {
				removeFromAssignments(getAssignent(p.getVariable().toString()));
			}
			p.setOwner(this);
			assignments.add(p);
		}

		@Override
		public void removeFromAssignments(FIBCustomAssignment p) {
			p.setOwner(null);
			assignments.remove(p);
		}

		@Override
		public boolean isCustomRendering() {
			return customRendering;
		}

		@Override
		public void setCustomRendering(boolean customRendering) {
			this.customRendering = customRendering;
		}

		@Override
		public boolean isDisableTerminateEditOnFocusLost() {
			return disableTerminateEditOnFocusLost;
		}

		@Override
		public void setDisableTerminateEditOnFocusLost(boolean disableTerminateEditOnFocusLost) {
			this.disableTerminateEditOnFocusLost = disableTerminateEditOnFocusLost;
		}

		private BindingModel customComponentBindingModel;

		@Override
		public BindingModel getCustomComponentBindingModel() {
			if (customComponentBindingModel == null) {
				createCustomComponentBindingModel();
			}
			return customComponentBindingModel;
		}

		private void createCustomComponentBindingModel() {
			customComponentBindingModel = new BindingModel();

			customComponentBindingModel.addToBindingVariables(new BindingVariable(FIBCustomImpl.COMPONENT_NAME, getComponentClass()));
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
		}

		@Override
		public void finalizeTableDeserialization() {
			super.finalizeTableDeserialization();
			for (FIBCustomAssignment assign : getAssignments()) {
				assign.finalizeDeserialization();
			}
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.Custom;
		}

		@Override
		public Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return getAssignments();
		}
	}

	@ModelEntity
	@ImplementationClass(FIBCustomAssignment.FIBCustomAssignmentImpl.class)
	@XMLElement(xmlTag = "ColumnAssignment")
	public static interface FIBCustomAssignment extends FIBModelObject {
		@PropertyIdentifier(type = FIBCustomColumn.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY /*, inverse = FIBCustomColumn.ASSIGNMENTS_KEY*/)
		@CloningStrategy(StrategyType.IGNORE)
		public FIBCustomColumn getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBCustomColumn customColumn);

		@Getter(value = VARIABLE_KEY)
		@XMLAttribute
		public DataBinding<Object> getVariable();

		@Setter(VARIABLE_KEY)
		public void setVariable(DataBinding<Object> variable);

		@Getter(value = VALUE_KEY)
		@XMLAttribute
		public DataBinding<Object> getValue();

		@Setter(VALUE_KEY)
		public void setValue(DataBinding<Object> value);

		@Getter(value = MANDATORY_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isMandatory();

		@Setter(MANDATORY_KEY)
		public void setMandatory(boolean mandatory);

		@DeserializationFinalizer
		public void finalizeDeserialization();

		public static abstract class FIBCustomAssignmentImpl extends FIBModelObjectImpl implements FIBCustomAssignment {
			private DataBinding<Object> variable;
			private DataBinding<Object> value;

			private final boolean mandatory = true;

			public FIBCustomAssignmentImpl() {
			}

			/*public FIBCustomAssignmentImpl(FIBCustomColumn customColumn, DataBinding<Object> variable, DataBinding<Object> value,
					boolean mandatory) {
				this();
				this.mandatory = mandatory;
				setVariable(variable);
				setValue(value);
			}*/

			@Override
			public boolean isMandatory() {
				return mandatory;
			}

			@Override
			public void setOwner(FIBCustomColumn customColumn) {
				performSuperSetter(OWNER_KEY, customColumn);
				if (value != null) {
					value.setOwner(customColumn);
				}
			}

			@Override
			public FIBComponent getComponent() {
				if (getOwner() != null) {
					return getOwner().getComponent();
				}
				return null;
			}

			@Override
			public DataBinding<Object> getVariable() {
				if (variable == null) {
					variable = new DataBinding<>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET);
				}
				return variable;
			}

			@Override
			public void setVariable(DataBinding<Object> variable) {
				if (variable != null) {
					variable.setOwner(this);
					variable.setDeclaredType(Object.class);
					variable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
				}
				this.variable = variable;
				if (getBindingFactory() != null) {
					if (getOwner() != null && variable != null) {
						variable.decode();
					}
				}
			}

			@Override
			public DataBinding<Object> getValue() {
				if (value == null) {
					value = new DataBinding<>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
				}
				return value;
			}

			@Override
			public void setValue(DataBinding<Object> value) {
				if (value != null) {
					value.setOwner(getOwner()); // Warning, still null while deserializing
					value.setDeclaredType(Object.class);
					value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					this.value = value;
				}
				else {
					getValue();
				}
			}

			@Override
			public void finalizeDeserialization() {

				if (variable != null) {
					variable.decode();
				}
				if (value != null) {
					value.setOwner(getOwner());
					value.decode();
				}
			}

			@Override
			public BindingModel getBindingModel() {
				if (getOwner() != null) {
					return getOwner().getCustomComponentBindingModel();
				}
				return null;
			}

			@Override
			public BindingFactory getBindingFactory() {
				// TODO Auto-generated method stub
				return super.getBindingFactory();
			}

		}

		@DefineValidationRule
		public static class AssignmentVariableBindingMustBeValid extends BindingMustBeValid<FIBCustomAssignment> {
			public AssignmentVariableBindingMustBeValid() {
				super("assignment_'variable'_binding_is_not_valid", FIBCustomAssignment.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBCustomAssignment object) {
				return object.getVariable();
			}

		}

		@DefineValidationRule
		public static class AssignmentValueBindingMustBeValid extends BindingMustBeValid<FIBCustomAssignment> {
			public AssignmentValueBindingMustBeValid() {
				super("assignment_'value'_binding_is_not_valid", FIBCustomAssignment.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBCustomAssignment object) {
				return object.getValue();
			}

		}

	}

}

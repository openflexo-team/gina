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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

@ModelEntity
@ImplementationClass(FIBCustom.FIBCustomImpl.class)
@XMLElement(xmlTag = "Custom")
public interface FIBCustom extends FIBWidget {

	/**
	 * Minimal API for a technology-specific component embeddable in a FIBCustom
	 * 
	 * @author sylvain
	 *
	 * @param <V>
	 */
	public static interface FIBCustomComponent<V> {
		@Documented
		@Retention(RetentionPolicy.RUNTIME)
		public @interface CustomComponentParameter {
			/** name of parameter */
			String name();

			/** type of parameter */
			Type type();

			/** Enumeration of different types */
			public static enum Type {
				MANDATORY, OPTIONAL
			};
		}

		public V getEditedObject();

		public void setEditedObject(V object);

		public V getRevertValue();

		public void setRevertValue(V object);

		public void addApplyCancelListener(ApplyCancelListener l);

		public void removeApplyCancelListener(ApplyCancelListener l);

		public Class<V> getRepresentedType();

		public void init(FIBCustom component, FIBController controller);

		public void delete();
	}

	@PropertyIdentifier(type = Class.class)
	public static final String COMPONENT_CLASS_KEY = "componentClass";
	@PropertyIdentifier(type = Class.class)
	public static final String DATA_CLASS_FOR_COMPONENT_KEY = "dataClassForComponent";
	@PropertyIdentifier(type = FIBCustomAssignment.class, cardinality = Cardinality.LIST)
	public static final String ASSIGNMENTS_KEY = "assignments";

	@Getter(value = COMPONENT_CLASS_KEY)
	@XMLAttribute(xmlTag = "componentClassName")
	public Class<?> getComponentClass();

	@Setter(COMPONENT_CLASS_KEY)
	public void setComponentClass(Class<?> componentClass);

	@Getter(value = DATA_CLASS_FOR_COMPONENT_KEY)
	@XMLAttribute
	public Class<?> getDataClassForComponent();

	@Setter(DATA_CLASS_FOR_COMPONENT_KEY)
	public void setDataClassForComponent(Class<?> dataClass);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBCustomAssignment.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBCustomAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<FIBCustomAssignment> assignments);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(FIBCustomAssignment aAssignment);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(FIBCustomAssignment aAssignment);

	@Finder(collection = ASSIGNMENTS_KEY, attribute = FIBCustomAssignment.VARIABLE_KEY)
	public FIBCustomAssignment getAssignent(String variableName);

	public BindingModel getCustomComponentBindingModel();

	public FIBCustomAssignment createAssignment();

	public FIBCustomAssignment deleteAssignment(FIBCustomAssignment assignment);

	public static abstract class FIBCustomImpl extends FIBWidgetImpl implements FIBCustom {

		private static final Logger logger = Logger.getLogger(FIBCustom.class.getPackage().getName());

		public static final String COMPONENT_NAME = "component";

		private Class<?> componentClass;
		private Class<?> dataClassForComponent;
		private final Class<?> defaultDataClass = null;

		private List<FIBCustomAssignment> assignments;

		public FIBCustomImpl() {
			assignments = new Vector<>();
		}

		@Override
		public String getBaseName() {
			return "CustomSelector";
		}

		@Override
		public Type getDataType() {
			/*
			 * if (getData() != null && getData().isSet() &&
			 * getData().isValid()) { return getData().getAnalyzedType(); }
			 */
			return getDefaultDataType();

		}

		@Override
		public Type getDefaultDataType() {
			if (getDataClassForComponent() != null) {
				return getDataClassForComponent();
			}
			return Object.class;
		}

		@Override
		public Class<?> getComponentClass() {
			return componentClass;

		}

		@Override
		public void setComponentClass(Class<?> componentClass) {
			FIBPropertyNotification<?> notification = requireChange(COMPONENT_CLASS_KEY, componentClass);
			if (notification != null) {
				this.componentClass = componentClass;
				if (componentClass != null) {
					customComponentBindingModel = null;
					createCustomComponentBindingModel();
					for (Method m : componentClass.getMethods()) {
						FIBCustomComponent.CustomComponentParameter annotation = m
								.getAnnotation(FIBCustomComponent.CustomComponentParameter.class);
						if (annotation != null) {
							String variableName = COMPONENT_NAME + "." + annotation.name();
							if (!hasAssignment(variableName)) {
								FIBCustomAssignment newAssigment = getModelFactory().newInstance(FIBCustomAssignment.class);
								newAssigment.setOwner(this);
								newAssigment.setVariable(new DataBinding<>(variableName));
								newAssigment.setValue(null);
								newAssigment.setMandatory(annotation.type() == FIBCustomComponent.CustomComponentParameter.Type.MANDATORY);
								addToAssignments(newAssigment);
							}
						}

					}
				}
				hasChanged(notification);
			}
		}

		@Override
		public Class<?> getDataClassForComponent() {
			if (dataClassForComponent == null && getComponentClass() != null && !isDeserializing()) {
				dataClassForComponent = getDataClassForComponent(getComponentClass());
			}
			return dataClassForComponent;
		}

		@Override
		public void setDataClassForComponent(Class<?> dataClassForComponent) {

			if ((dataClassForComponent == null && this.dataClassForComponent != null)
					|| (dataClassForComponent != null && !dataClassForComponent.equals(this.dataClassForComponent))) {
				Class<?> oldValue = this.dataClassForComponent;
				this.dataClassForComponent = dataClassForComponent;
				getPropertyChangeSupport().firePropertyChange("dataClassForComponent", oldValue, dataClassForComponent);
			}
		}

		@Override
		protected FIBCustomType makeViewType() {
			return new FIBCustomType(this);
		}

		public boolean hasAssignment(String variableName) {
			return getAssignent(variableName) != null;
		}

		@Override
		public FIBCustomAssignment getAssignent(String variableName) {
			for (FIBCustomAssignment a : assignments) {
				if (variableName != null && variableName.equals(a.getVariable().toString())) {
					return a;
				}
			}
			return null;
		}

		@Override
		public List<FIBCustomAssignment> getAssignments() {
			return assignments;
		}

		@Override
		public void setAssignments(List<FIBCustomAssignment> assignments) {
			this.assignments = assignments;
		}

		@Override
		public void addToAssignments(FIBCustomAssignment a) {
			if (getAssignent(a.getVariable().toString()) != null) {
				removeFromAssignments(getAssignent(a.getVariable().toString()));
				performSuperAdder(ASSIGNMENTS_KEY, a);
				return;
			}
			/*
			 * a.setOwner(this); assignments.add(a);
			 * getPropertyChangeSupport().firePropertyChange(ASSIGNMENTS_KEY,
			 * null, assignments);
			 */
			performSuperAdder(ASSIGNMENTS_KEY, a);
		}

		/*
		 * @Override public void removeFromAssignments(FIBCustomAssignment a) {
		 * a.setOwner(null); assignments.remove(a);
		 * getPropertyChangeSupport().firePropertyChange(ASSIGNMENTS_KEY, null,
		 * assignments); }
		 */

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

			customComponentBindingModel.addToBindingVariables(new BindingVariable(COMPONENT_NAME, getComponentClass()));
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			for (FIBCustomAssignment assign : getAssignments()) {
				assign.revalidateBindings();
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			for (FIBCustomAssignment assign : getAssignments()) {
				assign.finalizeDeserialization();
			}
		}

		/*
		 * @Override public Type getDynamicAccessType() { Type[] args = new
		 * Type[2]; args[0] = getComponentClass(); args[1] = getDataType();
		 * return new ParameterizedTypeImpl(FIBCustomWidget.class, args); }
		 */

		@Override
		public boolean getManageDynamicModel() {
			return true;
		}

		@Override
		public FIBCustomAssignment createAssignment() {
			logger.info("Called createAssignment()");
			FIBCustomAssignment newAssignment = getModelFactory().newInstance(FIBCustomAssignment.class);
			addToAssignments(newAssignment);
			return newAssignment;
		}

		@Override
		public FIBCustomAssignment deleteAssignment(FIBCustomAssignment assignment) {
			logger.info("Called deleteAssignment() with " + assignment);
			removeFromAssignments(assignment);
			return assignment;
		}

		private static final Hashtable<Class<?>, Class<?>> DATA_CLASS_FOR_COMPONENT = new Hashtable<>();

		/**
		 * Stuff to retrieve default data class from component class<br>
		 * NB: this is STATIC !!!!<br>
		 * NB2: this is really cpu-expansive: take care to use of this
		 * 
		 * @param componentClass
		 * @return
		 */
		private static Class<?> getDataClassForComponent(Class<?> componentClass) {
			Class<?> returned = DATA_CLASS_FOR_COMPONENT.get(componentClass);
			if (returned == null) {
				logger.fine("Searching dataClass for " + componentClass);
				FIBCustomComponent<?> customComponent = null;
				for (Constructor<?> constructor : componentClass.getConstructors()) {
					if (constructor.getGenericParameterTypes().length == 1) {
						Object[] args = new Object[1];
						args[0] = null;
						try {
							customComponent = (FIBCustomComponent<?>) constructor.newInstance(args);
							break;
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logger.warning("While trying to instanciate " + componentClass + " with null");
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassCastException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				logger.fine("customComponent=" + customComponent);
				if (customComponent != null) {
					returned = customComponent.getRepresentedType();
					DATA_CLASS_FOR_COMPONENT.put(componentClass, returned);
					logger.fine("Found " + returned);
					return returned;
				}
				return Object.class;
			}
			return returned;
		}

		@Override
		public Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return getAssignments();
		}

	}

	@ModelEntity
	@ImplementationClass(FIBCustomAssignment.FIBCustomAssignmentImpl.class)
	@XMLElement(xmlTag = "Assignment")
	public static interface FIBCustomAssignment extends FIBModelObject {
		@PropertyIdentifier(type = FIBCustomColumn.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY)
		@CloningStrategy(StrategyType.IGNORE)
		public FIBCustom getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBCustom customColumn);

		@Getter(value = VARIABLE_KEY)
		@XMLAttribute
		public DataBinding<?> getVariable();

		@Setter(VARIABLE_KEY)
		public void setVariable(DataBinding<?> variable);

		@Getter(value = VALUE_KEY)
		@XMLAttribute
		public DataBinding<?> getValue();

		@Setter(VALUE_KEY)
		public void setValue(DataBinding<?> value);

		@Getter(value = MANDATORY_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isMandatory();

		@Setter(MANDATORY_KEY)
		public void setMandatory(boolean mandatory);

		@DeserializationFinalizer
		public void finalizeDeserialization();

		public void revalidateBindings();

		public static abstract class FIBCustomAssignmentImpl extends FIBModelObjectImpl implements FIBCustomAssignment {
			@Deprecated
			public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class,
					DataBinding.BindingDefinitionType.GET_SET, true);
			@Deprecated
			public BindingDefinition VALUE = new BindingDefinition("value", Object.class, DataBinding.BindingDefinitionType.GET, true);

			private DataBinding<?> variable;
			private DataBinding<?> value;

			private final boolean mandatory = true;

			@Override
			public boolean isMandatory() {
				return mandatory;
			}

			@Override
			public void setOwner(FIBCustom custom) {
				performSuperSetter(OWNER_KEY, custom);
				if (value != null) {
					value.setOwner(custom);
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
			public DataBinding<?> getVariable() {
				if (variable == null) {
					variable = new DataBinding<>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET);
					variable.setBindingName("variable");
				}
				return variable;
			}

			@Override
			public void setVariable(DataBinding<?> variable) {
				if (variable != null) {
					variable.setOwner(this);
					variable.setDeclaredType(Object.class);
					variable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
					variable.setBindingName("variable");
				}
				this.variable = variable;
				if (getOwner() != null && variable != null) {
					variable.decode();
				}
				if (variable != null && variable.isValid()) {
					VALUE.setType(variable.getAnalyzedType());
					if (value != null) {
						value.setBindingDefinition(VALUE);
					}
				}
			}

			@Override
			public DataBinding<?> getValue() {
				if (value == null) {
					value = new DataBinding<>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
					value.setBindingName("value");
				}
				return value;
			}

			@Override
			public void setValue(DataBinding<?> value) {
				if (value != null) {
					value.setOwner(getOwner()); // Warning, still null while
												// deserializing
					value.setDeclaredType(Object.class);
					value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					value.setBindingName("value");
					this.value = value;
				}
				else {
					getValue();
				}
			}

			@Override
			public void revalidateBindings() {
				if (variable != null) {
					variable.revalidate();
				}
				if (value != null) {
					value.setOwner(getOwner());
					value.revalidate();
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

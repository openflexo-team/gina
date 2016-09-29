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

package org.openflexo.gina.model.widget;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingModel;
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
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;

@ModelEntity
@ImplementationClass(FIBReferencedComponent.FIBReferencedComponentImpl.class)
@XMLElement(xmlTag = "FIBReferencedComponent")
public interface FIBReferencedComponent extends FIBWidget {

	@PropertyIdentifier(type = File.class)
	public static final String COMPONENT_FILE_KEY = "componentFile";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DYNAMIC_COMPONENT_FILE_KEY = "dynamicComponentFile";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DYNAMIC_COMPONENT_KEY = "dynamicComponent";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CONTROLLER_FACTORY_KEY = "controllerFactory";
	@PropertyIdentifier(type = Vector.class)
	public static final String ASSIGNMENTS_KEY = "assignments";

	@Getter(value = COMPONENT_FILE_KEY, isStringConvertable = true)
	@XMLAttribute
	public Resource getComponentFile();

	@Setter(COMPONENT_FILE_KEY)
	public void setComponentFile(Resource componentFile);

	// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
	public File getComponentActualFile();

	public void setComponentActualFile(File file) throws MalformedURLException, LocatorNotFoundException;

	@Getter(value = DYNAMIC_COMPONENT_FILE_KEY)
	@XMLAttribute
	public DataBinding<Resource> getDynamicComponentFile();

	@Setter(DYNAMIC_COMPONENT_FILE_KEY)
	public void setDynamicComponentFile(DataBinding<Resource> dynamicComponentFile);

	@Getter(value = DYNAMIC_COMPONENT_KEY)
	@XMLAttribute
	public DataBinding<FIBComponent> getDynamicComponent();

	@Setter(DYNAMIC_COMPONENT_KEY)
	public void setDynamicComponent(DataBinding<FIBComponent> dynamicComponent);

	@Getter(value = CONTROLLER_FACTORY_KEY)
	@XMLAttribute
	public DataBinding<FIBController> getControllerFactory();

	@Setter(CONTROLLER_FACTORY_KEY)
	public void setControllerFactory(DataBinding<FIBController> controllerFactory);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBReferenceAssignment.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBReferenceAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<FIBReferenceAssignment> assignments);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(FIBReferenceAssignment aAssignment);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(FIBReferenceAssignment aAssignment);

	public FIBReferenceAssignment createAssignment();

	public FIBReferenceAssignment deleteAssignment(FIBReferenceAssignment assignment);

	public static abstract class FIBReferencedComponentImpl extends FIBWidgetImpl implements FIBReferencedComponent {

		private static final Logger logger = Logger.getLogger(FIBReferencedComponent.class.getPackage().getName());

		private Resource componentFile;
		private DataBinding<Resource> dynamicComponentFile;
		private DataBinding<FIBComponent> dynamicComponent;
		private DataBinding<FIBController> controllerFactory;

		// TODO: Should be moved to FIBReferencedComponent widget
		// private FIBComponent referencedComponent;
		// private Vector<FIBReferenceAssignment> assignments;

		public FIBReferencedComponentImpl() {
			// assignments = new Vector<FIBReferenceAssignment>();
		}

		@Override
		public String getBaseName() {
			return "ReferencedComponent";
		}

		@Override
		public Type getDefaultDataType() {
			/*if (referencedComponent != null) {
				return referencedComponent.getDataType();
			}*/
			return Object.class;
		}

		@Override
		public Resource getComponentFile() {
			return componentFile;
		}

		@Override
		public void setComponentFile(Resource componentFile) {
			FIBPropertyNotification<Resource> notification = requireChange(COMPONENT_FILE_KEY, componentFile);
			if (notification != null) {
				this.componentFile = componentFile;
				// component = null;
				notify(notification);
			}
		}

		// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
		@Override
		public File getComponentActualFile() {
			if (componentFile instanceof FileResourceImpl) {
				return ((FileResourceImpl) componentFile).getFile();
			}
			else
				return null;
		}

		@Override
		public void setComponentActualFile(File file) throws MalformedURLException, LocatorNotFoundException {

			this.setComponentFile(new FileResourceImpl(file));
		}

		@Override
		public DataBinding<Resource> getDynamicComponentFile() {

			if (dynamicComponentFile == null) {
				dynamicComponentFile = new DataBinding<Resource>(this, Resource.class, DataBinding.BindingDefinitionType.GET);
				dynamicComponentFile.setBindingName("dynamicComponentFile");
				// dynamicComponentFile.setCacheable(true);
			}
			return dynamicComponentFile;
		}

		@Override
		public void setDynamicComponentFile(DataBinding<Resource> dynamicComponentFile) {

			FIBPropertyNotification<DataBinding<Resource>> notification = requireChange(DYNAMIC_COMPONENT_FILE_KEY, dynamicComponentFile);

			if (notification != null) {

				if (dynamicComponentFile != null) {
					dynamicComponentFile.setOwner(this);
					dynamicComponentFile.setDeclaredType(Resource.class);
					dynamicComponentFile.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					dynamicComponentFile.setBindingName("dynamicComponentFile");
					// dynamicComponentFile.setCacheable(true);
				}

				this.dynamicComponentFile = dynamicComponentFile;

				// referencedComponent = null;
				notify(notification);
			}

		}

		@Override
		public DataBinding<FIBComponent> getDynamicComponent() {

			if (dynamicComponent == null) {
				dynamicComponent = new DataBinding<FIBComponent>(this, FIBComponent.class, DataBinding.BindingDefinitionType.GET);
				dynamicComponent.setBindingName("dynamicComponent");
				// dynamicComponentFile.setCacheable(true);
			}
			return dynamicComponent;
		}

		@Override
		public void setDynamicComponent(DataBinding<FIBComponent> dynamicComponent) {

			FIBPropertyNotification<DataBinding<FIBComponent>> notification = requireChange(DYNAMIC_COMPONENT_KEY, dynamicComponent);

			if (notification != null) {

				if (dynamicComponent != null) {
					dynamicComponent.setOwner(this);
					dynamicComponent.setDeclaredType(FIBComponent.class);
					dynamicComponent.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					dynamicComponent.setBindingName("dynamicComponent");
					// dynamicComponent.setCacheable(true);
				}

				this.dynamicComponent = dynamicComponent;

				// referencedComponent = null;
				notify(notification);
			}

		}

		@Override
		public DataBinding<FIBController> getControllerFactory() {

			if (controllerFactory == null) {
				controllerFactory = new DataBinding<FIBController>(this, FIBController.class, DataBinding.BindingDefinitionType.GET);
				controllerFactory.setBindingName("controllerFactory");
			}
			return controllerFactory;
		}

		@Override
		public void setControllerFactory(DataBinding<FIBController> controllerFactory) {

			FIBPropertyNotification<DataBinding<FIBController>> notification = requireChange(CONTROLLER_FACTORY_KEY, controllerFactory);

			if (notification != null) {

				if (controllerFactory != null) {
					controllerFactory.setOwner(this);
					controllerFactory.setDeclaredType(FIBController.class);
					controllerFactory.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					controllerFactory.setBindingName("controllerFactory");
				}

				this.controllerFactory = controllerFactory;

				// referencedComponent = null;
				notify(notification);
			}

		}

		@Override
		public Type getDataType() {
			/*if (referencedComponent != null) {
				return referencedComponent.getDataType();
			}*/
			return super.getDataType();
		}

		public boolean hasAssignment(String variableName) {
			return getAssignment(variableName) != null;
		}

		public FIBReferenceAssignment getAssignment(String variableName) {
			for (FIBReferenceAssignment a : getAssignments()) {
				if (variableName != null && variableName.equals(a.getVariable().toString())) {
					return a;
				}
			}
			return null;
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			for (FIBReferenceAssignment assign : getAssignments()) {
				assign.revalidateBindings();
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			for (FIBReferenceAssignment assign : getAssignments()) {
				assign.finalizeDeserialization();
			}
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getDynamicComponentFile());
			return returned;
		}

		/*
		@Override
		public Boolean getManageDynamicModel() {
			return true;
		}
		 */

		@Override
		public FIBReferenceAssignment createAssignment() {
			logger.info("Called createAssignment()");
			FIBReferenceAssignment newAssignment = getModelFactory().newInstance(FIBReferenceAssignment.class);
			addToAssignments(newAssignment);
			return newAssignment;
		}

		@Override
		public FIBReferenceAssignment deleteAssignment(FIBReferenceAssignment assignment) {
			logger.info("Called deleteAssignment() with " + assignment);
			removeFromAssignments(assignment);
			return assignment;
		}

	}

	@ModelEntity
	@ImplementationClass(FIBReferenceAssignment.FIBReferenceAssignmentImpl.class)
	@XMLElement(xmlTag = "ReferenceAssignment")
	public static interface FIBReferenceAssignment extends FIBModelObject {
		@PropertyIdentifier(type = FIBReferencedComponent.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY /*, inverse = FIBReferencedComponent.ASSIGNMENTS_KEY*/)
		@CloningStrategy(StrategyType.IGNORE)
		public FIBReferencedComponent getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBReferencedComponent customColumn);

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

		public void revalidateBindings();

		public static abstract class FIBReferenceAssignmentImpl extends FIBModelObjectImpl implements FIBReferenceAssignment {

			@Deprecated
			public static BindingDefinition VARIABLE = new BindingDefinition("variable", Object.class,
					DataBinding.BindingDefinitionType.GET_SET, true);
			@Deprecated
			public BindingDefinition VALUE = new BindingDefinition("value", Object.class, DataBinding.BindingDefinitionType.GET, true);

			private DataBinding<Object> variable;
			private DataBinding<Object> value;

			private final boolean mandatory = true;

			@Override
			public boolean isMandatory() {
				return mandatory;
			}

			@Override
			public void setOwner(FIBReferencedComponent referencedComponent) {
				performSuperSetter(OWNER_KEY, referencedComponent);
				if (value != null) {
					value.setOwner(referencedComponent);
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
					variable = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET_SET);
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
			public DataBinding<Object> getValue() {
				if (value == null) {
					value = new DataBinding<Object>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
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
					return getOwner().getBindingModel();
				}
				return null;
			}

		}
	}

	public static class DynamicComponentFileBindingMustBeValid extends BindingMustBeValid<FIBReferencedComponent> {
		public DynamicComponentFileBindingMustBeValid() {
			super("'dynamic_componentFile'_binding_is_not_valid", FIBReferencedComponent.class);
		}

		@Override
		public DataBinding<Resource> getBinding(FIBReferencedComponent object) {
			return object.getDynamicComponentFile();
		}

	}

}

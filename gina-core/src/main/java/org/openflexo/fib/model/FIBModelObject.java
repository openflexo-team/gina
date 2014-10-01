/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.DeletableProxyObject;
import org.openflexo.model.factory.EmbeddingType;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.ProblemIssue;
import org.openflexo.model.validation.Validable;
import org.openflexo.model.validation.ValidationError;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.model.validation.ValidationWarning;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.StringUtils;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBModelObject.FIBModelObjectImpl.class)
public interface FIBModelObject extends Validable, Bindable, AccessibleProxyObject, CloneableProxyObject, DeletableProxyObject {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = List.class)
	public static final String PARAMETERS_KEY = "parameters";

	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = DESCRIPTION_KEY)
	@XMLAttribute
	public String getDescription();

	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<FIBParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(FIBParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(FIBParameter aParameter);

	// @Finder(collection = PARAMETERS_KEY, attribute = FIBParameter.NAME_KEY)
	public String getParameter(String parameterName);

	public void clearParameters();

	public FIBParameter createNewParameter();

	public FIBModelFactory getFactory();

	public boolean isValid();

	public ValidationReport validate();

	public Collection<? extends FIBModelObject> getEmbeddedObjects();

	public List<FIBModelObject> getObjectsWithName(String aName);

	public String generateUniqueName(String baseName);

	public String getBaseName();

	/**
	 * Return the FIBComponent this model objects refer to
	 * 
	 * @return
	 */
	public FIBComponent getComponent();

	public void notify(FIBModelNotification notification);

	public void deleteParameter(FIBParameter p);

	public boolean isParameterAddable();

	public boolean isParameterDeletable(FIBParameter p);

	public static abstract class FIBModelObjectImpl implements FIBModelObject {

		private static final Logger logger = Logger.getLogger(FIBModelObject.class.getPackage().getName());

		public static final String DELETED_PROPERTY = "Deleted";

		// TODO: Localizer for the FIB, should be refactored
		public static LocalizedDelegateGUIImpl LOCALIZATION;

		static {
			ResourceLocator rl = ResourceLocator.getResourceLocator();
			Resource generalLocalizedDelegate = ResourceLocator.locateResource("Localized");
			Resource fibLocalizedDelegate = ResourceLocator.locateResource("FIBLocalized");

			if (fibLocalizedDelegate != null) {
				if (generalLocalizedDelegate != null) {
					LOCALIZATION = LocalizedDelegateGUIImpl.getLocalizedDelegate(fibLocalizedDelegate,
							LocalizedDelegateGUIImpl.getLocalizedDelegate(generalLocalizedDelegate, null, false), true);
				} else {
					LOCALIZATION = LocalizedDelegateGUIImpl.getLocalizedDelegate(fibLocalizedDelegate, null, true);
				}
			} else {
				if (generalLocalizedDelegate != null) {
					LOCALIZATION = LocalizedDelegateGUIImpl.getLocalizedDelegate(generalLocalizedDelegate, null, true);
				} else {
					LOCALIZATION = LocalizedDelegateGUIImpl.getLocalizedDelegate(generalLocalizedDelegate, null, false);
				}
			}
		}

		// private String name;
		// private String description;
		// private boolean isDeleted = false;

		// private final List<FIBParameter> parameters = new Vector<FIBParameter>();

		// private final PropertyChangeSupport pcSupport;

		public FIBModelObjectImpl() {
			super();
			// pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public FIBModelFactory getFactory() {
			return FIBLibrary.instance().getFIBModelFactory();
		}

		/**
		 * Return all embedded objects<br>
		 * This method is really generic and use PAMELA annotations.<br>
		 * You might want to override this method to provide a more precise implementation
		 * 
		 */
		@Override
		public Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return (Collection<? extends FIBModelObject>) getFactory().getEmbeddedObjects(this, EmbeddingType.CLOSURE);
		}

		/**
		 * Return all embedded objects which need to be validated<br>
		 * This method is really generic and use PAMELA annotations. You might want to override this method to provide a more precise
		 * implementation
		 * 
		 */
		@Override
		public final Collection<? extends Validable> getEmbeddedValidableObjects() {

			List<?> embeddedObjects = getFactory().getEmbeddedObjects(this, EmbeddingType.CLOSURE);
			List<Validable> returned = new ArrayList<Validable>();
			for (Object e : embeddedObjects) {
				if (e instanceof Validable) {
					returned.add((Validable) e);
				}
			}
			return returned;
		}

		@Override
		public String getParameter(String parameterName) {
			for (FIBParameter p : getParameters()) {
				if (parameterName.equals(p.getName())) {
					return p.getValue();
				}
			}
			return null;
		}

		@Override
		public FIBParameter createNewParameter() {
			FIBParameter returned = getFactory().newInstance(FIBParameter.class);
			returned.setName("param");
			returned.setValue("value");
			addToParameters(returned);
			return returned;
		}

		@Override
		public void deleteParameter(FIBParameter p) {
			removeFromParameters(p);
		}

		@Override
		public boolean isParameterAddable() {
			return true;
		}

		@Override
		public boolean isParameterDeletable(FIBParameter p) {
			return true;
		}

		/**
		 * Return the FIBComponent this model objects refer to
		 * 
		 * @return
		 */
		@Override
		public abstract FIBComponent getComponent();

		@Override
		public BindingModel getBindingModel() {
			if (getComponent() != null) {
				return getComponent().getBindingModel();
			}
			return null;
		}

		@Override
		public BindingFactory getBindingFactory() {
			if (getComponent() != null) {
				return getComponent().getBindingFactory();
			}
			return FIBLibrary.instance().getBindingFactory();
		}

		/**
		 * Called when supplied data binding has been decoded (syntaxic and semantics analysis performed)
		 * 
		 * @param dataBinding
		 */
		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {

		}

		/*public void initializeDeserialization() {

		}

		public void finalizeDeserialization() {
		}*/

		// *******************************************************************************
		// * Utils *
		// *******************************************************************************

		protected <T extends Object> void notifyChange(String key, T oldValue, T newValue) {
			// Never notify unchanged values
			if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
				return;
			}
			getPropertyChangeSupport().firePropertyChange(key, oldValue, newValue);
		}

		protected <T extends Object> FIBPropertyNotification<T> requireChange(String key, T value) {
			FIBProperty<T> property = (FIBProperty<T>) FIBProperty.getFIBProperty(getClass(), key);
			if (property == null) {
				logger.warning("Cannot find property " + property + " in " + getClass());
			}
			T oldValue = (T) objectForKey(key);
			if (oldValue == null) {
				if (value == null) {
					return null; // No change
				} else {
					return new FIBPropertyNotification<T>(property, oldValue, value);
				}
			} else {
				if (oldValue.equals(value)) {
					return null; // No change
				} else {
					return new FIBPropertyNotification<T>(property, oldValue, value);
				}
			}
		}

		@Override
		public void notify(FIBModelNotification notification) {
			hasChanged(notification);
		}

		protected void hasChanged(FIBModelNotification notification) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Change attribute " + notification.getAttributeName() + " for object " + this + " was: "
						+ notification.oldValue() + " is now: " + notification.newValue());
			}
			getPropertyChangeSupport()
					.firePropertyChange(notification.getAttributeName(), notification.oldValue(), notification.newValue());
		}

		/**
		 * Called when supplied data binding changed its value
		 * 
		 * @param dataBinding
		 */
		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
		}

		public static boolean equals(Object o1, Object o2) {
			if (o1 == o2) {
				return true;
			}
			if (o1 == null) {
				return o2 == null;
			} else {
				return o1.equals(o2);
			}
		}

		public static boolean notEquals(Object o1, Object o2) {
			return !equals(o1, o2);
		}

		@Override
		public final boolean isValid() {
			ValidationReport report = validate();
			return report.getErrorsCount() == 0;
		}

		@Override
		public final ValidationReport validate() {
			return getFactory().getValidationModel().validate(this);
		}

		public Class<?> getImplementedInterface() {
			return getFactory().getModelEntityForInstance(this).getImplementedInterface();
		}

		@Override
		public String generateUniqueName(String baseName) {
			String currentName = baseName;
			int i = 2;
			while (isNameUsedInHierarchy(currentName)) {
				currentName = baseName + i;
				i++;
			}
			return currentName;
		}

		@Override
		public String getBaseName() {
			return getFactory().getModelEntityForInstance(this).getImplementedInterface().getSimpleName();
		}

		public boolean isNameUsedInHierarchy(String aName) {
			return isNameUsedInHierarchy(aName, getComponent().getRootComponent());
		}

		private static boolean isNameUsedInHierarchy(String aName, FIBModelObject object) {
			if (object.getName() != null && object.getName().equals(aName)) {
				return true;
			}
			if (object.getEmbeddedObjects() != null) {
				for (FIBModelObject o : object.getEmbeddedObjects()) {
					if (isNameUsedInHierarchy(aName, o)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public List<FIBModelObject> getObjectsWithName(String aName) {
			return retrieveObjectsWithName(aName, getComponent().getRootComponent(), new ArrayList<FIBModelObject>());
		}

		private static List<FIBModelObject> retrieveObjectsWithName(String aName, FIBModelObject object, List<FIBModelObject> list) {
			if (object.getName() != null && object.getName().equals(aName)) {
				list.add(object);
			}
			if (object.getEmbeddedObjects() != null) {
				for (FIBModelObject o : object.getEmbeddedObjects()) {
					retrieveObjectsWithName(aName, o, list);
				}
			}
			return list;
		}

		@Override
		public void clearParameters() {
			getParameters().clear();
		}

	}

	public static class FIBModelObjectShouldHaveAUniqueName extends ValidationRule<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> {

		public FIBModelObjectShouldHaveAUniqueName() {
			super(FIBModelObject.class, "object_should_not_have_duplicated_name");
		}

		@Override
		public ValidationIssue<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> applyValidation(FIBModelObject object) {
			if (StringUtils.isNotEmpty(object.getName())) {
				List<FIBModelObject> allObjectsWithThatName = object.getObjectsWithName(object.getName());
				if (allObjectsWithThatName.size() > 1) {
					allObjectsWithThatName.remove(object);
					GenerateUniqueName fixProposal = new GenerateUniqueName();
					ProblemIssue<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> returned;
					if (object instanceof FIBWidget && ((FIBWidget) object).getManageDynamicModel()) {
						returned = new ValidationError<FIBModelObjectShouldHaveAUniqueName, FIBModelObject>(this, object,
								"object_($object.toString)_has_duplicated_name", fixProposal);
					} else {
						returned = new ValidationWarning<FIBModelObjectShouldHaveAUniqueName, FIBModelObject>(this, object,
								"object_($object.toString)_has_duplicated_name", fixProposal);
					}
					returned.addToRelatedValidableObjects(allObjectsWithThatName);
					return returned;
				}
			}
			return null;
		}

		protected static class GenerateUniqueName extends FixProposal<FIBModelObjectShouldHaveAUniqueName, FIBModelObject> {

			public GenerateUniqueName() {
				super("generate_unique_name_:_($uniqueName)");
			}

			@Override
			protected void fixAction() {
				getValidable().setName(getUniqueName());
			}

			public String getUniqueName() {
				return getValidable().generateUniqueName(getValidable().getBaseName());
			}

		}
	}

	public static abstract class BindingMustBeValid<C extends FIBModelObject> extends ValidationRule<BindingMustBeValid<C>, C> {
		public BindingMustBeValid(String ruleName, Class<C> clazz) {
			super(clazz, ruleName);
		}

		public abstract DataBinding<?> getBinding(C object);

		// public abstract BindingDefinition getBindingDefinition(C object);

		@Override
		public ValidationIssue<BindingMustBeValid<C>, C> applyValidation(C object) {
			if (getBinding(object) != null && getBinding(object).isSet()) {
				if (!getBinding(object).isValid()) {
					DeleteBinding<C> deleteBinding = new DeleteBinding<C>(this);
					return new ValidationError<BindingMustBeValid<C>, C>(this, object, BindingMustBeValid.this.getRuleName() + " '"
							+ getBinding(object) + "' reason: " + getBinding(object).invalidBindingReason(), deleteBinding);
				}
			}
			return null;
		}

		protected static class DeleteBinding<C extends FIBModelObject> extends FixProposal<BindingMustBeValid<C>, C> {

			private final BindingMustBeValid rule;

			public DeleteBinding(BindingMustBeValid rule) {
				super("delete_this_binding");
				this.rule = rule;
			}

			@Override
			protected void fixAction() {
				rule.getBinding(getValidable()).setExpression(null);
			}

		}
	}

}

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

package org.openflexo.fib.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBBrowserAction.FIBBrowserActionImpl.class)
@Imports({ @Import(FIBBrowserAction.FIBAddAction.class), @Import(FIBBrowserAction.FIBRemoveAction.class),
		@Import(FIBBrowserAction.FIBCustomAction.class) })
public abstract interface FIBBrowserAction extends FIBModelObject {

	public static enum ActionType {
		Add, Delete, Custom
	}

	@PropertyIdentifier(type = FIBBrowserElement.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String METHOD_KEY = "method";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_AVAILABLE_KEY = "isAvailable";

	@Getter(value = OWNER_KEY, inverse = FIBBrowserElement.ACTIONS_KEY)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBBrowserElement getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBBrowserElement browserElement);

	@Getter(value = METHOD_KEY)
	@XMLAttribute
	public DataBinding<Object> getMethod();

	@Setter(METHOD_KEY)
	public void setMethod(DataBinding<Object> method);

	@Getter(value = IS_AVAILABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getIsAvailable();

	@Setter(IS_AVAILABLE_KEY)
	public void setIsAvailable(DataBinding<Boolean> isAvailable);

	public ActionType getActionType();

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public void updateBindingModel();

	public void searchLocalized(LocalizationEntryRetriever retriever);

	public static abstract class FIBBrowserActionImpl extends FIBModelObjectImpl implements FIBBrowserAction {

		private static final Logger logger = Logger.getLogger(FIBBrowserAction.class.getPackage().getName());

		private DataBinding<Object> method;
		private DataBinding<Boolean> isAvailable;

		private BindingModel actionBindingModel;

		@Deprecated
		public static BindingDefinition METHOD = new BindingDefinition("method", Object.class, BindingDefinitionType.EXECUTE, false);
		@Deprecated
		public static BindingDefinition IS_AVAILABLE = new BindingDefinition("isAvailable", Boolean.class, BindingDefinitionType.EXECUTE,
				false);

		@Override
		public FIBComponent getComponent() {
			if (getOwner() != null) {
				return getOwner().getComponent();
			}
			return null;
		}

		@Override
		public DataBinding<Object> getMethod() {
			if (method == null) {
				method = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
			}
			/*if(method.getOwner()==null){
				method.setOwner(this);
			}*/
			return method;
		}

		@Override
		public void setMethod(DataBinding<Object> method) {
			if (method != null) {
				method.setOwner(this);
				method.setDeclaredType(Object.class);
				method.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
			}
			this.method = method;
		}

		@Override
		public DataBinding<Boolean> getIsAvailable() {
			if (isAvailable == null) {
				isAvailable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return isAvailable;
		}

		@Override
		public void setIsAvailable(DataBinding<Boolean> isAvailable) {
			if (isAvailable != null) {
				isAvailable.setOwner(this);
				isAvailable.setDeclaredType(Boolean.class);
				isAvailable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.isAvailable = isAvailable;
		}

		@Override
		public void updateBindingModel() {
			if (actionBindingModel != null) {
				actionBindingModel.setBaseBindingModel(getOwner().getActionBindingModel());
			}
		}

		@Override
		public BindingModel getBindingModel() {
			if (getOwner() != null) {
				if (actionBindingModel == null) {
					actionBindingModel = new BindingModel(getOwner().getActionBindingModel());
					actionBindingModel.addToBindingVariables(new BindingVariable("action", Object.class) {
						@Override
						public Type getType() {
							return FIBBrowserActionImpl.this.getClass();
						}

						@Override
						public boolean isCacheable() {
							return false;
						}
					});
				}
				return actionBindingModel;
			}
			return null;
		}

		@Override
		public void finalizeDeserialization() {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("finalizeDeserialization() for FIBTableAction " + getName());
			}
			if (method != null) {
				method.decode();
			}
			if (isAvailable != null) {
				isAvailable.decode();
			}
		}

		public Object performAction(BindingEvaluationContext context, Object selectedObject) {
			if (getMethod() != null && getMethod().isSet()) {
				try {
					return getMethod().getBindingValue(context);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
					return null;
				} catch (NullReferenceException e) {
					e.printStackTrace();
					return null;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			retriever.foundLocalized(getName());
		}

	}

	@ModelEntity
	@ImplementationClass(FIBAddAction.FIBAddActionImpl.class)
	@XMLElement(xmlTag = "BrowserAddAction")
	public static interface FIBAddAction extends FIBBrowserAction {

		public static abstract class FIBAddActionImpl extends FIBBrowserActionImpl implements FIBAddAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Add;
			}
		}
	}

	@ModelEntity
	@ImplementationClass(FIBRemoveAction.FIBRemoveActionImpl.class)
	@XMLElement(xmlTag = "BrowserRemoveAction")
	public static interface FIBRemoveAction extends FIBBrowserAction {

		public static abstract class FIBRemoveActionImpl extends FIBBrowserActionImpl implements FIBRemoveAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Delete;
			}
		}

	}

	@ModelEntity
	@ImplementationClass(FIBCustomAction.FIBCustomActionImpl.class)
	@XMLElement(xmlTag = "BrowserCustomAction")
	public static interface FIBCustomAction extends FIBBrowserAction {

		@PropertyIdentifier(type = boolean.class)
		public static final String IS_STATIC_KEY = "isStatic";

		@Getter(value = IS_STATIC_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isStatic();

		@Setter(IS_STATIC_KEY)
		public void setStatic(boolean isStatic);

		public static abstract class FIBCustomActionImpl extends FIBBrowserActionImpl implements FIBCustomAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Custom;
			}
		}

	}

	@DefineValidationRule
	public static class MethodBindingMustBeValid extends BindingMustBeValid<FIBBrowserAction> {
		public MethodBindingMustBeValid() {
			super("'method'_binding_is_not_valid", FIBBrowserAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserAction object) {
			return object.getMethod();
		}
	}

	@DefineValidationRule
	public static class IsAvailableBindingMustBeValid extends BindingMustBeValid<FIBBrowserAction> {
		public IsAvailableBindingMustBeValid() {
			super("'is_available'_binding_is_not_valid", FIBBrowserAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserAction object) {
			return object.getIsAvailable();
		}
	}

}

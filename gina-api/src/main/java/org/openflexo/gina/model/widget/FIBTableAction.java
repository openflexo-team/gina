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

import java.util.logging.Logger;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.DeserializationFinalizer;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBTableAction.FIBTableActionImpl.class)
@Imports({ @Import(FIBTableAction.FIBAddAction.class), @Import(FIBTableAction.FIBRemoveAction.class),
		@Import(FIBTableAction.FIBCustomAction.class) })
public abstract interface FIBTableAction extends FIBModelObject {

	public static enum ActionType {
		Add, Delete, Custom
	}

	@PropertyIdentifier(type = FIBTable.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String METHOD_KEY = "method";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_AVAILABLE_KEY = "isAvailable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ALLOWS_BATCH_EXECUTION_KEY = "allowsBatchExecution";

	@Getter(value = OWNER_KEY /*, inverse = FIBTable.ACTIONS_KEY*/)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBTable getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBTable table);

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

	public abstract ActionType getActionType();

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public void searchLocalized(LocalizationEntryRetriever retriever);

	@Getter(value = ALLOWS_BATCH_EXECUTION_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAllowsBatchExecution();

	@Setter(ALLOWS_BATCH_EXECUTION_KEY)
	public void setAllowsBatchExecution(boolean allowsBatchExecution);

	public static abstract class FIBTableActionImpl extends FIBModelObjectImpl implements FIBTableAction {

		private static final Logger logger = Logger.getLogger(FIBTableAction.class.getPackage().getName());

		private DataBinding<Object> method;
		private DataBinding<Boolean> isAvailable;

		@Override
		public FIBTable getComponent() {
			return getOwner();
		}

		@Override
		public void setOwner(FIBTable ownerTable) {
			// BindingModel oldBindingModel = getBindingModel();
			performSuperSetter(OWNER_KEY, ownerTable);
		}

		@Override
		public DataBinding<Object> getMethod() {
			if (method == null) {
				method = new DataBinding<>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				method.setBindingName("method");
			}
			return method;
		}

		@Override
		public void setMethod(DataBinding<Object> method) {
			if (method != null) {
				method.setOwner(this);
				method.setDeclaredType(Object.class);
				method.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				method.setBindingName("method");
			}
			this.method = method;
		}

		@Override
		public DataBinding<Boolean> getIsAvailable() {
			if (isAvailable == null) {
				isAvailable = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
				isAvailable.setBindingName("isAvailable");
			}
			return isAvailable;
		}

		@Override
		public void setIsAvailable(DataBinding<Boolean> isAvailable) {
			if (isAvailable != null) {
				isAvailable.setOwner(this);
				isAvailable.setDeclaredType(Boolean.class);
				isAvailable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				isAvailable.setBindingName("isAvailable");
			}
			this.isAvailable = isAvailable;
		}

		@Override
		public BindingModel getBindingModel() {
			if (getOwner() != null) {
				return getOwner().getActionBindingModel();
			}
			return null;
		}

		@Override
		public void finalizeDeserialization() {
			logger.fine("finalizeDeserialization() for FIBTableAction " + getName());
			if (method != null) {
				method.decode();
			}
		}

		@Override
		public abstract ActionType getActionType();

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			retriever.foundLocalized(getName());
		}

		@Override
		public String getPresentationName() {
			return getName();
		}
	}

	@ModelEntity
	@ImplementationClass(FIBAddAction.FIBAddActionImpl.class)
	@XMLElement(xmlTag = "AddAction")
	public static interface FIBAddAction extends FIBTableAction {

		public static abstract class FIBAddActionImpl extends FIBTableActionImpl implements FIBAddAction {

			@Override
			public ActionType getActionType() {
				return ActionType.Add;
			}
		}
	}

	@ModelEntity
	@ImplementationClass(FIBRemoveAction.FIBRemoveActionImpl.class)
	@XMLElement(xmlTag = "RemoveAction")
	public static interface FIBRemoveAction extends FIBTableAction {

		public static abstract class FIBRemoveActionImpl extends FIBTableActionImpl implements FIBRemoveAction {
			@Override
			public ActionType getActionType() {
				return ActionType.Delete;
			}
		}
	}

	@ModelEntity
	@ImplementationClass(FIBCustomAction.FIBCustomActionImpl.class)
	@XMLElement(xmlTag = "CustomAction")
	public static interface FIBCustomAction extends FIBTableAction {

		@PropertyIdentifier(type = boolean.class)
		public static final String IS_STATIC_KEY = "isStatic";

		@Getter(value = IS_STATIC_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isStatic();

		@Setter(IS_STATIC_KEY)
		public void setStatic(boolean isStatic);

		public static abstract class FIBCustomActionImpl extends FIBTableActionImpl implements FIBCustomAction {
			@Override
			public ActionType getActionType() {
				return ActionType.Custom;
			}
		}
	}

	@DefineValidationRule
	public static class MethodBindingMustBeValid extends BindingMustBeValid<FIBTableAction> {
		public MethodBindingMustBeValid() {
			super("'method'_binding_is_not_valid", FIBTableAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableAction object) {
			return object.getMethod();
		}
	}

	@DefineValidationRule
	public static class IsAvailableBindingMustBeValid extends BindingMustBeValid<FIBTableAction> {
		public IsAvailableBindingMustBeValid() {
			super("'is_available'_binding_is_not_valid", FIBTableAction.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableAction object) {
			return object.getIsAvailable();
		}
	}
}

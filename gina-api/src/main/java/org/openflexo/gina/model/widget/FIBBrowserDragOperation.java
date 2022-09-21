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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.widget.FIBBrowserDragOperation.FIBBrowserDragOperationImpl.FIBBrowserDragOperationBindable;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBBrowserDragOperation.FIBBrowserDragOperationImpl.class)
@XMLElement(xmlTag = "DragOperation")
public interface FIBBrowserDragOperation extends FIBModelObject {

	@PropertyIdentifier(type = FIBBrowserElement.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = FIBBrowserElement.class)
	public static final String TARGET_ELEMENT_KEY = "targetElement";
	@PropertyIdentifier(type = String.class)
	public static final String TARGET_ELEMENT_NAME_KEY = "targetElementName";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_AVAILABLE_ACTION_KEY = "isAvailable";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ACTION_KEY = "action";

	@Getter(value = OWNER_KEY)
	public FIBBrowserElement getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBBrowserElement owner);

	@Getter(value = TARGET_ELEMENT_KEY)
	public FIBBrowserElement getTargetElement();

	@Setter(TARGET_ELEMENT_KEY)
	public void setTargetElement(FIBBrowserElement targetElement);

	@Getter(value = TARGET_ELEMENT_NAME_KEY)
	@XMLAttribute
	public String getTargetElementName();

	@Setter(TARGET_ELEMENT_NAME_KEY)
	public void setTargetElementName(String targetElementName);

	@Getter(value = ACTION_KEY)
	@XMLAttribute
	public DataBinding<Object> getAction();

	@Setter(ACTION_KEY)
	public void setAction(DataBinding<Object> action);

	@Getter(value = IS_AVAILABLE_ACTION_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getIsAvailable();

	@Setter(IS_AVAILABLE_ACTION_KEY)
	public void setIsAvailable(DataBinding<Boolean> available);

	public void finalizeBrowserDeserialization();

	public void revalidateBindings();

	public void updateBindingModel();

	public FIBBrowserDragOperationBindable getDragOperationBindable();

	public static abstract class FIBBrowserDragOperationImpl extends FIBModelObject.FIBModelObjectImpl implements FIBBrowserDragOperation {

		private static final Logger logger = Logger.getLogger(FIBBrowserElementChildren.class.getPackage().getName());

		private FIBBrowserElement targetElement;
		private DataBinding<Object> action;
		private DataBinding<Boolean> isAvailable;
		private final FIBBrowserDragOperationBindable dragOperationBindable;

		public FIBBrowserDragOperationImpl() {
			dragOperationBindable = new FIBBrowserDragOperationBindable();
		}

		@Override
		public void setOwner(FIBBrowserElement browserElement) {
			performSuperSetter(OWNER_KEY, browserElement);
			componentChanged();
			dragOperationBindable.updateBindingModel();
		}

		@Override
		public FIBBrowserElement getTargetElement() {
			FIBBrowserElement returned = (FIBBrowserElement) performSuperGetter(TARGET_ELEMENT_KEY);
			if (returned == null && targetElementName != null) {
				if (getOwner() != null && getOwner().getOwner() != null) {
					returned = getOwner().getOwner().getElement(targetElementName);
				}
			}
			return returned;
		}

		@Override
		public void setTargetElement(FIBBrowserElement targetElement) {
			performSuperSetter(TARGET_ELEMENT_KEY, targetElement);
			dragOperationBindable.updateBindingModel();
		}

		private String targetElementName = null;

		@Override
		public String getTargetElementName() {
			if (getTargetElement() != null) {
				return getTargetElement().getName();
			}
			return targetElementName;
		}

		@Override
		public void setTargetElementName(String targetElementName) {
			this.targetElementName = targetElementName;
			if (getOwner() != null && getOwner().getOwner() != null) {
				setTargetElement(getOwner().getOwner().getElement(targetElementName));
			}
		}

		public FIBBrowser getBrowser() {
			return getOwner().getOwner();
		}

		@Override
		public String getPresentationName() {
			return getName();
		}

		@Override
		public FIBBrowserDragOperationBindable getDragOperationBindable() {
			return dragOperationBindable;
		}

		@Override
		public DataBinding<Object> getAction() {
			if (action == null) {
				action = new DataBinding<>(getDragOperationBindable(), Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return action;
		}

		@Override
		public void setAction(DataBinding<Object> action) {
			if (action != null) {
				action.setOwner(getDragOperationBindable());
				action.setDeclaredType(Object.class);
				action.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.action = action;
		}

		@Override
		public DataBinding<Boolean> getIsAvailable() {
			if (isAvailable == null) {
				isAvailable = new DataBinding<>(getDragOperationBindable(), Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return isAvailable;
		}

		@Override
		public void setIsAvailable(DataBinding<Boolean> isAvailable) {
			if (isAvailable != null) {
				isAvailable.setOwner(getDragOperationBindable());
				isAvailable.setDeclaredType(Boolean.class);
				isAvailable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.isAvailable = isAvailable;
		}

		@Override
		public FIBComponent getComponent() {
			if (getOwner() != null) {
				return getOwner().getComponent();
			}
			return null;
		}

		@Override
		public void revalidateBindings() {
			if (action != null) {
				action.setOwner(getOwner().getIteratorBindable());
				action.revalidate();
			}
			if (isAvailable != null) {
				isAvailable.setOwner(getOwner().getIteratorBindable());
				isAvailable.revalidate();
			}
		}

		@Override
		public void finalizeBrowserDeserialization() {
			logger.fine("finalizeBrowserDeserialization() for FIBBrowserDragOperation");
			if (action != null) {
				action.setOwner(getDragOperationBindable());
				// action.decode();
			}
			if (isAvailable != null) {
				isAvailable.setOwner(getDragOperationBindable());
				// isAvailable.decode();
			}
		}

		@Override
		public void updateBindingModel() {
			dragOperationBindable.updateBindingModel();
		}

		public class FIBBrowserDragOperationBindable extends DefaultBindable {
			private BindingModel dragOperationBindingModel = null;
			private BindingVariable draggedBindingVariable;
			private BindingVariable targetBindingVariable;

			@Override
			public BindingModel getBindingModel() {
				if (dragOperationBindingModel == null) {
					createDragOperationBindingModel();
				}
				return dragOperationBindingModel;
			}

			public void updateBindingModel() {
				getBindingModel();
				if (getOwner() != null) {
					dragOperationBindingModel.setBaseBindingModel(getOwner() != null ? getOwner().getBindingModel() : null);
				}
				targetBindingVariable.getPropertyChangeSupport().firePropertyChange("type", null, targetBindingVariable.getType());
			}

			private void createDragOperationBindingModel() {
				dragOperationBindingModel = new BindingModel(getOwner() != null ? getOwner().getBindingModel() : null);
				dragOperationBindingModel.addToBindingVariables(draggedBindingVariable = new BindingVariable("dragged", Object.class) {
					@Override
					public Type getType() {
						if (getOwner() != null) {
							return getOwner().getDataType();
						}
						return Object.class;
					}
				});
				draggedBindingVariable.setCacheable(false);
				dragOperationBindingModel.addToBindingVariables(targetBindingVariable = new BindingVariable("target", Object.class) {
					@Override
					public Type getType() {
						if (getTargetElement() != null) {
							return getTargetElement().getDataType();
						}
						return Object.class;
					}
				});
				targetBindingVariable.setCacheable(false);
			}

			public FIBComponent getComponent() {
				if (getOwner() != null) {
					return getOwner().getComponent();
				}
				return null;
			}

			@Override
			public BindingFactory getBindingFactory() {
				if (getComponent() == null) {
					return null;
				}
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			}

		}

	}

	@DefineValidationRule
	public static class IsAvailableBindingMustBeValid extends BindingMustBeValid<FIBBrowserDragOperation> {
		public IsAvailableBindingMustBeValid() {
			super("'is_available'_binding_is_not_valid", FIBBrowserDragOperation.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserDragOperation object) {
			return object.getIsAvailable();
		}
	}

	@DefineValidationRule
	public static class ActionBindingMustBeValid extends BindingMustBeValid<FIBBrowserDragOperation> {
		public ActionBindingMustBeValid() {
			super("'action'_binding_is_not_valid", FIBBrowserDragOperation.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserDragOperation object) {
			return object.getAction();
		}
	}

}

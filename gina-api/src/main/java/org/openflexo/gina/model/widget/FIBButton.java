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

import javax.swing.Icon;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.gina.model.FIBModelObject.BindingMustBeValid;
import org.openflexo.gina.model.FIBWidget.FIBWidgetImpl;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBButton.FIBButtonImpl.class)
@XMLElement(xmlTag = "Button")
public interface FIBButton extends FIBWidget {

	public static enum ButtonType {
		Trigger, Toggle
	}

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ACTION_KEY = "action";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String BUTTON_ICON_KEY = "buttonIcon";
	@PropertyIdentifier(type = ButtonType.class)
	public static final String BUTTON_TYPE_KEY = "buttonType";
	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_DEFAULT_KEY = "isDefault";

	@Getter(value = ACTION_KEY)
	@XMLAttribute
	public DataBinding<Object> getAction();

	@Setter(ACTION_KEY)
	public void setAction(DataBinding<Object> action);

	@Getter(value = BUTTON_ICON_KEY)
	@XMLAttribute
	public DataBinding<Icon> getButtonIcon();

	@Setter(BUTTON_ICON_KEY)
	public void setButtonIcon(DataBinding<Icon> buttonIcon);

	@Getter(value = BUTTON_TYPE_KEY)
	@XMLAttribute
	public ButtonType getButtonType();

	@Setter(BUTTON_TYPE_KEY)
	public void setButtonType(ButtonType buttonType);

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Getter(value = IS_DEFAULT_KEY)
	@XMLAttribute(xmlTag = "default")
	public Boolean isDefault();

	@Setter(IS_DEFAULT_KEY)
	public void setIsDefault(Boolean isDefault);

	public static abstract class FIBButtonImpl extends FIBWidgetImpl implements FIBButton {

		@Deprecated
		public static BindingDefinition BUTTON_ICON = new BindingDefinition("buttonIcon", Icon.class,
				DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition ACTION = new BindingDefinition("action", Object.class, DataBinding.BindingDefinitionType.EXECUTE,
				false);

		private DataBinding<Object> action;
		private ButtonType buttonType = ButtonType.Trigger;
		private String label;
		private Boolean isDefault;
		private DataBinding<Icon> buttonIcon;

		public FIBButtonImpl() {
		}

		@Override
		public String getBaseName() {
			return "Button";
		}

		@Override
		public String getIdentifier() {
			return getLabel();
		}

		@Override
		public Type getDefaultDataClass() {
			return String.class;
		}

		@Override
		public DataBinding<Object> getAction() {
			if (action == null) {
				action = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				action.setBindingName("action");
			}
			return action;
		}

		@Override
		public void setAction(DataBinding<Object> action) {
			if (action != null) {
				action.setOwner(this);
				action.setDeclaredType(Void.TYPE);
				action.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				action.setBindingName("action");
			}
			this.action = action;
		}

		@Override
		public ButtonType getButtonType() {
			return buttonType;
		}

		@Override
		public void setButtonType(ButtonType buttonType) {
			FIBPropertyNotification<ButtonType> notification = requireChange(BUTTON_TYPE_KEY, buttonType);
			if (notification != null) {
				this.buttonType = buttonType;
				hasChanged(notification);
			}
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void setLabel(String label) {
			FIBPropertyNotification<String> notification = requireChange(LABEL_KEY, label);
			if (notification != null) {
				this.label = label;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean isDefault() {
			return isDefault;
		}

		@Override
		public void setIsDefault(Boolean isDefault) {
			FIBPropertyNotification<Boolean> notification = requireChange(IS_DEFAULT_KEY, isDefault);
			if (notification != null) {
				this.isDefault = isDefault;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<Icon> getButtonIcon() {
			if (buttonIcon == null) {
				buttonIcon = new DataBinding<Icon>(this, Icon.class, DataBinding.BindingDefinitionType.GET);
			}
			return buttonIcon;
		}

		@Override
		public void setButtonIcon(DataBinding<Icon> buttonIcon) {
			if (buttonIcon != null) {
				buttonIcon.setOwner(this);
				buttonIcon.setDeclaredType(Icon.class);
				buttonIcon.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.buttonIcon = buttonIcon;
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			super.searchLocalized(retriever);
			retriever.foundLocalized(getLabel());
		}
	}

	@DefineValidationRule
	public static class ActionBindingMustBeValid extends BindingMustBeValid<FIBButton> {
		public ActionBindingMustBeValid() {
			super("'action'_binding_is_not_valid", FIBButton.class);
		}

		@Override
		public DataBinding getBinding(FIBButton object) {
			return object.getAction();
		}

	}

}

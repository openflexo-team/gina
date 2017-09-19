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

import org.openflexo.connie.DataBinding;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBButtonColumn.FIBButtonColumnImpl.class)
@XMLElement(xmlTag = "ButtonColumn")
public interface FIBButtonColumn extends FIBTableColumn {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ACTION_KEY = "action";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ENABLED_KEY = "enabled";

	@Getter(value = ACTION_KEY)
	@XMLAttribute
	public DataBinding<Object> getAction();

	@Setter(ACTION_KEY)
	public void setAction(DataBinding<Object> action);

	@Getter(value = ENABLED_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getEnabled();

	@Setter(ENABLED_KEY)
	public void setEnabled(DataBinding<Boolean> enabled);

	public static abstract class FIBButtonColumnImpl extends FIBTableColumnImpl implements FIBButtonColumn {

		private DataBinding<Object> action;
		private DataBinding<Boolean> enabled;

		// private DataBinding buttonIcon;

		@Override
		public DataBinding<Object> getAction() {
			if (action == null) {
				action = new DataBinding<>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
			}
			return action;
		}

		@Override
		public void setAction(DataBinding<Object> action) {
			if (action != null) {
				action.setOwner(this);
				action.setDeclaredType(Void.TYPE);
				action.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
			}
			this.action = action;
		}

		@Override
		public DataBinding<Boolean> getEnabled() {
			if (enabled == null) {
				enabled = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return enabled;
		}

		@Override
		public void setEnabled(DataBinding<Boolean> enabled) {
			if (enabled != null) {
				enabled.setOwner(this);
				enabled.setDeclaredType(Boolean.class);
				enabled.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.enabled = enabled;
		}

		/*
		public DataBinding getButtonIcon() {
			if (buttonIcon == null) {
				buttonIcon = new DataBinding(this, Parameters.buttonIcon, BUTTON_ICON);
			}
			return buttonIcon;
		}
		
		public void setButtonIcon(DataBinding buttonIcon) {
			if (buttonIcon != null) {
				buttonIcon.setOwner(this);
				buttonIcon.setBindingAttribute(Parameters.buttonIcon);
				buttonIcon.setBindingDefinition(BUTTON_ICON);
			}
			this.buttonIcon = buttonIcon;
		}
		*/
		@Override
		public Type getDefaultDataClass() {
			return String.class;
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.Button;
		}

	}
}

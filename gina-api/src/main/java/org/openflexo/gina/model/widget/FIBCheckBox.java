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

import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBCheckBox.FIBCheckBoxImpl.class)
@XMLElement(xmlTag = "CheckBox")
public interface FIBCheckBox extends FIBWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String NEGATE_KEY = "negate";
	@PropertyIdentifier(type = boolean.class)
	public static final String SELECTED_KEY = "selected";

	@Getter(value = NEGATE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getNegate();

	@Setter(NEGATE_KEY)
	public void setNegate(boolean negate);

	@Getter(value = SELECTED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getSelected();

	@Setter(SELECTED_KEY)
	public void setSelected(boolean selected);

	public static abstract class FIBCheckBoxImpl extends FIBWidgetImpl implements FIBCheckBox {

		private boolean negate = false;
		private boolean selected = false;

		@Override
		public String getBaseName() {
			return "Checkbox";
		}

		@Override
		public Type getDefaultDataType() {
			return Boolean.class;
		}

		@Override
		public boolean getNegate() {
			return negate;
		}

		@Override
		public void setNegate(boolean negate) {
			FIBPropertyNotification<Boolean> notification = requireChange(NEGATE_KEY, negate);
			if (notification != null) {
				this.negate = negate;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getSelected() {
			return selected;
		}

		@Override
		public void setSelected(boolean selected) {
			FIBPropertyNotification<Boolean> notification = requireChange(SELECTED_KEY, selected);
			if (notification != null) {
				this.selected = selected;
				hasChanged(notification);
			}
		}

		@Override
		public boolean isFocusable() {
			return true;
		}

	}
}

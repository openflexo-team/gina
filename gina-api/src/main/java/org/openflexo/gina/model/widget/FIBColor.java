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

import java.awt.Color;
import java.lang.reflect.Type;

import org.openflexo.gina.model.FIBWidget;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBColor.FIBColorImpl.class)
@XMLElement(xmlTag = "Color")
public interface FIBColor extends FIBWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String ALLOWS_NULL_KEY = "allowsNull";
	@PropertyIdentifier(type = String.class)
	public static final String ALLOWS_NULL_TEXT_KEY = "allowsNullText";

	@Getter(value = ALLOWS_NULL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAllowsNull();

	@Setter(ALLOWS_NULL_KEY)
	public void setAllowsNull(boolean allowsNull);

	@Getter(value = ALLOWS_NULL_TEXT_KEY)
	@XMLAttribute
	public String getAllowsNullText();

	@Setter(ALLOWS_NULL_TEXT_KEY)
	public void setAllowsNullText(String allowsNullText);

	public static abstract class FIBColorImpl extends FIBWidgetImpl implements FIBColor {

		// private boolean allowsNull = false;

		public FIBColorImpl() {
		}

		@Override
		public String getBaseName() {
			return "ColorSelector";
		}

		@Override
		public Type getDefaultDataType() {
			return Color.class;
		}

		/*@Override
		public boolean getAllowsNull() {
			return allowsNull;
		}
		
		@Override
		public void setAllowsNull(boolean allowsNull) {
			FIBPropertyNotification<Boolean> notification = requireChange(ALLOWS_NULL_KEY, allowsNull);
			if (notification != null) {
				this.allowsNull = allowsNull;
				hasChanged(notification);
			}
		}*/

		@Override
		public String getAllowsNullText() {
			String returned = (String) performSuperGetter(ALLOWS_NULL_TEXT_KEY);
			if (returned == null) {
				return "define_color";
			}
			return returned;
		}

		@Override
		public boolean isFocusable() {
			return true;
		}

	}
}

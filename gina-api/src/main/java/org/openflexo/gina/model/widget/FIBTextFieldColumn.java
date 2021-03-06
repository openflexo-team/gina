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
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTextFieldColumn.FIBTextFieldColumnImpl.class)
@XMLElement(xmlTag = "TextFieldColumn")
public interface FIBTextFieldColumn extends FIBTableColumn {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String IS_EDITABLE_KEY = "isEditable";

	@Getter(value = IS_EDITABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getIsEditable();

	@Setter(IS_EDITABLE_KEY)
	public void setIsEditable(DataBinding<Boolean> isEditable);

	public static abstract class FIBTextFieldColumnImpl extends FIBTableColumnImpl implements FIBTextFieldColumn {

		private DataBinding<Boolean> isEditable;

		@Override
		public DataBinding<Boolean> getIsEditable() {
			if (isEditable == null) {
				isEditable = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return isEditable;
		}

		@Override
		public void setIsEditable(DataBinding<Boolean> isEditable) {
			if (isEditable != null) {
				isEditable.setOwner(this);
				isEditable.setDeclaredType(Boolean.class);
				isEditable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.isEditable = isEditable;
		}

		@Override
		public void finalizeTableDeserialization() {
			super.finalizeTableDeserialization();
			if (isEditable != null) {
				isEditable.decode();
			}
		}

		@Override
		public Type getDefaultDataClass() {
			return String.class;
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.TextField;
		}

	}
}

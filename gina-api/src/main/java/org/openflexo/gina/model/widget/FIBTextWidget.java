/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBTextWidget.FIBTextWidgetImpl.class)
public abstract interface FIBTextWidget extends FIBWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = String.class)
	public static final String TEXT_KEY = "text";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EDITABLE_KEY = "editable";

	@Getter(value = VALIDATE_ON_RETURN_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isValidateOnReturn();

	@Setter(VALIDATE_ON_RETURN_KEY)
	public void setValidateOnReturn(boolean validateOnReturn);

	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	@Getter(value = TEXT_KEY)
	@XMLAttribute
	public String getText();

	@Setter(TEXT_KEY)
	public void setText(String text);

	@Getter(value = EDITABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getEditable();

	@Setter(EDITABLE_KEY)
	public void setEditable(DataBinding<Boolean> editable);

	public static abstract class FIBTextWidgetImpl extends FIBWidgetImpl implements FIBTextWidget {

		// private boolean validateOnReturn = false;
		// private String text = null;
		// private Integer columns = null;
		private DataBinding<Boolean> editable;

		@Override
		public DataBinding<Boolean> getEditable() {
			if (editable == null) {
				editable = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return editable;
		}

		@Override
		public void setEditable(DataBinding<Boolean> editable) {
			if (editable != null) {
				editable.setOwner(this);
				editable.setDeclaredType(Boolean.class);
				editable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.editable = editable;
		}

		@Override
		public Type getDefaultDataType() {
			return String.class;
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getEditable());
			return returned;
		}

		@Override
		public boolean isFocusable() {
			return true;
		}

	}
}

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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBindingFactory;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import com.google.common.reflect.TypeToken;

@ModelEntity
@ImplementationClass(FIBDropDownColumn.FIBDropDownColumnImpl.class)
@XMLElement(xmlTag = "DropDownColumn")
public interface FIBDropDownColumn extends FIBTableColumn {

	@PropertyIdentifier(type = String.class)
	public static final String STATIC_LIST_KEY = "staticList";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LIST_KEY = "list";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ARRAY_KEY = "array";

	@Getter(value = STATIC_LIST_KEY)
	@XMLAttribute
	public String getStaticList();

	@Setter(STATIC_LIST_KEY)
	public void setStaticList(String staticList);

	@Getter(value = LIST_KEY)
	@XMLAttribute
	public DataBinding<List<?>> getList();

	@Setter(LIST_KEY)
	public void setList(DataBinding<List<?>> list);

	@Getter(value = ARRAY_KEY)
	@XMLAttribute
	public DataBinding<Object[]> getArray();

	@Setter(ARRAY_KEY)
	public void setArray(DataBinding<Object[]> array);

	@Override
	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding getData();

	public static abstract class FIBDropDownColumnImpl extends FIBTableColumnImpl implements FIBDropDownColumn {

		private static final Logger logger = Logger.getLogger(FIBMultipleValues.class.getPackage().getName());

		public String staticList;

		private DataBinding<List<?>> list;
		private DataBinding<Object[]> array;

		public FIBDropDownColumnImpl() {
		}

		@Override
		public DataBinding<List<?>> getList() {
			if (list == null) {
				list = DataBindingFactory.makeListBinding(this);
			}
			return list;
		}

		@Override
		public void setList(DataBinding<List<?>> list) {
			if (list != null) {
				list.setOwner(this);
				list.setDeclaredType(new TypeToken<List<?>>() {
				}.getType());
				list.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				list.setBindingName("list");
			}
			this.list = list;
		}

		@Override
		public DataBinding<Object[]> getArray() {
			if (array == null) {
				array = DataBindingFactory.makeArrayBinding(this);
			}
			return array;
		}

		@Override
		public void setArray(DataBinding<Object[]> array) {
			if (array != null) {
				array.setOwner(this);
				array.setDeclaredType(new TypeToken<Object[]>() {
				}.getType());
				array.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				array.setBindingName("array");
			}
			this.array = array;
		}

		@Override
		public void finalizeTableDeserialization() {
			super.finalizeTableDeserialization();
			/*if (list != null) {
				list.decode();
			}
			if (array != null) {
				array.decode();
			}*/
		}

		@Override
		public Type getDefaultDataClass() {
			return Object.class;
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.DropDown;
		}

	}

	@DefineValidationRule
	public static class ListBindingMustBeValid extends BindingMustBeValid<FIBDropDownColumn> {
		public ListBindingMustBeValid() {
			super("'list'_binding_is_not_valid", FIBDropDownColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBDropDownColumn object) {
			return object.getList();
		}
	}

	@DefineValidationRule
	public static class ArrayBindingMustBeValid extends BindingMustBeValid<FIBDropDownColumn> {
		public ArrayBindingMustBeValid() {
			super("'array'_binding_is_not_valid", FIBDropDownColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBDropDownColumn object) {
			return object.getArray();
		}
	}

}

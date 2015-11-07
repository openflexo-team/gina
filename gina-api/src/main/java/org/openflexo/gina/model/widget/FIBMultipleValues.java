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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.connie.type.GenericArrayTypeImpl;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.WilcardTypeImpl;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.ValidationError;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.toolbox.StringUtils;

import com.google.common.reflect.TypeToken;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBMultipleValues.FIBMultipleValuesImpl.class)
public abstract interface FIBMultipleValues extends FIBWidget {

	@PropertyIdentifier(type = String.class)
	public static final String STATIC_LIST_KEY = "staticList";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LIST_KEY = "list";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ARRAY_KEY = "array";
	@PropertyIdentifier(type = Boolean.class)
	public static final String SHOW_ICON_KEY = "showIcon";
	@PropertyIdentifier(type = Boolean.class)
	public static final String SHOW_TEXT_KEY = "showText";
	@PropertyIdentifier(type = Class.class)
	public static final String ITERATOR_CLASS_KEY = "iteratorClass";
	@PropertyIdentifier(type = boolean.class)
	public static final String AUTO_SELECT_FIRST_ROW_KEY = "autoSelectFirstRow";

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

	@Getter(value = SHOW_ICON_KEY)
	@XMLAttribute
	public Boolean getShowIcon();

	@Setter(SHOW_ICON_KEY)
	public void setShowIcon(Boolean showIcon);

	@Getter(value = SHOW_TEXT_KEY)
	@XMLAttribute
	public Boolean getShowText();

	@Setter(SHOW_TEXT_KEY)
	public void setShowText(Boolean showText);

	@Getter(value = ITERATOR_CLASS_KEY)
	@XMLAttribute(xmlTag = "iteratorClassName")
	public Class getIteratorClass();

	@Setter(ITERATOR_CLASS_KEY)
	public void setIteratorClass(Class iteratorClass);

	@Getter(value = AUTO_SELECT_FIRST_ROW_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAutoSelectFirstRow();

	@Setter(AUTO_SELECT_FIRST_ROW_KEY)
	public void setAutoSelectFirstRow(boolean autoSelectFirstRow);

	public boolean isEnumType();

	@Override
	public Type getDataType();

	public static abstract class FIBMultipleValuesImpl extends FIBWidgetImpl implements FIBMultipleValues {

		private static final Logger logger = Logger.getLogger(FIBMultipleValues.class.getPackage().getName());

		@Deprecated
		public BindingDefinition LIST = new BindingDefinition("list",
				new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(Object.class)), DataBinding.BindingDefinitionType.GET, false) {
			@Override
			public Type getType() {
				return getListBindingType();
			}
		};
		@Deprecated
		public BindingDefinition ARRAY = new BindingDefinition("array", new GenericArrayTypeImpl(new WilcardTypeImpl(Object.class)),
				DataBinding.BindingDefinitionType.GET, false) {
			@Override
			public Type getType() {
				return getArrayBindingType();
			}
		};

		/*@Deprecated
		private final BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET_SET,
				false) {
			@Override
			public Type getType() {
				return getDataType();
			};
		};*/

		private String staticList;

		private DataBinding<List<?>> list;
		private DataBinding<Object[]> array;

		private Class iteratorClass;
		private Class expectedIteratorClass;

		private boolean showIcon = false;
		private boolean showText = true;

		private boolean autoSelectFirstRow = false;

		public FIBMultipleValuesImpl() {
		}

		private Type LIST_BINDING_TYPE;
		private Type ARRAY_BINDING_TYPE;

		private Type getListBindingType() {
			if (LIST_BINDING_TYPE == null) {
				LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(getIteratorType()));
			}
			return LIST_BINDING_TYPE;
		}

		private Type getArrayBindingType() {
			if (ARRAY_BINDING_TYPE == null) {
				ARRAY_BINDING_TYPE = new GenericArrayTypeImpl(new WilcardTypeImpl(getIteratorType()));
			}
			return ARRAY_BINDING_TYPE;
		}

		@Override
		public DataBinding<List<?>> getList() {
			if (list == null) {
				list = new DataBinding<List<?>>(this, new TypeToken<List<?>>() {
				}.getType(), DataBinding.BindingDefinitionType.GET);
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
			}
			this.list = list;
		}

		@Override
		public DataBinding<Object[]> getArray() {
			if (array == null) {
				array = new DataBinding<Object[]>(this, new TypeToken<Object[]>() {
				}.getType(), DataBinding.BindingDefinitionType.GET);
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
			}
			this.array = array;
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			if (list != null) {
				list.decode();
			}
			if (array != null) {
				array.decode();
			}
		}

		public boolean isStaticList() {
			return (getList() == null || !getList().isSet()) && (getArray() == null || !getArray().isSet())
					&& StringUtils.isNotEmpty(getStaticList());
		}

		@Override
		public boolean isEnumType() {
			if (getData() != null) {
				Type type = getData().getAnalyzedType();
				if (type instanceof Class && ((Class) type).isEnum()) {
					return true;
				}
			}
			if (iteratorClass != null && iteratorClass.isEnum()) {
				return true;
			}
			if (expectedIteratorClass != null && expectedIteratorClass.isEnum()) {
				return true;
			}
			return false;
		}

		private Type iteratorType;

		public Type getIteratorType() {
			Class<?> iteratorClass = getIteratorClass();
			if (iteratorClass.getTypeParameters().length > 0) {
				if (iteratorType == null) {
					iteratorType = TypeUtils.makeInferedType(iteratorClass);
				}
				return iteratorType;
			}
			return iteratorClass;
		}

		@Override
		public Class getIteratorClass() {
			if (isStaticList()) {
				return String.class;
			}
			if (iteratorClass == null) {
				if (expectedIteratorClass != null) {
					return expectedIteratorClass;
				}
				else {
					return Object.class;
				}
			}
			return iteratorClass;
		}

		@Override
		public void setIteratorClass(Class iteratorClass) {
			FIBPropertyNotification<Class> notification = requireChange(ITERATOR_CLASS_KEY, iteratorClass);
			if (notification != null) {
				LIST_BINDING_TYPE = null;
				ARRAY_BINDING_TYPE = null;
				this.iteratorClass = iteratorClass;
				hasChanged(notification);
			}
		}

		@Override
		public Type getDataType() {
			if (isStaticList()) {
				return String.class;
			}
			if (iteratorClass != null) {
				return iteratorClass;
			}
			return super.getDataType();
		}

		@Override
		public Type getFormattedObjectType() {
			if (isStaticList()) {
				return String.class;
			}
			if (iteratorClass != null) {
				return iteratorClass;
			}
			return getDataType();
		}

		/*@Deprecated
		@Override
		public BindingDefinition getDataBindingDefinition() {
			return DATA;
		}*/

		/*@Override
		public final Type getDefaultDataClass()
		{
			return getIteratorClass();
		}*/

		@Override
		public final Type getDefaultDataType() {
			return Object.class;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			// logger.info("******* notifyBindingChanged with "+binding);
			super.notifiedBindingChanged(binding);
			if (binding == getList()) {
				if (getList() != null) {
					Type accessedType = getList().getAnalyzedType();
					if (accessedType instanceof ParameterizedType
							&& ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
						Class newIteratorClass = TypeUtils.getBaseClass(((ParameterizedType) accessedType).getActualTypeArguments()[0]);
						if (getIteratorClass() == null || !TypeUtils.isClassAncestorOf(newIteratorClass, getIteratorClass())) {
							setIteratorClass(newIteratorClass);
						}
					}
				}
			}
			else if (binding == getArray()) {
				if (getArray() != null) {
					Type accessedType = getArray().getAnalyzedType();
					if (accessedType instanceof GenericArrayType) {
						Class newIteratorClass = TypeUtils.getBaseClass(((GenericArrayType) accessedType).getGenericComponentType());
						if (getIteratorClass() == null || !TypeUtils.isClassAncestorOf(newIteratorClass, getIteratorClass())) {
							setIteratorClass(newIteratorClass);
						}
					}
				}
			}
			else if (binding == getData()) {
				if (getData() != null) {
					Type accessedType = getData().getAnalyzedType();
					/*if (accessedType instanceof Class && ((Class)accessedType).isEnum()) {
						setIteratorClass((Class)accessedType);
					}*/
					if (accessedType instanceof Class) {
						expectedIteratorClass = (Class) accessedType;
					}

				}
			}
			else if (binding == getFormat()) {
				notifyChange(FORMAT_KEY, null, getFormat());
			}
		}

		@Override
		public String getStaticList() {
			return staticList;
		}

		@Override
		public final void setStaticList(String staticList) {
			FIBPropertyNotification<String> notification = requireChange(STATIC_LIST_KEY, staticList);
			if (notification != null) {
				this.staticList = staticList;
				LIST_BINDING_TYPE = null;
				ARRAY_BINDING_TYPE = null;
				// logger.info("FIBMultiple: setStaticList with " + staticList);
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getShowIcon() {
			return showIcon;
		}

		@Override
		public void setShowIcon(Boolean showIcon) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_ICON_KEY, showIcon);
			if (notification != null) {
				this.showIcon = showIcon;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getShowText() {
			return showText;
		}

		@Override
		public void setShowText(Boolean showText) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_TEXT_KEY, showText);
			if (notification != null) {
				this.showText = showText;
				hasChanged(notification);
			}
		}

		/*@Override
		public Type getDynamicAccessType() {
			Type[] args = new Type[4];
			args[0] = new WilcardTypeImpl(getClass());
			args[1] = new WilcardTypeImpl(Object.class);
			args[2] = getDataType();
			args[3] = getIteratorType();
			return new ParameterizedTypeImpl(FIBMultipleValueWidget.class, args);
		}*/

		@Override
		public boolean getAutoSelectFirstRow() {
			return autoSelectFirstRow;
		}

		@Override
		public void setAutoSelectFirstRow(boolean autoSelectFirstRow) {
			FIBPropertyNotification<Boolean> notification = requireChange(AUTO_SELECT_FIRST_ROW_KEY, autoSelectFirstRow);
			if (notification != null) {
				this.autoSelectFirstRow = autoSelectFirstRow;
				hasChanged(notification);
			}
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getList());
			returned.add(getArray());
			return returned;
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			super.searchLocalized(retriever);
			if (StringUtils.isNotEmpty(getStaticList())) {
				StringTokenizer st = new StringTokenizer(getStaticList(), ",");
				while (st.hasMoreTokens()) {
					retriever.foundLocalized(st.nextToken());
				}
			}

			// TODO localize enums ??? not sure it is relevant
			/*if (isEnumType()) {
				Class<? extends Enum<?>> type = getIteratorClass();
				for (Enum<?> e : type.getEnumConstants()) {
					
				}
			}*/
		}

	}

	@DefineValidationRule
	public static class FIBMultipleValuesMustDefineValueRange
			extends ValidationRule<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues> {
		public FIBMultipleValuesMustDefineValueRange() {
			super(FIBMultipleValues.class, "widget_must_define_values_range_(either_static_list_or_dynamic_list_or_array_or_enumeration)");
		}

		@Override
		public ValidationIssue<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues> applyValidation(FIBMultipleValues object) {

			if (!object.isHidden() && StringUtils.isEmpty(object.getStaticList()) && !object.getList().isSet() && !object.getArray().isSet()
					&& !object.isEnumType()) {
				GenerateDefaultStaticList fixProposal = new GenerateDefaultStaticList();
				return new ValidationError<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues>(this, object,
						"widget_does_not_define_any_values_range_(either_static_list_or_dynamic_list_or_array_or_enumeration)",
						fixProposal);
			}
			return null;
		}

		protected static class GenerateDefaultStaticList extends FixProposal<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues> {

			public GenerateDefaultStaticList() {
				super("generate_default_static_list");
			}

			@Override
			protected void fixAction() {
				getValidable().setStaticList("Item 1 ,Item 2 ,Item 3 ");
			}

		}
	}

	@DefineValidationRule
	public static class ListBindingMustBeValid extends BindingMustBeValid<FIBMultipleValues> {
		public ListBindingMustBeValid() {
			super("'list'_binding_is_not_valid", FIBMultipleValues.class);
		}

		@Override
		public DataBinding getBinding(FIBMultipleValues object) {
			return object.getList();
		}

	}

	@DefineValidationRule
	public static class ArrayBindingMustBeValid extends BindingMustBeValid<FIBMultipleValues> {
		public ArrayBindingMustBeValid() {
			super("'array'_binding_is_not_valid", FIBMultipleValues.class);
		}

		@Override
		public DataBinding getBinding(FIBMultipleValues object) {
			return object.getArray();
		}

	}

}
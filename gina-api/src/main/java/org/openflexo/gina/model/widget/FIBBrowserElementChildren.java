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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBBrowserElementChildren.FIBBrowserElementChildrenImpl.class)
@XMLElement(xmlTag = "Children")
public interface FIBBrowserElementChildren extends FIBModelObject {

	@PropertyIdentifier(type = FIBBrowserElement.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VISIBLE_KEY = "visible";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CAST_KEY = "cast";

	@Getter(value = OWNER_KEY /*, inverse = FIBBrowserElement.CHILDREN_KEY*/)
	public FIBBrowserElement getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBBrowserElement customColumn);

	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding<Object> getData();

	@Setter(DATA_KEY)
	public void setData(DataBinding<Object> data);

	@Getter(value = VISIBLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getVisible();

	@Setter(VISIBLE_KEY)
	public void setVisible(DataBinding<Boolean> visible);

	@Getter(value = CAST_KEY)
	@XMLAttribute
	public DataBinding<Object> getCast();

	@Setter(CAST_KEY)
	public void setCast(DataBinding<Object> cast);

	public void finalizeBrowserDeserialization();

	public boolean isMultipleAccess();

	public ImageIcon getImageIcon();

	public Class<?> getBaseClass();

	public Type getAccessedType();

	public Bindable getChildBindable();

	public void revalidateBindings();

	public void updateBindingModel();

	public static abstract class FIBBrowserElementChildrenImpl extends FIBModelObjectImpl implements FIBBrowserElementChildren {

		private static final Logger logger = Logger.getLogger(FIBBrowserElementChildren.class.getPackage().getName());

		// private FIBBrowserElement browserElement;
		private DataBinding<Object> data;
		private DataBinding<Boolean> visible;
		private DataBinding<Object> cast;
		private FIBChildBindable childBindable;

		@Override
		public String getPresentationName() {
			if (getData() != null && getData().isSet() && getData().isValid()) {
				return getData().toString();
			}
			return getName();
		}

		@Override
		public void updateBindingModel() {
			if (childBindable != null) {
				childBindable.updateBindingModel();
			}
		}

		private class FIBChildBindable extends DefaultBindable {
			private BindingModel childBindingModel = null;

			private void updateBindingModel() {
				if (childBindingModel != null) {
					childBindingModel.setBaseBindingModel(FIBBrowserElementChildrenImpl.this.getBindingModel());
				}
			}

			@Override
			public BindingFactory getBindingFactory() {
				return FIBBrowserElementChildrenImpl.this.getBindingFactory();
			}

			@Override
			public BindingModel getBindingModel() {
				if (childBindingModel == null) {
					createChildBindingModel();
				}
				return childBindingModel;
			}

			private void createChildBindingModel() {
				childBindingModel = new BindingModel(FIBBrowserElementChildrenImpl.this.getBindingModel());
				childBindingModel.addToBindingVariables(new BindingVariable("child", Object.class) {
					@Override
					public Type getType() {
						if (getData().isSet()) {
							Type type = getData().getAnalyzedType();
							if (isSupportedListType(type)) {
								if (type instanceof ParameterizedType) {
									return ((ParameterizedType) type).getActualTypeArguments()[0];
								}
								logger.warning(
										"Found supported list type " + type + " but it is not parameterized so I can't guess its content");
								return Object.class;
							}
							return type;
						}
						return Object.class;
					}

					@Override
					public boolean isCacheable() {
						return false;
					}

				});
			}

			public FIBComponent getComponent() {
				return getOwner().getComponent();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			}

		}

		@Override
		public FIBChildBindable getChildBindable() {
			if (childBindable == null) {
				childBindable = new FIBChildBindable();
			}
			return childBindable;
		}

		public FIBBrowser getBrowser() {
			return getOwner().getOwner();
		}

		@Override
		public DataBinding<Object> getData() {
			if (data == null) {
				data = new DataBinding<>(getOwner() != null ? getOwner().getIteratorBindable() : null, Object.class,
						DataBinding.BindingDefinitionType.GET);
			}
			return data;
		}

		@Override
		public void setData(DataBinding<Object> data) {
			if (data != null) {
				data.setOwner(getOwner() != null ? getOwner().getIteratorBindable() : null);
				data.setDeclaredType(Object.class);
				data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.data = data;
		}

		@Override
		public DataBinding<Boolean> getVisible() {
			if (visible == null) {
				visible = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return visible;
		}

		@Override
		public void setVisible(DataBinding<Boolean> visible) {
			if (visible != null) {
				visible.setOwner(getOwner() != null ? getOwner().getIteratorBindable() : null);
				visible.setDeclaredType(Boolean.class);
				visible.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.visible = visible;
		}

		@Override
		public DataBinding<Object> getCast() {
			if (cast == null) {
				cast = new DataBinding<>(getChildBindable(), Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return cast;
		}

		@Override
		public void setCast(DataBinding<Object> cast) {
			cast.setOwner(getChildBindable());
			cast.setDeclaredType(Object.class);
			cast.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			this.cast = cast;
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
			if (data != null) {
				data.setOwner(getOwner().getIteratorBindable());
				data.forceRevalidate();
			}
			if (visible != null) {
				visible.setOwner(getOwner().getIteratorBindable());
				visible.forceRevalidate();
			}
		}

		@Override
		public void finalizeBrowserDeserialization() {
			logger.fine("finalizeBrowserDeserialization() for FIBBrowserElementChildren ");
			if (data != null) {
				data.setOwner(getOwner().getIteratorBindable());
				data.decode();
			}
			if (visible != null) {
				visible.setOwner(getOwner().getIteratorBindable());
				visible.decode();
			}
		}

		@Override
		public ImageIcon getImageIcon() {
			if (getBaseClass() == null) {
				return null;
			}
			FIBBrowserElement e = getBrowser().elementForType(getBaseClass());
			if (e != null) {
				return e.getImageIcon();
			}
			return null;
		}

		@Override
		public Class getBaseClass() {
			Type accessedType = getAccessedType();
			if (accessedType == null) {
				return null;
			}
			if (isMultipleAccess()) {
				return TypeUtils.getBaseClass(((ParameterizedType) accessedType).getActualTypeArguments()[0]);
			}
			else {
				return TypeUtils.getBaseClass(getAccessedType());
			}
		}

		@Override
		public Type getAccessedType() {
			if (data != null && data.isSet()) {
				return data.getAnalyzedType();
			}
			return null;
		}

		@Override
		public boolean isMultipleAccess() {
			/*System.out.println("This="+this);
			System.out.println("getAccessedType()="+getAccessedType());
			System.out.println("TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(accessedType))="+TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(getAccessedType())));
			System.out.println("accessedType instanceof ParameterizedType="+(getAccessedType() instanceof ParameterizedType));
			System.out.println("((ParameterizedType)accessedType).getActualTypeArguments().length > 0="+(((ParameterizedType)getAccessedType()).getActualTypeArguments().length > 0));*/
			Type accessedType = getAccessedType();
			if (accessedType == null) {
				return false;
			}
			return isSupportedListType(accessedType);
		}

		private static boolean isSupportedListType(Type accessedType) {
			return TypeUtils.isClassAncestorOf(Iterable.class, TypeUtils.getBaseClass(accessedType))
					|| TypeUtils.isClassAncestorOf(Enumeration.class, TypeUtils.getBaseClass(accessedType));
		}

	}

	@DefineValidationRule
	public static class DataBindingMustBeValid extends BindingMustBeValid<FIBBrowserElementChildren> {
		public DataBindingMustBeValid() {
			super("'data'_binding_is_not_valid", FIBBrowserElementChildren.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElementChildren object) {
			return object.getData();
		}
	}

	@DefineValidationRule
	public static class VisibleBindingMustBeValid extends BindingMustBeValid<FIBBrowserElementChildren> {
		public VisibleBindingMustBeValid() {
			super("'visible'_binding_is_not_valid", FIBBrowserElementChildren.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElementChildren object) {
			return object.getVisible();
		}
	}

	@DefineValidationRule
	public static class CastBindingMustBeValid extends BindingMustBeValid<FIBBrowserElementChildren> {
		public CastBindingMustBeValid() {
			super("'cast'_binding_is_not_valid", FIBBrowserElementChildren.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElementChildren object) {
			return object.getCast();
		}
	}

}

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

package org.openflexo.gina.model;

import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.DataBinding.CachingStrategy;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.model.validation.ValidationWarning;
import org.openflexo.toolbox.StringUtils;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBWidget.FIBWidgetImpl.class)
public abstract interface FIBWidget extends FIBComponent {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = Boolean.class)
	public static final String READ_ONLY_KEY = "readOnly";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String FORMAT_KEY = "format";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ICON_KEY = "icon";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String TOOLTIP_KEY = "tooltip";
	@PropertyIdentifier(type = String.class)
	public static final String TOOLTIP_TEXT_KEY = "tooltipText";
	@PropertyIdentifier(type = Boolean.class)
	public static final String LOCALIZE_KEY = "localize";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ENABLE_KEY = "enable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String MANAGE_DYNAMIC_MODEL_KEY = "manageDynamicModel";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CLICK_ACTION_KEY = "clickAction";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DOUBLE_CLICK_ACTION_KEY = "doubleClickAction";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String RIGHT_CLICK_ACTION_KEY = "rightClickAction";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ENTER_PRESSED_ACTION_KEY = "enterPressedAction";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_CHANGED_ACTION_KEY = "valueChangedAction";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_TRANSFORM_KEY = "valueTransform";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_VALIDATOR_KEY = "valueValidator";

	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding<?> getData();

	@Setter(DATA_KEY)
	public void setData(DataBinding<?> data);

	public Type getDataType();

	public Class<?> getDataClass();

	// public void setDataType(Type type);

	@Getter(value = READ_ONLY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getReadOnly();

	@Setter(READ_ONLY_KEY)
	public void setReadOnly(boolean readOnly);

	@Getter(value = FORMAT_KEY)
	@XMLAttribute
	public DataBinding<String> getFormat();

	@Setter(FORMAT_KEY)
	public void setFormat(DataBinding<String> format);

	@Getter(value = ICON_KEY)
	@XMLAttribute
	public DataBinding<Icon> getIcon();

	@Setter(ICON_KEY)
	public void setIcon(DataBinding<Icon> icon);

	@Getter(value = TOOLTIP_KEY)
	@XMLAttribute
	public DataBinding<String> getTooltip();

	@Setter(TOOLTIP_KEY)
	public void setTooltip(DataBinding<String> tooltip);

	@Getter(value = TOOLTIP_TEXT_KEY)
	@XMLAttribute
	public String getTooltipText();

	@Setter(TOOLTIP_TEXT_KEY)
	public void setTooltipText(String tooltipText);

	@Getter(value = LOCALIZE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getLocalize();

	@Setter(LOCALIZE_KEY)
	public void setLocalize(boolean localize);

	@Getter(value = ENABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getEnable();

	@Setter(ENABLE_KEY)
	public void setEnable(DataBinding<Boolean> enable);

	@Getter(value = MANAGE_DYNAMIC_MODEL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getManageDynamicModel();

	@Setter(MANAGE_DYNAMIC_MODEL_KEY)
	public void setManageDynamicModel(boolean manageDynamicModel);

	@Getter(value = CLICK_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getClickAction();

	@Setter(CLICK_ACTION_KEY)
	public void setClickAction(DataBinding<?> clickAction);

	@Getter(value = DOUBLE_CLICK_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getDoubleClickAction();

	@Setter(DOUBLE_CLICK_ACTION_KEY)
	public void setDoubleClickAction(DataBinding<?> doubleClickAction);

	@Getter(value = RIGHT_CLICK_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getRightClickAction();

	@Setter(RIGHT_CLICK_ACTION_KEY)
	public void setRightClickAction(DataBinding<?> rightClickAction);

	@Getter(value = ENTER_PRESSED_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getEnterPressedAction();

	@Setter(ENTER_PRESSED_ACTION_KEY)
	public void setEnterPressedAction(DataBinding<?> enterPressedAction);

	@Getter(value = VALUE_CHANGED_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getValueChangedAction();

	@Setter(VALUE_CHANGED_ACTION_KEY)
	public void setValueChangedAction(DataBinding<?> valueChangedAction);

	@Getter(value = VALUE_TRANSFORM_KEY)
	@XMLAttribute
	public DataBinding<Object> getValueTransform();

	@Setter(VALUE_TRANSFORM_KEY)
	public void setValueTransform(DataBinding<Object> valueTransform);

	@Getter(value = VALUE_VALIDATOR_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getValueValidator();

	@Setter(VALUE_VALIDATOR_KEY)
	public void setValueValidator(DataBinding<Boolean> valueValidator);

	public boolean hasClickAction();

	public boolean hasDoubleClickAction();

	public boolean hasRightClickAction();

	public boolean hasEnterPressedAction();

	public Bindable getFormatter();

	public Bindable getValueBindable();

	public Bindable getEventListener();

	/*
	 * public void addFibListener(GinaEventListener l);
	 * 
	 * public List<GinaEventListener> getFibListeners();
	 */

	public Type getDefaultDataType();

	public static abstract class FIBWidgetImpl extends FIBComponentImpl implements FIBWidget {

		/*
		 * @Deprecated public static BindingDefinition TOOLTIP = new
		 * BindingDefinition("tooltip", String.class,
		 * DataBinding.BindingDefinitionType.GET, false);
		 * 
		 * @Deprecated public static BindingDefinition ENABLE = new
		 * BindingDefinition("enable", Boolean.class,
		 * DataBinding.BindingDefinitionType.GET, false);
		 * 
		 * @Deprecated public static BindingDefinition FORMAT = new
		 * BindingDefinition("format", String.class,
		 * DataBinding.BindingDefinitionType.GET, false);
		 * 
		 * @Deprecated public static BindingDefinition ICON = new
		 * BindingDefinition("icon", Icon.class,
		 * DataBinding.BindingDefinitionType.GET, false);
		 * 
		 * @Deprecated public static BindingDefinition VALUE_CHANGED_ACTION =
		 * new BindingDefinition("valueChangedAction", Void.class,
		 * DataBinding.BindingDefinitionType.EXECUTE, false);
		 * 
		 * @Deprecated public static BindingDefinition CLICK_ACTION = new
		 * BindingDefinition("clickAction", Void.class,
		 * DataBinding.BindingDefinitionType.EXECUTE, false);
		 * 
		 * @Deprecated public static BindingDefinition DOUBLE_CLICK_ACTION = new
		 * BindingDefinition("doubleClickAction", Void.class,
		 * DataBinding.BindingDefinitionType.EXECUTE, false);
		 * 
		 * @Deprecated public static final BindingDefinition RIGHT_CLICK_ACTION
		 * = new BindingDefinition("rightClickAction", Void.class,
		 * DataBinding.BindingDefinitionType.EXECUTE, false);
		 * 
		 * @Deprecated public static final BindingDefinition
		 * ENTER_PRESSED_ACTION = new BindingDefinition("enterPressedAction",
		 * Void.class, DataBinding.BindingDefinitionType.EXECUTE, false);
		 */

		private DataBinding<?> data;

		private DataBinding<String> tooltip;

		private DataBinding<Boolean> enable;
		private DataBinding<String> format;
		private DataBinding<Icon> icon;

		private boolean manageDynamicModel = false;
		private boolean readOnly = false;
		// private Boolean localize = true;
		private String tooltipText;
		private DataBinding<?> clickAction;
		private DataBinding<?> doubleClickAction;
		private DataBinding<?> rightClickAction;
		private DataBinding<?> enterPressedAction;
		private DataBinding<?> valueChangedAction;
		private DataBinding<Boolean> valueValidator;

		private final FIBFormatter formatter;
		private final FIBValueBindable valueBindable;
		private final FIBEventListener eventListener;
		private DataBinding<Object> valueTransform;

		// private List<GinaEventListener> fibListeners = new
		// ArrayList<GinaEventListener>();

		public FIBWidgetImpl() {
			super();
			formatter = new FIBFormatter();
			valueBindable = new FIBValueBindable();
			eventListener = new FIBEventListener();
		}

		@Override
		protected FIBWidgetType<?> makeViewType() {
			return new FIBWidgetType<>(this);
		}

		/*
		 * public void addFibListener(GinaEventListener l) {
		 * fibListeners.add(l); }
		 * 
		 * public List<GinaEventListener> getFibListeners() { return
		 * fibListeners; }
		 */

		/*
		 * public void actionPerformed(String action, Object... args) {
		 * FIBActionEvent e;
		 * 
		 * FIBEventFactory f =
		 * FIBActionListenerManager.getInstance().getFactory();
		 * 
		 * switch(action) { case "clicked": e = f.createButtonEvent(action);
		 * case "text-inserted": FIBTextEvent ev = f.createTextEvent(action, );
		 * e = ev; ev.setValue((String) args[0]); ev.setPosition((int) args[1]);
		 * break; case "text-removed": e.setPosition((int) args[0]);
		 * e.setSize((int) args[1]); break; }
		 * 
		 * e.setIdentity(this.getBaseName(), this.getName(),
		 * this.getRootComponent().getID());
		 * 
		 * this.actionPerformed(e); }
		 */

		@Override
		public void bindingModelMightChange(BindingModel oldBindingModel) {
			super.bindingModelMightChange(oldBindingModel);
			formatter.bindingModelMightChange(oldBindingModel);
			valueBindable.bindingModelMightChange(oldBindingModel);
			eventListener.bindingModelMightChange(oldBindingModel);
		}

		/*@Override
		public String getIdentifier() {
			return null;
		}*/

		@Override
		public Enumeration children() {
			return null;
		}

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		@Override
		public TreeNode getChildAt(int childIndex) {
			return null;
		}

		@Override
		public int getChildCount() {
			return 0;
		}

		@Override
		public int getIndex(TreeNode node) {
			return -1;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		private boolean isSettingData = false;

		@Override
		public DataBinding<?> getData() {
			if (data == null && !isSettingData) {
				isSettingData = true;
				data = new DataBinding<>(this, getDataType(), DataBinding.BindingDefinitionType.GET);
				data.setBindingName(DATA_KEY);
				isSettingData = false;
			}
			return data;
		}

		@Override
		public void setData(DataBinding<?> data) {
			if (data != null) {
				data.setOwner(this);
				data.setDeclaredType(getDataType());
				data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				data.setBindingName(DATA_KEY);
			}
			this.data = data;
		}

		@Override
		public DataBinding<String> getTooltip() {
			if (tooltip == null) {
				tooltip = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				tooltip.setBindingName(TOOLTIP_KEY);
			}
			return tooltip;
		}

		@Override
		public void setTooltip(DataBinding<String> tooltip) {
			if (tooltip != null) {
				tooltip.setOwner(this);
				tooltip.setDeclaredType(String.class);
				tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				tooltip.setBindingName(TOOLTIP_KEY);
			}
			this.tooltip = tooltip;
		}

		@Override
		public DataBinding<Boolean> getEnable() {
			if (enable == null) {
				enable = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
				enable.setBindingName(ENABLE_KEY);
			}
			return enable;
		}

		@Override
		public void setEnable(DataBinding<Boolean> enable) {
			if (enable != null) {
				enable.setOwner(this);
				enable.setDeclaredType(Boolean.class);
				enable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				enable.setBindingName(ENABLE_KEY);
			}
			this.enable = enable;
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (data != null) {
				data.revalidate();
			}
			if (enable != null) {
				enable.revalidate();
			}
			if (format != null) {
				format.revalidate();
			}
			if (icon != null) {
				icon.revalidate();
			}
			if (tooltip != null) {
				tooltip.revalidate();
			}
			if (clickAction != null) {
				clickAction.revalidate();
			}
			if (doubleClickAction != null) {
				doubleClickAction.revalidate();
			}
			if (rightClickAction != null) {
				rightClickAction.revalidate();
			}
			if (enterPressedAction != null) {
				enterPressedAction.revalidate();
			}
			if (valueChangedAction != null) {
				valueChangedAction.revalidate();
			}
			if (valueTransform != null) {
				valueTransform.revalidate();
			}
			if (valueValidator != null) {
				valueValidator.revalidate();
			}

		}

		@Override
		public void finalizeDeserialization() {

			super.finalizeDeserialization();
			getEventListener().createEventListenerBindingModel();
			if (data != null) {
				data.decode();
			}
			if (enable != null) {
				enable.decode();
			}
			if (format != null) {
				format.decode();
			}
			if (icon != null) {
				icon.decode();
			}
			if (tooltip != null) {
				tooltip.decode();
			}
			if (clickAction != null) {
				clickAction.decode();
			}
			if (doubleClickAction != null) {
				doubleClickAction.decode();
			}
			if (rightClickAction != null) {
				rightClickAction.decode();
			}
			if (enterPressedAction != null) {
				enterPressedAction.decode();
			}
			if (valueChangedAction != null) {
				valueChangedAction.decode();
			}
			if (valueTransform != null) {
				valueTransform.decode();
			}
			if (valueValidator != null) {
				valueValidator.decode();
			}
		}

		@Override
		public Type getDataType() {
			/*
			 * if (getData() != null && getData().isSet()) { return
			 * getData().getAnalyzedType(); }
			 */
			return getDefaultDataType();

		}

		@Override
		public Class<?> getDataClass() {
			return TypeUtils.getBaseClass(getDataType());
		}

		@Override
		public abstract Type getDefaultDataType();

		/*
		 * @Override public Type getDynamicAccessType() { if
		 * (getManageDynamicModel()) { if (getData() != null &&
		 * getData().isSet()) { return super.getDynamicAccessType(); } else {
		 * Type[] args = new Type[3]; args[0] = new WilcardTypeImpl(getClass());
		 * args[1] = new WilcardTypeImpl(Object.class); args[2] = getDataType();
		 * return new ParameterizedTypeImpl(FIBWidgetView.class, args); } }
		 * return null; }
		 */

		/**
		 * Return (create when null) binding variable identified by component name (this is dynamic access to data beeing edited in the
		 * component)<br>
		 * 
		 * @return
		 */
		@Override
		public BindingVariable getDynamicAccessBindingVariable() {
			if (dynamicAccessBindingVariable == null) {
				if (StringUtils.isNotEmpty(getName()) && getDynamicAccessType() != null && getManageDynamicModel()) {
					dynamicAccessBindingVariable = new BindingVariable(getName(), getDynamicAccessType());
					getBindingModel().addToBindingVariables(dynamicAccessBindingVariable);
				}
			}
			return dynamicAccessBindingVariable;
		}

		@Override
		public boolean getManageDynamicModel() {
			return manageDynamicModel;
		}

		@Override
		public void setManageDynamicModel(boolean manageDynamicModel) {
			FIBPropertyNotification<Boolean> notification = requireChange(MANAGE_DYNAMIC_MODEL_KEY, manageDynamicModel);
			if (notification != null) {
				this.manageDynamicModel = manageDynamicModel;
				updateDynamicAccessBindingVariable();
				hasChanged(notification);
			}
		}

		@Override
		public boolean getReadOnly() {
			return readOnly;
		}

		@Override
		public void setReadOnly(boolean readOnly) {
			FIBPropertyNotification<Boolean> notification = requireChange(READ_ONLY_KEY, readOnly);
			if (notification != null) {
				this.readOnly = readOnly;
				hasChanged(notification);
			}
		}

		@Override
		public String getTooltipText() {
			return tooltipText;
		}

		@Override
		public void setTooltipText(String tooltipText) {
			FIBPropertyNotification<String> notification = requireChange(TOOLTIP_TEXT_KEY, tooltipText);
			if (notification != null) {
				this.tooltipText = tooltipText;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<Object> getValueTransform() {
			if (valueTransform == null) {
				valueTransform = new DataBinding<Object>(valueBindable, getDataType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getDataType();
					}
				};
				valueTransform.setBindingName("valueTransform");
			}
			return valueTransform;
		}

		@Override
		public void setValueTransform(DataBinding<Object> valueTransform) {
			if (valueTransform != null) {
				this.valueTransform = new DataBinding<Object>(valueTransform.toString(), this, valueTransform.getDeclaredType(),
						valueTransform.getBindingDefinitionType()) {
					@Override
					public Type getDeclaredType() {
						return getDataType();
					}
				};
				this.valueTransform.setBindingName("valueTransform");
			}
			else {
				this.valueTransform = null;
			}
		}

		@Override
		public FIBValueBindable getValueBindable() {
			return valueBindable;
		}

		@Override
		public DataBinding<String> getFormat() {
			if (format == null) {
				format = new DataBinding<>(formatter, String.class, DataBinding.BindingDefinitionType.GET);
				format.setCachingStrategy(CachingStrategy.NO_CACHING);
				format.setBindingName(FORMAT_KEY);
			}
			return format;
		}

		@Override
		public void setFormat(DataBinding<String> format) {
			FIBPropertyNotification<DataBinding<String>> notification = requireChange(FORMAT_KEY, format);
			if (notification != null) {
				if (format != null) {
					format.setOwner(formatter);
					format.setDeclaredType(String.class);
					format.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					format.setCachingStrategy(CachingStrategy.NO_CACHING);
					format.setBindingName(FORMAT_KEY);
				}
				this.format = format;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<Icon> getIcon() {
			if (icon == null) {
				icon = new DataBinding<>(formatter, Icon.class, DataBinding.BindingDefinitionType.GET);
				icon.setCachingStrategy(CachingStrategy.NO_CACHING);
				icon.setBindingName(ICON_KEY);
			}
			return icon;
		}

		@Override
		public void setIcon(DataBinding<Icon> icon) {
			FIBPropertyNotification<DataBinding<Icon>> notification = requireChange(ICON_KEY, icon);
			if (notification != null) {
				if (icon != null) {
					icon.setOwner(formatter);
					icon.setDeclaredType(Icon.class);
					icon.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					icon.setCachingStrategy(CachingStrategy.NO_CACHING);
					icon.setBindingName(ICON_KEY);
				}
				this.icon = icon;
				hasChanged(notification);
			}
		}

		/*
		 * @Override public Boolean getLocalize() { return localize; }
		 * 
		 * @Override public void setLocalize(Boolean localize) {
		 * FIBPropertyNotification<Boolean> notification =
		 * requireChange(LOCALIZE_KEY, localize); if (notification != null) {
		 * this.localize = localize; hasChanged(notification); } }
		 */

		@Override
		public FIBFormatter getFormatter() {
			return formatter;
		}

		public Type getFormattedObjectType() {
			return getDataType();
		}

		/*
		 * @Override public void notifiedBindingModelRecreated() {
		 * super.notifiedBindingModelRecreated(); if (getFormatter() != null) {
		 * getFormatter().notifiedBindingModelRecreated(); } }
		 */

		private class FIBFormatter extends DefaultBindable {
			private BindingModel formatterBindingModel = null;

			private void bindingModelMightChange(BindingModel oldBindingModel) {
				getBindingModel();
				formatterBindingModel.setBaseBindingModel(FIBWidgetImpl.this.getBindingModel());
			}

			/*
			 * public void notifiedBindingModelRecreated() {
			 * createFormatterBindingModel(); }
			 */

			@Override
			public BindingModel getBindingModel() {
				if (formatterBindingModel == null) {
					createFormatterBindingModel();
				}
				return formatterBindingModel;
			}

			private void createFormatterBindingModel() {
				formatterBindingModel = new BindingModel(FIBWidgetImpl.this.getBindingModel());
				formatterBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
					@Override
					public Type getType() {
						return getFormattedObjectType();
					}

					@Override
					public boolean isCacheable() {
						return false;
					}
				});
			}

			public FIBComponent getComponent() {
				return FIBWidgetImpl.this;
			}

			@Override
			public String toString() {
				if (FIBWidgetImpl.this instanceof FIBDropDown) {
					return "FIBFormatter[" + FIBWidgetImpl.this + "] iteratorClass=" + ((FIBDropDown) FIBWidgetImpl.this).getIteratorClass()
							+ " dataType=" + ((FIBDropDown) FIBWidgetImpl.this).getDataType() + " obtained from "
							+ ((FIBDropDown) FIBWidgetImpl.this).getDescription();
				}
				return "FIBFormatter[" + FIBWidgetImpl.this + "]" + " dataType=" + FIBWidgetImpl.this.getDataType();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> binding) {
				if (binding == getFormat()) {
					FIBWidgetImpl.this.notifiedBindingChanged(binding);
				}
			}

			@Override
			public BindingFactory getBindingFactory() {
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {

			}

		}

		private class FIBValueBindable extends DefaultBindable {
			private BindingModel valueTransformerBindingModel = null;

			private void bindingModelMightChange(BindingModel oldBindingModel) {
				getBindingModel();
				valueTransformerBindingModel.setBaseBindingModel(FIBWidgetImpl.this.getBindingModel());
			}

			@Override
			public BindingModel getBindingModel() {
				if (valueTransformerBindingModel == null) {
					createValueTransformerBindingModel();
				}
				return valueTransformerBindingModel;
			}

			private void createValueTransformerBindingModel() {
				valueTransformerBindingModel = new BindingModel(FIBWidgetImpl.this.getBindingModel());
				valueTransformerBindingModel.addToBindingVariables(new BindingVariable("value", Object.class) {
					@Override
					public Type getType() {
						return getDataType();
					}

					@Override
					public boolean isCacheable() {
						return false;
					}
				});
			}

			public FIBComponent getComponent() {
				return FIBWidgetImpl.this;
			}

			@Override
			public String toString() {
				if (FIBWidgetImpl.this instanceof FIBDropDown) {
					return "FIBValueBindable[" + FIBWidgetImpl.this + "] iteratorClass="
							+ ((FIBDropDown) FIBWidgetImpl.this).getIteratorClass() + " dataType="
							+ ((FIBDropDown) FIBWidgetImpl.this).getDataType() + " obtained from "
							+ ((FIBDropDown) FIBWidgetImpl.this).getDescription();
				}
				return "FIBValueBindable[" + FIBWidgetImpl.this + "]" + " dataType=" + FIBWidgetImpl.this.getDataType();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> binding) {
				if (binding == getValueTransform()) {
					FIBWidgetImpl.this.notifiedBindingChanged(binding);
				}
				else if (binding == getValueValidator()) {
					FIBWidgetImpl.this.notifiedBindingChanged(binding);
				}
			}

			@Override
			public BindingFactory getBindingFactory() {
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {

			}

		}

		/*
		 * @Override public void updateBindingModel() {
		 * super.updateBindingModel(); if (deserializationPerformed) {
		 * getEventListener().createEventListenerBindingModel();
		 * getFormatter().createFormatterBindingModel(); } }
		 */

		@Override
		public FIBEventListener getEventListener() {
			return eventListener;
		}

		private class FIBEventListener extends DefaultBindable {
			private BindingModel eventListenerBindingModel = null;

			private void bindingModelMightChange(BindingModel oldBindingModel) {
				getBindingModel();
				eventListenerBindingModel.setBaseBindingModel(FIBWidgetImpl.this.getBindingModel());
			}

			@Override
			public BindingModel getBindingModel() {
				if (eventListenerBindingModel == null) {
					createEventListenerBindingModel();
				}
				return eventListenerBindingModel;
			}

			private void createEventListenerBindingModel() {
				eventListenerBindingModel = new BindingModel(FIBWidgetImpl.this.getBindingModel());
				BindingVariable eventVariable = new BindingVariable("event", FIBMouseEvent.class);
				eventVariable.setCacheable(false);
				eventListenerBindingModel.addToBindingVariables(eventVariable);
			}

			public FIBComponent getComponent() {
				return FIBWidgetImpl.this;
			}

			@Override
			public String toString() {
				return "FIBEventListener[" + FIBWidgetImpl.this + "]";
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> binding) {
				if (binding == getClickAction() || binding == getDoubleClickAction() || binding == getRightClickAction()) {
					FIBWidgetImpl.this.notifiedBindingChanged(binding);
				}
			}

			@Override
			public BindingFactory getBindingFactory() {
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {

			}
		}

		@Override
		public DataBinding<Boolean> getValueValidator() {
			if (valueValidator == null) {
				valueValidator = new DataBinding<>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
				valueValidator.setBindingName(VALUE_VALIDATOR_KEY);
			}
			return valueValidator;
		}

		@Override
		public void setValueValidator(DataBinding<Boolean> valueValidator) {
			if (valueValidator != null) {
				valueValidator.setOwner(this);
				valueValidator.setDeclaredType(Boolean.class);
				valueValidator.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				valueValidator.setBindingName(VALUE_VALIDATOR_KEY);
			}
			this.valueValidator = valueValidator;
		}

		@Override
		public DataBinding<?> getValueChangedAction() {
			if (valueChangedAction == null) {
				valueChangedAction = new DataBinding<>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				valueChangedAction.setBindingName(VALUE_CHANGED_ACTION_KEY);
			}
			return valueChangedAction;
		}

		@Override
		public void setValueChangedAction(DataBinding<?> valueChangedAction) {
			if (valueChangedAction != null) {
				valueChangedAction.setOwner(this);
				valueChangedAction.setDeclaredType(Object.class);
				valueChangedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				valueChangedAction.setBindingName(VALUE_CHANGED_ACTION_KEY);
			}
			this.valueChangedAction = valueChangedAction;
		}

		@Override
		public boolean hasClickAction() {
			return clickAction != null && clickAction.isValid();
		}

		@Override
		public final DataBinding<?> getClickAction() {
			if (clickAction == null) {
				clickAction = new DataBinding<>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				clickAction.setBindingName(CLICK_ACTION_KEY);
			}
			return clickAction;
		}

		@Override
		public final void setClickAction(DataBinding<?> clickAction) {
			if (clickAction != null) {
				clickAction.setOwner(eventListener);
				clickAction.setDeclaredType(Object.class);
				clickAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				clickAction.setBindingName(CLICK_ACTION_KEY);
			}
			this.clickAction = clickAction;
		}

		@Override
		public boolean hasDoubleClickAction() {
			return doubleClickAction != null && doubleClickAction.isValid();
		}

		@Override
		public DataBinding<?> getDoubleClickAction() {
			if (doubleClickAction == null) {
				doubleClickAction = new DataBinding<>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				doubleClickAction.setBindingName(DOUBLE_CLICK_ACTION_KEY);
			}
			return doubleClickAction;
		}

		@Override
		public void setDoubleClickAction(DataBinding<?> doubleClickAction) {
			if (doubleClickAction != null) {
				doubleClickAction.setOwner(eventListener);
				doubleClickAction.setDeclaredType(Object.class);
				doubleClickAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				doubleClickAction.setBindingName(DOUBLE_CLICK_ACTION_KEY);
			}
			this.doubleClickAction = doubleClickAction;
		}

		@Override
		public boolean hasRightClickAction() {
			return rightClickAction != null && rightClickAction.isValid();
		}

		@Override
		public DataBinding<?> getRightClickAction() {
			if (rightClickAction == null) {
				rightClickAction = new DataBinding<>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				rightClickAction.setBindingName(RIGHT_CLICK_ACTION_KEY);
			}
			return rightClickAction;
		}

		@Override
		public void setRightClickAction(DataBinding<?> rightClickAction) {
			if (rightClickAction != null) {
				rightClickAction.setOwner(eventListener);
				rightClickAction.setDeclaredType(Object.class);
				rightClickAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				rightClickAction.setBindingName(RIGHT_CLICK_ACTION_KEY);
			}
			this.rightClickAction = rightClickAction;
		}

		@Override
		public boolean hasEnterPressedAction() {
			return enterPressedAction != null && enterPressedAction.isValid();
		}

		@Override
		public DataBinding<?> getEnterPressedAction() {
			if (enterPressedAction == null) {
				enterPressedAction = new DataBinding<>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
				enterPressedAction.setBindingName(ENTER_PRESSED_ACTION_KEY);
			}
			return enterPressedAction;
		}

		@Override
		public void setEnterPressedAction(DataBinding<?> enterPressedAction) {
			if (enterPressedAction != null) {
				enterPressedAction.setOwner(eventListener);
				enterPressedAction.setDeclaredType(Object.class);
				enterPressedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				enterPressedAction.setBindingName(ENTER_PRESSED_ACTION_KEY);
			}
			this.enterPressedAction = enterPressedAction;
		}

		public boolean isPaletteElement() {
			return getParameter("isPaletteElement") != null && getParameter("isPaletteElement").equalsIgnoreCase("true");
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getEnable());
			return returned;
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			retriever.foundLocalized(getTooltipText());
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			// If a binding changed its value, then notify related property (asserting binding name has been set to property name)
			getPropertyChangeSupport().firePropertyChange(binding.getBindingName() != null ? binding.getBindingName() : "UnknownBinding",
					null, binding);
		}

	}

	@DefineValidationRule
	public static class FIBWidgetDeclaredAsDynamicShouldHaveAName
			extends ValidationRule<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> {
		public FIBWidgetDeclaredAsDynamicShouldHaveAName() {
			super(FIBWidget.class, "widgets_declaring_managing_dynamic_model_should_have_a_name");
		}

		@Override
		public ValidationIssue<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> applyValidation(FIBWidget object) {
			if (object.getManageDynamicModel() && StringUtils.isEmpty(object.getName())) {
				GenerateDefaultName fixProposal1 = new GenerateDefaultName();
				DisableDynamicModelManagement fixProposal2 = new DisableDynamicModelManagement();
				return new ValidationWarning<>(this, object,
						"widget_($validable.toString)_declares_managing_dynamic_model_but_does_not_have_a_name", fixProposal1,
						fixProposal2);
			}
			return null;
		}

		protected static class GenerateDefaultName extends FixProposal<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> {

			public GenerateDefaultName() {
				super("generate_default_name_:_'($defaultName)'");
			}

			@Override
			protected void fixAction() {
				getValidable().setName(getDefaultName());
			}

			public String getDefaultName() {
				return getValidable().generateUniqueName(getValidable().getBaseName());
			}

		}

		protected static class DisableDynamicModelManagement extends FixProposal<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> {

			public DisableDynamicModelManagement() {
				super("disable_dynamic_model_management");
			}

			@Override
			protected void fixAction() {
				getValidable().setManageDynamicModel(false);
			}

		}
	}

	@DefineValidationRule
	public static class TooltipBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public TooltipBindingMustBeValid() {
			super("'tooltip'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getTooltip();
		}

	}

	@DefineValidationRule
	public static class EnableBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public EnableBindingMustBeValid() {
			super("'enable'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<Boolean> getBinding(FIBWidget object) {
			return object.getEnable();
		}

	}

	@DefineValidationRule
	public static class FormatBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public FormatBindingMustBeValid() {
			super("'format'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<String> getBinding(FIBWidget object) {
			return object.getFormat();
		}

	}

	@DefineValidationRule
	public static class IconBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public IconBindingMustBeValid() {
			super("'icon'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<Icon> getBinding(FIBWidget object) {
			return object.getIcon();
		}

	}

	@DefineValidationRule
	public static class ClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public ClickActionBindingMustBeValid() {
			super("'click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBWidget object) {
			return object.getClickAction();
		}

	}

	@DefineValidationRule
	public static class DoubleClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public DoubleClickActionBindingMustBeValid() {
			super("'double_click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBWidget object) {
			return object.getDoubleClickAction();
		}

	}

	@DefineValidationRule
	public static class RightClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public RightClickActionBindingMustBeValid() {
			super("'right_click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBWidget object) {
			return object.getRightClickAction();
		}

	}

	@DefineValidationRule
	public static class ValueChangeActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public ValueChangeActionBindingMustBeValid() {
			super("'value_change_acion'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBWidget object) {
			return object.getValueChangedAction();
		}

	}

}

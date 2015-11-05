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
import java.awt.Font;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.DataBinding.CachingStrategy;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.gina.model.FIBModelObject.BindingMustBeValid;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBTableColumn.FIBTableColumnImpl.class)
@Imports({ @Import(FIBButtonColumn.class), @Import(FIBCheckBoxColumn.class), @Import(FIBCustomColumn.class),
		@Import(FIBDropDownColumn.class), @Import(FIBIconColumn.class), @Import(FIBLabelColumn.class), @Import(FIBNumberColumn.class),
		@Import(FIBTextFieldColumn.class) })
public abstract interface FIBTableColumn extends FIBModelObject {

	public static enum ColumnType {
		CheckBox, Custom, DropDown, Icon, Label, Number, TextField, Button;
	}

	@PropertyIdentifier(type = FIBTable.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String FORMAT_KEY = "format";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ICON_KEY = "icon";
	@PropertyIdentifier(type = Boolean.class)
	public static final String SHOW_ICON_KEY = "showIcon";
	@PropertyIdentifier(type = String.class)
	public static final String TITLE_KEY = "title";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMN_WIDTH_KEY = "columnWidth";
	@PropertyIdentifier(type = Boolean.class)
	public static final String RESIZABLE_KEY = "resizable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_TITLE_KEY = "displayTitle";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String COLOR_KEY = "color";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String BG_COLOR_KEY = "bgColor";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String TOOLTIP_KEY = "tooltip";
	@PropertyIdentifier(type = String.class)
	public static final String TOOLTIP_TEXT_KEY = "tooltipText";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_CHANGED_ACTION_KEY = "valueChangedAction";

	@Getter(value = OWNER_KEY, inverse = FIBTable.COLUMNS_KEY)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBTable getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBTable customColumn);

	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding<?> getData();

	@Setter(DATA_KEY)
	public void setData(DataBinding<?> data);

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

	@Getter(value = SHOW_ICON_KEY)
	@XMLAttribute
	public Boolean getShowIcon();

	@Setter(SHOW_ICON_KEY)
	public void setShowIcon(Boolean showIcon);

	@Getter(value = TITLE_KEY)
	@XMLAttribute
	public String getTitle();

	@Setter(TITLE_KEY)
	public void setTitle(String title);

	@Getter(value = COLUMN_WIDTH_KEY)
	@XMLAttribute
	public Integer getColumnWidth();

	@Setter(COLUMN_WIDTH_KEY)
	public void setColumnWidth(Integer columnWidth);

	@Getter(value = RESIZABLE_KEY)
	@XMLAttribute
	public Boolean getResizable();

	@Setter(RESIZABLE_KEY)
	public void setResizable(Boolean resizable);

	@Getter(value = DISPLAY_TITLE_KEY)
	@XMLAttribute
	public Boolean getDisplayTitle();

	@Setter(DISPLAY_TITLE_KEY)
	public void setDisplayTitle(Boolean displayTitle);

	@Getter(value = FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(FONT_KEY)
	public void setFont(Font font);

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public DataBinding<Color> getColor();

	@Setter(COLOR_KEY)
	public void setColor(DataBinding<Color> color);

	@Getter(value = BG_COLOR_KEY)
	@XMLAttribute
	public DataBinding<Color> getBgColor();

	@Setter(BG_COLOR_KEY)
	public void setBgColor(DataBinding<Color> bgColor);

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

	@Getter(value = VALUE_CHANGED_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getValueChangedAction();

	@Setter(VALUE_CHANGED_ACTION_KEY)
	public void setValueChangedAction(DataBinding<?> valueChangedAction);

	public void finalizeTableDeserialization();

	public Font retrieveValidFont();

	public Type getDataClass();

	public ColumnType getColumnType();

	public boolean getHasSpecificFont();

	public void setHasSpecificFont(boolean aFlag);

	public Bindable getFormatter();

	public void searchLocalized(LocalizationEntryRetriever retriever);

	public static abstract class FIBTableColumnImpl extends FIBModelObjectImpl implements FIBTableColumn {

		private static final Logger logger = Logger.getLogger(FIBTableColumn.class.getPackage().getName());

		@Deprecated
		private static BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		private static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		private static BindingDefinition FORMAT = new BindingDefinition("format", String.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		private static BindingDefinition COLOR = new BindingDefinition("color", Color.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		private static BindingDefinition BG_COLOR = new BindingDefinition("bgColor", Color.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		private static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class,
				DataBinding.BindingDefinitionType.EXECUTE, false);

		private DataBinding<?> data;
		private DataBinding<String> format;
		private DataBinding<Icon> icon;
		private DataBinding<String> tooltip;
		private DataBinding<Color> color;
		private DataBinding<Color> bgColor;
		private String title;
		private String tooltipText;
		private int columnWidth = 100;
		private boolean resizable = true;
		private boolean displayTitle = true;
		private Font font;
		private DataBinding<?> valueChangedAction;
		private boolean showIcon = false;

		private final FIBFormatter formatter;

		public FIBTableColumnImpl() {
			formatter = new FIBFormatter();
		}

		public BindingDefinition getDataBindingDefinition() {
			return DATA;
		}

		public BindingDefinition getTooltipBindingDefinition() {
			return TOOLTIP;
		}

		public BindingDefinition getFormatBindingDefinition() {
			return FORMAT;
		}

		public BindingDefinition getColorBindingDefinition() {
			return COLOR;
		}

		public BindingDefinition getBgColorBindingDefinition() {
			return BG_COLOR;
		}

		public BindingDefinition getValueChangedActionBindingDefinition() {
			return VALUE_CHANGED_ACTION;
		}

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		protected void bindingModelMightChange(BindingModel oldBindingModel) {
			formatter.bindingModelMightChange(oldBindingModel);
		}

		@Override
		public DataBinding<?> getData() {
			if (data == null) {
				data = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
				data.setCachingStrategy(CachingStrategy.NO_CACHING);
			}
			return data;
		}

		@Override
		public void setData(DataBinding<?> data) {
			if (data != null) {
				data.setOwner(this);
				data.setDeclaredType(Object.class);
				data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				data.setCachingStrategy(CachingStrategy.NO_CACHING);
			}
			this.data = data;
		}

		@Override
		public void finalizeTableDeserialization() {
			logger.fine("finalizeDeserialization() for FIBTableColumn " + title);
			if (data != null) {
				data.decode();
			}
			if (tooltip != null) {
				tooltip.decode();
			}
		}

		@Override
		public BindingModel getBindingModel() {
			if (getOwner() != null) {
				return getOwner().getTableBindingModel();
			}
			return null;
		}

		@Override
		public Type getDataClass() {
			if (getData() != null) {
				return getData().getAnalyzedType();
			}
			return getDefaultDataClass();
		}

		public abstract Type getDefaultDataClass();

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public void setTitle(String title) {
			FIBPropertyNotification<String> notification = requireChange(TITLE_KEY, title);
			if (notification != null) {
				this.title = title;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getColumnWidth() {
			return columnWidth;
		}

		@Override
		public void setColumnWidth(Integer columnWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(COLUMN_WIDTH_KEY, columnWidth);
			if (notification != null) {
				this.columnWidth = columnWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getResizable() {
			return resizable;
		}

		@Override
		public void setResizable(Boolean resizable) {
			FIBPropertyNotification<Boolean> notification = requireChange(RESIZABLE_KEY, resizable);
			if (notification != null) {
				this.resizable = resizable;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getDisplayTitle() {
			return displayTitle;
		}

		@Override
		public void setDisplayTitle(Boolean displayTitle) {
			FIBPropertyNotification<Boolean> notification = requireChange(DISPLAY_TITLE_KEY, displayTitle);
			if (notification != null) {
				this.displayTitle = displayTitle;
				hasChanged(notification);
			}
		}

		@Override
		public Font retrieveValidFont() {
			if (font == null && getOwner() != null) {
				return getOwner().retrieveValidFont();
			}
			return getFont();
		}

		@Override
		public Font getFont() {
			return font;
		}

		@Override
		public void setFont(Font font) {
			FIBPropertyNotification<Font> notification = requireChange(FONT_KEY, font);
			if (notification != null) {
				this.font = font;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getHasSpecificFont() {
			return getFont() != null;
		}

		@Override
		public void setHasSpecificFont(boolean aFlag) {
			if (aFlag) {
				setFont(retrieveValidFont());
			} else {
				setFont(null);
			}
		}

		@Override
		public abstract ColumnType getColumnType();

		@Override
		public DataBinding<String> getFormat() {
			if (format == null) {
				format = new DataBinding<String>(formatter, String.class, DataBinding.BindingDefinitionType.GET);
				format.setCachingStrategy(CachingStrategy.NO_CACHING);
				format.setBindingName("format");
			}
			return format;
		}

		@Override
		public void setFormat(DataBinding<String> format) {
			if (format != null) {
				format.setOwner(formatter);
				format.setDeclaredType(String.class);
				format.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				format.setCachingStrategy(CachingStrategy.NO_CACHING);
				format.setBindingName("format");
			}
			this.format = format;
		}

		@Override
		public DataBinding<Icon> getIcon() {
			if (icon == null) {
				icon = new DataBinding<Icon>(formatter, Icon.class, DataBinding.BindingDefinitionType.GET);
				icon.setCachingStrategy(CachingStrategy.NO_CACHING);
				icon.setBindingName("icon");
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
					icon.setBindingName("icon");
				}
				this.icon = icon;
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
		public FIBFormatter getFormatter() {
			return formatter;
		}

		private class FIBFormatter extends DefaultBindable {
			private BindingModel formatterBindingModel = null;

			private void bindingModelMightChange(BindingModel oldBindingModel) {
				getBindingModel();
				formatterBindingModel.setBaseBindingModel(getOwner().getTableBindingModel());
			}

			@Override
			public BindingModel getBindingModel() {
				if (formatterBindingModel == null) {
					createFormatterBindingModel();
				}
				return formatterBindingModel;
			}

			private void createFormatterBindingModel() {
				if (getOwner() != null) {
					formatterBindingModel = new BindingModel(getOwner().getTableBindingModel());
					formatterBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
						@Override
						public Type getType() {
							return getDataClass();
						}

						@Override
						public boolean isCacheable() {
							return false;
						}
					});
				}
			}

			public FIBComponent getComponent() {
				return FIBTableColumnImpl.this.getComponent();
			}

			@Override
			public BindingFactory getBindingFactory() {
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			}

		}

		public int getIndex() {
			if (getOwner() != null) {
				return getOwner().getColumns().indexOf(this);
			}
			return -1;
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
		public DataBinding<String> getTooltip() {
			if (tooltip == null) {
				tooltip = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
				tooltip.setCachingStrategy(CachingStrategy.NO_CACHING);
				tooltip.setBindingName("tooltip");
			}
			return tooltip;
		}

		@Override
		public void setTooltip(DataBinding<String> tooltip) {
			if (tooltip != null) {
				tooltip.setOwner(this);
				tooltip.setDeclaredType(String.class);
				tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				tooltip.setCachingStrategy(CachingStrategy.NO_CACHING);
				tooltip.setBindingName("tooltip");
			}
			this.tooltip = tooltip;
		}

		@Override
		public DataBinding<Color> getColor() {
			if (color == null) {
				color = new DataBinding<Color>(this, Color.class, DataBinding.BindingDefinitionType.GET);
				color.setCachingStrategy(CachingStrategy.NO_CACHING);
				color.setBindingName("color");
			}
			return color;
		}

		@Override
		public void setColor(DataBinding<Color> color) {
			if (color != null) {
				color.setOwner(this);
				color.setDeclaredType(Color.class);
				color.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				color.setCachingStrategy(CachingStrategy.NO_CACHING);
				color.setBindingName("color");
			}
			this.color = color;
		}

		@Override
		public DataBinding<Color> getBgColor() {
			if (bgColor == null) {
				bgColor = new DataBinding<Color>(this, Color.class, DataBinding.BindingDefinitionType.GET);
				bgColor.setCachingStrategy(CachingStrategy.NO_CACHING);
				bgColor.setBindingName("bgColor");
			}
			return bgColor;
		}

		@Override
		public void setBgColor(DataBinding<Color> bgColor) {
			if (bgColor != null) {
				bgColor.setOwner(this);
				bgColor.setDeclaredType(Color.class);
				bgColor.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				bgColor.setCachingStrategy(CachingStrategy.NO_CACHING);
				bgColor.setBindingName("bgColor");
			}
			this.bgColor = bgColor;
		}

		@Override
		public DataBinding<?> getValueChangedAction() {
			if (valueChangedAction == null) {
				valueChangedAction = new DataBinding<Void>(this, Void.class, DataBinding.BindingDefinitionType.EXECUTE);
				valueChangedAction.setCachingStrategy(CachingStrategy.NO_CACHING);
				valueChangedAction.setBindingName("valueChangedAction");
			}
			return valueChangedAction;
		}

		@Override
		public void setValueChangedAction(DataBinding<?> valueChangedAction) {
			if (valueChangedAction != null) {
				valueChangedAction.setOwner(this);
				valueChangedAction.setDeclaredType(Void.class);
				valueChangedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
				valueChangedAction.setCachingStrategy(CachingStrategy.NO_CACHING);
				valueChangedAction.setBindingName("valueChangedAction");
			}
			this.valueChangedAction = valueChangedAction;
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			retriever.foundLocalized(getTitle());
			retriever.foundLocalized(getTooltipText());
		}

	}

	@DefineValidationRule
	public static class DataBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public DataBindingMustBeValid() {
			super("'data'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getData();
		}
	}

	@DefineValidationRule
	public static class FormatBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public FormatBindingMustBeValid() {
			super("'format'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getFormat();
		}
	}

	@DefineValidationRule
	public static class TooltipBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public TooltipBindingMustBeValid() {
			super("'tooltip'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getTooltip();
		}
	}

	@DefineValidationRule
	public static class ColorBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public ColorBindingMustBeValid() {
			super("'color'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getColor();
		}
	}

	@DefineValidationRule
	public static class BgColorBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public BgColorBindingMustBeValid() {
			super("'background_color'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getBgColor();
		}
	}

	@DefineValidationRule
	public static class ValueChangedActionBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public ValueChangedActionBindingMustBeValid() {
			super("'value_changed_action'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getValueChangedAction();
		}
	}

}

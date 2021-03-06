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
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBList.FIBListImpl.class)
@XMLElement(xmlTag = "List")
public interface FIBList extends FIBMultipleValues {

	public static enum LayoutOrientation {
		vertical {
			@Override
			public int getSwingValue() {
				return 0;
			}
		},
		horizontal {
			@Override
			public int getSwingValue() {
				return 1;
			}
		},
		jesaispas {
			@Override
			public int getSwingValue() {
				return 2;
			}
		};

		public abstract int getSwingValue();
	}

	@PropertyIdentifier(type = Integer.class)
	public static final String VISIBLE_ROW_COUNT_KEY = "visibleRowCount";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROW_HEIGHT_KEY = "rowHeight";
	@PropertyIdentifier(type = boolean.class)
	public static final String CREATE_NEW_ROW_ON_CLICK_KEY = "createNewRowOnClick";
	@PropertyIdentifier(type = boolean.class)
	public static final String BOUND_TO_SELECTION_MANAGER_KEY = "boundToSelectionManager";
	@PropertyIdentifier(type = SelectionMode.class)
	public static final String SELECTION_MODE_KEY = "selectionMode";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SELECTED_KEY = "selected";
	@PropertyIdentifier(type = LayoutOrientation.class)
	public static final String LAYOUT_ORIENTATION_KEY = "layoutOrientation";
	@PropertyIdentifier(type = Color.class)
	public static final String TEXT_SELECTION_COLOR_KEY = "textSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String TEXT_NON_SELECTION_COLOR_KEY = "textNonSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_SELECTION_COLOR_KEY = "backgroundSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_SECONDARY_SELECTION_COLOR_KEY = "backgroundSecondarySelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_NON_SELECTION_COLOR_KEY = "backgroundNonSelectionColor";

	@Getter(value = VISIBLE_ROW_COUNT_KEY)
	@XMLAttribute
	public Integer getVisibleRowCount();

	@Setter(VISIBLE_ROW_COUNT_KEY)
	public void setVisibleRowCount(Integer visibleRowCount);

	@Getter(value = ROW_HEIGHT_KEY)
	@XMLAttribute
	public Integer getRowHeight();

	@Setter(ROW_HEIGHT_KEY)
	public void setRowHeight(Integer rowHeight);

	@Getter(value = CREATE_NEW_ROW_ON_CLICK_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getCreateNewRowOnClick();

	@Setter(CREATE_NEW_ROW_ON_CLICK_KEY)
	public void setCreateNewRowOnClick(boolean createNewRowOnClick);

	@Getter(value = BOUND_TO_SELECTION_MANAGER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getBoundToSelectionManager();

	@Setter(BOUND_TO_SELECTION_MANAGER_KEY)
	public void setBoundToSelectionManager(boolean boundToSelectionManager);

	@Getter(value = SELECTION_MODE_KEY)
	@XMLAttribute
	public SelectionMode getSelectionMode();

	@Setter(SELECTION_MODE_KEY)
	public void setSelectionMode(SelectionMode selectionMode);

	@Getter(value = SELECTED_KEY)
	@XMLAttribute
	public DataBinding<Object> getSelected();

	@Setter(SELECTED_KEY)
	public void setSelected(DataBinding<Object> selected);

	@Getter(value = LAYOUT_ORIENTATION_KEY)
	@XMLAttribute
	public LayoutOrientation getLayoutOrientation();

	@Setter(LAYOUT_ORIENTATION_KEY)
	public void setLayoutOrientation(LayoutOrientation layoutOrientation);

	@Getter(value = TEXT_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getTextSelectionColor();

	@Setter(TEXT_SELECTION_COLOR_KEY)
	public void setTextSelectionColor(Color textSelectionColor);

	@Getter(value = TEXT_NON_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getTextNonSelectionColor();

	@Setter(TEXT_NON_SELECTION_COLOR_KEY)
	public void setTextNonSelectionColor(Color textNonSelectionColor);

	@Getter(value = BACKGROUND_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundSelectionColor();

	@Setter(BACKGROUND_SELECTION_COLOR_KEY)
	public void setBackgroundSelectionColor(Color backgroundSelectionColor);

	@Getter(value = BACKGROUND_SECONDARY_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundSecondarySelectionColor();

	@Setter(BACKGROUND_SECONDARY_SELECTION_COLOR_KEY)
	public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor);

	@Getter(value = BACKGROUND_NON_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundNonSelectionColor();

	@Setter(BACKGROUND_NON_SELECTION_COLOR_KEY)
	public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor);

	public static abstract class FIBListImpl extends FIBMultipleValuesImpl implements FIBList {

		private static final Logger logger = Logger.getLogger(FIBList.class.getPackage().getName());

		private DataBinding<Object> selected;

		private Integer visibleRowCount = 5;
		private Integer rowHeight;
		private boolean createNewRowOnClick = false;
		private boolean boundToSelectionManager = false;

		private SelectionMode selectionMode = SelectionMode.MultipleIntervalSelection;

		private Color textSelectionColor;
		private Color textNonSelectionColor;
		private Color backgroundSelectionColor;
		private Color backgroundSecondarySelectionColor;
		private Color backgroundNonSelectionColor;

		private LayoutOrientation layoutOrientation = LayoutOrientation.vertical;

		public FIBListImpl() {
		}

		@Override
		protected FIBListType makeViewType() {
			return new FIBListType(this);
		}

		@Override
		public String getBaseName() {
			return "List";
		}

		/*@Override
		public Type getDynamicAccessType() {
			Type[] args = new Type[2];
			args[0] = new WilcardTypeImpl(Object.class);
			args[1] = getIteratorType();
			return new ParameterizedTypeImpl(FIBListWidget.class, args);
		}*/

		@Override
		public DataBinding<Object> getSelected() {
			if (selected == null) {
				selected = new DataBinding<>(this, getIteratorType(), DataBinding.BindingDefinitionType.GET_SET);
			}
			return selected;
		}

		@Override
		public void setSelected(DataBinding<Object> selected) {
			if (selected != null) {
				selected.setOwner(this);
				selected.setDeclaredType(getIteratorType());
				selected.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			}
			this.selected = selected;
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (selected != null) {
				selected.forceRevalidate();
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			if (selected != null) {
				selected.decode();
			}
		}

		@Override
		public Integer getVisibleRowCount() {
			return visibleRowCount;
		}

		@Override
		public void setVisibleRowCount(Integer visibleRowCount) {
			FIBPropertyNotification<Integer> notification = requireChange(VISIBLE_ROW_COUNT_KEY, visibleRowCount);
			if (notification != null) {
				this.visibleRowCount = visibleRowCount;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getRowHeight() {
			return rowHeight;
		}

		@Override
		public void setRowHeight(Integer rowHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(ROW_HEIGHT_KEY, rowHeight);
			if (notification != null) {
				this.rowHeight = rowHeight;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getCreateNewRowOnClick() {
			return createNewRowOnClick;
		}

		@Override
		public void setCreateNewRowOnClick(boolean createNewRowOnClick) {
			FIBPropertyNotification<Boolean> notification = requireChange(CREATE_NEW_ROW_ON_CLICK_KEY, createNewRowOnClick);
			if (notification != null) {
				this.createNewRowOnClick = createNewRowOnClick;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getBoundToSelectionManager() {
			return boundToSelectionManager;
		}

		@Override
		public void setBoundToSelectionManager(boolean boundToSelectionManager) {
			FIBPropertyNotification<Boolean> notification = requireChange(BOUND_TO_SELECTION_MANAGER_KEY, boundToSelectionManager);
			if (notification != null) {
				this.boundToSelectionManager = boundToSelectionManager;
				hasChanged(notification);
			}
		}

		@Override
		public SelectionMode getSelectionMode() {
			return selectionMode;
		}

		@Override
		public void setSelectionMode(SelectionMode selectionMode) {
			FIBPropertyNotification<SelectionMode> notification = requireChange(SELECTION_MODE_KEY, selectionMode);
			if (notification != null) {
				this.selectionMode = selectionMode;
				hasChanged(notification);
			}
		}

		@Override
		public Color getTextSelectionColor() {
			return textSelectionColor;
		}

		@Override
		public void setTextSelectionColor(Color textSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(TEXT_SELECTION_COLOR_KEY, textSelectionColor);
			if (notification != null) {
				this.textSelectionColor = textSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getTextNonSelectionColor() {
			return textNonSelectionColor;
		}

		@Override
		public void setTextNonSelectionColor(Color textNonSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(TEXT_NON_SELECTION_COLOR_KEY, textNonSelectionColor);
			if (notification != null) {
				this.textNonSelectionColor = textNonSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundSelectionColor() {
			return backgroundSelectionColor;
		}

		@Override
		public void setBackgroundSelectionColor(Color backgroundSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_SELECTION_COLOR_KEY, backgroundSelectionColor);
			if (notification != null) {
				this.backgroundSelectionColor = backgroundSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundSecondarySelectionColor() {
			return backgroundSecondarySelectionColor;
		}

		@Override
		public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_SECONDARY_SELECTION_COLOR_KEY,
					backgroundSecondarySelectionColor);
			if (notification != null) {
				this.backgroundSecondarySelectionColor = backgroundSecondarySelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundNonSelectionColor() {
			return backgroundNonSelectionColor;
		}

		@Override
		public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_NON_SELECTION_COLOR_KEY, backgroundNonSelectionColor);
			if (notification != null) {
				this.backgroundNonSelectionColor = backgroundNonSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public LayoutOrientation getLayoutOrientation() {
			return layoutOrientation;
		}

		@Override
		public void setLayoutOrientation(LayoutOrientation layoutOrientation) {
			FIBPropertyNotification<LayoutOrientation> notification = requireChange(LAYOUT_ORIENTATION_KEY, layoutOrientation);
			if (notification != null) {
				this.layoutOrientation = layoutOrientation;
				hasChanged(notification);
			}
		}

	}
}

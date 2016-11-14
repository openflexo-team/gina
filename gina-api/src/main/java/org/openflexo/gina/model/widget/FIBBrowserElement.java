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
import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction;
import org.openflexo.gina.model.widget.FIBBrowserAction.FIBCustomAction;
import org.openflexo.gina.model.widget.FIBBrowserAction.FIBRemoveAction;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.rm.BasicResourceImpl;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;

@ModelEntity
@ImplementationClass(FIBBrowserElement.FIBBrowserElementImpl.class)
@XMLElement(xmlTag = "BrowserElement")
public interface FIBBrowserElement extends FIBModelObject {

	@PropertyIdentifier(type = FIBBrowser.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = Class.class)
	public static final String DATA_CLASS_KEY = "dataClass";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ICON_KEY = "icon";
	@PropertyIdentifier(type = Resource.class)
	public static final String IMAGE_ICON_RESOURCE_KEY = "imageIconResource";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String TOOLTIP_KEY = "tooltip";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ENABLED_KEY = "enabled";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VISIBLE_KEY = "visible";
	@PropertyIdentifier(type = File.class)
	public static final String IMAGE_ICON_FILE_KEY = "imageIconFile";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_EDITABLE_KEY = "isEditable";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EDITABLE_LABEL_KEY = "editableLabel";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DYNAMIC_FONT_KEY = "dynamicFont";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SELECTED_DYNAMIC_COLOR_KEY = "selectedDynamicColor";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String NON_SELECTED_DYNAMIC_COLOR_KEY = "nonSelectedDynamicColor";
	@PropertyIdentifier(type = boolean.class)
	public static final String FILTERED_KEY = "filtered";
	@PropertyIdentifier(type = boolean.class)
	public static final String DEFAULT_VISIBLE_KEY = "defaultVisible";
	@PropertyIdentifier(type = List.class)
	public static final String CHILDREN_KEY = "children";
	@PropertyIdentifier(type = List.class)
	public static final String ACTIONS_KEY = "actions";
	@PropertyIdentifier(type = List.class)
	public static final String DRAG_OPERATIONS_KEY = "dragOperations";

	@Getter(value = OWNER_KEY /*, inverse = FIBBrowser.ELEMENTS_KEY*/)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBBrowser getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBBrowser browser);

	@Getter(DATA_CLASS_KEY)
	@XMLAttribute(xmlTag = "dataClassName")
	public Class<?> getDataClass();

	@Setter(DATA_CLASS_KEY)
	public void setDataClass(Class<?> dataClass);

	@Getter(LABEL_KEY)
	@XMLAttribute
	public DataBinding<String> getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(DataBinding<String> label);

	@Getter(ICON_KEY)
	@XMLAttribute
	public DataBinding<Icon> getIcon();

	@Setter(ICON_KEY)
	public void setIcon(DataBinding<Icon> icon);

	@Getter(TOOLTIP_KEY)
	@XMLAttribute
	public DataBinding<String> getTooltip();

	@Setter(TOOLTIP_KEY)
	public void setTooltip(DataBinding<String> tooltip);

	@Getter(ENABLED_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getEnabled();

	@Setter(ENABLED_KEY)
	public void setEnabled(DataBinding<Boolean> enabled);

	@Getter(VISIBLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getVisible();

	@Setter(VISIBLE_KEY)
	public void setVisible(DataBinding visible);

	@Getter(value = IS_EDITABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsEditable();

	@Setter(IS_EDITABLE_KEY)
	public void setIsEditable(boolean isEditable);

	@Getter(EDITABLE_LABEL_KEY)
	@XMLAttribute
	public DataBinding<String> getEditableLabel();

	@Setter(EDITABLE_LABEL_KEY)
	public void setEditableLabel(DataBinding<String> editableLabel);

	@Getter(FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(FONT_KEY)
	public void setFont(Font font);

	public boolean getHasSpecificFont();

	public void setHasSpecificFont(boolean aFlag);

	@Getter(DYNAMIC_FONT_KEY)
	@XMLAttribute
	public DataBinding<Font> getDynamicFont();

	@Setter(DYNAMIC_FONT_KEY)
	public void setDynamicFont(DataBinding<Font> dynamicFont);

	@Getter(SELECTED_DYNAMIC_COLOR_KEY)
	@XMLAttribute
	public DataBinding<Color> getSelectedDynamicColor();

	@Setter(SELECTED_DYNAMIC_COLOR_KEY)
	public void setSelectedDynamicColor(DataBinding<Color> dynamicColor);

	@Getter(NON_SELECTED_DYNAMIC_COLOR_KEY)
	@XMLAttribute
	public DataBinding<Color> getNonSelectedDynamicColor();

	@Setter(NON_SELECTED_DYNAMIC_COLOR_KEY)
	public void setNonSelectedDynamicColor(DataBinding<Color> dynamicColor);

	@Getter(value = FILTERED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getFiltered();

	@Setter(FILTERED_KEY)
	public void setFiltered(boolean filtered);

	@Getter(value = DEFAULT_VISIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDefaultVisible();

	@Setter(DEFAULT_VISIBLE_KEY)
	public void setDefaultVisible(boolean defaultVisible);

	@Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserElementChildren.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBBrowserElementChildren> getChildren();

	@Setter(CHILDREN_KEY)
	public void setChildren(List<FIBBrowserElementChildren> children);

	@Adder(CHILDREN_KEY)
	public void addToChildren(FIBBrowserElementChildren aChildren);

	@Remover(CHILDREN_KEY)
	public void removeFromChildren(FIBBrowserElementChildren aChildren);

	@Getter(value = ACTIONS_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserAction.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBBrowserAction> getActions();

	@Setter(ACTIONS_KEY)
	public void setActions(List<FIBBrowserAction> actions);

	@Adder(ACTIONS_KEY)
	public void addToActions(FIBBrowserAction anAction);

	@Remover(ACTIONS_KEY)
	public void removeFromActions(FIBBrowserAction anAction);

	@Getter(value = DRAG_OPERATIONS_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserDragOperation.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBBrowserDragOperation> getDragOperations();

	@Setter(DRAG_OPERATIONS_KEY)
	public void setDragOperations(List<FIBBrowserDragOperation> actions);

	@Adder(DRAG_OPERATIONS_KEY)
	public void addToDragOperations(FIBBrowserDragOperation anAction);

	@Remover(DRAG_OPERATIONS_KEY)
	public void removeFromDragOperations(FIBBrowserDragOperation anAction);

	public void finalizeBrowserDeserialization();

	public void updateBindingModel();

	public void notifiedBindingModelRecreated();

	public Bindable getIteratorBindable();

	public ImageIcon getImageIcon();

	public BindingModel getActionBindingModel();

	public Font retrieveValidFont();

	@Deprecated
	public FIBAddAction createAddAction();

	@Deprecated
	public FIBRemoveAction createRemoveAction();

	@Deprecated
	public FIBCustomAction createCustomAction();

	@Deprecated
	public FIBBrowserAction deleteAction(FIBBrowserAction actionToDelete);

	public void moveToTop(FIBBrowserElementChildren e);

	public void moveUp(FIBBrowserElementChildren e);

	public void moveDown(FIBBrowserElementChildren e);

	public void moveToBottom(FIBBrowserElementChildren e);

	@Getter(value = IMAGE_ICON_RESOURCE_KEY, isStringConvertable = true)
	@XMLAttribute
	public Resource getImageIconResource();

	@Setter(IMAGE_ICON_RESOURCE_KEY)
	public void setImageIconResource(Resource imageIconResource);

	public File getImageIconFile();

	public void setImageIconFile(File file) throws MalformedURLException, LocatorNotFoundException;

	@Deprecated
	public FIBBrowserElementChildren createChildren();

	@Deprecated
	public FIBBrowserElementChildren deleteChildren(FIBBrowserElementChildren elementToDelete);

	public void searchLocalized(LocalizationEntryRetriever retriever);

	public void revalidateBindings();

	public static abstract class FIBBrowserElementImpl extends FIBModelObjectImpl implements FIBBrowserElement {

		private static final Logger logger = Logger.getLogger(FIBBrowserElement.class.getPackage().getName());

		@Deprecated
		public static BindingDefinition LABEL = new BindingDefinition("label", String.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition ENABLED = new BindingDefinition("enabled", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition EDITABLE_LABEL = new BindingDefinition("editableLabel", String.class,
				DataBinding.BindingDefinitionType.GET_SET, false);
		@Deprecated
		public static BindingDefinition DYNAMIC_FONT = new BindingDefinition("dynamicFont", Font.class,
				DataBinding.BindingDefinitionType.GET, false);

		// private Class dataClass;

		private DataBinding<String> label;
		private DataBinding<Icon> icon;
		private DataBinding<String> tooltip;
		private DataBinding<Boolean> enabled;
		private DataBinding<Boolean> visible;

		private Resource imageIconResource;
		private ImageIcon imageIcon;
		private boolean isEditable = false;
		private DataBinding<String> editableLabel;
		private DataBinding<Font> dynamicFont;
		private DataBinding<Color> selectedDynamicColor;
		private DataBinding<Color> nonSelectedDynamicColor;

		private boolean filtered = false;
		private boolean defaultVisible = true;

		private Font font;

		// private List<FIBBrowserAction> actions;
		// private List<FIBBrowserElementChildren> children;

		private BindingModel actionBindingModel;

		private final FIBBrowserElementIteratorBindable iteratorBindable;

		public FIBBrowserElementImpl() {
			iteratorBindable = new FIBBrowserElementIteratorBindable();
			// children = new Vector<FIBBrowserElementChildren>();
			// actions = new Vector<FIBBrowserAction>();
		}

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		@Override
		public void setOwner(FIBBrowser browser) {
			// BindingModel oldBindingModel = getBindingModel();
			performSuperSetter(OWNER_KEY, browser);
			iteratorBindable.updateBindingModel();
			for (FIBBrowserElementChildren e : getChildren()) {
				e.updateBindingModel();
			}
			// Update binding model for actions associated with this fib browser elememt
			for (FIBBrowserAction action : getActions()) {
				action.updateBindingModel();
			}
			// Update binding model for drag operations associated with this fib browser elememt
			for (FIBBrowserDragOperation dragOperation : getDragOperations()) {
				dragOperation.updateBindingModel();
			}
		}

		@Override
		public DataBinding<String> getLabel() {
			if (label == null) {
				label = new DataBinding<String>(iteratorBindable, String.class, DataBinding.BindingDefinitionType.GET);
				label.setBindingName("label");
			}
			return label;
		}

		@Override
		public void setLabel(DataBinding<String> label) {
			FIBPropertyNotification<DataBinding<String>> notification = requireChange(LABEL_KEY, label);
			if (notification != null) {
				if (label != null) {
					label.setOwner(iteratorBindable);
					label.setDeclaredType(String.class);
					label.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					label.setBindingName("label");
				}
				this.label = label;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<Icon> getIcon() {
			if (icon == null) {
				icon = new DataBinding<Icon>(iteratorBindable, Icon.class, DataBinding.BindingDefinitionType.GET);
				icon.setBindingName("icon");
			}
			return icon;
		}

		@Override
		public void setIcon(DataBinding<Icon> icon) {
			FIBPropertyNotification<DataBinding<Icon>> notification = requireChange(ICON_KEY, icon);
			if (notification != null) {
				if (icon != null) {
					icon.setOwner(iteratorBindable);
					icon.setDeclaredType(Icon.class);
					icon.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					icon.setBindingName("icon");
				}
				this.icon = icon;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<String> getTooltip() {
			if (tooltip == null) {
				tooltip = new DataBinding<String>(iteratorBindable, String.class, DataBinding.BindingDefinitionType.GET);
				tooltip.setBindingName("tooltip");
			}
			return tooltip;
		}

		@Override
		public void setTooltip(DataBinding<String> tooltip) {
			if (tooltip != null) {
				tooltip.setOwner(iteratorBindable);
				tooltip.setDeclaredType(String.class);
				tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				tooltip.setBindingName("tooltip");
			}
			this.tooltip = tooltip;
		}

		@Override
		public DataBinding<Boolean> getEnabled() {
			if (enabled == null) {
				enabled = new DataBinding<Boolean>(iteratorBindable, Boolean.class, DataBinding.BindingDefinitionType.GET);
				enabled.setBindingName("enabled");
			}
			return enabled;
		}

		@Override
		public void setEnabled(DataBinding<Boolean> enabled) {
			if (enabled != null) {
				enabled.setOwner(iteratorBindable);
				enabled.setDeclaredType(Boolean.class);
				enabled.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				enabled.setBindingName("enabled");
			}
			this.enabled = enabled;
		}

		@Override
		public DataBinding<Boolean> getVisible() {
			if (visible == null) {
				visible = new DataBinding<Boolean>(iteratorBindable, Boolean.class, DataBinding.BindingDefinitionType.GET);
				visible.setBindingName("visible");
			}
			return visible;
		}

		@Override
		public void setVisible(DataBinding visible) {
			if (visible != null) {
				visible.setOwner(iteratorBindable);
				visible.setDeclaredType(Boolean.class);
				visible.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				visible.setBindingName("visible");
			}
			this.visible = visible;
		}

		@Override
		public boolean getIsEditable() {
			return isEditable;
		}

		@Override
		public void setIsEditable(boolean isEditable) {
			FIBPropertyNotification<Boolean> notification = requireChange(IS_EDITABLE_KEY, isEditable);
			if (notification != null) {
				this.isEditable = isEditable;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<String> getEditableLabel() {
			if (editableLabel == null) {
				editableLabel = new DataBinding<String>(iteratorBindable, String.class, DataBinding.BindingDefinitionType.GET_SET);
			}
			return editableLabel;
		}

		@Override
		public void setEditableLabel(DataBinding<String> editableLabel) {
			FIBPropertyNotification<DataBinding<String>> notification = requireChange(EDITABLE_LABEL_KEY, editableLabel);
			if (notification != null) {
				if (editableLabel != null) {
					editableLabel.setOwner(iteratorBindable);
					editableLabel.setDeclaredType(String.class);
					editableLabel.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
				}
				this.editableLabel = editableLabel;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getFiltered() {
			return filtered;
		}

		@Override
		public void setFiltered(boolean filtered) {
			FIBPropertyNotification<Boolean> notification = requireChange(FILTERED_KEY, filtered);
			if (notification != null) {
				this.filtered = filtered;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getDefaultVisible() {
			return defaultVisible;
		}

		@Override
		public void setDefaultVisible(boolean defaultVisible) {
			FIBPropertyNotification<Boolean> notification = requireChange(DEFAULT_VISIBLE_KEY, defaultVisible);
			if (notification != null) {
				this.defaultVisible = defaultVisible;
				hasChanged(notification);
			}
		}

		@Override
		public void revalidateBindings() {
			if (label != null) {
				label.revalidate();
			}
			if (icon != null) {
				icon.revalidate();
			}
			if (tooltip != null) {
				tooltip.revalidate();
			}
			if (enabled != null) {
				enabled.revalidate();
			}
			if (visible != null) {
				visible.revalidate();
			}
			if (editableLabel != null) {
				editableLabel.revalidate();
			}
			for (FIBBrowserElementChildren c : getChildren()) {
				c.revalidateBindings();
			}
		}

		@Override
		public void finalizeBrowserDeserialization() {
			logger.fine("finalizeBrowserDeserialization() for FIBBrowserElement " + getDataClass());
			if (label != null) {
				label.decode();
			}
			if (icon != null) {
				icon.decode();
			}
			if (tooltip != null) {
				tooltip.decode();
			}
			if (enabled != null) {
				enabled.decode();
			}
			if (visible != null) {
				visible.decode();
			}
			if (editableLabel != null) {
				editableLabel.decode();
			}
			for (FIBBrowserElementChildren c : getChildren()) {
				c.finalizeBrowserDeserialization();
			}
			for (FIBBrowserDragOperation op : getDragOperations()) {
				op.finalizeBrowserDeserialization();
			}
		}

		@Override
		public BindingModel getBindingModel() {
			if (getOwner() != null) {
				return getOwner().getBindingModel();
			}
			return null;
		}

		@Override
		public void updateBindingModel() {
			actionBindingModel = null;
		}

		@Override
		public BindingModel getActionBindingModel() {
			if (actionBindingModel == null) {
				createActionBindingModel();
			}
			return actionBindingModel;
		}

		private void createActionBindingModel() {
			actionBindingModel = new BindingModel(getBindingModel());

			BindingVariable selectedVariable = new BindingVariable("selected", getDataClass());
			selectedVariable.setCacheable(false);
			actionBindingModel.addToBindingVariables(selectedVariable);
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);

			// logger.info("******** Table: "+getName()+" Add BindingVariable: iterator type="+getIteratorClass());
		}

		@Override
		public void notifiedBindingModelRecreated() {
			createActionBindingModel();
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
			}
			else {
				setFont(null);
			}
		}

		@Override
		public DataBinding<Font> getDynamicFont() {
			if (dynamicFont == null) {
				dynamicFont = new DataBinding<Font>(iteratorBindable, Font.class, DataBinding.BindingDefinitionType.GET);
			}
			return dynamicFont;
		}

		@Override
		public void setDynamicFont(DataBinding<Font> dynamicFont) {
			if (dynamicFont != null) {
				dynamicFont.setOwner(iteratorBindable);
				dynamicFont.setDeclaredType(Font.class);
				dynamicFont.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.dynamicFont = dynamicFont;
		}

		@Override
		public DataBinding<Color> getSelectedDynamicColor() {
			if (selectedDynamicColor == null) {
				selectedDynamicColor = new DataBinding<Color>(iteratorBindable, Color.class, DataBinding.BindingDefinitionType.GET);
			}
			return selectedDynamicColor;
		}

		@Override
		public void setSelectedDynamicColor(DataBinding<Color> selectedDynamicColor) {
			if (selectedDynamicColor != null) {
				selectedDynamicColor.setOwner(iteratorBindable);
				selectedDynamicColor.setDeclaredType(Color.class);
				selectedDynamicColor.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.selectedDynamicColor = selectedDynamicColor;
		}

		@Override
		public DataBinding<Color> getNonSelectedDynamicColor() {
			if (nonSelectedDynamicColor == null) {
				nonSelectedDynamicColor = new DataBinding<Color>(iteratorBindable, Color.class, DataBinding.BindingDefinitionType.GET);
			}
			return nonSelectedDynamicColor;
		}

		@Override
		public void setNonSelectedDynamicColor(DataBinding<Color> nonSelectedDynamicColor) {
			if (nonSelectedDynamicColor != null) {
				nonSelectedDynamicColor.setOwner(iteratorBindable);
				nonSelectedDynamicColor.setDeclaredType(Color.class);
				nonSelectedDynamicColor.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.nonSelectedDynamicColor = nonSelectedDynamicColor;
		}

		@Override
		public Resource getImageIconResource() {
			return imageIconResource;
		}

		@Override
		public void setImageIconResource(Resource imageIconResource) {
			FIBPropertyNotification<Resource> notification = requireChange(IMAGE_ICON_RESOURCE_KEY, imageIconResource);
			if (notification != null) {
				this.imageIconResource = imageIconResource;
				if (imageIconResource instanceof FileResourceImpl) {
					this.imageIcon = new ImageIcon(((FileResourceImpl) imageIconResource).getFile().getAbsolutePath());
				}
				else if (imageIconResource instanceof BasicResourceImpl) {
					this.imageIcon = new ImageIcon(((BasicResourceImpl) imageIconResource).getURL());
				}
				hasChanged(notification);
			}
		}

		// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
		@Override
		public File getImageIconFile() {
			if (imageIconResource instanceof FileResourceImpl) {
				return ((FileResourceImpl) imageIconResource).getFile();
			}
			else
				return null;
		}

		@Override
		public void setImageIconFile(File file) throws MalformedURLException, LocatorNotFoundException {

			this.setImageIconResource(new FileResourceImpl(file));
		}

		@Override
		public ImageIcon getImageIcon() {
			return imageIcon;
		}

		@Override
		public FIBBrowserElementIteratorBindable getIteratorBindable() {
			return iteratorBindable;
		}

		public class FIBBrowserElementIteratorBindable extends DefaultBindable {
			private BindingModel iteratorBindingModel = null;

			@Override
			public BindingModel getBindingModel() {
				if (iteratorBindingModel == null) {
					createIteratorBindingModel();
				}
				return iteratorBindingModel;
			}

			private void updateBindingModel() {
				getBindingModel();
				iteratorBindingModel.setBaseBindingModel(FIBBrowserElementImpl.this.getBindingModel());
			}

			private void createIteratorBindingModel() {
				iteratorBindingModel = new BindingModel(FIBBrowserElementImpl.this.getBindingModel());
				iteratorBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
					@Override
					public Type getType() {
						return getDataClass();
					}

					@Override
					public String getVariableName() {
						return FIBBrowserElementImpl.this.getName();
					}

					@Override
					public boolean isCacheable() {
						return false;
					}
				});
			}

			public FIBComponent getComponent() {
				return FIBBrowserElementImpl.this.getComponent();
			}

			@Override
			public BindingFactory getBindingFactory() {
				if (getComponent() == null) {
					return null;
				}
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			}

		}

		@Override
		public Class<?> getDataClass() {
			Class<?> returned = (Class<?>) performSuperGetter(DATA_CLASS_KEY);
			if (returned == null && getOwner() != null) {
				return getOwner().getIteratorClass();
			}
			return returned;
		}

		@Override
		public void setDataClass(Class<?> dataClass) {
			// System.out.println("For browser element " + getName() + " set data class " + dataClass);
			performSuperSetter(DATA_CLASS_KEY, dataClass);
			/*FIBPropertyNotification<Class> notification = requireChange(DATA_CLASS_KEY, dataClass);
			if (notification != null) {
				this.dataClass = dataClass;
				hasChanged(notification);
			}*/
		}

		/*@Override
		public List<FIBBrowserAction> getActions() {
			return actions;
		}
		
		@Override
		public void setActions(List<FIBBrowserAction> actions) {
			this.actions = actions;
		}
		
		@Override
		public void addToActions(FIBBrowserAction anAction) {
			logger.fine("Add to actions " + anAction);
			anAction.setOwner(this);
			actions.add(anAction);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, actions);
		}
		
		@Override
		public void removeFromActions(FIBBrowserAction anAction) {
			anAction.setOwner(null);
			actions.remove(anAction);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, actions);
		}*/

		@Override
		@Deprecated
		public FIBAddAction createAddAction() {
			FIBAddAction newAction = getModelFactory().newInstance(FIBAddAction.class);
			newAction.setName("add_action");
			addToActions(newAction);
			return newAction;
		}

		@Override
		@Deprecated
		public FIBRemoveAction createRemoveAction() {
			FIBRemoveAction newAction = getModelFactory().newInstance(FIBRemoveAction.class);
			newAction.setName("delete_action");
			addToActions(newAction);
			return newAction;
		}

		@Override
		@Deprecated
		public FIBCustomAction createCustomAction() {
			FIBCustomAction newAction = getModelFactory().newInstance(FIBCustomAction.class);
			newAction.setName("custom_action");
			addToActions(newAction);
			return newAction;
		}

		@Override
		@Deprecated
		public FIBBrowserAction deleteAction(FIBBrowserAction actionToDelete) {
			logger.info("Called deleteAction() with " + actionToDelete);
			removeFromActions(actionToDelete);
			return actionToDelete;
		}

		/*@Override
		public List<FIBBrowserElementChildren> getChildren() {
			return children;
		}
		
		@Override
		public void setChildren(List<FIBBrowserElementChildren> children) {
			this.children = children;
		}
		
		@Override
		public void addToChildren(FIBBrowserElementChildren aChildren) {
			aChildren.setOwner(this);
			children.add(aChildren);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, children);
		}
		
		@Override
		public void removeFromChildren(FIBBrowserElementChildren aChildren) {
			aChildren.setOwner(null);
			children.remove(aChildren);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, children);
		}*/

		@Override
		@Deprecated
		public FIBBrowserElementChildren createChildren() {
			logger.info("Called createChildren()");
			FIBBrowserElementChildren newChildren = getModelFactory().newInstance(FIBBrowserElementChildren.class);
			newChildren.setName("children" + (getChildren().size() > 0 ? getChildren().size() : ""));
			addToChildren(newChildren);
			return newChildren;
		}

		@Override
		@Deprecated
		public FIBBrowserElementChildren deleteChildren(FIBBrowserElementChildren elementToDelete) {
			logger.info("Called elementToDelete() with " + elementToDelete);
			removeFromChildren(elementToDelete);
			return elementToDelete;
		}

		@Override
		public void moveToTop(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			getChildren().remove(e);
			getChildren().add(0, e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		@Override
		public void moveUp(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			int index = getChildren().indexOf(e);
			getChildren().remove(e);
			getChildren().add(index - 1, e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		@Override
		public void moveDown(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			int index = getChildren().indexOf(e);
			getChildren().remove(e);
			getChildren().add(index + 1, e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		@Override
		public void moveToBottom(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			getChildren().remove(e);
			getChildren().add(e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		@Override
		public String toString() {
			return "FIBBrowserElement(name=" + getName() + ",type=" + getDataClass() + ")";
		}

		@Override
		public Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return getChildren();
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			for (FIBBrowserAction action : getActions()) {
				action.searchLocalized(retriever);
			}
		}

		@Override
		public String getPresentationName() {
			return getName();
		}
	}

	@DefineValidationRule
	public static class LabelBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public LabelBindingMustBeValid() {
			super("'label'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getLabel();
		}

	}

	@DefineValidationRule
	public static class IconBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public IconBindingMustBeValid() {
			super("'icon'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getIcon();
		}
	}

	@DefineValidationRule
	public static class TooltipBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public TooltipBindingMustBeValid() {
			super("'tooltip'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getTooltip();
		}
	}

	@DefineValidationRule
	public static class EnabledBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public EnabledBindingMustBeValid() {
			super("'enabled'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getEnabled();
		}
	}

	@DefineValidationRule
	public static class VisibleBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public VisibleBindingMustBeValid() {
			super("'visible'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getVisible();
		}
	}

	@DefineValidationRule
	public static class EditableLabelBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public EditableLabelBindingMustBeValid() {
			super("'editable_label'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getEditableLabel();
		}
	}

	@DefineValidationRule
	public static class DynamicFontBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public DynamicFontBindingMustBeValid() {
			super("'dynamic_font'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getDynamicFont();
		}
	}

	@DefineValidationRule
	public static class DynamicSelectedColorBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public DynamicSelectedColorBindingMustBeValid() {
			super("'selected_color'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getSelectedDynamicColor();
		}
	}

	@DefineValidationRule
	public static class DynamicNonSelectedColorBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public DynamicNonSelectedColorBindingMustBeValid() {
			super("'non_selected_color'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getNonSelectedDynamicColor();
		}
	}

}

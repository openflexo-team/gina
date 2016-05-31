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
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.widget.FIBBrowserElement.FIBBrowserElementImpl;
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

@ModelEntity
@ImplementationClass(FIBBrowser.FIBBrowserImpl.class)
@XMLElement(xmlTag = "Browser")
public interface FIBBrowser extends FIBWidget {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ROOT_KEY = "root";
	@PropertyIdentifier(type = Class.class)
	public static final String ITERATOR_CLASS_KEY = "iteratorClass";
	@PropertyIdentifier(type = Integer.class)
	public static final String VISIBLE_ROW_COUNT_KEY = "visibleRowCount";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROW_HEIGHT_KEY = "rowHeight";
	@PropertyIdentifier(type = boolean.class)
	public static final String BOUND_TO_SELECTION_MANAGER_KEY = "boundToSelectionManager";
	@PropertyIdentifier(type = boolean.class)
	public static final String DEEP_EXPLORATION_KEY = "deepExploration";
	@PropertyIdentifier(type = TreeSelectionMode.class)
	public static final String SELECTION_MODE_KEY = "selectionMode";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SELECTED_KEY = "selected";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SELECTION_KEY = "selection";
	@PropertyIdentifier(type = Vector.class)
	public static final String ELEMENTS_KEY = "elements";
	@PropertyIdentifier(type = boolean.class)
	public static final String SHOW_FOOTER_KEY = "showFooter";
	@PropertyIdentifier(type = boolean.class)
	public static final String ROOT_VISIBLE_KEY = "rootVisible";
	@PropertyIdentifier(type = boolean.class)
	public static final String SHOW_ROOTS_HANDLE_KEY = "showRootsHandle";
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
	@PropertyIdentifier(type = Color.class)
	public static final String BORDER_SELECTION_COLOR_KEY = "borderSelectionColor";

	@Getter(value = ROOT_KEY)
	@XMLAttribute
	public DataBinding<Object> getRoot();

	@Setter(ROOT_KEY)
	public void setRoot(DataBinding<Object> root);

	@Getter(value = ITERATOR_CLASS_KEY)
	@XMLAttribute(xmlTag = "iteratorClassName")
	public Class getIteratorClass();

	@Setter(ITERATOR_CLASS_KEY)
	public void setIteratorClass(Class iteratorClass);

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

	@Getter(value = BOUND_TO_SELECTION_MANAGER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getBoundToSelectionManager();

	@Setter(BOUND_TO_SELECTION_MANAGER_KEY)
	public void setBoundToSelectionManager(boolean boundToSelectionManager);

	@Getter(value = DEEP_EXPLORATION_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getDeepExploration();

	@Setter(DEEP_EXPLORATION_KEY)
	public void setDeepExploration(boolean deepExploration);

	@Getter(value = SELECTION_MODE_KEY)
	@XMLAttribute(xmlTag = "selectionMode")
	public TreeSelectionMode getTreeSelectionMode();

	@Setter(SELECTION_MODE_KEY)
	public void setTreeSelectionMode(TreeSelectionMode selectionMode);

	@Getter(value = SELECTED_KEY)
	@XMLAttribute
	public DataBinding<Object> getSelected();

	@Setter(SELECTED_KEY)
	public void setSelected(DataBinding<Object> selected);

	@Getter(value = SELECTION_KEY)
	@XMLAttribute
	public DataBinding<List> getSelection();

	@Setter(SELECTION_KEY)
	public void setSelection(DataBinding<List> selection);

	@Getter(value = ELEMENTS_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserElement.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBBrowserElement> getElements();

	@Setter(ELEMENTS_KEY)
	public void setElements(List<FIBBrowserElement> elements);

	@Adder(ELEMENTS_KEY)
	public void addToElements(FIBBrowserElement aElement);

	@Remover(ELEMENTS_KEY)
	public void removeFromElements(FIBBrowserElement aElement);

	@Getter(value = SHOW_FOOTER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getShowFooter();

	@Setter(SHOW_FOOTER_KEY)
	public void setShowFooter(boolean showFooter);

	@Getter(value = ROOT_VISIBLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getRootVisible();

	@Setter(ROOT_VISIBLE_KEY)
	public void setRootVisible(boolean rootVisible);

	@Getter(value = SHOW_ROOTS_HANDLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getShowRootsHandle();

	@Setter(SHOW_ROOTS_HANDLE_KEY)
	public void setShowRootsHandle(boolean showRootsHandle);

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

	@Getter(value = BORDER_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBorderSelectionColor();

	@Setter(BORDER_SELECTION_COLOR_KEY)
	public void setBorderSelectionColor(Color borderSelectionColor);

	public FIBBrowserElement elementForClass(Class<?> aClass);

	public FIBBrowserElement createElement();

	public FIBBrowserElement deleteElement(FIBBrowserElement elementToDelete);

	public void moveToTop(FIBBrowserElement e);

	public void moveUp(FIBBrowserElement e);

	public void moveDown(FIBBrowserElement e);

	public void moveToBottom(FIBBrowserElement e);

	public static abstract class FIBBrowserImpl extends FIBWidgetImpl implements FIBBrowser {

		private static final Logger logger = Logger.getLogger(FIBBrowser.class.getPackage().getName());

		private DataBinding<Object> root;
		private DataBinding<Object> selected;
		private DataBinding<List> selection;

		private Integer visibleRowCount;
		private Integer rowHeight;
		private boolean boundToSelectionManager = false;

		private TreeSelectionMode selectionMode = TreeSelectionMode.DiscontiguousTreeSelection;

		private boolean showFooter = true;
		private boolean rootVisible = true;
		private boolean showRootsHandle = true;

		private Color textSelectionColor;
		private Color textNonSelectionColor;
		private Color backgroundSelectionColor;
		private Color backgroundSecondarySelectionColor;
		private Color backgroundNonSelectionColor;
		private Color borderSelectionColor;

		private Class iteratorClass;

		private final Hashtable<Class<?>, FIBBrowserElement> elementsForClasses;

		public FIBBrowserImpl() {
			elementsForClasses = new Hashtable<Class<?>, FIBBrowserElement>();
		}

		@Override
		protected FIBBrowserType makeViewType() {
			return new FIBBrowserType(this);
		}

		@Override
		public String getBaseName() {
			return "Browser";
		}

		@Override
		public void bindingModelMightChange(BindingModel oldBindingModel) {
			super.bindingModelMightChange(oldBindingModel);
			for (FIBBrowserElement e : getElements()) {
				((FIBBrowserElementImpl) e).bindingModelMightChange(oldBindingModel);
			}
		}

		@Override
		public DataBinding<Object> getRoot() {
			if (root == null) {
				root = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return root;
		}

		@Override
		public void setRoot(DataBinding<Object> root) {
			if (root != null) {
				root.setOwner(this);
				root.setDeclaredType(Object.class);
				root.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.root = root;
		}

		@Override
		public DataBinding<Object> getSelected() {
			if (selected == null) {
				selected = new DataBinding<Object>(this, getIteratorClass(), DataBinding.BindingDefinitionType.GET_SET);
			}
			return selected;
		}

		@Override
		public void setSelected(DataBinding<Object> selected) {
			if (selected != null) {
				selected.setOwner(this);
				selected.setDeclaredType(getIteratorClass());
				selected.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			}
			this.selected = selected;
		}

		@Override
		public DataBinding<List> getSelection() {
			if (selection == null) {
				selection = new DataBinding<List>(this, List.class, DataBinding.BindingDefinitionType.GET_SET);
			}
			return selection;
		}

		@Override
		public void setSelection(DataBinding<List> selection) {
			if (selection != null) {
				selection.setOwner(this);
				selection.setDeclaredType(List.class);
				selection.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			}
			this.selection = selection;
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			for (FIBBrowserElement element : getElements()) {
				element.revalidateBindings();
			}
			if (selected != null) {
				selected.revalidate();
			}
			if (selection != null) {
				selection.revalidate();
			}
		}

		@Override
		public void finalizeDeserialization() {
			logger.fine("finalizeDeserialization() for FIBTable " + getName());
			super.finalizeDeserialization();
			// Give a chance to the iterator to be typed
			for (FIBBrowserElement element : getElements()) {
				element.finalizeBrowserDeserialization();
			}
			if (selected != null) {
				selected.decode();
			}
			if (selection != null) {
				selection.decode();
			}
		}

		@Override
		public Class getIteratorClass() {
			if (iteratorClass == null) {
				iteratorClass = Object.class;
			}
			return iteratorClass;

		}

		@Override
		public void setIteratorClass(Class iteratorClass) {
			FIBPropertyNotification<Class> notification = requireChange(ITERATOR_CLASS_KEY, iteratorClass);
			if (notification != null) {
				this.iteratorClass = iteratorClass;
				hasChanged(notification);
			}
		}

		@Override
		public Type getDefaultDataType() {
			return getIteratorClass();
		}

		/*
		 * @Override public Type getDynamicAccessType() { Type[] args = new
		 * Type[2]; args[0] = new WilcardTypeImpl(Object.class); args[1] =
		 * getIteratorClass(); return new
		 * ParameterizedTypeImpl(FIBBrowserWidget.class, args); }
		 */

		@Override
		public boolean getManageDynamicModel() {
			return true;
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

		/*
		 * @Override public Vector<FIBBrowserElement> getElements() { return
		 * elements; }
		 */

		@Override
		public void setElements(List<FIBBrowserElement> elements) {
			performSuperSetter(ELEMENTS_KEY, elements);
			updateElementsForClasses();
		}

		@Override
		public void addToElements(FIBBrowserElement anElement) {
			performSuperAdder(ELEMENTS_KEY, anElement);
			// System.out.println("**** Adding element " + anElement.getName() +
			// " for class " + anElement.getDataClass());
			// Thread.dumpStack();
			updateElementsForClasses();
		}

		@Override
		public void removeFromElements(FIBBrowserElement anElement) {
			performSuperRemover(ELEMENTS_KEY, anElement);
			updateElementsForClasses();
		}

		@Override
		public FIBBrowserElement createElement() {
			logger.info("Called createElement()");
			FIBBrowserElement newElement = getModelFactory().newInstance(FIBBrowserElement.class);
			newElement.setName("element" + (getElements().size() > 0 ? getElements().size() : ""));
			addToElements(newElement);
			return newElement;
		}

		@Override
		public FIBBrowserElement deleteElement(FIBBrowserElement elementToDelete) {
			logger.info("Called elementToDelete() with " + elementToDelete);
			removeFromElements(elementToDelete);
			return elementToDelete;
		}

		@Override
		public void moveToTop(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			getElements().remove(e);
			getElements().add(0, e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		@Override
		public void moveUp(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			int index = getElements().indexOf(e);
			getElements().remove(e);
			getElements().add(index - 1, e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		@Override
		public void moveDown(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			int index = getElements().indexOf(e);
			getElements().remove(e);
			getElements().add(index + 1, e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		@Override
		public void moveToBottom(FIBBrowserElement e) {
			if (e == null) {
				return;
			}
			getElements().remove(e);
			getElements().add(e);
			getPropertyChangeSupport().firePropertyChange(ELEMENTS_KEY, null, getElements());
		}

		@Override
		public TreeSelectionMode getTreeSelectionMode() {
			return selectionMode;
		}

		@Override
		public void setTreeSelectionMode(TreeSelectionMode selectionMode) {
			FIBPropertyNotification<TreeSelectionMode> notification = requireChange(SELECTION_MODE_KEY, selectionMode);
			if (notification != null) {
				this.selectionMode = selectionMode;
				hasChanged(notification);
			}
		}

		protected void updateElementsForClasses() {
			elementsForClasses.clear();
			for (FIBBrowserElement e : getElements()) {
				if (e.getDataClass() instanceof Class) {
					elementsForClasses.put(e.getDataClass(), e);
				}
			}
		}

		@Override
		public FIBBrowserElement elementForClass(Class<?> aClass) {
			return TypeUtils.objectForClass(aClass, elementsForClasses);
		}

		@Override
		public boolean getShowFooter() {
			return showFooter;
		}

		@Override
		public void setShowFooter(boolean showFooter) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_FOOTER_KEY, showFooter);
			if (notification != null) {
				this.showFooter = showFooter;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getRootVisible() {
			return rootVisible;
		}

		@Override
		public void setRootVisible(boolean rootVisible) {
			FIBPropertyNotification<Boolean> notification = requireChange(ROOT_VISIBLE_KEY, rootVisible);
			if (notification != null) {
				this.rootVisible = rootVisible;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getShowRootsHandle() {
			return showRootsHandle;
		}

		@Override
		public void setShowRootsHandle(boolean showRootsHandle) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_ROOTS_HANDLE_KEY, showRootsHandle);
			if (notification != null) {
				this.showRootsHandle = showRootsHandle;
				hasChanged(notification);
			}
		}

		@Override
		public Color getTextSelectionColor() {
			if (textSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Tree.selectionForeground");
			}
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
			if (textNonSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Tree.textForeground");
			}
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
			if (backgroundSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Tree.selectionBackground");
			}
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
			if (backgroundSecondarySelectionColor == null) {
				return new Color(178, 215, 255);
			}
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
			if (backgroundNonSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Tree.textBackground");
			}
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
		public Color getBorderSelectionColor() {
			return borderSelectionColor;
		}

		@Override
		public void setBorderSelectionColor(Color borderSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BORDER_SELECTION_COLOR_KEY, borderSelectionColor);
			if (notification != null) {
				this.borderSelectionColor = borderSelectionColor;
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
			returned.add(getSelected());
			returned.add(getRoot());
			return returned;
		}

		@Override
		public Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return getElements();
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			super.searchLocalized(retriever);
			for (FIBBrowserElement element : getElements()) {
				element.searchLocalized(retriever);
			}
		}

	}

	@DefineValidationRule
	public static class RootBindingMustBeValid extends BindingMustBeValid<FIBBrowser> {
		public RootBindingMustBeValid() {
			super("'root'_binding_is_not_valid", FIBBrowser.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowser object) {
			return object.getRoot();
		}

	}

	@DefineValidationRule
	public static class SelectedBindingMustBeValid extends BindingMustBeValid<FIBBrowser> {
		public SelectedBindingMustBeValid() {
			super("'selected'_binding_is_not_valid", FIBBrowser.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowser object) {
			return object.getSelected();
		}

	}

	@DefineValidationRule
	public static class SelectionBindingMustBeValid extends BindingMustBeValid<FIBBrowser> {
		public SelectionBindingMustBeValid() {
			super("'selection'_binding_is_not_valid", FIBBrowser.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowser object) {
			return object.getSelection();
		}

	}

}

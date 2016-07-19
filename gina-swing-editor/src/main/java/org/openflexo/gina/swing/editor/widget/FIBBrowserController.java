/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.widget;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.graph.FIBDiscreteFunction;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.model.graph.FIBGraphFunction;
import org.openflexo.gina.model.graph.FIBNumericFunction;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserAction;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBButtonColumn;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckBoxColumn;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustomColumn;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBDropDownColumn;
import org.openflexo.gina.model.widget.FIBIconColumn;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBLabelColumn;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBNumberColumn;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableAction;
import org.openflexo.gina.model.widget.FIBTableAction.ActionType;
import org.openflexo.gina.model.widget.FIBTableAction.FIBAddAction;
import org.openflexo.gina.model.widget.FIBTableAction.FIBCustomAction;
import org.openflexo.gina.model.widget.FIBTableAction.FIBRemoveAction;
import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.model.widget.FIBTextFieldColumn;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.icon.IconFactory;
import org.openflexo.model.ModelEntity;
import org.openflexo.toolbox.StringUtils;

public class FIBBrowserController extends FIBController /*implements Observer*/ {

	private static final Logger logger = Logger.getLogger(FIBBrowserController.class.getPackage().getName());

	private FIBEditorController editorController;

	private String searchedLabel;

	public FIBBrowserController(FIBComponent rootComponent, FIBEditorController editorController) {
		this(rootComponent, SwingViewFactory.INSTANCE);
		setEditorController(editorController);
	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

	public void setEditorController(FIBEditorController editorController) {
		this.editorController = editorController;
		/*if (editorController != null) {
			editorController.addObserver(this);
		}*/

		getPropertyChangeSupport().firePropertyChange("editorController", null, editorController);
	}

	public FIBBrowserController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public FIBComponent getSelectedComponent() {
		if (editorController != null) {
			return editorController.getSelectedObject().getComponent();
		}
		return null;
	}

	public void setSelectedComponent(FIBComponent selectedComponent) {
		logger.info(">>>>setSelectedComponent with " + selectedComponent + " editorController=" + editorController);
		if (editorController != null) {
			if (editorController.getSelectedObject() != selectedComponent) {
				Object oldValue = editorController.getSelectedObject();
				editorController.setSelectedObject(selectedComponent);
				getPropertyChangeSupport().firePropertyChange("selectedComponent", oldValue, selectedComponent);
			}
		}
	}

	public ImageIcon iconFor(FIBModelObject element) {
		if (element == null) {
			return null;
		}
		if (element instanceof FIBComponent && ((FIBComponent) element).isRootComponent()) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		}
		else if (element instanceof FIBTabPanel) {
			return FIBEditorIconLibrary.TABS_ICON;
		}
		else if (element instanceof FIBPanel) {
			return FIBEditorIconLibrary.PANEL_ICON;
		}
		else if (element instanceof FIBSplitPanel) {
			return FIBEditorIconLibrary.SPLIT_PANEL_ICON;
		}
		else if (element instanceof FIBCheckBox) {
			return FIBEditorIconLibrary.CHECKBOX_ICON;
		}
		else if (element instanceof FIBLabel) {
			return FIBEditorIconLibrary.LABEL_ICON;
		}
		else if (element instanceof FIBTable) {
			return FIBEditorIconLibrary.TABLE_ICON;
		}
		else if (element instanceof FIBBrowser) {
			return FIBEditorIconLibrary.BROWSER_ICON;
		}
		else if (element instanceof FIBTextArea) {
			return FIBEditorIconLibrary.TEXTAREA_ICON;
		}
		else if (element instanceof FIBTextField) {
			return FIBEditorIconLibrary.TEXTFIELD_ICON;
		}
		else if (element instanceof FIBImage) {
			return FIBEditorIconLibrary.IMAGE_ICON;
		}
		else if (element instanceof FIBNumber) {
			return FIBEditorIconLibrary.NUMBER_ICON;
		}
		else if (element instanceof FIBDropDown) {
			return FIBEditorIconLibrary.DROPDOWN_ICON;
		}
		else if (element instanceof FIBRadioButtonList) {
			return FIBEditorIconLibrary.RADIOBUTTON_ICON;
		}
		else if (element instanceof FIBButton) {
			return FIBEditorIconLibrary.BUTTON_ICON;
		}
		else if (element instanceof FIBCustom) {
			return FIBEditorIconLibrary.CUSTOM_ICON;
		}
		else if (element instanceof FIBReferencedComponent) {
			return FIBEditorIconLibrary.REFERENCE_COMPONENT_ICON;
		}
		else if (element instanceof FIBGraph) {
			return FIBEditorIconLibrary.GRAPH_ICON;
		}
		else if (element instanceof FIBGraphFunction) {
			return FIBEditorIconLibrary.GRAPH_FUNCTION_ICON;
		}
		else if (element instanceof FIBTableColumn) {
			return FIBEditorIconLibrary.TABLE_COLUMN_ICON;
		}
		else if (element instanceof FIBTableAction) {
			if (((FIBTableAction) element).getActionType() == ActionType.Add) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.TABLE_ACTION_ICON, FIBEditorIconLibrary.DUPLICATE);
			}
			if (((FIBTableAction) element).getActionType() == ActionType.Delete) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.TABLE_ACTION_ICON, FIBEditorIconLibrary.DELETE);
			}
			return FIBEditorIconLibrary.TABLE_ACTION_ICON;
		}
		else if (element instanceof FIBBrowserElement) {
			return FIBEditorIconLibrary.BROWSER_ELEMENT_ICON;
		}
		else if (element instanceof FIBBrowserElementChildren) {
			return FIBEditorIconLibrary.BROWSER_ELEMENT_CHILDREN_ICON;
		}
		else if (element instanceof FIBBrowserAction) {
			if (((FIBBrowserAction) element).getActionType() == org.openflexo.gina.model.widget.FIBBrowserAction.ActionType.Add) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.BROWSER_ELEMENT_ACTION_ICON, FIBEditorIconLibrary.DUPLICATE);
			}
			if (((FIBBrowserAction) element).getActionType() == org.openflexo.gina.model.widget.FIBBrowserAction.ActionType.Delete) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.BROWSER_ELEMENT_ACTION_ICON, FIBEditorIconLibrary.DELETE);
			}
			return FIBEditorIconLibrary.BROWSER_ELEMENT_ACTION_ICON;
		}
		return null;

	}

	public String textFor(FIBComponent component) {
		if (component == null) {
			return null;
		}

		ModelEntity<?> e = component.getModelFactory().getModelEntityForInstance(component);

		if (StringUtils.isNotEmpty(component.getName())) {
			return component.getName();
		}
		else {
			return "<" + e.getImplementedInterface().getSimpleName() + ">";
		}
	}

	public void rightClick(FIBComponent component, MouseEvent event) {
		editorController.getContextualMenu().displayPopupMenu(component, ((JFIBView<?, ?>) getRootView()).getJComponent(), event);
	}

	public String getSearchedLabel() {
		return searchedLabel;
	}

	public void setSearchedLabel(String searchedLabel) {
		this.searchedLabel = searchedLabel;
	}

	public void search() {
		System.out.println("Searching " + getSearchedLabel());
	}

	public FIBBrowserElement createElement(FIBBrowser browser) {
		logger.info("Called createElement()");
		FIBBrowserElement newElement = editorController.getFactory().newInstance(FIBBrowserElement.class);
		newElement.setName("element" + (browser.getElements().size() > 0 ? browser.getElements().size() : ""));
		browser.addToElements(newElement);
		return newElement;
	}

	public FIBBrowserElement deleteElement(FIBBrowserElement elementToDelete) {
		logger.info("Called elementToDelete() with " + elementToDelete);
		if (elementToDelete.getOwner() != null) {
			elementToDelete.getOwner().removeFromElements(elementToDelete);
			elementToDelete.delete();
		}
		return elementToDelete;
	}

	public org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction createBrowserAddAction(FIBBrowserElement element) {
		org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction newAction = editorController.getFactory()
				.newInstance(org.openflexo.gina.model.widget.FIBBrowserAction.FIBAddAction.class);
		newAction.setName("add_action");
		element.addToActions(newAction);
		return newAction;
	}

	public org.openflexo.gina.model.widget.FIBBrowserAction.FIBRemoveAction createBrowserRemoveAction(FIBBrowserElement element) {
		org.openflexo.gina.model.widget.FIBBrowserAction.FIBRemoveAction newAction = editorController.getFactory()
				.newInstance(org.openflexo.gina.model.widget.FIBBrowserAction.FIBRemoveAction.class);
		newAction.setName("delete_action");
		element.addToActions(newAction);
		return newAction;
	}

	public org.openflexo.gina.model.widget.FIBBrowserAction.FIBCustomAction createBrowserCustomAction(FIBBrowserElement element) {
		org.openflexo.gina.model.widget.FIBBrowserAction.FIBCustomAction newAction = editorController.getFactory()
				.newInstance(org.openflexo.gina.model.widget.FIBBrowserAction.FIBCustomAction.class);
		newAction.setName("custom_action");
		element.addToActions(newAction);
		return newAction;
	}

	public FIBBrowserAction deleteBrowserAction(FIBBrowserAction actionToDelete) {
		logger.info("Called deleteBrowserAction() with " + actionToDelete);
		if (actionToDelete.getOwner() != null) {
			actionToDelete.getOwner().removeFromActions(actionToDelete);
			actionToDelete.delete();
		}
		return actionToDelete;
	}

	public FIBBrowserElementChildren createChildren(FIBBrowserElement element) {
		logger.info("Called createChildren()");
		FIBBrowserElementChildren newChildren = editorController.getFactory().newInstance(FIBBrowserElementChildren.class);
		newChildren.setName("children" + (element.getChildren().size() > 0 ? element.getChildren().size() : ""));
		element.addToChildren(newChildren);
		return newChildren;
	}

	public FIBBrowserElementChildren deleteChildren(FIBBrowserElementChildren childrenToDelete) {
		logger.info("Called elementToDelete() with " + childrenToDelete);
		if (childrenToDelete.getOwner() != null) {
			childrenToDelete.getOwner().removeFromChildren(childrenToDelete);
			childrenToDelete.delete();
		}
		return childrenToDelete;
	}

	public FIBLabelColumn createLabelColumn(FIBTable table) {
		FIBLabelColumn newColumn = editorController.getFactory().newInstance(FIBLabelColumn.class);
		newColumn.setName("label");
		newColumn.setTitle("label");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBTextFieldColumn createTextFieldColumn(FIBTable table) {
		FIBTextFieldColumn newColumn = editorController.getFactory().newInstance(FIBTextFieldColumn.class);
		newColumn.setName("textfield");
		newColumn.setTitle("textfield");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBCheckBoxColumn createCheckBoxColumn(FIBTable table) {
		FIBCheckBoxColumn newColumn = editorController.getFactory().newInstance(FIBCheckBoxColumn.class);
		newColumn.setName("checkbox");
		newColumn.setTitle("checkbox");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBDropDownColumn createDropDownColumn(FIBTable table) {
		FIBDropDownColumn newColumn = editorController.getFactory().newInstance(FIBDropDownColumn.class);
		newColumn.setName("dropdown");
		newColumn.setTitle("dropdown");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBNumberColumn createNumberColumn(FIBTable table) {
		FIBNumberColumn newColumn = editorController.getFactory().newInstance(FIBNumberColumn.class);
		newColumn.setName("number");
		newColumn.setTitle("number");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBIconColumn createIconColumn(FIBTable table) {
		FIBIconColumn newColumn = editorController.getFactory().newInstance(FIBIconColumn.class);
		newColumn.setName("icon");
		newColumn.setTitle("icon");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBCustomColumn createCustomColumn(FIBTable table) {
		FIBCustomColumn newColumn = editorController.getFactory().newInstance(FIBCustomColumn.class);
		newColumn.setName("custom");
		newColumn.setTitle("custom");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBButtonColumn createButtonColumn(FIBTable table) {
		FIBButtonColumn newColumn = editorController.getFactory().newInstance(FIBButtonColumn.class);
		newColumn.setName("button");
		newColumn.setTitle("button");
		table.addToColumns(newColumn);
		return newColumn;
	}

	public FIBTableColumn deleteColumn(FIBTableColumn columnToDelete) {
		logger.info("Called deleteColumn() with " + columnToDelete);
		if (columnToDelete.getOwner() != null) {
			columnToDelete.getOwner().removeFromColumns(columnToDelete);
			columnToDelete.delete();
		}
		return columnToDelete;
	}

	public FIBAddAction createAddAction(FIBTable table) {
		System.out.println("create add action");
		FIBAddAction newAction = editorController.getFactory().newInstance(FIBAddAction.class);
		newAction.setName("add_action");
		table.addToActions(newAction);
		return newAction;
	}

	public FIBRemoveAction createRemoveAction(FIBTable table) {
		FIBRemoveAction newAction = editorController.getFactory().newInstance(FIBRemoveAction.class);
		newAction.setName("delete_action");
		table.addToActions(newAction);
		return newAction;
	}

	public FIBCustomAction createCustomAction(FIBTable table) {
		FIBCustomAction newAction = editorController.getFactory().newInstance(FIBCustomAction.class);
		newAction.setName("custom_action");
		table.addToActions(newAction);
		return newAction;
	}

	public FIBTableAction deleteAction(FIBTableAction actionToDelete) {
		logger.info("Called deleteAction() with " + actionToDelete);
		if (actionToDelete.getOwner() != null) {
			actionToDelete.getOwner().removeFromActions(actionToDelete);
			actionToDelete.delete();
		}
		return actionToDelete;
	}

	public void moveToTop(FIBTableColumn c) {
		if (c == null || c.getOwner() == null) {
			return;
		}
		c.getOwner().getColumns().remove(c);
		c.getOwner().getColumns().add(0, c);
		c.getOwner().getPropertyChangeSupport().firePropertyChange(FIBTable.COLUMNS_KEY, null, c.getOwner().getColumns());
	}

	public void moveUp(FIBTableColumn c) {
		if (c == null || c.getOwner() == null) {
			return;
		}
		int index = c.getOwner().getColumns().indexOf(c);
		c.getOwner().getColumns().remove(c);
		c.getOwner().getColumns().add(index - 1, c);
		c.getOwner().getPropertyChangeSupport().firePropertyChange(FIBTable.COLUMNS_KEY, null, c.getOwner().getColumns());
	}

	public void moveDown(FIBTableColumn c) {
		if (c == null || c.getOwner() == null) {
			return;
		}
		int index = c.getOwner().getColumns().indexOf(c);
		c.getOwner().getColumns().remove(c);
		c.getOwner().getColumns().add(index + 1, c);
		c.getOwner().getPropertyChangeSupport().firePropertyChange(FIBTable.COLUMNS_KEY, null, c.getOwner().getColumns());
	}

	public void moveToBottom(FIBTableColumn c) {
		if (c == null || c.getOwner() == null) {
			return;
		}
		c.getOwner().getColumns().remove(c);
		c.getOwner().getColumns().add(c);
		c.getOwner().getPropertyChangeSupport().firePropertyChange(FIBTable.COLUMNS_KEY, null, c.getOwner().getColumns());
	}

	public FIBNumericFunction createNumericFunction(FIBGraph graph) {
		return editorController.getFactory().newFIBNumericFunction(graph);
	}

	public FIBDiscreteFunction createDiscreteFunction(FIBGraph graph) {
		return editorController.getFactory().newFIBDiscreteFunction(graph);
	}

	public FIBGraphFunction deleteFunction(FIBGraphFunction function) {
		if (function.getOwner() != null) {
			function.getOwner().removeFromFunctions(function);
			function.delete();
		}
		return function;
	}
}

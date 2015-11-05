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

package org.openflexo.fib.editor.widget;

import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBEditorIconLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.container.FIBPanel;
import org.openflexo.fib.model.container.FIBSplitPanel;
import org.openflexo.fib.model.container.FIBTabPanel;
import org.openflexo.fib.model.widget.FIBBrowser;
import org.openflexo.fib.model.widget.FIBButton;
import org.openflexo.fib.model.widget.FIBCheckBox;
import org.openflexo.fib.model.widget.FIBDropDown;
import org.openflexo.fib.model.widget.FIBLabel;
import org.openflexo.fib.model.widget.FIBNumber;
import org.openflexo.fib.model.widget.FIBRadioButtonList;
import org.openflexo.fib.model.widget.FIBReferencedComponent;
import org.openflexo.fib.model.widget.FIBTable;
import org.openflexo.fib.model.widget.FIBTextArea;
import org.openflexo.fib.model.widget.FIBTextField;
import org.openflexo.fib.view.GinaViewFactory;
import org.openflexo.model.ModelEntity;

public class FIBBrowserController extends FIBController implements Observer {

	private static final Logger logger = Logger.getLogger(FIBBrowserController.class.getPackage().getName());

	private FIBEditorController editorController;

	private String searchedLabel;

	public FIBBrowserController(FIBComponent rootComponent, FIBEditorController editorController, GinaViewFactory<?> viewFactory) {
		this(rootComponent, viewFactory);
		setEditorController(editorController);
	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

	public void setEditorController(FIBEditorController editorController) {
		this.editorController = editorController;
		if (editorController != null) {
			editorController.addObserver(this);
		}

		getPropertyChangeSupport().firePropertyChange("editorController", null, editorController);
	}

	public FIBBrowserController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public FIBComponent getSelectedComponent() {
		if (editorController != null) {
			return editorController.getSelectedObject();
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

	public ImageIcon iconFor(FIBComponent component) {
		if (component == null) {
			return null;
		}
		if (component.isRootComponent()) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		}
		else if (component instanceof FIBTabPanel) {
			return FIBEditorIconLibrary.TABS_ICON;
		}
		else if (component instanceof FIBPanel) {
			return FIBEditorIconLibrary.PANEL_ICON;
		}
		else if (component instanceof FIBSplitPanel) {
			return FIBEditorIconLibrary.SPLIT_PANEL_ICON;
		}
		else if (component instanceof FIBCheckBox) {
			return FIBEditorIconLibrary.CHECKBOX_ICON;
		}
		else if (component instanceof FIBLabel) {
			return FIBEditorIconLibrary.LABEL_ICON;
		}
		else if (component instanceof FIBTable) {
			return FIBEditorIconLibrary.TABLE_ICON;
		}
		else if (component instanceof FIBBrowser) {
			return FIBEditorIconLibrary.TREE_ICON;
		}
		else if (component instanceof FIBTextArea) {
			return FIBEditorIconLibrary.TEXTAREA_ICON;
		}
		else if (component instanceof FIBTextField) {
			return FIBEditorIconLibrary.TEXTFIELD_ICON;
		}
		else if (component instanceof FIBNumber) {
			return FIBEditorIconLibrary.NUMBER_ICON;
		}
		else if (component instanceof FIBDropDown) {
			return FIBEditorIconLibrary.DROPDOWN_ICON;
		}
		else if (component instanceof FIBRadioButtonList) {
			return FIBEditorIconLibrary.RADIOBUTTON_ICON;
		}
		else if (component instanceof FIBButton) {
			return FIBEditorIconLibrary.BUTTON_ICON;
		}
		else if (component instanceof FIBReferencedComponent) {
			return FIBEditorIconLibrary.REFERENCE_COMPONENT_ICON;
		}
		return null;

	}

	public String textFor(FIBComponent component) {
		if (component == null) {
			return null;
		}

		ModelEntity<?> e = component.getFactory().getModelEntityForInstance(component);

		if (component.getName() != null) {
			return component.getName() + " (" + e.getImplementedInterface().getSimpleName() + ")";
		}
		else if (component.getIdentifier() != null) {
			return component.getIdentifier() + " (" + e.getImplementedInterface().getSimpleName() + ")";
		}
		else {
			return "<" + e.getImplementedInterface().getSimpleName() + ">";
		}
	}

	public void rightClick(FIBComponent component, MouseEvent event) {
		editorController.getContextualMenu().displayPopupMenu(component, getRootView().getJComponent(), event);
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

}

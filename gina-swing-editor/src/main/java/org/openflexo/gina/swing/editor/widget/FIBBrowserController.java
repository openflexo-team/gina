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
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
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
			return FIBEditorIconLibrary.TREE_ICON;
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

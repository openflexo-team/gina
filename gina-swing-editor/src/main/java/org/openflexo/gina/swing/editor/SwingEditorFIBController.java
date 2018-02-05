/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.connie.annotations.NotificationUnsafe;
import org.openflexo.gina.FIBFolder;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBValidationReport;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.model.graph.FIBGraphFunction;
import org.openflexo.gina.model.operator.FIBConditional;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserAction;
import org.openflexo.gina.model.widget.FIBBrowserDragOperation;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
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
import org.openflexo.gina.model.widget.FIBTableAction;
import org.openflexo.gina.model.widget.FIBTableAction.ActionType;
import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.gina.swing.utils.FIBUtilsIconLibrary;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.InformationIssue;
import org.openflexo.model.validation.ValidationError;
import org.openflexo.model.validation.ValidationModel;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.model.validation.ValidationWarning;
import org.openflexo.rm.Resource;

/**
 * A {@link FIBController} used in GINA SwingEditor, and addressing a data object of type T
 * 
 * @author sylvain
 *
 * @param <T>
 *            type of data object beeing managed by this controller
 */
public class SwingEditorFIBController<T> extends FIBController implements PropertyChangeListener {

	static final Logger LOGGER = Logger.getLogger(SwingEditorFIBController.class.getPackage().getName());

	private final Map<Object, ImageIcon> cachedIcons = new HashMap<>();
	private final List<ValidationReport> observedReports = new ArrayList<>();
	private FIBModelObject selectedObject;

	public SwingEditorFIBController(FIBComponent rootComponent) {
		super(rootComponent, SwingViewFactory.INSTANCE);
		// Default parent localizer is the main localizer
		setParentLocalizer(FIBEditor.EDITOR_LOCALIZATION);
	}

	protected ImageIcon retrieveIconForObject(Object object) {

		if (object instanceof FIBModelObject) {
			FIBValidationReport report = getValidationReport((FIBModelObject) object);
			if (report != null) {
				if (!observedReports.contains(report)) {
					report.getPropertyChangeSupport().addPropertyChangeListener(this);
					// System.out.println("Observing " + report);
					observedReports.add(report);
				}
			}
		}

		if (object instanceof ValidationError) {
			if (((ValidationError<?, ?>) object).isFixable()) {
				return FIBUtilsIconLibrary.FIXABLE_ERROR_ICON;
			}
			else {
				return FIBUtilsIconLibrary.UNFIXABLE_ERROR_ICON;
			}
		}
		else if (object instanceof ValidationWarning) {
			if (((ValidationWarning<?, ?>) object).isFixable()) {
				return FIBUtilsIconLibrary.FIXABLE_WARNING_ICON;
			}
			else {
				return FIBUtilsIconLibrary.UNFIXABLE_WARNING_ICON;
			}
		}
		else if (object instanceof InformationIssue) {
			return FIBUtilsIconLibrary.INFO_ISSUE_ICON;
		}
		else if (object instanceof FixProposal) {
			return FIBUtilsIconLibrary.FIX_PROPOSAL_ICON;
		}

		if (object instanceof FIBFolder) {
			return FIBIconLibrary.FOLDER_ICON;
		}
		else if (object instanceof FIBLibrary) {
			return FIBIconLibrary.FOLDER_ICON;
		}
		else if (object instanceof Resource) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		}

		if (object instanceof FIBIteration) {
			return FIBEditorIconLibrary.ITERATION_ICON;
		}
		else if (object instanceof FIBConditional) {
			return FIBEditorIconLibrary.CONDITIONAL_ICON;
		}
		else if (object instanceof FIBComponent && ((FIBComponent) object).isRootComponent()) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		}
		else if (object instanceof FIBTabPanel) {
			return FIBEditorIconLibrary.TABS_ICON;
		}
		else if (object instanceof FIBPanel) {
			return FIBEditorIconLibrary.PANEL_ICON;
		}
		else if (object instanceof FIBSplitPanel) {
			return FIBEditorIconLibrary.SPLIT_PANEL_ICON;
		}
		else if (object instanceof FIBCheckBox) {
			return FIBEditorIconLibrary.CHECKBOX_ICON;
		}
		else if (object instanceof FIBLabel) {
			return FIBEditorIconLibrary.LABEL_ICON;
		}
		else if (object instanceof FIBTable) {
			return FIBEditorIconLibrary.TABLE_ICON;
		}
		else if (object instanceof FIBBrowser) {
			return FIBEditorIconLibrary.BROWSER_ICON;
		}
		else if (object instanceof FIBTextArea) {
			return FIBEditorIconLibrary.TEXTAREA_ICON;
		}
		else if (object instanceof FIBTextField) {
			return FIBEditorIconLibrary.TEXTFIELD_ICON;
		}
		else if (object instanceof FIBImage) {
			return FIBEditorIconLibrary.IMAGE_ICON;
		}
		else if (object instanceof FIBNumber) {
			return FIBEditorIconLibrary.NUMBER_ICON;
		}
		else if (object instanceof FIBDropDown) {
			return FIBEditorIconLibrary.DROPDOWN_ICON;
		}
		else if (object instanceof FIBRadioButtonList) {
			return FIBEditorIconLibrary.RADIOBUTTON_ICON;
		}
		else if (object instanceof FIBButton) {
			return FIBEditorIconLibrary.BUTTON_ICON;
		}
		else if (object instanceof FIBCustom) {
			return FIBEditorIconLibrary.CUSTOM_ICON;
		}
		else if (object instanceof FIBReferencedComponent) {
			return FIBEditorIconLibrary.REFERENCE_COMPONENT_ICON;
		}
		else if (object instanceof FIBGraph) {
			return FIBEditorIconLibrary.GRAPH_ICON;
		}
		else if (object instanceof FIBGraphFunction) {
			return FIBEditorIconLibrary.GRAPH_FUNCTION_ICON;
		}
		else if (object instanceof FIBTableColumn) {
			return FIBEditorIconLibrary.TABLE_COLUMN_ICON;
		}
		else if (object instanceof FIBTableAction) {
			if (((FIBTableAction) object).getActionType() == ActionType.Add) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.TABLE_ACTION_ICON, FIBEditorIconLibrary.DUPLICATE);
			}
			if (((FIBTableAction) object).getActionType() == ActionType.Delete) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.TABLE_ACTION_ICON, FIBEditorIconLibrary.DELETE);
			}
			return FIBEditorIconLibrary.TABLE_ACTION_ICON;
		}
		else if (object instanceof FIBBrowserElement) {
			return FIBEditorIconLibrary.BROWSER_ELEMENT_ICON;
		}
		else if (object instanceof FIBBrowserElementChildren) {
			return FIBEditorIconLibrary.BROWSER_ELEMENT_CHILDREN_ICON;
		}
		else if (object instanceof FIBBrowserAction) {
			if (((FIBBrowserAction) object).getActionType() == org.openflexo.gina.model.widget.FIBBrowserAction.ActionType.Add) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.BROWSER_ELEMENT_ACTION_ICON, FIBEditorIconLibrary.DUPLICATE);
			}
			if (((FIBBrowserAction) object).getActionType() == org.openflexo.gina.model.widget.FIBBrowserAction.ActionType.Delete) {
				return IconFactory.getImageIcon(FIBEditorIconLibrary.BROWSER_ELEMENT_ACTION_ICON, FIBEditorIconLibrary.DELETE);
			}
			return FIBEditorIconLibrary.BROWSER_ELEMENT_ACTION_ICON;
		}
		else if (object instanceof FIBBrowserDragOperation) {
			return FIBEditorIconLibrary.DRAG_OPERATION_ICON;
		}

		return null;
	}

	@NotificationUnsafe
	public final ImageIcon iconForObject(Object object) {

		ImageIcon returned = cachedIcons.get(object);
		if (returned == null) {
			returned = retrieveIconForObject(object);
			if (object instanceof FIBModelObject && hasValidationReport((FIBModelObject) object)) {
				if (hasErrors((FIBModelObject) object)) {
					returned = IconFactory.getImageIcon(returned, FIBUtilsIconLibrary.ERROR);
				}
				else if (hasWarnings((FIBModelObject) object)) {
					returned = IconFactory.getImageIcon(returned, FIBUtilsIconLibrary.WARNING);
				}
				cachedIcons.put(object, returned);
			}
		}
		return returned;
	}

	protected void clearCachedIcons() {
		cachedIcons.clear();
	}

	@Override
	public T getDataObject() {
		return (T) super.getDataObject();
	}

	public FIBModelObject getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(FIBModelObject selectedObject) {
		if ((selectedObject == null && this.selectedObject != null)
				|| (selectedObject != null && !selectedObject.equals(this.selectedObject))) {
			FIBModelObject oldValue = this.selectedObject;
			this.selectedObject = selectedObject;
			getPropertyChangeSupport().firePropertyChange("selectedObject", oldValue, selectedObject);
		}
	}

	public boolean hasValidationReport(FIBModelObject object) {
		FIBValidationReport validationReport = getValidationReport(object);
		return (validationReport != null);
	}

	public boolean hasErrors(FIBModelObject object) {
		FIBValidationReport validationReport = getValidationReport(object);
		if (validationReport != null) {
			return validationReport.hasErrors(object);
		}
		return false;
	}

	public boolean hasWarnings(FIBModelObject object) {
		FIBValidationReport validationReport = getValidationReport(object);
		if (validationReport != null) {
			return validationReport.hasWarnings(object);
		}
		return false;
	}

	public FIBValidationReport getValidationReport(FIBModelObject object) {
		return null;
	}

	public ValidationModel getValidationModel(FIBModelObject object) {
		if (object != null) {
			return object.getComponent().getModelFactory().getValidationModel();
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof ValidationReport) {
			clearCachedIcons();
		}
	}

}

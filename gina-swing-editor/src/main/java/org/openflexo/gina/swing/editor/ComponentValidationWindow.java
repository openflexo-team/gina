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

package org.openflexo.gina.swing.editor;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.utils.validation.ValidationPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.validation.ValidationIssue;

public class ComponentValidationWindow extends JDialog {

	private final ValidationPanel validationPanel;
	private final FIBEditorController editorController;

	public ComponentValidationWindow(JFrame frame, FIBEditorController editorController, FIBLibrary fibLibrary) {
		super(frame, FlexoLocalization.localizedForKey(FIBEditor.LOCALIZATION, "component_validation"), false);
		this.editorController = editorController;
		validationPanel = new ValidationPanel(null, fibLibrary, FIBEditor.LOCALIZATION) {
			@Override
			protected void performSelect(ValidationIssue<?, ?> validationIssue) {
				ComponentValidationWindow.this.performSelect(validationIssue);
			}
		};
		getContentPane().add(validationPanel);
		pack();
	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

	public EditedFIBComponent getEditedComponent() {
		return getEditorController().getEditedComponent();
	}

	protected void performSelect(ValidationIssue<?, ?> validationIssue) {
		if (validationIssue != null && validationIssue.getValidable() instanceof FIBModelObject) {
			if (editorController != null) {
				editorController.setSelectedObject(((FIBModelObject) validationIssue.getValidable()).getComponent());
			}
		}
	}

	public void validateAndDisplayReportForComponent(FIBComponent component) throws InterruptedException {
		validationPanel.validate(component.getModelFactory().getValidationModel(), component);
		setVisible(true);
	}
}

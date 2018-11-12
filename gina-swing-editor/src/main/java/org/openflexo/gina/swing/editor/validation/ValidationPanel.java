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

package org.openflexo.gina.swing.editor.validation;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.utils.FIBJPanel;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.pamela.validation.Validable;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationModel;
import org.openflexo.pamela.validation.ValidationReport;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

@SuppressWarnings("serial")
public abstract class ValidationPanel extends FIBJPanel<FIBComponent> {

	public static Resource VALIDATION_PANEL_FIB = ResourceLocator.locateResource("Fib/ValidationPanel.fib");

	public ValidationPanel(FIBEditorController editorController, FIBLibrary fibLibrary, LocalizedDelegate parentLocalizer) {
		super(fibLibrary.retrieveFIBComponent(VALIDATION_PANEL_FIB, true),
				editorController != null ? editorController.getFIBComponent() : null, parentLocalizer);
		getController().setEditorController(editorController);
	}

	@Override
	protected ValidationFIBController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return new ValidationFIBController(fibComponent, SwingViewFactory.INSTANCE) {
			@Override
			protected void performSelect(ValidationIssue<?, ?> validationIssue) {
				ValidationPanel.this.performSelect(validationIssue);
			}
		};
	}

	public void setEditorController(FIBEditorController editorController) {
		getController().setEditorController(editorController);
		setEditedObject(editorController.getFIBComponent());
	}

	@Override
	public ValidationFIBController getController() {
		return (ValidationFIBController) super.getController();
	}

	protected abstract void performSelect(ValidationIssue<?, ?> validationIssue);

	protected void performValidation(ValidationModel validationModel, Validable objectToValidate) throws InterruptedException {
		// startValidation(validationModel);
		ValidationReport report = validationModel.validate(objectToValidate);
		// stopValidation(validationModel);
		getController().setDataObject(report);
	}

	public void validate(ValidationModel validationModel, Validable objectToValidate) throws InterruptedException {
		performValidation(validationModel, objectToValidate);
	}

	@Override
	public Class<FIBComponent> getRepresentedType() {
		return FIBComponent.class;
	}

	@Override
	public void delete() {
		// TODO
	}

	/*public void startValidation(ValidationModel validationModel) {
	}
	
	public void stopValidation(ValidationModel validationModel) {
	}*/
}

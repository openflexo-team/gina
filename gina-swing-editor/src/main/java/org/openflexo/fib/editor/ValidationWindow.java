package org.openflexo.fib.editor;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.validation.ValidationPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.validation.ValidationIssue;

public class ValidationWindow extends JDialog {

	private final ValidationPanel validationPanel;
	private final FIBEditorController editorController;

	public ValidationWindow(JFrame frame, FIBEditorController editorController) {
		super(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_validation"), false);
		this.editorController = editorController;
		validationPanel = new ValidationPanel(null, FIBAbstractEditor.LOCALIZATION) {
			@Override
			protected void performSelect(ValidationIssue<?, ?> validationIssue) {
				ValidationWindow.this.performSelect(validationIssue);
			}
		};
		getContentPane().add(validationPanel);
		pack();
	}

	protected void performSelect(ValidationIssue<?, ?> validationIssue) {
		if (validationIssue != null && validationIssue.getObject() instanceof FIBComponent) {
			if (editorController != null) {
				editorController.setSelectedObject((FIBComponent) validationIssue.getObject());
			}
		}
	}

	public void validateAndDisplayReportForComponent(FIBComponent component) {
		validationPanel.validate(component.getFactory().getValidationModel(), component);
		setVisible(true);
	}
}
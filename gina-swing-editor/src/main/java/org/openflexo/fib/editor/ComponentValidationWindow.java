package org.openflexo.fib.editor;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.swing.validation.ValidationPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.validation.ValidationIssue;

public class ComponentValidationWindow extends JDialog {

	private final ValidationPanel validationPanel;
	private final FIBEditorController editorController;

	public ComponentValidationWindow(JFrame frame, FIBEditorController editorController) {
		super(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_validation"), false);
		this.editorController = editorController;
		validationPanel = new ValidationPanel(null, FIBAbstractEditor.LOCALIZATION) {
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

	public FIBComponent getFIBComponent() {
		return getEditorController().getFIBComponent();
	}

	protected void performSelect(ValidationIssue<?, ?> validationIssue) {
		if (validationIssue != null && validationIssue.getValidable() instanceof FIBModelObject) {
			if (editorController != null) {
				editorController.setSelectedObject(((FIBModelObject) validationIssue.getValidable()).getComponent());
			}
		}
	}

	public void validateAndDisplayReportForComponent(FIBComponent component) {
		validationPanel.validate(component.getFactory().getValidationModel(), component);
		setVisible(true);
	}
}
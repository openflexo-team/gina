package org.openflexo.fib.editor;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBValidationController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class ValidationWindow {

	public static Resource COMPONENT_VALIDATION_FIB = ResourceLocator.locateResource("Fib/ComponentValidation.fib");

	private FIBView validationView = null;
	private JDialog validationDialog = null;
	private FIBValidationController validationController = null;

	public ValidationWindow(JFrame frame, FIBEditorController controller) {
		FIBComponent componentValidationComponent = FIBLibrary.instance().retrieveFIBComponent(COMPONENT_VALIDATION_FIB, true);
		validationController = new FIBValidationController(componentValidationComponent, controller);
		validationView = FIBController.makeView(componentValidationComponent, validationController);
		validationDialog = new JDialog(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_validation"),
				false);
		validationDialog.getContentPane().add(validationView.getResultingJComponent());
		validationDialog.pack();
	}

	public void validateAndDisplayReportForComponent(FIBComponent component) {
		ValidationReport report = component.validate();
		validationView.getController().setDataObject(report);
		validationDialog.setVisible(true);
	}
}
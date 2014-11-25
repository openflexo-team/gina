package org.openflexo.fib.swing.validation;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.FIBJPanel;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.validation.Validable;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationModel;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

@SuppressWarnings("serial")
public abstract class ValidationPanel extends FIBJPanel<ValidationReport> {

	public static Resource VALIDATION_PANEL_FIB = ResourceLocator.locateResource("Fib/ValidationPanel.fib");

	public ValidationPanel(ValidationReport validationReport, LocalizedDelegate parentLocalizer) {
		super(FIBLibrary.instance().retrieveFIBComponent(VALIDATION_PANEL_FIB, true), validationReport, parentLocalizer);
	}

	@Override
	protected FIBValidationController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return new FIBValidationController(fibComponent) {
			@Override
			protected void performSelect(ValidationIssue<?, ?> validationIssue) {
				ValidationPanel.this.performSelect(validationIssue);
			}
		};
	}

	@Override
	public FIBValidationController getController() {
		return (FIBValidationController) super.getController();
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
	public Class<ValidationReport> getRepresentedType() {
		return ValidationReport.class;
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
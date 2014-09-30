package org.openflexo.fib.swing.validation;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
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

	public static Resource COMPONENT_VALIDATION_FIB = ResourceLocator.locateResource("Fib/ComponentValidation.fib");

	public ValidationPanel(ValidationReport validationReport, LocalizedDelegate parentLocalizer) {
		super(FIBLibrary.instance().retrieveFIBComponent(COMPONENT_VALIDATION_FIB, true), validationReport, parentLocalizer);
	}

	@Override
	protected FIBController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return new FIBValidationController(fibComponent) {
			@Override
			protected void performSelect(ValidationIssue<?, ?> validationIssue) {
				ValidationPanel.this.performSelect(validationIssue);
			}

			@Override
			public void checkAgain() {
				startValidation(getValidationModel());
				super.checkAgain();
				stopValidation(getValidationModel());
			}
		};
	}

	protected abstract void performSelect(ValidationIssue<?, ?> validationIssue);

	public void validate(ValidationModel validationModel, Validable objectToValidate) {
		startValidation(validationModel);
		ValidationReport report = validationModel.validate(objectToValidate);
		stopValidation(validationModel);
		getController().setDataObject(report);
	}

	@Override
	public Class<ValidationReport> getRepresentedType() {
		return ValidationReport.class;
	}

	@Override
	public void delete() {
		// TODO
	}

	public void startValidation(ValidationModel validationModel) {
	}

	public void stopValidation(ValidationModel validationModel) {
	}
}
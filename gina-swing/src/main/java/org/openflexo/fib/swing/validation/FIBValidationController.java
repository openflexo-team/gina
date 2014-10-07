package org.openflexo.fib.swing.validation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.FIBUtilsIconLibrary;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.InformationIssue;
import org.openflexo.model.validation.ProblemIssue;
import org.openflexo.model.validation.Validable;
import org.openflexo.model.validation.ValidationError;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationModel;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.model.validation.ValidationWarning;

public class FIBValidationController extends FIBController {

	static final Logger logger = Logger.getLogger(FIBValidationController.class.getPackage().getName());

	private ValidationIssue<?, ?> selectedValidationIssue;

	public FIBValidationController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public ValidationIssue<?, ?> getSelectedValidationIssue() {
		return selectedValidationIssue;
	}

	public void setSelectedValidationIssue(ValidationIssue<?, ?> validationIssue) {
		if (selectedValidationIssue != validationIssue) {
			selectedValidationIssue = validationIssue;
			performSelect(validationIssue);
		}
	}

	// To be overriden
	protected void performSelect(ValidationIssue<?, ?> validationIssue) {

	}

	public ImageIcon iconFor(Object validationObject) {
		if (validationObject instanceof ValidationError) {
			if (((ValidationError) validationObject).isFixable()) {
				return FIBUtilsIconLibrary.FIXABLE_ERROR_ICON;
			} else {
				return FIBUtilsIconLibrary.UNFIXABLE_ERROR_ICON;
			}
		} else if (validationObject instanceof ValidationWarning) {
			if (((ValidationWarning) validationObject).isFixable()) {
				return FIBUtilsIconLibrary.FIXABLE_WARNING_ICON;
			} else {
				return FIBUtilsIconLibrary.UNFIXABLE_WARNING_ICON;
			}
		} else if (validationObject instanceof InformationIssue) {
			return FIBUtilsIconLibrary.INFO_ISSUE_ICON;
		} else if (validationObject instanceof FixProposal) {
			return FIBUtilsIconLibrary.FIX_PROPOSAL_ICON;
		}
		return null;
	}

	public void checkAgain() {
		setDataObject(getValidationModel().validate(getValidatedObject()));
	}

	public void fixIssue(FixProposal<?, ?> fixProposal) {
		if (fixProposal != null) {
			fixProposal.apply(true);
			setDataObject(getDataObject(), true);
		}
	}

	public void fixAllIssues(List<ValidationIssue<?, ?>> issues) {
		fixIssues(issues, null);
	}

	public void fixIssues(List<ValidationIssue<?, ?>> issues, FixProposal<?, ?> fixProposal) {

		if (issues.size() == 1 && fixProposal != null && fixProposal.getProblemIssue() == issues.get(0)) {
			fixIssue(fixProposal);
		} else {
			for (ValidationIssue<?, ?> issue : new ArrayList<ValidationIssue<?, ?>>(issues)) {
				if (issue instanceof ProblemIssue) {
					FixProposal<?, ?> proposal = null;
					if (fixProposal != null && fixProposal.getProblemIssue() == issue) {
						proposal = fixProposal;
					} else if (((ProblemIssue) issue).getFixProposals().size() > 0) {
						if (((ProblemIssue) issue).getFixProposals().size() == 1) {
							proposal = ((ProblemIssue<?, ?>) issue).getFixProposals().get(0);
						} else {
							List<FixProposal<?, ?>> availableProposals = ((ProblemIssue<?, ?>) issue).getFixProposals(fixProposal
									.getClass());
							if (availableProposals.size() > 0) {
								proposal = availableProposals.get(0);
							} else {
								proposal = ((ProblemIssue<?, ?>) issue).getFixProposals().get(0);
							}
						}
					}

					if (proposal != null) {
						fixIssue(proposal);
					}
				}
			}
		}
	}

	public void enableRule(ValidationRule<?, ?> rule) {

		System.out.println("enableRule " + rule);

		rule.setIsEnabled(true);
	}

	public void disableRule(ValidationRule<?, ?> rule) {

		System.out.println("disableRule " + rule);

		if (getDataObject() instanceof ValidationReport) {
			rule.setIsEnabled(false);
			ValidationReport report = getDataObject();
			List<ValidationIssue<?, ?>> issuesToRemove = report.issuesRegarding(rule);
			for (ValidationIssue<?, ?> issue : issuesToRemove) {
				issue.delete();
			}
			report.getPropertyChangeSupport().firePropertyChange("filteredIssues", null, report.getFilteredIssues());
		}

	}

	@Override
	public ValidationReport getDataObject() {
		return (ValidationReport) super.getDataObject();
	}

	public Validable getValidatedObject() {
		return getDataObject().getRootObject();
	}

	public ValidationModel getValidationModel() {
		return getDataObject().getValidationModel();
	}

	public Color getValidationRuleColor(ValidationRule<?, ?> rule) {
		return (rule.getIsEnabled() ? Color.BLACK : Color.LIGHT_GRAY);
	}
}

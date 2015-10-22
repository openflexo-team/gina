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

package org.openflexo.fib.swing.validation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.utils.FIBUtilsIconLibrary;
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

	static final Logger LOGGER = Logger.getLogger(FIBValidationController.class.getPackage().getName());

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

	public void checkAgain() throws InterruptedException {
		if (getValidationModel() != null && getValidatedObject() != null) {
			setDataObject(getValidationModel().validate(getValidatedObject()));
		}
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

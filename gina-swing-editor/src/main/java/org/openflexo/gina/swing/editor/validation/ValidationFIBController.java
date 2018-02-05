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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBValidationReport;
import org.openflexo.gina.swing.editor.ComponentSwingEditorFIBController;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.ProblemIssue;
import org.openflexo.model.validation.Validable;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationModel;
import org.openflexo.model.validation.ValidationRule;

public class ValidationFIBController extends ComponentSwingEditorFIBController {

	static final Logger LOGGER = Logger.getLogger(ValidationFIBController.class.getPackage().getName());

	private ValidationIssue<?, ?> selectedValidationIssue;

	public ValidationFIBController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public ValidationFIBController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public ValidationFIBController(FIBComponent rootComponent, FIBEditorController editorController) {
		super(rootComponent, editorController);
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

	@Override
	public FIBModelObject getSelectedObject() {
		FIBModelObject returned = super.getSelectedObject();
		System.out.println("getSelectedObject() ? " + returned);
		if (returned == null) {
			return getDataObject();
		}
		return returned;
	}

	// To be overriden
	protected void performSelect(ValidationIssue<?, ?> validationIssue) {

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

	public void fixAllIssues(Collection<ValidationIssue<?, ?>> issues) {
		fixIssues(issues, null);
	}

	public void fixIssues(Collection<ValidationIssue<?, ?>> issues, FixProposal<?, ?> fixProposal) {

		if (issues.size() == 1 && fixProposal != null && fixProposal.getProblemIssue() == issues.iterator().next()) {
			fixIssue(fixProposal);
		}
		else {
			for (ValidationIssue<?, ?> issue : new ArrayList<>(issues)) {
				if (issue instanceof ProblemIssue) {
					FixProposal<?, ?> proposal = null;
					if (fixProposal != null && fixProposal.getProblemIssue() == issue) {
						proposal = fixProposal;
					}
					else {
						List<FixProposal<?, ?>> proposals = ((ProblemIssue) issue).getFixProposals();
						if (proposals.size() > 0) {
							proposal = proposals.get(0);
							if (proposals.size() > 1) {
								List<FixProposal<?, ?>> availableProposals = ((ProblemIssue) issue).getFixProposals(fixProposal.getClass());
								if (availableProposals.size() > 0) {
									proposal = availableProposals.get(0);
								}
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

		if (getDataObject() != null) {
			rule.setIsEnabled(false);
			FIBValidationReport report = getValidationReport(getDataObject());
			List<ValidationIssue<?, ?>> issuesToRemove = report.issuesRegarding(rule);
			for (ValidationIssue<?, ?> issue : issuesToRemove) {
				issue.delete();
			}
			report.getPropertyChangeSupport().firePropertyChange("filteredIssues", null, report.getFilteredIssues());
		}

	}

	public Validable getValidatedObject() {
		return getDataObject();
	}

	public ValidationModel getValidationModel() {
		return getValidationModel(getDataObject());
	}

	public Color getValidationRuleColor(ValidationRule<?, ?> rule) {
		return (rule.getIsEnabled() ? Color.BLACK : Color.LIGHT_GRAY);
	}

	@Override
	public void setDataObject(Object anObject) {
		super.setDataObject(anObject);
		getPropertyChangeSupport().firePropertyChange("showErrorsWarnings", !showErrorsWarnings(), showErrorsWarnings());
	}

	private boolean showErrorsWarnings = true;

	public boolean showErrorsWarnings() {
		return showErrorsWarnings && getDataObject() != null;
	}

	public void setShowErrorsWarnings(boolean showErrorsWarnings) {
		System.out.println("setShowErrorsWarnings with " + showErrorsWarnings);
		if (this.showErrorsWarnings != showErrorsWarnings) {
			this.showErrorsWarnings = showErrorsWarnings;
			getPropertyChangeSupport().firePropertyChange("showErrorsWarnings", !showErrorsWarnings, showErrorsWarnings);
		}
	}

	public void showIssue(ValidationIssue<?, ?> issue) {
		if (issue != null) {
			Validable objectToSelect = issue.getValidable();
			// getFlexoController().selectAndFocusObject((FlexoObject) objectToSelect);
		}
	}

	public void fixIssue(ValidationIssue<?, ?> issue) {
		System.out.println("fixIssue " + issue);
		if (issue instanceof ProblemIssue) {
			/*VirtualModel vmToRevalidate = null;
			if (issue.getValidationReport().getRootObject() instanceof VirtualModel) {
				vmToRevalidate = (VirtualModel) issue.getValidationReport().getRootObject();
			}
			IssueFixing fixing = new IssueFixing((ProblemIssue<?, ?>) issue, getFlexoController());
			FixIssueDialog dialog = new FixIssueDialog(fixing, getFlexoController());
			dialog.showDialog();
			if (dialog.getStatus() == Status.VALIDATED) {
				fixing.fix();
				if (vmToRevalidate != null) {
					revalidate(vmToRevalidate);
				}
			}
			else if (dialog.getStatus() == Status.NO) {
				fixing.ignore();
			}*/
		}
	}

	public void revalidate(FIBComponent component) {
		System.out.println("Tiens, faudrait revalider " + component);
		/*if (getServiceManager() != null) {
			FMLTechnologyAdapterController tac = getServiceManager().getTechnologyAdapterControllerService()
					.getTechnologyAdapterController(FMLTechnologyAdapterController.class);
			FMLValidationReport virtualModelReport = (FMLValidationReport) tac.getValidationReport(virtualModel);
			RevalidationTask validationTask = new RevalidationTask(virtualModelReport);
			getServiceManager().getTaskManager().scheduleExecution(validationTask);
		}*/
	}

}

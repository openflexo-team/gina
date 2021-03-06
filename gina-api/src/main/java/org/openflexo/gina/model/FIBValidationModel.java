/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina.model;

import org.openflexo.connie.BindingEvaluator;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.pamela.validation.Validable;
import org.openflexo.pamela.validation.ValidationModel;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * A {@link ValidationModel} used to validate FIB model
 * 
 * @author sylvain
 * 
 */
public class FIBValidationModel extends ValidationModel {

	private static Resource fibValidationLocalizedDelegate = ResourceLocator.locateResource("GinaLocalization/FIBValidation");
	public static LocalizedDelegate VALIDATION_LOCALIZATION = new LocalizedDelegateImpl(fibValidationLocalizedDelegate,
			FIBModelObjectImpl.GINA_LOCALIZATION, true, true);

	public FIBValidationModel(FIBModelFactory fibModelFactory) {
		super(fibModelFactory.getModelContext());
	}

	/**
	 * Overrides shouldNotifyValidation
	 * 
	 * @see org.openflexo.pamela.validation.ValidationModel#shouldNotifyValidation(org.openflexo.pamela.validation.Validable)
	 */
	@Override
	protected boolean shouldNotifyValidation(Validable next) {
		return true;
	}

	/**
	 * Return a boolean indicating if validation of each rule must be notified
	 * 
	 * @param next
	 * @return a boolean
	 */
	@Override
	protected boolean shouldNotifyValidationRules() {
		return true;
	}

	/**
	 * Overrides fixAutomaticallyIfOneFixProposal
	 * 
	 * @see org.openflexo.pamela.validation.ValidationModel#fixAutomaticallyIfOneFixProposal()
	 */
	@Override
	public boolean fixAutomaticallyIfOneFixProposal() {
		return false;
	}

	@Override
	public String localizedInContext(String key, Object context) {
		String localized = VALIDATION_LOCALIZATION.localizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage(), true);
		if (localized != null && localized.contains("($")) {
			String asBindingExpression = asBindingExpression(localized);
			try {
				return (String) BindingEvaluator.evaluateBinding(asBindingExpression, context);
			} catch (Exception e) {
				e.printStackTrace();
				return localized;
			}
		}
		return localized;
	}

	@Override
	public FIBValidationReport validate(Validable object) throws InterruptedException {
		if (object instanceof FIBComponent) {
			return new FIBValidationReport(this, (FIBComponent) object);
		}
		else {
			return null;
		}
	}

}

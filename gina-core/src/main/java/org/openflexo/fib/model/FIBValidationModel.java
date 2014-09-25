/*
 * (c) Copyright 2012-2014 Openflexo
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.model;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.validation.Validable;
import org.openflexo.model.validation.ValidationModel;

/**
 * A {@link ValidationModel} used to validate FIB model
 * 
 * @author sylvain
 * 
 */
public class FIBValidationModel extends ValidationModel {

	public FIBValidationModel(FIBModelFactory fibModelFactory) throws ModelDefinitionException {
		super(fibModelFactory.getModelContext());
	}

	/**
	 * Overrides shouldNotifyValidation
	 * 
	 * @see org.openflexo.model.validation.ValidationModel#shouldNotifyValidation(org.openflexo.model.validation.Validable)
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
	 * @see org.openflexo.model.validation.ValidationModel#fixAutomaticallyIfOneFixProposal()
	 */
	@Override
	public boolean fixAutomaticallyIfOneFixProposal() {
		return false;
	}

	@Override
	public String localizedForKey(String key) {
		return FlexoLocalization.localizedForKey(key);
	}

}

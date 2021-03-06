/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import java.util.logging.Logger;

import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.DeserializationFinalizer;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.validation.FixProposal;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.pamela.validation.ValidationWarning;

@ModelEntity
@ImplementationClass(FIBDependancy.FIBDependancyImpl.class)
@XMLElement(xmlTag = "Dependancy")
public interface FIBDependancy extends FIBModelObject {

	@PropertyIdentifier(type = String.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = String.class)
	public static final String MASTER_COMPONENT_NAME_KEY = "masterComponentName";

	@Getter(value = OWNER_KEY /*, inverse = FIBComponent.EXPLICIT_DEPENDANCIES_KEY*/)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBComponent getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBComponent owner);

	@Getter(value = MASTER_COMPONENT_NAME_KEY)
	@XMLAttribute(xmlTag = "componentName")
	public String getMasterComponentName();

	@Setter(MASTER_COMPONENT_NAME_KEY)
	public void setMasterComponentName(String masterComponentName);

	public FIBComponent getMasterComponent();

	public void setMasterComponent(FIBComponent masterComponent);

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public static abstract class FIBDependancyImpl extends FIBModelObjectImpl implements FIBDependancy {

		private static final Logger logger = Logger.getLogger(FIBDependancy.class.getPackage().getName());

		// Owner depends of masterComponent

		private FIBComponent masterComponent;
		private String masterComponentName;

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		@Override
		public FIBComponent getMasterComponent() {
			return masterComponent;
		}

		@Override
		public void setMasterComponent(FIBComponent masterComponent) {
			FIBPropertyNotification<FIBComponent> notification = requireChange(MASTER_COMPONENT_NAME_KEY, masterComponent);
			if (notification != null && getOwner() != null) {
				this.masterComponent = masterComponent;
				// try {
				getOwner().declareDependantOf(masterComponent);
				/*} catch (DependancyLoopException e) {
					logger.warning("DependancyLoopException raised while applying explicit dependancy for " + getOwner() + " and "
							+ getMasterComponent() + " message: " + e.getMessage());
				}*/
				hasChanged(notification);
			}
		}

		@Override
		public String getMasterComponentName() {
			if (getMasterComponent() != null) {
				return getMasterComponent().getName();
			}
			return masterComponentName;
		}

		@Override
		public void setMasterComponentName(String masterComponentName) {
			this.masterComponentName = masterComponentName;
		}

		@Override
		public void finalizeDeserialization() {
			setMasterComponent(getComponent().getRootComponent().getComponentNamed(masterComponentName));
		}

	}

	@DefineValidationRule
	public static class DependancyShouldNotBeRegisteredTwice extends ValidationRule<DependancyShouldNotBeRegisteredTwice, FIBDependancy> {
		public DependancyShouldNotBeRegisteredTwice() {
			super(FIBDependancy.class, "dependancy_should_not_be_registered_twice");
		}

		@Override
		public ValidationIssue<DependancyShouldNotBeRegisteredTwice, FIBDependancy> applyValidation(FIBDependancy dependancy) {
			if (dependancy.getOwner() != null) {
				if (dependancy.getOwner().getExplicitDependancies().indexOf(dependancy) != dependancy.getOwner().getExplicitDependancies()
						.lastIndexOf(dependancy)) {
					RemoveExtraReferences fixProposal = new RemoveExtraReferences(dependancy);
					return new ValidationWarning<>(this, dependancy, "dependancy_to_($validable.toString)_is_registered_twice",
							fixProposal);
				}
			}

			return null;
		}

		protected static class RemoveExtraReferences extends FixProposal<DependancyShouldNotBeRegisteredTwice, FIBDependancy> {

			private final FIBDependancy dependancy;

			public RemoveExtraReferences(FIBDependancy dependancy) {
				super("remove_duplicated_references");
				this.dependancy = dependancy;
			}

			@Override
			protected void fixAction() {
				FIBComponent component = dependancy.getOwner();
				if (component != null) {
					while (component.getExplicitDependancies().indexOf(dependancy) != component.getExplicitDependancies()
							.lastIndexOf(dependancy)) {
						component.removeFromExplicitDependancies(dependancy);
					}
				}
			}

		}

	}

}

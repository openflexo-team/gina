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

package org.openflexo.fib.swing.utils.validation;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.container.FIBPanel;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.model.widget.FIBButton;
import org.openflexo.fib.model.widget.FIBLabel;
import org.openflexo.fib.model.widget.FIBTextField;
import org.openflexo.fib.model.widget.FIBLabel.Align;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.validation.ParameteredFixProposal;
import org.openflexo.model.validation.ParameteredFixProposal.ParameterDefinition;
import org.openflexo.model.validation.ParameteredFixProposal.StringParameter;

/**
 * Represents a FIBComponent built at run-time with a {@link ParameteredFixProposal}, and allowing to edit the parameters of the proposal
 * 
 * @author sylvain
 * 
 */
@Deprecated
// TODO: not sure it is still used
@ModelEntity
@ImplementationClass(AskParametersComponent.AskParametersComponentImpl.class)
public interface AskParametersComponent extends FIBPanel {

	public ParameteredFixProposal<?, ?> getFixProposal();

	public static abstract class AskParametersComponentImpl extends FIBPanelImpl implements AskParametersComponent {

		private static FIBModelFactory FACTORY;

		static {
			try {
				FACTORY = new FIBModelFactory(AskParametersComponent.class);
			} catch (ModelDefinitionException e1) {
				e1.printStackTrace();
			}
		}

		public static AskParametersComponent makeAskParametersComponent(ParameteredFixProposal<?, ?> fixProposal) {
			AskParametersComponentImpl returned = (AskParametersComponentImpl) FACTORY.newInstance(AskParametersComponent.class);
			returned.initWithFixProposal(fixProposal);
			return returned;
		}

		private ParameteredFixProposal<?, ?> fixProposal;

		private void initWithFixProposal(ParameteredFixProposal<?, ?> fixProposal) {
			this.fixProposal = fixProposal;
			setLayout(Layout.twocols);
			setDataClass(ParameteredFixProposal.class);

			FIBLabel title = FACTORY.newFIBLabel(fixProposal.getMessage());
			title.setAlign(Align.center);

			addToSubComponents(title, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));

			for (ParameterDefinition<?> p : fixProposal.getParameters()) {
				if (p instanceof StringParameter) {
					FIBLabel label = FACTORY.newFIBLabel(p.getLabel());
					FIBTextField tf = FACTORY.newFIBTextField();
					tf.setData(new DataBinding<String>("data.getStringParameter(\"" + p.getName() + "\").value", tf, String.class,
							BindingDefinitionType.GET_SET));
					// TODO: not finished, and should be tested
					addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
					addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				}
			}

			FIBPanel controlPanel = FACTORY.newFIBPanel();
			controlPanel.setLayout(Layout.buttons);

			FIBButton validateButton = FACTORY.newFIBButton();
			validateButton.setLabel("validate");
			validateButton.setAction(new DataBinding<Object>("controller.validateAndDispose()", validateButton, Void.TYPE,
					BindingDefinitionType.EXECUTE));

			FIBButton cancelButton = FACTORY.newFIBButton();
			cancelButton.setLabel("cancel");
			cancelButton.setAction(new DataBinding<Object>("controller.cancelAndDispose()", validateButton, Void.TYPE,
					BindingDefinitionType.EXECUTE));

			controlPanel.addToSubComponents(validateButton);
			controlPanel.addToSubComponents(cancelButton);

			addToSubComponents(controlPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));

		}

		@Override
		public ParameteredFixProposal<?, ?> getFixProposal() {
			return fixProposal;
		}
	}

}

package org.openflexo.fib.swing.validation;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabel.Align;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
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

			FIBLabel title = FACTORY.newFIBLabel(fixProposal.getLocalizedMessage());
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
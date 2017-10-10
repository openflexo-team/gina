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

package org.openflexo.gina.swing.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.toolbox.ToolBox;

/**
 * This panel is the way a DataBinding is edited when it takes the form of a complex expression (responds true to
 * {@link DataBinding#isExpression()}). This panel is always instanciated in the context of a {@link BindingSelector}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class BindingExpressionSelectorPanel extends AbstractBindingSelectorPanel {

	private final BindingSelector bindingSelector;
	protected ButtonsControlPanel _controlPanel;
	protected JButton _applyButton;
	protected JButton _cancelButton;
	protected JButton _resetButton;
	private BindingExpressionPanel _expressionPanel;

	/**
	 * @param bindingSelector
	 */
	BindingExpressionSelectorPanel(BindingSelector bindingSelector) {
		super();
		this.bindingSelector = bindingSelector;
	}

	@Override
	public void delete() {
		if (_expressionPanel != null) {
			_expressionPanel.delete();
			_expressionPanel = null;
		}
	}

	@Override
	public Dimension getDefaultSize() {
		return new Dimension(520, 250);
	}

	@Override
	protected void init() {
		setLayout(new BorderLayout());

		_controlPanel = new ButtonsControlPanel() {
			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(key, component);
			}
		};
		_applyButton = _controlPanel.addButton("apply", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingExpressionSelectorPanel.this.bindingSelector.apply();
			}
		});
		_cancelButton = _controlPanel.addButton("cancel", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingExpressionSelectorPanel.this.bindingSelector.cancel();
			}
		});
		_resetButton = _controlPanel.addButton("reset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingExpressionSelectorPanel.this.bindingSelector.getEditedObject().reset();
				BindingExpressionSelectorPanel.this.bindingSelector.apply();
			}
		});

		_controlPanel.applyFocusTraversablePolicyTo(_controlPanel, false);

		// BindingExpression newBindingExpression = new BindingExpression(new BindingExpression.BindingValueVariable(new
		// Variable("parent")));

		/*if (bindingSelector.getEditedObject().getExpression() == null || !(bindingSelector.getEditedObject().isExpression())) {
			BindingSelector.logger.severe("Unexpected object " + bindingSelector.getEditedObject().getClass().getSimpleName()
					+ " found instead of BindingExpression");
			bindingSelector.getEditedObject().setExpression(bindingSelector.makeBindingExpression());
		}*/

		/*if (_bindingSelector.getEditedObject() == null || ((BindingExpression) _bindingSelector.getEditedObject()).getExpression() == null) {
			_bindingSelector._editedObject = _bindingSelector.makeBindingExpression();
		}*/

		if (BindingSelector.LOGGER.isLoggable(Level.FINE)) {
			BindingSelector.LOGGER.fine("init() called in BindingExpressionSelectorPanel with " + bindingSelector.getEditedObject()
					+ " expression=" + bindingSelector.getEditedObject().getExpression());
		}

		_expressionPanel = new BindingExpressionPanel(bindingSelector.getEditedObject()) {
			@Override
			protected void fireEditedExpressionChanged(DataBinding<?> expression) {
				// Called when the binding represented by the panel has changed
				super.fireEditedExpressionChanged(expression);
				bindingSelector.fireEditedObjectChanged();
				updateStatus(expression);

			}
		};

		add(_expressionPanel, BorderLayout.CENTER);
		add(_controlPanel, BorderLayout.SOUTH);

		update();
	}

	@Override
	protected void processTabPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processBackspace() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processDelete() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processDownPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processEnterPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processUpPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void synchronizePanelWithTextFieldValue(String textValue) {
	}

	@Override
	protected void update() {
		if (BindingSelector.LOGGER.isLoggable(Level.FINE)) {
			BindingSelector.LOGGER.fine("update() called for BindingExpressionSelectorPanel");
		}

		_expressionPanel.setEditedExpression(bindingSelector.getEditedObject());

		updateStatus(bindingSelector.getEditedObject());

	}

	@Override
	protected void updateStatus(DataBinding<?> bindingExpression) {

		boolean isValid = bindingExpression.forceRevalidate();

		// Update apply button state
		_applyButton.setEnabled(bindingExpression != null && isValid);
		if (bindingExpression != null && isValid) {
			if (ToolBox.isMacOSLaf()) {
				_applyButton.setSelected(true);
			}
		}
		if (bindingExpression != null) {
			bindingSelector.getTextField().setForeground(bindingExpression.isValid() ? Color.BLACK : Color.RED);
		}
	}

	@Override
	protected void fireBindableChanged() {
		update();
	}

	@Override
	protected void willApply() {
	}
}

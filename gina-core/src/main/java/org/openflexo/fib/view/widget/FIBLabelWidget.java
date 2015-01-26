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

package org.openflexo.fib.view.widget;

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.view.FIBWidgetView;

public class FIBLabelWidget extends FIBWidgetView<FIBLabel, JLabel, String> {
	private static final Logger LOGGER = Logger.getLogger(FIBLabelWidget.class.getPackage().getName());

	private JLabel labelWidget;

	public FIBLabelWidget(FIBLabel model, FIBController controller) {
		super(model, controller);
		if (model.getData().isValid()) {
			labelWidget = new JLabel(" ");
		} else {
			labelWidget = new JLabel();
		}
		labelWidget.setFocusable(false); // There is not much point in giving focus to a label since there is no KeyBindings nor KeyListener
											// on it.
		labelWidget.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, TOP_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
				RIGHT_COMPENSATING_BORDER));
		updateFont();
		updateAlign();
		updateLabel();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateLabel();
		widgetUpdating = false;
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// Read only component
		return false;
	}

	@Override
	public JLabel getJComponent() {
		return labelWidget;
	}

	@Override
	public JLabel getDynamicJComponent() {
		return labelWidget;
	}

	final protected void updateAlign() {
		if (labelWidget == null || getWidget() == null) {
			return;
		}
		if (getWidget().getAlign() == null) {
			return;
		}
		labelWidget.setHorizontalAlignment(getWidget().getAlign().getAlign());
	}

	final protected void updateLabel() {
		if (labelWidget == null || getWidget() == null) {
			return;
		}
		if (getWidget().getData().isValid()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (getWidget() != null) {
						labelWidget.setText(getWidget().getLocalize() ? getLocalized(getValue()) : getValue());
					}
				}
			});
		} else {
			labelWidget.setText(getWidget().getLocalize() ? getLocalized(getWidget().getLabel()) : getWidget().getLabel());
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateLabel();
	}
}

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.listener.GinaStackEvent;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.gina.event.FIBChangeValueEvent;
import org.openflexo.gina.event.FIBEvent;
import org.openflexo.gina.event.FIBEventFactory;
import org.openflexo.gina.event.FIBTextEvent;

/**
 * Represents a widget able to edit a boolean, a Boolean or a String object
 * 
 * @author sguerin
 */
public class FIBCheckBoxWidget extends FIBWidgetView<FIBCheckBox, JCheckBox, Boolean> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBCheckBoxWidget.class.getPackage().getName());

	private final JCheckBox checkbox;

	private boolean isNegate = false;

	/**
	 * @param model
	 */
	public FIBCheckBoxWidget(FIBCheckBox model, FIBController controller) {
		super(model, controller);
		checkbox = new JCheckBox();
		checkbox.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
				RIGHT_COMPENSATING_BORDER));
		checkbox.setOpaque(false);
		checkbox.setBorderPaintedFlat(true);
		checkbox.setSelected(model.getSelected());
		if (isReadOnly()) {
			checkbox.setEnabled(false);
		} else {
			checkbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					GinaStackEvent stack = FIBCheckBoxWidget.this.actionPerformed(FIBEventFactory.getInstance().createChangeValueEvent(
							getValue() ? "unchecked" : "checked", FIBCheckBoxWidget.this.checkbox.isSelected()));
					
					updateModelFromWidget();
					
					stack.unstack();
				}
			});
		}
		checkbox.addFocusListener(this);

		// _jCheckBox.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

		isNegate = model.getNegate();

		updateFont();
	}
	
	public void executeEvent(FIBEvent e) {
		
		widgetExecuting = true;

		switch(e.getAction()) {
		case "checked":
			checkbox.setSelected(true);
			break;
		case "unchecked":
			checkbox.setSelected(false);
			break;
		}
		
		widgetExecuting = false;

	}

	@Override
	public Boolean getValue() {
		Boolean value = super.getValue();
		return value != null && value;
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		Boolean value = getValue();
		if (notEquals(isNegate ? value == null || !value : value != null && value, checkbox.isSelected())) {
			widgetUpdating = true;
			if (value != null) {
				if (isNegate) {
					value = !value;
				}
				checkbox.setSelected(value);
			}
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (isReadOnly()) {
			return false;
		}

		Boolean value = getValue();
		if (notEquals(isNegate ? value == null || !value : value != null && value, checkbox.isSelected())) {
			setValue(isNegate ? !checkbox.isSelected() : checkbox.isSelected());
			return true;
		}
		return false;

	}

	@Override
	public JCheckBox getJComponent() {
		return checkbox;
	}

	@Override
	public JCheckBox getDynamicJComponent() {
		return checkbox;
	}

	@Override
	public Boolean getDefaultData() {
		return getComponent().getSelected();
	}

}

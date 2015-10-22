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

package org.openflexo.fib.swing.view.widget;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.swing.view.FIBWidgetView;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.swing.FontSelector;

public class FIBFontWidget extends FIBWidgetView<FIBFont, FontSelector, Font> implements ApplyCancelListener {

	private static final Logger LOGGER = Logger.getLogger(FIBFontWidget.class.getPackage().getName());

	protected FontSelector _selector;
	private final JCheckBox checkBox;
	private Font revertValue;

	private final JPanel container;

	public FIBFontWidget(FIBFont model, FIBController controller) {
		super(model, controller);
		_selector = new FontSelector();
		if (isReadOnly()) {
			_selector.getDownButton().setEnabled(false);
		} else {
			_selector.addApplyCancelListener(this);
		}
		_selector.addFocusListener(this);
		checkBox = new JCheckBox();
		checkBox.setToolTipText(FlexoLocalization.localizedForKey("undefined_value", checkBox));
		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_selector.setEnabled(!checkBox.isSelected());
				updateModelFromWidget();
			}
		});
		container = new JPanel(new GridBagLayout());
		container.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		container.add(checkBox, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		container.add(_selector, gbc);
		updateCheckboxVisibility();
		updateFont();
	}

	final public void updateCheckboxVisibility() {
		checkBox.setVisible(getWidget().getAllowsNull());
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// if (notEquals(getValue(),getSelectedFont())) {
		widgetUpdating = true;
		checkBox.setSelected(getValue() != null);
		_selector.setEnabled((getValue() != null || !getWidget().getAllowsNull()) && isEnabled());
		setFont(getValue());
		revertValue = getValue();
		widgetUpdating = false;
		return true;
		// }
		// return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// if (notEquals(getValue(), getSelectedFont())) {
		if (isReadOnly()) {
			return false;
		}

		Font editedObject = null;
		if (checkBox.isSelected()) {
			editedObject = _selector.getEditedObject();
		}
		setValue(editedObject);
		return true;
		// }
		// return false;
	}

	@Override
	public JComponent getJComponent() {
		return container;
	}

	@Override
	public FontSelector getDynamicJComponent() {
		return _selector;
	}

	protected void setFont(Font aFont) {
		_selector.setEditedObject(aFont);
	}

	@Override
	public void fireApplyPerformed() {
		GinaStackEvent stack = GENotifier.raise(FIBEventFactory.getInstance().createValueEvent(
				FIBValueEventDescription.CHANGED, getValue().getFontName()));
		
		updateModelFromWidget();
		
		stack.end();
	}

	@Override
	public void fireCancelPerformed() {
		setValue(revertValue);
	}

}

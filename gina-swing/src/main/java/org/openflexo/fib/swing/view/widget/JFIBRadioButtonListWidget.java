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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.view.widget.impl.FIBRadioButtonListWidgetImpl;

public class JFIBRadioButtonListWidget<T> extends FIBRadioButtonListWidgetImpl<JPanel, T> {

	static final Logger LOGGER = Logger.getLogger(JFIBRadioButtonListWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingTechnologyAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingRadioButtonRenderingTechnologyAdapter<T> extends SwingRenderingTechnologyAdapter<JPanel>
			implements RadioButtonRenderingTechnologyAdapter<JPanel> {

		private ButtonGroup buttonGroup;

		@Override
		public Object getSelectedItem(JPanel component) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setSelectedItem(JPanel component, Object item) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getSelectedIndex(JPanel component) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setSelectedIndex(JPanel component, int index) {
			// TODO Auto-generated method stub

		}

	}

	private JRadioButton[] radioButtonArray;

	private final JPanel panel;

	private T selectedValue;

	public JFIBRadioButtonListWidget(FIBRadioButtonList model, FIBController controller) {
		super(model, controller);
		buttonGroup = new ButtonGroup();
		panel = new JPanel(new GridLayout(0, model.getColumns(), model.getHGap(), model.getVGap()));
		panel.setOpaque(false);
		rebuildRadioButtons();
		updateData();
		/*if (getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
				radioButtonArray[0].setSelected(true);
				setSelected(getMultipleValueModel().getElementAt(0));
			}*/

		if ((getWidget().getData() == null || !getWidget().getData().isValid()) && getWidget().getAutoSelectFirstRow()
				&& getMultipleValueModel().getSize() > 0) {
			setSelectedValue(getMultipleValueModel().getElementAt(0));
		}
	}

	private void selectFirstRowIfRequired() {
		if (selectedValue == null && (getWidget().getData() != null && getWidget().getData().isValid())
				&& getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
			setSelectedValue(getMultipleValueModel().getElementAt(0));
		}
	}

	@Override
	protected FIBMultipleValueModel<T> createMultipleValueModel() {
		return new FIBMultipleValueModel<T>();
	}

	@Override
	protected void proceedToListModelUpdate() {
		rebuildRadioButtons();
		if (!widgetUpdating && !isDeleted() && getDynamicJComponent() != null) {
			updateWidgetFromModel();
		}
	}

	final protected void rebuildRadioButtons() {
		if (panel != null) {
			panel.removeAll();
			((GridLayout) panel.getLayout()).setColumns(getWidget().getColumns());
			((GridLayout) panel.getLayout()).setHgap(getWidget().getHGap());
			((GridLayout) panel.getLayout()).setVgap(getWidget().getVGap());
			buttonGroup = new ButtonGroup();
			radioButtonArray = new JRadioButton[getMultipleValueModel().getSize()];
			for (int i = 0; i < getMultipleValueModel().getSize(); i++) {
				T object = getMultipleValueModel().getElementAt(i);
				JRadioButton rb = new JRadioButton(getStringRepresentation(object), equals(object, selectedValue));
				rb.setOpaque(false);
				rb.addActionListener(new RadioButtonListener(rb, object, i));
				radioButtonArray[i] = rb;
				// Handle the case of icon should be displayed
				if (getWidget().getShowIcon() && getWidget().getIcon().isSet() && getWidget().getIcon().isValid()) {
					rb.setHorizontalAlignment(JCheckBox.LEFT);
					rb.setText(null);
					final JLabel label = new JLabel(getStringRepresentation(object), getIconRepresentation(object), JLabel.LEADING);
					Dimension ps = rb.getPreferredSize();
					rb.setLayout(new BorderLayout());
					label.setLabelFor(rb);
					label.setBorder(BorderFactory.createEmptyBorder(0, ps.width, 0, 0));
					rb.add(label);
				}
				panel.add(rb);
				buttonGroup.add(rb);
				if (object.equals(getValue())) {
					rb.doClick();
				}
			}
			updateFont();
			panel.revalidate();
			selectFirstRowIfRequired();
		}
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		T value = getValue();
		if (notEquals(value, selectedValue) /*|| listModelRequireChange()*/ || multipleValueModel == null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateWidgetFromModel()");
			}

			widgetUpdating = true;
			selectedValue = value;
			setSelected(value);
			// TODO: handle selected index
			rebuildRadioButtons();

			/*if (selectedValue == null) {
				if (getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
					radioButtonArray[0].doClick();
				}
				setSelected(getMultipleValueModel().getElementAt(0));
				selectedValue = getMultipleValueModel().getElementAt(0);
				setValue(selectedValue);
			}*/

			widgetUpdating = false;
			return true;
		}
		return false;
	}

	private class RadioButtonListener implements ActionListener {

		private final T value;
		private final JRadioButton button;
		private final int index;

		public RadioButtonListener(JRadioButton button, T value, int index) {
			this.button = button;
			this.value = value;
			this.index = index;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == button && button.isSelected()) {
				selectedValue = value;
				updateModelFromWidget();
				setSelected(value);
				setSelectedIndex(index);
			}
		}

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), selectedValue)) {
			modelUpdating = true;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateModelFromWidget with " + selectedValue);
			}
			if (selectedValue != null && !widgetUpdating) {
				setValue(selectedValue);
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return panel;
	}

	@Override
	public JPanel getDynamicJComponent() {
		return panel;
	}

	@Override
	public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			for (JRadioButton rb : radioButtonArray) {
				rb.setFont(getFont());
			}
		}
	}

	@Override
	public T getSelectedValue() {
		return selectedValue;
	}

	@Override
	public void setSelectedValue(T selectedValue) {
		if (selectedValue != null) {
			int newSelectedIndex = getMultipleValueModel().indexOf(selectedValue);
			if (newSelectedIndex >= 0 && newSelectedIndex < getMultipleValueModel().getSize()) {
				radioButtonArray[newSelectedIndex].doClick();
			}
		}
	}

}

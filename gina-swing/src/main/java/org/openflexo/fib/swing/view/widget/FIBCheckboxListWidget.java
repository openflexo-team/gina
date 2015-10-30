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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckboxList;

public class FIBCheckboxListWidget<T> extends JFIBMultipleValueWidget<FIBCheckboxList, JPanel, List<T>, T> {

	static final Logger logger = Logger.getLogger(FIBCheckboxListWidget.class.getPackage().getName());

	private JCheckBox[] checkboxesArray;
	private JLabel[] labelsArray;

	private final JPanel panel;

	private List<T> selectedValues;

	private BindingValueListChangeListener<T, List<T>> listenerToDataAsListValue;

	public FIBCheckboxListWidget(FIBCheckboxList model, FIBController controller) {
		super(model, controller);
		listenDataAsListValueChange();
		panel = new JPanel(new GridLayout(0, model.getColumns(), model.getHGap(), model.getVGap()));
		panel.setOpaque(false);
		selectedValues = new ArrayList<T>();
		rebuildCheckboxes();

	}

	private void listenDataAsListValueChange() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
		}
		if (getComponent().getData() != null && getComponent().getData().isValid()) {
			listenerToDataAsListValue = new BindingValueListChangeListener<T, List<T>>(((DataBinding) getComponent().getData()),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<T> newValue) {
					//System.out
					//		.println(" bindingValueChanged() detected for data list="
					//				+ (getComponent() != null ? getComponent().getData() : "") + " with newValue=" + newValue + " source="
					//				+ source);
					updateData();
					updateMultipleValues();
					rebuildCheckboxes();
				}
			};
		}
	}

	@Override
	public boolean update() {
		super.update();
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.refreshObserving();
		}
		return true;
	}

	private boolean containsObject(Object object) {
		if (selectedValues == null) {
			return false;
		} else {
			return selectedValues.contains(object);
		}
	}

	@Override
	protected FIBMultipleValueModel<T> createMultipleValueModel() {
		return new FIBMultipleValueModel<T>();
	}

	@Override
	protected void proceedToListModelUpdate() {
		rebuildCheckboxes();
	}

	final protected void rebuildCheckboxes() {
		if (getWidget() == null) {
			return;
		}
		if (panel != null) {
			panel.removeAll();
			((GridLayout) panel.getLayout()).setColumns(getWidget().getColumns());
			((GridLayout) panel.getLayout()).setHgap(getWidget().getHGap());
			((GridLayout) panel.getLayout()).setVgap(getWidget().getVGap());
			checkboxesArray = new JCheckBox[getMultipleValueModel().getSize()];
			labelsArray = new JLabel[getMultipleValueModel().getSize()];

			for (int i = 0; i < getMultipleValueModel().getSize(); i++) {
				T object = getMultipleValueModel().getElementAt(i);
				String text = getStringRepresentation(object);
				JCheckBox cb = new JCheckBox(text, containsObject(object));
				cb.setOpaque(false);
				cb.addActionListener(new CheckboxListener(cb, object, i));
				checkboxesArray[i] = cb;
				if (getWidget().getShowIcon() && getWidget().getIcon().isSet() && getWidget().getIcon().isValid()) {
					cb.setHorizontalAlignment(JCheckBox.LEFT);
					cb.setText(null);
					final JLabel label = new JLabel(text, getIconRepresentation(object), JLabel.LEADING);
					Dimension ps = cb.getPreferredSize();
					cb.setLayout(new BorderLayout());
					label.setLabelFor(cb);
					label.setBorder(BorderFactory.createEmptyBorder(0, ps.width, 0, 0));
					cb.add(label);
				}
				panel.add(cb);
			}
			updateFont();
			panel.revalidate();

			if ((getWidget().getData() == null || !getWidget().getData().isValid()) && getWidget().getAutoSelectFirstRow()
					&& getMultipleValueModel().getSize() > 0) {
				checkboxesArray[0].setSelected(true);
				List<T> newList = Collections.singletonList(getMultipleValueModel().getElementAt(0));
				setSelected(newList);
				setValue(newList);
			}

		}

	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		List<T> value = getValue();
		if (notEquals(value, selectedValues) /*|| listModelRequireChange()*/|| multipleValueModel == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}

			widgetUpdating = true;
			selectedValues = value;
			// rebuildCheckboxes();
			for (int i = 0; i < getMultipleValueModel().getSize(); i++) {
				T o = getMultipleValueModel().getElementAt(i);
				if (selectedValues != null) {
					checkboxesArray[i].setSelected(selectedValues.contains(o));
				} else {
					checkboxesArray[i].setSelected(false);
				}
			}
			setSelected(selectedValues);

			widgetUpdating = false;
			return true;
		}
		for (int i = 0; i < getMultipleValueModel().getSize(); i++) {
			T o = getMultipleValueModel().getElementAt(i);
			if (selectedValues != null) {
				checkboxesArray[i].setSelected(selectedValues.contains(o));
			} else {
				checkboxesArray[i].setSelected(false);
			}
		}
		return false;
	}

	private class CheckboxListener implements ActionListener {

		private final T value;
		private final JCheckBox cb;
		private final int index;

		public CheckboxListener(JCheckBox cb, T value, int index) {
			this.cb = cb;
			this.value = value;
			this.index = index;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cb) {
				if (cb.isSelected()) {
					if (!containsObject(value)) {
						if (selectedValues == null) {
							selectedValues = new ArrayList<T>();
						}
						selectedValues.add(value);
					}
				} else {
					if (containsObject(value)) {
						selectedValues.remove(value);
					}
				}
				updateModelFromWidget();
				setSelected(selectedValues);
				setSelectedIndex(index);
			}
		}

	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), selectedValues)) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + selectedValues);
			}
			if (!widgetUpdating) {
				setValue(selectedValues);
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
			for (JCheckBox cb : checkboxesArray) {
				cb.setFont(getFont());
			}
		}
		if (getWidget().getShowIcon() && getWidget().getIcon().isSet() && getWidget().getIcon().isValid()) {
			for (JLabel l : labelsArray) {
				if (l != null) {
					l.setFont(getFont());
				}
			}
		}
	}

	public JCheckBox getCheckboxAtIndex(int index) {
		if (index >= 0 && index < checkboxesArray.length) {
			return checkboxesArray[index];
		}
		return null;
	}
}
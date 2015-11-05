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
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.widget.FIBRadioButtonList;
import org.openflexo.fib.swing.view.SwingRenderingAdapter;
import org.openflexo.fib.swing.view.widget.JFIBRadioButtonListWidget.JRadioButtonPanel;
import org.openflexo.fib.view.impl.FIBViewImpl;
import org.openflexo.fib.view.widget.impl.FIBRadioButtonListWidgetImpl;

public class JFIBRadioButtonListWidget<T> extends FIBRadioButtonListWidgetImpl<JRadioButtonPanel<T>, T> {

	static final Logger LOGGER = Logger.getLogger(JFIBRadioButtonListWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingRadioButtonRenderingAdapter<T> extends SwingRenderingAdapter<JRadioButtonPanel<T>>
			implements RadioButtonRenderingAdapter<JRadioButtonPanel<T>, T> {

		@Override
		public T getSelectedItem(JRadioButtonPanel<T> component) {
			return component.getSelectedValue();
		}

		@Override
		public void setSelectedItem(JRadioButtonPanel<T> component, T item) {
			component.setSelectedValue(item);
		}

		@Override
		public int getSelectedIndex(JRadioButtonPanel<T> component) {
			return component.getSelectedIndex();
		}

		@Override
		public void setSelectedIndex(JRadioButtonPanel<T> component, int index) {
			component.setSelectedIndex(index);
		}

	}

	public JFIBRadioButtonListWidget(FIBRadioButtonList model, FIBController controller) {
		super(model, controller, new SwingRadioButtonRenderingAdapter<T>());
	}

	@Override
	protected JRadioButtonPanel<T> makeTechnologyComponent() {
		return new JRadioButtonPanel<T>(this);
	}

	@Override
	protected void proceedToListModelUpdate() {
		getTechnologyComponent().update();
		if (!widgetUpdating && !isDeleted() && getTechnologyComponent() != null) {
			updateWidgetFromModel();
		}
	}

	public static class JRadioButtonPanel<T> extends JPanel {

		private T selectedValue;
		private ButtonGroup buttonGroup;
		private JRadioButton[] radioButtonArray;
		private final JFIBRadioButtonListWidget<T> widget;

		public JRadioButtonPanel(JFIBRadioButtonListWidget<T> widget) {
			super(new GridLayout(0, widget.getWidget().getColumns(), widget.getWidget().getHGap(), widget.getWidget().getVGap()));
			setOpaque(false);
			this.widget = widget;
			rebuildRadioButtons();
		}

		public void update() {
			removeAll();
			((GridLayout) getLayout()).setColumns(widget.getWidget().getColumns());
			((GridLayout) getLayout()).setHgap(widget.getWidget().getHGap());
			((GridLayout) getLayout()).setVgap(widget.getWidget().getVGap());
			// TODO: remove listeners !!!
			rebuildRadioButtons();
		}

		private void rebuildRadioButtons() {
			buttonGroup = new ButtonGroup();
			radioButtonArray = new JRadioButton[widget.getMultipleValueModel().getSize()];
			for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
				T object = widget.getMultipleValueModel().getElementAt(i);
				JRadioButton rb = new JRadioButton(widget.getStringRepresentation(object), FIBViewImpl.equals(object, selectedValue));
				rb.setOpaque(false);
				rb.addActionListener(new RadioButtonListener(rb, object, i));
				radioButtonArray[i] = rb;
				// Handle the case of icon should be displayed
				if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
					rb.setHorizontalAlignment(JCheckBox.LEFT);
					rb.setText(null);
					final JLabel label = new JLabel(widget.getStringRepresentation(object), widget.getIconRepresentation(object),
							JLabel.LEADING);
					Dimension ps = rb.getPreferredSize();
					rb.setLayout(new BorderLayout());
					label.setLabelFor(rb);
					label.setBorder(BorderFactory.createEmptyBorder(0, ps.width, 0, 0));
					rb.add(label);
				}
				add(rb);
				buttonGroup.add(rb);
				if (object.equals(widget.getValue())) {
					rb.setSelected(true);
					// rb.doClick();
					selectedValue = widget.getValue();
				}
			}
			revalidate();
		}

		public T getSelectedValue() {
			return selectedValue;
		}

		public void setSelectedValue(T selectedValue) {
			if (selectedValue != null) {
				int newSelectedIndex = widget.getMultipleValueModel().indexOf(selectedValue);
				if (newSelectedIndex >= 0 && newSelectedIndex < widget.getMultipleValueModel().getSize()) {
					radioButtonArray[newSelectedIndex].doClick();
				}
			}
		}

		public int getSelectedIndex() {
			return widget.getMultipleValueModel().indexOf(getSelectedValue());
		}

		public void setSelectedIndex(int index) {
			setSelectedValue(widget.getMultipleValueModel().getElementAt(index));
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
					widget.fireSelectedValueChanged();
				}
			}
		}
	}

	protected void fireSelectedValueChanged() {
		updateModelFromWidget();
	}

	@Override
	public JPanel getJComponent() {
		return getTechnologyComponent();
	}

}

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

package org.openflexo.gina.swing.view.widget;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBRadioButtonListWidget.JRadioButtonPanel;
import org.openflexo.gina.view.impl.FIBViewImpl;
import org.openflexo.gina.view.widget.impl.FIBRadioButtonListWidgetImpl;

public class JFIBRadioButtonListWidget<T> extends FIBRadioButtonListWidgetImpl<JRadioButtonPanel<T>, T>
		implements JFIBView<FIBRadioButtonList, JRadioButtonPanel<T>> {

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
			if (component != null) {
				return component.getSelectedValue();
			}
			return null;
		}

		@Override
		public void setSelectedItem(JRadioButtonPanel<T> component, T item) {
			if (component != null) {
				component.setSelectedValue(item);
			}
		}

		@Override
		public int getSelectedIndex(JRadioButtonPanel<T> component) {
			if (component != null) {
				return component.getSelectedIndex();
			}
			return -1;
		}

		@Override
		public void setSelectedIndex(JRadioButtonPanel<T> component, int index) {
			if (component != null) {
				component.setSelectedIndex(index);
			}
		}

		@Override
		public Color getDefaultForegroundColor(JRadioButtonPanel<T> component) {
			return UIManager.getColor("RadioButton.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JRadioButtonPanel<T> component) {
			return UIManager.getColor("RadioButton.background");
		}

	}

	public JFIBRadioButtonListWidget(FIBRadioButtonList model, FIBController controller) {
		super(model, controller, new SwingRadioButtonRenderingAdapter<T>());
	}

	@Override
	public SwingRadioButtonRenderingAdapter<T> getRenderingAdapter() {
		return (SwingRadioButtonRenderingAdapter<T>) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	protected JRadioButtonPanel<T> makeTechnologyComponent() {
		return new JRadioButtonPanel<>(this);
	}

	@Override
	protected void proceedToListModelUpdate() {
		getTechnologyComponent().update();
	}

	public static class JRadioButtonPanel<T> extends JPanel {

		private T selectedValue;
		private ButtonGroup buttonGroup;
		private JRadioButton[] radioButtonArray;
		private JLabel[] labelsArray;
		private final JFIBRadioButtonListWidget<T> widget;

		public JRadioButtonPanel(JFIBRadioButtonListWidget<T> widget) {
			super(new GridBagLayout());
			setOpaque(false);
			this.widget = widget;
			rebuildRadioButtons();
		}

		public void update() {
			if (isRebuildingRadioButtons) {
				return;
			}
			removeAll();
			rebuildRadioButtons();
		}

		/*@Override
		public Dimension getPreferredSize() {
			if (widget.getComponent().getTrimText()) {
				for (JLabel l : labelsArray) {
					if (l != null) {
						System.out.println("label l: " + l.getPreferredSize() + " for " + l.getText());
					}
				}
				return getSize();
			}
			return super.getPreferredSize();
		}*/

		boolean isRebuildingRadioButtons = false;

		private void rebuildRadioButtons() {

			isRebuildingRadioButtons = true;

			buttonGroup = new ButtonGroup();

			radioButtonArray = new JRadioButton[widget.getMultipleValueModel().getSize()];
			labelsArray = new JLabel[widget.getMultipleValueModel().getSize()];

			for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
				T object = widget.getMultipleValueModel().getElementAt(i);
				JRadioButton rb = new JRadioButton("", FIBViewImpl.equals(object, selectedValue));
				rb.setVerticalAlignment(JCheckBox.TOP);
				rb.setOpaque(false);
				rb.addActionListener(new RadioButtonListener(rb, object, i));
				radioButtonArray[i] = rb;

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(0, 0, 0, 0);
				c.fill = GridBagConstraints.NONE;
				c.weightx = 0; // 1.0;
				c.gridwidth = 1;
				c.anchor = GridBagConstraints.NORTHEAST;
				add(rb, c);

				JLabel label = new JLabel(widget.getWidget().getTrimText() ? "<html>" + widget.getStringRepresentation(object) + "</html>"
						: widget.getStringRepresentation(object));
				labelsArray[i] = label;

				// Handle the case of icon should be displayed
				if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
					label.setIcon(widget.getIconRepresentation(object));
				}

				c.insets = new Insets(2, 5, 5, 5);
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.WEST;
				c.weightx = 1.0; // 2.0;
				c.gridwidth = GridBagConstraints.REMAINDER;

				add(label, c);

				buttonGroup.add(rb);
				if (object.equals(widget.getValue())) {
					rb.setSelected(true);
					// rb.doClick();
					selectedValue = widget.getValue();

					widget.selectionChanged();
					widget.setSelected(selectedValue);
					widget.setSelectedIndex(getSelectedIndex());

				}
			}
			revalidate();
			isRebuildingRadioButtons = false;
		}

		public T getSelectedValue() {
			return selectedValue;
		}

		public void setSelectedValue(T selectedValue) {
			if (selectedValue != null) {
				// System.out.println("on doit reselectionner " + selectedValue);
				int newSelectedIndex = widget.getMultipleValueModel().indexOf(selectedValue);
				// System.out.println("newSelectedIndex=" + newSelectedIndex);
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
					widget.selectionChanged();
				}
			}
		}
	}

	@Override
	protected void updateRadioButtonListLayout() {
		getTechnologyComponent().update();
	}

}

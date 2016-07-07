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
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
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
		return new JRadioButtonPanel<T>(this);
	}

	@Override
	protected void proceedToListModelUpdate() {
		getTechnologyComponent().update();
		/*if (!isUpdating() && !isDeleted() && getTechnologyComponent() != null) {
			// updateWidgetFromModel();
			updateData();
		}*/
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

		private boolean isLayouted = false;

		public void update() {
			removeAll();
			rebuildRadioButtons();
		}

		private void resizeWidthTo(int width) {
			FontMetrics fm = getFontMetrics(getFont());
			for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
				T object = widget.getMultipleValueModel().getElementAt(i);
				String labelText = widget.getStringRepresentation(object);
				if (labelText != null) {
					List<String> lines = trimString(labelText, width, fm);
					StringBuffer htmlText = new StringBuffer();
					htmlText.append("<html>");
					for (int j = 0; j < lines.size(); j++) {
						String line = lines.get(j);
						htmlText.append(line + (j == lines.size() - 1 ? "" : "<br>"));
					}
					htmlText.append("</html>");
					labelsArray[i].setText(htmlText.toString());
				}
			}
			revalidate();
			repaint();
		}

		private void rebuildRadioButtons() {

			isLayouted = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (!isLayouted && JRadioButtonPanel.this.widget.getWidget().getTrimText()) {
						isLayouted = true;
						resizeWidthTo(getSize().width - 50);
					}
				}
			});

			buttonGroup = new ButtonGroup();

			radioButtonArray = new JRadioButton[widget.getMultipleValueModel().getSize()];
			labelsArray = new JLabel[widget.getMultipleValueModel().getSize()];

			for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
				T object = widget.getMultipleValueModel().getElementAt(i);
				JRadioButton rb = new JRadioButton(widget.getWidget().getTrimText() ? "" : widget.getStringRepresentation(object),
						FIBViewImpl.equals(object, selectedValue));
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

				JLabel label = new JLabel();
				labelsArray[i] = label;

				// Handle the case of icon should be displayed
				if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
					label.setIcon(widget.getIconRepresentation(object));
				}

				c.insets = new Insets(2, 5, 5, 5);
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.CENTER;
				c.weightx = 1.0; // 2.0;
				c.gridwidth = GridBagConstraints.REMAINDER;

				add(label, c);

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

	/*protected void fireSelectedValueChanged() {
		selectionChanged();
	}*/

	@Override
	protected void updateRadioButtonListLayout() {
		getTechnologyComponent().update();
	}

}

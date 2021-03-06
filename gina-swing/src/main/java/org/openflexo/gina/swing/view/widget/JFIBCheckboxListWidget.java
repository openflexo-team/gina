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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBCheckboxListWidget.JCheckBoxListPanel;
import org.openflexo.gina.view.widget.impl.FIBCheckboxListWidgetImpl;

public class JFIBCheckboxListWidget<T> extends FIBCheckboxListWidgetImpl<JCheckBoxListPanel<T>, T>
		implements JFIBView<FIBCheckboxList, JCheckBoxListPanel<T>> {

	static final Logger logger = Logger.getLogger(JFIBCheckboxListWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingCheckboxListRenderingAdapter<T> extends SwingRenderingAdapter<JCheckBoxListPanel<T>>
			implements CheckboxListRenderingAdapter<JCheckBoxListPanel<T>, T> {

		@Override
		public List<T> getSelectedItems(JCheckBoxListPanel<T> component) {
			return component.getSelectedValues();
		}

		@Override
		public void setSelectedItems(JCheckBoxListPanel<T> component, List<T> items) {
			component.setSelectedValues(items);
		}

		@Override
		public Color getDefaultForegroundColor(JCheckBoxListPanel<T> component) {
			return UIManager.getColor("CheckBox.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JCheckBoxListPanel<T> component) {
			return UIManager.getColor("CheckBox.background");
		}

	}

	public JFIBCheckboxListWidget(FIBCheckboxList model, FIBController controller) {
		super(model, controller, new SwingCheckboxListRenderingAdapter<T>());

	}

	@Override
	public SwingCheckboxListRenderingAdapter<T> getRenderingAdapter() {
		return (SwingCheckboxListRenderingAdapter<T>) super.getRenderingAdapter();
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
	protected JCheckBoxListPanel<T> makeTechnologyComponent() {
		return new JCheckBoxListPanel<>(this);
	}

	@Override
	protected void proceedToListModelUpdate() {
		getTechnologyComponent().update();
	}

	public static class JCheckBoxListPanel<T> extends JPanel {
		private JCheckBox[] checkboxesArray;
		private JLabel[] labelsArray;
		private final JFIBCheckboxListWidget<T> widget;
		private List<T> selectedValues;

		public JCheckBoxListPanel(JFIBCheckboxListWidget<T> widget) {
			super(new GridBagLayout());
			setOpaque(false);
			this.widget = widget;
			rebuildCheckboxes();
		}

		public List<T> getSelectedValues() {
			return selectedValues;
		}

		public void setSelectedValues(List<T> selectedValues) {
			if ((selectedValues == null && this.selectedValues != null)
					|| (selectedValues != null && !selectedValues.equals(this.selectedValues))) {
				this.selectedValues = selectedValues;
				for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
					T o = widget.getMultipleValueModel().getElementAt(i);
					if (selectedValues != null) {
						checkboxesArray[i].setSelected(selectedValues.contains(o));
					}
					else {
						checkboxesArray[i].setSelected(false);
					}
				}
			}
		}

		public void update() {
			removeAll();
			// TODO: remove listeners !!!
			rebuildCheckboxes();
		}

		final protected void rebuildCheckboxes() {

			checkboxesArray = new JCheckBox[widget.getMultipleValueModel().getSize()];
			labelsArray = new JLabel[widget.getMultipleValueModel().getSize()];

			for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
				T object = widget.getMultipleValueModel().getElementAt(i);
				// We create checkboxes without text
				// Text will be assigned to checkboxes during the first ComponentResize event (see constructor)
				JCheckBox cb = new JCheckBox("", containsObject(object));
				cb.setOpaque(false);
				cb.addActionListener(new CheckboxListener(cb, object, i));
				checkboxesArray[i] = cb;

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(0, 0, 0, 0);
				c.fill = GridBagConstraints.NONE;
				c.weightx = 0; // 1.0;
				c.gridwidth = 1;
				c.anchor = GridBagConstraints.NORTHEAST;
				add(cb, c);

				JLabel label = new JLabel(widget.getWidget().getTrimText() ? "<html>" + widget.getStringRepresentation(object) + "</html>"
						: widget.getStringRepresentation(object));
				labelsArray[i] = label;

				// Handle the case of icon should be displayed
				if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
					label.setIcon(widget.getIconRepresentation(object));
				}

				// c.insets = new Insets(2, 5, 5, 5);
				c.insets = new Insets(0, 0, 0, 0);
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.WEST;
				c.weightx = 1.0; // 2.0;
				c.gridwidth = GridBagConstraints.REMAINDER;

				add(label, c);

			}
			revalidate();

			if ((widget.getWidget().getData() == null || !widget.getWidget().getData().isValid())
					&& widget.getWidget().getAutoSelectFirstRow() && widget.getMultipleValueModel().getSize() > 0) {
				checkboxesArray[0].setSelected(true);
				List<T> newList = Collections.singletonList(widget.getMultipleValueModel().getElementAt(0));
				widget.setSelected(newList);
				widget.setValue(newList);
			}

		}

		@Override
		public void setFont(Font font) {
			super.setFont(font);
			if (checkboxesArray != null) {
				for (JCheckBox cb : checkboxesArray) {
					cb.setFont(getFont());
				}
			}
			if (widget != null) {
				if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
					if (labelsArray != null) {
						for (JLabel l : labelsArray) {
							if (l != null) {
								l.setFont(getFont());
							}
						}
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

		private boolean containsObject(Object object) {
			if (selectedValues == null) {
				return false;
			}
			else {
				return selectedValues.contains(object);
			}
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
								selectedValues = new ArrayList<>();
							}
							selectedValues.add(value);
						}
					}
					else {
						if (containsObject(value)) {
							selectedValues.remove(value);
						}
					}

					widget.selectionChanged();
					widget.setSelected(selectedValues);
					widget.setSelectedIndex(index);

					widget.performValueChangedAction();
				}
			}

		}

	}

	@Override
	protected void updateCheckboxListLayout() {
		getTechnologyComponent().update();
	}

	public JCheckBox getCheckboxAtIndex(int index) {
		return getTechnologyComponent().getCheckboxAtIndex(index);
	}

}

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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.swing.view.widget.JFIBCheckboxListWidget.JCheckBoxListPanel;
import org.openflexo.fib.view.widget.impl.FIBCheckboxListWidgetImpl;

public class JFIBCheckboxListWidget<T> extends FIBCheckboxListWidgetImpl<JCheckBoxListPanel<T>, T> {

	static final Logger logger = Logger.getLogger(JFIBCheckboxListWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingTechnologyAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingCheckboxListRenderingTechnologyAdapter<T> extends SwingRenderingTechnologyAdapter<JCheckBoxListPanel<T>>
			implements CheckboxListRenderingTechnologyAdapter<JCheckBoxListPanel<T>, T> {

		@Override
		public List<T> getSelectedItems(JCheckBoxListPanel<T> component) {
			return component.getSelectedValues();
		}

		@Override
		public void setSelectedItems(JCheckBoxListPanel<T> component, List<T> items) {
			component.setSelectedValues(items);
		}

	}

	public JFIBCheckboxListWidget(FIBCheckboxList model, FIBController controller) {
		super(model, controller, new SwingCheckboxListRenderingTechnologyAdapter<T>());

	}

	@Override
	protected JCheckBoxListPanel<T> makeTechnologyComponent() {
		return new JCheckBoxListPanel<>(this);
	}

	@Override
	protected void proceedToListModelUpdate() {
		getDynamicJComponent().update();
	}

	@Override
	public JPanel getJComponent() {
		return getDynamicJComponent();
	}

	public static class JCheckBoxListPanel<T> extends JPanel {
		private JCheckBox[] checkboxesArray;
		private JLabel[] labelsArray;
		private final JFIBCheckboxListWidget<T> widget;
		private List<T> selectedValues;

		public JCheckBoxListPanel(JFIBCheckboxListWidget<T> widget) {
			super(new GridLayout(0, widget.getWidget().getColumns(), widget.getWidget().getHGap(), widget.getWidget().getVGap()));
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
			((GridLayout) getLayout()).setColumns(widget.getWidget().getColumns());
			((GridLayout) getLayout()).setHgap(widget.getWidget().getHGap());
			((GridLayout) getLayout()).setVgap(widget.getWidget().getVGap());
			// TODO: remove listeners !!!
			rebuildCheckboxes();
		}

		final protected void rebuildCheckboxes() {
			checkboxesArray = new JCheckBox[widget.getMultipleValueModel().getSize()];
			labelsArray = new JLabel[widget.getMultipleValueModel().getSize()];

			for (int i = 0; i < widget.getMultipleValueModel().getSize(); i++) {
				T object = widget.getMultipleValueModel().getElementAt(i);
				String text = widget.getStringRepresentation(object);
				JCheckBox cb = new JCheckBox(text, containsObject(object));
				cb.setOpaque(false);
				cb.addActionListener(new CheckboxListener(cb, object, i));
				checkboxesArray[i] = cb;
				if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
					cb.setHorizontalAlignment(JCheckBox.LEFT);
					cb.setText(null);
					final JLabel label = new JLabel(text, widget.getIconRepresentation(object), JLabel.LEADING);
					Dimension ps = cb.getPreferredSize();
					cb.setLayout(new BorderLayout());
					label.setLabelFor(cb);
					label.setBorder(BorderFactory.createEmptyBorder(0, ps.width, 0, 0));
					cb.add(label);
				}
				add(cb);
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
			for (JCheckBox cb : checkboxesArray) {
				cb.setFont(getFont());
			}
			if (widget.getWidget().getShowIcon() && widget.getWidget().getIcon().isSet() && widget.getWidget().getIcon().isValid()) {
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
								selectedValues = new ArrayList<T>();
							}
							selectedValues.add(value);
						}
					}
					else {
						if (containsObject(value)) {
							selectedValues.remove(value);
						}
					}
					widget.updateModelFromWidget();
					widget.setSelected(selectedValues);
					widget.setSelectedIndex(index);
				}
			}

		}

	}
}

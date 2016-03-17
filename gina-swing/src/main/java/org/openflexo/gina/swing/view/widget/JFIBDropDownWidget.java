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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBDropDownWidget.JDropDownPanel;
import org.openflexo.gina.view.widget.impl.FIBDropDownWidgetImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation for a widget able to select an item in a list (combo box)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this view
 * 
 * @author sylvain
 */
public class JFIBDropDownWidget<T> extends FIBDropDownWidgetImpl<JDropDownPanel<T>, T>
		implements FocusListener, JFIBView<FIBDropDown, JDropDownPanel<T>> {

	static final Logger logger = Logger.getLogger(JFIBDropDownWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingDropDownRenderingAdapter<T> extends SwingRenderingAdapter<JDropDownPanel<T>>
			implements DropDownRenderingAdapter<JDropDownPanel<T>, T> {

		@Override
		public T getSelectedItem(JDropDownPanel<T> component) {
			return (T) component.jComboBox.getSelectedItem();
		}

		@Override
		public void setSelectedItem(JDropDownPanel<T> component, T item) {
			component.jComboBox.setSelectedItem(item);
		}

		@Override
		public int getSelectedIndex(JDropDownPanel<T> component) {
			return component.jComboBox.getSelectedIndex();
		}

		@Override
		public void setSelectedIndex(JDropDownPanel<T> component, int index) {
			component.jComboBox.setSelectedIndex(index);
		}

	}

	public static class JDropDownPanel<T> extends JPanel {
		private final JButton resetButton;
		private final JComboBox<T> jComboBox;

		public JDropDownPanel(final JFIBDropDownWidget<T> widget) {
			super(new BorderLayout());
			resetButton = new JButton();
			resetButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "reset", resetButton));
			resetButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jComboBox.getModel().setSelectedItem(null);
					widget.setValue(null);
				}
			});
			if (!ToolBox.isMacOSLaf()) {
				setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
						RIGHT_COMPENSATING_BORDER));
			}

			setOpaque(false);
			addFocusListener(widget);

			widget.multipleValueModel = null;
			jComboBox = new JComboBox<T>(widget.getListModel());
			jComboBox.setFont(widget.getFont());
			jComboBox.setRenderer(widget.getListCellRenderer());
			jComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Action performed in " + this.getClass().getName());
					}
					// widget.updateModelFromWidget();
					widget.selectionChanged();
				}
			});

			add(jComboBox, BorderLayout.CENTER);
			if (widget.getComponent().getShowReset()) {
				add(resetButton, BorderLayout.EAST);
			}

		}

		public JComboBox<T> getJComboBox() {
			return jComboBox;
		}
	}

	public JFIBDropDownWidget(FIBDropDown model, FIBController controller) {
		super(model, controller, new SwingDropDownRenderingAdapter<T>());
	}

	@Override
	public SwingDropDownRenderingAdapter<T> getRenderingAdapter() {
		return (SwingDropDownRenderingAdapter<T>) super.getRenderingAdapter();
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
	protected JDropDownPanel<T> makeTechnologyComponent() {
		return new JDropDownPanel(this);
	}

	/*@Override
	public T updateData() {
		T newValue = super.updateData();
		if (newValue == null && getWidget().getAutoSelectFirstRow() && getListModel().getSize() > 0) {
			getTechnologyComponent().jComboBox.setSelectedIndex(0);
			newValue = getListModel().getElementAt(0);
		}
		return newValue;
	}*/

	@Override
	protected void proceedToListModelUpdate() {
		if (getTechnologyComponent() != null) {
			getTechnologyComponent().jComboBox.setModel(getListModel());
			// System.out.println("New list model = " + getListModel());
			/*if (!isUpdating() && !isDeleted() && getTechnologyComponent() != null) {
				updateData();
			}*/
		}
	}

}

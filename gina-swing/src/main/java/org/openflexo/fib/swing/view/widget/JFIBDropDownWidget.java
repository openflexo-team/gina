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
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.view.widget.impl.FIBDropDownWidgetImpl;
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
public class JFIBDropDownWidget<T> extends FIBDropDownWidgetImpl<JComboBox<T>, T>implements FocusListener {

	static final Logger logger = Logger.getLogger(JFIBDropDownWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingTechnologyAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingDropDownRenderingTechnologyAdapter<T> extends SwingRenderingTechnologyAdapter<JComboBox<T>>
			implements DropDownRenderingTechnologyAdapter<JComboBox<T>> {

		@Override
		public Object getSelectedItem(JComboBox<T> component) {
			return component.getSelectedItem();
		}

		@Override
		public void setSelectedItem(JComboBox<T> component, Object item) {
			component.setSelectedItem(item);
		}

		@Override
		public int getSelectedIndex(JComboBox<T> component) {
			return component.getSelectedIndex();
		}

		@Override
		public void setSelectedIndex(JComboBox<T> component, int index) {
			component.setSelectedIndex(index);
		}

	}

	private final JButton resetButton;
	private final JPanel dropdownPanel;

	public JFIBDropDownWidget(FIBDropDown model, FIBController controller) {
		super(model, controller, new SwingDropDownRenderingTechnologyAdapter<T>());

		dropdownPanel = new JPanel(new BorderLayout());
		resetButton = new JButton();
		resetButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "reset", resetButton));
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getDynamicJComponent().getModel().setSelectedItem(null);
				setValue(null);
			}
		});
		if (!ToolBox.isMacOSLaf()) {
			dropdownPanel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}

		dropdownPanel.add(getDynamicJComponent(), BorderLayout.CENTER);
		if (model.getShowReset()) {
			dropdownPanel.add(resetButton, BorderLayout.EAST);
		}
		dropdownPanel.setOpaque(false);
		dropdownPanel.addFocusListener(this);

		updateFont();

	}

	@Override
	protected JComboBox<T> makeTechnologyComponent() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("initJComboBox()");
		}
		JComboBox<T> jComboBox;
		Point locTemp = null;
		Container parentTemp = null;
		if (getJComponent() != null && getJComponent().getParent() != null) {
			locTemp = getJComponent().getLocation();
			parentTemp = getJComponent().getParent();
			parentTemp.remove(getJComponent());
			parentTemp.remove(resetButton);
		}
		multipleValueModel = null;
		jComboBox = new JComboBox<T>(getListModel());
		/*if (getDataObject() == null) {
			Vector<Object> defaultValue = new Vector<Object>();
			defaultValue.add(FlexoLocalization.localizedForKey("no_selection"));
			_jComboBox = new JComboBox(defaultValue);
		} else {
			// TODO: Verify that there is no reason for this comboBoxModel to be cached.
			multipleValueModel=null;
			_jComboBox = new JComboBox(getListModel());
		}*/
		jComboBox.setFont(getFont());
		jComboBox.setRenderer(getListCellRenderer());
		jComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Action performed in " + this.getClass().getName());
				}
				updateModelFromWidget();
			}
		});
		if (parentTemp != null) {
			// _jComboBox.setSize(dimTemp);
			jComboBox.setLocation(locTemp);
			((JPanel) parentTemp).add(jComboBox, BorderLayout.CENTER);
			if (getWidget().getShowReset()) {
				((JPanel) parentTemp).add(resetButton, BorderLayout.EAST);
			}
		}
		// Important: otherwise might be desynchronized
		jComboBox.revalidate();

		if ((getWidget().getData() == null || !getWidget().getData().isValid()) && getWidget().getAutoSelectFirstRow()
				&& getListModel().getSize() > 0) {
			jComboBox.setSelectedIndex(0);
		}
		jComboBox.setEnabled(isComponentEnabled());

		return jComboBox;
	}

	@Override
	protected void proceedToListModelUpdate() {
		if (getDynamicJComponent() != null) {
			getDynamicJComponent().setModel(getListModel());
			// System.out.println("New list model = " + getListModel());
			if (!widgetUpdating && !isDeleted() && getDynamicJComponent() != null) {
				updateWidgetFromModel();
			}
		}
	}

	@Override
	public JPanel getJComponent() {
		return dropdownPanel;
	}

}

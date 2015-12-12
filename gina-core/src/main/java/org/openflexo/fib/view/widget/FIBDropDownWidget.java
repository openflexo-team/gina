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

package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

public class FIBDropDownWidget<T> extends FIBMultipleValueWidget<FIBDropDown, JComboBox, T, T> {

	static final Logger logger = Logger.getLogger(FIBDropDownWidget.class.getPackage().getName());

	private final JButton resetButton;

	private final JPanel dropdownPanel;

	protected JComboBox jComboBox;

	public FIBDropDownWidget(FIBDropDown model, FIBController controller) {
		super(model, controller);
		initJComboBox();
		dropdownPanel = new JPanel(new BorderLayout());
		resetButton = new JButton();
		resetButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "reset", resetButton));
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jComboBox.getModel().setSelectedItem(null);
				setValue(null);
			}
		});
		if (!ToolBox.isMacOSLaf()) {
			dropdownPanel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}

		dropdownPanel.add(jComboBox, BorderLayout.CENTER);
		if (model.getShowReset()) {
			dropdownPanel.add(resetButton, BorderLayout.EAST);
		}
		dropdownPanel.setOpaque(false);
		dropdownPanel.addFocusListener(this);

		updateFont();

	}

	final protected void initJComboBox() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("initJComboBox()");
		}
		Point locTemp = null;
		Container parentTemp = null;
		if (jComboBox != null && jComboBox.getParent() != null) {
			locTemp = jComboBox.getLocation();
			parentTemp = jComboBox.getParent();
			parentTemp.remove(jComboBox);
			parentTemp.remove(resetButton);
		}
		multipleValueModel = null;
		jComboBox = new JComboBox(getListModel());
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
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {

		// System.out.println("getValue()=" + getValue());
		// System.out.println("jComboBox.getSelectedItem()=" + jComboBox.getSelectedItem());
		// System.out.println("listModelRequireChange()=" + listModelRequireChange());

		if (notEquals(getValue(), jComboBox.getSelectedItem()) /*|| listModelRequireChange()*/) {

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}
			widgetUpdating = true;
			// initJComboBox();
			jComboBox.setSelectedItem(getValue());

			widgetUpdating = false;

			if (getValue() == null && getWidget().getAutoSelectFirstRow() && getListModel().getSize() > 0) {
				updateMultipleValues();
				jComboBox.setSelectedIndex(0);
			}

			return true;
		}

		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (widgetUpdating || modelUpdating) {
			return false;
		}
		if (notEquals(getValue(), jComboBox.getSelectedItem())) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + jComboBox.getSelectedItem());
			}
			if (jComboBox.getSelectedItem() != null && !widgetUpdating) {
				setValue((T) jComboBox.getSelectedItem());
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	public MyComboBoxModel getListModel() {
		return (MyComboBoxModel) getMultipleValueModel();
	}

	@Override
	protected MyComboBoxModel createMultipleValueModel() {
		return new MyComboBoxModel(getValue());
	}

	@Override
	protected void proceedToListModelUpdate() {
		if (jComboBox != null) {
			jComboBox.setModel(getListModel());
			// System.out.println("New list model = " + getListModel());
			if (!widgetUpdating && !isDeleted() && getDynamicJComponent() != null) {
				updateWidgetFromModel();
			}
		}
	}

	/*@Override
	protected MyComboBoxModel updateListModelWhenRequired() {
		if (multipleValueModel == null) {
			multipleValueModel = new MyComboBoxModel(getValue());
			if (jComboBox != null) {
				jComboBox.setModel((MyComboBoxModel) multipleValueModel);
			}
		} else {
			MyComboBoxModel aNewMyComboBoxModel = new MyComboBoxModel(getValue());
			if (!aNewMyComboBoxModel.equals(multipleValueModel)) {
				multipleValueModel = aNewMyComboBoxModel;
				jComboBox.setModel((MyComboBoxModel) multipleValueModel);
			}
		}
		return (MyComboBoxModel) multipleValueModel;
	}*/

	protected class MyComboBoxModel extends FIBMultipleValueModel implements ComboBoxModel {
		protected Object selectedItem = null;

		public MyComboBoxModel(Object selectedObject) {
			super();
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if (selectedItem != anItem) {
				widgetUpdating = true;
				selectedItem = anItem;
				// logger.info("setSelectedItem() with " + anItem + " widgetUpdating=" + widgetUpdating + " modelUpdating=" +
				// modelUpdating);
				setSelected((T) anItem);
				setSelectedIndex(indexOf(anItem));
				/*if (!widgetUpdating && !modelUpdating) {
					notifyDynamicModelChanged();
				}*/
				widgetUpdating = false;
			}
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		@Override
		public int hashCode() {
			return selectedItem == null ? 0 : selectedItem.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof FIBDropDownWidget.MyComboBoxModel) {
				if (selectedItem != ((MyComboBoxModel) object).selectedItem) {
					return false;
				}
			}
			return super.equals(object);
		}

	}

	@Override
	public JPanel getJComponent() {
		return dropdownPanel;
	}

	@Override
	public JComboBox getDynamicJComponent() {
		return jComboBox;
	}

	@Override
	public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			jComboBox.setFont(getFont());
		}
	}

}

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

package org.openflexo.gina.view.widget.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.view.widget.FIBDropDownWidget;

/**
 * Default base implementation for a widget able to select an item in a list (combo box)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this view
 * 
 * @author sylvain
 */
public abstract class FIBDropDownWidgetImpl<C, T> extends FIBMultipleValueWidgetImpl<FIBDropDown, C, T, T>
		implements FIBDropDownWidget<C, T> {

	static final Logger logger = Logger.getLogger(FIBDropDownWidgetImpl.class.getPackage().getName());

	public FIBDropDownWidgetImpl(FIBDropDown model, FIBController controller, DropDownRenderingAdapter<C, T> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
	}

	@Override
	public DropDownRenderingAdapter<C, T> getRenderingAdapter() {
		return (DropDownRenderingAdapter<C, T>) super.getRenderingAdapter();
	}

	@Override
	public T updateData() {

		T newValue = super.updateData();

		if (newValue == null && getWidget().getAutoSelectFirstRow() && getListModel().getSize() > 0) {

			// System.out.println("Selecting first value of" + getWidget().getName() + " : " + getListModel().getElementAt(0));

			newValue = getListModel().getElementAt(0);
			setValue(newValue);
		}

		if (notEquals(newValue, getRenderingAdapter().getSelectedItem(getTechnologyComponent())) /*|| listModelRequireChange()*/) {

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}
			// widgetUpdating = true;
			getRenderingAdapter().setSelectedItem(getTechnologyComponent(), newValue);

			// widgetUpdating = false;

			// return true;
		}

		// return false;
		return newValue;
	}

	private boolean modelUpdating = false;

	/**
	 * Update the model given the actual state of the widget
	 */
	protected void selectionChanged() {
		if (isUpdating() || modelUpdating) {
			return;
		}
		if (notEquals(getValue(), getRenderingAdapter().getSelectedItem(getTechnologyComponent()))) {
			modelUpdating = true;
			T newValue = getRenderingAdapter().getSelectedItem(getTechnologyComponent());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + newValue);
			}
			if (newValue != null && !isUpdating()) {
				setValue(newValue);
			}
			modelUpdating = false;
			// return true;
		}
		// return false;
	}

	public MyComboBoxModel getListModel() {
		return (MyComboBoxModel) getMultipleValueModel();
	}

	@Override
	protected MyComboBoxModel createMultipleValueModel() {
		return new MyComboBoxModel(getValue());
	}

	@SuppressWarnings("serial")
	protected class MyComboBoxModel extends FIBMultipleValueModelImpl implements ComboBoxModel<T> {
		protected Object selectedItem = null;

		public MyComboBoxModel(Object selectedObject) {
			super();
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if (selectedItem != anItem) {
				// widgetUpdating = true;
				selectedItem = anItem;
				// logger.info("setSelectedItem() with " + anItem + " widgetUpdating=" + widgetUpdating + " modelUpdating=" +
				// modelUpdating);
				setSelected((T) anItem);
				setSelectedIndex(indexOf(anItem));
				/*if (!widgetUpdating && !modelUpdating) {
					notifyDynamicModelChanged();
				}*/
				// widgetUpdating = false;
			}
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		/*@Override
		public int hashCode() {
			return selectedItem == null ? 0 : selectedItem.hashCode();
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof FIBDropDownWidgetImpl.MyComboBoxModel) {
				if (selectedItem != ((MyComboBoxModel) object).selectedItem) {
					return false;
				}
			}
			return super.equals(object);
		}*/

	}

	@Override
	public T getSelectedItem() {
		return getRenderingAdapter().getSelectedItem(getTechnologyComponent());
	}

	@Override
	public void setSelectedItem(T item) {
		getRenderingAdapter().setSelectedItem(getTechnologyComponent(), item);
	}

}

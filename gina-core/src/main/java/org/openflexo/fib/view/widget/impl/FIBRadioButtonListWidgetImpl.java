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

package org.openflexo.fib.view.widget.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.widget.FIBRadioButtonList;
import org.openflexo.fib.view.widget.FIBRadioButtonListWidget;

/**
 * Base implementation for a widget able to select an item in a radio button panel
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this view
 * 
 * @author sylvain
 */
public abstract class FIBRadioButtonListWidgetImpl<C, T> extends FIBMultipleValueWidgetImpl<FIBRadioButtonList, C, T, T>
		implements FIBRadioButtonListWidget<C, T> {

	static final Logger LOGGER = Logger.getLogger(FIBRadioButtonListWidgetImpl.class.getPackage().getName());

	public FIBRadioButtonListWidgetImpl(FIBRadioButtonList model, FIBController controller,
			RadioButtonRenderingAdapter<C, T> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
	}

	@Override
	public RadioButtonRenderingAdapter<C, T> getRenderingAdapter() {
		return (RadioButtonRenderingAdapter<C, T>) super.getRenderingAdapter();
	}

	@Override
	public boolean update() {
		boolean returned = super.update();
		selectFirstRowIfRequired();
		return returned;
	}

	private void selectFirstRowIfRequired() {
		if (getSelectedValue() == null && getWidget().getAutoSelectFirstRow() && getMultipleValueModel().getSize() > 0) {
			setSelectedValue(getMultipleValueModel().getElementAt(0));
		}
	}

	@Override
	protected FIBMultipleValueModel<T> createMultipleValueModel() {
		return new FIBMultipleValueModelImpl();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		T value = getValue();
		if (notEquals(value, getSelectedValue()) /*|| listModelRequireChange()*/ || multipleValueModel == null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateWidgetFromModel()");
			}

			widgetUpdating = true;
			setSelectedValue(value);
			setSelected(value);
			setSelectedIndex(getMultipleValueModel().indexOf(value));

			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), getSelectedValue())) {
			modelUpdating = true;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateModelFromWidget with " + getSelectedValue());
			}
			if (getSelectedValue() != null && !widgetUpdating) {
				setValue(getSelectedValue());
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	public T getSelectedValue() {
		return getRenderingAdapter().getSelectedItem(getTechnologyComponent());
	}

	public void setSelectedValue(T selectedValue) {
		getRenderingAdapter().setSelectedItem(getTechnologyComponent(), selectedValue);
	}
}

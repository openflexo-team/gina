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

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBMultipleValues;
import org.openflexo.gina.view.widget.FIBCheckboxListWidget;

public abstract class FIBCheckboxListWidgetImpl<C, T> extends FIBMultipleValueWidgetImpl<FIBCheckboxList, C, List<T>, T>
		implements FIBCheckboxListWidget<C, T> {

	static final Logger logger = Logger.getLogger(FIBCheckboxListWidgetImpl.class.getPackage().getName());

	private BindingValueListChangeListener<T, List<T>> listenerToDataAsListValue;

	public FIBCheckboxListWidgetImpl(FIBCheckboxList model, FIBController controller, CheckboxListRenderingAdapter<C, T> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
	}

	@Override
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		listenDataAsListValueChange();
	}

	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenDataAsListValueChange();
	}

	@Override
	public CheckboxListRenderingAdapter<C, T> getRenderingAdapter() {
		return (CheckboxListRenderingAdapter<C, T>) super.getRenderingAdapter();
	}

	private void listenDataAsListValueChange() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
		}
		if (getComponent().getData() != null && getComponent().getData().isValid()) {
			listenerToDataAsListValue = new BindingValueListChangeListener<T, List<T>>(((DataBinding) getComponent().getData()),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<T> newValue) {
					// System.out
					// .println(" bindingValueChanged() detected for data list="
					// + (getComponent() != null ? getComponent().getData() : "") + " with newValue=" + newValue + " source="
					// + source);
					updateData();
					updateMultipleValues();
				}
			};
		}
	}

	private void stopListenDataAsListValueChange() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
			listenerToDataAsListValue = null;
		}
	}

	@Override
	protected FIBMultipleValueModel<T> createMultipleValueModel() {
		return new FIBMultipleValueModelImpl();
	}

	@Override
	protected abstract void proceedToListModelUpdate();

	@Override
	public List<T> updateData() {
		List<T> newValue = super.updateData();
		if (notEquals(newValue, getSelectedValues()) /*|| listModelRequireChange()*/ || multipleValueModel == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}

			setSelectedValues(newValue);
		}
		return newValue;
	}

	private boolean modelUpdating = false;

	/**
	 * Update the model given the actual state of the widget
	 */
	protected boolean selectionChanged() {
		if (notEquals(getValue(), getSelectedValues())) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + getSelectedValues());
			}
			if (!isUpdating()) {
				setValue(getSelectedValues());
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	public List<T> getSelectedValues() {
		return getRenderingAdapter().getSelectedItems(getTechnologyComponent());
	}

	public void setSelectedValues(List<T> selectedValues) {
		getRenderingAdapter().setSelectedItems(getTechnologyComponent(), selectedValues);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ((evt.getPropertyName().equals(FIBWidget.FORMAT_KEY)) || (evt.getPropertyName().equals(FIBWidget.LOCALIZE_KEY))
				|| (evt.getPropertyName().equals(FIBWidget.ICON_KEY)) || (evt.getPropertyName().equals(FIBMultipleValues.SHOW_ICON_KEY))
				|| (evt.getPropertyName().equals(FIBMultipleValues.SHOW_TEXT_KEY))
				|| (evt.getPropertyName().equals(FIBCheckboxList.TRIM_TEXT_KEY))) {
			updateCheckboxListLayout();
		}
		super.propertyChange(evt);
	}

	protected abstract void updateCheckboxListLayout();

}

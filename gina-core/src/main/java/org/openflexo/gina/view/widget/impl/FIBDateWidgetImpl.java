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
import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBDate;
import org.openflexo.gina.model.widget.FIBFont;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBDateWidget;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Default base implementation for a widget able to select a {@link Date}
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBDateWidgetImpl<C> extends FIBWidgetViewImpl<FIBDate, C, Date> implements FIBDateWidget<C>, ApplyCancelListener {

	private static final Logger LOGGER = Logger.getLogger(FIBDateWidgetImpl.class.getPackage().getName());

	private Date revertValue;

	public FIBDateWidgetImpl(FIBDate model, FIBController controller, DateWidgetRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		// updateCheckboxVisibility();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateCheckboxVisibility();
		// updateWidgetFromModel();
	}

	@Override
	public DateWidgetRenderingAdapter<C> getRenderingAdapter() {
		return (DateWidgetRenderingAdapter<C>) super.getRenderingAdapter();
	}

	protected final void updateCheckboxVisibility() {
		getRenderingAdapter().setCheckboxVisible(getTechnologyComponent(), getWidget().getAllowsNull());
	}

	@Override
	public Date updateData() {
		Date newFont = super.updateData();
		Date editedObject = getSelectedDate();
		if (!getRenderingAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedObject = null;
		}
		if (notEquals(newFont, editedObject)) {
			// widgetUpdating = true;
			// try {
			getRenderingAdapter().setCheckboxSelected(getTechnologyComponent(), newFont != null);
			getRenderingAdapter().setCheckboxEnabled(getTechnologyComponent(),
					(newFont != null || !getWidget().getAllowsNull()) && isEnabled());
			setSelectedDate(newFont);
			// } finally {
			// widgetUpdating = false;
			// }
			// return true;
		}
		// return false;
		return newFont;
	}

	protected boolean fontChanged() {
		Date editedObject = null;
		if (getRenderingAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedObject = getSelectedDate();
		}
		if (notEquals(getValue(), editedObject)) {
			if (isReadOnly()) {
				return false;
			}
			// modelUpdating = true;
			// try {
			setValue(editedObject);
			// } finally {
			// modelUpdating = false;
			// }
			return true;
		}
		return false;
	}

	public Date getSelectedDate() {
		return getRenderingAdapter().getSelectedDate(getTechnologyComponent());
	}

	public void setSelectedDate(Date aDate) {
		getRenderingAdapter().setSelectedDate(getTechnologyComponent(), aDate);
	}

	@Override
	public void fireApplyPerformed() {
		GinaStackEvent stack = GENotifier
				.raise(FIBEventFactory.getInstance().createValueEvent(FIBValueEventDescription.CHANGED, getValue()));

		fontChanged();
		// updateModelFromWidget();

		stack.end();
	}

	@Override
	public void fireCancelPerformed() {
		setValue(revertValue);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBFont.ALLOWS_NULL_KEY)) {
			updateCheckboxVisibility();
		}
		super.propertyChange(evt);
	}
}

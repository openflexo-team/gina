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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBColor;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBColorWidget;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Default base implementation for a widget able to select a Color
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBColorWidgetImpl<C> extends FIBWidgetViewImpl<FIBColor, C, Color>
		implements FIBColorWidget<C>, ApplyCancelListener {

	private static final Logger logger = Logger.getLogger(FIBColorWidgetImpl.class.getPackage().getName());

	public FIBColorWidgetImpl(FIBColor model, FIBController controller, ColorWidgetRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		// updateCheckboxVisibility();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateCheckboxVisibility();
	}

	@Override
	public ColorWidgetRenderingAdapter<C> getRenderingAdapter() {
		return (ColorWidgetRenderingAdapter<C>) super.getRenderingAdapter();
	}

	protected final void updateCheckboxVisibility() {
		getRenderingAdapter().setCheckboxVisible(getTechnologyComponent(), getWidget().getAllowsNull());
	}

	@Override
	public void fireApplyPerformed() {

		GinaStackEvent stack = GENotifier.raise(FIBEventFactory.getInstance().createValueEvent(FIBValueEventDescription.CHANGED,
				getValue() != null ? getValue().getRGB() : null));

		// updateModelFromWidget();
		colorChanged();

		stack.end();
	}

	@Override
	public void fireCancelPerformed() {
		// Nothing to do, widget resets itself automatically and model has not been changed.
	}

	@Override
	public Color updateData() {
		Color newColor = super.updateData();
		Color editedObject = getSelectedColor();
		if (!getRenderingAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedObject = null;
		}
		if (notEquals(newColor, editedObject)) {
			// widgetUpdating = true;
			// try {
			getRenderingAdapter().setCheckboxSelected(getTechnologyComponent(), newColor != null);
			getRenderingAdapter().setCheckboxEnabled(getTechnologyComponent(),
					(newColor != null || !getWidget().getAllowsNull()) && isEnabled());
			setSelectedColor(newColor);
			/*} finally {
				widgetUpdating = false;
			}*/
			// return true;
		}
		// return false;
		return newColor;
	}

	protected boolean colorChanged() {
		Color editedObject = null;
		if (getRenderingAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedObject = getSelectedColor();
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

	public Color getSelectedColor() {
		return getRenderingAdapter().getSelectedColor(getTechnologyComponent());
	}

	public void setSelectedColor(Color aColor) {
		getRenderingAdapter().setSelectedColor(getTechnologyComponent(), aColor);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBColor.ALLOWS_NULL_KEY)) {
			updateCheckboxVisibility();
		}
		super.propertyChange(evt);
	}

}

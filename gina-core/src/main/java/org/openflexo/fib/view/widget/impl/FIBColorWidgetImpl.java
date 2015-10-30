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

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBColor;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBColorWidget;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Default base implementation for a widget able to select a Color
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBColorWidgetImpl<C> extends FIBWidgetViewImpl<FIBColor, C, Color>implements FIBColorWidget<C>, ApplyCancelListener {

	private static final Logger logger = Logger.getLogger(FIBColorWidgetImpl.class.getPackage().getName());

	public FIBColorWidgetImpl(FIBColor model, FIBController controller,
			ColorWidgetRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);
		updateCheckboxVisibility();
	}

	@Override
	public ColorWidgetRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (ColorWidgetRenderingTechnologyAdapter) super.getRenderingTechnologyAdapter();
	}

	public final void updateCheckboxVisibility() {
		getRenderingTechnologyAdapter().setCheckboxVisible(getDynamicJComponent(), getWidget().getAllowsNull());
	}

	@Override
	public void fireApplyPerformed() {
		GinaStackEvent stack = GENotifier
				.raise(FIBEventFactory.getInstance().createValueEvent(FIBValueEventDescription.CHANGED, getValue().getRGB()));

		updateModelFromWidget();

		stack.end();
	}

	@Override
	public void fireCancelPerformed() {
		// Nothing to do, widget resets itself automatically and model has not been changed.
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		Color editedObject = getColor();
		if (!getRenderingTechnologyAdapter().isCheckboxSelected(getDynamicJComponent())) {
			editedObject = null;
		}
		if (notEquals(getValue(), editedObject)) {
			widgetUpdating = true;
			try {
				getRenderingTechnologyAdapter().setCheckboxSelected(getDynamicJComponent(), getValue() != null);
				getRenderingTechnologyAdapter().setCheckboxEnabled(getDynamicJComponent(),
						(getValue() != null || !getWidget().getAllowsNull()) && isEnabled());
				setColor(getValue());
			} finally {
				widgetUpdating = false;
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
		Color editedObject = null;
		if (getRenderingTechnologyAdapter().isCheckboxSelected(getDynamicJComponent())) {
			editedObject = getColor();
		}
		if (notEquals(getValue(), editedObject)) {
			if (isReadOnly()) {
				return false;
			}
			modelUpdating = true;
			try {
				setValue(editedObject);
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	public Color getColor() {
		return getRenderingTechnologyAdapter().getColor(getDynamicJComponent());
	}

	protected void setColor(Color aColor) {
		getRenderingTechnologyAdapter().setColor(getDynamicJComponent(), aColor);
	}

}

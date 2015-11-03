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

import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBFont;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBFontWidget;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Default base implementation for a widget able to select a Font
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBFontWidgetImpl<C> extends FIBWidgetViewImpl<FIBFont, C, Font>implements FIBFontWidget<C>, ApplyCancelListener {

	private static final Logger LOGGER = Logger.getLogger(FIBFontWidgetImpl.class.getPackage().getName());

	private Font revertValue;

	public FIBFontWidgetImpl(FIBFont model, FIBController controller, FontWidgetRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);
		updateCheckboxVisibility();
	}

	@Override
	public FontWidgetRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (FontWidgetRenderingTechnologyAdapter) super.getRenderingTechnologyAdapter();
	}

	public final void updateCheckboxVisibility() {
		getRenderingTechnologyAdapter().setCheckboxVisible(getTechnologyComponent(), getWidget().getAllowsNull());
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		Font editedObject = getSelectedFont();
		if (!getRenderingTechnologyAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedObject = null;
		}
		if (notEquals(getValue(), editedObject)) {
			widgetUpdating = true;
			try {
				getRenderingTechnologyAdapter().setCheckboxSelected(getTechnologyComponent(), getValue() != null);
				getRenderingTechnologyAdapter().setCheckboxEnabled(getTechnologyComponent(),
						(getValue() != null || !getWidget().getAllowsNull()) && isEnabled());
				setSelectedFont(getValue());
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
		Font editedObject = null;
		if (getRenderingTechnologyAdapter().isCheckboxSelected(getTechnologyComponent())) {
			editedObject = getSelectedFont();
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

	public Font getSelectedFont() {
		return getRenderingTechnologyAdapter().getSelectedFont(getTechnologyComponent());
	}

	public void setSelectedFont(Font aFont) {
		getRenderingTechnologyAdapter().setSelectedFont(getTechnologyComponent(), aFont);
	}

	@Override
	public void fireApplyPerformed() {
		GinaStackEvent stack = GENotifier
				.raise(FIBEventFactory.getInstance().createValueEvent(FIBValueEventDescription.CHANGED, getValue().getFontName()));

		updateModelFromWidget();

		stack.end();
	}

	@Override
	public void fireCancelPerformed() {
		setValue(revertValue);
	}

}

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
import org.openflexo.fib.model.FIBTextWidget;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBGenericTextWidget;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBFocusEventDescription;
import org.openflexo.gina.event.description.FIBTextEventDescription;

/**
 * Default base implementation for a simple widget allowing to display/edit a String in a TextField
 * 
 * @author sylvain
 */
public abstract class FIBGenericTextWidgetImpl<F extends FIBTextWidget, C> extends FIBWidgetViewImpl<F, C, String>
		implements FIBGenericTextWidget<F, C> {

	private static final Logger logger = Logger.getLogger(FIBGenericTextWidgetImpl.class.getPackage().getName());

	protected boolean validateOnReturn;

	public FIBGenericTextWidgetImpl(F model, FIBController controller,
			GenericTextRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);

		validateOnReturn = model.isValidateOnReturn();

		updateEditable();
		updateText();
	}

	@Override
	public GenericTextRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (GenericTextRenderingTechnologyAdapter<C>) super.getRenderingTechnologyAdapter();
	}

	public void updateEditable() {
		getRenderingTechnologyAdapter().setEditable(getDynamicJComponent(), !isReadOnly());
	}

	public void updateText() {
		if (getWidget().getText() != null) {
			getRenderingTechnologyAdapter().setText(getDynamicJComponent(), getWidget().getText());
		}
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
			case FIBTextEventDescription.INSERTED: {
				FIBTextEventDescription ev = (FIBTextEventDescription) e;
				String text = getRenderingTechnologyAdapter().getText(getDynamicJComponent());
				if (text.length() >= ev.getPosition()) {
					getRenderingTechnologyAdapter().setText(getDynamicJComponent(),
							(text.substring(0, ev.getPosition()) + e.getValue() + text.substring(ev.getPosition())));
					getRenderingTechnologyAdapter().setCaretPosition(getDynamicJComponent(), ev.getPosition() + e.getValue().length());
				}
				break;
			}
			case FIBTextEventDescription.REMOVED: {
				FIBTextEventDescription ev = (FIBTextEventDescription) e;
				String text = getRenderingTechnologyAdapter().getText(getDynamicJComponent());
				if (text.length() >= ev.getPosition() + ev.getLength())
					getRenderingTechnologyAdapter().setText(getDynamicJComponent(),
							text.substring(0, ev.getPosition()) + text.substring(ev.getPosition() + ev.getLength()));
				break;
			}
			case FIBFocusEventDescription.FOCUS_GAINED:
				getRenderingTechnologyAdapter().requestFocus(getDynamicJComponent());
				break;
			case FIBFocusEventDescription.FOCUS_LOST:
				getRenderingTechnologyAdapter().requestFocusInParent(getDynamicJComponent());
				break;
		}

		widgetExecuting = false;
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {

		String currentWidgetValue = getRenderingTechnologyAdapter().getText(getDynamicJComponent());
		if (notEquals(getValue(), currentWidgetValue)) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(currentWidgetValue)) {
				return false;
			}
			widgetUpdating = true;
			try {
				int caret = getRenderingTechnologyAdapter().getCaretPosition(getDynamicJComponent());
				getRenderingTechnologyAdapter().setText(getDynamicJComponent(), getValue());
				if (caret > -1 && caret < getValue().length()) {
					getRenderingTechnologyAdapter().setCaretPosition(getDynamicJComponent(), caret);
				}
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
		if (notEquals(getValue(), getRenderingTechnologyAdapter().getText(getDynamicJComponent()))) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in " + this);
			}
			modelUpdating = true;
			try {
				setValue(getRenderingTechnologyAdapter().getText(getDynamicJComponent()));
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

}

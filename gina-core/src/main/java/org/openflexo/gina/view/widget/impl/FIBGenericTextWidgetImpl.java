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

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBFocusEventDescription;
import org.openflexo.gina.event.description.FIBTextEventDescription;
import org.openflexo.gina.model.widget.FIBTextWidget;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBGenericTextWidget;

/**
 * Default base implementation for a simple widget allowing to display/edit a String in a TextField
 * 
 * @author sylvain
 */
public abstract class FIBGenericTextWidgetImpl<F extends FIBTextWidget, C> extends FIBWidgetViewImpl<F, C, String>
		implements FIBGenericTextWidget<F, C> {

	private static final Logger logger = Logger.getLogger(FIBGenericTextWidgetImpl.class.getPackage().getName());

	protected boolean validateOnReturn;

	public FIBGenericTextWidgetImpl(F model, FIBController controller, GenericTextRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);

		validateOnReturn = model.isValidateOnReturn();

		// updateEditable();
		// updateText();
	}

	@Override
	public GenericTextRenderingAdapter<C> getRenderingAdapter() {
		return (GenericTextRenderingAdapter<C>) super.getRenderingAdapter();
	}

	public void updateEditable() {
		getRenderingAdapter().setEditable(getTechnologyComponent(), !isReadOnly());
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateStaticText();
	}

	/**
	 * Update value represented in text component from the value obtained from the model
	 * 
	 * @return
	 */
	@Override
	public String updateData() {

		String newText = super.updateData();

		String currentWidgetValue = getRenderingAdapter().getText(getTechnologyComponent());
		if (notEquals(newText, currentWidgetValue)) {
			if (modelUpdating) {
				return newText;
			}
			if (newText != null && (newText + "\n").equals(currentWidgetValue)) {
				return newText;
			}
			widgetUpdating = true;
			try {
				int caret = getRenderingAdapter().getCaretPosition(getTechnologyComponent());
				getRenderingAdapter().setText(getTechnologyComponent(), newText);
				if (caret > -1 && newText != null && caret < newText.length()) {
					getRenderingAdapter().setCaretPosition(getTechnologyComponent(), caret);
				}
			} finally {
				widgetUpdating = false;
			}
		}
		return newText;
	}

	private boolean widgetUpdating = false;

	protected boolean widgetUpdating() {
		return widgetUpdating;
	}

	/**
	 * Update static value represented in text component
	 */
	private void updateStaticText() {
		// TODO: use it to make placeholder support
		/*if (modelUpdating) {
			return;
		}
		widgetUpdating = true;
		try {
			if (StringUtils.isNotEmpty(getWidget().getText())) {
				getRenderingAdapter().setText(getTechnologyComponent(), getWidget().getText());
			}
		} finally {
			widgetUpdating = false;
		}*/
	}

	private boolean modelUpdating = false;

	protected boolean textChanged() {
		if (widgetUpdating) {
			return false;
		}
		if (notEquals(getValue(), getRenderingAdapter().getText(getTechnologyComponent()))) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in " + this);
			}
			modelUpdating = true;
			try {
				setValue(getRenderingAdapter().getText(getTechnologyComponent()));
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
			case FIBTextEventDescription.INSERTED: {
				FIBTextEventDescription ev = (FIBTextEventDescription) e;
				String text = getRenderingAdapter().getText(getTechnologyComponent());
				if (text.length() >= ev.getPosition()) {
					getRenderingAdapter().setText(getTechnologyComponent(),
							(text.substring(0, ev.getPosition()) + e.getValue() + text.substring(ev.getPosition())));
					getRenderingAdapter().setCaretPosition(getTechnologyComponent(), ev.getPosition() + e.getValue().length());
				}
				break;
			}
			case FIBTextEventDescription.REMOVED: {
				FIBTextEventDescription ev = (FIBTextEventDescription) e;
				String text = getRenderingAdapter().getText(getTechnologyComponent());
				if (text.length() >= ev.getPosition() + ev.getLength())
					getRenderingAdapter().setText(getTechnologyComponent(),
							text.substring(0, ev.getPosition()) + text.substring(ev.getPosition() + ev.getLength()));
				break;
			}
			case FIBFocusEventDescription.FOCUS_GAINED:
				getRenderingAdapter().requestFocus(getTechnologyComponent());
				break;
			case FIBFocusEventDescription.FOCUS_LOST:
				getRenderingAdapter().requestFocusInParent(getTechnologyComponent());
				break;
		}

		widgetExecuting = false;
	}

}

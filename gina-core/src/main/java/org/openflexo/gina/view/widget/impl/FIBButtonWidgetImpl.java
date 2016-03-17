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
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBMouseEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBButtonWidget.ButtonWidgetRenderingAdapter;

/**
 * Default base implementation for a widget with a button<br>
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBButtonWidgetImpl<C> extends FIBWidgetViewImpl<FIBButton, C, String> {

	private static final Logger logger = Logger.getLogger(FIBButtonWidgetImpl.class.getPackage().getName());

	public FIBButtonWidgetImpl(FIBButton model, FIBController controller, ButtonWidgetRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		// updateLabel();
		// updateIcon();
	}

	@Override
	public ButtonWidgetRenderingAdapter<C> getRenderingAdapter() {
		return (ButtonWidgetRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateLabel();
		updateIcon();
	}

	/*@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateLabel();
		updateIcon();
		widgetUpdating = false;
		return false;
	}*/

	/**
	 * Update the model given the actual state of the widget
	 */
	/*@Override
	public synchronized boolean updateModelFromWidget() {
		// not relevant
		return false;
	}*/

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
			case FIBMouseEventDescription.CLICKED:
				this.buttonClicked();
				break;
		}

		widgetExecuting = false;
	}

	public synchronized void buttonClicked() {
		GinaStackEvent stackElement = GENotifier.raise(FIBEventFactory.getInstance().createMouseEvent(FIBMouseEventDescription.CLICKED));

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Button " + getWidget() + " has clicked");
			logger.fine("Action: " + getWidget().getAction() + " valid=" + getWidget().getAction().isValid());
			logger.fine("Data: " + getController().getDataObject());
		}

		setData(getComponent().getName());
		DataBinding<?> action = getWidget().getAction();
		if (action.isValid()) {
			try {
				action.execute(getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		update();

		// updateComponentsExplicitelyDeclaredAsDependant();

		// updateWidgetFromModel();

		stackElement.end();
	}

	protected void updateLabel() {
		// logger.info("Button update label with key=" + getWidget().getLabel());
		if (getWidget().getLabel() != null) {
			String text;
			/*if (getValue() != null) {
				if (getWidget().getLocalize()) {
					text = getLocalized(getValue());
				}
				else {
					text = getValue();
				}
			}
			else {*/
			if (getWidget().getLocalize()) {
				text = getLocalized(getWidget().getLabel());
			}
			else {
				text = getWidget().getLabel();
			}
			// }
			getRenderingAdapter().setText(getTechnologyComponent(), text);
		}
	}

	protected void updateIcon() {
		// logger.info("Button update label with key="+getWidget().getLabel());
		if (getWidget().getButtonIcon() != null && getWidget().getButtonIcon().isSet() && getWidget().getButtonIcon().isValid()) {
			Icon icon;
			try {
				icon = getWidget().getButtonIcon().getBindingValue(getBindingEvaluationContext());
				getRenderingAdapter().setIcon(getTechnologyComponent(), icon);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
				getRenderingAdapter().setIcon(getTechnologyComponent(), null);
			} catch (NullReferenceException e) {
				e.printStackTrace();
				getRenderingAdapter().setIcon(getTechnologyComponent(), null);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				getRenderingAdapter().setIcon(getTechnologyComponent(), null);
			}
		}
		else {
			getRenderingAdapter().setIcon(getTechnologyComponent(), null);
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateLabel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBButton.LABEL_KEY)) {
			updateLabel();
		}
		if (evt.getPropertyName().equals(FIBButton.BUTTON_ICON_KEY)) {
			updateIcon();
		}

		super.propertyChange(evt);
	}

}

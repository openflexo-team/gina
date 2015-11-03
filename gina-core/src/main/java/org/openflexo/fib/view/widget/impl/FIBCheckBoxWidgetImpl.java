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

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBCheckBoxWidget;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBValueEventDescription;

/**
 * Default base implementation for a widget able to edit a boolean (or Boolean) object
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBCheckBoxWidgetImpl<C> extends FIBWidgetViewImpl<FIBCheckBox, C, Boolean>implements FIBCheckBoxWidget<C> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBCheckBoxWidgetImpl.class.getPackage().getName());

	private boolean isNegate = false;

	/**
	 * @param model
	 */
	public FIBCheckBoxWidgetImpl(FIBCheckBox model, FIBController controller,
			CheckBoxRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);
		isNegate = model.getNegate();
	}

	@Override
	public CheckBoxRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (CheckBoxRenderingTechnologyAdapter<C>) super.getRenderingTechnologyAdapter();
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
			case FIBValueEventDescription.CHECKED:
				getRenderingTechnologyAdapter().setSelected(getTechnologyComponent(), true);
				break;
			case FIBValueEventDescription.UNCHECKED:
				getRenderingTechnologyAdapter().setSelected(getTechnologyComponent(), false);
				break;
		}

		widgetExecuting = false;
	}

	@Override
	public Boolean getValue() {
		Boolean value = super.getValue();
		return value != null && value;
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		Boolean value = getValue();
		if (notEquals(isNegate ? value == null || !value : value != null && value,
				getRenderingTechnologyAdapter().getSelected(getTechnologyComponent()))) {
			widgetUpdating = true;
			if (value != null) {
				if (isNegate) {
					value = !value;
				}
				getRenderingTechnologyAdapter().setSelected(getTechnologyComponent(), value);
			}
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
		if (isReadOnly()) {
			return false;
		}

		Boolean value = getValue();
		if (notEquals(isNegate ? value == null || !value : value != null && value,
				getRenderingTechnologyAdapter().getSelected(getTechnologyComponent()))) {
			setValue(isNegate ? !getRenderingTechnologyAdapter().getSelected(getTechnologyComponent())
					: getRenderingTechnologyAdapter().getSelected(getTechnologyComponent()));
			return true;
		}
		return false;

	}

	@Override
	public Boolean getDefaultData() {
		return getComponent().getSelected();
	}

}

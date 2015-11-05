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

import org.apache.commons.lang3.StringUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.widget.FIBLabel;
import org.openflexo.fib.view.impl.FIBWidgetViewImpl;
import org.openflexo.fib.view.widget.FIBLabelWidget;

/**
 * Default base implementation for a simple widget allowing to display a label
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBLabelWidgetImpl<C> extends FIBWidgetViewImpl<FIBLabel, C, String>implements FIBLabelWidget<C> {
	private static final Logger LOGGER = Logger.getLogger(FIBLabelWidgetImpl.class.getPackage().getName());

	public FIBLabelWidgetImpl(FIBLabel model, FIBController controller, LabelRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		updateAlign();
		updateLabel();
	}

	@Override
	public LabelRenderingAdapter<C> getRenderingAdapter() {
		return (LabelRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (modelUpdating) {
			return false;
		}
		widgetUpdating = true;
		updateLabel();
		widgetUpdating = false;
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// Read only component
		return false;
	}

	final protected void updateAlign() {
		if (getWidget().getAlign() == null) {
			return;
		}
		getRenderingAdapter().setHorizontalAlignment(getTechnologyComponent(), getWidget().getAlign().getAlign());
	}

	final protected void updateLabel() {
		String label = "";
		if (getWidget().getData().isSet() && getWidget().getData().isValid()) {
			label = (getWidget().getLocalize() ? getLocalized(getValue()) : getValue());
		}
		else if (StringUtils.isNotEmpty(getWidget().getLabel())) {
			label = (getWidget().getLocalize() ? getLocalized(getWidget().getLabel()) : getWidget().getLabel());
		}
		getRenderingAdapter().setText(getTechnologyComponent(), label);
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateLabel();
	}
}

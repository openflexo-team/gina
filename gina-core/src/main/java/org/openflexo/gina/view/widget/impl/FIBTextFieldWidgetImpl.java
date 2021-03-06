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
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.view.widget.FIBTextFieldWidget;

/**
 * Default base implementation for a simple widget allowing to display/edit a String in a TextField
 * 
 * @author sylvain
 */
public abstract class FIBTextFieldWidgetImpl<C> extends FIBGenericTextWidgetImpl<FIBTextField, C> implements FIBTextFieldWidget<C> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBTextFieldWidgetImpl.class.getPackage().getName());

	protected static final int DEFAULT_COLUMNS = 10;

	public FIBTextFieldWidgetImpl(FIBTextField model, FIBController controller, TextFieldRenderingAdapter<C> renderingAdapter) {
		super(model, controller, renderingAdapter);
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateColumns();
	}

	@Override
	public TextFieldRenderingAdapter<C> getRenderingAdapter() {
		return (TextFieldRenderingAdapter<C>) super.getRenderingAdapter();
	}

	public void updateColumns() {
		getRenderingAdapter().setColumns(getTechnologyComponent(),
				getWidget().getColumns() != null && getWidget().getColumns() > 0 ? getWidget().getColumns() : getDefaultColumns());
	}

	public int getDefaultColumns() {
		return DEFAULT_COLUMNS;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBNumber.COLUMNS_KEY)) {
			updateColumns();
		}
		super.propertyChange(evt);
	}

}

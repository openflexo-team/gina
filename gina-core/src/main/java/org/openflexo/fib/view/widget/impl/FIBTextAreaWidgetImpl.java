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
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.view.widget.FIBTextAreaWidget;

/**
 * Default base implementation for a widget able to edit a Text (more than one line) object
 * 
 * @author sylvain
 */
public abstract class FIBTextAreaWidgetImpl<C> extends FIBGenericTextWidgetImpl<FIBTextArea, C>implements FIBTextAreaWidget<C> {

	private static final Logger LOGGER = Logger.getLogger(FIBTextAreaWidgetImpl.class.getPackage().getName());

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 5;

	public FIBTextAreaWidgetImpl(FIBTextArea model, FIBController controller,
			TextAreaRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);
		updateColumns();
		updateRows();
	}

	@Override
	public TextAreaRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (TextAreaRenderingTechnologyAdapter<C>) super.getRenderingTechnologyAdapter();
	}

	public void updateColumns() {
		getRenderingTechnologyAdapter().setColumns(getDynamicJComponent(),
				getWidget().getColumns() != null && getWidget().getColumns() > 0 ? getWidget().getColumns() : getDefaultColumns());
	}

	public int getDefaultColumns() {
		return DEFAULT_COLUMNS;
	}

	public void updateRows() {
		getRenderingTechnologyAdapter().setRows(getDynamicJComponent(),
				getWidget().getRows() != null && getWidget().getRows() > 0 ? getWidget().getRows() : getDefaultRows());
	}

	public int getDefaultRows() {
		return DEFAULT_ROWS;
	}

}
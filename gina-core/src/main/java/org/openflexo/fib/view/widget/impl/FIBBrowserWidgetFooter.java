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

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.widget.FIBBrowserElement;
import org.openflexo.fib.view.widget.FIBBrowserWidget;

/**
 * Represent a table widget footer (the footer is synchronized with the selection of table)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of objects managed by this footer
 * 
 * @author sylvain
 */
public interface FIBBrowserWidgetFooter<C, T> {

	public C getFooterComponent();

	public void initializeActions(FIBBrowserWidget<?, T> widget);

	public void updateActionsFor(FIBBrowserElement element);

	public void delete();

	public void setFocusedObject(Object object);

	public FIBController getController();

	public String getLocalized(String key);

}

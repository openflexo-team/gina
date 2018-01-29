/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.gina.view;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBWidget;

/**
 * Represent the view factory for a given technology (eg. Swing)
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public interface GinaViewFactory<C> {

	public boolean allowsFIBEdition();

	/**
	 * Build and return a new view (instance of {@link FIBContainerView}) for supplied {@link FIBContainer} and controller
	 * 
	 * @param fibContainer
	 * @param controller
	 * @param updateNow
	 * @return
	 */
	public <F extends FIBContainer> FIBContainerView<F, ? extends C, ? extends C> makeContainer(F fibContainer, FIBController controller,
			boolean updateNow);

	/**
	 * Build and return a new view (instance of {@link FIBWidgetView}) for supplied {@link FIBWidget} and controller
	 * 
	 * @param fibWidget
	 * @param controller
	 * @return
	 */
	public <F extends FIBWidget> FIBWidgetView<F, ? extends C, ?> makeWidget(F fibWidget, FIBController controller, boolean updateNow);

	public void show(FIBController controller);

	public void hide(FIBController controller);

	public void disposeWindow(FIBController controller);

}

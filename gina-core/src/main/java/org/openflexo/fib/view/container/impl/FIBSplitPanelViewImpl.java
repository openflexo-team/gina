/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.fib.view.container.impl;

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.SplitLayoutConstraints;
import org.openflexo.fib.view.container.FIBSplitPanelView;
import org.openflexo.fib.view.impl.FIBContainerViewImpl;
import org.openflexo.fib.view.impl.FIBViewImpl;

/**
 * Base implementation of a panel split into a given policy, with adjustable sliders
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view contains
 * 
 * @author sylvain
 */
public abstract class FIBSplitPanelViewImpl<C, C2> extends FIBContainerViewImpl<FIBSplitPanel, C, C2>implements FIBSplitPanelView<C, C2> {

	private static final Logger logger = Logger.getLogger(FIBSplitPanelViewImpl.class.getPackage().getName());

	public FIBSplitPanelViewImpl(FIBSplitPanel model, FIBController controller, SplitPanelRenderingAdapter<C, C2> renderingAdapter) {
		super(model, controller, renderingAdapter);
	}

	@Override
	public SplitPanelRenderingAdapter<C, C2> getRenderingAdapter() {
		return (SplitPanelRenderingAdapter<C, C2>) super.getRenderingAdapter();
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {

		for (FIBComponent subComponent : getNotHiddenSubComponents()) {
			FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
			if (subView == null) {
				subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent);
			}
			// FIBViewImpl subView = getController().buildView(subComponent);
			// if (subView != null) {
			registerViewForComponent(subView, subComponent);
			registerComponentWithConstraints(subView.getTechnologyComponent(),
					((SplitLayoutConstraints) subComponent.getConstraints()).getSplitIdentifier());
			// }
		}
	}

}

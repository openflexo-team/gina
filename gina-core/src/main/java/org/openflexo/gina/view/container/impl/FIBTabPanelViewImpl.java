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

package org.openflexo.gina.view.container.impl;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.FIBTabPanelView;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.gina.view.impl.FIBViewImpl;

/**
 * Base implementation for a panel presenting some children component as tabs
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view contains
 * 
 * @author sylvain
 */
public abstract class FIBTabPanelViewImpl<C, C2> extends FIBContainerViewImpl<FIBTabPanel, C, C2>implements FIBTabPanelView<C, C2> {

	private static final Logger logger = Logger.getLogger(FIBTabPanelViewImpl.class.getPackage().getName());

	public FIBTabPanelViewImpl(FIBTabPanel model, FIBController controller, TabPanelRenderingAdapter<C, C2> renderingAdapter) {
		super(model, controller, renderingAdapter);
	}

	@Override
	public TabPanelRenderingAdapter<C, C2> getRenderingAdapter() {
		return (TabPanelRenderingAdapter<C, C2>) super.getRenderingAdapter();
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBTab> allTabs = new Vector<FIBTab>();
		for (FIBComponent subComponent : getNotHiddenSubComponents()) {
			if (subComponent instanceof FIBTab) {
				allTabs.add((FIBTab) subComponent);
			}
		}

		for (FIBTab tab : allTabs) {
			// logger.info("!!!!!!!!!!!!!!!!!!!! Build view for tab " + tab);
			FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().buildView(tab);
			if (subView != null) {
				registerViewForComponent(subView, tab);
				registerComponentWithConstraints(subView.getTechnologyComponent(), getLocalized(tab.getTitle()));
			}
		}

	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		for (FIBView<?, ?> v : getSubViews()) {
			if (v.getComponent() instanceof FIBTab) {
				getLocalized(((FIBTab) v.getComponent()).getTitle());
			}
			else {
				logger.warning("Unexpected component found in TabPanel: " + v.getComponent());
			}
		}
	}

	public void setSelectedIndex(int index) {
		getRenderingAdapter().setSelectedIndex(getTechnologyComponent(), index);
	}
}

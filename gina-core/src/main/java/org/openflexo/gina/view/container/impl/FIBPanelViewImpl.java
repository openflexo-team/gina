/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.view.container.FIBPanelView;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;

/**
 * Base implementation for a basic panel, as a container of some children component, with a given layout, and a border
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view contains
 * 
 * @author sylvain
 */
public abstract class FIBPanelViewImpl<C, C2> extends FIBContainerViewImpl<FIBPanel, C, C2>implements FIBPanelView<C, C2> {

	private static final Logger logger = Logger.getLogger(FIBPanelViewImpl.class.getPackage().getName());

	public FIBPanelViewImpl(FIBPanel model, FIBController controller, PanelRenderingAdapter<C, C2> renderingAdapter) {
		super(model, controller, renderingAdapter);

		updateBorder();
	}

	@Override
	public PanelRenderingAdapter<C, C2> getRenderingAdapter() {
		return (PanelRenderingAdapter<C, C2>) super.getRenderingAdapter();
	}

	public abstract void updateBorder();

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateBorder();
	}

	/**
	 * Called to configure technology-specific component with relevant layout
	 */
	protected abstract void setPanelLayoutParameters(C technologyComponent);

	@Override
	public synchronized void updateLayout() {
		logger.info("relayout panel " + getComponent());

		// TODO: please reimplement this and make it more efficient !!!!

		clearContainer();

		setPanelLayoutParameters(getTechnologyComponent());
		buildSubComponents();
		// updateDataObject(getDataObject());
		update();
	}

	/**
	 * Remove all components present in this container
	 */
	protected abstract void clearContainer();

	@Override
	protected abstract void retrieveContainedJComponentsAndConstraints();

	protected abstract C2 makeEmptyPanel();

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBPanel.BORDER_KEY) || evt.getPropertyName().equals(FIBPanel.BORDER_COLOR_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BORDER_TITLE_KEY) || evt.getPropertyName().equals(FIBPanel.BORDER_TOP_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BORDER_LEFT_KEY) || evt.getPropertyName().equals(FIBPanel.BORDER_RIGHT_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BORDER_BOTTOM_KEY) || evt.getPropertyName().equals(FIBPanel.TITLE_FONT_KEY)
				|| evt.getPropertyName().equals(FIBPanel.DARK_LEVEL_KEY)) {
			updateBorder();
		}
		if (evt.getPropertyName().equals(FIBPanel.LAYOUT_KEY) || evt.getPropertyName().equals(FIBPanel.FLOW_ALIGNMENT_KEY)
				|| evt.getPropertyName().equals(FIBPanel.BOX_LAYOUT_AXIS_KEY) || evt.getPropertyName().equals(FIBPanel.V_GAP_KEY)
				|| evt.getPropertyName().equals(FIBPanel.H_GAP_KEY) || evt.getPropertyName().equals(FIBPanel.ROWS_KEY)
				|| evt.getPropertyName().equals(FIBPanel.COLS_KEY) || evt.getPropertyName().equals(FIBPanel.PROTECT_CONTENT_KEY)) {
			updateLayout();
		}
		if (getComponent() instanceof FIBTab && evt.getPropertyName().equals(FIBTab.TITLE_KEY)) {
			// Arghlll how do we update titles on this.
		}
		super.propertyChange(evt);
	}

}

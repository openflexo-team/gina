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

package org.openflexo.fib.swing.view.container;

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.swing.view.FIBView;

public class FIBTabView<C extends FIBTab, T> extends FIBPanelView<C, T> {

	private static final Logger logger = Logger.getLogger(FIBTabView.class.getPackage().getName());

	private boolean wasSelected = false;

	public FIBTabView(C model, FIBController controller) {
		super(model, controller);
	}

	/*
	 * @Override public void updateDataObject(Object dataObject) {
	 * System.out.println("Je suis le FIBTabView " + getComponent().getName());
	 * System.out.println("J'etais visible " + isVisible() +
	 * " et je deviens visible " + isComponentVisible());
	 * super.updateDataObject(dataObject); }
	 */

	@Override
	protected void updateVisibility() {

		// logger.info("Called performSetIsVisible " + isVisible + " on TabComponent " + getComponent().getTitle());

		super.updateVisibility();
		// We need to perform this additional operation here, because JTabbedPane already plays with the "visible" flag to handle the
		// currently selected/visible tab
		if (getParentView() instanceof FIBTabPanelView) {
			FIBTabPanelView parent = (FIBTabPanelView) getParentView();
			if (isViewVisible() && getResultingJComponent().getParent() == null) {
				int newIndex = 0;
				for (FIBView<?, ?> v : getParentView().getSubViews().values()) {
					if (v instanceof FIBTabView && v.isComponentVisible()) {
						FIBTab tab = ((FIBTabView<?, ?>) v).getComponent();
						if (getComponent().getParent().getIndex(getComponent()) > tab.getParent().getIndex(tab)) {
							newIndex = parent.getJComponent().indexOfComponent(v.getResultingJComponent()) + 1;
						}
					}
				}

				logger.fine("********** Adding component " + getComponent().getTitle() + " at index " + newIndex);

				parent.getJComponent().add(getResultingJComponent(), getLocalized(getComponent().getTitle()), newIndex);
				if (wasSelected) {
					parent.getJComponent().setSelectedComponent(getResultingJComponent());
				}
			} else if (!isViewVisible() && getResultingJComponent().getParent() != null) {
				wasSelected = parent.getJComponent().getSelectedComponent() == getResultingJComponent();
				parent.getJComponent().remove(getResultingJComponent());
				logger.fine("********** Removing component " + getComponent().getTitle());
			}
		}

	}

}

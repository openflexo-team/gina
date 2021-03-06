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

package org.openflexo.gina.swing.view.container;

import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.FIBTabView;

public class JFIBTabView extends JFIBPanelView implements FIBTabView<JPanel, JComponent> {

	private static final Logger logger = Logger.getLogger(JFIBTabView.class.getPackage().getName());

	private boolean wasSelected = false;

	public JFIBTabView(FIBTab model, FIBController controller) {
		super(model, controller);
	}

	@Override
	public FIBTab getComponent() {
		return (FIBTab) super.getComponent();
	}

	@Override
	protected void updateVisibility() {

		// logger.info("Called performSetIsVisible " + isVisible +
		// " on TabComponent " + getComponent().getTitle());

		super.updateVisibility();
		// We need to perform this additional operation here, because
		// JTabbedPane already plays with the "visible" flag to handle the
		// currently selected/visible tab
		if (getParentView() instanceof JFIBTabPanelView) {
			JFIBTabPanelView parent = (JFIBTabPanelView) getParentView();
			JComponent resultingJComponent = getResultingJComponent();
			JTabbedPane parentResultingJComponent = parent.getTechnologyComponent();

			if (isViewVisible() && resultingJComponent.getParent() == null) {
				int newIndex = 0;
				for (FIBView<?, ?> v : getParentView().getSubViews()) {
					try {
						if (v instanceof JFIBTabView && v.isComponentVisible()) {
							JComponent childResultingJComponent = ((JFIBTabView) v).getResultingJComponent();
							FIBTab tab = ((JFIBTabView) v).getComponent();
							if (getComponent().getParent().getIndex(getComponent()) > tab.getParent().getIndex(tab)) {
								newIndex = parentResultingJComponent.indexOfComponent(childResultingJComponent) + 1;
							}
						}
					} catch (NullReferenceException e) {
						e.printStackTrace();
					}
				}

				logger.fine("********** Adding component " + getComponent().getTitle() + " at index " + newIndex);

				parentResultingJComponent.add(resultingJComponent, /*getLocalized(*/getComponent().getTitle()/*)*/, newIndex);
				if (wasSelected) {
					parentResultingJComponent.setSelectedComponent(resultingJComponent);
				}
			}
			else if (!isViewVisible() && resultingJComponent.getParent() != null) {
				wasSelected = parentResultingJComponent.getSelectedComponent() == resultingJComponent;
				parentResultingJComponent.remove(resultingJComponent);
				logger.fine("********** Removing component " + getComponent().getTitle());
			}
		}

	}

}

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

package org.openflexo.gina.swing.editor.view.container.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints;
import org.openflexo.gina.model.container.layout.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.editor.view.container.JFIBEditablePanelView;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.layout.JBorderLayout;
import org.openflexo.logging.FlexoLogger;

/**
 * Swing implementation for border layout
 * 
 * @author sylvain
 */
public class JEditableBorderLayout extends JBorderLayout implements
		JFIBEditableLayoutManager<JPanel, JComponent, BorderLayoutConstraints> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditablePanelView.class.getPackage().getName());

	public JEditableBorderLayout(JFIBEditablePanelView panelView) {
		super(panelView);
	}

	@Override
	public JFIBEditablePanelView getContainerView() {
		return (JFIBEditablePanelView) super.getContainerView();
	}

	@Override
	public List<PlaceHolder> makePlaceHolders(final Dimension preferredSize) {

		List<PlaceHolder> returned = new ArrayList<PlaceHolder>();

		if (!getComponent().getProtectContent()) {

			BorderLayoutLocation[] placeholderLocations = { BorderLayoutLocation.north, BorderLayoutLocation.south,
					BorderLayoutLocation.center, BorderLayoutLocation.east, BorderLayoutLocation.west };
			Map<BorderLayoutLocation, FIBComponent> existingComponents = new HashMap<BorderLayoutLocation, FIBComponent>();
			Map<BorderLayoutLocation, Component> phComponents = new HashMap<BorderLayoutLocation, Component>();

			JPanel panel = new JPanel(new BorderLayout());
			panel.setPreferredSize(getContainerView().getResultingJComponent().getSize());
			panel.setSize(getContainerView().getResultingJComponent().getSize());

			for (final BorderLayoutLocation l : placeholderLocations) {
				FIBComponent foundComponent = null;
				for (FIBComponent subComponent : getComponent().getSubComponents()) {
					BorderLayoutConstraints blc = (BorderLayoutConstraints) subComponent.getConstraints();
					if (blc.getLocation() == l) {
						foundComponent = subComponent;
						break;
					}
				}
				Component phComponent;
				if (foundComponent != null) {
					existingComponents.put(l, foundComponent);
					JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(foundComponent);
					phComponent = Box.createRigidArea(childView.getResultingJComponent().getSize());
				} else {
					phComponent = new JPanel() {
						@Override
						public Dimension getPreferredSize() {
							return preferredSize;
						}
					};
				}
				phComponents.put(l, phComponent);
				panel.add(phComponent, l.getConstraint());
			}
			panel.doLayout();

			for (final BorderLayoutLocation l : placeholderLocations) {
				FIBComponent existingComponent = existingComponents.get(l);
				Rectangle bounds = phComponents.get(l).getBounds();

				// System.out.println("Pour " + l.getConstraint() + " existing="
				// + existingComponent + " bounds=" + bounds);

				if (existingComponent == null) {
					PlaceHolder newPlaceHolder = new PlaceHolder(getContainerView(), "<" + l.getConstraint() + ">",
							bounds) {
						@Override
						public void insertComponent(FIBComponent newComponent) {

							System.out.println("He he on ajoute un composant " + newComponent + " a "
									+ getContainerView().getComponent());

							BorderLayoutConstraints blConstraints = new BorderLayoutConstraints(l);
							newComponent.setConstraints(blConstraints);
							getComponent().addToSubComponents(newComponent);
						}
					};
					logger.fine("Made placeholder for " + l.getConstraint());
					newPlaceHolder.setVisible(false);
					returned.add(newPlaceHolder);
				}
			}
		}

		return returned;
	}

}

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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBPanel.FlowLayoutAlignment;
import org.openflexo.gina.model.container.layout.FlowLayoutConstraints;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.editor.view.container.JFIBEditablePanelView;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.layout.JFlowLayout;
import org.openflexo.logging.FlexoLogger;

/**
 * Swing implementation for edition of a flow layout
 * 
 * @author sylvain
 */
public class JEditableFlowLayout extends JFlowLayout implements JFIBEditableLayoutManager<JPanel, JComponent, FlowLayoutConstraints> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditablePanelView.class.getPackage().getName());

	public JEditableFlowLayout(JFIBEditablePanelView panelView) {
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

			JPanel panel = new JPanel(makeFlowLayout());
			panel.setPreferredSize(getContainerView().getResultingJComponent().getSize());
			panel.setSize(getContainerView().getResultingJComponent().getSize());

			final Dimension placeHolderSize = new Dimension(30, 20);
			int deltaX = 0;
			int deltaY = 0;

			if (getComponent().getFlowAlignment() == FlowLayoutAlignment.CENTER) {
				deltaX = 0;
				deltaY = 0;
			}
			else if (getComponent().getFlowAlignment() == FlowLayoutAlignment.LEADING
					|| getComponent().getFlowAlignment() == FlowLayoutAlignment.LEFT) {
				deltaX = -15 - getComponent().getHGap() / 2;
				deltaY = 0;
			}
			else if (getComponent().getFlowAlignment() == FlowLayoutAlignment.TRAILING
					|| getComponent().getFlowAlignment() == FlowLayoutAlignment.RIGHT) {
				deltaX = 15 + getComponent().getHGap() / 2;
				deltaY = 0;
			}

			if (getComponent().getSubComponents().size() == 0) {
				// Special case: do not delta placeholders for empty container
				deltaX = 0;
				deltaY = 0;
			}

			// Before each component, we will add an empty panel to compute
			// placeholder location
			for (int i = 0; i < getComponent().getSubComponents().size(); i++) {
				panel.removeAll();
				for (int j = 0; j < i; j++) {
					FIBComponent c = getComponent().getSubComponents().get(j);
					JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(c);
					panel.add(Box.createRigidArea(childView.getResultingJComponent().getSize()));
				}
				Component phComponent = new JPanel() {
					@Override
					public Dimension getPreferredSize() {
						return placeHolderSize;
					}
				};
				panel.add(phComponent);
				for (int j = i; j < getComponent().getSubComponents().size(); j++) {
					FIBComponent c = getComponent().getSubComponents().get(j);
					JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(c);
					panel.add(Box.createRigidArea(childView.getResultingJComponent().getSize()));
				}
				panel.doLayout();
				// System.out.println("OK placeholder i=" + i + ", bounds=" +
				// phComponent.getBounds());
				final int insertionIndex = i;

				Rectangle placeHolderBounds = makePlaceHolderBounds(phComponent, deltaX, deltaY);
				/*
				 * Rectangle placeHolderBounds = new
				 * Rectangle(phComponent.getBounds().x + deltaX,
				 * phComponent.getBounds().y + deltaY, phComponent.getWidth(),
				 * phComponent.getHeight()); if (placeHolderBounds.x < 0) {
				 * placeHolderBounds.width = placeHolderBounds.width +
				 * placeHolderBounds.x; placeHolderBounds.x = 0; }
				 */
				PlaceHolder newPlaceHolder = new PlaceHolder(getContainerView(), "< flow item >", placeHolderBounds) {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						putSubComponentsAtIndex(newComponent, insertionIndex);
					}
				};
				newPlaceHolder.setVisible(false);
				returned.add(newPlaceHolder);
			}

			// Then add a placeholder at the end
			panel.removeAll();
			for (int i = 0; i < getComponent().getSubComponents().size(); i++) {
				FIBComponent c = getComponent().getSubComponents().get(i);
				JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(c);
				panel.add(Box.createRigidArea(childView.getResultingJComponent().getSize()));
			}
			Component phComponent = new JPanel() {
				@Override
				public Dimension getPreferredSize() {
					return placeHolderSize;
				}
			};
			panel.add(phComponent);
			panel.doLayout();

			// System.out.println("OK last placeholder bounds=" +
			// phComponent.getBounds());
			Rectangle placeHolderBounds = makePlaceHolderBounds(phComponent, deltaX, deltaY);
			// Rectangle placeHolderBounds = new
			// Rectangle(phComponent.getBounds().x + deltaX,
			// phComponent.getBounds().y
			// + deltaY, phComponent.getWidth(), phComponent.getHeight());
			PlaceHolder newPlaceHolder = new PlaceHolder(getContainerView(), "< flow item >", placeHolderBounds) {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FlowLayoutConstraints flowConstraints = new FlowLayoutConstraints();
					newComponent.setConstraints(flowConstraints);

					putSubComponentsAtIndex(newComponent, getComponent().getSubComponents().size());
				}
			};
			newPlaceHolder.setVisible(false);
			returned.add(newPlaceHolder);
		}

		return returned;
	}

	private Rectangle makePlaceHolderBounds(Component component, int deltaX, int deltaY) {
		Rectangle placeHolderBounds = new Rectangle(component.getBounds().x + deltaX, component.getBounds().y + deltaY,
				component.getWidth(), component.getHeight());
		if (placeHolderBounds.x < 0) {
			placeHolderBounds.width = placeHolderBounds.width + placeHolderBounds.x;
			placeHolderBounds.x = 0;
		}
		if (placeHolderBounds.y < 0) {
			placeHolderBounds.height = placeHolderBounds.height + placeHolderBounds.y;
			placeHolderBounds.y = 0;
		}
		if (placeHolderBounds.x + placeHolderBounds.width > getContainerView().getResultingJComponent().getWidth()) {
			placeHolderBounds.width = getContainerView().getResultingJComponent().getWidth() - placeHolderBounds.x;
		}
		if (placeHolderBounds.y + placeHolderBounds.height > getContainerView().getResultingJComponent().getHeight()) {
			placeHolderBounds.height = getContainerView().getResultingJComponent().getHeight() - placeHolderBounds.y;
		}
		return placeHolderBounds;
	}

	protected void putSubComponentsAtIndex(FIBComponent subComponent, int index) {
		if (getComponent().getSubComponents().contains(subComponent)) {
			// This is a simple move
			System.out.println("Moving component at index " + index);
			getComponent().moveToSubComponentsAtIndex(subComponent, index);
		}
		else {
			System.out.println("Inserting component at index " + index);
			getComponent().insertToSubComponentsAtIndex(subComponent, index);
		}

	}

}

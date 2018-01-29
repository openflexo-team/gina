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
import org.openflexo.gina.model.container.layout.GridBagLayoutConstraints;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.editor.view.container.JFIBEditablePanelView;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.layout.JGridBagLayout;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.logging.FlexoLogger;

/**
 * Swing implementation for edition of a GridBag layout
 * 
 * @author sylvain
 */
public class JEditableGridBagLayout extends JGridBagLayout
		implements JFIBEditableLayoutManager<JPanel, JComponent, GridBagLayoutConstraints> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditablePanelView.class.getPackage().getName());

	public JEditableGridBagLayout(FIBContainerViewImpl<?, JPanel, JComponent> panelView) {
		super(panelView);
	}

	/*@Override
	public JFIBPanelView getContainerView() {
		return (JFIBPanelView) super.getContainerView();
	}*/

	@Override
	public List<PlaceHolder> makePlaceHolders(final Dimension preferredSize) {

		List<PlaceHolder> returned = new ArrayList<>();

		if (!getComponent().getProtectContent()) {

			JPanel panel = new JPanel();
			panel.setLayout(makeGridBagLayout());
			panel.setPreferredSize(((JFIBView<?, ?>) getContainerView()).getResultingJComponent().getSize());
			panel.setSize(((JFIBView<?, ?>) getContainerView()).getResultingJComponent().getSize());

			// final Dimension placeHolderSize = new Dimension(30, 20);
			int deltaX = 0; // -preferredSize.width / 2;
			int deltaY = 0; // -preferredSize.height / 2;

			/*if (getComponent().getBoxLayoutAxis() == BoxLayoutAxis.X_AXIS) {
				deltaX = -preferredSize.width / 2;
				deltaY = 0;
			}
			else if (getComponent().getBoxLayoutAxis() == BoxLayoutAxis.Y_AXIS) {
				deltaX = 0;
				deltaY = -preferredSize.height / 2;
			}*/

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
					Component child = Box.createRigidArea(childView.getResultingJComponent().getSize());
					_addChildToContainerWithConstraints(child, panel, (GridBagLayoutConstraints) c.getConstraints());
					// panel.add(Box.createRigidArea(childView.getResultingJComponent().getSize()));
				}
				JComponent phComponent = new JPanel() {
					@Override
					public Dimension getPreferredSize() {
						return preferredSize;
					}
				};
				// phComponent.setAlignmentX(0.5f);
				// phComponent.setAlignmentY(0.5f);
				// panel.add(phComponent);
				_addChildToContainerWithConstraints(phComponent, panel, new GridBagLayoutConstraints());
				for (int j = i; j < getComponent().getSubComponents().size(); j++) {
					FIBComponent c = getComponent().getSubComponents().get(j);
					JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(c);
					Component child = Box.createRigidArea(childView.getResultingJComponent().getSize());
					_addChildToContainerWithConstraints(child, panel, (GridBagLayoutConstraints) c.getConstraints());
					// panel.add(Box.createRigidArea(childView.getResultingJComponent().getSize()));
				}
				panel.doLayout();
				// System.out.println("OK placeholder i=" + i + ", bounds=" +
				// phComponent.getBounds());
				final int insertionIndex = i;

				Rectangle placeHolderBounds = makePlaceHolderBounds(phComponent, deltaX, deltaY, preferredSize);
				/*
				 * Rectangle placeHolderBounds = new
				 * Rectangle(phComponent.getBounds().x + deltaX,
				 * phComponent.getBounds().y + deltaY, phComponent.getWidth(),
				 * phComponent.getHeight()); if (placeHolderBounds.x < 0) {
				 * placeHolderBounds.width = placeHolderBounds.width +
				 * placeHolderBounds.x; placeHolderBounds.x = 0; }
				 */
				PlaceHolder newPlaceHolder = new PlaceHolder((FIBSwingEditableContainerView<?, ?>) getContainerView(), "< item >",
						placeHolderBounds) {
					@Override
					public void insertComponent(FIBComponent newComponent, int oldIndex) {
						GridBagLayoutConstraints constraints = new GridBagLayoutConstraints();
						newComponent.setConstraints(constraints);
						putSubComponentsAtIndex(newComponent, insertionIndex);
					}
				};
				newPlaceHolder.setVisible(false);
				// System.out.println(">>> Add placeholder bounds=" + placeHolderBounds);
				returned.add(newPlaceHolder);
			}

			// Then add a placeholder at the end
			panel.removeAll();
			for (int i = 0; i < getComponent().getSubComponents().size(); i++) {
				FIBComponent c = getComponent().getSubComponents().get(i);
				JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(c);
				Component child = Box.createRigidArea(childView.getResultingJComponent().getSize());
				_addChildToContainerWithConstraints(child, panel, (GridBagLayoutConstraints) c.getConstraints());
				// panel.add(Box.createRigidArea(childView.getResultingJComponent().getSize()));
			}
			JComponent phComponent = new JPanel() {
				@Override
				public Dimension getPreferredSize() {
					return preferredSize;
				}
			};
			phComponent.setAlignmentX(0.5f);
			phComponent.setAlignmentY(0.5f);
			_addChildToContainerWithConstraints(phComponent, panel, new GridBagLayoutConstraints());
			// panel.add(phComponent);
			panel.doLayout();

			// System.out.println("OK last placeholder bounds=" +
			// phComponent.getBounds());
			Rectangle placeHolderBounds = makePlaceHolderBounds(phComponent, deltaX, deltaY, preferredSize);
			// Rectangle placeHolderBounds = new
			// Rectangle(phComponent.getBounds().x + deltaX,
			// phComponent.getBounds().y
			// + deltaY, phComponent.getWidth(), phComponent.getHeight());
			PlaceHolder newPlaceHolder = new PlaceHolder((FIBSwingEditableContainerView<?, ?>) getContainerView(), "< item >",
					placeHolderBounds) {
				@Override
				public void insertComponent(FIBComponent newComponent, int oldIndex) {
					GridBagLayoutConstraints constraints = new GridBagLayoutConstraints();
					newComponent.setConstraints(constraints);
					putSubComponentsAtIndex(newComponent, getComponent().getSubComponents().size());
				}
			};
			newPlaceHolder.setVisible(false);
			// System.out.println("*** Add placeholder bounds=" + placeHolderBounds);
			returned.add(newPlaceHolder);
		}

		return returned;
	}

	private Rectangle makePlaceHolderBounds(Component component, int deltaX, int deltaY, Dimension preferredSize) {
		Rectangle placeHolderBounds = new Rectangle(component.getBounds().x + deltaX, component.getBounds().y + deltaY,
				/*(int) preferredSize.getWidth(), (int) preferredSize.getHeight()*/ component.getWidth(), component.getHeight());

		if (placeHolderBounds.width < preferredSize.getWidth()) {
			placeHolderBounds.width = (int) preferredSize.getWidth();
		}
		if (placeHolderBounds.height < preferredSize.getHeight()) {
			placeHolderBounds.height = (int) preferredSize.getHeight();
		}

		if (placeHolderBounds.x < 0) {
			// placeHolderBounds.width = placeHolderBounds.width + placeHolderBounds.x;
			placeHolderBounds.x = 0;
		}
		if (placeHolderBounds.y < 0) {
			// placeHolderBounds.height = placeHolderBounds.height + placeHolderBounds.y;
			placeHolderBounds.y = 0;
		}
		if (placeHolderBounds.x + placeHolderBounds.width > ((JFIBView<?, ?>) getContainerView()).getResultingJComponent().getWidth()) {
			// placeHolderBounds.width = getContainerView().getResultingJComponent().getWidth() - placeHolderBounds.x;
			placeHolderBounds.x = ((JFIBView<?, ?>) getContainerView()).getResultingJComponent().getWidth() - placeHolderBounds.width;
		}
		if (placeHolderBounds.y + placeHolderBounds.height > ((JFIBView<?, ?>) getContainerView()).getResultingJComponent().getHeight()) {
			// placeHolderBounds.height = getContainerView().getResultingJComponent().getHeight() - placeHolderBounds.y;
			placeHolderBounds.y = ((JFIBView<?, ?>) getContainerView()).getResultingJComponent().getHeight() - placeHolderBounds.height;
		}
		return placeHolderBounds;
	}

	protected void putSubComponentsAtIndex(FIBComponent subComponent, int index) {
		if (getComponent().getSubComponents().contains(subComponent)) {
			// This is a simple move
			// System.out.println("Moving component at index " + index);
			getComponent().moveToSubComponentsAtIndex(subComponent, index);
		}
		else {
			// System.out.println("Inserting component at index " + index);
			getComponent().insertToSubComponentsAtIndex(subComponent, index);
		}

	}

}

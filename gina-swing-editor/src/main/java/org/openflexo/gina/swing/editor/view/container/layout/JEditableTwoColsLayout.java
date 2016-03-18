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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.editor.view.container.JFIBEditablePanelView;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.layout.JTwoColsLayout;
import org.openflexo.logging.FlexoLogger;

/**
 * Swing implementation for edition of a TwoCols layout
 * 
 * @author sylvain
 */
public class JEditableTwoColsLayout extends JTwoColsLayout
		implements JFIBEditableLayoutManager<JPanel, JComponent, TwoColsLayoutConstraints> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditablePanelView.class.getPackage().getName());

	public JEditableTwoColsLayout(JFIBEditablePanelView panelView) {
		super(panelView);
	}

	@Override
	public JFIBEditablePanelView getContainerView() {
		return (JFIBEditablePanelView) super.getContainerView();
	}

	private void fillInContainerWithSubComponents(Container panel, int fromIndex, int toIndex, boolean addGlueWhenRequiredAtTheEnd) {
		FIBComponent lastAddedChild = null;
		for (int j = fromIndex; j < toIndex; j++) {
			FIBComponent c = getComponent().getSubComponents().get(j);
			JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(c);
			if (c.getConstraints() instanceof TwoColsLayoutConstraints) {
				TwoColsLayoutConstraints contraints = (TwoColsLayoutConstraints) c.getConstraints();
				if (lastAddedChild != null && lastAddedChild.getConstraints() instanceof TwoColsLayoutConstraints
						&& ((TwoColsLayoutConstraints) lastAddedChild.getConstraints()).getLocation() == TwoColsLayoutLocation.left
						&& (contraints.getLocation() == TwoColsLayoutLocation.left
								|| contraints.getLocation() == TwoColsLayoutLocation.center)) {
					// A second LEFT or CENTER component after a previous LEFT one, add glue
					// We have to add an extra glue at the end of last component if this one was declared as in LEFT position
					Component glue = Box.createHorizontalGlue();
					_addChildToContainerWithConstraints(glue, panel,
							new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				}

				if (contraints.getLocation() == TwoColsLayoutLocation.right && ((lastAddedChild == null) || (lastAddedChild != null
						&& lastAddedChild.getConstraints() instanceof TwoColsLayoutConstraints
						&& ((TwoColsLayoutConstraints) lastAddedChild.getConstraints()).getLocation() == TwoColsLayoutLocation.right))) {
					Component glue = Box.createHorizontalGlue();
					_addChildToContainerWithConstraints(glue, panel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
				}

				_addChildToContainerWithConstraints(Box.createRigidArea(childView.getResultingJComponent().getSize()), panel, contraints);

				if (addGlueWhenRequiredAtTheEnd && j == toIndex - 1 && contraints.getLocation() == TwoColsLayoutLocation.left) {
					Component glue = Box.createHorizontalGlue();
					_addChildToContainerWithConstraints(glue, panel,
							new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				}
			}
			lastAddedChild = c;
		}

	}

	/**
	 * Make placeholders for component implementing this layout<br>
	 * This method is called during a drag-and-drop scheme initiated from the palette
	 */
	@Override
	public List<PlaceHolder> makePlaceHolders(final Dimension preferredSize) {

		List<PlaceHolder> returned = new ArrayList<PlaceHolder>();

		if (!getComponent().getProtectContent()) {

			JPanel panel = new JPanel(makeTwoColsLayout());
			panel.setPreferredSize(getContainerView().getResultingJComponent().getSize());
			panel.setSize(getContainerView().getResultingJComponent().getSize());

			List<List<FIBComponent>> rows = new ArrayList<>();
			List<FIBComponent> currentRow = null;
			for (int i = 0; i < getComponent().getSubComponents().size(); i++) {
				FIBComponent c = getComponent().getSubComponents().get(i);
				if (c.getConstraints() instanceof TwoColsLayoutConstraints) {
					TwoColsLayoutConstraints contraints = (TwoColsLayoutConstraints) c.getConstraints();

					if (currentRow == null) {
						currentRow = new ArrayList<>();
					}

					if (contraints.getLocation() == TwoColsLayoutLocation.left && currentRow.size() > 0) {
						FIBComponent previousComponent = currentRow.get(currentRow.size() - 1);
						if (previousComponent.getConstraints() instanceof TwoColsLayoutConstraints
								&& ((TwoColsLayoutConstraints) previousComponent.getConstraints())
										.getLocation() == TwoColsLayoutLocation.left)
							rows.add(currentRow);
						currentRow = new ArrayList<>(); // New row
					}

					currentRow.add(c);

					if (contraints.getLocation() == TwoColsLayoutLocation.center
							|| contraints.getLocation() == TwoColsLayoutLocation.right) {
						rows.add(currentRow);
						currentRow = null; // Means new row !
					}
				}
			}
			if (currentRow != null) {
				rows.add(currentRow);
			}

			// System.out.println("Building placeholders for " + rows.size() + " rows");
			// for (int r = 0; r < rows.size(); r++) {
			// System.out.println("Row " + r + " with " + rows.get(r));
			// }

			// In this first step, we will add placeholders for a previous lineb before each row

			for (int r = 0; r < rows.size(); r++) {

				List<FIBComponent> row = rows.get(r);
				// System.out.println("Row " + r + " with " + row);

				// Reinitialize the panel used for layout computation
				panel.removeAll();

				int lastInsertedElementIndex = 0;

				// First put components already beeing inside the container, until last inserted index matching this row
				if (r > 0) {
					List<FIBComponent> previousRow = rows.get(r - 1);
					FIBComponent lastElementInRow = previousRow.get(previousRow.size() - 1);
					lastInsertedElementIndex = getComponent().getSubComponents().indexOf(lastElementInRow) + 1;
					fillInContainerWithSubComponents(panel, 0, lastInsertedElementIndex, true);
				}

				// Build center placeholder (will be added for the first row only)
				final TwoColsLayoutConstraints centerConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false);
				Component centerPHComponent = null;
				if (r == 0) {
					centerPHComponent = Box.createRigidArea(preferredSize);
					_addChildToContainerWithConstraints(centerPHComponent, panel, centerConstraints);
				}

				// Build placeholder for a previous line at left location
				final TwoColsLayoutConstraints leftConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false);
				Component leftPHComponent = Box.createRigidArea(preferredSize);
				_addChildToContainerWithConstraints(leftPHComponent, panel, leftConstraints);

				// Build placeholder for a previous line at right location
				final TwoColsLayoutConstraints rightConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false);
				Component rightPHComponent = Box.createRigidArea(preferredSize);
				_addChildToContainerWithConstraints(rightPHComponent, panel, rightConstraints);

				// And add all remaining existing component
				fillInContainerWithSubComponents(panel, lastInsertedElementIndex, getComponent().getSubComponents().size(), true);

				panel.doLayout();

				// For the first row only, add the 3 placeholders and shit them at required height
				if (r == 0) {
					returned.add(makePlaceHolder("<center>", centerConstraints, lastInsertedElementIndex, centerPHComponent, 0,
							-preferredSize.height));
					returned.add(makePlaceHolder("<left>", leftConstraints, lastInsertedElementIndex, leftPHComponent, 0,
							-preferredSize.height));
					returned.add(makePlaceHolder("<right>", rightConstraints, lastInsertedElementIndex, rightPHComponent, 0,
							-preferredSize.height));
				}
				else {

					if (row.size() == 1 && row.get(0).getConstraints() instanceof TwoColsLayoutConstraints) {
						TwoColsLayoutConstraints existingConstraint = (TwoColsLayoutConstraints) row.get(0).getConstraints();
						if (existingConstraint.getLocation() == TwoColsLayoutLocation.left) {
							// Right component is missing, do not shadow placeholder for this hole, just add the "left" one
							returned.add(makePlaceHolder("<left>", leftConstraints, lastInsertedElementIndex, leftPHComponent, 0, 0));
						}
						else if (existingConstraint.getLocation() == TwoColsLayoutLocation.right) {
							// Left component is missing, do not shadow placeholder for this hole, just add the "right" one
							returned.add(makePlaceHolder("<right>", rightConstraints, lastInsertedElementIndex, rightPHComponent, 0, 0));
						}
						else if (existingConstraint.getLocation() == TwoColsLayoutLocation.center) {
							// put the two placeholders
							returned.add(makePlaceHolder("<left>", leftConstraints, lastInsertedElementIndex, leftPHComponent, 0, 0));
							returned.add(makePlaceHolder("<right>", rightConstraints, lastInsertedElementIndex, rightPHComponent, 0, 0));
						}
					}
					else {
						// Otherwise add the two left/right placeholders
						returned.add(makePlaceHolder("<left>", leftConstraints, lastInsertedElementIndex, leftPHComponent, 0, 0));
						returned.add(makePlaceHolder("<right>", rightConstraints, lastInsertedElementIndex, rightPHComponent, 0, 0));
					}
				}

				// If row is not complete add placeholder matching missing component
				if (row.size() == 1 && row.get(0).getConstraints() instanceof TwoColsLayoutConstraints) {

					panel.removeAll();

					lastInsertedElementIndex = 0;
					if (r > 0) {
						List<FIBComponent> previousRow = rows.get(r - 1);
						FIBComponent lastElementInRow = previousRow.get(previousRow.size() - 1);
						lastInsertedElementIndex = getComponent().getSubComponents().indexOf(lastElementInRow) + 1;
						fillInContainerWithSubComponents(panel, 0, lastInsertedElementIndex, true);
					}

					FIBComponent existingComponent = row.get(0);

					TwoColsLayoutConstraints presentConstraint = (TwoColsLayoutConstraints) existingComponent.getConstraints();

					if (presentConstraint.getLocation() == TwoColsLayoutLocation.left) {

						System.out.println("Right component is missing");

						JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(existingComponent);
						_addChildToContainerWithConstraints(Box.createRigidArea(childView.getResultingJComponent().getSize()), panel,
								presentConstraint);

						lastInsertedElementIndex++;

						// Put a placeholder right to existing
						rightPHComponent = Box.createRigidArea(preferredSize);
						_addChildToContainerWithConstraints(rightPHComponent, panel, rightConstraints);

						// Put other components
						fillInContainerWithSubComponents(panel, lastInsertedElementIndex, getComponent().getSubComponents().size(), false);

						panel.doLayout();

						returned.add(makePlaceHolder("<right>", rightConstraints, lastInsertedElementIndex, rightPHComponent, 0, 0));
					}

					else if (presentConstraint.getLocation() == TwoColsLayoutLocation.right) {

						System.out.println("Left component is missing");

						// Put a placeholder left to existing
						leftPHComponent = Box.createRigidArea(preferredSize);
						_addChildToContainerWithConstraints(leftPHComponent, panel, leftConstraints);

						JFIBView<?, ?> childView = (JFIBView<?, ?>) getContainerView().getSubViewsMap().get(existingComponent);
						_addChildToContainerWithConstraints(Box.createRigidArea(childView.getResultingJComponent().getSize()), panel,
								presentConstraint);

						// Put other components
						fillInContainerWithSubComponents(panel, lastInsertedElementIndex + 1, getComponent().getSubComponents().size(),
								false);

						panel.doLayout();

						returned.add(makePlaceHolder("<left>", leftConstraints, lastInsertedElementIndex, leftPHComponent, 0, 0));
					}
				}
			}

			// Then add placeholders at the end of the component
			panel.removeAll();
			fillInContainerWithSubComponents(panel, 0, getComponent().getSubComponents().size(), true);

			// Same code as in doLayout(), where we add an extra glue at the end of last component if this one was declared as in LEFT
			// position
			/*if (getContainerView().getComponent().getSubComponents().size() > 0) {
				FIBComponent lastComponent = getContainerView().getComponent().getSubComponents()
						.get(getContainerView().getComponent().getSubComponents().size() - 1);
				if (lastComponent.getConstraints() instanceof TwoColsLayoutConstraints) {
					TwoColsLayoutConstraints contraints = (TwoColsLayoutConstraints) lastComponent.getConstraints();
					if (contraints.getLocation() == TwoColsLayoutLocation.left) {
						// We have to add glue
						Component glue = Box.createHorizontalGlue();
						_addChildToContainerWithConstraints(glue, panel,
								new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
					}
				}
			}*/

			final TwoColsLayoutConstraints leftConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false);
			Component leftPHComponent = Box.createRigidArea(preferredSize);
			_addChildToContainerWithConstraints(leftPHComponent, panel, leftConstraints);

			final TwoColsLayoutConstraints rightConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false);
			Component rightPHComponent = Box.createRigidArea(preferredSize);
			_addChildToContainerWithConstraints(rightPHComponent, panel, rightConstraints);

			final TwoColsLayoutConstraints centerConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false);
			Component centerPHComponent = Box.createRigidArea(preferredSize);
			_addChildToContainerWithConstraints(centerPHComponent, panel, centerConstraints);

			panel.doLayout();

			returned.add(makePlaceHolder("<left>", leftConstraints, getComponent().getSubComponents().size(), leftPHComponent, 0,
					preferredSize.height));
			returned.add(makePlaceHolder("<right>", rightConstraints, getComponent().getSubComponents().size(), rightPHComponent, 0,
					preferredSize.height));
			returned.add(makePlaceHolder("<center>", centerConstraints, getComponent().getSubComponents().size(), centerPHComponent, 0,
					preferredSize.height));

		}

		return returned;

	}

	private PlaceHolder makePlaceHolder(String text, final TwoColsLayoutConstraints leftConstraints, final int index, Component component,
			int deltaX, int deltaY) {
		Rectangle placeHolderBounds = makePlaceHolderBounds(component, deltaX, deltaY);
		PlaceHolder returned = new PlaceHolder(getContainerView(), text, placeHolderBounds) {
			@Override
			public void insertComponent(FIBComponent newComponent) {
				putSubComponentsAtIndexWithConstraints(newComponent, index, leftConstraints);
			}
		};
		returned.setVisible(false);
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

	protected void putSubComponentsAtIndexWithConstraints(FIBComponent subComponent, int index, TwoColsLayoutConstraints constraints) {
		if (getComponent().getSubComponents().contains(subComponent)) {
			// This is a simple move
			System.out.println("Moving component at index " + index);
			getComponent().moveToSubComponentsAtIndex(subComponent, index);
		}
		else {
			System.out.println("Inserting component at index " + index);
			// getComponent().setConstraints(constraints);
			getComponent().insertToSubComponentsAtIndex(subComponent, constraints, index);
		}

	}

}
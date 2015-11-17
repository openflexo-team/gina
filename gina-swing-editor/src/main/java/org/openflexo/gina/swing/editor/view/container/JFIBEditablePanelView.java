/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.view.container;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.BorderLayoutConstraints;
import org.openflexo.gina.model.container.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.gina.model.container.BoxLayoutConstraints;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FlowLayoutConstraints;
import org.openflexo.gina.model.container.GridBagLayoutConstraints;
import org.openflexo.gina.model.container.GridLayoutConstraints;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerViewDelegate;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.JFIBPanelView;
import org.openflexo.logging.FlexoLogger;

public class JFIBEditablePanelView extends JFIBPanelView implements FIBSwingEditableContainerView<FIBPanel, JPanel> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditablePanelView.class.getPackage().getName());

	private final FIBSwingEditableContainerViewDelegate<FIBPanel, JPanel> delegate;

	private Vector<PlaceHolder> placeholders;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditablePanelView(final FIBPanel model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;
		// logger.info("************ Created FIBEditablePanelView for "+model);

		delegate = new FIBSwingEditableContainerViewDelegate<FIBPanel, JPanel>(this);

		// getJComponent().setBorder(BorderFactory.createMatteBorder(10,10,10,10,Color.yellow));

	}

	@Override
	public void delete() {

		if (placeholders != null) {
			placeholders.clear();
		}
		placeholders = null;
		delegate.delete();
		super.delete();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		if (placeholders == null) {
			placeholders = new Vector<PlaceHolder>();
		}
		placeholders.removeAllElements();

		super.retrieveContainedJComponentsAndConstraints();

		if (!getComponent().getProtectContent()) {

			// FlowLayout
			if (getComponent().getLayout() == Layout.flow || getComponent().getLayout() == Layout.buttons) {
				final FlowLayoutConstraints beginPlaceHolderConstraints = new FlowLayoutConstraints();
				PlaceHolder beginPlaceHolder = new PlaceHolder(this, "<begin>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, beginPlaceHolderConstraints, 0);
					}
				};
				// registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints, 0);
				placeholders.add(beginPlaceHolder);
				beginPlaceHolder.setVisible(false);
				final FlowLayoutConstraints endPlaceHolderConstraints = new FlowLayoutConstraints();
				PlaceHolder endPlaceHolder = new PlaceHolder(this, "<end>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, endPlaceHolderConstraints,
								JFIBEditablePanelView.this.getComponent().getSubComponents().size());
					}
				};
				// registerComponentWithConstraints(endPlaceHolder, endPlaceHolderConstraints);
				placeholders.add(endPlaceHolder);
				endPlaceHolder.setVisible(false);
			}

			// BoxLayout

			if (getComponent().getLayout() == Layout.box) {
				final BoxLayoutConstraints beginPlaceHolderConstraints = new BoxLayoutConstraints();
				PlaceHolder beginPlaceHolder = new PlaceHolder(this, "<begin>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, beginPlaceHolderConstraints, 0);
					}
				};
				// registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints);
				placeholders.add(beginPlaceHolder);
				beginPlaceHolder.setVisible(false);
				final BoxLayoutConstraints endPlaceHolderConstraints = new BoxLayoutConstraints();
				PlaceHolder endPlaceHolder = new PlaceHolder(this, "<end>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, endPlaceHolderConstraints);
					}
				};
				// registerComponentWithConstraints(endPlaceHolder);
				placeholders.add(endPlaceHolder);
				endPlaceHolder.setVisible(false);
			}

			// BorderLayout
			if (getComponent().getLayout() == Layout.border) {

				BorderLayout bl = (BorderLayout) getJComponent().getLayout();
				BorderLayoutLocation[] placeholderLocations = { BorderLayoutLocation.north, BorderLayoutLocation.south,
						BorderLayoutLocation.center, BorderLayoutLocation.east, BorderLayoutLocation.west };
				for (final BorderLayoutLocation l : placeholderLocations) {
					boolean found = false;
					for (FIBComponent subComponent : getComponent().getSubComponents()) {
						BorderLayoutConstraints blc = (BorderLayoutConstraints) subComponent.getConstraints();
						if (blc.getLocation() == l) {
							found = true;
						}
					}
					if (!found) {
						PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + l.getConstraint() + ">") {
							@Override
							public void insertComponent(FIBComponent newComponent) {
								BorderLayoutConstraints blConstraints = new BorderLayoutConstraints(l);
								newComponent.setConstraints(blConstraints);
								JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent);
							}
						};
						// registerComponentWithConstraints(newPlaceHolder, l.getConstraint());
						newPlaceHolder.setVisible(false);
						placeholders.add(newPlaceHolder);
						logger.fine("Made placeholder for " + l.getConstraint());
					}
				}
			}

			// TwoColsLayout

			if (getComponent().getLayout() == Layout.twocols) {
				final TwoColsLayoutConstraints beginCenterPlaceHolderConstraints = new TwoColsLayoutConstraints(
						TwoColsLayoutLocation.center, true, false);
				PlaceHolder beginCenterPlaceHolder = new PlaceHolder(this, "<center>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, beginCenterPlaceHolderConstraints, 0);
					}
				};
				// registerComponentWithConstraints(beginCenterPlaceHolder, beginCenterPlaceHolderConstraints, 0);
				placeholders.add(beginCenterPlaceHolder);
				beginCenterPlaceHolder.setVisible(false);

				final TwoColsLayoutConstraints beginLeftPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left,
						true, false);
				final TwoColsLayoutConstraints beginRightPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right,
						true, false);
				PlaceHolder beginRightPlaceHolder = new PlaceHolder(this, "<right>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, beginRightPlaceHolderConstraints, 0);
						JFIBEditablePanelView.this.getComponent().addToSubComponents(editorController.getFactory().newFIBLabel("<left>"),
								beginLeftPlaceHolderConstraints, 0);
					}
				};
				// registerComponentWithConstraints(beginRightPlaceHolder, beginRightPlaceHolderConstraints, 0);
				placeholders.add(beginRightPlaceHolder);
				beginRightPlaceHolder.setVisible(false);
				PlaceHolder beginLeftPlaceHolder = new PlaceHolder(this, "<left>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(editorController.getFactory().newFIBLabel("<right>"),
								beginRightPlaceHolderConstraints, 0);
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, beginLeftPlaceHolderConstraints, 0);
					}
				};
				// registerComponentWithConstraints(beginLeftPlaceHolder, beginLeftPlaceHolderConstraints, 0);
				placeholders.add(beginLeftPlaceHolder);
				beginLeftPlaceHolder.setVisible(false);

				final TwoColsLayoutConstraints endCenterPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.center,
						true, false);
				PlaceHolder endCenterPlaceHolder = new PlaceHolder(this, "<center>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, endCenterPlaceHolderConstraints);
					}
				};
				// registerComponentWithConstraints(endCenterPlaceHolder, endCenterPlaceHolderConstraints);
				placeholders.add(endCenterPlaceHolder);
				endCenterPlaceHolder.setVisible(false);

				final TwoColsLayoutConstraints endLeftPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left,
						true, false);
				final TwoColsLayoutConstraints endRightPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right,
						true, false);
				PlaceHolder endLeftPlaceHolder = new PlaceHolder(this, "<left>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, endLeftPlaceHolderConstraints);
						JFIBEditablePanelView.this.getComponent().addToSubComponents(editorController.getFactory().newFIBLabel("<right>"),
								endRightPlaceHolderConstraints);
					}
				};
				// registerComponentWithConstraints(endLeftPlaceHolder, endLeftPlaceHolderConstraints);
				placeholders.add(endLeftPlaceHolder);
				endLeftPlaceHolder.setVisible(false);

				PlaceHolder endRightPlaceHolder = new PlaceHolder(this, "<right>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(editorController.getFactory().newFIBLabel("<left>"),
								endLeftPlaceHolderConstraints);
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, endRightPlaceHolderConstraints);
					}
				};
				// registerComponentWithConstraints(endRightPlaceHolder, endRightPlaceHolderConstraints);
				placeholders.add(endRightPlaceHolder);
				endRightPlaceHolder.setVisible(false);

			}

			// GridBagLayout

			if (getComponent().getLayout() == Layout.gridbag) {
				final GridBagLayoutConstraints beginPlaceHolderConstraints = new GridBagLayoutConstraints();
				PlaceHolder beginPlaceHolder = new PlaceHolder(this, "<begin>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, beginPlaceHolderConstraints, 0);
					}
				};
				// registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints, 0);
				placeholders.add(beginPlaceHolder);
				beginPlaceHolder.setVisible(false);
				final GridBagLayoutConstraints endPlaceHolderConstraints = new GridBagLayoutConstraints();
				PlaceHolder endPlaceHolder = new PlaceHolder(this, "<end>") {
					@Override
					public void insertComponent(FIBComponent newComponent) {
						JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent, endPlaceHolderConstraints);
					}
				};
				// registerComponentWithConstraints(endPlaceHolder);
				placeholders.add(endPlaceHolder);
				endPlaceHolder.setVisible(false);
			}

			// Now, we sort again subComponents, since we may have added some
			// placeholders
			/*
			 * if (getComponent().getLayout() == Layout.flow ||
			 * getComponent().getLayout() == Layout.box ||
			 * getComponent().getLayout() == Layout.twocols ||
			 * getComponent().getLayout() == Layout.gridbag) {
			 * Collections.sort(getSubComponents(), new Comparator<JComponent>()
			 * {
			 * 
			 * @Override public int compare(JComponent o1, JComponent o2) {
			 * Object c1 = getConstraints().get(o1); Object c2 =
			 * getConstraints().get(o2); if (c1 instanceof ComponentConstraints
			 * && c2 instanceof ComponentConstraints) { ComponentConstraints cc1
			 * = (ComponentConstraints) c1; ComponentConstraints cc2 =
			 * (ComponentConstraints) c2; return cc1.getIndex() -
			 * cc2.getIndex(); } return 0; } }); }
			 */

			// logger.info("******** Set DropTargets");
			if (getEditorController() != null) {
				for (PlaceHolder ph : placeholders) {
					System.out.println("Set DropTarget for " + ph);
					// Put right drop target
					// new FIBDropTarget(ph);
				}
			}
			/*
			 * else { SwingUtilities.invokeLater(new Runnable() {
			 * 
			 * @Override public void run() { updateLayout(); } }); }
			 */
		}
	}

	// Special case for GridLayout
	@Override
	protected JComponent getChildComponent(final int col, final int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return ((JFIBView<?, ?>) getController().viewForComponent(subComponent)).getResultingJComponent();
			}
		}

		/*if (!getComponent().getProtectContent()) {
			// Otherwise, it's a PlaceHolder
			PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + col + "," + row + ">") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					GridLayoutConstraints glConstraints = new GridLayoutConstraints(col, row);
					newComponent.setConstraints(glConstraints);
					JFIBEditablePanelView.this.getComponent().addToSubComponents(newComponent);
				}
			};
			newPlaceHolder.setVisible(false);
			placeholders.add(newPlaceHolder);
		
			return newPlaceHolder;
		}
		else {
			// Otherwise, it's an empty cell
			return new JPanel();
		}*/

		// Otherwise, it's an empty cell
		return new JPanel();

	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return placeholders;
	}

	@Override
	public FIBSwingEditableContainerViewDelegate<FIBPanel, JPanel> getDelegate() {
		return delegate;
	}

	@Override
	public List<PlaceHolder> makePlaceHolders() {
		System.out.println("Je suis sense calculer les placeholders pour la vue " + this + " size=" + getResultingJComponent().getSize());

		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(getResultingJComponent().getSize());
		panel.setSize(getResultingJComponent().getSize());
		Component center;
		Component north;
		panel.add(center = Box.createRigidArea(new Dimension(200, 200)), BorderLayout.CENTER);
		panel.add(north = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(30, 30);
			}
		}, BorderLayout.NORTH);

		System.out.println("center size = " + center.getSize());
		System.out.println("north size = " + north.getSize());
		panel.doLayout();
		System.out.println("center size = " + center.getSize());
		System.out.println("north size = " + north.getSize());

		return null;
	}
}

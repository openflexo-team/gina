/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

import java.awt.Dimension;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBLeaf;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBNode;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBSplit;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.layout.SplitLayoutConstraints;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerViewDelegate;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.view.container.JFIBSplitPanelView;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.MultiSplitLayout.Node;

public class JFIBEditableSplitPanelView extends JFIBSplitPanelView
		implements FIBSwingEditableContainerView<FIBSplitPanel, JXMultiSplitPane> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditableSplitPanelView.class.getPackage().getName());

	private final FIBSwingEditableContainerViewDelegate<FIBSplitPanel, JXMultiSplitPane> delegate;

	// private Vector<PlaceHolder> placeholders;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditableSplitPanelView(FIBSplitPanel model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBSwingEditableContainerViewDelegate<FIBSplitPanel, JXMultiSplitPane>(this);
	}

	@Override
	public void delete() {
		/*
		 * placeholders.clear(); placeholders = null;
		 */
		delegate.delete();
		super.delete();
	}

	private void appendPlaceHolder(final FIBLeaf n) {
		boolean found = false;
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (n.getName().equals(((SplitLayoutConstraints) subComponent.getConstraints()).getSplitIdentifier())) {
				found = true;
			}
		}
		if (!found) {

			final SplitLayoutConstraints splitLayoutConstraints = SplitLayoutConstraints.makeSplitLayoutConstraints(n.getName());
			PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + n.getName() + ">") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					System.out.println("getComponent=" + JFIBEditableSplitPanelView.this.getComponent());
					JFIBEditableSplitPanelView.this.getComponent().addToSubComponents(newComponent, splitLayoutConstraints);
				}
			};
			// registerComponentWithConstraints(newPlaceHolder, n.getName());
			newPlaceHolder.setVisible(false);
			// placeholders.add(newPlaceHolder);
			// logger.info("Made placeholder for " + n.getName());
		}

	}

	private void appendPlaceHolders(FIBSplit<?> s) {
		for (FIBNode n : s.getChildren()) {
			appendPlaceHolders(n);
		}
	}

	private void appendPlaceHolders(Node n) {
		if (n instanceof FIBSplit) {
			appendPlaceHolders((FIBSplit) n);
		}
		else if (n instanceof FIBLeaf) {
			appendPlaceHolder((FIBLeaf) n);
		}
	}

	/*
	 * @Override protected void retrieveContainedJComponentsAndConstraints() {
	 * if (placeholders == null) { placeholders = new Vector<PlaceHolder>(); }
	 * placeholders.removeAllElements();
	 * 
	 * super.retrieveContainedJComponentsAndConstraints();
	 * 
	 * appendPlaceHolders(getLayout().getModel());
	 * 
	 * // logger.info("******** Set DropTargets"); if (getEditorController() !=
	 * null) { for (PlaceHolder ph : placeholders) {
	 * logger.fine("Set DropTarget for " + ph); // Put right drop target // new
	 * FIBDropTarget(ph); } } }
	 */

	/*
	 * @Override public Vector<PlaceHolder> getPlaceHolders() { return
	 * placeholders; }
	 */

	@Override
	public FIBSwingEditableContainerViewDelegate<FIBSplitPanel, JXMultiSplitPane> getDelegate() {
		return delegate;
	}

	@Override
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize) {
		System.out.println("Je suis sense calculer les placeholders pour la vue " + this + " size=" + getResultingJComponent().getSize());
		return null;
	}

}

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

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBLeaf;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBNode;
import org.openflexo.gina.model.container.FIBMultiSplitLayoutFactory.FIBRowSplit;
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

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JFIBEditableSplitPanelView extends JFIBSplitPanelView
		implements FIBSwingEditableContainerView<FIBSplitPanel, JXMultiSplitPane> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditableSplitPanelView.class.getPackage().getName());

	private final FIBSwingEditableContainerViewDelegate<FIBSplitPanel, JXMultiSplitPane> delegate;

	private final FIBEditorController editorController;

	@Override
	public JComponent getDraggableComponent() {
		return getTechnologyComponent();
	}

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditableSplitPanelView(FIBSplitPanel model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;
		delegate = new FIBSwingEditableContainerViewDelegate<>(this);
	}

	@Override
	public void delete() {
		delegate.delete();
		super.delete();
	}

	@Override
	protected void paintAdditionalInfo(Graphics g) {
		if (getDelegate().getPlaceholders() != null) {
			for (PlaceHolder ph : getDelegate().getPlaceholders()) {
				ph.paint(g);
			}
		}
	}

	private void appendPlaceHolder(final FIBLeaf n, Rectangle current, List<PlaceHolder> placeHolders) {
		boolean found = false;
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (n.getName().equals(((SplitLayoutConstraints) subComponent.getConstraints()).getSplitIdentifier())) {
				found = true;
			}
		}
		if (!found) {
			final SplitLayoutConstraints splitLayoutConstraints = SplitLayoutConstraints.makeSplitLayoutConstraints(n.getName());
			PlaceHolder newPlaceHolder = new PlaceHolder(this, "<" + n.getName() + ">", current) {
				@Override
				public void insertComponent(FIBComponent newComponent, int oldIndex) {
					System.out.println("getComponent=" + JFIBEditableSplitPanelView.this.getComponent());
					JFIBEditableSplitPanelView.this.getComponent().addToSubComponents(newComponent, splitLayoutConstraints);
				}
			};
			newPlaceHolder.setVisible(false);
			placeHolders.add(newPlaceHolder);
		}
	}

	private void appendPlaceHolders(FIBSplit<?> s, Rectangle current, List<PlaceHolder> placeHolders) {
		int size = s.getChildren().size();
		boolean vertical = s instanceof FIBRowSplit;
		int width = vertical ? current.width/size : current.width;
		int height = vertical ? current.height : current.height/size;

		int x = 0, y = 0;
		for (FIBNode n : s.getChildren()) {
			Rectangle childSize = new Rectangle(x, y, width, height);
			appendPlaceHolders(n, childSize, placeHolders);
			if (vertical) {
				x += width;
			} else {
				y += height;
			}
		}
	}

	private void appendPlaceHolders(Node n, Rectangle current, List<PlaceHolder> placeHolders) {
		if (n instanceof FIBSplit) {
			appendPlaceHolders((FIBSplit) n, current, placeHolders);
		}
		else if (n instanceof FIBLeaf) {
			appendPlaceHolder((FIBLeaf) n, current, placeHolders);
		}
	}


	@Override
	public FIBSwingEditableContainerViewDelegate<FIBSplitPanel, JXMultiSplitPane> getDelegate() {
		return delegate;
	}

	@Override
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize) {
		List<PlaceHolder> result = new ArrayList<>();
		Dimension size = getJComponent().getSize();
		Rectangle origin = new Rectangle(0,0, size.width, size.height);
		appendPlaceHolders(this.getLayout().getModel(), origin, result);
		return result;
	}

}

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

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerViewDelegate;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.OperatorDecorator;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.editor.view.container.layout.JEditableBorderLayout;
import org.openflexo.gina.swing.editor.view.container.layout.JEditableBoxLayout;
import org.openflexo.gina.swing.editor.view.container.layout.JEditableFlowLayout;
import org.openflexo.gina.swing.editor.view.container.layout.JEditableGridBagLayout;
import org.openflexo.gina.swing.editor.view.container.layout.JEditableGridLayout;
import org.openflexo.gina.swing.editor.view.container.layout.JEditableTwoColsLayout;
import org.openflexo.gina.swing.editor.view.container.layout.JFIBEditableLayoutManager;
import org.openflexo.gina.swing.view.container.JFIBPanelView;
import org.openflexo.gina.swing.view.container.layout.JAbsolutePositionningLayout;
import org.openflexo.gina.swing.view.container.layout.JButtonLayout;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.operator.FIBIterationView;
import org.openflexo.logging.FlexoLogger;

public class JFIBEditablePanelView extends JFIBPanelView implements FIBSwingEditableContainerView<FIBPanel, JPanel> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditablePanelView.class.getPackage().getName());

	private final FIBSwingEditableContainerViewDelegate<FIBPanel, JPanel> delegate;

	private final FIBEditorController editorController;

	@Override
	public JComponent getDraggableComponent() {
		return getTechnologyComponent();
	}

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditablePanelView(final FIBPanel model, FIBEditorController editorController) {
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
	public FIBSwingEditableContainerViewDelegate<FIBPanel, JPanel> getDelegate() {
		return delegate;
	}

	@Override
	protected void paintAdditionalInfo(Graphics g) {
		if (getDelegate().getPlaceholders() != null) {
			for (PlaceHolder ph : getDelegate().getPlaceholders()) {
				ph.paint(g);
			}
		}
		if (getDelegate().getOperatorDecorators() != null) {
			for (OperatorDecorator operatorDecorator : getDelegate().getOperatorDecorators()) {
				operatorDecorator.paint(g);
			}
		}
	}

	@Override
	public FIBLayoutManager<JPanel, JComponent, ?> makeFIBLayoutManager(Layout layoutType) {
		if (layoutType == null) {
			return super.makeFIBLayoutManager(layoutType);
		}
		switch (layoutType) {
			case none:
				return new JAbsolutePositionningLayout(this);
			case border:
				return new JEditableBorderLayout(this);
			case box:
				return new JEditableBoxLayout(this);
			case flow:
				return new JEditableFlowLayout(this);
			case buttons:
				return new JButtonLayout(this);
			case twocols:
				return new JEditableTwoColsLayout(this);
			case grid:
				return new JEditableGridLayout(this);
			case gridbag:
				return new JEditableGridBagLayout(this);
			default:
				return super.makeFIBLayoutManager(layoutType);
		}

	}

	@Override
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize) {
		if (getLayoutManager() instanceof JFIBEditableLayoutManager) {
			return ((JFIBEditableLayoutManager<JPanel, JComponent, ?>) getLayoutManager()).makePlaceHolders(preferredSize);
		}
		return Collections.emptyList();
	}

	@Override
	public List<OperatorDecorator> makeOperatorDecorators() {
		List<OperatorDecorator> returned = new ArrayList<>();
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (subComponent instanceof FIBIteration) {
				List<FIBView<?, ?>> subViews = new ArrayList<>();
				FIBIterationView<?, ?> iterationView = (FIBIterationView<?, ?>) getSubViewsMap().get(subComponent);
				System.out.println("iterationView=" + iterationView);
				if (iterationView.getComponent().getSubComponents().size() > 0) {
					subViews.addAll(iterationView.getSubViews());
					System.out.println("hop: " + iterationView.getSubViews());
				}
				else {
					subViews.add(iterationView);
					System.out.println("hop2: " + iterationView);
				}
				OperatorDecorator newIterationDecorator = new OperatorDecorator(this, (FIBIteration) subComponent, subViews);
				returned.add(newIterationDecorator);
			}
		}
		return returned;
	}

	@Override
	public void updateLayout() {
		delegate.updateOperatorDecorators();
		super.updateLayout();
	}

	@Override
	public void changeLayout() {
		delegate.updateOperatorDecorators();
		super.changeLayout();
	}

	private boolean operatorContentsStart = false;

	// TODO: avoid code duplication in FIBSwingEditableView
	@Override
	public boolean isOperatorContentsStart() {
		return operatorContentsStart;
	}

	// TODO: avoid code duplication in FIBSwingEditableView
	@Override
	public void setOperatorContentsStart(boolean operatorContentsStart) {
		if (operatorContentsStart != this.operatorContentsStart) {
			this.operatorContentsStart = operatorContentsStart;
			FIBSwingEditableView.updateOperatorContentsStart(this, operatorContentsStart);
			getPropertyChangeSupport().firePropertyChange("operatorContentsStart", !operatorContentsStart, operatorContentsStart);
		}
	}

}

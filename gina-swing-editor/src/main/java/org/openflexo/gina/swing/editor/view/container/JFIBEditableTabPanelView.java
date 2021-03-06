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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerViewDelegate;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.OperatorDecorator;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.view.container.JFIBTabPanelView;
import org.openflexo.logging.FlexoLogger;

public class JFIBEditableTabPanelView extends JFIBTabPanelView implements FIBSwingEditableContainerView<FIBTabPanel, JTabbedPane> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditableTabPanelView.class.getPackage().getName());

	private final FIBSwingEditableContainerViewDelegate<FIBTabPanel, JTabbedPane> delegate;

	private final FIBEditorController editorController;

	@Override
	public JComponent getDraggableComponent() {
		return getTechnologyComponent();
	}

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditableTabPanelView(FIBTabPanel model, FIBEditorController editorController) {
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
	public FIBSwingEditableContainerViewDelegate<FIBTabPanel, JTabbedPane> getDelegate() {
		return delegate;
	}

	@Override
	public JTabbedPane getJComponent() {
		return super.getJComponent();
	}

	@Override
	public void addSubComponentsAndDoLayout() {
		super.addSubComponentsAndDoLayout();
		getJComponent().addTab("+", new JPanel());
		getJComponent().setModel(new DefaultSingleSelectionModel() {
			@Override
			public void setSelectedIndex(int index) {
				if (getJComponent().getTabCount() > 1) {
					index = Math.min(index, getJComponent().getTabCount() - 2);
				}
				super.setSelectedIndex(index);
			}
		});
		getJComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int tab = getJComponent().getUI().tabForCoordinate(getJComponent(), e.getX(), e.getY());
				if (tab == getJComponent().getTabCount() - 1) {
					FIBTab newTabComponent = editorController.getFactory().newFIBTab();
					newTabComponent.setLayout(Layout.border);
					newTabComponent.setTitle("NewTab");
					newTabComponent.finalizeDeserialization();
					getComponent().addToSubComponents(newTabComponent);
				}
			}
		});
	}

	@Override
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize) {
		return Collections.emptyList();
	}

	@Override
	public List<OperatorDecorator> makeOperatorDecorators() {
		return Collections.emptyList();
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

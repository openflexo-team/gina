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

package org.openflexo.gina.swing.editor.view.widget;

import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableViewDelegate;
import org.openflexo.gina.swing.view.widget.JFIBTableWidget;
import org.openflexo.gina.swing.view.widget.JTablePanel;
import org.openflexo.logging.FlexoLogger;

public class JFIBEditableTableWidget<T> extends JFIBTableWidget<T> implements FIBSwingEditableView<FIBTable, JTablePanel<T>> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(JFIBEditableTableWidget.class.getPackage().getName());

	private final FIBSwingEditableViewDelegate<FIBTable, JTablePanel<T>> delegate;

	private final FIBEditorController editorController;

	@Override
	public JComponent getDraggableComponent() {
		return getTechnologyComponent();
	}

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditableTableWidget(FIBTable model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBSwingEditableViewDelegate<>(this);
	}

	@Override
	public synchronized void delete() {
		delegate.delete();
		super.delete();
	}

	@Override
	public FIBSwingEditableViewDelegate<FIBTable, JTablePanel<T>> getDelegate() {
		return delegate;
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

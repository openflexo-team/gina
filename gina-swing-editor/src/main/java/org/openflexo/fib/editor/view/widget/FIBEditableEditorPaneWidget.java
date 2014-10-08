/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.editor.view.widget;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JEditorPane;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBEditorPane;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.widget.FIBEditorPaneWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableEditorPaneWidget extends FIBEditorPaneWidget implements FIBEditableView<FIBEditorPane, JEditorPane> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(FIBEditableEditorPaneWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBEditorPane, JEditorPane> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableEditorPaneWidget(FIBEditorPane model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBEditorPane, JEditorPane>(this);
		model.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void delete() {
		delegate.delete();
		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.delete();
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return null;
	}

	@Override
	public FIBEditableViewDelegate<FIBEditorPane, JEditorPane> getDelegate() {
		return delegate;
	}

	@Override
	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		if (isDeleted()) {
			return;
		}
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if (propertyName.equals(FIBEditorPane.CONTENT_TYPE_KEY)) {
			updateContentType();
		}
		if (propertyName.equals(FIBLabel.LABEL_KEY)) {
			relayoutParentBecauseLabelChanged();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

	protected void relayoutParentBecauseLabelChanged() {
		FIBContainerView<?, ?, ?> parentView = getParentView();
		FIBEditorController controller = getEditorController();
		parentView.updateLayout();
		controller.notifyFocusedAndSelectedObject();
	}

}

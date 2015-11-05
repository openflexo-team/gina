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

package org.openflexo.fib.editor.view.widget;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.widget.FIBRadioButtonList;
import org.openflexo.fib.swing.utils.swing.view.widget.FIBRadioButtonListWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableRadioButtonListWidget<T> extends FIBRadioButtonListWidget<T> implements FIBEditableView<FIBRadioButtonList, JPanel> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(FIBEditableRadioButtonListWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBRadioButtonList, JPanel> delegate;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableRadioButtonListWidget(FIBRadioButtonList model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBEditableViewDelegate<FIBRadioButtonList, JPanel>(this);
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
	public FIBEditableViewDelegate<FIBRadioButtonList, JPanel> getDelegate() {
		return delegate;
	}

	@Override
	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {
		if (isDeleted()) {
			return;
		}
		super.receivedModelNotifications(o, propertyName, oldValue, newValue);
		if ((propertyName.equals(FIBWidget.FORMAT_KEY)) || (propertyName.equals(FIBWidget.LOCALIZE_KEY))
				|| (propertyName.equals(FIBRadioButtonList.COLUMNS_KEY)) || (propertyName.equals(FIBRadioButtonList.H_GAP_KEY))
				|| (propertyName.equals(FIBRadioButtonList.V_GAP_KEY))) {
			rebuildRadioButtons();
		}
		delegate.receivedModelNotifications(o, propertyName, oldValue, newValue);
	}

}

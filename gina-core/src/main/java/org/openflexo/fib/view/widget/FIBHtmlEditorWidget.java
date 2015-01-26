/**
 * 
 * Copyright (c) 2013-2014, Openflexo
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

package org.openflexo.fib.view.widget;

import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBHtmlEditor;
import org.openflexo.fib.model.FIBHtmlEditorOption;
import org.openflexo.fib.view.FIBWidgetView;

import com.metaphaseeditor.MetaphaseEditorConfiguration;
import com.metaphaseeditor.MetaphaseEditorPanel;

/**
 * Represents a widget able to edit an HTML fragment
 * 
 * @author sylvain
 */
public class FIBHtmlEditorWidget extends FIBWidgetView<FIBHtmlEditor, MetaphaseEditorPanel, String> {

	private static final Logger LOGGER = Logger.getLogger(FIBHtmlEditorWidget.class.getPackage().getName());

	private MetaphaseEditorConfiguration _editorConfiguration;
	private final MetaphaseEditorPanel _editor;
	boolean validateOnReturn;

	public FIBHtmlEditorWidget(FIBHtmlEditor model, FIBController controller) {
		super(model, controller);
		_editor = new MetaphaseEditorPanel(_editorConfiguration = buildConfiguration()) {
			@Override
			public void documentWasEdited() {
				super.documentWasEdited();
				updateModelFromWidget();
			}
		};
		/* _editor.getDocument().addDocumentListener(new DocumentListener() {
		     public void changedUpdate(DocumentEvent e)
		     {
		          if ((!validateOnReturn) && (!widgetUpdating))
		              updateModelFromWidget();
		     }

		     public void insertUpdate(DocumentEvent e)
		     {
		         if ((!validateOnReturn) && (!widgetUpdating))
		             updateModelFromWidget();
		     }

		     public void removeUpdate(DocumentEvent e)
		     {
		         if ((!validateOnReturn) && (!widgetUpdating))
		             updateModelFromWidget();
		     }
		 });*/
		updateFont();
	}

	protected void updateHtmlEditorConfiguration() {
		_editor.updateComponents(_editorConfiguration = buildConfiguration());
	}

	private MetaphaseEditorConfiguration buildConfiguration() {
		MetaphaseEditorConfiguration returned = new MetaphaseEditorConfiguration();
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine1()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(1));
		}
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine2()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(2));
		}
		for (FIBHtmlEditorOption o : getComponent().getOptionsInLine3()) {
			returned.addToOptions(o.makeMetaphaseEditorOption(3));
		}
		return returned;
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		// _editor.selectAll();
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), _editor.getDocument())) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(_editor.getDocument())) {
				return false;
			}
			widgetUpdating = true;
			if (getValue() != null) {
				_editor.setDocument(getValue());
			} else {
				_editor.setDocument("");
			}
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		// System.out.println("updateModelFromWidget() with " + _editor.getDocument());
		if (notEquals(getValue(), _editor.getDocument())) {
			modelUpdating = true;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateModelFromWidget() in TextAreaWidget");
			}
			setValue(_editor.getDocument());
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public MetaphaseEditorPanel getJComponent() {
		return _editor;
	}

	@Override
	public MetaphaseEditorPanel getDynamicJComponent() {
		return _editor;
	}

	@Override
	final public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			_editor.setFont(getFont());
		}
	}
}

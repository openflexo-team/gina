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

package org.openflexo.gina.view.widget.impl;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBEditorWidget;

/**
 * Represents a widget able to edit a Text (more than one line) object using a syntax-colored editor (eg code editor)
 *
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBEditorWidgetImpl<C> extends FIBWidgetViewImpl<FIBEditor, C, String> implements FIBEditorWidget<C> {

	private static final Logger LOGGER = Logger.getLogger(FIBEditorWidgetImpl.class.getPackage().getName());

	protected boolean validateOnReturn;

	public FIBEditorWidgetImpl(FIBEditor model, FIBController controller, EditorWidgetRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		validateOnReturn = model.isValidateOnReturn();
		// updateFont();
		// updateTokenMarkerStyle();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateTokenMarkerStyle();
	}

	@Override
	public EditorWidgetRenderingAdapter<C> getRenderingAdapter() {
		return (EditorWidgetRenderingAdapter<C>) super.getRenderingAdapter();
	}

	/*@Override
	public synchronized boolean updateWidgetFromModel() {
	
		String editedText = getRenderingAdapter().getText(getTechnologyComponent());
	
		if (notEquals(getValue(), editedText)) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(editedText)) {
				return false;
			}
			widgetUpdating = true;
			try {
				int caretPosition = getRenderingAdapter().getCaretPosition(getTechnologyComponent());
				getRenderingAdapter().setText(getTechnologyComponent(), getValue());
				if (getValue() != null) {
					getRenderingAdapter().setCaretPosition(getTechnologyComponent(),
							caretPosition < getValue().length() ? caretPosition : getValue().length());
				}
			} finally {
				widgetUpdating = false;
			}
			return true;
		}
		return false;
	}*/

	@Override
	public String updateData() {

		String newText = super.updateData();

		String currentWidgetValue = getRenderingAdapter().getText(getTechnologyComponent());
		if (notEquals(newText, currentWidgetValue)) {
			if (modelUpdating) {
				return newText;
			}
			if (newText != null && (newText + "\n").equals(currentWidgetValue)) {
				return newText;
			}
			// widgetUpdating = true;
			// try {
			int caretPosition = getRenderingAdapter().getCaretPosition(getTechnologyComponent());
			getRenderingAdapter().setText(getTechnologyComponent(), newText);
			if (caretPosition > -1 && newText != null) {
				getRenderingAdapter().setCaretPosition(getTechnologyComponent(),
						caretPosition < getValue().length() ? caretPosition : getValue().length());
			}
			// } finally {
			// widgetUpdating = false;
			// }
		}
		return newText;
	}

	private boolean modelUpdating = false;

	protected boolean textChanged() {
		String editedText = getRenderingAdapter().getText(getTechnologyComponent());

		if (notEquals(getValue(), editedText)) {
			modelUpdating = true;
			try {
				setValue(editedText);
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	final public void updateTokenMarkerStyle() {
		if (getWidget().getSyntaxStyle() != null) {
			getRenderingAdapter().setSyntaxStyle(getTechnologyComponent(), getWidget().getSyntaxStyle());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBEditor.SYNTAX_STYLE_KEY)) {
			updateTokenMarkerStyle();
		}

		super.propertyChange(evt);
	}

}

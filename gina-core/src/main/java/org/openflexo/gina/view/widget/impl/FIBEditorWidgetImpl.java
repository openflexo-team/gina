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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.model.widget.FIBEditor.FIBTokenMarkerStyle;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBEditorWidget;
import org.openflexo.jedit.BatchFileTokenMarker;
import org.openflexo.jedit.CCTokenMarker;
import org.openflexo.jedit.CTokenMarker;
import org.openflexo.jedit.EiffelTokenMarker;
import org.openflexo.jedit.FMLTokenMarker;
import org.openflexo.jedit.HTMLTokenMarker;
import org.openflexo.jedit.IDLTokenMarker;
import org.openflexo.jedit.JavaScriptTokenMarker;
import org.openflexo.jedit.JavaTokenMarker;
import org.openflexo.jedit.PHPTokenMarker;
import org.openflexo.jedit.PatchTokenMarker;
import org.openflexo.jedit.PerlTokenMarker;
import org.openflexo.jedit.PropsTokenMarker;
import org.openflexo.jedit.PythonTokenMarker;
import org.openflexo.jedit.SQLTokenMarker;
import org.openflexo.jedit.ShellScriptTokenMarker;
import org.openflexo.jedit.TSQLTokenMarker;
import org.openflexo.jedit.TeXTokenMarker;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.jedit.WODTokenMarker;
import org.openflexo.jedit.XMLTokenMarker;

/**
 * Represents a widget able to edit a Text (more than one line) object using a syntax-colored editor (eg code editor)
 *
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBEditorWidgetImpl<C> extends FIBWidgetViewImpl<FIBEditor, C, String>implements FIBEditorWidget<C> {

	private static final Logger LOGGER = Logger.getLogger(FIBEditorWidgetImpl.class.getPackage().getName());

	protected boolean validateOnReturn;

	public FIBEditorWidgetImpl(FIBEditor model, FIBController controller, EditorWidgetRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		validateOnReturn = model.isValidateOnReturn();
		updateFont();
		updateTokenMarkerStyle();
	}

	@Override
	public EditorWidgetRenderingAdapter<C> getRenderingAdapter() {
		return (EditorWidgetRenderingAdapter<C>) super.getRenderingAdapter();
	}

	@Override
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
				getRenderingAdapter().setCaretPosition(getTechnologyComponent(),
						caretPosition < getValue().length() ? caretPosition : getValue().length());
			} finally {
				widgetUpdating = false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {

		String editedText = getRenderingAdapter().getText(getTechnologyComponent());

		if (notEquals(getValue(), editedText)) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateModelFromWidget() in TextAreaWidget");
			}
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
		if (getWidget().getTokenMarkerStyle() != null) {
			getRenderingAdapter().setTokenMarker(getTechnologyComponent(), makeTokenMarker(getWidget().getTokenMarkerStyle()));
		}
	}

	public static TokenMarker makeTokenMarker(FIBTokenMarkerStyle tokenMarkerStyle) {
		if (tokenMarkerStyle == FIBTokenMarkerStyle.BatchFile) {
			return new BatchFileTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.C) {
			return new CTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.CC) {
			return new CCTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.IDL) {
			return new IDLTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.JavaScript) {
			return new JavaScriptTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.Java) {
			return new JavaTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.Eiffel) {
			return new EiffelTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.HTML) {
			return new HTMLTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.Patch) {
			return new PatchTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.Perl) {
			return new PerlTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.PHP) {
			return new PHPTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.Props) {
			return new PropsTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.Python) {
			return new PythonTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.ShellScript) {
			return new ShellScriptTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.SQL) {
			return new SQLTokenMarker(null);
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.TSQL) {
			return new TSQLTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.TeX) {
			return new TeXTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.WOD) {
			return new WODTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.XML) {
			return new XMLTokenMarker();
		}
		else if (tokenMarkerStyle == FIBTokenMarkerStyle.FML) {
			return new FMLTokenMarker();
		}
		else {
			return null;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBEditor.TOKEN_MARKER_STYLE_KEY)) {
			updateTokenMarkerStyle();
		}

		super.propertyChange(evt);
	}

}

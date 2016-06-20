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

package org.openflexo.gina.view.widget;

import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.widget.FIBGenericTextWidget.GenericTextRenderingAdapter;
import org.openflexo.jedit.TokenMarker;

/**
 * Represents a widget able to edit a Text (more than one line) object using a syntax-aware and syntax-colored editor (eg code editor)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public interface FIBEditorWidget<C> extends FIBWidgetView<FIBEditor, C, String> {

	@Override
	public EditorWidgetRenderingAdapter<C> getRenderingAdapter();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface EditorWidgetRenderingAdapter<C> extends GenericTextRenderingAdapter<C> {

		public int getColumns(C component);

		public void setColumns(C component, int columns);

		public int getRows(C component);

		public void setRows(C component, int rows);

		public TokenMarker getTokenMarker(C component);

		public void setTokenMarker(C component, TokenMarker tokenMarker);

	}

}

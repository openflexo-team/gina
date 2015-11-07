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

import java.awt.Font;

import org.openflexo.gina.model.widget.FIBFont;
import org.openflexo.gina.view.FIBWidgetView;

/**
 * Represents a widget able to select a Font<br>
 * When allowNull set to true, might select a null value
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public interface FIBFontWidget<C> extends FIBWidgetView<FIBFont, C, Font> {

	@Override
	public FontWidgetRenderingAdapter<C> getRenderingAdapter();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface FontWidgetRenderingAdapter<C> extends RenderingAdapter<C> {

		public Font getSelectedFont(C component);

		public void setSelectedFont(C component, Font aFont);

		public boolean isCheckboxVisible(C component);

		public void setCheckboxVisible(C component, boolean visible);

		public boolean isCheckboxSelected(C component);

		public void setCheckboxSelected(C component, boolean selected);

		public boolean isCheckboxEnabled(C component);

		public void setCheckboxEnabled(C component, boolean enabled);

	}

}
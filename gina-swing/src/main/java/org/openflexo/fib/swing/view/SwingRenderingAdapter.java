/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

package org.openflexo.fib.swing.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;

import org.openflexo.fib.view.FIBView.RenderingAdapter;

/**
 * A {@link RenderingAdapter} implementation dedicated for Swing
 * 
 * @author sylvain
 * 
 */
public class SwingRenderingAdapter<J extends JComponent> implements RenderingAdapter<J> {

	@Override
	public Color getForegroundColor(J component) {
		return component.getForeground();
	}

	@Override
	public void setForegroundColor(J component, Color aColor) {
		component.setForeground(aColor);
	}

	@Override
	public Color getBackgroundColor(J component) {
		return component.getBackground();
	}

	@Override
	public void setBackgroundColor(J component, Color aColor) {
		component.setBackground(aColor);
	}

	@Override
	public Font getFont(J component) {
		return component.getFont();
	}

	@Override
	public void setFont(J component, Font aFont) {
		component.setFont(aFont);
	}

	@Override
	public String getToolTipText(J component) {
		return component.getToolTipText();
	}

	@Override
	public void setToolTipText(J component, String aText) {
		component.setToolTipText(aText);
	}

	@Override
	public boolean isOpaque(J component) {
		return component.isOpaque();
	}

	@Override
	public void setOpaque(J component, boolean opaque) {
		component.setOpaque(opaque);
	}

	@Override
	public void requestFocus(J component) {
		component.requestFocus();
	}

	@Override
	public void requestFocusInParent(J component) {
		component.getParent().requestFocus();
	}

}

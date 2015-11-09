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

package org.openflexo.gina.swing.editor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.Focusable;
import org.openflexo.swing.NoInsetsBorder;

public abstract class PlaceHolder extends JPanel implements Focusable {

	static final Logger logger = FlexoLogger.getLogger(PlaceHolder.class.getPackage().getName());

	private static final Border focusBorder = new NoInsetsBorder(BorderFactory.createLineBorder(Color.RED));
	private static final Border nonFocusBorder = new NoInsetsBorder(BorderFactory.createEtchedBorder());

	private boolean isFocused = false;

	private final FIBSwingEditableContainerView<?, ?> view;
	private final String text;

	public PlaceHolder(FIBSwingEditableContainerView<?, ?> view, String text) {
		super(new BorderLayout());
		this.view = view;
		this.text = text;
		JLabel label = new JLabel(text);
		label.setForeground(Color.DARK_GRAY);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		add(label, BorderLayout.CENTER);
		setBorder(nonFocusBorder);
	}

	@Override
	public void setFocused(boolean aFlag) {
		if (aFlag) {
			isFocused = true;
			setBorder(focusBorder);
		}
		else {
			isFocused = false;
			setBorder(nonFocusBorder);
		}
	}

	@Override
	public boolean isFocused() {
		return isFocused;
	}

	public FIBSwingEditableContainerView<?, ?> getView() {
		return view;
	}

	@Override
	public String toString() {
		return "PlaceHolder:[" + text + "]";
	}

	public abstract void insertComponent(FIBComponent newComponent);

	public void willDelete() {
		getView().getJComponent().remove(this);
		getView().getPlaceHolders().remove(this);
	}

	public void hasDeleted() {
		/*if (getView().getJComponent() instanceof JPanel && ((JPanel)getView().getJComponent()).getLayout() instanceof BorderLayout) {
			System.out.println("Bon, qu'est ce qu'on a la ?");
			BorderLayout bl = (BorderLayout)(((JPanel)getView().getJComponent()).getLayout());
			for (Component c : getView().getJComponent().getComponents()) {
				System.out.println("> Hop: "+c+" "+bl.getConstraints(c));
			}
		}*/

	}

}

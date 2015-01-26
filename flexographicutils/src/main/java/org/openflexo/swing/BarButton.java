/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.toolbox.ToolBox;

public class BarButton extends JButton {

	private boolean internallySelected = false;
	private Color defaultBackgroundColor;

	public BarButton(Action a) {
		this();
		setAction(a);
	}

	public BarButton(Icon icon) {
		this(null, icon);
	}

	public BarButton(String text, Icon icon) {
		super(text, icon);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (isEnabled()) {
					setContentAreaFilled(true);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setContentAreaFilled(false);
			}
		});
		updateInternalUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		updateInternalUI();
	}

	private void updateInternalUI() {
		setEnabled(true);
		setBorderPainted(false);
		setRolloverEnabled(true);
		setContentAreaFilled(false);
		setOpaque(false);
		defaultBackgroundColor = getBackground();
	}

	public BarButton(String text) {
		this(text, null);
	}

	public BarButton() {
		this(null, null);
	}

	@Override
	public void setContentAreaFilled(boolean b) {
		b |= internallySelected;
		super.setContentAreaFilled(b);
		setOpaque(b);
	}

	@Override
	public void setSelected(boolean b) {
		internallySelected = b;
		if (ToolBox.isMacOSLaf()) {
			if (b) {
				setBackground(defaultBackgroundColor.darker());
			} else {
				setBackground(defaultBackgroundColor);
			}
		} else {
			super.setSelected(b);
		}
		setContentAreaFilled(b);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				JMenuBar panel = new JMenuBar();
				panel.setBackground(UIManager.getDefaults().getColor("ToolBar.floatingForeground"));
				frame.add(panel);
				for (int i = 0; i < 10; i++) {
					BarButton bar = new BarButton(UtilsIconLibrary.UK_FLAG);
					bar.setSelected(i == 1);
					panel.add(bar);
				}
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}

/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class WindowSynchronizer implements ComponentListener, WindowListener {

	private List<Window> synchronizedWindows;
	private List<Window> shownWindows;
	private Window activeWindow;

	private Dimension size;
	private Point location;

	public WindowSynchronizer() {
		synchronizedWindows = new ArrayList<>();
		shownWindows = new ArrayList<>();
	}

	public void addToSynchronizedWindows(Window window) {
		if (!synchronizedWindows.contains(window)) {
			synchronizedWindows.add(window);
			if (window.isVisible()) {
				shownWindows.add(window);
			}
			window.addWindowListener(this);
			window.addComponentListener(this);
		}
	}

	public void removeFromSynchronizedWindows(Window window) {
		synchronizedWindows.remove(window);
		shownWindows.remove(window);
		window.removeWindowListener(this);
		window.removeComponentListener(this);
		if (activeWindow == window) {
			activeWindow = null;
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		resetIfNeeded(e);
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
		activeWindow = e.getWindow();
		if (activeWindow != null) {
			if (size != null) {
				activeWindow.setSize(size);
			}
			else {
				size = activeWindow.getSize();
			}
			if (location != null) {
				activeWindow.setLocation(location);
			}
			else {
				location = activeWindow.getLocation();
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		resetIfNeeded(e);
	}

	protected void resetIfNeeded(WindowEvent e) {
		if (e.getWindow() == activeWindow) {
			activeWindow = null;
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getComponent() == activeWindow) {
			size = activeWindow.getSize();
			location = activeWindow.getLocation();
			updateSizeForWindowsBut(activeWindow);
		}

	}

	private void updateSizeForWindowsBut(Window activeWindow) {
		for (Window w : shownWindows) {
			if (w != activeWindow) {
				w.setSize(size);
			}
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		if (e.getComponent() == activeWindow) {
			size = activeWindow.getSize();
			location = activeWindow.getLocation();
			updateLocationForWindowsBut(activeWindow);
		}
	}

	private void updateLocationForWindowsBut(Window activeWindow) {
		for (Window w : shownWindows) {
			if (w != activeWindow) {
				w.setLocation(location);
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		shownWindows.add((Window) e.getComponent());
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		shownWindows.remove(e.getComponent());
	}

}

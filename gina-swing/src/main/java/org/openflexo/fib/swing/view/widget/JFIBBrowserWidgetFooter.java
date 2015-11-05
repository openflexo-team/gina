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

package org.openflexo.fib.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

import org.openflexo.fib.model.widget.FIBBrowserAction;
import org.openflexo.fib.model.widget.FIBBrowserElement;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.fib.view.widget.browser.impl.FIBBrowserActionListener;
import org.openflexo.fib.view.widget.browser.impl.FIBBrowserElementType;
import org.openflexo.fib.view.widget.browser.impl.FIBBrowserFilterMenuItem;
import org.openflexo.fib.view.widget.browser.impl.FIBBrowserWidgetFooterImpl;
import org.openflexo.fib.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.swing.ImageButton;

/**
 * Swing implementation for a browser widget footer (the footer is synchronized with the selection of browser)
 * 
 * @param <T>
 *            type of objects managed by this footer
 * 
 * @author sylvain
 */
public class JFIBBrowserWidgetFooter<T> extends FIBBrowserWidgetFooterImpl<JPanel, T>implements MouseListener, WindowListener {

	protected static final Logger logger = Logger.getLogger(JFIBBrowserWidgetFooter.class.getPackage().getName());

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;

	private final BrowserButton plusButton;
	private final BrowserButton minusButton;
	private final BrowserButton optionsButton;
	private final JButton filtersButton;

	private final JPanel footerPanel;

	public JFIBBrowserWidgetFooter(FIBBrowserWidgetImpl<?, T> widget) {
		super(widget);

		initializeActions(widget);

		footerPanel = new JPanel();
		footerPanel.setOpaque(false);
		footerPanel.setBorder(BorderFactory.createEmptyBorder());
		footerPanel.setLayout(new BorderLayout());

		plusButton = new BrowserButton(_addActions, FIBIconLibrary.BROWSER_PLUS_ICON, FIBIconLibrary.BROWSER_PLUS_DISABLED_ICON,
				FIBIconLibrary.BROWSER_PLUS_SELECTED_ICON);
		minusButton = new BrowserButton(_removeActions, FIBIconLibrary.BROWSER_MINUS_ICON, FIBIconLibrary.BROWSER_MINUS_DISABLED_ICON,
				FIBIconLibrary.BROWSER_MINUS_SELECTED_ICON);
		optionsButton = new BrowserButton(_otherActions, FIBIconLibrary.BROWSER_OPTIONS_ICON, FIBIconLibrary.BROWSER_OPTIONS_DISABLED_ICON,
				FIBIconLibrary.BROWSER_OPTIONS_SELECTED_ICON);

		filtersButton = new ImageButton(FIBIconLibrary.BROWSER_FILTERS_ICON);
		filtersButton.setDisabledIcon(FIBIconLibrary.BROWSER_FILTERS_DISABLED_ICON);
		filtersButton.setPressedIcon(FIBIconLibrary.BROWSER_FILTERS_SELECTED_ICON);
		filtersButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point point = filtersButton.getLocationOnScreen();
				JPopupMenu popupMenu = getFiltersPopupMenu();
				popupMenu.pack();
				point.y -= popupMenu.getHeight();
				popupMenu.setInvoker(filtersButton);
				popupMenu.setLocation(point);
				popupMenu.setVisible(true);
			}
		});
		JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		plusMinusPanel.add(plusButton.getButton());
		plusMinusPanel.add(minusButton.getButton());
		plusMinusPanel.setOpaque(false);
		JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		optionsPanel.add(optionsButton.getButton());
		optionsPanel.setOpaque(false);
		if (hasFilters()) {
			optionsPanel.add(filtersButton);
		}

		footerPanel.add(plusMinusPanel, BorderLayout.WEST);
		footerPanel.add(optionsPanel, BorderLayout.EAST);

		footerPanel.revalidate();
	}

	@Override
	public JPanel getFooterComponent() {
		return footerPanel;
	}

	@Override
	public void setFocusedObject(Object object) {
		plusButton.handleSelectionChanged(object);
		minusButton.handleSelectionChanged(object);
		optionsButton.handleSelectionChanged(object);
	}

	private JPopupMenu filtersPopupMenu;

	public JPopupMenu getFiltersPopupMenu() {
		if (filtersPopupMenu == null) {
			filtersPopupMenu = makeFiltersPopupMenu();
		}
		for (Component menuItem : filtersPopupMenu.getComponents()) {
			if (menuItem instanceof FIBBrowserFilterMenuItem) {
				((FIBBrowserFilterMenuItem) menuItem).update();
			}
		}
		return filtersPopupMenu;
	}

	private boolean hasFilters() {
		for (FIBBrowserElement el : _widget.getComponent().getElements()) {
			if (el.getFiltered()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("serial")
	private JPopupMenu makeFiltersPopupMenu() {

		JPopupMenu returned = new JPopupMenu() {
			@Override
			public void setVisible(boolean b) {
				if (b && !isVisible()) {
					addPopupClosers(getWindow(footerPanel));
				}
				else if (!b && isVisible()) {
					removePopupClosers(getWindow(footerPanel));
				}
				super.setVisible(b);
				if (!b) {
					filtersButton.setIcon(FIBIconLibrary.BROWSER_FILTERS_ICON);
				}
			}

			@Override
			public void menuSelectionChanged(boolean isIncluded) {
				super.menuSelectionChanged(true);
			}
		};

		for (Map.Entry<FIBBrowserElement, FIBBrowserElementType> e : _widget.getBrowserModel().getElementTypes().entrySet()) {
			if (e.getKey().getFiltered()) {
				returned.add(new FIBBrowserFilterMenuItem(e.getValue()));
			}
		}

		returned.addMenuKeyListener(new MenuKeyListener() {

			@Override
			public void menuKeyPressed(MenuKeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && filtersPopupMenu != null && filtersPopupMenu.isVisible()) {
					closeFiltersPopupMenu();
				}
			}

			@Override
			public void menuKeyReleased(MenuKeyEvent e) {
			}

			@Override
			public void menuKeyTyped(MenuKeyEvent e) {
			}

		});
		return returned;
	}

	protected void closeFiltersPopupMenu() {
		logger.info("closeFiltersPopupMenu()");
		if (filtersPopupMenu != null && filtersPopupMenu.isVisible()) {
			filtersPopupMenu.setVisible(false);
		}
		filtersPopupMenu = null;
	}

	/**
	 * Copied directly from BasicPopupMenuUI - PK 06-08-04
	 * 
	 * @param c
	 *            componenet of which we want to find the owning window
	 * @return the window that is contins after plenty of leves the component c
	 */
	protected Window getWindow(Component c) {
		Component w = c;
		while (!(w instanceof Window) && w != null) {
			w = w.getParent();
		}
		return (Window) w;
	}

	/**
	 * Added mouselistners to each component of the root container c, exept this button, and the calendar popup, because mouseclicks in them
	 * are not supposed to clsoe the popup.
	 * 
	 * @param c
	 *            the root container
	 */
	protected void addPopupClosers(Container c) {
		if (c == getWindow(footerPanel) && c != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("addPopupClosers");
			}
			((Window) c).addWindowListener(this);
		}
		if (c != filtersPopupMenu && c != null) {
			c.addMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				addPopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	/**
	 * Added mouselistners to each component of the root container c, exept this button, and the calendar popup, because mouseclicks in them
	 * are not supposed to clsoe the popup.
	 * 
	 * @param c
	 *            the root container
	 */
	protected void removePopupClosers(Container c) {
		if (c instanceof Window) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("removePopupClosers");
			}
			((Window) c).removeWindowListener(this);
		}
		if (c != filtersPopupMenu && c != null) {
			c.removeMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				removePopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() != filtersPopupMenu && e.getSource() != filtersButton) {
			closeFiltersPopupMenu();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		closeFiltersPopupMenu();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		closeFiltersPopupMenu();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		closeFiltersPopupMenu();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	private class BrowserButton implements ActionListener {
		private final Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>> actions;
		private final JButton button;

		public BrowserButton(Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>> actions, Icon icon,
				Icon disabledIcon, Icon pressedIcon) {
			super();
			this.actions = actions;
			button = new ImageButton(icon);
			button.setDisabledIcon(disabledIcon);
			button.setPressedIcon(pressedIcon);
			button.addActionListener(this);
		}

		public JButton getButton() {
			return button;
		}

		public void handleSelectionChanged(Object selection) {
			boolean active = true;
			if (selection != null) {
				FIBBrowserElement element = elementForObject(selection);
				if (element != null) {
					Map<FIBBrowserAction, FIBBrowserActionListener<T>> browserActions = actions.get(element);
					int activeActionCount = 0;
					if (browserActions != null && browserActions.size() > 0) {
						for (Entry<FIBBrowserAction, FIBBrowserActionListener<T>> e : browserActions.entrySet()) {
							if (e.getValue().isActive(selection)) {
								activeActionCount++;
							}
						}
					}
					active = activeActionCount > 0;
				}
				else {
					active = false;
				}
			}
			else {
				active = false;
			}
			button.setEnabled(active);
		}

		private FIBBrowserElement elementForObject(Object object) {
			if (object == null) {
				return null;
			}
			else {
				return _widget.getComponent().elementForClass(object.getClass());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FIBBrowserElement element = elementForObject(_widget.getSelected());
			if (element != null) {
				List<FIBBrowserActionListener<T>> listeners = new ArrayList<FIBBrowserActionListener<T>>();
				Map<FIBBrowserAction, FIBBrowserActionListener<T>> browserActions = actions.get(element);
				if (browserActions != null && browserActions.size() > 0) {
					for (Entry<FIBBrowserAction, FIBBrowserActionListener<T>> entry : browserActions.entrySet()) {
						if (entry.getValue().isActive(_widget.getSelected())) {
							listeners.add(entry.getValue());
						}
					}
				}
				if (listeners.size() == 1) {
					listeners.get(0).setSelectedObject(_widget.getSelected());
					listeners.get(0).actionPerformed(e);
				}
				else if (listeners.size() > 1) {
					JPopupMenu popupMenu = new JPopupMenu();
					for (FIBBrowserActionListener<T> actionListener : listeners) {
						actionListener.setSelectedObject(_widget.getSelected());
						JMenuItem menuItem = new JMenuItem(getLocalized(actionListener.getBrowserAction().getName()));
						menuItem.addActionListener(actionListener);
						popupMenu.add(menuItem);
					}
					popupMenu.setInvoker(button);
					Point location = button.getLocationOnScreen();
					location.y -= popupMenu.getPreferredSize().getHeight();
					popupMenu.setLocation(location);
					popupMenu.setVisible(true);
				}
			}
		}
	}

}

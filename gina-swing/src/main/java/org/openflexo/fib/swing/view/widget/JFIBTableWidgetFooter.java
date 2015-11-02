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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.fib.view.widget.table.FIBTableActionListener;
import org.openflexo.fib.view.widget.table.FIBTableWidgetFooterImpl;

/**
 * Swing implementation for a table widget footer (the footer is synchronized with the selection of table)<br>
 * In Swing, this is a JPanel with 3 JButtons +/- and custom.
 * 
 * @author sylvain
 */
public class JFIBTableWidgetFooter<T> extends FIBTableWidgetFooterImpl<JPanel, T> {

	protected static final Logger LOGGER = Logger.getLogger(JFIBTableWidgetFooter.class.getPackage().getName());

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;

	protected JButton plusButton;
	protected JButton minusButton;
	protected JButton optionsButton;

	protected JPopupMenu popupMenu = null;

	private final JPanel footerPanel;

	/**
	 * Stores controls: key is the JButton and value the FIBTableActionListener
	 */
	// private Hashtable<JButton,FIBTableActionListener> _controls;

	public JFIBTableWidgetFooter(FIBTableWidget<?, T> widget) {
		super(widget);

		initializeActions(widget);

		footerPanel = new JPanel();
		footerPanel.setOpaque(false);
		footerPanel.setBorder(BorderFactory.createEmptyBorder());
		footerPanel.setLayout(new BorderLayout());

		JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());
		plusMinusPanel.setOpaque(false);
		plusButton = new JButton(FIBIconLibrary.BROWSER_PLUS_ICON);
		plusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultiplePlusActions()) {
					plusPressed();
					plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
				}
			}

		});
		plusButton.setBorder(BorderFactory.createEmptyBorder());
		plusButton.setDisabledIcon(FIBIconLibrary.BROWSER_PLUS_DISABLED_ICON);
		// plusButton.setSelectedIcon(FlexoCst.BROWSER_PLUS_SELECTED_ICON);
		plusButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (plusButton.isEnabled()) {
					plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_SELECTED_ICON);
				}
				if (hasMultiplePlusActions()) {
					getPlusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
					plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (plusButton.isEnabled()) {
					plusButton.setIcon(FIBIconLibrary.BROWSER_PLUS_ICON);
				}
				if (hasMultiplePlusActions()) {
					getPlusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});

		minusButton = new JButton(FIBIconLibrary.BROWSER_MINUS_ICON);
		minusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultipleMinusActions()) {
					minusPressed();
					minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
				}
			}

		});
		minusButton.setBorder(BorderFactory.createEmptyBorder());
		minusButton.setDisabledIcon(FIBIconLibrary.BROWSER_MINUS_DISABLED_ICON);
		// minusButton.setSelectedIcon(FlexoCst.BROWSER_MINUS_SELECTED_ICON);
		minusButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (minusButton.isEnabled()) {
					minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_SELECTED_ICON);
				}
				if (hasMultipleMinusActions()) {
					getMinusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
					minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (minusButton.isEnabled()) {
					minusButton.setIcon(FIBIconLibrary.BROWSER_MINUS_ICON);
				}
				if (hasMultipleMinusActions()) {
					getMinusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});

		plusMinusPanel.add(plusButton);
		plusMinusPanel.add(minusButton);

		footerPanel.add(plusMinusPanel, BorderLayout.WEST);
		optionsButton = new JButton(FIBIconLibrary.BROWSER_OPTIONS_ICON);
		optionsButton.setBorder(BorderFactory.createEmptyBorder());
		optionsButton.setDisabledIcon(FIBIconLibrary.BROWSER_OPTIONS_DISABLED_ICON);
		footerPanel.add(optionsButton, BorderLayout.EAST);

		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (optionsButton.isEnabled()) {
					optionsButton.setIcon(FIBIconLibrary.BROWSER_OPTIONS_SELECTED_ICON);
				}
				getOptionActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (optionsButton.isEnabled()) {
					optionsButton.setIcon(FIBIconLibrary.BROWSER_OPTIONS_ICON);
				}
				getOptionActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
			}

		});

		handleSelectionCleared();

		footerPanel.revalidate();
	}

	@Override
	public JPanel getFooterComponent() {
		return footerPanel;
	}

	private JPopupMenu plusActionMenu = null;
	private JPopupMenu minusActionMenu = null;
	private JPopupMenu optionsActionMenu = null;

	private boolean plusActionMenuNeedsRecomputed = true;
	private boolean minusActionMenuNeedsRecomputed = true;
	private boolean optionsActionMenuNeedsRecomputed = true;

	@Override
	public void handleSelectionChanged() {
		// System.out.println("handleSelectionChanged");
		plusActionMenuNeedsRecomputed = true;
		minusActionMenuNeedsRecomputed = true;
		optionsActionMenuNeedsRecomputed = true;

		if (hasMultiplePlusActions()) {
			plusButton.setEnabled(true && tableWidget.isEnabled());
		}
		else {
			boolean isActive = false;
			for (FIBTableAction action : addActions.keySet()) {
				FIBTableActionListener<T> actionListener = addActions.get(action);
				if (actionListener.isActive(tableWidget.getSelected())) {
					isActive = true;
				}
			}
			plusButton.setEnabled(isActive && tableWidget.isEnabled());
		}

		boolean isMinusActive = false;
		for (FIBTableAction action : removeActions.keySet()) {
			FIBTableActionListener<T> actionListener = removeActions.get(action);
			if (actionListener.isActive(tableWidget.getSelected())) {
				isMinusActive = true;
			}
		}
		minusButton.setEnabled(isMinusActive && tableWidget.isEnabled());

		optionsButton.setEnabled(otherActions.size() > 0 && tableWidget.isEnabled());

		/*FlexoModelObject focusedObject = getFocusedObject();
		Vector<FlexoModelObject> globalSelection = buildGlobalSelection();
		plusButton.setEnabled((focusedObject != null) && (getActionTypesWithAddType(focusedObject).size() > 0));
		minusButton.setEnabled((focusedObject != null) && (getActionTypesWithDeleteType(focusedObject, globalSelection).size() > 0));
		plusActionMenuNeedsRecomputed = true;*/
	}

	private JPopupMenu getPlusActionMenu() {
		if (plusActionMenuNeedsRecomputed) {
			plusActionMenu = new JPopupMenu();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Build plus menu");
			}

			for (FIBTableAction action : sortedAddKeys) {
				FIBTableActionListener<T> actionListener = addActions.get(action);
				actionListener.setSelectedObject(tableWidget.getSelected());
				// actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
				menuItem.addActionListener(actionListener);
				plusActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(tableWidget.getSelected()));
			}

			plusActionMenuNeedsRecomputed = false;
		}
		return plusActionMenu;
	}

	private JPopupMenu getMinusActionMenu() {
		if (minusActionMenuNeedsRecomputed) {
			minusActionMenu = new JPopupMenu();

			for (FIBTableAction action : sortedRemoveKeys) {
				FIBTableActionListener<T> actionListener = removeActions.get(action);
				actionListener.setSelectedObject(tableWidget.getSelected());
				// actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
				menuItem.addActionListener(actionListener);
				minusActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(tableWidget.getSelected()));
			}

			minusActionMenuNeedsRecomputed = false;
		}
		return minusActionMenu;
	}

	private JPopupMenu getOptionActionMenu() {
		if (optionsActionMenuNeedsRecomputed) {
			optionsActionMenu = new JPopupMenu();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Build plus menu");
			}

			for (FIBTableAction action : sortedOtherKeys) {
				FIBTableActionListener<T> actionListener = otherActions.get(action);
				actionListener.setSelectedObject(tableWidget.getSelected());
				// actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
				menuItem.addActionListener(actionListener);
				optionsActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(tableWidget.getSelected()));
			}

			optionsActionMenuNeedsRecomputed = false;
		}
		return optionsActionMenu;
	}

	@Override
	public void delete() {
		super.delete();
	}

}

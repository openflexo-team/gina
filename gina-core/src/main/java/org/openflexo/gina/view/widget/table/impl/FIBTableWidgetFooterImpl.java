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

package org.openflexo.gina.view.widget.table.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableAction;
import org.openflexo.gina.view.widget.FIBTableWidget;
import org.openflexo.gina.view.widget.impl.FIBTableWidgetFooter;

/**
 * Default base implementation for a table widget footer (the footer is synchronized with the selection of table)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBTableWidgetFooterImpl<C, T> implements FIBTableWidgetFooter<C, T> {

	protected static final Logger LOGGER = Logger.getLogger(FIBTableWidgetFooterImpl.class.getPackage().getName());

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;

	protected FIBTableWidget<?, T> tableWidget;
	protected FIBTable fibTable;

	protected List<FIBTableAction> sortedAddKeys;
	protected List<FIBTableAction> sortedRemoveKeys;
	protected List<FIBTableAction> sortedOtherKeys;

	/**
	 * Stores controls: key is the JButton and value the FIBTableActionListener
	 */
	// private Hashtable<JButton,FIBTableActionListener> _controls;

	public FIBTableWidgetFooterImpl(FIBTableWidget<?, T> widget) {
		super();
		tableWidget = widget;

		initializeActions(widget);

	}

	@Override
	public void handleSelectionCleared() {
		handleSelectionChanged();
	}

	@Override
	public void plusPressed() {
		for (FIBTableAction action : addActions.keySet()) {
			FIBTableActionListener<T> actionListener = addActions.get(action);
			if (actionListener.isActive(tableWidget.getSelected())) {
				actionListener.performAction(tableWidget.getSelected());
			}
		}
	}

	@Override
	public void minusPressed() {
		for (FIBTableAction action : removeActions.keySet()) {
			FIBTableActionListener<T> actionListener = removeActions.get(action);
			int index = -1;
			if (tableWidget.getSelected() != null && tableWidget.getValue() instanceof List) {
				index = ((List<T>) tableWidget.getValue()).indexOf(tableWidget.getSelected());
			}

			if (tableWidget.getSelection() != null && tableWidget.getSelection().size() > 1) {
				// Multiple selection
				for (T selectedObject : tableWidget.getSelection()) {
					if (actionListener.isActive(selectedObject)) {
						actionListener.performAction(selectedObject);
					}
				}
			}
			else if (tableWidget.getSelected() != null) {
				if (actionListener.isActive(tableWidget.getSelected())) {
					actionListener.performAction(tableWidget.getSelected());
				}
			}

			if (index > -1 && tableWidget.getValue() instanceof List) {
				List<T> l = (List<T>) tableWidget.getValue();
				if (index < l.size()) {
					tableWidget.performSelect(l.get(index));
				}
				else if (l.size() > 0) {
					tableWidget.performSelect(l.get(l.size() - 1));
				}
			}
		}
	}

	@Override
	public boolean hasMultiplePlusActions() {
		return addActions.size() > 1;
	}

	@Override
	public boolean hasMultipleMinusActions() {
		return removeActions.size() > 1;
	}

	protected Hashtable<FIBTableAction, FIBTableActionListener<T>> addActions;
	protected Hashtable<FIBTableAction, FIBTableActionListener<T>> removeActions;
	protected Hashtable<FIBTableAction, FIBTableActionListener<T>> otherActions;

	@Override
	public void initializeActions(FIBTableWidget<?, T> tableWidget) {
		addActions = new Hashtable<>();
		removeActions = new Hashtable<>();
		otherActions = new Hashtable<>();
		sortedAddKeys = new ArrayList<>();
		sortedRemoveKeys = new ArrayList<>();
		sortedOtherKeys = new ArrayList<>();

		for (FIBTableAction plAction : tableWidget.getComponent().getActions()) {
			FIBTableActionListener<T> plActionListener = new FIBTableActionListener<>(plAction, tableWidget);
			if (plActionListener.isAddAction()) {
				addActions.put(plAction, plActionListener);
				sortedAddKeys.add(plAction);
			}
			else if (plActionListener.isRemoveAction()) {
				removeActions.put(plAction, plActionListener);
				sortedRemoveKeys.add(plAction);
			}
			else if (plActionListener.isCustomAction()) {
				otherActions.put(plAction, plActionListener);
				sortedOtherKeys.add(plAction);
			}
		}
	}

	@Override
	public void delete() {
		for (FIBTableAction a : addActions.keySet()) {
			addActions.get(a).delete();
		}
		for (FIBTableAction a : removeActions.keySet()) {
			removeActions.get(a).delete();
		}
		for (FIBTableAction a : otherActions.keySet()) {
			otherActions.get(a).delete();
		}

		tableWidget = null;
		fibTable = null;
	}

	@Override
	public Enumeration<FIBTableActionListener<T>> getAddActionListeners() {
		return addActions.elements();
	}

	@Override
	public void setModel(Object model) {
		// logger.info("Set model with "+model);
		for (FIBTableAction action : addActions.keySet()) {
			FIBTableActionListener<T> actionListener = addActions.get(action);
			actionListener.setModel(model);
		}
		for (FIBTableAction action : removeActions.keySet()) {
			FIBTableActionListener<T> actionListener = removeActions.get(action);
			actionListener.setModel(model);
		}
		for (FIBTableAction action : otherActions.keySet()) {
			FIBTableActionListener<T> actionListener = otherActions.get(action);
			actionListener.setModel(model);
		}
		handleSelectionChanged();
		/* for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		      FIBTableActionListener actionListener = (FIBTableActionListener) en.nextElement();
		  	actionListener.setModel(model);
		  }
		  updateControls(null);*/
	}

	@Override
	public FIBController getController() {
		return tableWidget.getController();
	}

	@Override
	public String getLocalized(String key) {
		return getController().getLocalizerForComponent(fibTable).localizedForKey(key);
	}

}

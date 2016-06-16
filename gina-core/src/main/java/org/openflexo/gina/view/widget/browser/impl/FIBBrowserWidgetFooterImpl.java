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

package org.openflexo.gina.view.widget.browser.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBBrowserAction;
import org.openflexo.gina.model.widget.FIBBrowserAction.ActionType;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetFooter;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetImpl;

/**
 * Base implementation for a browser widget footer (the footer is synchronized with the selection of browser)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of objects managed by this footer
 * 
 * @author sylvain
 */
public abstract class FIBBrowserWidgetFooterImpl<C, T> implements FIBBrowserWidgetFooter<C, T> {

	protected static final Logger logger = Logger.getLogger(FIBBrowserWidgetFooterImpl.class.getPackage().getName());

	protected FIBBrowserWidgetImpl<?, T> _widget;

	protected Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>> _addActions;
	protected Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>> _removeActions;
	protected Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>> _otherActions;

	public FIBBrowserWidgetFooterImpl(FIBBrowserWidgetImpl<?, T> widget) {
		super();
		_widget = widget;

		for (final FIBBrowserElement e : widget.getBrowser().getElements()) {
			e.getPropertyChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(FIBBrowserElement.ACTIONS_KEY)) {
						updateActionsFor(e);
					}
				}
			});
		}

		initializeActions(widget);

	}

	@Override
	public void initializeActions(FIBBrowserWidget<?, T> widget) {
		_addActions = new LinkedHashMap<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>>();
		_removeActions = new LinkedHashMap<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>>();
		_otherActions = new LinkedHashMap<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener<T>>>();

		for (FIBBrowserElement element : widget.getComponent().getElements()) {

			Map<FIBBrowserAction, FIBBrowserActionListener<T>> addActions = new LinkedHashMap<FIBBrowserAction, FIBBrowserActionListener<T>>();
			Map<FIBBrowserAction, FIBBrowserActionListener<T>> removeActions = new LinkedHashMap<FIBBrowserAction, FIBBrowserActionListener<T>>();
			Map<FIBBrowserAction, FIBBrowserActionListener<T>> otherActions = new LinkedHashMap<FIBBrowserAction, FIBBrowserActionListener<T>>();

			for (FIBBrowserAction plAction : element.getActions()) {
				FIBBrowserActionListener<T> plActionListener = new FIBBrowserActionListener<T>(_widget, plAction);
				if (plActionListener.isAddAction()) {
					addActions.put(plAction, plActionListener);
				}
				else if (plActionListener.isRemoveAction()) {
					removeActions.put(plAction, plActionListener);
				}
				else if (plActionListener.isCustomAction()) {
					otherActions.put(plAction, plActionListener);
				}
			}

			_addActions.put(element, addActions);
			_removeActions.put(element, removeActions);
			_otherActions.put(element, otherActions);
		}
	}

	@Override
	public void updateActionsFor(FIBBrowserElement element) {

		Map<FIBBrowserAction, FIBBrowserActionListener<T>> addActions = _addActions.get(element);
		Map<FIBBrowserAction, FIBBrowserActionListener<T>> removeActions = _removeActions.get(element);
		Map<FIBBrowserAction, FIBBrowserActionListener<T>> otherActions = _otherActions.get(element);

		List<FIBBrowserAction> addActionsToRemove = new ArrayList<FIBBrowserAction>(addActions.keySet());
		List<FIBBrowserAction> removeActionsToRemove = new ArrayList<FIBBrowserAction>(removeActions.keySet());
		List<FIBBrowserAction> otherActionsToRemove = new ArrayList<FIBBrowserAction>(otherActions.keySet());

		for (FIBBrowserAction plAction : element.getActions()) {
			if (plAction.getActionType() == ActionType.Add) {
				FIBBrowserActionListener<T> plActionListener = addActions.get(plAction);
				if (plActionListener != null) {
					addActionsToRemove.remove(plAction);
				}
				else {
					plActionListener = new FIBBrowserActionListener<T>(_widget, plAction);
					addActions.put(plAction, plActionListener);
				}
			}
			else if (plAction.getActionType() == ActionType.Delete) {
				FIBBrowserActionListener<T> plActionListener = removeActions.get(plAction);
				if (plActionListener != null) {
					removeActionsToRemove.remove(plAction);
				}
				else {
					plActionListener = new FIBBrowserActionListener<T>(_widget, plAction);
					removeActions.put(plAction, plActionListener);
				}
			}
			else if (plAction.getActionType() == ActionType.Custom) {
				FIBBrowserActionListener<T> plActionListener = otherActions.get(plAction);
				if (plActionListener != null) {
					otherActionsToRemove.remove(plAction);
				}
				else {
					plActionListener = new FIBBrowserActionListener<T>(_widget, plAction);
					otherActions.put(plAction, plActionListener);
				}
			}
		}

		for (FIBBrowserAction a : new ArrayList<FIBBrowserAction>(addActionsToRemove)) {
			addActions.remove(a);
		}
		for (FIBBrowserAction a : new ArrayList<FIBBrowserAction>(removeActionsToRemove)) {
			removeActions.remove(a);
		}
		for (FIBBrowserAction a : new ArrayList<FIBBrowserAction>(otherActionsToRemove)) {
			otherActions.remove(a);
		}

	}

	@Override
	public void delete() {
		if (_widget != null && _widget.getComponent() != null) {
			for (FIBBrowserElement element : _widget.getComponent().getElements()) {
				Map<FIBBrowserAction, FIBBrowserActionListener<T>> hashtable = _addActions != null ? _addActions.get(element) : null;
				if (hashtable != null) {
					for (Map.Entry<FIBBrowserAction, FIBBrowserActionListener<T>> e : hashtable.entrySet()) {
						e.getValue().delete();
					}
				}
				hashtable = _removeActions != null ? _removeActions.get(element) : null;
				if (hashtable != null) {
					for (Map.Entry<FIBBrowserAction, FIBBrowserActionListener<T>> e : hashtable.entrySet()) {
						e.getValue().delete();
					}
				}
				hashtable = _otherActions != null ? _otherActions.get(element) : null;
				if (hashtable != null) {
					for (Map.Entry<FIBBrowserAction, FIBBrowserActionListener<T>> e : hashtable.entrySet()) {
						e.getValue().delete();
					}
				}

			}
		}
		_widget = null;
	}

	@Override
	public FIBController getController() {
		return _widget.getController();
	}

	@Override
	public String getLocalized(String key) {
		return getController().getLocalizerForComponent(_widget.getComponent()).localizedForKey(key);
	}

}

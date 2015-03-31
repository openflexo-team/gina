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

package org.openflexo.fib.view.widget.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.fib.model.FIBBrowserAction.ActionType;
import org.openflexo.fib.model.FIBBrowserAction.FIBCustomAction;
import org.openflexo.fib.view.widget.FIBBrowserWidget;

public class FIBBrowserActionListener<T> implements ActionListener, BindingEvaluationContext, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(FIBBrowserActionListener.class.getPackage().getName());

	private FIBBrowserAction browserAction;

	private Object model;

	private final FIBBrowserWidget<T> widget;

	public FIBBrowserActionListener(FIBBrowserWidget<T> widget, FIBBrowserAction browserAction) {
		super();
		this.widget = widget;
		this.browserAction = browserAction;
		selectedObject = null;

		browserAction.getPropertyChangeSupport().addPropertyChangeListener(this);

	}

	public void delete() {
		browserAction.getPropertyChangeSupport().removePropertyChangeListener(this);
		this.browserAction = null;
		selectedObject = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == browserAction) {
			if ((evt.getPropertyName().equals(FIBBrowserAction.METHOD_KEY))
					|| (evt.getPropertyName().equals(FIBBrowserAction.IS_AVAILABLE_KEY))) {
				widget.updateBrowser();
			}
		}
	}

	public FIBController getController() {
		return widget.getController();
	}

	public boolean isAddAction() {
		return browserAction.getActionType() == ActionType.Add;
	}

	public boolean isRemoveAction() {
		return browserAction.getActionType() == ActionType.Delete;
	}

	public boolean isCustomAction() {
		return browserAction.getActionType() == ActionType.Custom;
	}

	public boolean isStatic() {
		return isCustomAction() && ((FIBCustomAction) browserAction).isStatic();
	}

	public boolean isActive(Object selectedObject) {
		if (isRemoveAction() && selectedObject == null) {
			return false;
		}
		if (browserAction.getIsAvailable() != null && browserAction.getIsAvailable().isValid()) {
			this.selectedObject = selectedObject;
			Boolean returned = null;
			try {
				returned = browserAction.getIsAvailable().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (returned == null) {
				return false;
			}
			if (TypeUtils.isBoolean(returned.getClass())) {
				return returned;
			}
		}
		return true;
	}

	protected void performAction(Object selectedObject) {
		if (browserAction.getMethod() != null && browserAction.getMethod().isValid()) {
			logger.fine("Perform action " + browserAction.getName() + " method " + browserAction.getMethod());
			logger.fine("controller=" + getController() + " of " + getController().getClass().getSimpleName());
			this.selectedObject = selectedObject;
			/*logger.info("selectedObject=" + selectedObject);
			logger.info("getMethod=" + getBrowserAction().getMethod());
			logger.info("valid=" + getBrowserAction().getMethod().isValid() + " reason:"
					+ getBrowserAction().getMethod().invalidBindingReason());*/
			try {
				final T newObject = (T) getBrowserAction().getMethod().getBindingValue(this);
				// browserModel.fireTableDataChanged();
				// browserModel.getBrowserWidget().updateWidgetFromModel();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (widget.mayRepresent(newObject)) {
							//System.out.println("Selecting new object: " + newObject);
							widget.setSelected(newObject);
						} else {
							//System.out.println("Cannot select new object");
						}
					}
				});
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public FIBBrowserAction getBrowserAction() {
		return browserAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		performAction(getSelectedObject());
	}

	public Object getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(Object selectedObject) {
		this.selectedObject = selectedObject;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	protected Object selectedObject;

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("selected")) {
			return selectedObject;
		} else if (variable.getVariableName().equals("action")) {
			return browserAction;
		} else {
			return widget.getBindingEvaluationContext().getValue(variable);
		}
	}
}

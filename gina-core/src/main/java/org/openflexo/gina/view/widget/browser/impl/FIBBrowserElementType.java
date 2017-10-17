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

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ToolBox;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class FIBBrowserElementType implements HasPropertyChangeSupport, BindingEvaluationContext, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(FIBBrowserElementType.class.getPackage().getName());

	private FIBBrowserModel fibBrowserModel;
	private FIBBrowserElement browserElementDefinition;
	private boolean isFiltered = false;

	private FIBController controller;

	private final PropertyChangeSupport pcSupport;

	public FIBBrowserElementType(FIBBrowserElement browserElementDefinition, FIBBrowserModel browserModel, FIBController controller) {
		super();
		this.controller = controller;
		this.fibBrowserModel = browserModel;
		this.browserElementDefinition = browserElementDefinition;

		pcSupport = new PropertyChangeSupport(this);

		browserElementDefinition.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void delete() {
		if (browserElementDefinition != null) {
			browserElementDefinition.getPropertyChangeSupport().removePropertyChangeListener(this);
		}

		this.controller = null;
		this.browserElementDefinition = null;
	}

	public FIBBrowserModel getBrowserModel() {
		return fibBrowserModel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == browserElementDefinition) {
			if (evt.getPropertyName().equals(FIBBrowserElement.ACTIONS_KEY)) {
				// There was here a huge perf issue caused by FIBBrowserElement.addToActions() call
				// in FIBBrowserView
				// Please investigate this issue
				return;
			}
			FIBBrowserWidget<?, ?> owner = (FIBBrowserWidget<?, ?>) controller.viewForComponent(browserElementDefinition.getOwner());
			if (owner != null)
				owner.updateBrowser();
		}
	}

	public FIBController getController() {
		return controller;
	}

	public FIBBrowser getBrowser() {
		return browserElementDefinition.getOwner();
	}

	public String getLocalized(String key) {
		return getController().getLocalizerForComponent(getBrowser()).localizedForKey(key);
	}

	protected void setModel(FIBBrowserModel model) {
		fibBrowserModel = model;
	}

	protected FIBBrowserModel getModel() {
		return fibBrowserModel;
	}

	/**
	 * Return a list of all bindings declared in the context of this browser element
	 * 
	 * @return
	 */
	public List<DataBinding<?>> getDeclaredBindings() {
		if (browserElementDefinition == null) {
			return null;
		}
		List<DataBinding<?>> returned = new ArrayList<>();
		returned.add(browserElementDefinition.getLabel());
		returned.add(browserElementDefinition.getIcon());
		returned.add(browserElementDefinition.getTooltip());
		returned.add(browserElementDefinition.getEnabled());
		returned.add(browserElementDefinition.getVisible());
		for (FIBBrowserElementChildren children : browserElementDefinition.getChildren()) {
			returned.add(children.getData());
			returned.add(children.getCast());
			returned.add(children.getVisible());
		}
		return returned;
	}

	public String getLabelFor(final Object object) {
		if (browserElementDefinition == null) {
			return "???" + object.toString();
		}
		if (browserElementDefinition.getLabel().isSet()) {
			setIteratorObject(object);
			try {
				return browserElementDefinition.getLabel().getBindingValue(this);
			} catch (Exception e) {
				// System.out.println("While evaluating " + browserElementDefinition.getLabel());
				e.printStackTrace();
			}
		}
		return object.toString();
	}

	public String getTooltipFor(final Object object) {
		if (browserElementDefinition == null) {
			return "???" + object.toString();
		}
		if (browserElementDefinition.getTooltip().isSet()) {
			setIteratorObject(object);
			try {
				return browserElementDefinition.getTooltip().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return browserElementDefinition.getName();
	}

	public Icon getIconFor(final Object object) {
		if (browserElementDefinition == null) {
			return null;
		}
		if (browserElementDefinition.getIcon().isSet()) {
			setIteratorObject(object);
			Object returned = null;
			try {
				returned = browserElementDefinition.getIcon().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (returned instanceof Icon) {
				return (Icon) returned;
			}
			return null;
		}
		else {
			return browserElementDefinition.getImageIcon();
		}
	}

	public boolean isEnabled(final Object object) {
		if (browserElementDefinition == null) {
			return false;
		}
		if (browserElementDefinition.getEnabled().isSet()) {
			setIteratorObject(object);
			Object enabledValue = null;
			try {
				enabledValue = browserElementDefinition.getEnabled().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (enabledValue != null) {
				return (Boolean) enabledValue;
			}
			return true;
		}
		else {
			return true;
		}
	}

	public boolean isVisible(final Object object) {
		if (browserElementDefinition == null) {
			return false;
		}
		if (isFiltered()) {
			return false;
		}
		if (browserElementDefinition.getVisible().isSet()) {
			setIteratorObject(object);
			try {
				Boolean returned = browserElementDefinition.getVisible().getBindingValue(this);
				if (returned != null) {
					return returned;
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}
		else {
			return true;
		}
	}

	public List<Object> getChildrenFor(final Object object) {
		if (browserElementDefinition == null) {
			return Collections.EMPTY_LIST;
		}
		List<Object> returned = new ArrayList<>();
		for (FIBBrowserElementChildren children : browserElementDefinition.getChildren()) {
			if (children.isMultipleAccess()) {
				List<?> childrenObjects = getChildrenListFor(children, object);
				// Might be null if some visibility was declared
				if (childrenObjects != null) {
					returned.addAll(childrenObjects);
				}
			}
			else {
				Object childrenObject = getChildrenFor(children, object);
				// Might be null if some visibility was declared
				if (childrenObject != null) {
					returned.add(childrenObject);
				}
			}
		}
		return returned;
	}

	protected Object getChildrenFor(FIBBrowserElementChildren children, final Object object) {
		if (children.getData().isSet()) {
			setIteratorObject(object);
			if (children.getVisible().isSet()) {
				boolean visible;
				try {
					Boolean bindingValue = children.getVisible().getBindingValue(this);
					visible = bindingValue != null ? bindingValue : false;
				} catch (Exception e) {
					visible = true;
				}
				if (!visible) {
					// Finally we dont want to see it
					return null;
				}
			}
			Object result = null;
			try {
				result = children.getData().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (children.getCast().isSet()) {
				return new CastFunction(children).apply(result);
			}
			return result;
		}
		else {
			return null;
		}
	}

	protected List<?> getChildrenListFor(final FIBBrowserElementChildren children, final Object object) {
		if (children.getData().isSet() && children.isMultipleAccess()) {
			setIteratorObject(object);
			if (children.getVisible().isSet()) {
				boolean visible;
				try {
					Boolean bindingValue = children.getVisible().getBindingValue(this);
					visible = bindingValue != null ? bindingValue : false;
				} catch (Exception e) {
					e.printStackTrace();
					visible = true;
				}
				if (!visible) {
					// Finally we dont want to see it
					return null;
				}
			}
			Object bindingValue = null;
			try {
				bindingValue = children.getData().getBindingValue(this);
			} catch (NullReferenceException e) {
				// Silently escape
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<?> list = ToolBox.getListFromIterable(bindingValue);
			if (list != null && children.getCast().isSet()) {
				list = Lists.transform(list, new CastFunction(children));
				List returned = list;
				// Remove all occurences of null (caused by cast)
				/*while (list.contains(null)) {
					list.remove(null);
				}*/
				if (list.contains(null)) {
					// The list contains null
					// We have to consider only non-null instances of elements, but we must avoid to destroy initial list:
					// This is the reason for what we have to clone the list while avoiding null elements
					returned = new ArrayList<>();
					for (Object o : list) {
						if (o != null) {
							returned.add(o);
						}
					}
				}
				return returned;
			}
			return list;
		}
		else {
			return null;
		}
	}

	public boolean isLabelEditable() {
		return getBrowserElement().getIsEditable() && getBrowserElement().getEditableLabel().isSet()
				&& getBrowserElement().getEditableLabel().isSettable();
	}

	public synchronized String getEditableLabelFor(final Object object) {
		if (isLabelEditable()) {
			setIteratorObject(object);
			try {
				return browserElementDefinition.getEditableLabel().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object.toString();
	}

	public synchronized void setEditableLabelFor(final Object object, String value) {
		if (isLabelEditable()) {
			setIteratorObject(object);
			try {
				browserElementDefinition.getEditableLabel().setBindingValue(value, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected Object iteratorObject;

	private void setIteratorObject(Object iteratorObject) {
		Object oldIteratorObject = this.iteratorObject;
		if (iteratorObject != oldIteratorObject) {
			this.iteratorObject = iteratorObject;
			// Sylvain:
			// This is not necessary anymore to fire this, since we will not track the modification of
			// a variable which is intrinsically temporary. Firing this causes huge performance issues
			// getPropertyChangeSupport().firePropertyChange(browserElementDefinition.getName(), oldIteratorObject, iteratorObject);
		}
	}

	@Override
	public synchronized Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(browserElementDefinition.getName())) {
			return iteratorObject;
		}
		else if (variable.getVariableName().equals("object")) {
			return iteratorObject;
		}
		else {
			return getBrowserModel().getWidget().getValue(variable);
		}
	}

	public FIBBrowserElement getBrowserElement() {
		return browserElementDefinition;
	}

	public Font getFont(final Object object) {
		if (browserElementDefinition.getDynamicFont().isSet()) {
			setIteratorObject(object);
			try {
				return browserElementDefinition.getDynamicFont().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else if (getBrowserElement() != null) {
			return getBrowserElement().retrieveValidFont();
		}
		else {
			return null;
		}
	}

	public Color getSelectedColor(final Object object) {
		if (browserElementDefinition.getSelectedDynamicColor().isSet()) {
			setIteratorObject(object);
			try {
				return browserElementDefinition.getSelectedDynamicColor().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			return null;
		}
	}

	public Color getNonSelectedColor(final Object object) {
		if (browserElementDefinition.getNonSelectedDynamicColor().isSet()) {
			setIteratorObject(object);
			try {
				return browserElementDefinition.getNonSelectedDynamicColor().getBindingValue(this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			return null;
		}
	}

	public boolean isFiltered() {
		return isFiltered;
	}

	public void setFiltered(boolean isFiltered) {
		if (this.isFiltered != isFiltered) {
			this.isFiltered = isFiltered;
			// Later, try to implement a way to rebuild tree with same expanded nodes
			fibBrowserModel.fireTreeRestructured();
		}
	}

	private final class CastFunction implements Function<Object, Object>, BindingEvaluationContext {
		private final FIBBrowserElementChildren children;

		private Object child;

		private CastFunction(FIBBrowserElementChildren children) {
			this.children = children;
		}

		@Override
		public synchronized Object apply(Object arg0) {
			child = arg0;
			try {
				Object result = null;
				try {
					result = children.getCast().getBindingValue(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			} finally {
				child = null;
			}
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals("child")) {
				return child;
			}
			else {
				return FIBBrowserElementType.this.getValue(variable);
			}
		}
	}

}

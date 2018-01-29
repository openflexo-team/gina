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

package org.openflexo.gina.view.widget.impl;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomAssignment;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBCustomWidget;
import org.openflexo.kvc.InvalidKeyValuePropertyException;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Generic Gina widget presenting a custom component<br>
 * This base API allows to encapsulate any component in a Gina component to be integrated inside a FIB hierarchy
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this custom component
 * 
 * @author sylvain
 * 
 */
public abstract class FIBCustomWidgetImpl<C, CC extends FIBCustomComponent<T>, T> extends FIBWidgetViewImpl<FIBCustom, C, T>
		implements FIBCustomWidget<C, CC, T>, ApplyCancelListener, BindingEvaluationContext {

	private static final Logger LOGGER = Logger.getLogger(FIBCustomWidgetImpl.class.getPackage().getName());

	public static final String COMPONENT = "component";

	// private FIBCustomComponent<T, C> customComponent;

	// private final JLabel ERROR_LABEL = new JLabel("<Cannot instanciate component>");

	public FIBCustomWidgetImpl(FIBCustom model, FIBController controller, CustomComponentRenderingAdapter<C, T> RenderingAdapter) {
		super(model, controller, RenderingAdapter);

		// We need here to "listen" all assignment values that may change
		assignmentValueBindingValueChangeListeners = new ArrayList<>();
		// listenAssignmentValuesChange();

	}

	/**
	 * Called when the component view explicitely change its visibility state from INVISIBLE to VISIBLE
	 */
	@Override
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		listenAssignmentValuesChange();
	}

	/**
	 * Called when the component view explicitely change its visibility state from VISIBLE to INVISIBLE
	 */
	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenAssignmentValuesChange();
	}

	@Override
	public CustomComponentRenderingAdapter<C, T> getRenderingAdapter() {
		return (CustomComponentRenderingAdapter<C, T>) super.getRenderingAdapter();
	}

	/*@Override
	protected C makeTechnologyComponent() {
		C customComponent = makeCustomComponent((Class) getWidget().getComponentClass(),
				(Class<T>) TypeUtils.getBaseClass(getWidget().getDataType()), getController());
		return customComponent;
	}*/

	@Override
	public abstract CC getCustomComponent();

	protected CC makeCustomComponent() {
		return makeCustomComponent((Class<CC>) (Class) getWidget().getComponentClass(),
				(Class<T>) TypeUtils.getBaseClass(getWidget().getDataType()), getController());
	}

	private CC makeCustomComponent(Class<CC> customComponentClass, Class<T> dataClass, FIBController controller) {
		if (customComponentClass == null) {
			LOGGER.warning("Could not instanciate custom component : no component class found");
			return null;
		}
		Class<?>[] types = new Class[1];
		types[0] = dataClass;
		try {
			boolean found = false;
			Constructor<CC> constructor = null;
			while (!found && types[0] != null) {
				try {
					constructor = customComponentClass.getConstructor(types);
					found = true;
				} catch (NoSuchMethodException e) {
					types[0] = types[0].getSuperclass();
				}
			}
			if (constructor == null) {
				for (Constructor c : customComponentClass.getConstructors()) {
					if (c.getGenericParameterTypes().length == 1) {
						constructor = c;
						break;
					}
				}
			}
			if (constructor == null) {
				LOGGER.warning("Could not instanciate class " + customComponentClass + " : no valid constructor found, (searched "
						+ customComponentClass.getSimpleName() + "(" + dataClass.getSimpleName() + ")...)");
				return null;
			}
			Object[] args = new Object[1];
			args[0] = null;
			CC returned = constructor.newInstance(args);
			returned.init(getComponent(), controller);
			returned.addApplyCancelListener(this);
			return returned;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public synchronized void delete() {
		if (getCustomComponent() != null) {
			getCustomComponent().removeApplyCancelListener(this);
		}
		super.delete();
	}

	/**
	 * Must be overriden in sub-classes if required
	 * 
	 * @param value
	 */
	protected void performModelUpdating(Object value) {
	}

	public void performModelUpdating() {
		performModelUpdating(getValue());
	}

	public T getEditedValue() {
		return getValue();
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	/*@Override
	public synchronized boolean updateModelFromWidget() {
		return updateModelFromWidget(false);
	}*/

	protected void dataChanged(boolean forceUpdate) {
		if (forceUpdate || notEquals(getValue(), getCustomComponent().getEditedObject())) {
			setValue(getCustomComponent().getEditedObject());
			FIBCustom widget = getWidget();
			// NPE Protection
			if (widget != null) {
				DataBinding<?> db = widget.getValueChangedAction();
				if (db != null && db.isValid()) {
					try {
						db.execute(getBindingEvaluationContext());
					} catch (TypeMismatchException e) {
						e.printStackTrace();
					} catch (NullReferenceException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					// return true;
				}
			}
		}
		// return false;
	}

	private void performAssignments() {
		if (isDeleted() || getWidget() == null) {
			return;
		}
		if (getWidget().getAssignments() != null) {
			for (FIBCustomAssignment assign : getWidget().getAssignments()) {
				DataBinding<?> variableDB = assign.getVariable();
				DataBinding<?> valueDB = assign.getValue();
				if (variableDB.isSet() && valueDB.isSet()) {
					// System.out.println("Assignement " + variableDB + " with " + valueDB);
					if (!valueDB.isValid()) {
						LOGGER.warning("Assignment value not valid: " + valueDB + " reason: " + valueDB.invalidBindingReason());
					}
					if (!variableDB.isValid()) {
						LOGGER.warning("Assignment variable not valid: " + variableDB + " reason: " + variableDB.invalidBindingReason());
					}
					if (valueDB != null && valueDB.isValid()) {
						Object value = null;
						try {
							value = valueDB.getBindingValue(getBindingEvaluationContext());
							// System.out.println("value=" + value);
							if (variableDB.isValid()) {
								// System.out.println("Assign " + Integer.toHexString(assign.hashCode()) + " assignment " + variableDB
								// + " set value with " + value);
								variableDB.setBindingValue(value, this);
							}
						} catch (InvalidKeyValuePropertyException e) {
							LOGGER.warning("Unexpected InvalidKeyValuePropertyException while performing assignation: " + variableDB + "="
									+ valueDB + " message: " + e.getMessage());
						} catch (TypeMismatchException e) {
							e.printStackTrace();
						} catch (NullReferenceException e) {
							// e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NotSettableContextException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private final List<BindingValueChangeListener<?>> assignmentValueBindingValueChangeListeners;

	/**
	 * Internally called to listen to an assignment change
	 * 
	 * @param variableDB
	 * @param valueDB
	 * @return
	 */
	private <T2> BindingValueChangeListener<T2> makeAssignmentValueBindingValueChangeListener(final DataBinding<?> variableDB,
			final DataBinding<T2> valueDB) {
		return new BindingValueChangeListener<T2>(valueDB, getBindingEvaluationContext()) {
			@Override
			public void bindingValueChanged(Object source, T2 newValue) {
				// System.out.println(" bindingValueChanged() detected for assignment value =" + valueDB + " with newValue=" + newValue);
				if (variableDB.isValid()) {
					// System.out.println("Assignment " + variableDB + " set value with " + newValue);
					try {
						variableDB.setBindingValue(newValue, FIBCustomWidgetImpl.this);
					} catch (TypeMismatchException e) {
						e.printStackTrace();
					} catch (NullReferenceException e) {
						// e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NotSettableContextException e) {
						e.printStackTrace();
					} catch (InvalidKeyValuePropertyException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * Internally called to listen all data changes of assignment values
	 */
	private void listenAssignmentValuesChange() {
		stopListenAssignmentValuesChange();

		for (FIBCustomAssignment assign : getWidget().getAssignments()) {
			DataBinding<?> variableDB = assign.getVariable();
			DataBinding<?> valueDB = assign.getValue();
			if (valueDB != null && valueDB.isValid()) {
				BindingValueChangeListener<?> l = makeAssignmentValueBindingValueChangeListener(variableDB, valueDB);
				assignmentValueBindingValueChangeListeners.add(l);
			}
		}
	}

	/**
	 * Internally called to stop listening all data changes of assignment values
	 */
	private void stopListenAssignmentValuesChange() {
		for (BindingValueChangeListener<?> l : assignmentValueBindingValueChangeListeners) {
			if (l != null) {
				l.stopObserving();
				l.delete();
			}
		}
		assignmentValueBindingValueChangeListeners.clear();
	}

	@Override
	public T updateData() {

		/*T newValue = getValue();
		
		if (equals(getRepresentedValue(), newValue)) {
			// Same value, do not continue
			return newValue;
		}*/

		if (!isViewVisible()) {
			return super.updateData();
		}

		if (getCustomComponent() != null) {

			T newValue = null;

			try {
				newValue = super.updateData();
				getCustomComponent().setEditedObject(newValue);
			} catch (ClassCastException e) {
				getCustomComponent().setEditedObject(null);
				LOGGER.warning("Unexpected ClassCastException in " + getTechnologyComponent() + ": " + e.getMessage());
				// e.printStackTrace();
			}

			// Perform assignement AFTER the edited value was set !!!
			// Tried to to it before raised to severe issues
			performAssignments();

			try {
				getCustomComponent().setRevertValue(newValue);
			} catch (ClassCastException e) {
				getCustomComponent().setRevertValue(null);
				LOGGER.warning("Unexpected ClassCastException in " + getTechnologyComponent() + ": " + e.getMessage());
				// e.printStackTrace();
			}

			return newValue;

		}
		return null;
	}

	@Override
	public void fireApplyPerformed() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("fireApplyPerformed() in FIBCustomWidget, value=" + getCustomComponent().getEditedObject());
		}
		// In this case, we force model updating
		// updateModelFromWidget(true);
		dataChanged(true);
	}

	@Override
	public void fireCancelPerformed() {
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("component")) {
			return getCustomComponent();
		}
		return super.getValue(variable);
	}

	protected abstract void updateCustomComponent();

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBCustom.COMPONENT_CLASS_KEY)) {
			updateCustomComponent();
		}

		super.propertyChange(evt);
	}

}

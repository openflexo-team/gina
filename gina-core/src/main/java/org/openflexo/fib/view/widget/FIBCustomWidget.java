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

package org.openflexo.fib.view.widget;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingValueChangeListener;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.kvc.InvalidKeyValuePropertyException;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Defines an abstract custom widget
 * 
 * @author sguerin
 * 
 */
public class FIBCustomWidget<J extends JComponent, T> extends FIBWidgetView<FIBCustom, J, T> implements ApplyCancelListener,
		BindingEvaluationContext {

	private static final Logger LOGGER = Logger.getLogger(FIBCustomWidget.class.getPackage().getName());

	public static final String COMPONENT = "component";

	private FIBCustomComponent<T, J> customComponent;

	private final JLabel ERROR_LABEL = new JLabel("<Cannot instanciate component>");

	public FIBCustomWidget(FIBCustom model, FIBController controller) {
		super(model, controller);
		try {
			customComponent = makeCustomComponent(model.getComponentClass(), (Class<T>) TypeUtils.getBaseClass(model.getDataType()),
					controller);
		} catch (ClassCastException e) {
			LOGGER.warning("Could not instanciate component: ClassCastException, see logs for details");
			e.printStackTrace();
		}
		if (customComponent != null) {
			customComponent.addApplyCancelListener(this);
		}

		// We need here to "listen" all assignment values that may change
		assignmentValueBindingValueChangeListeners = new ArrayList<BindingValueChangeListener<?>>();
		listenAssignmentValuesChange();

		updateFont();
	}

	private FIBCustomComponent<T, J> makeCustomComponent(Class<FIBCustomComponent<T, J>> customComponentClass, Class<T> dataClass,
			FIBController controller) {
		if (customComponentClass == null) {
			LOGGER.warning("Could not instanciate custom component : no component class found");
			return new NotFoundComponent();
		}
		Class[] types = new Class[1];
		types[0] = dataClass;
		try {
			boolean found = false;
			Constructor<FIBCustomComponent<T, J>> constructor = null;
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
				return new NotFoundComponent();
			}
			Object[] args = new Object[1];
			args[0] = null;
			FIBCustomComponent<T, J> returned = constructor.newInstance(args);
			returned.init(getComponent(), controller);
			setCustomComponent(returned);
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

	/**
	 * Must be overriden in sub-classes if required
	 * 
	 * @param value
	 */
	protected void performModelUpdating(Object value) {
	}

	public FIBCustomComponent<T, J> getCustomComponent() {
		return customComponent;
	}

	public void setCustomComponent(FIBCustomComponent<T, J> customComponent) {
		FIBCustomComponent<T, J> oldComponent = this.customComponent;
		if (oldComponent != customComponent) {
			this.customComponent = customComponent;
			getPropertyChangeSupport().firePropertyChange(COMPONENT, oldComponent, customComponent);
		}
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
	@Override
	public synchronized boolean updateModelFromWidget() {
		return updateModelFromWidget(false);
	}

	public synchronized boolean updateModelFromWidget(boolean forceUpdate) {
		if (forceUpdate || notEquals(getValue(), customComponent.getEditedObject())) {
			setValue(customComponent.getEditedObject());
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
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public J getJComponent() {
		if (customComponent == null) {
			return (J) ERROR_LABEL;
		}
		return customComponent.getJComponent();
	}

	@Override
	public J getDynamicJComponent() {
		if (customComponent != null) {
			return customComponent.getJComponent();
		}
		return null;
	}

	private void performAssignments() {
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
							// System.out.println("Assignment " + variableDB + " set value with " + value);
							variableDB.setBindingValue(value, this);
						}
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
						variableDB.setBindingValue(newValue, FIBCustomWidget.this);
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
		for (BindingValueChangeListener<?> l : assignmentValueBindingValueChangeListeners) {
			if (l != null) {
				l.stopObserving();
				l.delete();
			}
		}
		assignmentValueBindingValueChangeListeners.clear();

		for (FIBCustomAssignment assign : getWidget().getAssignments()) {
			DataBinding<?> variableDB = assign.getVariable();
			DataBinding<?> valueDB = assign.getValue();
			if (valueDB != null && valueDB.isValid()) {
				BindingValueChangeListener<?> l = makeAssignmentValueBindingValueChangeListener(variableDB, valueDB);
				assignmentValueBindingValueChangeListeners.add(l);
			}
		}
	}

	@Override
	public boolean updateWidgetFromModel() {

		if (!isComponentVisible()) {
			return false;
		}

		if (customComponent != null) {

			// performAssignments();

			try {
				T val = getValue();
				// if (val != null) {
				customComponent.setEditedObject(getValue());
				// }
			} catch (ClassCastException e) {
				customComponent.setEditedObject(null);
				LOGGER.warning("Unexpected ClassCastException in " + customComponent + ": " + e.getMessage());
				// e.printStackTrace();
			}

			// Perform assignement AFTER the edited value was set !!!
			// Tried to to it before raised to severe issues

			performAssignments();

			try {
				customComponent.setRevertValue(getValue());
			} catch (ClassCastException e) {
				customComponent.setRevertValue(null);
				LOGGER.warning("Unexpected ClassCastException in " + customComponent + ": " + e.getMessage());
				// e.printStackTrace();
			}

		}
		return true;
		// }
		// return false;
	}

	@Override
	public void fireApplyPerformed() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("fireApplyPerformed() in FIBCustomWidget, value=" + customComponent.getEditedObject());
		}
		// In this case, we force model updating
		updateModelFromWidget(true);
	}

	@Override
	public void fireCancelPerformed() {
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("component")) {
			return customComponent;
		}
		return null;
	}

	@Override
	public boolean isSelectableComponent() {
		return customComponent instanceof FIBSelectable;
	}

	@Override
	public FIBSelectable getSelectableComponent() {
		if (isSelectableComponent()) {
			return (FIBSelectable) customComponent;
		}
		return null;
	}

	protected class NotFoundComponent implements FIBCustomComponent {

		private JLabel label;

		public NotFoundComponent() {
			label = new JLabel("< Custom component >");
		}

		@Override
		public void init(FIBCustom component, FIBController controller) {
		}

		@Override
		public void addApplyCancelListener(ApplyCancelListener l) {
		}

		@Override
		public Object getEditedObject() {
			return null;
		}

		@Override
		public JComponent getJComponent() {
			return label;
		}

		@Override
		public Class<T> getRepresentedType() {
			return null;
		}

		@Override
		public T getRevertValue() {
			return null;
		}

		@Override
		public void removeApplyCancelListener(ApplyCancelListener l) {
		}

		@Override
		public void setEditedObject(Object object) {
		}

		@Override
		public void setRevertValue(Object object) {
		}

		@Override
		public void delete() {
			label = null;
		}
	}

}

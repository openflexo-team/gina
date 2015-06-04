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

package org.openflexo.fib.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.listener.FIBActionListener;
import org.openflexo.fib.listener.GinaHandler;
import org.openflexo.fib.listener.GinaStackEvent;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.gina.event.FIBEvent;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ToolBox;

/**
 * Abstract class representing a widget view
 * 
 * @author sylvain
 */
public abstract class FIBWidgetView<M extends FIBWidget, J extends JComponent, T> extends FIBView<M, J, T> implements FocusListener,
		PropertyChangeListener /*, HasDependencyBinding*/{

	private static final Logger LOGGER = Logger.getLogger(FIBWidgetView.class.getPackage().getName());

	public static final int META_MASK = ToolBox.getPLATFORM() == ToolBox.MACOS ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	public static final String ENABLED = "enabled";

	public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font DEFAULT_MEDIUM_FONT = new Font("SansSerif", Font.PLAIN, 10);

	protected boolean modelUpdating = false;
	protected boolean widgetUpdating = false;

	private boolean enabled = true;

	public static final Dimension MINIMUM_SIZE = new Dimension(30, 25);

	private final DynamicFormatter formatter;
	private DynamicValueBindingContext valueBindingContext;
	private final DynamicEventListener eventListener;

	// private Map<DataBinding<?>, BindingValueChangeListener<?>> listeners;

	private BindingValueChangeListener<Boolean> enableBindingValueChangeListener;
	
	private boolean eventListeningLocked;
	
	protected boolean widgetExecuting, widgetDisableUserEvent;

	// private DependingObjects dependingObjects;

	protected FIBWidgetView(M model, FIBController aController) {
		super(model, aController);
		formatter = new DynamicFormatter();
		valueBindingContext = new DynamicValueBindingContext();
		eventListener = new DynamicEventListener();
		eventListeningLocked = false;
		widgetExecuting = false;
		widgetDisableUserEvent = false;
		// addBindingValueChangeListeners();
		listenEnableValueChange();
	}

	private void listenEnableValueChange() {
		if (enableBindingValueChangeListener != null) {
			enableBindingValueChangeListener.stopObserving();
			enableBindingValueChangeListener.delete();
		}
		if (getComponent().getEnable() != null && getComponent().getEnable().isValid()) {
			enableBindingValueChangeListener = new BindingValueChangeListener<Boolean>(getComponent().getEnable(),
					getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, Boolean newValue) {
					// System.out.println(" bindingValueChanged() detected for enable=" + getComponent().getEnable() + " with newValue="
					// + newValue + " source=" + source);
					updateEnability();
				}

				@Override
				protected Boolean getDefaultValue() {
					return false;
				}
			};
		}
	}
	
	protected void lockListening() {
		eventListeningLocked = true;
	}
	
	protected void allowListening() {
		eventListeningLocked = false;
	}
	
	protected boolean isListeningLocked() {
		return eventListeningLocked;
	}
	
	public void executeEvent(FIBEvent e) {
	}
	
	public synchronized GinaStackEvent actionPerformed(FIBEvent e) {
		return actionPerformed(e, !widgetUpdating && !widgetExecuting && !widgetDisableUserEvent);
	}
	
	public synchronized GinaStackEvent actionPerformed(FIBEvent e, boolean fromUserOrigin) {
		e.setFromUser(fromUserOrigin);
		
		e.setIdentity(getWidget().getBaseName(), getWidget().getName(), getWidget().getRootComponent().getUniqueID());
		if (!isListeningLocked())
			for (FIBActionListener fl : getWidget().getFibListeners())
	            fl.actionPerformed(e);
		
		// create the stack element
		return GinaHandler.getInstance().pushStackEvent(e);
	}

	// public void updateBindingValueChangeListeners() {
	// System.out.println("++++++++++++++++++++++++ updateBindingValueChangeListeners(). Est-ce vraiment necessaire ?");
	/*if (listeners != null) {
		deleteBindingValueChangeListener();
	}
	addBindingValueChangeListeners();*/
	// }

	/*private void addBindingValueChangeListeners() {
		if (listeners != null) {
			deleteBindingValueChangeListener();
		}
		listeners = new HashMap<DataBinding<?>, BindingValueChangeListener<?>>();
		for (final DataBinding dataBinding : getDependencyBindings()) {
			BindingValueChangeListener listener = new BindingValueChangeListener(dataBinding, getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, Object newValue) {
					System.out.println(" YYYYYYYEEEEEEEEESSSSSSSSS aussi pour " + dataBinding + " avec newValue=" + newValue + " source="
							+ source);
					// bindingValueHasChanged(dataBinding, newValue);
				}
			};
		}
	}*/

	/*private void deleteBindingValueChangeListener() {
		for (BindingValueChangeListener<?> l : listeners.values()) {
			l.stopObserving();
			l.delete();
		}
		listeners.clear();
		listeners = null;
	}*/

	@Override
	public synchronized void delete() {

		if (enableBindingValueChangeListener != null) {
			enableBindingValueChangeListener.stopObserving();
			enableBindingValueChangeListener.delete();
		}

		super.delete();
	}

	public M getWidget() {
		return getComponent();
	}

	@Override
	public void updateData() {
		super.updateData();
		if (!widgetUpdating && !isDeleted() && getDynamicJComponent() != null && isComponentVisible()) {
			updateWidgetFromModel();
		}
	}

	/**
	 * Update the widget retrieving data from the model. This method is called when the observed property change.
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	public abstract boolean updateWidgetFromModel();

	/**
	 * Update the model given the actual state of the widget
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	public abstract boolean updateModelFromWidget();

	/*@Override
	public List<TargetObject> getChainedBindings(DataBinding<?> binding, TargetObject object) {
		return getWidget().getChainedBindings(binding, object);
	}

	@Override
	public List<DataBinding<?>> getDependencyBindings() {
		return getWidget().getDependencyBindings();
	}*/

	@Override
	public void focusGained(FocusEvent event) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("focusGained()");
		}
		gainFocus();
	}

	@Override
	public void focusLost(FocusEvent event) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("focusLost()");
		}

		if (event.getOppositeComponent() != null && SwingUtilities.isDescendingFrom(event.getOppositeComponent(), getJComponent())) {
			// Not relevant in this case
		} else {
			looseFocus();
		}
	}

	protected boolean _hasFocus;

	public void gainFocus() {
		if (getController() != null) {
			if (getController().getFocusedWidget() != null && getController().getFocusedWidget()._hasFocus == true) {
				getController().getFocusedWidget().looseFocus();
			}
			LOGGER.fine("Getting focus: " + getWidget());
			_hasFocus = true;
			getController().setFocusedWidget(this);
		}
	}

	public void looseFocus() {
		LOGGER.fine("Loosing focus: " + getWidget());
		if (!modelUpdating && !isDeleted()) {
			updateModelFromWidget();
		}
		_hasFocus = false;
	}

	public boolean isFocused() {
		return _hasFocus;
	}

	@Override
	public T getValue() {
		if (isDeleted()) {
			return null;
		}

		if (getWidget().getData() == null || getWidget().getData().isUnset()) {
			return getData();
		}

		if (getDataObject() == null) {
			return null;
		}

		Object value = null;

		try {
			/*if (getWidget().getData().toString().equals("data.targetPatternRole")) {
				System.out.println("hop");
				System.out.println("hop2");
				Object o = getBindingEvaluationContext().getValue(new BindingVariable("data", null));
				System.out.println("hop3");

			}*/

			/*if (getWidget().getData().toString().equals("data.userIdentifier")) {
				System.out.println("hop, data=" + getWidget().getData() + " valid=" + getWidget().getData().isValid());
				System.out.println("getBindingEvaluationContext()=" + getBindingEvaluationContext());
				Object data = getBindingEvaluationContext().getValue(new BindingVariable("data", null));
				System.out.println("data=" + data);
				System.out.println("userId=" + ((KeyValueCoding) data).objectForKey("userIdentifier"));
				value = getWidget().getData().getBindingValue(getBindingEvaluationContext());
				System.out.println("value=" + value + " of " + value.getClass());
			}*/

			value = getWidget().getData().getBindingValue(getBindingEvaluationContext());
			T returned = (T) value;
			setData(returned);
			return returned;
		} catch (TypeMismatchException e) {
			LOGGER.warning("Widget " + getWidget() + " TypeMismatchException: " + e.getMessage());
			return null;
		} catch (NullReferenceException e) {
			// logger.warning("Widget " + getWidget() + " NullReferenceException: " + e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
			LOGGER.warning("Widget " + getWidget() + " InvocationTargetException: " + e.getMessage());
			return null;
		}

	}

	public void setValue(T aValue) {
		if (!isEnabled()) {
			return;
		}

		if (isDeleted()) {
			return;
		}

		if (getWidget().getValueTransform() != null && getWidget().getValueTransform().isValid()) {
			T old = aValue;
			try {
				aValue = (T) getWidget().getValueTransform().getBindingValue(getValueBindingContext(aValue));
			} catch (TypeMismatchException e) {
				LOGGER.warning("Unexpected TypeMismatchException while evaluating " + getWidget().getValueTransform() + e.getMessage());
				e.printStackTrace();
			} catch (NullReferenceException e) {
				LOGGER.warning("Unexpected NullReferenceException while evaluating " + getWidget().getValueTransform() + e.getMessage());
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				LOGGER.warning("Unexpected InvocationTargetException while evaluating " + getWidget().getValueTransform() + e.getMessage());
				e.printStackTrace();
			}
			if (!equals(old, aValue)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateWidgetFromModel();
					}
				});
			}
		}

		boolean isValid = true;
		if (getWidget().getValueValidator() != null && getWidget().getValueValidator().isValid()) {
			Object object = null;
			try {
				object = getWidget().getValueValidator().getBindingValue(getValueBindingContext(aValue));
			} catch (TypeMismatchException e) {
				LOGGER.warning("Unexpected TypeMismatchException while evaluating " + getWidget().getValueValidator() + e.getMessage());
				e.printStackTrace();
			} catch (NullReferenceException e) {
				LOGGER.warning("Unexpected NullReferenceException while evaluating " + getWidget().getValueValidator() + e.getMessage());
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				LOGGER.warning("Unexpected InvocationTargetException while evaluating " + getWidget().getValueValidator() + e.getMessage());
				e.printStackTrace();
			}
			if (object == null) {
				isValid = false;
			} else if (object instanceof Boolean) {
				isValid = (Boolean) object;
			}
		}
		if (!isValid) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateWidgetFromModel();
				}
			});
			return;
		}

		setData(aValue);

		if (getWidget().getData() == null || getWidget().getData().isUnset()) {
		} else {
			if (getDataObject() == null) {
				return;
			}
			try {
				getWidget().getData().setBindingValue(aValue, getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				LOGGER.warning("Unexpected " + e + " cannot setValue() with " + getWidget().getData() + " and value " + aValue
						+ " message=" + e.getMessage());
				// e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NotSettableContextException e) {
				e.printStackTrace();
			}

		}

		updateComponentsExplicitelyDeclaredAsDependant();
		/*
		 * Iterator<FIBComponent> it = getWidget().getMayAltersIterator(); while(it.hasNext()) { FIBComponent c = it.next();
		 * logger.info("Modified "+aValue+" now update "+c); getController().viewForComponent(c).update(); }
		 */

		if (getWidget().getValueChangedAction().isValid()) {
			try {
				getWidget().getValueChangedAction().execute(getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	@Deprecated
	protected void updateDependingObjects() {

		// updateBindingValueChangeListeners();
	}

	/*public <T> void bindingValueHasChanged(DataBinding<T> dataBinding, T newValue) {
		// System.out.println("Widget "+getWidget()+" : receive notification "+o);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(new ArrayList<FIBComponent>());
				}
			});
		} else {
			update(new ArrayList<FIBComponent>());
		}
	}*/

	/*@Override
	public void update(Observable o, Object arg) {
		System.out.println("Widget " + getWidget() + " : receive notification " + o);*/
	/*if (!SwingUtilities.isEventDispatchThread()) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				update(new ArrayList<FIBComponent>());
			}
		});
	} else {
		update(new ArrayList<FIBComponent>());
	}*/
	// }

	@Override
	public final void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("Widget " + getWidget() + " : propertyChange " + evt);
		if (evt.getSource() instanceof FIBModelObject && (!((FIBModelObject) evt.getSource()).isDeleted())) {
			receivedModelNotifications((FIBModelObject) evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
		/*if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(new ArrayList<FIBComponent>());
				}
			});
		} else {
			update(new ArrayList<FIBComponent>());
		}*/
	}

	public void receivedModelNotifications(FIBModelObject o, String propertyName, Object oldValue, Object newValue) {

	}

	@Override
	protected boolean checkValidDataPath() {
		if (getParentView() != null && !getParentView().checkValidDataPath()) {
			return false;
		}
		if (getComponent().getDataType() != null) {
			Object value = getValue();
			if (value != null && !TypeUtils.isTypeAssignableFrom(getComponent().getDataType(), value.getClass(), true)) {
				// logger.fine("INVALID data path for component "+getComponent());
				// logger.fine("Value is "+getValue().getClass()+" while expected type is "+getComponent().getDataType());
				return false;
			}
		}
		return true;
	}

	/**
	 * This method is called to update view representing a FIBComponent.<br>
	 * 
	 * @return a flag indicating if component has been updated
	 */
	@Override
	public boolean update() {
		super.update();
		updateEnability();
		// if (isComponentVisible()) {
		updateDynamicTooltip();
		updateDependingObjects();

		if (updateWidgetFromModel()) {
			updateComponentsExplicitelyDeclaredAsDependant();
		}
		/*}*//* else if (checkValidDataPath()) {
				// Even if the component is not visible, its visibility may depend
				// it self from some depending component (which in that situation,
				// are very important to know, aren'they ?)
				updateDependingObjects();
				}*/

		if (enableBindingValueChangeListener != null) {
			enableBindingValueChangeListener.refreshObserving();
		}

		return true;
	}

	/**
	 * This method is called to update view representing a FIBComponent.<br>
	 * Callers are all the components that have been updated during current update loop. If the callers contains the component itself, does
	 * nothing and return.
	 * 
	 * @param callers
	 *            all the components that have been previously updated during current update loop
	 * @return a flag indicating if component has been updated
	 */
	/*@Override
	public boolean update(List<FIBComponent> callers) {
		try {
			if (!super.update(callers)) {
				return false;
			}
			updateEnability();
			// logger.info("Updating "+getWidget()+" value="+getValue());

			// Add the component to the list of callers to avoid loops
			callers.add(getComponent());

			if (isComponentVisible()) {
				updateDynamicTooltip();
				updateDependingObjects();
				if (updateWidgetFromModel()) {
					updateComponentsExplicitel(callers);
				}
			} else if (checkValidDataPath()) {
				// Even if the component is not visible, its visibility may depend
				// it self from some depending component (which in that situation,
				// are very important to know, aren'they ?)
				updateDependingObjects();
			}
			return true;
		} catch (Exception e) {
			logger.warning("Unexpected exception while updating FIBWidgetView: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}*/

	protected void updateComponentsExplicitelyDeclaredAsDependant() {
		if (getController() == null) {
			return;
		}
		// logger.info("updateDependancies() for " + getWidget());
		Iterator<FIBComponent> it = getWidget().getMayAltersIterator();
		while (it.hasNext()) {
			FIBComponent c = it.next();
			// logger.info("###### Component " + getWidget() + ", has been updated, now update " + c);
			FIBView<?, ?, ?> v = getController().viewForComponent(c);
			if (v != null) {
				// We want to avoid to update components that depends on me, when they are not visible
				v.updateVisibility();
				if (v.isComponentVisible()) {
					v.update();
				}
			} else {
				LOGGER.warning("Cannot find FIBView for component " + c);
			}
		}
		// logger.info("END updateDependancies() for " + getWidget());
	}

	// @Override
	// public void updateDataObject(final Object aDataObject) {
	/*if (!SwingUtilities.isEventDispatchThread()) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateDataObject(aDataObject);
			}
		});
		return;
	}*/
	// update(new ArrayList<FIBComponent>());
	// }

	@Override
	public void updateLanguage() {
		if (getValue() != null && getValue().getClass().isEnum() && getWidget().getLocalize()) {
			for (Object o : getValue().getClass().getEnumConstants()) {
				getStringRepresentation(o);
			}
		}
	}

	private DynamicValueBindingContext getValueBindingContext(T aValue) {
		if (valueBindingContext == null) {
			valueBindingContext = new DynamicValueBindingContext();
		}
		valueBindingContext.setValue(aValue);
		return valueBindingContext;
	}

	/**
	 * Return the effective base component to be added to swing hierarchy This component may be encapsulated in a JScrollPane
	 * 
	 * @return JComponent
	 */
	@Override
	public abstract JComponent getJComponent();

	/**
	 * Return the dynamic JComponent, ie the component on which dynamic is applied, and were actions are effective. This component must be
	 * either the same or a child of the one returned by {@link #getJComponent()}.
	 * 
	 * @return J
	 */
	@Override
	public abstract J getDynamicJComponent();

	public boolean isReadOnly() {
		return getWidget().getReadOnly();
	}

	public final boolean isWidgetEnabled() {
		return isComponentEnabled();
	}

	public final boolean isComponentEnabled() {
		boolean componentEnabled = true;
		if (getComponent() == null) {
			return false;
		}
		if (getComponent().getReadOnly()) {
			return false;
		}
		if (getComponent().getEnable() != null && getComponent().getEnable().isValid()) {
			try {
				Boolean isEnabled = getComponent().getEnable().getBindingValue(getBindingEvaluationContext());
				if (isEnabled != null) {
					componentEnabled = isEnabled;
				}
			} catch (TypeMismatchException e) {
				// logger.warning("Cannot evaluate: " + getComponent().getEnable());
				// e.printStackTrace();
				componentEnabled = true;
			} catch (NullReferenceException e) {
				// NullReferenceException is allowed, in this case, default enability is true
				componentEnabled = true;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				componentEnabled = true;
			}
		}
		return componentEnabled;
	}

	private void updateDynamicTooltip() {
		if (getComponent().getTooltip() != null && getComponent().getTooltip().isValid()) {
			try {
				String tooltipText = getComponent().getTooltip().getBindingValue(getBindingEvaluationContext());
				getDynamicJComponent().setToolTipText(tooltipText);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public String getStringRepresentation(final Object value) {
		if (value == null) {
			return "";
		}
		if (getWidget().getFormat() != null && getWidget().getFormat().isValid()) {
			formatter.setValue(value);
			String returned = null;
			try {
				returned = getWidget().getFormat().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (getWidget().getLocalize() && returned != null) {
				return getLocalized(returned);
			} else {
				return returned;
			}
		}
		if (value instanceof Enum) {
			String returned = value != null ? ((Enum<?>) value).name() : null;
			if (getWidget().getLocalize() && returned != null) {
				return getLocalized(returned);
			} else {
				return returned;
			}
		}
		if (value instanceof String) {
			if (getWidget().getLocalize()) {
				return getLocalized((String) value);
			}
		}
		return value.toString();
	}

	public Icon getIconRepresentation(final Object value) {
		if (value == null) {
			return null;
		}
		if (getWidget().getIcon() != null && getWidget().getIcon().isValid()) {
			formatter.setValue(value);
			try {
				return getWidget().getIcon().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void applySingleClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		try {
			getWidget().getClickAction().execute(eventListener);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void applyDoubleClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		try {
			getWidget().getDoubleClickAction().execute(eventListener);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void applyRightClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		try {
			getWidget().getRightClickAction().execute(eventListener);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public T getDefaultData() {
		return null;
	}

	@Override
	public void updateFont() {
		if (getFont() != null) {
			getDynamicJComponent().setFont(getFont());
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			getPropertyChangeSupport().firePropertyChange(VISIBLE, !enabled, enabled);
		}
	}

	/**
	 * Called when a widget becomes visible<br>
	 * There is no guarantee that widget is in sync with the model, so, we need to call updateWidgetFromModel again
	 */
	@Override
	protected void hiddenComponentBecomesVisible() {
		super.hiddenComponentBecomesVisible();
		updateWidgetFromModel();
	}

	public final void updateEnability() {
		if (isComponentEnabled()) {
			if (!enabled) {
				// Becomes enabled
				LOGGER.fine("Component becomes enabled");
				// System.out.println("Component  becomes enabled "+getJComponent());
				enableComponent(getJComponent());
				setEnabled(true);
			}
		} else {
			if (enabled) {
				// Becomes disabled
				LOGGER.fine("Component becomes disabled");
				// System.out.println("Component  becomes disabled "+getJComponent());
				disableComponent(getJComponent());
				setEnabled(false);
			}
		}
	}

	private void enableComponent(Component component) {
		if (component instanceof JScrollPane) {
			component = ((JScrollPane) component).getViewport().getView();
			if (component == null) {
				return;
			}
		}
		component.setEnabled(true);
		if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				enableComponent(c);
			}
		}
	}

	private void disableComponent(Component component) {
		if (component instanceof JScrollPane) {
			component = ((JScrollPane) component).getViewport().getView();
			if (component == null) {
				return;
			}
		}
		if (component != null) {
			component.setEnabled(false);
		}
		if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				disableComponent(c);
			}
		}
	}

	public abstract class FIBDynamicBindingEvaluationContext implements BindingEvaluationContext, HasPropertyChangeSupport {
		private Object value;
		private final PropertyChangeSupport pcSupport;

		public FIBDynamicBindingEvaluationContext() {
			pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

	}

	protected class DynamicValueBindingContext extends FIBDynamicBindingEvaluationContext {
		private Object value;
		protected final static String VALUE = "value";

		private void setValue(Object aValue) {
			Object oldValue = value;
			value = aValue;
			getPropertyChangeSupport().firePropertyChange(VALUE, oldValue, aValue);
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(VALUE)) {
				return value;
			} else {
				return getBindingEvaluationContext().getValue(variable);
			}
		}
	}

	protected class DynamicFormatter extends FIBDynamicBindingEvaluationContext {
		private Object value;
		protected final static String OBJECT = "object";

		private void setValue(Object aValue) {
			Object oldValue = value;
			value = aValue;
			getPropertyChangeSupport().firePropertyChange(OBJECT, oldValue, aValue);
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(OBJECT)) {
				return value;
			} else {
				return getBindingEvaluationContext().getValue(variable);
			}
		}
	}

	protected class DynamicEventListener extends FIBDynamicBindingEvaluationContext {
		private MouseEvent mouseEvent;
		protected final static String EVENT = "event";

		private void setEvent(MouseEvent mouseEvent) {
			MouseEvent oldEvent = this.mouseEvent;
			this.mouseEvent = mouseEvent;
			getPropertyChangeSupport().firePropertyChange(EVENT, oldEvent, mouseEvent);
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(EVENT)) {
				return mouseEvent;
			} else {
				return getBindingEvaluationContext().getValue(variable);
			}
		}
	}

	/**
	 * Semantics of this method is not trivial: the goal is to aggregate some notifications within a given time (supplied as a
	 * aggregationTimeOut), to do it only once.<br>
	 * Within this delay, all requests to this method will simply reinitialize time-out, and will be ignored. Only the first call will be
	 * executed in a new thread which will die immediately after.
	 * 
	 * @param r
	 *            runnable to run after last request + timeout
	 * @param aggregationTimeOut
	 *            in milliseconds
	 */
	public void invokeLater(final Runnable r, final long aggregationTimeOut) {
		synchronized (this) {
			lastSchedule = System.currentTimeMillis();
			if (!invokeLaterScheduled) {
				invokeLaterScheduled = true;
				Thread invokeLaterThread = new Thread(new Runnable() {
					@Override
					public void run() {
						while (System.currentTimeMillis() < lastSchedule + aggregationTimeOut) {
							// We need to wait
							try {
								Thread.sleep(aggregationTimeOut);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						synchronized (FIBWidgetView.this) {
							invokeLaterScheduled = false;
						}
						LOGGER.fine("Starting runnable declared in invokeLater");
						r.run();
					}
				}, "InvokeLaterThread");
				invokeLaterThread.start();
			} else {
				LOGGER.fine("Ignoring invokeLater");
			}
		}
	}

	private boolean invokeLaterScheduled = false;
	private long lastSchedule = -1;

}

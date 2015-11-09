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

package org.openflexo.gina.view.impl;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.GinaEvent.KIND;
import org.openflexo.gina.event.GinaEventNotifier;
import org.openflexo.gina.event.MissingIdentityParameterException;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBEventDescription;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBMouseEvent;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ToolBox;

/**
 * Abstract class representing a widget view
 * 
 * @author sylvain
 */
public abstract class FIBWidgetViewImpl<M extends FIBWidget, C, T> extends FIBViewImpl<M, C>implements FIBWidgetView<M, C, T> {

	private static final Logger LOGGER = Logger.getLogger(FIBWidgetViewImpl.class.getPackage().getName());

	public static final int META_MASK = ToolBox.getPLATFORM() == ToolBox.MACOS ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	public static final String ENABLED = "enabled";

	public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font DEFAULT_MEDIUM_FONT = new Font("SansSerif", Font.PLAIN, 10);

	protected C technologyComponent;

	private T data;

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

	protected GinaEventNotifier<FIBEventDescription> GENotifier;

	private BindingValueChangeListener<T> dataBindingValueChangeListener;

	protected FIBWidgetViewImpl(M model, FIBController aController, RenderingAdapter<C> RenderingAdapter) {
		super(model, aController, RenderingAdapter);
		formatter = new DynamicFormatter();
		valueBindingContext = new DynamicValueBindingContext();
		eventListener = new DynamicEventListener();
		eventListeningLocked = false;
		widgetExecuting = false;
		widgetDisableUserEvent = false;

		technologyComponent = makeTechnologyComponent();

		GENotifier = new GinaEventNotifier<FIBEventDescription>(this.getController().getEventManager(), this.getController()) {

			@Override
			public KIND computeClass(FIBEventDescription e) {
				if (!widgetUpdating && !widgetExecuting && !widgetDisableUserEvent)
					return KIND.USER_INTERACTION;
				else
					return KIND.SYSTEM_EVENT;
			}

			@Override
			public void setIdentity(FIBEventDescription e, Object o) throws MissingIdentityParameterException {
				e.setIdentity(getWidget().getBaseName(), getWidget().getName());
			}

		};

		// addBindingValueChangeListeners();
		listenDataValueChange();
		listenEnableValueChange();
	}

	/**
	 * Create technology-specific component representing FIBWidget
	 * 
	 * @return
	 */
	protected abstract C makeTechnologyComponent();

	/**
	 * Return technology-specific component representing widget<br>
	 * Note that, depending on the underlying technology, this technology-specific component might be embedded in an other component before
	 * to be added in component hierarchy (for example if component need to be embedded in a scroll pane)
	 * 
	 * @return C
	 */
	@Override
	public final C getTechnologyComponent() {
		return technologyComponent;
	}

	/**
	 * Return technology component for supplied {@link FIBComponent}<br>
	 * Return technology component if supplied {@link FIBComponent} is the represented widget itself, otherwise return null
	 * 
	 * @param component
	 * @return
	 */
	@Override
	public Object getTechnologyComponentForFIBComponent(FIBComponent component) {
		if (getComponent() == component) {
			return getTechnologyComponent();
		}
		return null;
	}

	private void listenDataValueChange() {
		if (dataBindingValueChangeListener != null) {
			dataBindingValueChangeListener.stopObserving();
			dataBindingValueChangeListener.delete();
		}

		if (getComponent().getData() != null && getComponent().getData().isValid()) {
			dataBindingValueChangeListener = new BindingValueChangeListener<T>((DataBinding<T>) getComponent().getData(),
					getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, T newValue) {
					// System.out.println(" bindingValueChanged() detected for data="
					// + getComponent().getData() + " with newValue="
					// + newValue + " source=" + source);

					updateData();
				}
			};
		}
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
					// System.out.println(" bindingValueChanged() detected for enable="
					// + getComponent().getEnable() + " with newValue="
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

	@Override
	public T getData() {
		return data;
	}

	@Override
	public void setData(T data) {

		T oldData = this.data;

		if (notEquals(oldData, data)) {

			if (data == null || (getComponent().getDataClass() == null)
					|| getComponent().getDataClass().isAssignableFrom(data.getClass())) {
				this.data = data;
				getPropertyChangeSupport().firePropertyChange(DATA, oldData, data);
			}
			else {
				if ((getComponent().getDataType() == null)
						|| TypeUtils.isTypeAssignableFrom(getComponent().getDataType(), data.getClass(), true)) {
					// System.out.println("OK, data " + data + " of " +
					// data.getClass() + " is an instance of "
					// + getComponent().getDataClass());
					this.data = (T) TypeUtils.castTo(data, getComponent().getDataType());
					getPropertyChangeSupport().firePropertyChange(DATA, oldData, data);
				}

				else {
					if (getComponent().getDataClass() != null) {
						// System.out.println("Sorry, data " + data + " of " +
						// data.getClass() + " is not an instance of "
						// + getComponent().getDataClass());
					}
					this.data = null;
					getPropertyChangeSupport().firePropertyChange(DATA, oldData, null);
				}
			}
		}
	}

	@Override
	public GinaEventNotifier<FIBEventDescription> getNotifier() {
		return GENotifier;
	}

	@Override
	public void lockListening() {
		eventListeningLocked = true;
	}

	@Override
	public void allowListening() {
		eventListeningLocked = false;
	}

	@Override
	public boolean isListeningLocked() {
		return eventListeningLocked;
	}

	@Override
	public void executeEvent(EventDescription e) {
	}

	/*
	 * @Override public boolean isMatching(GinaEvent e) { return false; }
	 * 
	 * @Override public GinaStackEvent eventPerformed(FIBEvent e) { return
	 * eventPerformed(e, !widgetUpdating && !widgetExecuting &&
	 * !widgetDisableUserEvent); }
	 * 
	 * public GinaStackEvent eventPerformed(FIBEvent e, boolean
	 * isUserInteraction) { e.setUserInteraction(isUserInteraction);
	 * 
	 * e.setIdentity(getWidget().getBaseName(), getWidget().getName(),
	 * getWidget().getRootComponent().getUniqueID()); if (!isListeningLocked())
	 * for (GinaEventListener fl : getWidget().getFibListeners())
	 * fl.eventPerformed(e);
	 * 
	 * // create the stack element return
	 * GinaHandler.getInstance().pushStackEvent(e); }
	 */

	// public void updateBindingValueChangeListeners() {
	// System.out.println("++++++++++++++++++++++++ updateBindingValueChangeListeners(). Est-ce vraiment necessaire ?");
	/*
	 * if (listeners != null) { deleteBindingValueChangeListener(); }
	 * addBindingValueChangeListeners();
	 */
	// }

	/*
	 * private void addBindingValueChangeListeners() { if (listeners != null) {
	 * deleteBindingValueChangeListener(); } listeners = new
	 * HashMap<DataBinding<?>, BindingValueChangeListener<?>>(); for (final
	 * DataBinding dataBinding : getDependencyBindings()) {
	 * BindingValueChangeListener listener = new
	 * BindingValueChangeListener(dataBinding, getBindingEvaluationContext()) {
	 * 
	 * @Override public void bindingValueChanged(Object source, Object newValue)
	 * { System.out.println(" YYYYYYYEEEEEEEEESSSSSSSSS aussi pour " +
	 * dataBinding + " avec newValue=" + newValue + " source=" + source); //
	 * bindingValueHasChanged(dataBinding, newValue); } }; } }
	 */

	/*
	 * private void deleteBindingValueChangeListener() { for
	 * (BindingValueChangeListener<?> l : listeners.values()) {
	 * l.stopObserving(); l.delete(); } listeners.clear(); listeners = null; }
	 */

	@Override
	public synchronized void delete() {

		if (dataBindingValueChangeListener != null) {
			dataBindingValueChangeListener.stopObserving();
			dataBindingValueChangeListener.delete();
		}

		if (enableBindingValueChangeListener != null) {
			enableBindingValueChangeListener.stopObserving();
			enableBindingValueChangeListener.delete();
		}

		super.delete();
	}

	@Override
	public M getWidget() {
		return getComponent();
	}

	@Override
	public void updateData() {
		setData(getValue());
		if (!widgetUpdating && !isDeleted() && getTechnologyComponent() != null && isComponentVisible()) {
			updateWidgetFromModel();
		}
	}

	/**
	 * Update the widget retrieving data from the model. This method is called when the observed property change.
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	@Override
	public abstract boolean updateWidgetFromModel();

	/**
	 * Update the model given the actual state of the widget
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	@Override
	public abstract boolean updateModelFromWidget();

	/*
	 * @Override public List<TargetObject> getChainedBindings(DataBinding<?>
	 * binding, TargetObject object) { return
	 * getWidget().getChainedBindings(binding, object); }
	 * 
	 * @Override public List<DataBinding<?>> getDependencyBindings() { return
	 * getWidget().getDependencyBindings(); }
	 */

	// TODO: refactor this: should be implemented in Swing
	public void focusGained(FocusEvent event) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("focusGained()");
		}
		gainFocus();
	}

	// TODO: refactor this: should be implemented in Swing
	public void focusLost(FocusEvent event) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("focusLost()");
		}

		if (getRenderingAdapter().newFocusedComponentIsDescendingFrom(getTechnologyComponent(), event)) {
			// Not relevant in this case
		}
		else {
			looseFocus();
		}
	}

	protected boolean _hasFocus;

	public void gainFocus() {
		if (getController() != null) {
			if (getController().getFocusedWidget() instanceof FIBWidgetViewImpl
					&& ((FIBWidgetViewImpl<?, ?, ?>) getController().getFocusedWidget())._hasFocus == true) {
				((FIBWidgetViewImpl<?, ?, ?>) getController().getFocusedWidget()).looseFocus();
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
	public final boolean hasValue() {
		return getComponent().getData() != null && getComponent().getData().isSet();
	}

	@SuppressWarnings("unchecked")
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
			/*
			 * if
			 * (getWidget().getData().toString().equals("data.targetPatternRole"
			 * )) { System.out.println("hop"); System.out.println("hop2");
			 * Object o = getBindingEvaluationContext().getValue(new
			 * BindingVariable("data", null)); System.out.println("hop3");
			 * 
			 * }
			 */

			/*
			 * if
			 * (getWidget().getData().toString().equals("data.userIdentifier"))
			 * { System.out.println("hop, data=" + getWidget().getData() +
			 * " valid=" + getWidget().getData().isValid());
			 * System.out.println("getBindingEvaluationContext()=" +
			 * getBindingEvaluationContext()); Object data =
			 * getBindingEvaluationContext().getValue(new
			 * BindingVariable("data", null)); System.out.println("data=" +
			 * data); System.out.println("userId=" + ((KeyValueCoding)
			 * data).objectForKey("userIdentifier")); value =
			 * getWidget().getData
			 * ().getBindingValue(getBindingEvaluationContext());
			 * System.out.println("value=" + value + " of " + value.getClass());
			 * }
			 */

			value = getWidget().getData().getBindingValue(getBindingEvaluationContext());
			T returned = (T) value;
			setData(returned);
			return returned;
		} catch (TypeMismatchException e) {
			LOGGER.warning("Widget " + getWidget() + " TypeMismatchException: " + e.getMessage());
			return null;
		} catch (NullReferenceException e) {
			// logger.warning("Widget " + getWidget() +
			// " NullReferenceException: " + e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
			LOGGER.warning("Widget " + getWidget() + " InvocationTargetException: " + e.getMessage());
			return null;
		}

	}

	@Override
	@SuppressWarnings("unchecked")
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
			}
			else if (object instanceof Boolean) {
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
		}
		else {
			if (getDataObject() == null) {
				return;
			}
			try {
				getWidget().getData().setBindingValue(aValue, getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				LOGGER.warning("Unexpected " + e + " cannot setValue() with " + getWidget().getData() + " and value " + aValue + " message="
						+ e.getMessage());
				// e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NotSettableContextException e) {
				e.printStackTrace();
			}

		}

		updateComponentsExplicitelyDeclaredAsDependant();
		/*
		 * Iterator<FIBComponent> it = getWidget().getMayAltersIterator();
		 * while(it.hasNext()) { FIBComponent c = it.next();
		 * logger.info("Modified "+aValue+" now update "+c);
		 * getController().viewForComponent(c).update(); }
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBWidget.DATA_KEY)) {
			updateData();
		}
		if (evt.getPropertyName().equals(FIBWidget.MANAGE_DYNAMIC_MODEL_KEY)) {
			getComponent().updateDynamicAccessBindingVariable();
		}
		else if (evt.getPropertyName().equals(FIBWidget.READ_ONLY_KEY) || evt.getPropertyName().equals(FIBWidget.ENABLE_KEY)) {
			updateEnability();
		}

		super.propertyChange(evt);
	}

	@Override
	protected boolean checkValidDataPath() {
		if (getParentView() instanceof FIBViewImpl && !((FIBViewImpl<?, ?>) getParentView()).checkValidDataPath()) {
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

		updateData();

		updateFont();

		updateEnability();
		// if (isComponentVisible()) {
		updateDynamicTooltip();
		updateDependingObjects();

		if (updateWidgetFromModel()) {
			updateComponentsExplicitelyDeclaredAsDependant();
		}
		/* } *//*
				* else if (checkValidDataPath()) { // Even if the component is
				* not visible, its visibility may depend // it self from some
				* depending component (which in that situation, // are very
				* important to know, aren'they ?) updateDependingObjects(); }
				*/

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
	/*
	 * @Override public boolean update(List<FIBComponent> callers) { try { if
	 * (!super.update(callers)) { return false; } updateEnability(); //
	 * logger.info("Updating "+getWidget()+" value="+getValue());
	 * 
	 * // Add the component to the list of callers to avoid loops
	 * callers.add(getComponent());
	 * 
	 * if (isComponentVisible()) { updateDynamicTooltip();
	 * updateDependingObjects(); if (updateWidgetFromModel()) {
	 * updateComponentsExplicitel(callers); } } else if (checkValidDataPath()) {
	 * // Even if the component is not visible, its visibility may depend // it
	 * self from some depending component (which in that situation, // are very
	 * important to know, aren'they ?) updateDependingObjects(); } return true;
	 * } catch (Exception e) {
	 * logger.warning("Unexpected exception while updating FIBWidgetView: " +
	 * e.getMessage()); e.printStackTrace(); return false; } }
	 */

	protected void updateComponentsExplicitelyDeclaredAsDependant() {
		if (getController() == null) {
			return;
		}
		// logger.info("updateDependancies() for " + getWidget());
		Iterator<FIBComponent> it = getWidget().getMayAltersIterator();
		while (it.hasNext()) {
			FIBComponent c = it.next();
			// logger.info("###### Component " + getWidget() +
			// ", has been updated, now update " + c);
			FIBView<?, ?> v = getController().viewForComponent(c);
			if (v instanceof FIBViewImpl) {
				// We want to avoid to update components that depends on me,
				// when they are not visible
				((FIBViewImpl<?, ?>) v).updateVisibility();
				if (v.isComponentVisible()) {
					v.update();
				}
			}
			else {
				LOGGER.warning("Cannot find FIBViewImpl for component " + c);
			}
		}
		// logger.info("END updateDependancies() for " + getWidget());
	}

	// @Override
	// public void updateDataObject(final Object aDataObject) {
	/*
	 * if (!SwingUtilities.isEventDispatchThread()) { if
	 * (logger.isLoggable(Level.WARNING)) { logger.warning(
	 * "Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass."
	 * ); } SwingUtilities.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() { updateDataObject(aDataObject); } });
	 * return; }
	 */
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

	@Override
	public boolean isReadOnly() {
		return getWidget().getReadOnly();
	}

	@Override
	public final boolean isWidgetEnabled() {
		return isComponentEnabled();
	}

	@Override
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
				// logger.warning("Cannot evaluate: " +
				// getComponent().getEnable());
				// e.printStackTrace();
				componentEnabled = true;
			} catch (NullReferenceException e) {
				// NullReferenceException is allowed, in this case, default
				// enability is true
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
				getRenderingAdapter().setToolTipText(getTechnologyComponent(), tooltipText);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
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
			}
			else {
				return returned;
			}
		}
		if (value instanceof Enum) {
			String returned = value != null ? ((Enum<?>) value).name() : null;
			if (getWidget().getLocalize() && returned != null) {
				return getLocalized(returned);
			}
			else {
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

	@Override
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

	/**
	 * Called after the platform-specific controller has detected a mouse event matching a single-click action<br>
	 * 
	 * @param event
	 *            platform-specific event which is translated into a generic FIBMouseEvent
	 */
	@Override
	public void applySingleClickAction(FIBMouseEvent event) {
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

	/**
	 * Called after the platform-specific controller has detected a mouse event matching a double-click action<br>
	 * 
	 * @param event
	 *            platform-specific event which is translated into a generic FIBMouseEvent
	 */
	@Override
	public void applyDoubleClickAction(FIBMouseEvent event) {
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

	/**
	 * Called after the platform-specific controller has detected a mouse event matching a right-click action<br>
	 * 
	 * @param event
	 *            platform-specific event which is translated into a generic FIBMouseEvent
	 */
	@Override
	public void applyRightClickAction(FIBMouseEvent event) {
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
			getRenderingAdapter().setFont(getTechnologyComponent(), getFont());
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
				getRenderingAdapter().setEnabled(getTechnologyComponent(), true);
				setEnabled(true);
			}
		}
		else {
			if (enabled) {
				// Becomes disabled
				LOGGER.fine("Component becomes disabled");
				// System.out.println("Component becomes disabled "+getJComponent());
				getRenderingAdapter().setEnabled(getTechnologyComponent(), false);
				setEnabled(false);
				setEnabled(false);
			}
		}
	}

	public abstract class FIBDynamicBindingEvaluationContext implements BindingEvaluationContext, HasPropertyChangeSupport {
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
			}
			else {
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
			}
			else {
				return getBindingEvaluationContext().getValue(variable);
			}
		}
	}

	protected class DynamicEventListener extends FIBDynamicBindingEvaluationContext {
		private FIBMouseEvent mouseEvent;
		protected final static String EVENT = "event";

		private void setEvent(FIBMouseEvent mouseEvent) {
			FIBMouseEvent oldEvent = this.mouseEvent;
			this.mouseEvent = mouseEvent;
			getPropertyChangeSupport().firePropertyChange(EVENT, oldEvent, mouseEvent);
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(EVENT)) {
				return mouseEvent;
			}
			else {
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
	@Override
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
						synchronized (FIBWidgetViewImpl.this) {
							invokeLaterScheduled = false;
						}
						LOGGER.fine("Starting runnable declared in invokeLater");
						r.run();
					}
				}, "InvokeLaterThread");
				invokeLaterThread.start();
			}
			else {
				LOGGER.fine("Ignoring invokeLater");
			}
		}
	}

	private boolean invokeLaterScheduled = false;
	private long lastSchedule = -1;

}

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.model.bindings.FIBChildBindingVariable;
import org.openflexo.gina.model.bindings.FIBComponentBindingModel;
import org.openflexo.gina.model.bindings.FIBVariableBindingVariable;
import org.openflexo.gina.model.bindings.RuntimeContext;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.operator.FIBIterationView;
import org.openflexo.gina.view.operator.FIBIterationView.IteratedContents;
import org.openflexo.gina.view.widget.FIBReferencedComponentWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;

/**
 * Default implementation of a {@link FIBView}
 * 
 * @author sylvain
 *
 * @param <M>
 *            type of {@link FIBComponent} this view represents
 * @param <C>
 *            type of technology-specific component
 * @param <T>
 *            type of data beeing represented by this view
 */
public abstract class FIBViewImpl<M extends FIBComponent, C> implements FIBView<M, C> {

	private static final Logger LOGGER = Logger.getLogger(FIBViewImpl.class.getPackage().getName());

	public static final int TOP_COMPENSATING_BORDER = 3;
	public static final int BOTTOM_COMPENSATING_BORDER = TOP_COMPENSATING_BORDER;
	public static final int LEFT_COMPENSATING_BORDER = 5;
	public static final int RIGHT_COMPENSATING_BORDER = LEFT_COMPENSATING_BORDER;

	private M component;
	private FIBController controller;

	private boolean visible = false;
	private boolean isDeleted = false;

	private FIBReferencedComponentWidget<C> embeddingComponent;

	private final PropertyChangeSupport pcSupport;

	private BindingValueChangeListener<Boolean> visibleBindingValueChangeListener;

	private final RenderingAdapter<C> renderingAdapter;

	// Values of variable
	// Lookup is performed on the name of the variable
	private final Map<String, Object> variables;
	private final Map<String, BindingValueChangeListener<?>> variableListeners;

	public FIBViewImpl(M model, FIBController controller, RenderingAdapter<C> renderingAdapter) {
		super();
		this.controller = controller;
		component = model;

		this.renderingAdapter = renderingAdapter;

		pcSupport = new PropertyChangeSupport(this);

		controller.registerView(this);

		model.getPropertyChangeSupport().addPropertyChangeListener(this);

		variables = new HashMap<>();
		variableListeners = new HashMap<>();

		// VERY IMPORTANT: we do it now and not later during update scheme because otherwise, we do not have any
		// chance to be notified of a visibility change !!!
		listenVisibleValueChange();
	}

	@Override
	public void delete() {

		// System.out.println("@@@@@@@@@ Delete view for component " + getComponent());
		// Thread.dumpStack();

		if (isDeleted) {
			return;
		}

		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);

		componentBecomesInvisible();

		stopListenVisibleValueChange();

		LOGGER.fine("Delete view for component " + getComponent());

		if (controller != null) {
			controller.unregisterView(this);
		}

		isDeleted = true;
		component = null;
		controller = null;
	}

	/**
	 * When called, means that the view is displayed again<br>
	 * Calling this method reactivate all observing schemes related to this view
	 */
	@Override
	public void showView() {
		if (!visible) {
			componentBecomesVisible();
		}
	}

	/**
	 * When called, means that the view is no more displayed<br>
	 * Calling this method disactivate all observing schemes related to this view
	 */
	@Override
	public void hideView() {
		componentBecomesInvisible();
	}

	/**
	 * Called when the component view explicitely change its visibility state from INVISIBLE to VISIBLE
	 */
	protected void componentBecomesVisible() {

		// System.out.println("************ Component " + getComponent() + " becomes VISIBLE !!!!!!");

		// Restart listen component variables
		startListeningVariablesValueChange();

		// VERY IMPORTANT: this was done in the constructor !
		// listenVisibleValueChange();

		visible = true;

		// Update properties of current component
		// (do not call update() otherwise a new verification of visibility will be done)
		// If technology component is null (may happen for iteration for example), skip this
		if (getTechnologyComponent() != null) {
			performUpdate();
		}

	}

	/**
	 * Called when the component view explicitely change its visibility state from VISIBLE to INVISIBLE
	 */
	protected void componentBecomesInvisible() {

		// System.out.println("************ Component " + getComponent() + " becomes INVISIBLE !!!!!!");

		// Don't listen anymore to component variables
		stopListeningVariablesValueChange();

		visible = false;

		// BIG TRICK: don't do it, otherwise you have no chance to be notified of a visibility change
		// DONT DO THAT: stopListenVisibleValueChange();
	}

	private void startListeningVariablesValueChange() {
		for (FIBVariable<?> variable : getComponent().getVariables()) {
			listenVariableValueChange(variable);
		}
	}

	private void stopListeningVariablesValueChange() {
		for (FIBVariable<?> variable : getComponent().getVariables()) {
			stopListenVariableValueChange(variable);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void listenVariableValueChange(final FIBVariable<T> variable) {

		BindingValueChangeListener<T> dataBindingValueChangeListener = (BindingValueChangeListener<T>) variableListeners.get(variable);

		if (dataBindingValueChangeListener != null) {
			stopListenVariableValueChange(variable);
		}

		if (variable.getValue() != null && variable.getValue().isValid()) {
			dataBindingValueChangeListener = new BindingValueChangeListener<T>(variable.getValue(), getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, T newValue) {
					setVariableValue(variable, newValue);
				}
			};
			variableListeners.put(variable.getName(), dataBindingValueChangeListener);

			// We force a first computing and notification of the value of variable
			try {
				T newValue = variable.getValue().getBindingValue(getBindingEvaluationContext());
				setVariableValue(variable, newValue);

			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void stopListenVariableValueChange(final FIBVariable<T> variable) {

		BindingValueChangeListener<T> dataBindingValueChangeListener = (BindingValueChangeListener<T>) variableListeners.get(variable);

		if (dataBindingValueChangeListener != null) {
			dataBindingValueChangeListener.stopObserving();
			dataBindingValueChangeListener.delete();
			variableListeners.remove(variable);
		}

	}

	/**
	 * Return value of supplied variable
	 * 
	 * @param variable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getVariableValue(FIBVariable<T> variable) {

		// Fixed Bug GINA-23
		// Caused by the fact that inspectors are overriden
		// Thus, FIBVariable "data" is duplicated here.
		// And the call to getVariableValue(FIBVariable) was incorrect because addressing the overriden FIBVariable.
		// To fix this, replaced HashMap variables in FIBViewImpl with a map where keys are String (and not FIBVariable)

		if (variables.containsKey(variable.getName())) {
			return (T) variables.get(variable.getName());
		}
		else if (getParentView() != null) {
			return getParentView().getVariableValue(variable);
		}
		return null;

	}

	protected <T> void fireVariableChanged(FIBVariable<T> variable, T oldValue, T newValue) {
		getPropertyChangeSupport().firePropertyChange(variable.getName(), oldValue, newValue);
	}

	/**
	 * Sets value of supplied variable
	 * 
	 * @param variable
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final <T> void setVariableValue(FIBVariable<T> variable, T value) {

		T oldValue = (T) variables.get(variable.getName());

		// System.out.println("setVariableValue " + variable.getName() + " de " + oldValue + " a " + value);

		if (notEquals(oldValue, value)) {
			variables.put(variable.getName(), value);
			// Fixed DIANA-23
			// Caution: modifications subject to regression: please report any regression
			try {
				if (isComponentVisible()) {
					fireVariableChanged(variable, oldValue, value);
				}
			} catch (NullReferenceException e) {
				// In this case, do nothing
			}
		}
	}

	@Override
	public RenderingAdapter<C> getRenderingAdapter() {
		return renderingAdapter;
	}

	private void listenVisibleValueChange() {
		if (visibleBindingValueChangeListener != null) {
			visibleBindingValueChangeListener.stopObserving();
			visibleBindingValueChangeListener.delete();
		}
		if (getComponent().getVisible() != null && getComponent().getVisible().isValid()) {
			visibleBindingValueChangeListener = new BindingValueChangeListener<Boolean>(getComponent().getVisible(),
					getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, Boolean newValue) {
					if (newValue != null && newValue) {
						// Handle the case where the component becomes visible AND has some variables
						// In this case, we have to update values of variables first
						for (FIBVariable variable : getComponent().getVariables()) {
							try {
								Object newVariableValue = variable.getValue().getBindingValue(getBindingEvaluationContext());
								setVariableValue(variable, newVariableValue);

							} catch (TypeMismatchException e) {
								e.printStackTrace();
							} catch (NullReferenceException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}

						}
					}
					updateVisibility();
				}

				@Override
				protected Boolean getDefaultValue() {
					return true;
				}

			};
		}
	}

	private void stopListenVisibleValueChange() {
		if (visibleBindingValueChangeListener != null) {
			visibleBindingValueChangeListener.stopObserving();
			visibleBindingValueChangeListener.delete();
			visibleBindingValueChangeListener = null;
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	@Override
	public boolean isViewVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if (this.visible != visible) {
			this.visible = visible;
			getPropertyChangeSupport().firePropertyChange(VISIBLE, !visible, visible);
		}
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public FIBController getController() {
		return controller;
	}

	/**
	 * Return the BindingEvaluationContext valid in the context of current widget.<br>
	 * Note that an embedded component (components used in the context of FIBReferencedComponent) should point to the
	 * BindingEvaluationContext of its embedding component
	 * 
	 * @return
	 */
	@Override
	public final BindingEvaluationContext getBindingEvaluationContext() {

		if (getEmbeddingComponent() != null) {
			return getEmbeddingComponent().getEmbeddedBindingEvaluationContext();
		}

		FIBContainerView<?, ?, ?> current = getParentView();
		while (current != null) {
			if (current instanceof FIBIterationView) {
				IteratedContents<?> iteratedContents = ((FIBIterationView) current).getIteratedContents(this);
				if (iteratedContents != null) {
					return iteratedContents;
				}
			}
			current = current.getParentView();
		}

		return this;

	}

	@Override
	public Object getValue(BindingVariable variable) {
		Object returned = null;
		if (variable.getVariableName().equals(FIBComponentBindingModel.CONTROLLER_KEY)) {
			return getController();
		}
		if (variable instanceof FIBVariableBindingVariable && getController() != null) {
			FIBComponent owner = ((FIBVariableBindingVariable) variable).getVariable().getOwner();
			FIBView<?, ?> variableView = getController().viewForComponent(owner);
			if (variableView == null) {
				// Try with the root view
				variableView = getController().getRootView();
			}
			if (variableView != null) {
				returned = variableView.getVariableValue(((FIBVariableBindingVariable) variable).getVariable());
			}
			if (returned != null) {
				return returned;
			}
		}
		if (variable instanceof FIBChildBindingVariable && getController() != null) {
			FIBView<?, ?> view = getController().viewForComponent(((FIBChildBindingVariable) variable).getComponent());
			if (view == null) {
				LOGGER.fine("Could not access component view for " + ((FIBChildBindingVariable) variable).getComponent());
			}
			return view;
		}

		if (getRuntimeContext() != null) {
			return getRuntimeContext().getValue(variable);
		}

		FIBContainerView<?, ?, ?> pView = getParentView();
		if (pView != null) {
			return pView.getValue(variable);
		}

		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setValue(Object value, BindingVariable variable) {
		if (variable instanceof FIBVariableBindingVariable) {
			FIBViewImpl variableView = (FIBViewImpl) getController()
					.viewForComponent(((FIBVariableBindingVariable) variable).getVariable().getOwner());
			if (variableView != null) {
				variableView.setVariableValue(((FIBVariableBindingVariable) variable).getVariable(), value);
			}
		}
		else if (getParentView() != null) {
			getParentView().setValue(value, variable);
		}
	}

	@Override
	public final Object getDataObject() {
		return getController().getDataObject();
	}

	@Override
	public M getComponent() {
		return component;
	}

	@Override
	public abstract void updateLanguage();

	/**
	 * Return technology-specific component representing widget<br>
	 * Note that, depending on the underlying technology, this technology-specific component might be embedded in an other component before
	 * to be added in component hierarchy (for example if component need to be embedded in a scroll pane)
	 * 
	 * @return C
	 */
	@Override
	public abstract C getTechnologyComponent();

	/**
	 * This method is called to update view representing a {@link FIBComponent}.
	 * 
	 * This is the major entry point for component updating.<br>
	 * <ul>
	 * <li>First visibility is updated</li>
	 * <li>If component is visible, then update all other properties, and then recursively call {@link #update()} on all components
	 * contained in this component</li>
	 * </ul>
	 * 
	 * This method should not be called to update a given property, as relevant property should listen itself to relevant notifications.
	 * 
	 * This method should be called with caution, as it might raise performance issues
	 * 
	 * Usually, this method should be called only once, when the component has been added to the whole hierarchy.
	 * 
	 * @return a flag indicating if component has been updated
	 */
	@Override
	public final boolean update() {

		if (isDeleted()) {
			System.out.println("update() called for a deleted component view !!!");
			return false;
		}

		if (isUpdating) {
			return false;
		}

		isUpdating = true;

		updateVisibility();

		if (!isViewVisible()) {
			isUpdating = false;
			return true;
		}

		// Even if technology component is null, update visibility before to escape
		if (getTechnologyComponent() == null) {
			return false;
		}

		performUpdate();
		isUpdating = false;

		return true;
	}

	private boolean isUpdating = false;

	/**
	 * Return flag indicating if this view is beeing updating
	 * 
	 * @return
	 */
	public boolean isUpdating() {
		return isUpdating;
	}

	/**
	 * Internally called to update the view, once the visibility has been handled by {@link #update()} method
	 * 
	 */
	protected void performUpdate() {

		if (getRenderingAdapter() != null) {
			updatePreferredSize();
			updateMaximumSize();
			updateMinimumSize();
			updateOpacity();
			updateBackgroundColor();
			updateForegroundColor();
			updateFont();
		}

		// IMPORTANT (Sylvain):
		// I commented out following statement which seems to me unnecessary
		// I'm not really sure not to have caused any regression
		/*
		 * if (dataBindingValueChangeListener != null) {
		 * dataBindingValueChangeListener.refreshObserving(); } if
		 * (visibleBindingValueChangeListener != null) {
		 * visibleBindingValueChangeListener.refreshObserving(); }
		 */

	}

	/**
	 * Computes and return visibility status for component, according to the component hierarchy (this component must be present in a
	 * hierarchy of components where all components are visible), and the eventual "visible" binding declared in this component<br>
	 * 
	 * @return
	 * @throws NullReferenceException
	 *             if computed expression leads to a NullReferenceException
	 */
	@Override
	public final boolean isComponentVisible() throws NullReferenceException {

		if (getParentView() != null && !getParentView().isComponentVisible()) {
			return false;
		}

		boolean componentVisible = true;
		if (getComponent() != null && getComponent().getVisible() != null && getComponent().getVisible().isSet()) {
			try {
				/*if (getComponent().getName() != null && getComponent().getName().contains("Background")) {
					System.out.println("je regarde si le composant " + getComponent().getName() + " est visible");
				}*/
				Boolean isVisible = getComponent().getVisible().getBindingValue(getBindingEvaluationContext());
				if (isVisible != null) {
					componentVisible = isVisible;
				}
			} catch (TypeMismatchException e) {
				LOGGER.warning("Unable to evaluate " + getComponent().getVisible());
				e.printStackTrace();
			} catch (NullReferenceException e) {
				throw e;
			} catch (InvocationTargetException e) {
				componentVisible = true;
			}
		}
		return componentVisible;
	}

	protected void updateVisibility() {
		try {
			// We would like to catch here NullReferenceException while computing visibility status
			// What we don't want is to get a visible status from false to true, just because a visible binding
			// was not evaluable because of a NullRereferenceException
			// If this happen, juste escape this method without changing status

			boolean newVisibilityStatus = isComponentVisible();

			if (newVisibilityStatus != visible) {
				visible = !visible;
				setVisible(visible);

				if (visible) {
					componentBecomesVisible();
				}
				else {
					componentBecomesInvisible();
				}

				// Render visibility status now
				if (getRenderingAdapter() != null) {
					getRenderingAdapter().setVisible(getTechnologyComponent(), visible);
				}

				if (getParentView() != null) {
					getParentView().invalidateAndUpdateLayoutLater();
					// getParentView().revalidateAndRepaint();
				}

			}
		} catch (NullReferenceException e1) {
			// do nothing
		}

	}

	public Object getDefaultData() {
		return null;
	}

	private FIBContainerView<?, ?, ?> parentView;

	@Override
	public FIBContainerView<?, ?, ?> getParentView() {
		/*if (getComponent() != null) {
			if (getComponent().getParent() != null) {
				return (FIBContainerView<?, ?, ?>) getController().viewForComponent(getComponent().getParent());
			}
		}
		return null;*/
		return parentView;
	}

	@Override
	public void setParentView(FIBContainerView<?, ?, ?> parentView) {
		this.parentView = parentView;
	}

	@Override
	public Font getFont() {
		if (getComponent() != null) {
			return getComponent().retrieveValidFont();
		}
		return null;
	}

	@Override
	public abstract void updateFont();

	@Override
	public String getLocalized(String key) {
		if (getController() != null && getController().getLocalizerForComponent(getComponent()) != null) {
			return getController().getLocalizerForComponent(getComponent()).localizedForKeyAndLanguage(key,
					FlexoLocalization.getCurrentLanguage(), true);
		}
		return key;
	}

	@Override
	public void languageChanged(Language language) {
		updateLanguage();
	}

	protected void updateOpacity() {
		if (getComponent() == null) {
			return;
		}
		if (getComponent().getOpaque() != null) {
			renderingAdapter.setOpaque(getTechnologyComponent(), getComponent().getOpaque());
			getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
		}
	}

	protected void updatePreferredSize() {
		if (getComponent() == null) {
			return;
		}
		if (getComponent().definePreferredDimensions()) {
			Dimension preferredSize = getRenderingAdapter().getPreferredSize(getTechnologyComponent());
			if (getComponent().getWidth() != null) {
				preferredSize.width = getComponent().getWidth();
			}
			if (getComponent().getHeight() != null) {
				preferredSize.height = getComponent().getHeight();
			}
			getRenderingAdapter().setPreferredSize(getTechnologyComponent(), preferredSize);
		}
		else {
			if (getComponent().hasTemporarySize()) {
				getRenderingAdapter().setPreferredSize(getTechnologyComponent(),
						new Dimension(getComponent().getTemporaryWidth(), getComponent().getTemporaryHeight()));
			}
			else {
				// System.out.println("Clear de la prefered size pour " +
				// getComponent());
				if (getTechnologyComponent() != null && getRenderingAdapter() != null) {
					getRenderingAdapter().setPreferredSize(getTechnologyComponent(), null);
				}
			}
		}
	}

	protected void updateMinimumSize() {
		if (getComponent() == null) {
			return;
		}
		if (getComponent().defineMinDimensions()) {
			Dimension minSize = getRenderingAdapter().getMinimumSize(getTechnologyComponent());
			if (getComponent().getMinWidth() != null) {
				minSize.width = getComponent().getMinWidth();
			}
			if (getComponent().getMinHeight() != null) {
				minSize.height = getComponent().getMinHeight();
			}
			getRenderingAdapter().setMinimumSize(getTechnologyComponent(), minSize);
		}
	}

	protected void updateMaximumSize() {
		if (getComponent() == null) {
			return;
		}
		if (getComponent().defineMaxDimensions()) {
			Dimension maxSize = getRenderingAdapter().getMaximumSize(getTechnologyComponent());
			if (getComponent().getMaxWidth() != null) {
				maxSize.width = getComponent().getMaxWidth();
			}
			if (getComponent().getMaxHeight() != null) {
				maxSize.height = getComponent().getMaxHeight();
			}
			getRenderingAdapter().setMinimumSize(getTechnologyComponent(), maxSize);
		}
	}

	protected void updateBackgroundColor() {
		if (getComponent() == null) {
			return;
		}
		if (getComponent().getBackgroundColor() != null) {
			getRenderingAdapter().setBackgroundColor(getTechnologyComponent(), getComponent().getBackgroundColor());
			getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
		}
		else {
			getRenderingAdapter().setBackgroundColor(getTechnologyComponent(),
					getRenderingAdapter().getDefaultBackgroundColor(getTechnologyComponent()));
			getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
		}
	}

	protected void updateForegroundColor() {
		if (getComponent() == null) {
			return;
		}
		if (getComponent().getForegroundColor() != null) {
			getRenderingAdapter().setForegroundColor(getTechnologyComponent(), getComponent().getForegroundColor());
			getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
		}
		else {
			getRenderingAdapter().setForegroundColor(getTechnologyComponent(),
					getRenderingAdapter().getDefaultForegroundColor(getTechnologyComponent()));
			getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
		}
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return o1 == null;
		}
		if (o1.equals(o2)) {
			return true;
		}
		else if (o1 instanceof Collection && o2 instanceof Collection) {
			return sameCollections((Collection) o1, (Collection) o2);
		}
		else {
			return false;
		}
	}

	public static boolean sameCollections(Collection c1, Collection c2) {
		return c1.size() == c2.size() && c1.containsAll(c2);
	}

	public static boolean notEquals(Object o1, Object o2) {
		return !equals(o1, o2);
	}

	@Override
	public FIBReferencedComponentWidget<C> getEmbeddingComponent() {
		return embeddingComponent;
	}

	@Override
	public void setEmbeddingComponent(FIBReferencedComponentWidget<C> embeddingComponent) {
		this.embeddingComponent = embeddingComponent;
	}

	@Override
	public void requestFocus() {
		getRenderingAdapter().requestFocus(getTechnologyComponent());
	}

	@Override
	public void requestFocusInWindow() {
		getRenderingAdapter().requestFocusInWindow(getTechnologyComponent());
	}

	@Override
	public void requestFocusInParent() {
		getRenderingAdapter().requestFocusInParent(getTechnologyComponent());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBComponent.CONSTRAINTS_KEY) || evt.getPropertyName().equals(FIBComponent.USE_SCROLL_BAR_KEY)
				|| evt.getPropertyName().equals(FIBComponent.HORIZONTAL_SCROLLBAR_POLICY_KEY)
				|| evt.getPropertyName().equals(FIBComponent.VERTICAL_SCROLLBAR_POLICY_KEY)) {
			if (getParentView() != null) {
				getParentView().updateLayout(false);
			}
		}
		else if (evt.getPropertyName().equals(FIBComponent.MIN_WIDTH_KEY) || evt.getPropertyName().equals(FIBComponent.MIN_HEIGHT_KEY)
				|| evt.getPropertyName().equals(FIBComponent.DEFINE_MIN_DIMENSIONS)) {
			updateMinimumSize();
			if (getParentView() != null) {
				getParentView().updateLayout(false);
			}
		}
		else if (evt.getPropertyName().equals(FIBComponent.WIDTH_KEY) || evt.getPropertyName().equals(FIBComponent.HEIGHT_KEY)
				|| evt.getPropertyName().equals(FIBComponent.DEFINE_PREFERRED_DIMENSIONS)) {
			updatePreferredSize();
			if (getParentView() != null) {
				getParentView().updateLayout(false);
			}
		}
		else if (evt.getPropertyName().equals(FIBComponent.MAX_WIDTH_KEY) || evt.getPropertyName().equals(FIBComponent.MAX_HEIGHT_KEY)
				|| evt.getPropertyName().equals(FIBComponent.DEFINE_MAX_DIMENSIONS)) {
			updateMaximumSize();
			if (getParentView() != null) {
				getParentView().updateLayout(false);
			}
		}
		else if (evt.getPropertyName().equals(FIBComponent.FONT_KEY)) {
			updateFont();
		}
		else if (evt.getPropertyName().equals(FIBComponent.BACKGROUND_COLOR_KEY)) {
			updateBackgroundColor();
		}
		else if (evt.getPropertyName().equals(FIBComponent.FOREGROUND_COLOR_KEY)) {
			updateForegroundColor();
		}
		else if (evt.getPropertyName().equals(FIBComponent.OPAQUE_KEY)) {
			updateOpacity();
		}

	}

	private RuntimeContext runtimeContext;

	@Override
	public RuntimeContext getRuntimeContext() {
		if (runtimeContext == null && getParentView() instanceof FIBViewImpl) {
			return ((FIBViewImpl) getParentView()).getRuntimeContext();
		}
		return runtimeContext;
	}

	@Override
	public void setRuntimeContext(RuntimeContext runtimeContext) {
		if ((runtimeContext == null && this.runtimeContext != null)
				|| (runtimeContext != null && !runtimeContext.equals(this.runtimeContext))) {
			RuntimeContext oldValue = this.runtimeContext;
			this.runtimeContext = runtimeContext;
			getPropertyChangeSupport().firePropertyChange("iteratedContents", oldValue, runtimeContext);
		}
	}

}

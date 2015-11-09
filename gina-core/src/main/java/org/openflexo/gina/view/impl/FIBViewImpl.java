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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
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

	private boolean visible = true;
	private boolean isDeleted = false;

	private FIBReferencedComponentWidget<C> embeddingComponent;

	private final PropertyChangeSupport pcSupport;

	private BindingValueChangeListener<Boolean> visibleBindingValueChangeListener;

	private final RenderingAdapter<C> renderingAdapter;

	private final Map<FIBVariable<?>, Object> variables;
	private final Map<FIBVariable<?>, BindingValueChangeListener<?>> variableListeners;

	public FIBViewImpl(M model, FIBController controller, RenderingAdapter<C> renderingAdapter) {
		super();
		this.controller = controller;
		component = model;

		this.renderingAdapter = renderingAdapter;

		pcSupport = new PropertyChangeSupport(this);

		controller.registerView(this);

		model.getPropertyChangeSupport().addPropertyChangeListener(this);

		variables = new HashMap<FIBVariable<?>, Object>();
		variableListeners = new HashMap<FIBVariable<?>, BindingValueChangeListener<?>>();

		for (FIBVariable<?> variable : getComponent().getVariables()) {
			listenVariableValueChange(variable);
		}

		listenVisibleValueChange();

	}

	@SuppressWarnings("unchecked")
	private <T> void listenVariableValueChange(final FIBVariable<T> variable) {

		BindingValueChangeListener<T> dataBindingValueChangeListener = (BindingValueChangeListener<T>) variableListeners.get(variable);

		if (dataBindingValueChangeListener != null) {
			dataBindingValueChangeListener.stopObserving();
			dataBindingValueChangeListener.delete();
		}

		if (variable.getValue() != null && variable.getValue().isValid()) {
			dataBindingValueChangeListener = new BindingValueChangeListener<T>(variable.getValue(), getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, T newValue) {
					// System.out.println(" bindingValueChanged() detected for data="
					// + getComponent().getData() + " with newValue="
					// + newValue + " source=" + source);

					setVariableValue(variable, newValue);
				}
			};
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
		return (T) variables.get(variable);
	}

	/**
	 * Sets value of supplied variable
	 * 
	 * @param variable
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> void setVariableValue(FIBVariable<T> variable, T value) {

		T oldValue = (T) variables.get(variable);

		if (notEquals(oldValue, value)) {
			variables.put(variable, value);
			getPropertyChangeSupport().firePropertyChange(variable.getName(), oldValue, value);
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
					// System.out.println(" bindingValueChanged() detected for visible="
					// + getComponent().getVisible() + " with newValue="
					// + newValue + " source=" + source);
					updateVisibility();
				}

				@Override
				protected Boolean getDefaultValue() {
					return true;
				}

			};
			/*
			 * if (getComponent().getVisible().toString().equals(
			 * "EnableMultipleLayoutsCheckBox.data")) {
			 * System.out.println("******** ok je l'ai");
			 * System.out.println(visibleBindingValueChangeListener.toString());
			 * }
			 */
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
	public void delete() {

		LOGGER.fine("@@@@@@@@@ Delete view for component " + getComponent());

		if (isDeleted) {
			return;
		}

		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);

		if (visibleBindingValueChangeListener != null) {
			visibleBindingValueChangeListener.stopObserving();
			visibleBindingValueChangeListener.delete();
		}

		LOGGER.fine("Delete view for component " + getComponent());

		if (controller != null) {
			controller.unregisterView(this);
		}

		isDeleted = true;
		component = null;
		controller = null;
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

	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public FIBController getController() {
		return controller;
	}

	/**
	 * Return the BindingEvaluationContext valid in the context of current widget.<br>
	 * Note that embedded component (components used in the context of FIBReferencedComponent) should point to the BindingEvaluationContext
	 * of their embedding component
	 * 
	 * @return
	 */
	@Override
	public BindingEvaluationContext getBindingEvaluationContext() {
		/*
		 * if (getComponent() != null && getComponent().getName() != null &&
		 * getComponent().getName().equals("DropSchemePanel")) { if
		 * (getEmbeddingComponent() == null) {
		 * System.out.println("for DropSchemePanel embedding component is " +
		 * getEmbeddingComponent()); } }
		 */
		/*
		 * if (getComponent() != null && getComponent().getName() != null &&
		 * getComponent().getName().equals("DropSchemeWidget")) {
		 * System.out.println("for DropSchemeWidget embedding component is " +
		 * getEmbeddingComponent()); }
		 */
		if (getEmbeddingComponent() != null) {
			return getEmbeddingComponent().getEmbeddedBindingEvaluationContext();
		}
		if (getParentView() != null) {
			return getParentView().getBindingEvaluationContext();
		}
		return getController();
	}

	@Override
	public final Object getDataObject() {
		return getController().getDataObject();
	}

	@Override
	public M getComponent() {
		return component;
	}

	// public abstract void updateDataObject(Object anObject);

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
	 * This method is called to update view representing a {@link FIBComponent}.<br>
	 * Usually, this method should be called only once, when the component has been added to the whole hierarchy.
	 * 
	 * @return a flag indicating if component has been updated
	 */
	@Override
	public boolean update() {

		System.out.println("update of " + this);

		updateVisibility();

		// IMPORTANT (Sylvain):
		// I commented out following statement which seems to me unnecessary
		// I'm not really sure not to have caused any regression
		/*
		 * if (dataBindingValueChangeListener != null) {
		 * dataBindingValueChangeListener.refreshObserving(); } if
		 * (visibleBindingValueChangeListener != null) {
		 * visibleBindingValueChangeListener.refreshObserving(); }
		 */

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
	 * public boolean update(List<FIBComponent> callers) { if
	 * (callers.contains(getComponent())) { return false; } updateVisibility();
	 * return true; }
	 */

	protected abstract boolean checkValidDataPath();

	@Override
	public final boolean isComponentVisible() {
		/*
		 * if (getComponent().getName() != null &&
		 * getComponent().getName().equals("DropSchemePanel")) {
		 * System.out.println("Bon, je me demande si c'est visible");
		 * System.out.println("getComponent().getVisible()=" +
		 * getComponent().getVisible()); System.out.println("valid=" +
		 * getComponent().getVisible().isValid());
		 * System.out.println("getBindingEvaluationContext=" +
		 * getBindingEvaluationContext()); try { System.out.println("result=" +
		 * getComponent
		 * ().getVisible().getBindingValue(getBindingEvaluationContext()));
		 * DataBinding<Object> binding1 = new DataBinding<Object>("data",
		 * getComponent(), Object.class, BindingDefinitionType.GET);
		 * System.out.println("data=" +
		 * binding1.getBindingValue(getBindingEvaluationContext()));
		 * DataBinding<Object> binding2 = new
		 * DataBinding<Object>("EditionActionBrowser.selected", getComponent(),
		 * Object.class, BindingDefinitionType.GET);
		 * System.out.println("EditionActionBrowser.selected=" +
		 * binding2.getBindingValue(getBindingEvaluationContext())); } catch
		 * (TypeMismatchException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (NullReferenceException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (InvocationTargetException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */

		if (getParentView() != null && !getParentView().isComponentVisible()) {
			return false;
		}

		boolean componentVisible = true;
		if (getComponent().getVisible() != null && getComponent().getVisible().isSet()) {
			try {
				Boolean isVisible = getComponent().getVisible().getBindingValue(getBindingEvaluationContext());
				if (isVisible != null) {
					componentVisible = isVisible;
				}
			} catch (TypeMismatchException e) {
				LOGGER.warning("Unable to evaluate " + getComponent().getVisible());
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// NullReferenceException is allowed, in this case, default
				// visibility is true
				componentVisible = true;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				componentVisible = true;
			}
		}
		return componentVisible;
	}

	protected void updateVisibility() {
		if (isComponentVisible() != visible) {
			visible = !visible;
			getRenderingAdapter().setVisible(getTechnologyComponent(), visible);
			if (visible) {
				hiddenComponentBecomesVisible();
			}
			setVisible(visible);
		}
	}

	protected void hiddenComponentBecomesVisible() {
	}

	public Object getDefaultData() {
		return null;
	}

	@Override
	public FIBContainerView<?, ?, ?> getParentView() {
		if (getComponent() != null) {
			if (getComponent().getParent() != null) {
				return (FIBContainerView<?, ?, ?>) getController().viewForComponent(getComponent().getParent());
			}
		}
		return null;
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
		if (getController().getLocalizerForComponent(getComponent()) != null) {
			return FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(getComponent()), key);
		}
		return key;
	}

	@Override
	public void languageChanged(Language language) {
		updateLanguage();
	}

	@Override
	public void updateGraphicalProperties() {
		updatePreferredSize();
		updateMaximumSize();
		updateMinimumSize();
		updateOpacity();
		updateBackgroundColor();
		updateForegroundColor();
	}

	protected void updateOpacity() {
		if (getComponent().getOpaque() != null) {
			renderingAdapter.setOpaque(getTechnologyComponent(), getComponent().getOpaque());
		}
	}

	protected void updatePreferredSize() {
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
	}

	protected void updateMinimumSize() {
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
		if (getComponent().getBackgroundColor() != null) {
			getRenderingAdapter().setBackgroundColor(getTechnologyComponent(), getComponent().getBackgroundColor());
		}
	}

	protected void updateForegroundColor() {
		if (getComponent().getForegroundColor() != null) {
			getRenderingAdapter().setForegroundColor(getTechnologyComponent(), getComponent().getForegroundColor());
		}
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null) {
			return o2 == null;
		}
		else {
			return o1.equals(o2);
		}
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
		/*
		 * if (getComponent() != null && getComponent().getName() != null &&
		 * getComponent().getName().equals("DropSchemePanel")) {
		 * System.out.println
		 * ("Set emmbedding component for DropSchemePanel with " +
		 * embeddingComponent); }
		 */
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
		if (evt.getPropertyName().equals(FIBComponent.CONSTRAINTS_KEY) || evt.getPropertyName().equals(FIBComponent.WIDTH_KEY)
				|| evt.getPropertyName().equals(FIBComponent.HEIGHT_KEY) || evt.getPropertyName().equals(FIBComponent.MIN_WIDTH_KEY)
				|| evt.getPropertyName().equals(FIBComponent.MIN_HEIGHT_KEY) || evt.getPropertyName().equals(FIBComponent.MAX_WIDTH_KEY)
				|| evt.getPropertyName().equals(FIBComponent.MAX_HEIGHT_KEY)
				|| evt.getPropertyName().equals(FIBComponent.USE_SCROLL_BAR_KEY)
				|| evt.getPropertyName().equals(FIBComponent.HORIZONTAL_SCROLLBAR_POLICY_KEY)
				|| evt.getPropertyName().equals(FIBComponent.VERTICAL_SCROLLBAR_POLICY_KEY)) {
			getParentView().updateLayout();
			// FIBEditorController controller = getEditorController();
			// controller.notifyFocusedAndSelectedObject();
		}
		else if (evt.getPropertyName().equals(FIBComponent.FONT_KEY)) {
			updateFont();
		}
		else if (evt.getPropertyName().equals(FIBComponent.BACKGROUND_COLOR_KEY)
				|| evt.getPropertyName().equals(FIBComponent.FOREGROUND_COLOR_KEY)
				|| evt.getPropertyName().equals(FIBComponent.OPAQUE_KEY)) {
			updateGraphicalProperties();
			// getTechnologyComponent().revalidate();
			// getTechnologyComponent().repaint();
		}

	}

}

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

package org.openflexo.fib.view.impl;

import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.widget.FIBReferencedComponentWidget;
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

	private JScrollPane scrolledComponent;

	private FIBReferencedComponentWidget<C> embeddingComponent;

	private final PropertyChangeSupport pcSupport;

	private BindingValueChangeListener<Boolean> visibleBindingValueChangeListener;

	private final RenderingTechnologyAdapter<C> renderingTechnologyAdapter;

	public FIBViewImpl(M model, FIBController controller, RenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super();
		this.controller = controller;
		component = model;

		this.renderingTechnologyAdapter = renderingTechnologyAdapter;

		pcSupport = new PropertyChangeSupport(this);

		controller.registerView(this);

		listenVisibleValueChange();

	}

	@Override
	public RenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return renderingTechnologyAdapter;
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
					// System.out.println(" bindingValueChanged() detected for visible=" + getComponent().getVisible() + " with newValue="
					// + newValue + " source=" + source);
					updateVisibility();
				}

				@Override
				protected Boolean getDefaultValue() {
					return true;
				}

			};
			/*if (getComponent().getVisible().toString().equals("EnableMultipleLayoutsCheckBox.data")) {
				System.out.println("******** ok je l'ai");
				System.out.println(visibleBindingValueChangeListener.toString());
			}*/
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
		if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("DropSchemePanel")) {
			if (getEmbeddingComponent() == null) {
				System.out.println("for DropSchemePanel embedding component is " + getEmbeddingComponent());
			}
		}*/
		/*if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("DropSchemeWidget")) {
			System.out.println("for DropSchemeWidget embedding component is " + getEmbeddingComponent());
		}*/
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
	public final M getComponent() {
		return component;
	}

	// public abstract void updateDataObject(Object anObject);

	@Override
	public abstract void updateLanguage();

	/**
	 * Return the effective base component to be added to swing hierarchy This component may be encapsulated in a JScrollPane
	 * 
	 * @return JComponent
	 */
	@Override
	public abstract JComponent getJComponent();

	/**
	 * Return technology-specific component representing widget<br>
	 * Note that, depending on the underlying technology, this technology-specific component might be embedded in an other component before to 
	 * be added in component hierarchy (for example if component need to be embedded in a scroll pane)
	 * 
	 * @return C
	 */
	@Override
	public abstract C getTechnologyComponent();

	/**
	 * Return the effective component to be added to swing hierarchy This component may be the same as the one returned by
	 * {@link #getJComponent()} or a encapsulation in a JScrollPane
	 * 
	 * @return JComponent
	 */
	@Override
	public JComponent getResultingJComponent() {
		if (getComponent().getUseScrollBar()) {
			if (scrolledComponent == null) {
				scrolledComponent = new JScrollPane(getJComponent(), getComponent().getVerticalScrollbarPolicy().getPolicy(),
						getComponent().getHorizontalScrollbarPolicy().getPolicy());
				scrolledComponent.setOpaque(false);
				scrolledComponent.getViewport().setOpaque(false);
				scrolledComponent.setBorder(BorderFactory.createEmptyBorder());
			}
			return scrolledComponent;
		}
		else {
			return getJComponent();
		}
	}

	/**
	 * This method is called to update view representing a FIBComponent.<br>
	 * 
	 * @return a flag indicating if component has been updated
	 */
	@Override
	public boolean update() {

		updateVisibility();

		// IMPORTANT (Sylvain):
		// I commented out following statement which seems to me unnecessary
		// I'm not really sure not to have caused any regression
		/*if (dataBindingValueChangeListener != null) {
			dataBindingValueChangeListener.refreshObserving();
		}
		if (visibleBindingValueChangeListener != null) {
			visibleBindingValueChangeListener.refreshObserving();
		}*/

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
	/*public boolean update(List<FIBComponent> callers) {
		if (callers.contains(getComponent())) {
			return false;
		}
		updateVisibility();
		return true;
	}*/

	protected abstract boolean checkValidDataPath();

	@Override
	public final boolean isComponentVisible() {
		/*if (getComponent().getName() != null && getComponent().getName().equals("DropSchemePanel")) {
			System.out.println("Bon, je me demande si c'est visible");
			System.out.println("getComponent().getVisible()=" + getComponent().getVisible());
			System.out.println("valid=" + getComponent().getVisible().isValid());
			System.out.println("getBindingEvaluationContext=" + getBindingEvaluationContext());
			try {
				System.out.println("result=" + getComponent().getVisible().getBindingValue(getBindingEvaluationContext()));
				DataBinding<Object> binding1 = new DataBinding<Object>("data", getComponent(), Object.class, BindingDefinitionType.GET);
				System.out.println("data=" + binding1.getBindingValue(getBindingEvaluationContext()));
				DataBinding<Object> binding2 = new DataBinding<Object>("EditionActionBrowser.selected", getComponent(), Object.class,
						BindingDefinitionType.GET);
				System.out.println("EditionActionBrowser.selected=" + binding2.getBindingValue(getBindingEvaluationContext()));
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

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
				// NullReferenceException is allowed, in this case, default visibility is true
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
			getResultingJComponent().setVisible(visible);
			if (getResultingJComponent().getParent() instanceof JComponent) {
				((JComponent) getResultingJComponent().getParent()).revalidate();
			}
			else if (getResultingJComponent().getParent() != null) {
				getResultingJComponent().getParent().validate();
			}
			if (getResultingJComponent().getParent() != null) {
				getResultingJComponent().getParent().repaint();
			}
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
			renderingTechnologyAdapter.setOpaque(getTechnologyComponent(), getComponent().getOpaque());
		}
	}

	protected void updatePreferredSize() {
		if (getComponent().definePreferredDimensions()) {
			Dimension preferredSize = getJComponent().getPreferredSize();
			if (getComponent().getWidth() != null) {
				preferredSize.width = getComponent().getWidth();
			}
			if (getComponent().getHeight() != null) {
				preferredSize.height = getComponent().getHeight();
			}
			getJComponent().setPreferredSize(preferredSize);
		}
	}

	protected void updateMinimumSize() {
		if (getComponent().defineMinDimensions()) {
			Dimension minSize = getJComponent().getMinimumSize();
			if (getComponent().getMinWidth() != null) {
				minSize.width = getComponent().getMinWidth();
			}
			if (getComponent().getMinHeight() != null) {
				minSize.height = getComponent().getMinHeight();
			}
			getJComponent().setMinimumSize(minSize);
		}
	}

	protected void updateMaximumSize() {
		if (getComponent().defineMaxDimensions()) {
			Dimension maxSize = getJComponent().getMaximumSize();
			if (getComponent().getMaxWidth() != null) {
				maxSize.width = getComponent().getMaxWidth();
			}
			if (getComponent().getMaxHeight() != null) {
				maxSize.height = getComponent().getMaxHeight();
			}
			getJComponent().setMinimumSize(maxSize);
		}
	}

	protected void updateBackgroundColor() {
		getJComponent().setBackground(getComponent().getBackgroundColor());
	}

	protected void updateForegroundColor() {
		getJComponent().setForeground(getComponent().getForegroundColor());
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
		/* if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("DropSchemePanel")) {
			System.out.println("Set emmbedding component for DropSchemePanel with " + embeddingComponent);
		}*/
		this.embeddingComponent = embeddingComponent;
	}
}

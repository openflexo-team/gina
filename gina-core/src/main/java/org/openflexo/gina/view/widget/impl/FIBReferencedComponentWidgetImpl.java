/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBReferencedComponent.FIBReferenceAssignment;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBReferencedComponentWidget;
import org.openflexo.kvc.InvalidKeyValuePropertyException;
import org.openflexo.rm.Resource;

/**
 * This component allows to reuse an other component, and embed it into a widget<br>
 * 
 * Referenced component may be statically or dynamically referenced
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view is embedding (this is the type of embedded component)
 * 
 * @author sylvain
 * 
 */
public abstract class FIBReferencedComponentWidgetImpl<C> extends FIBWidgetViewImpl<FIBReferencedComponent, C, Object>
		implements FIBReferencedComponentWidget<C> {

	private static final Logger logger = Logger.getLogger(FIBReferencedComponentWidgetImpl.class.getPackage().getName());

	public static final String COMPONENT = "component";

	private FIBComponent referencedComponent = null;

	protected FIBController embeddedFIBController;
	private final boolean isComponentLoading = false;

	private BindingValueChangeListener<Resource> dynamicComponentFileBindingValueChangeListener;
	private BindingValueChangeListener<FIBComponent> dynamicComponentBindingValueChangeListener;

	public FIBReferencedComponentWidgetImpl(FIBReferencedComponent model, FIBController controller,
			ReferencedComponentRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
	}

	/**
	 * Called when the component view explicitely change its visibility state from INVISIBLE to VISIBLE
	 */
	@Override
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		listenDynamicComponentFileValueChange();
		listenDynamicComponentValueChange();
	}

	/**
	 * Called when the component view explicitely change its visibility state from VISIBLE to INVISIBLE
	 */
	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenDynamicComponentFileValueChange();
		stopListenDynamicComponentValueChange();
	}

	@Override
	public ReferencedComponentRenderingAdapter<C> getRenderingAdapter() {
		return (ReferencedComponentRenderingAdapter<C>) super.getRenderingAdapter();
	}

	private void listenDynamicComponentFileValueChange() {
		if (dynamicComponentFileBindingValueChangeListener != null) {
			dynamicComponentFileBindingValueChangeListener.stopObserving();
			dynamicComponentFileBindingValueChangeListener.delete();
		}
		if (getComponent().getDynamicComponentFile() != null && getComponent().getDynamicComponentFile().isValid()) {
			dynamicComponentFileBindingValueChangeListener = new BindingValueChangeListener<Resource>(
					getComponent().getDynamicComponentFile(), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, Resource newValue) {
					// System.out.println(" bindingValueChanged() detected for dynamicComponentFile="
					// + getComponent().getDynamicComponentFile() + " with newValue=" + newValue + " source=" + source);
					updateComponent();
					// updateReferencedComponentView();
				}
			};
		}
	}

	private void stopListenDynamicComponentFileValueChange() {
		if (dynamicComponentFileBindingValueChangeListener != null) {
			dynamicComponentFileBindingValueChangeListener.stopObserving();
			dynamicComponentFileBindingValueChangeListener.delete();
			dynamicComponentFileBindingValueChangeListener = null;
		}
	}

	private void listenDynamicComponentValueChange() {
		if (dynamicComponentBindingValueChangeListener != null) {
			dynamicComponentBindingValueChangeListener.stopObserving();
			dynamicComponentBindingValueChangeListener.delete();
		}
		if (getComponent().getDynamicComponent() != null && getComponent().getDynamicComponent().isValid()) {
			dynamicComponentBindingValueChangeListener = new BindingValueChangeListener<FIBComponent>(getComponent().getDynamicComponent(),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, FIBComponent newValue) {
					// System.out.println(" bindingValueChanged() detected for dynamicComponent=" + getComponent().getDynamicComponent()
					// + " with newValue=" + newValue + " source=" + source);
					updateComponent();
					// updateReferencedComponentView();
				}
			};
		}
	}

	private void stopListenDynamicComponentValueChange() {
		if (dynamicComponentBindingValueChangeListener != null) {
			dynamicComponentBindingValueChangeListener.stopObserving();
			dynamicComponentBindingValueChangeListener.delete();
			dynamicComponentBindingValueChangeListener = null;
		}
	}

	public FIBComponent getReferencedComponent() {
		return referencedComponent;
	}

	public void setReferencedComponent(FIBComponent referencedComponent) {
		FIBComponent oldReferencedComponent = this.referencedComponent;
		if (oldReferencedComponent != referencedComponent) {
			this.referencedComponent = referencedComponent;
			getPropertyChangeSupport().firePropertyChange(COMPONENT, oldReferencedComponent, referencedComponent);
			referencedComponentChanged();
		}
	}

	protected abstract void referencedComponentChanged();

	public Resource getComponentFile() {
		FIBReferencedComponent widg = getWidget();
		// NPE Protection when widget is null
		if (widg != null) {
			if (widg.getDynamicComponentFile() != null && widg.getDynamicComponentFile().isSet()
					&& widg.getDynamicComponentFile().isValid()) {
				// The component file is dynamically defined, use it
				try {
					return widg.getDynamicComponentFile().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

			else if (widg.getComponentFile() != null) {
				// The component file is statically defined, use it
				return widg.getComponentFile();
			}
		}
		else {
			logger.warning("FIBReferencedComponent with null widget, please investigate");
		}

		return null;
	}

	protected void updateComponent() {
		FIBComponent oldReferencedComponent = referencedComponent;

		FIBComponent newReferencedComponent = retrieveReferencedComponent();

		if (notEquals(oldReferencedComponent, newReferencedComponent)) {
			// Component changed !!!!
			setReferencedComponent(newReferencedComponent);
		}

	}

	/**
	 * This is the internal method which computed the FIBComponent to display, either as a static or a dynamic scheme
	 * 
	 * @return
	 */
	private FIBComponent retrieveReferencedComponent() {

		FIBReferencedComponent widget = getWidget();
		// NPE Protection when widget is null
		if (widget != null) {

			if (widget.getDynamicComponent() != null && widget.getDynamicComponent().isSet() && widget.getDynamicComponent().isValid()) {
				// The component is dynamically defined, use it
				try {
					return widget.getDynamicComponent().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			}
		}

		// Maybe the component file is defined ???
		Resource componentFile = getComponentFile();

		if (componentFile != null) {
			if (getComponent().getFIBLibrary() != null) {
				return getComponent().getFIBLibrary().retrieveFIBComponent(componentFile);
			}
			else {
				logger.warning("Component has no FIBLibrary !");
				Thread.dumpStack();
			}
		}
		return null;
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateComponent();
		updateReferencedComponentView();
	}

	@Override
	public Object updateData() {
		if (referencedComponent != retrieveReferencedComponent()) {
			// We receive here a notification for a data change
			// BUT....
			// Meanwhile the referenced component changed too. It will be notified a little bit later.
			// So we return yet, and let the updateComponent() handle the new data value

			return null;
		}

		Object returned = super.updateData();
		updateReferencedComponentView();
		return returned;
	}

	@Override
	public abstract FIBView<?, C> getReferencedComponentView();

	protected FIBController makeEmbeddedFIBController(FIBComponent component) {
		// System.out.println("Building FIBController for component " + component);
		if (getComponent().getControllerFactory() != null && getComponent().getControllerFactory().isSet()
				&& getComponent().getControllerFactory().isValid()) {
			// System.out.println("Using custom factory");
			FIBController returned = null;
			try {
				returned = getComponent().getControllerFactory().getBindingValue(getBindingEvaluationContext());
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
			// System.out.println("factory " + getComponent().getControllerFactory() + " gives " + returned);
			if (returned != null) {
				return returned;
			}
		}
		return FIBController.instanciateController(component, getController().getViewFactory(), getController().getLocalizer());
	}

	protected FIBView<?, C> makeReferencedComponentView() {

		FIBView<?, C> referencedComponentView = null;

		FIBComponent loaded = getReferencedComponent();

		if (loaded != null) {

			// Now, we instantiate a new embedded FIBController

			FIBController embeddedFIBController = makeEmbeddedFIBController(loaded);
			embeddedFIBController.setDataObject(getValue());

			if (loaded instanceof FIBWidget) {
				referencedComponentView = (FIBWidgetView) embeddedFIBController.getViewFactory().makeWidget((FIBWidget) loaded,
						embeddedFIBController, true);
				referencedComponentView.setEmbeddingComponent(this);
			}
			else if (loaded instanceof FIBContainer) {
				referencedComponentView = (FIBContainerView) embeddedFIBController.getViewFactory().makeContainer((FIBContainer) loaded,
						embeddedFIBController, true);
				referencedComponentView.setEmbeddingComponent(this);
			}

		}
		else {
			if (!isComponentLoading) {
				logger.warning("ReferencedComponent = null and I'm NOT loading anything... : " + this.getComponentFile());
			}
		}

		return referencedComponentView;
	}

	public FIBController getEmbeddedFIBController() {
		return embeddedFIBController;
	}

	private void performAssignments() {

		if (embeddedFIBController == null) {
			return;
		}
		for (FIBReferenceAssignment assign : getWidget().getAssignments()) {
			DataBinding<?> variableDB = assign.getVariable();
			DataBinding<?> valueDB = assign.getValue();
			if (valueDB != null && valueDB.isValid()) {
				Object value = null;
				try {
					value = valueDB.getBindingValue(getBindingEvaluationContext());
					if (variableDB.isValid()) {
						Object oldValue = variableDB.getBindingValue(embeddedFIBController.getRootView());
						if (value != oldValue) {
							variableDB.setBindingValue(value, embeddedFIBController.getRootView());
						}
					}
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
	}

	protected void updateReferencedComponentView() {

		if (getReferencedComponent() == null) {
			return;
		}

		// logger.info("************ updateReferencedComponentView() called in FIBReferencedComponentWidget");

		// Kept for future debug use
		/*if (getComponent() != null && getComponent().getName() != null && getComponent().getName().equals("EditionActionWidget")) {
			FIBComponent rootComponent = embeddedFIBController.getRootComponent();
			System.out.println("************ OK, on remet a jour la FIBReferencedComponentWidget");
			System.out.println("rootComponent=" + rootComponent);
			System.out.println("rootComponent.root=" + rootComponent.getRootComponent());
			System.out.println("dataType=" + ((FIBContainer) rootComponent).getDataType());
			System.out.println("dataType2=" + ((FIBContainer) rootComponent.getRootComponent()).getDataType());
			System.out.println(rootComponent.getFIBLibrary().stringRepresentation(rootComponent));
		}*/

		if (getReferencedComponentView() != null && embeddedFIBController.getRootComponent() instanceof FIBContainer) {

			// Kept for future debug use
			/*FIBModelFactory fibModelFactory;
			try {
				fibModelFactory = new FIBModelFactory();
				System.out.println(fibModelFactory.stringRepresentation(embeddedFIBController.getRootComponent()));
			} catch (ModelDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			FIBContainer rootComponent = (FIBContainer) embeddedFIBController.getRootComponent();
			Type expectedDataType = null; // ((FIBContainer) embeddedFIBController.getRootComponent().getRootComponent()).getDataType();
			if (rootComponent.getVariables().size() > 0) {
				expectedDataType = rootComponent.getVariables().get(0).getType();
			}

			// If value is of right type, we update the referenced widget
			if (getValue() == null || TypeUtils.isOfType(getValue(), expectedDataType)) {

				performAssignments();

				embeddedFIBController.setDataObject(getValue(), false);

				// getReferencedComponentView().update();

			}
			else {
				logger.warning("Inconsistant data: " + getValue() + " is not a " + expectedDataType);
			}

		}

	}

	@Override
	public BindingEvaluationContext getEmbeddedBindingEvaluationContext() {
		return embeddedFIBController.getRootView();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBReferencedComponent.COMPONENT_FILE_KEY)
				|| evt.getPropertyName().equals(FIBReferencedComponent.DYNAMIC_COMPONENT_FILE_KEY)
				|| evt.getPropertyName().equals(FIBReferencedComponent.DYNAMIC_COMPONENT_KEY)) {
			updateComponent();
		}

		super.propertyChange(evt);
	}

}

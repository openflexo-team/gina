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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.FIBLibrary;
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
	private FIBView<FIBComponent, C> referencedComponentView;

	private FIBController embeddedFIBController;
	// private final GinaViewFactory factory;
	private final boolean isComponentLoading = false;

	private BindingValueChangeListener<Resource> dynamicComponentFileBindingValueChangeListener;

	public FIBReferencedComponentWidgetImpl(FIBReferencedComponent model, FIBController controller,
			ReferencedComponentRenderingAdapter<C> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		listenDynamicComponentFileValueChange();
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
					updateReferencedComponentView();
				}
			};
		}
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public boolean updateModelFromWidget() {
		return false;
	}

	public FIBComponent getReferencedComponent() {
		return referencedComponent;
	}

	public void setReferencedComponent(FIBComponent referencedComponent) {
		FIBComponent oldReferencedComponent = this.referencedComponent;
		if (oldReferencedComponent != referencedComponent) {
			this.referencedComponent = referencedComponent;
			getPropertyChangeSupport().firePropertyChange(COMPONENT, oldReferencedComponent, referencedComponent);
		}

	}

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

	private FIBComponent retrieveReferencedComponent() {

		FIBReferencedComponent widg = getWidget();
		// NPE Protection when widget is null
		if (widg != null) {

			if (widg.getDynamicComponent() != null && widg.getDynamicComponent().isSet() && widg.getDynamicComponent().isValid()) {
				// The component is dynamically defined, use it
				try {
					return widg.getDynamicComponent().getBindingValue(getBindingEvaluationContext());
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
			return FIBLibrary.instance().retrieveFIBComponent(componentFile);
		}
		return null;
	}

	@Override
	public boolean update() {
		super.update();
		updateDynamicallyReferencedComponentWhenRequired();
		return true;
	}

	/*@Override
	public final void updateDataObject(final Object dataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(dataObject);
				}
			});
			return;
		}
		// super.updateDataObject(dataObject);
		updateDynamicallyReferencedComponentWhenRequired();
		super.updateDataObject(dataObject);
	}*/

	/**
	 * Called whenever the referenced component may have changed
	 */
	private boolean updateDynamicallyReferencedComponentWhenRequired() {
		// We now check that the referenced component is still valid
		if (referencedComponent != retrieveReferencedComponent()) {
			// We have detected that referenced component has changed
			// We reset internal values and call updateLayout on the container
			referencedComponent = retrieveReferencedComponent();
			if (referencedComponentView != null) {
				referencedComponentView.delete();
				referencedComponentView = null;
			}
			// Call the parent view for a complete layout: the referencedComponentView will be computed during this loop
			if (getParentView() != null) {
				getParentView().updateLayout();
			}
			return true;
		}
		// Otherwise referenced component has not changed
		return false;
	}

	@Override
	protected C makeTechnologyComponent() {

		FIBComponent loaded = getReferencedComponent();

		// If an embedded FIBController is already declared, delete it
		if (embeddedFIBController != null) {
			embeddedFIBController.delete();
			embeddedFIBController = null;
		}

		if (loaded != null) {

			// Now, we instantiate a new embedded FIBController

			embeddedFIBController = FIBController.instanciateController(loaded, getController().getViewFactory(),
					getController().getLocalizer());

			embeddedFIBController.setDataObject(getValue());

			if (loaded instanceof FIBWidget) {
				referencedComponentView = (FIBWidgetView) embeddedFIBController.getViewFactory().makeWidget((FIBWidget) loaded,
						embeddedFIBController);
				referencedComponentView.setEmbeddingComponent(this);
			}
			else if (loaded instanceof FIBContainer) {
				referencedComponentView = (FIBContainerView) embeddedFIBController.getViewFactory().makeContainer((FIBContainer) loaded,
						embeddedFIBController);
				referencedComponentView.setEmbeddingComponent(this);
			}

		}
		else {
			if (!isComponentLoading) {
				logger.warning("ReferencedComponent = null and I'm NOT loading anything... : " + this.getComponentFile().getURI());
			}
		}

		return referencedComponentView.getTechnologyComponent();
	}

	@Override
	public FIBView<FIBComponent, C> getReferencedComponentView() {
		if (referencedComponentView == null) {
			technologyComponent = makeTechnologyComponent();
		}
		return referencedComponentView;
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
						Object oldValue = variableDB.getBindingValue(embeddedFIBController);
						if (value != oldValue) {
							variableDB.setBindingValue(value, embeddedFIBController);
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

	private boolean updateReferencedComponentView() {

		// logger.info("updateReferencedComponentView() called in FIBReferencedComponentWidget");

		boolean returned = updateDynamicallyReferencedComponentWhenRequired();

		if (getReferencedComponentView() != null) {

			if ((getValue() == null)
					|| (TypeUtils.isTypeAssignableFrom(embeddedFIBController.getRootComponent().getDataType(), getValue().getClass()))) {

				performAssignments();

				embeddedFIBController.setDataObject(getValue(), /*true*/false);

				referencedComponentView.update();
			}
			else {
				logger.warning("Inconsistant data: " + getValue() + " is not a " + embeddedFIBController.getRootComponent().getDataType());
			}

		}

		return returned;
	}

	@Override
	public boolean updateWidgetFromModel() {

		// logger.info("updateWidgetFromModel() called in FIBReferencedComponentWidget");

		return updateReferencedComponentView();

	}

	public void updateComponent() {
		referencedComponentView = null;
		logger.info("Updating component not implemented yet");
		getParentView().update();
		((FIBContainerView) getParentView()).updateLayout();
		getParentView().update();
	}

	@Override
	public BindingEvaluationContext getEmbeddedBindingEvaluationContext() {
		// return getBindingEvaluationContext();
		// return this;
		return embeddedFIBController;
	}

}

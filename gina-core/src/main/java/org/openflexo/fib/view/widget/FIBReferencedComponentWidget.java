/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.view.widget;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingValueChangeListener;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBViewFactory;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBReferencedComponent.FIBReferenceAssignment;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.rm.Resource;

/**
 * This component allows to reuse an other component, and embed it into a widget<br>
 * 
 * Referenced component may be statically or dynamically referenced
 * 
 * @author sguerin
 * 
 */
public class FIBReferencedComponentWidget extends FIBWidgetView<FIBReferencedComponent, JComponent, Object> /* implements
BindingEvaluationContext*/{

	private static final Logger logger = Logger.getLogger(FIBReferencedComponentWidget.class.getPackage().getName());

	public static final String COMPONENT = "component";

	private FIBComponent referencedComponent = null;
	private FIBView<FIBComponent, JComponent, ?> referencedComponentView;

	private FIBController embeddedFIBController;
	// private final FIBViewFactory factory;
	private boolean isComponentLoading = false;

	private final JLabel NOT_FOUND_LABEL;

	private BindingValueChangeListener<Resource> dynamicComponentFileBindingValueChangeListener;

	public FIBReferencedComponentWidget(FIBReferencedComponent model, FIBController controller, FIBViewFactory factory) {
		super(model, controller);
		// this.factory = factory;
		NOT_FOUND_LABEL = new JLabel(""/*"<" + model.getName() + ": not found component>"*/);
		updateFont();
		listenDynamicComponentFileValueChange();
	}

	private void listenDynamicComponentFileValueChange() {
		if (dynamicComponentFileBindingValueChangeListener != null) {
			dynamicComponentFileBindingValueChangeListener.stopObserving();
			dynamicComponentFileBindingValueChangeListener.delete();
		}
		if (getComponent().getDynamicComponentFile() != null && getComponent().getDynamicComponentFile().isValid()) {
			dynamicComponentFileBindingValueChangeListener = new BindingValueChangeListener<Resource>(getComponent()
					.getDynamicComponentFile(), getBindingEvaluationContext()) {

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
	public synchronized boolean updateModelFromWidget() {
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

		if (getWidget().getDynamicComponentFile() != null && getWidget().getDynamicComponentFile().isSet()
				&& getWidget().getDynamicComponentFile().isValid()) {
			// The component file is dynamically defined, use it
			File componentFile;
			try {
				return getWidget().getDynamicComponentFile().getBindingValue(getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		else if (getWidget().getComponentFile() != null) {
			// The component file is statically defined, use it
			return getWidget().getComponentFile();
		}

		return null;
	}

	private FIBComponent retrieveReferencedComponent() {

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
			getParentView().updateLayout();
			return true;
		}
		// Otherwise referenced component has not changed
		return false;
	}

	public FIBView<FIBComponent, JComponent, ?> getReferencedComponentView() {
		if (referencedComponentView == null && !isComponentLoading) {
			// System.out.println(">>>>>>> Making new FIBView for " + getWidget() + " for " + getWidget().getComponent());

			isComponentLoading = true;
			FIBComponent loaded = getReferencedComponent();

			// If an embedded FIBController is already declared, delete it
			if (embeddedFIBController != null) {
				embeddedFIBController.delete();
				embeddedFIBController = null;
			}

			if (loaded != null) {

				// Now, we instantiate a new embedded FIBController

				embeddedFIBController = FIBController.instanciateController(loaded, getController().getLocalizer());

				embeddedFIBController.setDataObject(getValue());

				if (loaded instanceof FIBWidget) {
					referencedComponentView = embeddedFIBController.getViewFactory().makeWidget((FIBWidget) loaded);
					referencedComponentView.setEmbeddingComponent(this);
				} else if (loaded instanceof FIBContainer) {
					referencedComponentView = embeddedFIBController.getViewFactory().makeContainer((FIBContainer) loaded);
					referencedComponentView.setEmbeddingComponent(this);
				}
			} else {
				if (!isComponentLoading) {
					logger.warning("ReferencedComponent = null and I'm NOT loading anything... : " + this.getComponentFile().getURI());
				}

			}

			isComponentLoading = false;

		}
		return referencedComponentView;
	}

	@Override
	public synchronized JComponent getJComponent() {
		if (getReferencedComponentView() != null) {
			JComponent returned = getReferencedComponentView().getJComponent();
			/*if (returned != null && getWidget().getOpaque() != null) {
					returned.setOpaque(getWidget().getOpaque());
				}*/
			return returned;
		}
		return NOT_FOUND_LABEL;
	}

	@Override
	public JComponent getDynamicJComponent() {
		return getJComponent();
	}

	private void performAssignments() {
		for (FIBReferenceAssignment assign : getWidget().getAssignments()) {
			DataBinding<?> variableDB = assign.getVariable();
			DataBinding<?> valueDB = assign.getValue();
			if (valueDB != null && valueDB.isValid()) {
				Object value = null;
				try {
					value = valueDB.getBindingValue(getBindingEvaluationContext());
					if (variableDB.isValid()) {
						// System.out.println("Assignment " + assign + " set value with " + value);
						variableDB.setBindingValue(value, embeddedFIBController);
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

	private boolean updateReferencedComponentView() {

		boolean returned = updateDynamicallyReferencedComponentWhenRequired();

		// We need here to "force" update while some assignments may be required

		// if (notEquals(getValue(), customComponent.getEditedObject())) {

		/*if (getWidget().getComponentClass().getName().endsWith("FIBForegroundStyleSelector")) {
				logger.info("GET updateWidgetFromModel() with " + getValue() + " for " + customComponent);
			}*/

		if (getReferencedComponentView() != null) {

			performAssignments();

			embeddedFIBController.setDataObject(getValue(), true);

			referencedComponentView.update();
		}

		return returned;
	}

	@Override
	public boolean updateWidgetFromModel() {

		return updateReferencedComponentView();

	}

	public void updateComponent() {
		referencedComponentView = null;
		logger.info("Updating component not implemented yet");
		getParentView().update();
		((FIBContainerView) getParentView()).updateLayout();
		getParentView().update();
	}

	public BindingEvaluationContext getEmbeddedBindingEvaluationContext() {
		// return getBindingEvaluationContext();
		// return this;
		return embeddedFIBController;
	}

}

/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.gina.model.bindings;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.gina.model.FIBViewType.DynamicProperty;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.toolbox.StringUtils;

/**
 * This is the {@link BindingModel} exposed by a {@link FIBComponent}<br>
 * This {@link BindingModel} is based on VirtualModel's (owner of this FlexoConcept) {@link BindingModel} if this owner is not null
 * 
 * Provides access to the {@link VirtualModelInstance}<br>
 * Allows reflexive access to the {@link VirtualModel} itself<br>
 * 
 * Note that default {@link RunTimeEvaluationContext} corresponding to this {@link BindingModel} is a {@link FlexoConceptInstance}
 * 
 * 
 * @author sylvain
 * 
 */
public class FIBComponentBindingModel extends BindingModel {

	private final FIBComponent component;

	private final Map<FIBVariable<?>, FIBVariableBindingVariable> variablesMap;
	private final Map<FIBComponent, FIBChildBindingVariable> childrenMap;
	private final Map<DynamicProperty, DynamicPropertyBindingVariable> dynamicPropertiesMap;

	private BindingVariable controllerBindingVariable = null;

	public static final String CONTROLLER_KEY = "controller";

	/**
	 * Build a new {@link BindingModel} dedicated to a FlexoConcept<br>
	 * Note that this constructor is called for final {@link FlexoConcept} (not for {@link VirtualModel} or any of future subclass of
	 * {@link FlexoConcept})
	 * 
	 * @param flexoConcept
	 */
	public FIBComponentBindingModel(final FIBComponent component) {
		this(component.getParent() != null ? component.getParent().getInferedBindingModel() : null, component);
	}

	/**
	 * Base constructor for any subclass of {@link FlexoConcept} (eg {@link VirtualModel})
	 * 
	 * @param baseBindingModel
	 * @param flexoConcept
	 */
	protected FIBComponentBindingModel(BindingModel baseBindingModel, FIBComponent component) {
		super(baseBindingModel);
		this.component = component;
		if (component != null && component.getPropertyChangeSupport() != null) {
			component.getPropertyChangeSupport().addPropertyChangeListener(this);
		}

		variablesMap = new HashMap<>();
		updateVariables();

		childrenMap = new HashMap<>();
		updateChildren();

		dynamicPropertiesMap = new HashMap<>();
		updateDynamicProperties();

		updateControllerVariable();

	}

	public BindingVariable getControllerBindingVariable() {
		return controllerBindingVariable;
	}

	public FIBComponent getComponent() {
		return component;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == component) {
			if (evt.getPropertyName().equals(FIBComponent.PARENT_KEY)) {
				// The FIBComponent changes it's parent component
				setBaseBindingModel(component.getParent() != null ? component.getParent().getInferedBindingModel() : null);
				updateControllerVariable();
				updateVariables();
			}
			else if (evt.getPropertyName().equals(FIBComponent.VARIABLES_KEY)) {
				// Variable were modified
				updateVariables();
			}
			else if (evt.getPropertyName().equals(FIBContainer.SUB_COMPONENTS_KEY)) {
				// Sub-components were modified
				updateChildren();
			}
			else if (evt.getPropertyName().equals(FIBComponent.CONTROLLER_CLASS_KEY)) {
				updateControllerVariable();
			}
		}
		else if (knownChildren.contains(evt.getSource())) {
			if (evt.getPropertyName().equals(FIBComponent.NAME_KEY) || evt.getPropertyName().equals(FIBWidget.MANAGE_DYNAMIC_MODEL_KEY)) {
				updateChildren();
			}
		}

	}

	private void updateControllerVariable() {
		if (component.isRootComponent()) {
			if (controllerBindingVariable == null) {
				controllerBindingVariable = new BindingVariable(CONTROLLER_KEY, component.getControllerClass());
				addToBindingVariables(controllerBindingVariable);
			}
		}
		else {
			if (controllerBindingVariable != null) {
				removeFromBindingVariables(controllerBindingVariable);
				controllerBindingVariable = null;
			}
		}
		if (controllerBindingVariable != null) {
			controllerBindingVariable.setType(component.getControllerClass());
		}

	}

	private void updateVariables() {

		List<FIBVariable<?>> variablesToBeDeleted = new ArrayList<>(variablesMap.keySet());

		for (FIBVariable<?> r : component.getVariables()) {
			if (variablesToBeDeleted.contains(r)) {
				variablesToBeDeleted.remove(r);
			}
			else if (variablesMap.get(r) == null) {
				FIBVariableBindingVariable bv = new FIBVariableBindingVariable(r);
				addToBindingVariables(bv);
				variablesMap.put(r, bv);
			}
		}

		for (FIBVariable<?> r : variablesToBeDeleted) {
			FIBVariableBindingVariable bvToRemove = variablesMap.get(r);
			removeFromBindingVariables(bvToRemove);
			variablesMap.remove(r);
			bvToRemove.delete();
		}

	}

	private void updateChildren() {

		List<FIBComponent> variablesToBeDeleted = new ArrayList<>(childrenMap.keySet());

		if (component instanceof FIBContainer) {
			for (FIBComponent r : ((FIBContainer) component).getAllSubComponents()) {
				if (StringUtils.isNotEmpty(r.getName()) && r.getDynamicAccessType() != null
						&& (!(r instanceof FIBWidget) || ((FIBWidget) r).getManageDynamicModel())) {
					FIBChildBindingVariable bv = childrenMap.get(r);
					if (variablesToBeDeleted.contains(r)) {
						variablesToBeDeleted.remove(r);
					}
					else if (bv == null) {
						bv = new FIBChildBindingVariable(r);
						addToBindingVariables(bv);
						childrenMap.put(r, bv);
					}
					bv.setVariableName(r.getName());
					bv.setType(r.getDynamicAccessType());
				}
			}
		}

		for (FIBComponent r : variablesToBeDeleted) {
			FIBChildBindingVariable bvToRemove = childrenMap.get(r);
			removeFromBindingVariables(bvToRemove);
			childrenMap.remove(r);
			bvToRemove.delete();
		}

		updateContentsListeners();
	}

	private void updateDynamicProperties() {

		List<DynamicProperty> variablesToBeDeleted = new ArrayList<>(dynamicPropertiesMap.keySet());

		if (component.getViewType() != null) {
			for (DynamicProperty r : component.getViewType().getDynamicProperties()) {
				if (variablesToBeDeleted.contains(r)) {
					variablesToBeDeleted.remove(r);
				}
				else if (dynamicPropertiesMap.get(r) == null) {
					DynamicPropertyBindingVariable bv = new DynamicPropertyBindingVariable(r);
					addToBindingVariables(bv);
					dynamicPropertiesMap.put(r, bv);
				}
			}
		}

		for (DynamicProperty r : variablesToBeDeleted) {
			DynamicPropertyBindingVariable bvToRemove = dynamicPropertiesMap.get(r);
			removeFromBindingVariables(bvToRemove);
			dynamicPropertiesMap.remove(r);
			bvToRemove.delete();
		}

	}

	private final List<FIBComponent> knownChildren = new ArrayList<>();

	private void updateContentsListeners() {

		List<FIBComponent> childrenNotToListenAnymore = new ArrayList<>();
		childrenNotToListenAnymore.addAll(knownChildren);

		if (component instanceof FIBContainer) {
			for (FIBComponent p : ((FIBContainer) component).getSubComponents()) {
				if (childrenNotToListenAnymore.contains(p)) {
					childrenNotToListenAnymore.remove(p);
				}
				else {
					if (!knownChildren.contains(p) && p.getPropertyChangeSupport() != null) {
						p.getPropertyChangeSupport().addPropertyChangeListener(this);
						knownChildren.add(p);
					}
				}
			}
		}

		for (FIBComponent p : childrenNotToListenAnymore) {
			if (p.getPropertyChangeSupport() != null) {
				p.getPropertyChangeSupport().removePropertyChangeListener(this);
				knownChildren.remove(p);
			}
		}
	}

	/**
	 * Delete this {@link BindingModel}
	 */
	@Override
	public void delete() {
		if (component != null && component.getPropertyChangeSupport() != null) {
			component.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}
}

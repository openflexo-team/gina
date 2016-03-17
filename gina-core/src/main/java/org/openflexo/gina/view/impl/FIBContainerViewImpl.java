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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;

/**
 * Default generic (and abstract) implementation for a "view" associated with a {@link FIBContainer} in a given rendering engine environment
 * (eg Swing)<br>
 * A {@link FIBContainerView} is a container for some sub-components (a set of {@link FIBView}) with a given layout
 * 
 * 
 * @author sylvain
 *
 * @param <M>
 *            type of {@link FIBComponent} this view represents
 * @param <C>
 *            type of technology-specific component
 * @param <C2>
 *            type of technology-specific component beeing contained by this view
 */
public abstract class FIBContainerViewImpl<M extends FIBContainer, C, C2> extends FIBViewImpl<M, C>implements FIBContainerView<M, C, C2> {

	private static final Logger LOGGER = Logger.getLogger(FIBContainerViewImpl.class.getPackage().getName());

	private final C technologyComponent;

	protected Map<FIBComponent, FIBViewImpl<?, C2>> subViewsMap;

	public FIBContainerViewImpl(M model, FIBController controller, ContainerRenderingAdapter<C, C2> RenderingAdapter) {
		super(model, controller, RenderingAdapter);

		subViewsMap = new Hashtable<FIBComponent, FIBViewImpl<?, C2>>();

		technologyComponent = makeTechnologyComponent();

		// update();
	}

	@Override
	public ContainerRenderingAdapter<C, C2> getRenderingAdapter() {
		return (ContainerRenderingAdapter<C, C2>) super.getRenderingAdapter();
	}

	/**
	 * Create technology-specific component representing FIBWidget
	 * 
	 * @return
	 */
	protected abstract C makeTechnologyComponent();

	@Override
	public final C getTechnologyComponent() {
		return technologyComponent;
	}

	@Override
	public void delete() {
		if (isDeleted()) {
			return;
		}
		if (subViewsMap != null) {
			for (FIBViewImpl<?, C2> v : new ArrayList<FIBViewImpl<?, C2>>(subViewsMap.values())) {
				v.delete();
			}
			subViewsMap.clear();
			subViewsMap = null;
		}
		super.delete();
	}

	/**
	 * Called when the component view explicitely change its visibility state from INVISIBLE to VISIBLE
	 */
	@Override
	protected void componentBecomesVisible() {
		// System.out.println("************ Component " + getComponent() + " becomes VISIBLE !!!!!!");
		super.componentBecomesVisible();
	}

	/**
	 * Called when the component view explicitely change its visibility state from VISIBLE to INVISIBLE
	 */
	@Override
	protected void componentBecomesInvisible() {
		System.out.println("************ Component " + getComponent() + " becomes INVISIBLE !!!!!!");
		super.componentBecomesInvisible();
	}

	protected final void buildSubComponents() {
		subViewsMap.clear();
		internallyBuildChildComponents();
		addSubComponentsAndDoLayout();
		// update();
	}

	private void internallyBuildChildComponents() {
		Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
		allSubComponents.addAll(getNotHiddenSubComponents());

		for (FIBComponent subComponent : allSubComponents) {
			FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
			if (subView == null) {
				subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent);
			}
			registerViewForComponent(subView, subComponent);
		}
	}

	protected abstract void addSubComponentsAndDoLayout();

	/**
	 * Return technology component for supplied FIBComponent<br>
	 * Search is deeply performed inside the whole component hierarchy
	 * 
	 * @param component
	 * @return
	 */
	@Override
	public Object getTechnologyComponentForFIBComponent(FIBComponent component) {
		if (getComponent() == component) {
			return getTechnologyComponent();
		}
		else {
			for (FIBViewImpl<?, ?> v : getSubViews()) {
				Object j = v.getTechnologyComponentForFIBComponent(component);
				if (j != null) {
					return j;
				}
			}
		}
		return null;
	}

	/*
	 * protected final void addChildComponent(C2 c2, Object constraint) {
	 * getRenderingAdapter().addComponent(c2, getTechnologyComponent(),
	 * constraint); }
	 */

	/*
	 * {// logger.info("addJComponent constraints=" + c); Object constraint =
	 * constraints.get(c); LOGGER.fine(getComponent() + ": addJComponent " + c +
	 * " constraint=" + constraint); if (constraint == null) {
	 * getJComponent().add(c); } else { getJComponent().add(c, constraint); } }
	 */

	// @Override
	// public abstract J getJComponent();

	// TODO: refactor this
	public Object getValue() {
		LOGGER.warning("Please reimplement this !");
		return null;
	}

	/*
	 * { if (getDataObject() == null) { return null; } if
	 * (getComponent().getData() == null || getComponent().getData().isUnset())
	 * { return null; } try { return (T)
	 * getComponent().getData().getBindingValue(getBindingEvaluationContext());
	 * } catch (TypeMismatchException e) { e.printStackTrace(); return null; }
	 * catch (NullReferenceException e) { e.printStackTrace(); return null; }
	 * catch (InvocationTargetException e) { e.printStackTrace(); return null; }
	 * }
	 */

	/*@Override
	public boolean update() {
		super.update();
		for (FIBView v : new ArrayList<FIBView>(subViewsMap.values())) {
			v.update();
		}
		return true;
	}*/

	@Override
	protected void performUpdate() {
		super.performUpdate();
		if (subViewsMap != null) {
			for (FIBView v : new ArrayList<FIBView>(subViewsMap.values())) {
				if (!v.isDeleted() && v.isViewVisible()) {
					v.update();
				}
			}
		}
	}

	/*
	 * @Override public void updateDataObject(final Object dataObject) {
	 * update(new Vector<FIBComponent>()); if (isComponentVisible()) { for
	 * (FIBViewImpl v : new ArrayList<FIBViewImpl>(subViews.values())) {
	 * v.updateDataObject(dataObject); } } updateDataDynamicValue(); }
	 */

	/*
	 * private void updateDataDynamicValue() { setData(getValue()); }
	 */

	/*
	 * private void updateDataDynamicValue() { if (getDynamicModel() != null &&
	 * getComponent().getData().isSet() && getComponent().getData().isValid()) {
	 * logger.fine("Container: " + getComponent() + " value data for " +
	 * getDynamicModel() + " is " + getValue()); Object newDataValue =
	 * getValue(); if (getDynamicModel().getData() != getValue()) {
	 * getDynamicModel().setData(getValue()); notifyDynamicModelChanged(); } } }
	 */

	@Override
	protected boolean checkValidDataPath() {
		if (getParentView() instanceof FIBViewImpl && !((FIBViewImpl) getParentView()).checkValidDataPath()) {
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

	@Override
	public void updateLanguage() {
		for (FIBView v : subViewsMap.values()) {
			// if
			// (!"True".equals(v.getComponent().getParameter(FIBContainer.INHERITED)))
			v.updateLanguage();
		}
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
	 * @Override public boolean update(List<FIBComponent> callers) { boolean
	 * returned = super.update(callers); updateDataDynamicValue();
	 * 
	 * return returned; }
	 */

	protected void registerViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component) {
		subViewsMap.put(component, view);
	}

	public Map<FIBComponent, FIBViewImpl<?, C2>> getSubViewsMap() {
		return subViewsMap;
	}

	@Override
	public Collection<FIBViewImpl<?, C2>> getSubViews() {
		return subViewsMap.values();
	}

	/*@Override
	protected void hiddenComponentBecomesVisible() {
		super.hiddenComponentBecomesVisible();
		for (FIBViewImpl<?, C2> view : subViewsMap.values()) {
			view.updateVisibility();
		}
	}*/

	/**
	 * Return all sub-components that are not declared as to be hidden in inheritance hierarchy
	 * 
	 * @return
	 */
	public List<FIBComponent> getNotHiddenSubComponents() {
		List<FIBComponent> returned = new ArrayList<FIBComponent>();
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (subComponent.getParameter("hidden") == null || subComponent.getParameter("hidden").equalsIgnoreCase("false")) {
				returned.add(subComponent);
			} /*
				* else { System.out.println("Ignoring " + subComponent); }
				*/
		}
		return returned;
	}

	@Override
	public synchronized void updateFont() {
		if (getFont() != null) {
			getRenderingAdapter().setFont(getTechnologyComponent(), getFont());
		}
		if (subViewsMap != null) {
			for (FIBView<?, C2> v : subViewsMap.values()) {
				v.updateFont();
			}
		}
		getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
	}

	@Override
	public abstract void updateLayout();

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBContainer.SUB_COMPONENTS_KEY)) {
			updateLayout();
		}

		super.propertyChange(evt);
	}
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.binding.SettableBindingEvaluationContext;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBVariable;
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
public abstract class FIBContainerViewImpl<M extends FIBContainer, C, C2> extends FIBViewImpl<M, C>
		implements FIBContainerView<M, C, C2>, SettableBindingEvaluationContext {

	private static final Logger LOGGER = Logger.getLogger(FIBContainerViewImpl.class.getPackage().getName());

	private C technologyComponent;

	protected Map<FIBComponent, FIBViewImpl<?, C2>> subViewsMap;

	public FIBContainerViewImpl(M model, FIBController controller, ContainerRenderingAdapter<C, C2> RenderingAdapter) {
		super(model, controller, RenderingAdapter);

		subViewsMap = new LinkedHashMap<>();

		technologyComponent = makeTechnologyComponent();

		// update();
	}

	protected void rebuildTechnologyComponent() {
		technologyComponent = makeTechnologyComponent();
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
			for (FIBViewImpl<?, C2> v : new ArrayList<>(subViewsMap.values())) {
				v.delete();
			}
			subViewsMap.clear();
			subViewsMap = null;
		}
		super.delete();
	}

	/**
	 * Called when the component view explicitly change its visibility state from INVISIBLE to VISIBLE
	 */
	@Override
	protected void componentBecomesVisible() {

		// When a container becomes visible, we have to update all contained components, because they
		// were deactivated

		// System.out.println("************ BEGIN Component " + getComponent() + " becomes VISIBLE !!!!!!");
		super.componentBecomesVisible();

		// Then iterate on all children, and update them
		if (subViewsMap != null) {
			for (FIBView<?, C2> v : new ArrayList<>(subViewsMap.values())) {
				if (!v.isDeleted()) {
					// System.out.println("Updating " + v.getComponent());
					v.update();
				}
			}
		}
		// System.out.println("************ END Component " + getComponent() + " becomes VISIBLE !!!!!!");
	}

	/**
	 * Called when the component view explicitly change its visibility state from VISIBLE to INVISIBLE
	 */
	@Override
	protected void componentBecomesInvisible() {
		// System.out.println("************ Component " + getComponent() + " becomes INVISIBLE !!!!!!");
		super.componentBecomesInvisible();
		// Then iterate on all children, and update them
		if (subViewsMap != null) {
			for (FIBView<?, C2> v : new ArrayList<>(subViewsMap.values())) {
				if (!v.isDeleted()) {
					// System.out.println("Updating " + v.getComponent());
					((FIBViewImpl<?, C2>) v).componentBecomesInvisible();
				}
			}
		}
	}

	public void buildSubComponents() {
		subViewsMap.clear();
		internallyBuildChildComponents();
		addSubComponentsAndDoLayout();
		performUpdateSubViews();
	}

	protected FIBView<?, C2> viewForComponent(FIBComponent subComponent) {
		return subViewsMap.get(subComponent);
	}

	private void internallyBuildChildComponents() {

		Vector<FIBComponent> allSubComponents = new Vector<>();
		allSubComponents.addAll(getNotHiddenSubComponents());

		for (FIBComponent subComponent : allSubComponents) {

			// FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
			FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) viewForComponent(subComponent);
			if (subView == null) {
				subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent, false);
			}
			registerViewForComponent(subView, subComponent);
		}
	}

	public abstract void addSubComponentsAndDoLayout();

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

	@Override
	protected void performUpdate() {
		super.performUpdate();
		performUpdateSubViews();
		if (layoutIsInvalid) {
			updateLayout();
			layoutIsInvalid = false;
		}
	}

	protected void performUpdateSubViews() {
		if (subViewsMap != null) {
			for (FIBView<?, C2> v : new ArrayList<>(subViewsMap.values())) {
				if (!v.isDeleted() /*&& v.isViewVisible()*/) {
					v.update();
				}
			}
		}
	}

	@Override
	public void updateLanguage() {
		for (FIBView<?, C2> v : subViewsMap.values()) {
			v.updateLanguage();
		}
	}

	public void registerViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component) {
		subViewsMap.put(component, view);
	}

	public void unregisterViewForComponent(FIBViewImpl<?, C2> view, FIBComponent component) {
		getController().unregisterView(view);
		subViewsMap.remove(component);
	}

	public Map<FIBComponent, FIBViewImpl<?, C2>> getSubViewsMap() {
		return subViewsMap;
	}

	@Override
	public Collection<FIBViewImpl<?, C2>> getSubViews() {
		return subViewsMap.values();
	}

	/**
	 * Recursive call to know if a view is contained inside this container
	 * 
	 * @param view
	 * @return
	 */
	@Override
	public boolean containsView(FIBView<?, C2> view) {
		for (FIBViewImpl<?, C2> fibViewImpl : subViewsMap.values()) {
			if (fibViewImpl == view) {
				return true;
			}
			else if (fibViewImpl instanceof FIBContainerView) {
				if (((FIBContainerView) fibViewImpl).containsView(view)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return all sub-components that are not declared as to be hidden in inheritance hierarchy
	 * 
	 * @return
	 */
	public List<FIBComponent> getNotHiddenSubComponents() {
		List<FIBComponent> returned = new ArrayList<>();
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (!subComponent.isHidden()) {
				// if (subComponent.getParameter("hidden") == null || subComponent.getParameter("hidden").equalsIgnoreCase("false")) {
				returned.add(subComponent);
			}
			else {
				System.out.println("Ignoring " + subComponent);
			}

		}
		return returned;
	}

	@Override
	public synchronized void updateFont() {
		if (getFont() != null && getRenderingAdapter() != null) {
			getRenderingAdapter().setFont(getTechnologyComponent(), getFont());
		}
		if (subViewsMap != null) {
			for (FIBView<?, C2> v : subViewsMap.values()) {
				v.updateFont();
			}
		}
		if (getRenderingAdapter() != null) {
			getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
		}
	}

	@Override
	public abstract void updateLayout();

	private boolean layoutIsInvalid = false;

	@Override
	public void invalidateAndUpdateLayoutLater() {
		// System.out.println("Called invalidate and update layout later for " + getComponent());
		layoutIsInvalid = true;
		// TODO: avoid when possible
		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (layoutIsInvalid) {
					updateLayout();
					layoutIsInvalid = false;
				}
			}
		});*/
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(FIBContainer.SUB_COMPONENTS_KEY)) {
			/*System.out.println("Hop a a ajoute " + evt.getNewValue());
			if (evt.getNewValue() instanceof FIBComponent) {
				FIBComponent newComponent = (FIBComponent) evt.getNewValue();
				if (newComponent.getFIBLibrary() != null) {
					System.out.println(newComponent.getFIBLibrary().stringRepresentation(newComponent));
				}
			}*/
			updateLayout();
		}

		super.propertyChange(evt);
	}

	@Override
	protected <T> void fireVariableChanged(FIBVariable<T> variable, T oldValue, T newValue) {
		super.fireVariableChanged(variable, oldValue, newValue);
		ArrayList<FIBView<?, ?>> subviews = new ArrayList<>(getSubViews());
		for (FIBView<?, ?> child : subviews) {
			((FIBViewImpl<?, ?>) child).fireVariableChanged(variable, oldValue, newValue);
		}
	}

}

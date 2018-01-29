/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

package org.openflexo.gina.view.container.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.container.layout.ComponentConstraints;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.FIBIterationView.IteratedContents;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;

/**
 * Represents a layout manager working in a {@link FIBContainer}
 *
 * @param <C>
 *            type of technology-specific container this layout manager is managing
 * @param <C2>
 *            type of technology-specific contents this layout manager is layouting
 *
 * @author sylvain
 */
public abstract class FIBLayoutManagerImpl<C, C2, CC extends ComponentConstraints> implements FIBLayoutManager<C, C2, CC> {

	private static final Logger logger = Logger.getLogger(FIBLayoutManagerImpl.class.getPackage().getName());

	private FIBContainerViewImpl<?, C, C2> containerView;
	private Map<FIBView<?, C2>, CC> constraints;

	public FIBLayoutManagerImpl(FIBContainerViewImpl<?, C, C2> containerView) {
		this.containerView = containerView;
		constraints = new HashMap<>();
	}

	public final FIBContainerViewImpl<?, C, C2> getContainerView() {
		return containerView;
	}

	public FIBContainer getComponent() {
		return containerView.getComponent();
	}

	public FIBController getController() {
		return getContainerView().getController();
	}

	@Override
	public void delete() {
		constraints.clear();
		constraints = null;
		containerView = null;
	}

	/*
	 * @Override public void retrieveContainedJComponentsAndConstraints() {
	 * Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
	 * allSubComponents.addAll(getContainerView().getNotHiddenSubComponents());
	 * 
	 * for (FIBComponent subComponent : allSubComponents) { FIBViewImpl<?, C2>
	 * subView = (FIBViewImpl<?, C2>)
	 * getController().viewForComponent(subComponent); if (subView == null) {
	 * subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent); }
	 * // FIBViewImpl subView = getController().buildView(c);
	 * getContainerView().registerViewForComponent(subView, subComponent);
	 * 
	 * // registerComponentWithConstraints(subView.getTechnologyComponent(), //
	 * subComponent.getConstraints()); } }
	 */

	@Override
	public void doLayout() {

		// TODO: we should check that type of constraints matches new layout if
		// this results from a layout type change

		// We perform here a first pass to set constraints for each sub
		// component view

		for (FIBView<?, C2> subComponentView : new ArrayList<>(getContainerView().getSubViews())) {
			registerComponentWithConstraints(subComponentView, (CC) subComponentView.getComponent().getConstraints());
		}

		// Then we add the components

		// Special case for iteration
		if (containerView instanceof FIBIterationViewImpl) {
			for (IteratedContents iteratedContents : ((FIBIterationViewImpl<?, ?>) containerView).getIteratedSubViewsMap().values()) {
				for (FIBComponent c : getContainerView().getComponent().getSubComponents()) {
					FIBView<?, C2> subComponentView = (FIBView<?, C2>) iteratedContents.getSubViewsMap().get(c);
					if (subComponentView != null) {
						performAddChild(subComponentView, (CC) c.getConstraints());
						subComponentView.getRenderingAdapter().setVisible(subComponentView.getTechnologyComponent(),
								subComponentView.isViewVisible());
					}
				}
			}

		}
		else {
			// Normal case
			for (FIBComponent c : getContainerView().getComponent().getSubComponents()) {
				FIBView<?, C2> subComponentView = getSubComponentView(c);
				if (subComponentView != null) {
					performAddChild(subComponentView, (CC) c.getConstraints());
					subComponentView.getRenderingAdapter().setVisible(subComponentView.getTechnologyComponent(),
							subComponentView.isViewVisible());
				}
			}
		}

		// for (FIBView<?, C2> subComponentView : new ArrayList<>(getContainerView().getSubViews())) {
		for (FIBComponent c : getContainerView().getComponent().getSubComponents()) {
			FIBView<?, C2> subComponentView = getSubComponentView(c);
			if (subComponentView != null) {
				performAddChild(subComponentView, (CC) /*subComponentView.getComponent()*/c.getConstraints());
				subComponentView.getRenderingAdapter().setVisible(subComponentView.getTechnologyComponent(),
						subComponentView.isViewVisible());
			}
		}

		getContainerView().getRenderingAdapter().revalidateAndRepaint(getContainerView().getTechnologyComponent());
	}

	protected abstract void performAddChild(FIBView<?, C2> childView, CC constraints);

	/*
	 * public void registerComponentWithConstraints(C2 component, Object
	 * constraint) { registerComponentWithConstraints(component, constraint,
	 * -1); }
	 */

	protected void registerComponentWithConstraints(FIBView<?, C2> subComponentView, CC constraint/*
																									* ,
																									* int
																									* index
																									*/) {
		logger.fine("Register component: " + subComponentView.getComponent() + " constraint=" + constraint);
		/*
		 * if (index < 0 || index > subComponents.size()) { index =
		 * subComponents.size(); } subComponents.add(index, component);
		 */
		if (constraint != null) {
			constraints.put(subComponentView, constraint);
		}
	}

	/*
	 * public void registerComponentWithConstraints(C2 component, int index) {
	 * registerComponentWithConstraints(component, null, index); }
	 * 
	 * public void registerComponentWithConstraints(C2 component) {
	 * registerComponentWithConstraints(component, null, -1); }
	 */

	@Override
	public Map<FIBView<?, C2>, CC> getConstraints() {
		return constraints;
	}

	/*
	 * protected void registerComponentWithConstraints(JComponent component, int
	 * index) { logger.fine("Register component: "+component+" index="+index);
	 * subComponents.insertElementAt(component,index); }
	 * 
	 * protected void registerComponentWithConstraints(JComponent component,
	 * Object constraint, int index) {
	 * logger.fine("Register component: "+component+" index="+index);
	 * subComponents.insertElementAt(component,index); if (constraint != null)
	 * constraints.put(component,constraint); }
	 */

	@Override
	public FIBView<?, C2> getSubComponentView(FIBComponent component) {

		return getContainerView().getSubViewsMap().get(component);
	}

	// protected abstract C2 makeEmptyPanel();

}

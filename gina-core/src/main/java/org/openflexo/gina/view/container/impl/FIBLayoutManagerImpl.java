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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.container.layout.ComponentConstraints;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.view.FIBOperatorView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.gina.view.impl.FIBViewImpl;
import org.openflexo.gina.view.operator.FIBIterationView;

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

	public void setOperatorContentsStart(FIBView<?, ?> view) {
	}

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

		System.out.println("On se refait le layout");

		for (FIBComponent c : getContainerView().getComponent().getSubComponents()) {
			FIBView<?, C2> subComponentView = getSubComponentView(c);
			if (subComponentView != null) {
				if (subComponentView instanceof FIBIterationView) {
					System.out.println("Represent iteration " + subComponentView.getComponent().getName());
					if (((FIBIterationView<?, ?>) subComponentView).handleIteration()) {
						// We execute the iteration, represent results
						for (FIBView<?, ?> fibView : ((FIBIterationView<?, ?>) subComponentView).getSubViews()) {
							performAddChild((FIBView<?, C2>) fibView, (CC) fibView.getComponent().getConstraints());
						}
					}
					else {
						System.out
								.println("subComponents=" + ((FIBIterationView<?, ?>) subComponentView).getComponent().getSubComponents());
						// We represent the iteration in Edit mode
						if (((FIBIterationView<?, ?>) subComponentView).getComponent().getSubComponents().size() == 0) {
							// Iteration is empty, represent it
							performAddChild(subComponentView, (CC) c.getConstraints());
						}
						else {
							// Iteration is not empty, represent contents of iteration
							System.out.println("subViews=" + ((FIBIterationView<?, ?>) subComponentView).getSubViews());
							subComponentView.update();
							System.out.println("subViews=" + ((FIBIterationView<?, ?>) subComponentView).getSubViews());
							boolean isFirst = true;
							for (FIBView<?, ?> fibView : ((FIBIterationView<?, ?>) subComponentView).getSubViews()) {
								// System.out.println("Represent " + fibView + " with " + fibView.getComponent().getConstraints());
								performAddChild((FIBView<?, C2>) fibView, (CC) fibView.getComponent().getConstraints());
								if (isFirst) {
									setOperatorContentsStart(fibView);
								}
								isFirst = false;
							}
						}
					}
				}
				else {
					performAddChild(subComponentView, (CC) c.getConstraints());
				}
				if (subComponentView.getRenderingAdapter() != null) {
					subComponentView.getRenderingAdapter().setVisible(subComponentView.getTechnologyComponent(),
							subComponentView.isViewVisible());
				}
			}
		}

		getContainerView().getRenderingAdapter().revalidateAndRepaint(getContainerView().getTechnologyComponent());
	}

	protected abstract void performAddChild(FIBView<?, C2> childView, CC constraints);

	protected void registerComponentWithConstraints(FIBView<?, C2> subComponentView, CC constraint) {
		logger.fine("Register component: " + subComponentView.getComponent() + " constraint=" + constraint);
		if (constraint != null) {
			constraints.put(subComponentView, constraint);
		}
	}

	@Override
	public Map<FIBView<?, C2>, CC> getConstraints() {
		return constraints;
	}

	@Override
	public FIBView<?, C2> getSubComponentView(FIBComponent component) {

		return getContainerView().getSubViewsMap().get(component);
	}

	public List<FIBView<?, C2>> getFlattenedContents() {

		List<FIBView<?, C2>> list = new ArrayList<>();
		for (FIBViewImpl<?, C2> subView : getContainerView().getSubViews()) {
			if (subView instanceof FIBOperatorView) {
				for (FIBView<?, C2> childView : ((FIBOperatorView<?, ?, C2>) subView).getSubViews()) {
					list.add(childView);
				}
			}
			else {
				list.add(subView);
			}
		}
		return list;

	}

}

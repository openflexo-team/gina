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

package org.openflexo.fib.view.container.impl;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.container.FIBPanel;
import org.openflexo.fib.model.container.GridLayoutConstraints;
import org.openflexo.fib.model.container.FIBPanel.Layout;
import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.impl.FIBContainerViewImpl;
import org.openflexo.fib.view.impl.FIBViewImpl;

/**
 * Base implementation for a basic panel, as a container of some children component, with a given layout, and a border
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view contains
 * 
 * @author sylvain
 */
public abstract class FIBPanelViewImpl<C, C2> extends FIBContainerViewImpl<FIBPanel, C, C2>implements FIBPanelView<C, C2> {

	private static final Logger logger = Logger.getLogger(FIBPanelViewImpl.class.getPackage().getName());

	public FIBPanelViewImpl(FIBPanel model, FIBController controller, PanelRenderingAdapter<C, C2> renderingAdapter) {
		super(model, controller, renderingAdapter);

		updateBorder();
	}

	@Override
	public PanelRenderingAdapter<C, C2> getRenderingAdapter() {
		return (PanelRenderingAdapter<C, C2>) super.getRenderingAdapter();
	}

	public abstract void updateBorder();

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateBorder();
	}

	/**
	 * Called to configure technology-specific component with relevant layout
	 */
	protected abstract void setPanelLayoutParameters(C technologyComponent);

	@Override
	public synchronized void updateLayout() {
		logger.info("relayout panel " + getComponent());

		// TODO: please reimplement this and make it more efficient !!!!

		/*if (getSubViews() != null) {
			for (FIBViewImpl v : getSubViews().values()) {
				if (v.getComponent().isDeleted()) {
					v.delete();
				}
			}
		}*/
		getJComponent().removeAll();

		setPanelLayoutParameters(getTechnologyComponent());
		buildSubComponents();
		// updateDataObject(getDataObject());
		update();
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBComponent> allSubComponents = new Vector<FIBComponent>();
		allSubComponents.addAll(getNotHiddenSubComponents());
		// Vector<FIBComponent> allSubComponents = getComponent().getSubComponents();

		// if (fibComponent.getParameter("hidden") == null
		// || fibComponent.getParameter("hidden").equalsIgnoreCase("false")) {

		if (getComponent().getLayout() == Layout.flow || getComponent().getLayout() == Layout.box
				|| getComponent().getLayout() == Layout.buttons || getComponent().getLayout() == Layout.twocols
				|| getComponent().getLayout() == Layout.gridbag) {

			/*System.out.println("Apres le retrieve: ");
			for (FIBComponent c : allSubComponents) {
				if (c.getConstraints() != null) {
					if (!c.getConstraints().hasIndex()) {
						System.out.println("> Index: ? "+c);
					}
					else {
						System.out.println("> Index: "+c.getConstraints().getIndex()+" "+c);
					}
				}
			}
			
			System.out.println("*********************************************");*/

		}

		if (getComponent().getLayout() == Layout.grid) {

			for (FIBComponent subComponent : getNotHiddenSubComponents()) {
				FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
				if (subView == null) {
					subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent);
				}
				// FIBViewImpl subView = getController().buildView(c);
				registerViewForComponent(subView, subComponent);
			}

			for (int i = 0; i < getComponent().getRows(); i++) {
				for (int j = 0; j < getComponent().getCols(); j++) {
					registerComponentWithConstraints(getChildComponent(j, i), null);
				}
			}
		}

		else {
			for (FIBComponent subComponent : allSubComponents) {
				FIBViewImpl<?, C2> subView = (FIBViewImpl<?, C2>) getController().viewForComponent(subComponent);
				if (subView == null) {
					subView = (FIBViewImpl<?, C2>) getController().buildView(subComponent);
				}
				// FIBViewImpl subView = getController().buildView(c);
				registerViewForComponent(subView, subComponent);

				// TODO: please handle issue with getResultingJComponent()
				registerComponentWithConstraints((C2) subView.getResultingJComponent(), subComponent.getConstraints());
			}
		}
	}

	@Override
	public JPanel getJComponent() {
		return (JPanel) getTechnologyComponent();
	}

	// Special case for GridLayout
	protected C2 getChildComponent(int col, int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return (C2) getController().viewForComponent(subComponent).getResultingJComponent();
			}
		}
		// Otherwise, it's an empty cell
		return makeEmptyPanel();

	}

	protected abstract C2 makeEmptyPanel();

	@Override
	public void delete() {
		super.delete();
	}

}

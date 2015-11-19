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

package org.openflexo.gina.swing.view.container.layout;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.GridLayoutConstraints;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.JFIBPanelView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBLayoutManagerImpl;

/**
 * Swing implementation for grid layout
 * 
 * @author sylvain
 */
public class JGridLayout extends FIBLayoutManagerImpl<JPanel, JComponent, GridLayoutConstraints> {

	public JGridLayout(JFIBPanelView panelView) {
		super(panelView);
	}

	@Override
	public FIBPanel getComponent() {
		return (FIBPanel) super.getComponent();
	}

	@Override
	public void setLayoutManager(JPanel container) {
		container.setLayout(new GridLayout(getComponent().getRows(), getComponent().getCols(),
				getComponent().getHGap(), getComponent().getVGap()));
	}

	@Override
	public void doLayout() {
		for (FIBView<?, JComponent> subComponentView : getContainerView().getSubViews()) {
			registerComponentWithConstraints(subComponentView, (GridLayoutConstraints) subComponentView.getComponent()
					.getConstraints());
		}

		for (int i = 0; i < getComponent().getRows(); i++) {
			for (int j = 0; j < getComponent().getCols(); j++) {
				getContainerView().getTechnologyComponent().add(getChildComponent(j, i));
			}
		}
	}

	@Override
	protected void performAddChild(FIBView<?, JComponent> childView, GridLayoutConstraints constraints) {
		// Not applicable, doLayout() has been overriden and this method is not
		// used with overriden scheme
	}

	protected JComponent getChildComponent(int col, int row) {
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints) subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) {
				return getSubComponentView(subComponent).getResultingJComponent();
			}
		}
		// Otherwise, it's an empty cell
		return makeEmptyPanel();

	}

	@Override
	public JFIBView<?, JComponent> getSubComponentView(FIBComponent component) {
		return (JFIBView<?, JComponent>) super.getSubComponentView(component);
	}

	// @Override
	protected JComponent makeEmptyPanel() {
		JPanel returned = new JPanel();
		returned.setOpaque(false);
		return returned;
	}

}

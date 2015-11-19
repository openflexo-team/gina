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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.container.BorderLayoutConstraints;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.JFIBPanelView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBLayoutManagerImpl;

/**
 * Swing implementation for border layout
 * 
 * @author sylvain
 */
public class JBorderLayout extends FIBLayoutManagerImpl<JPanel, JComponent, BorderLayoutConstraints> {

	public JBorderLayout(JFIBPanelView panelView) {
		super(panelView);
	}

	@Override
	public void setLayoutManager(JPanel container) {
		container.setLayout(new BorderLayout());
	}

	@Override
	public void doLayout() {
		// TODO Auto-generated method stub
		super.doLayout();
		getContainerView().getTechnologyComponent().revalidate();
		getContainerView().getTechnologyComponent().repaint();
	}

	@Override
	protected void performAddChild(FIBView<?, JComponent> childView, BorderLayoutConstraints constraints) {

		System.out.println("**************** On ajoute au composant: " + getContainerView().getTechnologyComponent());
		System.out.println("Le composant: " + ((JFIBView<?, ?>) childView).getResultingJComponent());
		System.out.println("contraintes: " + constraints.getLocation().getConstraint());

		getContainerView().getTechnologyComponent().add(((JFIBView<?, ?>) childView).getResultingJComponent(),
				constraints.getLocation().getConstraint());
	}
}

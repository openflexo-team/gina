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

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.layout.FlowLayoutConstraints;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.container.JFIBPanelView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBLayoutManagerImpl;

/**
 * Swing implementation for flow layout
 * 
 * @author sylvain
 */
public class JFlowLayout extends FIBLayoutManagerImpl<JPanel, JComponent, FlowLayoutConstraints> {

	public JFlowLayout(JFIBPanelView panelView) {
		super(panelView);
	}

	@Override
	public FIBPanel getComponent() {
		return (FIBPanel) super.getComponent();
	}

	@Override
	public void setLayoutManager(JPanel container) {

		container.setLayout(makeFlowLayout());

	}

	protected FlowLayout makeFlowLayout() {
		if (getComponent() != null && getComponent().getFlowAlignment() != null) {
			return new FlowLayout(getComponent().getFlowAlignment().getAlign(), getComponent().getHGap(), getComponent().getVGap());
		}
		return null;
	}

	@Override
	protected void performAddChild(FIBView<?, JComponent> childView, FlowLayoutConstraints constraints) {
		getContainerView().getTechnologyComponent().add(((JFIBView<?, ?>) childView).getResultingJComponent());
	}
}

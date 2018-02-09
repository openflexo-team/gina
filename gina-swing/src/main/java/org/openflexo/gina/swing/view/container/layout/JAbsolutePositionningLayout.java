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

import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.container.layout.NoneLayoutConstraints;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBLayoutManagerImpl;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;

/**
 * Swing implementation for absolute positionning layout
 * 
 * @author sylvain
 */
public class JAbsolutePositionningLayout extends FIBLayoutManagerImpl<JPanel, JComponent, NoneLayoutConstraints> {

	public JAbsolutePositionningLayout(FIBContainerViewImpl<?, JPanel, JComponent> panelView) {
		super(panelView);
	}

	@Override
	public void setLayoutManager(JPanel container) {
		container.setLayout(null);
	}

	@Override
	public List<JComponent> getExistingComponents() {
		return (List) Arrays.asList(getContainerView().getTechnologyComponent().getComponents());
	}

	@Override
	protected void performAddChild(FIBView<?, JComponent> childView, NoneLayoutConstraints constraints) {
		JComponent addedJComponent = ((JFIBView<?, ?>) childView).getResultingJComponent();
		addedJComponent.setLocation(constraints.getX(), constraints.getY());
		addedJComponent.setSize(addedJComponent.getPreferredSize());
		getContainerView().getTechnologyComponent().add(addedJComponent);
	}

	@Override
	protected void performRemoveChild(JComponent componentToRemove) {
		getContainerView().getTechnologyComponent().remove(componentToRemove);
	}

	@Override
	public NoneLayoutConstraints makeDefaultConstraints() {
		return new NoneLayoutConstraints();
	}
}

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

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.layout.GridBagLayoutConstraints;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBLayoutManagerImpl;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;

/**
 * Swing implementation for grid bag layout
 * 
 * @author sylvain
 */
public class JGridBagLayout extends FIBLayoutManagerImpl<JPanel, JComponent, GridBagLayoutConstraints> {

	public JGridBagLayout(FIBContainerViewImpl<?, JPanel, JComponent> panelView) {
		super(panelView);
	}

	@Override
	public FIBPanel getComponent() {
		return (FIBPanel) super.getComponent();
	}

	@Override
	public void setLayoutManager(JPanel container) {
		container.setLayout(makeGridBagLayout());
	}

	@Override
	public List<JComponent> getExistingComponents() {
		return (List) Arrays.asList(getContainerView().getTechnologyComponent().getComponents());
	}

	protected GridBagLayout makeGridBagLayout() {
		return new GridBagLayout();
	}

	@Override
	protected void performAddChild(FIBView<?, JComponent> childView, GridBagLayoutConstraints gridBagConstraints) {

		JComponent addedJComponent = ((JFIBView<?, ?>) childView).getResultingJComponent();
		_addChildToContainerWithConstraints(addedJComponent, getContainerView().getTechnologyComponent(), gridBagConstraints);

	}

	@Override
	protected void performRemoveChild(JComponent componentToRemove) {
		getContainerView().getTechnologyComponent().remove(componentToRemove);
	}

	protected void _addChildToContainerWithConstraints(Component child, Container container, GridBagLayoutConstraints gridBagConstraints) {

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridBagConstraints.getGridX();
		c.gridy = gridBagConstraints.getGridY();
		c.gridwidth = gridBagConstraints.getGridWidth();
		c.gridheight = gridBagConstraints.getGridHeight();
		c.weightx = gridBagConstraints.getWeightX();
		c.weighty = gridBagConstraints.getWeightY();
		c.anchor = gridBagConstraints.getAnchor().getAnchor();
		c.fill = gridBagConstraints.getFill().getFill();
		c.insets = new Insets(gridBagConstraints.getInsetsTop(), gridBagConstraints.getInsetsLeft(), gridBagConstraints.getInsetsBottom(),
				gridBagConstraints.getInsetsRight());
		c.ipadx = gridBagConstraints.getPadX();
		c.ipady = gridBagConstraints.getPadY();

		container.add(child, c);

	}

	@Override
	public void doLayout() {
		getContainerView().getTechnologyComponent().removeAll();
		super.doLayout();
		getContainerView().getRenderingAdapter().revalidateAndRepaint(getContainerView().getTechnologyComponent());
	}

	@Override
	public GridBagLayoutConstraints makeDefaultConstraints() {
		return new GridBagLayoutConstraints();
	}
}

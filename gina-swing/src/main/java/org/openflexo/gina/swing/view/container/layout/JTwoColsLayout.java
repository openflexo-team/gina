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

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBLayoutManagerImpl;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;

/**
 * Swing implementation for 2-cols layout
 * 
 * @author sylvain
 */
public class JTwoColsLayout extends FIBLayoutManagerImpl<JPanel, JComponent, TwoColsLayoutConstraints> {

	public JTwoColsLayout(FIBContainerViewImpl<?, JPanel, JComponent> panelView) {
		super(panelView);
	}

	@Override
	public FIBPanel getComponent() {
		return (FIBPanel) super.getComponent();
	}

	@Override
	public void setLayoutManager(JPanel container) {
		container.setLayout(makeTwoColsLayout());
	}

	protected GridBagLayout makeTwoColsLayout() {
		return new GridBagLayout();
	}

	protected void _addChildToContainerWithConstraints(Component child, Container container, TwoColsLayoutConstraints twoColsConstraints) {
		GridBagConstraints c = new GridBagConstraints();
		// c.insets = new Insets(3, 3, 3, 3);
		// System.out.println("twoColsConstraints=" + twoColsConstraints);
		c.insets = new Insets(twoColsConstraints.getInsetsTop(), twoColsConstraints.getInsetsLeft(), twoColsConstraints.getInsetsBottom(),
				twoColsConstraints.getInsetsRight());
		if (twoColsConstraints.getLocation() == TwoColsLayoutLocation.left) {
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0; // 1.0;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.NORTHEAST;
			if (twoColsConstraints.getExpandVertically()) {
				// c.weighty = 1.0;
				c.fill = GridBagConstraints.VERTICAL;
			}
			else {
				// c.insets = new Insets(5, 2, 0, 2);
			}
		}
		else {
			if (twoColsConstraints.getExpandHorizontally()) {
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.CENTER;
				if (twoColsConstraints.getExpandVertically()) {
					c.weighty = 1.0;
				}
			}
			else {
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.WEST;
			}
			c.weightx = 1.0; // 2.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
		}

		container.add(child, c);

	}

	private FIBComponent lastAddedChild = null;

	@Override
	protected void performAddChild(FIBView<?, JComponent> childView, TwoColsLayoutConstraints twoColsConstraints) {

		if (lastAddedChild != null && lastAddedChild.getConstraints() instanceof TwoColsLayoutConstraints
				&& ((TwoColsLayoutConstraints) lastAddedChild.getConstraints()).getLocation() == TwoColsLayoutLocation.left
				&& (twoColsConstraints.getLocation() == TwoColsLayoutLocation.left
						|| twoColsConstraints.getLocation() == TwoColsLayoutLocation.center)) {
			// A second LEFT or CENTER component after a previous LEFT one, add glue
			// We have to add an extra glue at the end of last component if this one was declared as in LEFT position
			Component glue = Box.createHorizontalGlue();
			_addChildToContainerWithConstraints(glue, getContainerView().getTechnologyComponent(),
					new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		}

		if (childView.isViewVisible() && twoColsConstraints.getLocation() == TwoColsLayoutLocation.right
				&& ((lastAddedChild == null) || (lastAddedChild != null
						&& lastAddedChild.getConstraints() instanceof TwoColsLayoutConstraints
						&& ((TwoColsLayoutConstraints) lastAddedChild.getConstraints()).getLocation() == TwoColsLayoutLocation.right))) {
			Component glue = Box.createHorizontalGlue();
			_addChildToContainerWithConstraints(glue, getContainerView().getTechnologyComponent(),
					new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, true, false));
		}

		JComponent addedJComponent = ((JFIBView<?, ?>) childView).getResultingJComponent();
		_addChildToContainerWithConstraints(addedJComponent, getContainerView().getTechnologyComponent(), twoColsConstraints);

		lastAddedChild = childView.getComponent();

	}

	@Override
	public void doLayout() {
		lastAddedChild = null;
		super.doLayout();
		if (getContainerView().getComponent().getSubComponents().size() > 0) {
			FIBComponent lastComponent = getContainerView().getComponent().getSubComponents()
					.get(getContainerView().getComponent().getSubComponents().size() - 1);
			if (lastComponent.getConstraints() instanceof TwoColsLayoutConstraints) {
				TwoColsLayoutConstraints contraints = (TwoColsLayoutConstraints) lastComponent.getConstraints();
				if (contraints.getLocation() == TwoColsLayoutLocation.left) {
					// We have to add an extra glue at the end of last component if this one was declared as in LEFT position
					Component glue = Box.createHorizontalGlue();
					_addChildToContainerWithConstraints(glue, getContainerView().getTechnologyComponent(),
							new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				}
			}
		}

		/*Component glue = Box.createHorizontalGlue();
		_addChildToContainerWithConstraints(glue, getContainerView().getTechnologyComponent(),
				new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));*/

	}
}

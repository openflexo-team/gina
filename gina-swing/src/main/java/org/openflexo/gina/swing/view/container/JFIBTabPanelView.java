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

package org.openflexo.gina.swing.view.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.container.impl.FIBTabPanelViewImpl;

/**
 * Swing implementation for a panel presenting some children component as tabs<br>
 * Implementation is based on {@link JTabbedPane}
 * 
 * @author sylvain
 */
public class JFIBTabPanelView extends FIBTabPanelViewImpl<JTabbedPane, JComponent> implements
		JFIBView<FIBTabPanel, JTabbedPane> {

	private static final Logger logger = Logger.getLogger(JFIBTabPanelView.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JPanel with
	 * a given layout<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingTabPanelRenderingAdapter extends SwingRenderingAdapter<JTabbedPane> implements
			TabPanelRenderingAdapter<JTabbedPane, JComponent> {

		@Override
		public int getSelectedIndex(JTabbedPane component) {
			return component.getSelectedIndex();
		}

		@Override
		public void setSelectedIndex(JTabbedPane component, int selectedIndex) {
			component.setSelectedIndex(selectedIndex);
		}
	}

	public JFIBTabPanelView(FIBTabPanel model, FIBController controller) {
		super(model, controller, new SwingTabPanelRenderingAdapter());
	}

	@Override
	public SwingTabPanelRenderingAdapter getRenderingAdapter() {
		return (SwingTabPanelRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected JTabbedPane makeTechnologyComponent() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (getComponent().isRestrictPreferredSizeToSelectedComponent()) {
					updatePreferredSizeWhenRestrictPreferredSizeToSelectedComponent();
				}
			}
		});
		tabbedPane.addContainerListener(new ContainerListener() {

			@Override
			public void componentRemoved(ContainerEvent e) {
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				if (getComponent().isRestrictPreferredSizeToSelectedComponent()) {
					updatePreferredSizeWhenRestrictPreferredSizeToSelectedComponent();
				}
			}
		});
		return tabbedPane;
	}

	private void updatePreferredSizeWhenRestrictPreferredSizeToSelectedComponent() {
		for (int i = 0; i < getTechnologyComponent().getTabCount(); i++) {
			Component tab = getTechnologyComponent().getComponentAt(i);
			if (tab != null) {
				tab.setPreferredSize(new Dimension(0, 0));
			}
		}
		if (getTechnologyComponent().getSelectedIndex() > -1) {
			Component tab = getTechnologyComponent().getComponentAt(getTechnologyComponent().getSelectedIndex());
			if (tab != null) {
				tab.setPreferredSize(null);
			}
		}
		getTechnologyComponent().revalidate();
	}

	// TODO: optimize it
	@Override
	public synchronized void updateLayout() {
		int index = getTechnologyComponent().getSelectedIndex();
		for (FIBView<?, JComponent> v : getSubViews()) {
			v.delete();
		}
		getTechnologyComponent().removeAll();
		buildSubComponents();

		update();

		index = Math.min(index, getTechnologyComponent().getTabCount() - 1);
		if (index > -1) {
			getTechnologyComponent().setSelectedIndex(index);
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		int index = 0;
		for (FIBView<?, JComponent> v : getSubViews()) {
			if (v.getComponent() instanceof FIBTab) {
				if (v.getTechnologyComponent().getParent() != null) {
					getTechnologyComponent().setTitleAt(index, getLocalized(((FIBTab) v.getComponent()).getTitle()));
					index++;
				}
			} else {
				logger.warning("Unexpected component found in TabPanel: " + v.getComponent());
			}
		}
	}

	@Override
	protected void addSubComponentsAndDoLayout() {
		for (FIBComponent c : getComponent().getSubComponents()) {
			JFIBView<?, JComponent> subComponentView = (JFIBView<?, JComponent>) getSubViewsMap().get(c);
			if (c instanceof FIBTab) {
				getTechnologyComponent().add(((FIBTab) c).getTitle(), subComponentView.getResultingJComponent());
			}
		}
	}

}

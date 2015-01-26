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

package org.openflexo.fib.view.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;

public class FIBTabPanelView<T> extends FIBContainerView<FIBTabPanel, JTabbedPane, T> {

	private static final Logger logger = Logger.getLogger(FIBTabPanelView.class.getPackage().getName());

	private JTabbedPane tabbedPane;

	public FIBTabPanelView(FIBTabPanel model, FIBController controller) {
		super(model, controller);
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected JTabbedPane createJComponent() {
		tabbedPane = new JTabbedPane();
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
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			Component tab = tabbedPane.getComponentAt(i);
			if (tab != null) {
				tab.setPreferredSize(new Dimension(0, 0));
			}
		}
		if (tabbedPane.getSelectedIndex() > -1) {
			Component tab = tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
			if (tab != null) {
				tab.setPreferredSize(null);
			}
		}
		tabbedPane.revalidate();
	}

	@Override
	public JTabbedPane getJComponent() {
		return tabbedPane;
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBTab> allTabs = new Vector<FIBTab>();
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (subComponent instanceof FIBTab) {
				allTabs.add((FIBTab) subComponent);
			}
		}

		for (FIBTab tab : allTabs) {
			// logger.info("!!!!!!!!!!!!!!!!!!!! Build view for tab " + tab);
			FIBView subView = getController().buildView(tab);
			if (subView != null) {
				registerViewForComponent(subView, tab);
				registerComponentWithConstraints(subView.getResultingJComponent(), getLocalized(tab.getTitle()));
			}
		}

	}

	// TODO: optimize it
	@Override
	public synchronized void updateLayout() {
		int index = tabbedPane.getSelectedIndex();
		for (FIBView v : getSubViews().values()) {
			v.delete();
		}
		getResultingJComponent().removeAll();
		buildSubComponents();
		// updateDataObject(getDataObject());

		update();

		index = Math.min(index, tabbedPane.getTabCount() - 1);
		if (index > -1) {
			tabbedPane.setSelectedIndex(index);
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		int index = 0;
		for (FIBView v : getSubViews().values()) {
			if (v.getComponent() instanceof FIBTab) {
				if (v.getJComponent().getParent() != null) {
					tabbedPane.setTitleAt(index, getLocalized(((FIBTab) v.getComponent()).getTitle()));
					index++;
				} else {
					getLocalized(((FIBTab) v.getComponent()).getTitle());
				}
			} else {
				logger.warning("Unexpected component found in TabPanel: " + v.getComponent());
			}
		}
	}

	public void setSelectedIndex(int index) {
		getJComponent().setSelectedIndex(index);
	}
}

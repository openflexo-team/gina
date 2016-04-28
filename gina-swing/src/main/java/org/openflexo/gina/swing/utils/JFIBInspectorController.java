/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.swing.view.container.JFIBTabPanelView;
import org.openflexo.gina.utils.FIBInspector;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;

public class JFIBInspectorController implements Observer, ChangeListener {

	static final Logger logger = Logger.getLogger(JFIBInspectorController.class.getPackage().getName());

	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	private final JDialog inspectorDialog;
	private final JPanel EMPTY_CONTENT;
	private final JPanel rootPane;

	private final InspectorGroup inspectorGroup;

	private final Hashtable<FIBInspector, JFIBView<?, ?>> inspectorViews;

	private final LocalizedDelegate localizer;

	public JFIBInspectorController(JFrame frame, Resource inspectorDirectory, FIBLibrary fibLibrary, LocalizedDelegate localizer) {
		inspectorGroup = new InspectorGroup(inspectorDirectory, fibLibrary);
		inspectorViews = new Hashtable<FIBInspector, JFIBView<?, ?>>();
		this.localizer = localizer;

		/*for (FIBInspector inspector : inspectorGroup.getInspectors().values()) {
			JFIBView<?, ?> inspectorView = (JFIBView<?, ?>) FIBController.makeView(inspector, SwingViewFactory.INSTANCE, localizer, true);
			FlexoLocalization.addToLocalizationListeners(inspectorView);
			inspectorViews.put(inspector, inspectorView);
			logger.info("Initialized inspector for " + inspector.getDataClass());
		}*/

		inspectorDialog = new JDialog(frame, "Inspector", false);
		inspectorDialog.setBounds(JFIBPreferences.getInspectorBounds());
		new ComponentBoundSaver(inspectorDialog) {

			@Override
			public void saveBounds(Rectangle bounds) {
				JFIBPreferences.setInspectorBounds(bounds);
			}
		};
		// GPO: Isn't there a bit too much panels here?
		EMPTY_CONTENT = new JPanel(new BorderLayout());
		// EMPTY_CONTENT.setPreferredSize(new Dimension(400,400));
		EMPTY_CONTENT.add(new JLabel("No selection", SwingConstants.CENTER), BorderLayout.CENTER);

		rootPane = new JPanel(new BorderLayout());
		inspectorDialog.getContentPane().setLayout(new BorderLayout());
		inspectorDialog.getContentPane().add(rootPane, BorderLayout.CENTER);

		switchToEmptyContent();
		inspectorDialog.setResizable(true);
		rootPane.revalidate();
		inspectorDialog.setVisible(true);
	}

	private JFIBView<?, ?> getInspectorViewForInspector(FIBInspector inspector) {
		JFIBView<?, ?> inspectorView = inspectorViews.get(inspector);
		if (inspectorView == null) {
			inspector.mergeWithParentInspectors();
			inspectorView = (JFIBView<?, ?>) FIBController.makeView(inspector, SwingViewFactory.INSTANCE, localizer, true);
			FlexoLocalization.addToLocalizationListeners(inspectorView);
			inspectorViews.put(inspector, inspectorView);
		}
		return inspectorView;

	}

	private FIBInspector currentInspector = null;
	private JFIBView<?, ?> currentInspectorView = null;

	private Object currentInspectedObject = null;

	public void inspectObject(Object object) {
		if (object == currentInspectedObject) {
			return;
		}

		currentInspectedObject = object;

		FIBInspector newInspector = inspectorGroup.inspectorForObject(object);

		if (newInspector == null) {
			logger.warning("No inspector for " + object);
			switchToEmptyContent();
		} else {
			if (newInspector != currentInspector) {
				switchToInspector(newInspector);
			}
			currentInspectorView.getController().setDataObject(object);
		}
	}

	private void switchToEmptyContent() {
		// System.out.println("switchToEmptyContent()");
		if (currentInspectorView != null) {
			currentInspectorView.hideView();
		}
		currentInspector = null;
		currentInspectorView = null;
		rootPane.removeAll();
		rootPane.add(EMPTY_CONTENT, BorderLayout.CENTER);
		rootPane.revalidate();
		rootPane.repaint();
	}

	private void switchToInspector(FIBInspector newInspector) {

		if (currentInspector == newInspector) {
			return;
		}
		/*
		 * if (newInspector.getDataClass() == FIBPanel.class) {
		 * System.out.println("Hop: "+newInspector.getXMLRepresentation()); }
		 */

		// System.out.println("switchToInspector() "+newInspector);

		if (currentInspectorView != null) {
			currentInspectorView.hideView();
		}

		JTabbedPane tabPanelViewJComponent = null;

		if (tabPanelView != null) {
			tabPanelViewJComponent = tabPanelView.getJComponent();
			tabPanelViewJComponent.removeChangeListener(this);
			// System.out.println("removeChangeListener for "+tabPanelView.getJComponent());
		}

		currentInspectorView = getInspectorViewForInspector(newInspector);

		if (currentInspectorView != null) {
			rootPane.removeAll();
			JComponent resultingJComponent = currentInspectorView.getResultingJComponent();
			rootPane.add(resultingJComponent, BorderLayout.CENTER);
			rootPane.revalidate();
			rootPane.repaint();
			currentInspector = newInspector;
			inspectorDialog.setTitle(newInspector.getParameter("title"));
			tabPanelView = (JFIBTabPanelView) currentInspectorView.getController().viewForComponent(currentInspector.getTabPanel());
			tabPanelViewJComponent = tabPanelView.getJComponent();
			if (lastInspectedTabIndex >= 0 && lastInspectedTabIndex < tabPanelViewJComponent.getTabCount()) {
				tabPanelViewJComponent.setSelectedIndex(lastInspectedTabIndex);
			}
			tabPanelViewJComponent.addChangeListener(this);
			// System.out.println("addChangeListener for "+tabPanelView.getJComponent());
		} else {
			logger.warning("No inspector view for " + newInspector);
			switchToEmptyContent();
		}
	}

	@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof FIBEditorNotification) {
			if (notification instanceof SelectedObjectChange) {
				SelectedObjectChange selectionChange = (SelectedObjectChange) notification;
				if (selectionChange.newValue() != null) {
					inspectObject(selectionChange.newValue());
				}
			}
		}
	}

	private int lastInspectedTabIndex = -1;
	private JFIBTabPanelView tabPanelView;

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabPanelViewJComponent = tabPanelView.getJComponent();
		lastInspectedTabIndex = tabPanelViewJComponent.getSelectedIndex();
		// System.out.println("Change for index "+lastInspectedTabIndex);
	}

	public void setVisible(boolean flag) {
		inspectorDialog.setVisible(flag);
	}
}

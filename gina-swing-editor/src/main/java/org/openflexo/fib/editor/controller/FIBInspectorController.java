/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.editor.controller;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.editor.FIBPreferences;
import org.openflexo.fib.editor.notifications.FIBEditorNotification;
import org.openflexo.fib.editor.notifications.SelectedObjectChange;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.toolbox.ResourceLocation;
import org.openflexo.toolbox.ResourceLocator;

public class FIBInspectorController implements Observer, ChangeListener {

	static final Logger logger = Logger.getLogger(FIBInspectorController.class.getPackage().getName());

	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	private JDialog inspectorDialog;
	private JPanel EMPTY_CONTENT;
	private JPanel rootPane;

	private Hashtable<Class<?>, FIBInspector> inspectors;
	private Hashtable<FIBInspector, FIBView> inspectorViews;

	public FIBModelFactory INSPECTOR_FACTORY;

	public FIBInspectorController(JFrame frame) {
		inspectors = new Hashtable<Class<?>, FIBInspector>();
		inspectorViews = new Hashtable<FIBInspector, FIBView>();

		try {
			INSPECTOR_FACTORY = new FIBModelFactory(FIBInspector.class);
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResourceLocation dir = rl.locateResource("EditorInspectors");

		for (ResourceLocation f : rl.listResources(dir,Pattern.compile(".*[.]inspector"))) {
			// System.out.println("Read "+f.getAbsolutePath());
			logger.info("Loading " + f.getURL());
			FIBInspector inspector = (FIBInspector) FIBLibrary.instance().retrieveFIBComponent(f, false, INSPECTOR_FACTORY);
			if (inspector != null) {
				if (inspector.getDataClass() != null) {
					// try {
					inspectors.put(inspector.getDataClass(), inspector);
					logger.info("Loaded inspector: " + f.getURL() + " for " + inspector.getDataClass());
					/*} catch (ClassNotFoundException e) {
						logger.warning("Not found: " + inspector.getDataClassName());
					}*/
				}
			} else {
				logger.warning("Not found: " + f.getURL());
			}
		}

		for (FIBInspector inspector : new ArrayList<FIBInspector>(inspectors.values())) {
			inspector.appendSuperInspectors(this);
		}

		for (FIBInspector inspector : inspectors.values()) {

			FIBView inspectorView = FIBController.makeView(inspector, FIBAbstractEditor.LOCALIZATION);
			FlexoLocalization.addToLocalizationListeners(inspectorView);
			inspectorViews.put(inspector, inspectorView);
			logger.info("Initialized inspector for " + inspector.getDataClass());
		}

		inspectorDialog = new JDialog(frame, "Inspector", false);
		inspectorDialog.setBounds(FIBPreferences.getInspectorBounds());
		new ComponentBoundSaver(inspectorDialog) {

			@Override
			public void saveBounds(Rectangle bounds) {
				FIBPreferences.setInspectorBounds(bounds);
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

	private FIBInspector currentInspector = null;
	private FIBView currentInspectorView = null;

	private Object currentInspectedObject = null;

	public void inspectObject(Object object) {
		if (object == currentInspectedObject) {
			return;
		}

		currentInspectedObject = object;

		FIBInspector newInspector = inspectorForObject(object);

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
		currentInspector = null;
		currentInspectorView = null;
		rootPane.removeAll();
		rootPane.add(EMPTY_CONTENT, BorderLayout.CENTER);
		rootPane.revalidate();
		rootPane.repaint();
	}

	private void switchToInspector(FIBInspector newInspector) {
		/*if (newInspector.getDataClass() == FIBPanel.class) {
			System.out.println("Hop: "+newInspector.getXMLRepresentation());
		}*/

		if (tabPanelView != null) {
			tabPanelView.getJComponent().removeChangeListener(this);
			// System.out.println("removeChangeListener for "+tabPanelView.getJComponent());
		}

		// System.out.println("switchToInspector() "+newInspector);
		FIBView view = inspectorViews.get(newInspector);
		if (view != null) {
			currentInspectorView = view;
			rootPane.removeAll();
			rootPane.add(currentInspectorView.getResultingJComponent(), BorderLayout.CENTER);
			rootPane.revalidate();
			rootPane.repaint();
			currentInspector = newInspector;
			inspectorDialog.setTitle(newInspector.getParameter("title"));
			tabPanelView = (FIBTabPanelView) currentInspectorView.getController().viewForComponent(currentInspector.getTabPanel());
			if (lastInspectedTabIndex >= 0 && lastInspectedTabIndex < tabPanelView.getJComponent().getTabCount()) {
				tabPanelView.getJComponent().setSelectedIndex(lastInspectedTabIndex);
			}
			tabPanelView.getJComponent().addChangeListener(this);
			// System.out.println("addChangeListener for "+tabPanelView.getJComponent());
		} else {
			logger.warning("No inspector view for " + newInspector);
			switchToEmptyContent();
		}
	}

	protected FIBInspector inspectorForObject(Object object) {
		if (object == null) {
			return null;
		}
		return inspectorForClass(object.getClass());
	}

	protected FIBInspector inspectorForClass(Class<?> aClass) {
		return TypeUtils.objectForClass(aClass, inspectors);

		// Old code, to be removed
		/*Class c = aClass;
		while (c != null) {
			FIBInspector returned = inspectors.get(c);
			if (returned != null) {
				return returned;
			} else {
				c = c.getSuperclass();
			}
		}
		return null;*/
	}

	protected Hashtable<Class<?>, FIBInspector> getInspectors() {
		return inspectors;
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
	private FIBTabPanelView tabPanelView;

	@Override
	public void stateChanged(ChangeEvent e) {
		lastInspectedTabIndex = tabPanelView.getJComponent().getSelectedIndex();
		// System.out.println("Change for index "+lastInspectedTabIndex);
	}

	public void setVisible(boolean flag) {
		inspectorDialog.setVisible(flag);
	}
}

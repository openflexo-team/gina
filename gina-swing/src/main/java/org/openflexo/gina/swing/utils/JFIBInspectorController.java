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
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.gina.FIBInspector;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.InspectorGroup;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;

/**
 * Base implementation of an inspector controller in the context of a set of inspectors beeing displayed<br>
 * MAnages a JPanel represented by this inspector controller: unbound in swing hierarchy at creation, see {@link #getRootPane()}
 * 
 * Uses the Observable/Observer scheme: deprecated, will be replaced by HasPropertyChangeSupport scheme
 * 
 * @author sylvain
 *
 */
public class JFIBInspectorController implements Observer {

	static final Logger logger = Logger.getLogger(JFIBInspectorController.class.getPackage().getName());

	private final JPanel EMPTY_CONTENT;
	private final JPanel rootPane;

	private final InspectorGroup inspectorGroup;

	private final Hashtable<FIBInspector, JFIBView<?, ?>> inspectorViews;

	private final LocalizedDelegate localizer;

	public JFIBInspectorController(Resource inspectorDirectory, FIBLibrary fibLibrary, LocalizedDelegate localizer) {
		this(inspectorDirectory, fibLibrary, localizer, null);
	}

	public JFIBInspectorController(Resource inspectorDirectory, FIBLibrary fibLibrary, LocalizedDelegate localizer,
			final FIBEditorLoadingProgress progress) {
		inspectorGroup = new InspectorGroup(inspectorDirectory, fibLibrary, FIBModelObjectImpl.GINA_LOCALIZATION) {
			@Override
			public void progress(Resource f, FIBInspector inspector) {
				super.progress(f, inspector);
				if (progress != null) {
					progress.progress(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("loaded_inspector") + " "
							+ inspector.getDataClass().getSimpleName());
				}
			}
		};
		inspectorViews = new Hashtable<>();
		this.localizer = localizer;

		// GPO: Isn't there a bit too much panels here?
		EMPTY_CONTENT = new JPanel(new BorderLayout());
		// EMPTY_CONTENT.setPreferredSize(new Dimension(400,400));
		EMPTY_CONTENT.add(new JLabel("No selection", SwingConstants.CENTER), BorderLayout.CENTER);

		rootPane = new JPanel(new BorderLayout());

	}

	/**
	 * Return JPanel represented by this inspector controller: unbound in swing hierarchy at creation
	 * 
	 * @return
	 */
	public JPanel getRootPane() {
		return rootPane;
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

	protected FIBInspector currentInspector = null;
	protected JFIBView<?, ?> currentInspectorView = null;

	private Object currentInspectedObject = null;

	public void inspectObject(Object object) {

		// System.out.println("Inspect object " + object + " current=" + currentInspectedObject);

		if (object == currentInspectedObject) {
			return;
		}

		currentInspectedObject = object;

		FIBInspector newInspector = inspectorGroup.inspectorForObject(object);

		if (newInspector == null) {
			logger.warning("No inspector for " + object);
			switchToEmptyContent();
		}
		else {
			if (newInspector != currentInspector) {
				switchToInspector(newInspector);
			}
			currentInspectorView.getController().setDataObject(object);
		}
	}

	protected void switchToEmptyContent() {
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

	/**
	 * Request switching to new inspector<br>
	 * Returned flag indicates if the root pane has been updated (type of inspected object changed)
	 * 
	 * @param newInspector
	 * @return
	 */
	protected boolean switchToInspector(FIBInspector newInspector) {

		if (currentInspector == newInspector) {
			return false;
		}
		/*
		 * if (newInspector.getDataClass() == FIBPanel.class) {
		 * System.out.println("Hop: "+newInspector.getXMLRepresentation()); }
		 */

		// System.out.println("switchToInspector() "+newInspector);

		if (currentInspectorView != null) {
			currentInspectorView.hideView();
		}

		currentInspectorView = getInspectorViewForInspector(newInspector);
		if (currentInspectorView != null) {
			currentInspectorView.showView();
		}

		if (currentInspectorView != null) {
			updateRootPane();
			currentInspector = newInspector;
			return true;
		}
		else {
			logger.warning("No inspector view for " + newInspector);
			switchToEmptyContent();
		}

		return false;
	}

	protected void updateRootPane() {
		rootPane.removeAll();
		JComponent resultingJComponent = currentInspectorView.getResultingJComponent();
		rootPane.add(resultingJComponent, BorderLayout.CENTER);
		rootPane.revalidate();
		rootPane.repaint();
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
}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.inspector;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.utils.FIBEditorNotification;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.SelectedObjectChange;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.FlexoCollabsiblePanelGroup;

/**
 * Manages all context-specific inspectors attached to a FIBEditor
 * 
 * @author sylvain
 * 
 */
public class FIBEditorInspectorController implements Observer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBEditorInspectorController.class.getPackage().getName());

	JFIBInspectorController basicInspector;
	JFIBInspectorController containerInspector;
	JFIBInspectorController controlsInspector;
	JFIBInspectorController layoutInspector;
	JFIBInspectorController fontAndColorsInspector;
	JFIBInspectorController advancedInspector;

	private final FlexoCollabsiblePanelGroup panelGroup;

	private static final FIBLibrary INSPECTORS_FIB_LIBRARY = FIBLibraryImpl.createInstance(null);

	public FIBEditorInspectorController() {

		basicInspector = new JFIBInspectorController(ResourceLocator.locateResource("Inspectors/Basic"), INSPECTORS_FIB_LIBRARY,
				FIBModelObjectImpl.GINA_LOCALIZATION);

		containerInspector = new JFIBInspectorController(ResourceLocator.locateResource("Inspectors/Container"), INSPECTORS_FIB_LIBRARY,
				FIBModelObjectImpl.GINA_LOCALIZATION);

		controlsInspector = new JFIBInspectorController(ResourceLocator.locateResource("Inspectors/Controls"), INSPECTORS_FIB_LIBRARY,
				FIBModelObjectImpl.GINA_LOCALIZATION);

		layoutInspector = new JFIBInspectorController(ResourceLocator.locateResource("Inspectors/Layout"), INSPECTORS_FIB_LIBRARY,
				FIBModelObjectImpl.GINA_LOCALIZATION);

		fontAndColorsInspector = new JFIBInspectorController(ResourceLocator.locateResource("Inspectors/FontAndColors"),
				INSPECTORS_FIB_LIBRARY, FIBModelObjectImpl.GINA_LOCALIZATION);

		advancedInspector = new JFIBInspectorController(ResourceLocator.locateResource("Inspectors/Advanced"), INSPECTORS_FIB_LIBRARY,
				FIBModelObjectImpl.GINA_LOCALIZATION);

		panelGroup = new FlexoCollabsiblePanelGroup();
		panelGroup.addContents("Basic", basicInspector.getRootPane());
		panelGroup.addContents("Container", containerInspector.getRootPane());
		panelGroup.addContents("Controls", controlsInspector.getRootPane());
		panelGroup.addContents("Layout", layoutInspector.getRootPane());
		panelGroup.addContents("Font and colors", fontAndColorsInspector.getRootPane());
		panelGroup.addContents("Advanced", advancedInspector.getRootPane());
		panelGroup.setOpenedPanel(0); // Open definition inspector
	}

	public FlexoCollabsiblePanelGroup getPanelGroup() {
		return panelGroup;
	}

	public void attachToEditor(FIBEditorController editorController) {
		System.out.println("CHECK THIS !!!!");
	}

	public List<FIBModelObject> getSelection() {
		return null;
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

	public void inspectObject(Object object) {
		basicInspector.inspectObject(object);
		containerInspector.inspectObject(object);
		controlsInspector.inspectObject(object);
		layoutInspector.inspectObject(object);
		fontAndColorsInspector.inspectObject(object);
		advancedInspector.inspectObject(object);
	}
}

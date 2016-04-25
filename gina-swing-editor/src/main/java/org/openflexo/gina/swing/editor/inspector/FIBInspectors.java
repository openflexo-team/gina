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
import java.util.logging.Logger;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.utils.FIBInspector;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.FlexoCollabsiblePanelGroup;

/**
 * SWING implementation of {@link DianaInspectors}
 * 
 * @author sylvain
 * 
 */
public class FIBInspectors {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FIBInspectors.class.getPackage().getName());

	// private JInspector<FIBComponent> fontAndColorsInspector;

	private final FlexoCollabsiblePanelGroup panelGroup;

	private static final FIBLibrary INSPECTORS_FIB_LIBRARY = FIBLibraryImpl.createInstance();

	public FIBInspectors() {

		Resource fontAndColorInspectorsDir = ResourceLocator.locateResource("EditorInspectors/FontAndColors");
		InspectorGroup fontAndColorInspectorGroup = new InspectorGroup(fontAndColorInspectorsDir, INSPECTORS_FIB_LIBRARY) {
			@Override
			public void progress(Resource f, FIBInspector inspector) {
				super.progress(f, inspector);
				System.out.println("********** LOADED: " + inspector);
			}
		};

		JFIBView<?, ?> inspectorView = (JFIBView<?, ?>) FIBController.makeView(
				fontAndColorInspectorGroup.inspectorForClass(FIBComponent.class), SwingViewFactory.INSTANCE,
				FlexoLocalization.getMainLocalizer());

		panelGroup = new FlexoCollabsiblePanelGroup();
		panelGroup.addContents("Font and colors", inspectorView.getJComponent());
		panelGroup.setOpenedPanel(0); // Open foreground style inspector
	}

	public FlexoCollabsiblePanelGroup getPanelGroup() {
		return panelGroup;
	}

	public void attachToEditor(FIBEditorController editorController) {
		// if (fontAndColorsInspector != null) {
		// fontAndColorsInspector.setData(getInspectedForegroundStyle());
		// }
	}

	/*public JInspector<InspectedFontAndColorsProperties> getFontAndColorsInspector() {
		if (fontAndColorsInspector == null) {
			fontAndColorsInspector = new JInspector<InspectedFontAndColorsProperties>(
					AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(JDianaInspectorsResources.FOREGROUND_STYLE_FIB_FILE, true),
					getInspectedForegroundStyle(), JDianaInspectorsResources.FOREGROUND_NAME, InspectedFontAndColorsProperties.class);
		}
		return foregroundStyleInspector;
	}
	
	@SuppressWarnings("serial")
	public static class JInspector<T> extends FIBJPanel<T>implements DianaInspectors.Inspector<T> {
	
		private final String title;
		private final Class<T> representedType;
	
		protected JInspector(FIBComponent fibComponent, T data, String title, final Class<T> representedType) {
			super(fibComponent, data, (LocalizedDelegate) null);
			this.representedType = representedType;
			this.title = title;
		}
	
		@Override
		public void setData(T data) {
			setEditedObject(data);
		}
	
		@Override
		public Class<T> getRepresentedType() {
			return representedType;
		}
	
		@Override
		public void delete() {
		}
	
		public String getTitle() {
			return title;
		}
	}
	*/

	public List<FIBModelObject> getSelection() {
		return null;
	}

}

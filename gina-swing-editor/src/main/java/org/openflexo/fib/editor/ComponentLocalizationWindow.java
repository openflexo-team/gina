/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.fib.editor;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.localization.LocalizedPanel;
import org.openflexo.localization.FlexoLocalization;

/**
 * Dialog used in FIBEditor, and used to edit localized of a {@link FIBComponent}
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public class ComponentLocalizationWindow extends JDialog {

	private final LocalizedPanel localizedPanel;
	private final FIBEditorController editorController;

	public ComponentLocalizationWindow(JFrame frame, final FIBEditorController editorController) {
		super(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_localization"), false);
		this.editorController = editorController;
		localizedPanel = new LocalizedPanel(editorController.getFIBComponent().getLocalizedDictionary(), FIBAbstractEditor.LOCALIZATION,
				false, true) {
			@Override
			public void searchLocalized() {
				editorController.getFIBComponent().searchAndRegisterAllLocalized();
			}
		};
		getContentPane().add(localizedPanel);
		pack();
	}

	public FIBComponent getFIBComponent() {
		return getEditorController().getFIBComponent();
	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

}

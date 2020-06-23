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

package org.openflexo.gina.swing.editor.controller.action;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.exceptions.ModelExecutionException;

public class PasteAction extends AbstractEditorActionImpl {

	public static KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_V, FIBEditor.META_MASK);

	public PasteAction(FIBEditorController anEditorController) {
		super("paste", FIBEditorIconLibrary.PASTE_ICON, anEditorController);
	}

	@Override
	public KeyStroke getShortcut() {
		return ACCELERATOR;
	}

	@Override
	public boolean isEnabledFor(FIBModelObject object) {
		return object != null && getEditorController().getEditor().getClipboard() != null;
	}

	@Override
	public boolean isVisibleFor(FIBModelObject object) {
		return true;
	}

	@Override
	public FIBModelObject performAction(FIBModelObject object) {
		System.out.println("Paste");
		try {
			object.getComponent().getModelFactory().paste(getEditorController().getEditor().getClipboard(), object);
			/*if (getEditorController().getEditor().getClipboard().isSingleObject()) {
				Object c = getEditorController().getEditor().getClipboard().getSingleContents();
				if (c instanceof FIBComponent) {
					((FIBComponent) c).translateNameWhenRequired();
				}
			}
			else {
				for (Object c : getEditorController().getEditor().getClipboard().getMultipleContents()) {
					if (c instanceof FIBComponent) {
						((FIBComponent) c).translateNameWhenRequired();
					}
				}
			}*/
			for (Object c : getEditorController().getEditor().getClipboard().getLastReferenceContents()) {
				System.out.println("On paste " + c);
				if (c instanceof FIBComponent) {
					((FIBComponent) c).translateNameWhenRequired();
				}
			}
		} catch (ModelExecutionException e) {
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return object;
	}

}

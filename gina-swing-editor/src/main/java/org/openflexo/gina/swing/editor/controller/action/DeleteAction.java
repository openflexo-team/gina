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

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.logging.FlexoLogger;

public class DeleteAction extends AbstractEditorActionImpl {

	private static final Logger logger = FlexoLogger.getLogger(DeleteAction.class.getPackage().getName());

	public DeleteAction(FIBEditorController anEditorController, final JFrame frame) {
		super("delete", FIBEditorIconLibrary.DELETE_ICON, anEditorController, frame);
	}

	@Override
	public KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(FIBEditor.DELETE_KEY_CODE, 0);
	}

	@Override
	public boolean isEnabledFor(FIBModelObject object) {
		return object instanceof FIBComponent;
	}

	@Override
	public boolean isVisibleFor(FIBModelObject object) {
		return true;
	}

	@Override
	public FIBModelObject performAction(FIBModelObject object) {
		if (object instanceof FIBComponent) {
			FIBContainer parent = ((FIBComponent) object).getParent();
			boolean deleteIt = JOptionPane.showConfirmDialog(getFrame(), object + ": really delete this component (undoable operation) ?",
					"information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
			if (deleteIt) {
				logger.info("Removing object " + object + " from " + parent);
				parent.removeFromSubComponents((FIBComponent) object);
				object.delete();
			}
			return parent;
		}
		return null;
	}
}

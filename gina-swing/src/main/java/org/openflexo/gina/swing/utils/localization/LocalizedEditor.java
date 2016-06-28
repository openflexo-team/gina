/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils.localization;

import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;

/**
 * Non-modal dialog used to edit some locales as {@link LocalizedDelegate}
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public class LocalizedEditor extends JDialog {

	private static final Logger LOGGER = Logger.getLogger(LocalizedEditor.class.getPackage().getName());

	private final LocalizedPanel localizedPanel;
	private final LocalizedDelegate localizedToEdit;

	public LocalizedEditor(JFrame frame, String title, LocalizedDelegate localizedToEdit, LocalizedDelegate editorLocalized,
			boolean displaySave, boolean displaySearchLocalized) {
		super(frame, editorLocalized.localizedForKeyAndLanguage(title, FlexoLocalization.getCurrentLanguage()), false);
		localizedPanel = new LocalizedPanel(localizedToEdit, editorLocalized, displaySave, displaySearchLocalized) {
			@Override
			public void save() {
				LocalizedEditor.this.save();
			}

			@Override
			public void searchLocalized() {
				LocalizedEditor.this.searchLocalized();
			}
		};
		this.localizedToEdit = localizedToEdit;
		getContentPane().add(localizedPanel);
		pack();
	}

	public void searchLocalized() {
		localizedToEdit.searchLocalized();
	}

	public void save() {
		if (localizedToEdit instanceof LocalizedDelegateImpl) {
			((LocalizedDelegateImpl) localizedToEdit).saveAllDictionaries();
		}
		else {
			LOGGER.warning("save localized not implemented");
		}
	}

}

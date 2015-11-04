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

package org.openflexo.fib.swing.utils.localization;

import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.utils.FIBJPanel;
import org.openflexo.fib.swing.view.SwingViewFactory;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * Swing widget allowing to edit locales stored as a {@link LocalizedDelegate}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class LocalizedPanel extends FIBJPanel<LocalizedDelegate> {

	private static final Logger LOGGER = Logger.getLogger(LocalizedPanel.class.getPackage().getName());

	public static Resource lOCALIZED_PANEL_FIB = ResourceLocator.locateResource("Fib/LocalizedPanel.fib");

	final boolean displaySave;
	final boolean displaySearchLocalized;

	public LocalizedPanel(LocalizedDelegate localizedDelegate, LocalizedDelegate editorLocalizer, boolean displaySave,
			boolean displaySearchLocalized) {
		super(FIBLibrary.instance().retrieveFIBComponent(lOCALIZED_PANEL_FIB, true), localizedDelegate, editorLocalizer);
		this.displaySave = displaySave;
		this.displaySearchLocalized = displaySearchLocalized;
		// We have here to renotify those flags, because the GUI was build BEFORE those values are set
		getController().getPropertyChangeSupport().firePropertyChange("displaySaveButton", !displaySave, displaySave);
		getController().getPropertyChangeSupport().firePropertyChange("displaySearchLocalizedButton", !displaySearchLocalized,
				displaySearchLocalized);
	}

	@Override
	protected FIBController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return new FIBLocalizedEditorController(fibComponent, SwingViewFactory.INSTANCE) {
			@Override
			public boolean displaySaveButton() {
				return LocalizedPanel.this.displaySaveButton();
			}

			@Override
			public boolean displaySearchLocalizedButton() {
				return LocalizedPanel.this.displaySearchLocalizedButton();
			}

			@Override
			public void save() {
				LocalizedPanel.this.save();
			}

			@Override
			public void searchLocalized() {
				LocalizedPanel.this.searchLocalized();
			}
		};
	}

	@Override
	public Class<LocalizedDelegate> getRepresentedType() {
		return LocalizedDelegate.class;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
	}

	public boolean displaySaveButton() {
		return displaySave;
	}

	public boolean displaySearchLocalizedButton() {
		return displaySearchLocalized;
	}

	public void searchLocalized() {
		getEditedObject().searchLocalized();
	}

	public void save() {
		if (getEditedObject() instanceof LocalizedDelegateImpl) {
			((LocalizedDelegateImpl) getEditedObject()).saveAllDictionaries();
		}
		else {
			LOGGER.warning("save localized not implemented");
		}
	}

	/*public static void main(String[] args) {
	
		Resource fibValidationLocalizedDelegate = ResourceLocator.locateResource("FIBValidationLocalized");
		LocalizedDelegate VALIDATION_LOCALIZATION = FlexoLocalization
				.getLocalizedDelegate(fibValidationLocalizedDelegate, null, true, true);
	
		JFrame f = new JFrame();
		LocalizedPanel editor = new LocalizedPanel(VALIDATION_LOCALIZATION, null);
		f.getContentPane().add(editor);
		f.validate();
		f.pack();
		f.show();
	}*/

}

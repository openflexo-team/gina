package org.openflexo.fib.swing.localization;

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

	private static final Logger logger = Logger.getLogger(LocalizedEditor.class.getPackage().getName());

	private final LocalizedPanel localizedPanel;
	private final LocalizedDelegate localizedToEdit;

	public LocalizedEditor(JFrame frame, String title, LocalizedDelegate localizedToEdit, LocalizedDelegate editorLocalized,
			boolean displaySave, boolean displaySearchLocalized) {
		super(frame, editorLocalized.getLocalizedForKeyAndLanguage(title, FlexoLocalization.getCurrentLanguage()), false);
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
		} else {
			logger.warning("save localized not implemented");
		}
	}

}
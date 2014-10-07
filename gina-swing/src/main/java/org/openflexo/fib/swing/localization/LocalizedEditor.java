package org.openflexo.fib.swing.localization;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;

/**
 * Non-modal dialog used to edit some locales as {@link LocalizedDelegate}
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public class LocalizedEditor extends JDialog {

	private final LocalizedPanel localizedPanel;

	public LocalizedEditor(JFrame frame, String title, LocalizedDelegate localizedToEdit, LocalizedDelegate editorLocalized) {
		super(frame, editorLocalized.getLocalizedForKeyAndLanguage(title, FlexoLocalization.getCurrentLanguage()), false);
		localizedPanel = new LocalizedPanel(localizedToEdit, editorLocalized);
		getContentPane().add(localizedPanel);
		pack();
	}
}
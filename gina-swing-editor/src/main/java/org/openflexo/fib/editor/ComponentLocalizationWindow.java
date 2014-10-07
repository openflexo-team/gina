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

	public ComponentLocalizationWindow(JFrame frame, FIBEditorController editorController) {
		super(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "component_localization"), false);
		this.editorController = editorController;
		localizedPanel = new LocalizedPanel(editorController.getFIBComponent().getLocalizedDictionary(), FIBAbstractEditor.LOCALIZATION);
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
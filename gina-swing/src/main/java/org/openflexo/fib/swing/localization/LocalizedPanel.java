package org.openflexo.fib.swing.localization;

import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.FIBJPanel;
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

	private static final Logger logger = Logger.getLogger(LocalizedPanel.class.getPackage().getName());

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
		return new FIBLocalizedEditorController(fibComponent) {
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
		} else {
			logger.warning("save localized not implemented");
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

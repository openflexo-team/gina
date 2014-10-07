package org.openflexo.fib.swing.localization;

import java.util.logging.Logger;

import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.FIBJPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
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

	private static final Logger logger = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	public static Resource lOCALIZED_PANEL_FIB = ResourceLocator.locateResource("Fib/LocalizedPanel.fib");

	public LocalizedPanel(LocalizedDelegate localizedDelegate, LocalizedDelegate editorLocalizer) {
		super(FIBLibrary.instance().retrieveFIBComponent(lOCALIZED_PANEL_FIB, true), localizedDelegate, editorLocalizer);
	}

	@Override
	protected FIBController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return new FIBLocalizedEditorController(fibComponent);
	}

	@Override
	public Class<LocalizedDelegate> getRepresentedType() {
		return LocalizedDelegate.class;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {

		Resource fibValidationLocalizedDelegate = ResourceLocator.locateResource("FIBValidationLocalized");
		LocalizedDelegate VALIDATION_LOCALIZATION = FlexoLocalization
				.getLocalizedDelegate(fibValidationLocalizedDelegate, null, true, true);

		JFrame f = new JFrame();
		LocalizedPanel editor = new LocalizedPanel(VALIDATION_LOCALIZATION, null);
		f.getContentPane().add(editor);
		f.validate();
		f.pack();
		f.show();
	}

}

package org.openflexo.replay.editor;

import java.io.File;

import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * This class is used to launch the fib editor (Editor/Editor.fib).
 * This editor is used to edit a recorded scenario.
 * *TODO not functional : scenario and events can be loaded, but editing event properties is not possible yet*
 * 
 * @author Alexandre
 *
 */
public class GinaRecorderEditor {

	public GinaRecorderEditor() {
		final ResourceLocator rl = ResourceLocator.getResourceLocator();

		Resource fibResource = ResourceLocator.locateSourceCodeResource("Editor/Editor.fib");
		System.out.println("Fib: " + fibResource);

		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResource);
		fibComponent.setControllerClass(GinaRecorderEditorController.class);

		JFrame frame = new JFrame();

		//FIBRecorderEditorController controller = FIBController.instanciateController(fibComponent, FlexoLocalization.getMainLocalizer());
		FIBView editor = GinaRecorderEditorController.makeView(fibComponent, (LocalizedDelegate) null);

		//FIBRecorderManager.getInstance().getCurrentRecorder().load(new File("D:/test-gina-recorder"));
		// TODO editor.getController().setDataObject(GinaManager.getInstance().getCurrentRecorder().getRootNode());

		frame.getContentPane().add(editor.getResultingJComponent());
		frame.setSize(720, 800);
		frame.setVisible(true);
	}

}

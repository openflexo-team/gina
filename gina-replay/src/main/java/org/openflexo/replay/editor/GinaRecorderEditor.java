package org.openflexo.replay.editor;

import javax.swing.JFrame;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.FIBView;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class GinaRecorderEditor {

	public GinaRecorderEditor() {
		final ResourceLocator rl = ResourceLocator.getResourceLocator();

		Resource fibResource = ResourceLocator.locateSourceCodeResource("Editor/Editor.fib");
		System.out.println("Fib: " + fibResource);

		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResource);
		fibComponent.setControllerClass(GinaRecorderEditorController.class);

		JFrame frame = new JFrame();

		// FIBRecorderEditorController controller = FIBController.instanciateController(fibComponent, FlexoLocalization.getMainLocalizer());
		FIBView editor = GinaRecorderEditorController.makeView(fibComponent, SwingViewFactory.INSTANCE, (LocalizedDelegate) null);

		// FIBRecorderManager.getInstance().getCurrentRecorder().load(new File("D:/test-gina-recorder"));
		// TODO editor.getController().setDataObject(GinaManager.getInstance().getCurrentRecorder().getRootNode());

		frame.getContentPane().add(editor.getResultingJComponent());
		frame.setSize(720, 800);
		frame.setVisible(true);
	}

}

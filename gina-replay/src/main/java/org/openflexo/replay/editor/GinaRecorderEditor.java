package org.openflexo.replay.editor;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class GinaRecorderEditor {

	public GinaRecorderEditor() {
		Resource fibResource = ResourceLocator.locateSourceCodeResource("Editor/Editor.fib");
		System.out.println("Fib: " + fibResource);

		FIBComponent fibComponent = ApplicationFIBLibraryImpl.instance().retrieveFIBComponent(fibResource);
		fibComponent.setControllerClass(GinaRecorderEditorController.class);

		JFrame frame = new JFrame();

		// FIBRecorderEditorController controller =
		// FIBController.instanciateController(fibComponent,
		// FlexoLocalization.getMainLocalizer());
		JFIBView<?, JComponent> editor = (JFIBView<?, JComponent>) GinaRecorderEditorController.makeView(fibComponent,
				SwingViewFactory.INSTANCE, (LocalizedDelegate) null, true);

		// FIBRecorderManager.getInstance().getCurrentRecorder().load(new
		// File("D:/test-gina-recorder"));
		// TODO
		// editor.getController().setDataObject(GinaManager.getInstance().getCurrentRecorder().getRootNode());

		frame.getContentPane().add(editor.getJComponent());
		frame.setSize(720, 800);
		frame.setVisible(true);
	}

}

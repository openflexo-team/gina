package org.openflexo.himtester;

import java.io.File;

import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class FIBRecorderEditor {

	public FIBRecorderEditor() {
		final ResourceLocator rl = ResourceLocator.getResourceLocator();

		Resource fibResource = ResourceLocator.locateSourceCodeResource("Editor/Editor.fib");
		System.out.println("Fib: " + fibResource);
		
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResource);

		JFrame frame = new JFrame();

		FIBView testFibPanel = FIBController.makeView(fibComponent, (LocalizedDelegate) null);
		
		FIBRecorderManager.getInstance().getCurrentRecorder().load(new File("D:/test-gina-recorder"));
		testFibPanel.getController().setDataObject(FIBRecorderManager.getInstance().getCurrentRecorder().getRootNode());

		frame.getContentPane().add(testFibPanel.getResultingJComponent());
		frame.setSize(720, 640);
		frame.setVisible(true);
	}
	
}

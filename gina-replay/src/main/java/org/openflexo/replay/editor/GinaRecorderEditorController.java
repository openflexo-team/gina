package org.openflexo.replay.editor;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.GinaViewFactory;

public class GinaRecorderEditorController extends FIBController {

	public GinaRecorderEditorController(FIBComponent rootComponent, GinaViewFactory<?> viewFactory) {
		super(rootComponent, viewFactory);
	}

	public void nextStep() {
		/*try {
			GinaManager.getInstance().getCurrentRecorder().checkNextStep(false);
		} catch (InvalidRecorderStateException e) {
			e.printStackTrace();
		}*/
	}

	public void play() {
	}

	public void reset() {
	}

	public void gotoEnd() {
	}

	public void save() {
		/*GinaManager.getInstance().getCurrentRecorder()
				.save(new File("D:/test-gina-recorder"));*/
	}

}

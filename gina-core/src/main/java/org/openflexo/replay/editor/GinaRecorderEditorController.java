package org.openflexo.replay.editor;

import java.io.File;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.replay.GinaRecorderManager;
import org.openflexo.replay.InvalidRecorderStateException;

public class GinaRecorderEditorController extends FIBController {

	public GinaRecorderEditorController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	public void nextStep() {
		try {
			GinaRecorderManager.getInstance().getCurrentRecorder().checkNextStep(false);
		} catch (InvalidRecorderStateException e) {
			e.printStackTrace();
		}
	}

	public void play() {
	}
	
	public void reset() {
	}
	
	public void gotoEnd() {
	}

	public void save() {
		GinaRecorderManager.getInstance().getCurrentRecorder()
				.save(new File("D:/test-gina-recorder"));
	}

}

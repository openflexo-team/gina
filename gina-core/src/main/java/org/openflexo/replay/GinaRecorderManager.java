package org.openflexo.replay;

import java.io.File;

import org.openflexo.gina.event.FIBEvent;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.replay.editor.GinaRecorderEditor;


public class GinaRecorderManager {
	static private GinaRecorderManager instance;

	private GinaRecorder recorder;
	private ModelFactory factory;
	private ModelContext context;
	
	static public GinaRecorderManager getInstance() {
		if (instance == null)
			instance = new GinaRecorderManager();
		
		return instance;
	}


	private GinaRecorderManager() {
		this.recorder = null;
		this.factory = null;
		this.context = null;
	}

	public GinaRecorder createAndSetToCurrent() {
		GinaRecorder r = new GinaRecorder();
		
		setCurrentRecorder(r);
		
		return r;
	}

	public void setCurrentRecorder(GinaRecorder r) {
		recorder = r;
	}

	public GinaRecorder getCurrentRecorder() {
		return recorder;
	}
	
	public ModelFactory getFactory() {
		if (factory == null) {
			try {
				context = new ModelContext(FIBEvent.class, GinaRecordedNode.class);
				factory = new ModelFactory(context);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		return factory;
	}


	public void setup() {
		if (recorder == null) {
			createAndSetToCurrent();

			/*recorder.load(new File("D:/test-gina-recorder"));
			recorder.play();*/
			
			recorder.startRecording();
			
			GinaRecorderEditor editor = new GinaRecorderEditor();
		}
	}
}

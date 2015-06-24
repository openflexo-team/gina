package org.openflexo.replay;

import org.openflexo.gina.event.GinaEvent;
import org.openflexo.model.ModelContext;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.replay.editor.GinaRecorderEditor;


public class GinaRecorderManager {
	static private GinaRecorderManager instance;

	private GinaRecorder recorder;
	private ModelFactory factory;
	private ModelContext context;
	
	/**
	 * Return the unique instance of the manage and create it if needed
	 * @return GinaRecorderManager instance
	 */
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

	/**
	 * Instantiate a GinaRecorder and set it as the current recorder
	 * @return the newly created recorder
	 */
	public GinaRecorder createAndSetToCurrent() {
		GinaRecorder r = new GinaRecorder();
		
		setCurrentRecorder(r);
		
		return r;
	}

	/**
	 * Set the current recorder
	 * @param r the recorder instance
	 */
	public void setCurrentRecorder(GinaRecorder r) {
		recorder = r;
	}

	/**
	 * Return the current recorder
	 * @return the current recorder
	 */
	public GinaRecorder getCurrentRecorder() {
		return recorder;
	}
	
	/**
	 * Get the Pamela model factory for the GinaRecordedNode and the GinaEvent
	 * @return the model factory
	 */
	public ModelFactory getFactory() {
		if (factory == null) {
			try {
				context = new ModelContext(GinaEvent.class, GinaRecordedNode.class);
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

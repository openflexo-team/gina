package org.openflexo.gina.task;

public class GinaTaskAdapter implements GenericTaskAdapter {

	@Override
	public boolean checkTask(Runnable r) {
		return r instanceof GinaTask;
	}
	
	@Override
	public boolean checkThread(Thread t) {
		return t instanceof GinaTaskThread;
	}
	
	@Override
	public GinaTaskThread createThread(ThreadGroup group, Runnable r, String name, long stackSize) {
		return new GinaTaskThread(group, r, name, stackSize);
	}

}

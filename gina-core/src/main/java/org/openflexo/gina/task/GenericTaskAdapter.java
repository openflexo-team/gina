package org.openflexo.gina.task;

public interface GenericTaskAdapter {

	boolean checkTask(Runnable r);
	
	boolean checkThread(Thread t);

	GinaTaskThread createThread(ThreadGroup group, Runnable r, String name, long stackSize);

}

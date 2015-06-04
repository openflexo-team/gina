package org.openflexo.himtester.simple;

import org.openflexo.gina.task.GinaTask;

public class SimpleTask extends GinaTask {

	public SimpleTask(String title) {
		super(title);
		System.out.println("<<TASK>> started");
	}

	@Override
	public void performTask() throws InterruptedException {
		// TODO Auto-generated method stub
		for(int i = 0; i < 5; ++i) {
			Thread.sleep(1000);
			System.out.println("<<TASK>> " + i);
		}
	}

}

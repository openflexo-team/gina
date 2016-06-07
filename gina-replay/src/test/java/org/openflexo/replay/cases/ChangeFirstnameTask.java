package org.openflexo.replay.cases;

import java.util.Random;

import org.openflexo.gina.manager.EventManager;
import org.openflexo.gina.manager.GinaEventContext;
import org.openflexo.replay.sampleData.Person;
import org.openflexo.replay.utils.Task;

public class ChangeFirstnameTask extends Task {

	private Person person;

	public ChangeFirstnameTask(EventManager manager, String title, Person person) {
		super(manager, title);

		this.person = person;
	}

	@Override
	public void run() {
		this.GENotifier.startThreadStack(this);

		System.out.println("<<TASK>> started");

		for (int i = 0; i < 0; ++i) {
			Random rand = new Random();

			try {
				Thread.sleep(5 + rand.nextInt(10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("<<TASK>> " + i);

			// GinaEventFilter filter = new GinaEventFilter(this.person);
			GinaEventContext context = new GinaEventContext(this.GENotifier.getManager(), this);
			context.open();
			this.person.setFirstName(this.person.getFirstName() + i);
			context.close();
		}

		this.GENotifier.endTask(this);
	}

}

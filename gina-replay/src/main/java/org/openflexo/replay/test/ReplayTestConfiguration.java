package org.openflexo.replay.test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.openflexo.gina.event.GinaEvent;
import org.openflexo.gina.event.description.ApplicationEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.manager.GinaEventFactory;
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.GinaReplaySession;
import org.openflexo.replay.InteractionCycle;
import org.openflexo.replay.Scenario;

/**
 * This class is used to configure a replay. As you can see in the example org.openflexo.replay.test.Tester, this class should be used as
 * the following : - instantiate the class - load a scenario via loadScenario - execute runMain in order to launch the scenario environment
 * (it will load the main function of the target class)
 * 
 * The target class MUST implements the ginaReplayStartupHook function as in the replay.utils.Case example : static public void
 * ginaReplayStartupHook(ReplayTestConfiguration config) { testConfiguration = config; }
 *
 * Then the static testConfiguration
 * 
 * @author Alexandre
 *
 */
public class ReplayTestConfiguration {

	private GinaReplayManager manager;
	private File scenarioToLoad;
	private Scenario scenarioBase;
	private GinaEventFactory factory;
	private Class<?> mainClassLauncher;

	public ReplayTestConfiguration() {
		super();

		factory = new GinaEventFactory();
		factory.addModel(Scenario.class);
		factory.addModel(EventDescription.class);
		factory.addModel(GinaEvent.class);
	}

	/**
	 * Load a specified scenario to be replayed. This will search the mainClass that will be launched by executing runMake().
	 * 
	 * @param scenarioToLoad
	 */
	public void loadScenario(File scenarioToLoad) {
		this.scenarioToLoad = scenarioToLoad;

		if (scenarioToLoad != null) {
			deserializeScenario(scenarioToLoad);

			EventDescription d = ((InteractionCycle) scenarioBase.getNodes().get(0)).getUserInteraction().getDescription();
			if (d instanceof ApplicationEventDescription) {
				ApplicationEventDescription aed = (ApplicationEventDescription) d;
				try {
					mainClassLauncher = Class.forName(aed.getMainClass());
					// System.out.println("Main class : " + mainClassLauncher);
				} catch (ClassNotFoundException e) {
					System.out.println("The class '" + aed.getMainClass() + "' cannot be found in the classpath !");
					scenarioBase = null;
				}
			}
			else
				scenarioBase = null;
		}
	}

	public Class<?> getMainClassLauncher() {
		return mainClassLauncher;
	}

	/**
	 * Set the static configuration to this and run the main class of the scenario. The main class should have a main function to be
	 * launched.
	 */
	public void runMain() {
		if (mainClassLauncher == null)
			return;

		GinaReplaySession.setNextTestConfiguration(this);
		// set the hook
		/*try {
			Method hook = mainClassLauncher.getMethod("ginaReplayStartupHook", ReplayTestConfiguration.class);
			hook.invoke(null, this);
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println("The hook 'ginaReplayStartupHook' cannot be found in '" + mainClassLauncher + "' !");
			mainClassLauncher = null;
			scenarioBase = null;
			return;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("The hook 'ginaReplayStartupHook' cannot be invoked in '" + mainClassLauncher + "' !");
			mainClassLauncher = null;
			scenarioBase = null;
			return;
		}*/

		// run the main
		try {
			Method hook = mainClassLauncher.getMethod("main", String[].class);
			String[] params = null;
			hook.invoke(null, (Object) params);
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println("The main function cannot be found in '" + mainClassLauncher + "' !");
			mainClassLauncher = null;
			scenarioBase = null;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("The main function cannot be invoked in '" + mainClassLauncher + "' !");
			mainClassLauncher = null;
			scenarioBase = null;
			return;
		}
	}

	public GinaReplaySession getSession() {
		return manager.getCurrentSession();
	}

	public GinaReplayManager getManager() {
		return manager;
	}

	public void setManager(GinaReplayManager manager) {
		this.manager = manager;
	}

	public void startup(GinaReplayManager manager) {
		this.manager = manager;

		if (scenarioToLoad != null) {
			this.manager.getCurrentSession().load(scenarioToLoad);
			this.manager.getCurrentSession().launched();
		}
	}

	private void deserializeScenario(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);

			scenarioBase = (Scenario) factory.getModelFactory().deserialize(in);

		} catch (Exception e) {
			scenarioToLoad = null;
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}

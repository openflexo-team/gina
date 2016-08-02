package org.openflexo.replay.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.replay.ReplayConfiguration;
import org.openflexo.replay.test.TestParameter;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

/**
 * This manages a diana recording/replaying example. TODO some parts are functionals (check
 * /diana-api/src/main/java/org/openflexo/gina/event/description/TreeNodeEventDescription.java) It will require to import event enabled
 * version of diana to be used.
 * 
 * @author Alexandre
 *
 */
@RunWith(Parameterized.class)
public class DrawindTreeNodeCaseTest {
	static private ReplayConfiguration testConfiguration;

	public static void init() {
		testConfiguration = new ReplayConfiguration();
		File scenarioDir = ((FileResourceImpl) ResourceLocator.locateResource("scenarii/last-scenario")).getFile();
		testConfiguration.loadScenario(scenarioDir);
		testConfiguration.runMain();
	}

	@Parameters(name = "{index}: Node {0}")
	public static Collection<TestParameter[]> data() {
		init();
		LinkedList<TestParameter[]> list = new LinkedList<TestParameter[]>();

		int size = testConfiguration.getManager().getCurrentSession().getScenario().size();

		for (int i = 0; i < size; ++i) {
			TestParameter[] param = {
					new TestParameter(i, testConfiguration.getManager().getCurrentSession().getScenario().getNodes().get(i)) };
			list.add(param);
		}

		return list;
	}

	public DrawindTreeNodeCaseTest(TestParameter param) {
	}

	@Test
	public void test() {
		try {
			testConfiguration.getManager().getCurrentSession().checkNextStep(false);
		} catch (InvalidRecorderStateException e) {
			assertTrue(e.getMessage(), false);
		}

		assertTrue(true);
	}
}

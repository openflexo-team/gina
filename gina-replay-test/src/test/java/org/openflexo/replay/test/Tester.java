package org.openflexo.replay.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openflexo.gina.event.InvalidRecorderStateException;
import org.openflexo.replay.ReplayConfiguration;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

/**
 * This class will replay and check the last scenario file. It is useful to check a recorded scenario quickly.
 * 
 * @author Alexandre
 *
 */
@RunWith(Parameterized.class)
public class Tester {

	private TestParameter testParam;

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
		LinkedList<TestParameter[]> list = new LinkedList<>();

		int size = testConfiguration.getSession().getScenario().size();

		for (int i = 0; i < size; ++i) {
			TestParameter[] param = {
					new TestParameter(i, testConfiguration.getManager().getCurrentSession().getScenario().getNodes().get(i)) };
			list.add(param);
		}

		return list;
	}

	public Tester(TestParameter param) {
		testParam = param;
	}

	@Test
	public void test() {
		try {
			testConfiguration.getSession().checkNextStep(true);
		} catch (InvalidRecorderStateException e) {
			assertTrue(e.getMessage(), false);
		}

		assertTrue(true);
	}

	@BeforeClass
	public static void initGUI() {
		// simpleCase = new MultiEventsCase();
		// gcDelegate = new GraphicalContextDelegate(FIBCheckboxListWidgetTest.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		// gcDelegate.waitGUI();
	}

	/*@Before
	public void setUp() {
		gcDelegate.setUp();
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}*/
}

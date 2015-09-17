package org.openflexo.replay.test;

import static org.junit.Assert.*;

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
import org.openflexo.replay.GinaReplayManager;
import org.openflexo.replay.cases.MultiEventsCase;

@RunWith(Parameterized.class)
public class Tester {

	private TestParameter testParam;

	static private ReplayTestConfiguration testConfiguration;
	
	public static void init() {
		testConfiguration = new ReplayTestConfiguration();
		testConfiguration.loadScenario(new File("D:/test-gina-recorder-temp"));
		testConfiguration.runMain();
	}

	@Parameters
    public static Collection<TestParameter[]> data() {
    	init();
    	LinkedList<TestParameter[]> list = new LinkedList<TestParameter[]>();

		int size = testConfiguration.getManager().getCurrentReplayer().getScenario().size();

        for(int i=0; i<size; ++i)
        {
        	TestParameter[] param = {new TestParameter(i)};
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
    		testConfiguration.getManager().getCurrentReplayer().checkNextStep(false);
		} catch (InvalidRecorderStateException e) {
			assertTrue(e.getMessage(), false);
		}

    	assertTrue(true);
    }
    
    @BeforeClass
	public static void initGUI() {
    	//simpleCase = new MultiEventsCase();
		//gcDelegate = new GraphicalContextDelegate(FIBCheckboxListWidgetTest.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		//gcDelegate.waitGUI();
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

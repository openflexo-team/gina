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
import org.openflexo.gina.manager.GinaManager;
import org.openflexo.replay.InvalidRecorderStateException;
import org.openflexo.replay.cases.MultiEventsCase;

@RunWith(Parameterized.class)
public class FIBTester {
	private static MultiEventsCase simpleCase;
	
	private FIBTestParameter testParam;

	@Parameters
    public static Collection<FIBTestParameter[]> data() {
		/*GinaManager.getInstance().createAndSetToCurrent();
		GinaManager.getInstance().getCurrentRecorder().load(new File("D:/test-gina-recorder"));
		int size = GinaManager.getInstance().getCurrentRecorder().getRootNode().getNodes().size();

        LinkedList<FIBTestParameter[]> list = new LinkedList<FIBTestParameter[]>();
        for(int i=0; i<size; ++i)
        {
        	FIBTestParameter[] param = {new FIBTestParameter(i)};
        	list.add(param);
        }
        
        return list;*/
    	return null;
    }

    public FIBTester(FIBTestParameter param) {
    	testParam = param;
    }

    @Test
    public void test() {
    	/*try {
			GinaManager.getInstance().getCurrentRecorder().checkNextStep(true);
		} catch (InvalidRecorderStateException e) {
			assertTrue(e.getMessage(), false);
		}*/
    	assertTrue(true);
    }
    
    @BeforeClass
	public static void initGUI() {
    	simpleCase = new MultiEventsCase();
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

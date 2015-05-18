package org.openflexo.himtester.test;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openflexo.himtester.FIBInvalidStateException;
import org.openflexo.himtester.FIBRecorderCallbackTest;
import org.openflexo.himtester.FIBRecorderManager;
import org.openflexo.himtester.simple.SimpleExampleCase;

@RunWith(Parameterized.class)
public class FIBTester {
	private static SimpleExampleCase simpleCase;
	
	private FIBTestParameter testParam;

	@Parameters
    public static Collection<FIBTestParameter[]> data() {
		FIBRecorderManager.getInstance().createAndSetToCurrent();
		FIBRecorderManager.getInstance().getCurrentRecorder().load(new File("D:/test-gina-recorder"));
		int size = FIBRecorderManager.getInstance().getCurrentRecorder().getRootNode().getNodes().size();

        LinkedList<FIBTestParameter[]> list = new LinkedList<FIBTestParameter[]>();
        for(int i=0; i<size; ++i)
        {
        	FIBTestParameter[] param = {new FIBTestParameter(i)};
        	list.add(param);
        }
        
        return list;
    }

    public FIBTester(FIBTestParameter param) {
    	testParam = param;
    }

    @Test
    public void test() {
    	try {
			FIBRecorderManager.getInstance().getCurrentRecorder().checkNextStep(true);
		} catch (FIBInvalidStateException e) {
			assertTrue(e.getMessage(), false);
		}
    	assertTrue(true);
    }
    
    @BeforeClass
	public static void initGUI() {
    	simpleCase = new SimpleExampleCase();
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

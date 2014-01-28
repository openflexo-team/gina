package org.openflexo.fib.widget;

import java.io.File;

import org.junit.Test;
import org.openflexo.fib.utils.GenericFIBTestCase;

/**
 * Test that FIBs defined in tooling context are valid
 * 
 * @author sylvain
 * 
 */
public class TestToolingFibs extends GenericFIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib"), "Fib/"));
	}

	@Test
	public void testClassSelector() {
		validateFIB("Fib/ClassSelector.fib");
	}

	@Test
	public void testLocalizedEditor() {
		validateFIB("Fib/LocalizedEditor.fib");
	}

	@Test
	public void testLoggingViewer() {
		validateFIB("Fib/LoggingViewer.fib");
	}

}

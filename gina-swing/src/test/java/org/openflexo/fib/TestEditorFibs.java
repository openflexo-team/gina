package org.openflexo.fib;

import org.junit.Test;
import org.openflexo.fib.utils.GenericFIBTestCase;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

public class TestEditorFibs extends GenericFIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(((FileResourceImpl) ResourceLocator.locateResource("Fib")).getFile(), "Fib/"));
	}

	@Test
	public void testClassSelector() {
		validateFIB("Fib/ClassSelector.fib");
	}

	@Test
	public void testLocalizedPanel() {
		validateFIB("Fib/LocalizedPanel.fib");
	}

	@Test
	public void testLoggingViewer() {
		validateFIB("Fib/LoggingViewer.fib");
	}

	@Test
	public void testValidationPanel() {
		validateFIB("Fib/ValidationPanel.fib");
	}

}

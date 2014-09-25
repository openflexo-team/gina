package org.openflexo.fib.editor;

import org.junit.Test;
import org.openflexo.fib.utils.GenericFIBTestCase;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

public class TestEditorFibs extends GenericFIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(((FileResourceImpl) ResourceLocator.locateResource("Fib")).getFile(), "Fib/"));
	}

	@Test
	public void testBrowser() {
		validateFIB("Fib/Browser.fib");
	}

	@Test
	public void testComponentLocalization() {
		validateFIB("Fib/ComponentLocalization.fib");
	}

	@Test
	public void testComponentValidation() {
		validateFIB("Fib/ComponentValidation.fib");
	}

}

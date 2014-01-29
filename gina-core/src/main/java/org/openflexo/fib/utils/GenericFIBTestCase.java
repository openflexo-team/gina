package org.openflexo.fib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.toolbox.FileResource;

/**
 * Generic test case allowing to test a FIB component
 * 
 * @author sylvain
 * 
 */
public abstract class GenericFIBTestCase {

	static final Logger logger = Logger.getLogger(GenericFIBTestCase.class.getPackage().getName());

	public void validateFIB(String fibRelativePath) {
		validateFIB(new FileResource(fibRelativePath));
	}

	public void validateFIB(File fibFile) {
		try {
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(fibFile);
			if (component == null) {
				fail("Component not found: " + fibFile.getAbsolutePath());
			}
			ValidationReport validationReport = component.validate();
			for (ValidationError error : validationReport.getErrors()) {
				logger.severe("FIBComponent validation error: Object: " + error.getObject() + " message: " + error.getMessage());
			}
			assertEquals(0, validationReport.getErrorNb());
		} finally {
			FIBLibrary.instance().removeFIBComponentFromCache(fibFile);
		}
	}

	public static String generateFIBTestCaseClass(File directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".fib")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".fib"));
				sb.append("@Test\n");
				sb.append("public void test" + fibName + "() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
		return sb.toString();
	}

}
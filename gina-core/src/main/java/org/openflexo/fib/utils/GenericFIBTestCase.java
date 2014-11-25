package org.openflexo.fib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.model.validation.ValidationError;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * Generic test case allowing to test a FIB component
 * 
 * @author sylvain
 * 
 */
public abstract class GenericFIBTestCase {

	static final Logger logger = Logger.getLogger(GenericFIBTestCase.class.getPackage().getName());
	static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	public void validateFIB(String fibRelativePath) {
		validateFIB(ResourceLocator.locateResource(fibRelativePath));
	}

	public void validateFIB(Resource fibFile) {
		try {
			System.out.println("Validating fib file " + fibFile);
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(fibFile);
			if (component == null) {
				fail("Component not found: " + fibFile.getURI());
			}
			ValidationReport validationReport = component.validate();
			for (ValidationError error : validationReport.getErrors()) {
				logger.severe("FIBComponent validation error: Object: " + error.getValidable() + " message: "
						+ validationReport.getValidationModel().localizedIssueMessage(error) + " detais="
						+ validationReport.getValidationModel().localizedIssueDetailedInformations(error));
			}
			assertEquals(0, validationReport.getErrorsCount());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Interrupted FIB validation");
		} finally {
			FIBLibrary.instance().removeFIBComponentFromCache(fibFile);
		}
	}

	public static String generateFIBTestCaseClass(File directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				generateFIBTestCaseClass(f, relativePath + f.getName() + File.separator, sb);
			} else if (f.getName().endsWith(".fib")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".fib"));
				sb.append("@Test\n");
				sb.append("public void test" + fibName + "() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
		return sb.toString();
	}

	private static void generateFIBTestCaseClass(File directory, String relativePath, StringBuffer sb) {
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				generateFIBTestCaseClass(f, relativePath + f.getName() + File.separator);
			} else if (f.getName().endsWith(".fib")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".fib"));
				sb.append("@Test\n");
				sb.append("public void test" + fibName + "() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
	}

}

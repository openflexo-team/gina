package org.openflexo.fib.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.editor.controller.FIBInspector;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.utils.GenericFIBTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Generic test case allowing to test a FIB component used as an inspector (a .inspector file)
 * 
 * @author sylvain
 * 
 */
public abstract class FIBInspectorTestCase extends GenericFIBTestCase {

	static final Logger logger = Logger.getLogger(FIBInspectorTestCase.class.getPackage().getName());

	public static FIBModelFactory INSPECTOR_FACTORY;

	static {
		try {
			INSPECTOR_FACTORY = new FIBModelFactory(FIBInspector.class);
		} catch (ModelDefinitionException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void validateFIB(File fibFile) {
		try {
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(fibFile, false, INSPECTOR_FACTORY);
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

	public static String generateInspectorTestCaseClass(File directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".inspector")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".inspector"));
				sb.append("@Test\n");
				sb.append("public void test" + fibName + "Inspector() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
		return sb.toString();
	}

}
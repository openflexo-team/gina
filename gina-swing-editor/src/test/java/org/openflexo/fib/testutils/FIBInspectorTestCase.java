package org.openflexo.fib.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.editor.controller.FIBInspector;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.utils.GenericFIBTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.validation.ValidationError;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

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
	public void validateFIB(Resource fibFile) {
		try {
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(fibFile, false, INSPECTOR_FACTORY);
			if (component == null) {
				fail("Component not found: " + fibFile.getURI());
			}
			ValidationReport validationReport = component.validate();
			for (ValidationError error : validationReport.getErrors()) {
				logger.severe("FIBComponent validation error: Object: " + error.getObject() + " message: " + error.getMessage());
			}
			assertEquals(0, validationReport.getErrorsCount());
		} finally {
			FIBLibrary.instance().removeFIBComponentFromCache(fibFile);
		}
	}

	public static String generateInspectorTestCaseClass(Resource directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (Resource rloc : directory.getContents(Pattern.compile(".*[.]inspector"))) {
			File f = ResourceLocator.getResourceLocator().retrieveResourceAsFile(rloc);
			String fibName = f.getName().substring(0, f.getName().indexOf(".inspector"));
			sb.append("@Test\n");
			sb.append("public void test" + fibName + "Inspector() {\n");
			sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
			sb.append("}\n\n");
		}
		return sb.toString();
	}

}

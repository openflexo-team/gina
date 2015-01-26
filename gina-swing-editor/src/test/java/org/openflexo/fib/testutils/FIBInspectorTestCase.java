/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

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
				logger.severe("FIBComponent validation error: Object: " + error.getValidable() + " message: " + error.getMessage());
			}
			assertEquals(0, validationReport.getErrorsCount());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Interrupted FIB validation");
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

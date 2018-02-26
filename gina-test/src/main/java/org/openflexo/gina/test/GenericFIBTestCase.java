/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.FIBComponent;
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
		Resource resource = ResourceLocator.locateResource(fibRelativePath);
		assertNotNull("Resource " + fibRelativePath + " not found", resource);
		validateFIB(resource);
	}

	public FIBLibrary getFIBLibrary() {
		return ApplicationFIBLibraryImpl.instance();
	}

	protected void initFIBComponent(FIBComponent component) {
	}

	public void validateFIB(Resource fibFile) {
		try {
			System.out.println("Validating fib file " + fibFile);
			FIBComponent component = getFIBLibrary().retrieveFIBComponent(fibFile);
			if (component == null) {
				fail("Component not found: " + fibFile.getURI());
			}
			initFIBComponent(component);
			ValidationReport validationReport = component.validate();
			for (ValidationError<?, ?> error : validationReport.getAllErrors()) {
				logger.severe("FIBComponent validation error: Object: " + error.getValidable() + " message: "
						+ validationReport.getValidationModel().localizedIssueMessage(error) + " detais="
						+ validationReport.getValidationModel().localizedIssueDetailedInformations(error));
			}
			if (validationReport.getErrorsCount() > 0) {
				fail("Validation failed for FIB: " + fibFile);
			}
			// assertEquals(0, validationReport.getErrorsCount());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Interrupted FIB validation");
		} finally {
			ApplicationFIBLibraryImpl.instance().removeFIBComponentFromCache(fibFile);
		}
	}

	public static String generateFIBTestCaseClass(File directory, String relativePath) {
		StringBuffer sb = new StringBuffer();
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				generateFIBTestCaseClass(f, relativePath + f.getName() + File.separator, sb);
			}
			else if (f.getName().endsWith(".fib")) {
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
			}
			else if (f.getName().endsWith(".fib")) {
				String fibName = f.getName().substring(0, f.getName().indexOf(".fib"));
				sb.append("@Test\n");
				sb.append("public void test" + fibName + "() {\n");
				sb.append("  validateFIB(\"" + relativePath + f.getName() + "\");\n");
				sb.append("}\n\n");
			}
		}
	}

}

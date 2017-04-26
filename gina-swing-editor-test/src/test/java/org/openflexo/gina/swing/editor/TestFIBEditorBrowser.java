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

package org.openflexo.gina.swing.editor;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.swing.editor.widget.FIBEditorBrowser;
import org.openflexo.gina.testutils.FIBComponentGraphicalContextDelegate;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.validation.ValidationReport;

/**
 * Test the structural and behavioural features of FIBEditorBrowser
 * 
 * @author sylvain
 * 
 */
public class TestFIBEditorBrowser {

	private static FIBComponentGraphicalContextDelegate gcDelegate;

	private static FIBModelFactory factory;
	private static FIBPanel component;

	@Test
	public void test1InstanciateWidget() throws ModelDefinitionException, InterruptedException {

		factory = new FIBModelFactory(null);
		component = factory.newInstance(FIBPanel.class);

		FIBEditorBrowser browser = new FIBEditorBrowser(component, null);

		ValidationReport validationReport = browser.getFIBComponent().validate();
		assertEquals(0, validationReport.getErrorsCount());

		gcDelegate = new FIBComponentGraphicalContextDelegate(TestFIBEditorBrowser.class.getSimpleName(), FIBEditorBrowser.FIB_FILE,
				component);
		gcDelegate.addTab("FIBEditorBrowser", browser.getController());
	}

	@Test
	public void test2AddSubComponents() throws ModelDefinitionException {

		factory = new FIBModelFactory(null);
		component = factory.newInstance(FIBPanel.class);

		FIBLabel newLabel = factory.newFIBLabel("Hello world");
		component.addToSubComponents(newLabel);
	}

	@AfterClass
	public static void waitGUI() {
		if (gcDelegate != null) {
			gcDelegate.waitGUI();
		}
	}

	@After
	public void tearDown() throws Exception {
		if (gcDelegate != null) {
			gcDelegate.tearDown();
		}
	}

}

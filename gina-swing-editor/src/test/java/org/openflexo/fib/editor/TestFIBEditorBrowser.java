package org.openflexo.fib.editor;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.openflexo.fib.editor.widget.FIBEditorBrowser;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.testutils.FIBComponentGraphicalContextDelegate;
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

		factory = new FIBModelFactory();
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

		factory = new FIBModelFactory();
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

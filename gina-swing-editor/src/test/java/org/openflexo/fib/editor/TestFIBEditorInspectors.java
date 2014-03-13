package org.openflexo.fib.editor;

import org.junit.Test;
import org.openflexo.fib.testutils.FIBInspectorTestCase;
import org.openflexo.toolbox.ResourceLocator;

/**
 * Test all inspectors used in FIBEditor
 * 
 * @author sylvain
 * 
 */
public class TestFIBEditorInspectors extends FIBInspectorTestCase {

	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(ResourceLocator.getResourceLocator().locateResource("EditorInspectors"), "EditorInspectors/"));
	}

	@Test
	public void testFIBBrowserInspector() {
		validateFIB("EditorInspectors/FIBBrowser.inspector");
	}

	@Test
	public void testFIBButtonInspector() {
		validateFIB("EditorInspectors/FIBButton.inspector");
	}

	@Test
	public void testFIBCheckBoxInspector() {
		validateFIB("EditorInspectors/FIBCheckBox.inspector");
	}

	@Test
	public void testFIBCheckboxListInspector() {
		validateFIB("EditorInspectors/FIBCheckboxList.inspector");
	}

	@Test
	public void testFIBColorInspector() {
		validateFIB("EditorInspectors/FIBColor.inspector");
	}

	@Test
	public void testFIBComponentInspector() {
		validateFIB("EditorInspectors/FIBComponent.inspector");
	}

	@Test
	public void testFIBContainerInspector() {
		validateFIB("EditorInspectors/FIBContainer.inspector");
	}

	@Test
	public void testFIBCustomInspector() {
		validateFIB("EditorInspectors/FIBCustom.inspector");
	}

	@Test
	public void testFIBDropDownInspector() {
		validateFIB("EditorInspectors/FIBDropDown.inspector");
	}

	@Test
	public void testFIBEditorInspector() {
		validateFIB("EditorInspectors/FIBEditor.inspector");
	}

	@Test
	public void testFIBFileInspector() {
		validateFIB("EditorInspectors/FIBFile.inspector");
	}

	@Test
	public void testFIBFontInspector() {
		validateFIB("EditorInspectors/FIBFont.inspector");
	}

	@Test
	public void testFIBHtmlEditorInspector() {
		validateFIB("EditorInspectors/FIBHtmlEditor.inspector");
	}

	@Test
	public void testFIBImageInspector() {
		validateFIB("EditorInspectors/FIBImage.inspector");
	}

	@Test
	public void testFIBLabelInspector() {
		validateFIB("EditorInspectors/FIBLabel.inspector");
	}

	@Test
	public void testFIBListInspector() {
		validateFIB("EditorInspectors/FIBList.inspector");
	}

	@Test
	public void testFIBModelObjectInspector() {
		validateFIB("EditorInspectors/FIBModelObject.inspector");
	}

	@Test
	public void testFIBMultipleValuesInspector() {
		validateFIB("EditorInspectors/FIBMultipleValues.inspector");
	}

	@Test
	public void testFIBNumberInspector() {
		validateFIB("EditorInspectors/FIBNumber.inspector");
	}

	@Test
	public void testFIBPanelInspector() {
		validateFIB("EditorInspectors/FIBPanel.inspector");
	}

	@Test
	public void testFIBRadioButtonListInspector() {
		validateFIB("EditorInspectors/FIBRadioButtonList.inspector");
	}

	@Test
	public void testFIBReferencedComponentInspector() {
		validateFIB("EditorInspectors/FIBReferencedComponent.inspector");
	}

	@Test
	public void testFIBSplitPanelInspector() {
		validateFIB("EditorInspectors/FIBSplitPanel.inspector");
	}

	@Test
	public void testFIBTabInspector() {
		validateFIB("EditorInspectors/FIBTab.inspector");
	}

	@Test
	public void testFIBTableInspector() {
		validateFIB("EditorInspectors/FIBTable.inspector");
	}

	@Test
	public void testFIBTabPanelInspector() {
		validateFIB("EditorInspectors/FIBTabPanel.inspector");
	}

	@Test
	public void testFIBTextAreaInspector() {
		validateFIB("EditorInspectors/FIBTextArea.inspector");
	}

	@Test
	public void testFIBTextFieldInspector() {
		validateFIB("EditorInspectors/FIBTextField.inspector");
	}

	@Test
	public void testFIBWidgetInspector() {
		validateFIB("EditorInspectors/FIBWidget.inspector");
	}

}

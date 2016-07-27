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

import org.junit.Test;
import org.openflexo.gina.testutils.FIBInspectorTestCase;
import org.openflexo.rm.ResourceLocator;

/**
 * Test all inspectors used in FIBEditor
 * 
 * @author sylvain
 * 
 */
public class TestFIBEditorInspectors2 extends FIBInspectorTestCase {

	public static void main(String[] args) {
		System.out.println("Dir=" + ResourceLocator.getResourceLocator().locateResource("Inspectors"));
		System.out
				.println(generateInspectorTestCaseClass(ResourceLocator.getResourceLocator().locateResource("Inspectors"), "Inspectors/"));
	}

	@Test
	public void testFIBComponentAdvancedInspector() {
		validateFIB("Inspectors/Advanced/FIBComponent.inspector");
	}

	@Test
	public void testFIBModelObjectAdvancedInspector() {
		validateFIB("Inspectors/Advanced/FIBModelObject.inspector");
	}

	@Test
	public void testFIBWidgetAdvancedInspector() {
		validateFIB("Inspectors/Advanced/FIBWidget.inspector");
	}

	@Test
	public void testFIBBrowserBasicInspector() {
		validateFIB("Inspectors/Basic/FIBBrowser.inspector");
	}

	@Test
	public void testFIBBrowserActionBasicInspector() {
		validateFIB("Inspectors/Basic/FIBBrowserAction.inspector");
	}

	@Test
	public void testFIBBrowserElementBasicInspector() {
		validateFIB("Inspectors/Basic/FIBBrowserElement.inspector");
	}

	@Test
	public void testFIBBrowserElementChildrenBasicInspector() {
		validateFIB("Inspectors/Basic/FIBBrowserElementChildren.inspector");
	}

	@Test
	public void testFIBButtonBasicInspector() {
		validateFIB("Inspectors/Basic/FIBButton.inspector");
	}

	@Test
	public void testFIBCheckBoxBasicInspector() {
		validateFIB("Inspectors/Basic/FIBCheckBox.inspector");
	}

	@Test
	public void testFIBCheckboxListBasicInspector() {
		validateFIB("Inspectors/Basic/FIBCheckboxList.inspector");
	}

	@Test
	public void testFIBColorBasicInspector() {
		validateFIB("Inspectors/Basic/FIBColor.inspector");
	}

	@Test
	public void testFIBComponentBasicInspector() {
		validateFIB("Inspectors/Basic/FIBComponent.inspector");
	}

	@Test
	public void testFIBContinuousPolarFunctionGraphBasicInspector() {
		validateFIB("Inspectors/Basic/FIBContinuousPolarFunctionGraph.inspector");
	}

	@Test
	public void testFIBContinuousSimpleFunctionGraphBasicInspector() {
		validateFIB("Inspectors/Basic/FIBContinuousSimpleFunctionGraph.inspector");
	}

	@Test
	public void testFIBCustomBasicInspector() {
		validateFIB("Inspectors/Basic/FIBCustom.inspector");
	}

	@Test
	public void testFIBDiscretePolarFunctionGraphBasicInspector() {
		validateFIB("Inspectors/Basic/FIBDiscretePolarFunctionGraph.inspector");
	}

	@Test
	public void testFIBDiscreteSimpleFunctionGraphBasicInspector() {
		validateFIB("Inspectors/Basic/FIBDiscreteSimpleFunctionGraph.inspector");
	}

	@Test
	public void testFIBDropDownBasicInspector() {
		validateFIB("Inspectors/Basic/FIBDropDown.inspector");
	}

	@Test
	public void testFIBEditorBasicInspector() {
		validateFIB("Inspectors/Basic/FIBEditor.inspector");
	}

	@Test
	public void testFIBFileBasicInspector() {
		validateFIB("Inspectors/Basic/FIBFile.inspector");
	}

	@Test
	public void testFIBFontBasicInspector() {
		validateFIB("Inspectors/Basic/FIBFont.inspector");
	}

	@Test
	public void testFIBGraphFunctionBasicInspector() {
		validateFIB("Inspectors/Basic/FIBGraphFunction.inspector");
	}

	@Test
	public void testFIBHtmlEditorBasicInspector() {
		validateFIB("Inspectors/Basic/FIBHtmlEditor.inspector");
	}

	@Test
	public void testFIBImageBasicInspector() {
		validateFIB("Inspectors/Basic/FIBImage.inspector");
	}

	@Test
	public void testFIBLabelBasicInspector() {
		validateFIB("Inspectors/Basic/FIBLabel.inspector");
	}

	@Test
	public void testFIBListBasicInspector() {
		validateFIB("Inspectors/Basic/FIBList.inspector");
	}

	@Test
	public void testFIBMultipleValuesBasicInspector() {
		validateFIB("Inspectors/Basic/FIBMultipleValues.inspector");
	}

	@Test
	public void testFIBNumberBasicInspector() {
		validateFIB("Inspectors/Basic/FIBNumber.inspector");
	}

	@Test
	public void testFIBNumericFunctionBasicInspector() {
		validateFIB("Inspectors/Basic/FIBNumericFunction.inspector");
	}

	@Test
	public void testFIBRadioButtonListBasicInspector() {
		validateFIB("Inspectors/Basic/FIBRadioButtonList.inspector");
	}

	@Test
	public void testFIBReferencedComponentBasicInspector() {
		validateFIB("Inspectors/Basic/FIBReferencedComponent.inspector");
	}

	@Test
	public void testFIBSimpleFunctionGraphBasicInspector() {
		validateFIB("Inspectors/Basic/FIBSimpleFunctionGraph.inspector");
	}

	@Test
	public void testFIBSingleParameteredGraphBasicInspector() {
		validateFIB("Inspectors/Basic/FIBSingleParameteredGraph.inspector");
	}

	@Test
	public void testFIBTabBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTab.inspector");
	}

	@Test
	public void testFIBTableBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTable.inspector");
	}

	@Test
	public void testFIBTableActionBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTableAction.inspector");
	}

	@Test
	public void testFIBTableColumnBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTableColumn.inspector");
	}

	@Test
	public void testFIBTextAreaBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTextArea.inspector");
	}

	@Test
	public void testFIBTextFieldBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTextField.inspector");
	}

	@Test
	public void testFIBTextWidgetBasicInspector() {
		validateFIB("Inspectors/Basic/FIBTextWidget.inspector");
	}

	@Test
	public void testFIBWidgetBasicInspector() {
		validateFIB("Inspectors/Basic/FIBWidget.inspector");
	}

	@Test
	public void testFIBContainerContainerInspector() {
		validateFIB("Inspectors/Container/FIBContainer.inspector");
	}

	@Test
	public void testFIBPanelContainerInspector() {
		validateFIB("Inspectors/Container/FIBPanel.inspector");
	}

	@Test
	public void testFIBSplitPanelContainerInspector() {
		validateFIB("Inspectors/Container/FIBSplitPanel.inspector");
	}

	@Test
	public void testFIBTabPanelContainerInspector() {
		validateFIB("Inspectors/Container/FIBTabPanel.inspector");
	}

	@Test
	public void testFIBBrowserControlsInspector() {
		validateFIB("Inspectors/Controls/FIBBrowser.inspector");
	}

	@Test
	public void testFIBButtonControlsInspector() {
		validateFIB("Inspectors/Controls/FIBButton.inspector");
	}

	@Test
	public void testFIBEditorControlsInspector() {
		validateFIB("Inspectors/Controls/FIBEditor.inspector");
	}

	@Test
	public void testFIBListControlsInspector() {
		validateFIB("Inspectors/Controls/FIBList.inspector");
	}

	@Test
	public void testFIBTableControlsInspector() {
		validateFIB("Inspectors/Controls/FIBTable.inspector");
	}

	@Test
	public void testFIBTextWidgetControlsInspector() {
		validateFIB("Inspectors/Controls/FIBTextWidget.inspector");
	}

	@Test
	public void testFIBWidgetControlsInspector() {
		validateFIB("Inspectors/Controls/FIBWidget.inspector");
	}

	@Test
	public void testFIBBrowserActionDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBBrowserAction.inspector");
	}

	@Test
	public void testFIBBrowserElementDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBBrowserElement.inspector");
	}

	@Test
	public void testFIBComponentDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBComponent.inspector");
	}

	@Test
	public void testFIBContainerDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBContainer.inspector");
	}

	@Test
	public void testFIBModelObjectDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBModelObject.inspector");
	}

	@Test
	public void testFIBTableActionDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBTableAction.inspector");
	}

	@Test
	public void testFIBTableColumnDefinitionInspector() {
		validateFIB("Inspectors/Definition/FIBTableColumn.inspector");
	}

	@Test
	public void testFIBBrowserFontAndColorsInspector() {
		validateFIB("Inspectors/FontAndColors/FIBBrowser.inspector");
	}

	@Test
	public void testFIBComponentFontAndColorsInspector() {
		validateFIB("Inspectors/FontAndColors/FIBComponent.inspector");
	}

	@Test
	public void testFIBGraphFunctionFontAndColorsInspector() {
		validateFIB("Inspectors/FontAndColors/FIBGraphFunction.inspector");
	}

	@Test
	public void testFIBTableFontAndColorsInspector() {
		validateFIB("Inspectors/FontAndColors/FIBTable.inspector");
	}

	@Test
	public void testFIBTableColumnFontAndColorsInspector() {
		validateFIB("Inspectors/FontAndColors/FIBTableColumn.inspector");
	}

	@Test
	public void testFIBComponentLayoutInspector() {
		validateFIB("Inspectors/Layout/FIBComponent.inspector");
	}

	@Test
	public void testFIBGraphLayoutInspector() {
		validateFIB("Inspectors/Layout/FIBGraph.inspector");
	}

	@Test
	public void testFIBPanelLayoutInspector() {
		validateFIB("Inspectors/Layout/FIBPanel.inspector");
	}

}

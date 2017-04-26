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

package org.openflexo.gina.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test append() feature in FIBContainer
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestInspector extends FIBTestCase {

	public static InspectorGroup inspectorGroup;

	@Test
	@TestOrder(1)
	public void testInstantiateInspectorGroup() {
		inspectorGroup = new InspectorGroup(ResourceLocator.locateResource("TestInspector"), ApplicationFIBLibraryImpl.instance(), null);
		assertNotNull(inspectorGroup);
	}

	@Test
	@TestOrder(2)
	public void testFIBComponent() {
		FIBInspector componentInspector = inspectorGroup.inspectorForClass(FIBComponent.class);
		System.out.println(componentInspector.getXMLRepresentation());
	}

	@Test
	@TestOrder(3)
	public void testFIBWidget() {
		FIBInspector widgetInspector = inspectorGroup.inspectorForClass(FIBWidget.class);
		System.out.println(widgetInspector.getXMLRepresentation());
	}

	@Test
	@TestOrder(4)
	public void testFIBTextAreaWidget() {
		FIBInspector textAreaInspector = inspectorGroup.inspectorForClass(FIBTextArea.class);
		System.out.println(textAreaInspector.getXMLRepresentation());
	}

	@Test
	@TestOrder(5)
	public void testFIBTextFieldWidget() {
		FIBInspector textFieldInspector = inspectorGroup.inspectorForClass(FIBTextField.class);
		System.out.println(textFieldInspector.getXMLRepresentation());
	}

	@Test
	@TestOrder(6)
	public void testFIBTextFieldWidgetInspectors() {
		List<FIBInspector> inspectors = inspectorGroup.inspectorsForClass(FIBTextField.class);
		System.out.println("inspectors=" + inspectors);
		assertEquals(3, inspectors.size());
		assertTrue(inspectors.contains(inspectorGroup.inspectorForClass(FIBComponent.class)));
		assertTrue(inspectors.contains(inspectorGroup.inspectorForClass(FIBWidget.class)));
		assertTrue(inspectors.contains(inspectorGroup.inspectorForClass(FIBTextField.class)));
	}
}

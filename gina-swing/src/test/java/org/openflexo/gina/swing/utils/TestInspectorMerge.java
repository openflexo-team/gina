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

package org.openflexo.gina.swing.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.gina.utils.FIBInspector;
import org.openflexo.gina.utils.InspectorGroup;
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
public class TestInspectorMerge extends FIBTestCase {

	public static InspectorGroup inspectorGroup;

	@Test
	@TestOrder(1)
	public void testInstantiateInspectorGroup() {
		inspectorGroup = new InspectorGroup(ResourceLocator.locateResource("TestInspectorMerge"), ApplicationFIBLibraryImpl.instance(),
				null);
		assertNotNull(inspectorGroup);
	}

	@Test
	@TestOrder(2)
	public void testFIBWidget() {
		FIBInspector widgetInspector = inspectorGroup.inspectorForClass(FIBWidget.class);
		System.out.println(widgetInspector.getXMLRepresentation());
		assertEquals(widgetInspector.getDataClass(), FIBWidget.class);

		FIBTextField tooltipTextTextField = null;
		for (FIBComponent c : widgetInspector.getAllSubComponents()) {
			if (c.getName().equals("TooltipTextTextField")) {
				tooltipTextTextField = (FIBTextField) c;
			}
		}
		assertNotNull(tooltipTextTextField);
		DataBinding<?> db = tooltipTextTextField.getData();
		FIBComponent rootComponent = ((FIBComponent) db.getOwner()).getRootComponent();

		// assertEquals(db.getOwner(),)
		System.out.println("rootComponent=" + rootComponent);
		assertEquals(rootComponent, widgetInspector);
	}

	@Test
	@TestOrder(4)
	public void testFIBTextFieldWidget() {
		FIBInspector textFieldInspector = inspectorGroup.inspectorForClass(FIBTextField.class);
		System.out.println(textFieldInspector.getXMLRepresentation());

		assertEquals(textFieldInspector.getDataClass(), FIBTextField.class);

		FIBTextField tooltipTextTextField = null;
		for (FIBComponent c : textFieldInspector.getAllSubComponents()) {
			if (c.getName().equals("TooltipTextTextField")) {
				tooltipTextTextField = (FIBTextField) c;
			}
		}
		assertNotNull(tooltipTextTextField);
		DataBinding<?> db = tooltipTextTextField.getData();
		FIBComponent rootComponent = ((FIBComponent) db.getOwner()).getRootComponent();

		// assertEquals(db.getOwner(),)
		System.out.println("rootComponent=" + rootComponent);
		assertEquals(rootComponent, textFieldInspector);
	}

	@Test
	@TestOrder(5)
	public void testFIBTextFieldWidgetInspectors() {
		List<FIBInspector> inspectors = inspectorGroup.inspectorsForClass(FIBTextField.class);
		System.out.println("inspectors=" + inspectors);
		assertEquals(2, inspectors.size());
		assertTrue(inspectors.contains(inspectorGroup.inspectorForClass(FIBWidget.class)));
		assertTrue(inspectors.contains(inspectorGroup.inspectorForClass(FIBTextField.class)));
	}
}

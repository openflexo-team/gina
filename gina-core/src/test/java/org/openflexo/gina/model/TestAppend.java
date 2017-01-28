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

package org.openflexo.gina.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.test.FIBTestCase;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test append() feature in FIBContainer
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestAppend extends FIBTestCase {

	public static FIBPanel c1, c2, cloneOfC2;

	@Test
	@TestOrder(1)
	public void testInstantiateC1() {

		log("testInstantiateC1()");

		c1 = newFIBPanel();
		c1.setName("C1");

		FIBTabPanel tabPanel1 = newFIBTabPanel();
		tabPanel1.setName("TabPanel");
		c1.addToSubComponents(tabPanel1);

		FIBTab tabA1 = newFIBTab();
		tabA1.setName("TabA");
		tabPanel1.addToSubComponents(tabA1);

		FIBLabel label1 = newFIBLabel("label1");
		tabA1.addToSubComponents(label1);

		FIBTab tabB1 = newFIBTab();
		tabB1.setName("TabB");
		tabPanel1.addToSubComponents(tabB1);

		FIBTab tabC1 = newFIBTab();
		tabC1.setName("TabC");
		tabPanel1.addToSubComponents(tabC1);

		FIBLabel label3 = newFIBLabel("label3");
		tabC1.addToSubComponents(label3);

		System.out.println("Component c1:\n");
		System.out.println(factory.stringRepresentation(c1));

		assertTrue(c1.checkContainmentIntegrity());
	}

	@Test
	@TestOrder(2)
	public void testInstantiateC2() {

		log("testInstantiateC2()");

		c2 = newFIBPanel();
		c2.setName("C2");

		FIBTabPanel tabPanel2 = newFIBTabPanel();
		tabPanel2.setName("TabPanel");
		c2.addToSubComponents(tabPanel2);

		FIBTab tabA2 = newFIBTab();
		tabA2.setName("TabA");
		tabPanel2.addToSubComponents(tabA2);

		FIBTab tabB2 = newFIBTab();
		tabB2.setName("TabB");
		tabPanel2.addToSubComponents(tabB2);

		FIBLabel label2 = newFIBLabel("label2");
		tabB2.addToSubComponents(label2);

		FIBTab tabC2 = newFIBTab();
		tabC2.setName("TabC");
		tabPanel2.addToSubComponents(tabC2);

		FIBLabel label4 = newFIBLabel("label4");
		tabC2.addToSubComponents(label4);

		System.out.println("\nComponent c2:\n");
		System.out.println(factory.stringRepresentation(c2));

		assertTrue(c2.checkContainmentIntegrity());
	}

	@Test
	@TestOrder(3)
	public void testClone() {

		log("testClone()");

		cloneOfC2 = (FIBPanel) c2.cloneObject();
		cloneOfC2.setName("cloneOfC2");

		FIBTabPanel tabPanel2 = (FIBTabPanel) c2.getSubComponents().get(0);
		FIBTabPanel clonedTabPanel2 = (FIBTabPanel) cloneOfC2.getSubComponents().get(0);

		assertNotSame(tabPanel2, clonedTabPanel2);

		assertTrue(cloneOfC2.checkContainmentIntegrity());

	}

	@Test
	@TestOrder(3)
	public void testMerge() {

		log("testMerge()");

		c1.append(cloneOfC2);

		FIBTabPanel tabPanel = (FIBTabPanel) c1.getSubComponentNamed("TabPanel");
		assertNotNull(tabPanel);
		FIBTab tabA = (FIBTab) tabPanel.getSubComponentNamed("TabA");
		assertNotNull(tabA);
		FIBTab tabB = (FIBTab) tabPanel.getSubComponentNamed("TabB");
		assertNotNull(tabB);
		FIBTab tabC = (FIBTab) tabPanel.getSubComponentNamed("TabC");
		assertNotNull(tabC);

		assertEquals(1, tabA.getSubComponents().size());
		assertEquals(1, tabB.getSubComponents().size());
		assertEquals(2, tabC.getSubComponents().size());

		assertTrue(c1.checkContainmentIntegrity());
		assertTrue(c2.checkContainmentIntegrity());

	}
}

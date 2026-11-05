/**
 * 
 * Copyright (c) 2026, Openflexo
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.junit.Test;

/**
 * {@link FlexoCollabsiblePanelGroup#insertContentsAtIndex(String, JComponent, int)} inserts a panel, in an empty group too,
 * and keeps the panels already there
 * 
 * @author sylvain
 * 
 */
public class TestFlexoCollabsiblePanelGroup {

	@Test
	public void testInsertInEmptyGroup() {
		FlexoCollabsiblePanelGroup group = new FlexoCollabsiblePanelGroup();
		FlexoCollabsiblePanel inserted = group.insertContentsAtIndex("Inserted", new JLabel("inserted"), 0);
		assertEquals(1, ((JComponent) group.getViewport().getView()).getComponentCount());
		assertSame(inserted, ((JComponent) group.getViewport().getView()).getComponent(0));
		assertFalse(inserted.isCollapsed());
	}

	@Test
	public void testInsertKeepsOtherPanels() {
		FlexoCollabsiblePanelGroup group = new FlexoCollabsiblePanelGroup();
		group.addContents("First", new JLabel("first"));
		group.addContents("Second", new JLabel("second"));
		FlexoCollabsiblePanel inserted = group.insertContentsAtIndex("Inserted", new JLabel("inserted"), 0);
		JComponent mainPane = (JComponent) group.getViewport().getView();
		assertEquals(3, mainPane.getComponentCount());
		assertSame(inserted, mainPane.getComponent(0));
		FlexoCollabsiblePanel first = (FlexoCollabsiblePanel) mainPane.getComponent(1);
		FlexoCollabsiblePanel second = (FlexoCollabsiblePanel) mainPane.getComponent(2);
		assertEquals("First", first.getTitle());
		// Each panel is still managed by the group: opening one collapses the others
		assertTrue(first.isCollapsed() && second.isCollapsed());
		group.setOpenedPanel(2);
		assertSame(second, mainPane.getComponent(2));
		assertFalse(second.isCollapsed());
		assertTrue(inserted.isCollapsed() && first.isCollapsed());
		group.setOpenedPanel(1);
		assertFalse(first.isCollapsed());
		assertTrue(inserted.isCollapsed() && second.isCollapsed());
	}

}

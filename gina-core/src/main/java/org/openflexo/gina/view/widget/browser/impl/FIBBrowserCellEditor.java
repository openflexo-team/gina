/**
 * 
 */
/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.gina.view.widget.browser.impl;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;

import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;

public class FIBBrowserCellEditor extends DefaultTreeCellEditor {

	public FIBBrowserCellEditor(JTree tree, FIBBrowserCellRenderer renderer) {
		super(tree, renderer);
	}

	@Override
	public Component getTreeCellEditorComponent(JTree arg0, Object element, boolean _selected, boolean expanded, boolean leaf, int row) {
		String editingName = ((BrowserCell) element).getBrowserElementType().getEditableLabelFor(
				((BrowserCell) element).getRepresentedObject());
		Icon customIcon = getRenderer().getIcon(((BrowserCell) element).getRepresentedObject());
		if (customIcon != null) {
			getRenderer().setClosedIcon(customIcon);
			getRenderer().setOpenIcon(customIcon);
			getRenderer().setLeafIcon(customIcon);
		}
		Component returned = super.getTreeCellEditorComponent(arg0, editingName, _selected, expanded, leaf, row);
		return returned;
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		if (super.isCellEditable(event)) {
			if (event != null) {
				if (event.getSource() instanceof JTree) {
					setTree((JTree) event.getSource());
					if (event instanceof MouseEvent) {
						TreePath path = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
						if (path != null) {
							BrowserCell cell = (BrowserCell) path.getLastPathComponent();
							return cell.getBrowserElementType().isLabelEditable();
						}
					}
				}
			}
		}
		return false;
	}

	protected FIBBrowserCellRenderer getRenderer() {
		return (FIBBrowserCellRenderer) renderer;
	}

}

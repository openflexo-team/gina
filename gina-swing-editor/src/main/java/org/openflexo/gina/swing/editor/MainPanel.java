/**
 * 
 * Copyright (c) 2013-2016, Openflexo
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

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorController.FIBEditorPanel;

/**
 * A {@link JTabbedPane} that represents all {@link EditedFIBComponent} of an editor<br>
 * This swing component might be used or not for a {@link FIBEditor}
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public class MainPanel extends JTabbedPane implements ChangeListener {

	private FIBEditor editor;

	public MainPanel(FIBEditor editor) {
		super();
		this.editor = editor;
		addChangeListener(this);
	}

	public FIBEditorController getFocusedEditedComponent() {
		int index = getSelectedIndex();
		Component componentAtSelectedIndex = getComponentAt(index);
		if (componentAtSelectedIndex instanceof FIBEditorPanel) {
			return ((FIBEditorPanel) componentAtSelectedIndex).getEditorController();
		}
		return null;
	}

	public void newEditedComponent(FIBEditorController controller) {
		add(controller.getEditorPanel(), controller.getEditedComponent().getName());
		revalidate();
		setSelectedIndex(getComponentCount() - 1);
	}

	public void focusOnEditedComponent(FIBEditorController controller) {
		int newIndex = indexOfEditorController(controller);
		if (newIndex != getSelectedIndex()) {
			System.out.println("Changing for new index: " + newIndex);
			setSelectedIndex(newIndex);
		}
	}

	private int indexOfEditorController(FIBEditorController controller) {
		for (int i = 0; i < getComponentCount(); i++) {
			Component componentAtSelectedIndex = getComponentAt(i);
			if (componentAtSelectedIndex instanceof FIBEditorPanel) {
				if (((FIBEditorPanel) componentAtSelectedIndex).getEditorController() == controller) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		FIBEditor.logger.info("Change for " + e);
		editor.activate(getFocusedEditedComponent());
	}
}

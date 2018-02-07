/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.gina.swing.editor.controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.controller.action.CopyAction;
import org.openflexo.gina.swing.editor.controller.action.CutAction;
import org.openflexo.gina.swing.editor.controller.action.DeleteAction;
import org.openflexo.gina.swing.editor.controller.action.EditorAction;
import org.openflexo.gina.swing.editor.controller.action.InspectAction;
import org.openflexo.gina.swing.editor.controller.action.MakeReusableComponent;
import org.openflexo.gina.swing.editor.controller.action.OpenComponent;
import org.openflexo.gina.swing.editor.controller.action.PasteAction;
import org.openflexo.gina.swing.editor.controller.action.WrapWithPanelAction;
import org.openflexo.gina.swing.editor.controller.action.WrapWithSplitPanelAction;
import org.openflexo.logging.FlexoLogger;

public class ContextualMenu {
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(ContextualMenu.class.getPackage().getName());

	private FIBEditorController editorController;
	private Hashtable<EditorAction, PopupMenuItem> actions;
	private JPopupMenu menu;
	private JFrame frame;

	public ContextualMenu(FIBEditorController anEditorController, final JFrame frame) {
		this.editorController = anEditorController;
		this.frame = frame;
		actions = new Hashtable<>();
		menu = new JPopupMenu();

		addToActions(new InspectAction(editorController));
		menu.addSeparator();

		addToActions(new CopyAction(editorController));
		addToActions(new CutAction(editorController));
		addToActions(new PasteAction(editorController));
		menu.addSeparator();

		// addToActions(new UndoAction(editorController));
		// addToActions(new RedoAction(editorController));
		// menu.addSeparator();

		JMenu wrapWith = new JMenu(FIBEditor.EDITOR_LOCALIZATION.localizedForKey("wrap_with"));
		menu.add(wrapWith);
		addToActions(new WrapWithPanelAction(editorController), wrapWith);
		addToActions(new WrapWithSplitPanelAction(editorController), wrapWith);
		JMenu moveTo = new JMenu(FIBEditor.EDITOR_LOCALIZATION.localizedForKey("move_to"));
		menu.add(moveTo);
		addToActions(new MakeReusableComponent(editorController, frame));
		addToActions(new OpenComponent(editorController));

		menu.addSeparator();

		addToActions(new DeleteAction(editorController, frame));
	}

	public void addToActions(EditorAction action) {
		addToActions(action, null);
	}

	@SuppressWarnings("serial")
	public void addToActions(EditorAction action, JMenu subMenu) {
		PopupMenuItem newMenuItem = new PopupMenuItem(action);
		if (action.getShortcut() != null) {
			newMenuItem.setAccelerator(action.getShortcut());
			registerActionForKeyStroke(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (action.isEnabledFor(editorController.getSelectedObject())) {
						// System.out.println("Execute action " + action + " for object " + editorController.getSelectedObject());
						action.performAction(editorController.getSelectedObject());
					}
				}
			}, action.getShortcut(), action.getActionName());
		}
		if (subMenu != null) {
			subMenu.add(newMenuItem);
		}
		else {
			menu.add(newMenuItem);
		}

		actions.put(action, newMenuItem);
	}

	public void displayPopupMenu(FIBModelObject object, Component invoker, Point p) {
		if (menu == null) {
			return;
		}
		for (EditorAction action : actions.keySet()) {
			PopupMenuItem menuItem = actions.get(action);
			menuItem.setObject(object);
		}
		if (p != null) {
			menu.show(invoker, p.x, p.y);
		}
	}

	@SuppressWarnings("serial")
	class PopupMenuItem extends JMenuItem {
		private FIBModelObject object;
		private final EditorAction action;

		public PopupMenuItem(EditorAction anAction) {
			super(anAction.getActionName(), anAction.getActionIcon());
			this.action = anAction;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FIBModelObject selectThis = action.performAction(object);
					if (selectThis instanceof FIBComponent) {
						editorController.setSelectedObject(selectThis);
					}
				}
			});
		}

		public FIBModelObject getObject() {
			return object;
		}

		public void setObject(FIBModelObject object) {
			this.object = object;
			setEnabled(action.isEnabledFor(object));
			setVisible(action.isVisibleFor(object));
		}

	}

	public void registerActionForKeyStroke(AbstractAction action, KeyStroke accelerator, String actionName) {
		String key = actionName;
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, key);
		frame.getRootPane().getActionMap().put(key, action);
		if (accelerator.getKeyCode() == FIBEditor.DELETE_KEY_CODE) {
			int keyCode = FIBEditor.BACKSPACE_DELETE_KEY_CODE;
			frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(keyCode, accelerator.getModifiers()), key);
		}
	}

}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-technologyadapter-ui, a component of the software infrastructure 
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

package org.openflexo.gina.swing.test;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettesDialog;
import org.openflexo.gina.swing.utils.JFIBDialogInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.toolbox.ToolBox;

public class TestFIBEditor {

	private static FIBEditor instance;
	private static JFrame frame;
	private static JSplitPane splitPane;

	public static FIBEditor instanciateFIBEdition(String title, Resource fibResource, Object data) {
		FIBEditor editor = getInstance();
		Resource fib = ResourceLocator.locateSourceCodeResource(fibResource);
		// Unused FIBEditorController controller =
		editor.loadFIB(fib, data, frame);
		return editor;
	}

	public static FIBEditor getInstance() {
		if (instance == null) {
			instance = makeInstance();
		}
		return instance;
	}

	private static FIBEditor makeInstance() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			if (ToolBox.isMacOS()) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FIBEditor");
			}

			try {
				FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
				FlexoLocalization.initWith(FIBEditor.EDITOR_LOCALIZATION);
			} catch (SecurityException e) {
				e.printStackTrace();
			}

			final FIBEditor editor = new FIBEditor(FIBLibraryImpl.createInstance(null)) {
				@Override
				public boolean activate(FIBEditorController editorController) {
					if (super.activate(editorController)) {
						splitPane.setLeftComponent(editorController.getEditorBrowser());
						splitPane.revalidate();
						splitPane.repaint();
						return true;
					}
					return false;
				}

				@Override
				public boolean disactivate(FIBEditorController editorController) {
					if (super.disactivate(editorController)) {
						splitPane.setLeftComponent(null);
						splitPane.revalidate();
						splitPane.repaint();
						return true;
					}
					return false;
				}
			};

			frame = new JFrame();

			frame.setBounds(JFIBPreferences.getFrameBounds());

			new ComponentBoundSaver(frame) {

				@Override
				public void saveBounds(Rectangle bounds) {
					JFIBPreferences.setFrameBounds(bounds);
				}
			};

			frame.setTitle("Flexo Interface Builder Editor");

			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					editor.quit();
				}
			});

			editor.makeMenuBar(frame);

			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, editor.makeMainPanel());

			frame.getContentPane().add(splitPane);
			frame.validate();
			frame.setVisible(true);

			JFIBDialogInspectorController inspector = editor.makeInspector(frame);
			inspector.setVisible(true);

			FIBEditorPalettesDialog paletteDialog = editor.makePaletteDialog(frame);
			paletteDialog.setVisible(true);

			return editor;

		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		return null;
	}

}

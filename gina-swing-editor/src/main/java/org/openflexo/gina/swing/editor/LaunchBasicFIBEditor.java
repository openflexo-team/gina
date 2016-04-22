/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.controller.FIBEditorPalette;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.toolbox.ToolBox;

public class LaunchBasicFIBEditor {

	private static final Logger logger = FlexoLogger.getLogger(LaunchBasicFIBEditor.class.getPackage().getName());

	private static JSplitPane splitPane;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				init();
			}
		});
	}

	private static void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			if (ToolBox.isMacOS()) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FIBEditor");
			}

			try {
				FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
				FlexoLocalization.initWith(FIBEditor.LOCALIZATION);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final FIBEditor editor = new FIBEditor(FIBLibraryImpl.createInstance()) {
				@Override
				public void activate(EditedFIBComponent editedFIB, FIBEditorController controller) {
					splitPane.setLeftComponent(controller.getEditorBrowser());
					splitPane.revalidate();
				}

				@Override
				public void disactivate(EditedFIBComponent editedFIB, FIBEditorController controller) {
					splitPane.setLeftComponent(null);
					splitPane.revalidate();
				}
			};

			JFrame frame = new JFrame();

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

			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, editor.getMainPanel());

			frame.getContentPane().add(splitPane);
			frame.validate();
			frame.setVisible(true);

			JFIBInspectorController inspector = editor.makeInspector(frame);
			inspector.setVisible(true);

			FIBEditorPalette palette = editor.makePalette(frame);
			palette.setVisible(true);

		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}

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

package org.openflexo.gina.testutils;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.LaunchAdvancedFIBEditor;
import org.openflexo.gina.swing.editor.MainPanel;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.inspector.FIBEditorInspectorController;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.widget.FIBLibraryBrowser;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.JXMultiSplitPane.DividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Split;
import org.openflexo.toolbox.ToolBox;

public class GinaSwingEditorTestCase extends LaunchAdvancedFIBEditor {

	protected static SwingGraphicalContextDelegate gcDelegate;

	private static JXMultiSplitPane centerPanel;
	// private static JSplitPane splitPane;

	@BeforeClass
	public static void setupClass()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

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

		initGUI();
	}

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(GinaSwingEditorTestCase.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

	public FIBEditor instanciateFIBEdition(String title, Resource fibResource, Object data) {

		if (fibResource == null) {
			// Prevent NPE later
			System.err.println("Cannot find FIB " + title);
			return null;
		}

		final FIBEditor editor = new FIBEditor(FIBLibraryImpl.createInstance(null)) {
			@Override
			public boolean activate(FIBEditorController editorController) {
				if (super.activate(editorController)) {
					centerPanel.add(editorController.getEditorBrowser(), LayoutPosition.BOTTOM_LEFT.name());
					centerPanel.revalidate();
					centerPanel.repaint();
					return true;
				}
				return false;
			}

			@Override
			public boolean disactivate(FIBEditorController editorController) {
				if (super.disactivate(editorController)) {
					centerPanel.remove(editorController.getEditorBrowser());
					centerPanel.revalidate();
					centerPanel.repaint();
					return true;
				}
				return false;
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

		Split<?> defaultLayout = getDefaultLayout();

		MultiSplitLayout centerLayout = new MultiSplitLayout(true, MSL_FACTORY);
		centerLayout.setLayoutMode(MultiSplitLayout.NO_MIN_SIZE_LAYOUT);
		centerLayout.setModel(defaultLayout);

		centerPanel = new JXMultiSplitPane(centerLayout);
		centerPanel.setDividerSize(DIVIDER_SIZE);
		centerPanel.setDividerPainter(new DividerPainter() {

			@Override
			protected void doPaint(Graphics2D g, Divider divider, int width, int height) {
				if (!divider.isVisible()) {
					return;
				}
				if (divider.isVertical()) {
					int x = (width - KNOB_SIZE) / 2;
					int y = (height - DIVIDER_KNOB_SIZE) / 2;
					for (int i = 0; i < 3; i++) {
						Graphics2D graph = (Graphics2D) g.create(x, y + i * (KNOB_SIZE + KNOB_SPACE), KNOB_SIZE + 1, KNOB_SIZE + 1);
						graph.setPaint(KNOB_PAINTER);
						graph.fillOval(0, 0, KNOB_SIZE, KNOB_SIZE);
					}
				}
				else {
					int x = (width - DIVIDER_KNOB_SIZE) / 2;
					int y = (height - KNOB_SIZE) / 2;
					for (int i = 0; i < 3; i++) {
						Graphics2D graph = (Graphics2D) g.create(x + i * (KNOB_SIZE + KNOB_SPACE), y, KNOB_SIZE + 1, KNOB_SIZE + 1);
						graph.setPaint(KNOB_PAINTER);
						graph.fillOval(0, 0, KNOB_SIZE, KNOB_SIZE);
					}
				}

			}
		});

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

		FIBLibraryBrowser libraryBrowser = new FIBLibraryBrowser(editor.getFIBLibrary());
		FIBEditorPalettes palette = editor.makePalette();
		FIBEditorInspectorController inspectors = editor.makeInspectors();

		// JFIBDialogInspectorController inspector = editor.makeInspector(frame);
		// inspector.setVisible(true);

		MainPanel mainPanel = editor.makeMainPanel();

		centerPanel.add(libraryBrowser, LayoutPosition.TOP_LEFT.name());
		centerPanel.add(mainPanel, LayoutPosition.CENTER.name());
		centerPanel.add(palette, LayoutPosition.TOP_RIGHT.name());
		centerPanel.add(inspectors.getPanelGroup(), LayoutPosition.BOTTOM_RIGHT.name());

		// frame.validate();
		// frame.setVisible(true);

		// gcDelegate.getFrame().setTitle("Flexo Interface Builder Editor");

		/*splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, editor.makeMainPanel());
		
		JFIBDialogInspectorController inspector = editor.makeInspector(gcDelegate.getFrame());
		inspector.setVisible(true);
		
		FIBEditorPalettesDialog paletteDialog = editor.makePaletteDialog(gcDelegate.getFrame());
		paletteDialog.setVisible(true);*/

		Resource fib = ResourceLocator.locateSourceCodeResource(fibResource);

		// Unused FIBEditorController controller =
		editor.loadFIB(fib, data, gcDelegate.getFrame());

		gcDelegate.addTab(title, centerPanel);

		return editor;
	}

}

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.palette.FIBEditorPalettes;
import org.openflexo.gina.swing.editor.widget.FIBLibraryBrowser;
import org.openflexo.gina.swing.utils.JFIBInspectorController;
import org.openflexo.gina.swing.utils.JFIBPreferences;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.JXMultiSplitPane.DividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.Split;
import org.openflexo.swing.layout.MultiSplitLayoutFactory;
import org.openflexo.toolbox.ToolBox;

public class LaunchAdvancedFIBEditor {

	private static final Logger logger = FlexoLogger.getLogger(LaunchAdvancedFIBEditor.class.getPackage().getName());

	private static final MultiSplitLayoutFactory MSL_FACTORY = new MultiSplitLayoutFactory.DefaultMultiSplitLayoutFactory();

	private static JXMultiSplitPane centerPanel;

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
				FlexoLocalization.initWith(FIBEditor.EDITOR_LOCALIZATION);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final FIBEditor editor = new FIBEditor(FIBLibraryImpl.createInstance()) {
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

			Split defaultLayout = getDefaultLayout();

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
			// FIBInspectors inspectors = editor.makeInspectors();

			JFIBInspectorController inspector = editor.makeInspector(frame);
			inspector.setVisible(true);

			MainPanel mainPanel = editor.makeMainPanel();

			centerPanel.add(libraryBrowser, LayoutPosition.TOP_LEFT.name());
			centerPanel.add(mainPanel, LayoutPosition.CENTER.name());
			centerPanel.add(palette, LayoutPosition.TOP_RIGHT.name());
			// centerPanel.add(inspectors.getPanelGroup(), LayoutPosition.BOTTOM_RIGHT.name());

			frame.validate();
			frame.setVisible(true);

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

	public static enum LayoutPosition {
		TOP_LEFT, BOTTOM_LEFT, CENTER, TOP_RIGHT, BOTTOM_RIGHT;
	}

	public static enum LayoutColumns {
		LEFT, CENTER, RIGHT;
	}

	private static final int KNOB_SIZE = 5;
	private static final int KNOB_SPACE = 2;
	private static final int DIVIDER_SIZE = KNOB_SIZE + 2 * KNOB_SPACE;
	private static final int DIVIDER_KNOB_SIZE = 3 * KNOB_SIZE + 2 * KNOB_SPACE;

	private static final Paint KNOB_PAINTER = new RadialGradientPaint(new Point((KNOB_SIZE - 1) / 2, (KNOB_SIZE - 1) / 2),
			(KNOB_SIZE - 1) / 2, new float[] { 0.0f, 1.0f }, new Color[] { Color.GRAY, Color.LIGHT_GRAY });

	private static Split getDefaultLayout() {
		Split root = MSL_FACTORY.makeSplit();
		root.setName("ROOT");
		Split left = getVerticalSplit(LayoutPosition.TOP_LEFT, 0.5, LayoutPosition.BOTTOM_LEFT, 0.5);
		left.setWeight(0.2);
		left.setName(LayoutColumns.LEFT.name());
		Node center = MSL_FACTORY.makeLeaf(LayoutPosition.CENTER.name());
		center.setWeight(0.6);
		center.setName(LayoutColumns.CENTER.name());
		Split right = getVerticalSplit(LayoutPosition.TOP_RIGHT, 0.4, LayoutPosition.BOTTOM_RIGHT, 0.6);
		right.setWeight(0.2);
		right.setName(LayoutColumns.RIGHT.name());
		root.setChildren(left, MSL_FACTORY.makeDivider(), center, MSL_FACTORY.makeDivider(), right);
		return root;
	}

	private static Split getVerticalSplit(LayoutPosition position1, double weight1, LayoutPosition position2, double weight2) {
		Split split = MSL_FACTORY.makeSplit();
		split.setRowLayout(false);
		Leaf l1 = MSL_FACTORY.makeLeaf(position1.name());
		l1.setWeight(weight1);
		Leaf l2 = MSL_FACTORY.makeLeaf(position2.name());
		l2.setWeight(weight2);
		split.setChildren(l1, MSL_FACTORY.makeDivider(), l2);
		return split;
	}

	private static Split getVerticalSplit(LayoutPosition position1, LayoutPosition position2, LayoutPosition position3) {
		Split split = MSL_FACTORY.makeSplit();
		split.setRowLayout(false);
		Leaf l1 = MSL_FACTORY.makeLeaf(position1.name());
		l1.setWeight(0.2);
		Leaf l2 = MSL_FACTORY.makeLeaf(position2.name());
		l2.setWeight(0.6);
		Leaf l3 = MSL_FACTORY.makeLeaf(position3.name());
		l3.setWeight(0.2);
		split.setChildren(l1, MSL_FACTORY.makeDivider(), l2, MSL_FACTORY.makeDivider(), l3);
		return split;
	}

}

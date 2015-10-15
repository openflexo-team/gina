/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.replay.cases;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.GraphDrawing1;
import org.openflexo.fge.TestGraph;
import org.openflexo.fge.TestGraphNode;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.JDianaInteractiveViewer;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.fib.swing.logging.FlexoLoggingViewer;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.Window;

public class DrawindTreeNodeCase extends Case {

	private static final Logger LOGGER = FlexoLogger.getLogger(DrawindTreeNodeCase.class.getPackage().getName());
	
	public static void main(String[] args) {
		initCase(new DrawindTreeNodeCase());
	}

	/*public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		showPanel();
	}*/

	public static class TestDrawingController extends JDianaInteractiveEditor<TestGraph> {
		private final JPopupMenu contextualMenu;
		private final JDianaScaleSelector scaleSelector;

		public TestDrawingController(GraphDrawing1 aDrawing) {
			super(aDrawing, aDrawing.getFactory(), SwingViewFactory.INSTANCE, SwingToolFactory.DEFAULT);
			scaleSelector = (JDianaScaleSelector) getToolFactory().makeDianaScaleSelector(this);
			contextualMenu = new JPopupMenu();
			contextualMenu.add(new JMenuItem("Item"));
		}

		/*@Override
		public void addToSelectedObjects(DrawingTreeNode<?,?> anObject) {
			super.addToSelectedObjects(anObject);
			if (getSelectedObjects().size() == 1) {
				setChanged();
				notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
			} else {
				setChanged();
				notifyObservers(new MultipleSelection());
			}
		}

		@Override
		public void removeFromSelectedObjects(DrawingTreeNode<?,?> anObject) {
			super.removeFromSelectedObjects(anObject);
			if (getSelectedObjects().size() == 1) {
				setChanged();
				notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
			} else {
				setChanged();
				notifyObservers(new MultipleSelection());
			}
		}

		@Override
		public void clearSelection() {
			super.clearSelection();
			notifyObservers(new EmptySelection());
		}

		@Override
		public void selectDrawing() {
			super.selectDrawing();
			setChanged();
			notifyObservers(new UniqueSelection(getDrawingGraphicalRepresentation(), null));
		}
		*/

		/*@Override
		public JDrawingView<Graph> makeDrawingView() {
			JDrawingView<Graph> returned = super.makeDrawingView();
			returned.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
						logger.info("Display contextual menu");
					}
				}
			});
			return returned;
		}*/

	}

	public static void showPanel() {
		final JDialog dialog = new JDialog((Frame) null, false);

		JPanel panel = new JPanel(new BorderLayout());

		// final TestInspector inspector = new TestInspector();

		final GraphDrawing1 d = makeDrawing();
		final TestDrawingController dc = new TestDrawingController(d);
		// dc.disablePaintingCache();
		dc.getDrawingView().setName("[NO_CACHE]");
		panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		panel.add(dc.scaleSelector.getComponent(), BorderLayout.NORTH);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton inspectButton = new JButton("Inspect");
		inspectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// inspector.getWindow().setVisible(true);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), dialog);
			}
		});

		JButton screenshotButton = new JButton("Screenshot");
		screenshotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final BufferedImage screenshot = dc.getDelegate().makeScreenshot(dc.getDrawing().getRoot());
				JDialog screenshotDialog = new JDialog((Frame) null, false);
				screenshotDialog.getContentPane().add(new JPanel() {
					@Override
					public void paint(java.awt.Graphics g) {
						super.paint(g);
						g.drawImage(screenshot, 0, 0, screenshot.getWidth(), screenshot.getHeight(), null);
					}
				});
				screenshotDialog.setPreferredSize(new Dimension(400, 400));
				screenshotDialog.setLocation(500, 500);
				screenshotDialog.validate();
				screenshotDialog.pack();
				screenshotDialog.setVisible(true);
			}
		});

		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(closeButton);
		controlPanel.add(inspectButton);
		controlPanel.add(logButton);
		controlPanel.add(screenshotButton);

		panel.add(controlPanel, BorderLayout.SOUTH);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);

		JDianaInteractiveViewer<TestGraph> dc2 = new JDianaInteractiveViewer<TestGraph>(d, d.getFactory(), SwingToolFactory.DEFAULT);
		final JDialog dialog2 = new JDialog((Frame) null, false);
		dialog2.getContentPane().add(new JScrollPane(dc2.getDrawingView()));
		dialog2.setPreferredSize(new Dimension(400, 400));
		dialog2.setLocation(800, 100);
		dialog2.validate();
		dialog2.pack();
		dialog2.setVisible(true);
		// dc2.enablePaintingCache();

		// dc.addObserver(inspector);
		// inspector.getWindow().setVisible(true);
	}

	public static GraphDrawing1 makeDrawing() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		TestGraph graph = new TestGraph();
		TestGraphNode node1 = new TestGraphNode("node1", graph);
		TestGraphNode node2 = new TestGraphNode("node2", graph);
		TestGraphNode node3 = new TestGraphNode("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		GraphDrawing1 returned = new GraphDrawing1(graph, factory);
		returned.printGraphicalObjectHierarchy();
		//returned.setEventManager(getManager().getEventManager());
		return returned;
	}

	@Override
	public void initWindow(Window w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		showPanel();
	}

	@Override
	public Dimension getWindowSize() {
		// TODO Auto-generated method stub
		return null;
	}

}

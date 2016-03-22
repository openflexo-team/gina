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

package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBBrowserWidget.JTreePanel;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserCellEditor;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserCellRenderer;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;
import org.openflexo.gina.view.widget.impl.FIBBrowserWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation for a browser (a tree of various objects)<br>
 * Implementation is here based on JTree component
 * 
 * @param <T>
 *            type of row data
 * 
 * @author sylvain
 */
public class JFIBBrowserWidget<T> extends FIBBrowserWidgetImpl<JTreePanel<T>, T>
		implements FocusListener, JFIBView<FIBBrowser, JTreePanel<T>> {

	private static final Logger LOGGER = Logger.getLogger(JFIBBrowserWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTable<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingBrowserRenderingAdapter<T> extends SwingRenderingAdapter<JTreePanel<T>>
			implements BrowserRenderingAdapter<JTreePanel<T>, T> {

		@Override
		public int getVisibleRowCount(JTreePanel<T> component) {
			return component.getJTree().getVisibleRowCount();
		}

		@Override
		public void setVisibleRowCount(JTreePanel<T> component, int visibleRowCount) {
			component.getJTree().setVisibleRowCount(visibleRowCount);
		}

		@Override
		public int getRowHeight(JTreePanel<T> component) {
			return component.getJTree().getRowHeight();
		}

		@Override
		public void setRowHeight(JTreePanel<T> component, int rowHeight) {
			component.getJTree().setRowHeight(rowHeight);
		}

		@Override
		public TreeSelectionModel getTreeSelectionModel(JTreePanel<T> component) {
			return component.getJTree().getSelectionModel();
		}

		@Override
		public boolean isEditing(JTreePanel<T> component) {
			return component.getJTree().isEditing();
		}

		@Override
		public void cancelCellEditing(JTreePanel<T> component) {
			component.getJTree().getCellEditor().cancelCellEditing();
		}

		@Override
		public boolean isExpanded(JTreePanel<T> component, TreePath treePath) {
			return component.getJTree().isExpanded(treePath);
		}

		@Override
		public Color getDefaultForegroundColor(JTreePanel<T> component) {
			return UIManager.getColor("Tree.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JTreePanel<T> component) {
			return UIManager.getColor("Tree.background");
		}

	}

	public JFIBBrowserWidget(FIBBrowser fibBrowser, FIBController controller) {
		super(fibBrowser, controller, new SwingBrowserRenderingAdapter<T>());
	}

	@Override
	public SwingBrowserRenderingAdapter<T> getRenderingAdapter() {
		return (SwingBrowserRenderingAdapter<T>) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	public JFIBBrowserWidgetFooter<T> makeFooter() {
		JFIBBrowserWidgetFooter<T> returned = new JFIBBrowserWidgetFooter<T>(this);
		getTechnologyComponent().add(returned.getFooterComponent(), BorderLayout.SOUTH);
		return returned;
	}

	@Override
	public JFIBBrowserWidgetFooter<T> getFooter() {
		return (JFIBBrowserWidgetFooter<T>) super.getFooter();
	}

	@Override
	public boolean processRootChanged() {
		boolean returned = super.processRootChanged();
		if (returned) {
			LOGGER.fine("RootValue changed for FIBBrowserWidget " + getRootValue());
			try {
				getTechnologyComponent().getJTree()
						.fireTreeWillExpand(new TreePath(getTechnologyComponent().getJTree().getModel().getRoot()));
			} catch (ExpandVetoException e1) {
				e1.printStackTrace();
			}
		}
		return returned;
	}

	@Override
	public void deleteBrowser() {
		if (getTechnologyComponent() != null) {
			getTechnologyComponent().removeFocusListener(this);
			for (MouseListener l : getTechnologyComponent().getMouseListeners()) {
				getTechnologyComponent().removeMouseListener(l);
			}
		}
	}

	@Override
	protected void updateSelectionMode() {
		if (getBrowser() != null) {
			getTreeSelectionModel().setSelectionMode(getBrowser().getTreeSelectionMode().getMode());
		}
	}

	@Override
	protected JTreePanel<T> makeTechnologyComponent() {
		return new JTreePanel<>(this);
	}

	@Override
	public void performSelect(T object, boolean force) {

		if ((!force) && (object == getSelected())) {
			LOGGER.fine("FIBTableWidget: ignore performSelect " + object);
			return;
		}
		setSelected(object);
		if (object != null) {
			if (getBrowser().getDeepExploration()) {
				// Recursively and exhaustively explore the whole model to
				// retrieve all contents
				// To be able to unfold required folders to select searched
				// value
				// System.out.println("Explore whole contents");
				getBrowserModel().recursivelyExploreModelToRetrieveContents();
			}

			Collection<BrowserCell> cells = getBrowserModel().getBrowserCell(object);
			// logger.info("Select " + cells);
			getTreeSelectionModel().clearSelection();
			if (cells != null) {
				TreePath scrollTo = null;
				for (BrowserCell cell : cells) {
					TreePath treePath = cell.getTreePath();
					if (scrollTo == null) {
						scrollTo = treePath;
					}
					getTreeSelectionModel().addSelectionPath(treePath);
				}
				if (scrollTo != null) {
					getTechnologyComponent().getJTree().scrollPathToVisible(scrollTo);
				}
			}
		}
		else {
			clearSelection();
		}
	}

	@Override
	public void performSelect(List<T> objects, boolean force) {

		// TODO: implement premature return when selection has not changed

		/*
		 * if ((!force) && objects != null && (objects.equals(getSelection())))
		 * { LOGGER.fine("FIBTableWidget: ignore performSelect " + objects);
		 * return; }
		 */
		setSelection(objects);
		if (objects != null) {
			if (getBrowser().getDeepExploration()) {
				// Recursively and exhaustively explore the whole model to
				// retrieve all contents
				// To be able to unfold required folders to select searched
				// value
				// System.out.println("Explore whole contents");
				getBrowserModel().recursivelyExploreModelToRetrieveContents();
			}

			TreePath scrollTo = null;
			List<TreePath> treePathsAsList = new ArrayList<TreePath>();

			for (T object : objects) {
				Collection<BrowserCell> cells = getBrowserModel().getBrowserCell(object);
				if (cells != null) {
					for (BrowserCell cell : cells) {
						TreePath treePath = cell.getTreePath();
						if (scrollTo == null) {
							scrollTo = treePath;
						}
						treePathsAsList.add(treePath);
					}
				}
			}

			getTreeSelectionModel().clearSelection();
			getTreeSelectionModel().addSelectionPaths(treePathsAsList.toArray(new TreePath[treePathsAsList.size()]));

			if (scrollTo != null) {
				getTechnologyComponent().getJTree().scrollPathToVisible(scrollTo);
			}

		}
		else {
			clearSelection();
		}
	}

	@Override
	public void addToSelection(Object o) {

		super.addToSelection(o);

		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTreeSelectionModel().addSelectionPath(path);
			getTechnologyComponent().getJTree().scrollPathToVisible(path);
		}

	}

	@Override
	public void performExpand(T o) {

		super.performExpand(o);

		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTechnologyComponent().getJTree().expandPath(path);
		}

	}

	@SuppressWarnings("serial")
	public static class JTreePanel<T> extends JPanel {

		private JTree jTree;
		private JScrollPane scrollPane;
		private final JFIBBrowserWidget<T> widget;

		public JTreePanel(JFIBBrowserWidget<T> aWidget) {
			super(new BorderLayout());
			setOpaque(false);
			this.widget = aWidget;

			jTree = new JTree(widget.getBrowserModel()) {
				@Override
				protected void paintComponent(Graphics g) {
					// We try here to handle exception which may occur in SWING
					// layers
					try {
						super.paintComponent(g);
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception occured in SWING: " + e);
						e.printStackTrace();
					}
					/*
					 * if (ToolBox.isMacOSLaf()) { if (getSelectionRows() !=
					 * null && getSelectionRows().length > 0) { for (int
					 * selected : getSelectionRows()) { Rectangle rowBounds =
					 * getRowBounds(selected); if
					 * (getVisibleRect().intersects(rowBounds)) { Object value =
					 * getPathForRow(selected).getLastPathComponent(); Component
					 * treeCellRendererComponent =
					 * getCellRenderer().getTreeCellRendererComponent(this,
					 * value, true, isExpanded(selected),
					 * getModel().isLeaf(value), selected, getLeadSelectionRow()
					 * == selected); Color bgColor =
					 * treeCellRendererComponent.getBackground(); if
					 * (treeCellRendererComponent instanceof
					 * DefaultTreeCellRenderer) { bgColor =
					 * ((DefaultTreeCellRenderer)
					 * treeCellRendererComponent).getBackgroundSelectionColor();
					 * } if (bgColor != null) { g.setColor(bgColor);
					 * g.fillRect(0, rowBounds.y, getWidth(), rowBounds.height);
					 * Graphics g2 = g.create(rowBounds.x, rowBounds.y,
					 * rowBounds.width, rowBounds.height);
					 * treeCellRendererComponent.setBounds(rowBounds);
					 * treeCellRendererComponent.paint(g2); g2.dispose(); } } }
					 * } }
					 */
				}
			};
			jTree.addTreeWillExpandListener(new TreeWillExpandListener() {

				@Override
				public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
					TreePath path = event.getPath();
					if (path.getLastPathComponent() instanceof BrowserCell) {
						BrowserCell node = (BrowserCell) path.getLastPathComponent();
						node.loadChildren(widget.getBrowserModel(), null);
					}
				}

				@Override
				public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

				}
			});

			if (ToolBox.isMacOS()) {
				jTree.setSelectionModel(new DefaultTreeSelectionModel() {
					@Override
					public int[] getSelectionRows() {
						int[] selectionRows = super.getSelectionRows();
						// MacOS X does not support that we return null here
						// during DnD operations.
						if (selectionRows == null) {
							return new int[0];
						}
						return selectionRows;
					}
				});
			}
			jTree.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			jTree.addFocusListener(widget);
			jTree.setEditable(true);
			jTree.setScrollsOnExpand(true);
			FIBBrowserCellRenderer renderer = new FIBBrowserCellRenderer(widget);
			jTree.setCellRenderer(renderer);
			jTree.setCellEditor(new FIBBrowserCellEditor(jTree, renderer));
			ToolTipManager.sharedInstance().registerComponent(jTree);
			jTree.setAutoscrolls(true);

			/** Beginning of model dependent settings */
			jTree.setRootVisible(widget.getBrowser().getRootVisible());
			jTree.setShowsRootHandles(widget.getBrowser().getShowRootsHandle());

			// If a double-click action is set, desactivate tree
			// expanding/collabsing with double-click
			if (widget.getBrowser().getDoubleClickAction().isSet()) {
				jTree.setToggleClickCount(-1);
			}

			if (widget.getWidget().getRowHeight() != null) {
				jTree.setRowHeight(widget.getWidget().getRowHeight());
			}
			else {
				jTree.setRowHeight(0);
			}
			if (widget.getWidget().getVisibleRowCount() != null) {
				jTree.setVisibleRowCount(widget.getWidget().getVisibleRowCount());
			}

			// getTreeSelectionModel().setSelectionMode(getBrowser().getTreeSelectionMode().getMode());

			// getTreeSelectionModel().addTreeSelectionListener(this);
			jTree.getSelectionModel().addTreeSelectionListener(widget);

			scrollPane = new JScrollPane(jTree);

			add(scrollPane, BorderLayout.CENTER);

			if (widget.getWidget().getBoundToSelectionManager()) {
				jTree.registerKeyboardAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						widget.getController().performCopyAction(widget.getSelected(), widget.getSelection());
					}
				}, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK, false), JComponent.WHEN_FOCUSED);
				jTree.registerKeyboardAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						widget.getController().performCutAction(widget.getSelected(), widget.getSelection());
					}
				}, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK, false), JComponent.WHEN_FOCUSED);
				jTree.registerKeyboardAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						widget.getController().performPasteAction(widget.getSelected(), widget.getSelection());
					}
				}, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK, false), JComponent.WHEN_FOCUSED);
			}

			if (widget.getWidget().getShowFooter() && widget.getFooter() != null) {
				add(widget.getFooter().getFooterComponent(), BorderLayout.SOUTH);
			}

			widget.getBrowserModel().addTreeModelListener(new TreeModelListener() {

				@Override
				public void treeStructureChanged(TreeModelEvent e) {
					ensureRootExpanded();
				}

				@Override
				public void treeNodesRemoved(TreeModelEvent e) {

				}

				@Override
				public void treeNodesInserted(TreeModelEvent e) {
					ensureRootExpanded();
				}

				private void ensureRootExpanded() {
					if (!widget.getBrowser().getRootVisible() && (BrowserCell) widget.getBrowserModel().getRoot() != null
							&& ((BrowserCell) widget.getBrowserModel().getRoot()).getChildCount() == 1) {
						// Only one cell and roots are hidden, expand this first
						// cell

						widget.invokeLater(new Runnable() {
							@Override
							public void run() {
								// See issue OPENFLEXO-516. Sometimes, the
								// condition may have become false.
								if (!widget.getBrowser().getRootVisible() && (BrowserCell) widget.getBrowserModel().getRoot() != null
										&& ((BrowserCell) widget.getBrowserModel().getRoot()).getChildCount() == 1) {
									jTree.expandPath(new TreePath(new Object[] { (BrowserCell) widget.getBrowserModel().getRoot(),
											((BrowserCell) widget.getBrowserModel().getRoot()).getChildAt(0) }));
								}
							}
						}, 1000);
					}
				}

				@Override
				public void treeNodesChanged(TreeModelEvent e) {
					// TODO Auto-generated method stub

				}
			});

		}

		public JTree getJTree() {
			return jTree;
		}
	}

}

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

package org.openflexo.fib.swing.view.widget;

import java.awt.BorderLayout;
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
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellEditor;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellRenderer;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;
import org.openflexo.fib.view.widget.impl.FIBBrowserWidgetImpl;
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
public class JFIBBrowserWidget<T> extends FIBBrowserWidgetImpl<JTree, T>implements FocusListener {

	private static final Logger LOGGER = Logger.getLogger(JFIBBrowserWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JTable<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingBrowserRenderingTechnologyAdapter<T> extends SwingRenderingTechnologyAdapter<JTree>
			implements BrowserRenderingTechnologyAdapter<JTree, T> {

		@Override
		public int getVisibleRowCount(JTree component) {
			return component.getVisibleRowCount();
		}

		@Override
		public void setVisibleRowCount(JTree component, int visibleRowCount) {
			component.setVisibleRowCount(visibleRowCount);
		}

		@Override
		public int getRowHeight(JTree component) {
			return component.getRowHeight();
		}

		@Override
		public void setRowHeight(JTree component, int rowHeight) {
			component.setRowHeight(rowHeight);
		}

		@Override
		public TreeSelectionModel getTreeSelectionModel(JTree component) {
			return component.getSelectionModel();
		}

		@Override
		public boolean isEditing(JTree component) {
			return component.isEditing();
		}

		@Override
		public void cancelCellEditing(JTree component) {
			component.getCellEditor().cancelCellEditing();
		}

		@Override
		public boolean isExpanded(JTree component, TreePath treePath) {
			return component.isExpanded(treePath);
		}

	}

	private final JPanel _dynamicComponent;
	private JScrollPane scrollPane;

	public JFIBBrowserWidget(FIBBrowser fibBrowser, FIBController controller) {
		super(fibBrowser, controller, new SwingBrowserRenderingTechnologyAdapter<T>());
		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());
	}

	@Override
	public JFIBBrowserWidgetFooter<T> makeFooter() {
		return new JFIBBrowserWidgetFooter<>(this);
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
				getTechnologyComponent().fireTreeWillExpand(new TreePath(getTechnologyComponent().getModel().getRoot()));
			} catch (ExpandVetoException e1) {
				e1.printStackTrace();
			}
		}
		return returned;
	}

	@Override
	public JPanel getJComponent() {
		return _dynamicComponent;
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
	protected JTree makeTechnologyComponent() {
		JTree _tree = new JTree(getBrowserModel()) {
			@Override
			protected void paintComponent(Graphics g) {
				// We try here to handle exception which may occur in SWING layers
				try {
					super.paintComponent(g);
				} catch (Exception e) {
					LOGGER.warning("Unexpected exception occured in SWING: " + e);
					e.printStackTrace();
				}
				/*if (ToolBox.isMacOSLaf()) {
					if (getSelectionRows() != null && getSelectionRows().length > 0) {
						for (int selected : getSelectionRows()) {
							Rectangle rowBounds = getRowBounds(selected);
							if (getVisibleRect().intersects(rowBounds)) {
								Object value = getPathForRow(selected).getLastPathComponent();
								Component treeCellRendererComponent = getCellRenderer().getTreeCellRendererComponent(this, value, true,
										isExpanded(selected), getModel().isLeaf(value), selected, getLeadSelectionRow() == selected);
								Color bgColor = treeCellRendererComponent.getBackground();
								if (treeCellRendererComponent instanceof DefaultTreeCellRenderer) {
									bgColor = ((DefaultTreeCellRenderer) treeCellRendererComponent).getBackgroundSelectionColor();
								}
								if (bgColor != null) {
									g.setColor(bgColor);
									g.fillRect(0, rowBounds.y, getWidth(), rowBounds.height);
									Graphics g2 = g.create(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height);
									treeCellRendererComponent.setBounds(rowBounds);
									treeCellRendererComponent.paint(g2);
									g2.dispose();
								}
							}
						}
					}
				}*/
			}
		};
		_tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				TreePath path = event.getPath();
				if (path.getLastPathComponent() instanceof BrowserCell) {
					BrowserCell node = (BrowserCell) path.getLastPathComponent();
					node.loadChildren(getBrowserModel(), null);
				}
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

			}
		});

		if (ToolBox.isMacOS()) {
			_tree.setSelectionModel(new DefaultTreeSelectionModel() {
				@Override
				public int[] getSelectionRows() {
					int[] selectionRows = super.getSelectionRows();
					// MacOS X does not support that we return null here during DnD operations.
					if (selectionRows == null) {
						return new int[0];
					}
					return selectionRows;
				}
			});
		}
		_tree.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_tree.addFocusListener(this);
		_tree.setEditable(true);
		_tree.setScrollsOnExpand(true);
		FIBBrowserCellRenderer renderer = new FIBBrowserCellRenderer(this);
		_tree.setCellRenderer(renderer);
		_tree.setCellEditor(new FIBBrowserCellEditor(_tree, renderer));
		ToolTipManager.sharedInstance().registerComponent(_tree);
		_tree.setAutoscrolls(true);

		/** Beginning of model dependent settings */
		_tree.setRootVisible(getBrowser().getRootVisible());
		_tree.setShowsRootHandles(getBrowser().getShowRootsHandle());

		// If a double-click action is set, desactivate tree expanding/collabsing with double-click
		if (getBrowser().getDoubleClickAction().isSet()) {
			_tree.setToggleClickCount(-1);
		}

		if (getWidget().getRowHeight() != null) {
			_tree.setRowHeight(getWidget().getRowHeight());
		}
		else {
			_tree.setRowHeight(0);
		}
		if (getWidget().getVisibleRowCount() != null) {
			_tree.setVisibleRowCount(getWidget().getVisibleRowCount());
		}

		getTreeSelectionModel().setSelectionMode(getBrowser().getTreeSelectionMode().getMode());
		getTreeSelectionModel().addTreeSelectionListener(this);

		scrollPane = new JScrollPane(_tree);

		_dynamicComponent.removeAll();
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);

		if (getWidget().getBoundToSelectionManager()) {
			_tree.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getController().performCopyAction(getSelected(), getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK, false), JComponent.WHEN_FOCUSED);
			_tree.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getController().performCutAction(getSelected(), getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK, false), JComponent.WHEN_FOCUSED);
			_tree.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getController().performPasteAction(getSelected(), getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK, false), JComponent.WHEN_FOCUSED);
		}

		if (getWidget().getShowFooter()) {
			_dynamicComponent.add(getFooter().getFooterComponent(), BorderLayout.SOUTH);
		}
		_dynamicComponent.revalidate();
		_dynamicComponent.repaint();
		getBrowserModel().addTreeModelListener(new TreeModelListener() {

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
				if (!getBrowser().getRootVisible() && (BrowserCell) getBrowserModel().getRoot() != null
						&& ((BrowserCell) getBrowserModel().getRoot()).getChildCount() == 1) {
					// Only one cell and roots are hidden, expand this first cell

					invokeLater(new Runnable() {
						@Override
						public void run() {
							// See issue OPENFLEXO-516. Sometimes, the condition may have become false.
							if (!getBrowser().getRootVisible() && (BrowserCell) getBrowserModel().getRoot() != null
									&& ((BrowserCell) getBrowserModel().getRoot()).getChildCount() == 1) {
								getTechnologyComponent().expandPath(new TreePath(new Object[] { (BrowserCell) getBrowserModel().getRoot(),
										((BrowserCell) getBrowserModel().getRoot()).getChildAt(0) }));
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

		return _tree;
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
				// Recursively and exhaustively explore the whole model to retrieve all contents
				// To be able to unfold required folders to select searched value
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
					getTechnologyComponent().scrollPathToVisible(scrollTo);
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

		/*if ((!force) && objects != null && (objects.equals(getSelection()))) {
			LOGGER.fine("FIBTableWidget: ignore performSelect " + objects);
			return;
		}*/
		setSelection(objects);
		if (objects != null) {
			if (getBrowser().getDeepExploration()) {
				// Recursively and exhaustively explore the whole model to retrieve all contents
				// To be able to unfold required folders to select searched value
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
				getTechnologyComponent().scrollPathToVisible(scrollTo);
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
			getTechnologyComponent().scrollPathToVisible(path);
		}

	}

	@Override
	public void performExpand(T o) {

		super.performExpand(o);

		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTechnologyComponent().expandPath(path);
		}

	}

}

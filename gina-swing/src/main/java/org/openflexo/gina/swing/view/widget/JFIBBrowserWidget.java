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
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserDragOperation;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBBrowserWidget.JTreePanel;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserCellEditor;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserCellRenderer;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
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

		@Override
		public JTree getDynamicJComponent(JTreePanel<T> technologyComponent) {
			return technologyComponent.getJTree();
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
		JFIBBrowserWidgetFooter<T> returned = new JFIBBrowserWidgetFooter<>(this);
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
				getBrowserModel().recursivelyExploreModelToRetrieveContents();
			}

			Collection<BrowserCell> cells = getBrowserModel().getBrowserCell(object);

			if (cells == null || cells.size() == 0) {
				LOGGER.warning("POTENTIAL PERFS ISSUE HERE: Not found object " + object + " perform explore whole contents");
				// TODO: implements a method which explicitely search 'object'
				getBrowserModel().recursivelyExploreModelToRetrieveContents();
				cells = getBrowserModel().getBrowserCell(object);
			}

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
			List<TreePath> treePathsAsList = new ArrayList<>();

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

		private DnDJTree jTree;
		private JScrollPane scrollPane;
		private final JFIBBrowserWidget<T> widget;

		public JTreePanel(JFIBBrowserWidget<T> aWidget) {
			super(new BorderLayout());
			setOpaque(false);
			this.widget = aWidget;

			jTree = new DnDJTree(widget) {
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
			FIBBrowserCellRenderer<?> renderer = new FIBBrowserCellRenderer<>(widget);
			jTree.setCellRenderer(renderer);
			jTree.setCellEditor(new FIBBrowserCellEditor(jTree, renderer));
			ToolTipManager.sharedInstance().registerComponent(jTree);
			jTree.setAutoscrolls(true);

			/** Beginning of model dependent settings */
			jTree.setRootVisible(widget.getBrowser().getRootVisible());
			jTree.setShowsRootHandles(widget.getBrowser().getShowRootsHandle());

			// If a double-click action is set, deactivate tree
			// expanding/collapsing with double-click
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
					if (widget == null || widget.getBrowser() == null || widget.getBrowserModel() == null) {
						return;
					}
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

	@SuppressWarnings("serial")
	public static class DnDJTree extends JTree
			implements TreeSelectionListener, DragGestureListener, DropTargetListener, DragSourceListener, BindingEvaluationContext {

		/** Stores the parent Frame of the component */
		private Frame parentFrame = null;

		/** Stores the selected node info */
		protected TreePath selectedTreePath = null;
		protected BrowserCell selectedBrowserCell = null;

		/** Variables needed for DnD */
		private DragSource dragSource = null;
		private DragSourceContext dragSourceContext = null;

		/** The browser cell beeing currently dragged */
		private BrowserCell draggedBrowserCell;

		/** The browser cell destination of the drop: the one on we target */
		private BrowserCell targetBrowserCell;

		private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
		private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

		public static Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS
				? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;
		public static Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS
				? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO")
				: DragSource.DefaultMoveNoDrop;

		private JFIBBrowserWidget<?> widget;

		public DnDJTree(JFIBBrowserWidget<?> widget) {
			super(widget.getBrowserModel());
			this.widget = widget;

			addTreeSelectionListener(this);

			if (widget.getComponent().getAllowsDragAndDrop()) {

				dragSource = DragSource.getDefaultDragSource();

				DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, // DragSource
						DnDConstants.ACTION_MOVE, // specifies valid actions
						this // DragGestureListener
				);

				/* Eliminates right mouse clicks as valid actions - useful especially
				 * if you implement a JPopupMenu for the JTree
				 */
				dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

				/* First argument:  Component to associate the target with
				 * Second argument: DropTargetListener 
				 */
				// Unused DropTarget dropTarget =
				new DropTarget(this, this);
			}

		}

		/** Returns The selected node */
		public BrowserCell getSelectedBrowserCell() {
			return selectedBrowserCell;
		}

		/** DragGestureListener interface method */
		@Override
		public void dragGestureRecognized(DragGestureEvent e) {
			// Get the selected node
			BrowserCell dragNode = getSelectedBrowserCell();
			if (dragNode != null) {

				// Get the Transferable Object
				// Transferable transferable = (Transferable) dragNode.getUserObject();
				Transferable transferable = dragNode;

				/* ********************** CHANGED ********************** */

				// Select the appropriate cursor;
				Cursor cursor = DragSource.DefaultCopyNoDrop;
				int action = e.getDragAction();
				if (action == DnDConstants.ACTION_MOVE) {
					// cursor = DragSource.DefaultMoveNoDrop;
					cursor = dropKO;
				}

				// In fact the cursor is set to NoDrop because once an action is rejected
				// by a dropTarget, the dragSourceListener are no more invoked.
				// Setting the cursor to no drop by default is so more logical, because
				// when the drop is accepted by a component, then the cursor is changed by the
				// dropActionChanged of the default DragSource.

				// begin the drag
				dragSource.startDrag(e, cursor, transferable, this);
			}
		}

		/** DragSourceListener interface method */
		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			setCursor(Cursor.getDefaultCursor());
		}

		/** DragSourceListener interface method */
		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
			/* ********************** CHANGED ********************** */
			/* ****************** END OF CHANGE ******************** */
		}

		/** DragSourceListener interface method */
		@Override
		public void dragOver(DragSourceDragEvent dsde) {
			/* ********************** CHANGED ********************** */
			/* ****************** END OF CHANGE ******************** */
			// setCursor(Cursor.getDefaultCursor());
		}

		/** DragSourceListener interface method */
		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
		}

		/** DragSourceListener interface method */
		@Override
		public void dragExit(DragSourceEvent dsde) {
			// setCursor(Cursor.getDefaultCursor());
		}

		/** DropTargetListener interface method - What we do when drag is released */
		@Override
		public void drop(DropTargetDropEvent e) {
			// try {
			Transferable tr = e.getTransferable();

			// flavor not supported, reject drop
			if (!tr.isDataFlavorSupported(FIBBrowserModel.BROWSER_CELL_FLAVOR))
				e.rejectDrop();

			// cast into appropriate data type
			// BrowserCell childInfo = (BrowserCell) tr.getTransferData(FIBBrowserModel.BROWSER_CELL_FLAVOR);

			// get new parent node
			Point loc = e.getLocation();
			TreePath destinationPath = getPathForLocation(loc.x, loc.y);

			if (!isDroppable(selectedTreePath, destinationPath)) {
				e.rejectDrop();
				return;
			}

			// Unused BrowserCell newParent = (BrowserCell)
			destinationPath.getLastPathComponent();

			// get old parent node
			// Unused BrowserCell oldParent =
			getSelectedBrowserCell().getParent();

			int action = e.getDropAction();
			boolean copyAction = (action == DnDConstants.ACTION_COPY);

			/*System.out.println("Perform DND");
			System.out.println("from: " + oldParent);
			System.out.println("to: " + newParent);
			System.out.println("action: " + action);*/

			performDrop(selectedTreePath, destinationPath);

			if (copyAction)
				e.acceptDrop(DnDConstants.ACTION_COPY);
			else
				e.acceptDrop(DnDConstants.ACTION_MOVE);

			e.getDropTargetContext().dropComplete(true);

			// expand nodes appropriately - this probably isnt the best way...
			// DefaultTreeModel model = (DefaultTreeModel) getModel();
			// getModel().reload(oldParent);
			// getModel().reload(newParent);
			// TreePath parentPath = new TreePath(newParent.getPath());
			// expandPath(parentPath);

			/*} catch (IOException io) {
			e.rejectDrop();
			} catch (UnsupportedFlavorException ufe) {
			e.rejectDrop();
			}*/
		} // end of method

		/** DropTaregetListener interface method */
		@Override
		public void dragEnter(DropTargetDragEvent e) {
		}

		/** DropTaregetListener interface method */
		@Override
		public void dragExit(DropTargetEvent e) {
		}

		/** DropTaregetListener interface method */
		@Override
		public void dragOver(DropTargetDragEvent e) {
			// set cursor location. Needed in setCursor method
			Point cursorLocationBis = e.getLocation();
			TreePath destinationPath = getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

			// if destination path is okay accept drop...
			if (isDroppable(selectedTreePath, destinationPath)) {
				// System.out.println("C'est bien ca");
				e.acceptDrag(DnDConstants.ACTION_MOVE);
				setCursor(/*Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)*/dropOK);
				// setCursor(DragSource.DefaultMoveNoDrop);
			}
			// ...otherwise reject drop
			else {
				// System.out.println("Pas bon");
				e.rejectDrag();
				// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				setCursor(/*DragSource.DefaultMoveNoDrop*/dropKO);
			}
		}

		/** DropTaregetListener interface method */
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
		}

		/** TreeSelectionListener - sets selected node */
		@Override
		public void valueChanged(TreeSelectionEvent evt) {
			selectedTreePath = evt.getNewLeadSelectionPath();
			if (selectedTreePath == null) {
				selectedBrowserCell = null;
				return;
			}
			Object lastPathComponent = selectedTreePath.getLastPathComponent();
			if (lastPathComponent instanceof BrowserCell) {
				selectedBrowserCell = (BrowserCell) lastPathComponent;
			}
		}

		/**
		 * Convenience method to test whether drop location is valid
		 * 
		 * @param destination
		 *            The destination path
		 * @param dropper
		 *            The path for the node to be dropped
		 * @return null if no problems, otherwise an explanation
		 */
		private boolean isDroppable(TreePath dropper, TreePath destination) {

			if (destination == null || dropper == null) {
				return false;
			}

			System.out.println("Est ce que c'est droppable ????");

			draggedBrowserCell = (BrowserCell) dropper.getLastPathComponent();
			targetBrowserCell = (BrowserCell) destination.getLastPathComponent();

			System.out.println("dragged=" + draggedBrowserCell.getRepresentedObject());
			System.out.println("target=" + targetBrowserCell.getRepresentedObject());

			for (FIBBrowserDragOperation op : draggedBrowserCell.getBrowserElement().getDragOperations()) {
				Boolean applicable = false;
				if (targetBrowserCell.getBrowserElement() == op.getTargetElement()) {
					try {
						System.out.println("on regarde pour " + op);
						applicable = op.getIsAvailable().getBindingValue(this);
					} catch (TypeMismatchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NullReferenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (applicable != null && applicable) {
						System.out.println(">>>>>>>> Et oui on le retourne");
						return true;
					}
				}
			}

			return false;
			/*if (dropperCell.getRepresentedObject().getClass().getName().contains("Person")
			&& destinationCell.getRepresentedObject().getClass().getName().contains("Family")) {
			System.out.println("Oui c'est bien ca");
			return true;
			}*/

			// Typical Tests for dropping

			// Test 1.
			/*boolean destinationPathIsNull = destination == null;
			if (destinationPathIsNull)
			return "Invalid drop location.";
			
			// Test 2.
			PersonNode node = (PersonNode) destination.getLastPathComponent();
			if (!node.getAllowsChildren())
			return "This node does not allow children";
			
			if (destination.equals(dropper))
			return "Destination cannot be same as source";
			
			// Test 3.
			if (dropper.isDescendant(destination))
			return "Destination node cannot be a descendant.";
			
			// Test 4.
			if (dropper.getParentPath().equals(destination))
			return "Destination node cannot be a parent.";
			
			return null;*/
		}

		private boolean performDrop(TreePath dropper, TreePath destination) {

			if (destination == null || dropper == null) {
				return false;
			}

			draggedBrowserCell = (BrowserCell) dropper.getLastPathComponent();
			targetBrowserCell = (BrowserCell) destination.getLastPathComponent();

			for (FIBBrowserDragOperation op : draggedBrowserCell.getBrowserElement().getDragOperations()) {
				boolean applicable = false;
				try {
					applicable = op.getIsAvailable().getBindingValue(this);
					if (applicable) {
						op.getAction().execute(this);
						return true;
					}
				} catch (TypeMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return false;
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals("dragged")) {
				// System.out.println("dragged is" + draggedBrowserCell.getRepresentedObject());
				return draggedBrowserCell.getRepresentedObject();
			}
			else if (variable.getVariableName().equals("target")) {
				// System.out.println(" target is " + draggedBrowserCell.getRepresentedObject());
				return targetBrowserCell.getRepresentedObject();
			}
			else {
				return widget.getBindingEvaluationContext().getValue(variable);
			}
		}
	}
}

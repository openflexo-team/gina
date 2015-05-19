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

package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellEditor;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellRenderer;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;
import org.openflexo.fib.view.widget.browser.FIBBrowserWidgetFooter;
import org.openflexo.toolbox.ToolBox;

/**
 * Widget allowing to display a browser (a tree of various objects)
 * 
 * @author sguerin
 */
public class FIBBrowserWidget<T> extends FIBWidgetView<FIBBrowser, JTree, T> implements FIBSelectable<T>, TreeSelectionListener {

	private static final Logger LOGGER = Logger.getLogger(FIBBrowserWidget.class.getPackage().getName());

	public static final String SELECTED = "selected";
	public static final String SELECTION = "selection";

	private JTree _tree;
	private final JPanel _dynamicComponent;
	private final FIBBrowser _fibBrowser;
	private FIBBrowserModel _browserModel;
	private final FIBBrowserWidgetFooter _footer;
	// private ListSelectionModel _listSelectionModel;
	private JScrollPane scrollPane;

	private T selectedObject;
	private List<T> selection;

	private BindingValueChangeListener<T> selectedBindingValueChangeListener;
	private BindingValueListChangeListener<T, List<T>> selectionBindingValueChangeListener;
	private BindingValueChangeListener<Object> rootBindingValueChangeListener;

	public FIBBrowserWidget(FIBBrowser fibBrowser, FIBController controller) {
		super(fibBrowser, controller);
		_fibBrowser = fibBrowser;
		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());
		selection = new ArrayList<T>();
		_footer = new FIBBrowserWidgetFooter(this);
		buildBrowser();
		listenSelectedValueChange();
		listenSelectionValueChange();
		listenRootValueChange();
	}

	private void listenSelectedValueChange() {
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.stopObserving();
			selectedBindingValueChangeListener.delete();
		}
		if (getComponent().getSelected() != null && getComponent().getSelected().isValid()) {
			selectedBindingValueChangeListener = new BindingValueChangeListener<T>((DataBinding<T>) getComponent().getSelected(),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, T newValue) {
					// System.out.println(" bindingValueChanged() detected for selected=" + getComponent().getSelected() + " with newValue="
					// + newValue + " source=" + source);
					performSelect(newValue, false);
				}

			};
		}
	}

	private void listenSelectionValueChange() {

		if (selectionBindingValueChangeListener != null) {
			selectionBindingValueChangeListener.stopObserving();
			selectionBindingValueChangeListener.delete();
		}
		if (getComponent().getSelection() != null && getComponent().getSelection().isValid()) {
			selectionBindingValueChangeListener = new BindingValueListChangeListener<T, List<T>>(((DataBinding) getComponent()
					.getSelection()), getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, List<T> newValue) {
					// System.out.println(" bindingValueChanged() detected for selection=" + getComponent().getSelection() +
					// " with newValue="
					// + newValue + " source=" + source);
					performSelect(newValue, false);
				}
			};
		}
	}

	private void listenRootValueChange() {
		if (rootBindingValueChangeListener != null) {
			rootBindingValueChangeListener.stopObserving();
			rootBindingValueChangeListener.delete();
		}

		if (getComponent().getRoot() != null && getComponent().getRoot().isValid()) {

			rootBindingValueChangeListener = new BindingValueChangeListener<Object>(getComponent().getRoot(), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, Object newValue) {
					// System.out.println(" bindingValueChanged() detected for root=" + getComponent().getRoot() + " with newValue="
					// + newValue + " source=" + source);
					processRootChanged();
				}
			};

		}
	}

	@Override
	public synchronized void delete() {
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.stopObserving();
			selectedBindingValueChangeListener.delete();
		}
		_footer.delete();
		super.delete();
	}

	public FIBBrowser getBrowser() {
		return _fibBrowser;
	}

	public FIBBrowserModel getBrowserModel() {
		if (_browserModel == null) {
			_browserModel = new FIBBrowserModel(_fibBrowser, this, getController());
		}
		return _browserModel;
	}

	public JTree getJTree() {
		return _tree;
	}

	public FIBBrowserWidgetFooter getFooter() {
		return _footer;
	}

	public Object getRootValue() {
		if (getWidget() != null && getWidget().getRoot() != null) {
			try {
				return getWidget().getRoot().getBindingValue(getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// private static final Vector EMPTY_VECTOR = new Vector();

	private boolean processRootChanged() {
		boolean returned = getBrowserModel().updateRootObject(getRootValue());
		if (returned) {
			LOGGER.fine("RootValue changed for FIBBrowserWidget " + getRootValue());
			try {
				_tree.fireTreeWillExpand(new TreePath(_tree.getModel().getRoot()));
			} catch (ExpandVetoException e1) {
				e1.printStackTrace();
			}
		}
		// If root object has changed, this might be very usefull to update selected, too !!!
		updateSelected(true);
		return returned;
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// List valuesBeforeUpdating = getBrowserModel().getValues();
		Object wasSelected = getSelected();

		// boolean debug = false;
		// if (getWidget().getName() != null && getWidget().getName().equals("PatternRoleTable")) debug=true;

		// if (debug) System.out.println("valuesBeforeUpdating: "+valuesBeforeUpdating);
		// if (debug) System.out.println("wasSelected: "+wasSelected);

		if (_tree.isEditing()) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(getComponent().getName() + " - Tree is currently editing");
			}
			_tree.getCellEditor().cancelCellEditing();
		} else {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(getComponent().getName() + " - Tree is NOT currently edited ");
			}
		}

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine(getComponent().getName() + " updateWidgetFromModel() with " + getValue() + " dataObject=" + getDataObject());
		}

		boolean returned = processRootChanged();

		/*if (!getBrowser().getRootVisible() && ((BrowserCell) getBrowserModel().getRoot()).getChildCount() == 1) {
			// Only one cell and roots are hidden, expand this first cell
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (((BrowserCell) getBrowserModel().getRoot()).getChildCount() > 0) {
						getJTree().expandPath(
								new TreePath(new Object[] { (BrowserCell) getBrowserModel().getRoot(),
										((BrowserCell) getBrowserModel().getRoot()).getChildAt(0) }));
					}
				}
			});
		}*/
		// getBrowserModel().setModel(getDataObject());

		// We restore value if and only if we represent same browser
		/*if (getBrowserModel().getValues() == valuesBeforeUpdating && wasSelected != null) {
			setSelectedObject(wasSelected);
		}
		else {*/

		// logger.info("Bon, je remets a jour la selection du browser, value="+getComponent().getSelected().getBindingValue(getController())+" was: "+getSelectedObject());
		// System.out.println("getComponent().getSelected()="+getComponent().getSelected());
		// System.out.println("getComponent().getSelected().isValid()="+getComponent().getSelected().isValid());
		// System.out.println("value="+getComponent().getSelected().getBindingValue(getController()));

		try {
			T newSelectedObject = (T) getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
			if (newSelectedObject != null) {
				if (returned = notEquals(newSelectedObject, getSelected())) {
					setSelected(newSelectedObject);
				}
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// }

		return returned;
	}

	public TreeSelectionModel getTreeSelectionModel() {
		return _tree.getSelectionModel();
	}

	@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return _dynamicComponent;
	}

	@Override
	public JTree getDynamicJComponent() {
		return _tree;
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateBrowser();
		System.out.println("Ne pas oublier de localiser les actions ici");
		/*for (FIBTableAction a : getWidget().getActions()) {
			if (getWidget().getLocalize()) getLocalized(a.getName());
		}*/
	}

	public void updateBrowser() {
		deleteBrowser();

		if (_browserModel != null) {
			_browserModel.delete();
			_browserModel = null;
		}

		buildBrowser();

		update();
		// updateDataObject(getDataObject());
	}

	private void deleteBrowser() {
		if (_tree != null) {
			_tree.removeFocusListener(this);
			for (MouseListener l : _tree.getMouseListeners()) {
				_tree.removeMouseListener(l);
			}
		}
	}

	final private void buildBrowser() {
		_tree = new JTree(getBrowserModel()) {
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

		if (_fibBrowser.getRowHeight() != null) {
			_tree.setRowHeight(_fibBrowser.getRowHeight());
		} else {
			_tree.setRowHeight(0);
		}
		if (_fibBrowser.getVisibleRowCount() != null) {
			_tree.setVisibleRowCount(_fibBrowser.getVisibleRowCount());
		}

		getTreeSelectionModel().setSelectionMode(getBrowser().getSelectionMode().getMode());
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

		if (_fibBrowser.getShowFooter()) {
			_dynamicComponent.add(getFooter(), BorderLayout.SOUTH);
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
								getJTree().expandPath(
										new TreePath(new Object[] { (BrowserCell) getBrowserModel().getRoot(),
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
	}

	@Override
	public boolean synchronizedWithSelection() {
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable() {
		return getController() != null && getController().getLastFocusedSelectable() == this;
	}

	@Override
	public boolean mayRepresent(Object o) {
		if (o == null) {
			return false;
		}
		return _browserModel.containsObject(o);
	}

	@Override
	public void objectAddedToSelection(Object o) {
		addToSelectionNoNotification(o);
	}

	@Override
	public void objectRemovedFromSelection(Object o) {
		removeFromSelectionNoNotification(o);
	}

	@Override
	public void selectionResetted() {
		resetSelectionNoNotification();
	}

	@Override
	public boolean update() {
		super.update();
		updateSelected(false);
		// TODO: this should be not necessary
		// Vincent : It causes many notifications and for big browsers such as archimate emf metamodel one
		// it is tool long to produce the browser (35 seconds for this one).
		// Thus according to the TODO above, I commented it
		// getBrowserModel().fireTreeRestructured();
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.refreshObserving();
		}
		return true;
	}

	private final void updateSelected(boolean force) {

		if (getComponent() != null && getComponent().getSelected() != null) {
			try {
				if (getComponent().getSelected().isValid()
						&& getComponent().getSelected().getBindingValue(getBindingEvaluationContext()) != null) {
					T newSelectedObject = (T) getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
					if (notEquals(newSelectedObject, getSelected()) || force) {
						performSelect(newSelectedObject, force);
					}
				}
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/*@Override
	public void updateDataObject(final Object dataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(dataObject);
				}
			});
			return;
		}
		super.updateDataObject(dataObject);
		getBrowserModel().fireTreeRestructured();
	}*/

	@Override
	public T getSelected() {
		return selectedObject;
	}

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
					_tree.scrollPathToVisible(scrollTo);
				}
			}
		} else {
			clearSelection();
		}
	}

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
				_tree.scrollPathToVisible(scrollTo);
			}

		} else {
			clearSelection();
		}
	}

	public void setSelected(T object) {
		LOGGER.fine("Select " + object);
		if (getRootValue() == null) {
			return;
		}
		if (object == getSelected() /*&& !force*/) {
			LOGGER.fine("Ignore set selected object");
			return;
		}

		T oldSelectedObject = getSelected();

		selectedObject = object;

		getPropertyChangeSupport().firePropertyChange(SELECTED, oldSelectedObject, object);
	}

	public void clearSelection() {
		getTreeSelectionModel().clearSelection();
	}

	@Override
	public List<T> getSelection() {
		return selection;
	}

	public void setSelection(List<T> selection) {
		List<T> oldSelection = this.selection;
		this.selection = selection;
		getPropertyChangeSupport().firePropertyChange(SELECTION, oldSelection, selection);
	}

	private boolean ignoreNotifications = false;

	public synchronized void addToSelectionNoNotification(Object o) {
		ignoreNotifications = true;
		addToSelection(o);
		ignoreNotifications = false;
	}

	public synchronized void removeFromSelectionNoNotification(Object o) {
		ignoreNotifications = true;
		removeFromSelection(o);
		ignoreNotifications = false;
	}

	public synchronized void resetSelectionNoNotification() {
		ignoreNotifications = true;
		resetSelection();
		ignoreNotifications = false;
	}

	@Override
	public void addToSelection(Object o) {
		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTreeSelectionModel().addSelectionPath(path);
			getJTree().scrollPathToVisible(path);
		}
	}

	@Override
	public void removeFromSelection(Object o) {
		selection.remove(o);
		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTreeSelectionModel().removeSelectionPath(path);
		}
	}

	@Override
	public void resetSelection() {
		selection.clear();
		getTreeSelectionModel().clearSelection();
	}

	@Override
	public synchronized void valueChanged(TreeSelectionEvent e) {

		List<T> oldSelection = new ArrayList<T>(selection);
		T newSelectedObject;
		List<T> newSelection = new ArrayList<T>(selection);

		if (e.getNewLeadSelectionPath() == null || e.getNewLeadSelectionPath().getLastPathComponent() == null) {
			newSelectedObject = null;
		} else if (e.getNewLeadSelectionPath().getLastPathComponent() instanceof BrowserCell) {
			newSelectedObject = (T) ((BrowserCell) e.getNewLeadSelectionPath().getLastPathComponent()).getRepresentedObject();
			for (TreePath tp : e.getPaths()) {
				if (tp.getLastPathComponent() instanceof BrowserCell) {
					T obj = (T) ((BrowserCell) tp.getLastPathComponent()).getRepresentedObject();
					if (obj != null
							&& (getBrowser().getIteratorClass() == null || getBrowser().getIteratorClass().isAssignableFrom(obj.getClass()))) {
						if (e.isAddedPath(tp)) {
							if (!newSelection.contains(obj)) {
								newSelection.add(obj);
							}
						} else {
							newSelection.remove(obj);
						}
					}
				}
			}
		} else {
			newSelectedObject = null;
		}

		// logger.info("BrowserModel, selected object is now "+selectedObject);

		// System.out.println("selectedObject = " + selectedObject);
		// System.out.println("selection = " + selection);

		if (newSelectedObject == null) {
			setSelected(null);
		} else if (getBrowser().getIteratorClass() == null
				|| getBrowser().getIteratorClass().isAssignableFrom(newSelectedObject.getClass())) {
			setSelected(newSelectedObject);
		} else {
			// If selected element is not of expected class, set selected to be null
			// (we want to be sure that selected is an instance of IteratorClass)
			setSelected(null);
		}
		setSelection(newSelection);

		/*System.out.println("selectedObject=" + selectedObject);
		System.out.println("getComponent().getSelected()=" + getComponent().getSelected() + " of "
				+ getComponent().getSelected().getClass());
		System.out.println("getComponent().getSelected().isValid()=" + getComponent().getSelected().isValid());*/
		if (getComponent() != null) {
			if (getComponent().getSelected().isValid()) {
				LOGGER.fine("Sets SELECTED binding with " + getSelected());
				try {
					getComponent().getSelected().setBindingValue(getSelected(), getBindingEvaluationContext());
				} catch (TypeMismatchException e1) {
					e1.printStackTrace();
				} catch (NullReferenceException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (NotSettableContextException e1) {
					e1.printStackTrace();
				}
			}
		}

		// notifyDynamicModelChanged();

		updateFont();

		if (!ignoreNotifications) {
			FIBController ctrl = this.getController();
			if (ctrl != null) {
				ctrl.updateSelection(this, oldSelection, selection);
			} else {
				LOGGER.warning("INVESTIGATE: trying to update selection on a widget withour controlller! " + this.toString());
			}
		}

		_footer.setFocusedObject(newSelectedObject);

	}
}

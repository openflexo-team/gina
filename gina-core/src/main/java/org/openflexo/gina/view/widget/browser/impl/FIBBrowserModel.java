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

package org.openflexo.gina.view.widget.browser.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.toolbox.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class FIBBrowserModel extends DefaultTreeModel implements TreeModel {

	private static final Logger LOGGER = Logger.getLogger(FIBBrowserModel.class.getPackage().getName());

	private Map<FIBBrowserElement, FIBBrowserElementType> _elementTypes;
	private FIBBrowser _fibBrowser;
	private final Multimap<Object, BrowserCell> contents;

	private final FIBBrowserWidget widget;

	// Hacking zone:
	// Default behaviour is to update browser cells asynchronously in event-dispatch-thread
	// But in test environment, we may need to "force" the update to be done synchrounously
	public static boolean UPDATE_BROWSER_SYNCHRONOUSLY = false;

	/**
	 * Stores controls: key is the JButton and value the PropertyListActionListener
	 */
	// private Hashtable<JButton,PropertyListActionListener> _controls;

	public FIBBrowserModel(FIBBrowser fibBrowser, FIBBrowserWidget<?, ?> widget, FIBController controller) {
		super(null);
		contents = Multimaps.synchronizedMultimap(ArrayListMultimap.<Object, BrowserCell> create());
		_fibBrowser = fibBrowser;
		this.widget = widget;
		_elementTypes = new Hashtable<>();
		for (FIBBrowserElement browserElement : fibBrowser.getElements()) {
			addToElementTypes(browserElement, buildBrowserElementType(browserElement, controller));
		}

	}

	public FIBBrowserWidget<Object, TreePath> getWidget() {
		return widget;
	}

	public void delete() {
		for (FIBBrowserElement c : _elementTypes.keySet()) {
			_elementTypes.get(c).delete();
		}
		_elementTypes.clear();

		_elementTypes = null;
		_fibBrowser = null;
	}

	public FIBBrowserElement elementForObject(Object anObject) {
		return _fibBrowser.elementForObject(anObject);
	}

	public FIBBrowserElementType elementTypeForObject(Object anObject) {
		FIBBrowserElement element = elementForObject(anObject);

		if (element != null) {
			return _elementTypes.get(element);
		}
		else {
			LOGGER.warning("Could not find element for type " + anObject);
			/*System.out.println("Available=");
			for (FIBBrowserElement e : _fibBrowser.getElements()) {
				System.out.println("> " + e.getName() + " for " + e.getDataClass());
			}*/
			return null;
		}
	}

	public FIBBrowser getBrowser() {
		return _fibBrowser;
	}

	/**
	 * @param root
	 * @return flag indicating if change was required
	 */
	public boolean updateRootObject(Object root) {

		// System.out.println("updateRootObject with " + root);

		if (root == null) {
			// TODO: check this
			// logger.warning("Not implemented: please check this");
			setRoot(null);
			return false;
		}

		// nbOfBrowserCells = 0;

		BrowserCell rootCell = retrieveBrowserCell(root, null);
		// Creates cell if necesary
		if (rootCell == null) {
			rootCell = new BrowserCell(root, null);
			contents.put(root, rootCell);
		}
		if (getRoot() != rootCell) {

			// System.out.println("For " + hashCode() + ", root object switch from " + getRoot() + " to " + rootCell);
			// Thread.dumpStack();
			/*System.out
					.println("From: " + ((BrowserCell) getRoot()).getRepresentedObject() + " to: " + rootCell.getRepresentedObject());
			 */
			if (getRoot() != null) {
				((BrowserCell) getRoot()).delete();
			}
			LOGGER.fine("updateRootObject() with " + root + " rootCell=" + rootCell);
			setRoot(rootCell);
			// rootCell.loadChildren(this, null);

			return true;
		}
		return false;
	}

	public void fireTreeRestructured() {
		if (getRoot() instanceof BrowserCell) {
			((BrowserCell) getRoot()).update(true);
			// nodeStructureChanged((BrowserCell)getRoot());
		}
	}

	public void addToElementTypes(FIBBrowserElement element, FIBBrowserElementType elementType) {
		_elementTypes.put(element, elementType);
		elementType.setModel(this);
	}

	public void removeFromElementTypes(FIBBrowserElement element, FIBBrowserElementType elementType) {
		_elementTypes.remove(element);
		elementType.setModel(null);
	}

	public Map<FIBBrowserElement, FIBBrowserElementType> getElementTypes() {
		return _elementTypes;
	}

	private FIBBrowserElementType buildBrowserElementType(FIBBrowserElement browserElement, FIBController controller) {
		return new FIBBrowserElementType(browserElement, this, controller);
	}

	public TreePath[] getPaths(Object o) {
		Collection<BrowserCell> cells = contents.get(o);
		if (cells == null) {
			return new TreePath[0];
		}
		TreePath[] paths = new TreePath[cells.size()];
		int i = 0;
		for (BrowserCell cell : cells) {
			paths[i++] = getTreePath(cell);
		}
		return paths;
	}

	private TreePath getTreePath(BrowserCell cell) {
		Object[] path = getPathToRoot(cell);
		TreePath returned = new TreePath(path);
		return returned;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		BrowserCell cell = (BrowserCell) path.getLastPathComponent();
		if (cell.getBrowserElementType().isLabelEditable() && newValue instanceof String) {
			cell.getBrowserElementType().setEditableLabelFor(cell.getRepresentedObject(), (String) newValue);
		}
	}

	public Multimap<Object, BrowserCell> getContents() {
		return contents;
	}

	public String debugContents() {
		StringBuffer sb = new StringBuffer();
		if (getRoot() instanceof BrowserCell) {
			appendContents((BrowserCell) getRoot(), sb, 0);
		}
		return sb.toString();
	}

	private void appendContents(BrowserCell cell, StringBuffer sb, int indent) {
		sb.append(StringUtils.buildWhiteSpaceIndentation(indent * 2) + " > " + cell.getRepresentedObject() + "\n");
		for (int i = 0; i < cell.getChildCount(); i++) {
			if (cell.getChildAt(i) instanceof BrowserCell) {
				BrowserCell child = (BrowserCell) cell.getChildAt(i);
				appendContents(child, sb, indent + 1);
			}
		}
	}

	/**
	 * Obtain an iteration on all values which may be contained in this browser, by explicitely deeply-exploring all the tree.<br>
	 * Warning ! This method is dangerous for the perfs, and should never be called on a big "model"
	 * 
	 * @return
	 */
	public Iterator<Object> recursivelyExploreModelToRetrieveContents() {
		// We load all when not already up-t-date
		if (!exhaustiveContentsIsUpToDate) {
			computedExhaustiveContents.clear();
			if (getRoot() instanceof BrowserCell) {
				LOGGER.fine("!!!!! called recursivelyExploreModelToRetrieveContents() !!!!");
				((BrowserCell) getRoot()).update(true);
				exhaustiveContentsIsUpToDate = true;
			}
		}
		return contents.keys().iterator();
	}

	/**
	 * This set is used during exploration of all exhaustive contents, in order not no enter in an infinite loop
	 */
	private final Set<Object> computedExhaustiveContents = new HashSet<>();

	/**
	 * Flag indicating is exhaustive contents (obtained after deep-browsing) is up-to-date
	 */
	boolean exhaustiveContentsIsUpToDate = false;

	private BrowserCell retrieveBrowserCell(Object representedObject, BrowserCell parent) {
		/*System.out.println("retrieveBrowserCell for " + representedObject + " parent="
				+ (parent != null ? parent.getRepresentedObject() : "null"));*/
		ArrayList<BrowserCell> cells = new ArrayList<>(contents.get(representedObject));
		// Collection<BrowserCell> cells = contents.get(representedObject);
		if (cells != null) {
			for (BrowserCell cell : cells) {
				if (cell.getParent() == parent) {
					return cell;
				}
			}
		}
		return null; // Do not create it if not needed!
	}

	private void removeBrowserCell(BrowserCell cell) {
		contents.remove(cell.getRepresentedObject(), cell);
	}

	public boolean containsObject(Object representedObject) {
		return contents.get(representedObject) != null;
	}

	public Collection<BrowserCell> getBrowserCell(Object representedObject) {
		return contents.get(representedObject);
	}

	public class LoadingCell extends DefaultMutableTreeNode {
		public LoadingCell() {
			super("Loading...", false);
		}
	}

	final public static DataFlavor BROWSER_CELL_FLAVOR = new DataFlavor(BrowserCell.class, "BrowserCell");
	static DataFlavor flavors[] = { BROWSER_CELL_FLAVOR };

	public class BrowserCell extends DefaultMutableTreeNode implements Transferable/*, Serializable*/ {

		private boolean loaded = false;

		private FIBBrowserElementType browserElementType;
		private boolean isDeleted = false;
		private boolean isVisible = true;
		private final DynamicBindingEvaluationContextForBrowserCell dynamicBindingEvaluationContext;

		public BrowserCell(Object representedObject, BrowserCell father) {

			setParent(father);
			setAllowsChildren(true);
			setUserObject(representedObject);
			browserElementType = elementTypeForObject(representedObject);

			dynamicBindingEvaluationContext = new DynamicBindingEvaluationContextForBrowserCell();

			if (browserElementType != null) {

				// System.out.println("Build BrowserCell for " + representedObject);
				// System.out.println("elementType=" + browserElementType.getBrowserElement().getName());

				final List<?> newChildrenObjects = browserElementType.getChildrenFor(getRepresentedObject());

				// System.out.println("newChildrenObjects=" + newChildrenObjects);

				if (newChildrenObjects.size() > 0) {
					// System.out.println("For " + representedObject + " found " + newChildrenObjects.size() + " children: " +
					// newChildrenObjects);
					add(new LoadingCell());
				}

				/*if (getBrowserElement().getName() != null && getBrowserElement().getName().equals("diagramSpecification")) {
					logger.info("---------------> Created new DiagramSpecification browser element");
				}*/

				// dependingObjects = new DependingObjects(this);
				// dependingObjects.refreshObserving(browserElementType);

				browserElementType.getLabelFor(representedObject);

				listenLabelBindingValueChange(representedObject);
				listenIconBindingValueChange(representedObject);
				listenTooltipBindingValueChange(representedObject);
				listenEnabledBindingValueChange(representedObject);
				listenVisibleBindingValueChange(representedObject);
				listenChildrenDataBindingValueChange(representedObject);
				listenChildrenCastBindingValueChange(representedObject);
				listenChildrenVisibleBindingValueChange(representedObject);

			}

		}

		private BindingValueChangeListener<String> labelBindingValueChangeListener;
		private BindingValueChangeListener<Icon> iconBindingValueChangeListener;
		private BindingValueChangeListener<String> tooltipBindingValueChangeListener;
		private BindingValueChangeListener<Boolean> enabledBindingValueChangeListener;
		private BindingValueChangeListener<Boolean> visibleBindingValueChangeListener;
		private Map<FIBBrowserElementChildren, BindingValueChangeListener<?>> childrenDataBindingValueChangeListeners;
		private Map<FIBBrowserElementChildren, BindingValueChangeListener<?>> childrenCastBindingValueChangeListeners;
		private Map<FIBBrowserElementChildren, BindingValueChangeListener<Boolean>> childrenVisibleBindingValueChangeListeners;

		private void listenChildrenDataBindingValueChange(final Object representedObject) {
			if (childrenDataBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenDataBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenDataBindingValueChangeListeners.clear();
			}
			else {
				childrenDataBindingValueChangeListeners = new HashMap<>();
			}
			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.getChildrenFor(representedObject);
			if (browserElementType.getBrowserElement() != null) {
				for (final FIBBrowserElementChildren children : browserElementType.getBrowserElement().getChildren()) {
					if (children.getData().isValid()) {
						BindingValueChangeListener<?> l;
						if (children.isMultipleAccess()) {
							l = new BindingValueListChangeListener<Object, List<Object>>((DataBinding) children.getData(),
									/* browserElementType */
									// Instead of using browserElementType and to avoid to share it between multiple
									// BindingValueListChangeListener
									// using the same BindingEvaluationContext, we have to use here a proper instance of a
									// BindingEvaluationContext
									// dedicated to current BrowserCell
									dynamicBindingEvaluationContext) {
								@Override
								public void bindingValueChanged(Object source, List<Object> newValue) {
									/*System.out.println("For cell representing " + representedObject
											+ " bindingValueChanged() detected for data (as list) of children " + children.getName()
											+ " of " + browserElementType + " " + children.getData() + " with newValue=" + newValue
											+ " source=" + source);*/
									if (source == browserElementType) {
										// Ignore setIteratorObject() notification
										return;
									}
									if (!isDeleted) {
										BrowserCell.this.update(true);
									}
								}
							};
						}
						else {
							l = new BrowserCellBindingValueChangeListener<>(children.getData(), browserElementType);
						}
						childrenDataBindingValueChangeListeners.put(children, l);
					}
				}
			}
		}

		private void listenChildrenCastBindingValueChange(Object representedObject) {
			if (childrenCastBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenCastBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenCastBindingValueChangeListeners.clear();
			}
			else {
				childrenCastBindingValueChangeListeners = new HashMap<>();
			}
			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.getChildrenFor(representedObject);
			if (browserElementType.getBrowserElement() != null) {
				for (final FIBBrowserElementChildren children : browserElementType.getBrowserElement().getChildren()) {
					if (children.getCast().isValid()) {
						BindingValueChangeListener<?> l = new BrowserCellBindingValueChangeListener<>(children.getCast(),
								/* browserElementType */
								// Instead of using browserElementType and to avoid to share it between multiple
								// BindingValueListChangeListener
								// using the same BindingEvaluationContext, we have to use here a proper instance of a
								// BindingEvaluationContext
								// dedicated to current BrowserCell
								dynamicBindingEvaluationContext);
						childrenCastBindingValueChangeListeners.put(children, l);
					}
				}
			}
		}

		private void listenChildrenVisibleBindingValueChange(Object representedObject) {
			if (childrenVisibleBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenVisibleBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenVisibleBindingValueChangeListeners.clear();
			}
			else {
				childrenVisibleBindingValueChangeListeners = new HashMap<>();
			}

			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.getChildrenFor(representedObject);

			if (browserElementType.getBrowserElement() != null) {
				for (final FIBBrowserElementChildren children : browserElementType.getBrowserElement().getChildren()) {
					if (children.getVisible().isValid()) {
						BindingValueChangeListener<Boolean> l = new BrowserCellBindingValueChangeListener<>(children.getVisible(),
								/* browserElementType */
								// Instead of using browserElementType and to avoid to share it between multiple
								// BindingValueListChangeListener
								// using the same BindingEvaluationContext, we have to use here a proper instance of a
								// BindingEvaluationContext
								// dedicated to current BrowserCell
								dynamicBindingEvaluationContext);
						childrenVisibleBindingValueChangeListeners.put(children, l);
					}
				}
			}
		}

		private void listenLabelBindingValueChange(Object representedObject) {
			if (labelBindingValueChangeListener != null) {
				labelBindingValueChangeListener.stopObserving();
				labelBindingValueChangeListener.delete();
			}

			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.getLabelFor(representedObject);

			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getLabel().isValid()) {

				labelBindingValueChangeListener = new BrowserCellBindingValueChangeListener<>(
						browserElementType.getBrowserElement().getLabel(), /* browserElementType */
						// Instead of using browserElementType and to avoid to share it between multiple
						// BindingValueListChangeListener
						// using the same BindingEvaluationContext, we have to use here a proper instance of a
						// BindingEvaluationContext
						// dedicated to current BrowserCell
						dynamicBindingEvaluationContext);

			}
		}

		private void listenIconBindingValueChange(Object representedObject) {
			if (iconBindingValueChangeListener != null) {
				iconBindingValueChangeListener.stopObserving();
				iconBindingValueChangeListener.delete();
			}
			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.getIconFor(representedObject);
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getIcon().isValid()) {
				iconBindingValueChangeListener = new BrowserCellBindingValueChangeListener<>(
						browserElementType.getBrowserElement().getIcon(), /* browserElementType */
						// Instead of using browserElementType and to avoid to share it between multiple
						// BindingValueListChangeListener
						// using the same BindingEvaluationContext, we have to use here a proper instance of a
						// BindingEvaluationContext
						// dedicated to current BrowserCell
						dynamicBindingEvaluationContext);
			}
		}

		private void listenTooltipBindingValueChange(Object representedObject) {
			if (tooltipBindingValueChangeListener != null) {
				tooltipBindingValueChangeListener.stopObserving();
				tooltipBindingValueChangeListener.delete();
			}
			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.getTooltipFor(representedObject);
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getTooltip().isValid()) {
				tooltipBindingValueChangeListener = new BrowserCellBindingValueChangeListener<>(
						browserElementType.getBrowserElement().getTooltip(), /* browserElementType */
						// Instead of using browserElementType and to avoid to share it between multiple
						// BindingValueListChangeListener
						// using the same BindingEvaluationContext, we have to use here a proper instance of a
						// BindingEvaluationContext
						// dedicated to current BrowserCell
						dynamicBindingEvaluationContext);
			}
		}

		private void listenEnabledBindingValueChange(Object representedObject) {
			if (enabledBindingValueChangeListener != null) {
				enabledBindingValueChangeListener.stopObserving();
				enabledBindingValueChangeListener.delete();
			}
			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.isEnabled(representedObject);
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getEnabled().isValid()) {
				enabledBindingValueChangeListener = new BrowserCellBindingValueChangeListener<>(
						browserElementType.getBrowserElement().getEnabled(), /* browserElementType */
						// Instead of using browserElementType and to avoid to share it between multiple
						// BindingValueListChangeListener
						// using the same BindingEvaluationContext, we have to use here a proper instance of a
						// BindingEvaluationContext
						// dedicated to current BrowserCell
						dynamicBindingEvaluationContext);
			}
		}

		private void listenVisibleBindingValueChange(Object representedObject) {
			if (visibleBindingValueChangeListener != null) {
				visibleBindingValueChangeListener.stopObserving();
				visibleBindingValueChangeListener.delete();
			}
			// This is really important to this now
			// This will set the representedObject as iteratorObject, allowing to perform a correct observing
			browserElementType.isVisible(representedObject);
			if (browserElementType.getBrowserElement() != null && browserElementType.getBrowserElement().getVisible().isValid()) {
				visibleBindingValueChangeListener = new BrowserCellBindingValueChangeListener<>(
						browserElementType.getBrowserElement().getVisible(), /* browserElementType */
						// Instead of using browserElementType and to avoid to share it between multiple
						// BindingValueListChangeListener
						// using the same BindingEvaluationContext, we have to use here a proper instance of a
						// BindingEvaluationContext
						// dedicated to current BrowserCell
						dynamicBindingEvaluationContext);
			}
		}

		/**
		 * A {@link BindingEvaluationContext} dedicated to compute binding in the context of this {@link BrowserCell} (access to represented
		 * object aka user object)
		 * 
		 * @author sylvain
		 *
		 */
		private final class DynamicBindingEvaluationContextForBrowserCell implements BindingEvaluationContext {
			// private final Object representedObject;

			private DynamicBindingEvaluationContextForBrowserCell(/*Object representedObject*/) {
				// this.representedObject = representedObject;
			}

			@Override
			public Object getValue(BindingVariable variable) {
				FIBBrowserElement browserElement = getBrowserElement();
				String variableName = variable.getVariableName();
				if (browserElement != null && Objects.equals(variableName, browserElement.getName())) {
					return getUserObject(); // representedObject;
				}
				else if (variableName.equals("object")) {
					return getUserObject(); // representedObject;
				}
				else if (widget != null && widget.getController() != null) {
					return widget.getValue(variable);
				}
				else {
					return null;
				}
			}
		}

		/**
		 * Specific implementation of {@link BindingValueChangeListener} for BrowserCell<br>
		 * 
		 * The main difficulty is caused by the fact that iterator object is really dynamic<br>
		 * To efficiently observe modifications of values, we should here ignore the setIteratorObject() calls
		 * 
		 * @author sylvain
		 * 
		 * @param <T>
		 */
		protected class BrowserCellBindingValueChangeListener<T> extends BindingValueChangeListener<T> {

			public BrowserCellBindingValueChangeListener(DataBinding<T> dataBinding, BindingEvaluationContext context) {
				super(dataBinding, context);
			}

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() == browserElementType) {
					// Ignore setIteratorObject() notification
					return;
				}
				super.propertyChange(evt);
			}

			@Override
			public void bindingValueChanged(Object source, T newValue) {
				if (source == browserElementType) {
					// Ignore setIteratorObject() notification
					return;
				}
				if (!isDeleted) {
					updateBrowserCell();
				}
			}

			// Might be overriden when required
			public void updateBrowserCell() {
				BrowserCell.this.update(false);
			}
		}

		@Override
		public BrowserCell getParent() {
			return (BrowserCell) super.getParent();
		}

		final public Object getRepresentedObject() {
			return getUserObject();
		}

		public void loadChildren(final DefaultTreeModel model, final PropertyChangeListener progressListener) {
			if (loaded) {
				return;
			}

			loaded = true;

			update(false);

		}

		public boolean isLoaded() {
			return loaded;
		}

		/*@Override
		public List<DataBinding<?>> getDependencyBindings() {
			return getBrowserElementType().getDependencyBindings(getRepresentedObject());
		}*/

		// TODO: repair chained bindings, which is not more supported by this new implementation
		/*@Override
		public List<TargetObject> getChainedBindings(DataBinding binding, final TargetObject object) {
			for (FIBBrowserElementChildren child : browserElementType.getBrowserElement().getChildren()) {
				if (binding.equals(child.getData()) && child.getCast().isSet() && binding.toString().endsWith(object.propertyName)) {
					try {
						final Object bindingValue = child.getData().getBindingValue(browserElementType);
						List<?> list = ToolBox.getListFromIterable(bindingValue);
						if (list != null) {
							List<TargetObject> targetObjects = new ArrayList<TargetObject>();
							for (final Object o : list) {
								List<TargetObject> targetObjects2 = child.getCast().getTargetObjects(new BindingEvaluationContext() {
		
									@Override
									public Object getValue(BindingVariable variable) {
										if (variable.getVariableName().equals("child")) {
											return o;
										} else {
											return browserElementType.getValue(variable);
										}
									}
								});
								if (targetObjects2 != null) {
									targetObjects.addAll(targetObjects2);
								}
							}
							return targetObjects;
						} else {
							return child.getCast().getTargetObjects(new BindingEvaluationContext() {
		
								@Override
								public Object getValue(BindingVariable variable) {
									if (variable.getVariableName().equals("child")) {
										return bindingValue;
									} else {
										return browserElementType.getValue(variable);
									}
								}
							});
		
						}
					} catch (TypeMismatchException e) {
						continue;
					} catch (NullReferenceException e) {
						continue;
					} catch (InvocationTargetException e) {
						continue;
					}
		
				}
			}
			return null;
		}*/

		public void delete() {

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Delete BrowserCell for " + getRepresentedObject());
			}

			if (childrenDataBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenDataBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenDataBindingValueChangeListeners.clear();
			}
			if (childrenCastBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenCastBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenCastBindingValueChangeListeners.clear();
			}
			if (childrenVisibleBindingValueChangeListeners != null) {
				for (BindingValueChangeListener<?> l : childrenVisibleBindingValueChangeListeners.values()) {
					l.stopObserving();
					l.delete();
				}
				childrenVisibleBindingValueChangeListeners.clear();
			}

			if (labelBindingValueChangeListener != null) {
				labelBindingValueChangeListener.stopObserving();
				labelBindingValueChangeListener.delete();
			}
			if (iconBindingValueChangeListener != null) {
				iconBindingValueChangeListener.stopObserving();
				iconBindingValueChangeListener.delete();
			}
			if (tooltipBindingValueChangeListener != null) {
				tooltipBindingValueChangeListener.stopObserving();
				tooltipBindingValueChangeListener.delete();
			}
			if (enabledBindingValueChangeListener != null) {
				enabledBindingValueChangeListener.stopObserving();
				enabledBindingValueChangeListener.delete();
			}
			if (visibleBindingValueChangeListener != null) {
				visibleBindingValueChangeListener.stopObserving();
				visibleBindingValueChangeListener.delete();
			}

			if (children != null) {
				for (Object c : new ArrayList<>(children)) {
					if (c instanceof BrowserCell) {
						((BrowserCell) c).delete();
					}
				}
			}

			if (getRepresentedObject() != null) {
				removeBrowserCell(this);
			}

			/*
			 * GPO: Commented next line. We should check why we drop this representedObject from the selection
			 * Why not also check if we are the current selected object?
			 * By all means, we should find another way to do this than by doing it in the TreeModel. We could do that in FIBBrowserWidget.treeNodesRemoved. 
			if (selection.contains(representedObject)) {
				selection.remove(representedObject);
			}
			 */
			setUserObject(null);
			browserElementType = null;
			setParent(null);
			if (children != null) {
				children.clear();
			}
			isDeleted = true;
		}

		private boolean updateRequested = false;

		public void update(boolean recursively) {
			if (isUpdating) {
				return;
			}

			if (SwingUtilities.isEventDispatchThread() || UPDATE_BROWSER_SYNCHRONOUSLY) {
				if (!updateRequested) {
					updateRequested = true;
					updateSync(recursively);
				}
			}
			else if (!updateRequested) {
				updateRequested = true;
				SwingUtilities.invokeLater(() -> updateSync(recursively));
			}
		}

		private boolean isUpdating;

		private void updateSync(boolean recursively) {

			/*if (getParent() != null) {
				System.out.println("updateSync for " + getRepresentedObject() + " parent=" + getParent().getRepresentedObject()
						+ " isUpdating=" + getParent().isUpdating);
			}*/

			try {

				isUpdating = true;
				loaded = true;

				// During exploration of all exhaustive contents, in order not no enter in an infinite loop
				if (recursively) {
					computedExhaustiveContents.add(getRepresentedObject());
				}

				// logger.info("**************** update() " + this);
				if (browserElementType == null) {
					LOGGER.warning("No element type registered for " + getRepresentedObject());
					return;
				}

				// Special case for cells that were declared as invisible
				// When becomes visible, must tells to parent to update
				if (!isVisible) {
					if (browserElementType.isVisible(getRepresentedObject())) {
						LOGGER.fine("Cell " + this + " becomes visible");

						// should not update recursively, or else it will crash....
						isVisible = true;
						// getParent().update(recursively);
						getParent().update(false);
					}
				}

				// / Only updates children if Node is Expanded

				boolean isExpanded = widget.getRenderingAdapter().isExpanded(widget.getTechnologyComponent(), this.getTreePath());

				List<BrowserCell> oldChildren = null;
				List<BrowserCell> removedChildren = null;
				List<BrowserCell> newChildren = null;
				List<BrowserCell> cellsToForceUpdate = null;

				if (children == null) {
					oldChildren = Collections.emptyList();
					removedChildren = Collections.emptyList();
				}
				else {
					if (children.size() == 1 && children.firstElement() instanceof LoadingCell) {
						removeAllChildren();
					}
					oldChildren = new ArrayList(children);
					removedChildren = new ArrayList(children);
				}

				final List<?> newChildrenObjects = /*(isEnabled ?*/browserElementType
						.getChildrenFor(getRepresentedObject()) /*: new Vector())*/;
				int index = 0;

				if (!newChildrenObjects.isEmpty()) {
					newChildren = new ArrayList<>();

					// Optimization : do not need to create list if no children to update
					if (recursively) {
						cellsToForceUpdate = new ArrayList<>();
					}

					for (Object o : newChildrenObjects) {
						Object ro = getRepresentedObject();
						if (o != null && o != ro) {
							FIBBrowserElementType childElementType = elementTypeForObject(o);
							BrowserCell cell = retrieveBrowserCell(o, this);
							if (childElementType != null && childElementType.isVisible(o)) {
								if (isVisible && cell == null) { // Creates cell when necessary
									cell = new BrowserCell(o, this);
									contents.put(o, cell);
								}
								if (cell != null) {
									if (children != null && children.contains(cell)) {

										// OK, child still here
										removedChildren.remove(cell);
										if (recursively) {
											cellsToForceUpdate.add(cell);
										}
										index = children.indexOf(cell) + 1;
									}
									else {
										newChildren.add(cell);
										if (children == null) {
											children = new Vector<>();
										}
										children.insertElementAt(cell, index);
										index++;

										// In order not to enter in possibly infinite loop, force update contents of this cell
										// only if cell not loaded and if represented object was not already registered
										if (recursively && !cell.isLoaded()
												&& !computedExhaustiveContents.contains(cell.getRepresentedObject())) {
											// Do it at the end
											cellsToForceUpdate.add(cell);
											// cell.update(true);
										}
									}
								}

							}
							else if (cell != null) {
								cell.isVisible = false;
							}
						}
					}
				}
				else {
					newChildren = Collections.emptyList();
				}

				for (BrowserCell c : removedChildren) {
					if (children != null) {
						children.remove(c);
					}
					c.delete();
				}

				boolean requireSorting = false;
				if (children != null) {
					for (int i = 0; i < children.size() - 1; i++) {
						BrowserCell c1 = (BrowserCell) children.elementAt(i);
						BrowserCell c2 = (BrowserCell) children.elementAt(i + 1);
						if (c1 != null && c2 != null && newChildrenObjects
								.indexOf(c1.getRepresentedObject()) != newChildrenObjects.indexOf(c2.getRepresentedObject()) - 1) {
							requireSorting = true;
						}
					}
				}

				if (requireSorting) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Detected sorting required");
					}
					// Sort children according to supplied list
					Collections.sort(children, new Comparator<TreeNode>() {
						@Override
						public int compare(TreeNode o1, TreeNode o2) {
							Object obj1 = ((BrowserCell) o1).getRepresentedObject();
							Object obj2 = ((BrowserCell) o2).getRepresentedObject();
							return newChildrenObjects.indexOf(obj1) - newChildrenObjects.indexOf(obj2);
						}
					});
				}

				// System.out.println("removedChildren ["+removedChildren.size()+"] "+removedChildren);
				// System.out.println("newChildren ["+newChildren.size()+"] "+newChildren);
				// System.out.println("children ["+children.size()+"] "+children);

				// Unused boolean structureChanged = false;

				if (removedChildren.size() > 0 || newChildren.size() > 0) {
					// Unused structureChanged = true;
					exhaustiveContentsIsUpToDate = false;
					if (oldChildren.size() == 0) {
						// Special case, i don't undertand why (SGU)
						// OK, issue seems to be MacOS only but workaround works on all platforms.
						// To observe the issue, load WKF module on a project that imports other projects
						// Imported workflow tree is not correctly initiated after reload of project.
						try {
							nodeStructureChanged(this);
						} catch (Exception e) {
							// Might happen when a structural modification will call parent's nodeChanged()
							// An Exception might be raised here
							// We should investigate further, but since no real consequences are raised here, we just ignore exception
							e.printStackTrace();
							LOGGER.warning("Unexpected " + e.getClass().getSimpleName()
									+ " when refreshing browser, no severity but please investigate");
						}
					}
					else {
						if (removedChildren.size() > 0) {
							int[] childIndices = new int[removedChildren.size()];
							Object[] removedChildrenObjects = new Object[removedChildren.size()];
							for (int i = 0; i < removedChildren.size(); i++) {
								childIndices[i] = oldChildren.indexOf(removedChildren.get(i));
								removedChildrenObjects[i] = removedChildren.get(i);
							}
							try {
								nodesWereRemoved(this, childIndices, removedChildrenObjects);
							} catch (Exception e) {
								e.printStackTrace();
								LOGGER.warning("Unexpected exception: " + e);
							}
						}
						if (newChildren.size() > 0) {
							int[] childIndices = new int[newChildren.size()];
							for (int i = 0; i < newChildren.size(); i++) {
								childIndices[i] = children.indexOf(newChildren.get(i));
							}
							try {
								nodesWereInserted(this, childIndices);
							} catch (EmptyStackException e) {
								// TODO: please investigate
								LOGGER.warning("Unexpected exception: " + e);
							} catch (Exception e) {
								e.printStackTrace();
								LOGGER.warning("Unexpected exception: " + e);
							}
						}
					}
				}

				try {
					nodeChanged(this);
				} catch (ArrayIndexOutOfBoundsException e) {
					// Might happen when a structural modification will call parent's nodeChanged()
					// An ArrayIndexOutOfBoundsException might be raised here
					// We should investigate further, but since no real consequences are raised here, we just ignore exception
					// e.printStackTrace();
					LOGGER.warning("Unexpected ArrayIndexOutOfBoundsException when refreshing browser, no severity but please investigate");
					e.printStackTrace(System.out);
					nodeStructureChanged(this);
				} catch (NullPointerException e) {
					// Might happen when a structural modification will call parent's nodeChanged()
					// An NullPointerException might be raised here
					// We should investigate further, but since no real consequences are raised here, we just ignore exception
					// e.printStackTrace();
					LOGGER.warning("Unexpected NullPointerException when refreshing browser, no severity but please investigate");
				}

				if (requireSorting) {

					/*Object wasSelected = widget.getSelectedObject();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Will reselect " + wasSelected);
					}*/

					try {
						nodeStructureChanged(this);
					} catch (Exception e) {
						// Might happen when a structural modification will call parent's nodeChanged()
						// An Exception might be raised here
						// We should investigate further, but since no real consequences are raised here, we just ignore exception
						e.printStackTrace();
						LOGGER.warning("Unexpected " + e.getClass().getSimpleName()
								+ " when refreshing browser, no severity but please investigate");
					}

					/*if (wasSelected != null) {
						widget.resetSelection();
						widget.addToSelection(wasSelected);
					}*/

				}

				/*if (wasSelected) {
					widget.addToSelection(representedObject);
				}*/

				// dependingObjects.refreshObserving(browserElementType);

				if (cellsToForceUpdate != null) {
					for (BrowserCell cell : cellsToForceUpdate) {
						if (cell != this) // prevent multiple update of same cell
							// Update recursively only if node is Expanded or if recursively called, otherwise only the children are
							// updated
							cell.update(isExpanded || recursively);
					}
				}

			} finally {
				updateRequested = false;
				isUpdating = false;
			}
		}

		/*@Override
		public void update(Observable o, Object arg) {
			// logger.info("Object " + o + " received " + arg);
		
			if (!isDeleted && o == getRepresentedObject()) {
				update(false);
			}
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// logger.info("Object " + representedObject + " received " + evt);
		
			if (!isDeleted) {
				// System.out.println("cell " + this + " propertyChanged " + evt.getPropertyName() + " for " + evt.getSource());
				update(false);
			}
		}*/

		public FIBBrowserElement getBrowserElement() {
			if (browserElementType != null) {
				return browserElementType.getBrowserElement();
			}
			return null;
		}

		public FIBBrowserElementType getBrowserElementType() {
			return browserElementType;
		}

		public TreePath getTreePath() {
			return new TreePath(getPathToRoot(this, 0));
		}

		// --------- Transferable --------------

		@Override
		public boolean isDataFlavorSupported(DataFlavor df) {
			return df.equals(BROWSER_CELL_FLAVOR);
		}

		/** implements Transferable interface */
		@Override
		public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
			if (df.equals(BROWSER_CELL_FLAVOR)) {
				return new TransferedBrowserCell();
			}
			else
				throw new UnsupportedFlavorException(df);
		}

		/** implements Transferable interface */
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		// --------- Serializable --------------

		/*private void writeObject(java.io.ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
		}
		
		private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();
		}*/

	}

	public static class TransferedBrowserCell implements Serializable {
		// private final Point offset;

		// private final ShapeNode<?> transfered;

		public TransferedBrowserCell(/*ShapeNode<?> element, Point dragOffset*/) {
			super();
			// transfered = element;
			// offset = dragOffset;
		}

		/*public Point getOffset() {
			return offset;
		}
		
		public ShapeNode<?> getTransferedElement() {
			return transfered;
		}*/

	}

}

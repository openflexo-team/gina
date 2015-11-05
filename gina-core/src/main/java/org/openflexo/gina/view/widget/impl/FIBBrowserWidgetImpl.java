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

package org.openflexo.gina.view.widget.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBBrowserWidget;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;

/**
 * Base implementation for a browser (a tree of various objects)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of row data
 * 
 * @author sylvain
 */
public abstract class FIBBrowserWidgetImpl<C, T> extends FIBWidgetViewImpl<FIBBrowser, C, T>
		implements FIBBrowserWidget<C, T>, TreeSelectionListener {

	private static final Logger LOGGER = Logger.getLogger(FIBBrowserWidgetImpl.class.getPackage().getName());

	public static final String SELECTED = "selected";
	public static final String SELECTION = "selection";

	private FIBBrowserModel _browserModel;
	private final FIBBrowserWidgetFooter<?, T> _footer;

	private T selectedObject;
	private List<T> selection;

	private BindingValueChangeListener<T> selectedBindingValueChangeListener;
	private BindingValueListChangeListener<T, List<T>> selectionBindingValueChangeListener;
	private BindingValueChangeListener<Object> rootBindingValueChangeListener;

	public FIBBrowserWidgetImpl(FIBBrowser fibBrowser, FIBController controller, BrowserRenderingAdapter<C, T> RenderingAdapter) {
		super(fibBrowser, controller, RenderingAdapter);

		_footer = makeFooter();

		selection = new ArrayList<T>();

		// buildBrowser();

		listenSelectedValueChange();
		listenSelectionValueChange();
		listenRootValueChange();
	}

	public abstract FIBBrowserWidgetFooter<?, T> makeFooter();

	@Override
	public BrowserRenderingAdapter<C, T> getRenderingAdapter() {
		return (BrowserRenderingAdapter<C, T>) super.getRenderingAdapter();
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
			selectionBindingValueChangeListener = new BindingValueListChangeListener<T, List<T>>(
					((DataBinding) getComponent().getSelection()), getBindingEvaluationContext()) {
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

			rootBindingValueChangeListener = new BindingValueChangeListener<Object>(getComponent().getRoot(),
					getBindingEvaluationContext()) {

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
		return getWidget();
	}

	public FIBBrowserModel getBrowserModel() {
		if (_browserModel == null) {
			_browserModel = new FIBBrowserModel(getBrowser(), this, getController());
		}
		return _browserModel;
	}

	public FIBBrowserWidgetFooter<?, T> getFooter() {
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

	public boolean processRootChanged() {
		boolean returned = getBrowserModel().updateRootObject(getRootValue());
		// If root object has changed, this might be very usefull to update selected, too !!!
		updateSelected(true);
		updateSelection(true);
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

		if (getRenderingAdapter().isEditing(getTechnologyComponent())) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(getComponent().getName() + "  - Tree is currently editing");
			}
			getRenderingAdapter().cancelCellEditing(getTechnologyComponent());
		}
		else {
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

		// logger.info("Bon, je remets a jour la selection du browser,
		// value="+getComponent().getSelected().getBindingValue(getController())+" was: "+getSelectedObject());
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
		return getRenderingAdapter().getTreeSelectionModel(getTechnologyComponent());
	}

	@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateBrowser();
		// TODO: do not forget to localize actions here
		/*for (FIBTableAction a : getWidget().getActions()) {
			if (getWidget().getLocalize()) getLocalized(a.getName());
		}*/
	}

	@Override
	public void updateBrowser() {
		deleteBrowser();

		if (_browserModel != null) {
			_browserModel.delete();
			_browserModel = null;
		}

		technologyComponent = makeTechnologyComponent();

		update();
		// updateDataObject(getDataObject());
	}

	public abstract void deleteBrowser();

	@Override
	public boolean synchronizedWithSelection() {
		return getWidget().getBoundToSelectionManager();
	}

	@Override
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
		updateSelectionMode();
		updateSelected(false);
		updateSelection(false);
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

	protected abstract void updateSelectionMode();

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

	private final void updateSelection(boolean force) {

		/*if (getComponent() != null && getComponent().getSelection() != null) {
			System.out.println("************** updateSelection with " + getComponent().getSelection() + " valid="
					+ getComponent().getSelection().isValid() + " reason=" + getComponent().getSelection().invalidBindingReason());
		}*/

		if (getComponent() != null && getComponent().getSelection() != null && getComponent().getSelection().isValid()) {
			try {
				List<T> newSelection = getComponent().getSelection().getBindingValue(getBindingEvaluationContext());
				performSelect(newSelection, force);
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

		if (getBrowserModel().getPaths(o).length == 0 && getBrowser().getDeepExploration()) {
			// No matches yet, but we may recursively and exhaustively explore the whole model to retrieve all contents
			getBrowserModel().recursivelyExploreModelToRetrieveContents();
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

		if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getLastPathComponent() != null
				&& e.getNewLeadSelectionPath().getLastPathComponent() instanceof BrowserCell) {
			for (TreePath tp : e.getPaths()) {
				System.out.println("PATH : " + tp.getPath());
				/*if (tp.getLastPathComponent() instanceof BrowserCell) {
					BrowserCell cell = (BrowserCell) tp.getLastPathComponent();
					system.out.println(cell.);
				}*/

				/*for (Object to : tp.getPath()) {
					if (to instanceof BrowserCell) {
						BrowserCell cell = (BrowserCell) to;
						
						System.out.println();
					}
				}*/
			}
		}

		GinaStackEvent stack = GENotifier.raise(FIBEventFactory.getInstance().createMouseEvent("selected"));

		if (selection == null) {
			selection = new ArrayList<T>();
		}

		List<T> oldSelection = new ArrayList<T>(selection);
		T newSelectedObject;
		List<T> newSelection = new ArrayList<T>(selection);

		if (e.getNewLeadSelectionPath() == null || e.getNewLeadSelectionPath().getLastPathComponent() == null) {
			newSelectedObject = null;
		}
		else if (e.getNewLeadSelectionPath().getLastPathComponent() instanceof BrowserCell) {
			newSelectedObject = (T) ((BrowserCell) e.getNewLeadSelectionPath().getLastPathComponent()).getRepresentedObject();
			for (TreePath tp : e.getPaths()) {
				if (tp.getLastPathComponent() instanceof BrowserCell) {
					T obj = (T) ((BrowserCell) tp.getLastPathComponent()).getRepresentedObject();
					if (obj != null && (getBrowser().getIteratorClass() == null
							|| getBrowser().getIteratorClass().isAssignableFrom(obj.getClass()))) {
						if (e.isAddedPath(tp)) {
							if (!newSelection.contains(obj)) {
								newSelection.add(obj);
							}
						}
						else {
							newSelection.remove(obj);
						}
					}
				}
			}
		}
		else {
			newSelectedObject = null;
		}

		// logger.info("BrowserModel, selected object is now "+selectedObject);

		// System.out.println("selectedObject = " + selectedObject);
		System.out.println("selection = " + newSelection);

		if (newSelectedObject == null) {
			setSelected(null);
		}
		else if (getBrowser().getIteratorClass() == null
				|| getBrowser().getIteratorClass().isAssignableFrom(newSelectedObject.getClass())) {
			setSelected(newSelectedObject);
		}
		else {
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
			}
			else {
				LOGGER.warning("INVESTIGATE: trying to update selection on a widget without controlller! " + this.toString());
			}
		}

		_footer.setFocusedObject(newSelectedObject);

		stack.end();
	}

	@Override
	public void performExpand(T o) {

		if (getBrowserModel().getPaths(o).length == 0 && getBrowser().getDeepExploration()) {
			// No matches yet, but we may recursively and exhaustively explore the whole model to retrieve all contents
			getBrowserModel().recursivelyExploreModelToRetrieveContents();
		}

	}

}

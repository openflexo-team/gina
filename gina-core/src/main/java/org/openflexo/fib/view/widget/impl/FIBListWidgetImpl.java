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

package org.openflexo.fib.view.widget.impl;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.view.widget.FIBListWidget;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBSelectionEventDescription;
import org.openflexo.gina.event.description.item.DescriptionIntegerItem;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.gina.manager.GinaStackEvent;

public abstract class FIBListWidgetImpl<C, T> extends FIBMultipleValueWidgetImpl<FIBList, C, T, T>
		implements FIBListWidget<C, T>, FIBSelectable<T> {

	static final Logger LOGGER = Logger.getLogger(FIBListWidgetImpl.class.getPackage().getName());

	public static final String SELECTION = "selection";

	private List<T> selection;

	public FIBListWidgetImpl(FIBList model, FIBController controller, ListRenderingTechnologyAdapter<C> renderingTechnologyAdapter) {
		super(model, controller, renderingTechnologyAdapter);

		updateMultipleValues();
		updateVisibleRowCount();
		updateRowHeight();
		updateFont();
	}

	@Override
	public ListRenderingTechnologyAdapter<C> getRenderingTechnologyAdapter() {
		return (ListRenderingTechnologyAdapter<C>) super.getRenderingTechnologyAdapter();
	}

	public void updateVisibleRowCount() {
		if (getWidget().getVisibleRowCount() != null && getWidget().getVisibleRowCount() > 0) {
			getRenderingTechnologyAdapter().setVisibleRowCount(getDynamicJComponent(), getWidget().getVisibleRowCount());
		}
	}

	public void updateRowHeight() {
		if (getWidget().getRowHeight() != null && getWidget().getRowHeight() > 0) {
			getRenderingTechnologyAdapter().setRowHeight(getDynamicJComponent(), getWidget().getVisibleRowCount());
		}
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

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// updateListModelWhenRequired();
		if (getWidget().getData() != null
				&& notEquals(getValue(), getRenderingTechnologyAdapter().getSelectedItem(getDynamicJComponent()))) {

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateWidgetFromModel()");
			}
			widgetUpdating = true;
			getRenderingTechnologyAdapter().setSelectedItem(getDynamicJComponent(), getValue());
			widgetUpdating = false;
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		T newValue = (T) getRenderingTechnologyAdapter().getSelectedItem(getDynamicJComponent());
		if (notEquals(getValue(), newValue)) {
			modelUpdating = true;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateModelFromWidget with " + newValue);
			}
			if (newValue != null && !widgetUpdating) {
				setValue(newValue);
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	public FIBListModel getListModel() {
		return (FIBListModel) super.getMultipleValueModel();
	}

	@Override
	protected FIBListModel createMultipleValueModel() {
		return new FIBListModel();
	}

	@Override
	protected void proceedToListModelUpdate() {
		proceedToListModelUpdate(getListModel());
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;
		System.out.println("Execute");

		switch (e.getAction()) {
			case FIBSelectionEventDescription.SELECTED: {
				FIBSelectionEventDescription se = (FIBSelectionEventDescription) e;

				for (DescriptionItem item : se.getValues()) {
					if (item instanceof DescriptionIntegerItem) {
						DescriptionIntegerItem intItem = (DescriptionIntegerItem) item;
						if (item.getAction().equals(FIBSelectionEventDescription.SELECTED))
							getListSelectionModel().addSelectionInterval(intItem.getValue(), intItem.getValue());
						if (item.getAction().equals(FIBSelectionEventDescription.DESELECTED))
							getListSelectionModel().removeSelectionInterval(intItem.getValue(), intItem.getValue());
					}
				}

				break;
			}
		}

		System.out.println("Execute end");
		widgetExecuting = false;
	}

	private final FIBListModel oldListModel = null;

	protected abstract void proceedToListModelUpdate(FIBListModel aListModel);

	@SuppressWarnings("serial")
	protected class FIBListModel extends FIBMultipleValueModel implements ListSelectionListener {
		private T selectedObject;
		private final List<T> selection;

		public FIBListModel() {
			super();
			selectedObject = null;
			selection = new ArrayList<T>();
		}

		public T getSelectedObject() {
			return selectedObject;
		}

		public List<T> getSelection() {
			return selection;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// Ignore extra messages.
			if (e.getValueIsAdjusting()) {
				return;
			}

			GinaStackEvent stack = GENotifier.raise(FIBEventFactory.getInstance().createSelectionEvent(getListSelectionModel(),
					getListSelectionModel().getLeadSelectionIndex(), e.getFirstIndex(), e.getLastIndex()));

			if (widgetUpdating) {
				stack.end();
				return;
			}

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("valueChanged() selected index=" + getListSelectionModel().getMinSelectionIndex());
			}

			updateModelFromWidget();

			int i = getListSelectionModel().getMinSelectionIndex();
			int leadIndex = getListSelectionModel().getLeadSelectionIndex();
			if (!getListSelectionModel().isSelectedIndex(leadIndex)) {
				leadIndex = getListSelectionModel().getAnchorSelectionIndex();
			}
			while (!getListSelectionModel().isSelectedIndex(leadIndex) && i <= getListSelectionModel().getMaxSelectionIndex()) {
				leadIndex = i;
				i++;
			}

			selectedObject = getElementAt(leadIndex);

			List<T> oldSelection = selection;
			List<T> newSelection = new ArrayList<T>();
			for (i = getListSelectionModel().getMinSelectionIndex(); i <= getListSelectionModel().getMaxSelectionIndex(); i++) {
				if (getListSelectionModel().isSelectedIndex(i)) {
					newSelection.add(getElementAt(i));
				}
			}

			setSelected(selectedObject);
			setSelectedIndex(leadIndex);
			setSelection(newSelection);

			if (getComponent().getSelected().isValid()) {
				LOGGER.fine("Sets SELECTED binding with " + selectedObject);
				try {
					getComponent().getSelected().setBindingValue(selectedObject, getBindingEvaluationContext());
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

			updateFont();

			if (!ignoreNotifications) {
				getController().updateSelection(FIBListWidgetImpl.this, oldSelection, selection);
			}

			LOGGER.fine((isFocused() ? "LEADER" : "SECONDARY") + " Selected is " + selectedObject);
			LOGGER.fine((isFocused() ? "LEADER" : "SECONDARY") + " Selection is " + selection);

			stack.end();
		}

		private boolean ignoreNotifications = false;

		public void addToSelectionNoNotification(Object o) {
			int index = indexOf(o);
			ignoreNotifications = true;
			getListSelectionModel().addSelectionInterval(index, index);
			ignoreNotifications = false;
		}

		public void removeFromSelectionNoNotification(Object o) {
			int index = indexOf(o);
			ignoreNotifications = true;
			getListSelectionModel().removeSelectionInterval(index, index);
			ignoreNotifications = false;
		}

		public void resetSelectionNoNotification() {
			ignoreNotifications = true;
			getListSelectionModel().clearSelection();
			ignoreNotifications = false;
		}

		public void addToSelection(Object o) {
			int index = indexOf(o);
			getListSelectionModel().addSelectionInterval(index, index);
		}

		public void removeFromSelection(Object o) {
			int index = indexOf(o);
			getListSelectionModel().removeSelectionInterval(index, index);
		}

		public void resetSelection() {
			getListSelectionModel().clearSelection();
		}

	}

	public ListSelectionModel getListSelectionModel() {
		return getRenderingTechnologyAdapter().getListSelectionModel(getDynamicJComponent());
	}

	@Override
	public boolean mayRepresent(Object o) {
		return getListModel().indexOf(o) > -1;
	}

	@Override
	public void objectAddedToSelection(Object o) {
		getListModel().addToSelectionNoNotification(o);
	}

	@Override
	public void objectRemovedFromSelection(Object o) {
		getListModel().removeFromSelectionNoNotification(o);
	}

	@Override
	public void selectionResetted() {
		getListModel().resetSelectionNoNotification();
	}

	@Override
	public void addToSelection(Object o) {
		getListModel().addToSelection(o);
	}

	@Override
	public void removeFromSelection(Object o) {
		getListModel().removeFromSelection(o);
	}

	@Override
	public void resetSelection() {
		getListModel().resetSelection();
	}

	@Override
	public T getSelected() {
		return getListModel().getSelectedObject();
	}

	@Override
	public boolean synchronizedWithSelection() {
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable() {
		return getController().getLastFocusedSelectable() == this;
	}

	private FIBListCellRenderer listCellRenderer;

	@Override
	final public FIBListCellRenderer getListCellRenderer() {
		if (listCellRenderer == null) {
			listCellRenderer = new FIBListCellRenderer();
		}
		return listCellRenderer;
	}

	protected class FIBListCellRenderer extends FIBMultipleValueCellRenderer {
		public FIBListCellRenderer() {
			// Dimension s = getJComponent().getSize();
			// setPreferredSize(new Dimension(100,getWidget().getRowHeight()));
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			FIBListCellRenderer label = (FIBListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			// ((JComponent)label).setPreferredSize(new Dimension(label.getWidth(),getWidget().getRowHeight()));

			if (isSelected) {
				if (isLastFocusedSelectable()) {
					if (getWidget().getTextSelectionColor() != null) {
						setForeground(getWidget().getTextSelectionColor());
					}
					if (getWidget().getBackgroundSelectionColor() != null) {
						setBackground(getWidget().getBackgroundSelectionColor());
					}
				}
				else {
					if (getWidget().getTextNonSelectionColor() != null) {
						setForeground(getWidget().getTextNonSelectionColor());
					}
					if (getWidget().getBackgroundSecondarySelectionColor() != null) {
						setBackground(getWidget().getBackgroundSecondarySelectionColor());
					}
				}
			}
			else {
				if (getWidget().getTextNonSelectionColor() != null) {
					setForeground(getWidget().getTextNonSelectionColor());
				}
				if (getWidget().getBackgroundNonSelectionColor() != null) {
					setBackground(getWidget().getBackgroundNonSelectionColor());
				}
			}

			return label;
		}
	}

}

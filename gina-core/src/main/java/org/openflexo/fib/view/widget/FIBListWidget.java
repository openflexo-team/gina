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

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBList;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBSelectionEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.item.DescriptionIntegerItem;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.gina.manager.GinaStackEvent;

public class FIBListWidget<T> extends FIBMultipleValueWidget<FIBList, JList, T, T> implements FIBSelectable<T> {

	static final Logger LOGGER = Logger.getLogger(FIBListWidget.class.getPackage().getName());

	public static final String SELECTION = "selection";

	private List<T> selection;
	protected JList _list;

	public FIBListWidget(FIBList model, FIBController controller) {
		super(model, controller);

		Object[] listData = { "Item1", "Item2", "Item3" };
		_list = new JList(listData);
		_list.setCellRenderer(getListCellRenderer());
		_list.setSelectionMode(model.getSelectionMode().getMode());
		if (model.getVisibleRowCount() != null) {
			_list.setVisibleRowCount(model.getVisibleRowCount());
		}
		// _list.setPrototypeCellValue("0123456789012345");
		if (model.getRowHeight() != null) {
			_list.setFixedCellHeight(model.getRowHeight());
		}
		_list.setLayoutOrientation(model.getLayoutOrientation().getSwingValue());
		_list.addFocusListener(this);
		_list.setBorder(BorderFactory.createEtchedBorder());

		// _list.setMinimumSize(new Dimension(60,60));
		// _list.setPreferredSize(new Dimension(60,60));
		_list.revalidate();
		_list.repaint();

		updateMultipleValues();

		updateFont();
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
		if (getWidget().getData() != null && notEquals(getValue(), _list.getSelectedValue())) {

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateWidgetFromModel()");
			}
			widgetUpdating = true;
			// updateList();
			_list.setSelectedValue(getValue(), true);
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
		if (notEquals(getValue(), _list.getSelectedValue())) {
			modelUpdating = true;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("updateModelFromWidget with " + _list.getSelectedValue());
			}
			if (_list.getSelectedValue() != null && !widgetUpdating) {
				setValue((T) _list.getSelectedValue());
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

	/*protected synchronized void updateList() {
		if (multipleValueModel == null) {
			updateListModelWhenRequired();
		} else {
			_list.getSelectionModel().removeListSelectionListener((FIBListModel) multipleValueModel);
			multipleValueModel = new FIBListModel();
			setListModel((FIBListModel) multipleValueModel);
		}
	}*/

	/*@Override
	protected FIBListModel updateListModelWhenRequired() {
		if (multipleValueModel == null) {
			multipleValueModel = new FIBListModel();
			setListModel((FIBListModel) multipleValueModel);
		} else {
			FIBListModel newListModel = new FIBListModel();
			if (!newListModel.equals(multipleValueModel) || didLastKnownValuesChange()) {
				_list.getSelectionModel().removeListSelectionListener((FIBListModel) multipleValueModel);
				multipleValueModel = newListModel;
				setListModel((FIBListModel) multipleValueModel);
			}
		}
		return (FIBListModel) multipleValueModel;
	}*/
	
	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;
		System.out.println("Execute");

		switch(e.getAction()) {
		case FIBSelectionEventDescription.SELECTED: {
			FIBSelectionEventDescription se = (FIBSelectionEventDescription) e;

			for(DescriptionItem item : se.getValues()) {
				if (item instanceof DescriptionIntegerItem) {
					DescriptionIntegerItem intItem = (DescriptionIntegerItem) item;
					if (item.getAction().equals(FIBSelectionEventDescription.SELECTED))
						getListSelectionModel().addSelectionInterval(intItem.getValue(), intItem.getValue());
					if (item.getAction().equals(FIBSelectionEventDescription.DESELECTED))
						getListSelectionModel().removeSelectionInterval(intItem.getValue(), intItem.getValue());
				}
			}
			
			_list.revalidate();
			_list.repaint();
			break;
		}
		}
		
		System.out.println("Execute end");
		widgetExecuting = false;
	}

	private FIBListModel oldListModel = null;

	private void proceedToListModelUpdate(FIBListModel aListModel) {
		// logger.info("************* Updating GUI with " + aListModel);
		if (_list != null) {
			widgetUpdating = true;
			if (oldListModel != null) {
				_list.getSelectionModel().removeListSelectionListener(oldListModel);
			}
			oldListModel = aListModel;
			_list.setLayoutOrientation(getWidget().getLayoutOrientation().getSwingValue());
			_list.setSelectionMode(getWidget().getSelectionMode().getMode());
			if (getWidget().getVisibleRowCount() != null) {
				_list.setVisibleRowCount(getWidget().getVisibleRowCount());
			} else {
				_list.setVisibleRowCount(-1);
			}
			if (getWidget().getRowHeight() != null) {
				_list.setFixedCellHeight(getWidget().getRowHeight());
			} else {
				_list.setFixedCellHeight(-1);
			}
			_list.setModel(aListModel);
			_list.revalidate();
			_list.repaint();
			_list.getSelectionModel().addListSelectionListener(aListModel);
			widgetUpdating = false;
			Object objectToSelect = null;
			if (getComponent().getSelected().isValid()) {
				try {
					objectToSelect = getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if ((objectToSelect == null) && (getWidget().getData() == null || !getWidget().getData().isValid())
					&& getWidget().getAutoSelectFirstRow() && _list.getModel().getSize() > 0) {
				objectToSelect = _list.getModel().getElementAt(0);
			}
			if (objectToSelect != null) {
				for (int i = 0; i < _list.getModel().getSize(); i++) {
					if (_list.getModel().getElementAt(i) == objectToSelect) {
						final int index = i;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								_list.setSelectedIndex(index);
							}
						});
					}
				}
			}
		}
		/*if (getWidget().getAutoSelectFirstRow() && _list.getModel().getSize() > 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_list.setSelectedIndex(0);
				}
			});
		}*/
	}

	protected class FIBListModel extends FIBMultipleValueModel<T> implements ListSelectionListener {
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
			
			GinaStackEvent stack = GENotifier.raise(
					FIBEventFactory.getInstance().createSelectionEvent(getListSelectionModel(), getListSelectionModel().getLeadSelectionIndex(), e.getFirstIndex(), e.getLastIndex() ));


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
				getController().updateSelection(FIBListWidget.this, oldSelection, selection);
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
		return _list.getSelectionModel();
	}

	@Override
	public JList getJComponent() {
		return _list;
	}

	@Override
	public JList getDynamicJComponent() {
		return _list;
	}

	@Override
	final public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			_list.setFont(getFont());
		}
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
				} else {
					if (getWidget().getTextNonSelectionColor() != null) {
						setForeground(getWidget().getTextNonSelectionColor());
					}
					if (getWidget().getBackgroundSecondarySelectionColor() != null) {
						setBackground(getWidget().getBackgroundSecondarySelectionColor());
					}
				}
			} else {
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

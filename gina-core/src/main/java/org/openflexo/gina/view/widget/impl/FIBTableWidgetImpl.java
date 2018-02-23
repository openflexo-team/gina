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

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ListSelectionModel;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.FIBSelectionEventDescription;
import org.openflexo.gina.event.description.FIBTableEventDescription;
import org.openflexo.gina.event.description.item.DescriptionIntegerItem;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableAction;
import org.openflexo.gina.view.impl.FIBContainerViewImpl;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBTableWidget;
import org.openflexo.gina.view.widget.table.impl.FIBTableModel;

/**
 * Default base implementation for a table widget (a table display a list of rows, with some data presented as columns)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
public abstract class FIBTableWidgetImpl<C, T> extends FIBWidgetViewImpl<FIBTable, C, Collection<T>> implements FIBTableWidget<C, T> {

	private static final Logger LOGGER = Logger.getLogger(FIBTableWidgetImpl.class.getPackage().getName());

	public static final String SELECTED = "selected";
	public static final String SELECTION = "selection";

	private FIBTableModel<T> _tableModel;

	protected FIBTableWidgetFooter<?, T> footer;

	protected List<T> selection;
	protected T selectedObject;

	private BindingValueChangeListener<T> selectedBindingValueChangeListener;

	private BindingValueListChangeListener<T, Collection<T>> listenerToDataAsListValue;

	public FIBTableWidgetImpl(FIBTable fibTable, FIBController controller, TableRenderingAdapter<C, T> RenderingAdapter) {
		super(fibTable, controller, RenderingAdapter);

		/*for (FIBTableColumn column : fibTable.getColumns()) {
			column.getPropertyChangeSupport().addPropertyChangeListener(this);
		}*/

		updateFooter();
	}

	private void updateFooter() {
		if (getComponent().getShowFooter()) {
			footer = makeFooter();
		}
		else {
			removeFooter();
			footer = null;
		}
		updateTable();
	}

	private void updateHeader() {
		updateTable();
	}

	@Override
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		listenDataAsListValueChange();
		listenSelectedValueChange();
	}

	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenDataAsListValueChange();
		stopListenSelectedValueChange();
	}

	public abstract FIBTableWidgetFooter<?, T> makeFooter();

	public abstract FIBTableWidgetFooter<?, T> removeFooter();

	@Override
	public TableRenderingAdapter<C, T> getRenderingAdapter() {
		return (TableRenderingAdapter<C, T>) super.getRenderingAdapter();
	}

	private void listenDataAsListValueChange() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
		}
		if (getComponent().getData() != null && getComponent().getData().isValid()) {
			listenerToDataAsListValue = new BindingValueListChangeListener<T, Collection<T>>(((DataBinding) getComponent().getData()),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, Collection<T> newValue) {
					// System.out.println(" bindingValueChanged() detected for data list="
					// + getComponent().getEnable() + " with newValue="
					// + newValue + " source=" + source);
					updateData();
				}
			};
		}
	}

	private void stopListenDataAsListValueChange() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
			listenerToDataAsListValue = null;
		}
	}

	@SuppressWarnings("unchecked")
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
					// System.out.println(" bindingValueChanged() detected for selected="
					// + getComponent().getEnable() + " with newValue="
					// + newValue + " source=" + source);
					performSelect(newValue);
				}
			};
		}
	}

	private void stopListenSelectedValueChange() {
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.stopObserving();
			selectedBindingValueChangeListener.delete();
			selectedBindingValueChangeListener = null;
		}
	}

	public FIBTable getTable() {
		return getWidget();
	}

	public FIBTableWidgetFooter<?, T> getFooter() {
		return footer;
	}

	public FIBTableModel<T> getTableModel() {
		if (_tableModel == null) {
			_tableModel = new FIBTableModel<>(getTable(), this, getController());
		}
		return _tableModel;
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
			case FIBSelectionEventDescription.SELECTED: {
				FIBSelectionEventDescription se = (FIBSelectionEventDescription) e;
				for (DescriptionItem item : se.getValues()) {
					if (item instanceof DescriptionIntegerItem) {
						DescriptionIntegerItem intItem = (DescriptionIntegerItem) item;
						if (item.getAction().equals(FIBSelectionEventDescription.SELECTED))
							getListSelectionModel().addSelectionInterval(intItem.getValue(), intItem.getValue());
						;
						if (item.getAction().equals(FIBSelectionEventDescription.DESELECTED))
							getListSelectionModel().removeSelectionInterval(intItem.getValue(), intItem.getValue());
						;
					}
				}
				getListSelectionModel().setLeadSelectionIndex(se.getLead());
				break;
			}
			case FIBTableEventDescription.CHANGED: {
				FIBTableEventDescription te = (FIBTableEventDescription) e;
				getTableModel().setValueAt(te.getObjectValue(), te.getRow(), te.getCol());
				break;
			}
		}

		widgetExecuting = false;
	}

	@Override
	public Collection<T> updateData() {

		List<?> valuesBeforeUpdating = getTableModel().getValues();
		T wasSelected = getSelected();

		// boolean returned = false;

		// logger.info("----------> updateWidgetFromModel() for " +
		// getTable().getName());
		// Not to be done anymore, this is performed by updateEnability()
		/*
		 * if (_fibTable.getEnable().isSet() && _fibTable.getEnable().isValid())
		 * { Boolean enabledValue = true; try { enabledValue =
		 * _fibTable.getEnable().getBindingValue(getBindingEvaluationContext());
		 * } catch (TypeMismatchException e) { e.printStackTrace(); } catch
		 * (NullReferenceException e) { e.printStackTrace(); } catch
		 * (InvocationTargetException e) { e.printStackTrace(); }
		 * _table.setEnabled(enabledValue != null && enabledValue); }
		 */

		Collection<T> newValues = super.updateData();

		if (notEquals(newValues, getTableModel().getValues())) {

			// returned = true;

			// boolean debug = false;
			// if (getWidget().getName() != null &&
			// getWidget().getName().equals("PatternRoleTable")) debug=true;

			// if (debug)
			// System.out.println("valuesBeforeUpdating: "+valuesBeforeUpdating);
			// if (debug) System.out.println("wasSelected: "+wasSelected);

			if (getRenderingAdapter().isEditing(getTechnologyComponent())) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(getComponent().getName() + " - Table is currently editing at col:"
							+ getRenderingAdapter().getEditingColumn(getTechnologyComponent()) + " row:"
							+ getRenderingAdapter().getEditingRow(getTechnologyComponent()));
				}
				getRenderingAdapter().cancelCellEditing(getTechnologyComponent());
			}
			else {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(getComponent().getName() + " - Table is NOT currently edited ");
				}
			}

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(getComponent().getName() + " updateWidgetFromModel() with " + getValue() + " dataObject=" + getDataObject());
			}

			if (newValues == null) {
				getTableModel().setValues((List<T>) Collections.emptyList());
			}
			if (newValues instanceof Collection && !newValues.equals(valuesBeforeUpdating)) {
				getTableModel().setValues(newValues);
			}
			if (footer != null) {
				footer.setModel(getDataObject());
			}
		}

		/*
		 * System.out.println("updateWidgetFromModel() for table " +
		 * getComponent().getName());
		 * System.out.println("getTableModel().getValues()=" +
		 * getTableModel().getValues());
		 * System.out.println("valuesBeforeUpdating=" + valuesBeforeUpdating);
		 * System.out.println("wasSelected=" + wasSelected);
		 */

		// We restore value if and only if we represent same table
		if (equals(getTableModel().getValues(), valuesBeforeUpdating) && wasSelected != null) {
			// returned = true;
			setSelected(wasSelected);
		}
		else if (areSameValuesOrderIndifferent(getTableModel().getValues(), valuesBeforeUpdating)) {
			// Same values, only order differs, in this case, still select right
			// object
			// returned = true;
			setSelected(wasSelected);
		}
		else {
			try {
				if (getComponent().getSelected().isValid()
						&& getComponent().getSelected().getBindingValue(getBindingEvaluationContext()) != null) {
					T newSelectedObject = (T) getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
					if (notEquals(newSelectedObject, getSelected())) {
						setSelected(newSelectedObject);
					}
				}

				else if (getComponent().getAutoSelectFirstRow()) {
					if (getTableModel().getValues() != null && getTableModel().getValues().size() > 0) {
						// returned = true;
						// Take care to this option as it may cause many issues
						// A better solution is to remove this option and let
						// the newSelection manager manage such feature
						getListSelectionModel().clearSelection();
						getListSelectionModel().setSelectionInterval(0, 0);
						setSelected(getTableModel().getValues().get(0));
					}
				}
				else {
					// System.out.println("clear selection");
					// getListSelectionModel().clearSelection();
					// setSelected(null);
				}
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return newValues;
	}

	public ListSelectionModel getListSelectionModel() {
		return getRenderingAdapter().getListSelectionModel(getTechnologyComponent());
	}

	/*@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}*/

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateTable();
		for (FIBTableAction a : getWidget().getActions()) {
			if (getWidget().getLocalize()) {
				getLocalized(a.getName());
			}
		}
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateSelected();
	}

	/*@Override
	public boolean update() {
		super.update();
		updateSelected();
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.refreshObserving();
		}
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.refreshObserving();
		}
		return true;
	}*/

	private final void updateSelected() {

		try {
			if (getComponent().getSelected().isValid()
					&& getComponent().getSelected().getBindingValue(getBindingEvaluationContext()) != null) {
				T newSelectedObject = (T) getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
				if (notEquals(newSelectedObject, getSelected())) {
					performSelect(newSelectedObject);
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

	/**
	 * This method is a little bit brutal, we destroy here all the table and rebuild it
	 */
	@Override
	public void updateTable() {
		// logger.info("!!!!!!!! updateTable()");

		deleteTable();

		if (_tableModel != null) {
			_tableModel.delete();
			_tableModel = null;
		}

		// We now just update (issue: the JTable was never destroyed)
		// makeTechnologyComponent();
		updateTechnologyComponent();

		update();

	}

	protected abstract void updateTechnologyComponent();

	@Override
	public synchronized void delete() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
		}
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.stopObserving();
			selectedBindingValueChangeListener.delete();
		}
		// TODO: re-implement this properly and check that all listeners are
		// properly removed.
		if (getFooter() != null) {
			getFooter().delete();
		}
		deleteTable();
		super.delete();
	}

	protected abstract void deleteTable();

	@Override
	public boolean synchronizedWithSelection() {
		if (getWidget() == null) {
			return false;
		}
		return getWidget().getBoundToSelectionManager();
	}

	@Override
	public boolean isLastFocusedSelectable() {
		return getController().getLastFocusedSelectable() == this;
	}

	@Override
	public boolean mayRepresent(Object o) {
		if (getValue() != null) {
			return getValue().contains(o);
		}
		return false;
	}

	@Override
	public T getSelected() {
		return selectedObject;
	}

	public void setSelected(T object) {

		if (getValue() == null) {
			return;
		}
		Object oldSelected = getSelected();
		selectedObject = object;
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("FIBTable: setSelectedObject with object " + object + " current is " + getSelected());
		}

		getPropertyChangeSupport().firePropertyChange(SELECTED, oldSelected, object);
	}

	public void clearSelection() {
		getListSelectionModel().clearSelection();
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

	private static boolean areSameValuesOrderIndifferent(List<?> l1, List<?> l2) {
		if (l1 == null || l2 == null) {
			return false;
		}
		if (l1.size() != l2.size()) {
			return false;
		}
		Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if (o1 == null || o2 == null) {
					return 0;
				}
				return o1.hashCode() - o2.hashCode();
			}
		};
		List<Object> sortedL1 = new ArrayList<>(l1);
		Collections.sort(sortedL1, comparator);
		List<Object> sortedL2 = new ArrayList<>(l2);
		Collections.sort(sortedL2, comparator);
		for (int i = 0; i < sortedL1.size(); i++) {
			if (sortedL1.get(i) == null) {
				return true;
			}
			if (!sortedL1.get(i).equals(sortedL2.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if ((evt.getPropertyName().equals(FIBTable.COLUMNS_KEY)) || (evt.getPropertyName().equals(FIBTable.CREATE_NEW_ROW_ON_CLICK_KEY))
				|| (evt.getPropertyName().equals(FIBTable.ITERATOR_TYPE_KEY)) || (evt.getPropertyName().equals(FIBTable.ROW_HEIGHT_KEY))
				|| (evt.getPropertyName().equals(FIBTable.VISIBLE_ROW_COUNT_KEY))) {
			updateTable();
		}

		if (evt.getPropertyName().equals(FIBTable.SHOW_FOOTER_KEY)) {
			updateFooter();
		}

		if (evt.getPropertyName().equals(FIBTable.SHOW_HEADER_KEY)) {
			updateHeader();
		}

		if (getParentView() instanceof FIBContainerViewImpl && evt.getPropertyName().equals(FIBTable.LOOK_AND_FEEL_KEY)) {
			((FIBContainerViewImpl) getParentView()).unregisterViewForComponent(this, getComponent());
			if (getParentView() != null) {
				getParentView().changeLayout();
			}
		}

		super.propertyChange(evt);
	}

}

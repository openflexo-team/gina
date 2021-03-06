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

package org.openflexo.gina.view.widget.table.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBTableEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBButtonColumn;
import org.openflexo.gina.model.widget.FIBCheckBoxColumn;
import org.openflexo.gina.model.widget.FIBCustomColumn;
import org.openflexo.gina.model.widget.FIBDropDownColumn;
import org.openflexo.gina.model.widget.FIBIconColumn;
import org.openflexo.gina.model.widget.FIBLabelColumn;
import org.openflexo.gina.model.widget.FIBNumberColumn;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.model.widget.FIBTextFieldColumn;
import org.openflexo.gina.view.widget.FIBTableWidget;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FIBTableModel<T> extends AbstractTableModel {

	private static final Logger logger = Logger.getLogger(FIBTableModel.class.getPackage().getName());

	private List<T> _values;
	private List<AbstractColumn<T, ?>> _columns;
	private FIBTable _fibTable;
	private FIBTableWidget<?, T> _widget;

	private final Hashtable<Object, RowObjectModificationTracker> _rowObjectModificationTrackers;

	/**
	 * Stores controls: key is the JButton and value the PropertyListActionListener
	 */
	// private Hashtable<JButton,PropertyListActionListener> _controls;

	public FIBTableModel(FIBTable fibTable, FIBTableWidget<?, T> widget, FIBController controller) {
		super();
		_fibTable = fibTable;
		_widget = widget;
		_values = null;
		_columns = new ArrayList<>();
		for (FIBTableColumn column : fibTable.getColumns()) {
			addToColumns(buildTableColumn(column, controller));
		}

		_rowObjectModificationTrackers = new Hashtable<>();

	}

	public FIBTable getTable() {
		return _fibTable;
	}

	public void delete() {
		if (_values != null) {
			for (T o : _values) {
				// logger.info("REMOVE "+o);
				if (o instanceof HasPropertyChangeSupport) {
					PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o).getPropertyChangeSupport();
					// logger.info("Widget "+getWidget()+" remove property change listener: "+o);
					pcSupport.removePropertyChangeListener(getTracker(o));
					deleteTracker(o);
				}
				else if (o instanceof Observable) {
					// logger.info("Widget "+getWidget()+" remove observable: "+o);
					((Observable) o).deleteObserver(getTracker(o));
					deleteTracker(o);
				}
			}
		}

		for (AbstractColumn<T, ?> c : _columns) {
			c.delete();
		}

		_columns.clear();
		if (_values != null) {
			_values.clear();
		}

		_columns = null;
		_values = null;
		_widget = null;
		_fibTable = null;
	}

	public FIBTableWidget<?, T> getWidget() {
		return _widget;
	}

	public List<T> getValues() {
		return _values;
	}

	public void setValues(Collection<T> values) {
		// logger.info("setValues with "+values);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setValues() with " + values);
		}
		Collection<T> newValues = values;
		if (values == null) {
			newValues = Collections.emptyList();
		}

		List<T> oldValues = _values;
		_values = new ArrayList<>();
		List<T> removedValues = new ArrayList<>();
		List<T> addedValues = new ArrayList<>();
		if (oldValues != null) {
			removedValues.addAll(oldValues);
		}

		for (T v : newValues) {
			if (oldValues != null && oldValues.contains(v)) {
				removedValues.remove(v);
			}
			else {
				addedValues.add(v);
			}
			_values.add(v);
		}

		for (T o : addedValues) {
			logger.fine("ADD " + o);
			if (o instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o).getPropertyChangeSupport();
				logger.fine("Widget " + getWidget() + " remove property change listener: " + o);
				pcSupport.addPropertyChangeListener(getTracker(o));
			}
			else if (o instanceof Observable) {
				logger.fine("Widget " + getWidget() + " remove observable: " + o);
				((Observable) o).addObserver(getTracker(o));
			}
		}

		for (T o : removedValues) {
			logger.fine("REMOVE " + o);
			if (o instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o).getPropertyChangeSupport();
				logger.fine("Widget " + getWidget() + " remove property change listener: " + o);
				pcSupport.removePropertyChangeListener(getTracker(o));
				deleteTracker(o);
			}
			else if (o instanceof Observable) {
				logger.fine("Widget " + getWidget() + " remove observable: " + o);
				((Observable) o).deleteObserver(getTracker(o));
				deleteTracker(o);
			}

		}

		fireModelObjectHasChanged(oldValues, _values);
		fireTableDataChanged();
	}

	private void deleteTracker(Object o) {
		_rowObjectModificationTrackers.remove(o);
	}

	private RowObjectModificationTracker getTracker(T o) {
		RowObjectModificationTracker returned = _rowObjectModificationTrackers.get(o);
		if (returned == null) {
			returned = new RowObjectModificationTracker(o);
			_rowObjectModificationTrackers.put(o, returned);
		}
		return returned;
	}

	protected class RowObjectModificationTracker implements Observer, PropertyChangeListener {
		private final T rowObject;

		public RowObjectModificationTracker(T rowObject) {
			this.rowObject = rowObject;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// System.out.println("Row object "+evt.getSource()+" : propertyChange "+evt);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("table " + getTable().getName() + " propertyChange for " + rowObject + " source=" + evt.getSource() + " evt="
						+ evt);
			}
			updateRow();
		}

		@Override
		public void update(Observable o, Object arg) {
			updateRow();
		}

		private void updateRow() {
			fireTableRowsUpdated(indexOf(rowObject), indexOf(rowObject));
			// getTableWidget().notifyDynamicModelChanged();
		}
	}

	/**
	 * Notifies all listeners that represented model has changed
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	public void fireModelObjectHasChanged(List<T> oldValues, List<T> newValues) {
		// logger.info("fireModelObjectHasChanged in " + getTable().getName() +
		// " from " + oldValues + " to " + newValues);
		fireTableChanged(new ModelObjectHasChanged(this, oldValues, newValues));
	}

	public class ModelObjectHasChanged extends TableModelEvent {
		private final List<T> _oldValues;

		private final List<T> _newValues;

		public ModelObjectHasChanged(TableModel source, List<T> oldValues, List<T> newValues) {
			super(source);
			_oldValues = oldValues;
			_newValues = newValues;
		}

		public List<T> getNewValues() {
			return _newValues;
		}

		public List<T> getOldValues() {
			return _oldValues;
		}
	}

	@Override
	public int getRowCount() {
		if (getValues() != null) {
			return getValues().size();
		}
		return 0;
	}

	public T elementAt(int row) {
		if (getValues() != null && row >= 0 && row < getValues().size()) {
			return getValues().get(row);
		}
		return null;
	}

	public int indexOf(T object) {
		for (int i = 0; i < getRowCount(); i++) {
			if (elementAt(i) == object) {
				return i;
			}
		}
		return -1;
	}

	public void addToColumns(AbstractColumn<T, ?> aColumn) {
		_columns.add(aColumn);
		aColumn.setTableModel(this);
	}

	public void removeFromColumns(AbstractColumn<T, ?> aColumn) {
		_columns.remove(aColumn);
		aColumn.setTableModel(null);
	}

	public AbstractColumn<T, ?> columnAt(int index) {
		if (_columns == null) {
			return null;
		}
		AbstractColumn<T, ?> returned = _columns.get(index);
		return returned;
	}

	@Override
	public int getColumnCount() {
		return _columns.size();
	}

	@Override
	public String getColumnName(int col) {
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null) {
			return column.getLocalizedTitle();
		}
		return "???";
	}

	public int getDefaultColumnSize(int col) {
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null) {
			return column.getDefaultWidth();
		}
		return 75;
	}

	public boolean getColumnResizable(int col) {
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null) {
			return column.getResizable();
		}
		return true;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null) {
			return column.getValueClass();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null) {
			T object = elementAt(row);
			return column.isCellEditableFor(object);
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null) {
			T object = elementAt(row);
			return column.getValueFor(object/*
											 * ,
											 * _widget.getBindingEvaluationContext
											 * ()
											 */);
		}
		return null;

	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		FIBTableEventDescription desc = FIBEventFactory.getInstance().createTableEvent(FIBTableEventDescription.CHANGED, value,
				value != null ? value.getClass().getName() : null, row, col);
		GinaStackEvent gse = _widget.getNotifier().raise(desc);
		AbstractColumn<T, ?> column = columnAt(col);
		if (column != null && column instanceof EditableColumn) {
			T object = elementAt(row);
			((EditableColumn<T, Object>) column).setValueFor(object, value);
			fireCellUpdated(object, row, col);
		}
		gse.end();
	}

	public void fireCellUpdated(T editedObject, int row, int column) {
		// fireTableChanged(new TableModelEvent(this, row, row, column));
		int newRow = indexOf(editedObject);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("editedObject=" + editedObject);
			logger.fine("row was " + row);
			logger.fine("new row is " + newRow);
		}
		if (row != newRow) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("row changed !");
			}
			fireTableChanged(new RowMoveForObjectEvent(this, editedObject, newRow, column));
		}
	}

	public class RowMoveForObjectEvent extends TableModelEvent {
		private final Object _editedObject;
		private final int _newRow;
		private final int _column;

		public RowMoveForObjectEvent(TableModel source, Object editedObject, int newRow, int column) {
			super(source);
			_editedObject = editedObject;
			_column = column;
			_newRow = newRow;
		}

		public Object getEditedObject() {
			return _editedObject;
		}

		@Override
		public int getColumn() {
			return _column;
		}

		public int getNewRow() {
			return _newRow;
		}
	}

	/*
	 * protected class PropertyListCellRenderer extends DefaultTableCellRenderer
	 * { public Component getTableCellRendererComponent(JTable table, Object
	 * value, boolean isSelected, boolean hasFocus, int row, int column) {
	 * Component returned = super.getTableCellRendererComponent(table, value,
	 * isSelected, hasFocus, row, column); AbstractColumn col = get if (returned
	 * instanceof JComponent)
	 * ((JComponent)returned).setToolTipText(getLocalizedTooltip
	 * (getModel().elementAt(row))); return returned; } }
	 */
	/*
	 * protected void addToActions(PropertyListAction plAction) {
	 * PropertyListActionListener plActionListener = new
	 * PropertyListActionListener(plAction, this); JButton newButton = new
	 * JButton();
	 * newButton.setText(FlexoLocalization.localizedForKey(plAction.name,
	 * newButton)); if (plAction.help!=null)
	 * newButton.setToolTipText(FlexoLocalization.localizedForKey(plAction.help,
	 * newButton)); newButton.addActionListener(plActionListener);
	 * getControlPanel().add(newButton); _controls.put(newButton,
	 * plActionListener); updateControls(null); }
	 * 
	 * public Enumeration<PropertyListActionListener> getActionListeners() {
	 * return _controls.elements(); }
	 */

	/*
	 * if (controlPanel == null) { controlPanel = new JPanel() {
	 * 
	 * @Override public void remove(int index) { super.remove(index); } };
	 * controlPanel.setLayout(new FlowLayout()); controlPanel.setOpaque(false);
	 * } return controlPanel;
	 */

	public FIBTableColumn getPropertyListColumnWithTitle(String title) {
		return _fibTable.getColumnWithTitle(title);
	}

	@Override
	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fireTableRowsUpdated firstRow=" + firstRow + " lastRow=" + lastRow);
		}
		if (firstRow > -1 && lastRow > -1) {
			super.fireTableRowsUpdated(firstRow, lastRow);
		}
	}

	/**
	 * @return the table widget associated with this model.
	 * @deprecated model should not have access to its view nor its controller
	 */
	@Deprecated
	protected FIBTableWidget<?, T> getTableWidget() {
		return _widget;
	}

	private AbstractColumn<T, ?> buildTableColumn(FIBTableColumn column, FIBController controller) {
		if (column instanceof FIBLabelColumn) {
			return new LabelColumn<>((FIBLabelColumn) column, this, controller);
		}
		else if (column instanceof FIBTextFieldColumn) {
			return new TextFieldColumn<>((FIBTextFieldColumn) column, this, controller);
		}
		else if (column instanceof FIBCheckBoxColumn) {
			return new CheckBoxColumn<>((FIBCheckBoxColumn) column, this, controller);
		}
		else if (column instanceof FIBDropDownColumn) {
			return new DropDownColumn<>((FIBDropDownColumn) column, this, controller);
		}
		else if (column instanceof FIBIconColumn) {
			return new IconColumn<>((FIBIconColumn) column, this, controller);
		}
		else if (column instanceof FIBNumberColumn) {
			return new NumberColumn<>((FIBNumberColumn) column, this, controller);
		}
		else if (column instanceof FIBCustomColumn) {
			return new CustomColumn<>((FIBCustomColumn) column, this, controller);
		}
		else if (column instanceof FIBButtonColumn) {
			return new ButtonColumn<>((FIBButtonColumn) column, this, controller);
		}
		return null;

	}
}

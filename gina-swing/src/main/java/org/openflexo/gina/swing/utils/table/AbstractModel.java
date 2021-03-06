/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils.table;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.gina.view.widget.table.impl.EditableColumn;

/**
 * Represents a TableModel used by a TabularView
 * 
 * @author sguerin
 * 
 */
public abstract class AbstractModel<M extends Observable, D> extends DefaultTableModel implements Observer {

	private static final Logger LOGGER = Logger.getLogger(AbstractModel.class.getPackage().getName());

	private M _model;

	private final Vector<AbstractColumn<D, ?>> _columns;

	private int _rowHeight = -1;

	private final Vector<D> _observedObjects;

	public AbstractModel(M model) {
		super();
		_model = model;
		_columns = new Vector<>();
		_observedObjects = new Vector<>();
	}

	public M getModel() {
		return _model;
	}

	public void setModel(M model) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Set model for " + getClass().getName() + " with " + model);
		}
		M oldModel = _model;
		_model = model;
		fireModelObjectHasChanged(oldModel, model);
		fireTableDataChanged();
	}

	/**
	 * Notifies all listeners that represented model has changed
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	public void fireModelObjectHasChanged(M oldModel, M newModel) {
		fireTableChanged(new ModelObjectHasChanged(this, oldModel, newModel));
	}

	/**
	 * Notifies all listeners that supplied object must be selected
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	public void selectObject(D selectedObject) {
		fireTableChanged(new SelectObjectEvent(this, selectedObject));
	}

	public class TableStructureHasChanged extends TableModelEvent {
		public TableStructureHasChanged(TableModel source) {
			super(source);
		}
	}

	public class ModelObjectHasChanged extends TableModelEvent {
		private final M _oldModel;

		private final M _newModel;

		public ModelObjectHasChanged(TableModel source, M oldModel, M newModel) {
			super(source);
			_oldModel = oldModel;
			_newModel = newModel;
		}

		public M getNewModel() {
			return _newModel;
		}

		public M getOldModel() {
			return _oldModel;
		}
	}

	public class SelectObjectEvent extends TableModelEvent {
		private final D _selectedObject;

		public SelectObjectEvent(TableModel source, D selectedObject) {
			super(source);
			_selectedObject = selectedObject;
		}

		public D getSelectedObject() {
			return _selectedObject;
		}
	}

	public class RowMoveForObjectEvent extends TableModelEvent {
		private final D _editedObject;
		private final int _newRow;
		private final int _column;

		public RowMoveForObjectEvent(TableModel source, D editedObject, int newRow, int column) {
			super(source);
			_editedObject = editedObject;
			_column = column;
			_newRow = newRow;
		}

		public D getEditedObject() {
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

	@Override
	public void fireTableStructureChanged() {
		// logger.info("fireTableStructureChanged()");
		super.fireTableStructureChanged();
		fireTableChanged(new TableStructureHasChanged(this));
	}

	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		// logger.info("fireTableRowsDeleted("+firstRow+","+lastRow+")");
		super.fireTableRowsDeleted(firstRow, lastRow);
	}

	/**
	 * Notifies all listeners that represented model has changed
	 * 
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 */
	@Override
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("fireTableDataChanged() in " + getClass().getName() + " " + hashCode());
		}
		updateObservedObjects();
	}

	private void updateObservedObjects() {
		for (Enumeration<D> en = _observedObjects.elements(); en.hasMoreElements();) {
			Object observed = en.nextElement();
			if (observed instanceof Observable) {
				((Observable) observed).deleteObserver(this);
			}
		}
		_observedObjects.clear();
		for (int i = 0; i < getRowCount(); i++) {
			D observed = elementAt(i);
			_observedObjects.add(observed);
			if (observed instanceof Observable) {
				((Observable) observed).addObserver(this);
			}
		}
	}

	@Override
	public void update(Observable observable, Object dataModification) {
		int row = indexOf((D) observable);
		fireTableRowsUpdated(row, row);
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Update row " + row + " for object " + observable);
		}
	}

	@Override
	public abstract int getRowCount();

	public int getRowHeight() {
		return _rowHeight;
	}

	public void setRowHeight(int aRowHeight) {
		_rowHeight = aRowHeight;
	}

	public abstract D elementAt(int row);

	public int indexOf(D object) {
		// logger.info("Search index of="+object+" model="+getModel()+" "+getClass().getName());
		for (int i = 0; i < getRowCount(); i++) {
			D obj = elementAt(i);
			// logger.info("Examine object="+obj);
			if (obj == object) {
				return i;
			}
		}
		return -1;
	}

	public void addToColumns(AbstractColumn<D, ?> aColumn) {
		_columns.add(aColumn);
		aColumn.setModel(this);
	}

	public void insertColumnAtIndex(AbstractColumn<D, ?> aColumn, int index) {
		_columns.insertElementAt(aColumn, index);
		aColumn.setModel(this);
	}

	public void replaceColumnByColumn(AbstractColumn<D, ?> oldColumn, AbstractColumn<D, ?> newColumn) {
		int index = _columns.indexOf(oldColumn);
		if (index < 0) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Try to replaced a inexisting column. Not going further.");
			}
			return;
		}
		_columns.remove(index);
		_columns.insertElementAt(newColumn, index);
	}

	public void removeFromColumns(AbstractColumn<D, ?> aColumn) {
		_columns.remove(aColumn);
		aColumn.setModel(null);
	}

	public AbstractColumn<D, ?> columnAt(int index) {
		return _columns.elementAt(index);
	}

	public int getTotalPreferredWidth() {
		int returned = 0;
		for (int i = 0; i < getColumnCount(); i++) {
			returned += getDefaultColumnSize(i);
		}
		return returned;
	}

	@Override
	public int getColumnCount() {
		return _columns.size();
	}

	@Override
	public String getColumnName(int col) {
		AbstractColumn<?, ?> column = columnAt(col);
		if (column != null) {
			return column.getLocalizedTitle();
		}
		return "???";
	}

	public int getIndexForColumnWithName(String colName) {
		if (colName == null) {
			return -1;
		}
		for (int i = 0; i < getColumnCount(); i++) {
			if (colName.equals(getColumnName(i))) {
				return i;
			}
		}
		return -1;
	}

	public String getColumnTooltip(int col) {
		AbstractColumn<?, ?> column = columnAt(col);
		if (column != null) {
			return column.getLocalizedTooltip();
		}
		return "-";
	}

	public int getDefaultColumnSize(int col) {
		AbstractColumn<?, ?> column = columnAt(col);
		if (column != null) {
			return column.getDefaultWidth();
		}
		return 75;
	}

	public boolean getColumnResizable(int col) {
		AbstractColumn<?, ?> column = columnAt(col);
		if (column != null) {
			return column.getResizable();
		}
		return true;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		AbstractColumn<?, ?> column = columnAt(col);
		if (column != null) {
			return column.getValueClass();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			D object = elementAt(row);
			return column.isCellEditableFor(object);
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int col) {
		AbstractColumn<D, ?> column = columnAt(col);
		if (column != null) {
			D object = elementAt(row);
			return column.getValueFor(object);
		}
		return null;

	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		AbstractColumn<?, ?> column = columnAt(col);
		if (column != null && column instanceof EditableColumn) {
			D object = elementAt(row);
			((EditableColumn<D, Object>) column).setValueFor(object, value/*, getBindingEvaluationContext()*/);
			fireCellUpdated(object, row, col);
		}
	}

	/**
	 * Notifies all listeners that the value of the cell at <code>[row, column]</code> has been updated.
	 * 
	 * @param editedObject
	 *            object that was updated
	 * @param row
	 *            row of cell which has been updated
	 * @param column
	 *            column of cell which has been updated
	 * @see TableModelEvent
	 * @see EventListenerList
	 */
	public void fireCellUpdated(D editedObject, int row, int column) {
		// fireTableChanged(new TableModelEvent(this, row, row, column));
		int newRow = indexOf(editedObject);
		if (row != newRow) {
			fireTableChanged(new RowMoveForObjectEvent(this, editedObject, newRow, column));
		}
	}

	protected class DMCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			return returned;
		}
	}

	public abstract BindingEvaluationContext getBindingEvaluationContext();
}

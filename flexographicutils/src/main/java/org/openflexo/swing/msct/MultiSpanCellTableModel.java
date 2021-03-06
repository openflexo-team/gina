/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing.msct;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

/**
 * @version 1.0 11/22/98
 */

public class MultiSpanCellTableModel extends DefaultTableModel {

	protected CellAttribute cellAtt;

	public MultiSpanCellTableModel() {
		this((Vector<?>) null, 0);
	}

	public MultiSpanCellTableModel(int numRows, int numColumns) {
		Vector<?> names = new Vector<>(numColumns);
		names.setSize(numColumns);
		setColumnIdentifiers(names);
		dataVector = new Vector<>();
		setNumRows(numRows);
		cellAtt = new DefaultCellAttribute(numRows, numColumns);
	}

	public MultiSpanCellTableModel(Vector<?> columnNames, int numRows) {
		setColumnIdentifiers(columnNames);
		dataVector = new Vector<>();
		setNumRows(numRows);
		cellAtt = new DefaultCellAttribute(numRows, columnNames.size());
	}

	public MultiSpanCellTableModel(Object[] columnNames, int numRows) {
		this(convertToVector(columnNames), numRows);
	}

	public MultiSpanCellTableModel(Vector<?> data, Vector<?> columnNames) {
		setDataVector(data, columnNames);
	}

	public MultiSpanCellTableModel(Object[][] data, Object[] columnNames) {
		setDataVector(data, columnNames);
	}

	@Override
	public void setDataVector(Vector newData, Vector columnNames) {
		if (newData == null)
			throw new IllegalArgumentException("setDataVector() - Null parameter");
		dataVector = new Vector<>(0);
		columnIdentifiers = columnNames;
		// setColumnIdentifiers(columnNames);
		dataVector = newData;

		//
		cellAtt = new DefaultCellAttribute(dataVector.size(), columnIdentifiers.size());

		newRowsAdded(new TableModelEvent(this, 0, getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	@Override
	public void addColumn(Object columnName, Vector columnData) {
		if (columnName == null)
			throw new IllegalArgumentException("addColumn() - null parameter");
		columnIdentifiers.addElement(columnName);
		int index = 0;
		for (Object o : dataVector) {
			Vector v = (Vector) o;
			Object value;
			if ((columnData != null) && (index < columnData.size()))
				value = columnData.elementAt(index);
			else
				value = null;
			v.addElement(value);
			index++;
		}

		//
		cellAtt.addColumn();

		fireTableStructureChanged();
	}

	@Override
	public void addRow(Vector rowData) {
		Vector<?> newData = null;
		if (rowData == null) {
			newData = new Vector<>(getColumnCount());
		}
		else {
			rowData.setSize(getColumnCount());
		}
		dataVector.addElement(newData);

		//
		cellAtt.addRow();

		newRowsAdded(new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	@Override
	public void insertRow(int row, Vector rowData) {
		if (rowData == null) {
			rowData = new Vector<>(getColumnCount());
		}
		else {
			rowData.setSize(getColumnCount());
		}

		dataVector.insertElementAt(rowData, row);

		//
		cellAtt.insertRow(row);

		newRowsAdded(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	public CellAttribute getCellAttribute() {
		return cellAtt;
	}

	public void setCellAttribute(CellAttribute newCellAtt) {
		int numColumns = getColumnCount();
		int numRows = getRowCount();
		if ((newCellAtt.getSize().width != numColumns) || (newCellAtt.getSize().height != numRows)) {
			newCellAtt.setSize(new Dimension(numRows, numColumns));
		}
		cellAtt = newCellAtt;
		fireTableDataChanged();
	}

	/*
	public void changeCellAttribute(int row, int column, Object command) {
	 cellAtt.changeAttribute(row, column, command);
	}
	
	public void changeCellAttribute(int[] rows, int[] columns, Object command) {
	 cellAtt.changeAttribute(rows, columns, command);
	}
	*/

}

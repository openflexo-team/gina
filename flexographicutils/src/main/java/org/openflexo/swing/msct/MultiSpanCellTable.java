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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * @version 1.0 11/26/98
 */

public class MultiSpanCellTable extends JTable {

	public MultiSpanCellTable(TableModel model) {
		super(model);
		setUI(new MultiSpanCellTableUI());
		getTableHeader().setReorderingAllowed(false);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}

	/**
	 * Return cumulative row height from startIndex (inclusive) to endIndex (exclusive)
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private int getCumulativeRowHeight(int startIndex, int endIndex) {
		int returned = 0;
		for (int i = startIndex; i < endIndex; i++) {
			returned += getRowHeight(i);
		}
		return returned;
	}

	@Override
	public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
		Rectangle sRect = super.getCellRect(row, column, includeSpacing);
		if ((row < 0) || (column < 0) || (getRowCount() <= row) || (getColumnCount() <= column)) {
			return sRect;
		}
		CellSpan cellAtt = (CellSpan) ((MultiSpanCellTableModel) getModel()).getCellAttribute();
		if (!cellAtt.isVisible(row, column)) {
			int temp_row = row;
			int temp_column = column;
			row += cellAtt.getSpan(temp_row, temp_column)[CellSpan.ROW];
			column += cellAtt.getSpan(temp_row, temp_column)[CellSpan.COLUMN];
		}
		int[] n = cellAtt.getSpan(row, column);

		int index = 0;
		int columnMargin = getColumnModel().getColumnMargin();
		Rectangle cellFrame = new Rectangle();
		cellFrame.y = getCumulativeRowHeight(0, row);
		cellFrame.height = getCumulativeRowHeight(row, row + n[CellSpan.ROW]);

		Enumeration<?> eeration = getColumnModel().getColumns();
		while (eeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) eeration.nextElement();
			cellFrame.width = aColumn.getWidth() + columnMargin;
			if (index == column)
				break;
			cellFrame.x += cellFrame.width;
			index++;
		}
		for (int i = 0; i < n[CellSpan.COLUMN] - 1; i++) {
			TableColumn aColumn = (TableColumn) eeration.nextElement();
			cellFrame.width += aColumn.getWidth() + columnMargin;
		}

		if (!includeSpacing) {
			Dimension spacing = getIntercellSpacing();
			cellFrame.setBounds(cellFrame.x + spacing.width / 2, cellFrame.y + spacing.height / 2, cellFrame.width - spacing.width,
					cellFrame.height - spacing.height);
		}
		return cellFrame;
	}

	public Rectangle getExtendedCellRect(int row, int column, boolean includeSpacing) {
		Rectangle extCellRect = getCellRect(row, column, includeSpacing);
		int tableWidth = getColumnModel().getTotalColumnWidth();
		extCellRect.width = tableWidth - extCellRect.x;
		return extCellRect;
	}

	private int[] rowColumnAtPoint(Point point) {
		int[] retValue = { -1, -1 };
		// Unused var int currentHeight = 0;
		int row = super.rowAtPoint(point);
		/*int row = 0;
		while (point.y > currentHeight) {
			currentHeight += getRowHeight(row);
			row++;
		}*/

		// int row = point.y / (rowHeight + rowMargin);
		if ((row < 0) || (getRowCount() <= row))
			return retValue;
		int column = getColumnModel().getColumnIndexAtX(point.x);

		CellSpan cellAtt = (CellSpan) ((MultiSpanCellTableModel) getModel()).getCellAttribute();

		if (cellAtt.isVisible(row, column)) {
			retValue[CellSpan.COLUMN] = column;
			retValue[CellSpan.ROW] = row;
			return retValue;
		}
		retValue[CellSpan.COLUMN] = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
		retValue[CellSpan.ROW] = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
		return retValue;
	}

	@Override
	public int rowAtPoint(Point point) {
		return rowColumnAtPoint(point)[CellSpan.ROW];
	}

	@Override
	public int columnAtPoint(Point point) {
		return rowColumnAtPoint(point)[CellSpan.COLUMN];
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
		repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int firstIndex = e.getFirstIndex();
		int lastIndex = e.getLastIndex();
		if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
			repaint();
		}
		Rectangle dirtyRegion = getCellRect(firstIndex, 0, false);
		int numCoumns = getColumnCount();
		int index = firstIndex;
		for (int i = 0; i < numCoumns; i++) {
			dirtyRegion.add(getCellRect(index, i, false));
		}
		index = lastIndex;
		for (int i = 0; i < numCoumns; i++) {
			dirtyRegion.add(getCellRect(index, i, false));
		}
		repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
	}

}

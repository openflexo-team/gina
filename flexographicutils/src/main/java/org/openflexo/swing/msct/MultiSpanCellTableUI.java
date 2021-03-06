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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

/**
 * @version 1.0 11/26/98
 */

class MultiSpanCellTableUI extends BasicTableUI {

	@Override
	public void paint(Graphics g, JComponent c) {
		Rectangle oldClipBounds = g.getClipBounds();
		Rectangle clipBounds = new Rectangle(oldClipBounds);
		int tableWidth = table.getColumnModel().getTotalColumnWidth();
		clipBounds.width = Math.min(clipBounds.width, tableWidth);
		g.setClip(clipBounds);

		int firstIndex = table.rowAtPoint(new Point(0, clipBounds.y));
		int lastIndex = table.getRowCount() - 1;

		int heightForRow = getCumulativeRowHeight(0, firstIndex);
		for (int index = firstIndex; index <= lastIndex; index++) {
			Rectangle rowRect = new Rectangle(0, heightForRow, tableWidth, table.getRowHeight(index));
			if (rowRect.intersects(clipBounds)) {
				// System.out.println(); // debug
				// System.out.print("" + index +": "); // row
				paintRow(g, index);
			}
			heightForRow += table.getRowHeight(index);
		}
		g.setClip(oldClipBounds);
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
			returned += table.getRowHeight(i);
		}
		return returned;
	}

	private void paintRow(Graphics g, int row) {
		Rectangle rect = g.getClipBounds();
		boolean drawn = false;

		MultiSpanCellTableModel tableModel = (MultiSpanCellTableModel) table.getModel();
		CellSpan cellAtt = (CellSpan) tableModel.getCellAttribute();
		int numColumns = table.getColumnCount();

		for (int column = 0; column < numColumns; column++) {
			Rectangle cellRect = table.getCellRect(row, column, true);
			int cellRow, cellColumn;
			if (cellAtt.isVisible(row, column)) {
				cellRow = row;
				cellColumn = column;
				// System.out.print("   "+column+" "); // debug
			} else {
				cellRow = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
				cellColumn = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
				// System.out.print("  ("+column+")"); // debug
			}
			if (cellRect.intersects(rect)) {
				drawn = true;
				paintCell(g, cellRect, cellRow, cellColumn);
			} else {
				if (drawn)
					break;
			}
		}

		/*if (table instanceof MultiSpanCellTable) {
			for (int column = 0; column < numColumns; column++) {
				Rectangle extendedCellRect = ((MultiSpanCellTable) table).getExtendedCellRect(row, column, true);
				// System.out.println("J'ai peint " + table.getCellRect(row, column, true) + " et je dois essayer de peindre "
				// + extendedCellRect);
				int cellRow, cellColumn;
				if (cellAtt.isVisible(row, column)) {
					cellRow = row;
					cellColumn = column;
					// System.out.print("   "+column+" "); // debug
				} else {
					cellRow = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
					cellColumn = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
					// System.out.print("  ("+column+")"); // debug
				}
				if (extendedCellRect.intersects(rect)) {
					paintExtendedCell(g, extendedCellRect, cellRow, cellColumn);
				}
			}
		}*/
	}

	private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
		int spacingHeight = table.getRowMargin();
		int spacingWidth = table.getColumnModel().getColumnMargin();

		Color c = g.getColor();
		g.setColor(table.getGridColor());
		g.drawRect(cellRect.x, cellRect.y, column == table.getModel().getColumnCount() - 1 ? cellRect.width - 1 : cellRect.width,
				row == table.getModel().getRowCount() - 1 ? cellRect.height - 1 : cellRect.height);
		g.setColor(c);

		cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2, cellRect.width - spacingWidth, cellRect.height
				- spacingHeight);

		if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = table.prepareRenderer(renderer, row, column);

			if (component.getParent() == null) {
				rendererPane.add(component);
			}
			rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);

			if (renderer instanceof TableCellExtendedRenderer) {
				((TableCellExtendedRenderer) renderer).paintExtendedContents(g, row, column);
			}
		}
	}

	/*private void paintExtendedCell(Graphics g, Rectangle cellRect, int row, int column) {
		int spacingHeight = table.getRowMargin();
		int spacingWidth = table.getColumnModel().getColumnMargin();

		cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2, cellRect.width - spacingWidth, cellRect.height
				- spacingHeight);

		TableCellRenderer renderer = ((MultiSpanCellTable) table).getExtendedCellRenderer(row, column);
		if (renderer != null) {
			Component component = table.prepareRenderer(renderer, row, column);

			if (component.getParent() == null) {
				rendererPane.add(component);
			}
			rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
		}
	}*/
}

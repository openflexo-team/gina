/**
 * 
 */
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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.view.widget.FIBTableWidget;

@SuppressWarnings("serial")
public class FIBTableCellRenderer<T, V> extends DefaultTableCellRenderer {

	private final AbstractColumn<T, V> column;
	private Color disabledColor;
	private Color disabledBackgroundColor;

	public FIBTableCellRenderer(AbstractColumn<T, V> aColumn) {
		super();
		column = aColumn;
		setFont(column.getColumnModel().retrieveValidFont());
	}

	@Override
	public void updateUI() {
		super.updateUI();
		disabledColor = UIManager.getDefaults().getColor("Label.disabledForeground");
		disabledBackgroundColor = UIManager.getDefaults().getColor("TextArea.disabledBackground");
	}

	public FIBTableModel<T> getTableModel() {
		return column.getTableModel();
	}

	public FIBTableWidget<?, T> getTableWidget() {
		return column.getTableModel().getTableWidget();
	}

	public FIBTable getTable() {
		return getTableModel().getTable();
	}

	/**
	 * 
	 * Returns the cell renderer.
	 * 
	 * @param table
	 *            the <code>JTable</code>
	 * @param value
	 *            the value to assign to the cell at <code>[row, column]</code>
	 * @param isSelected
	 *            true if cell is selected
	 * @param hasFocus
	 *            true if cell has focus
	 * @param row
	 *            the row of the cell to render
	 * @param column
	 *            the column of the cell to render
	 * @return the default table cell renderer
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setForeground(null);
		setBackground(null);
		Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		row = table.convertRowIndexToModel(row);
		if (returned instanceof JComponent) {
			((JComponent) returned).setToolTipText(this.column.getTooltip(getTableModel().elementAt(row)));
		}

		if (returned instanceof JLabel) {
			FIBTableColumn columnModel = this.column.getColumnModel();
			((JLabel) returned).setText(this.column.getStringRepresentation(value));
			if (columnModel.getShowIcon()) {
				if (columnModel.getIcon() != null && columnModel.getIcon().isValid()) {
					((JLabel) returned).setIcon(this.column.getIconRepresentation(value));
				}
			}
		}

		if (getTableWidget().isEnabled()) {
			if (isSelected) {
				if (getTableWidget().isLastFocusedSelectable()) {
					if (getTable().getTextSelectionColor() != null) {
						setForeground(getTable().getTextSelectionColor());
					}
					if (getTable().getBackgroundSelectionColor() != null) {
						setBackground(getTable().getBackgroundSelectionColor());
					}
				}
				else {
					if (getTable().getTextNonSelectionColor() != null) {
						setForeground(getTable().getTextNonSelectionColor());
					}
					if (getTable().getBackgroundSecondarySelectionColor() != null) {
						setBackground(getTable().getBackgroundSecondarySelectionColor());
					}
				}
			}
			else {
				if (getTable().getTextNonSelectionColor() != null) {
					setForeground(getTable().getTextNonSelectionColor());
				}
				if (getTable().getBackgroundNonSelectionColor() != null) {
					setBackground(getTable().getBackgroundNonSelectionColor());
				}
			}
		}
		else {
			if (disabledColor != null) {
				setForeground(disabledColor);
			}
			else if (getForeground() != null) {
				setForeground(getForeground().brighter());
			}
			if (disabledBackgroundColor != null) {
				setBackground(disabledBackgroundColor);
			}
			else if (getBackground() != null) {
				setBackground(getBackground().darker());
			}
		}

		Color specificColor = this.column.getSpecificColor(getTableModel().elementAt(row));
		if (specificColor != null) {
			setForeground(specificColor);
		}

		Color specificBgColor = this.column.getSpecificBgColor(getTableModel().elementAt(row));
		if (specificBgColor != null) {
			setBackground(specificBgColor);
		}

		return returned;
	}

}

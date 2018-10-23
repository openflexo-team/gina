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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBIconColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class IconColumn<T> extends AbstractColumn<T, Icon> implements EditableColumn<T, Icon> {

	public IconColumn(FIBIconColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
	}

	@Override
	public FIBIconColumn getColumnModel() {
		return (FIBIconColumn) super.getColumnModel();
	}

	@Override
	public Class<Icon> getValueClass() {
		return Icon.class;
	}

	@Override
	public String toString() {
		return "IconColumn " + "@" + Integer.toHexString(hashCode());
	}

	/**
	 * Returns true as cell renderer is required here
	 * 
	 * @return true
	 */
	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	/**
	 * Make cell renderer for supplied value<br>
	 * Note that this renderer is not shared
	 * 
	 * @return
	 */
	// TODO: detach from SWING
	@Override
	public JComponent makeCellRenderer(T value) {
		JLabel returned = new JLabel();
		Object dataToRepresent = getValueFor(value);
		if (dataToRepresent instanceof Icon) {
			returned.setIcon(((Icon) dataToRepresent));
		}
		else {
			returned.setIcon(getIconRepresentation(dataToRepresent));
		}
		return returned;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		if (_iconTableCellRenderer == null) {
			_iconTableCellRenderer = new IconCellRenderer();
		}
		return _iconTableCellRenderer;
	}

	private IconCellRenderer _iconTableCellRenderer;

	@SuppressWarnings("serial")
	protected class IconCellRenderer extends FIBTableCellRenderer<T, Icon> {

		public IconCellRenderer() {
			super(IconColumn.this);
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
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			((JLabel) returned).setText(null);
			if (value instanceof Icon) {
				((JLabel) returned).setIcon((Icon) value);
			}
			else {
				((JLabel) returned).setIcon(null);
			}
			return returned;
		}
	}

}

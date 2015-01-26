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

package org.openflexo.fib.swing.utils.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class IconColumn<D> extends AbstractColumn<D, Icon> {

	public IconColumn(String title, int defaultWidth) {
		super(title, defaultWidth, false);
	}

	@Override
	public String getLocalizedTitle() {
		return " ";
	}

	@Override
	public Class getValueClass() {
		return Icon.class;
	}

	@Override
	public Icon getValueFor(D object) {
		return getIcon(object);
	}

	public abstract Icon getIcon(D object);

	@Override
	public String toString() {
		return "IconColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
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
			_iconTableCellRenderer.setToolTipText(getLocalizedTooltip());
		}
		return _iconTableCellRenderer;
	}

	private TabularViewCellRenderer _iconTableCellRenderer;

	private class IconCellRenderer extends TabularViewCellRenderer {
		IconCellRenderer() {
			super();
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
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			((JLabel) returned).setText(null);
			((JLabel) returned).setIcon((Icon) value);
			return returned;
		}
	}

}

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
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Represents a column where values can be viewed and edited as an Object taking its values (or null) along a list of available values
 * 
 * @author sguerin
 * 
 */
public abstract class DropDownColumn<D, T> extends AbstractColumn<D, T> implements EditableColumn<D, T> {

	private DropDownCellRenderer _cellRenderer;

	private DropDownCellEditor _cellEditor;

	public DropDownColumn(String title, int defaultWidth) {
		super(title, defaultWidth, true);
		_cellRenderer = new DropDownCellRenderer();
		_cellEditor = new DropDownCellEditor(new JComboBox<>());
	}

	@Override
	public Class getValueClass() {
		return Object.class;
	}

	@Override
	public T getValueFor(D object) {
		return getValue(object);
	}

	public abstract T getValue(D object);

	@Override
	public boolean isCellEditableFor(D object) {
		return true;
	}

	@Override
	public void setValueFor(D object, T value) {
		setValue(object, value);
		valueChanged(object, value);
	}

	public abstract void setValue(D object, T aValue);

	/**
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		return _cellRenderer;
	}

	protected class DropDownCellRenderer extends TabularViewCellRenderer {
		/**
		 * 
		 * Returns the selector cell renderer.
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
			if (returned instanceof JLabel) {
				((JLabel) returned).setText(renderValue((T) value));
			}
			return returned;
		}
	}

	protected abstract String renderValue(T value);

	protected abstract Vector<T> getAvailableValues();

	protected Vector<T> getAvailableValues(D object) {
		return null;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public boolean requireCellEditor() {
		return true;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public TableCellEditor getCellEditor() {
		return _cellEditor;
	}

	protected class DropDownCellEditor extends DefaultCellEditor {
		private Hashtable<Integer, DropDownComboBoxModel> _comboBoxModels;

		private JComboBox<Object> comboBox;

		public DropDownCellEditor(JComboBox<Object> aComboBox) {
			super(aComboBox);
			_comboBoxModels = new Hashtable<>();
			comboBox = aComboBox;
			comboBox.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (returned instanceof JLabel) {
						((JLabel) returned).setText(renderValue((T) value));
					}
					return returned;
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component returned = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			comboBox.setModel(getComboBoxModel(value, table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column)));
			return returned;
		}

		protected DropDownComboBoxModel getComboBoxModel(Object value, int row, int column) {
			DropDownComboBoxModel _comboBoxModel = _comboBoxModels.get(row);
			if (_comboBoxModel == null) {
				_comboBoxModel = new DropDownComboBoxModel(getModel().elementAt(row));
				_comboBoxModels.put(row, _comboBoxModel);
			}
			_comboBoxModel.setSelectedItem(value);
			return _comboBoxModel;
		}

		protected class DropDownComboBoxModel extends DefaultComboBoxModel<Object> {

			protected DropDownComboBoxModel() {
				super();
				for (Enumeration<?> en = getAvailableValues().elements(); en.hasMoreElements();) {
					addElement(en.nextElement());
				}
			}

			protected DropDownComboBoxModel(D element) {
				super();
				Vector<?> v = getAvailableValues(element);
				if (v != null) {
					for (Enumeration<?> en = v.elements(); en.hasMoreElements();) {
						addElement(en.nextElement());
					}
				}
				else {
					for (Enumeration<?> en = getAvailableValues().elements(); en.hasMoreElements();) {
						addElement(en.nextElement());
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "DropDownColumn " + "@" + Integer.toHexString(hashCode());
	}
}

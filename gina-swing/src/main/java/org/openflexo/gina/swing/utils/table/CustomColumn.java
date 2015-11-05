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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.swing.CustomPopup;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class CustomColumn<D, T> extends AbstractColumn<D, T> implements EditableColumn<D, T> {

	public CustomColumn(String title, int defaultWidth) {
		super(title, defaultWidth, true);
		_selectorCellRenderer = new SelectorCellRenderer();
		_selectorCellEditor = new SelectorCellEditor();
	}

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

	@Override
	public String toString() {
		return "SelectorColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}

	@Override
	public T getValueFor(D object) {
		return getValue(object);
	}

	public abstract T getValue(D object);

	/**
	 * @return
	 */
	@Override
	public TableCellRenderer getCellRenderer() {
		return _selectorCellRenderer;
	}

	private SelectorCellRenderer _selectorCellRenderer;

	protected class SelectorCellRenderer extends TabularViewCellRenderer {
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
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			D rowObject = elementAt(row);
			if (isSelected && hasFocus) {
				CustomPopup<T> returned = getViewSelector(rowObject, (T) value);
				if (ToolBox.isMacOSLaf()) {
					setComponentBackground(returned, hasFocus, isSelected, row, column);
				}
				return returned;
			} else {
				Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (returned instanceof JLabel) {
					((JLabel) returned).setText(getViewSelector(rowObject, (T) value).renderedString((T) value));
				}
				return returned;
			}
		}
	}

	protected abstract TextFieldCustomPopup<T> getViewSelector(D rowObject, T value);

	protected abstract TextFieldCustomPopup<T> getEditSelector(D rowObject, T value);

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
		return _selectorCellEditor;
	}

	protected SelectorCellEditor _selectorCellEditor;

	protected class SelectorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, ApplyCancelListener {
		TextFieldCustomPopup<T> _selector;

		public SelectorCellEditor() {
			_selector = getEditSelector(null, null);
			_selector.getTextField().setBorder(null);
			_selector.setBorder(null);
			_selector.addApplyCancelListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEditingStopped();
		}

		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		@Override
		public Object getCellEditorValue() {
			return _selector.getEditedObject();
		}

		// Implement the one method defined by TableCellEditor.
		@Override
		public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
			table.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
			addCellEditorListener(new CellEditorListener() {
				@Override
				public void editingCanceled(ChangeEvent e) {
					table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
				}

				@Override
				public void editingStopped(ChangeEvent e) {
					table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
				}
			});
			setEditedRowObject(elementAt(table.convertRowIndexToModel(row)));
			_selector = getEditSelector(elementAt(table.convertRowIndexToModel(row)), (T) value);
			_selector.getTextField().setBorder(null);
			_selector.setBorder(null);
			/*
			 * _selector.setEditedObject((T)value); _selector.setRevertValue((T)
			 * value);
			 */
			return _selector;
		}

		@Override
		public void fireApplyPerformed() {
			actionPerformed(null);
		}

		@Override
		public void fireCancelPerformed() {
			actionPerformed(null);
		}

		@Override
		protected void fireEditingCanceled() {
			if (_selector != null) {
				_selector.closePopup();
			}
			super.fireEditingCanceled();
		}

		@Override
		protected void fireEditingStopped() {
			if (_selector != null) {
				_selector.closePopup();
			}
			super.fireEditingStopped();

		}

	}

	protected D _editedRowObject;

	protected void setEditedRowObject(D anObject) {
		_editedRowObject = anObject;
	}

}

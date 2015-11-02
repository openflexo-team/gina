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

package org.openflexo.fib.view.widget.table;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTextFieldColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class TextFieldColumn<T> extends StringColumn<T> implements EditableColumn<T, String> {

	private DefaultCellEditor editor;

	public TextFieldColumn(FIBTextFieldColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return getDefaultTableCellRenderer();
	}

	@Override
	public boolean requireCellEditor() {
		return true;
	}

	@SuppressWarnings("serial")
	@Override
	public TableCellEditor getCellEditor() {
		if (editor == null) {
			editor = new DefaultCellEditor(new JTextField()) {
				@Override
				public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
					final JTextField textfield = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							textfield.selectAll();
						}
					});
					return textfield;
				}
			};
			Font font = getFont();
			if (font != null) {
				((JTextField) editor.getComponent()).setFont(font);
			}
		}
		return editor;
	}

	@Override
	public String toString() {
		return "EditableStringColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}
}

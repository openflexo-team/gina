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

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCheckBoxColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class CheckBoxColumn<T> extends AbstractColumn<T, Boolean> implements EditableColumn<T, Boolean> {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CheckBoxColumn.class.getPackage().getName());

	// private DefaultCellEditor editor;

	public CheckBoxColumn(FIBCheckBoxColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
	}

	@Override
	public FIBCheckBoxColumn getColumnModel() {
		return (FIBCheckBoxColumn) super.getColumnModel();
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

	@Override
	public String toString() {
		return "BooleanColumn " + "@" + Integer.toHexString(hashCode());
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	/* @Override
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
	 
	 @Override
	 public TableCellEditor getCellEditor() {
	 	if(editor==null) {
	 		editor = new DefaultCellEditor(new JCheckBox()) {
	 			@Override
	 			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	 				final JCheckBox checkBox = (JCheckBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
	 				return checkBox;
	 			}   			
	  		};
	 	}
	 	return editor;
	 }
	 
	*/
}

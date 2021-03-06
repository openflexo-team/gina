/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * The ButtonColumn class provides a renderer and an editor that looks like a JButton. The renderer and editor will then be used for a
 * specified column in the table. The TableModel will contain the String to be displayed on the button.
 * 
 * The button can be invoked by a mouse click or by pressing the space bar when the cell has focus. Optionally a mnemonic can be set to
 * invoke the button. When the button is invoked the provided Action is invoked. The source of the Action will be the table. The action
 * command will contain the model row number of the button that was clicked.
 * 
 */
public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {
	private Action action;
	private int mnemonic;
	private Border originalBorder;
	private Border focusBorder;

	private JButton renderButton;
	private JButton editButton;
	private Object editorValue;
	private JTable table;
	private int row;

	private boolean isButtonColumnEditor;

	/**
	 * Create the ButtonColumn to be used as a renderer and editor. The renderer and editor will automatically be installed on the
	 * TableColumn of the specified column.
	 * 
	 * @param action
	 *            the Action to be invoked when the button is invoked
	 */
	public ButtonColumn(Action action) {
		this.action = action;

		renderButton = new JButton();
		editButton = new JButton();
		editButton.setFocusPainted(false);
		editButton.addActionListener(this);
		originalBorder = editButton.getBorder();
	}

	@Override
	protected void fireEditingCanceled() {
		super.fireEditingCanceled();
		setTable(null);
		row = -1;
	}

	@Override
	protected void fireEditingStopped() {
		super.fireEditingStopped();
		setTable(null);
		row = -1;
	}

	/**
	 * Get foreground color of the button when the cell has focus
	 * 
	 * @return the foreground color
	 */
	public Border getFocusBorder() {
		return focusBorder;
	}

	/**
	 * The foreground color of the button when the cell has focus
	 * 
	 * @param focusBorder
	 *            the foreground color
	 */
	public void setFocusBorder(Border focusBorder) {
		this.focusBorder = focusBorder;
		editButton.setBorder(focusBorder);
	}

	public int getMnemonic() {
		return mnemonic;
	}

	/**
	 * The mnemonic to activate the button when the cell has focus
	 * 
	 * @param mnemonic
	 *            the mnemonic
	 */
	public void setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic;
		renderButton.setMnemonic(mnemonic);
		editButton.setMnemonic(mnemonic);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		setTable(table);
		if (value == null) {
			editButton.setText("");
			editButton.setIcon(null);
		} else if (value instanceof Icon) {
			editButton.setText("");
			editButton.setIcon((Icon) value);
		} else {
			editButton.setText(value.toString());
			editButton.setIcon(null);
		}
		editButton.setEnabled(isEnabled(table, value, row, column));
		this.editorValue = value;
		return editButton;
	}

	public boolean isEnabled(JTable table, Object value, int row, int column) {
		return true;
	}

	private void setTable(JTable table) {
		if (this.table != null) {
			this.table.removeMouseListener(this);
		}
		this.table = table;
		if (this.table != null) {
			this.table.addMouseListener(this);
		}
	}

	@Override
	public Object getCellEditorValue() {
		return editorValue;
	}

	//
	// Implement TableCellRenderer interface
	//
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			renderButton.setForeground(table.getSelectionForeground());
			renderButton.setBackground(table.getSelectionBackground());
		} else {
			renderButton.setForeground(table.getForeground());
			renderButton.setBackground(UIManager.getColor("Button.background"));
		}

		if (hasFocus) {
			renderButton.setBorder(focusBorder);
		} else {
			renderButton.setBorder(originalBorder);
		}

		// renderButton.setText( (value == null) ? "" : value.toString() );
		if (value == null) {
			renderButton.setText("");
			renderButton.setIcon(null);
		} else if (value instanceof Icon) {
			renderButton.setText("");
			renderButton.setIcon((Icon) value);
		} else {
			renderButton.setText(value.toString());
			renderButton.setIcon(null);
		}
		renderButton.setEnabled(isEnabled(table, value, row, column));
		return renderButton;
	}

	//
	// Implement ActionListener interface
	//
	/*
	 *	The button has been pressed. Stop editing and invoke the custom Action
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int i = table.convertRowIndexToModel(table.getEditingRow());
		fireEditingStopped();
		row = i;
		// Invoke the Action
		try {
			ActionEvent event = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "" + row);
			action.actionPerformed(event);
		} finally {
			row = -1;
		}

	}

	public int getClickedRow() {
		return row;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (table.isEditing() && table.getCellEditor() == this) {
			isButtonColumnEditor = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isButtonColumnEditor && table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}

		isButtonColumnEditor = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}

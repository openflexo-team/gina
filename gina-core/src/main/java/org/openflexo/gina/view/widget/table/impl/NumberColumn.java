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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBNumberColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class NumberColumn<T> extends AbstractColumn<T, Number> implements EditableColumn<T, Number> {

	private static final Logger logger = Logger.getLogger(NumberColumn.class.getPackage().getName());

	private DefaultCellEditor editor;

	public NumberColumn(FIBNumberColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
	}

	@Override
	public FIBNumberColumn getColumnModel() {
		return (FIBNumberColumn) super.getColumnModel();
	}

	@Override
	public synchronized Number getValueFor(T object) {
		Number returned = super.getValueFor(object);
		if (returned != null && getColumnModel() != null && getColumnModel().getNumberType() != null) {
			if (returned.getClass().equals(getColumnModel().getNumberType().getType())) {
				return returned;
			}
			Object castedObject = TypeUtils.castTo(returned, getColumnModel().getNumberType().getType());
			if (castedObject instanceof Number) {
				return (Number) castedObject;
			}
			logger.warning("Unexpected value: " + returned);
		}
		return returned;
	}

	@Override
	public Class<? extends Number> getValueClass() {
		if (getColumnModel() != null && getColumnModel().getNumberType() != null) {
			return getColumnModel().getNumberType().getType();
		}

		return Number.class;
	}

	@Override
	public String toString() {
		return "IntegerColumn " + "@" + Integer.toHexString(hashCode());
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

	@Override
	public String getStringRepresentation(Object value) {
		if (value != null && StringUtils.isNotEmpty(getColumnModel().getNumberFormat())) {
			DecimalFormat decimalFormat = new DecimalFormat(getColumnModel().getNumberFormat());
			try {
				return decimalFormat.format(value);
			} catch (IllegalArgumentException e) {
				logger.warning("Could not format as a " + getColumnModel().getNumberType() + ": " + value + " of "
						+ (value != null ? value.getClass() : null) + " given by evaluation of " + getColumnModel().getData());
			}
		}
		return super.getStringRepresentation(value);
	}

	@Override
	public TableCellEditor getCellEditor() {
		if (editor == null) {
			editor = new DefaultCellEditor(new JTextField()) {
				@Override
				public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

					final JTextField textfield = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
					textfield.setInputVerifier(new InputVerifier() {

						@Override
						public boolean verify(JComponent input) {
							if (input instanceof JTextField) {
								String text = ((JTextField) input).getText();
								return text == null || text.trim().length() == 0 || getValue(text) != null;
							}
							return true;
						}
					});

					if (value != null) {
						textfield.setText(getStringRepresentation(value));
					}
					else {
						textfield.setText("");
					}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							textfield.selectAll();
						}
					});
					return textfield;
				}

				@Override
				public Number getCellEditorValue() {
					Object cellEditorValue = super.getCellEditorValue();
					if (cellEditorValue != null && cellEditorValue.toString().trim().length() > 0) {
						return getValue(cellEditorValue.toString().trim());
					}
					return null;
				}

				private Number getValue(String value) {
					try {
						switch (getColumnModel().getNumberType()) {
							case ByteType:
								return Byte.parseByte(value);
							case ShortType:
								return Short.parseShort(value);
							case IntegerType:
								return Integer.parseInt(value);
							case LongType:
								return Long.parseLong(value);
							case FloatType:
								return Float.parseFloat(value);
							case DoubleType:
								return Double.parseDouble(value);
							default:
								return null;
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
		}
		return editor;
	}

	public static void main(String args[]) {

		// get format for default locale

		System.out.println("NumberFormat-1:");
		NumberFormat nf1 = NumberFormat.getInstance();
		System.out.println(nf1.format(1234.56789));
		System.out.println(nf1.format(1f / 3));
		System.out.println(nf1.format(1000));

		// get format for German locale

		System.out.println("NumberFormat-2:");
		NumberFormat nf2 = NumberFormat.getInstance(Locale.GERMAN);
		System.out.println(nf2.format(1234.56789));
		System.out.println(nf2.format(1f / 3));
		System.out.println(nf2.format(1000));

		System.out.println("DecimalFormat-1:");
		DecimalFormat df1 = new DecimalFormat("####.000");
		System.out.println(df1.format(1234.56));
		System.out.println(df1.format(1234.56789));
		System.out.println(df1.format(1f / 3));
		System.out.println(df1.format(1000));

		System.out.println("DecimalFormat-2:");
		DecimalFormat df2 = new DecimalFormat("##.00");
		System.out.println(df2.format(1234.56));
		System.out.println(df2.format(1234.56789));
		System.out.println(df2.format(1f / 3));
		System.out.println(df2.format(1000));

		System.out.println("DecimalFormat-3:");
		DecimalFormat df3 = new DecimalFormat("####0.00");
		System.out.println(df3.format(1234.56));
		System.out.println(df3.format(1234.56789));
		System.out.println(df3.format(1f / 3));
		System.out.println(df3.format(1000));

	}

}

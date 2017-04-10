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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBDropDownColumn;

public class DropDownColumn<T, V> extends AbstractColumn<T, V> implements EditableColumn<T, V> {
	static final Logger LOGGER = Logger.getLogger(DropDownColumn.class.getPackage().getName());

	private final DropDownCellRenderer _cellRenderer;

	private final DropDownCellEditor _cellEditor;

	public DropDownColumn(FIBDropDownColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super(columnModel, tableModel, controller);
		_cellRenderer = new DropDownCellRenderer();
		_cellEditor = new DropDownCellEditor(new JComboBox<V>());
	}

	@Override
	public FIBDropDownColumn getColumnModel() {
		return (FIBDropDownColumn) super.getColumnModel();
	}

	@Override
	public Class<V> getValueClass() {

		if (getColumnModel().getData() != null && getColumnModel().getData().isValid()) {
			Type analyzedType = getColumnModel().getData().getAnalyzedType();
			return (Class<V>) TypeUtils.getRawType(analyzedType);
		}

		if (getColumnModel().getStaticList() != null) {
			return (Class<V>) String.class;
		}

		else if (getColumnModel().getList() != null && getColumnModel().getList().isValid()) {
			Type analyzedType = getColumnModel().getList().getAnalyzedType();
			if (analyzedType instanceof ParameterizedType) {
				return (Class<V>) TypeUtils.getRawType(((ParameterizedType) analyzedType).getActualTypeArguments()[0]);
			}
		}

		else if (getColumnModel().getArray() != null && getColumnModel().getArray().isValid()) {
			Type analyzedType = getColumnModel().getArray().getAnalyzedType();
			if (analyzedType instanceof GenericArrayType) {
				return (Class<V>) TypeUtils.getRawType(((GenericArrayType) analyzedType).getGenericComponentType());
			}
		}

		LOGGER.warning("Could not determine value class");
		return (Class<V>) Object.class;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	@Override
	public boolean requireCellRenderer() {
		return true;
	}

	@Override
	public TableCellRenderer getCellRenderer() {
		return _cellRenderer;
	}

	@Override
	public boolean isCellEditableFor(Object object) {
		return true;
	}

	protected class DropDownCellRenderer extends FIBTableCellRenderer<T, V> {
		public DropDownCellRenderer() {
			super(DropDownColumn.this);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component returned = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (returned instanceof JLabel) {
				((JLabel) returned).setText(renderValue((T) value));
				((JLabel) returned).setFont(getFont());
				if (getColumnModel().getShowIcon()) {
					if (getColumnModel().getIcon() != null && getColumnModel().getIcon().isValid()) {
						((JLabel) returned).setIcon(getIconRepresentation(value));
					}
				}
			}
			return returned;
		}
	}

	protected String renderValue(T value) {
		return getStringRepresentation(value);
	}

	protected List<V> getAvailableValues(T object) {

		if (getColumnModel().getStaticList() != null) {
			Vector<String> list = new Vector<>();
			StringTokenizer st = new StringTokenizer(getColumnModel().getStaticList(), ",");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
			return (List<V>) list;
		}

		else if (getColumnModel().getList() != null && getColumnModel().getList().isSet()) {

			iteratorObject = object;
			List<V> accessedList = null;
			try {
				accessedList = (List<V>) getColumnModel().getList().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (accessedList instanceof List) {
				return accessedList;
			}
		}

		else if (getColumnModel().getArray() != null && getColumnModel().getArray().isSet()) {

			iteratorObject = object;
			V[] accessedArray = null;
			try {
				accessedArray = (V[]) getColumnModel().getArray().getBindingValue(this);
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			try {
				V[] array = accessedArray;
				List<V> list = new ArrayList<>();
				for (int i = 0; i < array.length; i++) {
					list.add(array[i]);
				}
				return list;
			} catch (ClassCastException e) {
				LOGGER.warning("ClassCastException " + e.getMessage());
			}
		}

		else if (getColumnModel().getData() != null && getColumnModel().getData().isValid()) {
			Type type = getColumnModel().getData().getAnalyzedType();
			if (type instanceof Class && ((Class<V>) type).isEnum()) {
				V[] array = ((Class<V>) type).getEnumConstants();
				List<V> list = new ArrayList<>();
				for (int i = 0; i < array.length; i++) {
					list.add(array[i]);
				}
				return list;
			}
		}
		LOGGER.warning("Could not access element list");
		return null;
	}

	@Override
	public boolean requireCellEditor() {
		return true;
	}

	@Override
	public TableCellEditor getCellEditor() {
		return _cellEditor;
	}

	@SuppressWarnings("serial")
	protected class DropDownCellEditor extends DefaultCellEditor {
		private Hashtable<Integer, DropDownComboBoxModel> _comboBoxModels;

		private JComboBox<V> comboBox;

		public DropDownCellEditor(JComboBox<V> aComboBox) {
			super(aComboBox);
			aComboBox.setFont(getFont());
			_comboBoxModels = new Hashtable<>();
			comboBox = aComboBox;
			comboBox.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (returned instanceof JLabel) {
						((JLabel) returned).setText(renderValue((T) value));
						((JLabel) returned).setFont(getFont());
						if (getColumnModel().getShowIcon()) {
							if (getColumnModel().getIcon() != null && getColumnModel().getIcon().isValid()) {
								((JLabel) returned).setIcon(getIconRepresentation(value));
							}
						}
					}
					return returned;
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component returned = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			comboBox.setModel(getComboBoxModel(value, row, column));
			return returned;
		}

		protected DropDownComboBoxModel getComboBoxModel(Object value, int row, int column) {
			// Don't use cache as it is never refreshed
			// DropDownComboBoxModel _comboBoxModel = _comboBoxModels.get(row);
			// if (_comboBoxModel == null) {
			DropDownComboBoxModel _comboBoxModel = new DropDownComboBoxModel(elementAt(row));
			// _comboBoxModels.put(row, _comboBoxModel);
			// }
			_comboBoxModel.setSelectedItem(value);
			return _comboBoxModel;
		}

		protected class DropDownComboBoxModel extends DefaultComboBoxModel<V> {

			protected DropDownComboBoxModel(T element) {
				super();
				List<V> v = getAvailableValues(element);
				if (v != null) {
					for (V elt : v)
						addElement(elt);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "DropDownColumn " + "@" + Integer.toHexString(hashCode());
	}
}

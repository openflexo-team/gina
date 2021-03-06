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
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Represents a column in a table
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of row object beeing handled by this column
 * @param <V>
 *            type of value beeing managed by column's cells
 */
public abstract class AbstractColumn<T, V> implements HasPropertyChangeSupport, BindingEvaluationContext, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(AbstractColumn.class.getPackage().getName());

	private String title;
	private int defaultWidth;
	private final boolean isResizable;
	private final boolean displayTitle;

	// private FIBTableModel fibTableModel;
	private FIBTableColumn columnModel;

	// Unused private String tooltipKey;

	private FIBTableCellRenderer<T, V> _defaultTableCellRenderer;

	private FIBController controller;

	private FIBTableModel<T> tableModel;

	private final ColumnDynamicFormatter formatter;

	private final PropertyChangeSupport pcSupport;

	public static LocalizedDelegate DATE_LOCALIZATION = new LocalizedDelegateImpl(
			ResourceLocator.locateResource("Localization/DateSelector"), null, true, true);

	private SimpleDateFormat dateFormatter;

	public AbstractColumn(FIBTableColumn columnModel, FIBTableModel<T> tableModel, FIBController controller) {
		super();
		this.controller = controller;
		this.tableModel = tableModel;
		this.columnModel = columnModel;
		formatter = new ColumnDynamicFormatter();
		title = columnModel.getTitle();
		defaultWidth = columnModel.getColumnWidth();
		isResizable = columnModel.getResizable();
		displayTitle = columnModel.getDisplayTitle();

		pcSupport = new PropertyChangeSupport(this);

		columnModel.getPropertyChangeSupport().addPropertyChangeListener(this);

	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	private boolean isDeleted = false;

	public boolean isDeleted() {
		return isDeleted;
	}

	public void delete() {
		columnModel.getPropertyChangeSupport().removePropertyChangeListener(this);

		this.controller = null;
		this.columnModel = null;
		title = null;

		if (_defaultTableCellRenderer != null) {
			_defaultTableCellRenderer.delete();
		}
		_defaultTableCellRenderer = null;

		isDeleted = true;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == columnModel) {
			if (evt.getPropertyName().equals(FIBTableColumn.DELETED)) {
				System.out.println("deleted column");
			}
			else if ((evt.getPropertyName().equals(FIBTableColumn.COLUMN_WIDTH_KEY))
					|| (evt.getPropertyName().equals(FIBTableColumn.DATA_KEY))
					|| (evt.getPropertyName().equals(FIBTableColumn.DISPLAY_TITLE_KEY))
					|| (evt.getPropertyName().equals(FIBTableColumn.FONT_KEY))
					|| (evt.getPropertyName().equals(FIBTableColumn.RESIZABLE_KEY))
					|| (evt.getPropertyName().equals(FIBTableColumn.TITLE_KEY))) {
				if (columnModel != null && columnModel.getOwner() != null && controller.viewForComponent(columnModel.getOwner()) != null) {
					((FIBTableWidget<?, ?>) controller.viewForComponent(columnModel.getOwner())).updateTable();
				}
			}
		}

	}

	public FIBController getController() {
		return controller;
	}

	public String getLocalized(String key) {
		if (getController() != null) {
			return getController().getLocalizerForComponent(getColumnModel().getOwner()).localizedForKeyAndLanguage(key,
					FlexoLocalization.getCurrentLanguage(), true);
		}
		logger.warning("Controller not defined");
		return key;
	}

	public FIBTableModel<T> getTableModel() {
		return tableModel;
	}

	protected void setTableModel(FIBTableModel<T> model) {
		tableModel = model;
	}

	public T elementAt(int row) {
		return tableModel.elementAt(row);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocalizedTitle() {
		if (title == null || !displayTitle) {
			return " ";
		}
		return getLocalized(getTitle());
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public boolean getResizable() {
		return isResizable;
	}

	public void setDefaultWidth(int width) {
		defaultWidth = width;
	}

	public boolean isCellEditableFor(T object) {
		return false;
	}

	public abstract Class<? extends V> getValueClass();

	@SuppressWarnings("unchecked")
	public synchronized V getValueFor(final T object /*, BindingEvaluationContext evaluationContext*/) {
		// bindingEvaluationContext = evaluationContext;
		setIteratorObject(object);
		/*
		 * System.out.println("column: "+columnModel);
		 * System.out.println("binding: "
		 * +columnModel.getData()+" valid: "+columnModel.getData().isValid());
		 * System.out.println("iterator: "+iteratorObject);
		 * System.out.println("return: "
		 * +columnModel.getData().getBindingValue(this));
		 */
		try {
			return (V) columnModel.getData().getBindingValue(this);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			return null;
		} catch (NullReferenceException e) {
			// logger.warning("Unexpected " + e.getMessage());
			// e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized void setValueFor(final T object, V value/*, BindingEvaluationContext evaluationContext*/) {
		// bindingEvaluationContext = evaluationContext;
		setIteratorObject(object);
		try {
			columnModel.getData().setBindingValue(value, this);
			notifyValueChangedFor(object, value/*, bindingEvaluationContext*/);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NotSettableContextException e) {
			e.printStackTrace();
		}
	}

	protected T iteratorObject;

	private void setIteratorObject(T iteratorObject) {
		T oldIteratorObject = this.iteratorObject;
		this.iteratorObject = iteratorObject;
		getPropertyChangeSupport().firePropertyChange(FIBTable.ITERATOR_NAME, oldIteratorObject, iteratorObject);
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(FIBTable.ITERATOR_NAME)) {
			return iteratorObject;
		}
		if ((tableModel != null) && (tableModel.getWidget() != null) && (tableModel.getWidget().getBindingEvaluationContext() != null)) {
			return tableModel.getWidget().getBindingEvaluationContext().getValue(variable);
		}
		return null;
	}

	/*private BindingEvaluationContext bindingEvaluationContext;
	
	public BindingEvaluationContext getBindingEvaluationContext() {
		if (bindingEvaluationContext != null) {
			return bindingEvaluationContext;
		}
		return getController();
	}*/

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellRenderer() {
		return false;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellRenderer getCellRenderer() {
		return getDefaultTableCellRenderer();
	}

	/**
	 * Make cell renderer for supplied value<br>
	 * Note that this renderer is not shared
	 * 
	 * @return
	 */
	// TODO: detach from SWING
	public JComponent makeCellRenderer(T value) {

		JLabel returned = new JLabel();
		Object dataToRepresent = getValueFor(value);
		returned.setText(getStringRepresentation(dataToRepresent));
		return returned;
	}

	/**
	 * Make cell editor for supplied value<br>
	 * Note that this renderer is not shared
	 * 
	 * @return
	 */
	// TODO: detach from SWING
	public JComponent makeCellEditor(T value, ActionListener actionListener) {

		return makeCellRenderer(value);
	}

	/**
	 * @return
	 */
	protected TableCellRenderer getDefaultTableCellRenderer() {
		if (_defaultTableCellRenderer == null) {
			_defaultTableCellRenderer = new FIBTableCellRenderer<>(this);
		}
		return _defaultTableCellRenderer;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public boolean requireCellEditor() {
		return false;
	}

	public String getTooltip(T object) {
		if (columnModel.getTooltip().isSet() && columnModel.getTooltip().isValid()) {
			setIteratorObject(object);
			try {
				return columnModel.getTooltip().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Color getSpecificColor(T object) {
		if (columnModel.getColor().isSet() && columnModel.getColor().isValid()) {
			setIteratorObject(object);
			try {
				return columnModel.getColor().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Color getSpecificBgColor(T object) {
		if (columnModel.getBgColor().isSet() && columnModel.getBgColor().isValid()) {
			setIteratorObject(object);
			try {
				return columnModel.getBgColor().getBindingValue(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Must be overriden if required
	 * 
	 * @return
	 */
	public TableCellEditor getCellEditor() {
		return null;
	}

	public FIBTableColumn getPropertyListColumn() {
		return getTableModel().getPropertyListColumnWithTitle(title);
	}

	private boolean valueChangeNotificationEnabled = true;

	protected void disableValueChangeNotification() {
		valueChangeNotificationEnabled = false;
	}

	protected void enableValueChangeNotification() {
		valueChangeNotificationEnabled = false;
	}

	public void notifyValueChangedFor(T object, V value /*, BindingEvaluationContext evaluationContext*/) {
		if (!valueChangeNotificationEnabled) {
			return;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyValueChangedFor " + object);
		}
		// bindingEvaluationContext = evaluationContext;
		// Following will force the whole row where object was modified to be
		// updated
		// (In case of some computed cells are to be updated according to ths
		// new value)
		getTableModel().fireTableRowsUpdated(getTableModel().indexOf(object), getTableModel().indexOf(object));
		// getTableModel().getTableWidget().notifyDynamicModelChanged();

		if (getColumnModel().getValueChangedAction().isValid()) {
			try {
				getColumnModel().getValueChangedAction().execute(this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	/* Unused
	public void setTooltipKey(String tooltipKey) {
		this.tooltipKey = tooltipKey;
	}
	*/
	public FIBTableColumn getColumnModel() {
		return columnModel;
	}

	public Font getFont() {
		return getColumnModel().retrieveValidFont();
	}

	public String getStringRepresentation(final Object value) {

		if (value == null) {
			return "";
		}
		if (getColumnModel().getFormat().isValid()) {
			formatter.setValue(value);
			try {
				return getColumnModel().getFormat().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		if (value instanceof Date) {

			if (dateFormatter == null) {
				dateFormatter = new SimpleDateFormat();
			}
			dateFormatter.applyPattern(DATE_LOCALIZATION.localizedForKey("MMMM d, yyyy"));
			return dateFormatter.format((Date) value);
		}

		return value.toString();
	}

	public Icon getIconRepresentation(final Object value) {
		if (value == null) {
			return null;
		}
		if (getColumnModel().getIcon() != null && getColumnModel().getIcon().isValid()) {
			formatter.setValue(value);
			try {
				return getColumnModel().getIcon().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected class ColumnDynamicFormatter implements BindingEvaluationContext, HasPropertyChangeSupport {
		private Object value;
		protected final static String OBJECT = "object";
		private final PropertyChangeSupport pcSupport;

		public ColumnDynamicFormatter() {
			pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		private void setValue(Object aValue) {
			Object oldValue = value;
			value = aValue;
			getPropertyChangeSupport().firePropertyChange(OBJECT, oldValue, aValue);
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals(OBJECT)) {
				return value;
			}
			return AbstractColumn.this.getValue(variable);
		}
	}
}

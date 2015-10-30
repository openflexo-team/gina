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

package org.openflexo.fib.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.swing.view.FIBWidgetView;
import org.openflexo.fib.swing.view.widget.table.FIBTableActionListener;
import org.openflexo.fib.swing.view.widget.table.FIBTableModel;
import org.openflexo.fib.swing.view.widget.table.FIBTableWidgetFooter;
import org.openflexo.gina.event.description.FIBEventDescription;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBFocusEventDescription;
import org.openflexo.gina.event.description.FIBMouseEventDescription;
import org.openflexo.gina.event.description.FIBSelectionEventDescription;
import org.openflexo.gina.event.description.FIBTableEventDescription;
import org.openflexo.gina.event.description.FIBTextEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.event.description.item.DescriptionIntegerItem;
import org.openflexo.gina.event.description.item.DescriptionItem;
import org.openflexo.gina.manager.GinaStackEvent;

/**
 * Widget allowing to display/edit a list of values
 * 
 * @author sguerin
 */
public class FIBTableWidget<T> extends FIBWidgetView<FIBTable, JTable, Collection<T>> implements TableModelListener, FIBSelectable<T>,
		ListSelectionListener {

	private static final Logger LOGGER = Logger.getLogger(FIBTableWidget.class.getPackage().getName());

	public static final String SELECTED = "selected";
	public static final String SELECTION = "selection";

	private JXTable _table;
	private final JPanel _dynamicComponent;
	private final FIBTable _fibTable;
	private FIBTableModel<T> _tableModel;
	// private ListSelectionModel _listSelectionModel;
	private JScrollPane scrollPane;

	private final FIBTableWidgetFooter footer;

	private List<T> selection;

	private T selectedObject;

	private BindingValueChangeListener<T> selectedBindingValueChangeListener;

	private BindingValueListChangeListener<T, List<T>> listenerToDataAsListValue;

	public FIBTableWidget(FIBTable fibTable, FIBController controller) {
		super(fibTable, controller);
		_fibTable = fibTable;
		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());

		footer = new FIBTableWidgetFooter(this);
		buildTable();
		listenDataAsListValueChange();
		listenSelectedValueChange();
	}

	private void listenDataAsListValueChange() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
		}
		if (getComponent().getData() != null && getComponent().getData().isValid()) {
			listenerToDataAsListValue = new BindingValueListChangeListener<T, List<T>>(((DataBinding) getComponent().getData()),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<T> newValue) {
					// System.out.println(" bindingValueChanged() detected for data list=" + getComponent().getEnable() + " with newValue="
					// + newValue + " source=" + source);
					updateData();
				}
			};
		}
	}

	private void listenSelectedValueChange() {
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.stopObserving();
			selectedBindingValueChangeListener.delete();
		}
		if (getComponent().getSelected() != null && getComponent().getSelected().isValid()) {
			selectedBindingValueChangeListener = new BindingValueChangeListener<T>((DataBinding<T>) getComponent().getSelected(),
					getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, T newValue) {
					// System.out.println(" bindingValueChanged() detected for selected=" + getComponent().getEnable() + " with newValue="
					// + newValue + " source=" + source);
					performSelect(newValue);
				}
			};
		}
	}

	public FIBTable getTable() {
		return _fibTable;
	}

	public FIBTableWidgetFooter getFooter() {
		return footer;
	}

	public FIBTableModel<T> getTableModel() {
		if (_tableModel == null) {
			_tableModel = new FIBTableModel<T>(_fibTable, this, getController());
		}
		return _tableModel;
	}

	/*public JLabel getLabel()
	{
	    if (_label == null) {
	        _label = new JLabel(_propertyModel.label + " : ", SwingConstants.CENTER);
	        _label.setText(FlexoLocalization.localizedForKey(_propertyModel.label, _label));
	        _label.setBackground(InspectorCst.BACK_COLOR);
	        _label.setFont(DEFAULT_LABEL_FONT);
	        if (_propertyModel.help != null && !_propertyModel.help.equals(""))
	            _label.setToolTipText(_propertyModel.help);
	    }
	    return _label;
	}*/
	
	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
		case FIBSelectionEventDescription.SELECTED: {
			FIBSelectionEventDescription se = (FIBSelectionEventDescription) e;
			for (DescriptionItem item : se.getValues()) {
				if (item instanceof DescriptionIntegerItem) {
					DescriptionIntegerItem intItem = (DescriptionIntegerItem) item;
					if (item.getAction().equals(
							FIBSelectionEventDescription.SELECTED))
						getListSelectionModel().addSelectionInterval(
								intItem.getValue(), intItem.getValue());
					;
					if (item.getAction().equals(
							FIBSelectionEventDescription.DESELECTED))
						getListSelectionModel().removeSelectionInterval(
								intItem.getValue(), intItem.getValue());
					;
				}
			}
			getListSelectionModel().setLeadSelectionIndex(se.getLead());
			break;
		}
		case FIBTableEventDescription.CHANGED: {
			FIBTableEventDescription te = (FIBTableEventDescription) e;
			getTableModel().setValueAt(te.getObjectValue(), te.getRow(), te.getCol());
			break;
		}
		}
		
		widgetExecuting = false;
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {

		List<?> valuesBeforeUpdating = getTableModel().getValues();
		T wasSelected = getSelected();

		boolean returned = false;

		// logger.info("----------> updateWidgetFromModel() for " + getTable().getName());
		// Not to be done anymore, this is performed by updateEnability()
		/*if (_fibTable.getEnable().isSet() && _fibTable.getEnable().isValid()) {
			Boolean enabledValue = true;
			try {
				enabledValue = _fibTable.getEnable().getBindingValue(getBindingEvaluationContext());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			_table.setEnabled(enabledValue != null && enabledValue);
		}*/
		if (notEquals(getValue(), getTableModel().getValues())) {

			returned = true;

			// boolean debug = false;
			// if (getWidget().getName() != null && getWidget().getName().equals("PatternRoleTable")) debug=true;

			// if (debug) System.out.println("valuesBeforeUpdating: "+valuesBeforeUpdating);
			// if (debug) System.out.println("wasSelected: "+wasSelected);

			if (_table.isEditing()) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(getComponent().getName() + " - Table is currently editing at col:" + _table.getEditingColumn() + " row:"
							+ _table.getEditingRow());
				}
				_table.getCellEditor().cancelCellEditing();
			} else {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(getComponent().getName() + " - Table is NOT currently edited ");
				}
			}

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(getComponent().getName() + " updateWidgetFromModel() with " + getValue() + " dataObject=" + getDataObject());
			}

			if (getValue() == null) {
				getTableModel().setValues((List<T>) Collections.emptyList());
			}
			if (getValue() instanceof Collection && !getValue().equals(valuesBeforeUpdating)) {
				getTableModel().setValues(getValue());
			}
			footer.setModel(getDataObject());
		}

		/*System.out.println("updateWidgetFromModel() for table " + getComponent().getName());
		System.out.println("getTableModel().getValues()=" + getTableModel().getValues());
		System.out.println("valuesBeforeUpdating=" + valuesBeforeUpdating);
		System.out.println("wasSelected=" + wasSelected);*/

		// We restore value if and only if we represent same table
		if (equals(getTableModel().getValues(), valuesBeforeUpdating) && wasSelected != null) {
			returned = true;
			setSelected(wasSelected);
		} else if (areSameValuesOrderIndifferent(getTableModel().getValues(), valuesBeforeUpdating)) {
			// Same values, only order differs, in this case, still select right object
			returned = true;
			setSelected(wasSelected);
		} else {
			try {
				if (getComponent().getSelected().isValid()
						&& getComponent().getSelected().getBindingValue(getBindingEvaluationContext()) != null) {
					T newSelectedObject = (T) getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
					if (returned = notEquals(newSelectedObject, getSelected())) {
						setSelected(newSelectedObject);
					}
				}

				else if (getComponent().getAutoSelectFirstRow()) {
					if (getTableModel().getValues() != null && getTableModel().getValues().size() > 0) {
						returned = true;
						// Take care to this option as it may cause many issues
						// A better solution is to remove this option and let the newSelection manager manage such feature
						/*System.out.println("lsm class = " + getListSelectionModel().getClass());
						System.out.println("asi=" + getListSelectionModel().getAnchorSelectionIndex());
						System.out.println("lsi=" + getListSelectionModel().getLeadSelectionIndex());
						System.out.println("msi=" + getListSelectionModel().getMinSelectionIndex());
						System.out.println("msi=" + getListSelectionModel().getMaxSelectionIndex());
						for (int i = 0; i < 5; i++) {
							System.out.println("selected (" + i + ") : " + getListSelectionModel().isSelectedIndex(i));
						}*/
						getListSelectionModel().clearSelection();
						getListSelectionModel().setSelectionInterval(0, 0);
						/*System.out.println("asi=" + getListSelectionModel().getAnchorSelectionIndex());
						System.out.println("lsi=" + getListSelectionModel().getLeadSelectionIndex());
						System.out.println("msi=" + getListSelectionModel().getMinSelectionIndex());
						System.out.println("msi=" + getListSelectionModel().getMaxSelectionIndex());
						for (int i = 0; i < 5; i++) {
							System.out.println("selected (" + i + ") : " + getListSelectionModel().isSelectedIndex(i));
						}*/
						// System.out.println(getListSelectionModel().);
						/*getListSelectionModel().clearSelection();
						getListSelectionModel().setLeadSelectionIndex(0);
						getListSelectionModel().setAnchorSelectionIndex(0);*/
						/*SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getListSelectionModel().addSelectionInterval(0, 0);
							}
						});*/
						// addToSelection(getTableModel().getValues().get(0));
					}
				} else {
					// System.out.println("clear selection");
					// getListSelectionModel().clearSelection();
					// setSelected(null);
				}
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return returned;
	}

	public ListSelectionModel getListSelectionModel() {
		return _table.getSelectionModel();
	}

	@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e instanceof FIBTableModel.ModelObjectHasChanged) {
			FIBTableModel.ModelObjectHasChanged event = (FIBTableModel.ModelObjectHasChanged) e;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Model has changed from " + event.getOldValues() + " to " + event.getNewValues());
			}
		} else if (e instanceof FIBTableModel.RowMoveForObjectEvent) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Reselect object, and then the edited cell");
			}
			FIBTableModel.RowMoveForObjectEvent event = (FIBTableModel.RowMoveForObjectEvent) e;
			getListSelectionModel().removeListSelectionListener(this);
			getListSelectionModel().addSelectionInterval(event.getNewRow(), event.getNewRow());
			getListSelectionModel().addListSelectionListener(this);
			_table.setEditingColumn(_table.convertColumnIndexToView(event.getColumn()));
			_table.setEditingRow(_table.convertRowIndexToView(event.getNewRow()));
		}
	}

	@Override
	public JPanel getJComponent() {
		return _dynamicComponent;
	}

	@Override
	public JTable getDynamicJComponent() {
		return _table;
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateTable();
		for (FIBTableAction a : getWidget().getActions()) {
			if (getWidget().getLocalize()) {
				getLocalized(a.getName());
			}
		}
	}

	@Override
	public boolean update() {
		super.update();
		updateSelected();
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.refreshObserving();
		}
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.refreshObserving();
		}
		return true;
	}

	private final void updateSelected() {

		try {
			if (getComponent().getSelected().isValid()
					&& getComponent().getSelected().getBindingValue(getBindingEvaluationContext()) != null) {
				T newSelectedObject = (T) getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
				if (notEquals(newSelectedObject, getSelected())) {
					performSelect(newSelectedObject);
				}
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void updateTable() {
		// logger.info("!!!!!!!! updateTable()");

		deleteTable();

		if (_tableModel != null) {
			_tableModel.removeTableModelListener(this);
			_tableModel.delete();
			_tableModel = null;
		}

		buildTable();

		/*logger.info("!!!!!!!!  getDataObject()="+getDataObject());
		logger.info("!!!!!!!!  getValue()="+getValue());
		logger.info("!!!!!!!!  getDynamicModel().data="+getDynamicModel().data);
		logger.info("!!!!!!!!  getComponent().getData()="+getComponent().getData());*/

		update();

		// updateDataObject(getDataObject());
	}

	@Override
	public synchronized void delete() {
		if (listenerToDataAsListValue != null) {
			listenerToDataAsListValue.stopObserving();
			listenerToDataAsListValue.delete();
		}
		if (selectedBindingValueChangeListener != null) {
			selectedBindingValueChangeListener.stopObserving();
			selectedBindingValueChangeListener.delete();
		}
		// TODO: re-implement this properly and check that all listeners are properly removed.
		getFooter().delete();
		deleteTable();
		getTableModel().removeTableModelListener(this);
		super.delete();
	}

	private void deleteTable() {
		if (_table != null) {
			_table.removeFocusListener(this);
		}
		if (getListSelectionModel() != null) {
			getListSelectionModel().removeListSelectionListener(this);
		}
		if (scrollPane != null && _fibTable.getCreateNewRowOnClick()) {
			for (MouseListener l : scrollPane.getMouseListeners()) {
				scrollPane.removeMouseListener(l);
			}
		}
		for (MouseListener l : _table.getMouseListeners()) {
			_table.removeMouseListener(l);
		}
	}

	final private void buildTable() {
		getTableModel().addTableModelListener(this);

		_table = new JXTable(getTableModel()) {

			@Override
			protected void resetDefaultTableCellRendererColors(Component renderer, int row, int column) {
			}

		};
		_table.setVisibleRowCount(0);
		_table.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
		_table.setAutoCreateRowSorter(true);
		_table.setFillsViewportHeight(true);
		_table.setShowHorizontalLines(false);
		_table.setShowVerticalLines(false);
		_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_table.addFocusListener(this);

		for (int i = 0; i < getTableModel().getColumnCount(); i++) {
			TableColumn col = _table.getColumnModel().getColumn(i);
			// FlexoLocalization.localizedForKey(getController().getLocalizer(),getTableModel().columnAt(i).getTitle());
			col.setWidth(getTableModel().getDefaultColumnSize(i));
			col.setPreferredWidth(getTableModel().getDefaultColumnSize(i));
			if (getTableModel().getColumnResizable(i)) {
				col.setResizable(true);
			} else {
				// L'idee, c'est d'etre vraiment sur ;-) !
				col.setWidth(getTableModel().getDefaultColumnSize(i));
				col.setMinWidth(getTableModel().getDefaultColumnSize(i));
				col.setMaxWidth(getTableModel().getDefaultColumnSize(i));
				col.setResizable(false);
			}
			if (getTableModel().columnAt(i).requireCellRenderer()) {
				col.setCellRenderer(getTableModel().columnAt(i).getCellRenderer());
			}
			if (getTableModel().columnAt(i).requireCellEditor()) {
				col.setCellEditor(getTableModel().columnAt(i).getCellEditor());
			}
		}
		if (_fibTable.getRowHeight() != null) {
			_table.setRowHeight(_fibTable.getRowHeight());
		}
		if (getTable().getVisibleRowCount() != null) {
			_table.setVisibleRowCount(getTable().getVisibleRowCount());
			if (_table.getRowHeight() == 0) {
				_table.setRowHeight(18);
			}
		}

		_table.setSelectionMode(_fibTable.getSelectionMode().getMode());
		// _table.getTableHeader().setReorderingAllowed(false);

		_table.getSelectionModel().addListSelectionListener(this);

		// _listSelectionModel = _table.getSelectionModel();
		// _listSelectionModel.addListSelectionListener(this);

		if (getWidget().getBoundToSelectionManager()) {
			_table.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getController().performCopyAction(getSelected(), getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			_table.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getController().performCutAction(getSelected(), getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			_table.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getController().performPasteAction(getSelected(), getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		}

		scrollPane = new JScrollPane(_table);
		scrollPane.setOpaque(false);
		if (_fibTable.getCreateNewRowOnClick()) {
			_table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (_table.getCellEditor() != null) {
						_table.getCellEditor().stopCellEditing();
						e.consume();
					}
					if (_fibTable.getCreateNewRowOnClick()) {
						if (!e.isConsumed() && e.getClickCount() == 2) {
							// System.out.println("OK, on essaie de gerer un new par double click");
							Enumeration<FIBTableActionListener> en = getFooter().getAddActionListeners();
							while (en.hasMoreElements()) {
								FIBTableActionListener action = en.nextElement();
								if (action.isAddAction()) {
									action.actionPerformed(new ActionEvent(_table, ActionEvent.ACTION_PERFORMED, null, EventQueue
											.getMostRecentEventTime(), e.getModifiers()));
									break;
								}
							}
						}
					}
				}
			});
		}
		/*_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				getController().fireMouseClicked(getDynamicModel(),e.getClickCount());
			}
		});*/

		_dynamicComponent.removeAll();
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);

		if (_fibTable.getShowFooter()) {
			_dynamicComponent.add(getFooter(), BorderLayout.SOUTH);
		}

		_dynamicComponent.revalidate();
		_dynamicComponent.repaint();
	}

	@Override
	public boolean synchronizedWithSelection() {
		if (getWidget() == null) {
			return false;
		}
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable() {
		return getController().getLastFocusedSelectable() == this;
	}

	@Override
	public boolean mayRepresent(Object o) {
		if (getValue() != null) {
			return getValue().contains(o);
		}
		return false;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (!(getTableModel().getValues() != null && getTableModel().getValues().size() > 0)) {
			return;
		}

		// Ignore extra messages.
		if (e.getValueIsAdjusting()) {
			return;
		}
		
		int i = getListSelectionModel().getMinSelectionIndex();
		int leadIndex = getListSelectionModel().getLeadSelectionIndex();
		if (!getListSelectionModel().isSelectedIndex(leadIndex)) {
			leadIndex = getListSelectionModel().getAnchorSelectionIndex();
		}
		while (!getListSelectionModel().isSelectedIndex(leadIndex) && i <= getListSelectionModel().getMaxSelectionIndex()) {
			leadIndex = i;
			i++;
		}

		// System.out.println("leadIndex=" + leadIndex);
		
		GinaStackEvent stack = GENotifier.raise(
				FIBEventFactory.getInstance().createSelectionEvent(getListSelectionModel(), leadIndex, e.getFirstIndex(), e.getLastIndex()) );
		
		widgetDisableUserEvent = true;

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("valueChanged() selected index=" + getListSelectionModel().getMinSelectionIndex());
		}

		// System.out.println("received " + e);

		if (leadIndex > -1) {
			leadIndex = _table.convertRowIndexToModel(leadIndex);
		}

		T newSelectedObject = getTableModel().elementAt(leadIndex);

		List<T> oldSelection = selection;
		List<T> newSelection = new ArrayList<T>();
		for (i = getListSelectionModel().getMinSelectionIndex(); i <= getListSelectionModel().getMaxSelectionIndex(); i++) {
			if (getListSelectionModel().isSelectedIndex(i)) {
				newSelection.add(getTableModel().elementAt(_table.convertRowIndexToModel(i)));
			}
		}

		setSelected(newSelectedObject);
		setSelection(newSelection);
		footer.handleSelectionChanged();

		// System.out.println("selectedObject=" + selectedObject);
		// System.out.println("selection=" + newSelection);

		if (getComponent().getSelected().isValid()) {
			LOGGER.fine("Sets SELECTED binding with " + selectedObject);
			try {
				getComponent().getSelected().setBindingValue(selectedObject, getBindingEvaluationContext());
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NotSettableContextException e1) {
				e1.printStackTrace();
			}
		}

		updateFont();

		if (!ignoreNotifications) {
			getController().updateSelection(this, oldSelection, newSelection);
		}

		/*SwingUtilities.invokeLater(new Runnable() {
			
			public void run()
			{
				System.out.println((isFocused() ? "LEADER" : "SECONDARY")+" Le grand vainqueur est "+selectedObject);
				System.out.println((isFocused() ? "LEADER" : "SECONDARY")+" La newSelection est "+newSelection);
			}
		});*/
		
		widgetDisableUserEvent = false;

		stack.end();
	}

	private boolean ignoreNotifications = false;

	@Override
	public T getSelected() {
		return selectedObject;
	}

	public void performSelect(T object) {
		if (object == getSelected()) {
			LOGGER.fine("FIBTableWidget: ignore performSelect " + object);
			return;
		}
		setSelected(object);
		if (object != null && getTableModel() != null && getTableModel().getValues() != null) {
			int index = getTableModel().getValues().indexOf(object);
			if (index > -1) {
				index = _table.convertRowIndexToView(index);
				// if (!notify) _table.getSelectionModel().removeListSelectionListener(getTableModel());
				getListSelectionModel().setSelectionInterval(index, index);
				// if (!notify) _table.getSelectionModel().addListSelectionListener(getTableModel());
			}
		} else {
			clearSelection();
		}
	}

	public void setSelected(T object) {

		if (getValue() == null) {
			return;
		}
		Object oldSelected = getSelected();
		selectedObject = object;
		LOGGER.fine("FIBTable: setSelectedObject with object " + object + " current is " + getSelected());

		getPropertyChangeSupport().firePropertyChange(SELECTED, oldSelected, object);
	}

	public void clearSelection() {
		getListSelectionModel().clearSelection();
	}

	@Override
	public List<T> getSelection() {
		return selection;
	}

	public void setSelection(List<T> selection) {
		List<T> oldSelection = this.selection;
		this.selection = selection;
		getPropertyChangeSupport().firePropertyChange(SELECTION, oldSelection, selection);
	}

	@Override
	public void objectAddedToSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			ignoreNotifications = true;
			try {
				index = _table.convertRowIndexToView(index);
				getListSelectionModel().addSelectionInterval(index, index);
			} catch (IndexOutOfBoundsException e) {
				LOGGER.warning("Unexpected " + e);
			}
			ignoreNotifications = false;
		}
	}

	@Override
	public void objectRemovedFromSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			ignoreNotifications = true;
			try {
				index = _table.convertRowIndexToView(index);
			} catch (IndexOutOfBoundsException e) {
				LOGGER.warning("Unexpected " + e);
			}
			getListSelectionModel().removeSelectionInterval(index, index);
			ignoreNotifications = false;
		}
	}

	@Override
	public void selectionResetted() {
		ignoreNotifications = true;
		getListSelectionModel().clearSelection();
		ignoreNotifications = false;
	}

	@Override
	public void addToSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			index = _table.convertRowIndexToView(index);
			getListSelectionModel().addSelectionInterval(index, index);
		}
	}

	@Override
	public void removeFromSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			index = _table.convertRowIndexToView(index);
			getListSelectionModel().removeSelectionInterval(index, index);
		}
	}

	@Override
	public void resetSelection() {
		getListSelectionModel().clearSelection();
	}

	private static boolean areSameValuesOrderIndifferent(List<?> l1, List<?> l2) {
		if (l1 == null || l2 == null) {
			return false;
		}
		if (l1.size() != l2.size()) {
			return false;
		}
		Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				return o1.hashCode() - o2.hashCode();
			}
		};
		List<Object> sortedL1 = new ArrayList<Object>(l1);
		Collections.sort(sortedL1, comparator);
		List<Object> sortedL2 = new ArrayList<Object>(l2);
		Collections.sort(sortedL2, comparator);
		for (int i = 0; i < sortedL1.size(); i++) {
			if (!sortedL1.get(i).equals(sortedL2.get(i))) {
				return false;
			}
		}
		return true;
	}
}
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
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.view.widget.impl.FIBTableWidgetImpl;
import org.openflexo.fib.view.widget.table.FIBTableActionListener;
import org.openflexo.fib.view.widget.table.FIBTableModel;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.manager.GinaStackEvent;

/**
 * Widget allowing to display/edit a list of values
 * 
 * @author sguerin
 */
public class JFIBTableWidget<T> extends FIBTableWidgetImpl<JXTable, T>
		implements TableModelListener, FIBSelectable<T>, ListSelectionListener, FocusListener {

	private static final Logger LOGGER = Logger.getLogger(JFIBTableWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JTable<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingTableRenderingTechnologyAdapter<T> extends SwingRenderingTechnologyAdapter<JXTable>
			implements TableRenderingTechnologyAdapter<JXTable, T> {

		@Override
		public int getVisibleRowCount(JXTable component) {
			return component.getVisibleRowCount();
		}

		@Override
		public void setVisibleRowCount(JXTable component, int visibleRowCount) {
			component.setVisibleRowCount(visibleRowCount);
		}

		@Override
		public int getRowHeight(JXTable component) {
			return component.getRowHeight();
		}

		@Override
		public void setRowHeight(JXTable component, int rowHeight) {
			component.setRowHeight(rowHeight);
		}

		@Override
		public ListSelectionModel getListSelectionModel(JXTable component) {
			return component.getSelectionModel();
		}

		@Override
		public boolean isEditing(JXTable component) {
			return component.isEditing();
		}

		@Override
		public int getEditingRow(JXTable component) {
			return component.getEditingRow();
		}

		@Override
		public int getEditingColumn(JXTable component) {
			return component.getEditingColumn();
		}

		@Override
		public void cancelCellEditing(JXTable component) {
			component.getCellEditor().cancelCellEditing();
		}

	}

	private final JPanel _dynamicComponent;
	private JScrollPane scrollPane;

	public JFIBTableWidget(FIBTable fibTable, FIBController controller) {
		super(fibTable, controller, new SwingTableRenderingTechnologyAdapter<T>());

		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());

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
		}
		else if (e instanceof FIBTableModel.RowMoveForObjectEvent) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Reselect object, and then the edited cell");
			}
			FIBTableModel.RowMoveForObjectEvent event = (FIBTableModel.RowMoveForObjectEvent) e;
			getListSelectionModel().removeListSelectionListener(this);
			getListSelectionModel().addSelectionInterval(event.getNewRow(), event.getNewRow());
			getListSelectionModel().addListSelectionListener(this);
			getTechnologyComponent().setEditingColumn(getTechnologyComponent().convertColumnIndexToView(event.getColumn()));
			getTechnologyComponent().setEditingRow(getTechnologyComponent().convertRowIndexToView(event.getNewRow()));
		}
	}

	@Override
	public JPanel getJComponent() {
		return _dynamicComponent;
	}

	@Override
	public void updateTable() {

		if (getTableModel() != null) {
			getTableModel().removeTableModelListener(this);
		}

		super.updateTable();

	}

	@Override
	public synchronized void delete() {
		if (getTableModel() != null) {
			getTableModel().removeTableModelListener(this);
		}

		super.delete();
	}

	@Override
	protected void deleteTable() {
		if (getTechnologyComponent() != null) {
			getTechnologyComponent().removeFocusListener(this);
		}
		if (getListSelectionModel() != null) {
			getListSelectionModel().removeListSelectionListener(this);
		}
		if (scrollPane != null && getTable().getCreateNewRowOnClick()) {
			for (MouseListener l : scrollPane.getMouseListeners()) {
				scrollPane.removeMouseListener(l);
			}
		}
		for (MouseListener l : getTechnologyComponent().getMouseListeners()) {
			getTechnologyComponent().removeMouseListener(l);
		}
	}

	@Override
	protected JXTable makeTechnologyComponent() {

		getTableModel().addTableModelListener(this);

		JXTable _table = new JXTable(getTableModel()) {

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
			}
			else {
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
		if (getTable().getRowHeight() != null) {
			_table.setRowHeight(getTable().getRowHeight());
		}
		if (getTable().getVisibleRowCount() != null) {
			_table.setVisibleRowCount(getTable().getVisibleRowCount());
			if (_table.getRowHeight() == 0) {
				_table.setRowHeight(18);
			}
		}

		_table.setSelectionMode(getTable().getSelectionMode().getMode());
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
		if (getTable().getCreateNewRowOnClick()) {
			_table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (getTechnologyComponent().getCellEditor() != null) {
						getTechnologyComponent().getCellEditor().stopCellEditing();
						e.consume();
					}
					if (getTable().getCreateNewRowOnClick()) {
						if (!e.isConsumed() && e.getClickCount() == 2) {
							// System.out.println("OK, on essaie de gerer un new par double click");
							Enumeration<FIBTableActionListener<T>> en = getFooter().getAddActionListeners();
							while (en.hasMoreElements()) {
								FIBTableActionListener<T> action = en.nextElement();
								if (action.isAddAction()) {
									action.actionPerformed(new ActionEvent(getTechnologyComponent(), ActionEvent.ACTION_PERFORMED, null,
											EventQueue.getMostRecentEventTime(), e.getModifiers()));
									break;
								}
							}
						}
					}
				}
			});
		}

		_dynamicComponent.removeAll();
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);

		if (getTable().getShowFooter()) {
			_dynamicComponent.add(getFooter().getFooterComponent(), BorderLayout.SOUTH);
		}

		_dynamicComponent.revalidate();
		_dynamicComponent.repaint();

		return _table;
	}

	@Override
	public JFIBTableWidgetFooter<T> getFooter() {
		return (JFIBTableWidgetFooter<T>) super.getFooter();
	}

	@Override
	public JFIBTableWidgetFooter<T> makeFooter() {
		return new JFIBTableWidgetFooter<T>(this);
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

		GinaStackEvent stack = GENotifier.raise(FIBEventFactory.getInstance().createSelectionEvent(getListSelectionModel(), leadIndex,
				e.getFirstIndex(), e.getLastIndex()));

		widgetDisableUserEvent = true;

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("valueChanged() selected index=" + getListSelectionModel().getMinSelectionIndex());
		}

		// System.out.println("received " + e);

		if (leadIndex > -1) {
			leadIndex = getTechnologyComponent().convertRowIndexToModel(leadIndex);
		}

		T newSelectedObject = getTableModel().elementAt(leadIndex);

		List<T> oldSelection = selection;
		List<T> newSelection = new ArrayList<T>();
		for (i = getListSelectionModel().getMinSelectionIndex(); i <= getListSelectionModel().getMaxSelectionIndex(); i++) {
			if (getListSelectionModel().isSelectedIndex(i)) {
				newSelection.add(getTableModel().elementAt(getTechnologyComponent().convertRowIndexToModel(i)));
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

		widgetDisableUserEvent = false;

		stack.end();
	}

	private boolean ignoreNotifications = false;

	@Override
	public void performSelect(T object) {
		if (object == getSelected()) {
			LOGGER.fine("FIBTableWidget: ignore performSelect " + object);
			return;
		}
		setSelected(object);
		if (object != null && getTableModel() != null && getTableModel().getValues() != null) {
			int index = getTableModel().getValues().indexOf(object);
			if (index > -1) {
				index = getTechnologyComponent().convertRowIndexToView(index);
				// if (!notify) _table.getSelectionModel().removeListSelectionListener(getTableModel());
				getListSelectionModel().setSelectionInterval(index, index);
				// if (!notify) _table.getSelectionModel().addListSelectionListener(getTableModel());
			}
		}
		else {
			clearSelection();
		}
	}

	@Override
	public void objectAddedToSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			ignoreNotifications = true;
			try {
				index = getTechnologyComponent().convertRowIndexToView(index);
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
				index = getTechnologyComponent().convertRowIndexToView(index);
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
			index = getTechnologyComponent().convertRowIndexToView(index);
			getListSelectionModel().addSelectionInterval(index, index);
		}
	}

	@Override
	public void removeFromSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			index = getTechnologyComponent().convertRowIndexToView(index);
			getListSelectionModel().removeSelectionInterval(index, index);
		}
	}

	@Override
	public void resetSelection() {
		getListSelectionModel().clearSelection();
	}

}

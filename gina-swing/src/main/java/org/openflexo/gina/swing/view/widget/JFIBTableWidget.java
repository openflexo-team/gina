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

package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingx.JXTable;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBTableWidgetFooter;
import org.openflexo.gina.view.widget.impl.FIBTableWidgetImpl;
import org.openflexo.gina.view.widget.table.impl.FIBTableModel;

/**
 * Widget allowing to display/edit a list of values
 * 
 * @author sguerin
 */
public class JFIBTableWidget<T> extends FIBTableWidgetImpl<JTablePanel<T>, T>
		implements TableModelListener, ListSelectionListener, FocusListener, JFIBView<FIBTable, JTablePanel<T>> {

	private static final Logger LOGGER = Logger.getLogger(JFIBTableWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTable<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingTableRenderingAdapter<T> extends SwingRenderingAdapter<JTablePanel<T>>
			implements TableRenderingAdapter<JTablePanel<T>, T> {

		@Override
		public int getVisibleRowCount(JTablePanel<T> component) {
			return component.getJTable().getVisibleRowCount();
		}

		@Override
		public void setVisibleRowCount(JTablePanel<T> component, int visibleRowCount) {
			component.getJTable().setVisibleRowCount(visibleRowCount);
		}

		@Override
		public int getRowHeight(JTablePanel<T> component) {
			return component.getJTable().getRowHeight();
		}

		@Override
		public void setRowHeight(JTablePanel<T> component, int rowHeight) {
			component.getJTable().setRowHeight(rowHeight);
		}

		@Override
		public ListSelectionModel getListSelectionModel(JTablePanel<T> component) {
			return component.getJTable().getSelectionModel();
		}

		@Override
		public boolean isEditing(JTablePanel<T> component) {
			return component.getJTable().isEditing();
		}

		@Override
		public int getEditingRow(JTablePanel<T> component) {
			return component.getJTable().getEditingRow();
		}

		@Override
		public int getEditingColumn(JTablePanel<T> component) {
			return component.getJTable().getEditingColumn();
		}

		@Override
		public void cancelCellEditing(JTablePanel<T> component) {
			component.getJTable().getCellEditor().cancelCellEditing();
		}

		@Override
		public Color getDefaultForegroundColor(JTablePanel<T> component) {
			return UIManager.getColor("Table.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JTablePanel<T> component) {
			return UIManager.getColor("Table.background");
		}

		@Override
		public JXTable getDynamicJComponent(JTablePanel<T> technologyComponent) {
			return technologyComponent.getJTable();
		}

	}

	public JFIBTableWidget(FIBTable fibTable, FIBController controller) {
		super(fibTable, controller, new SwingTableRenderingAdapter<T>());

		getTableModel().addTableModelListener(this);

	}

	@Override
	public SwingTableRenderingAdapter<T> getRenderingAdapter() {
		return (SwingTableRenderingAdapter<T>) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
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
			FIBTableModel<?>.ModelObjectHasChanged event = (FIBTableModel<?>.ModelObjectHasChanged) e;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Model has changed from " + event.getOldValues() + " to " + event.getNewValues());
			}
		}
		else if (e instanceof FIBTableModel.RowMoveForObjectEvent) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Reselect object, and then the edited cell");
			}
			FIBTableModel<?>.RowMoveForObjectEvent event = (FIBTableModel<?>.RowMoveForObjectEvent) e;
			getListSelectionModel().removeListSelectionListener(this);
			getListSelectionModel().addSelectionInterval(event.getNewRow(), event.getNewRow());
			getListSelectionModel().addListSelectionListener(this);
			getTechnologyComponent().getJTable()
					.setEditingColumn(getTechnologyComponent().getJTable().convertColumnIndexToView(event.getColumn()));
			getTechnologyComponent().getJTable()
					.setEditingRow(getTechnologyComponent().getJTable().convertRowIndexToView(event.getNewRow()));
		}
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
		for (MouseListener l : getTechnologyComponent().getMouseListeners()) {
			getTechnologyComponent().removeMouseListener(l);
		}
		getTechnologyComponent().delete();
	}

	@Override
	protected JTablePanel<T> makeTechnologyComponent() {
		return new JTablePanel<>(this);
	}

	@Override
	protected void updateTechnologyComponent() {
		getTechnologyComponent().updateTable();
	}

	@Override
	public JFIBTableWidgetFooter<T> getFooter() {
		return (JFIBTableWidgetFooter<T>) super.getFooter();
	}

	@Override
	public JFIBTableWidgetFooter<T> makeFooter() {
		JFIBTableWidgetFooter<T> returned = new JFIBTableWidgetFooter<>(this);
		getTechnologyComponent().add(returned.getFooterComponent(), BorderLayout.SOUTH);
		return returned;
	}

	@Override
	public FIBTableWidgetFooter<?, T> removeFooter() {
		FIBTableWidgetFooter<?, T> returned = getFooter();
		if (returned != null) {
			getTechnologyComponent().remove((JComponent) returned.getFooterComponent());
		}
		return returned;
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
			leadIndex = getTechnologyComponent().getJTable().convertRowIndexToModel(leadIndex);
		}

		T newSelectedObject = getTableModel().elementAt(leadIndex);

		List<T> oldSelection = selection;
		List<T> newSelection = new ArrayList<>();
		for (i = getListSelectionModel().getMinSelectionIndex(); i <= getListSelectionModel().getMaxSelectionIndex(); i++) {
			if (getListSelectionModel().isSelectedIndex(i)) {
				newSelection.add(getTableModel().elementAt(getTechnologyComponent().getJTable().convertRowIndexToModel(i)));
			}
		}

		setSelected(newSelectedObject);
		setSelection(newSelection);
		if (footer != null) {
			footer.handleSelectionChanged();
		}
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
				index = getTechnologyComponent().getJTable().convertRowIndexToView(index);
				// if (!notify)
				// _table.getSelectionModel().removeListSelectionListener(getTableModel());
				getListSelectionModel().setSelectionInterval(index, index);
				// if (!notify)
				// _table.getSelectionModel().addListSelectionListener(getTableModel());
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
				index = getTechnologyComponent().getJTable().convertRowIndexToView(index);
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
				index = getTechnologyComponent().getJTable().convertRowIndexToView(index);
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
			index = getTechnologyComponent().getJTable().convertRowIndexToView(index);
			getListSelectionModel().addSelectionInterval(index, index);
		}
	}

	@Override
	public void removeFromSelection(T o) {
		int index = getTableModel().getValues().indexOf(o);
		if (index > -1) {
			index = getTechnologyComponent().getJTable().convertRowIndexToView(index);
			getListSelectionModel().removeSelectionInterval(index, index);
		}
	}

	@Override
	public void resetSelection() {
		getListSelectionModel().clearSelection();
	}

}

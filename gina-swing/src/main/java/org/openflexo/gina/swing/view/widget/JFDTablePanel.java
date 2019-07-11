package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBMouseEvent;
import org.openflexo.gina.model.widget.FIBTableAction;
import org.openflexo.gina.model.widget.FIBTableAction.FIBAddAction;
import org.openflexo.gina.model.widget.FIBTableAction.FIBRemoveAction;
import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.swing.view.SwingViewFactory.SwingFIBMouseEvent;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.gina.view.widget.table.impl.FIBTableActionListener;
import org.openflexo.gina.view.widget.table.impl.FIBTableModel;

/**
 * Implements Flat-Design table widget
 * 
 * @author sylvain
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class JFDTablePanel<T> extends JPanel {

	private JFDTable<T> jTable;
	private final JScrollPane scrollPane;
	private final JFDFIBTableWidget<T> widget;

	public JFDTablePanel(JFDFIBTableWidget<T> aWidget) {
		super(new BorderLayout());
		setOpaque(false);
		this.widget = aWidget;

		jTable = makeJTable();

		scrollPane = new JScrollPane(jTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		scrollPane.setOpaque(false);

		add(scrollPane, BorderLayout.CENTER);
	}

	public void updateTable() {
		scrollPane.getViewport().remove(jTable);
		jTable = makeJTable();
		scrollPane.setViewportView(jTable);
	}

	public static class JFDTable<T> extends JPanel implements TableModelListener, TableColumnModelListener, ListSelectionListener {

		private GridBagLayout gridBagLayout;

		private JFDFIBTableWidget<T> widget;

		private FIBTableModel<T> dataModel;
		private TableColumnModel columnModel;
		private ListSelectionModel selectionModel;

		private JPanel tablePanel;

		private Map<T, List<JComponent>> componentsForValues = new HashMap<>();
		private Map<T, JButton> removeButtons = new HashMap<>();
		private Map<T, JButton> editButtons = new HashMap<>();
		private T selectedValue = null;
		private T focusedValue = null;
		private T editedValue = null;

		private FIBTableActionListener<T> addActionListener;
		private FIBTableActionListener<T> removeActionListener;

		public JFDTable(JFDFIBTableWidget<T> widget) {
			super(new BorderLayout());

			tablePanel = new JPanel();
			gridBagLayout = new GridBagLayout();
			tablePanel.setLayout(gridBagLayout);
			this.widget = widget;
			setTableModel(widget.getTableModel());
			selectionModel = new DefaultListSelectionModel();
			add(tablePanel, BorderLayout.CENTER);
			buildTable();

		}

		/**
		 * Returns the <code>TableModel</code> that provides the data displayed by this <code>JTable</code>.
		 *
		 * @return the <code>TableModel</code> that provides the data displayed by this <code>JTable</code>
		 * @see #setModel
		 */
		public TableModel getModel() {
			return dataModel;
		}

		/**
		 * Sets the data model for this table to <code>newModel</code> and registers with it for listener notifications from the new data
		 * model.
		 *
		 * @param dataModel
		 *            the new data source for this table
		 * @exception IllegalArgumentException
		 *                if <code>newModel</code> is <code>null</code>
		 * @see #getModel
		 * @beaninfo bound: true description: The model that is the source of the data for this view.
		 */
		public void setTableModel(FIBTableModel<T> dataModel) {
			if (dataModel == null) {
				throw new IllegalArgumentException("Cannot set a null TableModel");
			}
			if (this.dataModel != dataModel) {
				TableModel old = this.dataModel;
				if (old != null) {
					old.removeTableModelListener(this);
				}
				this.dataModel = dataModel;
				dataModel.addTableModelListener(this);

				tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));

				firePropertyChange("model", old, dataModel);

				/*if (getAutoCreateRowSorter()) {
					setRowSorter(new TableRowSorter<TableModel>(dataModel));
				}*/
			}
		}

		/**
		 * Sets the column model for this table to <code>newModel</code> and registers for listener notifications from the new column model.
		 * Also sets the column model of the <code>JTableHeader</code> to <code>columnModel</code>.
		 *
		 * @param columnModel
		 *            the new data source for this table
		 * @exception IllegalArgumentException
		 *                if <code>columnModel</code> is <code>null</code>
		 * @see #getColumnModel
		 * @beaninfo bound: true description: The object governing the way columns appear in the view.
		 */
		public void setColumnModel(TableColumnModel columnModel) {
			if (columnModel == null) {
				throw new IllegalArgumentException("Cannot set a null ColumnModel");
			}
			TableColumnModel old = this.columnModel;
			if (columnModel != old) {
				if (old != null) {
					old.removeColumnModelListener(this);
				}
				this.columnModel = columnModel;
				columnModel.addColumnModelListener(this);

				firePropertyChange("columnModel", old, columnModel);
				revalidate();
				repaint();
			}
		}

		/**
		 * Returns the <code>TableColumnModel</code> that contains all column information of this table.
		 *
		 * @return the object that provides the column state of the table
		 * @see #setColumnModel
		 */
		public TableColumnModel getColumnModel() {
			return columnModel;
		}

		/**
		 * Sets the row selection model for this table to <code>newModel</code> and registers for listener notifications from the new
		 * selection model.
		 *
		 * @param newModel
		 *            the new selection model
		 * @exception IllegalArgumentException
		 *                if <code>newModel</code> is <code>null</code>
		 * @see #getSelectionModel
		 * @beaninfo bound: true description: The selection model for rows.
		 */
		public void setSelectionModel(ListSelectionModel newModel) {
			if (newModel == null) {
				throw new IllegalArgumentException("Cannot set a null SelectionModel");
			}

			ListSelectionModel oldModel = selectionModel;

			if (newModel != oldModel) {
				if (oldModel != null) {
					oldModel.removeListSelectionListener(this);
				}

				selectionModel = newModel;
				newModel.addListSelectionListener(this);

				firePropertyChange("selectionModel", oldModel, newModel);
				repaint();
			}
		}

		public T getFocusedValue() {
			return focusedValue;
		}

		public T getSelectedValue() {
			return selectedValue;
		}

		@Override
		public void tableChanged(TableModelEvent e) {

			/*System.out.println("Je recois TableModelEvent " + e);
			System.out.println("rows=" + getModel().getRowCount());
			if (e instanceof ModelObjectHasChanged) {
				System.out.println(">>>>>>>>>> OK le model change pour celui la");
			}*/

			if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {

				// System.out.println("HEADER");

				// System.out.println("rows=" + getModel().getRowCount());
				// The whole thing changed
				/*clearSelectionAndLeadAnchor();
				
				rowModel = null;
				
				if (sortManager != null) {
				    try {
				        ignoreSortChange = true;
				        sortManager.sorter.modelStructureChanged();
				    } finally {
				        ignoreSortChange = false;
				    }
				    sortManager.allChanged();
				}
				
				if (getAutoCreateColumnsFromModel()) {
				    // This will effect invalidation of the JTable and JTableHeader.
				    createDefaultColumnsFromModel();
				    return;
				}
				
				resizeAndRepaint();*/
				return;
			}

			/*if (sortManager != null) {
			    sortedTableChanged(null, e);
			    return;
			}*/

			// The totalRowHeight calculated below will be incorrect if
			// there are variable height rows. Repaint the visible region,
			// but don't return as a revalidate may be necessary as well.
			/*if (rowModel != null) {
			    repaint();
			}*/

			if (e.getType() == TableModelEvent.INSERT) {
				tableRowsInserted(e);
				return;
			}

			if (e.getType() == TableModelEvent.DELETE) {
				tableRowsDeleted(e);
				return;
			}

			// Unused int modelColumn = e.getColumn();
			// Unused int start = e.getFirstRow();
			// Unused int end = e.getLastRow();

			/*Rectangle dirtyRegion;
			if (modelColumn == TableModelEvent.ALL_COLUMNS) {
				// 1 or more rows changed
				dirtyRegion = new Rectangle(0, start * getRowHeight(), getColumnModel().getTotalColumnWidth(), 0);
			}
			else {
				// A cell or column of cells has changed.
				// Unlike the rest of the methods in the JTable, the TableModelEvent
				// uses the coordinate system of the model instead of the view.
				// This is the only place in the JTable where this "reverse mapping"
				// is used.
				// int column = convertColumnIndexToView(modelColumn);
				// dirtyRegion = getCellRect(start, column, false);
			}*/

			// Now adjust the height of the dirty region according to the value of "end".
			// Check for Integer.MAX_VALUE as this will cause an overflow.
			/*if (end != Integer.MAX_VALUE) {
				dirtyRegion.height = (end - start + 1) * getRowHeight();
				repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
			}*/
			// In fact, if the end is Integer.MAX_VALUE we need to revalidate anyway
			// because the scrollbar may need repainting.
			/*else {
				clearSelectionAndLeadAnchor();
				resizeAndRepaint();
				rowModel = null;
			}*/

			// revalidate();
			// repaint();

			if (editedValue == null) {
				refreshTable();
			}

		}

		/*
		 * Invoked when rows have been inserted into the table.
		 * <p>
		 * Application code will not use these methods explicitly, they
		 * are used internally by JTable.
		 *
		 * @param e the TableModelEvent encapsulating the insertion
		 */
		private void tableRowsInserted(TableModelEvent e) {

			System.out.println("tableRowsInserted with " + e);

			int start = e.getFirstRow();
			int end = e.getLastRow();
			if (start < 0) {
				start = 0;
			}
			if (end < 0) {
				end = getModel().getRowCount() - 1;
			}

			// Adjust the selection to account for the new rows.
			int length = end - start + 1;
			selectionModel.insertIndexInterval(start, length, true);

			// If we have variable height rows, adjust the row model.
			/*if (rowModel != null) {
				rowModel.insertEntries(start, length, getRowHeight());
			}*/

			int rh = getRowHeight();
			Rectangle drawRect = new Rectangle(0, start * rh, getColumnModel().getTotalColumnWidth(),
					(getModel().getRowCount() - start) * rh);

			revalidate();
			// PENDING(milne) revalidate calls repaint() if parent is a ScrollPane
			// repaint still required in the unusual case where there is no ScrollPane
			repaint(drawRect);
		}

		/*
		 * Invoked when rows have been removed from the table.
		 * <p>
		 * Application code will not use these methods explicitly, they
		 * are used internally by JTable.
		 *
		 * @param e the TableModelEvent encapsulating the deletion
		 */
		private void tableRowsDeleted(TableModelEvent e) {

			System.out.println("tableRowsDeleted with " + e);

			int start = e.getFirstRow();
			int end = e.getLastRow();
			if (start < 0) {
				start = 0;
			}
			if (end < 0) {
				end = getModel().getRowCount() - 1;
			}

			int deletedCount = end - start + 1;
			int previousRowCount = getModel().getRowCount() + deletedCount;
			// Adjust the selection to account for the new rows
			selectionModel.removeIndexInterval(start, end);

			// If we have variable height rows, adjust the row model.
			/*if (rowModel != null) {
				rowModel.removeEntries(start, deletedCount);
			}*/

			int rh = getRowHeight();
			Rectangle drawRect = new Rectangle(0, start * rh, getColumnModel().getTotalColumnWidth(), (previousRowCount - start) * rh);

			revalidate();
			// PENDING(milne) revalidate calls repaint() if parent is a ScrollPane
			// repaint still required in the unusual case where there is no ScrollPane
			repaint(drawRect);
		}

		//
		// Implementing TableColumnModelListener interface
		//

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
		}

		/**
		 * Invoked when a column is added to the table column model.
		 * <p>
		 * Application code will not use these methods explicitly, they are used internally by JTable.
		 *
		 * @see TableColumnModelListener
		 */
		@Override
		public void columnAdded(TableColumnModelEvent e) {
			// If I'm currently editing, then I should stop editing
			if (isEditing()) {
				removeEditor();
			}
			revalidate();
			repaint();
		}

		/**
		 * Invoked when a column is removed from the table column model.
		 * <p>
		 * Application code will not use these methods explicitly, they are used internally by JTable.
		 *
		 * @see TableColumnModelListener
		 */
		@Override
		public void columnRemoved(TableColumnModelEvent e) {
			// If I'm currently editing, then I should stop editing
			if (isEditing()) {
				removeEditor();
			}
			revalidate();
			repaint();
		}

		/**
		 * Invoked when a column is repositioned. If a cell is being edited, then editing is stopped and the cell is redrawn.
		 * <p>
		 * Application code will not use these methods explicitly, they are used internally by JTable.
		 *
		 * @param e
		 *            the event received
		 * @see TableColumnModelListener
		 */
		@Override
		public void columnMoved(TableColumnModelEvent e) {
			if (isEditing() && !getCellEditor().stopCellEditing()) {
				getCellEditor().cancelCellEditing();
			}
			repaint();
		}

		/**
		 * Invoked when a column is moved due to a margin change. If a cell is being edited, then editing is stopped and the cell is
		 * redrawn.
		 * <p>
		 * Application code will not use these methods explicitly, they are used internally by JTable.
		 *
		 * @param e
		 *            the event received
		 * @see TableColumnModelListener
		 */
		@Override
		public void columnMarginChanged(ChangeEvent e) {
			if (isEditing() && !getCellEditor().stopCellEditing()) {
				getCellEditor().cancelCellEditing();
			}
			/*TableColumn resizingColumn = getResizingColumn();
			// Need to do this here, before the parent's
			// layout manager calls getPreferredSize().
			if (resizingColumn != null && autoResizeMode == AUTO_RESIZE_OFF) {
				resizingColumn.setPreferredWidth(resizingColumn.getWidth());
			}
			resizeAndRepaint();*/
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
		}

		private JButton addButton;
		private boolean hasAddAction = false;
		private boolean hasRemoveAction = false;
		private boolean hasEditAction = false;

		private void buildTable() {

			for (FIBTableAction fibAction : widget.getComponent().getActions()) {
				if (fibAction instanceof FIBAddAction) {
					addActionListener = new FIBTableActionListener<>(fibAction, widget);
					hasAddAction = true;
				}
				if (fibAction instanceof FIBRemoveAction) {
					removeActionListener = new FIBTableActionListener<>(fibAction, widget);
					hasRemoveAction = true;
				}
			}

			for (FIBTableColumn column : widget.getComponent().getColumns()) {
				if (column.isEditable()) {
					hasEditAction = true;
				}
			}

			addButton = new JButton();
			addButton.setBorder(BorderFactory.createEmptyBorder());
			addButton.setContentAreaFilled(false);
			addButton.setRolloverIcon(FIBIconLibrary.ADD_FO_ICON);
			addButton.setIcon(FIBIconLibrary.ADD_ICON);

			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (addActionListener != null) {
						System.out.println("On execute " + addActionListener);
						addActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null,
								EventQueue.getMostRecentEventTime(), e.getModifiers()));
					}
				}
			});

			JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
			addButtonPanel.add(addButton);

			add(addButtonPanel, BorderLayout.SOUTH);

			refreshTable();
		}

		private void refreshTable() {

			tablePanel.removeAll();
			componentsForValues.clear();
			removeButtons.clear();
			editButtons.clear();
			remaindedOpaque.clear();
			remaindedBackground.clear();
			remaindedForeground.clear();

			if (widget == null || widget.getComponent() == null) {
				return;
			}

			addButton.setVisible(hasAddAction);

			if (widget.getComponent().getShowHeader()) {
				for (FIBTableColumn column : widget.getComponent().getColumns()) {
					boolean isFirst = (column == widget.getComponent().getColumns().get(0));
					boolean isLast = (column == widget.getComponent().getColumns().get(widget.getComponent().getColumns().size() - 1));
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = (column.getResizable() ? column.getColumnWidth() : 0.0);
					c.weighty = 0.0;
					c.gridwidth = (isLast ? GridBagConstraints.REMAINDER : 1);
					c.anchor = GridBagConstraints.CENTER;
					c.gridx = GridBagConstraints.RELATIVE;
					c.gridy = GridBagConstraints.RELATIVE;

					c.insets = new Insets(3, isFirst ? 5 : 0, 3, 0);
					c.ipadx = 0;
					c.ipady = 0;

					JLabel titleLabel = new JLabel(column.getDisplayTitle() ? widget.getLocalized(column.getTitle()) : "");
					titleLabel.setPreferredSize(new Dimension(column.getColumnWidth(), getRowHeight() + 5));
					titleLabel.setOpaque(true);
					titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
					titleLabel.setForeground(Color.WHITE);
					titleLabel.setBackground(Color.GRAY);
					tablePanel.add(titleLabel, c);

				}
			}

			if (widget.getTableModel().getValues() != null) {
				int row = 0;
				for (T value : widget.getTableModel().getValues()) {
					buildTableForValue(value, row, value == editedValue);
					row++;
				}
			}

			if (getParent() != null) {
				getParent().revalidate();
			}
			else {
				revalidate();
			}

		}

		private Map<JComponent, Boolean> remaindedOpaque = new HashMap<>();
		private Map<JComponent, Color> remaindedBackground = new HashMap<>();
		private Map<JComponent, Color> remaindedForeground = new HashMap<>();

		private void applyFocusProperties(T value, Color foreground, Color background) {
			List<JComponent> componentList = componentsForValues.get(value);
			if (componentList != null) {
				for (JComponent jComponent : componentList) {
					applyFocusPropertiesOnComponent(jComponent, foreground, background);
				}
			}
		}

		private void resetFocusProperties(T value) {
			List<JComponent> componentList = componentsForValues.get(value);
			if (componentList != null) {
				for (JComponent jComponent : componentList) {
					resetFocusPropertiesOnComponent(jComponent);
				}
			}
		}

		private void applyFocusPropertiesOnComponent(JComponent jComponent, Color foreground, Color background) {
			// Hack: don't do it for JComboBox because LAF is not allowing it
			if (jComponent instanceof JComboBox) {
				return;
			}
			remaindedOpaque.put(jComponent, jComponent.isOpaque());
			remaindedForeground.put(jComponent, jComponent.getForeground());
			remaindedBackground.put(jComponent, jComponent.getBackground());
			jComponent.setOpaque(true);
			jComponent.setForeground(foreground);
			jComponent.setBackground(background);
		}

		private void resetFocusPropertiesOnComponent(JComponent jComponent) {
			// Hack: don't do it for JComboBox because LAF is not allowing it
			if (jComponent instanceof JComboBox) {
				return;
			}
			jComponent.setOpaque(remaindedOpaque.get(jComponent) != null ? remaindedOpaque.get(jComponent) : false);
			jComponent.setForeground(remaindedForeground.get(jComponent));
			jComponent.setBackground(remaindedBackground.get(jComponent));
		}

		private void focusValue(T value) {
			// System.out.println("focus " + value);
			applyFocusProperties(value, widget.getComponent().getTextSecondarySelectionColor(),
					widget.getComponent().getBackgroundSecondarySelectionColor());
			showButtonsWhenRequired(value);
			focusedValue = value;
		}

		private void unfocusValue(T value) {
			// System.out.println("unfocus " + value);
			resetFocusProperties(value);
			hideButtons(value);
			focusedValue = null;
		}

		private void showButtonsWhenRequired(T value) {
			getRemoveButton(value).setIcon(FIBIconLibrary.REMOVE_ICON);
			getRemoveButton(value).setRolloverIcon(FIBIconLibrary.REMOVE_FO_ICON);
			getEditButton(value).setIcon(FIBIconLibrary.EDIT_ICON);
			getEditButton(value).setRolloverIcon(FIBIconLibrary.EDIT_FO_ICON);
			getRemoveButton(value).setVisible(hasRemoveAction);
			getEditButton(value).setVisible(hasEditAction);
		}

		private void temporaryHideButtons(T value) {
			getRemoveButton(value).setIcon(FIBIconLibrary.EMPTY_ICON);
			getRemoveButton(value).setRolloverIcon(FIBIconLibrary.EMPTY_ICON);
			getEditButton(value).setIcon(FIBIconLibrary.EMPTY_ICON);
			getEditButton(value).setRolloverIcon(FIBIconLibrary.EMPTY_ICON);
		}

		private void hideButtons(T value) {
			getRemoveButton(value).setVisible(false);
			getEditButton(value).setVisible(false);
		}

		private List<T> getSelection() {
			if (selectedValue == null) {
				return Collections.emptyList();
			}
			else {
				return Collections.singletonList(selectedValue);
			}
		}

		private void selectValue(T value) {

			if (editedValue != null && editedValue != value) {
				stopEditValue();
			}
			if (focusedValue != null) {
				unfocusValue(focusedValue);
			}
			selectedValue = value;
			applyFocusProperties(value, widget.getComponent().getTextSelectionColor(), widget.getComponent().getBackgroundSelectionColor());
			getRemoveButton(value).setIcon(FIBIconLibrary.REMOVE_ICON);
			getRemoveButton(value).setRolloverIcon(FIBIconLibrary.REMOVE_FO_ICON);
			getEditButton(value).setIcon(FIBIconLibrary.EDIT_ICON);
			getEditButton(value).setRolloverIcon(FIBIconLibrary.EDIT_FO_ICON);
			getRemoveButton(value).setVisible(hasRemoveAction);
			getEditButton(value).setVisible(hasEditAction);

		}

		private void unselectValue(T value) {

			List<T> oldSelection = getSelection();
			selectedValue = null;
			resetFocusProperties(value);

			FIBController ctrl = widget.getController();
			if (ctrl != null) {
				ctrl.updateSelection(widget, oldSelection, getSelection());
			}

		}

		private void editValue(T value) {
			editedValue = value;
			refreshTable();
		}

		private void stopEditValue() {
			editedValue = null;
			refreshTable();
		}

		private MouseAdapter makeMouseAdapter(T value) {
			return new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					if (selectedValue != value) {
						focusValue(value);
					}
					if (getSelectedValue() != null) {
						// In this case, we focus a value while another value is selected
						if (getSelectedValue() != value) {
							// This is not the same, temporary hide icons
							temporaryHideButtons(getSelectedValue());
						}
						else {
							// When back to the value, show icons again
							showButtonsWhenRequired(getSelectedValue());
						}
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					if (selectedValue != value) {
						unfocusValue(value);
					}
				}

				private FIBMouseEvent makeMouseEvent(MouseEvent e) {
					return new SwingFIBMouseEvent(e);
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if (widget.synchronizedWithSelection()) {
						widget.getController().setSelectionLeader(widget);
					}

					if (e.getClickCount() == 1) {
						if (e.isShiftDown() && selectedValue == value) {
							clearSelection();
						}
						else {
							select(value);
						}
						if (widget.getWidget().hasRightClickAction() && (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3)) {
							// Detected right-click associated with action
							widget.applyRightClickAction(makeMouseEvent(e));
						}
						else if (widget.getWidget().hasClickAction()) {
							// Detected click associated with action
							widget.applySingleClickAction(makeMouseEvent(e));
						}
					}
					else if (e.getClickCount() == 2) {
						if (widget.getWidget().hasDoubleClickAction()) {
							// Detected double-click associated with action
							widget.applyDoubleClickAction(makeMouseEvent(e));
						}
						else {
							editValue(value);
						}
					}

					repaint();
				}
			};

		}

		private void buildTableForValue(T value, int row, boolean isEdited) {
			List<JComponent> components = new ArrayList<>();

			if (widget == null || widget.getComponent() == null) {
				return;
			}

			MouseAdapter mouseAdapter = makeMouseAdapter(value);

			ActionListener doneActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// System.out.println("stopEditValue");
					stopEditValue();
				}
			};

			int col = 0;
			for (FIBTableColumn column : widget.getComponent().getColumns()) {
				boolean isFirst = (column == widget.getComponent().getColumns().get(0));
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.weightx = (column.getResizable() ? column.getColumnWidth() : 0);
				c.weighty = 1.0;
				c.gridwidth = 1;
				c.anchor = GridBagConstraints.WEST;
				c.ipadx = 0;
				c.ipady = 0;

				c.insets = new Insets(0, isFirst ? 5 : 0, 0, 0);

				JComponent cellRenderer;

				if (isEdited) {
					cellRenderer = widget.getTableModel().columnAt(col).makeCellEditor(value, doneActionListener);
				}
				else {
					cellRenderer = widget.getTableModel().columnAt(col).makeCellRenderer(value);
				}

				cellRenderer.setPreferredSize(new Dimension(column.getColumnWidth(), getRowHeight()));
				tablePanel.add(cellRenderer, c);
				components.add(cellRenderer);
				cellRenderer.addMouseListener(mouseAdapter);

				if (value == selectedValue /*&& !isEdited*/) {
					applyFocusPropertiesOnComponent(cellRenderer, widget.getComponent().getTextSelectionColor(),
							widget.getComponent().getBackgroundSelectionColor());
				}
				else {
					resetFocusPropertiesOnComponent(cellRenderer);
				}

				col++;
			}

			JButton removeButton = getRemoveButton(value);
			JButton editButton = getEditButton(value);

			JPanel buttonsPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c4 = new GridBagConstraints();
			buttonsPanel.add(removeButton, c4);
			buttonsPanel.add(editButton, c4);
			components.add(buttonsPanel);

			if (value == selectedValue) {
				applyFocusPropertiesOnComponent(buttonsPanel, widget.getComponent().getTextSelectionColor(),
						widget.getComponent().getBackgroundSelectionColor());
			}
			else {
				resetFocusPropertiesOnComponent(buttonsPanel);
			}

			GridBagConstraints c3 = new GridBagConstraints();
			c3.fill = GridBagConstraints.BOTH;
			c3.weightx = 1.0;
			c3.weighty = 1.0;
			c3.gridwidth = GridBagConstraints.REMAINDER;
			c3.anchor = GridBagConstraints.CENTER;
			c3.ipadx = 0;
			c3.ipady = 0;

			tablePanel.add(buttonsPanel, c3);

			removeButton.setVisible(hasRemoveAction && value == selectedValue);
			editButton.setVisible(hasEditAction && value == selectedValue);

			componentsForValues.put(value, components);

		}

		private JButton getRemoveButton(T value) {

			JButton returned = removeButtons.get(value);
			if (returned == null) {
				returned = makeRemoveButton(value);
				removeButtons.put(value, returned);
			}
			return returned;
		}

		private JButton getEditButton(T value) {

			JButton returned = editButtons.get(value);
			if (returned == null) {
				returned = makeEditButton(value);
				editButtons.put(value, returned);
			}
			return returned;
		}

		private JButton makeRemoveButton(T value) {

			JButton removeButton = new JButton();
			removeButton.setBorder(BorderFactory.createEmptyBorder());
			removeButton.setRolloverIcon(FIBIconLibrary.REMOVE_FO_ICON);
			removeButton.setIcon(FIBIconLibrary.REMOVE_ICON);
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (removeActionListener != null) {
						removeActionListener.setSelectedObject(value);
						removeActionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null,
								EventQueue.getMostRecentEventTime(), e.getModifiers()));
					}
				}
			});
			removeButton.addMouseListener(makeMouseAdapter(value));
			return removeButton;
		}

		private JButton makeEditButton(T value) {

			JButton editButton = new JButton();
			editButton.setBorder(BorderFactory.createEmptyBorder());
			if (value == editedValue) {
				editButton.setIcon(FIBIconLibrary.DONE_ICON);
				editButton.setRolloverIcon(FIBIconLibrary.DONE_ICON);
			}
			else {
				editButton.setRolloverIcon(FIBIconLibrary.EDIT_FO_ICON);
				editButton.setIcon(FIBIconLibrary.EDIT_ICON);
			}
			editButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (editedValue == value) {
						stopEditValue();
					}
					else {
						editValue(value);
					}
				}
			});
			editButton.addMouseListener(makeMouseAdapter(value));
			return editButton;
		}

		public int getRowHeight() {
			if (widget.getComponent().getRowHeight() != null) {
				return widget.getComponent().getRowHeight();
			}
			return 20;
		}

		public void setRowHeight(int rowHeight) {
			// Not applicable
		}

		public int getVisibleRowCount() {
			return widget.getTableModel().getValues().size();
		}

		public void setVisibleRowCount(int rowCount) {
			// Not applicable
		}

		public ListSelectionModel getSelectionModel() {
			return selectionModel;
		}

		public boolean isEditing() {
			return editedValue != null;
		}

		public int getEditingRow() {
			// Not applicable
			return -1;
		}

		public int getEditingColumn() {
			// Not applicable
			return -1;
		}

		public TableCellEditor getCellEditor() {
			// Not applicable
			return null;
		}

		public int convertRowIndexToModel(int leadIndex) {
			return leadIndex;
		}

		public int convertRowIndexToView(int index) {
			return index;
		}

		public void removeEditor() {
		}

		public void select(T value) {
			if (getFocusedValue() != null) {
				unfocusValue(getFocusedValue());
			}
			if (getSelectedValue() != null) {
				unselectValue(getSelectedValue());
			}
			selectValue(value);
			widget.setSelected(value);
			widget.setSelection(Collections.singletonList(value));
			repaint();
		}

		public void clearSelection() {
			if (getSelectedValue() != null) {
				unselectValue(getSelectedValue());
			}
			widget.setSelected(null);
			widget.setSelection(Collections.emptyList());
			repaint();
		}

	}

	private JFDTable<T> makeJTable() {
		JFDTable<T> returned = new JFDTable<T>(widget);
		// returned.getSelectionModel().addListSelectionListener(widget);
		return returned;

	}

	public void delete() {

		if (scrollPane != null && widget.getTable().getCreateNewRowOnClick()) {
			for (MouseListener l : scrollPane.getMouseListeners()) {
				scrollPane.removeMouseListener(l);
			}
		}
	}

	public JFDTable<T> getJTable() {
		return jTable;
	}

}

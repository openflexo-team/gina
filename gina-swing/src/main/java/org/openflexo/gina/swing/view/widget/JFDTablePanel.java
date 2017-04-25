package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

import org.openflexo.gina.model.widget.FIBTableColumn;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.gina.view.widget.table.impl.FIBTableModel;
import org.openflexo.gina.view.widget.table.impl.FIBTableModel.ModelObjectHasChanged;
import org.openflexo.icon.IconFactory;

@SuppressWarnings("serial")
public class JFDTablePanel<T> extends JPanel {

	private JFDTable jTable;
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

		public JFDTable(JFDFIBTableWidget<T> widget) {
			super();
			gridBagLayout = new GridBagLayout();
			setLayout(gridBagLayout);
			this.widget = widget;
			setTableModel(widget.getTableModel());
			rebuildTable();
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

				// Set the column model of the header as well.
				/*if (tableHeader != null) {
					tableHeader.setColumnModel(columnModel);
				}*/

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

		@Override
		public void tableChanged(TableModelEvent e) {

			System.out.println("Je recois TableModelEvent " + e);
			System.out.println("rows=" + getModel().getRowCount());
			if (e instanceof ModelObjectHasChanged) {
				System.out.println(">>>>>>>>>> OK le model change pour celui la");
			}

			System.out.println(
					/*"col=" + e.getColumn() +*/ " firstRow=" + e.getFirstRow() + " lastRow=" + e.getLastRow() + " type=" + e.getType());

			if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {

				System.out.println("HEADER");

				System.out.println("rows=" + getModel().getRowCount());
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

			int modelColumn = e.getColumn();
			int start = e.getFirstRow();
			int end = e.getLastRow();

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

			revalidate();
			repaint();

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

		private void rebuildTable() {
			removeAll();

			if (widget.getComponent().getShowHeader()) {
				for (FIBTableColumn column : widget.getComponent().getColumns()) {
					boolean isLast = (column == widget.getComponent().getColumns().get(widget.getComponent().getColumns().size() - 1));
					GridBagConstraints c = new GridBagConstraints();
					// c.insets = new Insets(5, 5, 5, 5);
					c.fill = GridBagConstraints.NONE;
					c.weightx = (column.getResizable() ? column.getColumnWidth() : 0);
					c.weighty = 1.0;
					c.gridwidth = (isLast ? GridBagConstraints.REMAINDER : 1);
					c.anchor = GridBagConstraints.CENTER;
					/*if (twoColsConstraints.getExpandVertically()) {
						// c.weighty = 1.0;
						c.fill = GridBagConstraints.VERTICAL;
					}
					else {
						// c.insets = new Insets(5, 2, 0, 2);
					}*/

					JLabel titleLabel = new JLabel(column.getTitle());
					titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
					add(titleLabel, c);
				}
			}

			if (widget.getTableModel().getValues() != null) {
				for (T value : widget.getTableModel().getValues()) {
					System.out.println("On affiche la valeur " + value);
				}
			}

			JButton addButton = new JButton();
			addButton.setBorder(BorderFactory.createEmptyBorder());
			addButton.setRolloverIcon(IconFactory.getDisabledIcon(FIBIconLibrary.ADD_ICON));
			addButton.setIcon(FIBIconLibrary.ADD_ICON);

			GridBagConstraints c = new GridBagConstraints();
			// c.insets = new Insets(3, 3, 3, 3);
			// System.out.println("twoColsConstraints=" + twoColsConstraints);
			c.insets = new Insets(5, 5, 5, 5);
			c.fill = GridBagConstraints.NONE;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.NORTHWEST;
			/*if (twoColsConstraints.getExpandVertically()) {
				// c.weighty = 1.0;
				c.fill = GridBagConstraints.VERTICAL;
			}
			else {
				// c.insets = new Insets(5, 2, 0, 2);
			}*/

			add(addButton, c);

		}

		public int getVisibleRowCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setVisibleRowCount(int visibleRowCount) {
			// TODO Auto-generated method stub

		}

		public int getRowHeight() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void setRowHeight(int rowHeight) {
			// TODO Auto-generated method stub

		}

		public ListSelectionModel getSelectionModel() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isEditing() {
			// TODO Auto-generated method stub
			return false;
		}

		public int getEditingRow() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getEditingColumn() {
			// TODO Auto-generated method stub
			return 0;
		}

		public TableCellEditor getCellEditor() {
			// TODO Auto-generated method stub
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
	}

	private JFDTable makeJTable() {
		JFDTable returned = new JFDTable(widget);

		/*returned.setVisibleRowCount(0);
		returned.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
		returned.setAutoCreateRowSorter(true);
		returned.setFillsViewportHeight(true);
		returned.setShowHorizontalLines(false);
		returned.setShowVerticalLines(false);
		returned.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		returned.addFocusListener(widget);
		
		for (int i = 0; i < widget.getTableModel().getColumnCount(); i++) {
			TableColumn col = returned.getColumnModel().getColumn(i);
			// FlexoLocalization.localizedForKey(getController().getLocalizer(),getTableModel().columnAt(i).getTitle());
			col.setWidth(widget.getTableModel().getDefaultColumnSize(i));
			col.setPreferredWidth(widget.getTableModel().getDefaultColumnSize(i));
			if (widget.getTableModel().getColumnResizable(i)) {
				col.setResizable(true);
			}
			else {
				// L'idee, c'est d'etre vraiment sur ;-) !
				col.setWidth(widget.getTableModel().getDefaultColumnSize(i));
				col.setMinWidth(widget.getTableModel().getDefaultColumnSize(i));
				col.setMaxWidth(widget.getTableModel().getDefaultColumnSize(i));
				col.setResizable(false);
			}
			if (widget.getTableModel().columnAt(i).requireCellRenderer()) {
				col.setCellRenderer(widget.getTableModel().columnAt(i).getCellRenderer());
			}
			if (widget.getTableModel().columnAt(i).requireCellEditor()) {
				col.setCellEditor(widget.getTableModel().columnAt(i).getCellEditor());
			}
		}
		if (widget.getTable().getRowHeight() != null) {
			returned.setRowHeight(widget.getTable().getRowHeight());
		}
		if (widget.getTable().getVisibleRowCount() != null) {
			returned.setVisibleRowCount(widget.getTable().getVisibleRowCount());
			if (returned.getRowHeight() == 0) {
				returned.setRowHeight(18);
			}
		}
		
		if (widget.getTable().getSelectionMode() != null) {
			returned.setSelectionMode(widget.getTable().getSelectionMode().getMode());
		}
		
		// jTable.getTableHeader().setReorderingAllowed(false);
		
		returned.getSelectionModel().addListSelectionListener(widget);
		
		// _listSelectionModel = jTable.getSelectionModel();
		// _listSelectionModel.addListSelectionListener(this);
		
		if (widget.getWidget().getBoundToSelectionManager()) {
			returned.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					widget.getController().performCopyAction(widget.getSelected(), widget.getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_C, JFIBTableWidget.META_MASK, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			returned.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					widget.getController().performCutAction(widget.getSelected(), widget.getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_X, JFIBTableWidget.META_MASK, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			returned.registerKeyboardAction(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					widget.getController().performPasteAction(widget.getSelected(), widget.getSelection());
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_V, JFIBTableWidget.META_MASK, false), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		}
		
		if (widget.getTable().getCreateNewRowOnClick()) {
			returned.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (jTable.getCellEditor() != null) {
						jTable.getCellEditor().stopCellEditing();
						e.consume();
					}
					if (widget.getTable().getCreateNewRowOnClick()) {
						if (!e.isConsumed() && e.getClickCount() == 2) {
							// System.out.println("OK, on essaie de gerer un new par double click");
							Enumeration<FIBTableActionListener<T>> en = widget.getFooter().getAddActionListeners();
							while (en.hasMoreElements()) {
								FIBTableActionListener<T> action = en.nextElement();
								if (action.isAddAction()) {
									action.actionPerformed(new ActionEvent(jTable, ActionEvent.ACTION_PERFORMED, null,
											EventQueue.getMostRecentEventTime(), e.getModifiers()));
									break;
								}
							}
						}
					}
				}
			});
		}*/

		return returned;

	}

	public void delete() {

		if (scrollPane != null && widget.getTable().getCreateNewRowOnClick()) {
			for (MouseListener l : scrollPane.getMouseListeners()) {
				scrollPane.removeMouseListener(l);
			}
		}
	}

	public JFDTable getJTable() {
		return jTable;
	}
}

package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.openflexo.gina.view.widget.table.impl.FIBTableActionListener;

@SuppressWarnings("serial")
public class JTablePanel<T> extends JPanel {

	private JXTable jTable;
	private final JScrollPane scrollPane;
	private final JFIBTableWidget<T> widget;

	public JTablePanel(JFIBTableWidget<T> aWidget) {
		super(new BorderLayout());
		setOpaque(false);
		this.widget = aWidget;

		jTable = makeJTable();

		scrollPane = new JScrollPane(jTable);
		scrollPane.setOpaque(false);

		add(scrollPane, BorderLayout.CENTER);

		if (widget.getTable().getShowFooter() && widget.getFooter() != null) {
			add(widget.getFooter().getFooterComponent(), BorderLayout.SOUTH);
		}

	}

	public void updateTable() {
		scrollPane.getViewport().remove(jTable);
		jTable = makeJTable();
		scrollPane.setViewportView(jTable);
	}

	private JXTable makeJTable() {
		JXTable returned = new JXTable(widget.getTableModel()) {

			@Override
			protected void resetDefaultTableCellRendererColors(Component renderer, int row, int column) {
			}

		};
		returned.setVisibleRowCount(0);
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
		}

		return returned;
	}

	public void delete() {

		if (scrollPane != null && widget.getTable().getCreateNewRowOnClick()) {
			for (MouseListener l : scrollPane.getMouseListeners()) {
				scrollPane.removeMouseListener(l);
			}
		}
	}

	public JXTable getJTable() {
		return jTable;
	}
}
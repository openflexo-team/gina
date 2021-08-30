/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Tabular view representing an AbstractModel
 * 
 * @author sguerin
 * 
 */
public class TabularPanel extends JPanel implements TableModelListener, ListSelectionListener, PropertyChangeListener {

	protected static final Logger LOGGER = Logger.getLogger(TabularPanel.class.getPackage().getName());

	protected JTable _table;

	protected AbstractModel _model;

	protected ListSelectionModel _listSelectionModel;

	private JScrollPane scrollPane;

	protected Vector<Object> _selectedObjects;
	protected boolean _selectedObjectsNeedsRecomputing;

	public TabularPanel(AbstractModel<?, ?> model, int visibleRowCount) {
		this(model);
		setVisibleRowCount(visibleRowCount);
	}

	public TabularPanel(@Nonnull AbstractModel<?, ?> model) {
		super();
		_model = model;

		model.addTableModelListener(this);
		if (model.getModel() != null) {
			// model.getModel().addObserver(this);
			model.getModel().getPropertyChangeSupport().addPropertyChangeListener(this);
		}
		model.fireTableDataChanged();

		_table = new FlexoJTable(model);
		// _table.setPreferredSize(new Dimension(model.getTotalPreferredWidth(),100));

		_selectedObjects = new Vector<>();
		_selectedObjectsNeedsRecomputing = false;

		// _table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn col = _table.getColumnModel().getColumn(i);
			col.setPreferredWidth(model.getDefaultColumnSize(i));
			if (model.getColumnResizable(i)) {
				col.setResizable(true);
			}
			else {
				// L'idee, c'est d'etre vraiment sur ;-) !
				col.setWidth(model.getDefaultColumnSize(i));
				col.setMinWidth(model.getDefaultColumnSize(i));
				col.setMaxWidth(model.getDefaultColumnSize(i));
				col.setResizable(false);
			}
			if (model.columnAt(i).requireCellRenderer()) {
				col.setCellRenderer(model.columnAt(i).getCellRenderer());
			}
			if (model.columnAt(i).requireCellEditor()) {
				col.setCellEditor(model.columnAt(i).getCellEditor());
			}
		}

		if (model.getRowHeight() > 0) {
			_table.setRowHeight(model.getRowHeight());
		}

		_table.setShowVerticalLines(true);

		_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		_listSelectionModel = _table.getSelectionModel();
		_listSelectionModel.addListSelectionListener(this);

		scrollPane = new JScrollPane(_table);
		setLayout(new BorderLayout());
		add(_table.getTableHeader(), BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				int col = _table.columnAtPoint(p);
				int row = _table.rowAtPoint(p);

				if (col > -1 && col < _model.getColumnCount() && row > -1 && row < _model.getRowCount()) {

					if (e.getClickCount() == 2) {
						if (_model.isCellEditable(row, col)) {
							if (LOGGER.isLoggable(Level.FINE)) {
								if (LOGGER.isLoggable(Level.FINE)) {
									LOGGER.fine("Double-click detected in a editable cell. Do nothing !");
								}
							}
						}
						else if (row > -1 && row < _model.getRowCount()) {
							if (LOGGER.isLoggable(Level.FINE)) {
								if (LOGGER.isLoggable(Level.FINE)) {
									LOGGER.fine("Double-click detected in a NON-editable cell. Select !");
								}
							}
						}
					}
					else if (e.getClickCount() == 1) {
						if (LOGGER.isLoggable(Level.FINE)) {
							if (LOGGER.isLoggable(Level.FINE)) {
								LOGGER.fine("Simple-click detected !");
							}
						}
						if (_table.getEditingRow() > -1 && _table.getEditingRow() != row) {
							if (LOGGER.isLoggable(Level.INFO)) {
								LOGGER.info("Change row where edition was started, fire stop editing !");
							}
							TableCellEditor cellEditor = _model.columnAt(col).getCellEditor();
							if (cellEditor != null) {
								cellEditor.stopCellEditing();
							}
						}

						if (_model.columnAt(col) instanceof ToggleIconColumn) {
							ToggleIconColumn<?> toggleIconColumn = (ToggleIconColumn<?>) _model.columnAt(col);
							toggleIconColumn.toogleValue(row);
						}
					}
				}
			}
			/*public void mousePressed(MouseEvent e)
			{
			    super.mousePressed(e);
			    if ((!e.isConsumed()) && (_controller.getSelectionManager() != null)) {
			        _controller.getSelectionManager().getContextualMenuManager().processMousePressed(e);
			    }
			
			}
			
			public void mouseReleased(MouseEvent e)
			{
			    super.mouseReleased(e);
			    if ((!e.isConsumed()) && (_controller.getSelectionManager() != null)) {
			        _controller.getSelectionManager().getContextualMenuManager().processMouseReleased(e);
			    }
			}*/
		});

		/*_table.addMouseMotionListener(new MouseMotionAdapter() {
		    public void mouseMoved(MouseEvent e)
		    {
		        super.mouseMoved(e);
		        if ((!e.isConsumed()) && (_controller.getSelectionManager() != null)) {
		            _controller.getSelectionManager().getContextualMenuManager().processMouseMoved(e);
		        }
		    }
		});*/
		revalidate();

	}

	public AbstractModel<?, ?> getModel() {
		return _model;
	}

	public void setVisibleRowCount(int rows) {
		int height = 0;
		for (int row = 0; row < rows; row++) {
			height += _table.getRowHeight(row);
		}
		_table.setPreferredScrollableViewportSize(new Dimension(_table.getPreferredScrollableViewportSize().width, height));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// Ignore extra messages.
		if (e.getValueIsAdjusting()) {
			return;
		}

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("valueChanged() ListSelectionEvent=" + e + " ListSelectionModel=" + _listSelectionModel.toString());
		}
		_selectedObjectsNeedsRecomputing = true;

	}

	public Vector<Object> getSelectedObjects() {
		if (_selectedObjectsNeedsRecomputing) {
			_selectedObjects.clear();
			for (int i = 0; i < _model.getRowCount(); i++) {
				if (_listSelectionModel.isSelectedIndex(i)) {
					_selectedObjects.add(_model.elementAt(i));
				}
			}
			_selectedObjectsNeedsRecomputing = false;
		}
		return _selectedObjects;
	}

	public boolean isSelected(Vector<?> objectList) {
		return getSelectedObjects().containsAll(objectList);
	}

	public boolean isSelected(Object object) {
		return getSelectedObjects().contains(object);
	}

	public Vector<?> getObjects() {
		return getSelectedObjects();
	}

	public HasPropertyChangeSupport getObject() {
		return _model.getModel();
	}

	/*@Override
	public void update(Observable o, Object dataModification) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("update received in TabularPanel for " + o + " dataModification=" + dataModification);
		}
		_model.fireTableDataChanged();
	}*/

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// if (LOGGER.isLoggable(Level.FINE)) {
		// LOGGER.fine("update received in TabularPanel for " + o + " dataModification=" + dataModification);
		// }
		_model.fireTableDataChanged();
	}

	/**
	 * Overrides
	 * 
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e instanceof AbstractModel.ModelObjectHasChanged) {
			AbstractModel<?, ?>.ModelObjectHasChanged event = (AbstractModel<?, ?>.ModelObjectHasChanged) e;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Model has changed from " + event.getOldModel() + " to " + event.getNewModel());
			}
			if (event.getOldModel() != null) {
				event.getOldModel().getPropertyChangeSupport().removePropertyChangeListener(this);
				// event.getOldModel().deleteObserver(this);
			}
			if (event.getNewModel() != null) {
				event.getNewModel().getPropertyChangeSupport().addPropertyChangeListener(this);
				// event.getNewModel().addObserver(this);
			}
		}
		else if (e instanceof AbstractModel.SelectObjectEvent) {
			AbstractModel<?, ?>.SelectObjectEvent event = (AbstractModel<?, ?>.SelectObjectEvent) e;
			selectObject(event.getSelectedObject());
		}
		else if (e instanceof AbstractModel.RowMoveForObjectEvent) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Reselect object, and then the edited cell");
			}
			AbstractModel<?, ?>.RowMoveForObjectEvent event = (AbstractModel<?, ?>.RowMoveForObjectEvent) e;
			selectObject(event.getEditedObject());
			_table.setEditingColumn(event.getColumn());
			_table.setEditingRow(event.getNewRow());
		}

	}

	public void selectObject(Object object) {
		resetSelection();

		if (isSelected(object) == false) {
			// Change the selection status of b
			int index = _model.indexOf(object);
			_listSelectionModel.removeListSelectionListener(this);
			_listSelectionModel.addSelectionInterval(index, index);
			_listSelectionModel.addListSelectionListener(this);
			_selectedObjectsNeedsRecomputing = true;
		}
	}

	public void resetSelection() {
		_selectedObjectsNeedsRecomputing = true;
		_listSelectionModel.removeListSelectionListener(this);
		_listSelectionModel.clearSelection();
		_listSelectionModel.addListSelectionListener(this);
	}

}

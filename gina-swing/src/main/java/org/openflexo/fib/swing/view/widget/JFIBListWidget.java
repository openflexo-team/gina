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

import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.swing.view.SwingRenderingTechnologyAdapter;
import org.openflexo.fib.view.widget.impl.FIBListWidgetImpl;
import org.openflexo.gina.event.description.EventDescription;

public class JFIBListWidget<T> extends FIBListWidgetImpl<JList<T>, T>implements FocusListener {

	static final Logger LOGGER = Logger.getLogger(JFIBListWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingTechnologyAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingListRenderingTechnologyAdapter<T> extends SwingRenderingTechnologyAdapter<JList<T>>
			implements ListRenderingTechnologyAdapter<JList<T>> {

		@Override
		public Object getSelectedItem(JList<T> component) {
			return component.getSelectedValue();
		}

		@Override
		public void setSelectedItem(JList<T> component, Object item) {
			component.setSelectedValue(item, true);
		}

		@Override
		public int getSelectedIndex(JList<T> component) {
			return component.getSelectedIndex();
		}

		@Override
		public void setSelectedIndex(JList<T> component, int index) {
			component.setSelectedIndex(index);
		}

		@Override
		public int getVisibleRowCount(JList<T> component) {
			return component.getVisibleRowCount();
		}

		@Override
		public void setVisibleRowCount(JList<T> component, int visibleRowCount) {
			component.setVisibleRowCount(visibleRowCount);
		}

		@Override
		public int getRowHeight(JList<T> component) {
			return component.getFixedCellHeight();
		}

		@Override
		public void setRowHeight(JList<T> component, int rowHeight) {
			component.setFixedCellHeight(rowHeight);
		}

		@Override
		public ListSelectionModel getListSelectionModel(JList<T> component) {
			return component.getSelectionModel();
		}

	}

	public JFIBListWidget(FIBList model, FIBController controller) {
		super(model, controller, new SwingListRenderingTechnologyAdapter<T>());

		updateMultipleValues();

		updateFont();
	}

	@Override
	protected JList<T> makeTechnologyComponent() {

		JList<T> list = new JList<T>();
		list.setCellRenderer(getListCellRenderer());
		list.setSelectionMode(getWidget().getSelectionMode().getMode());
		if (getWidget().getVisibleRowCount() != null) {
			list.setVisibleRowCount(getWidget().getVisibleRowCount());
		}
		if (getWidget().getRowHeight() != null) {
			list.setFixedCellHeight(getWidget().getRowHeight());
		}
		list.setLayoutOrientation(getWidget().getLayoutOrientation().getSwingValue());
		list.addFocusListener(this);
		list.setBorder(BorderFactory.createEtchedBorder());

		// _list.setMinimumSize(new Dimension(60,60));
		// _list.setPreferredSize(new Dimension(60,60));
		list.revalidate();
		list.repaint();

		return list;
	}

	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;
		super.executeEvent(e);

		getDynamicJComponent().revalidate();
		getDynamicJComponent().repaint();

		System.out.println("Execute end");
		widgetExecuting = false;
	}

	private FIBListModel oldListModel = null;

	@Override
	protected void proceedToListModelUpdate(FIBListModel aListModel) {
		// logger.info("************* Updating GUI with " + aListModel);
		if (getDynamicJComponent() != null) {
			widgetUpdating = true;
			if (oldListModel != null) {
				getDynamicJComponent().getSelectionModel().removeListSelectionListener(oldListModel);
			}
			oldListModel = aListModel;
			getDynamicJComponent().setLayoutOrientation(getWidget().getLayoutOrientation().getSwingValue());
			getDynamicJComponent().setSelectionMode(getWidget().getSelectionMode().getMode());
			if (getWidget().getVisibleRowCount() != null) {
				getDynamicJComponent().setVisibleRowCount(getWidget().getVisibleRowCount());
			}
			else {
				getDynamicJComponent().setVisibleRowCount(-1);
			}
			if (getWidget().getRowHeight() != null) {
				getDynamicJComponent().setFixedCellHeight(getWidget().getRowHeight());
			}
			else {
				getDynamicJComponent().setFixedCellHeight(-1);
			}
			getDynamicJComponent().setModel(aListModel);
			getDynamicJComponent().revalidate();
			getDynamicJComponent().repaint();
			getDynamicJComponent().getSelectionModel().addListSelectionListener(aListModel);
			widgetUpdating = false;
			Object objectToSelect = null;
			if (getComponent().getSelected().isValid()) {
				try {
					objectToSelect = getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if ((objectToSelect == null) && (getWidget().getData() == null || !getWidget().getData().isValid())
					&& getWidget().getAutoSelectFirstRow() && getDynamicJComponent().getModel().getSize() > 0) {
				objectToSelect = getDynamicJComponent().getModel().getElementAt(0);
			}
			if (objectToSelect != null) {
				for (int i = 0; i < getDynamicJComponent().getModel().getSize(); i++) {
					if (getDynamicJComponent().getModel().getElementAt(i) == objectToSelect) {
						final int index = i;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getDynamicJComponent().setSelectedIndex(index);
							}
						});
					}
				}
			}
		}
		/*if (getWidget().getAutoSelectFirstRow() && _list.getModel().getSize() > 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_list.setSelectedIndex(0);
				}
			});
		}*/
	}

	@Override
	public JComponent getJComponent() {
		return getDynamicJComponent();
	}

}

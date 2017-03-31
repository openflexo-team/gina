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

import java.awt.Color;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBList.LayoutOrientation;
import org.openflexo.gina.model.widget.SelectionMode;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBListWidgetImpl;

public class JFIBListWidget<T> extends FIBListWidgetImpl<JList<T>, T> implements FocusListener, JFIBView<FIBList, JList<T>> {

	static final Logger LOGGER = Logger.getLogger(JFIBListWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JComboBox<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingListRenderingAdapter<T> extends SwingRenderingAdapter<JList<T>> implements ListRenderingAdapter<JList<T>, T> {

		@Override
		public T getSelectedItem(JList<T> component) {
			return component.getSelectedValue();
		}

		@Override
		public void setSelectedItem(JList<T> component, T item) {
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

		@Override
		public Color getDefaultForegroundColor(JList<T> component) {
			return UIManager.getColor("List.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JList<T> component) {
			return UIManager.getColor("List.background");
		}

	}

	public JFIBListWidget(FIBList model, FIBController controller) {
		super(model, controller, new SwingListRenderingAdapter<T>());

		updateMultipleValues();

		updateFont();
	}

	@Override
	public SwingListRenderingAdapter<T> getRenderingAdapter() {
		return (SwingListRenderingAdapter<T>) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	protected JList<T> makeTechnologyComponent() {

		JList<T> list = new JList<>();
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
		super.executeEvent(e);
		widgetExecuting = true;

		getTechnologyComponent().revalidate();
		getTechnologyComponent().repaint();

		System.out.println("Execute end");
		widgetExecuting = false;
	}

	private FIBListModel oldListModel = null;

	@Override
	protected void proceedToListModelUpdate(FIBListModel aListModel) {
		// logger.info("************* Updating GUI with " + aListModel);
		if (getTechnologyComponent() != null) {
			// widgetUpdating = true;
			if (oldListModel != null) {
				getTechnologyComponent().getSelectionModel().removeListSelectionListener(oldListModel);
			}
			oldListModel = aListModel;
			LayoutOrientation layoutOrientation = getWidget().getLayoutOrientation();
			if (layoutOrientation != null) {
				getTechnologyComponent().setLayoutOrientation(layoutOrientation.getSwingValue());
			}
			SelectionMode selectionMode = getWidget().getSelectionMode();
			if (selectionMode != null) {
				getTechnologyComponent().setSelectionMode(selectionMode.getMode());
			}
			if (getWidget().getVisibleRowCount() != null) {
				getTechnologyComponent().setVisibleRowCount(getWidget().getVisibleRowCount());
			}
			else {
				getTechnologyComponent().setVisibleRowCount(-1);
			}
			if (getWidget().getRowHeight() != null) {
				getTechnologyComponent().setFixedCellHeight(getWidget().getRowHeight());
			}
			else {
				getTechnologyComponent().setFixedCellHeight(-1);
			}
			getTechnologyComponent().setModel(aListModel);
			getTechnologyComponent().revalidate();
			getTechnologyComponent().repaint();
			getTechnologyComponent().getSelectionModel().addListSelectionListener(aListModel);
			// widgetUpdating = false;
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
					&& getWidget().getAutoSelectFirstRow() && getTechnologyComponent().getModel().getSize() > 0) {
				objectToSelect = getTechnologyComponent().getModel().getElementAt(0);
			}
			if (objectToSelect != null) {
				for (int i = 0; i < getTechnologyComponent().getModel().getSize(); i++) {
					if (getTechnologyComponent().getModel().getElementAt(i) == objectToSelect) {
						final int index = i;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								getTechnologyComponent().setSelectedIndex(index);
							}
						});
					}
				}
			}
		}
		/*
		 * if (getWidget().getAutoSelectFirstRow() && _list.getModel().getSize()
		 * > 0) { SwingUtilities.invokeLater(new Runnable() {
		 * 
		 * @Override public void run() { _list.setSelectedIndex(0); } }); }
		 */
	}

}

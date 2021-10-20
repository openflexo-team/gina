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

package org.openflexo.gina.view.widget.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueArrayChangeListener;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.widget.FIBMultipleValues;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBMultipleValueWidget;
import org.openflexo.toolbox.StringUtils;

/**
 * Default base implementation for a generic and abstract widget allowing to handle a list of values
 * 
 * @author sylvain
 * 
 * @param <M>
 *            type of {@link FIBComponent} this view represents (here a subclass of FIBMultipleValues)
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this view
 * @param <I>
 *            type of iterable beeing managed by this widget
 */
public abstract class FIBMultipleValueWidgetImpl<M extends FIBMultipleValues, C, T, I> extends FIBWidgetViewImpl<M, C, T>
		implements FIBMultipleValueWidget<M, C, T, I> {

	static final Logger LOGGER = Logger.getLogger(FIBMultipleValueWidgetImpl.class.getPackage().getName());

	private T selected;
	private int selectedIndex;

	private FIBMultipleValueCellRenderer listCellRenderer;
	protected FIBMultipleValueModel<I> multipleValueModel;

	private BindingValueListChangeListener<Object, List<Object>> listBindingValueChangeListener;
	private BindingValueArrayChangeListener<Object> arrayBindingValueChangeListener;

	public FIBMultipleValueWidgetImpl(M model, FIBController controller, MultipleValueRenderingAdapter<C, I> RenderingAdapter) {
		super(model, controller, RenderingAdapter);
		// ((FIBMultipleValueModelImpl) getMultipleValueModel()).update();
		// proceedToListModelUpdate();
	}

	@Override
	protected void componentBecomesVisible() {
		getWidget().getList().clearCacheForBindingEvaluationContext(getBindingEvaluationContext());
		getWidget().getArray().clearCacheForBindingEvaluationContext(getBindingEvaluationContext());
		super.componentBecomesVisible();
		listenListValueChange();
		listenArrayValueChange();
		if (getTechnologyComponent() != null) {
			performUpdate();
		}
	}

	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
		stopListenListValueChange();
		stopListenArrayValueChange();
	}

	@Override
	public MultipleValueRenderingAdapter<C, I> getRenderingAdapter() {
		return (MultipleValueRenderingAdapter<C, I>) super.getRenderingAdapter();
	}

	private void listenListValueChange() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
		}

		// TODO: investigate on this bug and workaround
		// Binding should be notified and we should not force revalidate
		if (getComponent().getList() != null && getComponent().getList().isSet() && !getComponent().getList().isValid()) {
			String invalidBindingReason = getComponent().getList().invalidBindingReason();
			// TODO: is this still required ?
			getComponent().getList().revalidate();
			LOGGER.warning("binding was not valid: " + getComponent().getList() + " reason: " + invalidBindingReason);
			if (getComponent().getList().isValid()) {
				LOGGER.warning("Binding has been force revalidated and is now valid. Please investigate.");
			}
		}

		if (getComponent().getList() != null && getComponent().getList().revalidate()) {
			listBindingValueChangeListener = new BindingValueListChangeListener<Object, List<Object>>(
					((DataBinding) getComponent().getList()), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<Object> newValue) {
					// System.out.println(" bindingValueChanged() detected for list=" + getComponent().getList() + " with newValue=" +
					// newValue
					// + " source=" + source);
					updateMultipleValues();
				}
			};
		}
	}

	private void stopListenListValueChange() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
			listBindingValueChangeListener = null;
		}
	}

	private void listenArrayValueChange() {
		if (arrayBindingValueChangeListener != null) {
			arrayBindingValueChangeListener.stopObserving();
			arrayBindingValueChangeListener.delete();
		}
		if (getComponent().getArray() != null && getComponent().getArray().isValid()) {
			arrayBindingValueChangeListener = new BindingValueArrayChangeListener<Object>(getComponent().getArray(),
					getBindingEvaluationContext()) {
				@Override
				public void bindingValueChanged(Object source, Object[] newValue) {
					// System.out.println(" bindingValueChanged() detected for array="
					// + getComponent().getArray() + " with newValue="
					// + newValue + " source=" + source);
					updateMultipleValues();
				}
			};
		}
	}

	private void stopListenArrayValueChange() {
		if (arrayBindingValueChangeListener != null) {
			arrayBindingValueChangeListener.stopObserving();
			arrayBindingValueChangeListener.delete();
			arrayBindingValueChangeListener = null;
		}
	}

	@Override
	public T updateData() {
		T newValue = super.updateData();
		if (getWidget().getData() != null && getWidget().getData().isSet() && getWidget().getData().isValid()) {
			Type type = getWidget().getData().getAnalyzedType();
			if (type instanceof Class && ((Class<?>) type).isEnum()) {
				updateMultipleValues();
			}
		}
		return newValue;
	}

	@Override
	public synchronized void delete() {
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.stopObserving();
			listBindingValueChangeListener.delete();
		}
		if (arrayBindingValueChangeListener != null) {
			arrayBindingValueChangeListener.stopObserving();
			arrayBindingValueChangeListener.delete();
		}
		super.delete();
	}

	@SuppressWarnings("serial")
	public class FIBMultipleValueModelImpl extends AbstractListModel<I> implements FIBMultipleValueModel<I> {
		private List<I> list = null;
		private I[] array = null;

		protected FIBMultipleValueModelImpl() {
			super();
			update();
		}

		final private void update() {
			LOGGER.fine("update FIBMultipleValueModel");

			if (isDeleted() || getWidget() == null) {
				return;
			}

			list = null;
			array = null;

			if (getWidget() != null && getWidget().getList() != null && getWidget().getList().isValid()) {

				List<?> accessedList = null;
				try {
					accessedList = getWidget().getList().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// e.printStackTrace();
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
				if (accessedList instanceof List) {
					list = (List<I>) accessedList;
				}

				/*if (DEBUG) {
					System.out.println("list=" + list);
					System.out.println("getWidget().getList()=" + getWidget().getList());
				
					DataBinding db = new DataBinding("data", getWidget().getList().getOwner(), Object.class, BindingDefinitionType.GET);
					System.out.println("db=" + db);
					System.out.println("valid=" + db.isValid());
					System.out.println("context=" + getBindingEvaluationContext());
					try {
						System.out.println("value=" + db.getBindingValue(getBindingEvaluationContext()));
					} catch (TypeMismatchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NullReferenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					DataBinding db2 = new DataBinding("data.availableShapeRoles", getWidget().getList().getOwner(), Object.class,
							BindingDefinitionType.GET);
					System.out.println("db2=" + db2);
					System.out.println("valid=" + db2.isValid());
					System.out.println("context=" + getBindingEvaluationContext());
					try {
						System.out.println("value2=" + db2.getBindingValue(getBindingEvaluationContext()));
					} catch (TypeMismatchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NullReferenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					getWidget().getList().forceRevalidate();
					getWidget().getList().clearCacheForBindingEvaluationContext(getBindingEvaluationContext());
				
					try {
						accessedList = getWidget().getList().getBindingValue(getBindingEvaluationContext());
						System.out.println("accessedList=" + accessedList);
					} catch (TypeMismatchException e) {
						e.printStackTrace();
					} catch (NullReferenceException e) {
						// e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					if (accessedList instanceof List) {
						list = (List<I>) accessedList;
					}
				
					System.out.println("J'ai maintenant " + list);
				
				}*/

			}

			else if (getWidget() != null && getWidget().getArray() != null && getWidget().getArray().isValid()) {

				Object accessedArray = null;
				try {
					accessedArray = getWidget().getArray().getBindingValue(getBindingEvaluationContext());
				} catch (TypeMismatchException e1) {
					e1.printStackTrace();
				} catch (NullReferenceException e1) {
					e1.printStackTrace();
				} catch (ReflectiveOperationException e1) {
					e1.printStackTrace();
				}
				try {
					array = (I[]) accessedArray;
				} catch (ClassCastException e) {
					LOGGER.warning("ClassCastException " + e.getMessage());
				}
			}

			else if (getWidget() != null && getWidget().getData() != null && getWidget().getData().isValid()) {
				Type type = getWidget().getData().getAnalyzedType();
				if (type instanceof Class && ((Class<?>) type).isEnum()) {
					array = (I[]) ((Class<?>) type).getEnumConstants();
				}
			}

			if (list == null && array == null && getWidget().getIteratorType() instanceof Class
					&& ((Class<?>) getWidget().getIteratorType()).isEnum()) {
				array = (I[]) ((Class<?>) getWidget().getIteratorType()).getEnumConstants();
			}

			if (list == null && array == null && StringUtils.isNotEmpty(getWidget().getStaticList())) {
				list = new ArrayList<>();
				StringTokenizer st = new StringTokenizer(getWidget().getStaticList(), ",");
				while (st.hasMoreTokens()) {
					list.add((I) st.nextToken());
				}
			}

			fireContentsChanged(this, 0, getSize());

		}

		@Override
		public int getSize() {
			if (list != null) {
				return list.size();
			}
			if (array != null) {
				return array.length;
			}
			return 0;
		}

		@Override
		public I getElementAt(int index) {
			if (list != null && index >= 0 && index < list.size()) {
				return list.get(index);
			}
			if (array != null && index >= 0 && index < array.length) {
				return array[index];
			}
			return null;
		}

		@Override
		public int indexOf(Object cur) {
			for (int i = 0; i < getSize(); i++) {
				if (getElementAt(i).equals(cur)) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + (list != null ? list.size() + " " + list.toString() : "null") + "]";
		}

		protected ArrayList<Object> toArrayList() {
			ArrayList<Object> returned = new ArrayList<>();
			for (int i = 0; i < getSize(); i++) {
				returned.add(getElementAt(i));
			}
			return returned;
		}

		/*
		 * protected boolean equalsToList(List l) { if (l == null) { return
		 * getSize() == 0; } if (getSize() == l.size()) { for (int i = 0; i <
		 * getSize(); i++) { if (!getElementAt(i).equals(l.get(i))) { return
		 * false; } } return true; } else { return false; } }
		 */
	}

	/*
	 * protected boolean listModelRequireChange() { return
	 * getMultipleValueModel().requireChange(); }
	 */

	protected class FIBMultipleValueCellRenderer extends DefaultListCellRenderer {
		private Dimension nullDimesion;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			FIBMultipleValueCellRenderer label;
			try {
				label = (FIBMultipleValueCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			} catch (NullPointerException e) {
				/* Happen sometimes while evaluating
				 * 
				 * Exception java.lang.NullPointerException:
				 * at javax.swing.DefaultListCellRenderer.getListCellRendererComponent(DefaultListCellRenderer.java:120)
				 * at org.openflexo.gina.view.widget.impl.FIBMultipleValueWidgetImpl$FIBMultipleValueCellRenderer.getListCellRendererComponent(FIBMultipleValueWidgetImpl.java:451)
				 * at javax.swing.plaf.basic.BasicComboBoxUI.getDisplaySize(BasicComboBoxUI.java:1366)
				 * at javax.swing.plaf.basic.BasicComboBoxUI.getMinimumSize(BasicComboBoxUI.java:934)
				 * at com.apple.laf.AquaComboBoxUI.getMinimumSize(AquaComboBoxUI.java:568)
				 * at javax.swing.plaf.basic.BasicComboBoxUI.getPreferredSize(BasicComboBoxUI.java:923)
				 * at javax.swing.JComponent.getPreferredSize(JComponent.java:1662)
				 * 
				 */
				label = new FIBMultipleValueCellRenderer();
			}
			if (value != null && nullDimesion == null) {
				nullDimesion = ((JComponent) getListCellRendererComponent(list, null, -1, false, false)).getPreferredSize();
			}
			if (getWidget().getShowText()) {
				if (value != null) {
					String stringRepresentation = getStringRepresentation(value);
					if (stringRepresentation == null || stringRepresentation.length() == 0) {
						stringRepresentation = "<html><i>" + FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("empty_string")
								+ "</i></html>";
					}
					label.setText(stringRepresentation);
				}
				else {
					label.setText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("no_selection"));
				}
				if (FIBMultipleValueWidgetImpl.this.getFont() != null) {
					label.setFont(FIBMultipleValueWidgetImpl.this.getFont());
				}
			}
			else {
				label.setText(null);
			}
			// label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));

			if (getWidget().getShowIcon()) {
				if (getWidget().getIcon() != null && getWidget().getIcon().isValid()) {
					label.setIcon(getIconRepresentation(value));
				}
			}

			// System.out.println("Actual preferred= "+list.getPreferredSize().getWidth());
			// System.out.println("I will prefer "+label.getPreferredSize().getWidth());

			return label;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension preferredSize = super.getPreferredSize();
			if (nullDimesion != null) {
				preferredSize.width = Math.max(preferredSize.width, nullDimesion.width);
				preferredSize.height = Math.max(preferredSize.height, nullDimesion.height);
			}
			return preferredSize;
		}

		@Override
		public void updateUI() {
			nullDimesion = null;
			super.updateUI();
		}
	}

	public FIBMultipleValueCellRenderer getListCellRenderer() {
		if (listCellRenderer == null) {
			listCellRenderer = new FIBMultipleValueCellRenderer();
		}
		return listCellRenderer;
	}

	@Override
	public FIBMultipleValueModel<I> getMultipleValueModel() {
		if (multipleValueModel == null) {
			multipleValueModel = createMultipleValueModel();
		}
		return multipleValueModel;
	}

	@Override
	public void clearMultipleValueModel() {
		multipleValueModel = null;
	}

	protected abstract FIBMultipleValueModel<I> createMultipleValueModel();

	protected abstract void proceedToListModelUpdate();

	/*
	 * protected FIBMultipleValueModel updateListModelWhenRequired() { if
	 * (multipleValueModel == null) { multipleValueModel = new
	 * FIBMultipleValueModel(); } else { FIBMultipleValueModel aNewListModel =
	 * new FIBMultipleValueModel(); if
	 * (!aNewListModel.equals(multipleValueModel) || didLastKnownValuesChange())
	 * { multipleValueModel = aNewListModel; } } return multipleValueModel; }
	 */

	/*
	 * protected FIBMultipleValueModel updateListModel() { multipleValueModel =
	 * null; updateListModelWhenRequired(); return multipleValueModel; }
	 */

	// private ArrayList<Object> lastKnownValues = null;

	/**
	 * Return a flag indicating if last known values declared as ListModel have changed since the last time this method was called.
	 * 
	 * @return
	 */
	/*
	 * protected boolean didLastKnownValuesChange() { boolean returned; if
	 * (multipleValueModel != null) { returned =
	 * !multipleValueModel.equalsToList(lastKnownValues); lastKnownValues =
	 * multipleValueModel.toArrayList(); } else { returned = lastKnownValues !=
	 * null; lastKnownValues = null; } return returned; }
	 */

	/*
	 * protected final FIBListModel rebuildListModel() { return
	 * multipleValueModel = buildListModel(); }
	 * 
	 * protected final FIBListModel buildListModel() { return new
	 * FIBListModel(); }
	 */

	/*
	 * @Override public final void updateDataObject(final Object dataObject) {
	 * if (!SwingUtilities.isEventDispatchThread()) { if
	 * (logger.isLoggable(Level.WARNING)) { logger.warning(
	 * "Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass."
	 * ); } SwingUtilities.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() { updateDataObject(dataObject); } }); return;
	 * } updateListModelWhenRequired(); super.updateDataObject(dataObject); }
	 */

	protected void updateMultipleValues() {
		((FIBMultipleValueModelImpl) getMultipleValueModel()).update();
		// Sylvain: i think this is not necessary
		// TODO: investigate on this
		// clearMultipleValueModel();
		proceedToListModelUpdate();
	}

	/*@Override
	public boolean update() {
	
		((FIBMultipleValueModelImpl) getMultipleValueModel()).update();
		proceedToListModelUpdate();
	
		super.update();
		updateMultipleValues();
		if (listBindingValueChangeListener != null) {
			listBindingValueChangeListener.refreshObserving();
		}
		if (arrayBindingValueChangeListener != null) {
			arrayBindingValueChangeListener.refreshObserving();
		}
	
		return true;
	}*/

	@Override
	protected void performUpdate() {
		updateMultipleValues();
		super.performUpdate();
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		if (getComponent().getLocalize()) {
			FIBMultipleValueModel<?> mvModel = getMultipleValueModel();
			for (int i = 0; i < mvModel.getSize(); i++) {
				/* String s = */getStringRepresentation(mvModel.getElementAt(i));
				// getLocalized(s);
			}
		}
	}

	@Override
	public T getSelected() {
		return selected;
	}

	@Override
	public void setSelected(T selected) {
		T oldSelected = this.selected;
		if (oldSelected != selected) {
			this.selected = selected;
			getPropertyChangeSupport().firePropertyChange(SELECTED, oldSelected, selected);
		}
	}

	@Override
	public int getSelectedIndex() {
		return selectedIndex;
	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		int oldSelectedIndex = this.selectedIndex;
		if (oldSelectedIndex != selectedIndex) {
			this.selectedIndex = selectedIndex;
			getPropertyChangeSupport().firePropertyChange(SELECTED_INDEX, oldSelectedIndex, selectedIndex);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBMultipleValues.STATIC_LIST_KEY) || evt.getPropertyName().equals(FIBMultipleValues.LIST_KEY)
				|| evt.getPropertyName().equals(FIBMultipleValues.ARRAY_KEY)) {
			// view.updateDataObject(view.getDataObject());
			updateMultipleValues();

		}
		super.propertyChange(evt);
	}

}

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

package org.openflexo.gina.model.widget;

import java.awt.Color;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.WilcardTypeImpl;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.widget.FIBTableAction.FIBAddAction;
import org.openflexo.gina.model.widget.FIBTableAction.FIBCustomAction;
import org.openflexo.gina.model.widget.FIBTableAction.FIBRemoveAction;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.ChainedCollection;

@ModelEntity
@ImplementationClass(FIBTable.FIBTableImpl.class)
@XMLElement(xmlTag = "Table")
public interface FIBTable extends FIBWidget {

	public static final String ITERATOR_NAME = "iterator";

	@PropertyIdentifier(type = Class.class)
	public static final String ITERATOR_CLASS_KEY = "iteratorClass";
	@PropertyIdentifier(type = Integer.class)
	public static final String VISIBLE_ROW_COUNT_KEY = "visibleRowCount";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROW_HEIGHT_KEY = "rowHeight";
	@PropertyIdentifier(type = boolean.class)
	public static final String AUTO_SELECT_FIRST_ROW_KEY = "autoSelectFirstRow";
	@PropertyIdentifier(type = boolean.class)
	public static final String CREATE_NEW_ROW_ON_CLICK_KEY = "createNewRowOnClick";
	@PropertyIdentifier(type = boolean.class)
	public static final String BOUND_TO_SELECTION_MANAGER_KEY = "boundToSelectionManager";
	@PropertyIdentifier(type = boolean.class)
	public static final String SHOW_FOOTER_KEY = "showFooter";
	@PropertyIdentifier(type = SelectionMode.class)
	public static final String SELECTION_MODE_KEY = "selectionMode";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String SELECTED_KEY = "selected";
	@PropertyIdentifier(type = List.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = List.class)
	public static final String ACTIONS_KEY = "actions";
	@PropertyIdentifier(type = Color.class)
	public static final String TEXT_SELECTION_COLOR_KEY = "textSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String TEXT_NON_SELECTION_COLOR_KEY = "textNonSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_SELECTION_COLOR_KEY = "backgroundSelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_SECONDARY_SELECTION_COLOR_KEY = "backgroundSecondarySelectionColor";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_NON_SELECTION_COLOR_KEY = "backgroundNonSelectionColor";

	@Getter(value = ITERATOR_CLASS_KEY)
	@XMLAttribute(xmlTag = "iteratorClassName")
	public Class getIteratorClass();

	@Setter(ITERATOR_CLASS_KEY)
	public void setIteratorClass(Class iteratorClass);

	@Getter(value = VISIBLE_ROW_COUNT_KEY)
	@XMLAttribute
	public Integer getVisibleRowCount();

	@Setter(VISIBLE_ROW_COUNT_KEY)
	public void setVisibleRowCount(Integer visibleRowCount);

	@Getter(value = ROW_HEIGHT_KEY)
	@XMLAttribute
	public Integer getRowHeight();

	@Setter(ROW_HEIGHT_KEY)
	public void setRowHeight(Integer rowHeight);

	@Getter(value = AUTO_SELECT_FIRST_ROW_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAutoSelectFirstRow();

	@Setter(AUTO_SELECT_FIRST_ROW_KEY)
	public void setAutoSelectFirstRow(boolean autoSelectFirstRow);

	@Getter(value = CREATE_NEW_ROW_ON_CLICK_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getCreateNewRowOnClick();

	@Setter(CREATE_NEW_ROW_ON_CLICK_KEY)
	public void setCreateNewRowOnClick(boolean createNewRowOnClick);

	@Getter(value = BOUND_TO_SELECTION_MANAGER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getBoundToSelectionManager();

	@Setter(BOUND_TO_SELECTION_MANAGER_KEY)
	public void setBoundToSelectionManager(boolean boundToSelectionManager);

	@Getter(value = SHOW_FOOTER_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getShowFooter();

	@Setter(SHOW_FOOTER_KEY)
	public void setShowFooter(boolean showFooter);

	@Getter(value = SELECTION_MODE_KEY)
	@XMLAttribute
	public SelectionMode getSelectionMode();

	@Setter(SELECTION_MODE_KEY)
	public void setSelectionMode(SelectionMode selectionMode);

	@Getter(value = SELECTED_KEY)
	@XMLAttribute
	public DataBinding<Object> getSelected();

	@Setter(SELECTED_KEY)
	public void setSelected(DataBinding<Object> selected);

	@Getter(value = COLUMNS_KEY, cardinality = Cardinality.LIST, inverse = FIBTableColumn.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBTableColumn> getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(List<FIBTableColumn> columns);

	@Adder(COLUMNS_KEY)
	public void addToColumns(FIBTableColumn aColumn);

	@Remover(COLUMNS_KEY)
	public void removeFromColumns(FIBTableColumn aColumn);

	@Getter(value = ACTIONS_KEY, cardinality = Cardinality.LIST, inverse = FIBTableAction.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBTableAction> getActions();

	@Setter(ACTIONS_KEY)
	public void setActions(List<FIBTableAction> actions);

	@Adder(ACTIONS_KEY)
	public void addToActions(FIBTableAction aAction);

	@Remover(ACTIONS_KEY)
	public void removeFromActions(FIBTableAction aAction);

	@Getter(value = TEXT_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getTextSelectionColor();

	@Setter(TEXT_SELECTION_COLOR_KEY)
	public void setTextSelectionColor(Color textSelectionColor);

	@Getter(value = TEXT_NON_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getTextNonSelectionColor();

	@Setter(TEXT_NON_SELECTION_COLOR_KEY)
	public void setTextNonSelectionColor(Color textNonSelectionColor);

	@Getter(value = BACKGROUND_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundSelectionColor();

	@Setter(BACKGROUND_SELECTION_COLOR_KEY)
	public void setBackgroundSelectionColor(Color backgroundSelectionColor);

	@Getter(value = BACKGROUND_SECONDARY_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundSecondarySelectionColor();

	@Setter(BACKGROUND_SECONDARY_SELECTION_COLOR_KEY)
	public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor);

	@Getter(value = BACKGROUND_NON_SELECTION_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundNonSelectionColor();

	@Setter(BACKGROUND_NON_SELECTION_COLOR_KEY)
	public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor);

	public BindingModel getActionBindingModel();

	@Finder(collection = COLUMNS_KEY, attribute = FIBTableColumn.TITLE_KEY)
	public FIBTableColumn getColumnWithTitle(String title);

	public BindingModel getTableBindingModel();

	@Deprecated
	public FIBAddAction createAddAction();

	@Deprecated
	public FIBRemoveAction createRemoveAction();

	@Deprecated
	public FIBCustomAction createCustomAction();

	@Deprecated
	public FIBTableAction deleteAction(FIBTableAction actionToDelete);

	@Deprecated
	public FIBLabelColumn createLabelColumn();

	@Deprecated
	public FIBTextFieldColumn createTextFieldColumn();

	@Deprecated
	public FIBCheckBoxColumn createCheckBoxColumn();

	@Deprecated
	public FIBDropDownColumn createDropDownColumn();

	@Deprecated
	public FIBNumberColumn createNumberColumn();

	@Deprecated
	public FIBIconColumn createIconColumn();

	@Deprecated
	public FIBCustomColumn createCustomColumn();

	@Deprecated
	public FIBButtonColumn createButtonColumn();

	@Deprecated
	public FIBTableColumn deleteColumn(FIBTableColumn columnToDelete);

	public void moveToTop(FIBTableColumn c);

	public void moveUp(FIBTableColumn c);

	public void moveDown(FIBTableColumn c);

	public void moveToBottom(FIBTableColumn c);

	public void moveToTop(FIBTableAction a);

	public void moveUp(FIBTableAction a);

	public void moveDown(FIBTableAction a);

	public void moveToBottom(FIBTableAction a);

	public Type getInferedIteratorType();

	public static abstract class FIBTableImpl extends FIBWidgetImpl implements FIBTable {

		private static final Logger logger = Logger.getLogger(FIBTable.class.getPackage().getName());

		@Deprecated
		private BindingDefinition SELECTED;

		@Deprecated
		public BindingDefinition getSelectedBindingDefinition() {
			if (SELECTED == null) {
				SELECTED = new BindingDefinition("selected", getIteratorType(), DataBinding.BindingDefinitionType.GET_SET, false);
			}
			return SELECTED;
		}

		private DataBinding<Object> selected;

		private Integer visibleRowCount;
		private Integer rowHeight;
		private boolean createNewRowOnClick = false;
		private boolean autoSelectFirstRow = false;
		private boolean boundToSelectionManager = false;
		private boolean showFooter = true;

		private SelectionMode selectionMode = SelectionMode.MultipleIntervalSelection;

		private Class iteratorClass;

		// private List<FIBTableColumn> columns;
		// private List<FIBTableAction> actions;

		private BindingModel tableBindingModel;
		private BindingModel actionBindingModel;

		private Color textSelectionColor;
		private Color textNonSelectionColor;
		private Color backgroundSelectionColor;
		private Color backgroundSecondarySelectionColor;
		private Color backgroundNonSelectionColor;

		@Override
		protected FIBTableType makeViewType() {
			return new FIBTableType(this);
		}

		@Override
		public String getBaseName() {
			return "Table";
		}

		/*@Override
		public void bindingModelMightChange(BindingModel oldBindingModel) {
			super.bindingModelMightChange(oldBindingModel);
			getTableBindingModel().setBaseBindingModel(getBindingModel());
			getActionBindingModel().setBaseBindingModel(getBindingModel());
			for (FIBTableColumn e : getColumns()) {
				((FIBTableColumnImpl) e).bindingModelMightChange(oldBindingModel);
			}
		}*/

		/*
		 * @Override public FIBTableColumn getColumnWithTitle(String title) {
		 * for (FIBTableColumn c : columns) { if (title.equals(c.getTitle())) {
		 * return c; } } return null; }
		 */

		/*
		 * @Override public List<FIBTableColumn> getColumns() { return columns;
		 * }
		 * 
		 * @Override public void setColumns(List<FIBTableColumn> columns) {
		 * this.columns = columns; }
		 * 
		 * @Override public void addToColumns(FIBTableColumn aColumn) {
		 * aColumn.setOwner(this); columns.add(aColumn);
		 * getPropertyChangeSupport().firePropertyChange(COLUMNS_KEY, null,
		 * columns); }
		 * 
		 * @Override public void removeFromColumns(FIBTableColumn aColumn) {
		 * aColumn.setOwner(null); columns.remove(aColumn);
		 * getPropertyChangeSupport().firePropertyChange(COLUMNS_KEY, null,
		 * columns); }
		 */

		/*
		 * @Override public List<FIBTableAction> getActions() { return actions;
		 * }
		 * 
		 * @Override public void setActions(List<FIBTableAction> actions) {
		 * this.actions = actions; }
		 * 
		 * @Override public void addToActions(FIBTableAction anAction) {
		 * logger.fine("Add to actions " + anAction); anAction.setOwner(this);
		 * actions.add(anAction);
		 * getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null,
		 * actions); }
		 * 
		 * @Override public void removeFromActions(FIBTableAction anAction) {
		 * anAction.setOwner(null); actions.remove(anAction);
		 * getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null,
		 * actions); }
		 */

		/*
		 * @Override public void updateBindingModel() {
		 * super.updateBindingModel(); tableBindingModel = null;
		 * actionBindingModel = null; }
		 */

		@Override
		public BindingModel getTableBindingModel() {
			if (tableBindingModel == null) {
				createTableBindingModel();
			}
			return tableBindingModel;
		}

		private void createTableBindingModel() {
			tableBindingModel = new BindingModel(getBindingModel());

			Type inferedIteratorType = getInferedIteratorType();

			BindingVariable iteratorVariable = new BindingVariable(ITERATOR_NAME,
					inferedIteratorType != null ? inferedIteratorType : getIteratorType());
			iteratorVariable.setCacheable(false);

			tableBindingModel.addToBindingVariables(iteratorVariable);
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
		}

		@Override
		public BindingModel getActionBindingModel() {
			if (actionBindingModel == null) {
				createActionBindingModel();
			}
			return actionBindingModel;
		}

		private void createActionBindingModel() {
			actionBindingModel = new BindingModel(getBindingModel());

			Type inferedIteratorType = getInferedIteratorType();

			BindingVariable selectedVariable = new BindingVariable("selected",
					inferedIteratorType != null ? inferedIteratorType : getIteratorType());
			selectedVariable.setCacheable(false);
			actionBindingModel.addToBindingVariables(selectedVariable);
			// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);

			// logger.info("******** Table: "+getName()+" Add BindingVariable: iterator type="+getIteratorClass());
		}

		/*
		 * @Override public void notifiedBindingModelRecreated() {
		 * super.notifiedBindingModelRecreated(); createTableBindingModel();
		 * createActionBindingModel(); }
		 */

		@Override
		public DataBinding<Object> getSelected() {
			if (selected == null) {
				selected = new DataBinding<>(this, getIteratorType(), DataBinding.BindingDefinitionType.GET_SET);
			}
			return selected;
		}

		@Override
		public void setSelected(DataBinding<Object> selected) {
			if (selected != null) {
				selected.setOwner(this);
				selected.setDeclaredType(getIteratorType());
				selected.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			}
			this.selected = selected;
		}

		@Override
		public void revalidateBindings() {
			super.revalidateBindings();
			if (getData() != null && getData().isValid()) {
				if (tableBindingModel != null) {
					tableBindingModel.getBindingVariableAt(0).setType(getInferedIteratorType());
				}
			}
			for (FIBTableColumn column : getColumns()) {
				column.revalidateBindings();
			}
			if (selected != null) {
				selected.revalidate();
			}
		}

		@Override
		public void finalizeDeserialization() {
			logger.fine("finalizeDeserialization() for FIBTable " + getName());

			/* if (tableBindingModel == null) */createTableBindingModel();
			/* if (actionBindingModel == null) */createActionBindingModel();

			super.finalizeDeserialization();

			for (FIBTableColumn column : getColumns()) {
				column.finalizeTableDeserialization();
			}
			if (selected != null) {
				selected.decode();
			}
		}

		/*
		 * public boolean hasDynamicKeyValueProperty(String name) { if
		 * (name.equals("selected")) return getDataClass() != null; return
		 * false; }
		 * 
		 * private DynamicKeyValueProperty SELECTED_DKVP;
		 * 
		 * public DynamicKeyValueProperty getDynamicKeyValueProperty(String
		 * name) { if (name.equals("selected")) { if (SELECTED_DKVP == null)
		 * SELECTED_DKVP = new DynamicKeyValueProperty("selected", getClass(),
		 * getDataClass()); return SELECTED_DKVP; } return null; }
		 */

		private Type iteratorType;

		public Type getIteratorType() {
			Class<?> iteratorClass = getIteratorClass();
			if (iteratorClass.getTypeParameters().length > 0) {
				if (iteratorType == null) {
					iteratorType = TypeUtils.makeInferedType(iteratorClass);
				}
				return iteratorType;
			}
			return iteratorClass;
		}

		@Override
		public Type getInferedIteratorType() {
			// Attempt to infer iterator type
			if (getData() != null && getData().isSet() && getData().isValid()) {
				Type accessedType = getData().getAnalyzedType();
				if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
					return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
				}
			}
			return null;
		}

		@Override
		public Class getIteratorClass() {
			if (iteratorClass == null) {
				iteratorClass = Object.class;
			}
			return iteratorClass;

		}

		@Override
		public void setIteratorClass(Class iteratorClass) {
			FIBPropertyNotification<Class> notification = requireChange(ITERATOR_CLASS_KEY, iteratorClass);
			if (notification != null) {
				this.iteratorClass = iteratorClass;
				createTableBindingModel();
				createActionBindingModel();
				hasChanged(notification);
			}
		}

		@Override
		public Type getDefaultDataType() {
			Type[] args = new Type[1];
			args[0] = new WilcardTypeImpl(getIteratorClass());
			return new ParameterizedTypeImpl(Collection.class, args);
		}

		/*
		 * @Override public Type getDynamicAccessType() { // Type[] args = new
		 * Type[2]; // args[0] = getDataType(); // args[1] = getIteratorType();
		 * return new ParameterizedTypeImpl(FIBTableWidget.class, new Type[] {
		 * Object.class, getIteratorType() }); }
		 */

		@Override
		public boolean getManageDynamicModel() {
			return true;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			logger.fine("notifyBindingChanged with " + binding);
			super.notifiedBindingChanged(binding);
			if (binding == getData() && getData() != null) {
				if (tableBindingModel != null) {
					tableBindingModel.getBindingVariableAt(0).setType(getInferedIteratorType());
				}
			}
		}

		/*
		 * public String getIteratorClassName() { return iteratorClassName; }
		 * 
		 * public void setIteratorClassName(String iteratorClassName) {
		 * FIBPropertyNotification<String> notification = requireChange(
		 * Parameters.iteratorClassName, iteratorClassName); if (notification !=
		 * null) { this.iteratorClassName = iteratorClassName; iteratorClass =
		 * null; createTableBindingModel(); hasChanged(notification); } }
		 */

		@Override
		public boolean getAutoSelectFirstRow() {
			return autoSelectFirstRow;
		}

		@Override
		public void setAutoSelectFirstRow(boolean autoSelectFirstRow) {
			FIBPropertyNotification<Boolean> notification = requireChange(AUTO_SELECT_FIRST_ROW_KEY, autoSelectFirstRow);
			if (notification != null) {
				this.autoSelectFirstRow = autoSelectFirstRow;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getVisibleRowCount() {
			return visibleRowCount;
		}

		@Override
		public void setVisibleRowCount(Integer visibleRowCount) {
			FIBPropertyNotification<Integer> notification = requireChange(VISIBLE_ROW_COUNT_KEY, visibleRowCount);
			if (notification != null) {
				this.visibleRowCount = visibleRowCount;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getRowHeight() {
			return rowHeight;
		}

		@Override
		public void setRowHeight(Integer rowHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(ROW_HEIGHT_KEY, rowHeight);
			if (notification != null) {
				this.rowHeight = rowHeight;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getCreateNewRowOnClick() {
			return createNewRowOnClick;
		}

		@Override
		public void setCreateNewRowOnClick(boolean createNewRowOnClick) {
			FIBPropertyNotification<Boolean> notification = requireChange(CREATE_NEW_ROW_ON_CLICK_KEY, createNewRowOnClick);
			if (notification != null) {
				this.createNewRowOnClick = createNewRowOnClick;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getShowFooter() {
			return showFooter;
		}

		@Override
		public void setShowFooter(boolean showFooter) {
			FIBPropertyNotification<Boolean> notification = requireChange(SHOW_FOOTER_KEY, showFooter);
			if (notification != null) {
				this.showFooter = showFooter;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getBoundToSelectionManager() {
			return boundToSelectionManager;
		}

		@Override
		public void setBoundToSelectionManager(boolean boundToSelectionManager) {
			FIBPropertyNotification<Boolean> notification = requireChange(BOUND_TO_SELECTION_MANAGER_KEY, boundToSelectionManager);
			if (notification != null) {
				this.boundToSelectionManager = boundToSelectionManager;
				hasChanged(notification);
			}
		}

		@Override
		@Deprecated
		public FIBAddAction createAddAction() {
			FIBAddAction newAction = getModelFactory().newInstance(FIBAddAction.class);
			newAction.setName("add_action");
			addToActions(newAction);
			return newAction;
		}

		@Override
		@Deprecated
		public FIBRemoveAction createRemoveAction() {
			FIBRemoveAction newAction = getModelFactory().newInstance(FIBRemoveAction.class);
			newAction.setName("delete_action");
			addToActions(newAction);
			return newAction;
		}

		@Override
		@Deprecated
		public FIBCustomAction createCustomAction() {
			FIBCustomAction newAction = getModelFactory().newInstance(FIBCustomAction.class);
			newAction.setName("custom_action");
			addToActions(newAction);
			return newAction;
		}

		@Override
		@Deprecated
		public FIBTableAction deleteAction(FIBTableAction actionToDelete) {
			logger.info("Called deleteAction() with " + actionToDelete);
			removeFromActions(actionToDelete);
			return actionToDelete;
		}

		@Override
		public void moveToTop(FIBTableAction c) {
			if (c == null) {
				return;
			}
			getActions().remove(c);
			getActions().add(0, c);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, getActions());
		}

		@Override
		public void moveUp(FIBTableAction a) {
			if (a == null) {
				return;
			}
			int index = getActions().indexOf(a);
			getActions().remove(a);
			getActions().add(index - 1, a);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, getActions());
		}

		@Override
		public void moveDown(FIBTableAction a) {
			if (a == null) {
				return;
			}
			int index = getActions().indexOf(a);
			getActions().remove(a);
			getActions().add(index + 1, a);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, getActions());
		}

		@Override
		public void moveToBottom(FIBTableAction a) {
			if (a == null) {
				return;
			}
			getActions().remove(a);
			getActions().add(a);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, getActions());
		}

		@Override
		@Deprecated
		public FIBLabelColumn createLabelColumn() {
			FIBLabelColumn newColumn = getModelFactory().newInstance(FIBLabelColumn.class);
			newColumn.setName("label");
			newColumn.setTitle("label");
			addToColumns(newColumn);
			return newColumn;
		}

		@Override
		@Deprecated
		public FIBTextFieldColumn createTextFieldColumn() {
			FIBTextFieldColumn newColumn = getModelFactory().newInstance(FIBTextFieldColumn.class);
			newColumn.setName("textfield");
			newColumn.setTitle("textfield");
			addToColumns(newColumn);
			return newColumn;
		}

		@Override
		@Deprecated
		public FIBCheckBoxColumn createCheckBoxColumn() {
			FIBCheckBoxColumn newColumn = getModelFactory().newInstance(FIBCheckBoxColumn.class);
			newColumn.setName("checkbox");
			newColumn.setTitle("checkbox");
			addToColumns(newColumn);
			return newColumn;
		}

		@Deprecated
		@Override
		public FIBDropDownColumn createDropDownColumn() {
			FIBDropDownColumn newColumn = getModelFactory().newInstance(FIBDropDownColumn.class);
			newColumn.setName("dropdown");
			newColumn.setTitle("dropdown");
			addToColumns(newColumn);
			return newColumn;
		}

		@Deprecated
		@Override
		public FIBNumberColumn createNumberColumn() {
			FIBNumberColumn newColumn = getModelFactory().newInstance(FIBNumberColumn.class);
			newColumn.setName("number");
			newColumn.setTitle("number");
			addToColumns(newColumn);
			return newColumn;
		}

		@Deprecated
		@Override
		public FIBIconColumn createIconColumn() {
			FIBIconColumn newColumn = getModelFactory().newInstance(FIBIconColumn.class);
			newColumn.setName("icon");
			newColumn.setTitle("icon");
			addToColumns(newColumn);
			return newColumn;
		}

		@Deprecated
		@Override
		public FIBCustomColumn createCustomColumn() {
			FIBCustomColumn newColumn = getModelFactory().newInstance(FIBCustomColumn.class);
			newColumn.setName("custom");
			newColumn.setTitle("custom");
			addToColumns(newColumn);
			return newColumn;
		}

		@Deprecated
		@Override
		public FIBButtonColumn createButtonColumn() {
			FIBButtonColumn newColumn = getModelFactory().newInstance(FIBButtonColumn.class);
			newColumn.setName("button");
			newColumn.setTitle("button");
			addToColumns(newColumn);
			return newColumn;
		}

		@Deprecated
		@Override
		public FIBTableColumn deleteColumn(FIBTableColumn columnToDelete) {
			logger.info("Called deleteColumn() with " + columnToDelete);
			removeFromColumns(columnToDelete);
			return columnToDelete;
		}

		@Override
		public void moveToTop(FIBTableColumn c) {
			if (c == null) {
				return;
			}
			getColumns().remove(c);
			getColumns().add(0, c);
			getPropertyChangeSupport().firePropertyChange(COLUMNS_KEY, null, getColumns());
		}

		@Override
		public void moveUp(FIBTableColumn c) {
			if (c == null) {
				return;
			}
			int index = getColumns().indexOf(c);
			getColumns().remove(c);
			getColumns().add(index - 1, c);
			getPropertyChangeSupport().firePropertyChange(COLUMNS_KEY, null, getColumns());
		}

		@Override
		public void moveDown(FIBTableColumn c) {
			if (c == null) {
				return;
			}
			int index = getColumns().indexOf(c);
			getColumns().remove(c);
			getColumns().add(index + 1, c);
			getPropertyChangeSupport().firePropertyChange(COLUMNS_KEY, null, getColumns());
		}

		@Override
		public void moveToBottom(FIBTableColumn c) {
			if (c == null) {
				return;
			}
			getColumns().remove(c);
			getColumns().add(c);
			getPropertyChangeSupport().firePropertyChange(COLUMNS_KEY, null, getColumns());
		}

		@Override
		public SelectionMode getSelectionMode() {
			return selectionMode;
		}

		@Override
		public void setSelectionMode(SelectionMode selectionMode) {
			FIBPropertyNotification<SelectionMode> notification = requireChange(SELECTION_MODE_KEY, selectionMode);
			if (notification != null) {
				this.selectionMode = selectionMode;
				hasChanged(notification);
			}
		}

		public static void main(String args[]) throws Exception {
			UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();

			for (UIManager.LookAndFeelInfo info : looks) {
				UIManager.setLookAndFeel(info.getClassName());

				UIDefaults defaults = UIManager.getDefaults();
				Enumeration<?> newKeys = defaults.keys();

				while (newKeys.hasMoreElements()) {
					Object obj = newKeys.nextElement();
					if ((obj instanceof String) && ((String) obj).contains("Table"))
						System.out.printf("%50s : %s\n", obj, UIManager.get(obj));
				}
			}
		}

		@Override
		public Color getTextSelectionColor() {
			if (textSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Table[Enabled+Selected].textForeground");
			}
			return textSelectionColor;
		}

		@Override
		public void setTextSelectionColor(Color textSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(TEXT_SELECTION_COLOR_KEY, textSelectionColor);
			if (notification != null) {
				this.textSelectionColor = textSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getTextNonSelectionColor() {
			if (textNonSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Table.textForeground");
			}
			return textNonSelectionColor;
		}

		@Override
		public void setTextNonSelectionColor(Color textNonSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(TEXT_NON_SELECTION_COLOR_KEY, textNonSelectionColor);
			if (notification != null) {
				this.textNonSelectionColor = textNonSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundSelectionColor() {
			if (backgroundSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Table[Enabled+Selected].textBackground");
			}
			return backgroundSelectionColor;
		}

		@Override
		public void setBackgroundSelectionColor(Color backgroundSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_SELECTION_COLOR_KEY, backgroundSelectionColor);
			if (notification != null) {
				this.backgroundSelectionColor = backgroundSelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundSecondarySelectionColor() {
			if (backgroundSecondarySelectionColor == null) {
				return new Color(178, 215, 255);
			}
			return backgroundSecondarySelectionColor;
		}

		@Override
		public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_SECONDARY_SELECTION_COLOR_KEY,
					backgroundSecondarySelectionColor);
			if (notification != null) {
				this.backgroundSecondarySelectionColor = backgroundSecondarySelectionColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundNonSelectionColor() {
			if (backgroundNonSelectionColor == null) {
				return UIManager.getLookAndFeelDefaults().getColor("Table.background");
			}
			return backgroundNonSelectionColor;
		}

		@Override
		public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_NON_SELECTION_COLOR_KEY, backgroundNonSelectionColor);
			if (notification != null) {
				this.backgroundNonSelectionColor = backgroundNonSelectionColor;
				hasChanged(notification);
			}
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getSelected());
			return returned;
		}

		@Override
		public Collection<? extends FIBModelObject> getEmbeddedObjects() {
			return new ChainedCollection<>(getColumns(), getActions());
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			super.searchLocalized(retriever);
			for (FIBTableColumn column : getColumns()) {
				column.searchLocalized(retriever);
			}
			for (FIBTableAction action : getActions()) {
				action.searchLocalized(retriever);
			}
		}

	}
}

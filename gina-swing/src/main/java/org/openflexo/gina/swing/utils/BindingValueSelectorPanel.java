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

package org.openflexo.gina.swing.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.Typed;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.swing.utils.BindingSelector.EditionMode;
import org.openflexo.gina.swing.utils.table.AbstractModel;
import org.openflexo.gina.swing.utils.table.BindingValueColumn;
import org.openflexo.gina.swing.utils.table.IconColumn;
import org.openflexo.gina.swing.utils.table.StringColumn;
import org.openflexo.gina.swing.utils.table.TabularPanel;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.MouseOverButton;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * This class encodes the panel representing a {@link BindingValue}<br>
 * Such a panel is always used in a context of a {@link BindingSelector} and thus always provides access to its {@link BindingSelector}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class BindingValueSelectorPanel extends AbstractBindingSelectorPanel implements ListSelectionListener {

	static final Logger LOGGER = Logger.getLogger(BindingValueSelectorPanel.class.getPackage().getName());

	private static final String SPECIFY_BASIC_BINDING = "specify_basic_binding";
	private static final String SPECIFY_COMPOUND_BINDING = "specify_complex_binding";

	/**
	 * References the master {@link BindingSelector}
	 */
	final BindingSelector bindingSelector;

	/**
	 * This is the panel where the browser is defined
	 */
	protected JPanel browserPanel;

	/**
	 * Panel where buttons are defined
	 */
	protected ButtonsControlPanel _controlPanel;

	protected JButton connectButton;
	protected JButton cancelButton;
	protected JButton resetButton;
	protected JButton expressionButton;

	protected JButton createsButton;

	private final Map<IBindingPathElement, Hashtable<Type, BindingColumnListModel>> _listModels;

	private final Vector<FilteredJList<?>> _lists;

	protected int defaultVisibleColCount = 3;

	protected final EmptyColumnListModel EMPTY_MODEL = new EmptyColumnListModel();

	private BindingColumnListModel _rootBindingColumnListModel = null;

	JLabel currentTypeLabel;
	private JLabel searchedTypeLabel;
	// private JTextArea bindingValueRepresentation;
	protected BindingColumnElement currentFocused = null;

	protected BindingValueSelectorPanel(BindingSelector bindingSelector) {
		super();
		this.bindingSelector = bindingSelector;
		_listModels = new Hashtable<>();
		_rootBindingColumnListModel = null;
		_lists = new Vector<>();
	}

	@Override
	public void delete() {
		if (_methodCallBindingsModel != null) {
			_methodCallBindingsModel.delete();
			_methodCallBindingsModel = null;
		}
		for (JList<?> list : _lists) {
			list.removeListSelectionListener(this);
			list.setModel(null);
		}
		_lists.clear();
		_listModels.clear();
		_rootBindingColumnListModel = null;
		currentFocused = null;
	}

	public int getIndexOfList(BindingColumnListModel model) {
		for (int i = 0; i < _lists.size(); i++) {
			FilteredJList<?> l = _lists.get(i);
			if (l.getModel() == model) {
				return i;
			}
		}
		return -1;
	}

	public Class<?> getAccessedEntity() {
		Class<?> reply = null;
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			i++;
		}
		if (last != null) {
			return TypeUtils.getBaseClass(last.getElement().getType());
		}
		return reply;
	}

	public BindingVariable getSelectedBindingVariable() {
		if (listAtIndex(0) != null && listAtIndex(0).getSelectedValue() != null) {
			return (BindingVariable) ((BindingColumnElement) listAtIndex(0).getSelectedValue()).getElement();
		}
		else if (listAtIndex(0) != null && listAtIndex(0).getModel().getSize() == 1) {
			return (BindingVariable) listAtIndex(0).getModel().getElementAt(0).getElement();
		}
		else {
			return null;
		}
	}

	@Deprecated
	private static BindingColumnElement findElementMatching(ListModel listModel, String subPartialPath, Vector<Integer> pathElementIndex) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.getElementAt(i) instanceof BindingColumnElement
					&& ((BindingColumnElement) listModel.getElementAt(i)).getLabel().startsWith(subPartialPath)) {
				if (pathElementIndex.size() == 0) {
					pathElementIndex.add(i);
				}
				else {
					pathElementIndex.set(0, i);
				}
				return (BindingColumnElement) listModel.getElementAt(i);
			}
		}
		return null;
	}

	Vector<BindingColumnElement> findElementsMatching(BindingColumnListModel listModel, String subPartialPath) {
		Vector<BindingColumnElement> returned = new Vector<>();
		for (int i = 0; i < listModel.getUnfilteredSize(); i++) {
			if (listModel.getUnfilteredElementAt(i).getLabel().startsWith(subPartialPath)) {
				returned.add(listModel.getUnfilteredElementAt(i));
			}
		}
		return returned;
	}

	BindingColumnElement findElementEquals(ListModel<?> listModel, String subPartialPath) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.getElementAt(i) instanceof BindingColumnElement) {
				if (((BindingColumnElement) listModel.getElementAt(i)).getLabel() != null
						&& ((BindingColumnElement) listModel.getElementAt(i)).getLabel().equals(subPartialPath)) {
					return (BindingColumnElement) listModel.getElementAt(i);
				}
			}
		}
		return null;
	}

	public Type getEndingTypeForSubPath(String pathIgnoringLastPart) {
		StringTokenizer token = new StringTokenizer(pathIgnoringLastPart, ".", false);
		Object obj = null;
		int i = 0;
		while (token.hasMoreTokens()) {
			obj = findElementEquals(listAtIndex(i).getModel(), token.nextToken());
			i++;
		}
		if (obj instanceof BindingColumnElement) {
			Typed element = ((BindingColumnElement) obj).getElement();
			return element.getType();
		}
		return null;
	}

	protected class MethodCallBindingsModel extends AbstractModel<FunctionPathElement, Function.FunctionArgument> {
		public MethodCallBindingsModel(FunctionPathElement functionPathElement) {
			super(functionPathElement);
			addToColumns(new IconColumn<Function.FunctionArgument>("icon", 25) {
				@Override
				public Icon getIcon(Function.FunctionArgument entity) {
					return FIBIconLibrary.METHOD_ICON;
				}
			});
			addToColumns(new StringColumn<Function.FunctionArgument>("name", 100) {
				@Override
				public String getValue(Function.FunctionArgument arg) {
					if (arg != null) {
						return arg.getArgumentName();
					}
					/*
					 * if (paramForValue(bindingValue) != null) return
					 * paramForValue(bindingValue).getName();
					 */
					return "null";
				}
			});
			addToColumns(new StringColumn<Function.FunctionArgument>("type", 100) {
				@Override
				public String getValue(Function.FunctionArgument arg) {
					if (arg != null) {
						return TypeUtils.simpleRepresentation(arg.getArgumentType());
					}
					return "null";
				}
			});
			addToColumns(new BindingValueColumn<Function.FunctionArgument>("value", 250, true) {
				@Override
				public DataBinding getValue(Function.FunctionArgument arg) {
					return getFunctionPathElement().getParameter(arg);
				}

				/**
				 * Called when the value of an argument has changed
				 */
				@Override
				public void setValue(Function.FunctionArgument arg, DataBinding aValue) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Sets value " + arg + " to be " + aValue);
					}

					if (arg != null && getFunctionPathElement() != null) {

						// OK, we first set the parameter value
						getFunctionPathElement().setParameter(arg, aValue);

						// We need to update parsed binding path according to this new value (important if the binding is still not
						// parseable)
						BindingValue bv = (BindingValue) bindingSelector.getEditedObject().getExpression();
						bv.updateParsedBindingPathFromBindingPath();

						// Then, we explicitely force the DataBinding to be reanalyzed (we cannot rely anymore on validity status)
						bindingSelector.getEditedObject().markedAsToBeReanalized();

					}

					// Finally, we notify that DataBinding has changed
					bindingSelector.fireEditedObjectChanged();
				}

				@Override
				public Bindable getBindableFor(DataBinding<?> value, Function.FunctionArgument rowObject) {
					if (value != null) {
						return value.getOwner();
					}
					return null;
				}

				@Override
				public boolean allowsCompoundBinding(DataBinding<?> value) {
					return true;
				}

				@Override
				public boolean allowsNewEntryCreation(DataBinding<?> value) {
					return false;
				}
			});
		}

		/*
		 * DMMethodParameter paramForValue(AbstractBinding bindingValue) { if
		 * ((bindingValue.getBindingDefinition() != null) &&
		 * (bindingValue.getBindingDefinition() instanceof
		 * MethodCall.MethodCallParamBindingDefinition)) { return
		 * ((MethodCall.MethodCallParamBindingDefinition
		 * )bindingValue.getBindingDefinition()).getParam(); } return null; }
		 */

		public FunctionPathElement getFunctionPathElement() {
			return getModel();
		}

		@Override
		public Function.FunctionArgument elementAt(int row) {
			if (row >= 0 && row < getRowCount()) {
				return getFunctionPathElement().getFunction().getArguments().get(row);
			}
			return null;
		}

		@Override
		public int getRowCount() {
			if (getFunctionPathElement() != null) {
				return getFunctionPathElement().getFunction().getArguments().size();
			}
			return 0;
		}

		@Override
		public void setModel(FunctionPathElement model) {
			// logger.info("Setting MethodCallBindingsModel with " + model);
			if (model != null) {
				model.instanciateParameters(bindingSelector.getBindable());
			}
			super.setModel(model);
		}

		@Override
		public BindingEvaluationContext getBindingEvaluationContext() {
			return null;
		}

		public void delete() {
			for (int i = 0; i < getColumnCount(); i++) {
				columnAt(i).delete();
			}
		}
	}

	protected class MethodCallBindingsPanel extends TabularPanel {
		public MethodCallBindingsPanel() {
			super(getMethodCallBindingsModel(), 3);
		}

	}

	private MethodCallBindingsModel _methodCallBindingsModel;

	private MethodCallBindingsPanel _methodCallBindingsPanel;

	public MethodCallBindingsPanel getMethodCallBindingsPanel() {
		if (_methodCallBindingsPanel == null) {
			_methodCallBindingsPanel = new MethodCallBindingsPanel();

		}

		return _methodCallBindingsPanel;
	}

	public MethodCallBindingsModel getMethodCallBindingsModel() {
		if (_methodCallBindingsModel == null) {
			_methodCallBindingsModel = new MethodCallBindingsModel(null);
		}
		return _methodCallBindingsModel;
	}

	@Override
	public Dimension getDefaultSize() {
		int baseHeight;

		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			baseHeight = 300;
		}
		else {
			baseHeight = 180;
		}

		if (bindingSelector.areStaticValuesAllowed() || bindingSelector.areCompoundBindingAllowed()) {
			baseHeight += 30;
		}

		return new Dimension(500, baseHeight);

	}

	@Override
	protected void willApply() {
		if (editStaticValue && staticBindingPanel != null) {
			staticBindingPanel.willApply();
		}
	}

	private MouseOverButton showHideCompoundBindingsButton;

	private ConstantValuePanel staticBindingPanel;

	@Override
	protected void init() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("init() with " + bindingSelector.editionMode + " for " + bindingSelector.getEditedObject());
		}

		setLayout(new BorderLayout());

		browserPanel = new JPanel();
		browserPanel.setLayout(new BoxLayout(browserPanel, BoxLayout.X_AXIS));
		for (int i = 0; i < defaultVisibleColCount; i++) {
			makeNewJList();
		}

		_controlPanel = new ButtonsControlPanel() {
			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(key, component);
			}
		};
		connectButton = _controlPanel.addButton("connect", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bindingSelector.apply();
			}
		});
		cancelButton = _controlPanel.addButton("cancel", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bindingSelector.cancel();
			}
		});
		resetButton = _controlPanel.addButton("reset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bindingSelector.getEditedObject().reset();
				bindingSelector.apply();
			}
		});
		if (bindingSelector.areBindingExpressionsAllowed()) {
			expressionButton = _controlPanel.addButton("expression", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bindingSelector.activateBindingExpressionMode();
				}
			});
		}

		_controlPanel.applyFocusTraversablePolicyTo(_controlPanel, false);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());

		if (bindingSelector.areCompoundBindingAllowed()) {
			showHideCompoundBindingsButton = new MouseOverButton();
			showHideCompoundBindingsButton.setBorder(BorderFactory.createEmptyBorder());
			showHideCompoundBindingsButton.setContentAreaFilled(false);
			showHideCompoundBindingsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
						bindingSelector.activateNormalBindingMode();
					}
					else {
						bindingSelector.activateCompoundBindingMode();
					}
				}
			});

			JLabel showHideCompoundBindingsButtonLabel = new JLabel("", SwingConstants.RIGHT);
			showHideCompoundBindingsButtonLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				showHideCompoundBindingsButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_ICON);
				showHideCompoundBindingsButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
				showHideCompoundBindingsButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(SPECIFY_BASIC_BINDING));
				showHideCompoundBindingsButtonLabel
						.setText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(SPECIFY_BASIC_BINDING) + "  ");
			}
			else {
				showHideCompoundBindingsButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
				showHideCompoundBindingsButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
				showHideCompoundBindingsButton
						.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(SPECIFY_COMPOUND_BINDING));
				showHideCompoundBindingsButtonLabel
						.setText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(SPECIFY_COMPOUND_BINDING) + "  ");
			}

			JPanel showHideCompoundBindingsButtonPanel = new JPanel();
			showHideCompoundBindingsButtonPanel.setLayout(new BorderLayout());
			showHideCompoundBindingsButtonPanel.add(showHideCompoundBindingsButtonLabel, BorderLayout.CENTER);
			showHideCompoundBindingsButtonPanel.add(showHideCompoundBindingsButton, BorderLayout.EAST);

			optionsPanel.add(showHideCompoundBindingsButtonPanel, BorderLayout.EAST);
		}

		if (bindingSelector.areStaticValuesAllowed()) {
			JPanel optionsWestPanel = new JPanel();
			optionsWestPanel.setLayout(new VerticalLayout());
			staticBindingPanel = new ConstantValuePanel(this);
			optionsWestPanel.add(staticBindingPanel);
			optionsPanel.add(optionsWestPanel, BorderLayout.WEST);
		}

		currentTypeLabel = new JLabel(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("no_type"), SwingConstants.LEFT);
		currentTypeLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
		currentTypeLabel.setForeground(Color.GRAY);

		searchedTypeLabel = new JLabel("[" + FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("no_type") + "]", SwingConstants.LEFT);
		searchedTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		searchedTypeLabel.setForeground(Color.RED);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.add(currentTypeLabel, BorderLayout.CENTER);
		labelPanel.add(searchedTypeLabel, BorderLayout.EAST);

		JComponent topPane;

		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			topPane = new JPanel();
			topPane.setLayout(new BorderLayout());
			/*bindingValueRepresentation = new JTextArea(3, 80);
			bindingValueRepresentation.setFont(new Font("SansSerif", Font.PLAIN, 10));
			bindingValueRepresentation.setEditable(false);
			bindingValueRepresentation.setLineWrap(true);*/
			// topPane.add(bindingValueRepresentation, BorderLayout.CENTER);
			topPane.add(labelPanel, BorderLayout.SOUTH);
		}
		else {
			topPane = labelPanel;
		}

		add(topPane, BorderLayout.NORTH);

		JComponent middlePane;

		// logger.info("Rebuild middle pane, with mode="+editionMode);

		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			middlePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(browserPanel,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
					getMethodCallBindingsPanel()); // ICI
			((JSplitPane) middlePane).setDividerLocation(0.5);
			((JSplitPane) middlePane).setResizeWeight(0.5);
		}
		else { // For NORMAL_BINDING and STATIC_BINDING
			middlePane = new JScrollPane(browserPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // ICI
		}

		JPanel middlePaneWithOptions = new JPanel();
		middlePaneWithOptions.setLayout(new BorderLayout());
		middlePaneWithOptions.add(middlePane, BorderLayout.CENTER);
		if (bindingSelector.areStaticValuesAllowed() || bindingSelector.areCompoundBindingAllowed()) {
			middlePaneWithOptions.add(optionsPanel, BorderLayout.SOUTH);
		}

		add(middlePaneWithOptions, BorderLayout.CENTER);
		add(_controlPanel, BorderLayout.SOUTH);

		resetMethodCallPanel();

		// Init static panel
		editStaticValue = true;
		setEditStaticValue(false);

		update();
		FilteredJList<?> firstList = listAtIndex(0);
		if (firstList != null && firstList.getModel().getSize() == 1) {
			firstList.setSelectedIndex(0);
		}

		// disableFocus(this);
	}

	/*private void disableFocus(Component c) {
		c.setFocusable(false);
		if (c instanceof Container) {
			for (Component c2 : ((Container) c).getComponents()) {
				disableFocus(c2);
			}
		}
	}*/

	protected void updateSearchedTypeLabel() {
		searchedTypeLabel.setText("[" + getTypeStringRepresentation() + "]");
	}

	private String getTypeStringRepresentation() {
		if (bindingSelector.getEditedObject() == null || bindingSelector.getEditedObject().getDeclaredType() == null) {
			return FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("no_type");
		}
		else {
			return TypeUtils.simpleRepresentation(bindingSelector.getEditedObject().getDeclaredType());
		}
	}

	protected int getVisibleColsCount() {
		return _lists.size();
	}

	public boolean ensureBindingValueExists() {
		if (bindingSelector.getEditedObject() == null) {
			return false;
		}
		if (bindingSelector.getEditedObject().getExpression() == null) {
			bindingSelector.getEditedObject().setExpression(bindingSelector.makeBinding());
			bindingSelector.fireEditedObjectChanged();
		}
		return true;
	}

	protected class FilteredJList<T> extends JList<T> {
		public FilteredJList() {
			super(new EmptyColumnListModel());
		}

		public String getFilter() {
			return getModel().getFilter();
		}

		public void setFilter(String aFilter) {
			getModel().setFilter(aFilter);
		}

		public boolean isFiltered() {
			return StringUtils.isNotEmpty(getFilter());
		}

		@Override
		public void setModel(ListModel<T> model) {
			if (model != null && !(model instanceof BindingColumnListModel)) {
				new Exception("Oops, this model is " + model).printStackTrace();
			}
			setFilter(null);
			if (model != null) {
				super.setModel(model);
			}
		}

		@Override
		public BindingColumnListModel getModel() {
			if (super.getModel() instanceof BindingColumnListModel) {
				return (BindingColumnListModel) super.getModel();
			}
			new Exception("Oops, got a " + super.getModel()).printStackTrace();
			return null;
		}
	}

	protected JList<String> makeNewJList() {
		FilteredJList<String> newList = new FilteredJList<>();

		TypeResolver typeResolver = new TypeResolver(newList);

		newList.addMouseMotionListener(typeResolver);
		newList.addMouseListener(typeResolver);

		_lists.add(newList);
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("makeNewJList() size = " + _lists.size());
		}
		newList.setPrototypeCellValue("123456789012345"); // ICI
		newList.setSize(new Dimension(100, 150));
		// newList.setPreferredSize(new Dimension(200,150)); // ICI
		// newList.setMinimumSize(new Dimension(200,150)); // ICI
		newList.setCellRenderer(new BindingSelectorCellRenderer());
		newList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		newList.addListSelectionListener(this);
		newList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (bindingSelector.getEditedObject() != null && bindingSelector.getEditedObject().isValid()) {
						bindingSelector.apply();
					}
				}
				else if (e.getClickCount() == 1) {
					// Trying to update MethodCall Panel
					JList<?> list = (JList<?>) e.getSource();
					int index = _lists.indexOf(list);
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Click on index " + index);
					}
					if (index < 0) {
						return;
					}

					if (list.getModel().getSize() == 1) {
						valueSelected(index, list);
					}

					_selectedPathElementIndex = index;
					updateMethodCallPanel();
				}
			}
		});

		newList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// processAnyKeyTyped(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					bindingSelector._selectorPanel.processEnterPressed();
					e.consume();
				}
				else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					bindingSelector._selectorPanel.processBackspace();
					e.consume();
				}
				else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
					bindingSelector._selectorPanel.processDelete();
					e.consume();
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					DataBinding<?> dataBinding = bindingSelector.getEditedObject();
					if (dataBinding.isBindingValue()) {
						int i = _lists.indexOf(e.getSource());
						if (i > -1 && i < _lists.size() && listAtIndex(i + 1) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0).getElement() instanceof BindingPathElement) {
							((BindingValue) dataBinding.getExpression()).setBindingPathElementAtIndex(
									(BindingPathElement) listAtIndex(i + 1).getModel().getElementAt(0).getElement(), i);
							bindingSelector.setEditedObject(dataBinding);
							bindingSelector.fireEditedObjectChanged();
							listAtIndex(i + 1).requestFocusInWindow();
						}
						e.consume();
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					DataBinding<?> dataBinding = bindingSelector.getEditedObject();
					if (dataBinding.isBindingValue()) {
						int i = _lists.indexOf(e.getSource()) - 1;
						if (((BindingValue) dataBinding.getExpression()).getBindingPath().size() > i && i > -1 && i < _lists.size()) {
							((BindingValue) dataBinding.getExpression()).removeBindingPathAt(i);
							// ((BindingValue) dataBinding.getExpression()).disconnect();
							// _bindingSelector.disconnect();
							bindingSelector.setEditedObject(dataBinding);
							bindingSelector.fireEditedObjectChanged();
							listAtIndex(i).requestFocusInWindow();
						}
						e.consume();
					}
				}
			}

		});

		browserPanel.add(
				new JScrollPane(newList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)); // ICI
		newList.setVisibleRowCount(6);
		revalidate();
		repaint();
		return newList;
	}

	int _selectedPathElementIndex = -1;

	protected void resetMethodCallPanel() {
		if (bindingSelector.getEditedObject() == null || bindingSelector.getEditedObject().isConstant()
				|| bindingSelector.getEditedObject().isBindingValue()
						&& ((BindingValue) bindingSelector.getEditedObject().getExpression()).getBindingPath().size() == 0) {
			_selectedPathElementIndex = -1;
		}
		else if (bindingSelector.getEditedObject().isBindingValue()) {
			_selectedPathElementIndex = ((BindingValue) bindingSelector.getEditedObject().getExpression()).getBindingPath().size();
		}
		updateMethodCallPanel();
	}

	void updateMethodCallPanel() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("updateMethodCallPanel with " + bindingSelector.editionMode + " binding=" + bindingSelector.getEditedObject()
					+ " _selectedPathElementIndex=" + _selectedPathElementIndex);
		}
		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING && bindingSelector.getEditedObject().isBindingValue()) {
			if (((BindingValue) bindingSelector.getEditedObject().getExpression()).isCompoundBinding() && _selectedPathElementIndex == -1) {
				_selectedPathElementIndex = ((BindingValue) bindingSelector.getEditedObject().getExpression()).getBindingPathElementCount();
			}
			if (_selectedPathElementIndex >= _lists.size()) {
				_selectedPathElementIndex = -1;
			}
			BindingValue bindingValue = (BindingValue) bindingSelector.getEditedObject().getExpression();
			if (bindingValue == null) {
				_selectedPathElementIndex = -1;
			}
			else if (_selectedPathElementIndex > bindingValue.getBindingPath().size()) {
				_selectedPathElementIndex = -1;
			}
			if (_selectedPathElementIndex > -1 && bindingValue != null) {
				JList<?> list = _lists.get(_selectedPathElementIndex);
				int newSelectedIndex = list.getSelectedIndex();
				if (newSelectedIndex > 0) {
					BindingColumnElement selectedValue = (BindingColumnElement) list.getSelectedValue();
					if (selectedValue.getElement() instanceof FunctionPathElement) {
						BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(_selectedPathElementIndex - 1);
						if (currentElement instanceof FunctionPathElement && ((FunctionPathElement) currentElement).getFunction() != null
								&& ((FunctionPathElement) currentElement).getFunction()
										.equals(((FunctionPathElement) selectedValue.getElement()).getFunction())) {
							getMethodCallBindingsModel().setModel((FunctionPathElement) currentElement);
							return;
						}
					}
				}
			}
			getMethodCallBindingsModel().setModel(null);
			return;
		}
	}

	protected void deleteJList(JList<?> list) {
		_lists.remove(list);
		Component[] scrollPanes = browserPanel.getComponents();
		for (int i = 0; i < scrollPanes.length; i++) {
			if (((Container) scrollPanes[i]).isAncestorOf(list)) {
				browserPanel.remove(scrollPanes[i]);
			}
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("deleteJList() size = " + _lists.size());
		}
		revalidate();
		repaint();
	}

	protected FilteredJList<?> listAtIndex(int index) {
		if (index >= 0 && index < _lists.size()) {
			return _lists.elementAt(index);
		}
		return null;
	}

	// TODO ???
	/*
	 * public void setBindingDefinition(BindingDefinition bindingDefinition) {
	 * if (bindingDefinition != getBindingDefinition()) {
	 * super.setBindingDefinition(bindingDefinition);
	 * staticBindingPanel.updateStaticBindingPanel(); } }
	 */

	@Override
	protected void fireBindableChanged() {
		_rootBindingColumnListModel = buildRootColumnListModel();
		update();
	}

	/*@Override
	protected void fireBindingDefinitionChanged() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fireBindingDefinitionChanged / Setting new binding definition: " + bindingSelector.getBindingDefinition());
		}
	
		update();
	
		if (staticBindingPanel != null) {
			staticBindingPanel.updateConstantValuePanel();
		}
	
	}*/

	private void clearColumns() {
		listAtIndex(0).setModel(getRootColumnListModel());
		int lastUpdatedList = 0;
		// Remove unused lists
		int lastVisibleList = defaultVisibleColCount - 1;
		if (lastUpdatedList > lastVisibleList) {
			lastVisibleList = lastUpdatedList;
		}
		int currentSize = getVisibleColsCount();
		for (int i = lastVisibleList + 1; i < currentSize; i++) {
			JList<?> toRemove = listAtIndex(getVisibleColsCount() - 1);
			deleteJList(toRemove);
		}
		// Sets model to null for visible but unused lists
		for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
			JList<?> list = listAtIndex(i);
			list.setModel(EMPTY_MODEL);
		}
	}

	@Override
	protected void update() {
		DataBinding<?> binding = bindingSelector.getEditedObject();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("update with " + binding);
		}

		// logger.info("Update in BindingValueSelectorPanel with binding " + binding);

		if (binding == null || binding.isConstant() || binding.isUnset()) {
			clearColumns();
			if (binding == null) {
				setEditStaticValue(false);
			}
		}
		else if (binding.isBindingValue()) {
			BindingValue bindingValue = (BindingValue) binding.getExpression();
			listAtIndex(0).setModel(getRootColumnListModel());
			int lastUpdatedList = 0;

			// logger.info("bindingValue.getBindingVariable()="+bindingValue.getBindingVariable());

			if (bindingValue.getBindingVariable() != null) {
				if (bindingValue.getBindingVariable().getType() != null) {
					listAtIndex(1)
							.setModel(getColumnListModel(bindingValue.getBindingVariable(), bindingValue.getBindingVariable().getType()));
				}
				else {
					listAtIndex(1).setModel(EMPTY_MODEL);
				}
				listAtIndex(0).removeListSelectionListener(this);
				BindingColumnElement elementToSelect = listAtIndex(0).getModel().getElementFor(bindingValue.getBindingVariable());

				listAtIndex(0).setSelectedValue(elementToSelect, true);
				listAtIndex(0).addListSelectionListener(this);
				lastUpdatedList = 1;

				for (int i = 0; i < bindingValue.getBindingPath().size(); i++) {
					BindingPathElement pathElement = bindingValue.getBindingPath().get(i);
					if (i + 2 == getVisibleColsCount()) {
						final JList<?> l = makeNewJList();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Rectangle r = SwingUtilities.convertRectangle(l, l.getBounds(), browserPanel);
								// System.out.println("scrollRectToVisible with "+r);
								browserPanel.scrollRectToVisible(r); // ICI
							}
						});
					}

					// Fixed MODULES-306/MODULES-333
					// I think this conditional is not necessary, but i'm not sure not to have missed something
					// Please report any regression
					// if (!(bindingValue.isValid() && bindingValue.isLastBindingPathElement(pathElement) && bindingSelector.isConnected()))
					// {
					Type resultingType = bindingValue.getBindingPath().get(i).getType();
					listAtIndex(i + 2).setModel(getColumnListModel(bindingValue.getBindingPath().get(i), resultingType));
					lastUpdatedList = i + 2;
					// }
					listAtIndex(i + 1).removeListSelectionListener(this);

					BindingColumnElement theElementToSelect = listAtIndex(i + 1).getModel().getElementFor(pathElement);
					listAtIndex(i + 1).setSelectedValue(theElementToSelect, true);

					listAtIndex(i + 1).addListSelectionListener(this);
					if (i < bindingValue.getBindingPath().size() - 1) {
						listAtIndex(i).setFilter(null);
					}
				}
				// logger.info("FIN");
			}

			// Remove and clean unused lists
			cleanLists(lastUpdatedList);

			if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING /*&& bindingValueRepresentation != null*/) {
				// bindingValueRepresentation.setText(bindingSelector.renderedString(binding));
				// bindingValueRepresentation.setForeground(bindingValue.isValid() ? Color.BLACK : Color.RED);
				updateMethodCallPanel();
			}

			// currentTypeLabel.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_type"));
			// currentTypeLabel.setToolTipText(null);

		}

		updateSearchedTypeLabel();

		if (binding != null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Binding " + binding + " isValid()=" + binding.isValid());
			}
			else if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Binding is null");
			}
		}

		updateStatus(binding);
	}

	@Override
	protected void updateStatus(DataBinding<?> binding) {
		// Set connect button state
		connectButton.setEnabled(binding != null && binding.isValid());
		/*if (!binding.isBindingValid()) {
			logger.info("Binding NOT valid: " + binding);
			binding.debugIsBindingValid();
		}*/
		if (binding != null && binding.isValid()) {
			if (ToolBox.isMacOSLaf()) {
				connectButton.setSelected(true);
			}
		}
		if (binding != null) {
			bindingSelector.getTextField().setForeground(binding.isValid() ? Color.BLACK : Color.RED);
			bindingSelector.getTextField().setSelectedTextColor(binding.isValid() ? Color.BLACK : Color.RED);

			if (bindingSelector.areStaticValuesAllowed() && staticBindingPanel != null) {
				staticBindingPanel.updateConstantValuePanel();
			}

			if (binding.isBindingValue()) {
				setEditStaticValue(false);
			}
			else if (binding.isConstant()) {
				setEditStaticValue(true);
			}
		}
	}

	private void cleanLists(int lastUpdatedList) {
		// Remove unused lists
		int lastVisibleList = defaultVisibleColCount - 1;
		if (lastUpdatedList > lastVisibleList) {
			lastVisibleList = lastUpdatedList;
		}
		int currentSize = getVisibleColsCount();
		for (int i = lastVisibleList + 1; i < currentSize; i++) {
			JList<?> toRemove = listAtIndex(getVisibleColsCount() - 1);
			deleteJList(toRemove);
		}
		// Sets model to null for visible but unused lists
		for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
			JList<?> list = listAtIndex(i);
			list.setModel(EMPTY_MODEL);
		}

	}

	private boolean editStaticValue;

	boolean getEditStaticValue() {
		return editStaticValue;
	}

	void setEditStaticValue(boolean aFlag) {
		if (!bindingSelector.areStaticValuesAllowed() || staticBindingPanel == null) {
			return;
		}
		if (editStaticValue != aFlag) {
			editStaticValue = aFlag;
			if (editStaticValue) {
				staticBindingPanel.enableStaticBindingPanel();
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(false);
				}
			}
			else {
				staticBindingPanel.disableStaticBindingPanel();
				// _bindingSelector.setEditedObject(null,false);
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(true);
				}
				// logger.info("bindable="+getBindable()+" bm="+getBindingModel());
				_rootBindingColumnListModel = buildRootColumnListModel();
				// if (listAtIndex(0).getModel() instanceof
				// EmptyColumnListModel) {
				listAtIndex(0).setModel(getRootColumnListModel());
				// }
			}
			staticBindingPanel.updateConstantValuePanel();
		}
	}

	private boolean editTranstypedBinding;

	boolean getEditTranstypedBinding() {
		return editTranstypedBinding;
	}

	protected void updateListModels() {
		_rootBindingColumnListModel = buildRootColumnListModel();

		for (Hashtable<Type, BindingColumnListModel> h : _listModels.values()) {
			for (BindingColumnListModel columnListModel : h.values()) {
				columnListModel.updateListModel();
			}
		}
	}

	protected BindingColumnListModel getRootColumnListModel() {
		if (_rootBindingColumnListModel == null) {
			_rootBindingColumnListModel = buildRootColumnListModel();
		}
		return _rootBindingColumnListModel;
	}

	protected BindingColumnListModel buildRootColumnListModel() {
		if (bindingSelector.getBindingModel() != null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("buildRootColumnListModel() from " + bindingSelector.getBindingModel());
			}
			return new RootBindingColumnListModel(bindingSelector.getBindingModel());
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("buildRootColumnListModel(): EMPTY_MODEL");
		}
		return EMPTY_MODEL;
	}

	// public void refreshColumListModel(DMType type){
	// _listModels.remove(type);
	// getColumnListModel(type);
	// }

	protected BindingColumnListModel getColumnListModel(IBindingPathElement element, Type resultingType) {
		if (element == null) {
			return EMPTY_MODEL;
		}
		if (resultingType == null) {
			return EMPTY_MODEL;
		}
		// if (TypeUtils.isResolved(element.getType())) {
		Hashtable<Type, BindingColumnListModel> h = _listModels.get(element);
		if (h == null) {
			h = new Hashtable<>();
			_listModels.put(element, h);
		}
		BindingColumnListModel returned = h.get(resultingType);
		if (returned == null) {
			returned = makeColumnListModel(element, resultingType);
			h.put(resultingType, returned);
		}
		return returned;
		/*} else {
			return EMPTY_MODEL;
		}*/
	}

	protected BindingColumnListModel makeColumnListModel(IBindingPathElement element, Type resultingType) {
		return new NormalBindingColumnListModel(element, resultingType);
	}

	protected class BindingColumnElement {
		private IBindingPathElement _element;
		private Type _resultingType;

		protected BindingColumnElement(IBindingPathElement element, Type resultingType) {
			_element = element;
			_resultingType = resultingType;
			if (resultingType == null) {
				LOGGER.warning("make BindingColumnElement with null type !");
			}
		}

		private void delete() {
			_element = null;
			_resultingType = null;
		}

		public IBindingPathElement getElement() {
			return _element;
		}

		// TODO: we also need to observe BindingPathElement to track type modifications !!!
		public Type getResultingType() {
			return _resultingType;
		}

		public String getLabel() {
			return getElement().getLabel();
			/*
			 * if (getElement() != null && getElement() instanceof
			 * BindingVariable) { return
			 * ((BindingVariable)getElement()).getVariableName(); } else if
			 * (getElement() != null && getElement() instanceof
			 * KeyValueProperty) { return ((KeyValueProperty)
			 * getElement()).getName(); } else if (getElement() != null &&
			 * getElement() instanceof MethodDefinition) { MethodDefinition
			 * method = (MethodDefinition) getElement(); return
			 * method.getSimplifiedSignature(); }
			 */
			// return "???";
		}

		public String getTypeStringRepresentation() {
			if (getResultingType() == null) {
				return FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("no_type");
			}
			return TypeUtils.simpleRepresentation(getResultingType());
		}

		public String getTooltipText() {
			return getElement().getTooltipText(getResultingType());

			/*
			 * if (getElement() instanceof BindingVariable) { return
			 * getTooltipText((BindingVariable)getElement()); } else if
			 * (getElement() instanceof KeyValueProperty) { return
			 * getTooltipText
			 * ((KeyValueProperty)getElement(),getResultingType()); } else if
			 * (getElement() instanceof MethodDefinition) { return
			 * getTooltipText
			 * ((MethodDefinition)getElement(),getResultingType()); } else
			 * return "???";
			 */
		}

		/*
		 * private String getTooltipText(BindingVariable bv) { String returned =
		 * "<html>"; String resultingTypeAsString; if (bv.getType()!=null) {
		 * resultingTypeAsString = TypeUtils.simpleRepresentation(bv.getType());
		 * resultingTypeAsString = ToolBox.replaceStringByStringInString("<",
		 * "&LT;", resultingTypeAsString); resultingTypeAsString =
		 * ToolBox.replaceStringByStringInString(">", "&GT;",
		 * resultingTypeAsString); } else { resultingTypeAsString = "???"; }
		 * returned +=
		 * "<p><b>"+resultingTypeAsString+" "+bv.getVariableName()+"</b></p>";
		 * //returned +=
		 * "<p><i>"+(bv.getDescription()!=null?bv.getDescription():
		 * FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		 * returned += "</html>"; return returned; }
		 */

		/*
		 * private String getTooltipText(KeyValueProperty property, Type
		 * resultingType) { String returned = "<html>"; String
		 * resultingTypeAsString; if (resultingType!=null) {
		 * resultingTypeAsString =
		 * TypeUtils.simpleRepresentation(resultingType); resultingTypeAsString
		 * = ToolBox.replaceStringByStringInString("<", "&LT;",
		 * resultingTypeAsString); resultingTypeAsString =
		 * ToolBox.replaceStringByStringInString(">", "&GT;",
		 * resultingTypeAsString); } else { resultingTypeAsString = "???"; }
		 * returned +=
		 * "<p><b>"+resultingTypeAsString+" "+property.getName()+"</b></p>";
		 * //returned +=
		 * "<p><i>"+(property.getDescription()!=null?property.getDescription
		 * ():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		 * returned += "</html>"; return returned; }
		 */

		/*
		 * private String getTooltipText(MethodDefinition method, Type
		 * resultingType) { String returned = "<html>"; String
		 * resultingTypeAsString; if (resultingType!=null) {
		 * resultingTypeAsString =
		 * TypeUtils.simpleRepresentation(resultingType); resultingTypeAsString
		 * = ToolBox.replaceStringByStringInString("<", "&LT;",
		 * resultingTypeAsString); resultingTypeAsString =
		 * ToolBox.replaceStringByStringInString(">", "&GT;",
		 * resultingTypeAsString); } else { resultingTypeAsString = "???"; }
		 * returned +=
		 * "<p><b>"+resultingTypeAsString+" "+method.getSimplifiedSignature
		 * ()+"</b></p>"; //returned +=
		 * "<p><i>"+(method.getDescription()!=null?method
		 * .getDescription():FlexoLocalization
		 * .localizedForKey("no_description"))+"</i></p>"; returned +=
		 * "</html>"; return returned; }
		 */

		@Override
		public String toString() {
			return "BindingColumnElement/" + getLabel() + "[" + _element.toString() + "]";
		}

		@Override
		public int hashCode() {
			return (_element == null ? 0 : _element.hashCode()) + (_resultingType == null ? 0 : _resultingType.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BindingColumnElement) {
				BindingColumnElement bce = (BindingColumnElement) obj;
				if (_element == null) {
					return false;
				}
				if (_resultingType == null) {
					return false;
				}
				return _element.equals(bce._element) && _resultingType.equals(bce._resultingType);
			}
			return super.equals(obj);
		}

	}

	abstract class BindingColumnListModel extends AbstractListModel implements PropertyChangeListener {
		public void fireModelChanged() {
			fireContentsChanged(this, 0, getUnfilteredSize() - 1);
		}

		public BindingColumnElement getElementFor(IBindingPathElement element) {
			// logger.info("getElementFor() " + element + " of " + element.getClass());
			/*if (element instanceof MethodCall) {
				element = ((MethodCall) element).getMethodDefinition();
			}*/
			for (int i = 0; i < getSize(); i++) {
				// logger.info("getElementAt(i)=" + getElementAt(i).getElement() + " of " + getElementAt(i).getElement().getClass());
				if (getElementAt(i).getElement().equals(element)) {
					return getElementAt(i);
				}
				if (element instanceof FunctionPathElement && getElementAt(i).getElement() instanceof FunctionPathElement) {
					// Special equals, we try to find a FunctionPathElement even if parameters are different
					FunctionPathElement f1 = (FunctionPathElement) element;
					FunctionPathElement f2 = (FunctionPathElement) getElementAt(i).getElement();
					if (f1.getFunction() != null && f1.getFunction().equals(f2.getFunction())) {
						// We decide here that both FunctionPathElement are equivalent
						return getElementAt(i);
					}

				}
			}
			LOGGER.info("I cannot find " + element + " of " + (element != null ? element.getClass() : null));
			/*for (int i = 0; i < getSize(); i++) {
				logger.info("Looking with " + getElementAt(i).getElement() + " of "
						+ (getElementAt(i).getElement() != null ? getElementAt(i).getElement().getClass() : null));
			}*/

			return null;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// System.out.println("*** propertyChange() called in " + this);
		}

		public void updateListModel() {
			// System.out.println("*** updateListModel() called in " + this);
		}

		public void delete() {

		}

		private String filter = null;

		public String getFilter() {
			return filter;
		}

		public void setFilter(String aFilter) {
			filter = aFilter;
			fireModelChanged();
		}

		@Override
		public int getSize() {
			if (getFilter() == null && !bindingSelector._hideFilteredObjects) {
				return getUnfilteredSize();
			}
			int returned = 0;
			if (!bindingSelector._hideFilteredObjects) {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter)) {
						returned++;
					}
				}
			}
			else if (getFilter() == null) {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (!isFiltered(getUnfilteredElementAt(i))) {
						returned++;
					}
				}
			}
			else {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter) && !isFiltered(getUnfilteredElementAt(i))) {
						returned++;
					}
				}
			}
			return returned;
		}

		@Override
		public BindingColumnElement getElementAt(int index) {
			if (getFilter() == null && !bindingSelector._hideFilteredObjects) {
				return getUnfilteredElementAt(index);
			}
			if (!bindingSelector._hideFilteredObjects) {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter)) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			}
			else if (getFilter() == null) {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (!isFiltered(getUnfilteredElementAt(i))) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			}
			else {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter) && !isFiltered(getUnfilteredElementAt(i))) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			}
			return null;
		}

		private boolean isFiltered(BindingColumnElement columnElement) {
			// Class resultingTypeBaseClass =
			// TypeUtils.getBaseClass(columnElement.getResultingType());
			// Unused Type resultingType =
			columnElement.getResultingType();

			if (columnElement.getElement() != null && columnElement.getElement() instanceof BindingVariable) {
				BindingVariable bv = (BindingVariable) columnElement.getElement();
				if (bv.getType() == null) {
					return true;
				}
			}
			else if (columnElement.getElement() != null) {
				DataBinding<?> binding = bindingSelector.getEditedObject();
				if (binding != null && binding.isBindingValue()) {
					BindingValue bindingValue = (BindingValue) binding.getExpression();
					if (bindingValue.isValid()
							&& bindingValue.isLastBindingPathElement(columnElement.getElement()/*, getIndexOfList(this) - 1*/)
							&& bindingSelector.isConnected()) {
						// setIcon(label, CONNECTED_ICON, list);
					}
					else if (columnElement.getResultingType() != null) {
						if (TypeUtils.isResolved(columnElement.getResultingType()) && bindingSelector.getBindable() != null) {
							// if (columnElement.getElement().getAccessibleBindingPathElements().size() > 0) {
							if (bindingSelector.getBindable().getBindingFactory()
									.getAccessibleSimplePathElements(columnElement.getElement()).size() > 0) {
							}
							else {
								if (!TypeUtils.isTypeAssignableFrom(binding.getDeclaredType(), columnElement.getResultingType(), true)) {
									return true;
								}
								if (binding.isSettable() && !columnElement.getElement().isSettable()) {
									return true;
								}
							}
						}
					}
				}
			} /*
				* else if (columnElement.getElement() != null &&
				* columnElement.getElement() instanceof KeyValueProperty) {
				* KeyValueProperty property = (KeyValueProperty)
				* columnElement.getElement(); AbstractBinding binding =
				* _bindingSelector.getEditedObject(); if
				* ((binding != null) && binding instanceof BindingValue) {
				* BindingValue bindingValue = (BindingValue)binding; if
				* (bindingValue.isConnected() &&
				* (bindingValue.isLastBindingPathElement(property,
				* getIndexOfList(this) - 1))) { //setIcon(label, CONNECTED_ICON,
				* list); } else if (columnElement.getResultingType() != null) { if
				* (TypeUtils.isResolved(columnElement.getResultingType()) &&
				* _bindingSelector.getBindable() != null) { if
				* (_bindingSelector.getBindable
				* ().getBindingFactory().getAccessibleBindingPathElements
				* (columnElement.getElement()).size() > 0) { //if
				* (KeyValueLibrary.getAccessibleProperties(resultingType).size() >
				* 0) { //setIcon(label, ARROW_RIGHT_ICON, list); } else { if
				* ((_bindingSelector.getBindingDefinition() != null) &&
				* (_bindingSelector.getBindingDefinition().getType() != null) &&
				* (!TypeUtils
				* .isTypeAssignableFrom(_bindingSelector
				* .getBindingDefinition
				* ().getType(),columnElement.getResultingType(),true))) { return
				* true; } if ((_bindingSelector.getBindingDefinition() != null) &&
				* (_bindingSelector.getBindingDefinition().getIsSettable()) &&
				* !property.isSettable()) { return true; } } } } } } else if
				* (columnElement.getElement() != null && columnElement.getElement()
				* instanceof MethodDefinition) { MethodDefinition method =
				* (MethodDefinition) columnElement.getElement();
				* 
				* String methodAsString = method.getSimplifiedSignature(); int idx
				* = getIndexOfList(this); if (idx > 0 &&
				* _lists.elementAt(idx-1).getSelectedValue()!=null) { Type context
				* =
				* ((BindingColumnElement)_lists.elementAt(idx-1).getSelectedValue(
				* )).getResultingType(); methodAsString =
				* method.getSimplifiedSignature();
				* //method.getSimplifiedSignatureInContext(context); }
				* 
				* AbstractBinding binding = _bindingSelector.getEditedObject(); if
				* (binding instanceof BindingValue) { BindingValue bindingValue =
				* (BindingValue)binding; BindingPathElement bpe =
				* bindingValue.getBindingPathElementAtIndex(getIndexOfList(this) -
				* 1); if ((bindingValue.isConnected()) &&
				* (bindingValue.isLastBindingPathElement(bpe, getIndexOfList(this)
				* - 1)) && ((bpe instanceof MethodCall) &&
				* (((MethodCall)bpe).getMethod().equals(method.getMethod())))) { }
				* else if (columnElement.getResultingType() != null &&
				* resultingType != null && _bindingSelector.getBindable() != null)
				* { if(_bindingSelector.getBindable().getBindingFactory().
				* getAccessibleBindingPathElements
				* (columnElement.getElement()).size() +
				* _bindingSelector.getBindable
				* ().getBindingFactory().getAccessibleCompoundBindingPathElements
				* (columnElement.getElement()).size() > 0) { //if
				* (KeyValueLibrary.getAccessibleProperties(resultingType).size() //
				* + KeyValueLibrary.getAccessibleMethods(resultingType).size() > 0)
				* { } else { if ((_bindingSelector.getBindingDefinition() != null)
				* && (_bindingSelector.getBindingDefinition().getType() != null &&
				* TypeUtils
				* .getBaseClass(_bindingSelector.getBindingDefinition().getType
				* ())!=null) &&
				* (!TypeUtils.isClassAncestorOf(TypeUtils.getBaseClass
				* (_bindingSelector
				* .getBindingDefinition().getType()),TypeUtils.getBaseClass
				* (resultingType)))) { return true; } } } } }
				*/
			return false;
		}

		public abstract int getUnfilteredSize();

		public abstract BindingColumnElement getUnfilteredElementAt(int index);

	}

	private class NormalBindingColumnListModel extends BindingColumnListModel {
		private final Type _type;
		private final IBindingPathElement _element;
		private final Vector<BindingPathElement> _accessibleProperties;
		private final Vector<BindingPathElement> _accessibleMethods;
		private final Vector<BindingColumnElement> _elements;

		NormalBindingColumnListModel(IBindingPathElement element, Type resultingType) {
			super();
			_element = element;
			_type = resultingType;
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Build NormalBindingColumnListModel for " + element + " base class=" + TypeUtils.getBaseClass(_type));
			}
			_accessibleProperties = new Vector<>();
			_accessibleMethods = new Vector<>();
			_elements = new Vector<>();
			updatePathElements();
			if (element instanceof HasPropertyChangeSupport && ((HasPropertyChangeSupport) element).getPropertyChangeSupport() != null) {
				((HasPropertyChangeSupport) element).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
		}

		@Override
		public void delete() {
			if (_element instanceof HasPropertyChangeSupport && ((HasPropertyChangeSupport) _element).getPropertyChangeSupport() != null) {
				((HasPropertyChangeSupport) _element).getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			super.delete();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(BindingPathElement.BINDING_PATH_CHANGED)) {
				updatePathElements();
			}
		}

		@Override
		public void updateListModel() {
			super.updateListModel();
			updatePathElements();
		}

		private void updatePathElements() {
			// System.out.println("*** updatePathElements() called in " + this);
			_accessibleProperties.clear();
			_accessibleMethods.clear();

			if (bindingSelector.getBindable() == null) {
				return;
			}

			_elements.clear();

			if (TypeUtils.getBaseClass(_type) == null) {
				return;
			}

			Bindable bdable = bindingSelector.getBindable();
			if (bdable != null) {
				BindingFactory bf = bdable.getBindingFactory();
				if (bf != null) {
					_accessibleProperties.addAll(bf.getAccessibleSimplePathElements(_element));

					if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
						_accessibleProperties
								.addAll(bindingSelector.getBindable().getBindingFactory().getAccessibleFunctionPathElements(_element));
					}

					for (BindingPathElement p : _accessibleProperties) {
						_elements.add(new BindingColumnElement(p, TypeUtils.makeInstantiatedType(p.getType(), _type)));
					}
					for (BindingPathElement m : _accessibleMethods) {
						_elements.add(new BindingColumnElement(m, TypeUtils.makeInstantiatedType(m.getType(), _type)));
					}
				}
			}
		}

		@Override
		public int getUnfilteredSize() {
			return _elements.size();
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			if (index < _elements.size() && index >= 0) {
				return _elements.elementAt(index);
			}
			return null;
		}

		/*@Override
		public void update(Observable observable, Object dataModification) {
		}*/

	}

	class EmptyColumnListModel extends BindingColumnListModel {
		@Override
		public int getUnfilteredSize() {
			return 0;
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			return null;
		}

	}

	private class RootBindingColumnListModel extends BindingColumnListModel {
		private final BindingModel _myBindingModel;
		private final Vector<BindingColumnElement> _elements;

		RootBindingColumnListModel(BindingModel bindingModel) {
			super();
			_myBindingModel = bindingModel;
			_elements = new Vector<>();
			bindingModel.getPropertyChangeSupport().addPropertyChangeListener(this);
			updateBindingVariables();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getSource() == _myBindingModel) {
				updateBindingVariables();
			}
			else if (evt.getSource() instanceof BindingVariable) {
				// Unused BindingVariable bv = (BindingVariable)
				evt.getSource();
				// System.out.println("-------> YES, j'ai vu que la variable: " + bv + " a ete modifiee: " + evt);
			}
		}

		@Override
		public void updateListModel() {
			super.updateListModel();
			updateBindingVariables();
		}

		private final List<BindingVariable> observedBindingVariables = new ArrayList<>();

		private void updateBindingVariables() {
			// System.out.println("*** updateBindingVariables() called in " + this);
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("BindingModel is: " + _myBindingModel + " with " + _myBindingModel.getBindingVariablesCount());
			}
			for (BindingVariable bv : observedBindingVariables) {
				if (bv.getPropertyChangeSupport() != null) {
					bv.getPropertyChangeSupport().removePropertyChangeListener(this);
				}
			}
			for (BindingColumnElement e : _elements) {
				e.delete();
			}
			_elements.clear();
			for (int i = 0; i < _myBindingModel.getBindingVariablesCount(); i++) {
				BindingVariable bv = _myBindingModel.getBindingVariableAt(i);
				_elements.add(new BindingColumnElement(bv, bv.getType()));
				if (bv.getPropertyChangeSupport() != null) {
					bv.getPropertyChangeSupport().addPropertyChangeListener(this);
				}
			}
		}

		@Override
		public int getUnfilteredSize() {
			return _elements.size();
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			return _elements.elementAt(index);
		}

		@Override
		public String toString() {
			return "RootBindingColumnListModel with " + getSize() + " elements";
		}

	}

	protected class TypeResolver extends MouseAdapter {

		private final JList<?> list;

		protected TypeResolver(JList<?> aList) {
			currentFocused = null;
			list = aList;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			displayLabel(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			displayLabel(e);
		}

		private void displayLabel(MouseEvent e) {

			// Get item index
			int index = list.locationToIndex(e.getPoint());

			// Get item
			if (index < 0 || index >= list.getModel().getSize()) {
				return;
			}
			BindingColumnElement item = ((BindingColumnListModel) list.getModel()).getElementAt(index);

			if (item != currentFocused) {
				currentFocused = item;
				currentTypeLabel.setText(currentFocused.getTypeStringRepresentation());
			}
		}

	}

	protected class BindingSelectorCellRenderer extends DefaultListCellRenderer {

		private final JPanel panel;
		private final JLabel iconLabel;

		public BindingSelectorCellRenderer() {
			panel = new JPanel(new BorderLayout());
			iconLabel = new JLabel();
			panel.add(iconLabel, BorderLayout.EAST);
			panel.add(this);
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object bce, int index, boolean isSelected, boolean cellHasFocus) {
			JComponent returned = (JComponent) super.getListCellRendererComponent(list, bce, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				label.setToolTipText(null);
				iconLabel.setVisible(false);

				if (bce instanceof BindingColumnElement) {
					BindingColumnElement columnElement = (BindingColumnElement) bce;
					// Class resultingTypeBaseClass =
					// TypeUtils.getBaseClass(columnElement.getResultingType());
					// Unused Type resultingType =
					columnElement.getResultingType();
					label.setText(columnElement.getLabel());
					// if (!(columnElement.getElement() instanceof FinalBindingPathElement)) {
					returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
					// }
					if (columnElement.getElement().getType() != null) {
						returned.setToolTipText(columnElement.getTooltipText());
					}
					else {
						label.setForeground(Color.GRAY);
					}

					DataBinding<?> binding = bindingSelector.getEditedObject();
					if (binding != null && binding.isBindingValue()) {
						BindingValue bindingValue = (BindingValue) binding.getExpression();
						// System.out.println("bindingValue=" + bindingValue + " valid=" + isValid());
						if (bindingValue.isValid()
								&& bindingValue.isLastBindingPathElement(columnElement.getElement()/*, _lists.indexOf(list) - 1*/)
								&& bindingSelector.isConnected()) {
							// System.out.println("connecte");
							returned = getIconLabelComponent(label, FIBIconLibrary.CONNECTED_ICON);
						}
						else if (columnElement.getResultingType() != null) {
							if (TypeUtils.isResolved(columnElement.getResultingType()) && bindingSelector.getBindable() != null) {
								// if (columnElement.getElement().getAccessibleBindingPathElements().size() > 0) {
								if (bindingSelector.getBindable().getBindingFactory() != null
										&& bindingSelector.getBindable().getBindingFactory()
												.getAccessibleSimplePathElements(columnElement.getElement()) != null
										&& bindingSelector.getBindable().getBindingFactory()
												.getAccessibleSimplePathElements(columnElement.getElement()).size() > 0) {
									returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
								}
								else {
									if (!TypeUtils.isTypeAssignableFrom(binding.getDeclaredType(), columnElement.getResultingType(),
											true)) {
										label.setForeground(Color.GRAY);
									}
									if (binding.isSettable() && !columnElement.getElement().isSettable()) {
										label.setForeground(Color.GRAY);
									}
								}
							}
						}
					}

				}
				else {
					// Happen because of prototype value !
					// logger.warning("Unexpected type: "+bce+" of "+(bce!=null?bce.getClass():"null"));
				}

			}
			return returned;
		}

		private JComponent getIconLabelComponent(JLabel label, Icon icon) {
			iconLabel.setVisible(true);
			iconLabel.setIcon(icon);
			iconLabel.setOpaque(label.isOpaque());
			iconLabel.setBackground(label.getBackground());
			panel.setToolTipText(label.getToolTipText());
			if (label.getParent() != panel) {
				panel.add(label);
			}
			return panel;
		}
	}

	protected JPanel getControlPanel() {
		return _controlPanel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting()) {
			return;
		}

		DataBinding<?> dataBinding = bindingSelector.getEditedObject();

		if (dataBinding == null) {
			LOGGER.warning("dataBinding should not be null");
			return;
		}

		if (dataBinding.getExpression() == null) {
			// if (bindingSelector.getBindingDefinition() != null && bindingSelector.getBindable() != null) {
			BindingValue newBindingValue = new BindingValue();
			newBindingValue.setBindingVariable(getSelectedBindingVariable());
			newBindingValue.setDataBinding(dataBinding);
			// System.out.println("getSelectedBindingVariable()=" + getSelectedBindingVariable());
			dataBinding.setExpression(newBindingValue /*bindingSelector.makeBinding()*/);
			// bindingValue.setBindingVariable(getSelectedBindingVariable());
			// setEditedObject(bindingValue);
			// bindingSelector.fireEditedObjectChanged();
			/*} else {
				return;
			}*/
		}

		JList<?> list = (JList<?>) e.getSource();
		int index = _lists.indexOf(list);
		_selectedPathElementIndex = index;
		if (index < 0) {
			return;
		}
		int newSelectedIndex = list.getSelectedIndex();

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("I select something from list at index " + index + " selected=" + newSelectedIndex);
		}

		if (newSelectedIndex < 0) {
			return;
		}

		// This call will perform BV edition
		valueSelected(index, list);

		list.removeListSelectionListener(this);
		list.setSelectedIndex(newSelectedIndex);
		list.addListSelectionListener(this);

	}

	private static boolean hasBindingPathForm(String textValue) {
		if (textValue.length() == 0) {
			return false;
		}

		boolean startingPathItem = true;
		for (int i = 0; i < textValue.length(); i++) {
			char c = textValue.charAt(i);
			if (c == '.') {
				startingPathItem = true;
			}
			else {
				boolean isNormalChar = c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' && !startingPathItem;
				if (!isNormalChar) {
					return false;
				}
				startingPathItem = false;
			}
		}
		return true;
	}

	@Override
	protected void synchronizePanelWithTextFieldValue(String textValue) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Request synchronizePanelWithTextFieldValue " + textValue);
		}

		try {
			bindingSelector.isUpdatingModel = true;

			if (!bindingSelector.popupIsShown() && textValue != null
					&& !bindingSelector.isAcceptableAsBeginningOfStaticBindingValue(textValue)) {
				boolean requestFocus = bindingSelector.getTextField().hasFocus();
				bindingSelector.openPopup();
				if (requestFocus) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							bindingSelector.getTextField().requestFocusInWindow();
						}
					});
				}
			}

			if (bindingSelector.getTextField().hasFocus()) {
				if (bindingSelector.getEditedObject() != null && bindingSelector.getEditedObject().isBindingValue()) {
					// ((BindingValue) _bindingSelector.getEditedObject()).disconnect();
				}
				if (bindingSelector._selectorPanel != null) {
					filterWithCurrentInput(textValue);
				}
			}

			if (textValue == null || !textValue.equals(bindingSelector.renderedString(bindingSelector.getEditedObject()))) {
				bindingSelector.getTextField().setForeground(Color.RED);
			}
			else {
				bindingSelector.getTextField().setForeground(Color.BLACK);
			}

		} finally {
			bindingSelector.isUpdatingModel = false;
		}

	}

	private void filterWithCurrentInput(String textValue) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Try to filter for current input " + textValue);
		}

		if (!hasBindingPathForm(textValue)) {
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(textValue, ".", false);
		boolean isCurrentlyValid = true;
		int listIndex = 0;
		String element = null;
		while (isCurrentlyValid && tokenizer.hasMoreTokens()) {
			element = tokenizer.nextToken();
			BindingColumnElement col_element = findElementEquals(_lists.get(listIndex).getModel(), element);
			if (col_element == null) {
				isCurrentlyValid = false;
			}
			else {
				bindingSelector.setUpdatingModel(true);
				if (!ensureBindingValueExists()) {
					bindingSelector.setUpdatingModel(false);
					return;
				}

				/*
				 * if (listIndex == 0) {
				 * logger.info("Je selectionne "+col_element
				 * +" pour la premiere colonne"); boolean found = false; for
				 * (int i=0; i<_lists.get(listIndex).getModel().getSize(); i++)
				 * { Object o =
				 * _lists.get(listIndex).getModel().getElementAt(i); if
				 * (o.equals(col_element)) { found = true;
				 * logger.info("OK, je l'ai trouve"); } else {
				 * logger.info("Pas pareil: "+o); } } if (!found)
				 * logger.info("Je le trouve pas"); }
				 */

				_lists.get(listIndex).removeListSelectionListener(this);
				_lists.get(listIndex).setFilter(null);
				_lists.get(listIndex).setSelectedValue(col_element, true);
				_lists.get(listIndex).addListSelectionListener(this);
				valueSelected(listIndex, _lists.get(listIndex));
				bindingSelector.setUpdatingModel(false);
				listIndex++;
			}
		}

		if (!isCurrentlyValid) {
			_lists.get(listIndex).setFilter(element);
			completionInfo = new CompletionInfo(_lists.get(listIndex), element, textValue);
			if (completionInfo.matchingElements.size() > 0) {
				BindingColumnElement col_element = completionInfo.matchingElements.firstElement();
				_lists.get(listIndex).removeListSelectionListener(this);
				_lists.get(listIndex).setSelectedValue(col_element, true);
				_lists.get(listIndex).addListSelectionListener(this);
			}

		}

		cleanLists(listIndex);
	}

	private CompletionInfo completionInfo;

	protected class CompletionInfo {
		String validPath = null;
		String completionInitPath = null;
		String commonBeginningPath = null;
		Vector<BindingColumnElement> matchingElements = null;

		protected CompletionInfo(FilteredJList<?> list, String subPartialPath, String fullPath) {
			validPath = fullPath.substring(0, fullPath.lastIndexOf(".") + 1);
			completionInitPath = subPartialPath;
			matchingElements = findElementsMatching(list.getModel(), subPartialPath);
			if (matchingElements.size() == 1) {
				commonBeginningPath = matchingElements.firstElement().getLabel();
			}
			else if (matchingElements.size() > 1) {
				int endCommonPathIndex = 0;
				boolean foundDiff = false;
				while (!foundDiff) {
					if (endCommonPathIndex < matchingElements.firstElement().getLabel().length()) {
						char c = matchingElements.firstElement().getLabel().charAt(endCommonPathIndex);
						for (int i = 1; i < matchingElements.size(); i++) {
							String label = matchingElements.elementAt(i).getLabel();
							if (endCommonPathIndex < label.length() && label.charAt(endCommonPathIndex) != c) {
								foundDiff = true;
							}
						}
						if (!foundDiff) {
							endCommonPathIndex++;
						}
					}
					else {
						foundDiff = true;
					}
				}
				commonBeginningPath = matchingElements.firstElement().getLabel().substring(0, endCommonPathIndex);
			}
		}

		@Override
		public String toString() {
			return "CompletionInfo, completionInitPath=" + completionInitPath + " validPath=" + validPath + " commonBeginningPath="
					+ commonBeginningPath + " matchingElements=" + matchingElements;
		}

		private boolean alreadyAutocompleted = false;

		protected void autoComplete() {
			if (!alreadyAutocompleted) {
				bindingSelector.getTextField().setText(validPath + commonBeginningPath);
			}
			else {
				bindingSelector.getTextField().setText(validPath + commonBeginningPath + ".");
			}
			alreadyAutocompleted = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					bindingSelector.getTextField().requestFocusInWindow();
				}
			});
		}

	}

	@Override
	protected void processEnterPressed() {
		LOGGER.fine("Pressed on ENTER");

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList<?> list = listAtIndex(index);
		if (list != null) {
			int currentSelected = list.getSelectedIndex();
			if (currentSelected > -1) {
				valueChanged(new ListSelectionEvent(list, currentSelected, currentSelected, false));
				// list.setSelectedIndex(currentSelected);
				update();
				completionInfo = null;
			}
		}

		// System.out.println("bindingSelector.getEditedObject()=" + bindingSelector.getEditedObject());
		// System.out.println("valid=" + bindingSelector.getEditedObject().isValid());

		if (bindingSelector.getEditedObject() != null && bindingSelector.getEditedObject().isValid()) {
			bindingSelector.apply();
		}
	}

	@Override
	protected void processDelete() {
		LOGGER.fine("Pressed on DELETE");
		suppressSelection();
	}

	@Override
	protected void processBackspace() {
		LOGGER.fine("Pressed on BACKSPACE");
		if (!suppressSelection()) {
			if (bindingSelector.getTextField().getText().length() > 0) {
				bindingSelector.getTextField().setText(
						bindingSelector.getTextField().getText().substring(0, bindingSelector.getTextField().getText().length() - 1));

			}
		}
	}

	private boolean suppressSelection() {
		if (bindingSelector.getTextField().getText().length() > 0) {
			if (bindingSelector.getTextField().getSelectedText() != null && bindingSelector.getTextField().getSelectedText().length() > 0) {
				int begin = bindingSelector.getTextField().getSelectionStart();
				int end = bindingSelector.getTextField().getSelectionEnd();
				bindingSelector.getTextField().setText(bindingSelector.getTextField().getText().substring(0, begin)
						+ bindingSelector.getTextField().getText().substring(end, bindingSelector.getTextField().getText().length()));
				return true;
			}
		}
		return false;
	}

	@Override
	protected void processTabPressed() {
		LOGGER.fine("Pressed on TAB, completionInfo=" + completionInfo);
		if (completionInfo != null) {
			// System.out.println("Autocomplete !!!");
			completionInfo.autoComplete();
		}
	}

	@Override
	protected void processUpPressed() {
		LOGGER.fine("Pressed on UP");

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList<?> list = listAtIndex(index);

		int currentSelected = list.getSelectedIndex();
		if (currentSelected > 0) {
			list.removeListSelectionListener(this);
			list.setSelectedIndex(currentSelected - 1);
			list.addListSelectionListener(this);
		}
	}

	@Override
	protected void processDownPressed() {
		LOGGER.fine("Pressed on DOWN");
		if (!bindingSelector.popupIsShown()) {
			bindingSelector.openPopup();
		}

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList<?> list = listAtIndex(index);

		int currentSelected = list.getSelectedIndex();
		list.removeListSelectionListener(this);
		list.setSelectedIndex(currentSelected + 1);
		list.addListSelectionListener(this);

	}

	/*@Override
	protected void processLeftPressed() {
		logger.fine("Pressed on LEFT");
	}*/

	/*@Override
	protected void processRightPressed() {
		logger.fine("Pressed on RIGHT");
	
		if (!bindingSelector.popupIsShown()) {
			bindingSelector.openPopup();
		}
	
		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}
	
		FilteredJList list = listAtIndex(index);
	
		int currentSelected = list.getSelectedIndex();
		if (currentSelected > -1 && list.isFiltered()) {
			valueChanged(new ListSelectionEvent(list, currentSelected, currentSelected, false));
			update();
			completionInfo = null;
		} else if (completionInfo != null) {
			completionInfo.autoComplete();
		} else {
			list.requestFocusInWindow();
		}
	}*/

	boolean isKeyPathFromTextASubKeyPath(String inputText) {
		int dotCount = StringUtils.countMatches(inputText, ".");
		if (listAtIndex(dotCount) == null) {
			return false;
		}
		BindingColumnListModel listModel = listAtIndex(dotCount).getModel();
		String subPartialPath = inputText.substring(inputText.lastIndexOf(".") + 1);
		Vector<Integer> pathElementIndex = new Vector<>();
		;
		BindingColumnElement pathElement = findElementMatching(listModel, subPartialPath, pathElementIndex);
		return pathElement != null;
	}

	boolean isKeyPathFromPanelValid() {
		if (bindingSelector.getEditedObject() == null) {
			return false;
		}
		int i = 0;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			i++;
		}
		if (listAtIndex(i - 1).getSelectedValue() instanceof BindingColumnElement) {
			if (TypeUtils.isTypeAssignableFrom(bindingSelector.getEditedObject().getDeclaredType(),
					((BindingColumnElement) listAtIndex(i - 1).getSelectedValue()).getResultingType(), true)) {
				return true;
			}
		}
		return false;
	}

	BindingValue makeBindingValueFromPanel() {
		if (bindingSelector.getEditedObject() == null || !bindingSelector.getEditedObject().isBindingValue()) {
			return null;
		}
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			// System.out.println("Selecting " + last.getElement());

			if (last.getElement() instanceof FunctionPathElement) {
				for (FunctionArgument arg : ((FunctionPathElement) last.getElement()).getArguments()) {
					DataBinding<?> argValue = ((FunctionPathElement) last.getElement()).getParameter(arg);
					if (argValue == null) {
						if (TypeUtils.isNumber(arg.getArgumentType())) {
							argValue = new DataBinding<>("0", bindingSelector.getBindable(), arg.getArgumentType(),
									BindingDefinitionType.GET);
						}
						else {
							argValue = new DataBinding<>("null", bindingSelector.getBindable(), arg.getArgumentType(),
									BindingDefinitionType.GET);
						}
						((FunctionPathElement) last.getElement()).setParameter(arg, argValue);
					}
					// System.out.println("> ARG " + arg + " = " + ((FunctionPathElement) last.getElement()).getParameter(arg));
				}
			}

			((BindingValue) bindingSelector.getEditedObject().getExpression())
					.setBindingPathElementAtIndex((BindingPathElement) last.getElement(), i - 1);
			i++;
		}
		if (last != null) {
			((BindingValue) bindingSelector.getEditedObject().getExpression())
					.removeBindingPathElementAfter((BindingPathElement) last.getElement());
		}
		return (BindingValue) bindingSelector.getEditedObject().getExpression();
	}

	protected void valueSelected(int index, JList<?> list) {

		DataBinding<?> binding = bindingSelector.getEditedObject();

		// Unused boolean bindingRecreated = false;

		if (!binding.isBindingValue()) {

			bindingSelector.editionMode = EditionMode.NORMAL_BINDING;
			binding.setExpression(bindingSelector.makeBinding()); // Should create a BindingValue instance !!!
			// Unused bindingRecreated = true;
			if (!binding.isBindingValue()) {
				LOGGER.severe("Should never happen: valueSelected() called for a non-BindingValue instance !");
				return;
			}
		}
		BindingValue bindingValue = (BindingValue) binding.getExpression();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Value selected: index=" + index + " list=" + list + " bindingValue=" + bindingValue);
		}
		BindingValueSelectorPanel.BindingColumnElement selectedValue = (BindingValueSelectorPanel.BindingColumnElement) list
				.getSelectedValue();

		// System.out.println("element: " + selectedValue.getElement() + " of " + selectedValue.getElement().getClass());
		// System.out.println("editionMode=" + bindingSelector.editionMode);

		if (selectedValue == null) {
			return;
		}
		if (index == 0 && selectedValue.getElement() instanceof BindingVariable) { // ICI
			if (list.getSelectedValue() != bindingValue.getBindingVariable()) {
				bindingSelector.disconnect();
				bindingValue.setBindingVariable((BindingVariable) selectedValue.getElement());
				binding.setExpression(bindingValue);
				bindingSelector.fireEditedObjectChanged();
			}
		}
		else {
			if (selectedValue.getElement() instanceof SimplePathElement) {
				// FIXED invalid type object comparison
				if (selectedValue.getElement() != bindingValue.getBindingPathElementAtIndex(index - 1)) {
					bindingSelector.disconnect();
					bindingValue.setBindingPathElementAtIndex((BindingPathElement) selectedValue.getElement(), index - 1);
					binding.setExpression(bindingValue);
					bindingSelector.fireEditedObjectChanged();
				}
			}
			else if (selectedValue.getElement() instanceof FunctionPathElement) {

				BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(index - 1);

				LOGGER.info("Selecting currentElement " + currentElement + " selectedValue=" + selectedValue);

				if (currentElement == null || !(currentElement instanceof FunctionPathElement)
						|| ((FunctionPathElement) currentElement).getFunction() == null || !((FunctionPathElement) currentElement)
								.getFunction().equals(((FunctionPathElement) selectedValue.getElement()).getFunction())) {
					bindingSelector.disconnect();
					Function function = ((FunctionPathElement) selectedValue.getElement()).getFunction();
					LOGGER.info("Selecting function " + function);
					List<DataBinding<?>> args = new ArrayList<>();
					for (FunctionArgument arg : function.getArguments()) {
						if (TypeUtils.isNumber(arg.getArgumentType())) {
							args.add(new DataBinding<>("0", bindingSelector.getBindable(), arg.getArgumentType(),
									BindingDefinitionType.GET));
						}
						else {
							args.add(new DataBinding<>("null", bindingSelector.getBindable(), arg.getArgumentType(),
									BindingDefinitionType.GET));
						}
					}
					FunctionPathElement newFunctionPathElement = bindingSelector.getBindable().getBindingFactory().makeFunctionPathElement(
							currentElement != null ? currentElement.getParent() : bindingValue.getLastBindingPathElement(), function, args);

					if (newFunctionPathElement != null) {
						// TODO: we need to handle here generic FunctionPathElement and not only JavaMethodPathElement
						/*JavaMethodPathElement newMethodCall = new JavaMethodPathElement(bindingValue.getLastBindingPathElement(),
								(MethodDefinition) ((FunctionPathElement) selectedValue.getElement()).getFunction(),
								new ArrayList<DataBinding<?>>());*/
						bindingValue.setBindingPathElementAtIndex(newFunctionPathElement, index - 1);
						binding.setExpression(bindingValue);
						bindingSelector.fireEditedObjectChanged();

					}
					else {
						LOGGER.warning("Cannot retrieve new FunctionPathElement for " + bindingValue.getLastBindingPathElement()
								+ " function=" + function);
					}
				}
				// }
			}
		}
	}
}

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

package org.openflexo.gina.model;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeNode;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.manager.HasBaseIdentifier;
import org.openflexo.gina.model.container.BorderLayoutConstraints;
import org.openflexo.gina.model.container.BoxLayoutConstraints;
import org.openflexo.gina.model.container.ButtonLayoutConstraints;
import org.openflexo.gina.model.container.ComponentConstraints;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.FIBTabPanel;
import org.openflexo.gina.model.container.FlowLayoutConstraints;
import org.openflexo.gina.model.container.GridBagLayoutConstraints;
import org.openflexo.gina.model.container.GridLayoutConstraints;
import org.openflexo.gina.model.container.NoneLayoutConstraints;
import org.openflexo.gina.model.container.SplitLayoutConstraints;
import org.openflexo.gina.model.container.TwoColsLayoutConstraints;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBColor;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBDropDown;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.model.widget.FIBFile;
import org.openflexo.gina.model.widget.FIBFont;
import org.openflexo.gina.model.widget.FIBHtmlEditor;
import org.openflexo.gina.model.widget.FIBImage;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBRadioButtonList;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.model.widget.FIBTable;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.DeserializationInitializer;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.model.validation.ValidationWarning;
import org.openflexo.rm.Resource;
import org.openflexo.toolbox.StringUtils;

/**
 * Represent a component in GINA model<br>
 * This is the base interface for any piece of graphical user interface which might be composed.<br>
 * 
 * They are mainly two kinds of components:
 * <ul>
 * <li>the containers, which contains other components (containers or widgets), with some layout, see {@link FIBContainer}</li>
 * <li>the widgets, that are atomic piece of GUI, and representing a particular data, see {@link FIBWidget}</li>
 * </ul>
 * 
 * The {@link FIBComponent} interface provides:
 * <ul>
 * <li>support for FIBVariable API</li>
 * <li>support for visibility</li>
 * <li>support for foreground and background colors</li>
 * <li>support for prefered, min and max sizes</li>
 * <li>support for scrollbar</li>
 * </ul>
 * 
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FIBComponent.FIBComponentImpl.class)
@Imports({ @Import(FIBPanel.class), @Import(FIBTab.class), @Import(FIBSplitPanel.class), @Import(FIBTabPanel.class),
		@Import(FIBBrowser.class), @Import(FIBButton.class), @Import(FIBCheckBox.class), @Import(FIBColor.class), @Import(FIBCustom.class),
		@Import(FIBFile.class), @Import(FIBFont.class), @Import(FIBHtmlEditor.class), @Import(FIBImage.class), @Import(FIBLabel.class),
		@Import(FIBCheckboxList.class), @Import(FIBDropDown.class), @Import(FIBList.class), @Import(FIBRadioButtonList.class),
		@Import(FIBNumber.class), @Import(FIBReferencedComponent.class), @Import(FIBTable.class), @Import(FIBEditor.class),
		@Import(FIBTextArea.class), @Import(FIBTextField.class) })
public abstract interface FIBComponent extends FIBModelObject, TreeNode, HasBaseIdentifier {

	public static enum VerticalScrollBarPolicy {
		VERTICAL_SCROLLBAR_AS_NEEDED {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
			}
		},
		VERTICAL_SCROLLBAR_NEVER {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
			}
		},
		VERTICAL_SCROLLBAR_ALWAYS {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
			}
		};
		public abstract int getPolicy();
	}

	public static enum HorizontalScrollBarPolicy {
		HORIZONTAL_SCROLLBAR_AS_NEEDED {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
			}
		},
		HORIZONTAL_SCROLLBAR_NEVER {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
			}
		},
		HORIZONTAL_SCROLLBAR_ALWAYS {
			@Override
			public int getPolicy() {
				return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
			}
		};
		public abstract int getPolicy();
	}

	@PropertyIdentifier(type = FIBContainer.class)
	public static final String PARENT_KEY = "parent";
	@PropertyIdentifier(type = Integer.class)
	public static final String INDEX_KEY = "index";
	@PropertyIdentifier(type = Class.class)
	public static final String CONTROLLER_CLASS_KEY = "controllerClass";
	@PropertyIdentifier(type = ComponentConstraints.class)
	public static final String CONSTRAINTS_KEY = "constraints";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VISIBLE_KEY = "visible";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = Boolean.class)
	public static final String OPAQUE_KEY = "opaque";
	@PropertyIdentifier(type = Color.class)
	public static final String BACKGROUND_COLOR_KEY = "backgroundColor";
	@PropertyIdentifier(type = Color.class)
	public static final String FOREGROUND_COLOR_KEY = "foregroundColor";
	@PropertyIdentifier(type = Integer.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Integer.class)
	public static final String HEIGHT_KEY = "height";
	@PropertyIdentifier(type = Integer.class)
	public static final String MIN_WIDTH_KEY = "minWidth";
	@PropertyIdentifier(type = Integer.class)
	public static final String MIN_HEIGHT_KEY = "minHeight";
	@PropertyIdentifier(type = Integer.class)
	public static final String MAX_WIDTH_KEY = "maxWidth";
	@PropertyIdentifier(type = Integer.class)
	public static final String MAX_HEIGHT_KEY = "maxHeight";
	@PropertyIdentifier(type = boolean.class)
	public static final String USE_SCROLL_BAR_KEY = "useScrollBar";
	@PropertyIdentifier(type = HorizontalScrollBarPolicy.class)
	public static final String HORIZONTAL_SCROLLBAR_POLICY_KEY = "horizontalScrollbarPolicy";
	@PropertyIdentifier(type = VerticalScrollBarPolicy.class)
	public static final String VERTICAL_SCROLLBAR_POLICY_KEY = "verticalScrollbarPolicy";
	@PropertyIdentifier(type = Vector.class)
	public static final String EXPLICIT_DEPENDANCIES_KEY = "explicitDependancies";
	@PropertyIdentifier(type = FIBLocalizedDictionary.class)
	public static final String LOCALIZED_DICTIONARY_KEY = "localizedDictionary";
	@PropertyIdentifier(type = FIBVariable.class, cardinality = Cardinality.LIST)
	public static final String VARIABLES_KEY = "variables";

	public static final String DEFAULT_DATA_VARIABLE = "data";

	@Override
	@Getter(value = PARENT_KEY/* , inverse = FIBContainer.SUB_COMPONENTS_KEY */)
	@CloningStrategy(StrategyType.IGNORE)
	public FIBContainer getParent();

	@Setter(PARENT_KEY)
	public void setParent(FIBContainer parent);

	@Getter(value = INDEX_KEY)
	@XMLAttribute
	public Integer getIndex();

	@Setter(INDEX_KEY)
	public void setIndex(Integer index);

	@Getter(value = CONTROLLER_CLASS_KEY)
	@XMLAttribute(xmlTag = "controllerClassName")
	public Class<? extends FIBController> getControllerClass();

	@Setter(CONTROLLER_CLASS_KEY)
	public void setControllerClass(Class<? extends FIBController> controllerClass);

	@Getter(value = CONSTRAINTS_KEY, isStringConvertable = true)
	@XMLAttribute
	public ComponentConstraints getConstraints();

	@Setter(CONSTRAINTS_KEY)
	public void setConstraints(ComponentConstraints constraints);

	@Getter(value = VISIBLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getVisible();

	@Setter(VISIBLE_KEY)
	public void setVisible(DataBinding<Boolean> visible);

	@Getter(value = FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(FONT_KEY)
	public void setFont(Font font);

	@Getter(value = OPAQUE_KEY)
	@XMLAttribute
	public Boolean getOpaque();

	@Setter(OPAQUE_KEY)
	public void setOpaque(Boolean opaque);

	@Getter(value = BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getBackgroundColor();

	@Setter(BACKGROUND_COLOR_KEY)
	public void setBackgroundColor(Color backgroundColor);

	@Getter(value = FOREGROUND_COLOR_KEY)
	@XMLAttribute
	public Color getForegroundColor();

	@Setter(FOREGROUND_COLOR_KEY)
	public void setForegroundColor(Color foregroundColor);

	@Getter(value = WIDTH_KEY)
	@XMLAttribute
	public Integer getWidth();

	@Setter(WIDTH_KEY)
	public void setWidth(Integer width);

	@Getter(value = HEIGHT_KEY)
	@XMLAttribute
	public Integer getHeight();

	@Setter(HEIGHT_KEY)
	public void setHeight(Integer height);

	@Getter(value = MIN_WIDTH_KEY)
	@XMLAttribute
	public Integer getMinWidth();

	@Setter(MIN_WIDTH_KEY)
	public void setMinWidth(Integer minWidth);

	@Getter(value = MIN_HEIGHT_KEY)
	@XMLAttribute
	public Integer getMinHeight();

	@Setter(MIN_HEIGHT_KEY)
	public void setMinHeight(Integer minHeight);

	@Getter(value = MAX_WIDTH_KEY)
	@XMLAttribute
	public Integer getMaxWidth();

	@Setter(MAX_WIDTH_KEY)
	public void setMaxWidth(Integer maxWidth);

	@Getter(value = MAX_HEIGHT_KEY)
	@XMLAttribute
	public Integer getMaxHeight();

	@Setter(MAX_HEIGHT_KEY)
	public void setMaxHeight(Integer maxHeight);

	@Getter(value = USE_SCROLL_BAR_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getUseScrollBar();

	@Setter(USE_SCROLL_BAR_KEY)
	public void setUseScrollBar(boolean useScrollBar);

	@Getter(value = HORIZONTAL_SCROLLBAR_POLICY_KEY)
	@XMLAttribute
	public HorizontalScrollBarPolicy getHorizontalScrollbarPolicy();

	@Setter(HORIZONTAL_SCROLLBAR_POLICY_KEY)
	public void setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy horizontalScrollbarPolicy);

	@Getter(value = VERTICAL_SCROLLBAR_POLICY_KEY)
	@XMLAttribute
	public VerticalScrollBarPolicy getVerticalScrollbarPolicy();

	@Setter(VERTICAL_SCROLLBAR_POLICY_KEY)
	public void setVerticalScrollbarPolicy(VerticalScrollBarPolicy verticalScrollbarPolicy);

	@Getter(value = EXPLICIT_DEPENDANCIES_KEY, cardinality = Cardinality.LIST, inverse = FIBDependancy.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<FIBDependancy> getExplicitDependancies();

	@Setter(EXPLICIT_DEPENDANCIES_KEY)
	public void setExplicitDependancies(List<FIBDependancy> explicitDependancies);

	@Adder(EXPLICIT_DEPENDANCIES_KEY)
	public void addToExplicitDependancies(FIBDependancy aExplicitDependancie);

	@Remover(EXPLICIT_DEPENDANCIES_KEY)
	public void removeFromExplicitDependancies(FIBDependancy aExplicitDependancie);

	@Getter(value = LOCALIZED_DICTIONARY_KEY, inverse = FIBLocalizedDictionary.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public FIBLocalizedDictionary getLocalizedDictionary();

	@Setter(LOCALIZED_DICTIONARY_KEY)
	public void setLocalizedDictionary(FIBLocalizedDictionary localizedDictionary);

	public boolean isRootComponent();

	public FIBComponent getRootComponent();

	// @Deprecated
	// public void notifiedBindingModelRecreated();

	public Type getDynamicAccessType();

	/**
	 * Recursive lookup method for contained FIBComponent
	 * 
	 * @param name
	 * @return
	 */
	public FIBComponent getComponentNamed(String name);

	public Font retrieveValidFont();

	public Color retrieveValidForegroundColor();

	public Color retrieveValidBackgroundColor();

	// public void updateBindingModel();

	public void declareDependantOf(FIBComponent aComponent);

	public List<FIBButton> getDefaultButtons();

	public Date getLastModified();

	public void setLastModified(Date lastModified);

	public Resource getResource();

	public void setResource(Resource resource);

	public FIBLocalizedDictionary retrieveFIBLocalizedDictionary();

	/*
	 * public Type getDataType();
	 * 
	 * public void setDataType(Type type);
	 */

	public boolean definePreferredDimensions();

	public void setDefinePreferredDimensions(boolean definePreferredDimensions);

	public boolean defineMaxDimensions();

	public void setDefineMaxDimensions(boolean defineMaxDimensions);

	public boolean defineMinDimensions();

	public void setDefineMinDimensions(boolean defineMinDimensions);

	public Iterator<FIBComponent> getMayDependsIterator();

	public Iterator<FIBComponent> getMayAltersIterator();

	public abstract String getIdentifier();

	@Deprecated
	public List<DataBinding<?>> getDeclaredBindings();

	@DeserializationInitializer
	public void initializeDeserialization();

	@DeserializationFinalizer
	public void finalizeDeserialization();

	public void setBindingFactory(BindingFactory bindingFactory);

	public List<FIBComponent> getMayDepends();

	public FIBDependancy createNewExplicitDependancy();

	public void deleteExplicitDependancy(FIBDependancy p);

	// TODO: move to FIBContainer
	public List<FIBComponent> getNamedComponents();

	/**
	 * Return (create when null) binding variable identified by "data"<br>
	 * Default behavior is to generate a binding variable with the java type identified by data class
	 */
	// public BindingVariable getDataBindingVariable();

	/**
	 * Return (create when null) binding variable identified by "controller"<br>
	 * Default behavior is to generate a binding variable with the java type identified by controller class
	 * 
	 * @return
	 */
	public BindingVariable getControllerBindingVariable();

	/**
	 * Return (create when null) binding variable identified by component name (this is dynamic access to data beeing edited in the
	 * component)<br>
	 * 
	 * @return
	 */
	public BindingVariable getDynamicAccessBindingVariable();

	// public void updateDataBindingVariable();

	public void updateControllerBindingVariable();

	public void updateDynamicAccessBindingVariable();

	/**
	 * Search localized entries using {@link LocalizationEntryRetriever}
	 * 
	 * @param retriever
	 */
	public void searchLocalized(LocalizationEntryRetriever retriever);

	/**
	 * Iterate on whole component to find all localization entries, based on FIBComponent model<br>
	 * Missing entries are added to FIBLocalizedDictionary
	 */
	public void searchAndRegisterAllLocalized();

	/**
	 * Return flag indicating if this component as been marked as "hidden" (parameters scheme)
	 * 
	 * @return
	 */
	public boolean isHidden();

	@Getter(value = VARIABLES_KEY, cardinality = Cardinality.LIST, inverse = FIBVariable.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBVariable<?>> getVariables();

	@Setter(VARIABLES_KEY)
	public void setVariables(List<FIBVariable<?>> variables);

	@Adder(VARIABLES_KEY)
	public void addToVariables(FIBVariable<?> aVariable);

	@Remover(VARIABLES_KEY)
	public void removeFromVariables(FIBVariable<?> aVariable);

	@Finder(collection = VARIABLES_KEY, attribute = FIBVariable.NAME_KEY)
	public FIBVariable<?> getVariable(String variableName);

	public FIBViewType getComponentType();

	public static abstract class FIBComponentImpl extends FIBModelObjectImpl implements FIBComponent {

		private static final Logger LOGGER = Logger.getLogger(FIBComponent.class.getPackage().getName());

		private BindingFactory bindingFactory;
		public static Color DISABLED_COLOR = Color.GRAY;

		@Deprecated
		public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
		// @Deprecated
		// private BindingDefinition DATA;

		private Resource definitionFile;

		private Date lastModified;

		/*
		 * @Deprecated public BindingDefinition getDataBindingDefinition() { if
		 * (DATA == null) { DATA = new BindingDefinition("data",
		 * getDefaultDataType(), DataBinding.BindingDefinitionType.GET, false);
		 * } return DATA; }
		 */

		private Integer index;
		private DataBinding<?> data;
		private DataBinding<Boolean> visible;

		private Font font;
		private Boolean opaque;
		private Color backgroundColor;
		private Color foregroundColor;

		private Integer width;
		private Integer height;

		private Integer minWidth;
		private Integer minHeight;

		private Integer maxWidth;
		private Integer maxHeight;

		private boolean useScrollBar = false;
		private HorizontalScrollBarPolicy horizontalScrollbarPolicy = null;
		private VerticalScrollBarPolicy verticalScrollbarPolicy = null;
		// private String dataClassName;

		private final Vector<FIBComponent> mayDepends;
		private final Vector<FIBComponent> mayAlters;

		// protected Class dataClass;
		private Class<? extends FIBController> controllerClass;

		private FIBContainer parent;

		protected BindingModel bindingModel = null;
		protected BindingVariable dataBindingVariable;
		protected BindingVariable controllerBindingVariable;
		protected BindingVariable dynamicAccessBindingVariable;

		private FIBViewType componentType;

		public FIBComponentImpl() {
			super();
			explicitDependancies = new Vector<FIBDependancy>();
			mayDepends = new Vector<FIBComponent>();
			mayAlters = new Vector<FIBComponent>();
		}

		@Override
		public FIBViewType getComponentType() {
			if (componentType == null) {
				componentType = makeComponentType();
			}
			return componentType;
		}

		protected FIBViewType<?> makeComponentType() {
			return new FIBViewType(this);
		}

		@Override
		public String getBaseIdentifier() {
			return this.getBaseName() + ":" + this.getName();
		}

		@Override
		public FIBContainer getParent() {
			return parent;
		}

		@Override
		public void setParent(FIBContainer parent) {
			if (this.parent != parent) {
				BindingModel oldBindingModel = getBindingModel();
				FIBContainer oldParent = this.parent;
				this.parent = parent;
				if (oldParent != null && getDynamicAccessBindingVariable() != null) {
					oldParent.getBindingModel().removeFromBindingVariables(getDynamicAccessBindingVariable());
				}
				// Changing parent might cause the BindingModel to be different
				bindingModelMightChange(oldBindingModel);
			}
		}

		@Override
		public void addToVariables(FIBVariable<?> aVariable) {
			performSuperAdder(VARIABLES_KEY, aVariable);
			if (isRootComponent() && getBindingModel().bindingVariableNamed(aVariable.getName()) == null) {
				getBindingModel().addToBindingVariables(aVariable.getBindingVariable());
			}
		}

		@Override
		public void removeFromVariables(FIBVariable<?> aVariable) {
			performSuperRemover(VARIABLES_KEY, aVariable);
			if (isRootComponent()) {
				getBindingModel().removeFromBindingVariables(aVariable.getBindingVariable());
			}
		}

		/*
		 * @Override public FIBVariable<?> getVariable(String variableName) {
		 * for (FIBVariable<?> v : getVariables()) { if
		 * (v.getName().equals(variableName)) { return v; } } return null; }
		 */

		/**
		 * Return a boolean indicating if hierarchy is valid (no cycle was detected in hierarchy)
		 * 
		 * @return
		 */
		private boolean hasValidHierarchy() {
			List<FIBComponent> ancestors = new Vector<FIBComponent>();
			FIBComponent c = this;
			while (c != null) {
				if (ancestors.contains(c)) {
					return false;
				}
				ancestors.add(c);
				c = c.getParent();
			}
			return true;
		}

		/*
		 * public Hashtable<String,String> getLayoutConstraints() { return
		 * layoutConstraints; }
		 * 
		 * public String getConstraint(String constraint) { return
		 * layoutConstraints.get(constraint); }
		 * 
		 * public boolean getBooleanConstraint(String constraint) { return
		 * layoutConstraints.get(constraint) != null &&
		 * layoutConstraints.get(constraint).equalsIgnoreCase("true"); }
		 * 
		 * 
		 * public String _getConstraints() { if (layoutConstraints.size() == 0)
		 * return null; StringBuffer returned = new StringBuffer(); boolean
		 * isFirst = true; for (String key : layoutConstraints.keySet()) {
		 * String value = layoutConstraints.get(key);
		 * returned.append((isFirst?"":";")+key+"="+value); isFirst = false; }
		 * return returned.toString(); }
		 * 
		 * public void _setConstraints(String someConstraints) { StringTokenizer
		 * st = new StringTokenizer(someConstraints,";"); while
		 * (st.hasMoreTokens()) { String next = st.nextToken(); StringTokenizer
		 * st2 = new StringTokenizer(next,"="); String key = null; String value
		 * = null; if (st2.hasMoreTokens()) key = st2.nextToken(); if
		 * (st2.hasMoreTokens()) value = st2.nextToken(); if (key != null &&
		 * value != null) { layoutConstraints.put(key,value); } } }
		 */

		private ComponentConstraints constraints;

		@Override
		public ComponentConstraints getConstraints() {
			constraints = _normalizeConstraintsWhenRequired(constraints);
			return constraints;
		}

		@Override
		public void setConstraints(ComponentConstraints someConstraints) {
			// ComponentConstraints normalizedConstraints = constraints;
			ComponentConstraints normalizedConstraints = _normalizeConstraintsWhenRequired(someConstraints);
			FIBPropertyNotification<ComponentConstraints> notification = requireChange(CONSTRAINTS_KEY, normalizedConstraints);
			if (notification != null) {
				if (normalizedConstraints != null) {
					normalizedConstraints.setComponent(this);
				}
				this.constraints = normalizedConstraints;
				hasChanged(notification);
			}
		}

		private ComponentConstraints _normalizeConstraintsWhenRequired(ComponentConstraints someConstraints) {
			if (getParent() instanceof FIBSplitPanel) {
				if (someConstraints == null) {
					SplitLayoutConstraints returned = new SplitLayoutConstraints(((FIBSplitPanel) getParent()).getFirstEmptyPlaceHolder());
					returned.setComponent(this);
					return returned;
				}
				if (!(someConstraints instanceof SplitLayoutConstraints)) {
					return new SplitLayoutConstraints(someConstraints);
				}
				someConstraints.setComponent(this);
				return someConstraints;
			}
			else if (getParent() instanceof FIBPanel) {
				// Init to default value when relevant but null
				if (someConstraints == null) {
					ComponentConstraints returned;
					switch (((FIBPanel) getParent()).getLayout()) {
						case none:
							returned = new NoneLayoutConstraints();
							break;
						case flow:
							returned = new FlowLayoutConstraints();
							break;
						case grid:
							returned = new GridLayoutConstraints();
							break;
						case box:
							returned = new BoxLayoutConstraints();
							break;
						case border:
							returned = new BorderLayoutConstraints();
							break;
						case twocols:
							returned = new TwoColsLayoutConstraints();
							break;
						case gridbag:
							returned = new GridBagLayoutConstraints();
							break;
						case buttons:
							returned = new ButtonLayoutConstraints();
							break;
						default:
							returned = new NoneLayoutConstraints();
							break;
					}
					returned.setComponent(this);
					return returned;
				}
				// Mutate to right type when necessary
				switch (((FIBPanel) getParent()).getLayout()) {
					case none:
						if (!(someConstraints instanceof NoneLayoutConstraints)) {
							return new NoneLayoutConstraints(someConstraints);
						}
						break;
					case flow:
						if (!(someConstraints instanceof FlowLayoutConstraints)) {
							return new FlowLayoutConstraints(someConstraints);
						}
						break;
					case grid:
						if (!(someConstraints instanceof GridLayoutConstraints)) {
							return new GridLayoutConstraints(someConstraints);
						}
						break;
					case box:
						if (!(someConstraints instanceof BoxLayoutConstraints)) {
							return new BoxLayoutConstraints(someConstraints);
						}
						break;
					case border:
						if (!(someConstraints instanceof BorderLayoutConstraints)) {
							return new BorderLayoutConstraints(someConstraints);
						}
						break;
					case twocols:
						if (!(someConstraints instanceof TwoColsLayoutConstraints)) {
							return new TwoColsLayoutConstraints(someConstraints);
						}
						break;
					case gridbag:
						if (!(someConstraints instanceof GridBagLayoutConstraints)) {
							return new GridBagLayoutConstraints(someConstraints);
						}
						break;
					default:
				}
				someConstraints.setComponent(this);
				return someConstraints;
			}
			else {
				// No constraints for a component which container is not custom
				// layouted
				return someConstraints;
			}
		}

		/*
		 * public String _getConditional() { return conditional; }
		 * 
		 * public void _setConditional(String conditional) { this.conditional =
		 * conditional; DefaultExpressionParser parser = new
		 * DefaultExpressionParser(); Vector<Variable> variables; try {
		 * conditionalExpression = parser.parse(conditional); variables =
		 * Expression.extractVariables(conditional);
		 * System.out.println("Variables for "+conditional+"\n"+variables); }
		 * catch (ParseException e) { e.printStackTrace(); } catch
		 * (TypeMismatchException e) { e.printStackTrace(); } }
		 * 
		 * public boolean isConditional() { return conditional != null; }
		 * 
		 * public boolean evaluateCondition(final Object dataObject) { if
		 * (dataObject == null) return false;
		 * 
		 * try { Expression returned = conditionalExpression.evaluate( new
		 * EvaluationContext( new ExpressionParser.DefaultConstantFactory(), new
		 * VariableFactory() { public Expression makeVariable(Word value) {
		 * Object valueObject =
		 * FIBKeyValueCoder.getObjectValue(dataObject,value.getValue()); if
		 * (valueObject instanceof String) { return new
		 * Constant.StringConstant((String)valueObject); } else if (valueObject
		 * instanceof Enum) { return new
		 * Constant.EnumConstant(((Enum)valueObject).name()); } else if
		 * (valueObject instanceof Integer) { return new
		 * Constant.IntegerConstant((Integer)valueObject); } else if
		 * (valueObject instanceof Long) { return new
		 * Constant.IntegerConstant((Long)valueObject); } else if (valueObject
		 * instanceof Short) { return new
		 * Constant.IntegerConstant((Short)valueObject); } else if (valueObject
		 * instanceof Float) { return new
		 * Constant.FloatConstant((Float)valueObject); } else if (valueObject
		 * instanceof Double) { return new
		 * Constant.FloatConstant((Double)valueObject); } else if (valueObject
		 * instanceof Boolean) { return ((Boolean)valueObject ?
		 * Constant.BooleanConstant.TRUE : Constant.BooleanConstant.FALSE); } //
		 * TODO Handle others return new Variable(value.getValue()); } }, new
		 * ExpressionParser.DefaultFunctionFactory()));
		 * System.out.println("After evaluation: "
		 * +returned+" of "+returned.getClass().getName()); if (returned
		 * instanceof BooleanConstant) return
		 * ((BooleanConstant)returned).getValue();
		 * logger.warning("Could not evaluate: "
		 * +conditional+" found: "+returned); return true; } catch
		 * (TypeMismatchException e) { e.printStackTrace();
		 * logger.warning("TypeMismatch: "+e.getMessage()); }
		 * 
		 * 
		 * return true; }
		 */

		@Override
		public boolean isRootComponent() {
			return getParent() == null;
		}

		/**
		 * Return the root component for this component. Iterate over the top of the component hierarchy.
		 * 
		 * @return
		 */
		@Override
		public FIBComponent getRootComponent() {
			FIBComponent current = this;
			while (current != null && !current.isRootComponent()) {
				current = current.getParent();
			}
			return current;
		}

		@Override
		public BindingModel getBindingModel() {
			if (isRootComponent()) {
				if (bindingModel == null) {
					createBindingModel();
				}
				return bindingModel;
			}
			else {
				if (getRootComponent() != null && getRootComponent() != this) {
					return getRootComponent().getBindingModel();
				}
				return null;
			}
		}

		/**
		 * Return (create when null) binding variable identified by "data"<br>
		 * Default behavior is to generate a binding variable with the java type identified by data class
		 */
		/*
		 * @Override public BindingVariable getDataBindingVariable() { if
		 * (dataBindingVariable == null) { dataBindingVariable = new
		 * BindingVariable("data", getDataType());
		 * getBindingModel().addToBindingVariables(dataBindingVariable); }
		 * return dataBindingVariable; }
		 */

		/**
		 * Return (create when null) binding variable identified by "controller"<br>
		 * Default behavior is to generate a binding variable with the java type identified by controller class
		 * 
		 * @return
		 */
		@Override
		public BindingVariable getControllerBindingVariable() {
			if (controllerBindingVariable == null) {
				controllerBindingVariable = new BindingVariable("controller", getControllerClass());
				getBindingModel().addToBindingVariables(controllerBindingVariable);
			}
			return controllerBindingVariable;
		}

		/**
		 * Return (create when null) binding variable identified by component name (this is dynamic access to data beeing edited in the
		 * component)<br>
		 * 
		 * @return
		 */
		@Override
		public BindingVariable getDynamicAccessBindingVariable() {
			if (dynamicAccessBindingVariable == null) {
				if (StringUtils.isNotEmpty(getName()) && getDynamicAccessType() != null) {
					dynamicAccessBindingVariable = new BindingVariable(getName(), getDynamicAccessType());
					getBindingModel().addToBindingVariables(dynamicAccessBindingVariable);
				}
			}
			return dynamicAccessBindingVariable;
		}

		/*
		 * @Override public void updateBindingModel() { if
		 * (deserializationPerformed) { logger.fine("updateBindingModel()");
		 * FIBComponentImpl root = (FIBComponentImpl) getRootComponent(); if
		 * (root != null) { root._bindingModel = null;
		 * root.createBindingModel(); } } }
		 */

		/*
		 * @Override public void updateDataBindingVariable() {
		 * getDataBindingVariable().setType(getDataType()); }
		 */

		@Override
		public void updateControllerBindingVariable() {
			getControllerBindingVariable().setType(getControllerClass());
		}

		@Override
		public void updateDynamicAccessBindingVariable() {
			if (getDynamicAccessBindingVariable() != null) {
				// if (getDynamicAccessBindingVariable().getVariableName() !=
				// getName()) {
				if (!getDynamicAccessBindingVariable().getVariableName().equals(getName())) {
					String oldName = getDynamicAccessBindingVariable().getVariableName();
					// System.out.println("* on change le nom de la variable a "
					// + getName());
					getDynamicAccessBindingVariable().setVariableName(getName());
					getBindingModel().getPropertyChangeSupport().firePropertyChange(BindingModel.BINDING_VARIABLE_NAME_CHANGED, oldName,
							getName());
				}
				if (getDynamicAccessBindingVariable().getType() != getDynamicAccessType()) {
					Type oldType = getDynamicAccessBindingVariable().getType();
					// System.out.println("* on change le type de la variable a "
					// + getDynamicAccessType());
					getDynamicAccessBindingVariable().setType(getDynamicAccessType());
					getBindingModel().getPropertyChangeSupport().firePropertyChange(BindingModel.BINDING_VARIABLE_TYPE_CHANGED, oldType,
							getDynamicAccessType());
				}
				if (getBindingModel().bindingVariableNamed(getName()) != getDynamicAccessBindingVariable()) {
					// This indicates that component hierarchy change, and that
					// dynamic access binding variable
					// Move from/to rootComponent binding model to/from this
					// component binding model
					if (getBindingModel().bindingVariableNamed(getName()) == null) {
						// System.out.println("* on ajoute la variable a " +
						// getDynamicAccessType());
						getBindingModel().addToBindingVariables(getDynamicAccessBindingVariable());
					}
				}
			}
		}

		public void bindingModelMightChange(BindingModel oldBindingModel) {

			// System.out.println("bindingModelMightChange");

			if (oldBindingModel != getBindingModel()) {
				// System.out.println("fire");
				getPropertyChangeSupport().firePropertyChange(BINDING_MODEL_PROPERTY, null, getBindingModel());
			}

			// Following is deprecated ???
			// getData().markedAsToBeReanalized();
			updateDynamicAccessBindingVariable();
		}

		/**
		 * Internally called to create component BindingModel<br>
		 * Note that {@link BindingModel} created by this method will not necessary be the one returned by getBindingModel() method, because
		 * all components BindingModel references the root component BindingModel<br>
		 * 
		 */
		private void createBindingModel() {

			bindingModel = new BindingModel();
			for (FIBVariable<?> v : getVariables()) {
				v.appendToBindingModel(bindingModel);
			}

			// getDataBindingVariable();
			getControllerBindingVariable();
			getDynamicAccessBindingVariable();
		}

		/*
		 * protected void createBindingModel(BindingModel baseBindingModel) { if
		 * (bindingModel == null) { if (baseBindingModel == null) { bindingModel
		 * = new BindingModel(); } else { bindingModel = new
		 * BindingModel(baseBindingModel); }
		 * 
		 * updateDataBindingVariable();
		 * 
		 * if (StringUtils.isNotEmpty(getName()) && getDynamicAccessType() !=
		 * null) { bindingModel.addToBindingVariables(new
		 * BindingVariable(getName(), getDynamicAccessType())); }
		 * 
		 * Iterator<FIBComponent> it = subComponentIterator(); while
		 * (it.hasNext()) { FIBComponent subComponent = it.next(); if
		 * (StringUtils.isNotEmpty(subComponent.getName()) &&
		 * subComponent.getDynamicAccessType() != null) { bindingModel
		 * .addToBindingVariables(new BindingVariable(subComponent.getName(),
		 * subComponent.getDynamicAccessType())); } }
		 * 
		 * Class myControllerClass = getControllerClass(); if (myControllerClass
		 * == null) { myControllerClass = FIBController.class; }
		 * 
		 * bindingModel.addToBindingVariables(new BindingVariable("controller",
		 * myControllerClass));
		 * 
		 * it = subComponentIterator(); while (it.hasNext()) { FIBComponent
		 * subComponent = it.next();
		 * subComponent.notifiedBindingModelRecreated(); }
		 * 
		 * } // logger.info("Created binding model at root component level:\n"+
		 * _bindingModel); }
		 */

		/*
		 * @Override
		 * 
		 * @Deprecated public void notifiedBindingModelRecreated() { }
		 */

		@Deprecated
		protected boolean deserializationPerformed = true;

		@Override
		public void initializeDeserialization() {
			deserializationPerformed = false;
		}

		@Override
		public void finalizeDeserialization() {

			/*
			 * if (isRootComponent()) { updateBindingModel(); }
			 */

			if (data != null) {
				data.decode();
			}

			if (visible != null) {
				visible.decode();
			}

		}

		@Override
		public List<FIBComponent> getMayDepends() {
			return mayDepends;
		}

		@Override
		public Iterator<FIBComponent> getMayDependsIterator() {
			return new ArrayList<FIBComponent>(mayDepends).iterator();
		}

		@Override
		public Iterator<FIBComponent> getMayAltersIterator() {
			return new ArrayList<FIBComponent>(mayAlters).iterator();
		}

		@Override
		public void declareDependantOf(FIBComponent aComponent) /*
																 * throws
																 * DependancyLoopException
																 */ {
			// logger.info("Component "+this+" depends of "+aComponent);
			if (aComponent != null) {
				if (aComponent == this) {
					LOGGER.warning("Forbidden reflexive dependencies");
					return;
				}
				// Look if this dependancy may cause a loop in dependancies
				/*
				 * try { Vector<FIBComponent> dependancies = new
				 * Vector<FIBComponent>(); dependancies.add(aComponent);
				 * searchLoopInDependenciesWith(aComponent, dependancies); }
				 * catch (DependencyLoopException e) {
				 * logger.warning("Forbidden loop in dependencies: " +
				 * e.getMessage()); throw e; }
				 */

				if (!mayDepends.contains(aComponent)) {
					mayDepends.add(aComponent);
					LOGGER.fine("Component " + this + " depends of " + aComponent);
				}
				if (!((FIBComponentImpl) aComponent).mayAlters.contains(this)) {
					((FIBComponentImpl) aComponent).mayAlters.add(this);
				}
			}
			else {
				LOGGER.warning("Trying to test dependency against a NULL Fib Component");
			}
		}

		/*
		 * private void searchLoopInDependanciesWith(FIBComponent aComponent,
		 * Vector<FIBComponent> dependancies) throws DependancyLoopException {
		 * for (FIBComponent c : aComponent.mayDepends) { if (c == this) { throw
		 * new DependencyLoopException(dependencies); } Vector<FIBComponent>
		 * newVector = new Vector<FIBComponent>();
		 * newVector.addAll(dependencies); newVector.add(c);
		 * searchLoopInDependenciesWith(c, newVector); } }
		 */

		/*
		 * protected static class DependancyLoopException extends Exception {
		 * private final Vector<FIBComponent> dependancies;
		 * 
		 * public DependencyLoopException(Vector<FIBComponent> dependancies) {
		 * this.dependencies = dependancies; }
		 * 
		 * @Override public String getMessage() { return
		 * "DependencyLoopException: " + dependencies; } }
		 */

		@Override
		public Integer getIndex() {
			if (index == null) {
				if (getConstraints() != null && getConstraints().hasIndex()) {
					return getConstraints().getIndex();
				}
			}
			return index;
		}

		@Override
		public void setIndex(Integer index) {
			FIBPropertyNotification<Integer> notification = requireChange(INDEX_KEY, index);
			if (notification != null) {
				this.index = index;
				hasChanged(notification);
				if (getParent() != null) {
					getParent().reorderComponents();
				}
			}
		}

		/*
		 * @Override public DataBinding<?> getData() { if (data == null) { data
		 * = new DataBinding<Object>(this, Object.class,
		 * DataBinding.BindingDefinitionType.GET) {
		 * 
		 * @Override public Type getDeclaredType() { return getDataType(); } };
		 * data.setBindingName("data"); } return data; }
		 * 
		 * @Override public void setData(DataBinding<?> data) { if (data !=
		 * null) { this.data = new DataBinding<Object>(data.toString(), this,
		 * data.getDeclaredType(), data.getBindingDefinitionType()) {
		 * 
		 * @Override public Type getDeclaredType() { return getDataType(); } };
		 * 
		 * this.data.setBindingName("data");
		 * 
		 * updateDynamicAccessBindingVariable();
		 * 
		 * } else { this.data = null; } }
		 */

		@Override
		public DataBinding<Boolean> getVisible() {
			if (visible == null) {
				visible = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return visible;
		}

		@Override
		public void setVisible(DataBinding<Boolean> visible) {
			if (visible != null) {
				visible = new DataBinding<Boolean>(visible.toString(), this, Boolean.class, DataBinding.BindingDefinitionType.GET);
				visible.setBindingName("data");
			}
			this.visible = visible;
		}

		@Override
		public String toString() {
			return getImplementedInterface().getSimpleName() + " ("
					+ (getName() != null ? getName() : getIdentifier() != null ? getIdentifier() : "unnamed") + ")";
		}

		/**
		 * Return the FIBComponent this component refer to
		 * 
		 * @return
		 */
		@Override
		public FIBComponentImpl getComponent() {
			return this;
		}

		/*
		 * private Type dataType;
		 * 
		 * @Override public Type getDataType() { if (dataType != null) { return
		 * dataType; } if (dataClass == null) { return Object.class; } return
		 * dataClass; }
		 * 
		 * @Override public void setDataType(Type type) { if (type != null &&
		 * !type.equals(this.dataType)) { Type oldType = this.dataType;
		 * this.dataType = type; updateDataBindingVariable();
		 * getPropertyChangeSupport().firePropertyChange("dataType", oldType,
		 * type); } }
		 */

		/*
		 * @Override public Class<?> getDataClass() { return dataClass; }
		 * 
		 * @Override
		 * 
		 * @SuppressWarnings("rawtypes") public void setDataClass(Class<?>
		 * dataClass) { FIBPropertyNotification<Class> notification =
		 * requireChange(DATA_CLASS_KEY, (Class) dataClass); if (notification !=
		 * null) { // System.out.println("data=" + getData() + " valid=" +
		 * getData().isValid() + " reason: " + //
		 * getData().invalidBindingReason()); this.dataClass = dataClass;
		 * getData().markedAsToBeReanalized(); // System.out.println("data=" +
		 * getData() + " valid=" + getData().isValid() + " reason: " + //
		 * getData().invalidBindingReason()); updateDataBindingVariable();
		 * updateDynamicAccessBindingVariable(); hasChanged(notification); } }
		 */

		@Override
		public Class<? extends FIBController> getControllerClass() {
			if (!isRootComponent()) {
				return null;
			}
			if (controllerClass == null) {
				return FIBController.class;
			}
			return controllerClass;
		}

		@Override
		public void setControllerClass(Class<? extends FIBController> controllerClass) {

			FIBPropertyNotification<Class> notification = requireChange(CONTROLLER_CLASS_KEY, (Class) controllerClass);
			if (notification != null) {
				this.controllerClass = controllerClass;
				updateControllerBindingVariable();
				hasChanged(notification);
			}
		}

		/*
		 * public Type getDefaultDataClass() { return Object.class;
		 * 
		 * }
		 */

		@Override
		public final Type getDynamicAccessType() {
			return getComponentType();
			/*
			 * Type[] args = new Type[2]; args[0] = new
			 * WilcardTypeImpl(getClass()); args[1] = new
			 * WilcardTypeImpl(Object.class); return new
			 * ParameterizedTypeImpl(FIBView.class, args);
			 */
		}

		@Override
		public final Font retrieveValidFont() {
			if (font == null) {
				if (!isRootComponent() && hasValidHierarchy()) {
					return getParent().retrieveValidFont();
				}
				else {
					return null; // Use system default
				}
			}

			return getFont();
		}

		@Override
		public final Color retrieveValidForegroundColor() {
			if (foregroundColor == null) {
				if (!isRootComponent() && hasValidHierarchy()) {
					return getParent().retrieveValidForegroundColor();
				}
				else {
					return null; // Use default
				}
			}

			return getForegroundColor();
		}

		@Override
		public final Color retrieveValidBackgroundColor() {
			if (backgroundColor == null) {
				if (!isRootComponent() && hasValidHierarchy()) {
					return getParent().retrieveValidBackgroundColor();
				}
				else {
					return null; // Use system default
				}
			}

			return getBackgroundColor();
		}

		@Override
		public Font getFont() {
			return font;
		}

		@Override
		public void setFont(Font font) {
			FIBPropertyNotification<Font> notification = requireChange(FONT_KEY, font);
			if (notification != null) {
				this.font = font;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getOpaque() {
			return opaque;
		}

		@Override
		public void setOpaque(Boolean opaque) {
			FIBPropertyNotification<Boolean> notification = requireChange(OPAQUE_KEY, opaque);
			if (notification != null) {
				this.opaque = opaque;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBackgroundColor() {
			return backgroundColor;
		}

		@Override
		public void setBackgroundColor(Color backgroundColor) {
			FIBPropertyNotification<Color> notification = requireChange(BACKGROUND_COLOR_KEY, backgroundColor);
			if (notification != null) {
				this.backgroundColor = backgroundColor;
				hasChanged(notification);
			}
		}

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}

		@Override
		public void setForegroundColor(Color foregroundColor) {
			FIBPropertyNotification<Color> notification = requireChange(FOREGROUND_COLOR_KEY, foregroundColor);
			if (notification != null) {
				this.foregroundColor = foregroundColor;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getUseScrollBar() {
			return useScrollBar;
		}

		@Override
		public void setUseScrollBar(boolean useScrollBar) {
			FIBPropertyNotification<Boolean> notification = requireChange(USE_SCROLL_BAR_KEY, useScrollBar);
			if (notification != null) {
				this.useScrollBar = useScrollBar;
				if (useScrollBar) {
					horizontalScrollbarPolicy = HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED;
					verticalScrollbarPolicy = VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED;
				}
				else {
					horizontalScrollbarPolicy = null;
					verticalScrollbarPolicy = null;
				}
				hasChanged(notification);
			}
		}

		@Override
		public HorizontalScrollBarPolicy getHorizontalScrollbarPolicy() {
			return horizontalScrollbarPolicy;
		}

		@Override
		public void setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy horizontalScrollbarPolicy) {
			FIBPropertyNotification<HorizontalScrollBarPolicy> notification = requireChange(HORIZONTAL_SCROLLBAR_POLICY_KEY,
					horizontalScrollbarPolicy);
			if (notification != null) {
				this.horizontalScrollbarPolicy = horizontalScrollbarPolicy;
				hasChanged(notification);
			}
		}

		@Override
		public VerticalScrollBarPolicy getVerticalScrollbarPolicy() {
			return verticalScrollbarPolicy;
		}

		@Override
		public void setVerticalScrollbarPolicy(VerticalScrollBarPolicy verticalScrollbarPolicy) {
			FIBPropertyNotification<VerticalScrollBarPolicy> notification = requireChange(VERTICAL_SCROLLBAR_POLICY_KEY,
					verticalScrollbarPolicy);
			if (notification != null) {
				this.verticalScrollbarPolicy = verticalScrollbarPolicy;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getWidth() {
			return width;
		}

		@Override
		public void setWidth(Integer width) {
			FIBPropertyNotification<Integer> notification = requireChange(WIDTH_KEY, width);
			if (notification != null) {
				this.width = width;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getHeight() {
			return height;
		}

		@Override
		public void setHeight(Integer height) {
			FIBPropertyNotification<Integer> notification = requireChange(HEIGHT_KEY, height);
			if (notification != null) {
				this.height = height;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMinWidth() {
			return minWidth;
		}

		@Override
		public void setMinWidth(Integer minWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(MIN_HEIGHT_KEY, minWidth);
			if (notification != null) {
				this.minWidth = minWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMinHeight() {
			return minHeight;
		}

		@Override
		public void setMinHeight(Integer minHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(MIN_HEIGHT_KEY, minHeight);
			if (notification != null) {
				this.minHeight = minHeight;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMaxWidth() {
			return maxWidth;
		}

		@Override
		public void setMaxWidth(Integer maxWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(MAX_WIDTH_KEY, maxWidth);
			if (notification != null) {
				this.maxWidth = maxWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getMaxHeight() {
			return maxHeight;
		}

		@Override
		public void setMaxHeight(Integer maxHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(MAX_HEIGHT_KEY, maxHeight);
			if (notification != null) {
				this.maxHeight = maxHeight;
				hasChanged(notification);
			}
		}

		@Override
		public final void setName(String name) {
			if (StringUtils.isEmpty(name)) {
				name = null;
			}
			performSuperSetter(NAME_KEY, name);
			updateDynamicAccessBindingVariable();
		}

		@Override
		public void addToParameters(FIBParameter p) {
			// Little hask to recover previously created fib
			if (p.getName().equals("controllerClassName")) {
				try {
					Class<?> myControllerClass = Class.forName(p.getValue());
					if (FIBController.class.isAssignableFrom(myControllerClass)) {
						setControllerClass((Class<? extends FIBController>) myControllerClass);
					}
				} catch (ClassNotFoundException e) {
					LOGGER.warning("Could not find class " + p.getValue());
				}

			}
			else {
				performSuperAdder(PARAMETERS_KEY, p);
			}
		}

		@Override
		public boolean definePreferredDimensions() {
			return width != null && height != null;
		}

		@Override
		public void setDefinePreferredDimensions(boolean definePreferredDimensions) {
			if (definePreferredDimensions() != definePreferredDimensions) {
				if (definePreferredDimensions) {
					// This is no more possible, we should find an other
					// solution
					/*
					 * FIBView<?, ?> v = FIBController.makeView(this,
					 * (LocalizedDelegate) null); Dimension p =
					 * v.getJComponent().getPreferredSize(); setWidth(p.width);
					 * setHeight(p.height); v.delete();
					 */
					setWidth(100);
					setHeight(100);
				}
				else {
					setWidth(null);
					setHeight(null);
				}
				notifyChange("definePreferredDimensions", !definePreferredDimensions(), definePreferredDimensions());
			}
		}

		@Override
		public boolean defineMaxDimensions() {
			return maxWidth != null && maxHeight != null;
		}

		@Override
		public void setDefineMaxDimensions(boolean defineMaxDimensions) {
			if (defineMaxDimensions) {
				// This is no more possible, we should find an other solution
				// FIBView<?, ?> v = FIBController.makeView(this,
				// (LocalizedDelegate) null);
				setMaxWidth(1024);
				setMaxHeight(1024);
				// v.delete();
			}
			else {
				setMaxWidth(null);
				setMaxHeight(null);
			}
		}

		@Override
		public boolean defineMinDimensions() {
			return minWidth != null && minHeight != null;
		}

		@Override
		public void setDefineMinDimensions(boolean defineMinDimensions) {
			if (defineMinDimensions) {
				// This is no more possible, we should find an other solution
				// FIBView<?, ?> v = FIBController.makeView(this,
				// (LocalizedDelegate) null);
				// Dimension p = v.getJComponent().getMinimumSize();
				// setMinWidth(p.width);
				// setMinHeight(p.height);
				// v.delete();
				setMinWidth(10);
				setMinHeight(10);
			}
			else {
				setMinWidth(null);
				setMinHeight(null);
			}
		}

		private Vector<FIBDependancy> explicitDependancies;

		// private Vector<FIBComponentDependancy> componentDependancies;

		/*
		 * public Vector<FIBComponentDependancy> getComponentDependancies() { if
		 * (componentDependancies == null) { componentDependancies = new
		 * Vector<FIBComponentDependancy>(); for (Iterator<FIBComponent>
		 * it=getMayDependsIterator(); it.hasNext();) {
		 * componentDependancies.add(new DynamicFIBDependancy(this,it.next()));
		 * } componentDependancies.addAll(explicitDependancies); } return
		 * componentDependancies; }
		 */

		@Override
		public Vector<FIBDependancy> getExplicitDependancies() {
			return explicitDependancies;
		}

		public void setExplicitDependancies(Vector<FIBDependancy> explicitDependancies) {
			FIBPropertyNotification<Vector<FIBDependancy>> notification = requireChange(EXPLICIT_DEPENDANCIES_KEY, explicitDependancies);
			explicitDependancies = null;
			if (notification != null) {
				this.explicitDependancies = explicitDependancies;
				hasChanged(notification);
			}
		}

		@Override
		public void addToExplicitDependancies(FIBDependancy p) {
			p.setOwner(this);
			explicitDependancies.add(p);
			if (p.getMasterComponent() != null) {
				// try {
				p.getOwner().declareDependantOf(p.getMasterComponent());
				/*
				 * } catch (DependancyLoopException e) { logger.warning(
				 * "DependancyLoopException raised while applying explicit dependancy for "
				 * + p.getOwner() + " and " + p.getMasterComponent() +
				 * " message: " + e.getMessage()); }
				 */
			}
			getPropertyChangeSupport().firePropertyChange(EXPLICIT_DEPENDANCIES_KEY, null, explicitDependancies);
		}

		@Override
		public void removeFromExplicitDependancies(FIBDependancy p) {
			p.setOwner(null);
			explicitDependancies.remove(p);
			getPropertyChangeSupport().firePropertyChange(EXPLICIT_DEPENDANCIES_KEY, null, explicitDependancies);
		}

		@Override
		public FIBDependancy createNewExplicitDependancy() {
			FIBDependancy returned = getFactory().newInstance(FIBDependancy.class);
			addToExplicitDependancies(returned);
			return returned;
		}

		@Override
		public void deleteExplicitDependancy(FIBDependancy p) {
			removeFromExplicitDependancies(p);
		}

		@Override
		public FIBLocalizedDictionary retrieveFIBLocalizedDictionary() {
			if (getLocalizedDictionary() == null) {
				FIBLocalizedDictionary newFIBLocalizedDictionary = getFactory().newInstance(FIBLocalizedDictionary.class);
				setLocalizedDictionary(newFIBLocalizedDictionary);
			}
			return getLocalizedDictionary();
		}

		@Override
		public Resource getResource() {
			return definitionFile;
		}

		@Override
		public void setResource(Resource definitionFile) {
			this.definitionFile = definitionFile;
		}

		@Override
		public List<FIBButton> getDefaultButtons() {
			List<FIBButton> defaultButtons = new ArrayList<FIBButton>();
			if (this instanceof FIBContainer) {
				List<FIBButton> buttons = getFIBButtons(((FIBContainer) this).getSubComponents());
				if (buttons.size() > 0) {
					for (FIBButton b : buttons) {
						if (b.isDefault() != null && b.isDefault()) {
							defaultButtons.add(b);
						}
					}
				}
			}
			return defaultButtons;
		}

		private List<FIBButton> getFIBButtons(List<FIBComponent> subComponents) {
			List<FIBButton> buttons = new ArrayList<FIBButton>();
			for (FIBComponent c : subComponents) {
				if (c instanceof FIBButton) {
					buttons.add((FIBButton) c);
				}
				else if (c instanceof FIBContainer) {
					buttons.addAll(getFIBButtons(((FIBContainer) c).getSubComponents()));
				}
			}
			return buttons;
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = new ArrayList<DataBinding<?>>();
			// returned.add(getData());
			for (FIBVariable<?> v : getVariables()) {
				if (v.getValue() != null && v.getValue().isSet()) {
					returned.add(v.getValue());
				}
			}

			returned.add(getVisible());
			return returned;
		}

		@Override
		public Date getLastModified() {
			return lastModified;
		}

		@Override
		public void setLastModified(Date lastModified) {
			this.lastModified = lastModified;
		}

		@Override
		public BindingFactory getBindingFactory() {
			if (bindingFactory != null) {
				return bindingFactory;
			}
			if (getParent() != null) {
				return getParent().getBindingFactory();
			}
			return FIBLibrary.instance().getBindingFactory();
		}

		@Override
		public void setBindingFactory(BindingFactory bindingFactory) {
			this.bindingFactory = bindingFactory;
		}

		// TODO: move to FIBContainer
		public Vector<FIBComponent> retrieveAllSubComponents() {
			if (this instanceof FIBContainer) {
				Vector<FIBComponent> returned = new Vector<FIBComponent>();
				addAllSubComponents((FIBContainer) this, returned);
				return returned;
			}
			return null;
		}

		// TODO: move to FIBContainer
		private void addAllSubComponents(FIBContainer c, Vector<FIBComponent> returned) {
			for (FIBComponent c2 : c.getSubComponents()) {
				returned.add(c2);
				if (c2 instanceof FIBContainer) {
					addAllSubComponents((FIBContainer) c2, returned);
				}
				/*
				 * else if (c2 instanceof FIBReferencedComponent){ FIBComponent
				 * referenced =((FIBReferencedComponent) c2).getComponent();
				 * 
				 * // TEST FIBReferencedComponent ref =
				 * ((FIBReferencedComponent) c2);
				 * 
				 * 
				 * if (referenced instanceof FIBContainer){
				 * returned.add((FIBContainer) referenced);
				 * addAllSubComponents((FIBContainer) referenced, returned); } }
				 */
			}
		}

		// TODO: move to FIBContainer
		public Iterator<FIBComponent> subComponentIterator() {
			Vector<FIBComponent> allSubComponents = retrieveAllSubComponents();
			if (allSubComponents == null) {
				return new Iterator<FIBComponent>() {
					@Override
					public boolean hasNext() {
						return false;
					}

					@Override
					public FIBComponentImpl next() {
						return null;
					}

					@Override
					public void remove() {
					}
				};
			}
			else {
				return allSubComponents.iterator();
			}
		}

		// TODO: move to FIBContainer
		@Override
		public Vector<FIBComponent> getNamedComponents() {
			Vector<FIBComponent> returned = new Vector<FIBComponent>();
			for (FIBComponent c : retrieveAllSubComponents()) {
				if (StringUtils.isNotEmpty(c.getName())) {
					returned.add(c);
				}
			}
			return returned;
		}

		// TODO: move to FIBContainer
		@Override
		public FIBComponent getComponentNamed(String name) {
			if (StringUtils.isNotEmpty(this.getName()) && this.getName().equals(name)) {
				return this;
			}
			for (FIBComponent c : retrieveAllSubComponents()) {
				if (StringUtils.isNotEmpty(c.getName()) && c.getName().equals(name)) {
					return c;
				}
			}
			return null;
		}

		/**
		 * Iterate on whole component to find all localization entries, based on FIBComponent model<br>
		 * Missing entries are added to FIBLocalizedDictionary
		 */
		@Override
		public void searchAndRegisterAllLocalized() {
			searchLocalized(getLocalizedDictionary());
			getLocalizedDictionary().refresh();

		}

		/**
		 * Return flag indicating if this component as been marked as "hidden" (parameters scheme)
		 * 
		 * @return
		 */
		@Override
		public boolean isHidden() {
			return (getParameter("hidden") != null && getParameter("hidden").equalsIgnoreCase("true"));
		}
	}

	@DefineValidationRule
	public static class RootComponentShouldHaveDataClass extends ValidationRule<RootComponentShouldHaveDataClass, FIBComponent> {
		public RootComponentShouldHaveDataClass() {
			super(FIBModelObject.class, "root_component_should_have_data_class");
		}

		@Override
		public ValidationIssue<RootComponentShouldHaveDataClass, FIBComponent> applyValidation(FIBComponent object) {
			if (object.isRootComponent() && object.getVariables().size() == 0) {
				return new ValidationWarning<RootComponentShouldHaveDataClass, FIBComponent>(this, object,
						"component_($object.toString)_is_declared_as_root_but_does_not_declare_any_variables");
			}
			return null;
		}

	}

	@DefineValidationRule
	public static class NonRootComponentShouldNotHaveLocalizedDictionary
			extends ValidationRule<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> {
		public NonRootComponentShouldNotHaveLocalizedDictionary() {
			super(FIBModelObject.class, "non_root_component_should_not_have_localized_dictionary");
		}

		@Override
		public ValidationIssue<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> applyValidation(FIBComponent object) {
			if (!object.isRootComponent() && object.getLocalizedDictionary() != null) {
				RemoveExtraLocalizedDictionary fixProposal = new RemoveExtraLocalizedDictionary();
				return new ValidationWarning<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent>(this, object,
						"component_($validable)_has_a_localized_dictionary_but_is_not_root_component", fixProposal);
			}
			return null;
		}

	}

	public static class RemoveExtraLocalizedDictionary extends FixProposal<NonRootComponentShouldNotHaveLocalizedDictionary, FIBComponent> {

		public RemoveExtraLocalizedDictionary() {
			super("remove_extra_dictionary");
		}

		@Override
		protected void fixAction() {
			getValidable().setLocalizedDictionary(null);
		}

	}

	public static class RootComponentShouldHaveMaximumOneDefaultButton
			extends ValidationRule<RootComponentShouldHaveMaximumOneDefaultButton, FIBComponent> {
		public RootComponentShouldHaveMaximumOneDefaultButton() {
			super(FIBModelObject.class, "root_component_should_have_maximum_one_default_button");
		}

		@Override
		public ValidationIssue<RootComponentShouldHaveMaximumOneDefaultButton, FIBComponent> applyValidation(FIBComponent object) {
			if (object.isRootComponent() && object instanceof FIBContainer) {
				List<FIBButton> defaultButtons = ((FIBContainer) object).getDefaultButtons();
				if (defaultButtons.size() > 1) {
					return new ValidationWarning<RootComponentShouldHaveMaximumOneDefaultButton, FIBComponent>(this, object,
							"component_($object.toString)_has_more_than_one_default_button");
				}
			}
			return null;
		}

	}

	@DefineValidationRule
	public static class VisibleBindingMustBeValid extends BindingMustBeValid<FIBComponent> {
		public VisibleBindingMustBeValid() {
			super("'visible'_binding_is_not_valid", FIBComponent.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBComponent object) {
			return object.getVisible();
		}

	}

	/**
	 * Interface used to search localized entries
	 * 
	 * @author sylvain
	 *
	 */
	public static interface LocalizationEntryRetriever {
		public void foundLocalized(String key);
	}

}
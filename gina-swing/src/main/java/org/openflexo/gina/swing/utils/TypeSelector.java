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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.connie.type.CustomTypeManager;
import org.openflexo.connie.type.GenericArrayTypeImpl;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.connie.type.PrimitiveType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.WilcardTypeImpl;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.controller.CustomTypeEditor;
import org.openflexo.gina.controller.CustomTypeEditorProvider;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.swing.utils.LoadedClassesInfo.ClassInfo;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;

/**
 * Widget allowing to edit a binding
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class TypeSelector extends TextFieldCustomPopup<Type>
		implements FIBCustomComponent<Type>, HasPropertyChangeSupport, PropertyChangeListener {
	static final Logger LOGGER = Logger.getLogger(TypeSelector.class.getPackage().getName());

	public static Resource FIB_FILE_NAME = ResourceLocator.locateResource("Fib/TypeSelector.fib");
	public static Resource JAVA_CLASS_EDITOR_FIB_FILE_NAME = ResourceLocator.locateResource("Fib/ClassEditor.fib");

	public static final Object JAVA_TYPE = new Object() {
		@Override
		public String toString() {
			return "Java type";
		}
	};
	public static final Object JAVA_LIST = new Object() {
		@Override
		public String toString() {
			return "Java list of";
		}
	};
	public static final Object JAVA_MAP = new Object() {
		@Override
		public String toString() {
			return "Java map of";
		}
	};
	public static final Object JAVA_ARRAY = new Object() {
		@Override
		public String toString() {
			return "Java array of";
		}
	};
	public static final Object JAVA_WILDCARD = new Object() {
		@Override
		public String toString() {
			return "Java wilcard";
		}
	};

	private Type _revertValue;

	protected TypeSelectorDetailsPanel _selectorPanel;

	private final List<Object> choices;
	private Object choice;

	private final PropertyChangeSupport pcSupport;

	private final List<GenericParameter> genericParameters;
	private final List<GenericBound> upperBounds;
	private final List<GenericBound> lowerBounds;

	private Type keyType = Object.class;

	private CustomTypeManager customTypeManager;
	private CustomTypeEditorProvider customTypeEditorProvider;

	private final Map<Class<? extends CustomType>, CustomTypeEditor<?>> customTypeEditors = new HashMap<>();
	private final Map<Class<? extends CustomType>, CustomTypeFactory<?>> customTypeFactories = new HashMap<>();

	public TypeSelector(Type editedObject) {
		super(editedObject);

		setRevertValue(editedObject);
		setFocusable(true);
		pcSupport = new PropertyChangeSupport(this);
		choices = new ArrayList<>();

		genericParameters = new ArrayList<>();
		upperBounds = new ArrayList<>();
		lowerBounds = new ArrayList<>();

		updateChoices();
		if (editedObject == null) {
			// choice = PrimitiveType.String;
			choice = null;
		}

		fireEditedObjectChanged();
	}

	public CustomTypeManager getCustomTypeManager() {
		return customTypeManager;
	}

	@CustomComponentParameter(name = "customTypeManager", type = CustomComponentParameter.Type.OPTIONAL)
	public void setCustomTypeManager(CustomTypeManager customTypeManager) {

		// System.out.println("******************> setCustomTypeManager with " + customTypeManager);

		if (customTypeManager != this.customTypeManager) {
			CustomTypeManager oldValue = this.customTypeManager;
			this.customTypeManager = customTypeManager;
			updateChoices();
			getPropertyChangeSupport().firePropertyChange("customTypeManager", oldValue, customTypeManager);
		}
	}

	public CustomTypeEditorProvider getCustomTypeEditorProvider() {
		return customTypeEditorProvider;
	}

	public void setCustomTypeEditorProvider(CustomTypeEditorProvider customTypeEditorProvider) {

		if ((customTypeEditorProvider == null && this.customTypeEditorProvider != null)
				|| (customTypeEditorProvider != null && !customTypeEditorProvider.equals(this.customTypeEditorProvider))) {
			CustomTypeEditorProvider oldValue = this.customTypeEditorProvider;
			this.customTypeEditorProvider = customTypeEditorProvider;
			updateChoices();
			getPropertyChangeSupport().firePropertyChange("customTypeEditorProvider", oldValue, customTypeEditorProvider);
		}
	}

	public <T extends CustomType> CustomTypeEditor<T> getCustomTypeEditor(Class<T> typeClass) {
		return (CustomTypeEditor<T>) customTypeEditors.get(typeClass);
	}

	public <T extends CustomType> CustomTypeFactory<T> getCustomTypeFactory(Class<T> typeClass) {
		return (CustomTypeFactory<T>) customTypeFactories.get(typeClass);
	}

	private void updateChoices() {
		choices.clear();

		// First add the primitives
		for (PrimitiveType primitiveType : PrimitiveType.values()) {
			choices.add(primitiveType);
		}

		// Then the technology specific types
		if (getCustomTypeManager() != null) {
			for (Class<? extends CustomType> customType : getCustomTypeManager().getCustomTypeFactories().keySet()) {
				CustomTypeFactory<?> specificFactory = getCustomTypeManager().getCustomTypeFactories().get(customType);
				if (getCustomTypeEditorProvider() != null) {
					CustomTypeEditor<?> specificEditor = getCustomTypeEditorProvider().getCustomTypeEditor(customType);
					if (specificFactory != null && specificEditor != null) {
						choices.add(customType);
						customTypeFactories.put(customType, specificFactory);
						customTypeEditors.put(customType, specificEditor);
					}
				}
			}
		}

		// Then all java types
		choices.add(JAVA_TYPE);
		choices.add(JAVA_LIST);
		choices.add(JAVA_MAP);
		choices.add(JAVA_ARRAY);
		choices.add(JAVA_WILDCARD);
	}

	public List<Object> getChoices() {
		return choices;
	}

	public Object getChoice() {
		return choice;
	}

	public String getPresentationName(Object aChoice) {
		if (aChoice instanceof Class) {
			CustomTypeEditor<?> editor = getCustomTypeEditor((Class<? extends CustomType>) aChoice);
			if (editor != null) {
				return editor.getPresentationName();
			}
		}
		return aChoice.toString();
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void setChoice(Object choice) {
		if (this.choice != choice) {
			Class<?> oldBaseClass = getBaseClass();
			CustomTypeFactory<?> oldCustomTypeFactory = getCurrentCustomTypeFactory();
			CustomTypeEditor<?> oldCustomTypeEditor = getCurrentCustomTypeEditor();

			boolean oldIsJavaType = isJavaType();
			boolean oldIsPrimitiveType = isPrimitiveType();
			boolean oldIsJavaList = isJavaList();
			boolean oldIsJavaMap = isJavaMap();
			boolean oldHasBaseJavaClass = hasBaseJavaClass();
			boolean oldIsJavaWildcard = isJavaWildcard();
			boolean oldIsCustomType = isCustomType();

			Object old = this.choice;

			if (old instanceof CustomTypeFactory) {
				((CustomTypeFactory) old).getPropertyChangeSupport().removePropertyChangeListener(this);
			}

			this.choice = choice;

			if (isCustomType()) {
				CustomTypeFactory factory = getCurrentCustomTypeFactory();
				factory.getPropertyChangeSupport().addPropertyChangeListener(this);
				if (getEditedObject() instanceof CustomType) {
					try {
						factory.configureFactory((CustomType) getEditedObject());
					} catch (ClassCastException e) {
						// That may happen if we have changed from a CustomTypefactory to another CustomTypefactory
						setEditedObject(factory.makeCustomType(null));
					}
				}
				else {
					setEditedObject(factory.makeCustomType(null));
				}
			}
			else if (choice instanceof PrimitiveType) {
				setEditedObject(((PrimitiveType) choice).getType());
			}
			else {
				// Will cause the edited object to be recomputed from new configuration values
				setBaseClass(oldBaseClass);
			}

			if (choice == JAVA_WILDCARD && !(getEditedObject() instanceof WildcardType)) {
				makeWildcardType();
			}

			getPropertyChangeSupport().firePropertyChange("choice", old, choice);
			getPropertyChangeSupport().firePropertyChange("isJavaType", oldIsJavaType, isJavaType());
			getPropertyChangeSupport().firePropertyChange("isPrimitiveType", oldIsPrimitiveType, isPrimitiveType());
			getPropertyChangeSupport().firePropertyChange("isJavaList", oldIsJavaList, isJavaList());
			getPropertyChangeSupport().firePropertyChange("isJavaMap", oldIsJavaMap, isJavaMap());
			getPropertyChangeSupport().firePropertyChange("hasBaseJavaClass", oldHasBaseJavaClass, hasBaseJavaClass());
			getPropertyChangeSupport().firePropertyChange("isJavaWildcard", oldIsJavaWildcard, isJavaWildcard());
			getPropertyChangeSupport().firePropertyChange("isCustomType", oldIsCustomType, isCustomType());
			getPropertyChangeSupport().firePropertyChange("currentCustomTypeFactory", oldCustomTypeFactory, getCurrentCustomTypeFactory());
			getPropertyChangeSupport().firePropertyChange("currentCustomTypeEditor", oldCustomTypeEditor, getCurrentCustomTypeEditor());
			if (isJavaType()) {
				getPropertyChangeSupport().firePropertyChange("classEditor", null, getClassEditor());
			}
		}
	}

	public boolean isJavaType() {
		return (choice instanceof PrimitiveType || choice == JAVA_TYPE || choice == JAVA_LIST || choice == JAVA_MAP || choice == JAVA_ARRAY
				|| choice == JAVA_WILDCARD);
	}

	public boolean isPrimitiveType() {
		return getPrimitiveType() != null;
	}

	public boolean isJavaList() {
		return (choice == JAVA_LIST);
	}

	public boolean isJavaMap() {
		return (choice == JAVA_MAP);
	}

	public boolean isJavaWildcard() {
		return (choice == JAVA_WILDCARD);
	}

	public boolean isCustomType() {
		return (choice instanceof Class);
	}

	private CustomTypeFactory<?> getCurrentCustomTypeFactory() {
		if (isCustomType()) {
			return customTypeFactories.get(choice);
		}
		return null;
	}

	public CustomTypeEditor<?> getCurrentCustomTypeEditor() {
		if (isCustomType()) {
			return customTypeEditors.get(choice);
		}
		return null;
	}

	public boolean hasBaseJavaClass() {
		return isJavaType() && (choice != JAVA_WILDCARD);
	}

	public boolean hasGenericParameters() {
		return genericParameters.size() > 0;
	}

	public Type getKeyType() {
		return keyType;
	}

	public void setKeyType(Type keyType) {
		if (!this.keyType.equals(keyType)) {
			this.keyType = keyType;
			getPropertyChangeSupport().firePropertyChange("keyType", null, keyType);
			updateEditedType();
		}
	}

	public String getKeyTypeStringRepresentation() {
		return TypeUtils.simpleRepresentation(getKeyType());
	}

	public PrimitiveType getPrimitiveType() {
		for (PrimitiveType primitiveType : org.openflexo.connie.type.PrimitiveType.values()) {
			if (primitiveType.getType().equals(getEditedObject())) {
				return primitiveType;
			}
		}
		return null;
	}

	private boolean widgetIsBeeingUpdating = false;

	@Override
	public void fireEditedObjectChanged() {

		widgetIsBeeingUpdating = true;

		try {
			super.fireEditedObjectChanged();

			Class<?> baseClass = getBaseClass();

			// First try to find the type of object
			PrimitiveType primitiveType = getPrimitiveType();
			if (primitiveType != null) {
				setChoice(primitiveType);
			}
			else {
				if (getEditedObject() instanceof CustomType && customTypeManager != null) {
					CustomTypeFactory ctFactory = customTypeManager.getCustomTypeFactories().get(getEditedObject().getClass());
					if (ctFactory != null && choices.contains(ctFactory.getCustomType())) {
						setChoice(ctFactory.getCustomType());
						ctFactory.configureFactory((CustomType) getEditedObject());
					}
				}
				else if (getEditedObject() instanceof Class) {
					setChoice(JAVA_TYPE);
				}
			}

			if (getEditedObject() instanceof ParameterizedType) {
				setChoice(JAVA_TYPE);
				updateGenericParameters(baseClass, getEditedObject());
			}
			else if (getEditedObject() instanceof WildcardType) {
				setChoice(JAVA_WILDCARD);
				updateWildcardBounds();
			}

			if (isJavaType()) {
				getClassEditor().setSelectedClassInfo(getClassEditor().getLoadedClassesInfo().getClass(baseClass));
				// getPropertyChangeSupport().firePropertyChange("loadedClassesInfo", null, getLoadedClassesInfo());
			}
		} finally {
			widgetIsBeeingUpdating = false;
		}

	}

	public List<GenericParameter> getGenericParameters() {
		return genericParameters;
	}

	public List<GenericBound> getUpperBounds() {
		return upperBounds;
	}

	public GenericBound createUpperBound() {
		GenericBound returned = new GenericBound(Object.class);
		upperBounds.add(returned);
		getPropertyChangeSupport().firePropertyChange("upperBounds", null, returned);
		makeWildcardType();
		return returned;
	}

	public void deleteUpperBound(GenericBound bound) {
		bound.delete();
		upperBounds.remove(bound);
		getPropertyChangeSupport().firePropertyChange("upperBounds", bound, null);
		makeWildcardType();
	}

	public List<GenericBound> getLowerBounds() {
		return lowerBounds;
	}

	public GenericBound createLowerBound() {
		GenericBound returned = new GenericBound(Object.class);
		lowerBounds.add(returned);
		getPropertyChangeSupport().firePropertyChange("lowerBounds", null, returned);
		makeWildcardType();
		return returned;
	}

	public void deleteLowerBound(GenericBound bound) {
		bound.delete();
		lowerBounds.remove(bound);
		getPropertyChangeSupport().firePropertyChange("lowerBounds", bound, null);
		makeWildcardType();
	}

	private void updateGenericParameters(Class<?> baseClass, Type actualType) {
		if (baseClass == null || baseClass.getTypeParameters().length == 0) {
			genericParameters.clear();
		}
		else {
			List<GenericParameter> genericParametersToRemove = new ArrayList<>(genericParameters);
			int index = 0;
			for (TypeVariable<?> tv : baseClass.getTypeParameters()) {
				GenericParameter foundGenericParameter = null;
				for (GenericParameter gp : genericParameters) {
					if (gp.getTypeVariable() == tv) {
						foundGenericParameter = gp;
						genericParametersToRemove.remove(foundGenericParameter);
						break;
					}
				}
				if (foundGenericParameter == null) {
					if (actualType != null) {
						genericParameters.add(new GenericParameter(tv, TypeUtils.getTypeArgument(actualType, baseClass, index)));
					}
					else {
						genericParameters.add(new GenericParameter(tv));
					}
				}
				index++;
			}
			for (GenericParameter gpToRemove : genericParametersToRemove) {
				genericParameters.remove(gpToRemove);
			}
		}

		getPropertyChangeSupport().firePropertyChange("genericParameters", null, genericParameters);
		getPropertyChangeSupport().firePropertyChange("hasGenericParameters", false, true);
	}

	public Class<?> getBaseClass() {
		if (choice == JAVA_LIST) {
			if (getEditedObject() instanceof ParameterizedType
					&& ((ParameterizedType) getEditedObject()).getActualTypeArguments().length > 0) {
				return TypeUtils.getBaseClass(((ParameterizedType) getEditedObject()).getActualTypeArguments()[0]);
			}
			return Object.class;
		}
		if (choice == JAVA_MAP) {
			if (getEditedObject() instanceof ParameterizedType
					&& ((ParameterizedType) getEditedObject()).getActualTypeArguments().length > 1) {
				return TypeUtils.getBaseClass(((ParameterizedType) getEditedObject()).getActualTypeArguments()[1]);
			}
			return Object.class;
		}
		return TypeUtils.getBaseClass(getEditedObject());
	}

	private ParameterizedType makeParameterizedType(Class<?> baseClass) {
		Type[] params = new Type[genericParameters.size()];
		for (int i = 0; i < genericParameters.size(); i++) {
			params[i] = genericParameters.get(i).getType();
		}
		return new ParameterizedTypeImpl(baseClass, params);
	}

	public void setBaseClass(Class<?> baseClass) {

		if (baseClass != getBaseClass()) {

			updateGenericParameters(baseClass, null);

			if (choice == JAVA_LIST) {
				if (hasGenericParameters()) {
					setEditedObject(new ParameterizedTypeImpl((List.class), makeParameterizedType(baseClass)));
				}
				else {
					setEditedObject(new ParameterizedTypeImpl((List.class), baseClass));
				}
			}
			else if (choice == JAVA_MAP) {
				if (hasGenericParameters()) {
					setEditedObject(new ParameterizedTypeImpl((Map.class), new Type[] { getKeyType(), makeParameterizedType(baseClass) }));
				}
				else {
					setEditedObject(new ParameterizedTypeImpl((Map.class), new Type[] { getKeyType(), baseClass }));
				}
			}
			else if (choice == JAVA_ARRAY) {
				if (hasGenericParameters()) {
					setEditedObject(new GenericArrayTypeImpl(makeParameterizedType(baseClass)));
				}
				else {
					setEditedObject(new GenericArrayTypeImpl(baseClass));
				}
			}
			else if (choice == JAVA_WILDCARD) {
				makeWildcardType();
			}
			else {

				if (hasGenericParameters()) {
					setEditedObject(makeParameterizedType(baseClass));
				}
				else {
					setEditedObject(baseClass);
				}
			}
		}
	}

	private void updateEditedType() {
		setBaseClass(getBaseClass());
	}

	private void makeWildcardType() {
		Type[] upper = new Type[upperBounds.size()];
		for (int i = 0; i < upperBounds.size(); i++)
			upper[i] = upperBounds.get(i).getType();
		Type[] lower = new Type[lowerBounds.size()];
		for (int i = 0; i < lowerBounds.size(); i++)
			lower[i] = lowerBounds.get(i).getType();
		setEditedObject(new WilcardTypeImpl(upper, lower));
	}

	private void updateWildcardBounds() {

		if (getEditedObject() instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) getEditedObject();
			upperBounds.clear();
			lowerBounds.clear();

			for (Type t : wildcardType.getUpperBounds()) {
				GenericBound newUpperBound = new GenericBound(t);
				upperBounds.add(newUpperBound);
			}
			for (Type t : wildcardType.getLowerBounds()) {
				GenericBound newLowerBound = new GenericBound(t);
				lowerBounds.add(newLowerBound);
			}

			getPropertyChangeSupport().firePropertyChange("upperBounds", null, getUpperBounds());
			getPropertyChangeSupport().firePropertyChange("lowerBounds", null, getLowerBounds());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("propertyChange with " + evt);

		// Do not propagate evt if widget is beeing updating
		if (widgetIsBeeingUpdating) {
			return;
		}

		if (isCustomType() && evt.getSource() == getCurrentCustomTypeFactory()) {
			// propertyChanged in CustomTypeFactory, regenerate new Type
			setEditedObject(getCurrentCustomTypeFactory().makeCustomType(null));
		}
		if (evt.getSource() == classEditor) {
			if (evt.getPropertyName().equals("selectedClassInfo")) {
				if (isJavaType()) {
					ClassInfo classInfo = (ClassInfo) evt.getNewValue();
					if (classInfo != null) {
						setBaseClass(classInfo.getClazz());
					}
				}
			}
		}
	}

	private ClassEditor classEditor = null;

	public ClassEditor getClassEditor() {
		if (classEditor == null) {
			classEditor = new ClassEditor();
			classEditor.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
		return classEditor;
	}

	@Override
	public void delete() {
		if (classEditor != null) {
			classEditor.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		classEditor.delete();
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
			_selectorPanel = null;
		}

	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(Type oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = oldValue;
		}
		else {
			_revertValue = null;
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public Type getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(Type editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected TypeSelectorDetailsPanel makeCustomPanel(Type editedObject) {
		return new TypeSelectorDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Type editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	public class TypeSelectorDetailsPanel extends ResizablePanel {
		private final FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected TypeSelectorDetailsPanel(Type editedObject) {
			super();

			fibComponent = ApplicationFIBLibraryImpl.instance().retrieveFIBComponent(FIB_FILE_NAME, true);
			controller = new CustomFIBController(fibComponent);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, null, true);

			controller.setDataObject(TypeSelector.this);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

			fireEditedObjectChanged();
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			controller = null;
			fibView = null;
		}

		public CustomFIBController getController() {
			return controller;
		}

		public void update() {
			controller.setDataObject(TypeSelector.this);
			// fireEditedObjectChanged();
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component) {
				super(component, SwingViewFactory.INSTANCE);
			}

			public void apply() {
				// setEditedObject(LoadedClassesInfo.instance().getSelectedClassInfo().getRepresentedClass());
				TypeSelector.this.apply();
			}

			public void cancel() {
				TypeSelector.this.cancel();
			}

			public void reset() {
				setEditedObject(null);
				TypeSelector.this.apply();
			}

			public void classChanged() {
				// System.out.println("Class changed !!!");
			}

		}

	}

	@Override
	public void apply() {

		if (isCustomType()) {
			System.out.println("On fait apply dans TypeSelector");
			Type newType = getCurrentCustomTypeEditor().getEditedType();
			System.out.println("newType=" + newType);
			setEditedObject(newType);
		}

		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	public TypeSelectorDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	public Class<Type> getRepresentedType() {
		return Type.class;
	}

	@Override
	public String renderedString(Type editedObject) {

		if (editedObject == null) {
			return "";
		}
		return TypeUtils.simpleRepresentation(editedObject);
	}

	public Resource getJavaClassEditorComponentResource() {
		return JAVA_CLASS_EDITOR_FIB_FILE_NAME;
	}

	public class GenericParameter extends PropertyChangedSupportDefaultImplementation {
		private TypeVariable<?> typeVariable;
		private Type type;

		public GenericParameter(TypeVariable<?> typeVariable, Type type) {
			super();
			this.typeVariable = typeVariable;
			this.type = type;
		}

		public GenericParameter(TypeVariable<?> typeVariable) {
			super();
			this.typeVariable = typeVariable;
		}

		public void delete() {
			this.typeVariable = null;
			this.type = null;
		}

		public TypeVariable<?> getTypeVariable() {
			return typeVariable;
		}

		public void setTypeVariable(TypeVariable<?> typeVariable) {
			if (typeVariable != this.typeVariable) {
				TypeVariable<?> oldValue = this.typeVariable;
				this.typeVariable = typeVariable;
				getPropertyChangeSupport().firePropertyChange("typeVariable", oldValue, typeVariable);
			}
		}

		public Type getType() {
			if (type == null) {
				return getUpperBound();
			}
			return type;
		}

		public void setType(Type type) {

			if ((type == null && this.type != null) || (type != null && !type.equals(this.type))) {
				Type oldValue = this.type;
				this.type = type;

				setEditedObject(makeParameterizedType(getBaseClass()));

				getPropertyChangeSupport().firePropertyChange("type", oldValue, type);
			}
		}

		@Override
		public String toString() {
			return typeVariable.getName() + "=" + TypeUtils.fullQualifiedRepresentation(type);
		}

		public ImageIcon getIcon() {
			return isValid() ? UtilsIconLibrary.OK_ICON : UtilsIconLibrary.ERROR_ICON;
		}

		public boolean isValid() {
			return (TypeUtils.isTypeAssignableFrom(getUpperBound(), getType()));
		}

		public Type getUpperBound() {
			if (getTypeVariable() != null && getTypeVariable().getBounds().length > 0) {
				return getTypeVariable().getBounds()[0];
			}
			return Object.class;
		}

		public String getTypeStringRepresentation() {
			return TypeUtils.simpleRepresentation(getType());
		}
	}

	public class GenericBound extends PropertyChangedSupportDefaultImplementation {
		private Type type;

		public GenericBound(Type type) {
			super();
			this.type = type;
		}

		public GenericBound() {
			this(null);
		}

		public void delete() {
			this.type = null;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {

			if ((type == null && this.type != null) || (type != null && !type.equals(this.type))) {
				Type oldValue = this.type;
				this.type = type;

				makeWildcardType();

				getPropertyChangeSupport().firePropertyChange("type", oldValue, type);
			}
		}

		@Override
		public String toString() {
			return TypeUtils.fullQualifiedRepresentation(type);
		}

		public ImageIcon getIcon() {
			return isValid() ? UtilsIconLibrary.OK_ICON : UtilsIconLibrary.ERROR_ICON;
		}

		public boolean isValid() {
			return true;
		}

		public String getTypeStringRepresentation() {
			return TypeUtils.simpleRepresentation(getType());
		}
	}

	/**
	 * This main allows to launch an application testing the TypeSelector
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SecurityException, IOException {

		Resource loggingFile = ResourceLocator.locateResource("Config/logging_INFO.properties");
		FlexoLoggingManager.initialize(-1, true, loggingFile, Level.INFO, null);
		final JDialog dialog = new JDialog((Frame) null, false);

		Type typeToEdit = String.class;
		// Type typeToEdit = new WilcardTypeImpl(String.class);

		final TypeSelector typeSelector = new TypeSelector(typeToEdit);
		typeSelector.setRevertValue(Object.class);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				typeSelector.delete();
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), dialog);
			}
		});

		JPanel panel = new JPanel(new VerticalLayout());
		panel.add(typeSelector);

		panel.add(closeButton);
		panel.add(logButton);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.pack();

		dialog.setVisible(true);
	}
}

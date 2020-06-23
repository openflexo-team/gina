/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.inspector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.FIBProperty;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.factory.CloneableProxyObject;
import org.openflexo.pamela.factory.KeyValueCoding;
import org.openflexo.pamela.undo.CompoundEdit;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Abstraction of a container synchronized with and reflecting a selection<br>
 * <ul>
 * <li>If selection is empty, then manage a default value (this value will be used to build new objects)</li>
 * <li>If selection is unique, manage value of unique selection</li>
 * <li>If selection is multiple, manage value of entire selection, by batch</li>
 * </ul>
 * 
 * @author sylvain
 * 
 * @param <S>
 */
public abstract class InspectedProperties<S extends KeyValueCoding> implements HasPropertyChangeSupport, PropertyChangeListener {

	private static final Logger logger = FlexoLogger.getLogger(InspectedProperties.class.getPackage().getName());

	private final FIBEditorController controller;
	private S defaultValue;

	private final PropertyChangeSupport pcSupport;

	private boolean isDeleted = false;

	private boolean shouldBeUpdated = true;

	protected InspectedProperties(FIBEditorController controller, S defaultValue) {
		this.controller = controller;
		this.defaultValue = defaultValue;
		pcSupport = new PropertyChangeSupport(this);
	}

	public FIBEditorController getController() {
		return controller;
	}

	protected Map<FIBProperty<?>, Object> storedPropertyValues = new HashMap<>();

	/**
	 * Return property value matching supplied parameter for current selection<br>
	 * <ul>
	 * <li>If selection is empty, then return default value (this value will be used to build new objects)</li>
	 * <li>If selection is unique, then return the right value</li>
	 * <li>If selection is multiple, return value of first selected object</li>
	 * </ul>
	 * Store the result for future comparison evaluations
	 * 
	 * @param property
	 * @return
	 */
	public <T> T getPropertyValue(FIBProperty<T> property) {
		T returned = _getPropertyValue(property);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Requesting " + property + " for " + getSelection() + ", returning " + returned);
		}
		storedPropertyValues.put(property, returned);
		return returned;
	}

	/**
	 * Return property value matching supplied parameter for current selection<br>
	 * Do not store the result for future comparison evaluations
	 * 
	 * @param property
	 * @return
	 */
	protected <T> T _getPropertyValue(FIBProperty<T> property) {
		T returned;
		if (getSelection().size() == 0) {
			if (defaultValue != null && defaultValue.hasKey(property.getName())) {
				returned = (T) defaultValue.objectForKey(property.getName());
			}
			else {
				returned = null;
			}
		}
		else {
			S style = getStyle(getSelection().get(0));
			if (style != null && style.hasKey(property.getName())) {
				returned = (T) style.objectForKey(property.getName());
			}
			else {
				/*if (style != null) {
					System.out.println("OK, j'ai bien un " + style.getClass().getSimpleName() + " mais c'est dur de lui appliquer "
							+ parameter);
					System.out.println("parameter.getDeclaringClass()=" + parameter.getDeclaringClass());
					System.out.println("style.getClass()=" + style.getClass());
				}*/
				returned = null;
			}
		}
		if (property.getType() != null && property.getType().isPrimitive() && returned == null) {
			return property.getDefaultValue();
		}
		return returned;
	}

	/**
	 * Sets property value matching supplied parameter for current selection, with supplied value<br>
	 * <ul>
	 * <li>If selection is empty, then sets default value (this value will be used to build new objects)</li>
	 * <li>If selection is unique, then sets the right value</li>
	 * <li>If selection is multiple, sets value of entire selection</li>
	 * </ul>
	 * Store the result for future comparison evaluations
	 * 
	 * @param parameter
	 * @param value
	 */
	public <T> void setPropertyValue(FIBProperty<T> parameter, T value) {
		T oldValue = getPropertyValue(parameter);
		// System.out.println("Sets from " + oldValue + " to " + value);
		if (requireChange(oldValue, value)) {
			if (getSelection().size() == 0) {
				if (defaultValue == null) {
					logger.warning("Cannot set " + parameter + " to " + value + " : no default value defined for " + this);
					return;
				}
				defaultValue.setObjectForKey(value, parameter.getName());
			}
			else {
				CompoundEdit setValueEdit = startRecordEdit("Set " + parameter.getName() + " to " + value);
				for (FIBModelObject n : getSelection()) {
					S style = getStyle(n);
					// System.out.println("For " + n + " use " + style + " and sets value of " + parameter.getName() + " with " + value);
					if (style != null) {
						style.setObjectForKey(value, parameter.getName());
					}
				}
				stopRecordEdit(setValueEdit);
			}
			storedPropertyValues.put(parameter, value);
			pcSupport.firePropertyChange(parameter.getName(), oldValue, value);
		}
	}

	protected CompoundEdit startRecordEdit(String editName) {
		if (getController().getUndoManager() != null && !getController().getUndoManager().isUndoInProgress()
				&& !getController().getUndoManager().isRedoInProgress()) {
			return getController().getUndoManager().startRecording(editName);
		}
		return null;
	}

	protected void stopRecordEdit(CompoundEdit edit) {
		if (edit != null && getController().getUndoManager() != null) {
			getController().getUndoManager().stopRecording(edit);
		}
	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	protected boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			}
			else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

	/**
	 * Abstract method returning sub-selection on which inspected style may apply (return all objects relevant for such style)
	 * 
	 * @return
	 */
	public abstract List<? extends FIBModelObject> getSelection();

	/**
	 * Return relevant style for a given {@link FIBModelObject}
	 * 
	 * @param node
	 * @return
	 */
	public abstract S getStyle(FIBModelObject object);

	/**
	 * Return value identified as default values (values that are used when selection is empty)
	 * 
	 * @return
	 */
	public S getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets value identified as default values (values that are used when selection is empty)
	 * 
	 * @param defaultValue
	 */
	public void setDefaultValue(S defaultValue) {
		this.defaultValue = defaultValue;
	}

	public FIBModelFactory getFactory() {
		// Not relevant
		return null;
	}

	/**
	 * Generate new style using supplied factory and inspected property values
	 * 
	 * @param factory
	 * @return
	 */
	public S cloneStyle() {
		if (defaultValue instanceof CloneableProxyObject) {
			if (getFactory() != null && getFactory().getEditingContext() != null
					&& getFactory().getEditingContext().getUndoManager() != null) {
				getFactory().getEditingContext().getUndoManager().enableAnticipatedRecording();
			}
			S returned = (S) ((CloneableProxyObject) defaultValue).cloneObject();
			if (getFactory() != null && getFactory().getEditingContext() != null
					&& getFactory().getEditingContext().getUndoManager() != null) {
				getFactory().getEditingContext().getUndoManager().disableAnticipatedRecording();
			}
			return returned;
		}
		logger.warning("Could not clone " + defaultValue);
		return defaultValue;
	}

	private final List<S> inspectedStyles = new ArrayList<>();

	/**
	 * Called to "tell" inspected style that the selection has changed and then resulting inspected style might be updated<br>
	 * 
	 */
	public void fireSelectionUpdated() {

		update();
	}

	/**
	 * Called to update inspected style<br>
	 * 
	 */
	public void update() {

		// We first unregister all existing observing scheme
		for (S s : inspectedStyles) {
			if (s instanceof HasPropertyChangeSupport && ((HasPropertyChangeSupport) s).getPropertyChangeSupport() != null) {
				((HasPropertyChangeSupport) s).getPropertyChangeSupport().removePropertyChangeListener(this);
			} /* else if (s instanceof Observable) {
				((Observable) s).deleteObserver(this);
				}*/
		}
		inspectedStyles.clear();

		// Then, we observe all styles being selected
		for (FIBModelObject n : getSelection()) {
			S s = getStyle(n);
			if (s instanceof HasPropertyChangeSupport) {
				inspectedStyles.add(s);
				// System.out.println("!!!!!!!!!!!!!!! Observing " + s + " for " + n);
				if (((HasPropertyChangeSupport) s).getPropertyChangeSupport() != null) {
					((HasPropertyChangeSupport) s).getPropertyChangeSupport().addPropertyChangeListener(this);
				}
			} /* else if (s instanceof Observable) {
				inspectedStyles.add(s);
				((Observable) s).addObserver(this);
				}*/
		}

		// Then we look if some properties have changed due to new selection
		fireChangedProperties();
	}

	/**
	 * Internally called to fire change events between previously registered values and current resulting values
	 */
	protected void fireChangedProperties() {

		if (getInspectedStyleClass() != null) {
			for (FIBProperty<?> p : FIBProperty.getFIBProperties(getInspectedStyleClass())) {
				fireChangedProperty(p);
			}
		}
	}

	protected Class<? extends S> getInspectedStyleClass() {
		return (Class<? extends S>) TypeUtils.getBaseClass(TypeUtils.getTypeArgument(getClass(), InspectedProperties.class, 0));
	}

	/**
	 * Internally called to fire change events between previously registered values and current resulting values<br>
	 * Do this only when needed on supplied FIBProperty
	 */
	protected <T> void fireChangedProperty(FIBProperty<T> p) {
		@SuppressWarnings("unchecked")
		T storedValue = (T) storedPropertyValues.get(p);
		T newValue = _getPropertyValue(p);
		if (requireChange(storedValue, newValue)) {
			_doFireChangedProperty(p, storedValue, newValue);
		}
	}

	protected <T> void _doFireChangedProperty(FIBProperty<T> p, T oldValue, T newValue) {
		pcSupport.firePropertyChange(p.getName(), oldValue, newValue);
		// System.out.println("fired changed property " + p + " from " + oldValue + " to " + newValue);
		// setChanged();
	}

	protected <T> void forceFireChangedProperty(FIBProperty<T> p) {
		@SuppressWarnings("unchecked")
		T storedValue = (T) storedPropertyValues.get(p);
		T newValue = _getPropertyValue(p);
		if (requireChange(storedValue, newValue)) {
			_doFireChangedProperty(p, storedValue, newValue);
		}
		else { // otherwise, we force it
			pcSupport.firePropertyChange(p.getName(), null, newValue);
			// setChanged();
		}

	}

	/**
	 * Called when a style composing current selection has changed a property.<br>
	 * We just call #fireChangedProperties()
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("****************** PropertyChange with " + evt + " property=" + evt.getPropertyName());
		if (shouldBeUpdated) {
			fireChangedProperties();
		}
	}

	public boolean shouldBeUpdated() {
		return shouldBeUpdated;
	}

	public void setShouldBeUpdated(boolean shouldBeUpdated) {
		this.shouldBeUpdated = shouldBeUpdated;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void destroy() {
		logger.warning("destroy() not implemented yet");
	}

	public boolean delete() {
		// TODO: implement this
		logger.warning("Delete() not implemented yet");
		isDeleted = true;
		return true;
	}

	public boolean undelete(boolean restoreProperties) {
		// TODO: implement this
		logger.warning("Undelete() not implemented yet");
		isDeleted = false;
		return true;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public String getDeletedProperty() {
		// Not relevant
		return null;
	}

	public <T> void notifyChange(FIBProperty<T> parameter, T oldValue, T newValue) {
		if (requireChange(oldValue, newValue)) {
			if (getSelection().size() == 0) {
				if (defaultValue instanceof HasPropertyChangeSupport) {
					((HasPropertyChangeSupport) defaultValue).getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue,
							newValue);
				}
			}
			else {
				for (FIBModelObject n : getSelection()) {
					S style = getStyle(n);
					if (style instanceof HasPropertyChangeSupport) {
						((HasPropertyChangeSupport) style).getPropertyChangeSupport().firePropertyChange(parameter.getName(), oldValue,
								newValue);
					}
				}
			}
		}
	}

	public <T> void notifyChange(FIBProperty<T> parameter) {
		T currentValue = getPropertyValue(parameter);
		notifyChange(parameter, currentValue != null ? null : parameter.getDefaultValue(), currentValue);
	}

	public <T> void notifyAttributeChange(FIBProperty<T> parameter) {
		notifyChange(parameter);
	}

	public Object performSuperGetter(String propertyIdentifier) {
		// Not relevant
		return null;
	}

	public void performSuperSetter(String propertyIdentifier, Object value) {
		// Not relevant

	}

	public void performSuperAdder(String propertyIdentifier, Object value) {
		// Not relevant

	}

	public void performSuperAdder(String propertyIdentifier, Object value, int index) {
		// Not relevant

	}

	public void performSuperRemover(String propertyIdentifier, Object value) {
		// Not relevant

	}

	public boolean performSuperDelete() {
		// Not relevant
		return false;
	}

	public boolean performSuperDelete(Object... context) {
		// Not relevant
		return false;
	}

	public boolean performSuperUndelete(boolean restoreProperties) {
		// Not relevant
		return false;
	}

	public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
		// Not relevant
		return null;
	}

	public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperDelete(Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
		// Not relevant

	}

	public void performSuperSetModified(boolean modified) {
		// Not relevant

	}

	public Object performSuperFinder(String finderIdentifier, Object value) {
		// Not relevant
		return null;
	}

	public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant
		return null;
	}

	public boolean delete(Object... context) {
		// Not relevant
		return false;
	}

	public boolean isSerializing() {
		// Not relevant
		return false;
	}

	public boolean isDeserializing() {
		// Not relevant
		return false;
	}

	public boolean isModified() {
		// Not relevant
		return false;
	}

	public void setModified(boolean modified) {
		// Not relevant

	}

	public boolean equalsObject(Object obj) {
		// Not relevant
		return false;
	}

	public Object cloneObject() {
		// Not relevant
		return null;
	}

	public Object cloneObject(Object... context) {
		// Not relevant
		return null;
	}

	public boolean isCreatedByCloning() {
		// Not relevant
		return false;
	}

	public boolean isBeingCloned() {
		// Not relevant
		return false;
	}

	@Override
	public Object clone() {
		// Not relevant
		return this;
	}

	public boolean hasKey(String key) {
		if (key != null) {
			FIBProperty<?> parameter = FIBProperty.getFIBProperty(getInspectedStyleClass(), key);
			if ((parameter != null) && (getPropertyValue(parameter) != null)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return object matching supplied key, if this object responses to this key
	 * 
	 * @param key
	 * @return
	 */
	public Object objectForKey(String key) {
		FIBProperty<?> parameter = FIBProperty.getFIBProperty(getInspectedStyleClass(), key);
		if (parameter != null) {
			return getPropertyValue(parameter);
		}
		return null;
	}

	/**
	 * Sets an object matching supplied key, if this object responses to this key
	 * 
	 * @param key
	 * @return
	 */
	public void setObjectForKey(Object value, String key) {
		FIBProperty<?> parameter = FIBProperty.getFIBProperty(getInspectedStyleClass(), key);
		Object oldValue = objectForKey(key);
		if (requireChange(oldValue, value)) {
			if (getSelection().size() == 0) {
				defaultValue.setObjectForKey(value, key);
			}
			else {
				CompoundEdit setValueEdit = startRecordEdit("Set " + key + " to " + value);
				for (FIBModelObject n : getSelection()) {
					S style = getStyle(n);
					if (style != null) {
						style.setObjectForKey(value, key);
					}
				}
				stopRecordEdit(setValueEdit);
			}
			storedPropertyValues.put(parameter, value);
			pcSupport.firePropertyChange(key, oldValue, value);
		}
	}

	/**
	 * Return type of key/value pair identified by supplied key identifier
	 * 
	 * @param key
	 * @return
	 */
	public Type getTypeForKey(String key) {
		if (hasKey(key)) {
			if (FIBProperty.getFIBProperty(getInspectedStyleClass(), key) != null) {
				return FIBProperty.getFIBProperty(getInspectedStyleClass(), key).getType();
			}
		}
		return null;
	}

}

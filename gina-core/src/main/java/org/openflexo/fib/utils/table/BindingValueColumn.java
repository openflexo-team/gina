/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.utils.table;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.utils.BindingSelector;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class BindingValueColumn<D> extends CustomColumn<D, DataBinding> {

	protected static final Logger logger = Logger.getLogger(BindingValueColumn.class.getPackage().getName());

	private Map<DataBinding, BindingSelector> viewSelectors;

	private Map<DataBinding, BindingSelector> editSelectors;

	public BindingValueColumn(String title, int defaultWidth, boolean allowsCompoundBinding) {
		super(title, defaultWidth);
	}

	public abstract boolean allowsCompoundBinding(DataBinding<?> value);

	public abstract boolean allowsNewEntryCreation(DataBinding<?> value);

	@Override
	public Class<DataBinding> getValueClass() {
		return DataBinding.class;
	}

	private void updateSelectorWith(BindingSelector selector, D rowObject, DataBinding<?> value) {
		DataBinding oldBV = selector.getEditedObject();
		// if (oldBV == null || !oldBV.equals(value)) {
		if (oldBV != value) {
			// logger.info("updateSelectorWith value=" + (value != null ? value + " (" + value.getBindingName() + ")" : "null"));
			// selector.setEditedObjectAndUpdateBDAndOwner(value);
			selector.setEditedObject(value);
			selector.setRevertValue(value != null ? value.clone() : null);
			/*if (allowsNewEntryCreation(value)) {
			    selector.allowsNewEntryCreation();
			}
			else {
			    selector.denyNewEntryCreation();
			}*/
			selector.setBindable(getBindableFor(value, rowObject));
			// selector.setBindingDefinition(getBindingDefinitionFor(value, rowObject));
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Selector: " + selector.toString() + " rowObject=" + rowObject + "" + " binding=" + value
						+ " with BindingDefinition " + getBindingDefinitionFor(value, rowObject));
			}
		}
	}

	public abstract Bindable getBindableFor(DataBinding<?> value, D rowObject);

	public abstract BindingDefinition getBindingDefinitionFor(DataBinding<?> value, D rowObject);

	@Override
	protected BindingSelector getViewSelector(D rowObject, DataBinding value) {
		if (viewSelectors == null) {
			viewSelectors = new HashMap<DataBinding, BindingSelector>();
		}
		BindingSelector returned = viewSelectors.get(value);
		if (returned == null) {
			returned = new BindingSelector(value);
			returned.setFont(MEDIUM_FONT);
			updateSelectorWith(returned, rowObject, value);
			viewSelectors.put(value, returned);
		}

		System.out.println("VIEW / Pour " + rowObject + " je retourne " + returned.hashCode());

		return returned;

		/*if (_viewSelector == null) {
			_viewSelector = new BindingSelector(value) {
				@Override
				public String toString() {
					return "VIEW";
				}
			};
			_viewSelector.setFont(MEDIUM_FONT);
		}
		updateSelectorWith(_viewSelector, rowObject, value);
		return _viewSelector;*/
	}

	@Override
	protected BindingSelector getEditSelector(final D rowObject, DataBinding value) {
		if (editSelectors == null) {
			editSelectors = new HashMap<DataBinding, BindingSelector>();
		}
		BindingSelector returned = editSelectors.get(value);
		if (returned == null) {
			returned = new BindingSelector(value) {
				@Override
				public void apply() {
					super.apply();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Apply");
					}
					if (rowObject != null) {
						setValue(rowObject, getEditedObject());
						System.out.println("SET / Pour " + rowObject + " je set " + getEditedObject());
					}
				}

				/*@Override
				public void fireEditedObjectChanged() {
					System.out.println("Tiens, j'ai vu que la valeur a change pour " + getEditedObject());
					super.fireEditedObjectChanged();
					if (rowObject != null) {
						setValue(rowObject, getEditedObject());
					}
				}*/

				@Override
				public void cancel() {
					super.cancel();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Cancel");
					}
					if (rowObject != null) {
						setValue(rowObject, getRevertValue());
					}
				}
			};
			returned.setFont(MEDIUM_FONT);
			updateSelectorWith(returned, rowObject, value);
			editSelectors.put(value, returned);
		}

		System.out.println("EDIT / Pour " + rowObject + " je retourne " + returned.hashCode());

		return returned;

		/*if (_editSelector == null) {
			_editSelector = new BindingSelector(value) {
				@Override
				public void apply() {
					super.apply();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Apply");
					}
					if (_editedRowObject != null) {
						setValue(_editedRowObject, getEditedObject());
					}
				}

				@Override
				public void fireEditedObjectChanged() {
					System.out.println("Tiens, j'ai vu que la valeur a change pour " + getEditedObject());
					super.fireEditedObjectChanged();
					if (_editedRowObject != null) {
						setValue(_editedRowObject, getEditedObject());
					}
				}

				@Override
				public void cancel() {
					super.cancel();
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Cancel");
					}
					if (_editedRowObject != null) {
						setValue(_editedRowObject, getRevertValue());
					}
				}

				@Override
				public String toString() {
					return "EDIT";
				}
			};
			//_editSelector.setFont(NORMAL_FONT);
			//logger.info("Build EditSelector for " + rowObject + " value=" + value);
		}
		updateSelectorWith(_editSelector, rowObject, value);
		return _editSelector;*/
	}

}

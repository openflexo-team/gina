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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.DataBinding;
import org.openflexo.gina.swing.utils.BindingSelector;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class BindingValueColumn<D> extends CustomColumn<D, DataBinding> {

	protected static final Logger LOGGER = Logger.getLogger(BindingValueColumn.class.getPackage().getName());

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
		DataBinding<?> oldBV = selector.getEditedObject();
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
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Selector: " + selector.toString() + " rowObject=" + rowObject + "" + " binding=" + value);
			}
		}
	}

	public abstract Bindable getBindableFor(DataBinding<?> value, D rowObject);

	@Override
	protected BindingSelector getViewSelector(D rowObject, DataBinding value) {
		if (viewSelectors == null) {
			viewSelectors = new HashMap<>();
		}
		BindingSelector returned = viewSelectors.get(value);
		if (returned == null) {
			returned = new BindingSelector(value);
			returned.setFont(MEDIUM_FONT);
			updateSelectorWith(returned, rowObject, value);
			viewSelectors.put(value, returned);
		}

		return returned;

	}

	@Override
	protected BindingSelector getEditSelector(final D rowObject, DataBinding value) {
		if (editSelectors == null) {
			editSelectors = new HashMap<>();
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
					}
				}

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

		return returned;
	}

	@Override
	public void delete() {

		super.delete();

		if (viewSelectors != null) {

			for (DataBinding<?> db : viewSelectors.keySet()) {
				BindingSelector bs = viewSelectors.get(db);
				if (bs != null) {
					bs.delete();
				}
			}
			viewSelectors.clear();
		}

		if (editSelectors != null) {
			for (DataBinding<?> db : editSelectors.keySet()) {
				BindingSelector bs = editSelectors.get(db);
				if (bs != null) {
					bs.delete();
				}
			}
			editSelectors.clear();
		}

		viewSelectors = null;
		editSelectors = null;
	}

}

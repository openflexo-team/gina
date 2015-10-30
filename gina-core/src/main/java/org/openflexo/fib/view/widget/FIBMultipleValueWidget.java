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

package org.openflexo.fib.view.widget;

import java.util.List;

import org.openflexo.fib.model.FIBMultipleValues;
import org.openflexo.fib.view.FIBWidgetView;

/**
 * Generic and abstract widget allowing to handle a list of values
 * 
 * @author sylvain
 * 
 * @param <M>
 *            type of {@link FIBMultipleValues} this view represents
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of data beeing represented by this view
 * @param <I>
 *            type of iterable beeing managed by this widget
 */
public interface FIBMultipleValueWidget<M extends FIBMultipleValues, C, T, I> extends FIBWidgetView<M, C, T> {

	public static final String SELECTED = "selected";
	public static final String SELECTED_INDEX = "selectedIndex";

	public T getSelected();

	public void setSelected(T selected);

	public int getSelectedIndex();

	public void setSelectedIndex(int selectedIndex);

	@Override
	public MultipleValueRenderingTechnologyAdapter<C, I> getRenderingTechnologyAdapter();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 *            type of technology-specific component this view manage
	 */
	public static interface MultipleValueRenderingTechnologyAdapter<C, T> extends RenderingTechnologyAdapter<C> {

	}

	public static interface SingleSelectionMultipleValueRenderingTechnologyAdapter<C, T>
			extends MultipleValueRenderingTechnologyAdapter<C, T> {

		public T getSelectedItem(C component);

		public void setSelectedItem(C component, T item);

		public int getSelectedIndex(C component);

		public void setSelectedIndex(C component, int index);

	}

	public static interface MultipleSelectionMultipleValueRenderingTechnologyAdapter<C, T>
			extends MultipleValueRenderingTechnologyAdapter<C, T> {

		public List<T> getSelectedItems(C component);

		public void setSelectedItems(C component, List<T> items);

	}

}

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

package org.openflexo.gina.view.widget;

import java.util.List;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.gina.controller.FIBSelectable;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.view.FIBWidgetView;

/**
 * Represents a browser (a tree of various objects)
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <T>
 *            type of row data
 * 
 * @author sylvain
 */
public interface FIBBrowserWidget<C, T> extends FIBWidgetView<FIBBrowser, C, T>, FIBSelectable<T> {

	@Override
	public BrowserRenderingAdapter<C, T> getRenderingAdapter();

	// public FIBBrowserModel getBrowserModel();

	public void updateBrowser();

	public void performSelect(T object, boolean force);

	public void performSelect(List<T> objects, boolean force);

	public void performExpand(T o);

	public boolean isEnabled();

	public boolean isLastFocusedSelectable();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface BrowserRenderingAdapter<C, T> extends RenderingAdapter<C> {

		public int getVisibleRowCount(C component);

		public void setVisibleRowCount(C component, int visibleRowCount);

		public int getRowHeight(C component);

		public void setRowHeight(C component, int rowHeight);

		public boolean isEditing(C component);

		public void cancelCellEditing(C component);

		public TreeSelectionModel getTreeSelectionModel(C component);

		public boolean isExpanded(C component, TreePath treePath);

	}

}

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

package org.openflexo.gina.view;

import java.util.Collection;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;

/**
 * Represent the "view" associated with a {@link FIBContainer} in a given rendering engine environment (eg Swing)<br>
 * A {@link FIBContainerView} is a container for some sub-components (a set of {@link FIBView}) with a given layout
 * 
 * A default implementation is provided in this library, see {@link FIBContainerViewImpl}
 * 
 * @author sylvain
 *
 * @param <M>
 *            type of {@link FIBComponent} this view represents
 * @param <C>
 *            type of technology-specific component
 * @param <C2>
 *            type of technology-specific component beeing contained by this view
 */
public interface FIBContainerView<M extends FIBContainer, C, C2> extends FIBView<M, C> {

	public Collection<? extends FIBView<?, C2>> getSubViews();

	public void changeLayout();

	public void updateLayout();

	public void invalidateAndUpdateLayoutLater();

	@Override
	public ContainerRenderingAdapter<C, C2> getRenderingAdapter();

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface ContainerRenderingAdapter<C, C2> extends RenderingAdapter<C> {

		// public void addComponent(C2 child, C parent, Object constraints);
	}

}

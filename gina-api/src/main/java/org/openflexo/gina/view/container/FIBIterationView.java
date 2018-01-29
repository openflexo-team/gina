/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

package org.openflexo.gina.view.container;

import java.awt.Image;
import java.util.Map;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBIteration;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBView;

/**
 * Represents an iteration
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * @param <C2>
 *            type of technology-specific component this view contains
 * 
 * @author sylvain
 */
public interface FIBIterationView<C, C2> extends FIBContainerView<FIBIteration, C, C2> {

	@Override
	public IterationRenderingAdapter<C, C2> getRenderingAdapter();

	public IteratedContents<?> getIteratedContents(FIBView<?, ?> view);

	public Map<Object, IteratedContents<?>> getIteratedSubViewsMap();

	public interface IteratedContents<I> extends BindingEvaluationContext {
		public I getIteratedValue();

		public Map<FIBComponent, ? extends FIBView<?, ?>> getSubViewsMap();

		public boolean containsView(FIBView<?, ?> view);
	}

	/**
	 * Specification of an adapter for a given rendering technology (eg Swing)
	 * 
	 * @author sylvain
	 *
	 * @param <C>
	 */
	public static interface IterationRenderingAdapter<C, C2> extends ContainerRenderingAdapter<C, C2> {

		public Image getBackgroundImage(C component, FIBIterationView<C, C2> panelView);

		public void setBackgroundImage(C component, Image anImage, FIBIterationView<C, C2> panelView);

	}

}

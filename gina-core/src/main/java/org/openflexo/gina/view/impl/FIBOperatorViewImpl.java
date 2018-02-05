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

package org.openflexo.gina.view.impl;

import java.util.logging.Logger;

import org.openflexo.connie.binding.SettableBindingEvaluationContext;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBOperator;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBOperatorView;

/**
 * Default generic (and abstract) implementation for a "view" associated with a {@link FIBOperator} in a given rendering engine environment
 * (eg Swing)<br>
 * A {@link FIBOperatorView} allows to access run-time (execution) context of underlying operator
 * 
 * @author sylvain
 *
 * @param <M>
 *            type of {@link FIBOperator} this view represents
 * @param <C>
 *            type of technology-specific component
 * @param <C2>
 *            type of technology-specific component beeing contained by this view
 */
public abstract class FIBOperatorViewImpl<M extends FIBOperator, C, C2> extends FIBContainerViewImpl<M, C, C2>
		implements FIBOperatorView<M, C, C2>, SettableBindingEvaluationContext {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(FIBOperatorViewImpl.class.getPackage().getName());

	public FIBOperatorViewImpl(M model, FIBController controller) {
		super(model, controller, null);
	}

	/**
	 * Returns the first parent view which is not an operator view
	 * 
	 * @return
	 */
	@Override
	public final FIBContainerView<?, ?, ?> getConcreteContainerView() {
		FIBContainerView<?, ?, ?> current = getParentView();
		while (current != null) {
			if (!(current instanceof FIBOperatorView)) {
				return current;
			}
			current = current.getParentView();
		}
		return null;
	}

	@Override
	public FIBLayoutManager<C, C2, ?> getLayoutManager() {
		if (getConcreteContainerView() != null) {
			return (FIBLayoutManager<C, C2, ?>) getConcreteContainerView().getLayoutManager();
		}
		return null;
	}

}

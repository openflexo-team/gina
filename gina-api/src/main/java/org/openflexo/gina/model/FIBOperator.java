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

import org.openflexo.gina.model.container.FIBPanel.Layout;
import org.openflexo.gina.model.container.layout.ComponentConstraints;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represent a control operator in GINA model
 * 
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FIBOperator.FIBOperatorImpl.class)
public abstract interface FIBOperator extends FIBContainer {

	/**
	 * Returns the first parent which is not an operator
	 * 
	 * @return
	 */
	public FIBContainer getConcreteContainer();

	public static abstract class FIBOperatorImpl extends FIBContainerImpl implements FIBOperator {

		/**
		 * Returns the first parent view which is not an operator view
		 * 
		 * @return
		 */
		@Override
		public final FIBContainer getConcreteContainer() {
			FIBContainer current = getParent();
			while (current != null) {
				if (!(current instanceof FIBOperator)) {
					return current;
				}
				current = current.getParent();
			}
			return null;
		}

		@Override
		public Layout getLayout() {
			if (getConcreteContainer() != null) {
				return getConcreteContainer().getLayout();
			}
			return super.getLayout();
		}

		@Override
		public ComponentConstraints getConstraints() {
			return null;
		}

		@Override
		public boolean isOperator() {
			return true;
		}

	}
}

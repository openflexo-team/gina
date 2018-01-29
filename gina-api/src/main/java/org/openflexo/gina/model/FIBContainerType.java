/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.gina.model.bindings.DynamicPropertyPathElement;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBContainerView} representing a given {@link FIBContainer}<br>
 * Note that to be able to expose dynamic properties, this class should be extended.
 * 
 * @author sylvain
 * 
 */
public class FIBContainerType<C extends FIBContainer> extends FIBViewType<C> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBContainerType.class.getPackage().getName());

	protected final Map<IBindingPathElement, List<DynamicPropertyPathElement<? super C>>> pathElements;

	public FIBContainerType(C aWidget) {
		super(aWidget);
		pathElements = new HashMap<>();
	}

	@Override
	public Class<? extends FIBContainerView> getBaseClass() {
		return FIBContainerView.class;
	}

	@Override
	public List<DynamicPropertyPathElement<? super C>> getAccessibleSimplePathElements(IBindingPathElement parent) {

		if (parent != null && parent.getType() instanceof FIBContainerType) {

			List<DynamicPropertyPathElement<? super C>> returned = pathElements.get(parent);
			if (returned == null) {
				returned = new ArrayList<>();
				for (DynamicProperty p : getDynamicProperties()) {
					returned.add(new DynamicPropertyPathElement<FIBContainer>(parent, getFIBComponent(), p));
				}
				pathElements.put(parent, returned);
			}
			return returned;
		}

		return Collections.emptyList();
	}

}

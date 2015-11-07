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

import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.gina.model.bindings.WidgetPathElement;
import org.openflexo.gina.model.bindings.WidgetValuePathElement;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBWidgetView} representing a given {@link FIBWidget}<br>
 * Note that to be able to expose dynamic properties, this class should be extended.
 * 
 * @author sylvain
 * 
 */
public class FIBWidgetType<W extends FIBWidget> extends FIBViewType<W> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBWidgetType.class.getPackage().getName());

	protected final Map<BindingPathElement, List<WidgetPathElement<? super W>>> pathElements;

	public FIBWidgetType(W aWidget) {
		super(aWidget);
		pathElements = new HashMap<BindingPathElement, List<WidgetPathElement<? super W>>>();
	}

	@Override
	public Class<? extends FIBWidgetView> getBaseClass() {
		return FIBWidgetView.class;
	}

	@Override
	public List<WidgetPathElement<? super W>> getAccessibleSimplePathElements(BindingPathElement parent) {

		if (parent != null && parent.getType() instanceof FIBWidgetType) {

			List<WidgetPathElement<? super W>> returned = pathElements.get(parent);
			if (returned == null) {
				returned = new ArrayList<>();
				returned.add(new WidgetValuePathElement(parent, getFIBComponent()));
				pathElements.put(parent, returned);
			}
			return returned;
		}

		return Collections.emptyList();
	}

}

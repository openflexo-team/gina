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

package org.openflexo.gina.model.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.gina.model.FIBWidgetType;
import org.openflexo.gina.model.bindings.TableSelectedPathElement;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a FlexoConceptInstance of a given FlexoConcept
 * 
 * @author sylvain
 * 
 */
public class FIBTableType extends FIBWidgetType<FIBTable> {

	protected static final Logger logger = FlexoLogger.getLogger(FIBTableType.class.getPackage().getName());

	private final Map<BindingPathElement, List<SimplePathElement>> pathElements;

	public FIBTableType(FIBTable aWidget) {
		super(aWidget);
		pathElements = new HashMap<BindingPathElement, List<SimplePathElement>>();
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {

		if (parent != null && parent.getType() instanceof FIBTableType) {

			List<SimplePathElement> returned = pathElements.get(parent);
			if (returned == null) {
				returned = new ArrayList<SimplePathElement>();
				returned.add(new TableSelectedPathElement(parent, getFIBComponent()));
				pathElements.put(parent, returned);
			}
			return returned;
		}

		return super.getAccessibleSimplePathElements(parent);
	}

}

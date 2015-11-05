/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.gina.model.bindings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.java.JavaBindingFactory;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBComponentType;
import org.openflexo.gina.model.FIBVariable;

public class FIBBindingFactory extends JavaBindingFactory {

	static final Logger logger = Logger.getLogger(FIBBindingFactory.class.getPackage().getName());

	private final Map<BindingPathElement, Map<Object, SimplePathElement>> storedBindingPathElements = new HashMap<BindingPathElement, Map<Object, SimplePathElement>>();

	protected SimplePathElement getSimplePathElement(Object object, BindingPathElement parent) {
		Map<Object, SimplePathElement> storedValues = storedBindingPathElements.get(parent);
		if (storedValues == null) {
			storedValues = new HashMap<Object, SimplePathElement>();
			storedBindingPathElements.put(parent, storedValues);
		}
		SimplePathElement returned = storedValues.get(object);
		if (returned == null) {
			returned = makeSimplePathElement(object, parent);
			storedValues.put(object, returned);
		}
		return returned;
	}

	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		if (object instanceof FIBVariable) {
			return new FIBVariablePathElement(parent, (FIBVariable) object);
		}
		logger.warning("Unexpected " + object + " for parent=" + parent);
		return null;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {

		if (parent != null) {
			Type pType = parent.getType();

			if (pType instanceof FIBComponentType) {
				List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
				FIBComponent concept = ((FIBComponentType) pType).getFIBComponent();

				if (concept != null) {
					for (FIBVariable<?> pr : concept.getVariables()) {
						returned.add(getSimplePathElement(pr, parent));
					}
				}
				return returned;
			}

			// In all other cases, consider it using Java rules
			return super.getAccessibleSimplePathElements(parent);
		}
		else {
			logger.warning("Trying to find accessible path elements for a NULL parent");
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement parent, String propertyName) {
		// We want to avoid code duplication, so iterate on all accessible simple path element and choose the right one
		SimplePathElement returned = null;
		List<? extends SimplePathElement> accessibleSimplePathElements = getAccessibleSimplePathElements(parent);
		if (accessibleSimplePathElements != null) {
			for (SimplePathElement e : accessibleSimplePathElements) {
				if (e.getLabel() != null && e.getLabel().equals(propertyName)) {
					returned = e;
				}
			}
		}
		// We cannot find a simple path element at this level, retrieve from java
		if (returned == null) {
			returned = super.makeSimplePathElement(parent, propertyName);
		}

		return returned;
	}

}

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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a FlexoConceptInstance of a given FlexoConcept
 * 
 * @author sylvain
 * 
 */
public class FIBComponentType implements CustomType {

	protected final FIBComponent fibComponent;

	protected static final Logger logger = FlexoLogger.getLogger(FIBComponentType.class.getPackage().getName());

	public FIBComponentType(FIBComponent aFIBComponent) {
		this.fibComponent = aFIBComponent;
	}

	public FIBComponent getFIBComponent() {
		return fibComponent;
	}

	@Override
	public Class<?> getBaseClass() {
		if (fibComponent != null) {
			return fibComponent.getClass();
		}
		return FIBComponent.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		if (aType instanceof FIBComponentType) {
			return getFIBComponent() == null || getFIBComponent() == ((FIBComponentType) aType).getFIBComponent();
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		if (fibComponent != null) {
			return fibComponent.getName();
		}
		return "FIBComponent";
	}

	@Override
	public String fullQualifiedRepresentation() {
		return simpleRepresentation();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	@Override
	public String getSerializationRepresentation() {
		return simpleRepresentation();
	}

	@Override
	public boolean isResolved() {
		return fibComponent != null;
	}

	@Override
	public void resolve(CustomTypeFactory<?> factory) {
		// We never try to serialize this, yet
		// Otherwise we should find a way to serialize a FIBComponent
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fibComponent == null) ? 0 : fibComponent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FIBComponentType other = (FIBComponentType) obj;
		if (fibComponent == null) {
			if (other.fibComponent != null)
				return false;
		}
		else if (!fibComponent.equals(other.fibComponent)) {
			return false;
		}
		return true;
	}

}

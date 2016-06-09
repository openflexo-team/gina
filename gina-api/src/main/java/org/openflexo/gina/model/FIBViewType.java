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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.gina.view.FIBView;
import org.openflexo.logging.FlexoLogger;

/**
 * Represent the type of a {@link FIBView} representing a given {@link FIBComponent}<br>
 * Note that to be able to expose dynamic properties, this class should be extended.
 * 
 * @author sylvain
 * 
 */
public class FIBViewType<F extends FIBComponent> implements CustomType {

	protected final F fibComponent;

	protected static final Logger logger = FlexoLogger.getLogger(FIBViewType.class.getPackage().getName());

	public FIBViewType(F aFIBComponent) {
		this.fibComponent = aFIBComponent;
	}

	public F getFIBComponent() {
		return fibComponent;
	}

	@Override
	public Class<? extends FIBView> getBaseClass() {
		return FIBView.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		if (aType instanceof FIBViewType) {
			return getFIBComponent() == null || getFIBComponent() == ((FIBViewType) aType).getFIBComponent();
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		if (fibComponent != null) {
			return getClass().getSimpleName() + "<" + fibComponent.getName() + ">";
		}
		return getClass().getSimpleName() + "<>";
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
		FIBViewType other = (FIBViewType) obj;
		if (fibComponent == null) {
			if (other.fibComponent != null)
				return false;
		}
		else if (!fibComponent.equals(other.fibComponent)) {
			return false;
		}
		return true;
	}

	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		return Collections.emptyList();
	}

	public List<DynamicProperty> getDynamicProperties() {
		return Collections.emptyList();
	}

	public static abstract class DynamicProperty {

		public abstract String getName();

		public abstract Type getType();

		private BindingVariable bindingVariable;

		public abstract String getTooltip();

		public BindingVariable getBindingVariable() {
			if (bindingVariable == null) {
				bindingVariable = new BindingVariable(getName(), getType()) {
					@Override
					public Type getType() {
						return DynamicProperty.this.getType();
					}
				};
			}
			return bindingVariable;
		}

		/**
		 * Return (create when null) binding variable identified by this dynamic property
		 */
		public BindingVariable appendToBindingModel(BindingModel bindingModel) {
			bindingModel.addToBindingVariables(getBindingVariable());
			return getBindingVariable();
		}

		public abstract Object getBindingValue(Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException;

		public abstract void setBindingValue(Object value, Object target, BindingEvaluationContext context)
				throws TypeMismatchException, NullReferenceException;

	}
}

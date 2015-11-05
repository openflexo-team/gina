/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.fib.model.widget;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.fib.model.FIBPropertyNotification;
import org.openflexo.fib.model.widget.FIBMultipleValues.FIBMultipleValuesImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBCheckboxList.FIBCheckboxListImpl.class)
@XMLElement(xmlTag = "CheckboxList")
public interface FIBCheckboxList extends FIBMultipleValues {

	@PropertyIdentifier(type = int.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = int.class)
	public static final String H_GAP_KEY = "HGap";
	@PropertyIdentifier(type = int.class)
	public static final String V_GAP_KEY = "VGap";

	@Getter(value = COLUMNS_KEY, defaultValue = "0")
	@XMLAttribute
	public int getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(int columns);

	@Getter(value = H_GAP_KEY, defaultValue = "0")
	@XMLAttribute(xmlTag = "hGap")
	public int getHGap();

	@Setter(H_GAP_KEY)
	public void setHGap(int HGap);

	@Getter(value = V_GAP_KEY, defaultValue = "0")
	@XMLAttribute(xmlTag = "vGap")
	public int getVGap();

	@Setter(V_GAP_KEY)
	public void setVGap(int VGap);

	public static abstract class FIBCheckboxListImpl extends FIBMultipleValuesImpl implements FIBCheckboxList {

		private int columns = 1;
		private int hGap = 0;
		private int vGap = -2;

		private static final Logger logger = Logger.getLogger(FIBCheckboxList.class.getPackage().getName());

		public FIBCheckboxListImpl() {
		}

		@Override
		public String getBaseName() {
			return "CheckboxList";
		}

		@Override
		public int getColumns() {
			return columns;
		}

		@Override
		public void setColumns(int columns) {
			FIBPropertyNotification<Integer> notification = requireChange(COLUMNS_KEY, columns);
			if (notification != null) {
				this.columns = columns;
				hasChanged(notification);
			}
		}

		@Override
		public int getHGap() {
			return hGap;
		}

		@Override
		public void setHGap(int hGap) {
			FIBPropertyNotification<Integer> notification = requireChange(H_GAP_KEY, hGap);
			if (notification != null) {
				this.hGap = hGap;
				hasChanged(notification);
			}
		}

		@Override
		public int getVGap() {
			return vGap;
		}

		@Override
		public void setVGap(int vGap) {
			FIBPropertyNotification<Integer> notification = requireChange(V_GAP_KEY, vGap);
			if (notification != null) {
				this.vGap = vGap;
				hasChanged(notification);
			}
		}

		@Override
		public Type getDataType() {
			Type[] args = new Type[1];
			args[0] = getIteratorType();
			return new ParameterizedTypeImpl(List.class, args);
		}

	}
}

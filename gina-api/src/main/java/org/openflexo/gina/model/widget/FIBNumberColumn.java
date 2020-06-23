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

package org.openflexo.gina.model.widget;

import java.lang.reflect.Type;

import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.widget.FIBNumber.NumberType;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBNumberColumn.FIBNumberColumnImpl.class)
@XMLElement(xmlTag = "NumberColumn")
public interface FIBNumberColumn extends FIBTableColumn {

	@PropertyIdentifier(type = NumberType.class)
	public static final String NUMBER_TYPE_KEY = "numberType";
	@PropertyIdentifier(type = String.class)
	public static final String NUMBER_FORMAT_KEY = "numberFormat";

	@Getter(value = NUMBER_TYPE_KEY)
	@XMLAttribute
	public NumberType getNumberType();

	@Setter(NUMBER_TYPE_KEY)
	public void setNumberType(NumberType numberType);

	/**
	 * Returns number format for that column, eg "###0.000" or "##.00"
	 * 
	 * @return
	 */
	@Getter(value = NUMBER_FORMAT_KEY)
	@XMLAttribute
	public String getNumberFormat();

	/**
	 * Sets number format for that column, eg "###0.000" or "##.00"
	 * 
	 * Symbol Meaning
	 * <ul>
	 * <li>0 a digit</li>
	 * <li># a digit, zero shows as absent</li>
	 * <li>. placeholder for decimal separator</li>
	 * <li>, placeholder for grouping separator.</li>
	 * <li>E separates mantissa and exponent for exponential formats.</li>
	 * <li>- default negative prefix.</li>
	 * <li>% multiply by 100 and show as percentage</li>
	 * <li>X any other characters can be used in the prefix or suffix</li>
	 * <li>' used to quote special characters in a prefix or suffix.</li>
	 * </ul>
	 * 
	 * @param numberFormat
	 */
	@Setter(NUMBER_FORMAT_KEY)
	public void setNumberFormat(String numberFormat);

	public static abstract class FIBNumberColumnImpl extends FIBTableColumnImpl implements FIBNumberColumn {

		private NumberType numberType = NumberType.IntegerType;

		@Override
		public NumberType getNumberType() {
			return numberType;
		}

		@Override
		public void setNumberType(NumberType numberType) {
			FIBPropertyNotification<NumberType> notification = requireChange(NUMBER_TYPE_KEY, numberType);
			if (notification != null) {
				this.numberType = numberType;
				hasChanged(notification);
			}
		}

		@Override
		public Type getDefaultDataClass() {
			return Number.class;
		}

		@Override
		public ColumnType getColumnType() {
			return ColumnType.Number;
		}

	}
}

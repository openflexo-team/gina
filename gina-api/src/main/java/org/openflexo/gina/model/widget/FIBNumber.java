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
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBNumber.FIBNumberImpl.class)
@XMLElement(xmlTag = "Number")
public interface FIBNumber extends FIBWidget {

	public static enum NumberType {
		ByteType {
			@Override
			public Class<Byte> getType() {
				return Byte.class;
			}
		},
		ShortType {
			@Override
			public Class<Short> getType() {
				return Short.class;
			}
		},
		IntegerType {
			@Override
			public Class<Integer> getType() {
				return Integer.class;
			}
		},
		LongType {
			@Override
			public Class<Long> getType() {
				return Long.class;
			}
		},
		FloatType {
			@Override
			public Class<Float> getType() {
				return Float.class;
			}
		},
		DoubleType {
			@Override
			public Class<Double> getType() {
				return Double.class;
			}
		};

		public abstract Class<? extends Number> getType();
	}

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = boolean.class)
	public static final String ALLOWS_NULL_KEY = "allowsNull";
	@PropertyIdentifier(type = Number.class)
	public static final String MIN_VALUE_KEY = "minValue";
	@PropertyIdentifier(type = Number.class)
	public static final String MAX_VALUE_KEY = "maxValue";
	@PropertyIdentifier(type = Number.class)
	public static final String INCREMENT_KEY = "increment";
	@PropertyIdentifier(type = NumberType.class)
	public static final String NUMBER_TYPE_KEY = "numberType";
	@PropertyIdentifier(type = String.class)
	public static final String NUMBER_FORMAT_KEY = "numberFormat";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";

	@Getter(value = VALIDATE_ON_RETURN_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getValidateOnReturn();

	@Setter(VALIDATE_ON_RETURN_KEY)
	public void setValidateOnReturn(boolean validateOnReturn);

	@Getter(value = ALLOWS_NULL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAllowsNull();

	@Setter(ALLOWS_NULL_KEY)
	public void setAllowsNull(boolean allowsNull);

	@Getter(value = MIN_VALUE_KEY)
	@XMLAttribute
	public Number getMinValue();

	@Setter(MIN_VALUE_KEY)
	public void setMinValue(Number minValue);

	@Getter(value = MAX_VALUE_KEY)
	@XMLAttribute
	public Number getMaxValue();

	@Setter(MAX_VALUE_KEY)
	public void setMaxValue(Number maxValue);

	@Getter(value = INCREMENT_KEY)
	@XMLAttribute
	public Number getIncrement();

	@Setter(INCREMENT_KEY)
	public void setIncrement(Number increment);

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

	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	public Number retrieveMaxValue();

	public Number retrieveMinValue();

	public Number retrieveIncrement();

	public int getIncrementAsInteger();

	public void setIncrementAsInteger(int incrementAsInteger);

	public int getMinValueAsInteger();

	public void setMinValueAsInteger(int minValueAsInteger);

	public int getMaxValueAsInteger();

	public void setMaxValueAsInteger(int maxValueAsInteger);

	public static abstract class FIBNumberImpl extends FIBWidgetImpl implements FIBNumber {

		private boolean allowsNull = false;
		private boolean validateOnReturn = false;
		private Number minValue;
		private Number maxValue;
		private Number increment;
		private NumberType numberType = NumberType.IntegerType;
		private Integer columns;

		public FIBNumberImpl() {
		}

		@Override
		public String getBaseName() {
			return "NumberSelector";
		}

		@Override
		public Number retrieveMinValue() {
			if (minValue == null) {
				switch (numberType) {
					case ByteType:
						minValue = Byte.MIN_VALUE;
						break;
					case ShortType:
						minValue = Short.MIN_VALUE;
						break;
					case IntegerType:
						minValue = Integer.MIN_VALUE;
						break;
					case LongType:
						minValue = Long.MIN_VALUE;
						break;
					case FloatType:
						minValue = -Float.MAX_VALUE;
						break;
					case DoubleType:
						minValue = -Double.MAX_VALUE;
						break;
					default:
						break;
				}
			}
			return minValue;
		}

		@Override
		public Number getMinValue() {
			return minValue;
		}

		@Override
		public void setMinValue(Number minValue) {
			this.minValue = minValue;
		}

		@Override
		public Number retrieveMaxValue() {
			if (maxValue == null) {
				switch (numberType) {
					case ByteType:
						maxValue = Byte.MAX_VALUE;
						break;
					case ShortType:
						maxValue = Short.MAX_VALUE;
						break;
					case IntegerType:
						maxValue = Integer.MAX_VALUE;
						break;
					case LongType:
						maxValue = Long.MAX_VALUE;
						break;
					case FloatType:
						maxValue = Float.MAX_VALUE;
						break;
					case DoubleType:
						maxValue = Double.MAX_VALUE;
						break;
					default:
						break;
				}
			}
			return maxValue;
		}

		@Override
		public Number getMaxValue() {
			return maxValue;
		}

		@Override
		public void setMaxValue(Number maxValue) {
			this.maxValue = maxValue;
		}

		@Override
		public Number retrieveIncrement() {
			if (increment == null) {
				switch (numberType) {
					case ByteType:
						increment = new Byte((byte) 1);
						break;
					case ShortType:
						increment = new Short((short) 1);
						break;
					case IntegerType:
						increment = new Integer(1);
						break;
					case LongType:
						increment = new Long(1);
						break;
					case FloatType:
						increment = new Float(1);
						break;
					case DoubleType:
						increment = new Double(1);
						break;
					default:
						break;
				}
			}
			return increment;
		}

		@Override
		public Number getIncrement() {
			return increment;
		}

		@Override
		public void setIncrement(Number increment) {
			this.increment = increment;
		}

		@Override
		public Type getDefaultDataType() {
			if (numberType == null) {
				return Number.class;
			}
			switch (numberType) {
				case ByteType:
					return Byte.class;
				case ShortType:
					return Short.class;
				case IntegerType:
					return Integer.class;
				case LongType:
					return Long.class;
				case FloatType:
					return Float.class;
				case DoubleType:
					return Double.class;
				default:
					return Number.class;
			}
		}

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
				if (getData() != null) {
					getData().setDeclaredType(getDefaultDataType());
				}
			}
		}

		@Override
		public boolean getValidateOnReturn() {
			return validateOnReturn;
		}

		@Override
		public void setValidateOnReturn(boolean validateOnReturn) {
			FIBPropertyNotification<Boolean> notification = requireChange(VALIDATE_ON_RETURN_KEY, validateOnReturn);
			if (notification != null) {
				this.validateOnReturn = validateOnReturn;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getAllowsNull() {
			return allowsNull;
		}

		@Override
		public void setAllowsNull(boolean allowsNull) {
			FIBPropertyNotification<Boolean> notification = requireChange(ALLOWS_NULL_KEY, allowsNull);
			if (notification != null) {
				this.allowsNull = allowsNull;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getColumns() {
			if (columns == null) {
				if (numberType == null) {
					return 3;
				}
				switch (numberType) {
					case ByteType:
						return 2;
					case ShortType:
						return 3;
					case IntegerType:
						return 4;
					case LongType:
						return 6;
					case FloatType:
						return 5;
					case DoubleType:
						return 5;
					default:
						return 3;
				}

			}
			return columns;
		}

		@Override
		public void setColumns(Integer columns) {
			FIBPropertyNotification<Integer> notification = requireChange(COLUMNS_KEY, columns);
			if (notification != null) {
				this.columns = columns;
				hasChanged(notification);
			}
		}

		@Override
		public int getIncrementAsInteger() {
			return getIncrement().intValue();
		}

		@Override
		public void setIncrementAsInteger(int incrementAsInteger) {
			setIncrement(incrementAsInteger);
		}

		@Override
		public int getMinValueAsInteger() {
			return getMinValue().intValue();
		}

		@Override
		public void setMinValueAsInteger(int minValueAsInteger) {
			setMinValue(minValueAsInteger);
		}

		@Override
		public int getMaxValueAsInteger() {
			return getMaxValue().intValue();
		}

		@Override
		public void setMaxValueAsInteger(int maxValueAsInteger) {
			setMaxValue(maxValueAsInteger);
		}

		@Override
		public boolean isFocusable() {
			return true;
		}

	}
}

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

package org.openflexo.fib.model.widget;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTextArea.FIBTextAreaImpl.class)
@XMLElement(xmlTag = "TextArea")
public interface FIBTextArea extends FIBTextWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROWS_KEY = "rows";

	@Override
	@Getter(value = VALIDATE_ON_RETURN_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isValidateOnReturn();

	@Override
	@Setter(VALIDATE_ON_RETURN_KEY)
	public void setValidateOnReturn(boolean validateOnReturn);

	@Override
	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Override
	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	@Getter(value = ROWS_KEY)
	@XMLAttribute
	public Integer getRows();

	@Setter(ROWS_KEY)
	public void setRows(Integer rows);

	public static abstract class FIBTextAreaImpl extends FIBTextWidgetImpl implements FIBTextArea {

		public Integer rows = null;

		public FIBTextAreaImpl() {
		}

		@Override
		public String getBaseName() {
			return "TextArea";
		}

		/**
		 * @return the rows
		 */
		@Override
		public Integer getRows() {
			return rows;
		}

		/**
		 * @param rows
		 *            the rows to set
		 */
		@Override
		public void setRows(Integer rows) {
			this.rows = rows;

		}

	}
}

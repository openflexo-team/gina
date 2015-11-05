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

package org.openflexo.gina.model.widget;

import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.widget.FIBTextWidget.FIBTextWidgetImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBEditor.FIBEditorImpl.class)
@XMLElement(xmlTag = "Editor")
public interface FIBEditor extends FIBTextWidget {

	public static enum FIBTokenMarkerStyle {
		None,
		BatchFile,
		C,
		CC,
		IDL,
		JavaScript,
		Java,
		Eiffel,
		HTML,
		Patch,
		Perl,
		PHP,
		Props,
		Python,
		ShellScript,
		SQL,
		TSQL,
		TeX,
		WOD,
		XML,
		FML /* Flexo Modelling Language */
	}

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROWS_KEY = "rows";
	@PropertyIdentifier(type = String.class)
	public static final String TEXT_KEY = "text";
	@PropertyIdentifier(type = FIBTokenMarkerStyle.class)
	public static final String TOKEN_MARKER_STYLE_KEY = "tokenMarkerStyle";

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

	@Override
	@Getter(value = TEXT_KEY)
	@XMLAttribute
	public String getText();

	@Override
	@Setter(TEXT_KEY)
	public void setText(String text);

	@Getter(value = TOKEN_MARKER_STYLE_KEY)
	@XMLAttribute
	public FIBTokenMarkerStyle getTokenMarkerStyle();

	@Setter(TOKEN_MARKER_STYLE_KEY)
	public void setTokenMarkerStyle(FIBTokenMarkerStyle tokenMarkerStyle);

	public static abstract class FIBEditorImpl extends FIBTextWidgetImpl implements FIBEditor {

		private Integer rows = null;
		private FIBTokenMarkerStyle tokenMarkerStyle = FIBTokenMarkerStyle.None;

		public FIBEditorImpl() {
		}

		@Override
		public String getBaseName() {
			return "Editor";
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
			FIBPropertyNotification<Integer> notification = requireChange(ROWS_KEY, rows);
			if (notification != null) {
				this.rows = rows;
				hasChanged(notification);
			}
			this.rows = rows;
		}

		@Override
		public FIBTokenMarkerStyle getTokenMarkerStyle() {
			return tokenMarkerStyle;
		}

		@Override
		public void setTokenMarkerStyle(FIBTokenMarkerStyle tokenMarkerStyle) {
			//System.out.println("setTokenMarkerStyle with " + tokenMarkerStyle);
			FIBPropertyNotification<FIBTokenMarkerStyle> notification = requireChange(TOKEN_MARKER_STYLE_KEY, tokenMarkerStyle);
			if (notification != null) {
				this.tokenMarkerStyle = tokenMarkerStyle;
				hasChanged(notification);
			}
		}

	}
}

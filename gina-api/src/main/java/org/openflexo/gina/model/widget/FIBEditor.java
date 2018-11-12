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
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBEditor.FIBEditorImpl.class)
@XMLElement(xmlTag = "Editor")
public interface FIBEditor extends FIBTextWidget {

	public static enum SyntaxStyle {
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

	@PropertyIdentifier(type = Integer.class)
	public static final String ROWS_KEY = "rows";
	@PropertyIdentifier(type = SyntaxStyle.class)
	public static final String SYNTAX_STYLE_KEY = "syntaxStyle";

	@Getter(value = ROWS_KEY)
	@XMLAttribute
	public Integer getRows();

	@Setter(ROWS_KEY)
	public void setRows(Integer rows);

	@Getter(value = SYNTAX_STYLE_KEY)
	@XMLAttribute
	public SyntaxStyle getSyntaxStyle();

	@Setter(SYNTAX_STYLE_KEY)
	public void setSyntaxStyle(SyntaxStyle tokenMarkerStyle);

	public static abstract class FIBEditorImpl extends FIBTextWidgetImpl implements FIBEditor {

		private Integer rows = null;
		private SyntaxStyle tokenMarkerStyle = SyntaxStyle.None;

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
		public SyntaxStyle getSyntaxStyle() {
			return tokenMarkerStyle;
		}

		@Override
		public void setSyntaxStyle(SyntaxStyle tokenMarkerStyle) {
			// System.out.println("setTokenMarkerStyle with " + tokenMarkerStyle);
			FIBPropertyNotification<SyntaxStyle> notification = requireChange(SYNTAX_STYLE_KEY, tokenMarkerStyle);
			if (notification != null) {
				this.tokenMarkerStyle = tokenMarkerStyle;
				hasChanged(notification);
			}
		}

	}
}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import java.awt.Font;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoFont {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoFont.class.getPackage().getName());

	private Font _theFont;

	public static final FlexoFont SANS_SERIF = new FlexoFont("Lucida Sans", Font.PLAIN, 10);

	public FlexoFont(String s) {
		this(nameFromString(s), styleFromString(s), sizeFromString(s));
	}

	public FlexoFont(String name, int style, int size) {
		super();
		_theFont = new Font(name, style, size);
	}

	public FlexoFont(Font aFont) {
		this(aFont.getName(), aFont.getStyle(), aFont.getSize());
	}

	@Override
	public String toString() {
		return _theFont.getName() + "," + _theFont.getStyle() + "," + _theFont.getSize();
	}

	public static FlexoFont stringToFont(String s) {
		return new FlexoFont(s);
	}

	private static String nameFromString(String s) {
		return s.substring(0, s.indexOf(","));
	}

	private static int styleFromString(String s) {
		return Integer.parseInt(s.substring(s.indexOf(",") + 1, s.lastIndexOf(",")));
	}

	private static int sizeFromString(String s) {
		return Integer.parseInt(s.substring(s.lastIndexOf(",") + 1));
	}

	private static final Hashtable<String, FlexoFont> fontHashtable = new Hashtable<>();

	public static FlexoFont get(String fontAsString) {
		if (fontAsString == null) {
			return null;
		}
		FlexoFont answer = fontHashtable.get(fontAsString);
		if (answer == null) {
			answer = new FlexoFont(fontAsString);
			fontHashtable.put(fontAsString, answer);
		}
		return answer;
	}

	public int getSize() {
		return getFont().getSize();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof FlexoFont) {
			FlexoFont font = (FlexoFont) obj;
			return getFont().equals(font.getFont());
		}
		return super.equals(obj);
	}

	public Font getFont() {
		return _theFont;
	}

	public void setFont(Font aFont) {
		_theFont = aFont;
	}

}

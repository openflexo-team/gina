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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoColor {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoColor.class.getPackage().getName());

	private static final Vector<Color> COLOR_SET;

	public static final int RAW_COLOR_VALUES[] = { 255, 255, 255, 204, 255, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255,
			204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 255, 204, 255, 255, 204, 204, 255, 204, 204, 255,
			204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 255, 204, 204, 255,
			204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204,
			204, 204, 204, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153,
			153, 255, 153, 153, 255, 204, 153, 255, 255, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153,
			153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 204, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255, 153,
			153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 204, 204, 204, 204, 102, 255, 255, 102,
			204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 153, 102, 255, 204, 102,
			255, 255, 102, 255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102,
			255, 153, 102, 255, 204, 102, 255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102,
			255, 102, 102, 255, 102, 102, 255, 153, 102, 255, 204, 153, 153, 153, 51, 255, 255, 51, 204, 255, 51, 153, 255, 51, 102, 255,
			51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 51, 255, 255, 51, 255, 255, 51, 204, 255, 51, 153, 255,
			51, 102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 51, 255, 255, 51, 204, 255, 51, 153, 255,
			51, 102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 153, 153, 153, 0, 255, 255,
			0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255, 0, 255,
			255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255, 0,
			204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 102, 102, 102,
			0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0, 204,
			204, 0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0,
			204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204,
			102, 102, 102, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0, 153,
			153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0,
			153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0,
			153, 153, 51, 51, 51, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, 102,
			0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102,
			102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102,
			102, 0, 102, 102, 0, 0, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51,
			0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0,
			51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 51, 51, 51 };

	static {
		COLOR_SET = new Vector<>();
		int i = RAW_COLOR_VALUES.length / 3;
		for (int j = 0; j < i; j++) {
			COLOR_SET.add(new Color(RAW_COLOR_VALUES[j * 3], RAW_COLOR_VALUES[j * 3 + 1], RAW_COLOR_VALUES[j * 3 + 2]));
		}
	}

	public static final Color BLACK_COLOR = Color.BLACK;

	public static final Color GRAY_COLOR = Color.GRAY;

	public static final Color WHITE_COLOR = Color.WHITE;

	public static final Color LIGHT_GRAY_COLOR = new Color(230, 230, 230);

	public static Color getRandomColor(List<Color> excludedColors) {
		List<Color> colors = new ArrayList<>(COLOR_SET);
		colors.removeAll(excludedColors);
		int i = new Random().nextInt(colors.size());
		return colors.get(i);
	}

	public static Color getColor(int index) {
		return COLOR_SET.get(index);
	}

}

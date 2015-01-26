/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.swing.merge;

import java.awt.Color;

import org.openflexo.diff.merge.MergeChange;
import org.openflexo.jedit.LinesHighlight;

public class MergeHighlight extends LinesHighlight {

	public static final Color ADDITION_SELECTED_COLOR = new Color(181, 213, 255);
	public static final Color ADDITION_UNSELECTED_COLOR = new Color(230, 255, 255);
	public static final Color ADDITION_SELECTED_BORDER_COLOR = Color.BLUE;
	public static final Color ADDITION_UNSELECTED_BORDER_COLOR = ADDITION_SELECTED_COLOR;

	public static final Color REMOVAL_SELECTED_COLOR = new Color(255, 190, 182);
	public static final Color REMOVAL_UNSELECTED_COLOR = new Color(255, 214, 214);
	public static final Color REMOVAL_SELECTED_BORDER_COLOR = Color.RED;
	public static final Color REMOVAL_UNSELECTED_BORDER_COLOR = REMOVAL_SELECTED_COLOR;

	public static final Color MODIFICATION_SELECTED_COLOR = new Color(220, 220, 220);
	public static final Color MODIFICATION_UNSELECTED_COLOR = new Color(240, 240, 240);
	public static final Color MODIFICATION_SELECTED_BORDER_COLOR = Color.GRAY;
	public static final Color MODIFICATION_UNSELECTED_BORDER_COLOR = MODIFICATION_SELECTED_COLOR;

	private Color selectedBg;
	private Color unselectedBg;
	private Color selectedFg;
	private Color unselectedFg;

	public MergeHighlight(MergeChange change, MergeTextArea MergeTA) {
		super();
		if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Left) {
			selectedBg = MODIFICATION_SELECTED_COLOR;
			unselectedBg = MODIFICATION_UNSELECTED_COLOR;
			selectedFg = MODIFICATION_SELECTED_BORDER_COLOR;
			unselectedFg = MODIFICATION_UNSELECTED_BORDER_COLOR;
		} else if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Conflict) {
			selectedBg = REMOVAL_SELECTED_COLOR;
			unselectedBg = REMOVAL_UNSELECTED_COLOR;
			selectedFg = REMOVAL_SELECTED_BORDER_COLOR;
			unselectedFg = REMOVAL_UNSELECTED_BORDER_COLOR;
		} else if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Right) {
			selectedBg = ADDITION_SELECTED_COLOR;
			unselectedBg = ADDITION_UNSELECTED_COLOR;
			selectedFg = ADDITION_SELECTED_BORDER_COLOR;
			unselectedFg = ADDITION_UNSELECTED_BORDER_COLOR;
		}
		deselect();
	}

	public void select() {
		setBgColor(selectedBg);
		setFgColor(selectedFg);
	}

	public void deselect() {
		setBgColor(unselectedBg);
		setFgColor(unselectedFg);
	}

}

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

package org.openflexo.swing.diff;

import java.awt.Color;

import org.openflexo.diff.ComputeDiff.AdditionChange;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.ModificationChange;
import org.openflexo.diff.ComputeDiff.RemovalChange;
import org.openflexo.jedit.LinesHighlight;

public class DiffHighlight extends LinesHighlight {

	private Color ADDITION_SELECTED_COLOR;
	private Color ADDITION_UNSELECTED_COLOR;
	private Color ADDITION_SELECTED_BORDER_COLOR;
	private Color ADDITION_UNSELECTED_BORDER_COLOR;

	private Color REMOVAL_SELECTED_COLOR;
	private Color REMOVAL_UNSELECTED_COLOR;
	private Color REMOVAL_SELECTED_BORDER_COLOR;
	private Color REMOVAL_UNSELECTED_BORDER_COLOR;

	private Color MODIFICATION_SELECTED_COLOR;
	private Color MODIFICATION_UNSELECTED_COLOR;
	private Color MODIFICATION_SELECTED_BORDER_COLOR;
	private Color MODIFICATION_UNSELECTED_BORDER_COLOR;

	private DiffTextArea diffTA;

	private Color selectedBg;
	private Color unselectedBg;
	private Color selectedFg;
	private Color unselectedFg;

	public DiffHighlight(DiffChange change, DiffTextArea diffTA) {
		super();
		this.diffTA = diffTA;
		setColors();
		if (change instanceof ModificationChange) {
			selectedBg = MODIFICATION_SELECTED_COLOR;
			unselectedBg = MODIFICATION_UNSELECTED_COLOR;
			selectedFg = MODIFICATION_SELECTED_BORDER_COLOR;
			unselectedFg = MODIFICATION_UNSELECTED_BORDER_COLOR;
		} else if (change instanceof RemovalChange) {
			selectedBg = REMOVAL_SELECTED_COLOR;
			unselectedBg = REMOVAL_UNSELECTED_COLOR;
			selectedFg = REMOVAL_SELECTED_BORDER_COLOR;
			unselectedFg = REMOVAL_UNSELECTED_BORDER_COLOR;
		} else if (change instanceof AdditionChange) {
			selectedBg = ADDITION_SELECTED_COLOR;
			unselectedBg = ADDITION_UNSELECTED_COLOR;
			selectedFg = ADDITION_SELECTED_BORDER_COLOR;
			unselectedFg = ADDITION_UNSELECTED_BORDER_COLOR;
		}
		deselect();
	}

	private void setColors() {
		ADDITION_SELECTED_COLOR = diffTA.isLeftOriented() ? new Color(255, 190, 182) : new Color(181, 213, 255);
		ADDITION_UNSELECTED_COLOR = diffTA.isLeftOriented() ? new Color(255, 214, 214) : new Color(230, 255, 255);
		ADDITION_SELECTED_BORDER_COLOR = diffTA.isLeftOriented() ? Color.RED : Color.BLUE;
		ADDITION_UNSELECTED_BORDER_COLOR = ADDITION_SELECTED_COLOR;

		REMOVAL_SELECTED_COLOR = diffTA.isLeftOriented() ? new Color(181, 213, 255) : new Color(255, 190, 182);
		REMOVAL_UNSELECTED_COLOR = diffTA.isLeftOriented() ? new Color(230, 255, 255) : new Color(255, 214, 214);
		REMOVAL_SELECTED_BORDER_COLOR = diffTA.isLeftOriented() ? Color.BLUE : Color.RED;
		REMOVAL_UNSELECTED_BORDER_COLOR = REMOVAL_SELECTED_COLOR;

		MODIFICATION_SELECTED_COLOR = new Color(255, 248, 150);
		MODIFICATION_UNSELECTED_COLOR = new Color(254, 254, 224);
		MODIFICATION_SELECTED_BORDER_COLOR = Color.BLACK;
		MODIFICATION_UNSELECTED_BORDER_COLOR = MODIFICATION_SELECTED_COLOR;

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

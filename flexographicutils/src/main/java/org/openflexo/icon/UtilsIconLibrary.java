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

package org.openflexo.icon;

import javax.swing.ImageIcon;

import org.openflexo.rm.ResourceLocator;

/**
 * Utility class containing all icons used in context of utils
 * 
 * @author sylvain
 * 
 */
public class UtilsIconLibrary {

	// Common icons used in the context of utils

	public static final ImageIcon CUSTOM_POPUP_DOWN = new ImageIconResource(ResourceLocator.locateResource("Icons/CustomPopupDown.png"));
	public static final ImageIcon CUSTOM_POPUP_DOWN_DISABLED = new ImageIconResource(
			ResourceLocator.locateResource("Icons/CustomPopupDownDisabled.png"));

	public static final ImageIcon CUSTOM_POPUP_BUTTON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/CustomPopupButton.png"));
	public static final ImageIcon CUSTOM_POPUP_OPEN_BUTTON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/CustomPopupOpenButton.png"));

	public static final ImageIcon CLOSE_TAB_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Actions/CloseTab.png"));
	public static final ImageIcon CLOSE_TAB_HOVER_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Actions/CloseTabHover.png"));
	public static final ImageIcon CLOSE_TAB_PRESSED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Actions/CloseTabPressed.png"));

	public static final ImageIcon ARROW_DOWN = new ImageIconResource(ResourceLocator.locateResource("Icons/ArrowDown.gif"));
	public static final ImageIcon ARROW_UP = new ImageIconResource(ResourceLocator.locateResource("Icons/ArrowUp.gif"));
	public static final ImageIcon ARROW_LEFT = new ImageIconResource(ResourceLocator.locateResource("Icons/ArrowLeft.gif"));
	public static final ImageIcon ARROW_RIGHT = new ImageIconResource(ResourceLocator.locateResource("Icons/ArrowRight.gif"));

	public static final ImageIcon ARROW_DOWN_2 = new ImageIconResource(ResourceLocator.locateResource("Icons/Arrows/Down.png"));
	public static final ImageIcon ARROW_UP_2 = new ImageIconResource(ResourceLocator.locateResource("Icons/Arrows/Up.png"));
	public static final ImageIcon ARROW_BOTTOM_2 = new ImageIconResource(ResourceLocator.locateResource("Icons/Arrows/Bottom.png"));
	public static final ImageIcon ARROW_TOP_2 = new ImageIconResource(ResourceLocator.locateResource("Icons/Arrows/Top.png"));

	// Diff icons

	public static final ImageIcon ADDITION_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsDiff/Addition.gif"));
	public static final ImageIcon REMOVAL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsDiff/Removal.gif"));
	public static final ImageIcon ADDITION_LEFT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/Addition-left.gif"));
	public static final ImageIcon REMOVAL_LEFT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/Removal-left.gif"));
	public static final ImageIcon MODIFICATION_LEFT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/Modification-left.gif"));
	public static final ImageIcon ADDITION_RIGHT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/Addition-right.gif"));
	public static final ImageIcon REMOVAL_RIGHT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/Removal-right.gif"));
	public static final ImageIcon MODIFICATION_RIGHT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/Modification-right.gif"));

	public static final ImageIcon RIGHT_UPDATE_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/RightUpdate.gif"));
	public static final ImageIcon LEFT_UPDATE_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsDiff/LeftUpdate.gif"));

	// Merge icons

	public static final ImageIcon LEFT_ADDITION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/r_outadd_ov2.gif"));
	public static final ImageIcon LEFT_MODIFICATION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/r_outchg_ov2.gif"));
	public static final ImageIcon LEFT_REMOVAL_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/r_outdel_ov2.gif"));
	public static final ImageIcon RIGHT_ADDITION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/r_inadd_ov2.gif"));
	public static final ImageIcon RIGHT_MODIFICATION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/r_inchg_ov2.gif"));
	public static final ImageIcon RIGHT_REMOVAL_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/r_indel_ov2.gif"));
	public static final ImageIcon CONFLICT_ADDITION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/confadd_ov2.gif"));
	public static final ImageIcon CONFLICT_MODIFICATION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/confchg_ov2.gif"));
	public static final ImageIcon CONFLICT_REMOVAL_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/confdel_ov2.gif"));
	public static final ImageIcon CONFLICT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/ConflictUnresolved.gif"));
	public static final ImageIcon RIGHT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/r_inchg_ov.gif"));
	public static final ImageIcon LEFT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/r_outchg_ov.gif"));
	public static final ImageIcon ACCEPT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/Accept.gif"));
	public static final ImageIcon REFUSE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/Refuse.gif"));
	public static final ImageIcon CHOOSE_LEFT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/Left.gif"));
	public static final ImageIcon CHOOSE_RIGHT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/Right.gif"));
	public static final ImageIcon CHOOSE_NONE = new ImageIconResource(ResourceLocator.locateResource("Icons/IconsMerge/Refuse.gif"));
	public static final ImageIcon CHOOSE_BOTH_LEFT_FIRST = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/LeftRight.gif"));
	public static final ImageIcon CHOOSE_BOTH_RIGHT_FIRST = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/RightLeft.gif"));
	public static final ImageIcon CUSTOM_EDITING_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/CustomEditing.gif"));
	public static final ImageIcon AUTOMATIC_MERGE_RESOLVING_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/SmartConflictResolving.gif"));
	public static final ImageIcon SMART_CONFLICT_RESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/SmartConflictResolved.gif"));
	public static final ImageIcon SMART_CONFLICT_UNRESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/SmartConflictUnresolved.gif"));
	public static final ImageIcon CUSTOM_EDITING_RESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/CustomEditingResolved.gif"));
	public static final ImageIcon CUSTOM_EDITING_UNRESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/CustomEditingUnresolved.gif"));
	public static final ImageIcon CONFLICT_RESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/ConflictResolved.gif"));
	public static final ImageIcon CONFLICT_UNRESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/ConflictUnresolved.gif"));
	public static final ImageIcon ADD_CONFLICT_RESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/AddConflictResolved.gif"));
	public static final ImageIcon ADD_CONFLICT_UNRESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/AddConflictUnresolved.gif"));
	public static final ImageIcon DEL_CONFLICT_RESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/DeleteConflictResolved.gif"));
	public static final ImageIcon DEL_CONFLICT_UNRESOLVED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/IconsMerge/DeleteConflictUnresolved.gif"));

	/* Icon markers */
	public static final IconMarker LEFT_ADDITION = new IconMarker(LEFT_ADDITION_ICON, 12, 7);
	public static final IconMarker LEFT_MODIFICATION = new IconMarker(LEFT_MODIFICATION_ICON, 12, 7);
	public static final IconMarker LEFT_REMOVAL = new IconMarker(LEFT_REMOVAL_ICON, 12, 7);
	public static final IconMarker RIGHT_ADDITION = new IconMarker(RIGHT_ADDITION_ICON, 12, 7);
	public static final IconMarker RIGHT_MODIFICATION = new IconMarker(RIGHT_MODIFICATION_ICON, 12, 7);
	public static final IconMarker RIGHT_REMOVAL = new IconMarker(RIGHT_REMOVAL_ICON, 12, 7);
	public static final IconMarker CONFLICT = new IconMarker(CONFLICT_ICON, 12, 7);

	// Utils icons

	public static final ImageIcon OK_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/OK.gif"));
	public static final ImageIcon WARNING_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Warning.gif"));
	public static final ImageIcon ERROR_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Error.gif"));

	public static final ImageIcon MOVE_UP_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Up.gif"));
	public static final ImageIcon MOVE_DOWN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Down.gif"));
	public static final ImageIcon MOVE_LEFT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Left.gif"));
	public static final ImageIcon MOVE_RIGHT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Right.gif"));
	public static final ImageIcon CLOCK_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Clock.gif"));
	public static final ImageIcon SEPARATOR_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Separator.gif"));
	public static final ImageIcon SEARCH_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Search.png"));
	public static final ImageIcon FILTERS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Filters.png"));
	public static final ImageIcon CANCEL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Cancel.png"));

	public static final ImageIcon ERROR_MARKER_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/ErrorMarker.gif"));
	public static final ImageIcon MINUS_MARKER_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/MinusMarker.png"));

	public static final IconMarker ERROR_MARKER = new IconMarker(UtilsIconLibrary.ERROR_MARKER_ICON, 0, 9);
	public static final IconMarker MINUS_MARKER = new IconMarker(UtilsIconLibrary.MINUS_MARKER_ICON, 0, 9);

	// Flags
	public static final ImageIcon UK_FLAG = new ImageIconResource(ResourceLocator.locateResource("Icons/Lang/uk-flag.gif"));
	public static final ImageIcon FR_FLAG = new ImageIconResource(ResourceLocator.locateResource("Icons/Lang/fr-flag.gif"));
	public static final ImageIcon NE_FLAG = new ImageIconResource(ResourceLocator.locateResource("Icons/Lang/ne-flag.gif"));

}

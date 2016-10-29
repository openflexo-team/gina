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

package org.openflexo.gina.utils;

import javax.swing.ImageIcon;

import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;

/**
 * Utility class containing all icons used in FIB
 * 
 * @author sylvain
 * 
 */
public class FIBIconLibrary {
	public static ResourceLocator rl = ResourceLocator.getResourceLocator();
	// GUI
	public static final ImageIcon BROWSER_PLUS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/GUI/BrowserPlus.gif"));
	public static final ImageIcon BROWSER_PLUS_DISABLED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserPlusDisabled.gif"));
	public static final ImageIcon BROWSER_PLUS_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserPlusSelected.gif"));
	public static final ImageIcon BROWSER_MINUS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/GUI/BrowserMinus.gif"));
	public static final ImageIcon BROWSER_MINUS_DISABLED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserMinusDisabled.gif"));
	public static final ImageIcon BROWSER_MINUS_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserMinusSelected.gif"));
	public static final ImageIcon BROWSER_OPTIONS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserOptions.gif"));
	public static final ImageIcon BROWSER_OPTIONS_DISABLED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserOptionsDisabled.gif"));
	public static final ImageIcon BROWSER_OPTIONS_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserOptionsSelected.gif"));
	public static final ImageIcon BROWSER_FILTERS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserFilters.gif"));
	public static final ImageIcon BROWSER_FILTERS_DISABLED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserFiltersDisabled.gif"));
	public static final ImageIcon BROWSER_FILTERS_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/BrowserFiltersSelected.gif"));

	public static final ImageIcon ROUND_PANEL_BORDER = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorder.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_TOP_LEFT = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderTopLeft.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_TOP_RIGHT = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderTopRight.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_BOTTOM_LEFT = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderBottomLeft.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_BOTTOM_RIGHT = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderBottomRight.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_TOP = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderTop.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_BOTTOM = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderBottom.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_RIGHT = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderRight.png"));
	public static final ImageIcon ROUND_PANEL_BORDER_LEFT = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/RoundPanelBorderLeft.png"));

	// Icons used in ClassSelector
	public static final ImageIcon JAVA_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ClassSelector/Java.png"));
	public static final ImageIcon PACKAGE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ClassSelector/Package.gif"));
	public static final ImageIcon CLASS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ClassSelector/Class.gif"));
	public static final ImageIcon INTERFACE_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/ClassSelector/Interface.gif"));
	public static final ImageIcon ENUM_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ClassSelector/Enumeration.gif"));

	// Icons used in BindingSelector
	public static final ImageIcon ARROW_RIGHT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/SmallArrowRight.gif"));
	public static final ImageIcon CONNECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Connected.gif"));
	public static final ImageIcon METHOD_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BindingSelector/Method.gif"));
	public static final ImageIcon TOGGLE_ARROW_BOTTOM_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/toggleArrowBottom.gif"));
	public static final ImageIcon TOGGLE_ARROW_BOTTOM_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/toggleArrowBottomSelected.gif"));
	public static final ImageIcon TOGGLE_ARROW_TOP_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/toggleArrowTop.gif"));
	public static final ImageIcon TOGGLE_ARROW_TOP_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/toggleArrowTopSelected.gif"));
	public static final ImageIcon OK_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BindingSelector/OK.gif"));
	public static final ImageIcon WARNING_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BindingSelector/Warning.gif"));
	public static final ImageIcon ERROR_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BindingSelector/Error.gif"));

	// (expression editor)
	public static final ImageIcon IF_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BindingSelector/Operators/If.png"));
	public static final ImageIcon DIVISION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Divider.gif"));
	public static final ImageIcon MULTIPLICATION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Multiplication.gif"));
	public static final ImageIcon ADDITION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Addition.gif"));
	public static final ImageIcon SUBSTRACTION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Substraction.gif"));
	public static final ImageIcon EQUALS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Equals.gif"));
	public static final ImageIcon NOT_EQUALS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/NotEquals.gif"));
	public static final ImageIcon LESS_THAN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/LessThan.gif"));
	public static final ImageIcon LESS_THAN_OR_EQUALS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/LessThanOrEquals.gif"));
	public static final ImageIcon GREATER_THAN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/GreaterThan.gif"));
	public static final ImageIcon GREATER_THAN_OR_EQUALS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/GreaterThanOrEquals.gif"));
	public static final ImageIcon AND_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/AND.gif"));
	public static final ImageIcon OR_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BindingSelector/Operators/OR.gif"));
	public static final ImageIcon NOT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/NOT.gif"));
	public static final ImageIcon SIN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Sin.gif"));
	public static final ImageIcon ASIN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/ASin.gif"));
	public static final ImageIcon COS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Cos.gif"));
	public static final ImageIcon ACOS_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/ACos.gif"));
	public static final ImageIcon TAN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Tan.gif"));
	public static final ImageIcon EXP_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Exp.gif"));
	public static final ImageIcon LOG_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Log.gif"));
	public static final ImageIcon ATAN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/ATan.gif"));
	public static final ImageIcon POWER_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/Power.gif"));
	public static final ImageIcon SQRT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/BindingSelector/Operators/SQRT.gif"));

	// Misc
	public static final ImageIconResource LOCALIZATION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/GUI/LocalizationIcon.png"));
	public static final ImageIcon DROP_OK_CURSOR = new ImageIconResource(ResourceLocator.locateResource("Icons/Cursors/DropOKCursor.gif"));
	public static final ImageIcon DROP_KO_CURSOR = new ImageIconResource(ResourceLocator.locateResource("Icons/Cursors/DropKOCursor.gif"));

	public static final ImageIcon FOLDER_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Folder.gif"));

}

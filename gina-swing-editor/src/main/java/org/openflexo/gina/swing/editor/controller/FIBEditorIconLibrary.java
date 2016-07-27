/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.controller;

import javax.swing.ImageIcon;

import org.openflexo.icon.IconMarker;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Provides graphical resources used in the context of FIB
 * 
 * 
 * @author sylvain
 * 
 */
public class FIBEditorIconLibrary {

	public static final ImageIcon DELETE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Actions/Delete.gif"));
	public static final ImageIcon HELP_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Actions/Help.gif"));
	public static final ImageIcon REFRESH_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Actions/Refresh.gif"));

	public static final ImageIcon ROLE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/SmallRole.gif"));
	public static final ImageIcon ROOT_COMPONENT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/RootComponentIcon.png"));
	public static final ImageIcon PANEL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/PanelIcon.png"));
	public static final ImageIcon REFERENCE_COMPONENT_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/InternalFrameIcon.png"));
	public static final ImageIcon SPLIT_PANEL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/SplitPaneIcon.png"));
	public static final ImageIcon BUTTON_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ButtonIcon.png"));
	public static final ImageIcon CHECKBOX_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/CheckBoxIcon.png"));
	public static final ImageIcon DROPDOWN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/DropDownIcon.png"));
	public static final ImageIcon LABEL_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/LabelIcon.png"));
	public static final ImageIcon RADIOBUTTON_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/RadioButtonIcon.png"));
	public static final ImageIcon TABS_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/TabbedPaneIcon.png"));
	public static final ImageIcon TEXTAREA_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/TextAreaIcon.png"));
	public static final ImageIcon TEXTFIELD_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/TextFieldIcon.png"));
	public static final ImageIcon TABLE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/TableIcon.png"));
	public static final ImageIcon TABLE_COLUMN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/TableColumnIcon.png"));
	public static final ImageIcon TABLE_ACTION_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ActionIcon.png"));
	public static final ImageIcon BROWSER_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/TreeIcon.png"));
	public static final ImageIcon BROWSER_ELEMENT_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/BrowserElement.png"));
	public static final ImageIcon BROWSER_ELEMENT_CHILDREN_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/CheckBoxIcon.png"));
	public static final ImageIcon BROWSER_ELEMENT_ACTION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/ActionIcon.png"));
	public static final ImageIcon NUMBER_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/SpinnerIcon.png"));
	public static final ImageIcon IMAGE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/ImageIcon.png"));
	public static final ImageIcon CUSTOM_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/LayeredPaneIcon.png"));
	public static final ImageIcon GRAPH_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Small_BIRT.gif"));
	public static final ImageIcon GRAPH_FUNCTION_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/FunctionIcon.png"));

	public static final IconMarker DELETE = new IconMarker(
			new ImageIconResource(ResourceLocator.locateResource("Icons/Markers/Delete.png")), 8, 8);
	public static final IconMarker DUPLICATE = new IconMarker(
			new ImageIconResource(ResourceLocator.locateResource("Icons/Markers/Plus.png")), 8, 0);

}

package org.openflexo.fib.editor.controller;

import javax.swing.ImageIcon;

import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.ResourceLocator;

public class FIBEditorIconLibrary {
	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	public static final ImageIcon DELETE_ICON = new ImageIconResource(rl.locateResource("Icons/Actions/Delete.gif"));
	public static final ImageIcon HELP_ICON = new ImageIconResource(rl.locateResource("Icons/Actions/Help.gif"));
	public static final ImageIcon REFRESH_ICON = new ImageIconResource(rl.locateResource("Icons/Actions/Refresh.gif"));

	public static final ImageIcon ROLE_ICON = new ImageIconResource(rl.locateResource("Icons/SmallRole.gif"));
	public static final ImageIcon ROOT_COMPONENT_ICON = new ImageIconResource(rl.locateResource("Icons/RootComponentIcon.png"));
	public static final ImageIcon PANEL_ICON = new ImageIconResource(rl.locateResource("Icons/PanelIcon.png"));
	public static final ImageIcon REFERENCE_COMPONENT_ICON = new ImageIconResource(rl.locateResource("Icons/InternalFrameIcon.png"));
	public static final ImageIcon SPLIT_PANEL_ICON = new ImageIconResource(rl.locateResource("Icons/SplitPaneIcon.png"));
	public static final ImageIcon BUTTON_ICON = new ImageIconResource(rl.locateResource("Icons/ButtonIcon.png"));
	public static final ImageIcon CHECKBOX_ICON = new ImageIconResource(rl.locateResource("Icons/CheckBoxIcon.png"));
	public static final ImageIcon DROPDOWN_ICON = new ImageIconResource(rl.locateResource("Icons/DropDownIcon.png"));
	public static final ImageIcon LABEL_ICON = new ImageIconResource(rl.locateResource("Icons/LabelIcon.png"));
	public static final ImageIcon RADIOBUTTON_ICON = new ImageIconResource(rl.locateResource("Icons/RadioButtonIcon.png"));
	public static final ImageIcon TABS_ICON = new ImageIconResource(rl.locateResource("Icons/TabbedPaneIcon.png"));
	public static final ImageIcon TEXTAREA_ICON = new ImageIconResource(rl.locateResource("Icons/TextAreaIcon.png"));
	public static final ImageIcon TEXTFIELD_ICON = new ImageIconResource(rl.locateResource("Icons/TextFieldIcon.png"));
	public static final ImageIcon TABLE_ICON = new ImageIconResource(rl.locateResource("Icons/TableIcon.png"));
	public static final ImageIcon TREE_ICON = new ImageIconResource(rl.locateResource("Icons/TreeIcon.png"));
	public static final ImageIcon NUMBER_ICON = new ImageIconResource(rl.locateResource("Icons/SpinnerIcon.png"));

	public static final ImageIconResource FIX_PROPOSAL_ICON = new ImageIconResource(rl.locateResource("Icons/Validation/FixProposal.gif"));
	public static final ImageIconResource INFO_ISSUE_ICON = new ImageIconResource(rl.locateResource("Icons/Validation/Info.gif"));
	public static final ImageIconResource FIXABLE_ERROR_ICON = new ImageIconResource(rl.locateResource("Icons/Validation/FixableError.gif"));
	public static final ImageIconResource UNFIXABLE_ERROR_ICON = new ImageIconResource(rl.locateResource("Icons/Validation/UnfixableError.gif"));
	public static final ImageIconResource FIXABLE_WARNING_ICON = new ImageIconResource(rl.locateResource("Icons/Validation/FixableWarning.gif"));
	public static final ImageIconResource UNFIXABLE_WARNING_ICON = new ImageIconResource(rl.locateResource("Icons/Validation/UnfixableWarning.gif"));

}

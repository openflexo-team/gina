package org.openflexo.fib.swing;

import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Provides graphical resources used in the context of FIB
 * 
 * 
 * @author sylvain
 * 
 */
public class FIBUtilsIconLibrary {

	public static final ImageIconResource FIX_PROPOSAL_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/FixProposal.gif"));
	public static final ImageIconResource INFO_ISSUE_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/Info.gif"));
	public static final ImageIconResource FIXABLE_ERROR_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/FixableError.gif"));
	public static final ImageIconResource UNFIXABLE_ERROR_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/UnfixableError.gif"));
	public static final ImageIconResource FIXABLE_WARNING_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/FixableWarning.gif"));
	public static final ImageIconResource UNFIXABLE_WARNING_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/UnfixableWarning.gif"));

}

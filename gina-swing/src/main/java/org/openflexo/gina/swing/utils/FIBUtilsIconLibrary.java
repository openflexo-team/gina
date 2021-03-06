/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils;

import org.openflexo.icon.IconMarker;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;

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
	public static final ImageIconResource VALIDATED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Validation/Validated.gif"));

	public static final IconMarker WARNING = new IconMarker(
			new ImageIconResource(ResourceLocator.locateResource("Icons/Markers/Warning.gif")), 0, 9);
	public static final IconMarker ERROR = new IconMarker(new ImageIconResource(ResourceLocator.locateResource("Icons/Markers/Error.gif")),
			0, 9);

}

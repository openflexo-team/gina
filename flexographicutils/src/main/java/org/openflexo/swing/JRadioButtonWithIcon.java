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

package org.openflexo.swing;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JRadioButton;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class JRadioButtonWithIcon extends JRadioButton {

	public JRadioButtonWithIcon(String text, File iconFile, boolean selected) {
		super();
		// Define an HTML fragment with an icon on the left and text on the
		// right.
		// The elements are embedded in a 3-column table.
		String label = null;
		try {
			label = "<html><table cellpadding=0><tr><td><img src=\""
			// The location of the icon
					+ iconFile.toURI().toURL() + "\"></td><td width="

					// The gap, in pixels, between icon and text
					+ 3 + "><td>"

					// Retrieve the current label text
					+ text + "</td></tr></table></html>";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		setText(label);
		setSelected(selected);
	}

	public JRadioButtonWithIcon(String text, File iconFile) {
		this(text, iconFile, false);
	}
}

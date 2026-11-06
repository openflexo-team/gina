/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * 
 * This file is part of Flexolocalization, a component of the software infrastructure 
 * developed at Openflexo.
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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.localization;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import org.openflexo.localization.LocalizedSwing;

/**
 * Builds Swing components whose text follows the language and the edition of the dictionaries: the component is registered in the
 * localizer, which updates it when one of them changes (see {@link LocalizedDelegate#updateGUILocalized()}).<br>
 * Use it as <code>LocalizedSwing.bind(new JButton(locales.localizedForKey("ok")), locales, "ok")</code>.
 */
public final class LocalizedSwing {

	private LocalizedSwing() {
	}

	/**
	 * Register supplied component, whose text was built from <code>key</code> by the caller, and return it.
	 */
	public static <T extends Component> T bind(T component, LocalizedDelegate localizer, String key) {
		if (component != null && localizer != null && key != null) {
			localizer.localizedForKey(key, component);
		}
		return component;
	}

	/**
	 * Return a titled border whose title is the one of <code>key</code> and follows the localizer.
	 */
	public static TitledBorder titledBorder(LocalizedDelegate localizer, String key) {
		TitledBorder returned = LocalizedSwing.titledBorder(localizer, key);
		localizer.localizedForKey(key, returned);
		return returned;
	}
}

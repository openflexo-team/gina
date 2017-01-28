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

import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.openflexo.kvc.ChoiceList;

/**
 * @author gpolet
 * 
 */
public class LookAndFeel implements ChoiceList<LookAndFeel> {

	private static final Vector<LookAndFeel> availableValues = new Vector<>();

	public static LookAndFeel getDefaultLookAndFeel() {
		for (LookAndFeel feel : availableValues()) {
			if (feel.getClassName().equals(UIManager.getSystemLookAndFeelClassName())) {
				return feel;
			}
		}
		return availableValues().firstElement();
	}

	public static Vector<LookAndFeel> availableValues() {
		if (availableValues.size() == 0) {
			LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
			for (int i = 0; i < lafs.length; i++) {
				LookAndFeelInfo feel = lafs[i];
				availableValues.add(new LookAndFeel(feel));
			}
		}
		return availableValues;
	}

	private final LookAndFeelInfo info;

	/**
	 *
	 */
	public LookAndFeel(LookAndFeelInfo info) {
		this.info = info;
	}

	public String getClassName() {
		return info.getClassName();
	}

	public String getName() {
		return info.getName();
	}

	/**
	 * Overrides getAvailableValues
	 * 
	 * @see org.openflexo.kvc.ChoiceList#getAvailableValues()
	 */
	@Override
	public Vector<LookAndFeel> getAvailableValues() {
		return availableValues;
	}

}

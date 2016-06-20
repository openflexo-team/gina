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

package org.openflexo.gina.model.container.layout;

import org.openflexo.gina.model.container.FIBPanel.Layout;

public class TwoColsLayoutConstraints extends ComponentConstraints {

	private static final String LOCATION = "location";
	private static final String EXPAND_VERTICALLY = "expandVertically";
	private static final String EXPAND_HORIZONTALLY = "expandHorizontally";

	public TwoColsLayoutLocation getLocation() {
		return getEnumValue(LOCATION, TwoColsLayoutLocation.class, TwoColsLayoutLocation.center);
	}

	public void setLocation(TwoColsLayoutLocation location) {
		setEnumValue(LOCATION, location);
	}

	public boolean getExpandVertically() {
		return getBooleanValue(EXPAND_VERTICALLY, false);
	}

	public void setExpandVertically(boolean flag) {
		setBooleanValue(EXPAND_VERTICALLY, flag);
	}

	public boolean getExpandHorizontally() {
		return getBooleanValue(EXPAND_HORIZONTALLY, false);
	}

	public void setExpandHorizontally(boolean flag) {
		setBooleanValue(EXPAND_HORIZONTALLY, flag);
	}

	public static enum TwoColsLayoutLocation {
		left, right, center;
	}

	private static final String INSETS_TOP = "insetsTop";
	private static final String INSETS_BOTTOM = "insetsBottom";
	private static final String INSETS_LEFT = "insetsLeft";
	private static final String INSETS_RIGHT = "insetsRight";

	public int getInsetsTop() {
		return getIntValue(INSETS_TOP, 0);
	}

	public void setInsetsTop(int insetsTop) {
		setIntValue(INSETS_TOP, insetsTop);
	}

	public int getInsetsBottom() {
		return getIntValue(INSETS_BOTTOM, 0);
	}

	public void setInsetsBottom(int insetsBottom) {
		setIntValue(INSETS_BOTTOM, insetsBottom);
	}

	public int getInsetsLeft() {
		return getIntValue(INSETS_LEFT, 0);
	}

	public void setInsetsLeft(int insetsLeft) {
		setIntValue(INSETS_LEFT, insetsLeft);
	}

	public int getInsetsRight() {
		return getIntValue(INSETS_RIGHT, 0);
	}

	public void setInsetsRight(int insetsRight) {
		setIntValue(INSETS_RIGHT, insetsRight);
	}

	public TwoColsLayoutConstraints() {
		super();
	}

	public TwoColsLayoutConstraints(TwoColsLayoutLocation location, boolean expandHorizontally, boolean expandVertically) {
		super();
		setLocation(location);
		setExpandHorizontally(expandHorizontally);
		setExpandVertically(expandVertically);
	}

	public TwoColsLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	public TwoColsLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	@Override
	protected Layout getType() {
		return Layout.twocols;
	}
}

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

import javax.swing.Icon;

import org.openflexo.gina.model.FIBModelObject;

public class EditorAction {

	private String actionName;
	private Icon actionIcon;
	private ActionPerformer performer;
	private ActionAvailability availability;

	public EditorAction(String actionName, Icon actionIcon, ActionPerformer performer, ActionAvailability availability) {
		super();
		this.actionName = actionName;
		this.actionIcon = actionIcon;
		this.performer = performer;
		this.availability = availability;
	}

	public static abstract class ActionPerformer {
		public abstract FIBModelObject performAction(FIBModelObject object);
	}

	public static abstract class ActionAvailability {
		public abstract boolean isAvailableFor(FIBModelObject object);
	}

	public String getActionName() {
		return actionName;
	}

	public Icon getActionIcon() {
		return actionIcon;
	}

	public ActionPerformer getPerformer() {
		return performer;
	}

	public ActionAvailability getAvailability() {
		return availability;
	}
}

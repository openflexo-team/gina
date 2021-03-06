/**
 * 
 * Copyright (c) 2013-2015, Openflexo
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

package org.openflexo.gina.swing.editor.view.container.layout;

import java.awt.Dimension;
import java.util.List;

import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.container.layout.ComponentConstraints;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.swing.editor.view.PlaceHolder;

/**
 * A layout manager working in a {@link FIBContainer} providing some editing facilities<br>
 * Provides edition placeholders
 * 
 * @param <C>
 *            type of technology-specific container this layout manager is managing
 * @param <C2>
 *            type of technology-specific contents this layout manager is layouting
 * @param <CC>
 *            type of component constraints
 *
 * @author sylvain
 */
public interface JFIBEditableLayoutManager<C, C2, CC extends ComponentConstraints> extends FIBLayoutManager<C, C2, CC> {

	/**
	 * Make placeholders for component implementing this layout<br>
	 * This method is called during a drag-and-drop scheme initiated from the palette
	 */
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize);

}

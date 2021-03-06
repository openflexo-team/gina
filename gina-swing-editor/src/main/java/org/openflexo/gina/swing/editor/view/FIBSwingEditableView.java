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

package org.openflexo.gina.swing.editor.view;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.view.FIBView;

/**
 * Represent a {@link FIBView} with editing features
 * 
 * @author sylvain
 *
 * @param <M>
 * @param <C>
 */
public interface FIBSwingEditableView<M extends FIBComponent, J extends JComponent> extends JFIBView<M, J> {

	public static final int OPERATOR_ICON_SPACE = 18;

	public abstract FIBEditorController getEditorController();

	// public abstract boolean hasValue();

	public FIBSwingEditableViewDelegate<M, J> getDelegate();

	/**
	 * Return JComponent on which drag and drop is to be applied
	 * 
	 * @return
	 */
	public JComponent getDraggableComponent();

	/**
	 * Return boolean indicating that in EDIT mode, this view is the first view on an operator<br>
	 * Usefull to rendering features (give extra space to render operator icon)
	 * 
	 * @return
	 */
	public boolean isOperatorContentsStart();

	/**
	 * Sets boolean indicating that in EDIT mode, this view is the first view on an operator<br>
	 * Usefull to rendering features (give extra space to render operator icon)
	 * 
	 * @param flag
	 */
	public void setOperatorContentsStart(boolean flag);

	public static void updateOperatorContentsStart(FIBSwingEditableView<?, ?> view, boolean flag) {
		if (flag) {
			if (view.getResultingJComponent().getBorder() == null) {
				view.getResultingJComponent().setBorder(BorderFactory.createEmptyBorder(0, OPERATOR_ICON_SPACE, 0, 0));
			}
			else {
				view.getResultingJComponent().setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(0, OPERATOR_ICON_SPACE, 0, 0), view.getResultingJComponent().getBorder()));
			}
		}
		else {
			if (view.getResultingJComponent().getBorder() instanceof CompoundBorder) {
				view.getResultingJComponent().setBorder(((CompoundBorder) view.getResultingJComponent().getBorder()).getInsideBorder());
			}
			else {
				view.getResultingJComponent().setBorder(null);
			}

		}
	}

}

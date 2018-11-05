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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.logging.FlexoLogger;

public class FIBSwingEditableContainerViewDelegate<M extends FIBContainer, J extends JComponent>
		extends FIBSwingEditableViewDelegate<M, J> {

	static final Logger logger = FlexoLogger.getLogger(FIBSwingEditableContainerViewDelegate.class.getPackage().getName());

	private List<PlaceHolder> placeholders;
	private List<OperatorDecorator> operatorDecorators = null;

	public FIBSwingEditableContainerViewDelegate(FIBSwingEditableContainerView<M, J> view) {
		super(view);
	}

	@Override
	public FIBSwingEditableContainerView<M, J> getView() {
		return (FIBSwingEditableContainerView<M, J>) super.getView();
	}

	public List<PlaceHolder> getPlaceholders() {
		return placeholders;
	}

	public void handlePlaceHolders(List<PlaceHolder> placeholders) {
		this.placeholders = placeholders;
		getView().getResultingJComponent().repaint();
	}

	public void dismissPlaceHolders() {
		if (placeholders != null) {
			for (PlaceHolder ph : placeholders) {
				ph.setVisible(false);
			}
		}
		if (getView().getResultingJComponent() != null) {
			getView().getResultingJComponent().repaint();
		}
	}

	public void deletePlaceHolders() {
		this.placeholders = null;
	}

	public List<OperatorDecorator> getOperatorDecorators() {
		if (operatorDecorators == null) {
			operatorDecorators = getView().makeOperatorDecorators();
		}
		return operatorDecorators;
	}

	public void updateOperatorDecorators() {
		operatorDecorators = null;
	}

	private FIBModelObject lastSelectedObject = null;

	@Override
	public FIBModelObject getLastSelectedObject() {
		if (lastSelectedObject != null) {
			return lastSelectedObject;
		}
		else {
			return getFIBComponent();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		OperatorDecorator d = getOperatorDecorator(e.getPoint());
		if (d != null) {
			lastSelectedObject = d.getOperator();
			getEditorController().setSelectedObject(d.getOperator());
			return;
		}
		else {
			lastSelectedObject = null;
		}

		super.mouseClicked(e);
	}

	public OperatorDecorator getOperatorDecorator(Point location) {
		if (operatorDecorators != null) {
			for (OperatorDecorator d : operatorDecorators) {
				//System.out.println(" > " + d.getOperator().getName() + " ? " + d.getBounds());
				if (d.getBounds().contains(location)) {
					return d;
				}
			}
		}
		return null;
	}
}

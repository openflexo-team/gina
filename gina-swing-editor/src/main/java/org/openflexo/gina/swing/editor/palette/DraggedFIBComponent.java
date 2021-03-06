/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.gina.swing.editor.palette;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableViewDelegate.FIBDropTarget;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.logging.FlexoLogger;

import java.awt.*;
import java.util.logging.Logger;

public class DraggedFIBComponent implements FIBDraggable {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditorController.class.getPackage().getName());

	private final FIBComponent draggedComponent;

	public DraggedFIBComponent(FIBComponent draggedComponent) {
		this.draggedComponent = draggedComponent;
	}

	@Override
	public void enableDragging() {
		System.out.println("Enable dragging for " + draggedComponent);
	}

	@Override
	public void disableDragging() {
		System.out.println("Disable dragging for " + draggedComponent);
	}

	@Override
	public boolean acceptDragging(FIBDropTarget target) {
		// System.out.println("acceptDragging ? for component: " +
		// target.getFIBComponent() + " place holder: " +
		// target.getPlaceHolder());
		return true;
	}

	@Override
	public boolean elementDragged(FIBDropTarget target, DropListener dropListener, Point pt) {
		PlaceHolder ph = target.getPlaceHolder(dropListener, pt);
		try {

			if (ph != null) {
				ph.willDelete();
				FIBContainer oldParent = draggedComponent.getParent();
				int oldIndex = -1;
				if (draggedComponent.getParent() != null) {
					oldIndex = draggedComponent.getParent().getSubComponents().indexOf(draggedComponent);
					// System.out.println("OldIndex was: " + oldIndex);
					draggedComponent.getParent().removeFromSubComponents(draggedComponent);
				}
				ph.insertComponent(draggedComponent, oldIndex);
				ph.hasDeleted();
				oldParent.notifyComponentMoved(draggedComponent);
				return true;
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Unexpected exception: " + e);
			return false;
		}

	}
}

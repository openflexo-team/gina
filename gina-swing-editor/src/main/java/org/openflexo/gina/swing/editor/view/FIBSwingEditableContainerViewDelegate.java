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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.gina.model.FIBContainer;
import org.openflexo.logging.FlexoLogger;

public class FIBSwingEditableContainerViewDelegate<M extends FIBContainer, J extends JComponent>
		extends FIBSwingEditableViewDelegate<M, J> {

	static final Logger logger = FlexoLogger.getLogger(FIBSwingEditableContainerViewDelegate.class.getPackage().getName());

	public FIBSwingEditableContainerViewDelegate(FIBSwingEditableContainerView<M, J> view) {

		super(view);

		/*if (view.getPlaceHolders() != null) {
			for (PlaceHolder ph : view.getPlaceHolders()) {
				// Listen to drag'n'drop events
				new FIBDropTarget(ph);
			}
		}*/
	}

	@Override
	public FIBSwingEditableContainerView<M, J> getView() {
		return (FIBSwingEditableContainerView<M, J>) super.getView();
	}

	private final List<Object> placeHolderVisibleRequesters = new ArrayList<Object>();

	public void addToPlaceHolderVisibleRequesters(Object requester) {
		if (!placeHolderVisibleRequesters.contains(requester)) {
			placeHolderVisibleRequesters.add(requester);
			updatePlaceHoldersVisibility();
		}
	}

	public void removeFromPlaceHolderVisibleRequesters(Object requester) {
		if (placeHolderVisibleRequesters.remove(requester)) {
			updatePlaceHoldersVisibility();
		}
	}

	private boolean updatePlaceHoldersVisibilityRequested = false;

	private void updatePlaceHoldersVisibility() {
		if (updatePlaceHoldersVisibilityRequested) {
			return;
		}
		updatePlaceHoldersVisibilityRequested = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (getView() != null) {
					boolean visible = placeHolderVisibleRequesters.size() > 0;
					if (getView().getPlaceHolders() != null) {
						/*for (PlaceHolder ph : getView().getPlaceHolders()) {
							ph.setVisible(visible);
						}*/
						System.out.println("Tiens ce serait pas mal d'afficher les placeholders, la");
					}
					updatePlaceHoldersVisibilityRequested = false;
				}
			}
		});
	}

}

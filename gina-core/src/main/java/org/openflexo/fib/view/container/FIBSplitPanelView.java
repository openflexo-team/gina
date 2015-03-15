/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.fib.view.container;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.SplitLayoutConstraints;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.KnobDividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;

public class FIBSplitPanelView<T> extends FIBContainerView<FIBSplitPanel, JXMultiSplitPane, T> {

	private static final Logger logger = Logger.getLogger(FIBSplitPanelView.class.getPackage().getName());

	private JXMultiSplitPane splitPane;
	private MultiSplitLayout layout;

	public FIBSplitPanelView(FIBSplitPanel model, FIBController controller) {
		super(model, controller);
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected JXMultiSplitPane createJComponent() {
		layout = new MultiSplitLayout(getComponent().getSplitLayoutFactory());
		layout.setLayoutByWeight(false);
		layout.setFloatingDividers(true);

		layout.setModel(getComponent().getSplit());

		splitPane = new JXMultiSplitPane(layout);

		splitPane.setDividerPainter(new KnobDividerPainter());

		updateLayout();

		// layout.setLayoutByWeight(false);
		// layout.setFloatingDividers(false);

		return splitPane;
	}

	public MultiSplitLayout getLayout() {
		return layout;
	}

	@Override
	public JXMultiSplitPane getJComponent() {
		return splitPane;
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {

		for (FIBComponent subComponent : getNotHiddenSubComponents()) {
			FIBView subView = getController().viewForComponent(subComponent);
			if (subView == null) {
				subView = getController().buildView(subComponent);
			}
			// FIBView subView = getController().buildView(subComponent);
			// if (subView != null) {
			registerViewForComponent(subView, subComponent);
			registerComponentWithConstraints(subView.getResultingJComponent(),
					((SplitLayoutConstraints) subComponent.getConstraints()).getSplitIdentifier());
			// }
		}
	}

	@Override
	public synchronized void updateLayout() {

		logger.info("relayout split panel " + getComponent());

		layout.setModel(getComponent().getSplit());

		/*if (getSubViews() != null) {
			for (FIBView v : getSubViews().values()) {
				if (v.getComponent().isDeleted()) {
					v.delete();
				}
			}
		}*/
		getJComponent().removeAll();

		buildSubComponents();
		// updateDataObject(getDataObject());

		update();

		getJComponent().revalidate();
		getJComponent().repaint();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				layout.setFloatingDividers(false);
			}
		});
	}
	/*@Override
	public void updateLanguage() {
		super.updateLanguage();
		int index = 0;
		for (FIBView v : subViews) {
			if (v.getComponent() instanceof FIBTab) {
				tabbedPane.setTitleAt(index, getLocalized(((FIBTab) v.getComponent()).getTitle()));
			} else {
				logger.warning("Unexpected component found in TabPanel: " + v.getComponent());
			}
			index++;
		}
	}*/

	/*	public void setSelectedIndex(int index) {
			getJComponent().setSelectedIndex(index);
		}*/
}

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

package org.openflexo.gina.swing.view.container;

import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.container.impl.FIBSplitPanelViewImpl;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.KnobDividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;

/**
 * Swing implementation of a panel split into a given policy, with adjustable
 * sliders<br>
 * Implementation is based on JXMultiSplitPane
 * 
 * 
 * @author sylvain
 */
public class JFIBSplitPanelView extends FIBSplitPanelViewImpl<JXMultiSplitPane, JComponent> implements
		JFIBView<FIBSplitPanel, JXMultiSplitPane> {

	private static final Logger logger = Logger.getLogger(JFIBSplitPanelView.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JPanel with
	 * a given layout<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingSplitPanelRenderingAdapter extends SwingRenderingAdapter<JXMultiSplitPane> implements
			SplitPanelRenderingAdapter<JXMultiSplitPane, JComponent> {

		@Override
		public void addComponent(JComponent child, JXMultiSplitPane parent, Object constraints) {
			if (constraints instanceof String) {
				String splitIdentifier = (String) constraints;
				parent.add(child, splitIdentifier);
			} else {
				logger.warning("Unexpected constraint: " + constraints);
				parent.add(child);
			}
		}
	}

	private MultiSplitLayout layout;

	public JFIBSplitPanelView(FIBSplitPanel model, FIBController controller) {
		super(model, controller, new SwingSplitPanelRenderingAdapter());
		// updateLayout();
	}

	@Override
	public SwingSplitPanelRenderingAdapter getRenderingAdapter() {
		return (SwingSplitPanelRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected JXMultiSplitPane makeTechnologyComponent() {
		layout = new MultiSplitLayout(getComponent().getSplitLayoutFactory());
		layout.setLayoutByWeight(false);
		layout.setFloatingDividers(true);

		layout.setModel(getComponent().getSplit());

		JXMultiSplitPane splitPane = new JXMultiSplitPane(layout);

		splitPane.setDividerPainter(new KnobDividerPainter());

		return splitPane;
	}

	public MultiSplitLayout getLayout() {
		return layout;
	}

	@Override
	protected void clearContainer() {
		getJComponent().removeAll();
	}

	@Override
	public synchronized void updateLayout() {

		logger.info("relayout split panel " + getComponent());

		layout.setModel(getComponent().getSplit());

		/*
		 * if (getSubViews() != null) { for (FIBViewImpl v :
		 * getSubViews().values()) { if (v.getComponent().isDeleted()) {
		 * v.delete(); } } }
		 */

		clearContainer();

		buildSubComponents();
		// updateDataObject(getDataObject());

		update();

		getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				layout.setFloatingDividers(false);
			}
		});
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
	}

}

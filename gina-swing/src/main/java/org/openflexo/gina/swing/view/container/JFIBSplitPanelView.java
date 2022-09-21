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

import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.openflexo.connie.expr.ExpressionEvaluator;
import org.openflexo.connie.java.expr.JavaExpressionEvaluator;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBSplitPanel;
import org.openflexo.gina.model.container.layout.FIBLayoutManager;
import org.openflexo.gina.model.container.layout.SplitLayoutConstraints;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.container.impl.FIBSplitPanelViewImpl;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.KnobDividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;

/**
 * Swing implementation of a panel split into a given policy, with adjustable sliders<br>
 * Implementation is based on JXMultiSplitPane
 * 
 * 
 * @author sylvain
 */
public class JFIBSplitPanelView extends FIBSplitPanelViewImpl<JXMultiSplitPane, JComponent>
		implements JFIBView<FIBSplitPanel, JXMultiSplitPane> {

	private static final Logger logger = Logger.getLogger(JFIBSplitPanelView.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JXMultiSplitPane<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingSplitPanelRenderingAdapter extends SwingRenderingAdapter<JXMultiSplitPane>
			implements SplitPanelRenderingAdapter<JXMultiSplitPane, JComponent> {

		@Override
		public Color getDefaultForegroundColor(JXMultiSplitPane component) {
			return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JXMultiSplitPane component) {
			return UIManager.getColor("SplitPane.background");
		}

	}

	private MultiSplitLayout layout;

	public JFIBSplitPanelView(FIBSplitPanel model, FIBController controller) {
		super(model, controller, new SwingSplitPanelRenderingAdapter());
		// updateLayout();
	}

	@Override
	public ExpressionEvaluator getEvaluator() {
		return new JavaExpressionEvaluator(this);
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

		JXMultiSplitPane splitPane = new JXMultiSplitPane(layout) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintAdditionalInfo(g);
			}
		};

		splitPane.setDividerPainter(new KnobDividerPainter());

		return splitPane;
	}

	public MultiSplitLayout getLayout() {
		return layout;
	}

	@Override
	public FIBLayoutManager<JXMultiSplitPane, JComponent, ?> getLayoutManager() {
		// TODO
		return null;
	}

	protected void paintAdditionalInfo(Graphics g) {
	}

	@Override
	protected void clearContainer() {
		getJComponent().removeAll();
	}

	@Override
	public void changeLayout() {
		// TODO is it enough ???
		updateLayout(false);
	}

	@Override
	public synchronized void updateLayout(boolean force) {
		layout.setModel(getComponent().getSplit());
		clearContainer();
		buildSubComponents();
		// update();
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

	@Override
	public void addSubComponentsAndDoLayout() {
		for (FIBComponent c : getComponent().getSubComponents()) {
			JFIBView<?, JComponent> subComponentView = (JFIBView<?, JComponent>) getSubViewsMap().get(c);
			if (c.getConstraints() instanceof SplitLayoutConstraints) {
				getTechnologyComponent().add(subComponentView.getResultingJComponent(),
						((SplitLayoutConstraints) c.getConstraints()).getSplitIdentifier());
			}
		}
	}

	@Override
	public void revalidateAndRepaint() {
		getRenderingAdapter().revalidateAndRepaint(getTechnologyComponent());
	}

}

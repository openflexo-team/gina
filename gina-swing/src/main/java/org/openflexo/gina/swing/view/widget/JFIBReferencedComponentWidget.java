/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBReferencedComponent;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBReferencedComponentWidget.JReferenceComponentPanel;
import org.openflexo.gina.view.widget.impl.FIBReferencedComponentWidgetImpl;

/**
 * Swing implementation for a component that references an other component<br>
 * This component allows to reuse an other component, and embed it into a widget<br>
 * This implementation manages here a cache of views (@see {@link #componentViews}).
 * 
 * Referenced component may be statically or dynamically referenced
 * 
 * @author sguerin
 * 
 */
public class JFIBReferencedComponentWidget extends FIBReferencedComponentWidgetImpl<JReferenceComponentPanel>
		implements JFIBView<FIBReferencedComponent, JReferenceComponentPanel> {

	private static final Logger logger = Logger.getLogger(JFIBReferencedComponentWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing referenced component<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingReferencedComponentRenderingAdapter extends SwingRenderingAdapter<JReferenceComponentPanel>
			implements ReferencedComponentRenderingAdapter<JReferenceComponentPanel> {

		@Override
		public Color getDefaultForegroundColor(JReferenceComponentPanel component) {
			return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JReferenceComponentPanel component) {
			return UIManager.getColor("Panel.background");
		}

	}

	public static SwingReferencedComponentRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingReferencedComponentRenderingAdapter();

	private Map<FIBComponent, JFIBView<?, JReferenceComponentPanel>> componentViews;

	public JFIBReferencedComponentWidget(FIBReferencedComponent model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
		componentViews = new HashMap<>();
	}

	@Override
	public SwingReferencedComponentRenderingAdapter getRenderingAdapter() {
		return (SwingReferencedComponentRenderingAdapter) super.getRenderingAdapter();
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
	protected JReferenceComponentPanel makeTechnologyComponent() {
		return new JReferenceComponentPanel(this);
	}

	@Override
	public JFIBView<?, JReferenceComponentPanel> getReferencedComponentView() {
		if (getTechnologyComponent() != null) {
			return getTechnologyComponent().getReferencedComponentView();
		}
		return null;
	}

	@Override
	protected JFIBView<?, JReferenceComponentPanel> makeReferencedComponentView() {
		JFIBView<?, JReferenceComponentPanel> returned = (JFIBView<?, JReferenceComponentPanel>) super.makeReferencedComponentView();
		componentViews.put(getReferencedComponent(), returned);
		return returned;
	}

	private JFIBView<?, JReferenceComponentPanel> retrieveReferencedComponentView() {
		JFIBView<?, JReferenceComponentPanel> returned = componentViews.get(getReferencedComponent());
		if (returned == null) {
			// Not found in the cache, rebuild it
			returned = makeReferencedComponentView();
		}
		else {
			// Found in the cache
		}
		return returned;
	}

	@Override
	protected void referencedComponentChanged() {
		if (getTechnologyComponent() != null) {
			getTechnologyComponent().updateReferenceComponent();
		}
	}

	@Override
	public synchronized void delete() {
		for (JFIBView<?, JReferenceComponentPanel> v : componentViews.values()) {
			v.delete();
		}
		componentViews.clear();
		super.delete();
	}

	@SuppressWarnings("serial")
	public static class JReferenceComponentPanel extends JPanel {
		private JLabel invalidComponentlabel;
		private JFIBReferencedComponentWidget widget;
		private JFIBView<?, JReferenceComponentPanel> referencedComponentView;

		public JReferenceComponentPanel(JFIBReferencedComponentWidget widget) {
			super(new BorderLayout());
			this.widget = widget;
			setOpaque(false);
			invalidComponentlabel = new JLabel("", JLabel.CENTER);
			invalidComponentlabel.setFocusable(false);
			updateReferenceComponent();
		}

		public JFIBReferencedComponentWidget getWidget() {
			return widget;
		}

		public JFIBView<?, JReferenceComponentPanel> getReferencedComponentView() {
			return referencedComponentView;
		}

		protected void updateReferenceComponent() {

			/*if (referencedComponentView != null) {
				referencedComponentView.delete();
			}*/

			// Remove all components inside container
			removeAll();

			// Call hideView() on old referencedComponentView to stop all observing schemes
			if (referencedComponentView != null) {
				referencedComponentView.hideView();
			}

			if (widget.getReferencedComponent() == null) {
				invalidComponentlabel.setText("< Reference component >");
				add(invalidComponentlabel, BorderLayout.CENTER);
				setBorder(BorderFactory.createEtchedBorder());
			}
			else {
				// referencedComponentView = (JFIBView<FIBComponent, JReferenceComponentPanel>) widget.makeReferencedComponentView();

				referencedComponentView = widget.retrieveReferencedComponentView();

				widget.embeddedFIBController = referencedComponentView.getController();
				// widget.embeddedFIBController.setDataObject(widget.getValue());

				if (referencedComponentView == null) {
					invalidComponentlabel.setText("< Invalid component >");
					add(invalidComponentlabel, BorderLayout.CENTER);
					setBorder(BorderFactory.createEtchedBorder());
				}
				else {
					add(referencedComponentView.getResultingJComponent(), BorderLayout.CENTER);
					setBorder(null);
					getWidget().updateReferencedComponentView();
				}

			}
			revalidate();
			repaint();
		}

	}

}

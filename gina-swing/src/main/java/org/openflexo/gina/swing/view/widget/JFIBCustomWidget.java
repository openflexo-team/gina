/**
 * 
 * Copyright (c) 2013-2014, Openflexo
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

package org.openflexo.gina.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBCustomWidget.JCustomComponentPanel;
import org.openflexo.gina.view.widget.impl.FIBCustomWidgetImpl;

/**
 * Defines an abstract custom widget
 * 
 * @author sguerin
 * 
 */
public class JFIBCustomWidget<CC extends JComponent & FIBCustomComponent<T>, T>
		extends FIBCustomWidgetImpl<JCustomComponentPanel<CC, T>, CC, T>implements JFIBView<FIBCustom, JCustomComponentPanel<CC, T>> {

	private static final Logger LOGGER = Logger.getLogger(JFIBCustomWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing custom component<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingCustomComponentRenderingAdapter<J extends JComponent, T> extends SwingRenderingAdapter<J>
			implements CustomComponentRenderingAdapter<J, T> {

		// private final JLabel ERROR_LABEL = new JLabel("<Cannot instanciate component>");

		// Return default label if technology component cannot be set
		/*@Override
		public JComponent getJComponent(JComponent technologyComponent) {
			if (technologyComponent == null) {
				return ERROR_LABEL;
			}
			return technologyComponent;
		}*/

		@Override
		public Color getDefaultForegroundColor(JComponent component) {
			return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JComponent component) {
			return UIManager.getColor("Panel.background");
		}

	}

	public JFIBCustomWidget(FIBCustom model, FIBController controller) {
		super(model, controller, new SwingCustomComponentRenderingAdapter<JCustomComponentPanel<CC, T>, T>());
	}

	@Override
	public SwingCustomComponentRenderingAdapter<JCustomComponentPanel<CC, T>, T> getRenderingAdapter() {
		return (SwingCustomComponentRenderingAdapter<JCustomComponentPanel<CC, T>, T>) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	/*@Override
	protected C makeTechnologyComponent() {
		C customComponent = makeCustomComponent((Class) getWidget().getComponentClass(),
				(Class<T>) TypeUtils.getBaseClass(getWidget().getDataType()), getController());
		return customComponent;
	}*/

	@Override
	protected JCustomComponentPanel<CC, T> makeTechnologyComponent() {
		return new JCustomComponentPanel<CC, T>(this);
	}

	@Override
	public CC getCustomComponent() {
		if (getTechnologyComponent() != null) {
			return getTechnologyComponent().getCustomComponent();
		}
		return null;
	}

	@Override
	protected void updateCustomComponent() {
		if (getTechnologyComponent() != null) {
			getTechnologyComponent().updateCustomComponent();
		}
		else {
			technologyComponent = makeTechnologyComponent();
		}
	}

	@SuppressWarnings("serial")
	public static class JCustomComponentPanel<CC extends JComponent & FIBCustomComponent<T>, T> extends JPanel {
		private JLabel invalidComponentlabel;
		private JFIBCustomWidget<CC, T> widget;
		private CC customComponent;

		public JCustomComponentPanel(JFIBCustomWidget<CC, T> widget) {
			super(new BorderLayout());
			this.widget = widget;
			setOpaque(false);
			invalidComponentlabel = new JLabel("", JLabel.CENTER);
			invalidComponentlabel.setFocusable(false);
			updateCustomComponent();
		}

		public JFIBCustomWidget<CC, T> getWidget() {
			return widget;
		}

		public CC getCustomComponent() {
			return customComponent;
		}

		protected void updateCustomComponent() {
			removeAll();
			if (widget.getComponent().getComponentClass() == null) {
				invalidComponentlabel.setText("< Custom component >");
				add(invalidComponentlabel, BorderLayout.CENTER);
				setBorder(BorderFactory.createEtchedBorder());
			}
			else {
				customComponent = widget.makeCustomComponent();
				if (customComponent == null) {
					invalidComponentlabel.setText("< Invalid component >");
					add(invalidComponentlabel, BorderLayout.CENTER);
					setBorder(BorderFactory.createEtchedBorder());
				}
				else {
					add(customComponent, BorderLayout.CENTER);
					setBorder(null);
				}
			}
			revalidate();
			repaint();
		}

	}

}

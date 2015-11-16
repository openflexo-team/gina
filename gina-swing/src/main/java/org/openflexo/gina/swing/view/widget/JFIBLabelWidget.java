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

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBLabelWidgetImpl;

/**
 * Swing implementation for a simple widget allowing to display a label (a JLabel)
 * 
 * @author sylvain
 */
public class JFIBLabelWidget extends FIBLabelWidgetImpl<JLabel>implements JFIBView<FIBLabel, JLabel> {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JFIBLabelWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingLabelRenderingAdapter extends SwingRenderingAdapter<JLabel>implements LabelRenderingAdapter<JLabel> {

		@Override
		public String getText(JLabel component) {
			return component.getText();
		}

		@Override
		public void setText(JLabel component, String aText) {
			component.setText(aText);
		}

		@Override
		public int getHorizontalAlignment(JLabel component) {
			return component.getHorizontalAlignment();
		}

		@Override
		public void setHorizontalAlignment(JLabel component, int align) {
			component.setHorizontalAlignment(align);
		}

		@Override
		public JComponent getJComponent(JLabel component) {
			return component;
		}
	}

	public static SwingLabelRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingLabelRenderingAdapter();

	public JFIBLabelWidget(FIBLabel model, FIBController controller) {
		super(model, controller, new SwingLabelRenderingAdapter() /*RENDERING_TECHNOLOGY_ADAPTER*/);
	}

	@Override
	public SwingLabelRenderingAdapter getRenderingAdapter() {
		return (SwingLabelRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		JComponent returned = getRenderingAdapter().getResultingJComponent(this);
		// System.out.println("Je retourne " + returned);
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	protected JLabel makeTechnologyComponent() {
		JLabel returned;
		/*
		 * if (getWidget().getData().isValid()) { returned = new JLabel(" "); }
		 * else {
		 */
		returned = new JLabel("");
		// }
		// There is not much point in giving focus to a label since there is no
		// KeyBindings nor KeyListener on it.
		returned.setFocusable(false);
		returned.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, TOP_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
				RIGHT_COMPENSATING_BORDER));
		return returned;
	}

}

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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBButton;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.FIBButtonWidget.ButtonWidgetRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBButtonWidgetImpl;

public class JFIBButtonWidget extends FIBButtonWidgetImpl<JButton> implements JFIBView<FIBButton, JButton> {

	private static final Logger logger = Logger.getLogger(JFIBButtonWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingButtonRenderingAdapter extends SwingRenderingAdapter<JButton>
			implements ButtonWidgetRenderingAdapter<JButton> {

		@Override
		public String getText(JButton component) {
			return component.getText();
		}

		@Override
		public void setText(JButton component, String aText) {
			component.setText(aText);
		}

		@Override
		public Icon getIcon(JButton component) {
			return component.getIcon();
		}

		@Override
		public void setIcon(JButton component, Icon anIcon) {
			component.setIcon(anIcon);
		}

		@Override
		public Color getDefaultForegroundColor(JButton component) {
			return UIManager.getColor("Button.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JButton component) {
			return UIManager.getColor("Button.background");
		}

	}

	public static SwingButtonRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingButtonRenderingAdapter();

	public JFIBButtonWidget(FIBButton model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	public SwingButtonRenderingAdapter getRenderingAdapter() {
		return (SwingButtonRenderingAdapter) super.getRenderingAdapter();
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
	protected JButton makeTechnologyComponent() {
		JButton buttonWidget = new JButton();
		buttonWidget.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClicked();
				// updateDependancies();
			}
		});
		return buttonWidget;
	}

}

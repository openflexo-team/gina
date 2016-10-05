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
import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBLabelWidget.JLabelPanel;
import org.openflexo.gina.view.widget.impl.FIBLabelWidgetImpl;

/**
 * Swing implementation for a simple widget allowing to display a label (a JLabel)
 * 
 * @author sylvain
 */
public class JFIBLabelWidget extends FIBLabelWidgetImpl<JLabelPanel>implements JFIBView<FIBLabel, JLabelPanel> {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JFIBLabelWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingLabelRenderingAdapter extends SwingRenderingAdapter<JLabelPanel>implements LabelRenderingAdapter<JLabelPanel> {

		// private boolean isLayouted = false;

		@Override
		public String getText(JLabelPanel component) {
			return component.getLabel().getText();
		}

		@Override
		public void setText(final JLabelPanel component, final String aText) {

			if (component.getWidget().getWidget().getTrimText()) {
				component.getLabel().setText("<html>" + aText + "</html>");
				/*isLayouted = false;
				if (component.getSize().width > 0) {
					trimTextTo(aText, component, component.getSize().width);
					isLayouted = true;
				}
				else {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (!isLayouted && component.getWidget().getWidget().getTrimText()) {
								trimTextTo(aText, component, component.getSize().width);
								isLayouted = true;
							}
						}
					});
				}*/
			}
			else {
				component.getLabel().setText(aText);
			}

		}

		/*private void trimTextTo(String labelText, JLabelPanel component, int width) {
			FontMetrics fm = component.getFontMetrics(component.getWidget().getFont());
			List<String> lines = trimString(labelText, width, fm);
			StringBuffer htmlText = new StringBuffer();
			htmlText.append("<html>");
			for (int j = 0; j < lines.size(); j++) {
				String line = lines.get(j);
				htmlText.append(line + (j == lines.size() - 1 ? "" : "<br>"));
			}
			htmlText.append("</html>");
			component.getLabel().setText(htmlText.toString());
		}*/

		@Override
		public int getHorizontalAlignment(JLabelPanel component) {
			return component.getLabel().getHorizontalAlignment();
		}

		@Override
		public void setHorizontalAlignment(JLabelPanel component, int align) {
			component.getLabel().setHorizontalAlignment(align);
		}

		@Override
		public Color getDefaultForegroundColor(JLabelPanel component) {
			return UIManager.getColor("Label.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JLabelPanel component) {
			return UIManager.getColor("Label.background");
		}

		@Override
		public void setFont(JLabelPanel component, Font aFont) {
			super.setFont(component, aFont);
			component.getLabel().setFont(aFont);
		}

		@Override
		public JLabel getDynamicJComponent(JLabelPanel technologyComponent) {
			return technologyComponent.getLabel();
		}
	}

	// public static SwingLabelRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingLabelRenderingAdapter();

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
		// JComponent returned = getRenderingAdapter().getResultingJComponent(this);
		// System.out.println("Je retourne " + returned);
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	protected JLabelPanel makeTechnologyComponent() {
		/*JLabel returned;
		returned = new JLabel("");
		returned.setFocusable(false);
		returned.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, TOP_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
				RIGHT_COMPENSATING_BORDER));
		return returned;*/
		return new JLabelPanel(this);
	}

	@SuppressWarnings("serial")
	public static class JLabelPanel extends JPanel {
		private JLabel label;
		private JFIBLabelWidget widget;

		public JLabelPanel(JFIBLabelWidget widget) {
			super(new BorderLayout());
			this.widget = widget;
			setOpaque(false);
			label = new JLabel("prout");
			label.setFocusable(false);
			setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, TOP_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER));
			add(label, BorderLayout.CENTER);
		}

		public JFIBLabelWidget getWidget() {
			return widget;
		}

		public JLabel getLabel() {
			return label;
		}
	}

}

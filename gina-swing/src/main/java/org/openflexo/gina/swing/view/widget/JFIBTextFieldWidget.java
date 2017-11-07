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
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBTextEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingTextRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBTextFieldWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation for a simple widget allowing to display/edit a String in a TextField
 * 
 * @author sylvain
 */
public class JFIBTextFieldWidget extends FIBTextFieldWidgetImpl<JTextField> implements FocusListener, JFIBView<FIBTextField, JTextField> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JFIBTextFieldWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingTextFieldRenderingAdapter extends SwingTextRenderingAdapter<JTextField>
			implements TextFieldRenderingAdapter<JTextField> {

		@Override
		public void setColumns(JTextField component, int columns) {
			component.setColumns(columns);
			if (component.getParent() != null) {
				component.getParent().revalidate();
				component.getParent().repaint();
			}
		}

		@Override
		public int getColumns(JTextField component) {
			return component.getColumns();
		}

		@Override
		public Color getDefaultForegroundColor(JTextField component) {
			return UIManager.getColor("TextField.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JTextField component) {
			return UIManager.getColor("TextField.background");
		}

	}

	public static SwingTextFieldRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingTextFieldRenderingAdapter();

	// private final JPanel panel;

	public JFIBTextFieldWidget(FIBTextField model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
		// panel = new JPanel(new BorderLayout());
		// panel.setOpaque(false);
		// panel.add(getTechnologyComponent(), BorderLayout.CENTER);
		/*if (!ToolBox.isMacOSLaf()) {
			panel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}*/

		// updateFont();
	}

	/*@Override
	protected void performUpdate() {
		super.performUpdate();
		if (!ToolBox.isMacOSLaf()) {
			panel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER));
		}
	}*/

	@Override
	public SwingTextFieldRenderingAdapter getRenderingAdapter() {
		return (SwingTextFieldRenderingAdapter) super.getRenderingAdapter();
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
	protected JTextField makeTechnologyComponent() {
		JTextField textField = null;
		if (getWidget().isPasswd()) {
			textField = new JPasswordField() {
				@Override
				public Dimension getMinimumSize() {
					return MINIMUM_SIZE;
				}
			};
		}
		else {
			textField = new JTextField() {
				@Override
				public Dimension getMinimumSize() {
					return MINIMUM_SIZE;
				}
			};
		}

		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
					// updateModelFromWidget();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				GinaStackEvent stack = null;
				try {
					stack = GENotifier.raise(FIBEventFactory.getInstance().createTextEvent(FIBTextEventDescription.INSERTED, e.getOffset(),
							e.getLength(), e.getDocument().getText(e.getOffset(), e.getLength()), getTechnologyComponent().getText()));
				} catch (BadLocationException e2) {
					e2.printStackTrace();
				}

				if (!validateOnReturn && !isUpdating()) {
					try {
						if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
							if (e.getLength() == 1) {
								char c = getTechnologyComponent().getText().charAt(e.getOffset());
								if (c == '´' || c == 'ˆ' || c == '˜' || c == '`' || c == '¨') {
									if (stack != null)
										stack.end();
									return;
								}
							}
						}
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
					textChanged();
					// updateModelFromWidget();
				}

				if (stack != null)
					stack.end();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				GinaStackEvent stack = GENotifier.raise(FIBEventFactory.getInstance().createTextEvent(FIBTextEventDescription.REMOVED,
						e.getOffset(), e.getLength(), "", getValue()));
				if (!validateOnReturn && !isUpdating() && !widgetUpdating()) {
					textChanged();
					// updateModelFromWidget();
				}
				stack.end();
			}
		});

		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textChanged();
				// updateModelFromWidget();
				final Window w = SwingUtilities.windowForComponent(getTechnologyComponent());
				if (w instanceof JDialog) {
					if (((JDialog) w).getRootPane().getDefaultButton() != null) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								((JDialog) w).getRootPane().getDefaultButton().doClick();
							}
						});
					}
				}
			}
		});

		textField.addFocusListener(this);

		textField.setEnabled(true);

		return textField;
	}

	@Override
	protected void componentGainsFocus() {
		super.componentGainsFocus();
		getTechnologyComponent().selectAll();
	}

	@Override
	protected void componentLoosesFocus() {
		super.componentLoosesFocus();
		if (!isUpdating()) {
			textChanged();
		}
	}

}

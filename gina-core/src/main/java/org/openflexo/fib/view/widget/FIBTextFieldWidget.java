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

package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.listener.FIBActionListenerManager;
import org.openflexo.fib.listener.GinaStackEvent;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.gina.event.FIBEvent;
import org.openflexo.gina.event.FIBEventFactory;
import org.openflexo.gina.event.FIBTextEvent;
import org.openflexo.toolbox.ToolBox;

/**
 * Simple widget allowing to display/edit a String
 * 
 * @author bmangez
 */
public class FIBTextFieldWidget extends FIBWidgetView<FIBTextField, JTextField, String> {

	private static final Logger logger = Logger.getLogger(FIBTextFieldWidget.class.getPackage().getName());

	private static final int DEFAULT_COLUMNS = 10;

	private JPanel panel;
	private JTextField textField;

	boolean validateOnReturn;

	public FIBTextFieldWidget(FIBTextField model, FIBController controller) {
		super(model, controller);
		if (model.isPasswd()) {
			textField = new JPasswordField() {
				@Override
				public Dimension getMinimumSize() {
					return MINIMUM_SIZE;
				}
			};
		} else {
			textField = new JTextField() {
				@Override
				public Dimension getMinimumSize() {
					return MINIMUM_SIZE;
				}
			};
		}
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(textField, BorderLayout.CENTER);
		if (!ToolBox.isMacOSLaf()) {
			panel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER));
		}

		if (isReadOnly()) {
			textField.setEditable(false);
		}

		validateOnReturn = model.isValidateOnReturn();
		if (model.getColumns() != null) {
			textField.setColumns(model.getColumns());
		} else {
			textField.setColumns(DEFAULT_COLUMNS);
		}

		if (model.getText() != null) {
			textField.setText(model.getText());
		}
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				GinaStackEvent stack = null;
				try {
					stack = actionPerformed(FIBEventFactory.getInstance().createTextEvent("text-inserted", e.getOffset(), e.getLength(),
							e.getDocument().getText(e.getOffset(), e.getLength()), getValue()));
				} catch (BadLocationException e2) {
					e2.printStackTrace();
				}

				if (!validateOnReturn && !widgetUpdating) {
					try {
						if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
							if (e.getLength() == 1) {
								char c = textField.getText().charAt(e.getOffset());
								if (c == '´' || c == 'ˆ' || c == '˜' || c == '`' || c == '¨') {
									if (stack != null)
										stack.unstack();
									return;
								}
							}
						}
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
					updateModelFromWidget();
				}
				
				if (stack != null)
					stack.unstack();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				GinaStackEvent stack = actionPerformed(FIBEventFactory.getInstance().createTextEvent("text-removed", e.getOffset(), e.getLength(),
						"", getValue()));
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
				stack.unstack();
			}
		});
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateModelFromWidget();
				final Window w = SwingUtilities.windowForComponent(textField);
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
		/*textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
            	FIBTextFieldWidget.this.getWidget().actionPerformed("text-inserted", String.valueOf(e.getKeyChar()), textField.getCaretPosition());
            }
        });*/
		textField.addFocusListener(this);

		updateFont();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		super.focusGained(arg0);
		textField.selectAll();

		actionPerformed(FIBEventFactory.getInstance().createFocusEvent("focus-gained")).unstack();
	}
	
	@Override
	public void focusLost(FocusEvent arg0) {
		super.focusLost(arg0);

		actionPerformed(FIBEventFactory.getInstance().createFocusEvent("focus-lost")).unstack();
	}

	public Class getDefaultType() {
		return String.class;
	}
	
	public void executeEvent(FIBEvent e) {
		widgetExecuting = true;

		switch(e.getAction()) {
		case "text-inserted": {
			FIBTextEvent ev = (FIBTextEvent) e;
			String text = textField.getText();
			if (text.length() >= ev.getPosition())
			{
				textField.setText(text.substring(0, ev.getPosition()) + e.getValue() + text.substring(ev.getPosition()));
				textField.setCaretPosition(ev.getPosition() + e.getValue().length());
			}
			break;
		}
		case "text-removed": {
			FIBTextEvent ev = (FIBTextEvent) e;
			String text = textField.getText();
			if (text.length() >= ev.getPosition() + ev.getLength())
				textField.setText(text.substring(0, ev.getPosition()) + text.substring(ev.getPosition() + ev.getLength()));
			break;
		}
		case "focus-gained":
			textField.requestFocus();
			break;
		case "focus-lost":
			textField.getParent().requestFocus();
			break;
		}
		
		widgetExecuting = false;
	}

	// DEBUG
	/*@Override
	public String getValue() {
		if (getWidget().getData().toString().equals("data.getFlexoConceptInstance(\"CityGR\").city.cityInModel1.name")) {

			System.out.println("getWidget().getData()=" + getWidget().getData());
			System.out.println("getWidget().getData().isValid()=" + getWidget().getData().isValid());
			System.out.println("getWidget().getData().invalidBindingReason()=" + getWidget().getData().invalidBindingReason());
			System.out.println("getBindingEvaluationContext()=" + getBindingEvaluationContext());

			DataBinding db1 = new DataBinding("data", getWidget().getData().getOwner(), Object.class, BindingDefinitionType.GET);
			DataBinding db2 = new DataBinding("data.getFlexoConceptInstance(\"CityGR\")", getWidget().getData().getOwner(), Object.class,
					BindingDefinitionType.GET);
			DataBinding db3 = new DataBinding("data.getFlexoConceptInstance(\"CityGR\").city", getWidget().getData().getOwner(),
					Object.class, BindingDefinitionType.GET);
			DataBinding db4 = new DataBinding("data.getFlexoConceptInstance(\"CityGR\").city.cityInModel1", getWidget().getData()
					.getOwner(), Object.class, BindingDefinitionType.GET);
			DataBinding db5 = new DataBinding("data.getFlexoConceptInstance(\"CityGR\").city.cityInModel1.name", getWidget().getData()
					.getOwner(), Object.class, BindingDefinitionType.GET);

			try {
				System.out.println("db1=" + db1.getBindingValue(getBindingEvaluationContext()));
				System.out.println("db2=" + db2.getBindingValue(getBindingEvaluationContext()));
				System.out.println("db3=" + db3.getBindingValue(getBindingEvaluationContext()));
				System.out.println("db4=" + db4.getBindingValue(getBindingEvaluationContext()));
				System.out.println("db5=" + db5.getBindingValue(getBindingEvaluationContext()));
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return super.getValue();
	}*/

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), textField.getText())) {
			if (modelUpdating) {
				return false;
			}
			widgetUpdating = true;
			try {
				int caret = textField.getCaretPosition();
				textField.setText(getValue());
				if (caret > -1 && caret < textField.getDocument().getLength()) {
					textField.setCaretPosition(caret);
				}
			} finally {
				widgetUpdating = false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), textField.getText())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in TextFieldWidget");
			}
			modelUpdating = true;
			try {
				setValue(textField.getText());
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return panel;
	}

	@Override
	public JTextField getDynamicJComponent() {
		return textField;
	}

}

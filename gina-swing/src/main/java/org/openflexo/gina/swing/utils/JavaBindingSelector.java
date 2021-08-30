/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultContextualizedBindable;
import org.openflexo.connie.expr.Operator;
import org.openflexo.connie.java.JavaBindingFactory;
import org.openflexo.connie.java.JavaGrammar;
import org.openflexo.connie.java.JavaTypingSpace;
import org.openflexo.connie.java.expr.JavaArithmeticBinaryOperator;
import org.openflexo.connie.java.expr.JavaArithmeticUnaryOperator;
import org.openflexo.connie.java.expr.JavaBooleanBinaryOperator;
import org.openflexo.connie.java.expr.JavaBooleanUnaryOperator;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.VerticalLayout;

/**
 * Widget allowing to edit a {@link DataBinding} using Java language
 * 
 * @author sguerin
 * 
 */
public class JavaBindingSelector extends BindingSelector {
	static final Logger LOGGER = Logger.getLogger(JavaBindingSelector.class.getPackage().getName());

	public JavaBindingSelector(DataBinding<?> editedObject) {
		super(editedObject, -1, new JavaGrammar());
	}

	public JavaBindingSelector(DataBinding<?> editedObject, int cols) {
		super(null, cols, new JavaGrammar());
	}

	@Override
	public ImageIcon iconForOperator(Operator op) {
		if (op == JavaArithmeticBinaryOperator.ADDITION) {
			return FIBIconLibrary.ADDITION_ICON;
		}
		else if (op == JavaArithmeticBinaryOperator.SUBSTRACTION) {
			return FIBIconLibrary.SUBSTRACTION_ICON;
		}
		else if (op == JavaArithmeticBinaryOperator.MULTIPLICATION) {
			return FIBIconLibrary.MULTIPLICATION_ICON;
		}
		else if (op == JavaArithmeticBinaryOperator.DIVISION) {
			return FIBIconLibrary.DIVISION_ICON;
		}
		/*else if (op == JavaArithmeticBinaryOperator.POWER) {
			return FIBIconLibrary.POWER_ICON;
		}*/
		else if (op == JavaBooleanBinaryOperator.EQUALS) {
			return FIBIconLibrary.EQUALS_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.NOT_EQUALS) {
			return FIBIconLibrary.NOT_EQUALS_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.LESS_THAN) {
			return FIBIconLibrary.LESS_THAN_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.LESS_THAN_OR_EQUALS) {
			return FIBIconLibrary.LESS_THAN_OR_EQUALS_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.GREATER_THAN) {
			return FIBIconLibrary.GREATER_THAN_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.GREATER_THAN_OR_EQUALS) {
			return FIBIconLibrary.GREATER_THAN_OR_EQUALS_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.AND) {
			return FIBIconLibrary.AND_ICON;
		}
		else if (op == JavaBooleanBinaryOperator.OR) {
			return FIBIconLibrary.OR_ICON;
		}
		else if (op == JavaBooleanUnaryOperator.NOT) {
			return FIBIconLibrary.NOT_ICON;
		}
		else if (op == JavaArithmeticUnaryOperator.UNARY_MINUS) {
			return FIBIconLibrary.SUBSTRACTION_ICON;
		}
		/*else if (op == ArithmeticUnaryOperator.SIN) {
			return FIBIconLibrary.SIN_ICON;
		}
		else if (op == ArithmeticUnaryOperator.ASIN) {
			return FIBIconLibrary.ASIN_ICON;
		}
		else if (op == ArithmeticUnaryOperator.COS) {
			return FIBIconLibrary.COS_ICON;
		}
		else if (op == ArithmeticUnaryOperator.ACOS) {
			return FIBIconLibrary.ACOS_ICON;
		}
		else if (op == ArithmeticUnaryOperator.TAN) {
			return FIBIconLibrary.TAN_ICON;
		}
		else if (op == ArithmeticUnaryOperator.ATAN) {
			return FIBIconLibrary.ATAN_ICON;
		}
		else if (op == ArithmeticUnaryOperator.EXP) {
			return FIBIconLibrary.EXP_ICON;
		}
		else if (op == ArithmeticUnaryOperator.LOG) {
			return FIBIconLibrary.LOG_ICON;
		}
		else if (op == ArithmeticUnaryOperator.SQRT) {
			return FIBIconLibrary.SQRT_ICON;
		}*/
		return null;
	}

	@Override
	public BindingSelector makeSubBindingSelector(DataBinding<?> dataBinding, ApplyCancelListener applyCancelListener) {
		return new JavaBindingSelector(dataBinding) {
			@Override
			public void apply() {
				super.apply();
				applyCancelListener.fireApplyPerformed();
			}

			@Override
			public void cancel() {
				super.cancel();
				applyCancelListener.fireCancelPerformed();
			}

			@Override
			public Dimension getPreferredSize() {
				Dimension parentDim = super.getPreferredSize();
				return new Dimension(100, parentDim.height);
			}
		};

	}

	// static JComponent currentFocusOwner;
	// static Border lastBorder;

	/**
	 * This main allows to launch an application testing the BindingSelector
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SecurityException, IOException {

		Resource loggingFile = ResourceLocator.locateResource("Config/logging_INFO.properties");
		FlexoLoggingManager.initialize(-1, true, loggingFile, Level.INFO, null);
		final JDialog dialog = new JDialog((Frame) null, false);

		// TODO GinaManager.getInstance().setup();

		Bindable testBindable = new TestBindable();

		// Unused BindingFactory factory =
		// new JavaBindingFactory();
		DataBinding<String> binding = new DataBinding<>("aString.toString", testBindable, String.class,
				DataBinding.BindingDefinitionType.GET);
		// DataBinding binding = new DataBinding<String>(testBindable,
		// Object.class, DataBinding.BindingDefinitionType.EXECUTE);

		/*KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		focusManager.addPropertyChangeListener(new PropertyChangeListener() {
		
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (focusManager.getFocusOwner() != null) {
					System.out.println(
							"Nouveau focusOwner: " + focusManager.getFocusOwner().getName() + " : " + focusManager.getFocusOwner());
					System.out.println("parent:" + focusManager.getFocusOwner().getParent());
					if (focusManager.getFocusOwner() instanceof JComponent) {
						if (currentFocusOwner != null) {
							currentFocusOwner.setBorder(lastBorder);
						}
						currentFocusOwner = (JComponent) focusManager.getFocusOwner();
						lastBorder = ((JComponent) focusManager.getFocusOwner()).getBorder();
						((JComponent) focusManager.getFocusOwner()).setBorder(BorderFactory.createLineBorder(Color.RED, 3));
					}
				}
				else {
					System.out.println("Nouveau focusOwner: " + focusManager.getFocusOwner());
				}
			}
		});*/

		final JavaBindingSelector _selector = new JavaBindingSelector(null) {
			@Override
			public void apply() {
				super.apply();
				// System.out.println("Apply, getEditedObject()=" +
				// getEditedObject());
			}

			@Override
			public void cancel() {
				super.cancel();
				// System.out.println("Cancel, getEditedObject()=" +
				// getEditedObject());
			}
		};
		_selector.setBindable(testBindable);
		_selector.setEditedObject(binding);
		_selector.setRevertValue(binding.clone());

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_selector.delete();
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), dialog);
			}
		});

		JPanel panel = new JPanel(new VerticalLayout());
		panel.add(_selector);

		panel.add(closeButton);
		panel.add(logButton);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.pack();

		dialog.setVisible(true);
	}

	public static class TestBindable extends DefaultContextualizedBindable {
		private final BindingFactory bindingFactory = new JavaBindingFactory();
		private final BindingModel bindingModel = new BindingModel();

		public TestBindable() {
			super(new JavaTypingSpace());
			bindingModel.addToBindingVariables(new BindingVariable("aString", String.class));
			bindingModel.addToBindingVariables(new BindingVariable("anInteger", Integer.class));
			bindingModel.addToBindingVariables(new BindingVariable("aFloat", Float.TYPE));
		}

		@Override
		public BindingModel getBindingModel() {
			return bindingModel;
		}

		@Override
		public BindingFactory getBindingFactory() {
			return bindingFactory;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		}
	}

}

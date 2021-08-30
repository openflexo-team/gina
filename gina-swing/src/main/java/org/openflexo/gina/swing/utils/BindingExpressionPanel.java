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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BinaryOperator;
import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.ConditionalExpression;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionEvaluator;
import org.openflexo.connie.expr.ExpressionPrettyPrinter;
import org.openflexo.connie.expr.Operator;
import org.openflexo.connie.expr.OperatorNotSupportedException;
import org.openflexo.connie.expr.SymbolicConstant;
import org.openflexo.connie.expr.UnaryOperator;
import org.openflexo.connie.expr.UnaryOperatorExpression;
import org.openflexo.connie.java.expr.JavaExpressionEvaluator;
import org.openflexo.connie.java.expr.JavaInstanceOfExpression;
import org.openflexo.connie.java.expr.JavaPrettyPrinter;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.swing.MouseOverButton;

public class BindingExpressionPanel extends JPanel implements FocusListener {

	static final Logger LOGGER = Logger.getLogger(BindingExpressionPanel.class.getPackage().getName());

	private BindingSelector bindingSelector;
	private DataBinding<?> dataBinding;

	// private JTextArea expressionTA;
	private JPanel controls;
	private ExpressionInnerPanel rootExpressionPanel;

	private enum ExpressionParsingStatus {
		UNDEFINED, VALID, SYNTAXICALLY_VALID, INVALID
	}

	private ExpressionParsingStatus status = ExpressionParsingStatus.UNDEFINED;
	private String message = UNDEFINED_EXPRESSION_MESSAGE;

	private static final String UNDEFINED_EXPRESSION_MESSAGE = "please_define_expression";
	private static final String UNDEFINED_OPERAND_FOR_OPERATOR = "undefined_operand_for_operator";
	private static final String VALID_EXPRESSION = "expression_is_valid_and_conform_to_required_type";
	private static final String VALID_EXPRESSION_BUT_MISMATCH_TYPE = "expression_is_valid_but_not_conform_to_requested_type_(found:($0)_expected:($1))";

	private JLabel statusIcon;
	private JLabel messageLabel;
	private JTextArea evaluationTA;
	protected JPanel evaluationPanel;

	protected ExpressionPrettyPrinter pp = JavaPrettyPrinter.getInstance(); // new DefaultExpressionPrettyPrinter();

	private ExpressionInnerPanel focusReceiver = null;

	private boolean avoidLoop = false;

	public BindingExpressionPanel(BindingSelector bindingSelector) {
		super();
		// logger.info("Instanciate BindingExpressionPanel with " + aDataBinding);
		setLayout(new BorderLayout());
		this.bindingSelector = bindingSelector;
		dataBinding = bindingSelector.getEditedObject();
		init();
	}

	public void delete() {
		if (rootExpressionPanel != null) {
			rootExpressionPanel.delete();
		}
		rootExpressionPanel = null;
		dataBinding = null;
	}

	public BindingSelector getBindingSelector() {
		return bindingSelector;
	}

	public void setEditedExpression(DataBinding<?> bindingExpression) {
		if (avoidLoop) {
			return;
		}
		dataBinding = bindingExpression;
		if (bindingExpression != null) {
			_setEditedExpression(bindingExpression.getExpression());
			if (rootExpressionPanel.getRepresentedExpression() == null
					|| !rootExpressionPanel.getRepresentedExpression().equals(bindingExpression.getExpression())) {
				avoidLoop = true;
				rootExpressionPanel.setRepresentedExpression(bindingExpression.getExpression());
				avoidLoop = false;
			}
		}
		update();
	}

	protected void setEditedExpression(Expression expression) {
		_setEditedExpression(expression);
		update();
		fireEditedExpressionChanged(dataBinding);
	}

	private void _setEditedExpression(Expression expression) {
		if (dataBinding != null) {
			dataBinding.setExpression(expression);
		}
		_checkEditedExpression();
	}

	protected void _checkEditedExpression() {
		Operator undefinedOperator = null;

		if (dataBinding == null) {
			return;
		}

		if (evaluationPanel != null && evaluationTA != null && evaluationPanel.isVisible()) {
			evaluationTA.setText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("cannot_evaluate"));
		}

		if (dataBinding.getExpression() != null) {
			if (expressionIsUndefined(dataBinding.getExpression())) {
				status = ExpressionParsingStatus.UNDEFINED;
				message = FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(UNDEFINED_EXPRESSION_MESSAGE);
				return;
			}
			else {
				for (BindingValue bv : dataBinding.getExpression().getAllBindingValues()) {
					if (!bv.isValid()) {
						message = FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("invalid_binding") + " " + bv;
						status = ExpressionParsingStatus.INVALID;
						return;
					}
				}
			}
		}

		if (dataBinding.getExpression() == null) {
			message = FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("cannot_parse") + " " + dataBinding.getUnparsedBinding();
			status = ExpressionParsingStatus.INVALID;
		}
		else if ((undefinedOperator = firstOperatorWithUndefinedOperand(dataBinding.getExpression())) != null) {
			status = ExpressionParsingStatus.INVALID;
			try {
				message = FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(UNDEFINED_OPERAND_FOR_OPERATOR) + " "
						+ undefinedOperator.getLocalizedName() + " [" + pp.getSymbol(undefinedOperator) + "]";
			} catch (OperatorNotSupportedException e) {
				message = FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(UNDEFINED_OPERAND_FOR_OPERATOR) + " "
						+ undefinedOperator.getLocalizedName() + " [?]";
			}
		}
		else {
			try {
				if (evaluationPanel != null && evaluationTA != null && evaluationPanel.isVisible() && dataBinding != null) {
					Expression evaluatedExpression = dataBinding.getExpression().evaluate(new BindingEvaluationContext() {

						@Override
						public Object getValue(BindingVariable variable) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public ExpressionEvaluator getEvaluator() {
							return new JavaExpressionEvaluator(this);
						}
					});
					if (evaluatedExpression != null) {
						evaluationTA.setText(evaluatedExpression.toString());
					}
				}

			} catch (TypeMismatchException e) {
				status = ExpressionParsingStatus.INVALID;
				message = e.getHTMLLocalizedMessage();
			} catch (NullReferenceException e) {
				status = ExpressionParsingStatus.INVALID;
				message = e.getHTMLLocalizedMessage();
			} catch (ReflectiveOperationException e) {
				status = ExpressionParsingStatus.INVALID;
				message = e.getLocalizedMessage();
			}

		}
	}

	private static boolean expressionIsUndefined(Expression expression) {
		return expression instanceof BindingValue && ((BindingValue) expression).getBindingVariable() == null;
	}

	private Operator firstOperatorWithUndefinedOperand(Expression expression) {
		if (expression instanceof BindingValue) {
			return null;
		}
		else if (expression instanceof Constant) {
			return null;
		}
		else if (expression instanceof BinaryOperatorExpression) {
			Expression leftOperand = ((BinaryOperatorExpression) expression).getLeftArgument();
			if (expressionIsUndefined(leftOperand)) {
				return ((BinaryOperatorExpression) expression).getOperator();
			}
			Operator returned = firstOperatorWithUndefinedOperand(leftOperand);
			if (returned == null) {
				Expression rightOperand = ((BinaryOperatorExpression) expression).getRightArgument();
				if (expressionIsUndefined(rightOperand)) {
					return ((BinaryOperatorExpression) expression).getOperator();
				}
				return firstOperatorWithUndefinedOperand(rightOperand);
			}
		}
		else if (expression instanceof UnaryOperatorExpression) {
			Expression operand = ((UnaryOperatorExpression) expression).getArgument();
			if (expressionIsUndefined(operand)) {
				return ((UnaryOperatorExpression) expression).getOperator();
			}
			return firstOperatorWithUndefinedOperand(operand);
		}
		return null;
	}

	public DataBinding<?> getEditedExpression() {
		return dataBinding;
	}

	protected void expressionMayHaveBeenEdited() {
		if (dataBinding == null) {
			return;
		}

		// try {
		Expression newExpression = null;
		/*if (expressionTA.getText().trim().equals("")) {
			newExpression = new BindingValue();
			((BindingValue) newExpression).setDataBinding(dataBinding);
			// newExpression = new BindingExpression.BindingValueVariable("", _bindingExpression.getOwner());
		} else {
			newExpression = ExpressionParser.parse(expressionTA.getText());
		}*/
		if (!newExpression.equals(dataBinding.getExpression()) || status == ExpressionParsingStatus.INVALID) {
			_setEditedExpression(newExpression);
			rootExpressionPanel.setRepresentedExpression(dataBinding.getExpression());
			update();
			fireEditedExpressionChanged(dataBinding);
		}
		/*} catch (org.openflexo.connie.expr.parser.ParseException e) {
			message = "ERROR: cannot parse ";// + expressionTA.getText();
			status = ExpressionParsingStatus.INVALID;
			updateAdditionalInformations();
		}*/
	}

	final private void init() {
		/*expressionTA = new JTextArea(3, 50);
		expressionTA.setLineWrap(true);
		expressionTA.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					expressionMayHaveBeenEdited();
				}
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					expressionMayHaveBeenEdited();
				}
			}
		});
		expressionTA.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				expressionMayHaveBeenEdited();
			}
		});*/
		/*expressionTA.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				expressionMayHaveBeenEdited();
			}
		});*/

		statusIcon = new JLabel();
		messageLabel = new JLabel("", SwingConstants.LEFT);
		messageLabel.setFont(new Font("SansSerif", Font.ITALIC, 9));

		evaluationPanel = new JPanel(new BorderLayout());
		evaluationTA = new JTextArea(1, 20);
		evaluationTA.setEditable(false);
		evaluationTA.setLineWrap(true);

		evaluationPanel.add(new JLabel(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("evaluation") + "  "), BorderLayout.WEST);
		evaluationPanel.add(evaluationTA, BorderLayout.CENTER);

		final MouseOverButton showEvaluationButton = new MouseOverButton();
		showEvaluationButton.setBorder(BorderFactory.createEmptyBorder());
		showEvaluationButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
		showEvaluationButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
		showEvaluationButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("show_evaluation"));
		showEvaluationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!evaluationPanel.isVisible()) {
					showEvaluationButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_ICON);
					showEvaluationButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
					showEvaluationButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("hide_evaluation"));
					evaluationPanel.setVisible(true);
					_checkEditedExpression();
				}
				else {
					showEvaluationButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
					showEvaluationButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
					showEvaluationButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("show_evaluation"));
					evaluationPanel.setVisible(false);
				}
			}
		});

		JPanel statusAndMessageLabel = new JPanel(new BorderLayout());
		statusAndMessageLabel.add(statusIcon, BorderLayout.WEST);
		statusAndMessageLabel.add(messageLabel, BorderLayout.CENTER);
		statusAndMessageLabel.add(showEvaluationButton, BorderLayout.EAST);

		JPanel topPanel = new JPanel(new BorderLayout());
		// topPanel.add(expressionTA, BorderLayout.NORTH);
		topPanel.add(statusAndMessageLabel, BorderLayout.CENTER);
		topPanel.add(evaluationPanel, BorderLayout.SOUTH);

		evaluationPanel.setVisible(false);

		add(topPanel, BorderLayout.NORTH);
		rootExpressionPanel = new ExpressionInnerPanel(dataBinding.getExpression()) {
			@Override
			public void representedExpressionChanged(Expression newExpression) {
				setEditedExpression(newExpression);
			}
		};
		focusReceiver = rootExpressionPanel;
		add(new JScrollPane(rootExpressionPanel), BorderLayout.CENTER);
		controls = new JPanel(new BorderLayout());

		final JPanel commonControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		final JPanel mathControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

		commonControls.add(createOperatorGroupPanel("logical", getBindingSelector().getExpressionGrammar().getLogicalOperators()));

		commonControls.add(createOperatorGroupPanel("comparison", getBindingSelector().getExpressionGrammar().getComparisonOperators()));

		commonControls.add(createOperatorGroupPanel("arithmetic", getBindingSelector().getExpressionGrammar().getArithmeticOperators()));

		final MouseOverButton moreButton = new MouseOverButton();
		moreButton.setBorder(BorderFactory.createEmptyBorder());
		moreButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
		moreButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
		moreButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("show_more_operators"));
		moreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!mathControls.isVisible()) {
					moreButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_ICON);
					moreButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
					moreButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("hide_extra_operators"));
					commonControls.remove(moreButton);
					mathControls.add(moreButton);
					mathControls.setVisible(true);
				}
				else {
					moreButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
					moreButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
					moreButton.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("show_more_operators"));
					mathControls.remove(moreButton);
					commonControls.add(moreButton);
					mathControls.setVisible(false);
				}
			}
		});

		commonControls.add(moreButton);

		controls.add(commonControls, BorderLayout.CENTER);

		boolean requiresMathControls = false;
		if (getBindingSelector().getExpressionGrammar().getScientificOperators() != null) {
			commonControls
					.add(createOperatorGroupPanel("scientific", getBindingSelector().getExpressionGrammar().getScientificOperators()));
			requiresMathControls = true;
		}

		if (getBindingSelector().getExpressionGrammar().getTrigonometricOperators() != null) {
			commonControls.add(
					createOperatorGroupPanel("trigonometric", getBindingSelector().getExpressionGrammar().getTrigonometricOperators()));
			requiresMathControls = true;
		}

		if (requiresMathControls) {
			controls.add(mathControls, BorderLayout.SOUTH);
		}

		mathControls.setVisible(false);

		add(controls, BorderLayout.SOUTH);
		update();
	}

	private JPanel createOperatorGroupPanel(String title, Operator... operators) {
		JPanel returned = new JPanel();
		returned.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		for (final Operator o : operators) {
			JButton b = new JButton(getBindingSelector().iconForOperator(o));
			b.setBorder(BorderFactory.createEmptyBorder());
			b.setToolTipText(o.getLocalizedName());
			returned.add(b);
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (o instanceof UnaryOperator) {
						appendUnaryOperator((UnaryOperator) o);
					}
					else if (o instanceof BinaryOperator) {
						appendBinaryOperator((BinaryOperator) o);
					}
				}
			});
		}

		if (title.equals("logical")) {
			JButton b = new JButton(FIBIconLibrary.IF_ICON);
			b.setBorder(BorderFactory.createEmptyBorder());
			b.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("conditional"));
			returned.add(b);
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					appendConditional();
				}
			});
		}

		returned.setBorder(BorderFactory.createTitledBorder(null, FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey(title),
				TitledBorder.CENTER, TitledBorder.TOP, new Font("SansSerif", Font.ITALIC, 8)));
		return returned;
	}

	protected void update() {
		_checkEditedExpression();
		updateExpressionTextArea();
		updateAdditionalInformations();
		revalidate();
		repaint();
	}

	protected void fireEditedExpressionChanged(DataBinding<?> expression) {
		// Override if required
	}

	protected void updateExpressionTextArea() {
		if (dataBinding == null) {
			return;
		}
		// expressionTA.setText(pp.getStringRepresentation(dataBinding.getExpression()));
		if (status == ExpressionParsingStatus.UNDEFINED) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		}
		else if (status == ExpressionParsingStatus.INVALID) {
			statusIcon.setIcon(FIBIconLibrary.ERROR_ICON);
		}
		else if (status == ExpressionParsingStatus.SYNTAXICALLY_VALID) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		}
		else if (status == ExpressionParsingStatus.VALID) {
			statusIcon.setIcon(FIBIconLibrary.OK_ICON);
		}
		messageLabel.setText(message);
	}

	protected void updateAdditionalInformations() {
		if (status == ExpressionParsingStatus.UNDEFINED) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		}
		else if (status == ExpressionParsingStatus.INVALID) {
			statusIcon.setIcon(FIBIconLibrary.ERROR_ICON);
		}
		else if (status == ExpressionParsingStatus.SYNTAXICALLY_VALID) {
			statusIcon.setIcon(FIBIconLibrary.WARNING_ICON);
		}
		else if (status == ExpressionParsingStatus.VALID) {
			statusIcon.setIcon(FIBIconLibrary.OK_ICON);
		}
		messageLabel.setText(message);
	}

	protected abstract class ExpressionInnerPanel extends JPanel {

		private final DataBinding<?> innerDataBinding;
		protected Expression _representedExpression;
		private BindingSelector _bindingSelector;

		// private JTextField variableOrConstantTextField;

		protected ExpressionInnerPanel(Expression expression) {
			super();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Build new ExpressionInnerPanel with " + (expression != null ? expression.toString() : "null"));
			}
			_representedExpression = expression;
			innerDataBinding = new DataBinding<>(dataBinding.getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
			innerDataBinding.setExpression(_representedExpression);
			update();
			// addFocusListeners();
		}

		public void delete() {
			if (_bindingSelector != null) {
				_bindingSelector.delete();
			}
			for (Component c : getComponents()) {
				if (c instanceof ExpressionInnerPanel) {
					((ExpressionInnerPanel) c).delete();
				}
			}
			removeAll();
			_representedExpression = null;
			_bindingSelector = null;
		}

		private void addFocusListeners() {
			addFocusListenersToAllComponentsOf(this);
		}

		private void addFocusListenersToAllComponentsOf(Component c) {
			c.addFocusListener(BindingExpressionPanel.this);
			if (c instanceof Container) {
				Container container = (Container) c;
				for (Component c2 : container.getComponents()) {
					addFocusListenersToAllComponentsOf(c2);
				}
			}
		}

		private void removeFocusListeners() {
			removeFocusListenersToAllComponentsOf(this);
		}

		private void removeFocusListenersToAllComponentsOf(Component c) {
			c.removeFocusListener(BindingExpressionPanel.this);
			if (c instanceof Container) {
				Container container = (Container) c;
				for (Component c2 : container.getComponents()) {
					removeFocusListenersToAllComponentsOf(c2);
				}
			}
		}

		/*protected void textChanged()
		{
			try {
				Expression newExpression;
				if (variableOrConstantTextField.getText().trim().equals("")) {
					newExpression = new Variable("");
				}
				else {
					newExpression = parser.parse(variableOrConstantTextField.getText());
				}
				setRepresentedExpression(newExpression);
				//System.out.println("Text has changed for "+variableOrConstantTextField.getText()+" parsed as "+newExpression);
			} catch (ParseException e) {
				System.out.println("ERROR: cannot parse "+variableOrConstantTextField.getText());
			}
		}*/

		protected class OperatorPanel extends JPanel {
			private final JButton currentOperatorIcon;

			protected OperatorPanel(Operator operator) {
				super();
				setLayout(new BorderLayout());

				currentOperatorIcon = new JButton(getBindingSelector().iconForOperator(operator));
				currentOperatorIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				currentOperatorIcon.setToolTipText(operator.getLocalizedName());

				add(currentOperatorIcon, BorderLayout.CENTER);
			}
		}

		protected class IfOperatorPanel extends JPanel {
			private final JButton currentOperatorIcon;

			protected IfOperatorPanel() {
				super();
				setLayout(new BorderLayout());

				currentOperatorIcon = new JButton(FIBIconLibrary.IF_ICON);
				currentOperatorIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				currentOperatorIcon.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("conditional"));

				add(currentOperatorIcon, BorderLayout.CENTER);
			}
		}

		private void addBinaryExpressionVerticalLayout() {
			GridBagLayout gridbag2 = new GridBagLayout();
			GridBagConstraints c2 = new GridBagConstraints();
			setLayout(gridbag2);

			final BinaryOperatorExpression exp = (BinaryOperatorExpression) _representedExpression;
			final ExpressionInnerPanel me = this;

			OperatorPanel operatorPanel = new OperatorPanel(exp.getOperator());

			c2.weightx = 0.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.CENTER;
			c2.fill = GridBagConstraints.VERTICAL;
			gridbag2.setConstraints(operatorPanel, c2);
			add(operatorPanel);

			operatorPanel.setBorder(BorderFactory.createEtchedBorder());
			JPanel argsPanel = new JPanel();

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			argsPanel.setLayout(gridbag);

			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setLeftArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(leftArg, c);
			argsPanel.add(leftArg);

			ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()/*,depth+1*/) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setRightArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(rightArg, c);
			argsPanel.add(rightArg);

			c2.weightx = 1.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.NORTH;
			c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(argsPanel, c2);
			add(argsPanel);

			Box box = Box.createHorizontalBox();
			c2.weightx = 1.0;
			c2.weighty = 1.0;
			c2.anchor = GridBagConstraints.SOUTH;
			c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(box, c2);
			add(box);

			isHorizontallyLayouted = false;
		}

		private void addConditionalExpressionVerticalLayout() {
			GridBagLayout gridbag2 = new GridBagLayout();
			GridBagConstraints c2 = new GridBagConstraints();
			setLayout(gridbag2);

			final ConditionalExpression exp = (ConditionalExpression) _representedExpression;
			final ExpressionInnerPanel me = this;

			IfOperatorPanel operatorPanel = new IfOperatorPanel();

			c2.weightx = 0.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.EAST;
			c2.fill = GridBagConstraints.NONE;
			c2.gridwidth = GridBagConstraints.RELATIVE;
			c2.insets = new Insets(0, 0, 0, 0);
			gridbag2.setConstraints(operatorPanel, c2);
			add(operatorPanel);

			operatorPanel.setBorder(BorderFactory.createEmptyBorder());

			ExpressionInnerPanel conditionArg = new ExpressionInnerPanel(exp.getCondition()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setCondition(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c2.weightx = 1.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.CENTER;
			c2.fill = GridBagConstraints.HORIZONTAL;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			c2.insets = new Insets(0, 0, 0, 0);
			gridbag2.setConstraints(conditionArg, c2);
			add(conditionArg);

			JLabel thenLabel = new JLabel("then");
			thenLabel.setFont(thenLabel.getFont().deriveFont(9.0f));

			c2.weightx = 0.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.EAST;
			c2.fill = GridBagConstraints.NONE;
			c2.gridwidth = GridBagConstraints.RELATIVE;
			c2.insets = new Insets(0, 0, 0, 3);
			gridbag2.setConstraints(thenLabel, c2);
			add(thenLabel);

			ExpressionInnerPanel thenExp = new ExpressionInnerPanel(exp.getThenExpression()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setThenExpression(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c2.weightx = 1.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.CENTER;
			c2.fill = GridBagConstraints.HORIZONTAL;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			c2.insets = new Insets(0, 0, 0, 0);
			gridbag2.setConstraints(thenExp, c2);
			add(thenExp);

			JLabel elseLabel = new JLabel("else");
			elseLabel.setFont(elseLabel.getFont().deriveFont(9.0f));

			c2.weightx = 0.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.EAST;
			c2.fill = GridBagConstraints.NONE;
			c2.gridwidth = GridBagConstraints.RELATIVE;
			c2.insets = new Insets(0, 0, 0, 3);
			gridbag2.setConstraints(elseLabel, c2);
			add(elseLabel);

			ExpressionInnerPanel elseExp = new ExpressionInnerPanel(exp.getElseExpression()/*,depth+1*/) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setElseExpression(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c2.weightx = 1.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.CENTER;
			c2.fill = GridBagConstraints.HORIZONTAL;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			c2.insets = new Insets(0, 0, 0, 0);
			gridbag2.setConstraints(elseExp, c2);
			add(elseExp);

			Box box = Box.createHorizontalBox();
			c2.weightx = 1.0;
			c2.weighty = 1.0;
			c2.anchor = GridBagConstraints.SOUTH;
			c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(box, c2);
			add(box);

			isHorizontallyLayouted = false;
		}

		private boolean isHorizontallyLayouted = true;

		private void addBinaryExpressionHorizontalLayout() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			setLayout(gridbag);

			final BinaryOperatorExpression exp = (BinaryOperatorExpression) _representedExpression;
			final ExpressionInnerPanel me = this;

			OperatorPanel operatorPanel = new OperatorPanel(exp.getOperator());

			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getLeftArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setLeftArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			gridbag.setConstraints(leftArg, c);
			add(leftArg);

			c.weightx = 0.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.NONE;
			gridbag.setConstraints(operatorPanel, c);
			add(operatorPanel);

			ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setRightArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(rightArg, c);
			add(rightArg);

			isHorizontallyLayouted = true;

		}

		private void addUnaryExpressionHorizontalLayout() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			setLayout(gridbag);

			final UnaryOperatorExpression exp = (UnaryOperatorExpression) _representedExpression;
			final ExpressionInnerPanel me = this;

			OperatorPanel operatorPanel = new OperatorPanel(exp.getOperator());

			c.weightx = 0.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.NONE;
			gridbag.setConstraints(operatorPanel, c);
			add(operatorPanel);

			ExpressionInnerPanel arg = new ExpressionInnerPanel(exp.getArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(arg, c);
			add(arg);

			isHorizontallyLayouted = true;
		}

		private void addJavaInstanceOfExpressionVerticalLayout() {
			GridBagLayout gridbag2 = new GridBagLayout();
			GridBagConstraints c2 = new GridBagConstraints();
			setLayout(gridbag2);

			final JavaInstanceOfExpression exp = (JavaInstanceOfExpression) _representedExpression;
			final ExpressionInnerPanel me = this;

			JLabel operatorPanel = new JLabel("instanceof");
			operatorPanel.setFont(operatorPanel.getFont().deriveFont(9.0f));

			c2.weightx = 0.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.CENTER;
			c2.fill = GridBagConstraints.VERTICAL;
			gridbag2.setConstraints(operatorPanel, c2);
			add(operatorPanel);

			JPanel argsPanel = new JPanel();

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			argsPanel.setLayout(gridbag);

			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(leftArg, c);
			argsPanel.add(leftArg);

			TypeSelector right = new TypeSelector(exp.getType()) {
				public void apply() {
					super.apply();
					exp.setType(getEditedObject());
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(right, c);
			argsPanel.add(right);

			c2.weightx = 1.0;
			c2.weighty = 0.0;
			c2.anchor = GridBagConstraints.NORTH;
			c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(argsPanel, c2);
			add(argsPanel);

			Box box = Box.createHorizontalBox();
			c2.weightx = 1.0;
			c2.weighty = 1.0;
			c2.anchor = GridBagConstraints.SOUTH;
			c2.fill = GridBagConstraints.BOTH;
			c2.gridwidth = GridBagConstraints.REMAINDER;
			gridbag2.setConstraints(box, c2);
			add(box);

			isHorizontallyLayouted = false;
		}

		private void addJavaInstanceOfExpressionHorizontalLayout() {
			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			setLayout(gridbag);

			final JavaInstanceOfExpression exp = (JavaInstanceOfExpression) _representedExpression;
			final ExpressionInnerPanel me = this;

			JLabel operatorPanel = new JLabel("instanceof");
			operatorPanel.setFont(operatorPanel.getFont().deriveFont(9.0f));

			ExpressionInnerPanel leftArg = new ExpressionInnerPanel(exp.getArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			gridbag.setConstraints(leftArg, c);
			add(leftArg);

			c.weightx = 0.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.VERTICAL;
			gridbag.setConstraints(operatorPanel, c);
			add(operatorPanel);

			/*ExpressionInnerPanel rightArg = new ExpressionInnerPanel(exp.getRightArgument()) {
				@Override
				public void representedExpressionChanged(Expression newExpression) {
					exp.setRightArgument(newExpression);
					// Take care that we have here a recursion with inner classes
					// (I known this is not recommanded)
					// We should here access embedding instance !
					me.representedExpressionChanged(exp);
				}
			};*/

			TypeSelector right = new TypeSelector(exp.getType()) {
				public void apply() {
					super.apply();
					exp.setType(getEditedObject());
				}
			};

			c.weightx = 1.0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(right, c);
			add(right);

			isHorizontallyLayouted = true;

		}

		private void update() {
			ExpressionInnerPanel parent = (ExpressionInnerPanel) SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, this);
			if (parent != null && parent.isHorizontallyLayouted && parent._representedExpression.getDepth() > 1) {
				parent.update();
				return;
			}
			removeFocusListeners();
			removeAll();

			// System.out.println("Update in ExpressionInnerPanel with " + _representedExpression + " of " +
			// _representedExpression.getClass());

			if (_representedExpression instanceof SymbolicConstant) {
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				setLayout(gridbag);
				JLabel symbolicConstantLabel = new JLabel(((SymbolicConstant) _representedExpression).getSymbol());
				c.weightx = 0.0;
				c.weighty = 1.0;
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.NONE;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(symbolicConstantLabel, c);
				add(symbolicConstantLabel);
			}

			else if (_representedExpression instanceof BindingValue || _representedExpression instanceof Constant) {
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				setLayout(gridbag);

				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("Building BindingSelector with " + _representedExpression);
				}

				_bindingSelector = getBindingSelector().makeSubBindingSelector(innerDataBinding, new ApplyCancelListener() {

					@Override
					public void fireApplyPerformed() {
						innerDataBinding.setExpression(_bindingSelector.getEditedObject().getExpression());
						setRepresentedExpression(_bindingSelector.getEditedObject().getExpression());
						fireEditedExpressionChanged(dataBinding);
					}

					@Override
					public void fireCancelPerformed() {
					}

				});

				if (innerDataBinding != null) {
					_bindingSelector.setRevertValue(innerDataBinding.clone());
				}

				c.weightx = 1.0;
				c.weighty = 1.0;
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;
				gridbag.setConstraints(_bindingSelector, c);
				add(_bindingSelector);

			}

			else if (_representedExpression instanceof BinaryOperatorExpression) {
				if (_representedExpression.getDepth() > 1) {
					addBinaryExpressionVerticalLayout();
				}
				else {
					addBinaryExpressionHorizontalLayout();
				}
			}

			else if (_representedExpression instanceof UnaryOperatorExpression) {
				addUnaryExpressionHorizontalLayout();
			}

			else if (_representedExpression instanceof ConditionalExpression) {
				addConditionalExpressionVerticalLayout();
			}

			else if (_representedExpression instanceof JavaInstanceOfExpression) {
				if (_representedExpression.getDepth() > 1) {
					addJavaInstanceOfExpressionVerticalLayout();
				}
				else {
					addJavaInstanceOfExpressionHorizontalLayout();
				}
			}

			addFocusListeners();
			revalidate();
			repaint();
		}

		public Expression getRepresentedExpression() {
			return _representedExpression;
		}

		public void setRepresentedExpression(Expression representedExpression) {
			_representedExpression = representedExpression;
			representedExpressionChanged(representedExpression);
			update();
			updateInfos();
		}

		private void updateInfos() {
			_checkEditedExpression();
			updateExpressionTextArea();
			updateAdditionalInformations();
		}

		public abstract void representedExpressionChanged(Expression newExpression);
	}

	protected void appendBinaryOperator(BinaryOperator operator) {
		// System.out.println("appendBinaryOperator " + operator);
		if (focusReceiver != null) {
			BindingValue variable = getBindingSelector().makeBinding();// new BindingValue();
			// variable.setDataBinding(dataBinding);
			Expression newExpression = getBindingSelector().getExpressionGrammar().makeBinaryOperatorExpression(operator,
					focusReceiver.getRepresentedExpression(), variable);
			// Expression newExpression = new JavaBinaryOperatorExpression(operator, focusReceiver.getRepresentedExpression(), variable);
			/*logger.info("variable="+variable.getBindingValue());
			logger.info("owner="+variable.getBindingValue().getOwner());
			logger.info("bd="+variable.getBindingValue().getBindingDefinition());*/
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}

	protected void appendUnaryOperator(UnaryOperator operator) {
		// System.out.println("appendUnaryOperator " + operator);
		if (focusReceiver != null) {
			Expression newExpression = getBindingSelector().getExpressionGrammar().makeUnaryOperatorExpression(operator,
					focusReceiver.getRepresentedExpression());
			// Expression newExpression = new UnaryOperatorExpression(operator, focusReceiver.getRepresentedExpression());
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}

	protected void appendConditional() {
		// System.out.println("appendConditional");
		if (focusReceiver != null) {
			BindingValue condition = getBindingSelector().makeBinding();// new BindingValue();
			// condition.setDataBinding(dataBinding);
			BindingValue elseExpression = getBindingSelector().makeBinding();// new BindingValue();
			// elseExpression.setDataBinding(dataBinding);
			Expression newExpression = getBindingSelector().getExpressionGrammar().makeConditionalExpression(condition,
					focusReceiver.getRepresentedExpression(), elseExpression);
			focusReceiver.setRepresentedExpression(newExpression);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		focusReceiver = (ExpressionInnerPanel) SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class, (Component) e.getSource());
		if (focusReceiver != null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Focus gained by expression " + focusReceiver.getRepresentedExpression() + " receiver=" + focusReceiver);
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// Dont care
		ExpressionInnerPanel whoLoseFocus = (ExpressionInnerPanel) SwingUtilities.getAncestorOfClass(ExpressionInnerPanel.class,
				(Component) e.getSource());
		if (whoLoseFocus != null) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Focus lost by expression " + whoLoseFocus.getRepresentedExpression() + " looser=" + whoLoseFocus);
			}
		}
	}

}

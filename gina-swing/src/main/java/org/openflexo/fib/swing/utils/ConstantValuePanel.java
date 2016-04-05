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

package org.openflexo.fib.swing.utils;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Type;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.expr.Constant.BooleanConstant;
import org.openflexo.connie.expr.Constant.FloatConstant;
import org.openflexo.connie.expr.Constant.IntegerConstant;
import org.openflexo.connie.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.connie.expr.Constant.StringConstant;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.fib.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.DateSelector;
import org.openflexo.toolbox.ToolBox;

/**
 * This panel allows to select or edit a constant value in the context of a {@link BindingValueSelectorPanel}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
class ConstantValuePanel extends JPanel {

	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 9);

	/**
	 * 
	 */
	private final BindingValueSelectorPanel bindingValueSelectorPanel;
	boolean isUpdatingPanel = false;
	protected JCheckBox selectStaticBindingCB = null;
	protected JComboBox selectValueCB = null;
	protected JTextField enterValueTF = null;
	protected DateSelector dateSelector = null;
	protected DurationSelector durationSelector = null;
	protected JSpinner integerValueChooser = null;
	protected JComboBox typeCB = null;

	private EvaluationType currentType;

	protected ConstantValuePanel(BindingValueSelectorPanel bindingValueSelectorPanel) {
		this.bindingValueSelectorPanel = bindingValueSelectorPanel;
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		initConstantValuePanel();
		updateConstantValuePanel();
	}

	final private void initConstantValuePanel() {
		selectStaticBindingCB = null;
		selectValueCB = null;
		enterValueTF = null;
		dateSelector = null;
		durationSelector = null;
		integerValueChooser = null;
		selectStaticBindingCB = new JCheckBox(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "define_a_constant_value"));
		selectStaticBindingCB.setFont(SMALL_FONT);
		selectStaticBindingCB.setSelected(false);
		selectStaticBindingCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConstantValuePanel.this.bindingValueSelectorPanel.setEditStaticValue(selectStaticBindingCB.isSelected());
			}
		});
		add(selectStaticBindingCB);
		if (bindingValueSelectorPanel.bindingSelector.getEditedObject() == null
				|| bindingValueSelectorPanel.bindingSelector.getEditedObject().getDeclaredType() == null) {
			enterValueTF = new JTextField(10);
			enterValueTF.setFont(SMALL_FONT);
			add(enterValueTF);
			currentType = EvaluationType.LITERAL;
			disableStaticBindingPanel();
		} else {
			currentType = kindOf(bindingValueSelectorPanel.bindingSelector.getEditedObject().getDeclaredType());
			if (currentType == EvaluationType.BOOLEAN) {
				final String UNSELECTED = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "select_a_value");
				final String TRUE = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "true");
				final String FALSE = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "false");
				String[] availableValues = { UNSELECTED, TRUE, FALSE };
				selectValueCB = new JComboBox(availableValues);
				selectValueCB.setFont(SMALL_FONT);
				selectValueCB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						if (selectValueCB.getSelectedItem().equals(TRUE)) {
							bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(BooleanConstant.TRUE);
							bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
						} else if (selectValueCB.getSelectedItem().equals(FALSE)) {
							bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(BooleanConstant.FALSE);
							bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
						}
					}
				});
				add(selectValueCB);
			} else if (currentType == EvaluationType.ARITHMETIC_INTEGER) {
				SpinnerNumberModel valueModel = new SpinnerNumberModel(1, Short.MIN_VALUE, Short.MAX_VALUE, 1);
				integerValueChooser = new JSpinner(valueModel);
				integerValueChooser.setEditor(new JSpinner.NumberEditor(integerValueChooser, "#"));
				integerValueChooser.setMinimumSize(integerValueChooser.getPreferredSize());
				integerValueChooser.setValue(1);
				integerValueChooser.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						Object v = integerValueChooser.getValue();
						if (v instanceof Number) {
							bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(
									new Constant.IntegerConstant(((Number) v).longValue()));
							bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
						}
					}
				});
				integerValueChooser.setFont(SMALL_FONT);
				integerValueChooser.getEditor().setFont(SMALL_FONT);
				add(integerValueChooser);
			} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				enterValueTF = new JTextField(10);
				enterValueTF.setFont(SMALL_FONT);
				enterValueTF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						if (bindingValueSelectorPanel.bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(
									bindingValueSelectorPanel.bindingSelector.makeStaticBindingFromString(enterValueTF.getText()));
							bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
						}
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!ConstantValuePanel.this.bindingValueSelectorPanel.connectButton.isEnabled()
								&& bindingValueSelectorPanel.bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
							ConstantValuePanel.this.bindingValueSelectorPanel.connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
								ConstantValuePanel.this.bindingValueSelectorPanel.connectButton.setSelected(true);
							}
						}
					}
				});
				add(enterValueTF);
			} else if (currentType == EvaluationType.STRING) {
				enterValueTF = new JTextField(10);
				enterValueTF.setFont(SMALL_FONT);
				enterValueTF.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (isUpdatingPanel) {
							return;
						}
						if (bindingValueSelectorPanel.bindingSelector.isAcceptableStaticBindingValue('"' + enterValueTF.getText() + '"')) {
							bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(
									bindingValueSelectorPanel.bindingSelector.makeStaticBindingFromString('"' + enterValueTF.getText() + '"'));
							bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
						}
					}
				});
				enterValueTF.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!ConstantValuePanel.this.bindingValueSelectorPanel.connectButton.isEnabled() && enterValueTF.getText().length() > 0) {
							ConstantValuePanel.this.bindingValueSelectorPanel.connectButton.setEnabled(true);
							if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
								ConstantValuePanel.this.bindingValueSelectorPanel.connectButton.setSelected(true);
							}
						}
					}
				});
				add(enterValueTF);
			}

			if (bindingValueSelectorPanel.getEditStaticValue()) {
				enableStaticBindingPanel();
				for (int i = 0; i < bindingValueSelectorPanel.getVisibleColsCount(); i++) {
					bindingValueSelectorPanel.listAtIndex(i).setEnabled(false);
				}
			} else {
				disableStaticBindingPanel();
			}

		}

		if (bindingValueSelectorPanel.bindingSelector.getEditedObject() == null
				|| bindingValueSelectorPanel.bindingSelector.getEditedObject().getDeclaredType() == null
				|| TypeUtils.isObject(bindingValueSelectorPanel.bindingSelector.getEditedObject().getDeclaredType())) {
			final String SELECT = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "select");
			final String BOOLEAN = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "boolean");
			final String INTEGER = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "integer");
			final String FLOAT = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "float");
			final String STRING = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "string");
			final String DATE = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "date");
			final String DURATION = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "duration");
			final String DKV = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "enum");
			final String NULL = FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "null");
			String[] availableValues = { SELECT, BOOLEAN, INTEGER, FLOAT, STRING, DATE, DURATION, DKV, NULL };
			typeCB = new JComboBox(availableValues);
			typeCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isUpdatingPanel) {
						return;
					}
					if (bindingValueSelectorPanel.bindingSelector.getEditedObject() == null) {
						return;
					}
					if (typeCB.getSelectedItem().equals(BOOLEAN)) {
						bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(Constant.BooleanConstant.TRUE);
						bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(INTEGER)) {
						bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(new Constant.IntegerConstant(0));
						bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(FLOAT)) {
						bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(new Constant.FloatConstant(0));
						bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(STRING)) {
						bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(new Constant.StringConstant(""));
						bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
					} else if (typeCB.getSelectedItem().equals(NULL)) {
						bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(ObjectSymbolicConstant.NULL);
						bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
					}
				}
			});
			isUpdatingPanel = true;
			if (currentType == EvaluationType.BOOLEAN) {
				typeCB.setSelectedItem(BOOLEAN);
			} else if (currentType == EvaluationType.ARITHMETIC_INTEGER) {
				typeCB.setSelectedItem(INTEGER);
			} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				typeCB.setSelectedItem(FLOAT);
			} else if (currentType == EvaluationType.STRING) {
				typeCB.setSelectedItem(STRING);
			} else if (currentType == EvaluationType.DATE) {
				typeCB.setSelectedItem(DATE);
			} else if (currentType == EvaluationType.DURATION) {
				typeCB.setSelectedItem(DURATION);
			} else if (currentType == EvaluationType.ENUM) {
				typeCB.setSelectedItem(DKV);
			}
			isUpdatingPanel = false;

			if (bindingValueSelectorPanel.bindingSelector.getEditedObject() != null
					&& bindingValueSelectorPanel.bindingSelector.getEditedObject().getExpression() == ObjectSymbolicConstant.NULL) {
				isUpdatingPanel = true;
				typeCB.setSelectedItem(NULL);
				isUpdatingPanel = false;
			}

			typeCB.setFont(SMALL_FONT);
			add(typeCB);
		}

	}

	void willApply() {
		if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
			if (bindingValueSelectorPanel.bindingSelector.isAcceptableStaticBindingValue(enterValueTF.getText())) {
				bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(
						bindingValueSelectorPanel.bindingSelector.makeStaticBindingFromString(enterValueTF.getText()));
				bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
			}
		} else if (currentType == EvaluationType.STRING) {
			if (bindingValueSelectorPanel.bindingSelector.isAcceptableStaticBindingValue('"' + enterValueTF.getText() + '"')) {
				bindingValueSelectorPanel.bindingSelector.getEditedObject().setExpression(
						bindingValueSelectorPanel.bindingSelector.makeStaticBindingFromString('"' + enterValueTF.getText() + '"'));
				bindingValueSelectorPanel.bindingSelector.fireEditedObjectChanged();
			}
		}
	}

	private EvaluationType kindOf(Type type) {
		if (TypeUtils.isObject(type) && bindingValueSelectorPanel.bindingSelector.getEditedObject().isConstant()) {
			return ((Constant) bindingValueSelectorPanel.bindingSelector.getEditedObject().getExpression()).getEvaluationType();
		} else {
			return TypeUtils.kindOfType(type);
		}
	}

	final void updateConstantValuePanel() {
		isUpdatingPanel = true;

		EvaluationType newType;
		if (bindingValueSelectorPanel.bindingSelector.getEditedObject() == null
				|| bindingValueSelectorPanel.bindingSelector.getEditedObject().getDeclaredType() == null) {
			newType = EvaluationType.LITERAL;
		} else {
			newType = kindOf(bindingValueSelectorPanel.bindingSelector.getEditedObject().getDeclaredType());
		}
		if (newType != currentType) {
			removeAll();
			initConstantValuePanel();
			revalidate();
			repaint();
		}
		DataBinding edited = bindingValueSelectorPanel.bindingSelector.getEditedObject();

		isUpdatingPanel = true;

		if (edited != null && edited.isConstant()) {

			if (currentType == EvaluationType.BOOLEAN && (edited.getExpression() instanceof BooleanConstant)) {
				selectValueCB.setSelectedItem(edited.getExpression().toString());
			} else if (currentType == EvaluationType.ARITHMETIC_INTEGER && (edited.getExpression() instanceof IntegerConstant)) {
				integerValueChooser.setValue(((IntegerConstant) edited.getExpression()).getValue());
			} else if (currentType == EvaluationType.ARITHMETIC_FLOAT) {
				if (edited.getExpression() instanceof FloatConstant) {
					enterValueTF.setText("" + ((FloatConstant) edited.getExpression()).getValue());
				} else if (edited.getExpression() instanceof IntegerConstant) {
					enterValueTF.setText("" + ((IntegerConstant) edited.getExpression()).getValue());
				}
			} else if (currentType == EvaluationType.STRING && (edited.getExpression() instanceof StringConstant)) {
				enterValueTF.setText(((StringConstant) edited.getExpression()).getValue());
			}
		}

		isUpdatingPanel = false;

	}

	void enableStaticBindingPanel() {
		bindingValueSelectorPanel.connectButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "validate"));
		selectStaticBindingCB.setSelected(true);
		if (selectValueCB != null) {
			selectValueCB.setEnabled(true);
		}
		if (enterValueTF != null) {
			enterValueTF.setEnabled(true);
		}
		if (dateSelector != null) {
			dateSelector.setEnabled(true);
		}
		if (durationSelector != null) {
			durationSelector.setEnabled(true);
		}
		if (integerValueChooser != null) {
			integerValueChooser.setEnabled(true);
		}
	}

	void disableStaticBindingPanel() {
		bindingValueSelectorPanel.connectButton.setText(FlexoLocalization.localizedForKey(FIBModelObjectImpl.LOCALIZATION, "connect"));
		selectStaticBindingCB.setSelected(false);
		if (selectValueCB != null) {
			selectValueCB.setEnabled(false);
		}
		if (enterValueTF != null) {
			enterValueTF.setEnabled(false);
		}
		if (dateSelector != null) {
			dateSelector.setEnabled(false);
		}
		if (durationSelector != null) {
			durationSelector.setEnabled(false);
		}
		if (integerValueChooser != null) {
			integerValueChooser.setEnabled(false);
		}
	}
}
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

package org.openflexo.fib.swing.view.widget;

import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.swing.view.FIBWidgetView;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.event.description.EventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit an Long or an Integer object
 * 
 * @author sguerin
 */
public abstract class FIBNumberWidget<T extends Number> extends FIBWidgetView<FIBNumber, JSpinner, T> {

	static final Logger logger = Logger.getLogger(FIBNumberWidget.class.getPackage().getName());

	boolean validateOnReturn;

	protected boolean ignoreTextfieldChanges = false;

	private final JPanel container;

	private final JCheckBox checkBox;

	JSpinner valueChooser;

	/**
	 * @param model
	 */
	public FIBNumberWidget(FIBNumber model, FIBController controller) {
		super(model, controller);
		validateOnReturn = model.getValidateOnReturn();

		Number min = model.getMinValue();
		Number max = model.getMaxValue();
		Number inc = model.getIncrement();

		container = new JPanel(new GridBagLayout());
		container.setOpaque(false);
		checkBox = new JCheckBox();
		checkBox.setToolTipText(FlexoLocalization.localizedForKey("undefined_value", checkBox));
		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				valueChooser.setEnabled(!checkBox.isSelected());
				
				/*FIBNumberWidget.this.actionPerformed(FIBEventFactory.getInstance().createChangeValueEvent(
						"change", FIBNumberWidget.this.checkBox.get));*/
				
				updateModelFromWidget();
			}
		});
		SpinnerNumberModel valueModel = makeSpinnerModel();
		valueChooser = new JSpinner(valueModel);
		valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser /* , "#.##" */));
		valueChooser.setValue(getDefaultValue());
		valueChooser.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == valueChooser && !ignoreTextfieldChanges) {
					GinaStackEvent stack = FIBNumberWidget.this.GENotifier.raise(FIBEventFactory.getInstance().createValueEvent(
							FIBValueEventDescription.CHANGED, FIBNumberWidget.this.valueChooser.getValue()));
					
					updateModelFromWidget();
					
					stack.end();
				}
			}
		});
		valueChooser.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JComponent editor = valueChooser.getEditor();
		if (editor instanceof DefaultEditor) {
			((DefaultEditor) editor).getTextField().setHorizontalAlignment(SwingConstants.LEFT);
			if (!ToolBox.isMacOSLaf()) {
				((DefaultEditor) editor).getTextField().setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
			}
		}

		if (model.getColumns() != null) {
			getTextField().setColumns(model.getColumns());
		} else {
			getTextField().setColumns(getDefaultColumns());
		}

		if (isReadOnly()) {
			valueChooser.setEnabled(false);
		}

		getJComponent().addFocusListener(this);
		getTextField().addFocusListener(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		container.add(valueChooser, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		container.add(checkBox, gbc);
		updateCheckboxVisibility();
		updateFont();
		if (!ToolBox.isMacOSLaf()) {
			container.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}
	}
	
	@Override
	public void executeEvent(EventDescription e) {
		widgetExecuting = true;

		switch (e.getAction()) {
		case FIBValueEventDescription.CHANGED: {
			T value = parseValue(e.getValue());
			System.out.println(value);
			valueChooser.setValue(value);
			break;
		}
		}
		
		widgetExecuting = false;
	}
	
	public abstract T parseValue(String str);
	
	/*@Override
	public boolean isMatching(GinaEvent e) {

		switch (e.getAction()) {
		case FIBValueEvent.CHANGED: {
			try {
				Number number = NumberFormat.getInstance().parse(e.getAbsoluteValue());
				@SuppressWarnings("unchecked")
				T value = (T) number;
				return (value == getValue());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			break;
		}
		}
		
		return false;
	}*/

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		if (event.getSource() == getTextField()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getTextField().selectAll();
				}
			});
		}
	}

	protected abstract SpinnerNumberModel makeSpinnerModel();

	public abstract T getDefaultValue();

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// logger.info("updateWidgetFromModel() with "+getValue());
		T editedValue = null;
		if (!checkBox.isSelected()) {
			editedValue = getEditedValue();
		}
		if (notEquals(getValue(), editedValue)) {

			widgetUpdating = true;
			valueChooser.setEnabled((getValue() != null || !getWidget().getAllowsNull()) && isEnabled());
			checkBox.setSelected(getValue() == null);
			T currentValue = null;

			if (getValue() == null) {
				// setValue(getDefaultValue());
				currentValue = getDefaultValue();
			} else {
				try {
					currentValue = getValue();
				} catch (ClassCastException e) {
					logger.warning("ClassCastException: " + e.getMessage());
					logger.warning("Data: " + getWidget().getData() + " returned " + getValue());
				}
			}

			ignoreTextfieldChanges = true;
			try {
				valueChooser.setValue(currentValue);
			} catch (IllegalArgumentException e) {
				logger.warning("IllegalArgumentException: " + e.getMessage());
			}

			ignoreTextfieldChanges = false;
			widgetUpdating = false;

			return true;
		}
		return false;
	}

	protected abstract T getEditedValue();

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		T editedValue = null;
		if (!checkBox.isSelected()) {
			editedValue = getEditedValue();
		}
		if (notEquals(getValue(), editedValue)) {
			if (isReadOnly()) {
				return false;
			}
			modelUpdating = true;
			setValue(editedValue);
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return container;
	}

	@Override
	public JSpinner getDynamicJComponent() {
		return valueChooser;
	}

	public JFormattedTextField getTextField() {
		JComponent editor = valueChooser.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			return ((JSpinner.DefaultEditor) editor).getTextField();
		}
		return null;
	}

	public abstract int getDefaultColumns();

	public static class FIBByteWidget extends FIBNumberWidget<Byte> {
		public FIBByteWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Byte min = getWidget().retrieveMinValue().byteValue();
			Byte max = getWidget().retrieveMaxValue().byteValue();
			Byte inc = getWidget().retrieveIncrement().byteValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Byte getDefaultValue() {
			return new Byte((byte) 0);
		}

		@Override
		protected Byte getEditedValue() {
			return ((Number) valueChooser.getValue()).byteValue();
		}

		@Override
		public int getDefaultColumns() {
			return 4;
		}

		@Override
		public Byte parseValue(String str) {
			return Byte.parseByte(str);
		}
	}

	public static class FIBShortWidget extends FIBNumberWidget<Short> {
		public FIBShortWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Short min = getWidget().retrieveMinValue().shortValue();
			Short max = getWidget().retrieveMaxValue().shortValue();
			Short inc = getWidget().retrieveIncrement().shortValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Short getDefaultValue() {
			return new Short((short) 0);
		}

		@Override
		protected Short getEditedValue() {
			return ((Number) valueChooser.getValue()).shortValue();
		}

		@Override
		public int getDefaultColumns() {
			return 6;
		}

		@Override
		public Short parseValue(String str) {
			return Short.parseShort(str);
		}
	}

	public static class FIBIntegerWidget extends FIBNumberWidget<Integer> {
		public FIBIntegerWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Integer min = getWidget().retrieveMinValue().intValue();
			Integer max = getWidget().retrieveMaxValue().intValue();
			Integer inc = getWidget().retrieveIncrement().intValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Integer getDefaultValue() {
			if (getWidget().getMinValue() != null && getWidget().getMinValue().intValue() > 0) {
				return getWidget().getMinValue().intValue();
			}
			return Integer.valueOf(0);
		}

		@Override
		protected Integer getEditedValue() {
			return ((Number) valueChooser.getValue()).intValue();
		}

		@Override
		public int getDefaultColumns() {
			return 8;
		}

		@Override
		public Integer parseValue(String str) {
			return Integer.parseInt(str);
		}
	}

	public static class FIBLongWidget extends FIBNumberWidget<Long> {
		public FIBLongWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Long min = getWidget().retrieveMinValue().longValue();
			Long max = getWidget().retrieveMaxValue().longValue();
			Long inc = getWidget().retrieveIncrement().longValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Long getDefaultValue() {
			return Long.valueOf(0);
		}

		@Override
		protected Long getEditedValue() {
			return ((Number) valueChooser.getValue()).longValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}

		@Override
		public Long parseValue(String str) {
			return Long.parseLong(str);
		}
	}

	public static class FIBFloatWidget extends FIBNumberWidget<Float> {
		public FIBFloatWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			float min = getWidget().retrieveMinValue().floatValue();
			float max = getWidget().retrieveMaxValue().floatValue();
			float inc = getWidget().retrieveIncrement().floatValue();
			try {
				return new SpinnerNumberModel((Number) getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Float getDefaultValue() {
			return Float.valueOf(0.0f);
		}

		@Override
		protected Float getEditedValue() {
			return ((Number) valueChooser.getValue()).floatValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}

		@Override
		public Float parseValue(String str) {
			return Float.parseFloat(str);
		}
	}

	public static class FIBDoubleWidget extends FIBNumberWidget<Double> {
		public FIBDoubleWidget(FIBNumber model, FIBController controller) {
			super(model, controller);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Double min = getWidget().retrieveMinValue().doubleValue();
			Double max = getWidget().retrieveMaxValue().doubleValue();
			Double inc = getWidget().retrieveIncrement().doubleValue();
			try {
				return new SpinnerNumberModel(getDefaultValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Double getDefaultValue() {
			return Double.valueOf(0.0);
		}

		@Override
		protected Double getEditedValue() {
			return ((Number) valueChooser.getValue()).doubleValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}

		@Override
		public Double parseValue(String str) {
			return Double.parseDouble(str);
		}
	}

	final public void updateCheckboxVisibility() {
		checkBox.setVisible(getWidget().getAllowsNull() && !TypeUtils.isPrimitive(getComponent().getDataType()));
	}

	public void updateColumns() {
		if (getComponent().getColumns() != null) {
			getTextField().setColumns(getComponent().getColumns());
		} else {
			getTextField().setColumns(0);
		}
		container.revalidate();
	}

}
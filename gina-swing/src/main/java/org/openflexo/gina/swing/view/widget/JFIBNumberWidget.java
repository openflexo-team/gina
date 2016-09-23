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
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
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
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.event.description.FIBEventFactory;
import org.openflexo.gina.event.description.FIBValueEventDescription;
import org.openflexo.gina.manager.GinaStackEvent;
import org.openflexo.gina.model.FIBModelObject.FIBModelObjectImpl;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBNumberWidget.NumberSelectorPanel;
import org.openflexo.gina.view.widget.impl.FIBNumberWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation for a widget able to edit a number
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 *
 * @author sylvain
 */
public class JFIBNumberWidget<T extends Number> extends FIBNumberWidgetImpl<NumberSelectorPanel<T>, T>
		implements FocusListener, JFIBView<FIBNumber, NumberSelectorPanel<T>> {

	static final Logger logger = Logger.getLogger(JFIBNumberWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing Font Widget<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingNumberWidgetRenderingAdapter<T extends Number> extends SwingRenderingAdapter<NumberSelectorPanel<T>>
			implements NumberWidgetRenderingAdapter<NumberSelectorPanel<T>, T> {

		@Override
		public T getNumber(NumberSelectorPanel<T> component) {
			return component.getEditedValue();
		}

		@Override
		public void setNumber(NumberSelectorPanel<T> component, T aNumber) {
			component.setEditedValue(aNumber);
		}

		@Override
		public int getColumns(NumberSelectorPanel<T> component) {
			return component.getTextField().getColumns();
		}

		@Override
		public void setColumns(NumberSelectorPanel<T> component, int columns) {
			component.getTextField().setColumns(columns);
		}

		@Override
		public boolean isCheckboxVisible(NumberSelectorPanel<T> component) {
			return component.checkBox.isVisible();
		}

		@Override
		public void setCheckboxVisible(NumberSelectorPanel<T> component, boolean visible) {
			component.checkBox.setVisible(visible);
		}

		@Override
		public boolean isCheckboxSelected(NumberSelectorPanel<T> component) {
			return component.checkBox.isSelected();
		}

		@Override
		public void setCheckboxSelected(NumberSelectorPanel<T> component, boolean selected) {
			component.checkBox.setSelected(selected);
		}

		@Override
		public boolean isWidgetEnabled(NumberSelectorPanel<T> component) {
			return component.checkBox.isEnabled();
		}

		@Override
		public void setWidgetEnabled(NumberSelectorPanel<T> component, boolean enabled) {
			component.checkBox.setEnabled(enabled);
		}

		@Override
		public Color getDefaultForegroundColor(NumberSelectorPanel<T> component) {
			return UIManager.getColor("FormattedTextField.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(NumberSelectorPanel<T> component) {
			return UIManager.getColor("FormattedTextField.background");
		}

		@Override
		public JSpinner getDynamicJComponent(NumberSelectorPanel<T> technologyComponent) {
			return technologyComponent.getValueChooser();
		}
	}

	public static abstract class NumberSelectorPanel<T extends Number> extends JPanel {
		protected final JCheckBox checkBox;
		protected final JSpinner valueChooser;
		protected final JFIBNumberWidget<T> widget;

		public NumberSelectorPanel(JFIBNumberWidget<T> aWidget) {
			super(new GridBagLayout());
			this.widget = aWidget;

			setOpaque(false);
			checkBox = new JCheckBox();
			checkBox.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedForKey("undefined_value", checkBox));
			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					valueChooser.setEnabled(!checkBox.isSelected());
					// widget.updateModelFromWidget();
					widget.valueChanged();
				}
			});
			SpinnerNumberModel valueModel = makeSpinnerModel();
			valueChooser = new JSpinner(valueModel);
			valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser /*
																			* ,
																			* "#.##"
																			*/));
			valueChooser.setValue(widget.getDefaultValue().doubleValue());
			valueChooser.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (e.getSource() == valueChooser && !widget.ignoreTextfieldChanges) {
						GinaStackEvent stack = widget.GENotifier.raise(
								FIBEventFactory.getInstance().createValueEvent(FIBValueEventDescription.CHANGED, valueChooser.getValue()));
						// widget.updateModelFromWidget();
						widget.valueChanged();
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

			if (widget.getWidget().getColumns() != null) {
				getTextField().setColumns(widget.getWidget().getColumns());
			}
			else {
				getTextField().setColumns(getDefaultColumns());
			}

			if (widget.isReadOnly()) {
				valueChooser.setEnabled(false);
			}

			addFocusListener(widget);
			getTextField().addFocusListener(widget);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			add(valueChooser, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			add(checkBox, gbc);
			if (!ToolBox.isMacOSLaf()) {
				setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
						RIGHT_COMPENSATING_BORDER));
			}

		}

		public void updateLanguage() {
			checkBox.setToolTipText(FIBModelObjectImpl.GINA_LOCALIZATION.localizedTooltipForKey("undefined_value", checkBox));
		}

		protected abstract SpinnerNumberModel makeSpinnerModel();

		public JFormattedTextField getTextField() {
			JComponent editor = valueChooser.getEditor();
			if (editor instanceof JSpinner.DefaultEditor) {
				return ((JSpinner.DefaultEditor) editor).getTextField();
			}
			return null;
		}

		public abstract int getDefaultColumns();

		public abstract T getEditedValue();

		public void setEditedValue(T aValue) {
			valueChooser.setValue(aValue.doubleValue());
		}

		public JSpinner getValueChooser() {
			return valueChooser;
		}
	}

	/**
	 * @param model
	 */
	public JFIBNumberWidget(FIBNumber model, FIBController controller) {
		super(model, controller, new SwingNumberWidgetRenderingAdapter<T>());
	}

	@Override
	public SwingNumberWidgetRenderingAdapter<T> getRenderingAdapter() {
		return (SwingNumberWidgetRenderingAdapter<T>) super.getRenderingAdapter();
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
	protected NumberSelectorPanel<T> makeTechnologyComponent() {
		if (getWidget().getNumberType() == null) {
			return null;
		}
		else {
			switch (getWidget().getNumberType()) {
				case ByteType:
					return (NumberSelectorPanel) new ByteSelectorPanel((JFIBNumberWidget) this);
				case ShortType:
					return (NumberSelectorPanel) new ShortSelectorPanel((JFIBNumberWidget) this);
				case IntegerType:
					return (NumberSelectorPanel) new IntegerSelectorPanel((JFIBNumberWidget) this);
				case LongType:
					return (NumberSelectorPanel) new LongSelectorPanel((JFIBNumberWidget) this);
				case FloatType:
					return (NumberSelectorPanel) new FloatSelectorPanel((JFIBNumberWidget) this);
				case DoubleType:
					return (NumberSelectorPanel) new DoubleSelectorPanel((JFIBNumberWidget) this);
				default:
					return null;
			}
		}
	}

	/*@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		if (event.getSource() == getTechnologyComponent().getTextField()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getTechnologyComponent().getTextField().selectAll();
				}
			});
		}
	}*/

	@Override
	protected void componentGainsFocus() {
		super.componentGainsFocus();
		getTechnologyComponent().getTextField().selectAll();
	}

	@SuppressWarnings("serial")
	public static class ByteSelectorPanel extends NumberSelectorPanel<Byte> {
		public ByteSelectorPanel(JFIBNumberWidget<Byte> aWidget) {
			super(aWidget);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Byte min = widget.getWidget().retrieveMinValue().byteValue();
			Byte max = widget.getWidget().retrieveMaxValue().byteValue();
			Byte inc = widget.getWidget().retrieveIncrement().byteValue();
			try {
				return new SpinnerNumberModel((Number) ((Number) widget.getDefaultValue()).byteValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Byte getEditedValue() {
			return ((Number) valueChooser.getValue()).byteValue();
		}

		@Override
		public int getDefaultColumns() {
			return 4;
		}

	}

	public static class ShortSelectorPanel extends NumberSelectorPanel<Short> {
		public ShortSelectorPanel(JFIBNumberWidget<Short> aWidget) {
			super(aWidget);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Short min = widget.getWidget().retrieveMinValue().shortValue();
			Short max = widget.getWidget().retrieveMaxValue().shortValue();
			Short inc = widget.getWidget().retrieveIncrement().shortValue();
			try {
				return new SpinnerNumberModel((Number) ((Number) widget.getDefaultValue()).shortValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Short getEditedValue() {
			return ((Number) valueChooser.getValue()).shortValue();
		}

		@Override
		public int getDefaultColumns() {
			return 6;
		}

	}

	public static class IntegerSelectorPanel extends NumberSelectorPanel<Integer> {
		public IntegerSelectorPanel(JFIBNumberWidget<Integer> aWidget) {
			super(aWidget);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Integer min = widget.getWidget().retrieveMinValue().intValue();
			Integer max = widget.getWidget().retrieveMaxValue().intValue();
			Integer inc = widget.getWidget().retrieveIncrement().intValue();
			try {
				return new SpinnerNumberModel((Number) ((Number) widget.getDefaultValue()).intValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Integer getEditedValue() {
			return ((Number) valueChooser.getValue()).intValue();
		}

		@Override
		public int getDefaultColumns() {
			return 8;
		}

	}

	public static class LongSelectorPanel extends NumberSelectorPanel<Long> {
		public LongSelectorPanel(JFIBNumberWidget<Long> aWidget) {
			super(aWidget);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Long min = widget.getWidget().retrieveMinValue().longValue();
			Long max = widget.getWidget().retrieveMaxValue().longValue();
			Long inc = widget.getWidget().retrieveIncrement().longValue();
			try {
				return new SpinnerNumberModel((Number) ((Number) widget.getDefaultValue()).longValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Long getEditedValue() {
			return ((Number) valueChooser.getValue()).longValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}

	}

	public static class FloatSelectorPanel extends NumberSelectorPanel<Float> {
		public FloatSelectorPanel(JFIBNumberWidget<Float> aWidget) {
			super(aWidget);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			float min = widget.getWidget().retrieveMinValue().floatValue();
			float max = widget.getWidget().retrieveMaxValue().floatValue();
			float inc = widget.getWidget().retrieveIncrement().floatValue();
			try {
				return new SpinnerNumberModel(((Number) widget.getDefaultValue()).floatValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Float getEditedValue() {
			return ((Number) valueChooser.getValue()).floatValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}

	}

	public static class DoubleSelectorPanel extends NumberSelectorPanel<Double> {
		public DoubleSelectorPanel(JFIBNumberWidget<Double> aWidget) {
			super(aWidget);
		}

		@Override
		protected SpinnerNumberModel makeSpinnerModel() {
			Double min = widget.getWidget().retrieveMinValue().doubleValue();
			Double max = widget.getWidget().retrieveMaxValue().doubleValue();
			Double inc = widget.getWidget().retrieveIncrement().doubleValue();
			try {
				return new SpinnerNumberModel((Number) ((Number) widget.getDefaultValue()).doubleValue(), min, max, inc);
			} catch (IllegalArgumentException e) {
				return new SpinnerNumberModel(min, min, max, inc);
			}
		}

		@Override
		public Double getEditedValue() {
			return ((Number) valueChooser.getValue()).doubleValue();
		}

		@Override
		public int getDefaultColumns() {
			return 10;
		}

	}

}

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

/**
 * Widget allowing to edit a file with a popup
 * 
 * @author sguerin
 * 
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.Duration.DurationUnit;

public class DurationSelector extends TextFieldCustomPopup<Duration> implements FIBCustomComponent<Duration> {
	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private static final String EMPTY_STRING = "";

	private Duration _revertValue;

	protected DurationChooserPanel durationChooserPanel;

	public DurationSelector() {
		this((Duration) null);
	}

	public DurationSelector(Duration aDuration) {
		super(aDuration);
		if (aDuration != null) {
			_revertValue = aDuration.clone();
		}
	}

	public DurationSelector(int col) {
		this((Duration) null, col);
	}

	public DurationSelector(Duration aDuration, int col) {
		super(aDuration, col);
		if (aDuration != null) {
			_revertValue = aDuration.clone();
		}
	}

	@Override
	protected ResizablePanel createCustomPanel(Duration editedObject) {
		durationChooserPanel = new DurationChooserPanel(editedObject);
		return durationChooserPanel;
	}

	@Override
	public void updateCustomPanel(Duration editedObject) {
		if (durationChooserPanel != null) {
			durationChooserPanel.update(editedObject);
		}
	}

	@Override
	public String renderedString(Duration editedObject) {
		if (editedObject != null) {
			return getLocalizedStringRepresentation(editedObject);
		}
		else {
			return EMPTY_STRING;
		}
	}

	public String getLocalizedStringRepresentation(Duration duration) {
		if (duration.getUnit() == null) {
			return "";
		}
		return duration.getValue() + " " + localizedForKey(duration.getUnit().getLocalizedKey() + (duration.getValue() > 1 ? "s" : ""));
	}

	protected class DurationChooserPanel extends ResizablePanel {
		ButtonsControlPanel controlPanel;
		JSpinner valueChooser;
		JComboBox<DurationUnit> unitChooser;

		private Dimension defaultDimension = new Dimension(320, 90);

		void update(Duration aDuration) {
			if (aDuration != null) {
				valueChooser.setValue((int) aDuration.getValue());
			}
			unitChooser.setSelectedItem(aDuration != null ? aDuration.getUnit() : null);
		}

		protected DurationChooserPanel(Duration aDuration) {
			super();

			unitChooser = new JComboBox<>(Duration.DurationUnit.values());
			unitChooser.setSelectedItem(DurationUnit.DAYS);
			unitChooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == unitChooser) {
						if (getEditedObject() == null) {
							setEditedObject(new Duration((Integer) valueChooser.getValue(), (DurationUnit) unitChooser.getSelectedItem()));
						}
						else {
							getEditedObject().setUnit((DurationUnit) unitChooser.getSelectedItem());
						}
					}
					fireEditedObjectChanged();
				}
			});
			unitChooser.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (returned != null && returned instanceof JLabel && value != null) {
						boolean isMultiple = getEditedObject() != null ? getEditedObject().getValue() > 1 : false;
						((JLabel) returned).setText(localizedForKey(((DurationUnit) value).getLocalizedKey() + (isMultiple ? "s" : "")));
					}
					return returned;
				}
			});

			SpinnerNumberModel valueModel = new SpinnerNumberModel(1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
			valueChooser = new JSpinner(valueModel);
			valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser, "#"));
			valueChooser.setMinimumSize(valueChooser.getPreferredSize());
			valueChooser.setValue(1);
			valueChooser.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (e.getSource() == valueChooser) {
						if (getEditedObject() == null) {
							setEditedObject(new Duration((Integer) valueChooser.getValue(), (DurationUnit) unitChooser.getSelectedItem()));
						}
						else {
							getEditedObject().setValue((Integer) valueChooser.getValue());
						}
					}
					fireEditedObjectChanged();
				}
			});

			JPanel valuesPanel = new JPanel();

			valuesPanel.setLayout(new FlowLayout());
			valuesPanel.add(valueChooser);
			valuesPanel.add(unitChooser);

			controlPanel = new ButtonsControlPanel() {
				@Override
				public String localizedForKeyAndButton(String key, JButton component) {
					return DurationSelector.this.localizedForKeyAndButton(key, component);
				}
			};
			controlPanel.addButton("apply", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					apply();
				}
			});
			controlPanel.addButton("cancel", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
			controlPanel.addButton("reset", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setEditedObject(null);
					apply();
				}
			});

			controlPanel.applyFocusTraversablePolicyTo(this, true);

			setLayout(new BorderLayout());
			add(valuesPanel, BorderLayout.CENTER);
			add(controlPanel, BorderLayout.SOUTH);

			durationChooserPanel = this;

			durationChooserPanel.setSize(defaultDimension);
			durationChooserPanel.setMinimumSize(defaultDimension);
			durationChooserPanel.setMaximumSize(defaultDimension);

			update(aDuration);
		}

		@Override
		public Dimension getDefaultSize() {
			return defaultDimension;
		}

		@Override
		public void setPreferredSize(Dimension aDimension) {
			// durationChooserPanel.setPreferredSize(aDimension);
			super.setPreferredSize(aDimension);
		}

	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? getEditedObject().clone() : getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		setEditedObject(_revertValue);
		closePopup();
		super.cancel();
	}

	@Override
	public void setRevertValue(Duration oldValue) {
		_revertValue = oldValue;
	}

	@Override
	public Duration getRevertValue() {
		return _revertValue;
	}

	@Override
	public void setEditedObject(Duration object) {
		if (object != null && object.getValue() == 0) {
			super.setEditedObject(null);
		}
		else {
			super.setEditedObject(object);
		}

		// GPO: We update the field programmatically but without using invokeLater because in the context of table cell rendering it will
		// cause all cells to display the same value.

		String val = renderedString(getEditedObject());
		if (val == null && getTextField().getText() == null) {
			return;
		}
		if (getTextField().getText() != null && getTextField().getText().equals(val)) {
			return;
		}
		_isProgrammaticalySet = true;
		getTextField().setText(val);
		_isProgrammaticalySet = false;
	}

	// Override if required
	public String localizedForKeyAndButton(String key, JButton component) {
		return key;
	}

	@Override
	protected void openPopup() {
		super.openPopup();
		if (durationChooserPanel != null) {
			durationChooserPanel.controlPanel.applyFocusTraversablePolicyTo(durationChooserPanel, true);
		}
	}

	@Override
	public Class<Duration> getRepresentedType() {
		return Duration.class;
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

}

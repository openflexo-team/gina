/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import java.awt.AlphaComposite;
/**
 * Widget allowing to edit a date with a calendar popup
 * 
 * @author sguerin
 * 
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.icon.ImageIconResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.rm.ResourceLocator;

public class DateSelector extends TextFieldCustomPopup<Date> {

	public static LocalizedDelegate DATE_LOCALIZATION = new LocalizedDelegateImpl(
			ResourceLocator.locateResource("Localization/DateSelector"), null, true, true);

	public static final ImageIconResource DECREASE_YEAR_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/DecreaseYearSelected.png"));
	public static final ImageIconResource INCREASE_YEAR_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/IncreaseYearSelected.png"));
	public static final ImageIconResource DECREASE_YEAR_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/DecreaseYear.png"));
	public static final ImageIconResource INCREASE_YEAR_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/IncreaseYear.png"));
	public static final ImageIconResource DECREASE_MONTH_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/DecreaseMonthSelected.png"));
	public static final ImageIconResource INCREASE_MONTH_SELECTED_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/IncreaseMonthSelected.png"));
	public static final ImageIconResource DECREASE_MONTH_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/DecreaseMonth.png"));
	public static final ImageIconResource INCREASE_MONTH_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/IncreaseMonth.png"));
	public static final ImageIconResource SELECTED_DATE_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/DateSelector/SelectedDate.png"));

	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font DAY_FONT = new Font("SansSerif", Font.PLAIN, 11);

	static final String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	static final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
			"November", "December", };

	private Calendar _cal;

	private SimpleDateFormat formatter;

	Date _revertValue;

	/* -------------- GUI Components --------------------------- */
	CalendarPanel calendarPanel;

	public DateSelector() {
		super(null);
	}

	public DateSelector(Date d) {
		this();
		setEditedObject(d);
		_revertValue = (Date) d.clone();
	}

	public Calendar getCalendar() {
		if (_cal == null) {
			_cal = Calendar.getInstance(Locale.ENGLISH);
		}
		return _cal;
	}

	@Override
	protected ResizablePanel createCustomPanel(Date editedObject) {
		calendarPanel = new CalendarPanel();
		calendarPanel.update();
		return calendarPanel;
	}

	@Override
	public Date getEditedObject() {
		return getDate();
	}

	@Override
	public void setEditedObject(Date object) {
		if (object != null) {
			getCalendar().setTime(object);
		}
		super.setEditedObject(object);
	}

	/**
	 * Returns the Date associated with the picker
	 * 
	 * @return The current selected date.
	 */
	public Date getDate() {
		return getCalendar().getTime();
	}

	/**
	 * Sets the date for the picker. All GUI elements associated with the picker are updated and the grid of date buttons is rebuilt
	 * 
	 * @param d
	 *            the new date for this date picker.
	 */
	public void setDate(Date d) {
		if (d == null) {
			d = new Date();
		}
		setEditedObject(d);
	}

	@Override
	public void updateCustomPanel(Date editedObject) {
		if (calendarPanel != null) {
			calendarPanel.update();
		}
	}

	@Override
	public String renderedString(Date editedObject) {
		if (formatter == null) {
			formatter = new SimpleDateFormat();
		}
		formatter.applyPattern(DATE_LOCALIZATION.localizedForKey("MMMM d, yyyy"));
		return formatter.format(getDate());
	}

	protected class CalendarPanel extends ResizablePanel {
		ButtonsControlPanel controlPanel;

		private JButton prevMonth;
		private JButton nextMonth;
		private JButton prevYear;
		private JButton nextYear;

		JComboBox<String> monthComboBox;

		JSpinner yearSpinner;

		private JPanel dayButtons;

		void update() {
			yearSpinner.setValue(Integer.valueOf(getCalendar().get(Calendar.YEAR)));
			monthComboBox.setSelectedIndex(getCalendar().get(Calendar.MONTH));
			rebuildDayButtons();
		}

		protected CalendarPanel() {
			super();

			// Create the month chooser as a comboBox
			String[] localizedMonths = new String[12];
			for (int i = 0; i < 12; i++) {
				localizedMonths[i] = DATE_LOCALIZATION.localizedForKey(months[i]);
			}
			monthComboBox = new JComboBox<>(localizedMonths);
			monthComboBox.setLightWeightPopupEnabled(false);
			monthComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getCalendar().set(Calendar.MONTH, monthComboBox.getSelectedIndex());
					fireEditedObjectChanged();
				}
			});

			// create the spinner that selects the current year
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			// Range is current year +/- 100 , step size 1
			SpinnerNumberModel yearModel = new SpinnerNumberModel(currentYear, currentYear - 100, currentYear + 100, 1);
			yearSpinner = new JSpinner(yearModel);
			yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
			yearSpinner.setMinimumSize(yearSpinner.getPreferredSize());
			// GUITools.getTextField(yearSpinner).addActionListener(this);
			// //just gets texfield of the spinners jformatted textfield
			yearSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (e.getSource() == yearSpinner) {
						getCalendar().set(Calendar.YEAR, ((Number) yearSpinner.getValue()).intValue());
					}
					fireEditedObjectChanged();
				}
			});
			// Create next/prev buttons
			prevMonth = new JButton(DECREASE_MONTH_SELECTED_ICON);
			prevMonth.setRolloverIcon(DECREASE_MONTH_ICON);
			prevMonth.setBorder(null);
			nextMonth = new JButton(INCREASE_MONTH_SELECTED_ICON);
			nextMonth.setRolloverIcon(INCREASE_MONTH_ICON);
			nextMonth.setBorder(null);
			prevYear = new JButton(DECREASE_YEAR_SELECTED_ICON);
			prevYear.setRolloverIcon(DECREASE_YEAR_ICON);
			prevYear.setBorder(null);
			nextYear = new JButton(INCREASE_YEAR_SELECTED_ICON);
			nextYear.setRolloverIcon(INCREASE_YEAR_ICON);
			nextYear.setBorder(null);

			// setup actionlisteners
			prevMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getCalendar().add(Calendar.MONTH, -1);
					fireEditedObjectChanged();
				}
			});
			nextMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getCalendar().add(Calendar.MONTH, 1);
					fireEditedObjectChanged();
				}
			});
			prevYear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getCalendar().add(Calendar.YEAR, -1);
					fireEditedObjectChanged();
				}
			});
			nextYear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getCalendar().add(Calendar.YEAR, 1);
					fireEditedObjectChanged();
				}
			});

			// Build the calendar Panel
			setLayout(new BorderLayout());

			// Build the month / year selectors
			JPanel topPanel = new JPanel(new BorderLayout());

			JPanel decreasePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
			decreasePanel.add(prevYear);
			decreasePanel.add(prevMonth);
			decreasePanel.setOpaque(true);
			decreasePanel.setBackground(new Color(220, 220, 230));
			JPanel increasePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
			increasePanel.add(nextMonth);
			increasePanel.add(nextYear);
			increasePanel.setOpaque(true);
			increasePanel.setBackground(new Color(220, 220, 230));

			JPanel choosersPanel = new JPanel(new FlowLayout());
			choosersPanel.add(monthComboBox);
			choosersPanel.add(yearSpinner);
			choosersPanel.setOpaque(true);
			choosersPanel.setBackground(new Color(220, 220, 230));

			topPanel.add(decreasePanel, BorderLayout.WEST);
			topPanel.add(choosersPanel, BorderLayout.CENTER);
			topPanel.add(increasePanel, BorderLayout.EAST);
			add(topPanel, BorderLayout.NORTH);

			calendarPanel = this;

			// Day button grid
			dayButtons = new JPanel(new GridBagLayout());
			rebuildDayButtons();
			add(dayButtons, BorderLayout.CENTER);

			controlPanel = new ButtonsControlPanel() {
				@Override
				public String localizedForKeyAndButton(String key, JButton component) {
					return DateSelector.this.localizedForKeyAndButton(key, component);
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

			add(controlPanel, BorderLayout.SOUTH);
		}

		@Override
		public Dimension getDefaultSize() {
			return getSize();
		}

		@Override
		public void setPreferredSize(Dimension preferredSize) {

		}

		/**
		 * Anytime the date buttons need to be changes, this should be called When the month or year changes, or when any dae button is
		 * clicked. Clicked day buttons are highlighted, and so the grid is redrawn
		 */
		private void rebuildDayButtons() {
			// Completely cleanout the panel and rebuild it from scratch.
			dayButtons.removeAll();

			// Build the Date Grid
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = c.weighty = 1;
			c.gridx = c.gridy = 0;
			c.fill = GridBagConstraints.BOTH;

			int dayOfWeek = 0;
			// This is needed because Caldendar does not provide an ordinal day
			// of week field.
			switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) // find
			// out
			// what
			// day
			// of
			// the
			// week
			// today
			// is
			{
				case Calendar.SUNDAY:
					dayOfWeek = 0;
					break;
				case Calendar.MONDAY:
					dayOfWeek = 1;
					break;
				case Calendar.TUESDAY:
					dayOfWeek = 2;
					break;
				case Calendar.WEDNESDAY:
					dayOfWeek = 3;
					break;
				case Calendar.THURSDAY:
					dayOfWeek = 4;
					break;
				case Calendar.FRIDAY:
					dayOfWeek = 5;
					break;
				case Calendar.SATURDAY:
					dayOfWeek = 6;
					break;
			}

			for (int i = 0; i < 7; i++) {
				c.gridx = i;
				JLabel l = new JLabel(DATE_LOCALIZATION.localizedForKey(days[i]), SwingConstants.CENTER);
				l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				// Make the Day of the week bold if it is today
				if (i == dayOfWeek && getCalendar().get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
						&& getCalendar().get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
					l.setFont(l.getFont().deriveFont(Font.BOLD));
				}
				dayButtons.add(l, c);
			}
			Calendar constructionCal = (Calendar) getCalendar().clone();

			for (int i = 1; i <= getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				constructionCal.set(Calendar.DAY_OF_MONTH, i);
				c.gridx = constructionCal.get(Calendar.DAY_OF_WEEK) - 1; // start
				// at
				// gridx
				// = 0
				c.gridy = constructionCal.get(Calendar.WEEK_OF_MONTH); // start
				// at
				// gridy
				// = 1,
				// because
				// of
				// the
				// day
				// labels
				DateButton b = new DateButton(constructionCal);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getCalendar().setTime(((DateButton) e.getSource()).getCalendar().getTime());
						fireEditedObjectChanged();
					}
				});
				dayButtons.add(b, c);
			}

			if (c.gridy < 6) {
				c.gridy = 6;
				dayButtons.add(Box.createRigidArea(new Dimension(20, 20)), c);
			}

			calendarPanel.validate();
			calendarPanel.repaint();
		}

		/**
		 * An extension of JButton that shows a date as a digit indicating the day of the month
		 */
		private class DateButton extends JButton {

			private boolean workingDay = true;
			private boolean focused = false;

			private Calendar cal;

			public DateButton(Calendar cal) {
				super("");
				this.cal = (Calendar) cal.clone();

				setFont(DAY_FONT);
				setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
				setOpaque(true);
				setBackground(Color.white);

				// If this button represents today, set appropriate status.
				// comaparing dates doesnt work since those are only equal if
				// they match upto the millisecond.

				/*System.out.println("date.getDay" + date.getDay());
				if (Integer.toString(cal.get(Calendar.DAY_OF_MONTH)).equals("12")) {
					System.out.println("Hop pour le 12");
					setIcon(SELECTED_DATE_ICON);
					setB
				}*/

				setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

				setPreferredSize(new Dimension(25, 25));
				setMinimumSize(getPreferredSize());
				setMaximumSize(getPreferredSize());

				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						super.mouseEntered(e);
						focused = true;
						repaint();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						super.mouseExited(e);
						focused = false;
						repaint();
					}
				});
			}

			public Calendar getCalendar() {
				return cal;
			}

			private void updateGraphicalProperties() {

				if (cal.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
						&& cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
					setFont(DAY_FONT.deriveFont(Font.BOLD));
					setBackground(new Color(240, 240, 255));
					setForeground(Color.BLACK);
				}
				else {
					setBackground(Color.WHITE);
					if (isSelectedDay()) {
						setFont(DAY_FONT.deriveFont(Font.BOLD));
						setForeground(Color.BLACK);
					}
					else if (isWorkingDay()) {
						setFont(DAY_FONT);
						setForeground(Color.BLACK);
					}
					else {
						setFont(DAY_FONT);
						setForeground(Color.GRAY);
					}
				}
			}

			@Override
			protected void paintComponent(Graphics g) {
				updateGraphicalProperties();
				Graphics2D g2 = (Graphics2D) g;
				if (isSelectedDay() || focused) {
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					float alpha = 0.4f;
					int type = AlphaComposite.SRC_OVER;
					Composite oldComposite = g2.getComposite();
					AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
					g2.setComposite(composite);
					g2.drawImage(SELECTED_DATE_ICON.getImage(), (getSize().width - 25) / 2 + 1, (getSize().height - 25) / 2 - 1, null);
					// alpha = 0.5f;
					// type = AlphaComposite.DST_OVER;
					// composite = AlphaComposite.getInstance(type, alpha);
					g2.setComposite(composite);

					super.paintComponent(g);

					// g2.setComposite(oldComposite);

				}
				else {
					super.paintComponent(g);
				}
				/*g2.setColor(Color.darkGray);
				g2.setStroke(new BasicStroke(0.5f));
				g2.drawLine(0, 0, 0, getSize().height);*/

			}

			public boolean isWorkingDay() {
				if (getCalendar().get(Calendar.DAY_OF_WEEK) == 1 || getCalendar().get(Calendar.DAY_OF_WEEK) == 7) {
					return false;
				}
				return workingDay;
			}

			public boolean isSelectedDay() {
				return DateSelector.this.getCalendar().get(Calendar.DAY_OF_YEAR) == getCalendar().get(Calendar.DAY_OF_YEAR)
						&& DateSelector.this.getCalendar().get(Calendar.YEAR) == getCalendar().get(Calendar.YEAR);
			}

			/*public void setSelectedDay(boolean selected) {
				this.selectedDay = selected;
			}*/

		}

	}

	@Override
	public void apply() {
		System.out.println("On fait apply dans DateSelector avec " + getEditedObject());
		setRevertValue(getEditedObject());
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
	public void setRevertValue(Date oldValue) {
		_revertValue = oldValue;
	}

	public Date getRevertValue() {
		return _revertValue;
	}

	@Override
	protected void openPopup() {
		super.openPopup();
		if (calendarPanel != null) {
			calendarPanel.controlPanel.applyFocusTraversablePolicyTo(calendarPanel, true);
		}
	}

	// Override if required
	public String localizedForKeyAndButton(String key, JButton component) {
		return key;
	}

	public static void main(String[] args) {
		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		System.out.println("language: " + FlexoLocalization.getCurrentLanguage());
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new DateSelector(new Date()), BorderLayout.CENTER);
		// frame.getContentPane().setPreferredSize(new Dimension(200, 20));
		frame.validate();
		frame.pack();
		frame.setVisible(true);
	}

}

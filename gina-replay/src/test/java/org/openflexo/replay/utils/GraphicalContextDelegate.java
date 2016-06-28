/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.replay.utils;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class GraphicalContextDelegate implements ChangeListener {

	private static FIBModelFactory factory;
	private static int startX = 128, startY = 128;

	private JFrame frame;
	private final EventProcessor eventProcessor;
	private JTabbedPane tabbedPane;
	private final boolean dontDestroyMe = false;

	public GraphicalContextDelegate(final String frameTitle) {
		this(frameTitle, new Dimension(560, 80), false);
	}

	public GraphicalContextDelegate(final String frameTitle, final Dimension dim) {
		this(frameTitle, dim, true);
	}

	public GraphicalContextDelegate(final String frameTitle, final Dimension dim, final boolean customDimension) {

		eventProcessor = new EventProcessor();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					/*tabbedPane = new JTabbedPane();
					tabbedPane.addChangeListener(GraphicalContextDelegate.this);*/
					frame = new JFrame(frameTitle);
					frame.setLayout(new BorderLayout());
					frame.setSize(dim);
					frame.setLocationRelativeTo(null);
					frame.setLocation(startX, startY);
					startX += (customDimension ? dim.width + 16 : 64);
					startY += (customDimension ? 16 : 64);
					/*JButton myButton;
					myButton = new JButton("I take the control");
					myButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dontDestroyMe = true;
						}
					});
					frame.getContentPane().add(myButton, BorderLayout.NORTH);*/
					// frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
					frame.setVisible(true);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public void setController(FIBController controller) {
		frame.getContentPane().add((JComponent) controller.getRootView().getTechnologyComponent(), BorderLayout.CENTER);
		frame.revalidate();
	}

	public void addTab(String title, FIBController controller) {
		addTab(title, (JComponent) controller.getRootView().getTechnologyComponent());
	}

	public void addTab(String title, JComponent component) {
		tabbedPane.add(title, component);
		tabbedPane.revalidate();
	}

	public JFrame getFrame() {
		return frame;
	}

	public void waitGUI() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (dontDestroyMe) {
			while (true) {
				try {
					synchronized (GraphicalContextDelegate.class) {
						GraphicalContextDelegate.class.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setUp() {
		eventProcessor.reset();
	}

	public void tearDown() throws Exception {
		if (eventProcessor.getException() != null) {
			throw new InvocationTargetException(eventProcessor.getException());
		}
	}

	@Override
	public final void stateChanged(ChangeEvent e) {
		selectedTab(tabbedPane.getSelectedIndex(), tabbedPane.getSelectedComponent());
	}

	public void selectedTab(int index, Component selectedComponent) {
	}

	public static class EventProcessor extends java.awt.EventQueue {

		private Throwable exception = null;

		public EventProcessor() {
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(this);
		}

		public void reset() {
			exception = null;
		}

		@Override
		protected void dispatchEvent(AWTEvent e) {
			try {
				super.dispatchEvent(e);
			} catch (Throwable exception) {
				this.exception = exception;
			}
		}

		public Throwable getException() {
			return exception;
		}
	}

	public static FIBModelFactory getFactory() {
		if (factory == null) {
			try {
				factory = new FIBModelFactory();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}

		return factory;
	}

}

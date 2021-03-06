/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.test;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.gina.swing.utils.JFIBDialog;
import org.openflexo.rm.Resource;

/**
 * Utility test class used to both interactively and automatically test FIBDialog components
 * 
 * @author sylvain
 * 
 */
public class FIBDialogGraphicalContextDelegate {

	private final EventProcessor eventProcessor;
	private boolean dontDestroyMe = false;

	public FIBDialogGraphicalContextDelegate(final JFIBDialog<?> dialog, final Resource componentResource) {
		eventProcessor = new EventProcessor();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					JButton testButton;
					testButton = new JButton("Test component");
					testButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dontDestroyMe = true;
						}
					});
					JButton editButton;
					editButton = new JButton("Edit component");
					editButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dontDestroyMe = true;
							dialog.dispose();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									TestFIBEditor.instanciateFIBEdition("TestFIBEditor", componentResource, dialog.getData());
								}
							});

						}
					});
					JPanel buttons = new JPanel(new FlowLayout());
					buttons.add(testButton);
					buttons.add(editButton);
					dialog.getContentPane().add(buttons, BorderLayout.NORTH);

				}
			});

			(new Thread(new Runnable() {

				@Override
				public void run() {
					// System.out.println("Juste avant d'ouvir la fenetre");
					waitGUI();
					dialog.dispose();
				}
			})).start();

			// System.out.println("Hop, je l'affiche");

			dialog.pack();
			dialog.setVisible(true);

			// System.out.println("Hop, je viens d'etre disposee, dontDestroyMe=" + dontDestroyMe);

			if (dontDestroyMe) {
				while (true) {
					try {
						synchronized (FIBDialogGraphicalContextDelegate.class) {
							FIBDialogGraphicalContextDelegate.class.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	private void waitGUI() {
		System.out.println("J'attends");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("J'arrete d'attendre");

		if (dontDestroyMe) {
			while (true) {
				try {
					synchronized (FIBDialogGraphicalContextDelegate.class) {
						FIBDialogGraphicalContextDelegate.class.wait();
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

}

/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.swing.layout;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFindPanel;

/**
 * A demo for the {@code JXCollapsiblePane}.
 * 
 * @author Karl George Schaefer
 */
@SuppressWarnings("serial")
public class CollapsiblePaneDemo extends JPanel {
	private JXCollapsiblePane collapsiblePane;
	private CardLayout containerStack;
	private JButton previousButton;
	private JButton collapsingButton;
	private JButton nextButton;

	/**
	 * main method allows us to run as a standalone demo.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("CollapsiblePaneDemo");

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(new CollapsiblePaneDemo());
				frame.setPreferredSize(new Dimension(800, 600));
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	public CollapsiblePaneDemo() {
		createCollapsiblePaneDemo();

		// Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

		bind();
	}

	private void createCollapsiblePaneDemo() {
		setLayout(new BorderLayout());

		collapsiblePane = new JXCollapsiblePane();
		collapsiblePane.setName("collapsiblePane");
		add(collapsiblePane, BorderLayout.NORTH);

		containerStack = new CardLayout();
		collapsiblePane.setLayout(containerStack);
		collapsiblePane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		collapsiblePane.add(new JTree(), "");
		collapsiblePane.add(new JTable(4, 4), "");
		collapsiblePane.add(new JXFindPanel(), "");

		add(new JLabel("Main Content Goes Here", JLabel.CENTER));

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		previousButton = new JButton("previous");
		previousButton.setName("previousButton");
		buttonPanel.add(previousButton);

		collapsingButton = new JButton("collabsing");
		collapsingButton.setName("toggleButton");
		buttonPanel.add(collapsingButton);

		nextButton = new JButton("next");
		nextButton.setName("nextButton");
		buttonPanel.add(nextButton);
	}

	private void bind() {
		collapsingButton.addActionListener(collapsiblePane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				containerStack.next(collapsiblePane.getContentPane());
			}
		});

		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				containerStack.previous(collapsiblePane.getContentPane());
			}
		});
	}
}

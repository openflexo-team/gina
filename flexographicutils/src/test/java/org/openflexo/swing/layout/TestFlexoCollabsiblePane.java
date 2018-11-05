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
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.swing.FlexoCollabsiblePanelGroup;

public class TestFlexoCollabsiblePane {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame f = new JFrame("Test Flexo Collapsible Pane");

			FlexoCollabsiblePanelGroup contentsPane = new FlexoCollabsiblePanelGroup();
			contentsPane.addContents("panel1", makeContents("prout", Color.red));
			contentsPane.addContents("panel2", makeContents("yoplaboum", Color.yellow));
			contentsPane.addContents("panel3", makeContents("zoubi", Color.blue));

			f.add(contentsPane, BorderLayout.CENTER);

			f.setSize(640, 480);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
		});
	}

	private static JPanel makeContents(String s, Color c) {
		JPanel returned = new JPanel(new BorderLayout());
		returned.setBackground(c);
		returned.setPreferredSize(new Dimension(300, 200));
		returned.add(new JLabel(s), BorderLayout.CENTER);
		return returned;
	}
}

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

package org.openflexo.jedit;

import java.awt.Dimension;

import javax.swing.JFrame;

public class JEditDemo extends JFrame {

	private JEditTextArea editorPane;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JEditDemo demo = new JEditDemo();
		demo.init();
		demo.setVisible(true);
	}

	private void init() {
		editorPane = new JEditTextArea();
		editorPane.setEditable(true);
		editorPane.setTokenMarker(new JavaTokenMarker());
		editorPane
				.setText("package org.openflexo.jedit;\nimport java.awt.Dimension;\nimport javax.swing.JFrame;\npublic class JEditDemo extends JFrame{\n	private JEditTextArea editorPane;\n\n	public static void main(String[] args) {\n// TODO Auto-generated method stub\nJEditDemo demo = new JEditDemo();\ndemo.init();\ndemo.setVisible(true);\n}\n\nprivate void init(){\neditorPane = new JEditTextArea();\neditorPane.setEditable(true);\neditorPane.setTokenMarker(new JavaTokenMarker());\ngetContentPane().add(editorPane);\nsetSize(new Dimension(400,400));\n}\n}\n");
		getContentPane().add(editorPane);
		setSize(new Dimension(400, 400));
	}

}

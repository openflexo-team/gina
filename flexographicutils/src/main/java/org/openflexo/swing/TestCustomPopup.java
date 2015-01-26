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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class TestCustomPopup extends TextFieldCustomPopup<Object> {

	private JTextArea _textArea;

	private JButton _button;

	public TestCustomPopup(String editedObject) {
		super(editedObject);
	}

	@Override
	protected ResizablePanel createCustomPanel(Object editedObject) {
		ResizablePanel customPanel;
		customPanel = new ResizablePanel() {
			@Override
			public Dimension getDefaultSize() {
				return getPreferredSize();
			}

			@Override
			public void setPreferredSize(Dimension aDimension) {
			}

		};
		customPanel.setLayout(new BorderLayout());
		customPanel.add(_textArea = new JTextArea((String) editedObject), BorderLayout.CENTER);
		customPanel.add(_button = new JButton("add hop"), BorderLayout.SOUTH);
		_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEditedObject((String) getEditedObject() + "hop");
			}
		});
		return customPanel;
	}

	@Override
	public void updateCustomPanel(Object editedObject) {
		_textArea.setText((String) editedObject);
	}

	@Override
	public String renderedString(Object editedObject) {
		return (String) editedObject;
	}

}

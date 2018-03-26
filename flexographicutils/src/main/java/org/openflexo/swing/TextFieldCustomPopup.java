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
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Abstract widget allowing to edit a complex object with a popup
 * 
 * @author sguerin
 * 
 */
public abstract class TextFieldCustomPopup<T> extends CustomPopup<T> {

	JTextField _textField;
	JLabel _label;
	private int requestedColNumber = -1;

	public TextFieldCustomPopup(T editedObject) {
		this(editedObject, -1);
	}

	public TextFieldCustomPopup(T editedObject, int cols) {
		super(editedObject);
		requestedColNumber = cols;
		if (requestedColNumber > 0) {
			_textField.setColumns(requestedColNumber);
		}
		_textField.setText(renderedString(editedObject));
	}

	/*// Override to use specific border
	@Override
	protected Border getDownButtonBorder()
	{
		return null;
	}*/

	@Override
	protected JComponent buildFrontComponent() {
		_frontComponent = new JPanel(new BorderLayout());
		_frontComponent.setOpaque(false);
		_textField = new JTextField() {
			@Override
			public void setText(String t) {
				startEditTextProgrammatically();
				super.setText(t);
				stopEditTextProgrammatically();
			};
		};
		_textField.setEditable(false);
		_textField.addActionListener(this);
		_textField.setMinimumSize(new Dimension(50, 25));
		_label = new JLabel();
		Insets labelInsets = _textField.getBorder().getBorderInsets(_textField);
		labelInsets.left = 0;
		_label.setBorder(new EmptyBorder(labelInsets));
		_frontComponent.add(_textField, BorderLayout.CENTER);
		_frontComponent.add(_label, BorderLayout.EAST);
		_label.setVisible(false);
		return _frontComponent;
	}

	@Override
	public void fireEditedObjectChanged() {
		super.fireEditedObjectChanged();
		updateTextFieldProgrammaticaly();
	}

	public void updateTextFieldProgrammaticaly() {
		String cur = _textField.getText();
		String val = renderedString(getEditedObject());
		if (cur == null && val == null) {
			return;
		}
		if (cur != null && cur.equals(val)) {
			return;
		}

		SwingUtilities.invokeLater(() -> {
			_isProgrammaticalySet = true;
			_textField.setText(renderedString(getEditedObject()));
			_isProgrammaticalySet = false;
		});
	}

	public abstract String renderedString(T editedObject);

	public boolean _isProgrammaticalySet = false;

	public boolean isProgrammaticalySet() {
		return _isProgrammaticalySet;
	}

	public void setProgrammaticalySet(boolean aFlag) {
		_isProgrammaticalySet = aFlag;
	}

	@Override
	public abstract void updateCustomPanel(T editedObject);

	public JTextField getTextField() {
		return _textField;
	}

	public JLabel getLabel() {
		return _label;
	}

	@Override
	public JPanel getFrontComponent() {
		return (JPanel) super.getFrontComponent();
	}

	@Override
	public void setFont(Font aFont) {
		super.setFont(aFont);
		if (_textField != null) {
			_textField.setFont(aFont);
		}
		if (_label != null) {
			_label.setFont(aFont);
		}
	}

	private boolean textIsBeeingProgrammaticallyEditing = false;

	protected void startEditTextProgrammatically() {
		textIsBeeingProgrammaticallyEditing = true;
	}

	protected void stopEditTextProgrammatically() {
		textIsBeeingProgrammaticallyEditing = false;
	}

	protected boolean textIsBeeingProgrammaticallyEditing() {
		return textIsBeeingProgrammaticallyEditing;
	}

}

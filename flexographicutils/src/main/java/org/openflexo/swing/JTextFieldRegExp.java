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

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class JTextFieldRegExp extends JTextField implements DocumentListener {

	private int errorLevel;
	public static final int ERROR_FREE = 0;
	public static final int WARNING = 1;
	public static final int REG_EXP_ERROR = 2;

	public static final Color DEFAULT_BORDER = Color.WHITE;
	public static final Color WARNING_BORDER = new Color(255, 204, 0);
	public static final Color ERROR_BORDER = new Color(255, 99, 99);

	// private JTextField _textField;
	private Vector<Pattern> warningRegExp;
	private Vector<String> warningMessages;
	private Vector<Pattern> errorRegExp;
	private Vector<String> errorMessages;

	private void init() {
		warningRegExp = new Vector<Pattern>();
		warningMessages = new Vector<String>();
		errorRegExp = new Vector<Pattern>();
		errorMessages = new Vector<String>();
		errorLevel = ERROR_FREE;
		getDocument().addDocumentListener(this);
	}

	public JTextFieldRegExp() {
		super();
		init();
	}

	public JTextFieldRegExp(Document arg0, String arg1, int arg2) {
		super(arg0, arg1, arg2);
		init();
	}

	public JTextFieldRegExp(int arg0) {
		super(arg0);
		init();
	}

	public JTextFieldRegExp(String arg0, int arg1) {
		super(arg0, arg1);
		init();
	}

	public JTextFieldRegExp(String arg0) {
		super(arg0);
		init();
	}

	public void addToWarnings(String regExp, String message) {
		warningRegExp.add(Pattern.compile(regExp));
		warningMessages.add(message);
	}

	public void addToErrors(String regExp, String message) {
		errorRegExp.add(Pattern.compile(regExp));
		errorMessages.add(message);
	}

	protected void doTheCheck() {
		String m = validateText();
		if (m == null) {
			setBackground(DEFAULT_BORDER);
			setToolTipText(null);
			return;
		}
		setToolTipText(m);
		setBackground(errorLevel == REG_EXP_ERROR ? ERROR_BORDER : WARNING_BORDER);
	}

	private String validateText() {
		errorLevel = ERROR_FREE;
		String err = validateError();
		if (err == null) {
			err = validateWarning();
			if (err != null) {
				errorLevel = WARNING;
			}
			return err;
		} else {
			errorLevel = REG_EXP_ERROR;
		}
		return err;
	}

	private String validateWarning() {
		return validate(warningRegExp, warningMessages);
	}

	private String validateError() {
		return validate(errorRegExp, errorMessages);
	}

	public boolean hasError() {
		return validateError() != null;
	}

	private String validate(Vector<Pattern> patterns, Vector<String> messages) {
		Enumeration<Pattern> en = patterns.elements();
		Pattern p = null;
		int i = 0;
		while (en.hasMoreElements()) {
			p = en.nextElement();
			if (!p.matcher(getText()).matches()) {
				return messages.get(i).toString();
			}
			i++;
		}
		return null;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		doTheCheck();

	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		doTheCheck();

	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		doTheCheck();

	}

}

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
import java.io.IOException;
import java.io.OutputStream;

public class JConsoleOutputStream extends OutputStream {

	private JConsole _console;
	private StringBuffer _buffer;
	private Color _color;

	public JConsoleOutputStream(JConsole console, Color color) {
		super();
		if (console == null) {
			throw new IllegalArgumentException("console cannot be null.");
		}
		_console = console;
		_buffer = new StringBuffer();
		if (color == null) {
			_color = Color.BLACK;
		} else {
			_color = color;
		}
	}

	private void resetBuffer() {
		_buffer = new StringBuffer();
	}

	@Override
	public void write(int b) throws IOException {
		if (b == Byte.valueOf(Character.LINE_SEPARATOR) || (char) b == '\n') {
			String out = _buffer.toString().trim();
			if (out.length() > 0) {
				_console.log(out + "\n", _color);
			}
			resetBuffer();
		} else {
			_buffer.append((char) b);
		}

	}

	@Override
	public void flush() throws IOException {
		_console.log(_buffer.toString(), _color);
		resetBuffer();
	}

}

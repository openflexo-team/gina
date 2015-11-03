/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.fib.swing.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.swing.view.SwingTextRenderingTechnologyAdapter;
import org.openflexo.fib.view.widget.impl.FIBTextAreaWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object
 * 
 * @author bmangez,sguerin
 */
public class JFIBTextAreaWidget extends FIBTextAreaWidgetImpl<JTextArea>implements FocusListener {

	private static final Logger LOGGER = Logger.getLogger(JFIBTextAreaWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingTechnologyAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingTechnologyAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingTextAreaRenderingTechnologyAdapter extends SwingTextRenderingTechnologyAdapter<JTextArea>
			implements TextAreaRenderingTechnologyAdapter<JTextArea> {

		@Override
		public int getColumns(JTextArea component) {
			return component.getColumns();
		}

		@Override
		public void setColumns(JTextArea component, int columns) {
			component.setColumns(columns);
		}

		@Override
		public int getRows(JTextArea component) {
			return component.getRows();
		}

		@Override
		public void setRows(JTextArea component, int rows) {
			component.setRows(rows);
		}

	}

	public static SwingTextAreaRenderingTechnologyAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingTextAreaRenderingTechnologyAdapter();

	private final JPanel panel;
	private JScrollPane scrollPane;

	public JFIBTextAreaWidget(FIBTextArea model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(getTechnologyComponent(), BorderLayout.CENTER);
		Border border;
		if (!ToolBox.isMacOSLaf()) {
			border = BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER);
		}
		else {
			border = BorderFactory.createEmptyBorder(2, 3, 2, 3);
		}
		border = BorderFactory.createCompoundBorder(border, BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		panel.setBorder(border);
	}

	@Override
	protected JTextArea makeTechnologyComponent() {
		JTextArea textArea = new JTextArea();

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!validateOnReturn && !widgetUpdating) {
					updateModelFromWidget();
				}
			}
		});
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updateModelFromWidget();
				}
			}
		});
		textArea.addFocusListener(this);

		textArea.setAutoscrolls(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEnabled(true);

		return textArea;
	}

	@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		getTechnologyComponent().selectAll();
	}

	@Override
	public JPanel getJComponent() {
		return panel;
	}

}

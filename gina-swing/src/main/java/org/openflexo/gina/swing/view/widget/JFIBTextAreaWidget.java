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

package org.openflexo.gina.swing.view.widget;

import java.awt.Color;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingTextRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBTextAreaWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object
 * 
 * @author bmangez,sguerin
 */
public class JFIBTextAreaWidget extends FIBTextAreaWidgetImpl<JTextArea>implements FocusListener, JFIBView<FIBTextArea, JTextArea> {

	private static final Logger LOGGER = Logger.getLogger(JFIBTextAreaWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingTextAreaRenderingAdapter extends SwingTextRenderingAdapter<JTextArea>
			implements TextAreaRenderingAdapter<JTextArea> {

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

	public static SwingTextAreaRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingTextAreaRenderingAdapter();

	public JFIBTextAreaWidget(FIBTextArea model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	public SwingTextAreaRenderingAdapter getRenderingAdapter() {
		return (SwingTextAreaRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	@Override
	protected JTextArea makeTechnologyComponent() {
		JTextArea textArea = new JTextArea("", DEFAULT_ROWS, DEFAULT_COLUMNS);

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
					// updateModelFromWidget();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
					// updateModelFromWidget();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
					// updateModelFromWidget();
				}
			}
		});
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					textChanged();
					// updateModelFromWidget();
				}
			}
		});
		textArea.addFocusListener(this);

		textArea.setAutoscrolls(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEnabled(true);

		Border border;
		if (!ToolBox.isMacOSLaf()) {
			border = BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER);
		}
		else {
			border = BorderFactory.createEmptyBorder(2, 3, 2, 3);
		}
		border = BorderFactory.createCompoundBorder(border, BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		textArea.setBorder(border);

		return textArea;
	}

	/*@Override
	public void focusGained(FocusEvent event) {
		super.focusGained(event);
		getTechnologyComponent().selectAll();
	}*/

	@Override
	protected void componentGainsFocus() {
		super.componentGainsFocus();
		getTechnologyComponent().selectAll();
	}

}

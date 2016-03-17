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
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBEditorWidgetImpl;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object using a syntax-colored editor (eg code editor)
 * 
 * @author bmangez,sguerin
 */
public class JFIBEditorWidget extends FIBEditorWidgetImpl<JEditTextArea>implements FocusListener, JFIBView<FIBEditor, JEditTextArea> {

	private static final Logger LOGGER = Logger.getLogger(JFIBEditorWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingEditorWidgetRenderingAdapter extends SwingRenderingAdapter<JEditTextArea>
			implements EditorWidgetRenderingAdapter<JEditTextArea> {

		@Override
		public int getColumns(JEditTextArea component) {
			return component.getColumns();
		}

		@Override
		public void setColumns(JEditTextArea component, int columns) {
			component.setColumns(columns);
		}

		@Override
		public int getRows(JEditTextArea component) {
			return component.getRows();
		}

		@Override
		public void setRows(JEditTextArea component, int rows) {
			component.setRows(rows);
		}

		@Override
		public String getText(JEditTextArea component) {
			return component.getText();
		}

		@Override
		public void setText(JEditTextArea component, String aText) {
			component.setText(aText);
		}

		@Override
		public boolean isEditable(JEditTextArea component) {
			return component.isEditable();
		}

		@Override
		public void setEditable(JEditTextArea component, boolean editable) {
			component.setEditable(editable);
		}

		@Override
		public int getCaretPosition(JEditTextArea component) {
			return component.getCaretPosition();
		}

		@Override
		public void setCaretPosition(JEditTextArea component, int caretPosition) {
			component.setCaretPosition(caretPosition);
		}

		@Override
		public TokenMarker getTokenMarker(JEditTextArea component) {
			return component.getTokenMarker();
		}

		@Override
		public void setTokenMarker(JEditTextArea component, TokenMarker tokenMarker) {
			component.setTokenMarker(tokenMarker);
		}

	}

	public static SwingEditorWidgetRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingEditorWidgetRenderingAdapter();

	private static final int DEFAULT_COLUMNS = 30;
	private static final int DEFAULT_ROWS = 5;

	public JFIBEditorWidget(FIBEditor model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	public SwingEditorWidgetRenderingAdapter getRenderingAdapter() {
		return (SwingEditorWidgetRenderingAdapter) super.getRenderingAdapter();
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
	protected JEditTextArea makeTechnologyComponent() {
		JEditTextArea textArea = new JEditTextArea();
		if (getWidget().getColumns() != null && getWidget().getColumns() > 0) {
			textArea.setColumns(getWidget().getColumns());
		}
		else {
			textArea.setColumns(DEFAULT_COLUMNS);
		}
		if (getWidget().getRows() != null && getWidget().getRows() > 0) {
			textArea.setRows(getWidget().getRows());
		}
		else {
			textArea.setRows(DEFAULT_ROWS);
		}
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

		textArea.setEditable(!isReadOnly());
		if (getWidget().getText() != null) {
			textArea.setText(getWidget().getText());
		}

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!validateOnReturn && !isUpdating()) {
					textChanged();
				}
			}
		});
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					textChanged();
				}
			}
		});
		textArea.addFocusListener(this);

		textArea.setAutoscrolls(true);
		textArea.setEnabled(true);

		textArea.setCaretPosition(0);

		return textArea;
	}

}

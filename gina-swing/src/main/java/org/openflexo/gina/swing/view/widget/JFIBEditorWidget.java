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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBEditor;
import org.openflexo.gina.model.widget.FIBEditor.SyntaxStyle;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBEditorWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a Text (more than one line) object using a syntax-colored editor (eg code editor)
 * 
 * @author bmangez,sguerin
 */
public class JFIBEditorWidget extends FIBEditorWidgetImpl<RTextScrollPane> implements FocusListener, JFIBView<FIBEditor, RTextScrollPane> {

	private static final Logger LOGGER = Logger.getLogger(JFIBEditorWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingEditorWidgetRenderingAdapter extends SwingRenderingAdapter<RTextScrollPane>
			implements EditorWidgetRenderingAdapter<RTextScrollPane> {

		@Override
		public int getColumns(RTextScrollPane component) {
			return component.getTextArea().getColumns();
		}

		@Override
		public void setColumns(RTextScrollPane component, int columns) {
			component.getTextArea().setColumns(columns);
		}

		@Override
		public int getRows(RTextScrollPane component) {
			return component.getTextArea().getRows();
		}

		@Override
		public void setRows(RTextScrollPane component, int rows) {
			component.getTextArea().setRows(rows);
		}

		@Override
		public String getText(RTextScrollPane component) {
			return component.getTextArea().getText();
		}

		@Override
		public void setText(RTextScrollPane component, String aText) {
			component.getTextArea().setText(aText);
		}

		@Override
		public boolean isEditable(RTextScrollPane component) {
			return component.getTextArea().isEditable();
		}

		@Override
		public void setEditable(RTextScrollPane component, boolean editable) {
			component.getTextArea().setEditable(editable);
		}

		@Override
		public int getCaretPosition(RTextScrollPane component) {
			return component.getTextArea().getCaretPosition();
		}

		@Override
		public void setCaretPosition(RTextScrollPane component, int caretPosition) {
			component.getTextArea().setCaretPosition(caretPosition);
		}

		@Override
		public SyntaxStyle getSyntaxStyle(RTextScrollPane component) {
			return getSyntaxStyle(((RSyntaxTextArea) component.getTextArea()).getSyntaxEditingStyle());
		}

		@Override
		public void setSyntaxStyle(RTextScrollPane component, SyntaxStyle syntaxStyle) {
			((RSyntaxTextArea) component.getTextArea()).setSyntaxEditingStyle(getSyntaxEditingStyle(syntaxStyle));
		}

		@Override
		public Color getDefaultForegroundColor(RTextScrollPane component) {
			return component.getTextArea().getForeground();
			// return UIManager.getColor("Panel.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(RTextScrollPane component) {
			return component.getTextArea().getBackground();
			// return UIManager.getColor("Panel.background");
		}

		private String getSyntaxEditingStyle(SyntaxStyle syntaxStyle) {
			if (syntaxStyle == null) {
				return null;
			}
			switch (syntaxStyle) {
				case Java:
					return SyntaxConstants.SYNTAX_STYLE_JAVA;
				case XML:
					return SyntaxConstants.SYNTAX_STYLE_XML;
				case HTML:
					return SyntaxConstants.SYNTAX_STYLE_HTML;
				case JavaScript:
					return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
				// TODO: others
				default:
					return null;
			}
		}

		private SyntaxStyle getSyntaxStyle(String syntaxEditingStyle) {
			if (syntaxEditingStyle.equals(SyntaxConstants.SYNTAX_STYLE_JAVA)) {
				return SyntaxStyle.Java;
			}
			else if (syntaxEditingStyle.equals(SyntaxConstants.SYNTAX_STYLE_XML)) {
				return SyntaxStyle.XML;
			}
			else if (syntaxEditingStyle.equals(SyntaxConstants.SYNTAX_STYLE_HTML)) {
				return SyntaxStyle.HTML;
			}
			else if (syntaxEditingStyle.equals(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT)) {
				return SyntaxStyle.JavaScript;
			}
			// TODO: others
			return null;
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
	protected RTextScrollPane makeTechnologyComponent() {
		RSyntaxTextArea textArea = new RSyntaxTextArea();
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

		// Always enable code folding
		textArea.setCodeFoldingEnabled(true);

		return new RTextScrollPane(textArea);
	}

}

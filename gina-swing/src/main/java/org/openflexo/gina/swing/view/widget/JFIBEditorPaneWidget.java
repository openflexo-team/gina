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
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBEditorPane;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingTextRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBEditorPaneWidgetImpl;
import org.openflexo.toolbox.ToolBox;

/**
 * Swing implementation for a text component to edit various kinds of content (a JEditorPane)
 * 
 * @author bmangez,sguerin
 */
public class JFIBEditorPaneWidget extends FIBEditorPaneWidgetImpl<JEditorPane>
		implements FocusListener, JFIBView<FIBEditorPane, JEditorPane> {

	private static final Logger logger = Logger.getLogger(JFIBEditorPaneWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JTextField<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingEditorPaneRenderingAdapter extends SwingTextRenderingAdapter<JEditorPane>
			implements EditorPaneRenderingAdapter<JEditorPane> {

		@Override
		public Color getDefaultForegroundColor(JEditorPane component) {
			return UIManager.getColor("TextArea.foreground");
		}

		@Override
		public Color getDefaultBackgroundColor(JEditorPane component) {
			return UIManager.getColor("TextArea.background");
		}

	}

	public static SwingEditorPaneRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingEditorPaneRenderingAdapter();

	/*private final JPanel panel;
	private final JEditorPane editorPane;
	private JScrollPane scrollPane;
	boolean validateOnReturn;*/

	public JFIBEditorPaneWidget(FIBEditorPane model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	final protected void updateContentType() {
		if (getComponent().getContentType() != null) {
			getTechnologyComponent().setContentType(getComponent().getContentType().getContentType());
		}
		else {
			getTechnologyComponent().setContentType("text/html");
		}
	}

	/*@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), editorPane.getText())) {
			if (modelUpdating) {
				return false;
			}
			if (getValue() != null && (getValue() + "\n").equals(editorPane.getText())) {
				return false;
			}
			widgetUpdating = true;
			try {
				editorPane.setText(getValue());
				updateFont();
				editorPane.setCaretPosition(0);
			} finally {
				widgetUpdating = false;
			}
			return true;
		}
		return false;
	}*/

	/**
	 * Update the model given the actual state of the widget
	 */
	/*@Override
	public synchronized boolean updateModelFromWidget() {
		if (notEquals(getValue(), editorPane.getText())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget() in TextAreaWidget");
			}
			modelUpdating = true;
			try {
				setValue(editorPane.getText());
			} finally {
				modelUpdating = false;
			}
			return true;
		}
		return false;
	}*/

	/*@Override
	public JPanel getJComponent() {
		return panel;
	}
	
	@Override
	public JEditorPane getDynamicJComponent() {
		return editorPane;
	}*/

	/**
	 * Return the effective component to be added to swing hierarchy This component may be the same as the one returned by
	 * {@link #getJComponent()} or a encapsulation in a JScrollPane
	 * 
	 * @return JComponent
	 */
	/*@Override
	public JComponent getResultingJComponent() {
		if (getComponent().getUseScrollBar()) {
			if (scrollPane == null) {
				scrollPane = new JScrollPane(editorPane, getComponent().getVerticalScrollbarPolicy().getPolicy(),
						getComponent().getHorizontalScrollbarPolicy().getPolicy());
				scrollPane.setOpaque(false);
				scrollPane.getViewport().setOpaque(false);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
				scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
			}
			panel.add(scrollPane);
			return panel;
		}
		else {
			return getJComponent();
		}
	}*/

	@Override
	final public void updateFont() {
		super.updateFont();
		if (getFont() != null) {
			if (getTechnologyComponent().getDocument() instanceof StyledDocument) {
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setFontFamily(sas, getFont().getFamily());
				StyleConstants.setFontSize(sas, getFont().getSize());
				((StyledDocument) getTechnologyComponent().getDocument()).setParagraphAttributes(0,
						getTechnologyComponent().getDocument().getLength(), sas, false);
			}
		}
	}

	@Override
	public SwingEditorPaneRenderingAdapter getRenderingAdapter() {
		return (SwingEditorPaneRenderingAdapter) super.getRenderingAdapter();
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
	protected JEditorPane makeTechnologyComponent() {
		JEditorPane editorPane = new JEditorPane();
		// panel = new JPanel(new BorderLayout());
		// panel.setOpaque(false);
		// panel.add(editorPane, BorderLayout.CENTER);
		validateOnReturn = getComponent().isValidateOnReturn();
		// Unused Border border;
		if (!ToolBox.isMacOSLaf()) {
			// border =
			BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER, BOTTOM_COMPENSATING_BORDER,
					RIGHT_COMPENSATING_BORDER);
		}
		else {
			// border =
			BorderFactory.createEmptyBorder(2, 3, 2, 3);
		}
		// panel.setBorder(border);
		editorPane.setEditable(!isReadOnly());
		if (getComponent().getText() != null) {
			editorPane.setText(getComponent().getText());
		}

		editorPane.getDocument().addDocumentListener(new DocumentListener() {
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
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					textChanged();
					// updateModelFromWidget();
				}
			}
		});

		// updateContentType();
		editorPane.addFocusListener(this);

		editorPane.setAutoscrolls(true);
		editorPane.setEnabled(true);

		// updateFont();

		editorPane.setCaretPosition(0);

		return editorPane;
	}

	@Override
	protected void componentGainsFocus() {
		super.componentGainsFocus();
		getTechnologyComponent().selectAll();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateContentType();
	}

}

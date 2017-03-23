/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.view.widget;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableViewDelegate;
import org.openflexo.gina.swing.view.widget.JFIBLabelWidget;
import org.openflexo.gina.swing.view.widget.JFIBLabelWidget.JLabelPanel;
import org.openflexo.logging.FlexoLogger;

public class JFIBEditableLabelWidget extends JFIBLabelWidget implements FIBSwingEditableView<FIBLabel, JLabelPanel> {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(JFIBEditableLabelWidget.class.getPackage().getName());

	private final FIBSwingEditableViewDelegate<FIBLabel, JLabelPanel> delegate;

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel in edition mode<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class EditableSwingLabelRenderingAdapter extends SwingLabelRenderingAdapter
			implements LabelRenderingAdapter<JLabelPanel> {

		@Override
		public String getText(JLabelPanel component) {
			return component.getLabel().getText();
		}

		@Override
		public void setText(JLabelPanel component, String aText) {
			System.out.println("??????? quelqu'un fait un set avec " + aText);
			super.setText(component, aText);
			((EditableJLabelPanel) component).getTextField().setText(aText);
		}

	}

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditableLabelWidget(FIBLabel model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;

		delegate = new FIBSwingEditableViewDelegate<>(this);
	}

	@Override
	public JComponent getDraggableComponent() {
		return getTechnologyComponent().getLabel();
	}

	@Override
	public void delete() {
		delegate.delete();
		super.delete();
	}

	@Override
	public FIBSwingEditableViewDelegate<FIBLabel, JLabelPanel> getDelegate() {
		return delegate;
	}

	@Override
	protected EditableJLabelPanel makeTechnologyComponent() {
		return new EditableJLabelPanel(this);
	}

	@Override
	protected void updateLabel() {
		if (((EditableJLabelPanel) getTechnologyComponent()).isEditing()) {
			return;
		}
		super.updateLabel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("Received event " + evt);
		/*if (evt.getPropertyName().equals(FIBLabel.LABEL_KEY)) {
			updateLabel();
			relayoutParentBecauseLabelChanged();
		}*/
		if (((EditableJLabelPanel) getTechnologyComponent()).isEditing()) {
			return;
		}
		super.propertyChange(evt);
	}

	@SuppressWarnings("serial")
	public static class EditableJLabelPanel extends JLabelPanel {

		private boolean isEditing = false;
		private JTextField textField;

		public EditableJLabelPanel(JFIBEditableLabelWidget widget) {
			super(widget);

			textField = new JTextField();
			textField.setBorder(null);
			textField.setBackground(null);
			textField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					labelChanged();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					labelChanged();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					labelChanged();
				}
			});
			textField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						endEdition();
					}
				}
			});
			textField.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					labelChanged();
					endEdition();
				}

				@Override
				public void focusGained(FocusEvent e) {
				}
			});

			getLabel().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if (e.getClickCount() == 2 && !isEditing) {
						startEdition();
					}
				}
			});
		}

		public boolean isEditing() {
			return isEditing;
		}

		public JTextField getTextField() {
			return textField;
		}

		protected void startEdition() {
			// System.out.println("Start edition for" + getWidget().getComponent().getLabel());
			remove(getLabel());
			textField.setText(getWidget().getComponent().getLabel());
			textField.setFont(getLabel().getFont());
			add(textField, BorderLayout.CENTER);
			// textField.selectAll();
			getWidget().getRenderingAdapter().revalidateAndRepaint(this);
			isEditing = true;
		}

		protected void endEdition() {
			// System.out.println("End edition du truc for " + getWidget().getComponent().getLabel());
			remove(textField);
			getLabel().setText(getWidget().getComponent().getLabel());
			getLabel().setFont(textField.getFont());
			add(getLabel(), BorderLayout.CENTER);
			getWidget().getRenderingAdapter().revalidateAndRepaint(this);
			isEditing = false;
		}

		protected void labelChanged() {
			// System.out.println("labelChanged() called");
			getWidget().getComponent().setLabel(textField.getText());
			getParent().revalidate();
			getParent().repaint();
		}

	}

}

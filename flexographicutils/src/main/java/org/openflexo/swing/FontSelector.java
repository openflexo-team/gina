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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.localization.FlexoLocalization;

/**
 * Widget allowing to view and edit a Font
 * 
 * @author sguerin
 * 
 */
public class FontSelector extends CustomPopup<Font> implements FontSelectionModel {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FontSelector.class.getPackage().getName());

	private Font _revertValue;

	protected FontDetailsPanel _selectorPanel;

	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	public FontSelector() {
		super(DEFAULT_FONT);
		setFocusable(true);
		getFrontComponent().update();
	}

	@Override
	public void setRevertValue(Font oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = new Font(oldValue.getFontName(), oldValue.getStyle(), oldValue.getSize());
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	public Font getRevertValue() {
		return _revertValue;
	}

	@Override
	public Font getEditedObject() {
		Font editedObject = super.getEditedObject();
		if (editedObject == null) {
			return DEFAULT_FONT;
		}
		return editedObject;
	}

	@Override
	public void setEditedObject(Font font) {
		super.setEditedObject(font);
		for (ChangeListener l : listenerList.getListeners(ChangeListener.class)) {
			l.stateChanged(new ChangeEvent(this));
		}
	}

	@Override
	public Font getSelectedFont() {
		if (getEditedObject() != null) {
			return getEditedObject();
		} else {
			return DEFAULT_FONT;
		}
	}

	@Override
	public void setSelectedFont(Font font) {
		setEditedObject(font);
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		listenerList.add(ChangeListener.class, listener);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		listenerList.remove(ChangeListener.class, listener);
	}

	@Override
	protected FontDetailsPanel createCustomPanel(Font editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected FontDetailsPanel makeCustomPanel(Font editedObject) {
		return new FontDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Font editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public FontSelector getJComponent() {
		return this;
	}

	public class FontDetailsPanel extends ResizablePanel {
		private JFontChooser fontChooser;
		private JButton _applyButton;
		private JButton _cancelButton;
		private JPanel _controlPanel;

		protected FontDetailsPanel(Font editedFont) {
			super();

			fontChooser = new JFontChooser(FontSelector.this);

			setLayout(new BorderLayout());
			add(fontChooser, BorderLayout.CENTER);

			_controlPanel = new JPanel();
			_controlPanel.setLayout(new FlowLayout());
			_controlPanel.add(_applyButton = new JButton(FlexoLocalization.localizedForKey("ok")));
			_controlPanel.add(_cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel")));
			_applyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					apply();
				}
			});
			_cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
			add(_controlPanel, BorderLayout.SOUTH);
		}

		public void update() {
		}

		@Override
		public Dimension getDefaultSize() {
			return getPreferredSize();
		}

		public void delete() {
		}
	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	public FontDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected FontPreviewPanel buildFrontComponent() {
		return new FontPreviewPanel();
	}

	@Override
	public FontPreviewPanel getFrontComponent() {
		return (FontPreviewPanel) super.getFrontComponent();
	}

	/*@Override
	protected Border getDownButtonBorder()
	{
		return BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(1,1,1,1),
				BorderFactory.createRaisedBevelBorder());
	}*/

	protected class FontPreviewPanel extends JPanel {
		private JLabel previewLabel;

		protected FontPreviewPanel() {
			super(new BorderLayout());
			// setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.LIGHT_GRAY));
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
			previewLabel = new JLabel();
			add(previewLabel, BorderLayout.CENTER);
			update();
			// setPreferredSize(new Dimension(40,19));
		}

		protected void update() {
			// logger.info("Update front panel with "+JFontChooser.fontDescription(getEditedObject())+" font="+getEditedObject());
			previewLabel.setFont(getEditedObject());
			if (FontSelector.super.getEditedObject() != null) {
				previewLabel.setText(JFontChooser.fontDescription(getEditedObject()));
			} else {
				previewLabel.setText("");
			}
		}
	}

}

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

/**
 * Widget allowing to edit a file with a popup
 * 
 * @author sguerin
 * 
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileSelector extends TextFieldCustomPopup<File> {
	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private static final String EMPTY_STRING = "";

	private File _oldValue;

	FileChooserPanel fileChooserPanel;

	private FileFilter _fileFilter;

	int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;

	int dialogType = JFileChooser.OPEN_DIALOG;

	public FileSelector() {
		this((File) null);
	}

	public FileSelector(FileFilter aFileFilter) {
		this(null, aFileFilter);
	}

	public FileSelector(File aFile) {
		super(aFile);
		_oldValue = aFile;
	}

	public FileSelector(File aFile, FileFilter aFileFilter) {
		this(aFile);
		setFileFilter(aFileFilter);
	}

	public FileSelector(File aFile, FileFilter aFileFilter, int aFileSelectionMode) {
		this(aFile);
		setFileFilter(aFileFilter);
		fileSelectionMode = aFileSelectionMode;
	}

	public FileSelector(File aFile, FileFilter aFileFilter, int aFileSelectionMode, int aDialogType) {
		this(aFile);
		setFileFilter(aFileFilter);
		fileSelectionMode = aFileSelectionMode;
		dialogType = aDialogType;
	}

	@Override
	public void delete() {
		super.delete();
		_fileFilter = null;
		fileChooserPanel = null;

	}

	public File getEditedFile() {
		return getEditedObject();
	}

	public void setEditedFile(File aFile) {
		setEditedObject(aFile);
	}

	public File getFile(Object currentEditedObject) {
		return (File) currentEditedObject;
	}

	@Override
	protected ResizablePanel createCustomPanel(File editedObject) {
		fileChooserPanel = new FileChooserPanel(getFile(editedObject));
		if (_fileFilter != null) {
			fileChooserPanel.setFileFilter(_fileFilter);
		}
		return fileChooserPanel;
	}

	@Override
	public void updateCustomPanel(File editedObject) {
		if (fileChooserPanel != null) {
			fileChooserPanel.update(getFile(editedObject));
		}
	}

	@Override
	public String renderedString(File editedObject) {
		if (editedObject != null) {
			return getFile(editedObject).getName();
		} else {
			return EMPTY_STRING;
		}
	}

	public void setFileFilter(FileFilter aFilter) {
		_fileFilter = aFilter;
		if (fileChooserPanel != null) {
			fileChooserPanel.setFileFilter(aFilter);
		}
	}

	protected class FileChooserPanel extends ResizablePanel implements ActionListener {
		private JFileChooser fileChooser;

		private Dimension defaultDimension = new Dimension(500, 400);

		void update(File aFile) {
			fileChooser.setSelectedFile(aFile);
		}

		protected FileChooserPanel(File aFile) {
			super();
			fileChooser = FlexoFileChooser.getFileChooser(aFile != null ? aFile.getAbsolutePath() : null);
			fileChooser.addActionListener(this);
			fileChooser.setFileSelectionMode(fileSelectionMode);
			fileChooser.setDialogType(dialogType);

			// Build the calendar Panel
			setLayout(new BorderLayout());
			add(fileChooser, BorderLayout.CENTER);

			fileChooserPanel = this;

			fileChooser.setSize(defaultDimension);
			fileChooser.setMinimumSize(defaultDimension);
			fileChooser.setMaximumSize(defaultDimension);
		}

		public void setFileFilter(FileFilter aFilter) {
			if (aFilter != null) {
				fileChooser.setFileFilter(aFilter);
			}
		}

		@Override
		public Dimension getDefaultSize() {
			return defaultDimension;
		}

		@Override
		public void setPreferredSize(Dimension aDimension) {
			fileChooser.setPreferredSize(aDimension);
		}

		/**
		 * Overrides
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				setEditedFile(fileChooser.getSelectedFile());
				apply();
			} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
				cancel();
			}
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
		setEditedObject(_oldValue);
		closePopup();
		super.cancel();
	}

	@Override
	public void setRevertValue(File oldValue) {
		_oldValue = oldValue;
	}

	public File getRevertValue() {
		return _oldValue;
	}

}

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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.openflexo.toolbox.ToolBox;

/**
 * A graphical component which is used to choose a file (open or save).<br>
 * This component provides an abstraction above both JFileChooser and FileDialog, allowing the user to choose one implementation
 * 
 * 
 * @author sylvain
 * 
 */
public class FlexoFileChooser {

	private enum ImplementationType {
		JFileChooserImplementation, FileDialogImplementation
	}

	static ImplementationType getImplementationType() {
		return ToolBox.isMacOS() ? ImplementationType.FileDialogImplementation : ImplementationType.JFileChooserImplementation;
	}

	/**
	 *
	 */
	public static JFileChooser getFileChooser(String location) {
		return new JFileChooser(location) {
			@Override
			public Icon getIcon(File f) {
				Icon returned = ToolBox.getIconFileChooserWithFix(f, getFileView());
				if (returned == null) {
					returned = super.getIcon(f);
				}
				return returned;
			}
		};
	}

	private FileDialog _fileDialog;
	private JFileChooser _fileChooser;
	private final Window _owner;

	public FlexoFileChooser(Window owner) {
		super();
		_owner = owner;
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			buildAsJFileChooser();
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			buildAsFileDialog();
		}
	}

	public void setCurrentDirectory(File dir) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setCurrentDirectory(dir);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			try {
				_fileDialog.setDirectory(dir != null ? dir.getCanonicalPath() : System.getProperty("user.home"));
			} catch (IOException e) {
				_fileDialog.setDirectory(System.getProperty("user.home"));
			}
		}
	}

	public String getDialogTitle() {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			return _fileChooser.getDialogTitle();
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			return _fileDialog.getTitle();
		}
		return null;
	}

	public void setDialogTitle(String title) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogTitle(title);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setTitle(title);
		}
	}

	public void setFileSelectionMode(int mode) {
		_mode = mode;
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			if (mode == JFileChooser.FILES_ONLY) {
				mode = JFileChooser.FILES_AND_DIRECTORIES;
			}
			_fileChooser.setFileSelectionMode(mode);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (mode == JFileChooser.DIRECTORIES_ONLY || mode == JFileChooser.FILES_AND_DIRECTORIES) {
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
			}
			else if (mode == JFileChooser.FILES_ONLY) {
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
			}
		}
		/*else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if(mode==JFileChooser.DIRECTORIES_ONLY){
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
				_fileDialog.setFilenameFilter(new FilenameFilter(){
					public boolean accept(File dir, String name) {
						return dir.isDirectory() && name==null;
					}
				});
			}else if(mode==JFileChooser.FILES_ONLY){
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
				_fileDialog.setFilenameFilter(new FilenameFilter(){
					public boolean accept(File dir, String name) {
						return dir.isDirectory() && name!=null && new File(dir,name).exists();
					}
				});
			}
		}*/
	}

	int _mode;

	/**
	 * <p>
	 * All extensions should be prefaced with '*.'
	 * <p>
	 * For more than one entry, use the ',' character
	 * <p>
	 * Example: '*.xsd, *.owl'
	 * <p>
	 * Note: trims whitespaces before and after extensions, and ignores case
	 * 
	 * @param filter
	 */
	public void setFileFilterAsString(final String filter) {
		if (filter == null || filter.trim().length() == 0) {
			return;
		}
		final String[] extensions = filter.split("[,;]");
		for (int i = 0; i < extensions.length; i++) {
			// We add .*? at the beginning to always match the beginning of the string
			// We trim all starting/ending whitespaces
			// We replace all '.' by '\.' because a dot means "match anything" but here we want to actually match a '.'
			// We replace all '*' with '.*?' because for a human, '*' means anything while for regexp '*' is a quantifier.
			// We also add a '?' to make it reluctant (otherwise .* will eat up everything and the final extension could be not matched)
			// We make it lower case so that the filter is not case sensitive.
			extensions[i] = ".*?" + extensions[i].trim().replace(".", "\\.").replace("*", ".*?").toLowerCase();
		}
		setFileFilter(new FileFilter() {
			private boolean accept(String name) {
				String lowerCase = name.toLowerCase();
				for (String extension : extensions) {
					if (lowerCase.matches(extension)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean accept(File f) {
				if (getImplementationType() == ImplementationType.FileDialogImplementation) {
					if (_mode == JFileChooser.DIRECTORIES_ONLY && !f.isDirectory()) {
						return false;
					}
					if (_mode == JFileChooser.FILES_ONLY && !f.isFile()) {
						return false;
					}
				}
				if (_mode == JFileChooser.DIRECTORIES_ONLY) {
					return f.isDirectory() && accept(f.getName());
				}
				else {
					return f.isDirectory() || accept(f.getName());
				}
			}

			@Override
			public String getDescription() {
				return filter;
			}
		});
	}

	public void setDialogType(int type) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogType(type);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (type == JFileChooser.SAVE_DIALOG) {
				_fileDialog.setMode(FileDialog.SAVE);
			}
			if (type == JFileChooser.OPEN_DIALOG) {
				_fileDialog.setMode(FileDialog.LOAD);
			}
		}
	}

	public void setFileView(FileView view) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setFileView(view);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {

		}
	}

	public void setFileFilter(FileFilter filter) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setFileFilter(filter);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setFilenameFilter(new FilenameFilterAdapter(filter));
		}
	}

	private static class FilenameFilterAdapter implements FilenameFilter {
		private final FileFilter _fileFilter;

		public FilenameFilterAdapter(FileFilter fileFilter) {
			super();
			_fileFilter = fileFilter;
		}

		@Override
		public boolean accept(File dir, String name) {
			if (name == null) {
				return _fileFilter.accept(dir);
			}
			return _fileFilter.accept(new File(dir, name));
		}

	}

	private Component buildAsJFileChooser() {
		_fileChooser = getFileChooser(System.getProperty("user.home"));
		FileFilter[] ff = _fileChooser.getChoosableFileFilters();
		for (int i = 0; i < ff.length; i++) {
			FileFilter filter = ff[i];
			_fileChooser.removeChoosableFileFilter(filter);
		}
		return _fileChooser;
	}

	private Component buildAsFileDialog() {
		if (_owner instanceof Frame) {
			_fileDialog = new FileDialog((Frame) _owner);
		}
		else if (_owner instanceof Dialog) {
			_fileDialog = new FileDialog((Dialog) _owner);
		}
		else {
			_fileDialog = new FileDialog(Frame.getFrames()[0]);
		}
		return _fileDialog;
	}

	public Component getComponent() {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			return _fileChooser;
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			return _fileDialog;
		}
		return null;
	}

	public int showDialog(String title) {
		setDialogTitle(title);
		return showOpenDialog();
	}

	public int showOpenDialog(Component parent) throws HeadlessException {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			return _fileChooser.showDialog(parent, null);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setMode(FileDialog.LOAD);
			_fileDialog.setAlwaysOnTop(true);
			_fileDialog.setModal(true);
			_fileDialog.setVisible(true);
			_fileDialog.toFront();
			if (_fileDialog.getFile() == null) {
				return JFileChooser.CANCEL_OPTION;
			}
			else {
				return JFileChooser.APPROVE_OPTION;
			}
		}
		return JFileChooser.ERROR_OPTION;
	}

	public int showOpenDialog() throws HeadlessException {
		return showOpenDialog(_owner);
	}

	public int showSaveDialog(Component parent) throws HeadlessException {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			return _fileChooser.showDialog(parent, null);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			_fileDialog.setMode(FileDialog.SAVE);
			_fileDialog.setModal(true);
			_fileDialog.setVisible(true);
			_fileDialog.toFront();
			if (_fileDialog.getFile() == null) {
				return JFileChooser.CANCEL_OPTION;
			}
			else {
				return JFileChooser.APPROVE_OPTION;
			}
		}
		return JFileChooser.ERROR_OPTION;
	}

	public int showSaveDialog() throws HeadlessException {
		return showSaveDialog(_owner);
	}

	public File getSelectedFile() {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			return _fileChooser.getSelectedFile();
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (_fileDialog.getFile() != null) {
				return new File(_fileDialog.getDirectory(), _fileDialog.getFile());
			}
		}
		return null;
	}

	public void setSelectedFile(File _file) {
		if (_file != null && !_file.isDirectory()) {
			if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
				_fileChooser.setSelectedFile(_file);
			}
			else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
				_fileDialog.setFile(_file.getName());
			}
		}
	}

	public void setApproveButtonText(String text) {
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			_fileChooser.setApproveButtonText(text);
		}
		else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			// Not implemented
		}
	}

}

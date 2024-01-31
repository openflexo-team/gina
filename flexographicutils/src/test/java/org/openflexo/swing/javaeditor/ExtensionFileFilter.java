/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.openflexo.swing.javaeditor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter for opening files with a specific extension.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ExtensionFileFilter extends FileFilter {

	private String desc;
	private String ext;

	/**
	 * Constructor.
	 *
	 * @param desc
	 *            A description of the file type.
	 * @param ext
	 *            The extension of the file type.
	 */
	ExtensionFileFilter(String desc, String ext) {
		this.desc = desc;
		this.ext = ext;
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().endsWith(ext);
	}

	@Override
	public String getDescription() {
		return desc + " (*." + ext + ")";
	}

}

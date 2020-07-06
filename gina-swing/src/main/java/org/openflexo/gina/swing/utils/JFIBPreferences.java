/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.gina.swing.utils;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.openflexo.pamela.converter.AWTRectangleConverter;
import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.pamela.model.StringConverterLibrary;
import org.openflexo.pamela.model.StringConverterLibrary.Converter;
import org.openflexo.rm.ResourceLocator;

public class JFIBPreferences {

	private static final String FIB = "FIB";

	public static final String FRAME = "Frame";
	public static final String PALETTE = "Palette";
	public static final String INSPECTOR = "Inspector";
	public static final String PREFERENCES = "Preferences";
	public static final String LAST_DIR = "LastDirectory";
	public static final String LAST_FILES_COUNT = "LAST_FILES_COUNT";
	public static final String LAST_FILE = "LAST_FILE";

	private static final Preferences prefs = Preferences.userRoot().node(FIB);

	private static AWTRectangleConverter RECTANGLE_CONVERTER = new AWTRectangleConverter();
	private static Converter<File> FILE_CONVERTER = StringConverterLibrary.getInstance().getConverter(File.class);

	private static Rectangle getPreferredBounds(String key, Rectangle def) {
		String s = prefs.get(key, null);
		if (s == null) {
			return def;
		}
		return RECTANGLE_CONVERTER.convertFromString(s, null);
	}

	public static void addPreferenceChangeListener(PreferenceChangeListener pcl) {
		prefs.addPreferenceChangeListener(pcl);
	}

	public static void removePreferenceChangeListener(PreferenceChangeListener pcl) {
		prefs.removePreferenceChangeListener(pcl);
	}

	private static void setPreferredBounds(String key, Rectangle value) {
		prefs.put(key, RECTANGLE_CONVERTER.convertToString(value));
	}

	private static File getPreferredFile(String key, File def) {
		String s = prefs.get(key, null);
		if (s == null) {
			return def;
		}
		try {
			return FILE_CONVERTER.convertFromString(s, null);
		} catch (InvalidDataException e) {
			return def;
		}
	}

	private static void setPreferredFile(String key, File value) {
		prefs.put(key, FILE_CONVERTER.convertToString(value));
	}

	public static Rectangle getFrameBounds() {
		return getPreferredBounds(FRAME, new Rectangle(0, 0, 1000, 800));
	}

	public static void setFrameBounds(Rectangle bounds) {
		setPreferredBounds(FRAME, bounds);
	}

	public static Rectangle getInspectorBounds() {
		return getPreferredBounds(INSPECTOR, new Rectangle(1000, 400, 300, 300));
	}

	public static void setInspectorBounds(Rectangle bounds) {
		setPreferredBounds(INSPECTOR, bounds);
	}

	public static Rectangle getPreferencesBounds() {
		return getPreferredBounds(PREFERENCES, new Rectangle(300, 400, 600, 600));
	}

	public static void setPreferencesBounds(Rectangle bounds) {
		setPreferredBounds(PREFERENCES, bounds);
	}

	public static Rectangle getPaletteBounds() {
		return getPreferredBounds(PALETTE, new Rectangle(1000, 0, 300, 300));
	}

	public static void setPaletteBounds(Rectangle bounds) {
		setPreferredBounds(PALETTE, bounds);
	}

	public static File getLastDirectory() {
		return getPreferredFile(LAST_DIR, ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResource("Fib")));
	}

	public static void setLastDirectory(File file) {
		setPreferredFile(LAST_DIR, file);
	}

	public static int getLastFileCount() {
		return prefs.getInt(LAST_FILES_COUNT, 10);
	}

	public static void setLastFileCount(int count) {
		prefs.putInt(LAST_FILES_COUNT, count);
	}

	public static List<File> getLastFiles() {
		List<File> list = new ArrayList<>();
		for (int i = 0; i < getLastFileCount(); i++) {
			File file = getPreferredFile(LAST_FILE + i, null);
			if (file != null) {
				list.add(file);
			}
		}
		return list;
	}

	public static void setLastFiles(List<File> files) {
		for (int i = 0; i < files.size(); i++) {
			setPreferredFile(LAST_FILE + i, files.get(i));
		}
	}

	public static void setLastFile(File file) {
		List<File> files = getLastFiles();
		if (files.contains(file)) {
			files.remove(file);
		}
		else if (files.size() == getLastFileCount()) {
			files.remove(getLastFileCount() - 1);
		}
		files.add(0, file);
		setLastFiles(files);
		setLastDirectory(file.getParentFile());
	}
}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;

import org.openflexo.gina.swing.utils.LoadedClassesInfo.ClassInfo;
import org.openflexo.gina.swing.utils.LoadedClassesInfo.PackageInfo;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;
import org.openflexo.toolbox.StringUtils;

/**
 * Model of a widget allowing to select a Java class in the application class loader
 * 
 * @author sguerin
 * 
 */
public class ClassEditor extends PropertyChangedSupportDefaultImplementation {
	@SuppressWarnings("hiding")
	static final Logger LOGGER = Logger.getLogger(ClassEditor.class.getPackage().getName());

	public static Resource FIB_FILE_NAME = ResourceLocator.getResourceLocator().locateResource("Fib/ClassEditor.fib");

	private LoadedClassesInfo loadedClassesInfo;

	private String filteredClassName = "";
	private boolean searchMode = false;
	public Vector<ClassInfo> matchingClasses = new Vector<ClassInfo>();

	public ClassEditor() {
		loadedClassesInfo = LoadedClassesInfo.getInstance();
		setFilteredClassName("");
		setSelectedClassInfo(null);
	}

	public ClassEditor(Class<?> editedClass) {
		this();
		selectClass(editedClass);
	}

	public void selectClass(Class<?> editedClass) {
		if (editedClass != null) {
			ClassInfo ci = loadedClassesInfo.registerClass(editedClass);
			setFilteredClassName(editedClass.getName());
			setSelectedClassInfo(ci);
		}
	}

	public void delete() {
		loadedClassesInfo = null;
	}

	public LoadedClassesInfo getLoadedClassesInfo() {
		return loadedClassesInfo;
	}

	public String getFilteredClassName() {
		return filteredClassName;
	}

	public void setFilteredClassName(String filteredClassName) {
		if (filteredClassName == null || !filteredClassName.equals(this.filteredClassName)) {
			String oldValue = this.filteredClassName;
			this.filteredClassName = filteredClassName;
			/*Vector<Class> foundClasses = new Vector<Class>();
			try {
				Class foundClass = Class.forName(getFilteredPackageName()+"."+filteredClassName);
				foundClasses.add(foundClass);
				logger.info("Found class "+foundClass);
			} catch (ClassNotFoundException e) {
			}
			for (Package p : packages.keySet()) {
				try {
					Class foundClass = Class.forName(p.getName()+"."+filteredClassName);
					foundClasses.add(foundClass);
					logger.info("Found class "+foundClass);
				} catch (ClassNotFoundException e) {
				}
			}
			for (Class c : foundClasses) {
				registerClass(c);
			}*/
			getPropertyChangeSupport().firePropertyChange("filteredClassName", oldValue, filteredClassName);
			if (searchMode) {
				updateMatchingClasses();
				getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
			}
		}
	}

	public void search() {
		// LOGGER.info("SEARCH " + filteredClassName);
		isExplicitelySearching = true;
		explicitelySearch();
		updateMatchingClasses();
		isExplicitelySearching = false;
		if (matchingClasses.size() != 1) {
			searchMode = true;
		}
		getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
	}

	public void done() {
		LOGGER.info("Done with SEARCH " + filteredClassName);
		searchMode = false;
		setFilteredClassName("");
		getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
	}

	public boolean searchMode() {
		return matchingClasses.size() != 1 && searchMode;
	}

	private boolean showFilteredPatterns = false;

	public boolean showFilteredPatterns() {
		return showFilteredPatterns;
	}

	public void toggleFilteredPatterns() {
		showFilteredPatterns = !showFilteredPatterns;
		getPropertyChangeSupport().firePropertyChange("showFilteredPatterns", !showFilteredPatterns, showFilteredPatterns);
	}

	/**
	 * Internally called to explicitely search in all known packages if class identified by simple name exists
	 */
	private void explicitelySearch() {
		LOGGER.info("*************** Searching class " + filteredClassName);
		/*Vector<Class> foundClasses = new Vector<Class>();
		try {
			Class foundClass = Class.forName(filteredClassName);
			foundClasses.add(foundClass);
			LOGGER.info("Found class " + foundClass);
		} catch (ClassNotFoundException e) {
		}
		for (PackageInfo packageInfo : loadedClassesInfo.getPackages()) {
			try {
				Class foundClass = Class.forName(packageInfo.getPackageName() + "." + filteredClassName);
				foundClasses.add(foundClass);
				LOGGER.info("Found class " + foundClass);
			} catch (ClassNotFoundException e) {
			}
		}
		for (Class c : foundClasses) {
			loadedClassesInfo.registerClass(c);
		}*/

		List<Class<?>> foundClasses = loadedClassesInfo.search(filteredClassName);
		for (Class c : foundClasses) {
			loadedClassesInfo.registerClass(c);
		}
	}

	private boolean isExplicitelySearching = false;

	private void updateMatchingClasses() {

		LOGGER.info("*************** updateMatchingClasses() for " + filteredClassName);
		// System.out.println("updateMatchingClasses() for " + filteredPackageName + " " + filteredClassName);

		matchingClasses.clear();

		if (!StringUtils.isEmpty(filteredClassName)) {
			String patternString = filteredClassName;
			if (patternString.startsWith("*")) {
				patternString = "." + filteredClassName;
			}
			try {
				String simpleName;
				if (patternString.lastIndexOf(".") > -1) {
					simpleName = patternString.substring(patternString.lastIndexOf(".") + 1);
				}
				else {
					simpleName = patternString;
				}
				List<ClassInfo> exactMatches = new ArrayList<ClassInfo>();
				if (loadedClassesInfo.getRegisteredClassesForName().get(simpleName) != null) {
					exactMatches = loadedClassesInfo.getRegisteredClassesForName().get(simpleName);
					matchingClasses.addAll(exactMatches);
				}
				Pattern pattern = Pattern.compile(patternString);
				for (String s : loadedClassesInfo.getRegisteredClassesForName().keySet()) {
					Matcher matcher = pattern.matcher(s);
					if (matcher.find()) {
						for (ClassInfo potentialMatch : loadedClassesInfo.getRegisteredClassesForName().get(s)) {
							PackageInfo packageInfo = loadedClassesInfo.registerPackage(potentialMatch.getClazz().getPackage());
							if (!packageInfo.isFiltered()) {
								if (!exactMatches.contains(potentialMatch)) {
									matchingClasses.add(potentialMatch);
									// System.out.println("Found "+potentialMatch);
								}
							}
						}
					}
				}
				if (matchingClasses.size() == 0 && !isExplicitelySearching) {
					// Special case, we try to instanciate class for each package
					System.out.println("Trying to find class....");
					search();
				}
			} catch (PatternSyntaxException e) {
				LOGGER.warning("PatternSyntaxException: " + patternString);
			}
		}

		// System.out.println("Matching classes= " + matchingClasses);

		loadedClassesInfo.getPropertyChangeSupport().firePropertyChange("packages", null, loadedClassesInfo.getPackages());
		getPropertyChangeSupport().firePropertyChange("matchingClasses", null, matchingClasses);

		if (matchingClasses.size() == 1) {
			setSelectedClassInfo(matchingClasses.firstElement());
		}

	}

	private ClassInfo selectedClassInfo;

	public ClassInfo getSelectedClassInfo() {
		return selectedClassInfo;
	}

	public void setSelectedClassInfo(ClassInfo selectedClassInfo) {
		if (selectedClassInfo != this.selectedClassInfo) {
			ClassInfo oldSelectedClassInfo = this.selectedClassInfo;
			LOGGER.fine("setSelectedClassInfo with " + selectedClassInfo);
			this.selectedClassInfo = selectedClassInfo;
			// if (selectedClassInfo != null) setFilteredClassName(selectedClassInfo.className);
			getPropertyChangeSupport().firePropertyChange("selectedClassInfo", oldSelectedClassInfo, selectedClassInfo);
			if (/*matchingClasses.size() < 2 &&*/ selectedClassInfo != null) {
				setFilteredClassName(selectedClassInfo.getClazz().getName());
			}
			searchMode = false;
			getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
		}
	}

	public void performSelect(ClassInfo selectedClassInfo) {
		setSelectedClassInfo(selectedClassInfo);
		if (selectedClassInfo != null) {
			setFilteredClassName(selectedClassInfo.getClazz().getName());
		}
	}

	public Icon getSearchIcon() {
		return UtilsIconLibrary.SEARCH_ICON;
	}

	public Icon getDoneIcon() {
		return UtilsIconLibrary.CANCEL_ICON;
	}

	public Icon getFiltersIcon() {
		return UtilsIconLibrary.FILTERS_ICON;
	}

	public Icon getJavaIcon() {
		return FIBIconLibrary.JAVA_ICON;
	}

	private Icon filterIcon = null;

	public Icon getFilterIcon() {
		if (filterIcon == null) {
			filterIcon = IconFactory.getImageIcon(FIBIconLibrary.PACKAGE_ICON, UtilsIconLibrary.MINUS_MARKER);
		}
		return filterIcon;
	}

}

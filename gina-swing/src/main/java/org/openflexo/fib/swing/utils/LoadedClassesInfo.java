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

package org.openflexo.fib.swing.utils;

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;

import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.ClassScope;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

/**
 * Utility class used to reflect all loaded java classes in {@link ClassLoader}<br>
 * 
 * A common memory area (static fields) is shared by all instances of this class
 * 
 * @author sylvain
 * 
 */
public class LoadedClassesInfo implements HasPropertyChangeSupport {

	private static final Logger LOGGER = Logger.getLogger(LoadedClassesInfo.class.getPackage().getName());

	static ClassLoader appLoader = ClassLoader.getSystemClassLoader();
	static ClassLoader currentLoader = LoadedClassesInfo.class.getClassLoader();
	static ClassLoader[] loaders = new ClassLoader[] { appLoader, currentLoader };

	// private static LoadedClassesInfo instance;

	static {
		LOGGER.info("Starting loading classes in LoadedClassesInfo");
		appLoader = ClassLoader.getSystemClassLoader();
		currentLoader = LoadedClassesInfo.class.getClassLoader();
		if (appLoader != currentLoader) {
			loaders = new ClassLoader[] { appLoader, currentLoader };
		} else {
			loaders = new ClassLoader[] { appLoader };
		}
		// instance = new LoadedClassesInfo();

		init();

		LOGGER.info("Finished loading classes in LoadedClassesInfo");
	}

	private static Hashtable<Package, PackageInfo> packages;
	private static Vector<PackageInfo> packageList;
	private static boolean needsReordering = true;
	private static Hashtable<String, Vector<ClassInfo>> classesForName;
	private static List<LoadedClassesInfo> instances;

	private String filteredPackageName = "*";
	private String filteredClassName = "";

	private final boolean searchMode = false;

	public Vector<ClassInfo> matchingClasses = new Vector<ClassInfo>();

	private final PropertyChangeSupport pcSupport;

	/**
	 * Statically initialize this class with {@link ClassLoader}s
	 */
	private static void init() {
		instances = new ArrayList<LoadedClassesInfo>();
		classesForName = new Hashtable<String, Vector<ClassInfo>>();
		packages = new Hashtable<Package, PackageInfo>() {
			@Override
			public synchronized PackageInfo put(Package key, PackageInfo value) {
				PackageInfo returned = super.put(key, value);
				needsReordering = true;
				for (LoadedClassesInfo instance : instances) {
					instance.pcSupport.firePropertyChange("packages", null, null);
				}
				return returned;
			};
		};
		for (Package p : Package.getPackages()) {
			registerPackage(p);
		}
		final Class<?>[] classes = ClassScope.getLoadedClasses(loaders);
		for (Class<?> cls : classes) {
			registerClass(cls);
			String className = cls.getName();
			URL classLocation = ClassScope.getClassLocation(cls);
			// System.out.println("Registered class: " + className + " from " +classLocation);
		}
	}

	public LoadedClassesInfo() {
		pcSupport = new PropertyChangeSupport(this);
		setFilteredPackageName("*");
		setFilteredClassName("");
		setSelectedClassInfo(null);
	}

	public LoadedClassesInfo(Class aClass) {
		this();
		if (aClass != null) {
			ClassInfo ci = registerClass(aClass);
			setFilteredPackageName("*");
			setFilteredClassName(aClass.getName());
			// instance.setFilteredPackageName(aClass.getPackage().getName());
			setSelectedClassInfo(ci);
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public static List<PackageInfo> getPackages() {
		if (needsReordering) {
			packageList = new Vector<PackageInfo>();
			for (Package p : packages.keySet()) {
				packageList.add(packages.get(p));
			}
			Collections.sort(packageList, new Comparator<PackageInfo>() {
				@Override
				public int compare(PackageInfo o1, PackageInfo o2) {
					return Collator.getInstance().compare(o1.packageName, o2.packageName);
				}
			});
			needsReordering = false;
		}
		return packageList;
	}

	private static PackageInfo registerPackage(Package p) {
		PackageInfo returned = packages.get(p);
		if (returned == null) {
			packages.put(p, returned = new PackageInfo(p));
		}
		return returned;
	}

	public static ClassInfo getClass(Class c) {
		return registerClass(c);
	}

	private static ClassInfo registerClass(Class c) {
		if (c == null) {
			LOGGER.warning("Null class " + c);
			return null;
		}

		if (c.getPackage() == null) {
			LOGGER.warning("No package for class " + c);
			return null;
		}

		PackageInfo p = registerPackage(c.getPackage());

		LOGGER.fine("Register class " + c);

		if (!c.isMemberClass() && !c.isAnonymousClass() && !c.isLocalClass()) {
			ClassInfo returned = p.classes.get(c);
			if (returned == null) {
				p.classes.put(c, returned = new ClassInfo(c));
				LOGGER.fine("Store " + returned + " in package " + p.packageName);
			}
			return returned;
		} else if (c.isMemberClass()) {
			// System.out.println("Member class: "+c+" of "+c.getDeclaringClass());
			ClassInfo parentClass = registerClass(c.getEnclosingClass());
			if (parentClass != null) {
				ClassInfo returned = parentClass.declareMember(c);
				return returned;
			}
			return null;
		} else {
			// System.out.println("Ignored class: "+c);
			return null;
		}

	}

	public static class PackageInfo implements HasPropertyChangeSupport {
		public String packageName;
		private final Hashtable<Class, ClassInfo> classes = new Hashtable<Class, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("classes", null, null);
				return returned;
			};
		};
		private Vector<ClassInfo> classesList;
		private boolean needsReordering = true;
		private final PropertyChangeSupport pcSupport;

		public PackageInfo(Package aPackage) {
			pcSupport = new PropertyChangeSupport(this);
			packageName = aPackage.getName();
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<ClassInfo> getClasses() {
			if (needsReordering) {
				classesList = new Vector<ClassInfo>();
				for (Class c : classes.keySet()) {
					classesList.add(classes.get(c));
				}
				Collections.sort(classesList, new Comparator<ClassInfo>() {
					@Override
					public int compare(ClassInfo o1, ClassInfo o2) {
						return Collator.getInstance().compare(o1.className, o2.className);
					}
				});
				needsReordering = false;
			}
			return classesList;
		}

		public boolean isFiltered(LoadedClassesInfo loadedClassesInfo) {
			if (loadedClassesInfo.getFilteredPackageName() == null || StringUtils.isEmpty(loadedClassesInfo.getFilteredPackageName())) {
				return false;
			}
			if (packageName.startsWith(loadedClassesInfo.getFilteredPackageName())) {
				return false;
			}
			String patternString = loadedClassesInfo.getFilteredPackageName();
			if (patternString.startsWith("*")) {
				patternString = "." + loadedClassesInfo.getFilteredPackageName();
			}
			try {
				Pattern pattern = Pattern.compile(patternString);
				Matcher matcher = pattern.matcher(packageName);
				return !matcher.find();
			} catch (PatternSyntaxException e) {
				LOGGER.warning("PatternSyntaxException: " + patternString);
				return false;
			}
		}

		public Icon getIcon() {
			return FIBIconLibrary.PACKAGE_ICON;
		}

	}

	public static class ClassInfo implements HasPropertyChangeSupport {
		private final Class clazz;
		public String className;
		public String packageName;
		public String fullQualifiedName;

		private final Hashtable<Class, ClassInfo> memberClasses = new Hashtable<Class, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("memberClasses", null, null);
				return returned;
			};
		};
		private Vector<ClassInfo> memberClassesList;
		private boolean needsReordering = true;
		private final PropertyChangeSupport pcSupport;

		public ClassInfo(Class aClass) {
			pcSupport = new PropertyChangeSupport(this);
			Vector<ClassInfo> listOfClassesWithThatName = classesForName.get(aClass.getSimpleName());
			if (listOfClassesWithThatName == null) {
				classesForName.put(aClass.getSimpleName(), listOfClassesWithThatName = new Vector<ClassInfo>());
			}
			listOfClassesWithThatName.add(this);
			className = aClass.getSimpleName();
			packageName = aClass.getPackage().getName();
			fullQualifiedName = aClass.getName();
			clazz = aClass;
			LOGGER.fine("Instanciate new ClassInfo for " + aClass);
		}

		public Class getClazz() {
			return clazz;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		protected ClassInfo declareMember(Class c) {
			ClassInfo returned = memberClasses.get(c);
			if (returned == null) {
				memberClasses.put(c, returned = new ClassInfo(c));
				needsReordering = true;
				LOGGER.fine(toString() + ": declare member: " + returned);
			}
			return returned;
		}

		public List<ClassInfo> getMemberClasses() {
			if (needsReordering) {
				memberClassesList = new Vector<ClassInfo>();
				for (Class c : memberClasses.keySet()) {
					memberClassesList.add(memberClasses.get(c));
				}
				Collections.sort(memberClassesList, new Comparator<ClassInfo>() {
					@Override
					public int compare(ClassInfo o1, ClassInfo o2) {
						return Collator.getInstance().compare(o1.className, o2.className);
					}
				});
				needsReordering = false;
			}
			return memberClassesList;
		}

		public Class getRepresentedClass() {
			return clazz;
		}

		@Override
		public String toString() {
			return "ClassInfo[" + clazz.getName() + "]";
		}

		public Icon getIcon() {
			if (clazz.isEnum()) {
				return FIBIconLibrary.ENUM_ICON;
			}
			if (clazz.isInterface()) {
				return FIBIconLibrary.INTERFACE_ICON;
			}
			return FIBIconLibrary.CLASS_ICON;
		}

	}

	public String getFilteredPackageName() {
		return filteredPackageName;
	}

	public void setFilteredPackageName(String filter) {
		if (filter == null || !filter.equals(this.filteredPackageName)) {
			String oldValue = this.filteredPackageName;
			this.filteredPackageName = filter;
			getPropertyChangeSupport().firePropertyChange("filteredPackageName", oldValue, filteredPackageName);
			updateMatchingClasses();
		}
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
			updateMatchingClasses();
		}
	}

	public void search() {
		isExplicitelySearching = true;
		explicitelySearch();
		updateMatchingClasses();
		isExplicitelySearching = false;
	}

	/**
	 * Internally called to explicitely search in all known packages if class identified by simple name exists
	 */
	private void explicitelySearch() {
		Vector<Class> foundClasses = new Vector<Class>();
		try {
			Class foundClass = Class.forName(getFilteredPackageName() + "." + filteredClassName);
			foundClasses.add(foundClass);
			LOGGER.info("Found class " + foundClass);
		} catch (ClassNotFoundException e) {
		}
		for (Package p : packages.keySet()) {
			try {
				Class foundClass = Class.forName(p.getName() + "." + filteredClassName);
				foundClasses.add(foundClass);
				LOGGER.info("Found class " + foundClass);
			} catch (ClassNotFoundException e) {
			}
		}
		for (Class c : foundClasses) {
			registerClass(c);
		}
	}

	private boolean isExplicitelySearching = false;

	private void updateMatchingClasses() {

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
				} else {
					simpleName = patternString;
				}
				Vector<ClassInfo> exactMatches = new Vector<ClassInfo>();
				if (classesForName.get(simpleName) != null) {
					exactMatches = classesForName.get(simpleName);
					matchingClasses.addAll(exactMatches);
				}
				Pattern pattern = Pattern.compile(patternString);
				for (String s : classesForName.keySet()) {
					Matcher matcher = pattern.matcher(s);
					if (matcher.find()) {
						for (ClassInfo potentialMatch : classesForName.get(s)) {
							PackageInfo packageInfo = registerPackage(potentialMatch.clazz.getPackage());
							if (!packageInfo.isFiltered(this)) {
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

		pcSupport.firePropertyChange("packages", null, getPackages());
		pcSupport.firePropertyChange("matchingClasses", null, matchingClasses);

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
			pcSupport.firePropertyChange("selectedClassInfo", oldSelectedClassInfo, selectedClassInfo);
			if (matchingClasses.size() < 2 && selectedClassInfo != null) {
				setFilteredClassName(selectedClassInfo.getClazz().getName());
			}
		}
	}

	public void performSelect(ClassInfo selectedClassInfo) {
		setSelectedClassInfo(selectedClassInfo);
		if (selectedClassInfo != null) {
			setFilteredClassName(selectedClassInfo.getClazz().getName());
		}
	}
}

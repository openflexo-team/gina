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

package org.openflexo.gina.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * Implements a logical group of inspectors as a set of merged FIB components
 * 
 * @author sylvain
 * 
 */
public class InspectorGroup {

	static final Logger logger = Logger.getLogger(InspectorGroup.class.getPackage().getName());

	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	private final Hashtable<Class<?>, FIBInspector> inspectors;

	private FIBModelFactory fibModelFactory;

	private final List<InspectorGroup> parentInspectorGroups;

	public InspectorGroup(Resource inspectorDirectory, InspectorGroup... someInspectorGroups) {
		inspectors = new Hashtable<Class<?>, FIBInspector>();

		try {
			fibModelFactory = new FIBModelFactory(FIBInspector.class);
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parentInspectorGroups = new ArrayList<>();
		for (InspectorGroup inspectorGroup : someInspectorGroups) {
			parentInspectorGroups.add(inspectorGroup);
		}

		for (Resource f : inspectorDirectory.getContents(Pattern.compile(".*[.]inspector"))) {
			// System.out.println("Read "+f.getAbsolutePath());
			logger.info("Loading " + f.getURI());
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(f, false, fibModelFactory);
			if (component instanceof FIBInspector) {
				FIBInspector inspector = (FIBInspector) component;
				if (inspector != null) {
					if (inspector.getDataClass() != null) {
						inspectors.put(inspector.getDataClass(), inspector);
						logger.fine("Loaded inspector: " + f.getURI() + " for " + inspector.getDataClass());
						progress(f, inspector);
					}
				}
				else {
					logger.warning("Not found: " + f.getURI());
				}
			}
			else {
				logger.warning("Component in " + f + " is not an inspector !");
			}
		}

		// We first identify all parents
		for (FIBInspector inspector : new ArrayList<FIBInspector>(inspectors.values())) {
			// System.out.println("identifySuperInspectors " + inspector.getInspectedClass());
			inspector.identifySuperInspectors(this);
		}

		// We first merge all inspectors inside the group
		for (FIBInspector inspector : new ArrayList<FIBInspector>(inspectors.values())) {
			// System.out.println("Merging " + inspector.getInspectedClass());
			inspector.mergeWithParentInspectors();
		}

		// Then, for each parent inspector group, we compute the resulting inspector from superclasses.
		// Then we choose the most specialized one to merge with the new inspector
		/*for (FIBInspector inspector : new ArrayList<FIBInspector>(inspectors.values())) {
			Map<Class<?>, FIBInspector> parentGroupInspectors = new HashMap<Class<?>, FIBInspector>();
			for (InspectorGroup parentGroup : parentInspectorGroups) {
				FIBInspector parentInspector = parentGroup.inspectorForClass(inspector.getDataClass());
				if (parentInspector != null) {
					parentGroupInspectors.put(parentInspector.getDataClass(), parentInspector);
				}
			}
			if (parentGroupInspectors.size() > 0) {
				// Fixed issue with multiple inheritance
				// I think this is really costly in cpu time
				// TODO: check and optimize this !!!
				for (FIBInspector inspectorToAppend : parentGroupInspectors.values()) {
					inspector.appendSuperInspector(inspectorToAppend, fibModelFactory);
				}
			}
		}*/

	}

	/**
	 * Return the most specialized inspector, contained in this group, that represents supplied object
	 * 
	 * @param aClass
	 * @return
	 */
	public FIBInspector inspectorForObject(Object object) {
		if (object == null) {
			return null;
		}
		return inspectorForClass(object.getClass());
	}

	/**
	 * Return the most specialized inspector, contained in this group, that represents supplied class
	 * 
	 * @param aClass
	 * @return
	 */
	public FIBInspector inspectorForClass(Class<?> aClass) {
		return TypeUtils.objectForClass(aClass, inspectors);
	}

	/**
	 * Return the list of all inspectors, contained in this group, that represents supplied class<br>
	 * We can obtain, this way, the list of all super inspectors that are composed for a given inspector.
	 * 
	 * @param aClass
	 * @return
	 */
	public List<FIBInspector> inspectorsForClass(Class<?> aClass) {
		List<FIBInspector> returned = new ArrayList<FIBInspector>();
		for (FIBInspector inspector : getInspectors().values()) {
			if (inspector.getDataClass().isAssignableFrom(aClass)) {
				if (!returned.contains(inspector)) {
					returned.add(inspector);
				}
			}
		}
		return returned;
	}

	public List<FIBInspector> getAllAccessiblesInspectors() {
		List<FIBInspector> returned = new ArrayList<>();
		visitSuperInspectors(this, returned);
		return returned;
	}

	private void visitSuperInspectors(InspectorGroup inspectorGroup, List<FIBInspector> inspectorList) {
		inspectorList.addAll(inspectorGroup.getInspectors().values());
		// System.out.println("visitSuperInspectors in " + this + " inspectorGroup=" + inspectorGroup + " parent=" +
		// inspectorGroup.parentInspectorGroups);
		for (InspectorGroup parentInspectorGroup : inspectorGroup.parentInspectorGroups) {
			if (parentInspectorGroup != this) {
				visitSuperInspectors(parentInspectorGroup, inspectorList);
			}
		}
	}

	public Hashtable<Class<?>, FIBInspector> getInspectors() {
		return inspectors;
	}

	public FIBModelFactory getFIBModelFactory() {
		return fibModelFactory;
	}

	/**
	 * Hook to plug progress
	 */
	public void progress(Resource f, FIBInspector inspector) {

	}
}

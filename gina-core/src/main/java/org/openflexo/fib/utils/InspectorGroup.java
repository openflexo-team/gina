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

package org.openflexo.fib.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBModelFactory;
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

	public InspectorGroup(Resource inspectorDirectory) {
		inspectors = new Hashtable<Class<?>, FIBInspector>();

		try {
			fibModelFactory = new FIBModelFactory(FIBInspector.class);
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Resource f : inspectorDirectory.getContents(Pattern.compile(".*[.]inspector"))) {
			// System.out.println("Read "+f.getAbsolutePath());
			logger.info("Loading " + f.getURI());
			FIBInspector inspector = (FIBInspector) FIBLibrary.instance().retrieveFIBComponent(f, false, fibModelFactory);
			if (inspector != null) {
				if (inspector.getDataClass() != null) {
					// try {
					inspectors.put(inspector.getDataClass(), inspector);
					logger.info("Loaded inspector: " + f.getURI() + " for " + inspector.getDataClass());
					/*} catch (ClassNotFoundException e) {
						logger.warning("Not found: " + inspector.getDataClassName());
					}*/
				}
			} else {
				logger.warning("Not found: " + f.getURI());
			}
		}

		for (FIBInspector inspector : new ArrayList<FIBInspector>(inspectors.values())) {
			// System.out.println(">>>>>>>>>>>>> BEGIN appendSuperInspectors for " + inspector.getDataClass());
			inspector.appendSuperInspectors(this);
			// System.out.println("<<<<<<<<<<<<< END appendSuperInspectors for " + inspector.getDataClass());
		}

	}

	public FIBInspector inspectorForObject(Object object) {
		if (object == null) {
			return null;
		}
		return inspectorForClass(object.getClass());
	}

	public FIBInspector inspectorForClass(Class<?> aClass) {
		return TypeUtils.objectForClass(aClass, inspectors);
	}

	public Hashtable<Class<?>, FIBInspector> getInspectors() {
		return inspectors;
	}

	public FIBModelFactory getFIBModelFactory() {
		return fibModelFactory;
	}
}

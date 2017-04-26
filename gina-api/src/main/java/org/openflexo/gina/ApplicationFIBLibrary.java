/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina;

import java.util.logging.Logger;

import org.openflexo.connie.type.CustomTypeManager;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * {@link ApplicationFIBLibrary} is the FIBLibrary that is used in the application.<br>
 * Only one instance of that class might be instantiated inside a JVM.
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(ApplicationFIBLibrary.ApplicationFIBLibraryImpl.class)
public interface ApplicationFIBLibrary extends FIBLibrary {

	public static abstract class ApplicationFIBLibraryImpl extends FIBLibraryImpl implements ApplicationFIBLibrary {

		static final Logger LOGGER = Logger.getLogger(ApplicationFIBLibrary.class.getPackage().getName());

		private static ApplicationFIBLibraryImpl instance;

		public ApplicationFIBLibraryImpl() {
			super();
		}

		public static ApplicationFIBLibrary createInstance(CustomTypeManager customTypeManager) {

			if (customTypeManager == null) {
				LOGGER.warning("ApplicationFIBLibrary created with a null customTypeManager. Please investigate");
				Thread.dumpStack();
			}
			instance = (ApplicationFIBLibraryImpl) FOLDER_FACTORY.newInstance(ApplicationFIBLibrary.class);
			instance.setCustomTypeManager(customTypeManager);
			try {
				instance.fibModelFactory = new FIBModelFactory(customTypeManager);
			} catch (ModelDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return instance;
		}

		public static ApplicationFIBLibrary instance() {
			if (instance == null) {
				createInstance(null);
			}
			return instance;
		}

		public static ApplicationFIBLibrary instance(CustomTypeManager customTypeManager) {
			if (instance == null) {
				createInstance(customTypeManager);
			}
			return instance;
		}

		public static boolean hasInstance() {
			return instance != null;
		}
	}
}

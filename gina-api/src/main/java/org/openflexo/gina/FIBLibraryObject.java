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

import org.openflexo.pamela.AccessibleProxyObject;
import org.openflexo.pamela.DeletableProxyObject;
import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

/**
 * A {@link FIBLibraryObject} is the base interface for FIBLibrary
 * 
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FIBLibraryObject.FIBLibraryObjectImpl.class)
public abstract interface FIBLibraryObject extends AccessibleProxyObject, DeletableProxyObject {

	public static abstract class FIBLibraryObjectImpl implements FIBLibraryObject {

		protected static ModelFactory FOLDER_FACTORY;

		static {
			try {
				FOLDER_FACTORY = new ModelFactory(
						ModelContextLibrary.getCompoundModelContext(FIBLibrary.class, ApplicationFIBLibrary.class, FIBFolder.class));
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}

		}

	}

}

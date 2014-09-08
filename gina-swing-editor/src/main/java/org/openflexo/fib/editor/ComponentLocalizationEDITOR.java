/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.editor;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class ComponentLocalizationEDITOR {

	public static void main(String[] args) {
		final ResourceLocator rl = ResourceLocator.getResourceLocator();
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(new FIBController(FIBLibrary.instance().retrieveFIBComponent(getFIBResource(), true)));
			}

			@Override
			public Resource getFIBResource() {
				return ResourceLocator.locateSourceCodeResource(FIBEditor.COMPONENT_LOCALIZATION_FIB);
			}
		};
		editor.launch();
	}
}

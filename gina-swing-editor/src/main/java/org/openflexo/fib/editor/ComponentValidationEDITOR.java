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
import org.openflexo.fib.swing.validation.ValidationPanel;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class ComponentValidationEDITOR {

	public static void main(String[] args) {

		final ResourceLocator rl = ResourceLocator.getResourceLocator();

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(FIBLibrary.instance().retrieveFIBComponent(ValidationPanel.COMPONENT_VALIDATION_FIB, true).validate(),
						FIBLibrary.instance().retrieveFIBComponent(FIBEditor.COMPONENT_LOCALIZATION_FIB, true).validate());
			}

			@Override
			public Resource getFIBResource() {
				return ResourceLocator.locateSourceCodeResource(ValidationPanel.COMPONENT_VALIDATION_FIB);
			}
		};
		editor.launch();
	}
}

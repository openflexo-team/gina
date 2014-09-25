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

import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class ValidationLocalizedEditorEDITOR {

	public static LocalizedDelegate VALIDATION_LOCALIZATION = new LocalizedDelegateImpl(ResourceLocator.getResourceLocator()
			.locateResource("FIBValidationLocalized"), null, true);

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(VALIDATION_LOCALIZATION);
			}

			@Override
			public Resource getFIBResource() {
				return ResourceLocator.locateSourceCodeResource(ResourceLocator.locateResource("Fib/LocalizedEditor.fib"));
			}
		};
		editor.launch();
	}
}

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
package org.openflexo.fib.editor.test;

import java.io.File;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.sampleData.Family;
import org.openflexo.toolbox.ResourceLocator;

public class TestDynamicReuse extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		Object[] returned = new Object[1];
		returned[0] = new Family();
		return returned;
	}

	@Override
	public File getFIBFile() {
		return ResourceLocator.locateFile("TestFIB/TestDynamicReuse.fib");
	}

	public static void main(String[] args) {
		main(TestDynamicReuse.class);
	}

}

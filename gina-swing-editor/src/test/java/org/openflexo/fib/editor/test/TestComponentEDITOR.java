/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.fib.editor.test;

import java.util.Vector;

import org.openflexo.gina.swing.editor.FIBAbstractEditor;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public class TestComponentEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(new TestComponentData());
			}

			@Override
			public Resource getFIBResource() {
				return ResourceLocator.locateSourceCodeResource("TestFIB/TestComponent.fib");
			}
		};
		editor.launch();
	}

	public static class TestComponentData {
		public String name;
		public String description;
		public int index;
		public Vector<Foo> someFoo;

		public TestComponentData() {
			name = "name";
			description = "description";
			index = 1;
			someFoo = new Vector<Foo>();
			someFoo.add(new Foo("i", 7));
			someFoo.add(new Foo("am", 2));
			someFoo.add(new Foo("the", 9));
			someFoo.add(new Foo("queen", 12));
		}

	}

	public static class Foo {
		public String fooName;
		public int fooIndex;
		public Vector<Foo2> someFoo2;

		public Foo(String name, int index) {
			fooName = name;
			fooIndex = index;
			someFoo2 = new Vector<Foo2>();
			for (int i = 0; i < index; i++) {
				someFoo2.add(new Foo2(fooName + "_foo2_number_" + i, i));
			}
		}

		@Override
		public String toString() {
			return fooName;
		}
	}

	public static class Foo2 {
		public String foo2Name = "foo2";
		public int foo2Index = 3;

		public Foo2(String name, int index) {
			foo2Name = name;
			foo2Index = index;
		}

		@Override
		public String toString() {
			return foo2Name;
		}
	}
}

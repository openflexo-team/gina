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

package org.openflexo.gina.swing.editor.test;

import java.beans.PropertyChangeSupport;
import java.util.Vector;

import org.junit.Test;
import org.openflexo.gina.testutils.GinaSwingEditorTestCase;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class TestFIBBrowser2 extends GinaSwingEditorTestCase {

	@Test
	public void testComponent() {
		final TestClass mainClass = new TestClass("Main");
		final TestMethod method11 = new TestMethod("method1");
		final TestMethod method12 = new TestMethod("method2");
		final TestClass class1 = new TestClass("Class1", method11, method12);
		final TestMethod method21 = new TestMethod("method1Disabled");
		final TestMethod method22 = new TestMethod("method2Disabled");
		final TestMethod method23 = new TestMethod("method3");
		final TestClass class2 = new TestClass("Class2", method21, method22, method23);
		final TestMethod method31 = new TestMethod("method1");
		// Unused final TestMethod method32 = new TestMethod("method2Disabled");
		final TestClass class3 = new TestClass("Class3Disabled", method31);
		final TestMethod method51 = new TestMethod("method1");
		final TestClass class5 = new TestClass("Class5Invisible", method51);
		final TestPackage testPackage = new TestPackage("my.package", mainClass, class1, class2, class3, class5);
		final TestMethod method41 = new TestMethod("fooDisabled");
		final TestMethod method42 = new TestMethod("foo2");
		final TestClass class4 = new TestClass("Class4", method41, method42);

		instanciateFIBEdition("TestBrowser2", ResourceLocator.locateSourceCodeResource("TestFIB/TestBrowser2.fib"), testPackage);

		/*		editor.addAction("change_name", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class1.setName("new_class_1_name");
			}
		});
		editor.addAction("enable_class3", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class3.setName("Class3");
			}
		});
		editor.addAction("add_class", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.addClass(class4);
			}
		});
		editor.addAction("add_method", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class3.addMethod(method32);
			}
		});
		editor.addAction("remove_class", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.removeClass(class4);
			}
		});
		editor.addAction("remove_classes", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.removeClass(class2);
				testPackage.removeClass(class1);
			}
		});
		editor.addAction("make_class5_visible", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class5.setName("Class5");
			}
		});
		 */

	}

	/*private static final ResourceLocator rl = ResourceLocator.getResourceLocator();
	
	public static Resource FIB_FILE = ResourceLocator.locateSourceCodeResource("TestFIB/TestBrowser2.fib");
	
	public static void main(String[] args) {
		final TestClass mainClass = new TestClass("Main");
		final TestMethod method11 = new TestMethod("method1");
		final TestMethod method12 = new TestMethod("method2");
		final TestClass class1 = new TestClass("Class1", method11, method12);
		final TestMethod method21 = new TestMethod("method1Disabled");
		final TestMethod method22 = new TestMethod("method2Disabled");
		final TestMethod method23 = new TestMethod("method3");
		final TestClass class2 = new TestClass("Class2", method21, method22, method23);
		final TestMethod method31 = new TestMethod("method1");
		final TestMethod method32 = new TestMethod("method2Disabled");
		final TestClass class3 = new TestClass("Class3Disabled", method31);
		final TestMethod method51 = new TestMethod("method1");
		final TestClass class5 = new TestClass("Class5Invisible", method51);
		final TestPackage testPackage = new TestPackage("my.package", mainClass, class1, class2, class3, class5);
		final TestMethod method41 = new TestMethod("fooDisabled");
		final TestMethod method42 = new TestMethod("foo2");
		final TestClass class4 = new TestClass("Class4", method41, method42);
	
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return FIBAbstractEditor.makeArray(testPackage);
			}
	
			@Override
			public Resource getFIBResource() {
				return FIB_FILE;
			}
		};
		editor.addAction("change_name", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class1.setName("new_class_1_name");
			}
		});
		editor.addAction("enable_class3", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class3.setName("Class3");
			}
		});
		editor.addAction("add_class", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.addClass(class4);
			}
		});
		editor.addAction("add_method", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class3.addMethod(method32);
			}
		});
		editor.addAction("remove_class", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.removeClass(class4);
			}
		});
		editor.addAction("remove_classes", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.removeClass(class2);
				testPackage.removeClass(class1);
			}
		});
		editor.addAction("make_class5_visible", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class5.setName("Class5");
			}
		});
		editor.launch();
	}*/

	public abstract static class TestObject implements HasPropertyChangeSupport {
		private final PropertyChangeSupport pcSupport;

		public TestObject() {
			pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}
	}

	public static class TestPackage extends TestObject {
		public String name;
		public TestClass mainClass;
		public Vector<TestClass> otherClasses;

		public TestPackage(String aName, TestClass mainClass, TestClass... otherClasses) {
			super();
			this.name = aName;
			this.mainClass = mainClass;
			this.otherClasses = new Vector<>();
			for (TestClass c : otherClasses) {
				this.otherClasses.add(c);
			}
		}

		public void setName(String string) {
			String oldName = name;
			name = string;
			getPropertyChangeSupport().firePropertyChange("name", oldName, name);
		}

		public void addClass(TestClass c) {
			this.otherClasses.add(c);
			getPropertyChangeSupport().firePropertyChange("otherClasses", null, null);
		}

		public void removeClass(TestClass c) {
			this.otherClasses.remove(c);
			getPropertyChangeSupport().firePropertyChange("otherClasses", null, null);
		}

		@Override
		public String toString() {
			return "TestPackage[" + name + "]";
		}

		public TestClass createNewClass() {
			TestClass returned = new TestClass("NewClass");
			addClass(returned);
			return returned;
		}
	}

	public static class TestClass extends TestObject {
		private String name;
		public Vector<TestMethod> methods;

		public TestClass(String aName) {
			methods = new Vector<>();
			this.name = aName;
		}

		public TestClass(String aName, TestMethod... methods) {
			this(aName);
			for (TestMethod m : methods) {
				addMethod(m);
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String string) {
			String oldName = name;
			name = string;
			getPropertyChangeSupport().firePropertyChange("name", oldName, name);
		}

		public void addMethod(TestMethod m) {
			methods.add(m);
			getPropertyChangeSupport().firePropertyChange("methods", null, null);
		}

		public void removeMethod(TestMethod m) {
			methods.remove(m);
			methods.add(m);
			getPropertyChangeSupport().firePropertyChange("methods", null, null);
		}

		public TestMethod createNewMethod() {
			TestMethod returned = new TestMethod("NewMethod");
			addMethod(returned);
			return returned;
		}

		public TestMethod createNewDisabledMethod() {
			TestMethod returned = new TestMethod("NewDisabledMethod");
			addMethod(returned);
			return returned;
		}

		public void delete() {
			System.out.println("Suppression !!!");
		}

		public void helloWorld1() {
			System.out.println("Hello World 1 !");
		}

		public void helloWorld2() {
			System.out.println("Hello World 2 !");
		}

		@Override
		public String toString() {
			return "TestClass[" + name + "]";
		}
	}

	public static class TestMethod extends TestObject {
		public String name;

		public TestMethod(String aName) {
			this.name = aName;
		}

		public void setName(String string) {
			String oldName = name;
			name = string;
			getPropertyChangeSupport().firePropertyChange("name", oldName, name);
		}

		@Override
		public String toString() {
			return "TestMethod[" + name + "]";
		}
	}

}
